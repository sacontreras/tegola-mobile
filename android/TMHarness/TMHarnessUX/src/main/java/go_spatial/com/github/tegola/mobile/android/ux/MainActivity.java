package go_spatial.com.github.tegola.mobile.android.ux;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.ipaulpro.afilechooser.utils.FileUtils;
import com.mapbox.mapboxsdk.exceptions.MapboxConfigurationException;

/*
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.OpenFileActivityBuilder;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.SearchableField;
*/

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.OnFocusChange;
import butterknife.OnItemSelected;
import go_spatial.com.github.tegola.mobile.android.controller.ClientAPI;
import go_spatial.com.github.tegola.mobile.android.controller.Exceptions;
import go_spatial.com.github.tegola.mobile.android.controller.FGS;
import go_spatial.com.github.tegola.mobile.android.controller.GPKG;
import go_spatial.com.github.tegola.mobile.android.controller.TEGOLA_BIN;
import go_spatial.com.github.tegola.mobile.android.controller.utils.HTTP;
import go_spatial.com.github.tegola.mobile.android.ux.Constants.REQUEST_CODES;
import go_spatial.com.github.tegola.mobile.android.ux.Constants.Strings;
import go_spatial.com.github.tegola.mobile.android.controller.Constants;

public class MainActivity
        extends LocationUpdatesManager.LocationUpdatesBrokerActivity
        implements ClientAPI.ControllerNotificationsListener, MBGLFragment.OnFragmentInteractionListener {

    private static final String TAG = MainActivity.class.getCanonicalName();

    @BindView(R.id.drawerlayout)
    protected DrawerLayout m_drawerlayout;
    @BindView(R.id.drawerlayout_content__main)
    protected LinearLayout m_drawerlayout_content__main;
    @BindView(R.id.drawerlayout_content__drawer)
    protected LinearLayout m_drawerlayout_content__drawer;
    @BindView(R.id.sv_main)
    protected ScrollView m_scvw_main;
    @BindView(R.id.btn_sect__mbgl_nfo__expand)
    protected Button m_btn_sect__mbgl_nfo__expand;
    @BindView(R.id.sect_content__mbgl_nfo)
    protected ExpandableRelativeLayout m_vw_sect_content__mbgl_nfo;
    @BindView(R.id.rg_val_mvt_source_sel)
    protected RadioGroup m_rg_val_mvt_source_sel;
    @BindView(R.id.edt_val_http_client_cfg__connect_timeout)
    protected EditText m_edt_val_http_client_cfg__connect_timeout;
    @BindView(R.id.edt_val_http_client_cfg__read_timeout)
    protected EditText m_edt_val_http_client_cfg__read_timeout;
    @BindView(R.id.edt_val_http_client_cfg__cache_size)
    protected EditText m_edt_val_http_client_cfg__cache_size;
    @BindView(R.id.edt_val_http_client_cfg__max_requests_per_host)
    protected EditText m_edt_val_http_client_cfg__max_requests_per_host;
    @BindView(R.id.sect__local_srvr_nfo)
    protected View m_sect__local_srvr_nfo;
    @BindView(R.id.tv_val_bin_ver)
    protected TextView m_tv_val_bin_ver;
    @BindView(R.id.rb_postgis_provider_type_sel)
    protected RadioButton m_rb_postgis_provider_type_sel;
    @BindView(R.id.rb_gpkg_provider_type_sel)
    protected RadioButton m_rb_gpkg_provider_type_sel;
    @BindView(R.id.sect__postgis_provider_spec)
    protected View m_sect__postgis_provider_spec;
    @BindView(R.id.rb_postgis_local_config_type_sel)
    protected RadioButton m_rb_postgis_local_config_type_sel;
    @BindView(R.id.tv_lbl_gpkg_provider_type_sel__manage_bundles)
    protected TextView m_tv_lbl_config_type_sel__local__manage_files;
    @BindView(R.id.rb_postgis_remote_config_type_sel)
    protected RadioButton m_rb_postgis_remote_config_type_sel;
    @BindView(R.id.vw_postgis_local_config_sel__container)
    protected View m_vw_postgis_local_config_sel__container;
    @BindView(R.id.spinner_postgis_local_config_sel)
    protected CustomSpinner m_spinner_postgis_local_config_sel;
    @BindView(R.id.btn_postgis_local_config_sel__edit_file)
    protected ImageButton m_btn_config_sel_local__edit_file;
    //    private ImageButton m_btn_config_sel_local_import__googledrive = null;
    @BindView(R.id.btn_postgis_local_config_import__sdcard)
    protected ImageButton m_btn_postgis_local_config_import__sdcard;
    @BindView(R.id.vw_postgis_remove_config__container)
    protected View m_vw_postgis_remote_config__container;
    @BindView(R.id.edt_postgis_remote_config_url)
    protected EditText m_edt_postgis_remote_config_url;
    @BindView(R.id.btn_postgis_remote_config_url__apply_changes)
    protected Button m_btn_postgis_remote_config_url__apply_changes;
    @BindView(R.id.sect__gpkg_provider_spec)
    protected View m_sect__gpkg_provider_spec;
    @BindView(R.id.spinner_gpkg_bundle_sel)
    protected CustomSpinner m_spinner_gpkg_bundle_sel;
    @BindView(R.id.spinner_gpkg_bundle_props_sel)
    protected CustomSpinner m_spinner_gpkg_bundle_props_sel;
    @BindView(R.id.tv_val_local_mvt_srvr_status)
    protected TextView m_tv_val_local_mvt_srvr_status;
    @BindView(R.id.btn_local_mvt_srvr_ctrl)
    protected Button m_btn_local_mvt_srvr_ctrl;
    @BindView(R.id.sect_content__item__srvr_console_output)
    protected View m_sect_content__item__srvr_console_output;
    @BindView(R.id.tv_tegola_console_output)
    protected TextView m_tv_tegola_console_output;
    @BindView(R.id.sect__remote_srvr_nfo)
    protected View m_sect__remote_srvr_nfo;
    @BindView(R.id.spinner_remote_tile_server_sel)
    protected CustomSpinner m_spinner_val_remote_tile_server;
    @BindView(R.id.btn_remote_mvt_srvr__open_stream)
    protected Button m_btn_stream_tiles_from_remote;

    private final ArrayList<String> m_spinner_val_local_config__items = new ArrayList<String>();
    private CustomSpinner.Adapter m_spinner_val_local_config__dataadapter = null;
    private final ArrayList<String> m_spinner_val_gpkg_bundle__items = new ArrayList<String>();
    private CustomSpinner.Adapter m_spinner_val_gpkg_bundle__dataadapter = null;
    private final ArrayList<String> m_spinner_val_gpkg_bundle_props__items = new ArrayList<String>();
    private CustomSpinner.Adapter m_spinner_val_gpkg_bundle_props__dataadapter = null;
    private final ArrayList<String> m_spinner_val_remote_tile_server__items = new ArrayList<String>();
    private CustomSpinner.Adapter m_spinner_val_remote_tile_server__dataadapter = null;
    private DrawerHandle m_drawer_handle = null;
    private ActionBarDrawerToggle m_drawerlayout_main__DrawerToggle = null;

    private final MBGLFragment mb_frag = new MBGLFragment();

    private ClientAPI.Client m_controllerClient = null;
    private boolean m_controller_running = false;

    private enum MAPVIEW_STATE {
        STREAM_CLOSED,
        OPENING_STREAM__REMOTE,
        STREAMING__REMOTE__DRAWER_OPEN,
        STREAMING__REMOTE__DRAWER_CLOSED,
        OPENING_STREAM__LOCAL,
        STREAMING__LOCAL__DRAWER_OPEN,
        STREAMING__LOCAL__DRAWER_CLOSED
        ;

        public static HashMap<String, MAPVIEW_STATE> name_map = new HashMap<String, MAPVIEW_STATE>() { {
                put(STREAM_CLOSED.name(), STREAM_CLOSED);
                put(OPENING_STREAM__REMOTE.name(), OPENING_STREAM__REMOTE);
                put(STREAMING__REMOTE__DRAWER_OPEN.name(), STREAMING__REMOTE__DRAWER_OPEN);
                put(STREAMING__REMOTE__DRAWER_CLOSED.name(), STREAMING__REMOTE__DRAWER_CLOSED);
                put(OPENING_STREAM__LOCAL.name(), OPENING_STREAM__LOCAL);
                put(STREAMING__LOCAL__DRAWER_OPEN.name(), STREAMING__LOCAL__DRAWER_OPEN);
                put(STREAMING__LOCAL__DRAWER_CLOSED.name(), STREAMING__LOCAL__DRAWER_CLOSED);
            }
        };

        public static HashMap<Integer, MAPVIEW_STATE> ordinal_map = new HashMap<Integer, MAPVIEW_STATE>() { {
                put(STREAM_CLOSED.ordinal(), STREAM_CLOSED);
                put(OPENING_STREAM__REMOTE.ordinal(), OPENING_STREAM__REMOTE);
                put(STREAMING__REMOTE__DRAWER_OPEN.ordinal(), STREAMING__REMOTE__DRAWER_OPEN);
                put(STREAMING__REMOTE__DRAWER_CLOSED.ordinal(), STREAMING__REMOTE__DRAWER_CLOSED);
                put(OPENING_STREAM__LOCAL.ordinal(), OPENING_STREAM__LOCAL);
                put(STREAMING__LOCAL__DRAWER_OPEN.ordinal(), STREAMING__LOCAL__DRAWER_OPEN);
                put(STREAMING__LOCAL__DRAWER_CLOSED.ordinal(), STREAMING__LOCAL__DRAWER_CLOSED);
            }
        };
    }
    private volatile MAPVIEW_STATE mvstate = MAPVIEW_STATE.STREAM_CLOSED;

    private final String FRAG_DRAWER_CONTENT = "FRAG_DRAWER_CONTENT";
    private final String SAVE_INSTANCE_ARG__CTRLR_RUNNING = "SAVE_INSTANCE_ARG__CTRLR_RUNNING";
    private final String SAVE_INSTANCE_ARG__MAPVIEW_STATE = "SAVE_INSTANCE_ARG__MAPVIEW_STATE";

//    private DriveId m_google_drive_id;
//    private final class MyGoogleApiClientCallbacks implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
//        private final String TAG = MyGoogleApiClientCallbacks.class.getName();
//
//        //GoogleApiClient override
//        @Override
//        public void onConnected(@Nullable Bundle bundle) {
//            Drawable drawable_cloud_download = ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_cloud_download_black_24dp);
//            int h = drawable_cloud_download.getIntrinsicHeight();
//            int w = drawable_cloud_download.getIntrinsicWidth();
//            drawable_cloud_download.setBounds(0, 0, w, h);
//            m_btn_config_sel_local_import__googledrive.setImageDrawable(drawable_cloud_download);
//            m_btn_config_sel_local_import__googledrive.setBackgroundColor(ContextCompat.getColor(MainActivity.this, android.R.color.holo_green_light));
//            Log.i(TAG, "onConnected: GoogleApiClient flow rcvr_hndlr: connection success");
//            Toast.makeText(getApplicationContext(), "GoogleApiClient successfully connected", Toast.LENGTH_SHORT).show();
//        }
//
//        //GoogleApiClient override
//        @Override
//        public void onConnectionSuspended(int i) {
//            Drawable drawable_cloud_disconnected = ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_cloud_off_black_24dp);
//            int h = drawable_cloud_disconnected.getIntrinsicHeight();
//            int w = drawable_cloud_disconnected.getIntrinsicWidth();
//            drawable_cloud_disconnected.setBounds(0, 0, w, h);
//            m_btn_config_sel_local_import__googledrive.setImageDrawable(drawable_cloud_disconnected);
//            m_btn_config_sel_local_import__googledrive.setBackgroundColor(ContextCompat.getColor(MainActivity.this, android.R.color.holo_red_dark));
//            Log.i(TAG, "onConnectionSuspended: GoogleApiClient flow rcvr_hndlr: connection suspended");
//            Toast.makeText(getApplicationContext(), "GoogleApiClient connection suspended", Toast.LENGTH_SHORT).show();
//        }
//
//        //GoogleApiClient override
//        @Override
//        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//            m_btn_config_sel_local_import__googledrive.setBackgroundColor(ContextCompat.getColor(MainActivity.this, android.R.color.holo_red_dark));
//            if (!connectionResult.hasResolution()) {
//                Log.e(TAG, "onConnectionFailed: GoogleApiClient connection failed: " + connectionResult.toString() + " -- flow control rcvr_hndlr: abnormal termination :(");
//                Toast.makeText(getApplicationContext(), "GoogleApiClient connection failed with no reported resolution!", Toast.LENGTH_SHORT).show();
//                GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, connectionResult.getErrorCode(), 0).show();
//                return;
//            }
//            Log.i(TAG, "onConnectionFailed: GoogleApiClient connection failed: " + connectionResult.toString() + " -- flow control rcvr_hndlr: starting GoogleApiClient connection resolution for this result...");
//            Toast.makeText(getApplicationContext(), "GoogleApiClient connection failed -- starting resolution flow...", Toast.LENGTH_SHORT).show();
//            try {
//                connectionResult.startResolutionForResult(MainActivity.this, REQUEST_CODES.REQUEST_CODE__GOOGLEAPICLIENT__RESOLVE_CONNECTION_FAILURE);
//            } catch (IntentSender.SendIntentException e) {
//                Log.e(TAG, "onConnectionFailed: GoogleApiClient connection failed: " + connectionResult.toString() + " -- flow control rcvr_hndlr: IntentSender failed to send intent; abnormal termination :(", e);
//                Toast.makeText(getApplicationContext(), "GoogleApiClient connection-failure resolution flow abnormally terminated!", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//    private MyGoogleApiClientCallbacks m_google_api_callbacks = null;

    //credit to: https://stackoverflow.com/questions/16754305/full-width-navigation-drawer?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
    private void fixMinDrawerMargin(DrawerLayout drawerLayout) {
        try {
            Field f = DrawerLayout.class.getDeclaredField("mMinDrawerMargin");
            f.setAccessible(true);
            f.set(drawerLayout, 0);
            drawerLayout.requestLayout();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: entered");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //map UI objects to UI resources
        ButterKnife.bind(this);

        fixMinDrawerMargin(m_drawerlayout);
        m_drawerlayout_main__DrawerToggle = new ActionBarDrawerToggle(this, m_drawerlayout, R.string.open, R.string.close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                float slideX = drawerView.getWidth() * slideOffset;
                m_drawerlayout_content__main.setTranslationX(slideX);
                m_drawerlayout_content__main.setScaleX(1 - slideOffset);
                m_drawerlayout_content__main.setScaleY(1 - slideOffset);
            }
        };

        //set up associated UI objects auxiliary objects if any - e.g. TAGs and data adapters
        m_spinner_val_local_config__dataadapter = new CustomSpinner.Adapter(this, m_spinner_val_local_config__items);
        m_spinner_postgis_local_config_sel.setAdapter(m_spinner_val_local_config__dataadapter);
        m_spinner_val_gpkg_bundle__dataadapter = new CustomSpinner.Adapter(this, m_spinner_val_gpkg_bundle__items);
        m_spinner_gpkg_bundle_sel.setAdapter(m_spinner_val_gpkg_bundle__dataadapter);
        m_spinner_val_gpkg_bundle_props__dataadapter = new CustomSpinner.Adapter(this, m_spinner_val_gpkg_bundle_props__items);
        m_spinner_gpkg_bundle_props_sel.setAdapter(m_spinner_val_gpkg_bundle_props__dataadapter);
        m_spinner_val_remote_tile_server__dataadapter = new CustomSpinner.Adapter(this, m_spinner_val_remote_tile_server__items);
        m_spinner_val_remote_tile_server.setAdapter(m_spinner_val_remote_tile_server__dataadapter);

        m_btn_local_mvt_srvr_ctrl.setTag(R.id.TAG__SRVR_RUNNING, false);

        //associate listeners for user-UI-interaction
        m_tv_lbl_config_type_sel__local__manage_files.setMovementMethod(LinkMovementMethod.getInstance());
        Spannable span__clickable_text__m_tv_lbl_config_type_sel__local__manage_files = (Spannable)m_tv_lbl_config_type_sel__local__manage_files.getText();
        span__clickable_text__m_tv_lbl_config_type_sel__local__manage_files.setSpan(ClickableSpan____m_tv_lbl_config_type_sel__local__manage_files, 0, span__clickable_text__m_tv_lbl_config_type_sel__local__manage_files.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        m_btn_config_sel_local_import__googledrive.setOnClickListener(OnClickListener__m_btn_config_sel_local_import__googledrive);

        //instantiate PersistentConfigSettingsManager singleton
        SharedPrefsManager.newInstance(this);

//        m_google_api_callbacks = new MyGoogleApiClientCallbacks();

        m_controllerClient = ClientAPI.initClient(
            MainActivity.this,
            MainActivity.this,
            new Handler(getMainLooper())
        );

        if (savedInstanceState != null) {
            m_controller_running = savedInstanceState.getBoolean(SAVE_INSTANCE_ARG__CTRLR_RUNNING, false);
            Log.d(TAG, String.format("onCreate: savedInstanceState.getBoolean(SAVE_INSTANCE_ARG__CTRLR_RUNNING)==%b", m_controller_running));
            mvstate = MAPVIEW_STATE.ordinal_map.get(savedInstanceState.getInt(SAVE_INSTANCE_ARG__MAPVIEW_STATE, MAPVIEW_STATE.STREAM_CLOSED.ordinal()));
            Log.d(TAG, String.format("onCreate: savedInstanceState.getBoolean(SAVE_INSTANCE_ARG__MAPVIEW_STATE)==%s", mvstate.name()));
        }

        //set title to build version
        setTitle(getString(R.string.app_name) + " - build " + BuildConfig.VERSION_NAME);

        m_drawerlayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        OnMVTServerStopped();
        OnControllerStopped();

        //set expandable sections UI initial "expanded" state
        m_vw_sect_content__mbgl_nfo.collapse();
        m_vw_sect_content__mbgl_nfo.setExpanded(false);

        m_sect_content__item__srvr_console_output.setVisibility(View.GONE);
        m_tv_tegola_console_output.setMovementMethod(new ScrollingMovementMethod());

        //now queue up initial automated UI actions
        new Handler().postDelayed(
            () -> {
                if (savedInstanceState == null || !savedInstanceState.getBoolean(SAVE_INSTANCE_ARG__CTRLR_RUNNING, false))
                    m_controllerClient.controller__start(MainActivity.class.getName());
                else {
                    m_controllerClient.mvt_server__query_state__is_running();
                    m_controllerClient.mvt_server__query_state__listen_port();
                }

                //reconcile expandable sections UI with initial "expanded" state
                m_vw_sect_content__mbgl_nfo.callOnClick();
                m_tv_tegola_console_output__scroll_max();
                //adjust main scroll view (since expandable sections may or may not have been expanded/collapsed based on initial settings)
                m_scvw_main__scroll_max();

                switch (mvstate) {
                    case STREAMING__REMOTE__DRAWER_OPEN: {
                        m_btn_stream_tiles_from_remote.callOnClick();
                        break;
                    }
                    case STREAMING__REMOTE__DRAWER_CLOSED: {
                        break;
                    }
                    case STREAMING__LOCAL__DRAWER_OPEN: {
                        break;
                    }
                    case STREAMING__LOCAL__DRAWER_CLOSED: {
                        break;
                    }
                    case STREAM_CLOSED:
                    default: {
                        break;
                    }
                }
            },
            50
        );
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, String.format("onSaveInstanceState: outState.putBoolean(SAVE_INSTANCE_ARG__CTRLR_RUNNING, %b) - and mvstate is %s", m_controller_running, mvstate.name()));
        outState.putBoolean(SAVE_INSTANCE_ARG__CTRLR_RUNNING, m_controller_running);
        outState.putInt(SAVE_INSTANCE_ARG__MAPVIEW_STATE, mvstate.ordinal());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: entered");
        //now queue up initial automated UI actions
        new Handler().postDelayed(() -> {
            //get mbgl config shared prefs
            if (SharedPrefsManager.INTEGER_SHARED_PREF.MBGL_CONFIG__CONNECT_TIMEOUT.getValue() == null) {
                Log.d(TAG, String.format("onResume.async.runnable.run: int shared pref %s is not set - default to BuildConfig.mbgl_http_connect_timeout==%d", SharedPrefsManager.INTEGER_SHARED_PREF.MBGL_CONFIG__CONNECT_TIMEOUT.toString(), BuildConfig.mbgl_http_connect_timeout));
                SharedPrefsManager.INTEGER_SHARED_PREF.MBGL_CONFIG__CONNECT_TIMEOUT.setValue(BuildConfig.mbgl_http_connect_timeout);
            }
            m_edt_val_http_client_cfg__connect_timeout.setText(Integer.toString(SharedPrefsManager.INTEGER_SHARED_PREF.MBGL_CONFIG__CONNECT_TIMEOUT.getValue()));
            if (SharedPrefsManager.INTEGER_SHARED_PREF.MBGL_CONFIG__READ_TIMEOUT.getValue() == null) {
                Log.d(TAG, String.format("onResume.async.runnable.run: int shared pref %s is not set - default to BuildConfig.mbgl_http_read_timeout==%d", SharedPrefsManager.INTEGER_SHARED_PREF.MBGL_CONFIG__READ_TIMEOUT.toString(), BuildConfig.mbgl_http_read_timeout));
                SharedPrefsManager.INTEGER_SHARED_PREF.MBGL_CONFIG__READ_TIMEOUT.setValue(BuildConfig.mbgl_http_read_timeout);
            }
            m_edt_val_http_client_cfg__read_timeout.setText(Integer.toString(SharedPrefsManager.INTEGER_SHARED_PREF.MBGL_CONFIG__READ_TIMEOUT.getValue()));
            if (SharedPrefsManager.INTEGER_SHARED_PREF.MBGL_CONFIG__MAX_REQ_PER_HOST.getValue() == null) {
                Log.d(TAG, String.format("onResume.async.runnable.run: int shared pref %s is not set - default to BuildConfig.mbgl_http_max_requests_per_host==%d", SharedPrefsManager.INTEGER_SHARED_PREF.MBGL_CONFIG__MAX_REQ_PER_HOST.toString(), BuildConfig.mbgl_http_max_requests_per_host));
                SharedPrefsManager.INTEGER_SHARED_PREF.MBGL_CONFIG__MAX_REQ_PER_HOST.setValue(BuildConfig.mbgl_http_max_requests_per_host);
            }
            m_edt_val_http_client_cfg__max_requests_per_host.setText(Integer.toString(SharedPrefsManager.INTEGER_SHARED_PREF.MBGL_CONFIG__MAX_REQ_PER_HOST.getValue()));
            if (SharedPrefsManager.INTEGER_SHARED_PREF.MBGL_CONFIG__CACHE_SIZE.getValue() == null) {
                Log.d(TAG, String.format("onResume.async.runnable.run: int shared pref %s is not set - default to BuildConfig.mbgl_http_cache_size==%d", SharedPrefsManager.INTEGER_SHARED_PREF.MBGL_CONFIG__CACHE_SIZE.toString(), BuildConfig.mbgl_http_cache_size));
                SharedPrefsManager.INTEGER_SHARED_PREF.MBGL_CONFIG__CACHE_SIZE.setValue(BuildConfig.mbgl_http_cache_size);
            }
            m_edt_val_http_client_cfg__cache_size.setText(Integer.toString(SharedPrefsManager.INTEGER_SHARED_PREF.MBGL_CONFIG__CACHE_SIZE.getValue()));

            boolean tile_source_is_local = SharedPrefsManager.BOOLEAN_SHARED_PREF.TM_TILE_SOURCE__IS_LOCAL.getValue();
            Log.d(TAG, String.format("onResume.async.runnable.run: boolean shared pref %s is: %b", SharedPrefsManager.BOOLEAN_SHARED_PREF.TM_TILE_SOURCE__IS_LOCAL.toString(), tile_source_is_local));
            int rb_val_mvt_source_sel = (
                tile_source_is_local
                    ? R.id.rb_mvt_local_tile_source_sel
                    : R.id.rb_mvt_remote_tile_source_sel
            );
            m_rg_val_mvt_source_sel.check(rb_val_mvt_source_sel);

            m_tv_tegola_console_output__scroll_max();

            //adjust main scroll view (since expandable sections may or may not have been expanded/collapsed based on initial settings)
            m_scvw_main__scroll_max();
        }, 50);

        super.onResume();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop: entered");
        super.onStop();
//        GoogleDriveFileDownloadManager.getInstance().disconnect_api_client();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: ClientAPI.uninitClient(m_controllerClient)");
        ClientAPI.uninitClient(m_controllerClient);
        super.onDestroy();
    }


    //user-UI-interaction listeners...
    @OnClick(R.id.btn_sect__mbgl_nfo__expand)
    protected void handleClick__expand_section(View v) {
        ExpandableRelativeLayout expandable_section = null;
        switch (v.getId()) {
            case R.id.btn_sect__mbgl_nfo__expand:
                expandable_section = m_vw_sect_content__mbgl_nfo;
                break;
            default: return;
        }
        final ExpandableRelativeLayout final_expandable_section = expandable_section;
        if (final_expandable_section.isExpanded()) {
            final_expandable_section.collapse();
            final_expandable_section.setExpanded(false);
        } else {
            final_expandable_section.expand();
            final_expandable_section.setExpanded(true);
        }
        new Handler().postDelayed(
            () -> reconcile_expandable_section(final_expandable_section),
            50
        );
    }

    //note that the validation (business logic) will be moved to the Model (MVP work-in-progress) but remains here for now even though it is not good architecture
    @OnEditorAction(
        {
            R.id.edt_val_http_client_cfg__connect_timeout,
            R.id.edt_val_http_client_cfg__read_timeout,
            R.id.edt_val_http_client_cfg__cache_size,
            R.id.edt_val_http_client_cfg__max_requests_per_host
        }
    )
    protected boolean handleEditorAction__mvt_client_http_config(TextView tv, int actionId, KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_NULL) {
            int etid = tv.getId();
            String s_val = tv.getText().toString().trim();
            Integer kc = keyEvent != null ? keyEvent.getKeyCode() : null;
            String keycode = kc != null ? String.valueOf(kc) : "<NO_KEY_PRESSED>";
            Log.d(
                TAG,
                String.format(
                    "handleEditorAction__mvt_client_http_config: handling %s action keyevent.keycode %s for edittext id %s",
                    actionId == EditorInfo.IME_NULL
                        ? "EditorInfo.IME_NULL"
                        : "EditorInfo.IME_ACTION_DONE",
                    kc != null ? String.valueOf(kc) : "<NO_KEY_PRESSED>",
                    getResources().getResourceName(etid)
                )
            );
            if (actionId == EditorInfo.IME_ACTION_DONE || (kc != null && kc == KeyEvent.KEYCODE_ENTER)) {
                boolean isvalid = false;
                int val = -1;
                try {
                    val = Integer.parseInt(s_val);
                    isvalid = val >= 0;
                } catch (NumberFormatException e) {}
                Log.d(
                    TAG,
                    String.format(
                        "handleEditorAction__mvt_client_http_config: %s condition for %s value %d",
                        isvalid ? "valid-value(commit)" : "invalid-value",
                        getResources().getResourceName(etid),
                        val
                    )
                );
                int default_val = -1;
                SharedPrefsManager.INTEGER_SHARED_PREF integer_shared_pref = null;
                switch (etid) {
                    case R.id.edt_val_http_client_cfg__connect_timeout: {
                        default_val = BuildConfig.mbgl_http_connect_timeout;
                        integer_shared_pref = SharedPrefsManager.INTEGER_SHARED_PREF.MBGL_CONFIG__CONNECT_TIMEOUT;
                        break;
                    }
                    case R.id.edt_val_http_client_cfg__read_timeout: {
                        default_val = BuildConfig.mbgl_http_read_timeout;
                        integer_shared_pref = SharedPrefsManager.INTEGER_SHARED_PREF.MBGL_CONFIG__READ_TIMEOUT;
                        break;
                    }
                    case R.id.edt_val_http_client_cfg__cache_size: {
                        default_val = BuildConfig.mbgl_http_cache_size;
                        integer_shared_pref = SharedPrefsManager.INTEGER_SHARED_PREF.MBGL_CONFIG__CACHE_SIZE;
                        break;
                    }
                    case R.id.edt_val_http_client_cfg__max_requests_per_host: {
                        default_val = BuildConfig.mbgl_http_max_requests_per_host;
                        integer_shared_pref = SharedPrefsManager.INTEGER_SHARED_PREF.MBGL_CONFIG__MAX_REQ_PER_HOST;
                        break;
                    }
                }
                if (isvalid)
                    integer_shared_pref.setValue(val);
                else
                    tv.setText(String.valueOf(default_val));
            }
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(tv.getWindowToken(), 0);
        }
        return false;   //do not consume event in order to allow Android to do default handling
    }

    @OnFocusChange(
        {
            R.id.edt_val_http_client_cfg__connect_timeout,
            R.id.edt_val_http_client_cfg__read_timeout,
            R.id.edt_val_http_client_cfg__cache_size,
            R.id.edt_val_http_client_cfg__max_requests_per_host,
            R.id.edt_postgis_remote_config_url
        }
    )
    protected void handleFocusChange(View v, boolean hasFocus) {
        Log.d(
            TAG,
            String.format(
                "handleFocusChange: %s %s focus",
                getResources().getResourceName(v.getId()),
                hasFocus ? "got" : "lost"
            )
        );
        if (!hasFocus) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    //Butterknife does not support OnCheckedChanged for RadioGroup, so we have to fall back to the version for individual radio buttons
    @OnCheckedChanged({R.id.rb_mvt_local_tile_source_sel, R.id.rb_mvt_remote_tile_source_sel})
    protected void handleCheckedChange__mvt_client_tile_source__local_or_remote(CompoundButton rb, boolean checked) {
        if (checked) {
            int checkedId = rb.getId();
            boolean
                want_local_tile_source = (checkedId == R.id.rb_mvt_local_tile_source_sel),
                setting_local_tile_source = SharedPrefsManager.BOOLEAN_SHARED_PREF.TM_TILE_SOURCE__IS_LOCAL.getValue();
            if (want_local_tile_source != setting_local_tile_source) {
                SharedPrefsManager.BOOLEAN_SHARED_PREF.TM_TILE_SOURCE__IS_LOCAL.setValue(want_local_tile_source);
                setting_local_tile_source = SharedPrefsManager.BOOLEAN_SHARED_PREF.TM_TILE_SOURCE__IS_LOCAL.getValue();
                Log.d(TAG, "handleCheckedChange__mvt_client_tile_source__local_or_remote: changed shared pref setting " + SharedPrefsManager.BOOLEAN_SHARED_PREF.TM_TILE_SOURCE__IS_LOCAL + " value to: " + setting_local_tile_source);
            }
            if (setting_local_tile_source) {//then get/update settings related to using local mvt server
                //set srvr provider type (postGIS/geopackage) based on PersistentConfigSettingsManager.TM_PROVIDER__IS_GEOPACKAGE val
                if (SharedPrefsManager.BOOLEAN_SHARED_PREF.TM_PROVIDER__IS_GEOPACKAGE.getValue() == true) {
                    m_rb_gpkg_provider_type_sel.setChecked(true);
                } else {
                    m_rb_postgis_provider_type_sel.setChecked(true);
                    //set srvr config selection type (local/remote) based on PersistentConfigSettingsManager.TM_CONFIG_TOML__IS_REMOTE val
                    if (SharedPrefsManager.BOOLEAN_SHARED_PREF.TM_CONFIG_TOML__IS_REMOTE.getValue() == true) {
                        m_rb_postgis_remote_config_type_sel.setChecked(true);
                    } else {
                        m_rb_postgis_local_config_type_sel.setChecked(true);
                    }
                }
            } else //get/update settings relatde to remote tile mvt server
                synchronize_spinner_remote_tile_server();

            m_sect__remote_srvr_nfo.setVisibility(want_local_tile_source ? View.GONE : View.VISIBLE);
            m_sect__local_srvr_nfo.setVisibility(want_local_tile_source ? View.VISIBLE : View.GONE);
            m_tv_tegola_console_output__scroll_max();
            m_scvw_main__scroll_max();
        }
    }

    @OnCheckedChanged({R.id.rb_postgis_provider_type_sel, R.id.rb_gpkg_provider_type_sel})
    protected void handleCheckedChange__local_mvt_srvr_provider_type(CompoundButton rb, boolean checked) {
        if (checked) {
            int checkedId = rb.getId();
            switch (checkedId) {
                case R.id.rb_postgis_provider_type_sel: {
                    m_sect__gpkg_provider_spec.setVisibility(View.GONE);
                    m_sect__postgis_provider_spec.setVisibility(View.VISIBLE);
                    SharedPrefsManager.BOOLEAN_SHARED_PREF.TM_PROVIDER__IS_GEOPACKAGE.setValue(false);
                    break;
                }
                case R.id.rb_gpkg_provider_type_sel: {
                    m_sect__postgis_provider_spec.setVisibility(View.GONE);
                    m_sect__gpkg_provider_spec.setVisibility(View.VISIBLE);
                    SharedPrefsManager.BOOLEAN_SHARED_PREF.TM_PROVIDER__IS_GEOPACKAGE.setValue(true);
                    synchronize_spinner_gpkg_bundle();
                    synchronize_spinner_gpkg_bundle_props();
                    break;
                }
            }
        }
    }

    @OnCheckedChanged({R.id.rb_postgis_local_config_type_sel, R.id.rb_postgis_remote_config_type_sel})
    protected void handleCheckedChange__local_mvt_srvr_postgis_config_type(CompoundButton rb, boolean checked) {
        if (checked) {
            int checkedId = rb.getId();
            switch (checkedId) {
                case R.id.rb_postgis_local_config_type_sel: {
                    boolean sdcardmounted = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
                    m_btn_postgis_local_config_import__sdcard.setBackgroundColor(sdcardmounted ? ContextCompat.getColor(getApplicationContext(), android.R.color.holo_green_light) : ContextCompat.getColor(getApplicationContext(), android.R.color.holo_red_dark));
                    m_btn_postgis_local_config_import__sdcard.setEnabled(sdcardmounted);
                    m_vw_postgis_remote_config__container.setVisibility(View.GONE);
                    m_vw_postgis_local_config_sel__container.setVisibility(View.VISIBLE);
                    SharedPrefsManager.BOOLEAN_SHARED_PREF.TM_CONFIG_TOML__IS_REMOTE.setValue(false);
                    synchronize_spinner_local_config();
                    break;
                }
                case R.id.rb_postgis_remote_config_type_sel: {
                    m_vw_postgis_local_config_sel__container.setVisibility(View.GONE);
                    m_vw_postgis_remote_config__container.setVisibility(View.VISIBLE);
                    SharedPrefsManager.BOOLEAN_SHARED_PREF.TM_CONFIG_TOML__IS_REMOTE.setValue(true);
                    break;
                }
            }
        }
    }

    @OnClick(R.id.btn_postgis_local_config_import__sdcard)
    protected void handleClick__local_mvt_srvr_postgis_local_config__import(View v) {
        import_config_toml__from_sdcard();
    }

    @OnItemSelected(R.id.spinner_postgis_local_config_sel)
    protected void handleItemSelected__local_mvt_srvr_postgis_local_config(AdapterView<?> adapter, View view, int position, long id) {
        String s_sel_val = adapter.getItemAtPosition(position).toString();
        Log.d(TAG, "handleItemSelected__local_mvt_srvr_postgis_local_config: triggered item selection @ position " + position + " with value " + (s_sel_val == null ? "null" : "\"" + s_sel_val + "\""));

        String s_cached_config_sel__local_val = SharedPrefsManager.STRING_SHARED_PREF.TM_POSTGIS_LOCAL_CONFIG_TOML__SELECTION.getValue();
        Log.d(TAG, "handleItemSelected__local_mvt_srvr_postgis_local_config: shared pref setting " + SharedPrefsManager.STRING_SHARED_PREF.TM_POSTGIS_LOCAL_CONFIG_TOML__SELECTION.toString() + " current value is \"" + s_cached_config_sel__local_val + "\"");

        boolean no_config_files = (s_sel_val == null || s_sel_val.compareTo(getString(R.string.srvr_config_type__local__no_config_files_found)) == 0);
        if (no_config_files) {
            Log.d(TAG, "handleItemSelected__local_mvt_srvr_postgis_local_config: no-config-files condition!");
            if (!s_cached_config_sel__local_val.isEmpty()) {
                Log.d(TAG, "handleItemSelected__local_mvt_srvr_postgis_local_config: clearing shared pref setting " + SharedPrefsManager.STRING_SHARED_PREF.TM_POSTGIS_LOCAL_CONFIG_TOML__SELECTION.toString() + " value (currently \"" + s_cached_config_sel__local_val + "\")");
                Toast.makeText(getApplicationContext(), "Clearing setting value for local config toml file selection since there are none available", Toast.LENGTH_SHORT).show();
                SharedPrefsManager.STRING_SHARED_PREF.TM_POSTGIS_LOCAL_CONFIG_TOML__SELECTION.setValue("");
            } else {
                Log.d(TAG, "handleItemSelected__local_mvt_srvr_postgis_local_config: skipping change to shared pref setting " + SharedPrefsManager.STRING_SHARED_PREF.TM_POSTGIS_LOCAL_CONFIG_TOML__SELECTION.toString() + " since it is already cleared (value is \"" + s_cached_config_sel__local_val + "\")");
            }

            //edit button obviously not applicable in this case
            m_btn_config_sel_local__edit_file.setVisibility(View.GONE);
            m_btn_config_sel_local__edit_file.setEnabled(false);
            //and neither is MVT srvr control (start/stop) button
            m_btn_local_mvt_srvr_ctrl.setEnabled(false);

            //finally display alertdialog notifying user that tegola cannot be used until a config file is imported
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this, R.style.alert_dialog));
            alertDialogBuilder.setTitle(getString(R.string.srvr_config_type__local__no_config_files_found));
            alertDialogBuilder
                    .setMessage(getString(R.string.srvr_config_type__local__no_config_files_found__alert_msg))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.OK), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } else {
            //first, update shared pref val as necessary - does sel value differ from cached?
            if (s_cached_config_sel__local_val.compareTo(s_sel_val) != 0) {
                Toast.makeText(getApplicationContext(), "Saving new setting value for local config toml file \"" + s_sel_val + "\" selection", Toast.LENGTH_SHORT).show();
                //now update shared pref
                SharedPrefsManager.STRING_SHARED_PREF.TM_POSTGIS_LOCAL_CONFIG_TOML__SELECTION.setValue(s_sel_val);
                Log.d(TAG, "handleItemSelected__local_mvt_srvr_postgis_local_config: changed setting " + SharedPrefsManager.STRING_SHARED_PREF.TM_POSTGIS_LOCAL_CONFIG_TOML__SELECTION.toString() + " value from \"" + s_cached_config_sel__local_val + "\" to \"" + SharedPrefsManager.STRING_SHARED_PREF.TM_POSTGIS_LOCAL_CONFIG_TOML__SELECTION.getValue() + "\"");
            } else {
                //no change to shared pref val
                Log.d(TAG, "handleItemSelected__local_mvt_srvr_postgis_local_config: skipping change to shared pref setting " + SharedPrefsManager.STRING_SHARED_PREF.TM_POSTGIS_LOCAL_CONFIG_TOML__SELECTION.toString() + " value (\"" + SharedPrefsManager.STRING_SHARED_PREF.TM_POSTGIS_LOCAL_CONFIG_TOML__SELECTION.getValue() + "\") since new value (\"" + s_sel_val + "\") is no different");
            }

            //now update m_btn_config_sel_local__edit_file UI based on existence of current local config toml file selection setting (SharedPrefsManager.STRING_SHARED_PREF.TM_POSTGIS_LOCAL_CONFIG_TOML__SELECTION.getValue())
            File file = new File(getFilesDir().getPath() + "/" + SharedPrefsManager.STRING_SHARED_PREF.TM_POSTGIS_LOCAL_CONFIG_TOML__SELECTION.getValue());
            m_btn_config_sel_local__edit_file.setVisibility(file.exists() ? View.VISIBLE : View.GONE);
            m_btn_config_sel_local__edit_file.setEnabled(file.exists());
            //and same MVT srvr control (start/stop) button
            m_btn_local_mvt_srvr_ctrl.setEnabled(file.exists());
        }
    }

    @OnClick(R.id.btn_postgis_local_config_sel__edit_file)
    protected void handleClick__local_mvt_srvr_postgis_local_config__edit(View v) {
        edit_local_config_file(SharedPrefsManager.STRING_SHARED_PREF.TM_POSTGIS_LOCAL_CONFIG_TOML__SELECTION.getValue());
    }

    @OnEditorAction(R.id.edt_postgis_remote_config_url)
    protected boolean handleEditorAction__local_mvt_srvr_postgis_remote_config_url(TextView tv, int actionId, KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_NULL)
            validate__m_edt_val_config_sel__remote();
        return false;
    }

    @OnClick(R.id.btn_postgis_remote_config_url__apply_changes)
    protected void handleClick__local_mvt_srvr_postgis_remote_config_url__apply_changes(View v) {
        String s_remote_config_toml_sel_normalized = m_edt_postgis_remote_config_url.getText().toString();
        Log.d(TAG, "handleClick__local_mvt_srvr_postgis_remote_config_url__apply_changes: triggered remote config change with value " + (s_remote_config_toml_sel_normalized == null ? "null" : "\"" + s_remote_config_toml_sel_normalized + "\""));
        if (s_remote_config_toml_sel_normalized == null) {
            Log.d(TAG, "handleClick__local_mvt_srvr_postgis_remote_config_url__apply_changes: normalizing remote config change (null) to \"\"");
            s_remote_config_toml_sel_normalized = "";
        }
        String s_old_config_sel__remote_val = SharedPrefsManager.STRING_SHARED_PREF.TM_POSTGIS_REMOTE_CONFIG_TOML__SELECTION.getValue();
        Log.d(TAG, "handleClick__local_mvt_srvr_postgis_remote_config_url__apply_changes: shared pref setting " + SharedPrefsManager.STRING_SHARED_PREF.TM_POSTGIS_REMOTE_CONFIG_TOML__SELECTION.toString() + " current value is \"" + SharedPrefsManager.STRING_SHARED_PREF.TM_POSTGIS_REMOTE_CONFIG_TOML__SELECTION.getValue() + "\"");
        if (s_old_config_sel__remote_val.compareTo(s_remote_config_toml_sel_normalized) != 0) {
            if (s_remote_config_toml_sel_normalized.isEmpty())
                Toast.makeText(getApplicationContext(), "Clearing remote config toml file selection", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getApplicationContext(), "Saving new setting value for remote config toml file https://" + s_remote_config_toml_sel_normalized, Toast.LENGTH_SHORT).show();
            SharedPrefsManager.STRING_SHARED_PREF.TM_POSTGIS_REMOTE_CONFIG_TOML__SELECTION.setValue(s_remote_config_toml_sel_normalized);
            Log.d(TAG, "handleClick__local_mvt_srvr_postgis_remote_config_url__apply_changes: changed setting " + SharedPrefsManager.STRING_SHARED_PREF.TM_POSTGIS_REMOTE_CONFIG_TOML__SELECTION.toString() + " value from \"" + s_old_config_sel__remote_val + "\" to \"" + SharedPrefsManager.STRING_SHARED_PREF.TM_POSTGIS_REMOTE_CONFIG_TOML__SELECTION.getValue() + "\"");
            m_btn_postgis_remote_config_url__apply_changes.setEnabled(false);
        } else {
            //no change to share pref val - do nothing other than log
            Log.d(TAG, "handleClick__local_mvt_srvr_postgis_remote_config_url__apply_changes: skipping change to shared pref setting " + SharedPrefsManager.STRING_SHARED_PREF.TM_POSTGIS_REMOTE_CONFIG_TOML__SELECTION.toString() + " value (\"" + SharedPrefsManager.STRING_SHARED_PREF.TM_POSTGIS_REMOTE_CONFIG_TOML__SELECTION.getValue() + "\") since normalized new value (\"" + s_remote_config_toml_sel_normalized + "\") is no different");
        }
        synchronize_edittext_config_remote();
    }

    @OnItemSelected(R.id.spinner_gpkg_bundle_sel)
    protected void handleItemSelected__local_mvt_srvr_gpkgbundle(AdapterView<?> adapter, View view, int position, long id) {
        String s_sel_val = adapter.getItemAtPosition(position).toString();
        Log.d(TAG, "handleItemSelected__local_mvt_srvr_gpkgbundle: triggered item selection @ position " + position + " with value " + (s_sel_val == null ? "null" : "\"" + s_sel_val + "\""));

        String s_cached_gpkg_bundle_val = SharedPrefsManager.STRING_SHARED_PREF.TM_PROVIDER__GPKG_BUNDLE__SELECTION.getValue();
        Log.d(TAG, "handleItemSelected__local_mvt_srvr_gpkgbundle: shared pref setting " + SharedPrefsManager.STRING_SHARED_PREF.TM_PROVIDER__GPKG_BUNDLE__SELECTION.toString() + " current value is \"" + s_cached_gpkg_bundle_val + "\"");

        boolean no_gpkg_bundles = (s_sel_val == null || s_sel_val.compareTo(getString(R.string.srvr_provider_type__gpkg__no_geopackage_bundles_installed)) == 0);
        if (no_gpkg_bundles) {
            Log.d(TAG, "handleItemSelected__local_mvt_srvr_gpkgbundle: no-gpkg-bundles condition!");
            if (!s_cached_gpkg_bundle_val.isEmpty()) {
                Log.d(TAG, "handleItemSelected__local_mvt_srvr_gpkgbundle: clearing shared pref setting " + SharedPrefsManager.STRING_SHARED_PREF.TM_PROVIDER__GPKG_BUNDLE__SELECTION.toString() + " value (currently \"" + s_cached_gpkg_bundle_val + "\")");
                Toast.makeText(getApplicationContext(), "Clearing setting value for geopackage-bundle selection since there are none installed", Toast.LENGTH_SHORT).show();
                SharedPrefsManager.STRING_SHARED_PREF.TM_PROVIDER__GPKG_BUNDLE__SELECTION.setValue("");
            } else {
                Log.d(TAG, "handleItemSelected__local_mvt_srvr_gpkgbundle: skipping change to shared pref setting " + SharedPrefsManager.STRING_SHARED_PREF.TM_PROVIDER__GPKG_BUNDLE__SELECTION.toString() + " since it is already cleared (value is \"" + s_cached_gpkg_bundle_val + "\")");
            }

            m_btn_local_mvt_srvr_ctrl.setEnabled(false);

            //finally display alertdialog notifying user that tegola cannot be used until a gpkg-bundle is installed
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this, R.style.alert_dialog));
            alertDialogBuilder.setTitle(getString(R.string.srvr_provider_type__gpkg__no_geopackage_bundles_installed));
            alertDialogBuilder
                    .setMessage(getString(R.string.srvr_provider_type__gpkg__no_geopackage_bundles_installed__alert_msg))
                    .setCancelable(false)
                    .setNegativeButton(
                            getString(R.string.cancel),
                            (dialog, id1) -> dialog.dismiss()
                    )
                    .setPositiveButton(
                            getString(R.string.OK),
                            (dialog, id12) -> {
                                startActivityForResult(new Intent(MainActivity.this, InstallGpkgBundleActivity.class), REQUEST_CODES.REQUEST_CODE__INSTALL_GPKG_BUNDLE);
                                dialog.dismiss();
                            }
                    );
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } else {
            //first, update shared pref val as necessary - does sel value differ from cached?
            if (s_cached_gpkg_bundle_val.compareTo(s_sel_val) != 0) {
                Toast.makeText(getApplicationContext(), "Saving new setting value for geopackage-bundle \"" + s_sel_val + "\" selection", Toast.LENGTH_SHORT).show();
                //now update shared pref
                SharedPrefsManager.STRING_SHARED_PREF.TM_PROVIDER__GPKG_BUNDLE__SELECTION.setValue(s_sel_val);
                Log.d(TAG, "handleItemSelected__local_mvt_srvr_gpkgbundle: changed setting " + SharedPrefsManager.STRING_SHARED_PREF.TM_PROVIDER__GPKG_BUNDLE__SELECTION.toString() + " value from \"" + s_cached_gpkg_bundle_val + "\" to \"" + SharedPrefsManager.STRING_SHARED_PREF.TM_PROVIDER__GPKG_BUNDLE__SELECTION.getValue() + "\"");
            } else {
                //no change to shared pref val
                Log.d(TAG, "handleItemSelected__local_mvt_srvr_gpkgbundle: skipping change to shared pref setting " + SharedPrefsManager.STRING_SHARED_PREF.TM_PROVIDER__GPKG_BUNDLE__SELECTION.toString() + " value (\"" + SharedPrefsManager.STRING_SHARED_PREF.TM_PROVIDER__GPKG_BUNDLE__SELECTION.getValue() + "\") since new value (\"" + s_sel_val + "\") is no different");
            }

            //now update UI based on existence of current local geopackage-bundle selection setting (SharedPrefsManager.STRING_SHARED_PREF.TM_PROVIDER__GPKG_BUNDLE__SELECTION.getValue())
            File f_gpkg_bundles_root_dir = null;
            try {
                f_gpkg_bundles_root_dir = GPKG.Local.F_GPKG_BUNDLE_ROOT_DIR.getInstance(getApplicationContext());
                File f_gpkg_bundle = new File(f_gpkg_bundles_root_dir, SharedPrefsManager.STRING_SHARED_PREF.TM_PROVIDER__GPKG_BUNDLE__SELECTION.getValue());
                //and same MVT srvr control (start/stop) button
                m_btn_local_mvt_srvr_ctrl.setEnabled(f_gpkg_bundle.exists());
                if (f_gpkg_bundle.exists())
                    synchronize_spinner_gpkg_bundle_props();
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @OnItemSelected(R.id.spinner_gpkg_bundle_props_sel)
    protected void handleItemSelected__local_mvt_srvr_gpkgbundle_props(AdapterView<?> adapter, View view, int position, long id) {
        String s_sel_val = adapter.getItemAtPosition(position).toString();
        Log.d(TAG, "handleItemSelected__local_mvt_srvr_gpkgbundle_props: triggered item selection @ position " + position + " with value " + (s_sel_val == null ? "null" : "\"" + s_sel_val + "\""));

        String s_cached_gpkg_bundle_config_val = SharedPrefsManager.STRING_SHARED_PREF.TM_PROVIDER__GPKG_BUNDLE_PROPS__SELECTION.getValue();
        Log.d(TAG, "handleItemSelected__local_mvt_srvr_gpkgbundle_props: shared pref setting " + SharedPrefsManager.STRING_SHARED_PREF.TM_PROVIDER__GPKG_BUNDLE_PROPS__SELECTION.toString() + " current value is \"" + s_cached_gpkg_bundle_config_val + "\"");

        boolean no_gpkg_bundle_cfg = (s_sel_val == null);
        if (no_gpkg_bundle_cfg) {
            Log.d(TAG, "handleItemSelected__local_mvt_srvr_gpkgbundle_props: no-gpkg-no_gpkg_bundle_cfg condition!");
            if (!s_cached_gpkg_bundle_config_val.isEmpty()) {
                Log.d(TAG, "handleItemSelected__local_mvt_srvr_gpkgbundle_props: clearing shared pref setting " + SharedPrefsManager.STRING_SHARED_PREF.TM_PROVIDER__GPKG_BUNDLE_PROPS__SELECTION.toString() + " value (currently \"" + s_cached_gpkg_bundle_config_val + "\")");
                Toast.makeText(getApplicationContext(), "Clearing setting value for geopackage-bundle config selection since there are none installed", Toast.LENGTH_SHORT).show();
                SharedPrefsManager.STRING_SHARED_PREF.TM_PROVIDER__GPKG_BUNDLE_PROPS__SELECTION.setValue("");
            } else {
                Log.d(TAG, "handleItemSelected__local_mvt_srvr_gpkgbundle_props: skipping change to shared pref setting " + SharedPrefsManager.STRING_SHARED_PREF.TM_PROVIDER__GPKG_BUNDLE_PROPS__SELECTION.toString() + " since it is already cleared (value is \"" + s_cached_gpkg_bundle_config_val + "\")");
            }

            m_btn_local_mvt_srvr_ctrl.setEnabled(false);

            //finally display alertdialog notifying user that tegola cannot be used until a gpkg-bundle is installed
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this, R.style.alert_dialog));
            alertDialogBuilder.setTitle(getString(R.string.srvr_provider_type__gpkg__no_geopackage_bundle_props_installed));
            alertDialogBuilder
                    .setMessage(getString(R.string.srvr_provider_type__gpkg__no_geopackage_bundle_configs_installed__alert_msg))
                    .setCancelable(false)
                    .setNegativeButton(
                            getString(R.string.cancel),
                            (dialog, id1) -> dialog.dismiss()
                    )
                    .setPositiveButton(
                            getString(R.string.OK),
                            (dialog, id12) -> {
                                startActivityForResult(new Intent(MainActivity.this, InstallGpkgBundleActivity.class), REQUEST_CODES.REQUEST_CODE__INSTALL_GPKG_BUNDLE);
                                dialog.dismiss();
                            }
                    );
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } else {
            //first, update shared pref val as necessary - does sel value differ from cached?
            if (s_cached_gpkg_bundle_config_val.compareTo(s_sel_val) != 0) {
                Toast.makeText(getApplicationContext(), "Saving new setting value for geopackage-bundle config \"" + s_sel_val + "\" selection", Toast.LENGTH_SHORT).show();
                //now update shared pref
                SharedPrefsManager.STRING_SHARED_PREF.TM_PROVIDER__GPKG_BUNDLE_PROPS__SELECTION.setValue(s_sel_val);
                Log.d(TAG, "handleItemSelected__local_mvt_srvr_gpkgbundle_props: changed setting " + SharedPrefsManager.STRING_SHARED_PREF.TM_PROVIDER__GPKG_BUNDLE_PROPS__SELECTION.toString() + " value from \"" + s_cached_gpkg_bundle_config_val + "\" to \"" + SharedPrefsManager.STRING_SHARED_PREF.TM_PROVIDER__GPKG_BUNDLE_PROPS__SELECTION.getValue() + "\"");
            } else {
                //no change to shared pref val
                Log.d(TAG, "handleItemSelected__local_mvt_srvr_gpkgbundle_props: skipping change to shared pref setting " + SharedPrefsManager.STRING_SHARED_PREF.TM_PROVIDER__GPKG_BUNDLE_PROPS__SELECTION.toString() + " value (\"" + SharedPrefsManager.STRING_SHARED_PREF.TM_PROVIDER__GPKG_BUNDLE_PROPS__SELECTION.getValue() + "\") since new value (\"" + s_sel_val + "\") is no different");
            }

            //now update UI based on existence of current local geopackage-bundle config selection setting (SharedPrefsManager.STRING_SHARED_PREF.TM_PROVIDER__GPKG_BUNDLE_PROPS__SELECTION.getValue())
            File f_gpkg_bundle_dir = null;
            try {
                f_gpkg_bundle_dir = new File(GPKG.Local.F_GPKG_BUNDLE_ROOT_DIR.getInstance(getApplicationContext()), SharedPrefsManager.STRING_SHARED_PREF.TM_PROVIDER__GPKG_BUNDLE__SELECTION.getValue());
                File[] f_gpkg_bundle_props = f_gpkg_bundle_dir.listFiles((dir, name) -> name.endsWith(".properties"));
                //and same MVT srvr control (start/stop) button
                m_btn_local_mvt_srvr_ctrl.setEnabled(f_gpkg_bundle_props.length > 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @OnItemSelected(R.id.spinner_remote_tile_server_sel)
    protected void handleItemSelected__remote_mvt_srvr(AdapterView<?> adapter, View view, int position, long id) {
        String s_sel_val = adapter.getItemAtPosition(position).toString();
        Log.d(TAG, String.format("handleItemSelected_remotetileserver: triggered item selection @ position %d with value %s", position, (s_sel_val == null ? "null" : "\"" + s_sel_val + "\"")));

        String s_cached_sel_canon_remote_tile_server = SharedPrefsManager.STRING_SHARED_PREF.TM_TILE_SOURCE__REMOTE.getValue();
        Log.d(TAG, String.format("handleItemSelected_remotetileserver: shared pref setting %s current value is \"%s\"", SharedPrefsManager.STRING_SHARED_PREF.TM_TILE_SOURCE__REMOTE.toString(), s_cached_sel_canon_remote_tile_server));

        boolean no_remote_tile_srvr_sel = (s_sel_val == null);
        if (no_remote_tile_srvr_sel) {
            Log.d(TAG, "handleItemSelected_remotetileserver: no_remote_tile_srvr_sel condition!");
            if (!s_cached_sel_canon_remote_tile_server.isEmpty()) {
                Log.d(TAG, String.format("handleItemSelected_remotetileserver: clearing shared pref setting %s value (currently \"%s\")", SharedPrefsManager.STRING_SHARED_PREF.TM_TILE_SOURCE__REMOTE.toString(), s_cached_sel_canon_remote_tile_server));
                Toast.makeText(getApplicationContext(), "Clearing setting value for canonical remote tile server selection since there are none available", Toast.LENGTH_SHORT).show();
                SharedPrefsManager.STRING_SHARED_PREF.TM_TILE_SOURCE__REMOTE.setValue("");
            } else {
                Log.d(TAG, String.format("handleItemSelected_remotetileserver: skipping change to shared pref setting %s since it is already cleared (value is \"%s\")", SharedPrefsManager.STRING_SHARED_PREF.TM_TILE_SOURCE__REMOTE.toString(), s_cached_sel_canon_remote_tile_server));
            }

            m_btn_stream_tiles_from_remote.setEnabled(false);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this, R.style.alert_dialog));
            alertDialogBuilder.setTitle(getString(R.string.no_valid_canonical_remote_tile_server_urls_found));
            alertDialogBuilder
                    .setMessage(getString(R.string.no_valid_canonical_remote_tile_server_urls_found__alert_msg))
                    .setCancelable(false)
                    .setNeutralButton(
                            getString(R.string.OK),
                            (dialog, id1) -> dialog.dismiss()
                    );
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } else {
            //first, update shared pref val as necessary - does sel value differ from cached?
            if (s_cached_sel_canon_remote_tile_server.compareTo(s_sel_val) != 0) {
                Toast.makeText(getApplicationContext(), String.format("Saving new setting value for remote tile server \"%s\" selection", s_sel_val), Toast.LENGTH_SHORT).show();
                //now update shared pref
                SharedPrefsManager.STRING_SHARED_PREF.TM_TILE_SOURCE__REMOTE.setValue(s_sel_val);
                Log.d(TAG, String.format("handleItemSelected_remotetileserver: changed setting %s value from \"%s\" to \"%s\"", SharedPrefsManager.STRING_SHARED_PREF.TM_TILE_SOURCE__REMOTE.toString(), s_cached_sel_canon_remote_tile_server, SharedPrefsManager.STRING_SHARED_PREF.TM_PROVIDER__GPKG_BUNDLE_PROPS__SELECTION.getValue()));
            } else {
                //no change to shared pref val
                Log.d(TAG, String.format("handleItemSelected_remotetileserver: skipping change to shared pref setting %s value (\"%s\") since new value (\"%s\") is no different", SharedPrefsManager.STRING_SHARED_PREF.TM_TILE_SOURCE__REMOTE.toString(), SharedPrefsManager.STRING_SHARED_PREF.TM_TILE_SOURCE__REMOTE.getValue(), s_sel_val));
            }

            m_btn_stream_tiles_from_remote.setEnabled(true);
        }
    }

    @OnClick(R.id.btn_local_mvt_srvr_ctrl)
    protected void handleClick__local_mvt_srvr__ctrl(View v) {
        Button btn_srvr_ctrl = (Button)v;
        Boolean srvr_started = (Boolean)btn_srvr_ctrl.getTag(R.id.TAG__SRVR_RUNNING);
        if (srvr_started == null || !srvr_started) {
            start_mvt_server();
            if (m_vw_sect_content__mbgl_nfo.isExpanded()) {
                m_vw_sect_content__mbgl_nfo.collapse();
                m_vw_sect_content__mbgl_nfo.setExpanded(false);
                reconcile_expandable_section(m_vw_sect_content__mbgl_nfo);
            }
        } else {
            stop_mvt_server();
        }
    }

    @OnClick(R.id.btn_remote_mvt_srvr__open_stream)
    protected void handleClick__remote_mvt_srvr__open_stream(View v) {
        if (m_btn_stream_tiles_from_remote.getText().toString().compareTo(getString(R.string.open_tile_stream)) == 0) {//cheap way for state control - state: STREAM_CLOSED
            String
                    root_url = SharedPrefsManager.STRING_SHARED_PREF.TM_TILE_SOURCE__REMOTE.getValue(),
                    endpoint = "/capabilities";
            if (root_url.endsWith(".json")) {
                int i = root_url.lastIndexOf("/");
                endpoint = root_url.substring(i);
                root_url = root_url.substring(0, i);
            }
            final String final_root_url = root_url, final_endpoint = endpoint, final_url = final_root_url + final_endpoint;
            //validate url first!
            if (HTTP.isValidUrl(final_url)) {
                Log.d(TAG, "handleClick__remote_mvt_srvr__open_stream: root_url==\"" + final_root_url + "\"; endpoint==\"" + final_endpoint + "\"");
                if (!root_url.isEmpty() && !endpoint.isEmpty()) {
                    Log.d(TAG, "handleClick__remote_mvt_srvr__open_stream: requesting capabilities from " + final_root_url);
                    mvstate = MAPVIEW_STATE.OPENING_STREAM__REMOTE;
                    new Handler().postDelayed(
                            () -> m_controllerClient.mvt_server__rest_api__get_json(
                                    final_root_url,
                                    final_endpoint,
                                    Constants.Strings.INTENT.ACTION.REQUEST.MVT_SERVER.REST_API.GET_JSON.EXTRA_KEY.PURPOSE.VALUE.LOAD_MAP.STRING
                            ),
                            50
                    );
                }
            } else {
                mvstate = MAPVIEW_STATE.STREAM_CLOSED;
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this, R.style.alert_dialog));
                alertDialogBuilder.setTitle("Cannot fetch from remote tile server!");
                alertDialogBuilder
                        .setMessage("Malformed remote tile server URL: \"" + final_url + "\"")
                        .setCancelable(false)
                        .setPositiveButton(
                                getString(R.string.OK),
                                (dialog, id) -> dialog.dismiss()
                        );
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                return;
            }
            if (m_vw_sect_content__mbgl_nfo.isExpanded()) {
                m_vw_sect_content__mbgl_nfo.collapse();
                m_vw_sect_content__mbgl_nfo.setExpanded(false);
                reconcile_expandable_section(m_vw_sect_content__mbgl_nfo);
            }
        } else {
            mbgl_map_stop();
            mvstate = MAPVIEW_STATE.STREAM_CLOSED;
            m_btn_stream_tiles_from_remote.setText(getString(R.string.open_tile_stream));
        }
    }

    private void synchronize_spinner_remote_tile_server() {
        //1. enumerate canonical remote servers in shared prefs - if empty, then set from resources
        String[] s_canon_remote_srvrs = SharedPrefsManager.STRING_ARRAY_SHARED_PREF.TM_CANONICAL_REMOTE_TILE_SERVERS.getValue();
        if (s_canon_remote_srvrs.length == 0) {
            final String[] sary_canonical_remote_tile_servers = getResources().getStringArray(R.array.remote_tile_servers__canonical);
            SharedPrefsManager.STRING_ARRAY_SHARED_PREF.TM_CANONICAL_REMOTE_TILE_SERVERS.setValue(sary_canonical_remote_tile_servers);
            s_canon_remote_srvrs = SharedPrefsManager.STRING_ARRAY_SHARED_PREF.TM_CANONICAL_REMOTE_TILE_SERVERS.getValue();
        }

        //2.1 remove current entries from spinner_remote_tile_server dataAdapter
        Log.d(TAG, "synchronize_spinner_remote_tile_server: clearing spinner items");
        m_spinner_val_remote_tile_server__items.clear();

        if (s_canon_remote_srvrs.length > 0) {//found canon remote tile servers
            //2.2 add found canon remote tile server urls into spinner_remote_tile_server dataAdapter
            for (int i = 0; i < s_canon_remote_srvrs.length; i++) {
                String s_url = s_canon_remote_srvrs[i];
                if (s_url != null)
                    s_url = s_url.trim();
                if (s_url != null && !s_url.isEmpty() && HTTP.isValidUrl(s_url)) {
                    Log.d(TAG, String.format("synchronize_spinner_remote_tile_server: found valid canon remote tile server url \"%s\", adding it to spinner_remote_tile_server data adapter!", s_url));
                    m_spinner_val_remote_tile_server__items.add(s_url);
                } else {
                    Log.d(TAG, String.format("synchronize_spinner_remote_tile_server: \"%s\" is not a valid url... will NOT be added to spinner_remote_tile_server", s_url));
                }
            }
        }
        if (m_spinner_val_remote_tile_server__items.size() == 0) {//no valid canon remote server urls found
            //2.2 add "not found" item @ position 0
            String s_no_valid_canonical_remote_tile_server_urls_found = getString(R.string.no_valid_canonical_remote_tile_server_urls_found);
            Log.d(TAG, String.format("synchronize_spinner_remote_tile_server: no valid canonical remote tile server URLs! adding \"%s\" to spinner items", s_no_valid_canonical_remote_tile_server_urls_found));
            m_spinner_val_gpkg_bundle_props__items.add(s_no_valid_canonical_remote_tile_server_urls_found);
        }

        //3. reconcile ConfigSettings.STRING_CONFIG_SETTING.TM_TILE_SOURCE__REMOTE setting with m_spinner_val_remote_tile_server items and update selection as necessary
        int i_sel_pos = m_spinner_val_remote_tile_server__dataadapter.getPosition(SharedPrefsManager.STRING_SHARED_PREF.TM_TILE_SOURCE__REMOTE.getValue());
        if (i_sel_pos != -1) {
            Log.d(TAG, String.format("synchronize_spinner_remote_tile_server: synchronizing shared pref setting %s current value \"%s\" spinner item selection to existing item position %d", SharedPrefsManager.STRING_SHARED_PREF.TM_TILE_SOURCE__REMOTE.toString(), SharedPrefsManager.STRING_SHARED_PREF.TM_TILE_SOURCE__REMOTE.getValue(), i_sel_pos));
        } else {
            //note that we must reset i_sel_pos to 0 here since it will be assigned -1 if we are here
            i_sel_pos = 0;
//            Log.d(TAG, String.format("synchronize_spinner_remote_tile_server: cannot synchronize shared prefs setting %s current value \"%s\" to spinner item selection since spinner does not currently have a selectable item with that value; setting spinner selected item position to %d for value \"%s\"", SharedPrefsManager.STRING_SHARED_PREF.TM_TILE_SOURCE__REMOTE.toString(), SharedPrefsManager.STRING_SHARED_PREF.TM_PROVIDER__GPKG_BUNDLE_PROPS__SELECTION.getValue(), i_sel_pos, m_spinner_val_gpkg_bundle__items.get(i_sel_pos)));
        }

        //4. commit changes to spinner to allow for listener to react
        m_spinner_val_remote_tile_server.setSelection(i_sel_pos);
        m_spinner_val_remote_tile_server__dataadapter.notifyDataSetChanged();
    }

    private void synchronize_spinner_gpkg_bundle() {
        //1. enumerate geopackage-bundles and display results in spinner (drop-down)
        File f_gpkg_bundles_root_dir = null;
        try {
            f_gpkg_bundles_root_dir = GPKG.Local.F_GPKG_BUNDLE_ROOT_DIR.getInstance(getApplicationContext());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }
        File[] f_gpkg_bundles_root_dir_files = f_gpkg_bundles_root_dir.listFiles();

        //2.1 remove current entries from m_spinner_gpkg_bundle_sel dataAdapter
        Log.d(TAG, "synchronize_spinner_gpkg_bundle: clearing spinner items");
        m_spinner_val_gpkg_bundle__items.clear();

        if (f_gpkg_bundles_root_dir_files.length > 0) {//found gpkg bundles
            //2.2 add found geopackage bundle names into m_spinner_gpkg_bundle_sel dataAdapter
            for (int i = 0; i < f_gpkg_bundles_root_dir_files.length; i++) {
                File f_gpkg_bundle_candidate = f_gpkg_bundles_root_dir_files[i];
                final String name = f_gpkg_bundle_candidate.getName();
                if (f_gpkg_bundle_candidate.isDirectory()) {
                    Log.d(TAG, "synchronize_spinner_gpkg_bundle: found geopackage-bundle \"" + name + "\" - adding it to spinner items");
                    m_spinner_val_gpkg_bundle__items.add(name);
                } else {
                    Log.d(TAG, "synchronize_spinner_gpkg_bundle: found errant file \"" + name + "\" in root geopackage-bundle directory - note that there should be no errant files here");
                }
            }
        } else {//no geopacklage bundles found
            //2.2 add "not found" item @ position 0
            String s_no_geopackage_bundles_installed = getString(R.string.srvr_provider_type__gpkg__no_geopackage_bundles_installed);
            Log.d(TAG, "synchronize_spinner_gpkg_bundle: no geopackage bundles installed! adding \"" + s_no_geopackage_bundles_installed + "\" to spinner items");
            m_spinner_val_gpkg_bundle__items.add(s_no_geopackage_bundles_installed);
        }

        //3. reconcile ConfigSettings.STRING_CONFIG_SETTING.TM_PROVIDER__GPKG_BUNDLE__SELECTION setting with m_spinner_val_gpkg_bundle__items selection and update selection as necessary
        int i_sel_pos = m_spinner_val_gpkg_bundle__dataadapter.getPosition(SharedPrefsManager.STRING_SHARED_PREF.TM_PROVIDER__GPKG_BUNDLE__SELECTION.getValue());
        if (i_sel_pos != -1) {
            Log.d(TAG, "synchronize_spinner_gpkg_bundle: synchronizing shared pref setting " + SharedPrefsManager.STRING_SHARED_PREF.TM_PROVIDER__GPKG_BUNDLE__SELECTION.toString() + " current value \"" + SharedPrefsManager.STRING_SHARED_PREF.TM_PROVIDER__GPKG_BUNDLE__SELECTION.getValue() + "\" spinner item selection to existing item position " + i_sel_pos);
        } else {
            //note that we must reset i_sel_pos to 0 here since it will be assigned -1 if we are here
            i_sel_pos = 0;
            Log.d(TAG,
                    "synchronize_spinner_gpkg_bundle: cannot synchronize shared prefs setting " + SharedPrefsManager.STRING_SHARED_PREF.TM_PROVIDER__GPKG_BUNDLE__SELECTION.toString() + " current value \"" + SharedPrefsManager.STRING_SHARED_PREF.TM_PROVIDER__GPKG_BUNDLE__SELECTION.getValue()
                            + "\" to spinner item selection since spinner does not currently have a selectable item with that value; setting spinner selected item position to " + i_sel_pos + " for value \"" + m_spinner_val_gpkg_bundle__items.get(i_sel_pos) + "\"");
        }

        //4. commit changes to spinner to allow for listener to react
        m_spinner_gpkg_bundle_sel.setSelection(i_sel_pos);
        m_spinner_val_gpkg_bundle__dataadapter.notifyDataSetChanged();
    }

    private void synchronize_spinner_gpkg_bundle_props() {
        //1. enumerate geopackage-bundle config files and display results in spinner (drop-down)
        File f_gpkg_bundle_dir = null;
        try {
            f_gpkg_bundle_dir = new File(GPKG.Local.F_GPKG_BUNDLE_ROOT_DIR.getInstance(getApplicationContext()).getPath(), SharedPrefsManager.STRING_SHARED_PREF.TM_PROVIDER__GPKG_BUNDLE__SELECTION.getValue());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!f_gpkg_bundle_dir.exists()) {
            Log.d(TAG, "synchronize_spinner_gpkg_bundle_props: gpkg-bundle does not exist - exiting");
            return;
        }
        File[] f_gpkg_bundle_props_files = f_gpkg_bundle_dir.listFiles(
            (dir, name) -> name.endsWith(".properties")
        );

        //2.1 remove current entries from synchronize_spinner_gpkg_bundle_props dataAdapter
        Log.d(TAG, "synchronize_spinner_gpkg_bundle_props: clearing spinner items");
        m_spinner_val_gpkg_bundle_props__items.clear();

        if (f_gpkg_bundle_props_files.length > 0) {//found props files
            //2.2 add found geopackage bundle config file names into synchronize_spinner_gpkg_bundle_props dataAdapter
            for (int i = 0; i < f_gpkg_bundle_props_files.length; i++) {
                File f_gpkg_bundle_config_candidate = f_gpkg_bundle_props_files[i];
                final String name = f_gpkg_bundle_config_candidate.getName();
                if (!f_gpkg_bundle_config_candidate.isDirectory()) {
                    Log.d(TAG, "synchronize_spinner_gpkg_bundle_props: found geopackage-bundle config file \"" + name + "\" - adding it to spinner items");
                    m_spinner_val_gpkg_bundle_props__items.add(name);
                } else {
                    Log.d(TAG, "synchronize_spinner_gpkg_bundle_props: \"" + name + "\" is a directory");
                }
            }
        } else {//no geopacklage bundle config files found
            //2.2 add "not found" item @ position 0
            String s_no_geopackage_bundle_props_files_installed = getString(R.string.srvr_provider_type__gpkg__no_geopackage_bundle_props_installed);
            Log.d(TAG, "synchronize_spinner_gpkg_bundle_props: no geopackage bundle configs installed! adding \"" + s_no_geopackage_bundle_props_files_installed + "\" to spinner items");
            m_spinner_val_gpkg_bundle_props__items.add(s_no_geopackage_bundle_props_files_installed);
        }

        //3. reconcile ConfigSettings.STRING_CONFIG_SETTING.TM_PROVIDER__GPKG_BUNDLE_PROPS__SELECTION setting with m_spinner_val_gpkg_bundle_props__items selection and update selection as necessary
        int i_sel_pos = m_spinner_val_gpkg_bundle_props__dataadapter.getPosition(SharedPrefsManager.STRING_SHARED_PREF.TM_PROVIDER__GPKG_BUNDLE_PROPS__SELECTION.getValue());
        if (i_sel_pos != -1) {
            Log.d(TAG, "synchronize_spinner_gpkg_bundle_props: synchronizing shared pref setting " + SharedPrefsManager.STRING_SHARED_PREF.TM_PROVIDER__GPKG_BUNDLE_PROPS__SELECTION.toString() + " current value \"" + SharedPrefsManager.STRING_SHARED_PREF.TM_PROVIDER__GPKG_BUNDLE_PROPS__SELECTION.getValue() + "\" spinner item selection to existing item position " + i_sel_pos);
        } else {
            //note that we must reset i_sel_pos to 0 here since it will be assigned -1 if we are here
            i_sel_pos = 0;
            Log.d(TAG,
                    "synchronize_spinner_gpkg_bundle_props: cannot synchronize shared prefs setting " + SharedPrefsManager.STRING_SHARED_PREF.TM_PROVIDER__GPKG_BUNDLE_PROPS__SELECTION.toString() + " current value \"" + SharedPrefsManager.STRING_SHARED_PREF.TM_PROVIDER__GPKG_BUNDLE_PROPS__SELECTION.getValue()
                            + "\" to spinner item selection since spinner does not currently have a selectable item with that value; setting spinner selected item position to " + i_sel_pos + " for value \"" + m_spinner_val_gpkg_bundle__items.get(i_sel_pos) + "\"");
        }

        //4. commit changes to spinner to allow for listener to react
        m_spinner_gpkg_bundle_props_sel.setSelection(i_sel_pos);
        m_spinner_val_gpkg_bundle_props__dataadapter.notifyDataSetChanged();
    }

    private final ClickableSpan ClickableSpan____m_tv_lbl_config_type_sel__local__manage_files = new ClickableSpan() {
        @Override
        public void onClick(View widget) {
            startActivityForResult(new Intent(MainActivity.this, ManageGpkgBundlesActivity.class), REQUEST_CODES.REQUEST_CODE__MANAGE_GPKG_BUNDLES);
        }
    };

    private void edit_local_config_file(@NonNull final String config_filename) {
        File f_config_toml = new File(getFilesDir().getPath() + "/" + config_filename);
        if (f_config_toml.exists()) {
            try {
                Log.d(TAG, "edit_local_config_file: " + f_config_toml.getPath() + " exists; starting ConfigFileEditorActivity for result...");
                Intent intent_edit_config_toml = new Intent(getApplicationContext(), ConfigFileEditorActivity.class);
                intent_edit_config_toml.putExtra(Strings.EDITOR_INTENT_EXTRAS.FILENAME, config_filename);
                startActivityForResult(intent_edit_config_toml, REQUEST_CODES.REQUEST_CODE__EDIT_TOML_FILE);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to start config file viewer/editor!", Toast.LENGTH_LONG).show();
            }
        } else {
            String s_err = f_config_toml.getPath() + " does not exist! nothing to edit/view";
            Log.e(TAG, "edit_local_config_file: " + s_err);
            Toast.makeText(this, s_err, Toast.LENGTH_LONG).show();
        }
    }

    private void import_config_toml__from_sdcard() {
        try {
            Intent intent_get_file_content = new Intent(Intent.ACTION_GET_CONTENT);
            intent_get_file_content.setType("text/plain" /*first preferred mime-type*/);
            intent_get_file_content.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{FileUtils.MIME_TYPE_TEXT /*second preferred mime-type*/, "application/octet-stream" /*third preferred and catch-all mime-type*/});
            intent_get_file_content.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(Intent.createChooser(intent_get_file_content, "Select Tegola config TOML file"), REQUEST_CODES.REQUEST_CODE__SELECT_TOML_FILES_FOR_IMPORT__LOCAL_STORAGE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //user clicks button to initiate import config toml files from google drive
//    private final View.OnClickListener OnClickListener__m_btn_config_sel_local_import__googledrive = new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            import_config_toml__from_google_drive();
//        }
//    };
//    private void import_config_toml__from_google_drive() {
//        GoogleDriveFileDownloadManager.getInstance().validate_init_api_client(this, m_google_api_callbacks, m_google_api_callbacks);
//        if (GoogleDriveFileDownloadManager.getInstance().validate_connect_api_client(this)) {
//            Log.i(TAG, "import_config_toml__from_google_drive: calling select_and_download_files() for Filter Filters.contains(SearchableField.TITLE, \".toml\")...");
//            GoogleDriveFileDownloadManager.getInstance().select_and_download_files(this, Filters.contains(SearchableField.TITLE, ".toml"), REQUEST_CODES.REQUEST_CODE__SELECT_TOML_FILES_FOR_IMPORT__GOOGLEDRIVE);
//        } else {
//            Log.i(TAG, "import_config_toml__from_google_drive: GoogleApiClient was not connected -- flow control was transferred to appropriate rcvr_hndlr");
//        }
//    }

    private void toggle_expandable_view(final ExpandableRelativeLayout erl, boolean expand) {
        if (expand)
            erl.expand();
        else
            erl.collapse();

    }

    //MBGLFragment overrides
    @Override
    public void onFragmentInteraction(E_MBGL_FRAG_ACTION e_mbgl_frag_action) {
        switch (e_mbgl_frag_action) {
            case HIDE: {
                if (m_drawer_handle != null)
                    m_drawer_handle.closerDrawer();
                break;
            }
            default: {
                //no-op
            }
        }
    }



    //auxiliary UI helper functions...
    private void reconcile_expandable_section(@NonNull final ExpandableRelativeLayout expandable_section) {
        Button btn_toggle = null;
        switch (expandable_section.getId()) {
            case R.id.sect_content__mbgl_nfo: {
                btn_toggle = m_btn_sect__mbgl_nfo__expand;
                break;
            }
        }
        boolean currently_expanded = expandable_section.isExpanded();
        Drawable drawable_arrow = ContextCompat.getDrawable(this, currently_expanded ? android.R.drawable.arrow_up_float : android.R.drawable.arrow_down_float);
        int h = drawable_arrow.getIntrinsicHeight();
        int w = drawable_arrow.getIntrinsicWidth();
        drawable_arrow.setBounds(0, 0, w, h);
        btn_toggle.setCompoundDrawables(null, null, drawable_arrow, null);
    }

    private void m_scvw_main__scroll_max() {
        m_scvw_main.postDelayed(
            () -> m_scvw_main.fullScroll(View.FOCUS_DOWN),
            50
        );
    }

    private void synchronize_spinner_local_config() {
        //1. enumerate local config.toml files and display results in spinner (drop-down)
        File f_filesDir = getFilesDir();
        File[] config_toml_files = f_filesDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".toml"));

        //2.1 remove current entries from m_spinner_postgis_local_config_sel dataAdapter
        Log.d(TAG, "synchronize_spinner_local_config: clearing spinner items");
        m_spinner_val_local_config__items.clear();

        if (config_toml_files.length > 0) {//found local config.toml files
            //2.2 add found config.toml filenames into m_spinner_postgis_local_config_sel dataAdapter
            for (int i = 0; i < config_toml_files.length; i++) {
                final String config_toml_filename = config_toml_files[i].getName();
                Log.d(TAG, "synchronize_spinner_local_config: found local config '.toml' file: " + config_toml_filename + " - adding it to spinner items");
                //add this config.toml filename to spinner (drop-down) for local config file selection
                m_spinner_val_local_config__items.add(config_toml_filename);
            }
        } else {//no local config.toml files found
            //2.2 add "not found" item @ position 0
            String s_config_sel__local_val__no_config_files_found = getString(R.string.srvr_config_type__local__no_config_files_found);
            Log.d(TAG, "synchronize_spinner_local_config: no local config '.toml' files found! adding \"" + s_config_sel__local_val__no_config_files_found + "\" to spinner items");
            m_spinner_val_local_config__items.add(s_config_sel__local_val__no_config_files_found);
        }

        //3. reconcile ConfigSettings.STRING_CONFIG_SETTING.TM_POSTGIS_LOCAL_CONFIG_TOML__SELECTION setting with m_spinner_val_local_config__dataadapter items and update selection as necessary
        int i_sel_pos = m_spinner_val_local_config__dataadapter.getPosition(SharedPrefsManager.STRING_SHARED_PREF.TM_POSTGIS_LOCAL_CONFIG_TOML__SELECTION.getValue());
        if (i_sel_pos != -1) {
            Log.d(TAG, "synchronize_spinner_local_config: synchronizing shared pref setting " + SharedPrefsManager.STRING_SHARED_PREF.TM_POSTGIS_LOCAL_CONFIG_TOML__SELECTION.toString() + " current value \"" + SharedPrefsManager.STRING_SHARED_PREF.TM_POSTGIS_LOCAL_CONFIG_TOML__SELECTION.getValue() + "\" spinner item selection to existing item position " + i_sel_pos);
        } else {
            //note that we must reset i_sel_pos to 0 here since it will be assigned -1 if we are here
            i_sel_pos = 0;
            Log.d(TAG,
                    "synchronize_spinner_local_config: cannot synchronize shared prefs setting " + SharedPrefsManager.STRING_SHARED_PREF.TM_POSTGIS_LOCAL_CONFIG_TOML__SELECTION.toString() + " current value \"" + SharedPrefsManager.STRING_SHARED_PREF.TM_POSTGIS_LOCAL_CONFIG_TOML__SELECTION.getValue()
                    + "\" to spinner item selection since spinner does not currently have a selectable item with that value; setting spinner selected item position to " + i_sel_pos + " for value \"" + m_spinner_val_local_config__items.get(i_sel_pos) + "\"");
        }

        //4. commit changes to spinner to allow for listener to react
        m_spinner_postgis_local_config_sel.setSelection(i_sel_pos);
        m_spinner_val_local_config__dataadapter.notifyDataSetChanged();
    }

    private void synchronize_edittext_config_remote() {
        m_edt_postgis_remote_config_url.setText(SharedPrefsManager.STRING_SHARED_PREF.TM_POSTGIS_REMOTE_CONFIG_TOML__SELECTION.getValue());
        m_btn_postgis_remote_config_url__apply_changes.setEnabled(false);
        if (SharedPrefsManager.STRING_SHARED_PREF.TM_POSTGIS_REMOTE_CONFIG_TOML__SELECTION.getValue().isEmpty()) {
            m_btn_local_mvt_srvr_ctrl.setEnabled(false);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this, R.style.alert_dialog));
            alertDialogBuilder.setTitle(getString(R.string.srvr_config_type__remote__no_url_specified));
            alertDialogBuilder
                    .setMessage(getString(R.string.srvr_config_type__remote__no_url_specified__alert_msg))
                    .setCancelable(false)
                    .setPositiveButton(
                        getString(R.string.OK),
                        (dialog, id) -> dialog.dismiss()
                    );
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } else
            m_btn_local_mvt_srvr_ctrl.setEnabled(true);
    }

    private void validate__m_edt_val_config_sel__remote() {
        String s_old_config_sel__remote_val = SharedPrefsManager.STRING_SHARED_PREF.TM_POSTGIS_REMOTE_CONFIG_TOML__SELECTION.getValue();
        String s_config_sel__remote_val__proposted = m_edt_postgis_remote_config_url.getText().toString();
        Log.d(TAG, "validate__m_edt_val_config_sel__remote: shared pref setting " + SharedPrefsManager.STRING_SHARED_PREF.TM_POSTGIS_REMOTE_CONFIG_TOML__SELECTION.toString() + " current value is \"" + s_old_config_sel__remote_val + "\"");
        if (s_old_config_sel__remote_val.compareTo(s_config_sel__remote_val__proposted) == 0) {
            Log.d(TAG, "validate__m_edt_val_config_sel__remote: m_edt_postgis_remote_config_url value is no different than shared pref setting " + SharedPrefsManager.STRING_SHARED_PREF.TM_POSTGIS_REMOTE_CONFIG_TOML__SELECTION.toString() + " current value \"" + s_old_config_sel__remote_val + "\"");
            m_btn_postgis_remote_config_url__apply_changes.setEnabled(false);
        } else {
            Log.d(TAG, "validate__m_edt_val_config_sel__remote: m_edt_postgis_remote_config_url proposed value \"" + s_config_sel__remote_val__proposted + "\" differs from shared pref setting " + SharedPrefsManager.STRING_SHARED_PREF.TM_POSTGIS_REMOTE_CONFIG_TOML__SELECTION.toString() + " current value \"" + s_old_config_sel__remote_val + "\"");
            m_btn_postgis_remote_config_url__apply_changes.setEnabled(true);
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(m_edt_postgis_remote_config_url.getWindowToken(), 0);
        }
    }


    //result rcvr_hndlr for both SD-card and GoogleDrive Tegola config TOML file selection (and any other requests to be handled via startActivityForResult)...
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case REQUEST_CODES.REQUEST_CODE__SELECT_TOML_FILES_FOR_IMPORT__LOCAL_STORAGE: {
                switch (resultCode) {
                    case RESULT_OK: {
                        final Uri file_uri = data.getData();
                        if (file_uri != null) {
                            Log.d(TAG, "onActivityResult: requestCode: REQUEST_CODE__SELECT_TOML_FILES_FOR_IMPORT__LOCAL_STORAGE | resultCode: RESULT_OK -- selected local storage file uri \"" + file_uri + "\"; calling local__file__import() for this file uri...");
                            try {
                                final local__file__import__result result = local__file__import(file_uri);
                                final String s_result_msg = (result.succeeded ? "Successfully imported" : "Failed to import") + " local storage file \"" + result.src_name + "\"";
                                Log.d(TAG, "onActivityResult: requestCode: REQUEST_CODE__SELECT_TOML_FILES_FOR_IMPORT__LOCAL_STORAGE | resultCode: RESULT_OK -- " + s_result_msg);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), s_result_msg, Toast.LENGTH_LONG).show();
                                        if (result.succeeded)
                                            synchronize_spinner_local_config();
                                    }
                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Log.d(TAG, "onActivityResult: requestCode: REQUEST_CODE__SELECT_TOML_FILES_FOR_IMPORT__LOCAL_STORAGE | resultCode: RESULT_OK -- but selected local storage file uri is null; aborting import");
                        }
                        break;
                    }
                    case RESULT_CANCELED: {
                        Log.i(TAG, "onActivityResult: requestCode: REQUEST_CODE__SELECT_TOML_FILES_FOR_IMPORT__LOCAL_STORAGE | resultCode: RESULT_CANCELED");
                        super.onActivityResult(requestCode, resultCode, data);
                        break;
                    }
                    default: {
                        Log.d(TAG, "onActivityResult: requestCode " + requestCode + ", resultCode " + resultCode);
                        super.onActivityResult(requestCode, resultCode, data);
                    }
                }
                break;
            }
