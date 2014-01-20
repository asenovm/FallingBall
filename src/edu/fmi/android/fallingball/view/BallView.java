package edu.fmi.android.fallingball.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import edu.fmi.android.fallingball.GameItem;
import edu.fmi.android.fallingball.listeners.OnGameEventsListener;
import edu.fmi.android.fallingball.listeners.OnPositionChangedListener;
import edu.fmi.fallingball.utils.ScreenUtil;

public class BallView extends View {

	/**
	 * {@value}
	 */
	public static final String TAG = BallView.class.getSimpleName();

	/**
	 * {@value}
	 */
	private static final int ORIENTATION_Y_BORDER_TOP = 1;

	/**
	 * {@value}
	 */
	private static final int ORIENTATION_X_BORDER_TOP = 0;

	/**
	 * {@value}
	 */
	private static final int ORIENTATION_Y_BORDER_LEFT = 0;

	/**
	 * {@value}
	 */
	private static final int ORIENTATION_X_BORDER_LEFT = 1;

	/**
	 * {@value}
	 */
	private static final int ORIENTATION_Y_BORDER_RIGHT = 0;

	/**
	 * {@value}
	 */
	private static final int ORIENTATION_X_BORDER_RIGHT = -1;

	/**
	 * {@value}
	 */
	private static final float VELOCITY_Y_INITIAL = 5f;

	/**
	 * {@value}
	 */
	private static final float VELOCITY_X_INITIAL = 0.2f;

	/**
	 * {@value}
	 */
	private static final int RADIUS_BALL = 10;

	private final Paint ballPaint;

	private final RectF boundingRect;

	private final Point screenSize;

	private final Vector topBorderVector;

	private final Vector leftBorderVector;

	private final Vector rightBorderVector;

	private float positionX;

	private float positionY;

	private Vector speedVector;

	private OnPositionChangedListener positionChangedListener;

	private OnGameEventsListener gameEventsListener;

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
		ballPaint.setDither(false);
		ballPaint.setColor(Color.WHITE);

		screenSize = ScreenUtil.getScreenSize(context);

		positionX = screenSize.x / 2;
		positionY = RADIUS_BALL;

		speedVector = new Vector(VELOCITY_X_INITIAL, VELOCITY_Y_INITIAL);
		leftBorderVector = new Vector(ORIENTATION_X_BORDER_LEFT,
				ORIENTATION_Y_BORDER_LEFT);
		topBorderVector = new Vector(ORIENTATION_X_BORDER_TOP,
				ORIENTATION_Y_BORDER_TOP);
		rightBorderVector = new Vector(ORIENTATION_X_BORDER_RIGHT,
				ORIENTATION_Y_BORDER_RIGHT);

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

		if (positionX <= RADIUS_BALL) {
			computeSpeedVector(leftBorderVector);
		} else if (positionX >= screenSize.x - RADIUS_BALL) {
			computeSpeedVector(rightBorderVector);
		}

		if (positionY < RADIUS_BALL) {
			computeSpeedVector(topBorderVector);
		}

		canvas.drawCircle(positionX, positionY, RADIUS_BALL, ballPaint);
		move(speedVector);

		if (positionY >= screenSize.y - RADIUS_BALL) {
			gameEventsListener.onGameEnd();
		}
	}

	public void setOnPositionChangedListener(
			final OnPositionChangedListener listener) {
		this.positionChangedListener = listener;
	}

	public void onCollisionDetected(Vector vector, final boolean isCell) {
		computeSpeedVector(vector);
		move(speedVector);
		if (isCell) {
			gameEventsListener.onGameScoreChanged();
		}
	}

	private void computeSpeedVector(final Vector collisionVector) {
		speedVector = speedVector.substract(collisionVector.multiply(2)
				.multiply(speedVector.dotProduct(collisionVector)));
	}

	private void move(final Vector speedVector) {
		positionY += speedVector.y;
		positionX += speedVector.x;

		boundingRect.set(positionX - RADIUS_BALL, positionY - RADIUS_BALL,
				positionX + RADIUS_BALL, positionY + RADIUS_BALL);

		positionChangedListener.onPositionChanged(GameItem.BALL, boundingRect);
	}

	public void setOnGameEventsListener(final OnGameEventsListener listener) {
		this.gameEventsListener = listener;
	}

}
