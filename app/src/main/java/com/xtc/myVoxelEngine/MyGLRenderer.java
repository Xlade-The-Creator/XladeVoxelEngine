package com.xtc.myVoxelEngine;

import android.util.Log;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import android.opengl.Matrix;

public class MyGLRenderer implements GLSurfaceView.Renderer {
	
	private FloatBuffer vertexBuffer;
	
	private FloatBuffer colorBuffer;
	
	static final int COORDS_PER_VERTEX = 3;
	
	private ShortBuffer drawListBuffer;
	
	private int mProgram;
	
	private final String vertexShaderCode = "uniform mat4 u_MVPMatrix;" + "attribute vec4 vPosition;" + "attribute vec4 aColor;" + "varying vec4 vColor;" + "void main() {" + 
	
	"gl_Position = u_MVPMatrix * vPosition;" + "vColor = aColor;" +
		
	"}";
	
	private final String fragmentShaderCode = "precision mediump float;" + "varying vec4 vColor;" + "void main() {" +
		
		"gl_FragColor = vColor;" + 
		
	"}";
	
	private int vPMatrixHandle;
	
	private int mPositionHandle;
	
	private int mColorHandle;
	
	private final float[] vMatrix = new float[16];
	
	private final float[] pMatrix = new float[16];
	
	private final float[] mvpMatrix = new float[16];
	
	private final float[] mMatrix = new float[16];
	
    private final float[] cubeVertices = {
		
		-0.5f, 0.5f, 0.5f,
	    -0.5f, -0.5f, 0.5f,
	    0.5f, -0.5f, 0.5f,
	    0.5f, 0.5f, 0.5f,
		
		0.5f, 0.5f, -0.5f,
		0.5f, -0.5f, -0.5f,
		-0.5f, -0.5f, -0.5f,
		-0.5f, 0.5f, -0.5f,
		
		-0.5f, 0.5f, -0.5f,
		-0.5f, -0.5f, -0.5f,
		-0.5f, -0.5f, 0.5f,
		-0.5f, 0.5f, 0.5f,
		
		0.5f, 0.5f, 0.5f,
	    0.5f, -0.5f, 0.5f,
	    0.5f, -0.5f, -0.5f,
	    0.5f, 0.5f, -0.5f,
		
		-0.5f, 0.5f, -0.5f,
		-0.5f, 0.5f, 0.5f,
		0.5f, 0.5f, 0.5f,
		0.5f, 0.5f, -0.5f,
		
		-0.5f, -0.5f, 0.5f,
		-0.5f, -0.5f, -0.5f,
		0.5f, -0.5f, -0.5f,
		0.5f, -0.5f, 0.5f,
	
    };
	
	private final float[] cubeColors = {
		
		1.0f, 0.0f, 0.0f, 1.0f,
	    1.0f, 0.0f, 0.0f, 1.0f,
	    1.0f, 0.0f, 0.0f, 1.0f,
	    1.0f, 0.0f, 0.0f, 1.0f,
		
	    0.0f, 1.0f, 0.0f, 1.0f,
	    0.0f, 1.0f, 0.0f, 1.0f,
	    0.0f, 1.0f, 0.0f, 1.0f,
		0.0f, 1.0f, 0.0f, 1.0f,
		
		0.0f, 0.0f, 1.0f, 1.0f,
	    0.0f, 0.0f, 1.0f, 1.0f,
	    0.0f, 0.0f, 1.0f, 1.0f,
	    0.0f, 0.0f, 1.0f, 1.0f,
		
	    1.0f, 0.5f, 0.0f, 1.0f,
	    1.0f, 0.5f, 0.0f, 1.0f,
	    1.0f, 0.5f, 0.0f, 1.0f,
		1.0f, 0.5f, 0.0f, 1.0f,
		
		1.0f, 1.0f, 0.0f, 1.0f,
	    1.0f, 1.0f, 0.0f, 1.0f,
	    1.0f, 1.0f, 0.0f, 1.0f,
		1.0f, 1.0f, 0.0f, 1.0f,
		
		1.0f, 0.0f, 1.0f, 1.0f,
	    1.0f, 0.0f, 1.0f, 1.0f,
	    1.0f, 0.0f, 1.0f, 1.0f,
		1.0f, 0.0f, 1.0f, 1.0f
	
    };
	
	private final short[] drawOrder = {
		
		0, 1, 2, 
		0, 2, 3,
		
		4, 5, 6,
		4, 6, 7,
		
		8, 9, 10,
		8, 10, 11,
		
		12, 13, 14, 
		12, 14, 15,
		
		16, 17, 18,
		16, 18, 19,
		
		20, 21, 22,
		20, 22, 23
	};
	
