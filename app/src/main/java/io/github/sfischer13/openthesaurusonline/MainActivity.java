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
import android.widget.TextView;

import io.github.sfischer13.openthesaurusonline.util.Net;
import io.github.sfischer13.openthesaurusonline.util.UI;
import io.github.sfischer13.openthesaurusonline.xml.Result;

public class MainActivity extends Activity implements TaskListener {
    private EditText input;
    private TextView output;
    private ProgressDialog progress;
    private Result result;

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
        setContentView(R.layout.activity_main);
        initUIElement();

        // TODO: deprecated
        restoreState();
    }

    private void restoreState() {
        if (getLastNonConfigurationInstance() != null) {
            result = (Result) getLastNonConfigurationInstance();
        } else {
            result = null;
        }
        setOutput();
    }

    private void initUIElement() {
        input = (EditText) findViewById(R.id.input);
        input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch();
                    return true;
                }
                return false;
            }
        });

        output = (TextView) findViewById(R.id.output);
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
        performSearch();
    }

    private void performSearch() {
        if (getInput().length() > 0) {
            if (Net.isConnected(this)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(input.getWindowToken(), 0);

                new QueryTask(this).execute(getInput());
            } else {
                UI.shortCenterToast(getApplicationContext(), R.string.no_network);
            }
        } else {
            UI.shortCenterToast(getApplicationContext(), R.string.input_too_short);
        }
    }

    private void setOutput() {
        if (result == null) {
            output.setText("");
        } else if (result.getSynsets().size() == 0) {
            output.setText("");
        } else {
            output.setText(result.toString());
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
        setOutput();

        if (progress != null && progress.isShowing()) {
            progress.dismiss();
        }

        if (result == null) {
            UI.shortCenterToast(getApplicationContext(), R.string.error);
        } else if (result.getSynsets().size() == 0) {
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
