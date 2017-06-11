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

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;

import sfischer13.openthesaurus.util.IO;
import sfischer13.openthesaurus.util.Info;


public class InfoActivity extends AppCompatActivity {

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
            content = content.replace("{{app_name}}", getString(R.string.app_name));
            content = content.replace("{{version}}", Info.versionString(this));
            html.loadDataWithBaseURL(null, content, "text/html", "utf-8", null);
        }
    }
}