	@Override
	    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
	    	
	    	GLES30.glClearColor(0.290f, 0.690f, 0.443f, 0.8f);
	    	
	    	GLES30.glEnable(GLES30.GL_DEPTH_TEST);
			
			GLES30.glEnable(GLES30.GL_CULL_FACE);
			
			GLES30.glCullFace(GLES30.GL_BACK);
		    
	    	ByteBuffer bb = ByteBuffer.allocateDirect(cubeVertices.length * 4);
	    	bb.order(ByteOrder.nativeOrder());
	    	vertexBuffer = bb.asFloatBuffer();
	    	vertexBuffer.put(cubeVertices);
	    	vertexBuffer.position(0);
		
		    ByteBuffer dlb = ByteBuffer.allocateDirect(drawOrder.length * 2);
			
			dlb.order(ByteOrder.nativeOrder());
			drawListBuffer = dlb.asShortBuffer();
			drawListBuffer.put(drawOrder);
			drawListBuffer.position(0);
			
			ByteBuffer cbb = ByteBuffer.allocateDirect(cubeColors.length * 4);
			cbb.order(ByteOrder.nativeOrder());
			colorBuffer = cbb.asFloatBuffer();
			colorBuffer.put(cubeColors);
			colorBuffer.position(0);
			
	    	int vertexShader = loadShader(GLES30.GL_VERTEX_SHADER, vertexShaderCode);
	    	int fragmentShader = loadShader(GLES30.GL_FRAGMENT_SHADER, fragmentShaderCode);
		
	    	mProgram = GLES30.glCreateProgram();
	    	GLES30.glAttachShader(mProgram, vertexShader);
	    	GLES30.glAttachShader(mProgram, fragmentShader);
		
	    	GLES30.glLinkProgram(mProgram);
			
			vPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "u_MVPMatrix");
			
	    };
	
	@Override
	    public void onSurfaceChanged(GL10 unused, int width, int height) {
			
	    	GLES30.glViewport(0, 0, width, height);
			
			float ratio = (float) width / height;
			
			Matrix.frustumM(pMatrix, 0, -ratio, ratio, -1, 1, 1.0f, 20.0f);
			
			Log.d("Voxel", "Handle: " + "vPMatrixHandle");
			
	    };
		
    @Override
	    public void onDrawFrame(GL10 unused) {
			
	    	GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);
		
	    	GLES30.glUseProgram(mProgram);
			
			Matrix.setIdentityM(mMatrix, 0);
			
			Matrix.setLookAtM(vMatrix, 0, -2.0f, 3.0f, -5.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
			
			Matrix.scaleM(mMatrix, 0, 2.0f, 2.0f, 2.0f);
			
			long time = System.currentTimeMillis() % 4000L;
			
			float angle = 0.090f * ((int) time);
			
			//Matrix.rotateM(mMatrix, 0, angle, 1.0f, 1.0f, 1.0f);
			
			float[] vPMatrix = new float[16];
			
			Matrix.multiplyMM(vPMatrix, 0, pMatrix, 0, vMatrix, 0);
			
			Matrix.multiplyMM(mvpMatrix, 0, vPMatrix, 0, mMatrix, 0);
			
	        GLES30.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0);
			
			mPositionHandle = GLES30.glGetAttribLocation(mProgram, "vPosition");
		
	    	GLES30.glEnableVertexAttribArray(mPositionHandle);
	    	GLES30.glVertexAttribPointer(mPositionHandle, 3, GLES30.GL_FLOAT, false, 0, vertexBuffer);
		
		    mColorHandle = GLES30.glGetAttribLocation(mProgram, "aColor");
			
			GLES30.glEnableVertexAttribArray(mColorHandle);
			GLES30.glVertexAttribPointer(mColorHandle, 4, GLES30.GL_FLOAT, false, 0, colorBuffer);
			
	   //int mColorHandle = GLES30.glGetUniformLocation(mProgram, "vColor");
		
	    //  float color[] = {
			
	    //		0.9f, 0.4f, 0.7f, 1.0f
			
	    //     };
		
	   //GLES30.glUniform4fv(mColorHandle, 1, color, 0);
			GLES30.glDrawElements(GLES30.GL_TRIANGLES, drawOrder.length, GLES30.GL_UNSIGNED_SHORT, drawListBuffer);
		
	    	GLES30.glDisableVertexAttribArray(mPositionHandle);
			
			GLES30.glDisableVertexAttribArray(mColorHandle);
			
	    };
	
	public static int loadShader(int type, String shaderCode){
		
		int shader = GLES30.glCreateShader(type);
		GLES30.glShaderSource(shader, shaderCode);
		GLES30.glCompileShader(shader);
		return shader;
		
	};
	
};