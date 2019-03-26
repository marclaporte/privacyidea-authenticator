/*
  privacyIDEA Authenticator

  Authors: Nils Behlen <nils.behlen@netknights.it>

  Copyright (c) 2017-2019 NetKnights GmbH

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/

package it.netknights.piauthenticator;

import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import static it.netknights.piauthenticator.AppConstants.AUTHENTICATION_URL;
import static it.netknights.piauthenticator.AppConstants.NONCE;
import static it.netknights.piauthenticator.AppConstants.NOTIFICATION_CHANNEL_ID;
import static it.netknights.piauthenticator.AppConstants.NOTIFICATION_ID;
import static it.netknights.piauthenticator.AppConstants.QUESTION;
import static it.netknights.piauthenticator.AppConstants.SERIAL;
import static it.netknights.piauthenticator.AppConstants.SIGNATURE;
import static it.netknights.piauthenticator.AppConstants.SSL_VERIFY;
import static it.netknights.piauthenticator.AppConstants.TITLE;
import static it.netknights.piauthenticator.Util.logprint;

public class FCMReceiverService extends FirebaseMessagingService {

    String question, nonce, serial, signature, title, url;
    boolean sslVerify = true;

    @Override
    public void onMessageReceived(RemoteMessage message) {
        // get the key-value pairs
        Map<String, String> map = message.getData();
        logprint("FCM message received: " + message.getData().toString());
        if (map.containsKey(QUESTION)) {
            question = map.get(QUESTION);
        }
        if (map.containsKey(NONCE)) {
            nonce = map.get(NONCE);
        }
        if (map.containsKey(SERIAL)) {
            serial = map.get(SERIAL);
        }
        if (map.containsKey(TITLE)) {
            title = map.get(TITLE);
        }
        if (map.containsKey(AUTHENTICATION_URL)) {
            url = map.get(AUTHENTICATION_URL);
        }
        if (map.containsKey(SIGNATURE)) {
            signature = map.get(SIGNATURE);
        }
        if (map.containsKey(SSL_VERIFY)) {
            if (Integer.parseInt(map.get(SSL_VERIFY)) < 1) {
                sslVerify = false;
            }
        }

        // Start the service with the data from the push when the button in the notification is pressed
        Intent service_intent = new Intent(this, PushAuthService.class);
        service_intent.putExtra(SERIAL, serial)
                .putExtra(NONCE, nonce)
                .putExtra(TITLE, title)
                .putExtra(AUTHENTICATION_URL, url)
                .putExtra(SIGNATURE, signature)
                .putExtra(QUESTION, question)
                .putExtra(SSL_VERIFY, sslVerify);

        PendingIntent pService_intent = PendingIntent.getService(this, 0, service_intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Action action = new NotificationCompat.Action.Builder(0, "Allow", pService_intent).build();

        Intent activity_intent = new Intent(this, MainActivity.class);
        activity_intent.putExtra(SERIAL, serial)
                .putExtra(NONCE, nonce)
                .putExtra(TITLE, title)
                .putExtra(AUTHENTICATION_URL, url)
                .putExtra(SIGNATURE, signature)
                .putExtra(QUESTION, question)
                .putExtra(SSL_VERIFY, sslVerify);

        PendingIntent pActivity_intent = PendingIntent.getActivity(this, 0, activity_intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // TODO indicate verification from within app?

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,
                NOTIFICATION_CHANNEL_ID)                                // Android 8+ uses notification channels
                .setSmallIcon(R.drawable.ic_pi_notification)
                .setContentTitle(title)
                .setContentText(question)
                .setPriority(NotificationCompat.PRIORITY_MAX)          // 7.1 and lower
                .addAction(action)                                     // Add the allow Button
                .setAutoCancel(true)                                   // Remove the notification after tabbing it
                .setWhen(0)
                .setContentIntent(pActivity_intent);                   // Intent for opening activity with the request

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        logprint("New Token in FCMReceiver: " + s);
    }

}