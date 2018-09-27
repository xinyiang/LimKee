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
import android.icu.util.Calendar;
import android.support.v4.app.NotificationCompat;
import com.limkee.R;
import com.limkee.login.LoginActivity;
import java.util.Date;


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
        String content;
        String cutofftime = cutoffHour + ":" + mins;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
        calendar.set(Calendar.MINUTE, Integer.parseInt(mins));
        //calendar.add(Calendar.DATE,1);

        if(isEnglish.equals("Yes")) {
            content = "Please place order before " + cutoffHour + ":" + mins + " AM for today's delivery";
        } else {
            content = "今日订单请在早上" + getChineseTime(cutofftime) + "前下单";
        }

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
        notificationIntent.putExtra("isEnglish", isEnglish);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + 24*60*60*1000, pendingIntent);
    }

    public static String getChineseTime(String time){
        String minutes = time.substring(3,time.length());
        String chineseHour = "";
        String chineseTime;

        time = time.substring(0,2);
        //check hour
        if (time.equals("01")){
            chineseHour = "一";
        } else if (time.equals("02")){
            chineseHour = "二";
        } else if (time.equals("03")){
            chineseHour = "三";
        } else if (time.equals("04")){
            chineseHour = "四";
        }  else if (time.equals("05")){
            chineseHour = "五";
        } else if (time.equals("06")){
            chineseHour = "六";
        } else if (time.equals("07")){
            chineseHour = "七";
        } else if (time.equals("08")){
            chineseHour = "八";
        } else if (time.equals("09")){
            chineseHour = "九";
        } else if (time.equals("10")) {
            chineseHour = "十";
        } else if (time.equals("11")) {
            chineseHour = "十一";
        } else if (time.equals("12")) {
            chineseHour = "十二";
        } else {
            chineseHour = "";
        }

        //check if got mins
        if (minutes.equals("00")){
            chineseTime = chineseHour + "点";
        } else if (minutes.equals("30")){
            chineseTime = chineseHour + "点半";
        } else{
            chineseTime = chineseHour + "点" + getNumber(minutes) + "分";
        }
        return chineseTime;
    }

    public static String getNumber(String number){
        String chineseNumber = "";

        if (number.equals("05")){
            chineseNumber = "零五";
        } else if (number.equals("10")){
            chineseNumber = "十";
        } else if (number.equals("15")){
            chineseNumber = "十五";
        } else if (number.equals("20")){
            chineseNumber = "二十";
        } else if (number.equals("25")){
            chineseNumber = "二十五";
        } else if (number.equals("35")){
            chineseNumber = "三十五";
        } else if (number.equals("40")){
            chineseNumber = "四十";
        } else if (number.equals("45")){
            chineseNumber = "四十五";
        } else if (number.equals("50")){
            chineseNumber = "五十";
        } else if (number.equals("55")){
            chineseNumber = "五十五";
        }  else {
            chineseNumber = "零";
        }
        return chineseNumber;
    }

}
