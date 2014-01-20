package edu.fmi.android.fallingball.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import edu.fmi.android.gyroship.R;
import edu.fmi.fallingball.utils.ScreenUtil;

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
	private static final String TEXT_TAP_TO_CONTINUE = "Tap anywhere to continue.";

	private final float gameOverTextX;

	private final float gameOverTextY;

	private final float tapTextX;

	private final float tapTextY;

	private final Paint textPaint;

	public FinishedGameView(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		textPaint = new Paint();
		textPaint.setColor(Color.WHITE);
		textPaint.setTextSize(TEXT_SIZE);

		final Point screenSize = ScreenUtil.getScreenSize(context);

		final float gameOverTextWidth = textPaint.measureText(TEXT_GAME_OVER);
		gameOverTextX = (screenSize.x - gameOverTextWidth) / 2;
		gameOverTextY = (screenSize.y - textPaint.ascent() - textPaint
				.descent()) / 2 - 35;

		final float tapTextWidth = textPaint.measureText(TEXT_TAP_TO_CONTINUE);
		tapTextX = (screenSize.x - tapTextWidth) / 2;
		tapTextY = (screenSize.y - textPaint.ascent() - textPaint.descent()) / 2 + 5;
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
		canvas.drawText(TEXT_GAME_OVER, gameOverTextX, gameOverTextY, textPaint);
		canvas.drawText(TEXT_TAP_TO_CONTINUE, tapTextX, tapTextY, textPaint);
	}
}
