package com.psi.ciclodias.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.mapbox.navigation.base.trip.model.TripNotificationState;
import com.mapbox.navigation.base.trip.notification.TripNotification;
import com.psi.ciclodias.R;

public class TreinoNotification implements TripNotification {

    private static final String CHANNEL_ID = "1411";
    private Context context;


    public TreinoNotification(Context context){
        this.context = context;
    }

    @NonNull
    @Override
    public Notification getNotification() {
        createNotificationChannel();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("Treino")
                .setContentText("Treino")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        return builder.build();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Treino";
            String description = "Sessão de Treino";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

        @Override
    public int getNotificationId() {
        return 0;
    }

    @Override
    public void onTripSessionStarted() {

    }

    @Override
    public void onTripSessionStopped() {

    }

    @Override
    public void updateNotification(@NonNull TripNotificationState tripNotificationState) {

    }
}
