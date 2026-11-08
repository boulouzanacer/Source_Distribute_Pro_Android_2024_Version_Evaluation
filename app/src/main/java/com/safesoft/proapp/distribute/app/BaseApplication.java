package com.safesoft.proapp.distribute.app;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;

import com.rt.printerlibrary.printer.RTPrinter;
import com.safesoft.proapp.distribute.BuildConfig;
import com.safesoft.proapp.distribute.cloud.BackupScheduler;
import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.utils.BaseEnum;
import com.safesoft.proapp.distribute.utils.LocaleManager;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Tony on 2017/12/1.
 */

public class BaseApplication extends Application {

    public static BaseApplication instance = null;
    private RTPrinter rtPrinter;
    private boolean isConnected = false;
    private final boolean isAppActivated = false;

    @BaseEnum.CmdType
    private int currentCmdType = BaseEnum.CMD_ESC;//默认为针打

    @BaseEnum.ConnectType
    private int currentConnectType = BaseEnum.NONE;//默认未连接

    public static final String SP_NAME_SETTING = "setting";

    public static String labelSizeStr = "80*40", labelWidth = "80", labelHeight = "40", labelSpeed = "2", labelType = "CPCL", labelOffset = "0";

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        // Applique la langue enregistrée (français par défaut) avant la création de
        // toute activité afin que la locale et le sens RTL (arabe) soient corrects
        // dès le premier écran.
        LocaleManager.applyStartupLanguage(this);

        // Install a custom EventBus subscriber index BEFORE any EventBus.getDefault() call.
        // This avoids EventBus reflecting on AndroidX framework classes (ComponentActivity),
        // which would trigger NoClassDefFoundError: android.app.PictureInPictureUiState
        // on API < 31 because getParameterTypes() is not wrapped in try/catch by EventBus 3.3.1.
        try {
            EventBus.builder()
                    .addIndex(new AndroidXSafeSubscriberIndex())
                    .installDefaultEventBus();
        } catch (Throwable t) {
            Log.v("TRACKKK", "EventBus index install fail: " + t.getMessage());
        }

        if (BuildConfig.DEBUG) {
            try {
                StrictMode.ThreadPolicy.Builder tpb = new StrictMode.ThreadPolicy.Builder()
                        .detectAll()
                        .penaltyLog();
                StrictMode.setThreadPolicy(tpb.build());

                StrictMode.VmPolicy.Builder vpb = new StrictMode.VmPolicy.Builder()
                        .detectLeakedSqlLiteObjects()
                        .detectActivityLeaks()
                        .detectLeakedClosableObjects()
                        .detectLeakedRegistrationObjects()
                        .penaltyLog();
                StrictMode.setVmPolicy(vpb.build());
            } catch (Throwable t) {
                Log.v("TRACKKK", "StrictMode init fail: " + t.getMessage());
            }
        }

        try {
            System.setProperty("java.net.preferIPv4Stack", "true");
            DATABASE.warmUp(this);
        } catch (Throwable t) {
            Log.v("TRACKKK", "BaseApplication warmUp fail: " + t.getMessage());
        }

