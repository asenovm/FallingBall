package edu.fmi.android.gyroship.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import edu.fmi.android.gyroship.OnPositionChangedListener;
import edu.fmi.android.gyroship.view.GameLayout.GameItem;

public class BallView extends View {

	/**
	 * {@value}
	 */
	public static final String TAG = BallView.class.getSimpleName();

	/**
	 * {@value}
	 */
	private static final int ORIENTATION_Y_PAD = 1;

	/**
	 * {@value}
	 */
	private static final int ORIENTATION_X_PAD = 0;

	/**
	 * {@value}
	 */
	private static final float VELOCITY_Y_INITIAL = 4f;

	/**
	 * {@value}
	 */
	private static final float VELOCITY_X_INITIAL = 0.5f;

	/**
	 * {@value}
	 */
	private static final int RADIUS_BALL = 10;

	private final Paint ballPaint;

	private final RectF boundingRect;

	private final Point screenSize;

	private final Vector padVector;

	private float positionX;

	private float positionY;

	private Vector speedVector;

	private OnPositionChangedListener listener;

	public static class Vector {
		private float x;

		private float y;

		public Vector(final float x, final float y) {
			this.x = x;
			this.y = y;
		}

		public Vector() {
			this(0, 0);
		}

		public float dotProduct(final Vector other) {
			return this.x * other.x + this.y * other.y;
		}

		public Vector multiply(final float coef) {
			return new Vector(x * coef, y * coef);
		}

		public Vector add(final Vector other) {
			return new Vector(x + other.x, y + other.y);
		}

		public Vector substract(final Vector other) {
			return new Vector(x - other.x, y - other.y);
		}

		@Override
		public String toString() {
			return "x= " + x + " y= " + y;
		}
	}

	public BallView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		ballPaint = new Paint();
		ballPaint.setColor(Color.WHITE);

		final WindowManager windowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		final Display display = windowManager.getDefaultDisplay();

		screenSize = new Point();
		display.getSize(screenSize);

		positionX = screenSize.x / 2;
		positionY = RADIUS_BALL;

		speedVector = new Vector(VELOCITY_X_INITIAL, VELOCITY_Y_INITIAL);
		padVector = new Vector(ORIENTATION_X_PAD, ORIENTATION_Y_PAD);

		boundingRect = new RectF(positionX - RADIUS_BALL, positionY
				- RADIUS_BALL, positionX + RADIUS_BALL, positionY + RADIUS_BALL);
	}

	public BallView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public BallView(Context context) {
		this(context, null);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawCircle(positionX, positionY, RADIUS_BALL, ballPaint);

		positionY += speedVector.y;
		positionX += speedVector.x;

		boundingRect.left = positionX - RADIUS_BALL;
		boundingRect.right = positionX + RADIUS_BALL;
		boundingRect.top = positionY - 2 * RADIUS_BALL;
		boundingRect.bottom = positionY + 2 * RADIUS_BALL;

		listener.onPositionChanged(GameItem.BALL, boundingRect);

		invalidate();
	}

	public float getX() {
		return positionX;
	}

	public float getY() {
		return positionY;
	}

	public RectF getRect() {
		return boundingRect;
	}

	public void setOnPositionChangedListener(
			final OnPositionChangedListener listener) {
		this.listener = listener;
	}

	public void onCollisionDetected(final RectF containerRect) {
		speedVector = speedVector.substract(padVector.multiply(2).multiply(
				speedVector.dotProduct(padVector)));

		positionX += speedVector.x;
		positionY += speedVector.y;

		boundingRect.set(positionX - RADIUS_BALL, positionY - RADIUS_BALL,
				positionX + RADIUS_BALL, positionY + RADIUS_BALL);

		listener.onPositionChanged(GameItem.BALL, boundingRect);
	}

}
