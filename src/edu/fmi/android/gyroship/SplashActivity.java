package edu.fmi.android.gyroship;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

public class SplashActivity extends Activity {

	/**
	 * {@value}
	 */
	private static final int INTERVAL_COUNTDOWN_TICK = 2000;

	/**
	 * {@value}
	 */
	private static final int INTERVAL_COUNTDOWN_TIMER = 2000;

	private class SplashTimer extends CountDownTimer {

		public SplashTimer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			finish();

			final Intent homeActivityIntent = new Intent(SplashActivity.this,
					HomeActivity.class);
			startActivity(homeActivityIntent);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			// blank
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		final CountDownTimer timer = new SplashTimer(INTERVAL_COUNTDOWN_TIMER,
				INTERVAL_COUNTDOWN_TICK);
		timer.start();
	}

}
