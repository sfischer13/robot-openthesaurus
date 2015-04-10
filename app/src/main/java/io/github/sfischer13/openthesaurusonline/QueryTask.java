package io.github.sfischer13.openthesaurusonline;

import android.os.AsyncTask;

import io.github.sfischer13.openthesaurusonline.xml.Parser;
import io.github.sfischer13.openthesaurusonline.xml.Result;

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