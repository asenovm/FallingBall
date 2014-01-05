package edu.fmi.android.gyroship.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

@SuppressLint("NewApi")
public class GameLayout extends RelativeLayout {

	/**
	 * {@value}
	 */
	@SuppressWarnings("unused")
	private static final String TAG = GameLayout.class.getSimpleName();

	private final ShipView shipView;

	public GameLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		shipView = new ShipView(context, attrs, defStyle);
		addView(shipView);
	}

	public GameLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public GameLayout(Context context) {
		this(context, null);
	}

	public void onResume() {
		shipView.onResume();
	}

	public void onPause() {
		shipView.onPause();
	}

}
