package edu.fmi.fallingball.utils;

public class Vector {

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

}
