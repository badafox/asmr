package com.matome.asmr;

import android.content.Context;
import android.content.SharedPreferences;

public  class Util {

    public static final String YOUTUBE_WORD ="word";
    public static final String YOUTUBE_ORDER ="oder";

    //設定保存
    public static void setSave(Context con, String value, String key){
        SharedPreferences pref = con.getSharedPreferences("DataSave", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=pref.edit();
        editor.putString(key,value);
        editor.apply();
    }

    //設定取得
    public static String getSearch(Context con, String key){
        SharedPreferences pref = con.getSharedPreferences("DataSave", Context.MODE_PRIVATE);
        String def = con.getString(R.string.youtube_word);
        return pref.getString(key, def);
    }

    public static String getOrder(Context con, String key){
        SharedPreferences pref = con.getSharedPreferences("DataSave", Context.MODE_PRIVATE);
        String def = con.getString(R.string.youtube_order);
        return pref.getString(key, def);
    }

}