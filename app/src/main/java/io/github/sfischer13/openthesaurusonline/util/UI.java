package io.github.sfischer13.openthesaurusonline.util;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class UI {
    public static void shortCenterToast(Context context, int stringId) {
        Toast toast = Toast.makeText(context, stringId, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }
}
