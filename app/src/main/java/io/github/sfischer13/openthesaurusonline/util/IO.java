package io.github.sfischer13.openthesaurusonline.util;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class IO {
    public static String readRawTextFile(Context context, int resourceId) {
        InputStream is = context.getResources().openRawResource(resourceId);
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);

        String result;
        StringBuilder sb = new StringBuilder();
        try {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
            result = sb.toString();
        } catch (IOException ioe) {
            result = null;
        } finally {
            try {
                br.close();
            } catch (IOException ioe) {
                result = null;
            }
        }

        return result;
    }
}