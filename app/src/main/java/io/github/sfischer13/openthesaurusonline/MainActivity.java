package io.github.sfischer13.openthesaurusonline;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;

import io.github.sfischer13.openthesaurusonline.util.Net;
import io.github.sfischer13.openthesaurusonline.util.UI;
import io.github.sfischer13.openthesaurusonline.xml.Result;
import io.github.sfischer13.openthesaurusonline.xml.ResultExpandableListAdapter;

public class MainActivity extends Activity implements TaskListener {
    private EditText input;
    private ProgressDialog progress;
    private Result result;
    private ExpandableListView list;

    // TODO: deprecated
    @Override
    public Object onRetainNonConfigurationInstance() {
        if (result != null) {
            return result;
        }
        return super.onRetainNonConfigurationInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: deprecated
        restoreState();

        setContentView(R.layout.activity_main);
        initUIElement();

        list = (ExpandableListView) findViewById(R.id.list);
        list.setAdapter(new ResultExpandableListAdapter(this, result));
        list.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView elv, View view, int i, int j, long l) {
                TextView textView = (TextView) view.findViewById(R.id.text);
                performLinkSearch(textView.getText().toString());
                return true;
            }
        });
    }

    private void restoreState() {
        if (getLastNonConfigurationInstance() != null) {
            result = (Result) getLastNonConfigurationInstance();
        } else {
            result = null;
        }
    }

    private void initUIElement() {
        input = (EditText) findViewById(R.id.input);
        input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performInputSearch();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void actionHelp(MenuItem item) {
        startActivity(new Intent(this, HelpActivity.class));
    }

    public void actionInfo(MenuItem item) {
        startActivity(new Intent(this, InfoActivity.class));
    }

    private String getInput() {
        return input.getText().toString().trim();
    }

    public void buttonClick(View view) {
        performInputSearch();
    }

    private void performInputSearch() {
        performSearch(getInput());
    }

    private void performLinkSearch(String text) {
        input.setText(text);
        performInputSearch();
    }


    private void hideInputKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
    }

    private void performSearch(String text) {
        if (text.length() > 0) {
            if (Net.isConnected(this)) {
                hideInputKeyboard();
                new QueryTask(this).execute(text);
            } else {
                UI.shortCenterToast(getApplicationContext(), R.string.no_network);
            }
        } else {
            UI.shortCenterToast(getApplicationContext(), R.string.input_too_short);
        }
    }

    @Override
    public void onTaskStarted() {
        lockScreenOrientation();

        progress = new ProgressDialog(this);
        progress.setMessage(getString(R.string.searching));
        progress.setIndeterminate(true);
        progress.setCancelable(false);
        progress.show();
    }

    @Override
    public void onTaskFinished(Result result) {
        this.result = result;
        list.setAdapter(new ResultExpandableListAdapter(this, result));

        // group expansion
        if (result != null) {
            if (result.getSynsets().size() != 0 && result.getSuggestions().size() != 0) {
                list.expandGroup(1);
            } else if (result.getSynsets().size() != 0 || result.getSuggestions().size() != 0) {
                list.expandGroup(0);
            }
        }

        // progress dialog
        if (progress != null && progress.isShowing()) {
            progress.dismiss();
        }

        // toast
        if (result == null) {
            UI.shortCenterToast(getApplicationContext(), R.string.error);
        } else if (result.getSynsets().size() == 0 && result.getSuggestions().size() == 0) {
            UI.shortCenterToast(getApplicationContext(), R.string.no_result);
        } else {
            // nothing yet
        }

        unlockScreenOrientation();
    }

    // TODO: use Fragment (Solution 2)
    // https://androidresearch.wordpress.com/2013/05/10/dealing-with-asynctask-and-screen-orientation/
    // http://stackoverflow.com/questions/9630981/asynctask-with-progressdialog-vs-orientation-change
    // http://stackoverflow.com/a/12303649
    private void lockScreenOrientation() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    private void unlockScreenOrientation() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }
}
