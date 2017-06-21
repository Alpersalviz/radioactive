package com.active.radioactive.helper;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class KeyboardHelper {

    public static void show(Context C, View V) {
        try {
            V.requestFocus();
            ((InputMethodManager) C.getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(V, InputMethodManager.SHOW_IMPLICIT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void hide(Context C, View V) {
        try {
            ((InputMethodManager) C.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(V.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
