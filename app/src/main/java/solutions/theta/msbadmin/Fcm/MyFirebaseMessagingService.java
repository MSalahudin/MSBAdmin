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

package solutions.theta.msbadmin.Fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;

import android.support.v4.app.NotificationCompat;
import android.util.Log;


import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.net.URL;

import solutions.theta.msbadmin.Classes.NotificationID;
import solutions.theta.msbadmin.MainActivity1;
import solutions.theta.msbadmin.R;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
Bitmap bitmapobj;
    String title,url,src,website,phone,whatsapp,email;
    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            String msg = (String)remoteMessage.getData().get("message");
             src = (String)remoteMessage.getData().get("src");
             title = (String)remoteMessage.getData().get("title");
             website=(String)remoteMessage.getData().get("website");
            phone=(String)remoteMessage.getData().get("phone");
            whatsapp=(String)remoteMessage.getData().get("whatsapp");
            email=(String)remoteMessage.getData().get("email");
            if(title==null){
                title="OMS";
            }
             url = (String)remoteMessage.getData().get("url");
            bitmapobj=getBitmapFromURL(src);
            if(bitmapobj==null){
                bitmapobj=BitmapFactory.decodeResource(getResources(), R.drawable.ic_stat_ic_notification);
           }
            sendNotification(msg);
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String messageBody) {
        Log.d(TAG, "msg: " + messageBody);
        Intent intent = new Intent(this, MainActivity1.class);
        intent.putExtra("title", title);
        intent.putExtra("Msg", messageBody);
        intent.putExtra("url", url);
        intent.putExtra("src", src);
        intent.putExtra("website", website);
        intent.putExtra("phone", phone);
        intent.putExtra("whatsapp", whatsapp);
        intent.putExtra("email", email);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, (int)System.currentTimeMillis()  /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.notifi)
                .setLargeIcon(bitmapobj)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(NotificationID.getID() /* ID of notification */, notificationBuilder.build());
    }

    public Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            return  image;
        } catch(IOException e) {
            System.out.println(e);
            return null;
        }

    }
}
