package com.limkee1.locale;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;

import java.util.Locale;

public class MyContextWrapper extends ContextWrapper {
    final static int CN = 0;
    final static int EN = 1;

    public MyContextWrapper(Context context) {
        super(context);
    }

    @TargetApi(Build.VERSION_CODES.N)
    public static ContextWrapper wrap(Context context) {
        int currLocale = LocaleUtil.getInstance(context).getLocale();
        Locale newLocale = new Locale("cn");
        if (currLocale == EN){
            newLocale = new Locale("en");
        }
        Resources res = context.getResources();
        Configuration configuration = res.getConfiguration();

        if (Build.VERSION.SDK_INT >= 24) {
            configuration.setLocale(newLocale);

            LocaleList localeList = new LocaleList(newLocale);
            LocaleList.setDefault(localeList);
            configuration.setLocales(localeList);

            context = context.createConfigurationContext(configuration);
        } else if (Build.VERSION.SDK_INT >= 17) {
            configuration.setLocale(newLocale);
            context = context.createConfigurationContext(configuration);

        } else {
            configuration.locale = newLocale;
            res.updateConfiguration(configuration, res.getDisplayMetrics());
        }

        return new ContextWrapper(context);
    }

    public static void saveSelectLanguage(Context context, boolean isEN) {
        if (isEN){
            LocaleUtil.getInstance(context).changeLocale(EN);
        }
        else {
            LocaleUtil.getInstance(context).changeLocale(CN);
        }
        wrap(context);
    }
}