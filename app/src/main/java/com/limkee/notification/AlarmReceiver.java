package com.limkee.notification;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.support.v4.app.NotificationCompat;
import com.limkee.R;
import com.limkee.catalogue.CatalogueFragment;
import com.limkee.login.LoginActivity;

import java.util.Calendar;
import java.util.Date;


public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "Boot Receiver:::";

    @Override
    public void onReceive(final Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String content = intent.getStringExtra("notif_content");
        final String id = "my_channel_id_01";
        CharSequence name = "channel_name";
        String description = "channel_description";
        int importance = NotificationManager.IMPORTANCE_LOW;
        int notificationId = intent.getIntExtra("notif_id", 0);
        String hour = intent.getStringExtra("hour");
        String mins = intent.getStringExtra("mins");

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
        calendar.set(Calendar.MINUTE, Integer.parseInt(mins));
        //calendar.add(Calendar.DATE,1);

        NotificationChannel mChannel = new NotificationChannel(id, name, importance);
        mChannel.setDescription(description);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.createNotificationChannel(mChannel);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, id)
                .setContentTitle("Gentle reminder")
                .setContentText(content)
                .setSmallIcon(R.drawable.logo)
                .setAutoCancel(true);
        Intent notifIntent = new Intent(context, LoginActivity.class);
        PendingIntent activity = PendingIntent.getActivity(context, notificationId, notifIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(activity);
        Notification notification = builder.build();

        notificationManager.notify(notificationId, notification);
        Intent notificationIntent = new Intent(context, AlarmReceiver.class);
        notificationIntent.putExtra("notif_content", content);
        notificationIntent.putExtra("notif_id", Integer.parseInt(new SimpleDateFormat("ddHHmmss").format(new Date())));
        notificationIntent.putExtra("hour", hour);
        notificationIntent.putExtra("mins", mins);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + 24*60*60*1000, pendingIntent);
    }
}
