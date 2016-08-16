package com.workerlooper;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * Created by isabel on 15/08/16.
 */
public class LooperThread extends Thread{
  public Handler mHandler;

  @Override
  public void run(){
    Looper.prepare();

    mHandler = new Handler(){
      @Override
      public void handleMessage(Message msg){

      }
    };
    Looper.loop();
  }

  public void quit() {
    this.quit();
  }
}
