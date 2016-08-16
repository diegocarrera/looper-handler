package com.workerlooper;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

  public LooperThread looperThread;
  Handler mHandler;


  public class MyThread implements Runnable {
    @Override
    public void run() {
      System.out.println("++++++++++++++inicia runnable-run");

      // agrega o genera un mensaje en blanco
      Message msg = Message.obtain();
      // setea un texto al mensaje
      msg.obj = "Hola mundo";

      //se envia un mensaje en un hilo diferente del hiloUI
      // este lo envia al messageQueue que utiliza el looperMain-UI

      // esta linea es similar a escribir handler.post(runnable)
      System.out.println("++++++++++++++envia mensaje al UI-Thread");
      mHandler.sendMessage(msg);

      //
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    System.out.println("++++++++++++++inicia app");


    //++++++++++++++++ 1. ENQUEUE A MESSAGE TO MESSAGEQUEUE
    // implica que se usa el looper del main

    mHandler = new Handler(){
      //funcion para recibir mensajes del MessageQueue acorde a
      //https://developer.android.com/reference/android/os/Handler.html

      //Solo los Msg.obj son permitidos en el MessageQueue.
      @Override
      public void handleMessage(Message msg) {
        System.out.println("recibe mensajes ...");
        System.out.println(msg.obj);
      }
    };

    /*
    System.out.println("++++++++++++++inicia Thread");
    Thread t = new Thread(new MyThread());
    t.start();
*/

    //+++++++++++++++++++ 2. ENQUEUE A RUNNABLE TO MESSAGEQUEUE

    System.out.println("++++++++++++++inicia Runnable");
    Runnable r = new Runnable() {
      @Override
      public void run() {
        System.out.println("My Runnable-run");
      }
    };
    Handler handler2 = new Handler();
    System.out.println("++++++++++++++envia runnable a MessageQueue");
    handler2.post(r);




  //  LooperThread looperThread = new LooperThread();
  //  looperThread.start();

  }

  @Override
  protected  void onDestroy(){
    super.onDestroy();
    //looperThread.quit();
  }
}
