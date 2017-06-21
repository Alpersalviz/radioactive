package com.active.radioactive.view;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.active.radioactive.MainActivity;
import com.active.radioactive.R;
import com.active.radioactive.helper.NetworkingHelper;
import com.active.radioactive.receiver.NetworkChangeReceiver;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EViewGroup;
import com.googlecode.androidannotations.annotations.ViewById;

@EViewGroup(R.layout.view_search)
public class SearchView extends RelativeLayout {

    @ViewById
    EditText _txtSearch;

    public SearchView(Context context) {
        super(context);
    }

    public SearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SearchView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setOnEditorActionListener(TextView.OnEditorActionListener listener) {
        _txtSearch.setOnEditorActionListener(listener);
    }

    public void setError(String s) {
        _txtSearch.setError(s);
    }

    @AfterViews
    public void init() {
    }

    public View getEditText() {
        return _txtSearch;
    }

    public String getSearchString() {
        return _txtSearch.getText().toString();
    }

}
