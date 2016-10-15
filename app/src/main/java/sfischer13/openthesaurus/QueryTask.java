/*
   Copyright 2015-2016 Stefan Fischer

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

import android.os.AsyncTask;

import sfischer13.openthesaurus.xml.Parser;
import sfischer13.openthesaurus.xml.Result;

class QueryTask extends AsyncTask<String, Void, Result> {
    private final TaskListener listener;

    public QueryTask(TaskListener listener) {
        this.listener = listener;
    }

    @Override
    protected Result doInBackground(String... terms) {
        return Parser.query(terms[0]);
    }

    @Override
    protected void onPreExecute() {
        listener.onTaskStarted();
    }

    @Override
    protected void onPostExecute(Result result) {
        listener.onTaskFinished(result);
    }
}