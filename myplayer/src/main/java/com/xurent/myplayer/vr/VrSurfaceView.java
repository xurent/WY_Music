package com.xurent.myplayer.vr;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;


public class VrSurfaceView extends GLSurfaceView {

    private VRGlassGLVideoRenderer vrGlassGLVideoRenderer;

    public VrSurfaceView(Context context) {
        this(context, null);
    }

    public VrSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEGLContextClientVersion(3);
        vrGlassGLVideoRenderer = new VRGlassGLVideoRenderer(context);
        setRenderer(vrGlassGLVideoRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        vrGlassGLVideoRenderer.setOnRenderListener(new VRGlassGLVideoRenderer.OnRenderListener() {
            @Override
            public void onRender() {
                requestRender();
            }
        });

    }

    public void setYUVData(int width, int height, byte[] y, byte[] u, byte[] v) {
        if (vrGlassGLVideoRenderer != null) {
            //vrGlassGLVideoRenderer.setYUVRenderData(width, height, y, u, v);
            Log.d("------VRVRVRVRVRV","YUV数据传输");
            requestRender();
        }
    }

    public VRGlassGLVideoRenderer getVrGlassGLVideoRenderer() {
        return vrGlassGLVideoRenderer;
    }


    public void changeInteRactionMode() {
        vrGlassGLVideoRenderer.changeInteractionMode();
    }

    public void changeDisplayMode() {
        vrGlassGLVideoRenderer.changeDisplayMode();
    }

}
