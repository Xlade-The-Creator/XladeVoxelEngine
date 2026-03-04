package com.xtc.myVoxelEngine;

import android.content.Context;
import android.opengl.GLSurfaceView;


public class MyGLSurfaceView extends GLSurfaceView {

    private final MyGLRenderer renderer;

    public MyGLSurfaceView(Context context) {
        super(context);
		
        setEGLContextClientVersion(3);
		
		setEGLConfigChooser(8, 8, 8, 8, 16, 0);

        renderer = new MyGLRenderer();

        setRenderer(renderer);

		setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
 
};
}
