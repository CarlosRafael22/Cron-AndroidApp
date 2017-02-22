package chron.carlosrafael.chatapp;

import android.content.Context;

import com.google.firebase.auth.FirebaseUser;

import org.json.JSONObject;

import java.util.ArrayList;

import chron.carlosrafael.chatapp.Models.ChatFirebase;

/**
 * Created by CarlosRafael on 10/02/2017.
 */

public class UserLoggedIn {
    //VAI VIRAR UM SINGLETON PRA TER SO UM NO PROGRAMA
    private static UserLoggedIn userLoggedIn;
    private Context myContext;
    private FirebaseUser currentFirebaseUser;
    private JSONObject djangoUser;

    private ArrayList<ChatFirebase> chatsFirebase;
    // Listener criado para possibilitar o carregamento do chat apos receber os chats da requisicao ao Firebase
    //private static ChatFirebaseReceivedInterface mListener;

    private UserLoggedIn(Context context){
        myContext = context;
    }

    public static UserLoggedIn getUserLoggedIn(Context context){
        if(userLoggedIn == null){
            userLoggedIn = new UserLoggedIn(context);
//            if (context instanceof ChatFirebaseReceivedInterface) {
//                mListener = (ChatFirebaseReceivedInterface) context;
//                userLoggedIn = new UserLoggedIn(context);
//            } else {
//                throw new RuntimeException(context.toString()
//                        + " must implement ChatFirebaseReceivedInterface");
//            }
        }
        return userLoggedIn;
    }

    public FirebaseUser getCurrentFirebaseUser() {
        return currentFirebaseUser;
    }

    public void setCurrentFirebaseUser(FirebaseUser currentFirebaseUser) {
        this.currentFirebaseUser = currentFirebaseUser;
    }

    public JSONObject getDjangoUser() {
        return djangoUser;
    }

    public void setDjangoUser(JSONObject djangoUser) {
        this.djangoUser = djangoUser;
    }

    public ArrayList<ChatFirebase> getChatsFirebase() {
        return chatsFirebase;
    }

    public void setChatsFirebase(ArrayList<ChatFirebase> chatsFirebase) {
        this.chatsFirebase = chatsFirebase;

        // Vou notificar que recebeu os chats do Firebase para o CoachChatFragment atualizar o chat
        //mListener.receivedChatFirebase();
    }


//    public interface ChatFirebaseReceivedInterface{
//        void receivedChatFirebase();
//    }
}

