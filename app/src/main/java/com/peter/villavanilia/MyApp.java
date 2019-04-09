package com.peter.villavanilia;

import android.app.Application;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Frutiger.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );


    }
}
