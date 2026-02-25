package com.safesoft.proapp.distribute.gps.service_start;

import static android.content.Context.JOB_SCHEDULER_SERVICE;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.Log;

import androidx.core.content.ContextCompat;

public class RestartBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "RestartBroadcastReceive";
    //private static JobScheduler jobScheduler;
    private RestartBroadcastReceiver restartBroadcastReceiver;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(RestartBroadcastReceiver.class.getSimpleName(), "Service Stopped, but this is a never ending service.");
        scheduleJob(context);
    }

    public static void scheduleJob(Context context) {

        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        stopJob(context);
        ComponentName componentName = new ComponentName(context, JobService.class);

        JobInfo jobInfo = new JobInfo.Builder(1, componentName)
                .setMinimumLatency(1000)
                .setPersisted(true)
                .build();

        jobScheduler.schedule(jobInfo);
        Log.i("LOCATION 2", "Jobs 2 = " + jobScheduler.getAllPendingJobs().size());
    }

    public static void stopJob(Context context) {
        JobScheduler js = context.getSystemService(JobScheduler.class);
        Log.i("LOCATION 2", "Jobs 1 = " + js.getAllPendingJobs().size());
        js.cancel(1);

        Intent intent = new Intent(context, AutoStartService.class);
        context.stopService(intent);
    }

    public static void reStartTracker(Context context) {
        // restart the never ending service
        Log.i(TAG, "Restarting tracker");
        Intent broadcastIntent = new Intent(Globals.RESTART_INTENT).setPackage(context.getPackageName());
        context.sendBroadcast(broadcastIntent);
    }


    private void registerRestarterReceiver(final Context context) {

        // the context can be null if app just installed and this is called from restartsensorservice
        // https://stackoverflow.com/questions/24934260/intentreceiver-components-are-not-allowed-to-register-to-receive-intents-when
        // Final decision: in case it is called from installation of new version (i.e. from manifest, the application is
        // null. So we must use context.registerReceiver. Otherwise this will crash and we try with context.getApplicationContext
        if (restartBroadcastReceiver == null)
            restartBroadcastReceiver = new RestartBroadcastReceiver();
        else try{
            context.unregisterReceiver(restartBroadcastReceiver);
        } catch (Exception e){
            // not registered
        }
        // give the time to run
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // we register the  receiver that will restart the background service if it is killed
                // see onDestroy of Service
                IntentFilter filter = new IntentFilter();
                filter.addAction(Globals.RESTART_INTENT);
                try {
                    ContextCompat.registerReceiver(context, restartBroadcastReceiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED);
                } catch (Exception e) {
                    try {
                        ContextCompat.registerReceiver(context.getApplicationContext(), restartBroadcastReceiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED);
                    } catch (Exception ex) {

                    }
                }
            }
        }, 1000);

    }
}