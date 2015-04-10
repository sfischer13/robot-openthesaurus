package io.github.sfischer13.openthesaurusonline;

import io.github.sfischer13.openthesaurusonline.xml.Result;

public interface TaskListener {
    void onTaskStarted();

    void onTaskFinished(Result result);
}
