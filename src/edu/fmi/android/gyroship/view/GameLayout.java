package edu.fmi.android.gyroship.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import edu.fmi.android.gyroship.OnPositionChangedListener;

public class GameLayout extends RelativeLayout implements
		OnPositionChangedListener {

	/**
	 * {@value}
	 */
	private static final String TAG = GameLayout.class.getSimpleName();

	private final PadView padView;

	private final BallView ballView;

	private RectF padViewRect;

	private RectF ballViewRect;

	public enum GameItem {
		PAD, BALL;
	}

	public GameLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		padView = new PadView(context, attrs, defStyle);
		ballView = new BallView(context, attrs, defStyle);

		addView(padView);
		addView(ballView);

		padView.setOnPositionChangedListener(this);
		ballView.setOnPositionChangedListener(this);

		padViewRect = new RectF();
		ballViewRect = new RectF();
	}

	public GameLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public GameLayout(Context context) {
		this(context, null);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		padView.setDimensions(w, h);
	}

	@SuppressLint("NewApi")
	@Override
	public void onPositionChanged(GameItem item, RectF position) {
		if (item == GameItem.BALL) {
			ballViewRect = position;
		} else if (item == GameItem.PAD) {
			padViewRect = position;
		}

		if (padViewRect.left < ballViewRect.left
				&& padViewRect.right > ballViewRect.right
				&& padViewRect.top < ballViewRect.top) {
			ballView.onCollisionDetected(padViewRect);
		}
	}
}
