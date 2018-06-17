package com.limkee.locale;

import android.content.Context;
import android.content.SharedPreferences;

public class LocaleUtil {

    private final SharedPreferences settings;
    private static volatile LocaleUtil instance;

    public LocaleUtil(Context context){
        settings = context.getSharedPreferences("locale", 0);
    }

    public void changeLocale (int locale){
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("locale", locale);
        editor.apply();
    }

    public static LocaleUtil getInstance(Context context) {
        if (instance == null) {
            synchronized (LocaleUtil.class) {
                if (instance == null) {
                    instance = new LocaleUtil(context);
                }
            }
        }
        return instance;
    }

    public int getLocale() {return settings.getInt("locale", 0);}
}
