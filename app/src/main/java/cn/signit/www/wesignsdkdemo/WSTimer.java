package cn.signit.www.wesignsdkdemo;

import android.os.Handler;
import android.os.Message;

import java.util.Timer;
import java.util.TimerTask;



public class WSTimer {
    private Timer timer;
    private TimerTask task;
    private OnListener listener;
    private int delay;

    public WSTimer(OnListener listener, int interval) {
        this.listener = listener;
        this.delay = interval;
    }

    private Handler handler = new Handler() {
        public synchronized void handleMessage(Message msg) {
            if (listener != null) listener.onAlarmClock();
        }
    };

    public void restartTime(int interval) {
        stopTime();
        timer = new Timer(true);
        task = new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0000);
            }
        };
        timer.scheduleAtFixedRate(task, delay, interval);
    }

    public void stopTime() {
        if (task != null) {
            task.cancel();
            task = null;
        }

        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }

    public interface OnListener {
        void onAlarmClock();
    }
}