        // Apply the "hide navigation bar" (immersive) setting to every activity.
        // The preference is saved by ActivitySetting under the key HIDE_NAV_BAR.
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                applyNavBarPreference(activity);
                registerWindowFocusListener(activity);
            }

            @Override
            public void onActivityStarted(Activity activity) {
            }

            @Override
            public void onActivityResumed(Activity activity) {
                applyNavBarPreference(activity);
                registerWindowFocusListener(activity);
            }

            @Override
            public void onActivityPaused(Activity activity) {
            }

            @Override
            public void onActivityStopped(Activity activity) {
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
            }
        });

        // Make sure the automatic cloud backup stays scheduled across app restarts.
        try {
            if (BackupScheduler.isEnabled(this)) {
                BackupScheduler.schedulePeriodic(this);
                if (BackupScheduler.prefs(this)
                        .getBoolean(BackupScheduler.KEY_BACKUP_PENDING, false)) {
                    BackupScheduler.scheduleImmediate(this);
                }
            }
        } catch (Throwable t) {
            Log.v("TRACKKK", "Cloud backup schedule fail: " + t.getMessage());
        }
    }

    private static final String PREFS_NAME = "ALL_PREFS";
    private static final String KEY_HIDE_NAV_BAR = "HIDE_NAV_BAR";

    /**
     * Registers a window focus change listener on the activity's decor view so that
     * the immersive mode is re-applied whenever the window regains focus (e.g. after
     * a dialog is dismissed).
     */
    private void registerWindowFocusListener(final Activity activity) {
        try {
            final View decorView = activity.getWindow().getDecorView();
            decorView.getViewTreeObserver().addOnWindowFocusChangeListener(
                    new ViewTreeObserver.OnWindowFocusChangeListener() {
                @Override
                public void onWindowFocusChanged(boolean hasFocus) {
                    if (hasFocus) {
                        applyNavBarPreference(activity);
                    }
                }
            });
        } catch (Throwable t) {
            Log.v("TRACKKK", "registerWindowFocusListener fail: " + t.getMessage());
        }
    }

    /**
     * Hides the system navigation bar (back, home, recent) on the given activity
     * when the user has enabled the "Masquer la barre de navigation" setting.
     * Uses WindowInsetsController on API 30+ and falls back to the deprecated
     * SYSTEM_UI_FLAG flags on API 26-29.
     */
    public void applyNavBarPreference(Activity activity) {
        try {
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            if (!prefs.getBoolean(KEY_HIDE_NAV_BAR, false)) {
                return;
            }
            applyImmersiveToWindow(activity.getWindow());
        } catch (Throwable t) {
            Log.v("TRACKKK", "applyNavBarPreference fail: " + t.getMessage());
        }
    }

    /**
     * Applies immersive (hide navigation bar) mode to any window (activity or dialog).
     * Uses WindowInsetsController on API 30+ and the deprecated SYSTEM_UI_FLAG flags
     * on API 26-29.
     */
    public static void applyImmersiveToWindow(Window window) {
        if (window == null) {
            return;
        }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                WindowInsetsController controller = window.getInsetsController();
                if (controller != null) {
                    controller.hide(WindowInsets.Type.navigationBars());
                    controller.setSystemBarsBehavior(
                            WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
                }
            } else {
                applyImmersiveLegacy(window);
            }
        } catch (Throwable t) {
            Log.v("TRACKKK", "applyImmersiveToWindow fail: " + t.getMessage());
        }
    }

    /**
     * Immersive mode for API 26-29 where WindowInsetsController is unavailable.
     * The SYSTEM_UI_FLAG_* constants are deprecated since API 30 but remain the
     * only supported mechanism on older platforms, hence the local suppression.
     */
    @SuppressWarnings("deprecation")
    private static void applyImmersiveLegacy(Window window) {
        View decorView = window.getDecorView();
        int flags = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(flags);
    }

    /**
     * Applies immersive mode to a dialog's window if the user has enabled the
     * "Masquer la barre de navigation" setting. Called by fragments that show
     * AlertDialogs to keep the navigation bar hidden during dialog display.
     */
    public static void applyImmersiveToDialog(Window window) {
        if (window == null) {
            return;
        }
        try {
            SharedPreferences prefs = instance.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            if (!prefs.getBoolean(KEY_HIDE_NAV_BAR, false)) {
                return;
            }
            applyImmersiveToWindow(window);
        } catch (Throwable t) {
            Log.v("TRACKKK", "applyImmersiveToDialog fail: " + t.getMessage());
        }
    }


    public static BaseApplication getInstance() {
        return instance;
    }


    public RTPrinter getRtPrinter() {
        return rtPrinter;
    }

    public void setRtPrinter(RTPrinter rtPrinter) {
        this.rtPrinter = rtPrinter;
    }

    @BaseEnum.CmdType
    public int getCurrentCmdType() {
        return currentCmdType;
    }

    public void setCurrentCmdType(@BaseEnum.CmdType int currentCmdType) {
        this.currentCmdType = currentCmdType;
    }

    @BaseEnum.ConnectType
    public int getCurrentConnectType() {
        return currentConnectType;
    }

    public void setCurrentConnectType(@BaseEnum.ConnectType int currentConnectType) {
        this.currentConnectType = currentConnectType;
    }

    public boolean getIsConnected() {
        return isConnected;
    }

    public void setIsConnected(boolean isConnected) {
        this.isConnected = isConnected;
    }

}
