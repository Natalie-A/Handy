package com.natalie.handy;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class MessageService extends IntentService {
    //Declare a constant KEY to pass a message from the Main Activity to the service
    public static final String EXTRA_MESSAGE = "MESSAGE";
    public static final int NOTIFICATION_ID = 1;

    public MessageService() {
        super("MessageService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        synchronized (this) {
            //synchronized() method is Java code which allows to to lock a particular block of code from access by other threads
            try {
                //wait for 3 seconds t
                wait(3000);
            } catch (InterruptedException error) {
                error.printStackTrace();
            }
            //try...catch is Java syntax which allows us to perform code actions on the try block, and catch error exceptions in the catch block, hence making us able to trace the line of code which has errors during debugging
        }
        //get the text from the intent
        String text = intent.getStringExtra(EXTRA_MESSAGE);
        //call show text method
        showText(text);
    }

    private void showText(final String text) {

        Intent intent = new Intent(this, MainActivity3.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity3.class);
        stackBuilder.addNextIntent(intent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, "My Notification")
                //this displays a small notification icon-in this case the icon called ic_jok_round
                .setSmallIcon(R.mipmap.ic_launcher_round)
                //set the title as your application name
                .setContentTitle(getString(R.string.app_name))
                //set the content text
                .setContentText(text)
                //make the notification disappear when clicked
                .setAutoCancel(true)
                //give it maximum priority to allow peeking
                .setPriority(Notification.PRIORITY_MAX)
                //set it to vibrate to get a large heads-up notification
                .setDefaults(Notification.DEFAULT_VIBRATE)
                //open Main Activity on clicking the notification
                .setContentIntent(pendingIntent);
        Notification notification1 = notification.build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("My Notification", "My Notification", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
            notification.setChannelId("My Notification");
        }
        //Issue the notification
        notificationManager.notify(NOTIFICATION_ID, notification1);
    }
}