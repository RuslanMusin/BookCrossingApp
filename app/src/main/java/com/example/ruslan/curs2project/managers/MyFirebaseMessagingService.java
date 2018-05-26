/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.ruslan.curs2project.managers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.ruslan.curs2project.R;
import com.example.ruslan.curs2project.ui.StartActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Date;
import java.util.Map;

import static com.example.ruslan.curs2project.utils.Const.TAG_LOG;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "Message from server " + remoteMessage.getFrom());

        Log.d(TAG_LOG,"message = " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            sendNotification(null, remoteMessage.getData());
        }
        if (remoteMessage.getNotification() != null) {
            sendNotification(remoteMessage.getNotification(), null);
        }
    }

    private void sendNotification(RemoteMessage.Notification notification,
                                  Map<String, String> data) {
        String title;
        String body;

        if(notification == null){
            title = "New Item Details";
            body = data.get("itemName")+" "+data.get("itemPrice")+" "+data.get("location");
        }else{
            title = notification.getTitle();
            body  = notification.getBody();
            Log.d(TAG_LOG, "title = " + title + " body = " + body);
        }


        Intent intent = new Intent(this,  StartActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, "fcm-instance-specific")
                        .setSmallIcon(R.drawable.ic_instagram_white)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Date date = new Date();

        notificationManager.notify((int) date.getTime(),notificationBuilder.build());
    }
}
