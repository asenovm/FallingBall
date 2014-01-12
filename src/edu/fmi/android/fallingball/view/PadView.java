package edu.fmi.android.fallingball.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
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
import edu.fmi.android.fallingball.OnPositionChangedListener;
import edu.fmi.android.fallingball.view.GameLayout.GameItem;

public class PadView extends View implements SensorEventListener {

	/**
	 * {@value}
	 */
	public static final String TAG = PadView.class.getSimpleName();

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

	private float positionX;

	private float positionY;

	private float accelerationX;

	private float accelerationY;

	private float horizontalBound;

	private float verticalBound;

	private float startX;

	private long mLastRenderTime;

	private float mRenderDelta;

	private float metersToPixelsX;

	private float sensorX;

	private float sensorY;

	private long sensorTimeStamp;

	private long cpuTimeStamp;

	private final Paint shipPaint;

	private final RectF boundingRect;

	private OnPositionChangedListener listener;

	public PadView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		final WindowManager windowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);

		DisplayMetrics metrics = new DisplayMetrics();
		windowManager.getDefaultDisplay().getMetrics(metrics);
		metersToPixelsX = metrics.xdpi / METRES_IN_INCH;

		final SensorManager sensorManager = (SensorManager) context
				.getSystemService(Context.SENSOR_SERVICE);
		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_UI);

		shipPaint = new Paint();
		shipPaint.setColor(Color.CYAN);

		boundingRect = new RectF();
	}

	public PadView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PadView(Context context) {
		this(context, null);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		final long renderTime = sensorTimeStamp
				+ (System.nanoTime() - cpuTimeStamp);

		if (mLastRenderTime != 0) {
			final float renderDelta = (renderTime - mLastRenderTime)
					/ NANOSECONDS_IN_A_SECOND;
			if (mRenderDelta != 0) {
				positionX = positionX + accelerationX * renderDelta
						* renderDelta;
				positionY = positionY + accelerationY * renderDelta
						* renderDelta;
				accelerationX = -sensorX;
				accelerationY = -sensorY;
			}
			mRenderDelta = renderDelta;
		}
		mLastRenderTime = renderTime;

		if (positionX > horizontalBound) {
			positionX = horizontalBound;
		} else if (positionX < -horizontalBound) {
			positionX = -horizontalBound;
		}
		if (positionY > verticalBound) {
			positionY = verticalBound;
		} else if (positionY < -verticalBound) {
			positionY = -verticalBound;
		}

		final float x = startX + positionX * metersToPixelsX;
		boundingRect.set(Math.max(0, x), verticalBound - HEIGHT_LINE,
				Math.round(x + LENGTH_LINE * metersToPixelsX), verticalBound);
		canvas.drawRect(boundingRect, shipPaint);
		listener.onPositionChanged(GameItem.PAD, boundingRect);

		invalidate();
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// blank
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

	public void setDimensions(final int width, final int height) {
		startX = Math.round(width - LENGTH_LINE * metersToPixelsX) * 0.5f;
		horizontalBound = (width / metersToPixelsX - LENGTH_LINE) * 0.5f;
		verticalBound = height;
	}

	public float getX() {
		return positionX;
	}

	public float getY() {
		return positionY;
	}

	public void setOnPositionChangedListener(
			final OnPositionChangedListener listener) {
		this.listener = listener;
	}

}
