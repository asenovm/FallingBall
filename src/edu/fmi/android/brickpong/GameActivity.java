package edu.fmi.android.brickpong;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import edu.fmi.android.brickpong.listeners.OnGameEventsListener;
import edu.fmi.android.brickpong.view.GameLayout;

public class GameActivity extends Activity implements OnGameEventsListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final GameLayout gameLayout = new GameLayout(this);
		gameLayout.setOnGameEventsListener(this);
		setContentView(gameLayout);

		final Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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