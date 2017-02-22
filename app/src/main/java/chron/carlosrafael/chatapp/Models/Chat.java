package chron.carlosrafael.chatapp.Models;

import java.util.ArrayList;

/**
 * Created by CarlosRafael on 15/02/2017.
 */

public class Chat {

    private String chatNameID;
    private String coach;
    private ArrayList<String> usernamesPacientes;

    public Chat(){

    }

    public Chat(String chatNameID, String coachUsername, ArrayList<String> usernamesPacientes){
        this.chatNameID = chatNameID;
        this.coach = coachUsername;
        this.usernamesPacientes = usernamesPacientes;
    }

    public String getChatNameID() {
        return chatNameID;
    }

    public void setChatNameID(String chatNameID) {
        this.chatNameID = chatNameID;
    }

    public String getCoach() {
        return coach;
    }

    public void setCoach(String coachUsername) {
        this.coach = coachUsername;
    }

    public ArrayList<String> getUsernamesPacientes() {
        return usernamesPacientes;
    }

    public void setUsernamesPacientes(ArrayList<String> usernamesPacientes) {
        this.usernamesPacientes = usernamesPacientes;
    }
}
