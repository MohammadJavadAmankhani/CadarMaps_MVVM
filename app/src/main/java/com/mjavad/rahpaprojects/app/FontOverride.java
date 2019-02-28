package com.mjavad.rahpaprojects.app;

import android.content.Context;
import android.graphics.Typeface;

import java.lang.reflect.Field;

public class FontOverride {


    public static void setDafalutFont(Context context , String typeFaceName , String fontName){

        final Typeface typeface = Typeface.createFromAsset(context.getAssets() , fontName);
        replaceFont(typeFaceName , typeface);
    }

    private static void replaceFont(String typeFaceName , Typeface typeface){
        try {
            final Field field = Typeface.class.getDeclaredField(typeFaceName);
            field.setAccessible(true);
            field.set(null , typeface);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }




}
