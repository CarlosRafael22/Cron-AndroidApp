package chron.carlosrafael.chatapp;

/**
 * Created by CarlosRafael on 22/10/2016.
 */

public class Message {

    private String text;
    private String name;
    private String photoUrl;

    //Testando pra ele verificar isso ao criar uma notificacao
    //Se tiver a mesma ID de uma msg ja vista ele nao cria
    private String firebaseID;

    public Message() {
    }

    public Message(String text, String name, String photoUrl) {
        this.text = text;
        this.name = name;
        this.photoUrl = photoUrl;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getFirebaseID() {
        return firebaseID;
    }

    public void setFirebaseID(String firebaseID) {
        this.firebaseID = firebaseID;
    }
}
