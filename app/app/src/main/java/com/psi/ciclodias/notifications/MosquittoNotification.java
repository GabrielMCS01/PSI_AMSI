package com.psi.ciclodias.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.psi.ciclodias.R;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Random;

public class MosquittoNotification implements MqttCallback {

    private static final String ID = "id";
    private static final String CHANNEL_ID = "71737165";
    private Context contexto;

    public MosquittoNotification(){
    }

    public void start(Context context) {

        contexto = context;
        createNotificationChannel(context);

        MqttClient mqttClient;

        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);

        String clientID = sharedPreferences.getString(ID, "");


        try {
            mqttClient = new MqttClient("tcp://ciclodias.duckdns.org:1883",
                    clientID,null);

            mqttClient.setCallback(this);
            mqttClient.connect();


            String myTopic = sharedPreferences.getString(ID, "");
            int subQoS = 0;
            mqttClient.subscribe(myTopic, subQoS);
            mqttClient.subscribe("leaderboard", subQoS);




        } catch (MqttException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void connectionLost(Throwable cause) {

    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        System.out.println(new String(message.getPayload()));

        NotificationCompat.Builder builder = new NotificationCompat.Builder(contexto, CHANNEL_ID)
                .setSmallIcon(R.drawable.ciclodias_logo_transparent)
                .setContentTitle("Mosquitto")
                .setContentText(new String(message.getPayload()))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(contexto);

        notificationManager.notify(new Random().nextInt(999999999), builder.build());

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }

    private void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.channel_name);
            String description = context.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
