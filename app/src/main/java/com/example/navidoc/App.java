package com.example.navidoc;

import android.app.Application;

import com.kontakt.sdk.android.common.KontaktSDK;

public class App extends Application
{
    private static final String API_KEY = "znjeAwBTgSDBaOgNarjpYrqnIDchswUK";

    @Override
    public void onCreate()
    {
        super.onCreate();
        initializeDependencies();
    }

    private void initializeDependencies() {
        KontaktSDK.initialize(API_KEY);
    }
}
