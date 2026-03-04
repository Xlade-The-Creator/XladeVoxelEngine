package com.xtc.myVoxelEngine;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.opengl.GLSurfaceView;

public class MainActivity extends AppCompatActivity {

    private GLSurfaceView glSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
		
        super.onCreate(savedInstanceState);
		
		glSurfaceView = new MyGLSurfaceView(this);
		
        setContentView(glSurfaceView);
	};
		
	@Override
	protected void onPause() {
		super.onPause();
		glSurfaceView.onPause();
	};
		
	@Override
	protected void onResume(){
		super.onResume();
		glSurfaceView.onResume();
	}
  ;
}
