package com.looperexample;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

//http://androidsrc.net/android-loopers-and-handlers-code-tutorial/

public final class DownloadThread extends Thread {

  private static final String TAG = DownloadThread.class.getSimpleName();
  private Handler handler;
  private int totalQueued;
  private int totalCompleted;
  private DownloadThreadListener listener;
  public DownloadThread(DownloadThreadListener listener) {
    this.listener = listener;
  }

  @Override
  public void run() {
    try {
      // preparing a looper on current thread
      // the current thread is being detected implicitly
      Looper.prepare();

      Log.i(TAG, "DownloadThread entering the loop");

      // now, the handler will automatically bind to the
      // Looper that is attached to the current thread
      // You don't need to specify the Looper explicitly
      handler = new Handler();

      // After the following line the thread will start
      // running the message loop and will not normally
      // exit the loop unless a problem happens or you
      // quit() the looper (see below)
      Looper.loop();

      Log.i(TAG, "DownloadThread exiting gracefully");
    } catch (Throwable t) {
      Log.e(TAG, "DownloadThread halted due to an error", t);
    }
  }

  //synchronized method is
  //https://docs.oracle.com/javase/tutorial/essential/concurrency/syncmeth.html
  /*
  First, it is not possible for two invocations of synchronized methods on the same
  object to interleave. When one thread is executing a synchronized method for an object,
  all other threads that invoke synchronized methods for the same object block
  (suspend execution) until the first thread is done with the object.
  Second, when a synchronized method exits, it automatically establishes a happens-before
  relationship with any subsequent invocation of a synchronized method for the same object. This guarantees that changes to the state of the object are visible to all threads.
  */

  // This method is allowed to be called from any thread
  public synchronized void requestStop() {
    // using the handler, post a Runnable that will quit()
    // the Looper attached to our DownloadThread
    // obviously, all previously queued tasks will be executed
    // before the loop gets the quit Runnable
    handler.post(new Runnable() {
      @Override
      public void run() {
        // This is guaranteed to run on the DownloadThread
        // so we can use myLooper() to get its looper
        Log.i(TAG, "DownloadThread loop quitting by request");

        Looper.myLooper().quit();
      }
    });
  }

  public synchronized void enqueueDownload(final DownloadTask task) {
    // Wrap DownloadTask into another Runnable to track the statistics
    handler.post(new Runnable() {
      @Override
      public void run() {
        try {
          task.run();
        } finally {
          // register task completion
          synchronized (DownloadThread.this) {
            totalCompleted++;
          }
          // a task was completed
          // tell the listener something has happened
          signalUpdate();
        }
      }
    });

    totalQueued++;
    // tell the listeners the queue is now longer
    signalUpdate();
  }

  public synchronized int getTotalQueued() {
    return totalQueued;
  }

  public synchronized int getTotalCompleted() {
    return totalCompleted;
  }

  // Please note! This method will normally be called from the download
  // thread.
  // Thus, it is up for the listener to deal with that (in case it is a UI
  // component,
  // it has to execute the signal handling code in the UI thread using Handler
  // - see
  // DownloadQueueActivity for example).
  private void signalUpdate() {
    if (listener != null) {
      listener.handleDownloadThreadUpdate();
    }
  }
}
