package edu.fmi.android.fallingball;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class HomeActivity extends Activity {

	/**
	 * {@value}
	 */
	private static final String TAG = HomeActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
	}

	public void startGame(final View clickedView) {
		final Intent gameIntent = new Intent(this, GameActivity.class);
		startActivity(gameIntent);
	}

	public void openSettings(final View clickedView) {
		Log.i(TAG, "settings opened");
	}

	public void openAchievements(final View clickedView) {
		Log.i(TAG, "achievements opened!");
	}
}
