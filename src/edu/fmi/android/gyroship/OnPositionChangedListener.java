package edu.fmi.android.gyroship;

import android.graphics.RectF;
import edu.fmi.android.gyroship.view.GameLayout.GameItem;

public interface OnPositionChangedListener {
	void onPositionChanged(final GameItem item, final RectF position);
}
