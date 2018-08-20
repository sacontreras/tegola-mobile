package go_spatial.com.github.tegola.mobile.android.ux;

import android.support.annotation.NonNull;
import android.util.Log;

import timber.log.Timber;

public class TMTimberReleaseTree extends Timber.Tree {
    @Override
    protected void log(int priority, String tag, @NonNull String message, Throwable t) {
        if (priority > Log.WARN)
            Log.println(priority, tag, message);
    }
}
