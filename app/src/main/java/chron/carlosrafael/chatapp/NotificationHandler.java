package chron.carlosrafael.chatapp;

import java.util.ArrayList;

/**
 * Created by CarlosRafael on 13/11/2016.
 */

public class NotificationHandler {

    //Setado pra true pq o padrao eh de que ele fique atualizando a mesma notificacao
    public static boolean createNewNotification = true;
    public static int onGoingNotificationID;

    //Meio que gambiarra pra saber o user que mandou a mensagem e ele mesmo nao receber uma notificacao
    public static String current_user;

    public static ArrayList<String> notificationMessages = new ArrayList<>();


    //ArrayList com as Keys das mensagens que chegaram pra ao receber uma notificacao verificar se a msg ja foi recebida
    //Se ela ja tiver sido mostrada no chat para o usuario entao nao manda notificacao
    public static ArrayList<String> keys_messagesAlreadyReceived = new ArrayList<>();

    //Quando clicou a gnt tem que tirar todas as mensagens do ArrayList e
    // deixar o boolean true para criar uma nova notificacao
    public static void emptyNotificationStack(){
        createNewNotification = true;
        notificationMessages.clear();
    }



}
