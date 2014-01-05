package edu.fmi.android.gyroship;

import android.app.Activity;
import android.os.Bundle;
import edu.fmi.android.gyroship.view.GameLayout;

public class GameActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(new GameLayout(this));
	}

}