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

package sfischer13.openthesaurus.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.Locale;

public class Info {
    private static PackageInfo packageInfo(Context context) {
        PackageManager pm = context.getPackageManager();
        String pn = context.getPackageName();
        try {
            return pm.getPackageInfo(pn, 0);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    private static String versionName(Context context) {
        PackageInfo pi = packageInfo(context);
        if (null == pi) {
            return "null";
        }
        return pi.versionName;
    }

    private static int versionCode(Context context) {
        PackageInfo pi = packageInfo(context);
        if (null == pi) {
            return -1;
        }
        return pi.versionCode;
    }

    public static String versionString(Context context) {
        return String.format(Locale.US, "%s (%d)", versionName(context), versionCode(context));
    }
}
