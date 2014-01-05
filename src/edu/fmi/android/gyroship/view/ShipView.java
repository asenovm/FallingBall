package edu.fmi.android.gyroship.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class ShipView extends View {

	private float positionX;

	private float positionY;

	private float accelerationX;

	private float accelerationY;

	private float horizontalBound;

	private float verticalBound;

	public ShipView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public ShipView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ShipView(Context context) {
		this(context, null);
	}

	public void computePhysics(float sx, float sy, float dT) {
		final float dTdT = dT * dT;
		final float x = positionX + accelerationX * dTdT;
		final float y = positionY + accelerationY * dTdT;
		positionX = x;
		positionY = y;
		accelerationX = -sx;
		accelerationY = -sy;
	}

	public void resolveCollisionWithBounds() {
		final float xmax = horizontalBound;
		final float ymax = verticalBound;
		final float x = positionX;
		final float y = positionX;
		if (x > xmax) {
			positionX = xmax;
		} else if (x < -xmax) {
			positionX = -xmax;
		}
		if (y > ymax) {
			positionY = ymax;
		} else if (y < -ymax) {
			positionY = -ymax;
		}
	}

	public float getPosX() {
		return positionX;
	}

	public float getPosY() {
		return positionY;
	}

	public void setBounds(final float horizontalBound, final float verticalBound) {
		this.horizontalBound = horizontalBound;
		this.verticalBound = verticalBound;
	}

}
