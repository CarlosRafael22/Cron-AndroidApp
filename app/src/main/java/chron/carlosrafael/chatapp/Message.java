package chron.carlosrafael.chatapp;

/**
 * Created by CarlosRafael on 22/10/2016.
 */

public class Message {

    private String text;
    private String nameSender;
    private String photoUrl;
    private String idSender;

    //Testando pra ele verificar isso ao criar uma notificacao
    //Se tiver a mesma ID de uma msg ja vista ele nao cria
    private String firebaseID;

    public Message() {
    }

    public Message(String text, String name, String photoUrl, String idSender) {
        this.text = text;
        this.nameSender = name;
        this.photoUrl = photoUrl;
        this.idSender = idSender;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

//    public String getName() {
//        return nameSender;
//    }
//
//    public void setName(String name) {
//        this.nameSender = name;
//    }

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

    public String getIdSender() {
        return idSender;
    }

    public void setIdSender(String idSender) {
        this.idSender = idSender;
    }

    public String getNameSender() {
        return nameSender;
    }

    public void setNameSender(String nameSender) {
        this.nameSender = nameSender;
    }
}
