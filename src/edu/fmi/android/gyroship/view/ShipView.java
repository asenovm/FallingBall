package edu.fmi.android.gyroship.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class ShipView extends View {

	private float positionX;

	private float positionY;

	public ShipView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public ShipView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ShipView(Context context) {
		this(context, null);
	}

	public float getPosX() {
		return positionX;
	}

	public float getPosY() {
		return positionY;
	}

	private float mAccelX;
	private float mAccelY;

	public void computePhysics(float sx, float sy, float dT) {
		final float dTdT = dT * dT;
		final float x = positionX + mAccelX * dTdT;
		final float y = positionY + mAccelY * dTdT;
		positionX = x;
		positionY = y;
		mAccelX = -sx;
		mAccelY = -sy;
	}
	
	public void setBounds(final float horizontalBound, final float verticalBound) {
		this.horizontalBound = horizontalBound;
		this.verticalBound = verticalBound;
	}
	
	private float horizontalBound;
	
	private float verticalBound;

	/*
	 * Resolving constraints and collisions with the Verlet integrator can be
	 * very simple, we simply need to move a colliding or constrained particle
	 * in such way that the constraint is satisfied.
	 */
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

}
