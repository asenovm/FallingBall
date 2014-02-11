package edu.fmi.android.brickpong.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import edu.fmi.android.brickpong.R;
import edu.fmi.android.brickpong.utils.ScreenUtil;

public class FinishedGameView extends RelativeLayout {

	/**
	 * {@value}
	 */
	@SuppressWarnings("unused")
	private static final String TAG = FinishedGameView.class.getSimpleName();

	/**
	 * {@value}
	 */
	private static final int TEXT_SIZE = 30;

	/**
	 * {@value}
	 */
	private static final String TEXT_GAME_OVER = "Game Over.";

	/**
	 * {@value}
	 */
	private static final String TEXT_GAME_WIN = "You won the game. Congratulations.";

	/**
	 * {@value}
	 */
	private static final String TEXT_TAP_TO_CONTINUE = "Tap anywhere to continue.";

	private float resultTextX;

	private float resultTextY;

	private float tapTextX;

	private float tapTextY;

	private final Paint textPaint;

	private String resultText;

	public FinishedGameView(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		textPaint = new Paint();
		textPaint.setColor(Color.WHITE);
		textPaint.setTextSize(TEXT_SIZE);

	}

	public FinishedGameView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public FinishedGameView(Context context) {
		this(context, null);
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);

		canvas.drawColor(getResources().getColor(R.color.transparent_black));
		canvas.drawText(resultText, resultTextX, resultTextY, textPaint);
		canvas.drawText(TEXT_TAP_TO_CONTINUE, tapTextX, tapTextY, textPaint);
	}

	public void setWin(final boolean won) {
		if (won) {
			resultText = TEXT_GAME_WIN;
		} else {
			resultText = TEXT_GAME_OVER;
		}

		final Point screenSize = ScreenUtil.getScreenSize(getContext());

		final float tapTextWidth = textPaint.measureText(TEXT_TAP_TO_CONTINUE);
		tapTextX = (screenSize.x - tapTextWidth) / 2;
		tapTextY = (screenSize.y - textPaint.ascent() - textPaint.descent() + TEXT_SIZE) / 2;

		final float resultTextWidth = textPaint.measureText(resultText);
		resultTextX = (screenSize.x - resultTextWidth) / 2;
		resultTextY = (screenSize.y - textPaint.ascent() - textPaint.descent() - TEXT_SIZE) / 2;
	}
}
