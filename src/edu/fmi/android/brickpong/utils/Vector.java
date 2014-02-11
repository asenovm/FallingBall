package edu.fmi.android.brickpong.utils;

public class Vector {

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

	public float x;

	public float y;

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

	public static Vector from(Direction direction) {
		if (direction == Direction.LEFT) {
			return new Vector(ORIENTATION_X_BORDER_LEFT,
					ORIENTATION_Y_BORDER_LEFT);
		} else if (direction == Direction.TOP) {
			return new Vector(ORIENTATION_X_BORDER_TOP,
					ORIENTATION_Y_BORDER_TOP);
		} else {
			return new Vector(ORIENTATION_X_BORDER_RIGHT,
					ORIENTATION_Y_BORDER_RIGHT);
		}
	}
}
