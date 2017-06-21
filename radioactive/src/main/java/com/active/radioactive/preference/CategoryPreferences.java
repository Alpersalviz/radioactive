package com.active.radioactive.preference;

import com.googlecode.androidannotations.annotations.sharedpreferences.DefaultString;
import com.googlecode.androidannotations.annotations.sharedpreferences.SharedPref;

@SharedPref(SharedPref.Scope.UNIQUE)
public interface CategoryPreferences {
    @DefaultString("[]")
    String getCategoriesJson();
}
