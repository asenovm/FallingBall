package edu.fmi.android.brickpong.view;

import java.util.HashSet;
import java.util.Set;
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
import edu.fmi.android.brickpong.GameItem;
import edu.fmi.android.brickpong.R;
import edu.fmi.android.brickpong.listeners.OnGameEventsListener;
import edu.fmi.android.brickpong.listeners.OnPositionChangedListener;
import edu.fmi.android.brickpong.utils.ScreenUtil;

public class GameLayout extends SurfaceView implements
		OnPositionChangedListener, OnGameEventsListener {

	private static final int CELL_START_Y = 100;

	private static final int CELL_START_X = 50;

	/**
	 * {@value}
	 */
	private static final String TAG = GameLayout.class.getSimpleName();

	private final PadView padView;

	private final BallView ballView;

	private final BorderView borderView;

	private final ResultsView resultsView;

	private final FinishedGameView finishedGameView;

	private final Set<CellView> cells;

	private RectF padViewRect;

	private OnGameEventsListener gameEventsListener;

	private boolean isGameFinished;

	private class LayoutRunnable implements Runnable {

		/**
		 * {@value}
		 */
		private static final int MILISECONDS_IN_A_SECOND = 1000;

		/**
		 * {@value}
		 */
		private static final int GAME_FPS = 45;

		/**
		 * {@value}
		 */
		private static final int TIME_FRAME = MILISECONDS_IN_A_SECOND
				/ GAME_FPS;

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
					finishedGameView.setWin(false);
					finishedGameView.draw(canvas);
				} else if (cells.isEmpty()) {
					finishedGameView.setWin(true);
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
				awaitNextFrame(startTime);
			}
		}

		private void awaitNextFrame(final long startTime) {
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
			if (isGameFinished || cells.isEmpty()) {
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

		padViewRect = new RectF();

		cells = initCells();
		setOnClickListener(new GameLayoutOnClickListener());
	}

	private Set<CellView> initCells() {
		final Context context = getContext();
		final Point screenSize = ScreenUtil.getScreenSize(context);

		final Set<CellView> result = new HashSet<CellView>();

		int currentX = CELL_START_X;
		int currentY = CELL_START_Y;
		int cellRows = 0;

		while (currentY < screenSize.y - CELL_START_Y) {
			while (currentX < screenSize.x - CELL_START_X - cellRows
					* CellView.WIDTH_CELL) {
				final CellView cell = new CellView(context);
				cell.setX(currentX);
				cell.setY(currentY);
				result.add(cell);

				currentX += CellView.WIDTH_CELL;
			}

			++cellRows;
			currentY += CellView.HEIGHT_CELL;
			currentX = CELL_START_X + cellRows * CellView.WIDTH_CELL;
		}
		return result;
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
			ballView.checkAndHandleCellCollision(cells);
		} else if (item == GameItem.PAD) {
			padViewRect = position;
		}

		ballView.checkAndHandlePadCollision(padViewRect);
	}

	@Override
	public void onGameEnd() {
		isGameFinished = true;
	}

	@Override
	public void onGameScoreChanged() {
		resultsView.updateResult();
	}
}
