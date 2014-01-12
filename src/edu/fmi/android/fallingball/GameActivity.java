package edu.fmi.android.fallingball;

import android.app.Activity;
import android.os.Bundle;
import edu.fmi.android.fallingball.listeners.OnGameEventsListener;
import edu.fmi.android.fallingball.view.GameLayout;

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

	@Override
	public void onGameScoreChanged() {
		// blank
	}

}