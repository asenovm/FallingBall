package edu.fmi.android.gyroship.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;

public class ShipView extends View {

	private final Paint shipPaint;

	private final SensorEventListener accelerometerListener;

	private float sensorX;

	private float sensorY;

	private float mMetersToPixelsX;

	private float mMetersToPixelsY;

	private long sensorTimeStamp;

	private long cpuTimeStamp;

	private long lastTimeStamp;

	private float lastDeltaT;

	private float positionX;

	private float positionY;

	private float accelerationX;

	private float accelerationY;

	private class GyroSensorListener implements SensorEventListener {

		private final float[] gravity;

		private final float[] linearAcceleration;

		public GyroSensorListener() {
			gravity = new float[3];
			linearAcceleration = new float[3];
		}

		public void onSensorChanged(SensorEvent event) {

			float alpha = 0.8f;

			gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
			gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
			gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

			linearAcceleration[0] = event.values[0] - gravity[0];
			linearAcceleration[1] = event.values[1] - gravity[1];
			linearAcceleration[2] = event.values[2] - gravity[2];

			final WindowManager manager = (WindowManager) getContext()
					.getSystemService(Context.WINDOW_SERVICE);
			final Display display = manager.getDefaultDisplay();

			switch (display.getRotation()) {
			case Surface.ROTATION_0:
				sensorX = linearAcceleration[0];
				sensorY = linearAcceleration[1];
				break;
			case Surface.ROTATION_90:
				sensorX = -linearAcceleration[1];
				sensorY = linearAcceleration[0];
				break;
			case Surface.ROTATION_180:
				sensorX = -linearAcceleration[0];
				sensorY = -linearAcceleration[1];
				break;
			case Surface.ROTATION_270:
				sensorX = linearAcceleration[1];
				sensorY = -linearAcceleration[0];
				break;
			}

			sensorTimeStamp = event.timestamp;
			cpuTimeStamp = System.nanoTime();
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// blank
		}

	}

	public ShipView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		accelerometerListener = new GyroSensorListener();

		shipPaint = new Paint();
		shipPaint.setColor(Color.MAGENTA);

		final WindowManager windowManager = (WindowManager) getContext()
				.getSystemService(Context.WINDOW_SERVICE);

		DisplayMetrics metrics = new DisplayMetrics();
		windowManager.getDefaultDisplay().getMetrics(metrics);
		mMetersToPixelsX = metrics.xdpi / 0.0254f;
		mMetersToPixelsY = metrics.ydpi / 0.0254f;
	}

	public ShipView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ShipView(Context context) {
		this(context, null);
	}

	public void onResume() {
		final SensorManager sensorManager = (SensorManager) getContext()
				.getSystemService(Context.SENSOR_SERVICE);

		sensorManager.registerListener(accelerometerListener,
				sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
				SensorManager.SENSOR_DELAY_GAME);
	}

	public void onPause() {
		final SensorManager sensorManager = (SensorManager) getContext()
				.getSystemService(Context.SENSOR_SERVICE);
		sensorManager.unregisterListener(accelerometerListener);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		final long now = sensorTimeStamp + (System.nanoTime() - cpuTimeStamp);

		if (lastTimeStamp != 0) {
			final float dT = (float) (now - lastTimeStamp)
					* (1.0f / 1000000000.0f);
			if (lastDeltaT != 0) {
				final float dTdT = dT * dT;
				final float x = positionX + accelerationX * dTdT;
				final float y = positionY + accelerationY * dTdT;

				positionX = x;
				positionY = y;
				accelerationX = -sensorX;
				accelerationY = -sensorY;
			}
			lastDeltaT = dT;
		}
		lastTimeStamp = now;

		final float xc = 0;
		final float yc = 0;
		final float xs = mMetersToPixelsX;
		final float ys = mMetersToPixelsY;
		/*
		 * We transform the canvas so that the coordinate system matches the
		 * sensors coordinate system with the origin in the center of the screen
		 * and the unit is the meter.
		 */

		
		final float x = xc + positionX * xs;
		final float y = yc - positionY * ys;
		canvas.drawRect(x, 0, x + 0.04f * mMetersToPixelsX + 0.5f, 0.04f * mMetersToPixelsX + 0.5f, shipPaint);

		// and make sure to redraw asap
		invalidate();

	}
}
