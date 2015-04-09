package io.github.sfischer13.openthesaurusonline;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import io.github.sfischer13.openthesaurusonline.util.IO;
import io.github.sfischer13.openthesaurusonline.util.Info;


public class InfoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        WebView html = (WebView) findViewById(R.id.info_html);
        html.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return true;
            }
        });
        String content = IO.readRawTextFile(this, R.raw.info);
        if (content != null) {
            content = content.replace("{{version}}", Info.versionString(this));
            html.loadDataWithBaseURL(null, content, "text/html", "utf-8", null);
        }
    }
}
