package com.active.radioactive.helper;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    private static String lang;

    public static <T> List<T> toList(T[] array) {
        List<T> list = new ArrayList<T>();
        for (T t : array)
            list.add(t);
        return list;
    }

    public static int fromDp(Context context, int value) {
        return (int) context.getResources().getDisplayMetrics().density * value;
    }


    public static String getLang() {
        return lang;
    }

    public static void setLang(String lang) {
        Utils.lang = lang;
    }
}
