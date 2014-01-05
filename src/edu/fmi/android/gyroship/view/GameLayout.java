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
import android.view.WindowManager;
import android.widget.RelativeLayout;

public class GameLayout extends RelativeLayout implements SensorEventListener {

	/**
	 * {@value}
	 */
	@SuppressWarnings("unused")
	private static final String TAG = GameLayout.class.getSimpleName();

	/**
	 * {@value}
	 */
	private static final int HEIGHT_LINE = 20;

	/**
	 * {@value}
	 */
	private static final float NANOSECONDS_IN_A_SECOND = 1000000000.0f;

	/**
	 * {@value}
	 */
	private static final float METRES_IN_INCH = 0.0254f;

	/**
	 * {@value}
	 */
	private static final float LENGTH_LINE = 0.015f;

	private float xOrigin;

	private float horizontalBound;

	private float verticalBound;

	private float sensorX;

	private float sensorY;

	private long sensorTimeStamp;

	private long cpuTimeStamp;

	private final ShipView shipView;

	private long mLastT;

	private float mLastDeltaT;

	private float xDpi;

	private float metersToPixelsX;

	private SensorManager sensorManager;

	public GameLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		shipView = new ShipView(context, attrs, defStyle);

		final WindowManager windowManager = (WindowManager) getContext()
				.getSystemService(Context.WINDOW_SERVICE);

		DisplayMetrics metrics = new DisplayMetrics();
		windowManager.getDefaultDisplay().getMetrics(metrics);
		xDpi = metrics.xdpi;
		metersToPixelsX = xDpi / METRES_IN_INCH;

		sensorManager = (SensorManager) context
				.getSystemService(Context.SENSOR_SERVICE);
		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_GAME);
	}

	public GameLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public GameLayout(Context context) {
		this(context, null);
	}

	private void updatePositions(float sx, float sy, long timestamp) {
		final long t = timestamp;
		if (mLastT != 0) {
			final float dT = (t - mLastT) / NANOSECONDS_IN_A_SECOND;
			if (mLastDeltaT != 0) {
				shipView.computePhysics(sx, sy, dT);
			}
			mLastDeltaT = dT;
		}
		mLastT = t;
	}

	public void update(float sx, float sy, long now) {
		updatePositions(sx, sy, now);
		shipView.resolveCollisionWithBounds();
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		final long now = sensorTimeStamp + (System.nanoTime() - cpuTimeStamp);
		final float sx = sensorX;
		final float sy = sensorY;

		update(sx, sy, now);

		final float xc = xOrigin;
		final float xs = metersToPixelsX;
		final float x = xc + shipView.getPosX() * xs;

		final Paint paint = new Paint();
		paint.setColor(Color.CYAN);

		canvas.drawRect(x, verticalBound - HEIGHT_LINE, Math.round(x + LENGTH_LINE * metersToPixelsX),
				verticalBound, paint);

		invalidate();
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		final WindowManager windowManager = (WindowManager) getContext()
				.getSystemService(Context.WINDOW_SERVICE);
		final Display display = windowManager.getDefaultDisplay();

		switch (display.getRotation()) {
		case Surface.ROTATION_0:
			sensorX = event.values[0];
			sensorY = event.values[1];
			break;
		case Surface.ROTATION_90:
			sensorX = -event.values[1];
			sensorY = event.values[0];
			break;
		case Surface.ROTATION_180:
			sensorX = -event.values[0];
			sensorY = -event.values[1];
			break;
		case Surface.ROTATION_270:
			sensorX = event.values[1];
			sensorY = -event.values[0];
			break;
		}

		sensorTimeStamp = event.timestamp;
		cpuTimeStamp = System.nanoTime();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		xOrigin = Math.round(w - LENGTH_LINE * metersToPixelsX) * 0.5f;
		horizontalBound = ((w / metersToPixelsX - LENGTH_LINE) * 0.5f);
		verticalBound = h;
		shipView.setBounds(horizontalBound, verticalBound);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// blank
	}

}
