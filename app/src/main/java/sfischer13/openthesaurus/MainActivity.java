/*
   Copyright 2015-2017 Stefan Fischer

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

package sfischer13.openthesaurus;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.ContextMenu;

import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import sfischer13.openthesaurus.util.Net;
import sfischer13.openthesaurus.util.UI;
import sfischer13.openthesaurus.xml.Result;
import sfischer13.openthesaurus.xml.ResultExpandableListAdapter;

public class MainActivity extends AppCompatActivity implements TaskListener {
    private static final String PREF_SAVED_QUERIES = "PREF_SAVED_QUERIES";
    private static final int HISTORY_SIZE = 15;
    private EditText input;
    private ProgressDialog progress;
    private Result result;
    private SharedPreferences preferences;
    private String[] queries;
    private ExpandableListView list;
    private ArrayAdapter<String> queryListAdapter;
    private ActionBarDrawerToggle drawerToggle;
    private LinearLayout queryDrawer;
    private DrawerLayout layout;

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        if (result != null) {
            return result;
        }
        return super.onRetainCustomNonConfigurationInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getLastCustomNonConfigurationInstance() != null) {
            result = (Result) getLastCustomNonConfigurationInstance();
        } else {
            result = null;
        }

        setContentView(R.layout.activity_main);

        layout = (DrawerLayout) findViewById(R.id.drawer_layout);

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
        input.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((keyCode == KeyEvent.KEYCODE_ENTER) && (event.getAction() == KeyEvent.ACTION_UP)) {
                    performInputSearch();
                    return true;
                }
                return false;
            }
        });
        input.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    int rightWidth = input.getCompoundDrawables()[2].getBounds().width();
                    if(event.getRawX() >= (input.getRight() - rightWidth)) {
                        input.setText("");
                        result = null;
                        displayResult();
                        return true;
                    }
                }
                return false;
            }
        });

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
        registerForContextMenu(list);

        // default values
        queries = new String[HISTORY_SIZE];
        Arrays.fill(queries, "");

        // restore saved queries
        preferences = getPreferences(Context.MODE_PRIVATE);
        String preferencesQueriesString = preferences.getString(PREF_SAVED_QUERIES, "");
        String[] restoredQueries = preferencesQueriesString.split("\n");
        int i = 0;
        for (String q : restoredQueries) {
            if (!q.isEmpty()) {
                   if (i < HISTORY_SIZE) {
                       queries[i] = q;
                       i = i + 1;
                   }
            }
        }

        queryDrawer = (LinearLayout) findViewById(R.id.query_drawer);

        ListView queryList = (ListView) findViewById(R.id.query_list);
        queryListAdapter = new ArrayAdapter<>(this, R.layout.query, R.id.query_text, queries);
        queryList.setAdapter(queryListAdapter);
        queryList.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                layout.closeDrawer(queryDrawer);
                String s = queries[position];
                if (!s.isEmpty()) {
                    input.setText(s);
                    performSearch(s);
                }
            }
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawerToggle = new ActionBarDrawerToggle(this, layout, R.string.feedback, R.string.app_name) {
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                setTitle(getString(R.string.history));
                invalidateOptionsMenu();
            }

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                setTitle(getString(R.string.app_name));
                invalidateOptionsMenu();
            }
        };
        layout.addDrawerListener(drawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) menuInfo;
        int type = ExpandableListView.getPackedPositionType(info.packedPosition);
        if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
            menu.setHeaderTitle(R.string.word);
            menu.add(Menu.NONE, 0, Menu.NONE, getString(R.string.context_copy));
            menu.add(Menu.NONE, 1, Menu.NONE, getString(R.string.context_web));
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) item.getMenuInfo();
        int groupId = ExpandableListView.getPackedPositionGroup(info.packedPosition);
        int childId = ExpandableListView.getPackedPositionChild(info.packedPosition);
        ResultExpandableListAdapter.TermChild child = (ResultExpandableListAdapter.TermChild) list.getExpandableListAdapter().getChild(groupId, childId);
        String text = child.getTerm().getTerm();

        switch (item.getItemId()) {
            case 0:
                setClipboard(text);
                return true;
            case 1:
                openUrl(getSynonymUrl(text));
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void actionFeedback(MenuItem item) {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", getString(R.string.app_contact), null));
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback_app));
        startActivity(Intent.createChooser(intent, getString(R.string.feedback)));
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

    public void buttonSearch(View view) {
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

    private String getSynonymUrl(String text) {
        return "https://www.openthesaurus.de/synonyme/" + Net.encodeUrl(text);
    }

    private void openUrl(String url) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    private void addToHistory(String text) {
        List<String> queriesAsList = new ArrayList<>(Arrays.asList(queries));
        queriesAsList.removeAll(Collections.singleton(text));
        queriesAsList.removeAll(Collections.singleton(""));
        queriesAsList.add(0, text);

        Arrays.fill(queries, "");
        int i = 0;
        for (String q : queriesAsList) {
            if (i < HISTORY_SIZE) {
                queries[i] = q;
                i = i + 1;
            }
        }

        saveHistory();
        queryListAdapter.notifyDataSetChanged();
    }

    private void saveHistory() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PREF_SAVED_QUERIES, TextUtils.join("\n", queries));
        editor.apply();
    }

    public void buttonClearHistory(View view) {
        Arrays.fill(queries, "");
        saveHistory();
        queryListAdapter.notifyDataSetChanged();
        layout.closeDrawer(queryDrawer);
    }

    private void setClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(getString(R.string.word), text);
        clipboard.setPrimaryClip(clip);
    }

    private void performSearch(String text) {
        if (text.length() > 0) {
            if (Net.isConnected(this)) {
                hideInputKeyboard();
                addToHistory(text);
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

        displayResult();

        // progress dialog
        if (progress != null && progress.isShowing()) {
            progress.dismiss();
        }

        // toast
        if (result == null) {
            UI.shortCenterToast(getApplicationContext(), R.string.error);
        } else if (result.getSynsets().size() == 0 && result.getSuggestions().size() == 0) {
            UI.shortCenterToast(getApplicationContext(), R.string.no_result);
        }

        unlockScreenOrientation();
    }

    private void displayResult() {
        list.setAdapter(new ResultExpandableListAdapter(this, result));
        // group expansion
        if (result != null) {
            if (result.getSynsets().size() != 0 && result.getSuggestions().size() != 0) {
                list.expandGroup(1);
            } else if (result.getSynsets().size() != 0 || result.getSuggestions().size() != 0) {
                list.expandGroup(0);
            }
        }
    }

    private void lockScreenOrientation() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        }
    }

    private void unlockScreenOrientation() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }
}
