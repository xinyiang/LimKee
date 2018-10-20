package com.limkee1.notification;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.support.v4.app.NotificationCompat;
import com.limkee1.R;
import com.limkee1.Utility.DateUtility;
import com.limkee1.login.LoginActivity;
import java.util.Date;
import android.icu.text.SimpleDateFormat;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String isEnglish = intent.getStringExtra("isEnglish");
        final String id = "my_channel_id_01";
        CharSequence name = "channel_name";
        String description = "channel_description";
        int importance = NotificationManager.IMPORTANCE_LOW;
        int notificationId = intent.getIntExtra("notif_id", 0);
        String hour = intent.getStringExtra("hour");
        String mins = intent.getStringExtra("mins");
        String cutoffHour = "" + (Integer.parseInt(hour) + 1);
        String content = "";
        String cutofftime = cutoffHour + ":" + mins;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
        calendar.set(Calendar.MINUTE, Integer.parseInt(mins));
        //calendar.add(Calendar.DATE,1);

        Date currentTimestamp = new Date();
        Calendar cutoffTimeCalendar = Calendar.getInstance();
        cutoffTimeCalendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(cutoffHour));
        cutoffTimeCalendar.set(Calendar.MINUTE, Integer.parseInt(mins));
        Date cutoffTimestamp = cutoffTimeCalendar.getTime();

        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        java.text.SimpleDateFormat sundayFormat = new java.text.SimpleDateFormat("EEEE");
        String dayOfWeek = "";
        String currentDate = "";
        Date timeNow = new Date();
        currentDate = sdf.format(timeNow);
        String date = currentDate.substring(0, 10);
        String day = date.substring(8, date.length());
        String month = date.substring(5, 7);
        String yr = date.substring(0, 4);

        //only remind 1 hour before cut off time
        if (currentTimestamp.after(cutoffTimestamp)) {
            //check if today is sunday
            try {
                Date todayDate = new Date(yr + "/" + month + "/" + day);
                dayOfWeek = sundayFormat.format(todayDate);

                if (dayOfWeek.equals("Sunday") || dayOfWeek.equals("Sun")) {
                    //do not push notification
                } else {
                    if (isEnglish.equals("Yes")) {
                        content = "For today's delivery, please place order before " + cutofftime + " AM today";
                    } else {
                        content = "若要今日送货，请在今天早上" + cutofftime + " 前下单";
                    }
                }
            } catch(Exception e){
                System.out.println("Exception e " + e.getMessage());
            }

        }

        if (content == null || !content.equals("")){
            //no need to push
        } else {
            Intent notificationIntent = new Intent(context, AlarmReceiver.class);
            notificationIntent.putExtra("notif_id", Integer.parseInt(new SimpleDateFormat("ddHHmmss").format(new Date())));
            notificationIntent.putExtra("hour", hour);
            notificationIntent.putExtra("mins", mins);
            notificationIntent.putExtra("isEnglish", isEnglish);
            notificationIntent.putExtra("notif_content", content);

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

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + 24 * 60 * 60 * 1000, pendingIntent);
        }
    }
}
