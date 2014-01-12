package edu.fmi.android.fallingball.listeners;

import android.graphics.RectF;
import edu.fmi.android.fallingball.view.GameLayout.GameItem;

public interface OnPositionChangedListener {
	void onPositionChanged(final GameItem item, final RectF position);
}
