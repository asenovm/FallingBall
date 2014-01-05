package edu.fmi.android.gyroship;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;

public class GameActivity extends Activity {

	private SimulationView mSimulationView;
	private SensorManager mSensorManager;
	private WindowManager mWindowManager;
	private Display mDisplay;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Get an instance of the SensorManager
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

		// Get an instance of the WindowManager
		mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		mDisplay = mWindowManager.getDefaultDisplay();

		// instantiate our simulation view and set it as the activity's content
		mSimulationView = new SimulationView(this);
		setContentView(mSimulationView);
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Start the simulation
		mSimulationView.startSimulation();
	}

	@Override
	protected void onPause() {
		super.onPause();
		/*
		 * When the activity is paused, we make sure to stop the simulation,
		 * release our sensor resources and wake locks
		 */

		// Stop the simulation
		mSimulationView.stopSimulation();
	}

	class SimulationView extends View implements SensorEventListener {
		// diameter of the balls in meters
		private static final float sBallDiameter = 0.004f;

		private Sensor mAccelerometer;
		private long mLastT;
		private float mLastDeltaT;

		private float mXDpi;
		private float mYDpi;
		private float mMetersToPixelsX;
		private float mMetersToPixelsY;
		private float mXOrigin;
		private float mYOrigin;
		private float mSensorX;
		private float mSensorY;
		private long mSensorTimeStamp;
		private long mCpuTimeStamp;
		private float mHorizontalBound;
		private float mVerticalBound;
		private final ParticleSystem mParticleSystem = new ParticleSystem();

		/*
		 * Each of our particle holds its previous and current position, its
		 * acceleration. for added realism each particle has its own friction
		 * coefficient.
		 */
		class Particle {
			private float mPosX;
			private float mPosY;
			private float mAccelX;
			private float mAccelY;

			public void computePhysics(float sx, float sy, float dT) {
				final float dTdT = dT * dT;
				final float x = mPosX + mAccelX * dTdT;
				final float y = mPosY + mAccelY * dTdT;
				mPosX = x;
				mPosY = y;
				mAccelX = -sx;
				mAccelY = -sy;
			}

			/*
			 * Resolving constraints and collisions with the Verlet integrator
			 * can be very simple, we simply need to move a colliding or
			 * constrained particle in such way that the constraint is
			 * satisfied.
			 */
			public void resolveCollisionWithBounds() {
				final float xmax = mHorizontalBound;
				final float ymax = mVerticalBound;
				final float x = mPosX;
				final float y = mPosY;
				if (x > xmax) {
					mPosX = xmax;
				} else if (x < -xmax) {
					mPosX = -xmax;
				}
				if (y > ymax) {
					mPosY = ymax;
				} else if (y < -ymax) {
					mPosY = -ymax;
				}
			}
		}

		/*
		 * A particle system is just a collection of particles
		 */
		class ParticleSystem {
			static final int NUM_PARTICLES = 1;
			private Particle mBalls[] = new Particle[NUM_PARTICLES];

			ParticleSystem() {
				/*
				 * Initially our particles have no speed or acceleration
				 */
				for (int i = 0; i < mBalls.length; i++) {
					mBalls[i] = new Particle();
				}
			}

			private void updatePositions(float sx, float sy, long timestamp) {
				final long t = timestamp;
				if (mLastT != 0) {
					final float dT = (float) (t - mLastT)
							* (1.0f / 1000000000.0f);
					if (mLastDeltaT != 0) {
						final int count = mBalls.length;
						for (int i = 0; i < count; i++) {
							Particle ball = mBalls[i];
							ball.computePhysics(sx, sy, dT);
						}
					}
					mLastDeltaT = dT;
				}
				mLastT = t;
			}

			/*
			 * Performs one iteration of the simulation. First updating the
			 * position of all the particles and resolving the constraints and
			 * collisions.
			 */
			public void update(float sx, float sy, long now) {
				// update the system's positions
				updatePositions(sx, sy, now);
				for (int i = 0; i < mBalls.length; i++) {
					Particle curr = mBalls[i];
					curr.resolveCollisionWithBounds();
				}
			}

			public int getParticleCount() {
				return mBalls.length;
			}

			public float getPosX(int i) {
				return mBalls[i].mPosX;
			}

			public float getPosY(int i) {
				return mBalls[i].mPosY;
			}
		}

		public void startSimulation() {
			/*
			 * It is not necessary to get accelerometer events at a very high
			 * rate, by using a slower rate (SENSOR_DELAY_UI), we get an
			 * automatic low-pass filter, which "extracts" the gravity component
			 * of the acceleration. As an added benefit, we use less power and
			 * CPU resources.
			 */
			mSensorManager.registerListener(this, mAccelerometer,
					SensorManager.SENSOR_DELAY_UI);
		}

		public void stopSimulation() {
			mSensorManager.unregisterListener(this);
		}

		public SimulationView(Context context) {
			super(context);
			mAccelerometer = mSensorManager
					.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

			DisplayMetrics metrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(metrics);
			mXDpi = metrics.xdpi;
			mYDpi = metrics.ydpi;
			mMetersToPixelsX = mXDpi / 0.0254f;
			mMetersToPixelsY = mYDpi / 0.0254f;

			Options opts = new Options();
			opts.inDither = true;
			opts.inPreferredConfig = Bitmap.Config.RGB_565;
		}

		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh) {
			// compute the origin of the screen relative to the origin of
			// the bitmap
			mXOrigin = (w - sBallDiameter * mMetersToPixelsX + 0.5f) * 0.5f;
			mYOrigin = (h - sBallDiameter * mMetersToPixelsY + 0.5f) * 0.5f;
			mHorizontalBound = ((w / mMetersToPixelsX - sBallDiameter) * 0.5f);
			mVerticalBound = ((h / mMetersToPixelsY - sBallDiameter) * 0.5f);
		}

		@SuppressLint("NewApi")
		@Override
		public void onSensorChanged(SensorEvent event) {
			if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER)
				return;
			/*
			 * record the accelerometer data, the event's timestamp as well as
			 * the current time. The latter is needed so we can calculate the
			 * "present" time during rendering. In this application, we need to
			 * take into account how the screen is rotated with respect to the
			 * sensors (which always return data in a coordinate space aligned
			 * to with the screen in its native orientation).
			 */

			switch (mDisplay.getRotation()) {
			case Surface.ROTATION_0:
				mSensorX = event.values[0];
				mSensorY = event.values[1];
				break;
			case Surface.ROTATION_90:
				mSensorX = -event.values[1];
				mSensorY = event.values[0];
				break;
			case Surface.ROTATION_180:
				mSensorX = -event.values[0];
				mSensorY = -event.values[1];
				break;
			case Surface.ROTATION_270:
				mSensorX = event.values[1];
				mSensorY = -event.values[0];
				break;
			}

			mSensorTimeStamp = event.timestamp;
			mCpuTimeStamp = System.nanoTime();
		}

		@Override
		protected void onDraw(Canvas canvas) {

			/*
			 * compute the new position of our object, based on accelerometer
			 * data and present time.
			 */

			final ParticleSystem particleSystem = mParticleSystem;
			final long now = mSensorTimeStamp
					+ (System.nanoTime() - mCpuTimeStamp);
			final float sx = mSensorX;
			final float sy = mSensorY;

			particleSystem.update(sx, sy, now);

			final float xc = mXOrigin;
			final float yc = mYOrigin;
			final float xs = mMetersToPixelsX;
			final float ys = mMetersToPixelsY;
			final int count = particleSystem.getParticleCount();
			for (int i = 0; i < count; i++) {
				/*
				 * We transform the canvas so that the coordinate system matches
				 * the sensors coordinate system with the origin in the center
				 * of the screen and the unit is the meter.
				 */

				final float x = xc + particleSystem.getPosX(i) * xs;
				final float y = yc - particleSystem.getPosY(i) * ys;
				final Paint paint = new Paint();
				paint.setColor(Color.CYAN);
				canvas.drawRect(x, y, x + sBallDiameter * mMetersToPixelsX
						+ 0.5f, y + sBallDiameter * mMetersToPixelsX + 0.5f,
						paint);
			}

			// and make sure to redraw asap
			invalidate();
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}
	}

}