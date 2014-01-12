package edu.fmi.android.gyroship;

import android.app.Activity;
import android.os.Bundle;
import edu.fmi.android.gyroship.listeners.OnGameEventsListener;
import edu.fmi.android.gyroship.view.GameLayout;

public class GameActivity extends Activity implements OnGameEventsListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final GameLayout gameLayout = new GameLayout(this);
		gameLayout.setOnGameFinishListener(this);
		setContentView(gameLayout);
	}

	@Override
	public void onGameEnd() {
		finish();
	}

}