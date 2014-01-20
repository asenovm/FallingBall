package edu.fmi.android.fallingball.view;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import edu.fmi.android.fallingball.GameItem;
import edu.fmi.android.fallingball.listeners.OnGameEventsListener;
import edu.fmi.android.fallingball.listeners.OnPositionChangedListener;
import edu.fmi.android.fallingball.view.BallView.Vector;
import edu.fmi.android.gyroship.R;
import edu.fmi.fallingball.utils.ScreenUtil;

public class GameLayout extends SurfaceView implements
		OnPositionChangedListener, OnGameEventsListener {

	/**
	 * {@value}
	 */
	private static final String TAG = GameLayout.class.getSimpleName();

	private final PadView padView;

	private final BallView ballView;

	private final BorderView borderView;

	private final ResultsView resultsView;

	private final FinishedGameView finishedGameView;

	private final List<CellView> cells;

	private RectF padViewRect;

	private RectF ballViewRect;

	private OnGameEventsListener gameEventsListener;

	private boolean isGameFinished;

	private class LayoutRunnable implements Runnable {

		/**
		 * {@value}
		 */
		private static final int GAME_FPS = 45;

		/**
		 * {@value}
		 */
		private static final int TIME_FRAME = 1000 / GAME_FPS;

		private boolean isSurfaceDestroyed;

		private final Paint backgroundPaint;

		private final Point screenSize;

		public LayoutRunnable() {
			backgroundPaint = new Paint();
			backgroundPaint.setShader(getBackgroundShader());
			backgroundPaint.setDither(false);
			backgroundPaint.setFilterBitmap(false);

			screenSize = ScreenUtil.getScreenSize(getContext());
		}

		private BitmapShader getBackgroundShader() {
			return new BitmapShader(BitmapFactory.decodeResource(
					getResources(), R.drawable.activity_background_image),
					Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
		}

		public void onSurfaceDestroyed() {
			isSurfaceDestroyed = true;
		}

		@Override
		public void run() {
			final SurfaceHolder holder = getHolder();
			Canvas canvas = null;

			while (!isSurfaceDestroyed
					&& (canvas = holder.lockCanvas()) != null) {

				final long startTime = System.currentTimeMillis();
				canvas.drawRect(0, 0, screenSize.x, screenSize.y,
						backgroundPaint);

				if (isGameFinished) {
					finishedGameView.draw(canvas);
				} else {
					padView.draw(canvas);
					ballView.draw(canvas);
					borderView.draw(canvas);
					resultsView.draw(canvas);
					for (final CellView cell : cells) {
						cell.draw(canvas);
					}
				}

				holder.unlockCanvasAndPost(canvas);
				renderNextFrame(startTime);
			}
		}

		private void renderNextFrame(final long startTime) {
			long sleepTime = TIME_FRAME
					- (System.currentTimeMillis() - startTime);

			if (sleepTime > 0) {
				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {
					Log.e(TAG, e.getMessage(), e);
				}
			} else {
				while (sleepTime < 0) {
					sleepTime += TIME_FRAME;
				}
			}
		}
	}

	private class HolderCallback implements SurfaceHolder.Callback {

		private final ExecutorService executorService;

		private final LayoutRunnable layoutRunnable;

		public HolderCallback() {
			layoutRunnable = new LayoutRunnable();
			executorService = Executors.newSingleThreadExecutor();
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			// blank
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			executorService.execute(layoutRunnable);
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			layoutRunnable.onSurfaceDestroyed();
			executorService.shutdown();
		}

	}

	private class GameLayoutOnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			if (isGameFinished) {
				gameEventsListener.onGameEnd();
			}
		}
	}

	public GameLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		padView = new PadView(context, attrs, defStyle);
		ballView = new BallView(context, attrs, defStyle);
		borderView = new BorderView(context, attrs, defStyle);
		resultsView = new ResultsView(context, attrs, defStyle);
		finishedGameView = new FinishedGameView(context, attrs, defStyle);

		padView.setOnPositionChangedListener(this);

		ballView.setOnPositionChangedListener(this);
		ballView.setOnGameEventsListener(this);

		final SurfaceHolder holder = getHolder();
		holder.addCallback(new HolderCallback());

		final Point screenSize = ScreenUtil.getScreenSize(context);

		padViewRect = new RectF();
		ballViewRect = new RectF();

		cells = new LinkedList<CellView>();

		int currentX = 50;
		int currentY = 100;
		int iter = 0;

		while (currentY < screenSize.y - 60) {
			while (currentX < screenSize.x - 50 - iter * 60) {
				final CellView cell = new CellView(context);
				cell.setX(currentX);
				cell.setY(currentY);
				cells.add(cell);

				currentX += 60;
			}

			++iter;
			currentY += 20;
			currentX = 50 + iter * 60;
		}

		setOnClickListener(new GameLayoutOnClickListener());
	}

	public GameLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public GameLayout(Context context) {
		this(context, null);
	}

	public void setOnGameEventsListener(final OnGameEventsListener listener) {
		this.gameEventsListener = listener;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		padView.setDimensions(w, h);
	}

	@Override
	public void onPositionChanged(GameItem item, RectF position) {
		if (item == GameItem.BALL) {
			ballViewRect = position;
		} else if (item == GameItem.PAD) {
			padViewRect = position;
		}

		if (hasCollision(padViewRect, ballViewRect)) {
			ballView.onCollisionDetected(new Vector(0, -1), false);
		}

		if (item == GameItem.BALL) {
			for (final CellView cell : cells) {
				final RectF cellRect = cell.getBoundingRect();

				if (ballViewRect.bottom > cellRect.top
						&& ballViewRect.bottom < cellRect.bottom
						&& ballViewRect.right > cellRect.left
						&& ballViewRect.left < cellRect.right) {
					cells.remove(cell);
					ballView.onCollisionDetected(new Vector(0, -1), true);
					return;
				} else if (ballViewRect.bottom > cellRect.bottom
						&& ballViewRect.top < cellRect.bottom
						&& ballViewRect.left < cellRect.right
						&& ballViewRect.right > cellRect.left) {
					cells.remove(cell);
					ballView.onCollisionDetected(new Vector(0, 1), true);
					return;
				}
			}
		}
	}

	@Override
	public void onGameEnd() {
		isGameFinished = true;
	}

	@Override
	public void onGameScoreChanged() {
		resultsView.updateResult();
	}

	private boolean hasCollision(final RectF containerRect,
			final RectF collisionRect) {
		return containerRect.top < collisionRect.bottom
				&& containerRect.left < collisionRect.right
				&& containerRect.right > collisionRect.left;
	}
}
