package com.example.clothingwebshop;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;


public class NotificationHandler {
    private static final String CHANNEL_ID = "clothingwebshop_notification_channel";
    private final int NOTIFICATION_ID = 0;

    private NotificationManager mNotifyManager;
    private Context mContext;


    public NotificationHandler(Context context) {
        this.mContext = context;
        this.mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        newChannel();
    }

    private void newChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            return;

        NotificationChannel channel = new NotificationChannel
                (CHANNEL_ID, "ClothingWebShop Notification", NotificationManager.IMPORTANCE_HIGH);

        channel.enableVibration(true);
        channel.setDescription("Vásároljon most szuper áron online ruhakészletünkből!");

        this.mNotifyManager.createNotificationChannel(channel);
    }

    public void send(String message) {
        Intent intent = new Intent(mContext, ShopListActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, NOTIFICATION_ID, intent, PendingIntent.FLAG_IMMUTABLE);



        NotificationCompat.Builder ncBuilder = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                .setContentTitle("Vásárlás történt a Ruhabolt alkalmazásban!")
                .setContentText(message)
                .setSmallIcon(R.drawable.icon_cart)
                .setContentIntent(pendingIntent);


        this.mNotifyManager.notify(NOTIFICATION_ID, ncBuilder.build());
    }

    public void cancel(){
        this.mNotifyManager.cancel(NOTIFICATION_ID);
    }

}