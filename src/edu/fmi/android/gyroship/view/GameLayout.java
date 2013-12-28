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
import android.widget.RelativeLayout;

public class GameLayout extends RelativeLayout {

	/**
	 * {@value}
	 */
	@SuppressWarnings("unused")
	private static final String TAG = GameLayout.class.getSimpleName();

	private final SensorEventListener accelerometerListener;

	private final Paint shipPaint;

	private TiltDirection direction;

	private enum TiltDirection {
		LEFT, RIGHT;
	}

	private class GyroSensorListener implements SensorEventListener {

		/**
		 * {@value}
		 */
		private static final double THRESHOLD_TILT_LEFT = -0.3;

		/**
		 * {@value}
		 */
		private static final double THRESHOLD_TILT_RIGHT = 0.3;

		private final float[] historyX = new float[10];

		public void onSensorChanged(SensorEvent event) {
			for (int i = 0; i < historyX.length - 1; ++i) {
				historyX[i + 1] = historyX[i];
			}
			historyX[0] = event.values[0];

			float totalX = 0F, averageX = 0F;
			for (int i = 0; i < historyX.length; ++i) {
				totalX += historyX[i];
			}

			averageX = totalX / historyX.length;
			if (averageX > THRESHOLD_TILT_RIGHT) {
				direction = TiltDirection.RIGHT;
				invalidate();
			} else if (averageX < THRESHOLD_TILT_LEFT) {
				direction = TiltDirection.LEFT;
				invalidate();
			}
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// blank
		}

	}

	public GameLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		accelerometerListener = new GyroSensorListener();

		shipPaint = new Paint();
		shipPaint.setColor(Color.MAGENTA);
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

	public GameLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public GameLayout(Context context) {
		this(context, null);
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);

		canvas.drawRect(0, 0, 20, 20, shipPaint);
	}

}
