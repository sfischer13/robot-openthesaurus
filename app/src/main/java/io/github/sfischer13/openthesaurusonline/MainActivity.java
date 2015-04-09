package io.github.sfischer13.openthesaurusonline;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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
import io.github.sfischer13.openthesaurusonline.xml.Parser;
import io.github.sfischer13.openthesaurusonline.xml.Result;

public class MainActivity extends Activity {
    private EditText input;
    private TextView output;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUIElement();
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

                new QueryTask().execute(getInput());
            } else {
                UI.shortCenterToast(getApplicationContext(), R.string.no_network);
            }
        } else {
            UI.shortCenterToast(getApplicationContext(), R.string.input_too_short);
        }
    }

    private class QueryTask extends AsyncTask<String, Void, Result> {
        final ProgressDialog dialog = new ProgressDialog(MainActivity.this);

        @Override
        protected Result doInBackground(String... terms) {
            return Parser.query(terms[0]);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage(getString(R.string.searching));
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected void onPostExecute(Result response) {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }

            if (response == null) {
                output.setText("");
                UI.shortCenterToast(getApplicationContext(), R.string.error);
            } else if (response.getSynsets().size() == 0) {
                output.setText("");
                UI.shortCenterToast(getApplicationContext(), R.string.no_result);
            } else {
                output.setText(response.toString());
            }
        }
    }
}
