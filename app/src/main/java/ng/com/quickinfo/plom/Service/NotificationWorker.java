package ng.com.quickinfo.plom.Service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

import ng.com.quickinfo.plom.DetailActivity;
import ng.com.quickinfo.plom.Receivers.NotificationReceiver;

import static ng.com.quickinfo.plom.Utils.Utilities.log;


public class NotificationWorker extends Worker {
    //notif
    NotificationReceiver notificationReceiver;
    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    /*
     * This method is responsible for doing the work
     * so whatever work that is needed to be performed
     * we will put it here
     *
     * For example, here I am calling the method displayNotification()
     * It will display a notification
     * So that we will understand the work is executed
     * */

    @NonNull
    @Override
    public Result doWork() {
        log("DetailWorker", "intent not null");
        Intent intent = new Intent();
        intent.setAction(DetailActivity.notifAction);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        displayNotification("My Worker", "Heyv I finished my work");
        return Result.SUCCESS;
    }

    /*
     * The method is doing nothing but only generating
     * a simple notification
     * If you are confused about it
     * you should check the Android Notification Tutorial
     * */
    private void displayNotification(String title, String task) {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        log("DetaiWorker", "displayed");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("simplifiedcoding", "simplifiedcoding", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(), "simplifiedcoding")
                .setContentTitle(title)
                .setContentText(task);
//                .setSmallIcon(R.mipmap.ic_launcher);

        notificationManager.notify(1, notification.build());
    }
}