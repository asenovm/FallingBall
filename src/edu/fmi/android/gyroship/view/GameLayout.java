package edu.fmi.android.gyroship.view;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import edu.fmi.android.gyroship.OnPositionChangedListener;
import edu.fmi.android.gyroship.listeners.OnGameEventsListener;

public class GameLayout extends SurfaceView implements
		OnPositionChangedListener, OnGameEventsListener {

	/**
	 * {@value}
	 */
	@SuppressWarnings("unused")
	private static final String TAG = GameLayout.class.getSimpleName();

	private final PadView padView;

	private final BallView ballView;

	private RectF padViewRect;

	private RectF ballViewRect;

	private OnGameEventsListener gameEventsListener;

	private class LayoutRunnable implements Runnable {

		private boolean isDestroyed;

		public void destroy() {
			isDestroyed = true;
		}

		@Override
		public void run() {
			final SurfaceHolder holder = getHolder();
			Canvas canvas = null;

			while (!isDestroyed && (canvas = holder.lockCanvas()) != null) {
				canvas.drawColor(Color.BLACK);
				padView.draw(canvas);
				ballView.draw(canvas);
				holder.unlockCanvasAndPost(canvas);
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
			layoutRunnable.destroy();
			executorService.shutdown();
		}

	}

	public enum GameItem {
		PAD, BALL;
	}

	public GameLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		padView = new PadView(context, attrs, defStyle);
		ballView = new BallView(context, attrs, defStyle);

		padView.setOnPositionChangedListener(this);

		ballView.setOnPositionChangedListener(this);
		ballView.setOnGameFinishListener(this);

		final SurfaceHolder holder = getHolder();
		holder.addCallback(new HolderCallback());

		padViewRect = new RectF();
		ballViewRect = new RectF();
	}

	public GameLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public GameLayout(Context context) {
		this(context, null);
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

		if (hasCollision()) {
			ballView.onCollisionDetected();
		}
	}

	private boolean hasCollision() {
		return padViewRect.left < ballViewRect.right
				&& padViewRect.right > ballViewRect.left
				&& padViewRect.top < ballViewRect.bottom;
	}

	public void setOnGameFinishListener(final OnGameEventsListener listener) {
		this.gameEventsListener = listener;
	}

	@Override
	public void onGameEnd() {
		gameEventsListener.onGameEnd();
	}
}
