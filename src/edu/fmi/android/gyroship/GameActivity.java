package edu.fmi.android.gyroship;

import android.app.Activity;
import android.os.Bundle;
import edu.fmi.android.gyroship.view.GameLayout;

public class GameActivity extends Activity {
	
	private GameLayout gameLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		gameLayout = new GameLayout(this);
		setContentView(gameLayout);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		gameLayout.onResume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		gameLayout.onPause();
	}

}
