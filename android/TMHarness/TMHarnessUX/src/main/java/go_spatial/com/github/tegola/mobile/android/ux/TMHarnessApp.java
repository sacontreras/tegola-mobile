package go_spatial.com.github.tegola.mobile.android.ux;

import go_spatial.com.github.tegola.mobile.android.controller.TMApp;
import timber.log.Timber;
import com.mapbox.mapboxsdk.Mapbox;

public class TMHarnessApp extends TMApp {

    @Override
    public void onCreate() {
        super.onCreate();

        Timber.plant(BuildConfig.DEBUG ? new Timber.DebugTree() : new TMTimberReleaseTree());

        // Mapbox Access token
        Mapbox.getInstance(getApplicationContext(), BuildConfig.mbglapi_access_token);
    }
}
