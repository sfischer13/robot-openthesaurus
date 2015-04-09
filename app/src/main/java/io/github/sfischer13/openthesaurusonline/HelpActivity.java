package io.github.sfischer13.openthesaurusonline;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import io.github.sfischer13.openthesaurusonline.util.IO;


public class HelpActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        WebView html = (WebView) findViewById(R.id.help_html);
        html.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return true;
            }
        });
        String content = IO.readRawTextFile(this, R.raw.help);
        html.loadDataWithBaseURL(null, content, "text/html", "utf-8", null);
    }
}
