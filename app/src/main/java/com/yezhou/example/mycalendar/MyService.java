package com.yezhou.example.mycalendar;

import android.app.Notification;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MyService extends Service {
    int time = 60000;

    MyThread myThread;
    class MyThread extends Thread {

        @Override
        public void run() {
            while (true){// 如果已经解绑 服务会Destroy线程再运行会出现错误Service not registered: com.yezhou.example.servicetest.MainActivity$1@fbe96f4(错误提示在unbindService(connection);)
                // 所以得出结论解绑后需要结束线程
                try {
                    Thread.sleep(time);    // 安卓中不能stop一个线程 那么如何在我一点击解绑 线程结束 0S-点击-1S 消除 点击-1s 这段时间
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;    // 捕获异常 跳出循环
                }
                myNotify();
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        myThread.interrupt();
    }

    public MyService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        myThread = new MyThread();
        myThread.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void myNotify() {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        Date date = new Date();
        calendar.setTime(date);
        MyDateItem item = new MyDateItem();
        item.setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        String[] strs = new String[2];
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(MyService.this, "MyCalendar.db", null, 1);
        Log.d("admin_dbHelper", calendar.get(Calendar.HOUR_OF_DAY) + "");
        if (dbHelper.remind(item.getDate(), strs) && calendar.get(Calendar.HOUR_OF_DAY) >= 6 && calendar.get(Calendar.HOUR_OF_DAY) <= 22) {    // 进行通知
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            Notification notification = new NotificationCompat.Builder(this)
                    .setContentTitle(strs[0])
                    .setContentText(strs[1])
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setAutoCancel(true)
                    .setVibrate(new long[] {0, 1000, 1000, 1000})
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setDefaults(NotificationCompat.DEFAULT_SOUND)
                    .build();
            manager.notify(1, notification);
            time = 60000;
        } else {
            time = 4 * 60 * 60000;
        }

    }
}
