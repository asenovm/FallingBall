package edu.fmi.fallingball.utils;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

public class ScreenUtil {

	private ScreenUtil() {
		// blank
	}

	public static Point getScreenSize(final Context context) {
		final WindowManager windowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		final Display display = windowManager.getDefaultDisplay();

		final Point point = new Point();
		display.getSize(point);

		return point;
	}

}
