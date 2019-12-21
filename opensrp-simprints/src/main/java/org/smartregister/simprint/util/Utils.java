package org.smartregister.simprint.util;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Author : Isaya Mollel on 2019-12-12.
 */
public class Utils {

    public static float convertDpToPixel(float dp, Context context) {
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

}
