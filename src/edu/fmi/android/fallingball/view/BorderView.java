package edu.fmi.android.fallingball.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;
import edu.fmi.android.fallingball.R;
import edu.fmi.fallingball.utils.ScreenUtil;

public class BorderView extends View {

	/**
	 * {@value}
	 */
	private static final int WIDTH_BORDER = 6;

	private final Paint borderPaint;

	private final Point screenSize;

	public BorderView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		borderPaint = new Paint();
		borderPaint.setColor(getResources().getColor(R.color.light_grey_1));
		borderPaint.setStrokeWidth(WIDTH_BORDER);
		borderPaint.setDither(false);

		screenSize = ScreenUtil.getScreenSize(context);
	}

	public BorderView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public BorderView(Context context) {
		this(context, null);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		canvas.drawLine(0, 0, screenSize.x, 0, borderPaint);
		canvas.drawLine(0, 0, 0, screenSize.y, borderPaint);
		canvas.drawLine(screenSize.x, 0, screenSize.x, screenSize.y,
				borderPaint);
	}
}
