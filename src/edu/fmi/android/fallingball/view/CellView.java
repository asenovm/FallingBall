package edu.fmi.android.fallingball.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import edu.fmi.android.gyroship.R;

public class CellView extends View {

	/**
	 * {@value}
	 */
	@SuppressWarnings("unused")
	private static final String TAG = CellView.class.getSimpleName();

	public static final int WIDTH_CELL = 60;

	private static final int WIDTH_BORDER = 2;

	public static final int HEIGHT_CELL = 20;

	private final Paint cellPaint;

	private final RectF boundingRect;

	private float positionX;

	private float positionY;

	public CellView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		cellPaint = new Paint();
		cellPaint.setStrokeWidth(WIDTH_BORDER);

		boundingRect = new RectF();
	}

	public CellView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CellView(Context context) {
		this(context, null);
	}

	public void setX(final float positionX) {
		this.positionX = positionX;
		boundingRect.left = positionX;
		boundingRect.right = positionX + WIDTH_CELL;
	}

	public void setY(final float positionY) {
		this.positionY = positionY;
		boundingRect.top = positionY;
		boundingRect.bottom = positionY + HEIGHT_CELL;
	}

	public RectF getBoundingRect() {
		return boundingRect;
	}

	public void hide() {
		setVisibility(View.GONE);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		cellPaint.setStyle(Paint.Style.FILL);
		cellPaint.setColor(getResources().getColor(R.color.grey));
		canvas.drawRect(positionX, positionY, positionX + WIDTH_CELL, positionY
				+ HEIGHT_CELL, cellPaint);

		cellPaint.setStyle(Paint.Style.STROKE);
		cellPaint.setColor(getResources().getColor(R.color.light_grey));
		canvas.drawRect(positionX, positionY, positionX + WIDTH_CELL, positionY
				+ HEIGHT_CELL, cellPaint);
	}

	@Override
	public String toString() {
		return positionX + " " + positionY;
	}

}
