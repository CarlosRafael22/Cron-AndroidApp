package chron.carlosrafael.chatapp;

/**
 * Copyright Google Inc. All Rights Reserved.
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

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.auth.api.model.StringList;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Random;

/**
 * Created by CarlosRafael on 02/11/2016.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFMService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Handle data payload of FCM messages.
        Log.d(TAG, "FCM Message From: " + remoteMessage.getFrom());
        Log.d(TAG, "FCM Message To: " + remoteMessage.getTo());
        Log.d(TAG, "FCM Message Id: " + remoteMessage.getMessageId());
        //Log.d(TAG, "FCM Data Message: " + remoteMessage.getData());


        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            Map messageMap = remoteMessage.getData();
            Log.d(TAG, "FCM Data Message: " + messageMap.get("message"));
            Log.d(TAG, "FCM Data Username: " + messageMap.get("username"));
            Log.d(TAG, "FCM Data MessageKey: " + messageMap.get("messageKey"));
            Log.d(TAG, "NotificationList: " + NotificationHandler.keys_messagesAlreadyReceived.size());
            if(NotificationHandler.keys_messagesAlreadyReceived.contains(messageMap.get("messageKey"))){
                Log.d(TAG, "Notification nao criou para: " + NotificationHandler.keys_messagesAlreadyReceived.get(NotificationHandler.keys_messagesAlreadyReceived.size() - 1));
            }



            //Como eu so to mandando o "data", ele vai entrar no onMessageReceived toda vez
            //Tanto com o app no Foreground quanto no Background
            //Entao aqui eu vejo, se ele nao tiver no Foreground eu crio a notificacao
            String notification_author = (String) messageMap.get("username");
            if(!ForegroundHandler.isActivityVisible() && !notification_author.equalsIgnoreCase(NotificationHandler.current_user)
                    && !NotificationHandler.keys_messagesAlreadyReceived.contains(messageMap.get("messageKey"))){
                sendNotification((String) messageMap.get("message"), (String) messageMap.get("username"));
            }

        }

        //sendNotification(notification.getBody());
    }


    public void displayNotification(RemoteMessage.Notification remoteNotification, Map<String,String> messageMap){

        String username = messageMap.get("username");
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle(remoteNotification.getTitle())
                .setContentText(username + " : " + remoteNotification.getBody())
                .setSmallIcon(R.mipmap.ic_launcher)
                .build();

        //Sets an ID for the notification
        int notificationID = 001;

        int numMessages = 0;

        //Gets an instance of the NotificationManager service
        NotificationManager notifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        //Builds the notification and issues it
        notifyManager.notify(notificationID, notification);
    }



    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String messageBody, String messageUser) {


        //Se ainda nao tiver clicado na notification a ID vai ter que ser a mesma
        //Se tiver clicado entao temos que gerar uma nova ID
        int notificationID;
        if(NotificationHandler.createNewNotification){
            Random generator = new Random();
            notificationID = generator.nextInt(100);

            NotificationHandler.onGoingNotificationID = notificationID;

            //Dizendo que nao precisa criar uma nova notificacao ate o usuario clicar na notificacao e abrir o chat
            NotificationHandler.createNewNotification = false;
        }else{
            notificationID = NotificationHandler.onGoingNotificationID;
        }


        //Quando o usuario clicar ele vai pra o MainActivity
        Intent intent = new Intent(this, MainActivity.class);

        //Botando um Extra para quando ele abrir por um Intent vindo do notification ele para de acumular as
        //mensagens de notificacao
        intent.putExtra("fromNotification", true);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("New message in chat")
                .setContentText(messageUser + " : " + messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //Adicionando as mensagens no ArrayList que mantem todas as mensagens da notification que ainda vai ser vista
        NotificationHandler.notificationMessages.add(messageUser + " : " + messageBody);

        //Se nao eh pra criar uma nova notificacao entao temos que:
        // 1 - Adicionar essa mensagem como a ultima no ArrayList da notificacao
        // 2 - Ler o ArrayList pra criar o InboxStyle com todas as mensagens
        if(!NotificationHandler.createNewNotification){

            int numMessages = NotificationHandler.notificationMessages.size();

            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
            String title = (numMessages>1)? String.valueOf(numMessages) + " new messages in chat" : String.valueOf(numMessages) + " new message in chat";
            inboxStyle.setBigContentTitle(title);
            //inboxStyle.addLine(messageBody);
            for(int i=0;i<NotificationHandler.notificationMessages.size();i++){
                //notificationBuilder.setContentText("Tamo no loop" + String.valueOf(i))
                notificationBuilder.setNumber(NotificationHandler.notificationMessages.size());
                inboxStyle.addLine(NotificationHandler.notificationMessages.get(i));
                //inboxStyle.addLine("Tamo no loop" + String.valueOf(i));
                // Because the ID remains unchanged, the existing notification is
                // updated.
//            notificationManager.notify(
//                    notificationID,
//                    notificationBuilder.build());
            }

            notificationBuilder.setStyle(inboxStyle);
        }

        notificationManager.notify(
                notificationID,
                notificationBuilder.build());


        // Issue the notification here.


        //notificationManager.notify(notificationID /* ID of notification */, notificationBuilder.build());

    }

}

