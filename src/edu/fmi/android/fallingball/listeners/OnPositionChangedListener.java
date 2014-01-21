package edu.fmi.android.fallingball.listeners;

import android.graphics.RectF;
import edu.fmi.android.fallingball.GameItem;

public interface OnPositionChangedListener {
	/**
	 * A callback fired when the position of the given <tt>GameItem</tt> has
	 * changed
	 * 
	 * @param item
	 *            the <tt>GameItem</tt> whose position has changed
	 * @param position
	 *            the rect, specifying the changed position of the mentioned
	 *            item
	 */
	void onPositionChanged(final GameItem item, final RectF position);
}
