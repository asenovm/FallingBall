package edu.fmi.android.fallingball.view;

import java.util.Collection;

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
import edu.fmi.fallingball.utils.Direction;
import edu.fmi.fallingball.utils.ScreenUtil;
import edu.fmi.fallingball.utils.Vector;

public class BallView extends View {

	/**
	 * {@value}
	 */
	public static final String TAG = BallView.class.getSimpleName();

	/**
	 * {@value}
	 */
	private static final int VELOCITY_X_MULTIPLIER = 4;

	/**
	 * {@value}
	 */
	private static final float VELOCITY_Y_INITIAL = 5f;

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

	private final Vector topBorderVector;

	private final Vector leftBorderVector;

	private final Vector rightBorderVector;

	private Vector speedVector;

	private float positionX;

	private float positionY;

	private OnPositionChangedListener positionChangedListener;

	private OnGameEventsListener gameEventsListener;

	public BallView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		ballPaint = new Paint();
		ballPaint.setDither(false);
		ballPaint.setColor(Color.WHITE);

		screenSize = ScreenUtil.getScreenSize(context);

		positionX = screenSize.x / 2;
		positionY = RADIUS_BALL;

		topBorderVector = Vector.from(Direction.TOP);
		leftBorderVector = Vector.from(Direction.LEFT);
		rightBorderVector = Vector.from(Direction.RIGHT);
		speedVector = new Vector(VELOCITY_X_INITIAL, VELOCITY_Y_INITIAL);

		boundingRect = new RectF();
		updatePosition(boundingRect);
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

		if (isLeftCollision()) {
			computeSpeedVector(leftBorderVector);
		} else if (isRightCollision()) {
			computeSpeedVector(rightBorderVector);
		} else if (isTopCollision()) {
			computeSpeedVector(topBorderVector);
		}

		canvas.drawCircle(positionX, positionY, RADIUS_BALL, ballPaint);
		move(speedVector);

		if (positionY >= screenSize.y - RADIUS_BALL) {
			gameEventsListener.onGameEnd();
		}
	}

	public void setOnPositionChangedListener(OnPositionChangedListener listener) {
		this.positionChangedListener = listener;
	}

	public void setOnGameEventsListener(OnGameEventsListener listener) {
		this.gameEventsListener = listener;
	}

	public void onCollisionDetected(final Vector vector,
			final float collisionRatio, final boolean isCell) {
		computeSpeedVector(vector, collisionRatio, isCell);
		move(speedVector);
		if (isCell) {
			gameEventsListener.onGameScoreChanged();
		}
	}

	private void computeSpeedVector(final Vector collisionVector) {
		speedVector = speedVector.substract(collisionVector.multiply(2)
				.multiply(speedVector.dotProduct(collisionVector)));
	}

	private void computeSpeedVector(final Vector collisionVector,
			final float collisionRatio, final boolean isCell) {
		computeSpeedVector(collisionVector);
		if (!isCell) {
			speedVector.x = getCollisionXVelocity(collisionRatio);
		}
	}

	private float getCollisionXVelocity(final float collisionRatio) {
		return -(0.5f - collisionRatio) * VELOCITY_X_MULTIPLIER
				* VELOCITY_X_INITIAL;
	}

	private void move(final Vector speedVector) {
		positionY += speedVector.y;
		positionX += speedVector.x;

		updatePosition(boundingRect);
		positionChangedListener.onPositionChanged(GameItem.BALL, boundingRect);
	}

	private boolean isRightCollision() {
		return positionX >= screenSize.x - RADIUS_BALL;
	}

	private boolean isTopCollision() {
		return positionY < RADIUS_BALL;
	}

	private boolean isLeftCollision() {
		return positionX <= RADIUS_BALL;
	}

	private void updatePosition(final RectF boundingRect) {
		boundingRect.set(positionX - RADIUS_BALL, positionY - RADIUS_BALL,
				positionX + RADIUS_BALL, positionY + RADIUS_BALL);
	}

	public void checkAndHandleCollision(final Collection<CellView> cells) {
		for (final CellView cell : cells) {
			final RectF cellRect = cell.getBoundingRect();
			final float collisionRatio = Math.min(1,
					(boundingRect.right - cellRect.left) / cellRect.width());

			if (isCollidingFromAbove(cellRect)) {
				cells.remove(cell);
				onCollisionDetected(new Vector(0, -1), collisionRatio, true);
				return;
			} else if (isCollidingFromBelow(cellRect)) {
				cells.remove(cell);
				onCollisionDetected(new Vector(0, 1), collisionRatio, true);
				return;
			}
		}

	}

	private boolean isCollidingFromBelow(final RectF cellRect) {
		return boundingRect.bottom > cellRect.bottom
				&& boundingRect.top < cellRect.bottom
				&& boundingRect.left < cellRect.right
				&& boundingRect.right > cellRect.left;
	}

	private boolean isCollidingFromAbove(final RectF cellRect) {
		return boundingRect.bottom > cellRect.top
				&& boundingRect.bottom < cellRect.bottom
				&& boundingRect.right > cellRect.left
				&& boundingRect.left < cellRect.right;
	}
}
