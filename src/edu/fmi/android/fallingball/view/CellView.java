package edu.fmi.android.fallingball.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class CellView extends View {

	/**
	 * {@value}
	 */
	@SuppressWarnings("unused")
	private static final String TAG = CellView.class.getSimpleName();

	private static final int CELL_WIDTH = 60;

	private static final int CELL_HEIGHT = 20;

	private final Paint cellPaint;

	private final RectF boundingRect;

	private float positionX;

	private float positionY;

	public CellView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		cellPaint = new Paint();
		cellPaint.setColor(Color.GRAY);
		cellPaint.setStrokeWidth(2);

		boundingRect = new RectF();
	}

	public CellView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CellView(Context context) {
		this(context, null);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		cellPaint.setStyle(Paint.Style.FILL);
		cellPaint.setColor(Color.GRAY);
		canvas.drawRect(positionX, positionY, positionX + CELL_WIDTH, positionY
				+ CELL_HEIGHT, cellPaint);

		cellPaint.setStyle(Paint.Style.STROKE);
		cellPaint.setColor(Color.BLACK);
		canvas.drawRect(positionX, positionY, positionX + CELL_WIDTH, positionY
				+ CELL_HEIGHT, cellPaint);
	}

	public void setX(final float positionX) {
		this.positionX = positionX;
		boundingRect.left = positionX;
		boundingRect.right = positionX + CELL_WIDTH;
	}

	public void setY(final float positionY) {
		this.positionY = positionY;
		boundingRect.top = positionY;
		boundingRect.bottom = positionY + CELL_HEIGHT;
	}

	@Override
	public String toString() {
		return positionX + " " + positionY;
	}

	public RectF getRect() {
		return boundingRect;
	}

	public void hide() {
		setVisibility(View.GONE);
	}

}
