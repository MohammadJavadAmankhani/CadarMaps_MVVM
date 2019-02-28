package com.mjavad.rahpaprojects.app;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;


public class app {
    public static class main{
        public static final String TAG = "MAPS";
        // TODO: Add your clientID and clientSecret here.
        static final String CLIENT_ID = "laboratory-4318993245929149746";
        static final String CLIENT_SECRET = "bh72R2xhYm9yYXRvcnnmjAL1pLKAvMiYOM9zxt87KUyF0MYXxnKSlsBYOwog8Q==";
    }

    public static void toast(String message){
        Toast.makeText(Application.getContext(), message, Toast.LENGTH_SHORT).show();
    }

    public static void log(String message){
        Log.e(main.TAG ,message);
    }
    public static void closeKeybored(View view){
        InputMethodManager inputMethodManager = (InputMethodManager) Application.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken() , InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
