package chron.carlosrafael.chatapp.Models;

import java.util.ArrayList;

/**
 * Created by CarlosRafael on 16/02/2017.
 */

public class ChatFirebase {

    private String chatID;
    private ArrayList<String> participantes;

    public ChatFirebase(String chatID, ArrayList<String> participantes){
        this.chatID = chatID;
        this.participantes = participantes;
    }

    public String getChatID() {
        return chatID;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }

    public ArrayList<String> getParticipantes() {
        return participantes;
    }

    public void setParticipantes(ArrayList<String> participantes) {
        this.participantes = participantes;
    }
}
