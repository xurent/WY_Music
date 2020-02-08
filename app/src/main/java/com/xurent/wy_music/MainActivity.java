package com.xurent.wy_music;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.xurent.myplayer.TimeInfoBean;
import com.xurent.myplayer.listener.WlOnCompleteListener;
import com.xurent.myplayer.listener.WlOnErrorListener;
import com.xurent.myplayer.listener.WlOnParparedListener;
import com.xurent.myplayer.listener.WlOnPauseResumeListener;
import com.xurent.myplayer.listener.WlOnloadListener;
import com.xurent.myplayer.listener.WlOntimeInfoListener;
import com.xurent.myplayer.log.MyLog;
import com.xurent.myplayer.opengl.GlSurfaceView;
import com.xurent.myplayer.player.WLPlayer;
import com.xurent.myplayer.util.WlTimeUtil;

public class MainActivity extends AppCompatActivity {

   private WLPlayer wlPlayer;
   private  TextView tv_Time;
    private GlSurfaceView glSurfaceView;
    private SeekBar seekBar;
    private  int position;
    private  boolean seek=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_Time=findViewById(R.id.tv_time);
        glSurfaceView=findViewById(R.id.wlglsurfaceview);
        seekBar=findViewById(R.id.seekbar);
        System.out.println("初始化完成");
        wlPlayer=new WLPlayer();
        wlPlayer.setGlSurfaceView(glSurfaceView);
        wlPlayer.setWlOnParparedListener(new WlOnParparedListener() {
            @Override
            public void onParpared() {
                MyLog.d("开始播放----");
                System.out.println("播放");
                wlPlayer.start();
            }
        });
        wlPlayer.setWlOnloadListener(new WlOnloadListener() {
            @Override
            public void onload(boolean load) {
                if(load){
                        MyLog.d("加载中....");
                }else{
                    MyLog.d("播放中....");
                }
            }
        });
        wlPlayer.setWlOnPauseResumeListener(new WlOnPauseResumeListener() {
            @Override
            public void onPause(boolean pause) {
                if(pause){
                    MyLog.d("暂停中...");
                }else{
                    MyLog.d("播放----中。。。");
                }
            }
        });
        wlPlayer.setWlOntimeInfoListener(new WlOntimeInfoListener() {
            @Override
            public void onTimeInfo(TimeInfoBean timeInfoBean) {
               // MyLog.d(timeInfoBean.toString());
                Message msg=Message.obtain();
                msg.what=1;
                msg.obj=timeInfoBean;
                handler.sendMessage(msg);
            }
        });
        wlPlayer.setWlOnErrorListener(new WlOnErrorListener() {
            @Override
            public void onError(int code, String msg) {
                MyLog.d("code:"+code+",msg:"+msg);
            }
        });
        wlPlayer.setWlOnCompleteListener(new WlOnCompleteListener() {
            @Override
            public void onComplete() {
                MyLog.d("播放完成了");
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    position=progress*wlPlayer.getDuration()/100;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                seek=true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                    wlPlayer.seek(position);
                    seek=false;
            }
        });
    }


    public void Player(View view) {
        //http://vfx.mtime.cn/Video/2019/03/21/mp4/190321153853126488.mp4
        wlPlayer.setSource("http://vfx.mtime.cn/Video/2019/03/21/mp4/190321153853126488.mp4");
        wlPlayer.parpared();

    }

    public void pause(View view) {
        wlPlayer.pause();
    }

    public void resume(View view) {
        wlPlayer.resume();
    }


    Handler handler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(msg.what==1){
                TimeInfoBean timeInfoBean= (TimeInfoBean) msg.obj;
                tv_Time.setText(WlTimeUtil.secdsToDateFormat(timeInfoBean.getTotalTime(),timeInfoBean.getTotalTime())
                +"/"+WlTimeUtil.secdsToDateFormat(timeInfoBean.getCurrentTime(),timeInfoBean.getTotalTime()));

                if(!seek&&timeInfoBean.getTotalTime()>0){
                    seekBar.setProgress(timeInfoBean.getCurrentTime()*100/timeInfoBean.getTotalTime());
                }
            }

        }
    };

    public void stop(View view) {
            wlPlayer.stop();
    }




    public void next(View view) {
        wlPlayer.playNext("http://sc1.111ttt.cn/2018/1/03/13/396131202421.mp3");
        wlPlayer.onCallNext();
    }

    public void VRplay(View view) {

        Intent intent=new Intent(this,VRplayActivity.class);
        startActivity(intent);

    }
}
