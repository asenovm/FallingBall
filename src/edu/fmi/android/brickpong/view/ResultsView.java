package edu.fmi.android.brickpong.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class ResultsView extends View {

	/**
	 * {@value}
	 */
	private static final String TEXT_SCORE = "Score: ";

	/**
	 * {@value}
	 */
	private static final int SIZE_TEXT = 16;

	/**
	 * {@value}
	 */
	private static final int RESULTS_Y = 20;

	/**
	 * {@value}
	 */
	private static final int RESULTS_X = 10;

	private final Paint resultsPaint;

	private int result;

	public ResultsView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		resultsPaint = new Paint();
		resultsPaint.setColor(Color.WHITE);
		resultsPaint.setTextSize(SIZE_TEXT);
	}

	public ResultsView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ResultsView(Context context) {
		this(context, null);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawText(getScoreText(), RESULTS_X, RESULTS_Y, resultsPaint);
	}

	public void updateResult() {
		++result;
	}

	private String getScoreText() {
		return TEXT_SCORE + Integer.toString(result);
	}

}
