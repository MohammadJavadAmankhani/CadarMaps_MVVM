package com.mjavad.rahpaprojects.app;

import android.content.Context;

import com.cedarstudios.cedarmapssdk.CedarMaps;

public class Application extends android.app.Application {

    private static  Context context;
    @Override
    public void onCreate() {
        super.onCreate();

        context = this;
        CedarMaps.getInstance()
                .setClientID(app.main.CLIENT_ID)
                .setClientSecret(app.main.CLIENT_SECRET)
                .setContext(this)
                .setMapID("cedarmaps.mix");
    }

    public static Context getContext(){
        return context;
    }
  /*  public void setFons(){
        String font = "fonts/iran_sans.ttf";
        FontOverride.setDafalutFont(getContext() , "MONOSPACE" , font);
    }*/
}
