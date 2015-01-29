package com.ostermann.sapinoscope;

import android.app.Application;
import android.content.Context;

public class Sapinoscope extends Application{

    private static Context context;

    public void onCreate(){
        super.onCreate();
        Sapinoscope.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return Sapinoscope.context;
    }
}