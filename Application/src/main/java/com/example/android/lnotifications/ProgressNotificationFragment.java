package com.example.android.lnotifications;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ProgressNotificationFragment extends Fragment {
    public static final int NOTIFICATION_ID = 1;

    public static ProgressNotificationFragment newInstance() {
        ProgressNotificationFragment fragment = new ProgressNotificationFragment();
        fragment.setRetainInstance(true);
        return fragment;
    }

    public ProgressNotificationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_progress_notification, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNotification();
            }
        });

        view.findViewById(R.id.button_background).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent serviceIntent = new Intent(getActivity(), ProgressService.class);
                serviceIntent.setAction(ProgressService.ACTION_START);
                getActivity().startService(serviceIntent);
            }
        });


    }

    private void createNotification() {
        /**
         * Use NotificationCompat.Builder to set up our notification.
         */
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity());
        builder.setContentTitle("Picture Download")
                .setContentText("Download in progress")
                .setSmallIcon(R.drawable.ic_launcher_notification)
                .setOngoing(true);

        //Create Intent to launch this Activity again if the notification is clicked.
        Intent i = new Intent(getActivity(), LNotificationActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent = PendingIntent.getActivity(getActivity(), 0, i,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(intent);

        /**
         * Send the notification. This will immediately display the notification icon in the
         * notification bar.
         */
        final NotificationManager notificationManager =
                (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

        // Start a lengthy operation in a background thread
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        // Do the "lengthy" operation 20 times
                        for (int i = 0; i <= 100; i += 5) {
                            // Sets the progress indicator to a max value, the
                            // current completion percentage, and "determinate"
                            // state
                            builder.setProgress(100, i, false);

                            builder.setContentText(String.format("%3d%%", (int) ((i / 100f) * 100)));
                            // Displays the progress bar for the first time.
                            notificationManager.notify(NOTIFICATION_ID, builder.build());
                            // Sleeps the thread, simulating an operation
                            // that takes time
                            try {
                                // Sleep for 5 seconds
                                Thread.sleep(5 * 1000);
                            } catch (InterruptedException e) {
                            }
                        }
                        // When the loop is finished, updates the notification
                        builder.setContentText("Download complete")
                                // Removes the progress bar
                                .setProgress(0, 0, false);
                        notificationManager.notify(NOTIFICATION_ID, builder.build());
                    }
                }
                // Starts the thread by calling the run() method in its Runnable
        ).start();

    }
}
