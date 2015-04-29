/*
   Copyright 2015 Stefan Fischer

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

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;

import sfischer13.openthesaurus.util.Net;
import sfischer13.openthesaurus.util.UI;
import sfischer13.openthesaurus.xml.Result;
import sfischer13.openthesaurus.xml.ResultExpandableListAdapter;

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
        registerForContextMenu(list);
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

    public void buttonClear(View view) {
        input.setText("");
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

    private void setClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(getString(R.string.word), text);
        clipboard.setPrimaryClip(clip);
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
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        }
    }

    private void unlockScreenOrientation() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }
}