//            case REQUEST_CODES.REQUEST_CODE__SELECT_TOML_FILES_FOR_IMPORT__GOOGLEDRIVE: {
//                switch (resultCode) {
//                    case RESULT_OK: {
//                        m_google_drive_id = (DriveId)data.getParcelableExtra(OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
//                        Log.i(TAG, "onActivityResult: requestCode: REQUEST_CODE__SELECT_TOML_FILES_FOR_IMPORT__GOOGLEDRIVE | resultCode: RESULT_OK -- flow control rcvr_hndlr: selected Google Drive file id " + m_google_drive_id.getResourceId() + "; calling GoogleDriveFileDownloadManager.download_file_contents() for this file...");
//                        GoogleDriveFileDownloadManager.getInstance().download_file_contents(this, m_google_drive_id, DriveFile.MODE_READ_ONLY, new GoogleDriveFileDownloadManager.FileContentsHandler() {
//                            @Override
//                            public void onProgress(long bytesDownloaded, long bytesExpected) {
//                            }
//
//                            @Override
//                            public void OnFileContentsDownloaded(final DriveContents google_drive_file_contents, final Metadata google_drive_file_metadata, final DriveFile google_drive_file) {
//                                final String
//                                    s_gd_id = google_drive_file_contents.getDriveId().encodeToString()
//                                    , s_gd_filename = google_drive_file_metadata.getOriginalFilename();
//                                Log.i(TAG, "inline GoogleDriveDownloadedFileContentsHandler.OnFileContentsDownloaded: triggered from PendingResult from call to google_drive__download_file_contents() -- successfully downloaded google drive file \"" + s_gd_filename + "\" contents from: id " + s_gd_id);
//                                try {
//                                    Log.i(TAG, "inline GoogleDriveDownloadedFileContentsHandler.OnFileContentsDownloaded: importing contents from google drive file \"" + s_gd_filename + " (id \"" + s_gd_id + ")\"");
//                                    final boolean succeeded = GoogleDriveFileDownloadManager.getInstance().import_file(MainActivity.this, google_drive_file_contents, google_drive_file_metadata, google_drive_file);
//                                    final String s_result_msg = (succeeded ? "Successfully imported" : "Failed to import") + " google drive file \"" + s_gd_filename + "\" (id " + s_gd_id + ")";
//                                    Log.i(TAG, "inline GoogleDriveDownloadedFileContentsHandler.OnFileContentsDownloaded: " + s_result_msg);
//                                    runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            Toast.makeText(getApplicationContext(), s_result_msg, Toast.LENGTH_SHORT).show();
//                                            if (succeeded)
//                                                synchronize_spinner_local_config();
//                                        }
//                                    });
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        });
//                        break;
//                    }
//                    case RESULT_CANCELED: {
//                        Log.i(TAG, "onActivityResult: requestCode: REQUEST_CODE__SELECT_TOML_FILES_FOR_IMPORT__GOOGLEDRIVE | resultCode: RESULT_CANCELED -- flow control rcvr_hndlr: user canceled -- normal flow termination");
//                        super.onActivityResult(requestCode, resultCode, data);
//                        break;
//                    }
//                    default: {
//                        Log.d(TAG, "onActivityResult: default case: requestCode " + requestCode + ", resultCode " + resultCode);
//                        super.onActivityResult(requestCode, resultCode, data);
//                        break;
//                    }
//                }
//                break;
//            }
//            case REQUEST_CODES.REQUEST_CODE__GOOGLEAPICLIENT__RESOLVE_CONNECTION_FAILURE: {
//                switch (resultCode) {
//                    case RESULT_OK: {
//                        Log.i(TAG, "onActivityResult: requestCode: REQUEST_CODE__GOOGLEAPICLIENT__RESOLVE_CONNECTION_FAILURE | resultCode: RESULT_OK -- flow control rcvr_hndlr: validating GoogleApiClient connection...");
//                        GoogleDriveFileDownloadManager.getInstance().validate_connect_api_client(this);
//                        break;
//                    }
//                    case RESULT_CANCELED: {
//                        Log.i(TAG, "onActivityResult: requestCode: REQUEST_CODE__GOOGLEAPICLIENT__RESOLVE_CONNECTION_FAILURE | resultCode: RESULT_CANCELED -- flow control rcvr_hndlr: abnormal flow termination :(");
//                        super.onActivityResult(requestCode, resultCode, data);
//                        break;
//                    }
//                    default: {
//                        Log.d(TAG, "onActivityResult: default case: requestCode " + requestCode + ", resultCode " + resultCode);
//                        super.onActivityResult(requestCode, resultCode, data);
//                        break;
//                    }
//                }
//                break;
//            }
//            case REQUEST_CODES.REQUEST_CODE__EDIT_TOML_FILE: {
//                switch (resultCode) {
//                    case RESULT_OK: {
//                        break;
//                    }
//                    case RESULT_CANCELED: {
//                        break;
//                    }
//                }
//                break;
//            }
            case REQUEST_CODES.REQUEST_CODE__MANAGE_GPKG_BUNDLES: {
                switch (resultCode) {
                    case ManageGpkgBundlesActivity.MNG_GPKG_BUNDLES_RESULT__CHANGED: {
                        Log.i(TAG, "onActivityResult: requestCode: REQUEST_CODE__MANAGE_GPKG_BUNDLES | resultCode: MNG_GPKG_BUNDLES_RESULT__CHANGED");
                        synchronize_spinner_gpkg_bundle();
                        synchronize_spinner_gpkg_bundle_props();
                        break;
                    }
                    case ManageGpkgBundlesActivity.MNG_GPKG_BUNDLES_RESULT__UNCHANGED: {
                        Log.i(TAG, "onActivityResult: requestCode: REQUEST_CODE__MANAGE_GPKG_BUNDLES | resultCode: MNG_GPKG_BUNDLES_RESULT__UNCHANGED");
                        super.onActivityResult(requestCode, resultCode, data);
                        break;
                    }
                    default: {
                        Log.d(TAG, "onActivityResult: default case: requestCode " + requestCode + ", resultCode " + resultCode);
                        super.onActivityResult(requestCode, resultCode, data);
                        break;
                    }
                }
                break;
            }
            case REQUEST_CODES.REQUEST_CODE__INSTALL_GPKG_BUNDLE: {
                switch (resultCode) {
                    case InstallGpkgBundleActivity.INSTALL_GPKG_BUNDLE_RESULT__SUCCESSFUL: {
                        Log.i(TAG, "onActivityResult: requestCode: REQUEST_CODE__INSTALL_GPKG_BUNDLE | resultCode: INSTALL_GPKG_BUNDLE_RESULT__SUCCESSFUL");
                        synchronize_spinner_gpkg_bundle();
                        synchronize_spinner_gpkg_bundle_props();
                        break;
                    }
                    case InstallGpkgBundleActivity.INSTALL_GPKG_BUNDLE_RESULT__CANCELLED: {
                        Log.i(TAG, "onActivityResult: requestCode: REQUEST_CODE__INSTALL_GPKG_BUNDLE | resultCode: INSTALL_GPKG_BUNDLE_RESULT__CANCELLED");
                        super.onActivityResult(requestCode, resultCode, data);
                        break;
                    }
                    case InstallGpkgBundleActivity.INSTALL_GPKG_BUNDLE_RESULT__FAILED: {
                        Log.i(TAG, "onActivityResult: requestCode: REQUEST_CODE__INSTALL_GPKG_BUNDLE | resultCode: INSTALL_GPKG_BUNDLE_RESULT__FAILED");
                        super.onActivityResult(requestCode, resultCode, data);
                        break;
                    }
                    default: {
                        Log.d(TAG, "onActivityResult: default case: requestCode " + requestCode + ", resultCode " + resultCode);
                        super.onActivityResult(requestCode, resultCode, data);
                        break;
                    }
                }
                break;
            }
            default: {
                Log.d(TAG, "onActivityResult: default case: requestCode " + requestCode + ", resultCode " + resultCode);
                super.onActivityResult(requestCode, resultCode, data);
                break;
            }
        }
    }

    //supporting functions for above result rcvr_hndlr
    private class local__file__import__result {
        public String src_name = "";
        public String src_path = "";
        public boolean succeeded = false;
    }
    private local__file__import__result local__file__import(final Uri local_file_uri) throws IOException {
        local__file__import__result result = new local__file__import__result();
        result.src_path = local_file_uri.getPath();
        InputStream inputstream_config_toml = null;
        boolean uselocalstorageprovider = getResources().getBoolean(R.bool.use_provider);
        if (uselocalstorageprovider) {
            Log.d(TAG, "local__file__import: using storage access framework since API level (" + Build.VERSION.SDK_INT + ") of device >= 19");
            Cursor cursor = this.getContentResolver().query(local_file_uri, null, null, null, null, null);
            try {
                if (cursor == null) {
                    Log.d(TAG, "local__file__import: getContentResolver().query() returned null cursor for uri " + local_file_uri.toString());
                } else {
                    // moveToFirst() returns false if the cursor has 0 rows.  Very handy for
                    // "if there's anything to look at, look at it" conditionals.
                    if (cursor.moveToFirst()) {
                        // Note it's called "Display Name".  This is
                        // provider-specific, and might not necessarily be the file name.
                        result.src_name = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                        Log.d(TAG, "local__file__import: cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME) returns display (file) name: " + result.src_name);
                    } else {
                        Log.d(TAG, "local__file__import: cursor.moveToFirst() failed for uri " + local_file_uri.toString());
                    }
                }
            } finally {
                if (cursor != null)
                    cursor.close();
            }
            if (result.src_name.isEmpty()) {
                Log.d(TAG, "local__file__import: result.src_name is empty; parsing result.src_name manually from uri path " + result.src_path);
                int i = result.src_path.lastIndexOf("/");
                if (i == -1)
                    i = 0;
                else
                    i += 1;
                result.src_name = result.src_path.substring(i);
            }
            Log.d(TAG, "local__file__import: using storage access framework content resolver to open inputstream from " + result.src_path + "...");
            inputstream_config_toml = getContentResolver().openInputStream(local_file_uri);
        } else {
            Log.d(TAG, "local__file__import: not using storage access framework since API level (" + Build.VERSION.SDK_INT + ") of device < 19");
            File f_local = new File(result.src_path);
            result.src_name = f_local.getName();
            Log.d(TAG, "local__file__import: opening inputstream from " + result.src_path + "...");
            inputstream_config_toml = new FileInputStream(f_local);
        }
        if (inputstream_config_toml == null )
            throw new IOException("Failed to open inputstream to " + result.src_path);
        final int file_size_in_bytes = inputstream_config_toml.available();
        byte[] buf_raw_config_toml = new byte[file_size_in_bytes];
        Log.d(TAG, "local__file__import: input file size is " + file_size_in_bytes + " bytes; reading...");
        inputstream_config_toml.read(buf_raw_config_toml);
        inputstream_config_toml.close();
        Log.d(TAG, "local__file__import: writing " + file_size_in_bytes + " bytes to new file (result.src_name \"" + result.src_name + "\") in app files directory...");
        FileOutputStream f_outputstream_new_tegola_config_toml = openFileOutput(result.src_name, Context.MODE_PRIVATE);
        f_outputstream_new_tegola_config_toml.write(buf_raw_config_toml);
        f_outputstream_new_tegola_config_toml.close();
        File f_new_tegola_config_toml = new File(getFilesDir().getPath() + "/" + result.src_name);
        result.succeeded = f_new_tegola_config_toml.exists();
        Log.d(TAG, "local__file__import: all done - " + (result.succeeded ? "successfully copied" : "failed to copy") + " " + result.src_name + " to app files directory");
        return result;
    }




    //ControllerLib-related stuff
    @Override
    public void OnControllerStarting() {
//        m_tv_val_ctrlr_status.setText(getString(R.string.starting));
    }
    @Override
    public void OnControllerRunning() {
        m_controller_running = true;
        Log.d(TAG, "OnControllerRunning: set m_controller_running == " + m_controller_running);
        try {
            m_tv_val_bin_ver.setText(TEGOLA_BIN.getInstance(getApplicationContext()).get_version_string());
        } catch (PackageManager.NameNotFoundException e) {
            //e.printStackTrace();
        } catch (IOException e) {
            //e.printStackTrace();
        } catch (Exceptions.TegolaBinaryNotExecutableException e) {
            //e.printStackTrace();
        } catch (Exceptions.UnsupportedCPUABIException e) {
            //e.printStackTrace();
        }
    }

    @Override
    public void OnControllerStopping() {
//        m_tv_val_ctrlr_status.setText(getString(R.string.stopping));
    }

    @Override
    public void OnControllerStopped() {
//        m_tv_val_ctrlr_status.setText(getString(R.string.stopped));
    }

    @Override
    public void OnMVTServerStarting() {
        m_tv_val_local_mvt_srvr_status.setText(getString(R.string.starting));
        m_sect_content__item__srvr_console_output.setVisibility(View.VISIBLE);
    }

    @Override
    public void OnMVTServerStartFailed(final String reason) {
        OnMVTServerStopped();
    }

    private void textview_setColorizedText(TextView view, String fulltext, String subtext, int color) {
        view.setText(fulltext, TextView.BufferType.SPANNABLE);
        Spannable str = (Spannable)view.getText();
        int i = fulltext.indexOf(subtext);
        str.setSpan(new ForegroundColorSpan(color), i, i + subtext.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    @Override
    public void OnMVTServerRunning(final int pid) {
        final StringBuilder sb_srvr_status = new StringBuilder();
        sb_srvr_status.append(getString(R.string.running));
        if (pid != -1)
            sb_srvr_status.append(" (pid " + pid + ")");
        textview_setColorizedText(m_tv_val_local_mvt_srvr_status, sb_srvr_status.toString(), getString(R.string.running), Color.GREEN);
        m_btn_local_mvt_srvr_ctrl.setTag(R.id.TAG__SRVR_RUNNING, true);
        m_btn_local_mvt_srvr_ctrl.setText(getString(R.string.close_tile_stream));
        //now disable edit-config button
        m_btn_config_sel_local__edit_file.setEnabled(false);
        m_sect_content__item__srvr_console_output.setVisibility(View.VISIBLE);
        m_tv_tegola_console_output.setText("");
    }

    //process stream-output (STDOUT/STDERR) and logcat-output helper functions associated w/ server-started state
    @Override
    public void OnMVTServerOutputLogcat(final String logcat_line) {
        sv_append_mvt_server_console_output("LOGCAT", logcat_line);
    }
    @Override
    public void OnMVTServerOutputStdErr(final String stderr_line) {
        sv_append_mvt_server_console_output("STDERR", stderr_line);
    }
    @Override
    public void OnMVTServerOutputStdOut(final String stdout_line) {
        sv_append_mvt_server_console_output("STDOUT", stdout_line);
    }
    private void m_tv_tegola_console_output__scroll_max() {
        m_tv_tegola_console_output.postDelayed(
            () -> {
                if (m_tv_tegola_console_output != null && m_tv_tegola_console_output.getLayout() != null) {
                    final int scrollAmount = m_tv_tegola_console_output.getLayout().getLineTop(m_tv_tegola_console_output.getLineCount()) - m_tv_tegola_console_output.getHeight();
                    // if there is no need to scroll, scrollAmount will be <=0
                    if (scrollAmount > 0)
                        m_tv_tegola_console_output.scrollTo(0, scrollAmount);
                    else
                        m_tv_tegola_console_output.scrollTo(0, 0);
                }
            },
            50
        );
    }
    private void sv_append_mvt_server_console_output(final String source, final String s) {
        if (s == null || s.trim().isEmpty())
            return;
        int color = Color.YELLOW;
        String s_src_trimmed = "UNSPECIFIED";
        if (source != null && !source.isEmpty()) {
            s_src_trimmed = source.trim();
            switch (source) {
                case "STDOUT":
                    color = Color.GREEN;
                    break;
                case "STDERR":
                    color = Color.RED;
                    break;
                case "LOGCAT":
                    color = Color.CYAN;
                    break;
            }
        }
        StringBuilder sb_line = new StringBuilder();
        sb_line.append(s_src_trimmed + "> ");
        sb_line.append(s);
        sb_line.append("\n");
        SpannableString ss_line = new SpannableString(sb_line.toString());
        ss_line.setSpan(new ForegroundColorSpan(color), 0, s_src_trimmed.length() + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        m_tv_tegola_console_output.append(ss_line);
        m_tv_tegola_console_output__scroll_max();
        m_scvw_main__scroll_max();
    }

    @Override
    public void OnMVTServerListening(final int port) {
        Boolean srvr_started = (Boolean) m_btn_local_mvt_srvr_ctrl.getTag(R.id.TAG__SRVR_RUNNING);
        if (srvr_started != null && srvr_started == true) {
            String s_srvr_status = m_tv_val_local_mvt_srvr_status.getText().toString() + "\n\t\tlistening on port " + port;
            textview_setColorizedText(m_tv_val_local_mvt_srvr_status, s_srvr_status, getString(R.string.running), Color.GREEN);
            mvstate = MAPVIEW_STATE.OPENING_STREAM__LOCAL;
            m_controllerClient.mvt_server__get_capabilities(
                Constants.Strings.INTENT.ACTION.REQUEST.MVT_SERVER.REST_API.GET_JSON.EXTRA_KEY.PURPOSE.VALUE.LOAD_MAP.STRING
            );
        }
    }

    private TegolaCapabilities parse_tegola_capabilities_json(final String s_tegola_tile_server_url__root, final String json) throws JSONException {
        final TegolaCapabilities tegolaCapabilities = new TegolaCapabilities();

        JSONTokener jsonTokener = new JSONTokener(json);
        tegolaCapabilities.root_json_object = new JSONObject(jsonTokener);
        Log.d(TAG, "parse_tegola_capabilities_json: json content is content is:\n" + tegolaCapabilities.root_json_object.toString());
        tegolaCapabilities.version = tegolaCapabilities.root_json_object.getString("version");
        if (tegolaCapabilities.version != null) {
            Log.d(TAG, "parse_tegola_capabilities_json: got \"version\" == \"" + tegolaCapabilities.version + "\"");
        } else
            tegolaCapabilities.version = "";
        JSONArray json_maps = tegolaCapabilities.root_json_object.getJSONArray("maps");
        if (json_maps != null) {
            ArrayList<TegolaCapabilities.Parsed.Map> al_maps = null;
            Log.d(TAG, "parse_tegola_capabilities_json: got \"maps\" JSONArray - contains " + json_maps.length() + " \"map\" JSON objects");
            if (json_maps.length() > 0) {
                al_maps = new ArrayList<TegolaCapabilities.Parsed.Map>();
                for (int i = 0; i < json_maps.length(); i++) {
                    JSONObject json_map = json_maps.getJSONObject(i);
                    if (json_map != null) {
                        Log.d(TAG, "parse_tegola_capabilities_json: got JSONObject for \"maps\"[" + i + "]");
                        TegolaCapabilities.Parsed.Map map = new TegolaCapabilities.Parsed.Map();
                        map.name = json_map.getString("name");
                        Log.d(TAG, "parse_tegola_capabilities_json: \"maps\"[" + i + "].\"name\" == \"" + map.name + "\"");
                        map.attribution = json_map.getString("attribution");
                        Log.d(TAG, "parse_tegola_capabilities_json: \"maps\"[" + i + "].\"attribution\" == \"" + map.attribution + "\"");
                        map.mbgl_style_json_url = s_tegola_tile_server_url__root + "/maps/" + map.name + "/style.json";
                        Log.d(TAG, "parse_tegola_capabilities_json: mbgl_style url for map \"" + map.name + "\" is " + map.mbgl_style_json_url);
                        JSONArray jsonarray_map_center = json_map.getJSONArray("center");
                        if (jsonarray_map_center != null) {
                            Log.d(TAG, "parse_tegola_capabilities_json: got \"center\" JSONArray - contains " + jsonarray_map_center.length() + " values");
                            if (jsonarray_map_center.length() == 3) {
                                map.center.latitude = jsonarray_map_center.getDouble(1);
                                Log.d(TAG, "parse_tegola_capabilities_json: got \"center\" latitude (pos 1) value: " + map.center.latitude);
                                map.center.longitude = jsonarray_map_center.getDouble(0);
                                Log.d(TAG, "parse_tegola_capabilities_json: got \"center\" longitude (pos 0) value: " + map.center.longitude);
                                map.center.zoom = jsonarray_map_center.getDouble(2);
                                Log.d(TAG, "parse_tegola_capabilities_json: got \"center\" zoom (pos 2) value: " + map.center.zoom);
                            } else {
                                Log.e(TAG, "parse_tegola_capabilities_json: \"center\" JSONArray contains " + jsonarray_map_center.length() + " values but 3 are required!");
                            }
                        } else {
                            Log.w(TAG, "parse_tegola_capabilities_json: tegola capabilities json map \"" + map.name + "\" does not contain \"center\" json object");
                        }
                        JSONArray jsonarray_map_layers = json_map.getJSONArray("layers");
                        if (jsonarray_map_layers != null) {
                            Log.d(TAG, "parse_tegola_capabilities_json: got \"layers\" JSONArray - contains " + jsonarray_map_layers.length() + " objects");
                            if (jsonarray_map_layers.length() > 0) {
                                ArrayList<TegolaCapabilities.Parsed.Map.Layer> al_layers = new ArrayList<TegolaCapabilities.Parsed.Map.Layer>();
                                Log.d(TAG, "parse_tegola_capabilities_json: collating layers objects for inf(minzoom) and sup(maxzoom)...");
                                for (int j = 0; j < jsonarray_map_layers.length(); j++) {
                                    JSONObject json_layer = jsonarray_map_layers.getJSONObject(j);
                                    if (json_layer != null) {
                                        Log.d(TAG, "parse_tegola_capabilities_json: got JSONObject for \"layers\"[" + j + "]");
                                        TegolaCapabilities.Parsed.Map.Layer layer = new TegolaCapabilities.Parsed.Map.Layer();
                                        layer.name = json_layer.getString("name");
                                        Log.d(TAG, "parse_tegola_capabilities_json: \"layers\"[" + j + "].\"name\" == \"" + layer.name + "\"");
                                        layer.minzoom = json_layer.getDouble("minzoom");
                                        Log.d(TAG, "parse_tegola_capabilities_json: \"layers\"[" + j + "].\"minzoom\" == \"" + layer.minzoom + "\"");
                                        if (tegolaCapabilities.parsed.maps_layers_minzoom == -1.0 || layer.minzoom < tegolaCapabilities.parsed.maps_layers_minzoom) {
                                            Log.d(TAG, "parse_tegola_capabilities_json: found new minzoom == " + layer.minzoom);
                                            tegolaCapabilities.parsed.maps_layers_minzoom = layer.minzoom;
                                        }
                                        layer.maxzoom = json_layer.getDouble("maxzoom");
                                        Log.d(TAG, "parse_tegola_capabilities_json: \"layers\"[" + j + "].\"maxzoom\" == \"" + layer.maxzoom + "\"");
                                        if (tegolaCapabilities.parsed.maps_layers_maxzoom == -1.0 || layer.maxzoom > tegolaCapabilities.parsed.maps_layers_maxzoom) {
                                            Log.d(TAG, "parse_tegola_capabilities_json: found new maxzoom == " + layer.maxzoom);
                                            tegolaCapabilities.parsed.maps_layers_maxzoom = layer.maxzoom;
                                        }
                                        al_layers.add(layer);
                                    }
                                }
                                map.layers = al_maps.toArray(new TegolaCapabilities.Parsed.Map.Layer[al_maps.size()]);
                            }
                        }
                        al_maps.add(map);
                    } else {
                        Log.e(TAG, "parse_tegola_capabilities_json: tegola capabilities json does not have a map json object at index " + i + " of \"maps\" json array");
                    }
                }
            } else {
                Log.e(TAG, "parse_tegola_capabilities_json: tegola capabilities json \"maps\" json array does not contain any elements!");
            }
            tegolaCapabilities.parsed.maps = al_maps.toArray(new TegolaCapabilities.Parsed.Map[al_maps.size()]);
            Log.d(TAG, "parse_tegola_capabilities_json: post-parse: tegolaCapabilities.parsed.maps contains " + tegolaCapabilities.parsed.maps.length + " elements");
            Log.d(TAG, "parse_tegola_capabilities_json: post-parse: tegolaCapabilities.parsed.maps_layers_minzoom == " + tegolaCapabilities.parsed.maps_layers_minzoom);
            Log.d(TAG, "parse_tegola_capabilities_json: post-parse: tegolaCapabilities.parsed.maps_layers_maxzoom == " + tegolaCapabilities.parsed.maps_layers_maxzoom);
        } else {
            Log.e(TAG, "parse_tegola_capabilities_json: tegola capabilities json does not contain \"maps\" json array!");
        }
        return tegolaCapabilities;
    }

    private void mbgl_map_start(@NonNull final TegolaCapabilities tegolaCapabilities) throws MapboxConfigurationException {
        if (tegolaCapabilities != null) {
            mbgl_map_stop();
            runOnUiThread(
                () -> {
                    Log.d(TAG, "mbgl_map_start: swapping drawer content to MBGLFragment");
                    MBGLFragment mbgl_frag = MBGLFragment.newInstance(tegolaCapabilities, BuildConfig.mbgl_debug_active);
                    LocationUpdatesManager.newInstance(MainActivity.this, mbgl_frag);
                    getSupportFragmentManager()
                        .beginTransaction()
                        .replace(
                            R.id.drawerlayout_content__drawer__frag_container,
                            mbgl_frag,
                            FRAG_DRAWER_CONTENT
                        )
                        .commit();
                    Log.d(TAG, "mbgl_map_start: adding drawerlistener m_drawerlayout_main__DrawerToggle to m_drawerlayout");
                    m_drawerlayout.addDrawerListener(m_drawerlayout_main__DrawerToggle);
                    Log.d(TAG, "mbgl_map_start: attaching drawerhandle R.layout.drawer_handle to m_drawerlayout_content__drawer");
                    m_drawer_handle = DrawerHandle.attach(m_drawerlayout_content__drawer, R.layout.drawer_handle, 0.95f);
                    Log.d(TAG, "mbgl_map_start: unlocking drawer");
                    m_drawerlayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                    m_drawer_handle.openDrawer();
                }
            );
            mvstate = (mvstate == MAPVIEW_STATE.OPENING_STREAM__REMOTE ? MAPVIEW_STATE.STREAMING__REMOTE__DRAWER_OPEN : MAPVIEW_STATE.STREAMING__LOCAL__DRAWER_OPEN);
        } else {
            throw new MapboxConfigurationException();
        }
    }

    @Override
    public void OnMVTServerJSONRead(final String s_tegola_url_root, final String json_url_endpoint, final String json, final String purpose) {
        Log.d(TAG, "OnMVTServerJSONRead: s_tegola_url_root: " + s_tegola_url_root + "; json_url_endpoint: " + json_url_endpoint + "; purpose: " + purpose);
        switch (json_url_endpoint) {
            case "/capabilities": {
                try {
                    final TegolaCapabilities tegolaCapabilities = parse_tegola_capabilities_json(s_tegola_url_root, json);
                    switch (purpose) {
                        case Constants.Strings.INTENT.ACTION.REQUEST.MVT_SERVER.REST_API.GET_JSON.EXTRA_KEY.PURPOSE.VALUE.LOAD_MAP.STRING: {
                            if (tegolaCapabilities.parsed.maps.length > 0) {
                                mbgl_map_start(tegolaCapabilities);
                                if (!s_tegola_url_root.contains("localhost"))
                                    m_btn_stream_tiles_from_remote.setText(getString(R.string.close_tile_stream));
                            }
                            break;
                        }
                    }
                } catch (JSONException e) {
                    OnMVTServerJSONReadFailed(s_tegola_url_root, json_url_endpoint, purpose, e.getMessage());
                }
                break;
            }
            default: {
                break;
            }
        }
    }

    @Override
    public void OnMVTServerJSONReadFailed(final String s_tegola_url_root, final String json_url_endpoint, final String purpose, final String s_reason) {
        runOnUiThread(
            () -> {
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                StringBuilder sb_alert_msg = new StringBuilder();
                switch (purpose) {
                    case Constants.Strings.INTENT.ACTION.REQUEST.MVT_SERVER.REST_API.GET_JSON.EXTRA_KEY.PURPOSE.VALUE.LOAD_MAP.STRING: {
                        alertDialog.setTitle("Failed loading maps!");
                        sb_alert_msg.append("Could not parse/read mbgl style json from " + s_tegola_url_root + json_url_endpoint);
                        break;
                    }
                    default: {
                        alertDialog.setTitle("Failed loading JSON!");
                        sb_alert_msg.append("Could not read json from " + s_tegola_url_root + json_url_endpoint);
                        break;
                    }
                }
                sb_alert_msg.append("\n\n\nError: " + s_reason);
                alertDialog.setMessage(sb_alert_msg.toString());
                alertDialog.setButton(
                    AlertDialog.BUTTON_NEUTRAL,
                    "OK",
                    (dialog, which) -> dialog.dismiss()
                );
                alertDialog.show();
            }
        );
    }

    @Override
    public void OnMVTServerStopping() {
        textview_setColorizedText(m_tv_val_local_mvt_srvr_status, getString(R.string.stopping), getString(R.string.stopping), Color.YELLOW);
    }

    private void mbgl_map_stop() {
        runOnUiThread(
            () -> {
                Log.d(TAG, "mbgl_map_stop: locking drawer closed");
                if (m_drawer_handle != null) {
                    m_drawer_handle.closerDrawer();
                    Log.d(TAG, "mbgl_map_stop: detaching drawer handle");
                    m_drawer_handle.detach();
                    m_drawer_handle = null;
                    mvstate = MAPVIEW_STATE.STREAM_CLOSED;
                } else {
                    Log.d(TAG, "mbgl_map_stop: no (null) drawerhandle to detach!");
                }
                m_drawerlayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                Log.d(TAG, "mbgl_map_stop: removing drawer listener: m_drawerlayout_main__DrawerToggle");
                m_drawerlayout.removeDrawerListener(m_drawerlayout_main__DrawerToggle);
                Fragment frag_current = getSupportFragmentManager().findFragmentByTag(FRAG_DRAWER_CONTENT);
                if (frag_current != null) {
                    Log.d(TAG, "mbgl_map_stop: removing MapFragment from drawer");
                    getSupportFragmentManager()
                        .beginTransaction()
                        .remove(frag_current)
                        .commit();
                }
            }
        );
    }

    @Override
    public void OnMVTServerStopped() {
        mbgl_map_stop();

        Log.d(TAG, "OnMVTServerStopped: updating status-related UX");
        textview_setColorizedText(m_tv_val_local_mvt_srvr_status, getString(R.string.stopped), getString(R.string.stopped), Color.RED);
        m_btn_local_mvt_srvr_ctrl.setTag(R.id.TAG__SRVR_RUNNING, false);
        m_btn_local_mvt_srvr_ctrl.setText(getString(R.string.open_tile_stream));
        m_btn_config_sel_local__edit_file.setEnabled(true);
        m_tv_tegola_console_output.setText("");
        m_sect_content__item__srvr_console_output.setVisibility(View.GONE);
    }

    private void start_mvt_server() {
        if (SharedPrefsManager.BOOLEAN_SHARED_PREF.TM_PROVIDER__IS_GEOPACKAGE.getValue() == true) {
            m_controllerClient.mvt_server__start(
                new FGS.MVT_SERVER_START_SPEC__GPKG_PROVIDER(
                    SharedPrefsManager.STRING_SHARED_PREF.TM_PROVIDER__GPKG_BUNDLE__SELECTION.getValue(),
                    SharedPrefsManager.STRING_SHARED_PREF.TM_PROVIDER__GPKG_BUNDLE_PROPS__SELECTION.getValue()
                )
            );
        } else {
            boolean remote_config = SharedPrefsManager.BOOLEAN_SHARED_PREF.TM_CONFIG_TOML__IS_REMOTE.getValue();
            String s_config_toml = "";
            if (!remote_config) {
                File
                        f_filesDir = getFilesDir()
                        , f_postgis_toml = new File(f_filesDir.getPath(), SharedPrefsManager.STRING_SHARED_PREF.TM_POSTGIS_LOCAL_CONFIG_TOML__SELECTION.getValue());
                s_config_toml = f_postgis_toml.getPath();
                if (!f_postgis_toml.exists()) {
                    Log.e(TAG, "mvt_server__start: failed to start mvt server for provider type postgis since toml file " + s_config_toml + " does not exist!");
                    return;
                }
            } else
                s_config_toml = SharedPrefsManager.STRING_SHARED_PREF.TM_POSTGIS_REMOTE_CONFIG_TOML__SELECTION.getValue();
            m_controllerClient.mvt_server__start(new FGS.MVT_SERVER_START_SPEC__POSTGIS_PROVIDER(remote_config, s_config_toml));
        }
    }

    private void stop_mvt_server() {
        mbgl_map_stop();
        m_controllerClient.mvt_server__stop();
    }
}