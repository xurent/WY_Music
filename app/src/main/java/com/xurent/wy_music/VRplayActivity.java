package com.xurent.wy_music;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.xurent.myplayer.listener.WlOnCompleteListener;
import com.xurent.myplayer.listener.WlOnErrorListener;
import com.xurent.myplayer.listener.WlOnParparedListener;
import com.xurent.myplayer.listener.WlOnPauseResumeListener;
import com.xurent.myplayer.listener.WlOnloadListener;
import com.xurent.myplayer.log.MyLog;
import com.xurent.myplayer.player.WLPlayer;
import com.xurent.myplayer.vr.VrSurfaceView;

import androidx.appcompat.app.AppCompatActivity;

public class VRplayActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String VIDEO_URL = "http://cnvod.cnr.cn/audio2017/ondemand/transcode/l_target/wcm_system/video/20190403/xw0219xwyt22_56/index.m3u8";
    //http://cnvod.cnr.cn/audio2017/ondemand/transcode/l_target/wcm_system/video/20190403/xw0219xwyt22_56/index.m3u8
    //private static final String VIDEO_URL = "http://video.newsapp.cnr.cn/data/video/2019/27675/index.m3u8";

    private Button changeDisplayModeBtn;
    private Button changeInteRactionModeBtn;

    private VrSurfaceView vrSurfaceView;
    private WLPlayer player;
    private SensorManager sensorManager;
    private Sensor mRotation;
    private final float[] mRotateMatrix = new float[16];
    private float[] mTempRotateMatrix = new float[16];
    private int mDeviceRotation = Surface.ROTATION_90;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_vr_glsurface_view);

        initView();

        initSensor();
    }

    //传感器的数据监听
    private void initSensor() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mRotation=sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        sensorManager.registerListener(new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                setRotateMatrix(event);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        }, mRotation, SensorManager.SENSOR_DELAY_GAME);
    }

    private void setRotateMatrix(SensorEvent event) {
        //Log.e("setRotateMatrix","setRotateMatrix");
        //横竖屏转换处理
        SensorManager.getRotationMatrixFromVector(mTempRotateMatrix, event.values);
        float[] values = event.values;
        switch (mDeviceRotation){
            case Surface.ROTATION_0:
                SensorManager.getRotationMatrixFromVector(mRotateMatrix, values);
                break;
            case Surface.ROTATION_90:
                SensorManager.getRotationMatrixFromVector(mTempRotateMatrix, values);
                SensorManager.remapCoordinateSystem(mTempRotateMatrix, SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, mRotateMatrix);
                break;
        }

        vrSurfaceView.getVrGlassGLVideoRenderer().setuRotateMatrix(mRotateMatrix);
    }

    private void initView() {
        changeDisplayModeBtn = findViewById(R.id.change_display_mode_btn);
        changeInteRactionModeBtn = findViewById(R.id.change_interaction_mode_btn);
        changeDisplayModeBtn.setOnClickListener(this);
        changeInteRactionModeBtn.setOnClickListener(this);

        vrSurfaceView=findViewById(R.id.play_vr_glsv);
        player=new WLPlayer();
        player.setWlOnParparedListener(new WlOnParparedListener() {
            @Override
            public void onParpared() {
                MyLog.d("开始播放----");
                System.out.println("播放");
                player.start();
            }
        });
        player.setWlOnloadListener(new WlOnloadListener() {
            @Override
            public void onload(boolean load) {
                if(load){
                    MyLog.d("加载中....");
                }else{
                    MyLog.d("播放中....");
                }
            }
        });
        player.setWlOnPauseResumeListener(new WlOnPauseResumeListener() {
            @Override
            public void onPause(boolean pause) {
                if(pause){
                    MyLog.d("暂停中...");
                }else{
                    MyLog.d("播放----中。。。");
                }
            }
        });
        player.setWlOnErrorListener(new WlOnErrorListener() {
            @Override
            public void onError(int code, String msg) {
                MyLog.d("code:"+code+",msg:"+msg);
            }
        });
        player.setWlOnCompleteListener(new WlOnCompleteListener() {
            @Override
            public void onComplete() {
                MyLog.d("播放完成了");
            }
        });

        player.setOpenVr(true);
        player.setVrSurfaceView(vrSurfaceView);
        player.setSource(VIDEO_URL);
        player.parpared();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        WindowManager windowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        mDeviceRotation = windowManager.getDefaultDisplay().getRotation();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.change_display_mode_btn:
                changeDisplayMode();
                break;
            case R.id.change_interaction_mode_btn:
                changeInteRactionMode();
                break;
        }
    }

    private void changeInteRactionMode() {
        vrSurfaceView.getVrGlassGLVideoRenderer().changeInteractionMode();
    }

    private void changeDisplayMode() {
        vrSurfaceView.getVrGlassGLVideoRenderer().changeDisplayMode();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.stop();
    }


}
