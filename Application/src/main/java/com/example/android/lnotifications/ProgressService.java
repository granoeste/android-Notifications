package com.example.android.lnotifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class ProgressService extends Service {
    private static final String TAG = ProgressService.class.getSimpleName();

    public static final int NOTIFICATION_ID = 1;
    public static final String ACTION_START = "com.example.android.lnotifications.ACTION_START";
    public static final String ACTION_DISMISS = "com.example.android.lnotifications.ACTION_DISMISS";

    private NotificationCompat.Builder mBuilder;
    private NotificationManager mNotificationManager;
    private ProgressThread mProgressThread;

    public ProgressService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        switch (action) {
        case ACTION_START:
            createNotification();
            break;
        case ACTION_DISMISS:
            if (mProgressThread != null) {
                mProgressThread.quit();
            }
            break;
        default:
        }
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy()");
    }

    private void createNotification() {
        /**
         * Use NotificationCompat.Builder to set up our notification.
         */
        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle("Picture Download")
                .setContentText("Download in progress")
                .setSmallIcon(R.drawable.ic_launcher_notification);

        //Create Intent to launch this Activity again if the notification is clicked.
        Intent i = new Intent(this, LNotificationActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent = PendingIntent.getActivity(this, 0, i,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(intent);

        // create dismiss action.
        Intent dismissIntent = new Intent(this, ProgressService.class);
        dismissIntent.setAction(ACTION_DISMISS);
        PendingIntent piDismiss = PendingIntent.getService(this, 0, dismissIntent, 0);

        // add action
        mBuilder.addAction(android.R.drawable.ic_delete, getString(R.string.dismiss), piDismiss);

        // set action when delete notification.
        mBuilder.setDeleteIntent(piDismiss);
        // [[ CAUTION ]]
        // Notification Auto-Cancel does not call DeleteIntent
        // - Stack Overflow http://stackoverflow.com/questions/13078230/notification-auto-cancel-does-not-call-deleteintent
        // --------------------------------------------------------------------

        /**
         * Send the notification. This will immediately display the notification icon in the
         * notification bar.
         */
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mProgressThread = new ProgressThread();
        mProgressThread.start();
    }


    public class ProgressThread extends Thread {
        private volatile boolean mQuit = false;

        @Override
        public void run() {
            // Do the "lengthy" operation 20 times
            for (int i = 0; i <= 100; i += 5) {
                try {
                    // Sets the progress indicator to a max value, the
                    // current completion percentage, and "determinate"
                    // state
                    mBuilder.setProgress(100, i, false);

                    mBuilder.setContentText(String.format("%3d%%", (int) ((i / 100f) * 100)));
                    // Displays the progress bar for the first time.
                    mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
                    // Sleeps the thread, simulating an operation
                    // that takes time
                    // Sleep for 5 seconds
                    Thread.sleep(5 * 1000);

                } catch (InterruptedException e) {
                    // We may have been interrupted because it was time to quit.
                    if (mQuit) {
                        Log.d(TAG, "Quit ProgressThread and ProgressServices.");
                        mNotificationManager.cancel(NOTIFICATION_ID);
                        stopSelf();
                        return;
                    }
                    continue;
                }

            }
            // When the loop is finished, updates the notification
            mBuilder.setContentText("Download complete")
                    // Removes the progress bar
                    .setProgress(0, 0, false);
            mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        }

        public void quit() {
            mQuit = true;
            interrupt();
        }
    }

}
