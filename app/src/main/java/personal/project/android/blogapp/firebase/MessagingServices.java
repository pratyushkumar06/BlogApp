package personal.project.android.blogapp.firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Objects;

import personal.project.android.blogapp.R;

import static android.app.Notification.DEFAULT_ALL;
import static android.app.PendingIntent.getActivity;

public class MessagingServices extends FirebaseMessagingService {
    int ID=(int) System.currentTimeMillis();
    NotificationChannel channel;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String click_ac= Objects.requireNonNull(remoteMessage.getNotification()).getClickAction();
        String dat1=remoteMessage.getData().get("message");
        String dat2=remoteMessage.getData().get("from_id");


        String channelId = "Your_channel_id";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel(channelId, "Channel human readable title", NotificationManager.IMPORTANCE_DEFAULT);

        }


        String message=remoteMessage.getNotification().getBody();
        String title=remoteMessage.getNotification().getTitle();

        NotificationCompat.Builder notifications=new NotificationCompat.Builder(this);
        notifications.setSmallIcon(R.drawable.ic_launcher_foreground);
        notifications.setTicker("This is a ticker");
        notifications.setWhen(System.currentTimeMillis());
        notifications.setDefaults(DEFAULT_ALL);
        notifications.setChannelId(channelId);// for android Oreo onwards creating channels is a must
        notifications.setContentText(message);
        notifications.setContentTitle(title);
        notifications.setAutoCancel(true);

        Intent intent=new Intent(click_ac);
        intent.putExtra("message",dat1);
        intent.putExtra("from_id",dat2);
        PendingIntent intent2=getActivity(MessagingServices.this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        notifications.setContentIntent(intent2);

        NotificationManager manager=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            manager.createNotificationChannel(channel);
        }
        manager.notify(ID,notifications.build());
    }
}
