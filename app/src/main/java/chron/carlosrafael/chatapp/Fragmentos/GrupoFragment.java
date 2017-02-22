package chron.carlosrafael.chatapp.Fragmentos;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import chron.carlosrafael.chatapp.ForegroundHandler;
import chron.carlosrafael.chatapp.HomeActivity;
import chron.carlosrafael.chatapp.Interfaces;
import chron.carlosrafael.chatapp.Message;
import chron.carlosrafael.chatapp.MessageViewHolder;
import chron.carlosrafael.chatapp.Models.ChatFirebase;
import chron.carlosrafael.chatapp.NotificationHandler;
import chron.carlosrafael.chatapp.R;
import chron.carlosrafael.chatapp.SignInActivity;
import chron.carlosrafael.chatapp.UserLoggedIn;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GrupoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GrupoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GrupoFragment extends Fragment implements HomeActivity.ChatFirebaseReceivedInterface{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private static final String TAG = "GrupoFragment";
    public static String MESSAGES_CHILD = "chatMessages/c5p14";
    public static final String CHATS_CHILD = "chats";
    public static final String NOTIFICATIONS_CHILD = "notificationRequests";
    private static final int REQUEST_INVITE = 1;
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 10;
    public static final String ANONYMOUS = "anonymous";
    private static final String MESSAGE_SENT_EVENT = "message_sent";

    //Objetos para lidar com as mensagens do chat

    //The RecyclerView widget is a more advanced and flexible version of ListView.
    //To use the RecyclerView widget, you have to specify an adapter and a layout manager.
    private RecyclerView mMessageRecyclerView;
    // A layout manager positions item views inside a RecyclerView and determines when to reuse item views that are no longer visible to the user.
    // To reuse (or recycle) a view, a layout manager may ask the adapter to replace the contents of the view with a different element from the dataset.
    private LinearLayoutManager mLinearLayoutManager;
    private ProgressBar mProgressBar;

    private Button mSendButton;
    private EditText mMessageEditText;
    private TextView userInfoTxtView;

    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    // Para mandter um listener do estado ao fazer login localmente com email ou senha
    private FirebaseAuth.AuthStateListener mAuthListener;

    //    Initialize the Firebase Realtime Database and add a listener to handle changes made to the data. Update the RecyclerView adapter so new messages will be shown.*/
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<Message, MessageViewHolder>
            mFirebaseAdapter;

    private String mUsername;
    private String mPhotoUrl;


    private UserLoggedIn userLoggedIn;

    public static final String BASE_URL = "http://54.202.76.189:8000/";


    // Serve para avisar o HomeActivity que o fragmento foi criado
    private Interfaces.OnFragmentCreatedListener createdFragListener;

    // Usada para fazer com que avise ao HomeActivity que o fragmento foi criado pela primeira vez
    // O onCreate e onCreateView sao executados todas as vezes q o Fragmento vai ser visto, porem
    // so queremos que ele mande o aviso na primeira vez que eh executado para pegar os chats
    private static boolean CREATED_FOR_THE_FIRST_TIME = false;

    public GrupoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GrupoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GrupoFragment newInstance(String param1, String param2) {
        GrupoFragment fragment = new GrupoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_grupo, container, false);

        userLoggedIn = UserLoggedIn.getUserLoggedIn(getContext());
        Log.d(TAG, "INICIANDO GRUPO COM USERLOGGEDIN: "+userLoggedIn);

        //Primeiro vai ver se foi aberto por causa de um Intent
        //Se for ele checa se esse intent veio da Notification
        //Se tiver vindo ele para de acumular as mensagens da BigStyle Notification
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.getExtras() != null) {
            Log.d(TAG, "Identificou Intent!");
            if (intent.getBooleanExtra("fromNotification", false)) {
                NotificationHandler.emptyNotificationStack();
                Log.d(TAG, "Era pra ter limpado!");
            }
        }

        //TODA VEZ QUE ELE ENTRAR ELE VAI LIMPAR O NotificationHandler.keys_messagesAlreadyReceived
        // PARA NAO FICAR ACUMULANDO AS KEYS DAS MSGS Q CHEGARAM
        NotificationHandler.keys_messagesAlreadyReceived.clear();
        Log.d(TAG, "NotificationList ZEROU: " + NotificationHandler.keys_messagesAlreadyReceived.size());


//        mAuthListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                FirebaseUser user = firebaseAuth.getCurrentUser();
//                if (user != null) {
//                    // User is signed in
//                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
//                } else {
//                    // User is signed out
//                    Log.d(TAG, "onAuthStateChanged:signed_out");
//                }
//                // ...
//            }
//        };

        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        Log.d(TAG, "FirebaseUser: " + mFirebaseUser);
        Log.d(TAG, "FirebaseUser UserloggedIn: " + userLoggedIn.getCurrentFirebaseUser());
        Log.d(TAG, "UserloggedIn: " + userLoggedIn.getDjangoUser());
        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(getContext(), SignInActivity.class));
            //finish();
            //return;
        } else {
            mUsername = mFirebaseUser.getDisplayName();
            if (mFirebaseUser.getPhotoUrl() != null) {
                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            }
        }

        // Initialize ProgressBar and RecyclerView.
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        mMessageRecyclerView = (RecyclerView) rootView.findViewById(R.id.messageRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        mLinearLayoutManager.setStackFromEnd(true);
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);


        // New child entries
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

//        mFirebaseDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot snapshot) {
//                // do some stuff once
//                if (snapshot.getValue() == null){
//                    mProgressBar.setVisibility(ProgressBar.INVISIBLE);
//                }
//            }
//            @Override
//            public void onCancelled(DatabaseError firebaseError) {
//            }
//        });

        ArrayList<ChatFirebase> chatsPaciente = userLoggedIn.getChatsFirebase();
        if(chatsPaciente != null) {
            mFirebaseAdapter = new FirebaseRecyclerAdapter<Message,
                    MessageViewHolder>(
                    Message.class,
                    R.layout.item_message,
                    MessageViewHolder.class,
                    mFirebaseDatabaseReference.child(MESSAGES_CHILD)) {

                @Override
                protected void populateViewHolder(MessageViewHolder viewHolder,
                                                  Message friendlyMessage, int position) {


                    String key = this.getRef(position).getKey();
                    Log.d(TAG, "MessageKey:" + key);

                    //Armazenando msgKey para nao criar uma notificacao para ela
                    NotificationHandler.keys_messagesAlreadyReceived.add(key);


                    mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                    viewHolder.messageTextView.setText(friendlyMessage.getText());
                    viewHolder.messengerTextView.setText(friendlyMessage.getNameSender());
                    if (friendlyMessage.getPhotoUrl() == null) {
                        viewHolder.messengerImageView
                                .setImageDrawable(ContextCompat
                                        .getDrawable(getActivity(),
                                                R.drawable.ic_account_circle_black_36dp));
                    } else {
                        Glide.with(getActivity())
                                .load(friendlyMessage.getPhotoUrl())
                                .into(viewHolder.messengerImageView);
                    }
                }
            };


            //AQUI NAO TEM HAVER COM POPULAR O ADAPTER DE ACORDO COM O BANCO MAS SIM
            // EM COMO O ADPATER VAI SE COMPORTAR QD O USUARIO TIVER SCROLLING E TAL

            //Register a new observer to listen for data changes
            //RecyclerView.AdapterDataObserver -> Observer base class for watching changes to an RecyclerView.Adapter
            mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    super.onItemRangeInserted(positionStart, itemCount);
                    int friendlyMessageCount = mFirebaseAdapter.getItemCount();
                    int lastVisiblePosition =
                            mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                    // If the recycler view is initially being loaded or the
                    // user is at the bottom of the list, scroll to the bottom
                    // of the list to show the newly added message.
                    if (lastVisiblePosition == -1 ||
                            (positionStart >= (friendlyMessageCount - 1) &&
                                    lastVisiblePosition == (positionStart - 1))) {
                        mMessageRecyclerView.scrollToPosition(positionStart);
                    }
                }
            });

            mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
            //NAO ESQUECE DE SETAR O ADAPTER!!
            mMessageRecyclerView.setAdapter(mFirebaseAdapter);


        }


        //PEGANDO CONTEUDO Q FOI DIGITADO PARA SER ENVIADO
        mMessageEditText = (EditText) rootView.findViewById(R.id.messageEditText);
        //mSendButton.setEnabled(true);

        mSendButton = (Button) rootView.findViewById(R.id.sendButton);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessageFirebase(mMessageEditText.getText().toString(),
                        mUsername,
                        mPhotoUrl);
            }
        });


        // ANTES DE RETORNAR EU AVISO PARA O HOMEACTIVITY Q ACABAMOS DE CRIAR A VIEW DE TODOS OS FRAGMENTS
        // E ASSIM ELE PODE CHAMAR O GETCHATS()
        //mListener.fragmentCreated();
        //getChatsUser();

        if(!CREATED_FOR_THE_FIRST_TIME){
            // Avisa para o HomeActivity que o fragment acabou de ser criado
            createdFragListener.onFragmentCreated(this.getClass().getSimpleName());

            // Seta para true para que ele nao avise mais ao HomeActivity que foi criado e assim
            // nao execute o onFragmentCreated no HomeActivity
            CREATED_FOR_THE_FIRST_TIME = true;
        }

        return rootView;
    }


//    public void getChatsUser(){
//
//
////        String pacienteUsername = "";
////        try {
////            pacienteUsername = userLoggedIn.getDjangoUser().getJSONObject("user").getString("username")
////        } catch (JSONException e) {
////            e.printStackTrace();
////        }
//
//        DatabaseReference chatUsersRef = mFirebaseDatabaseReference.child("chatUsers");
//        ValueEventListener chatListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//
//                String pacienteUsername = "None";
//
//                try {
//                    if(userLoggedIn.getDjangoUser() != null)
//                        pacienteUsername = userLoggedIn.getDjangoUser().getJSONObject("user").getString("username");
//                    else
//                        Log.d(TAG, "DJANGO USER EH NULL ");
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                if(user != null) {
//                    pacienteUsername = user.getDisplayName();
//                    Log.d(TAG, "TEM FIREBASE USER NO GETCHAT ");
//                }else {
//                    Log.d(TAG, "NAO TEM FIREBASE USER NO GETCHAT");
//                }
//
//
//                // IRA ARMAZENAR NO ARRAYLIST OS CHATS EM QUE O PACIENTE PARTICIPA
//                // PARA DPS SALVAR NO USERLOGGEDIN
//                ArrayList<ChatFirebase> chatsDoPaciente = new ArrayList<>();
//
//                for (DataSnapshot chatUsersSnapshot: dataSnapshot.getChildren()) {
//                    // TODO: handle the post
//                    if(chatUsersSnapshot.hasChild(pacienteUsername)){
//
//                        Object chat = chatUsersSnapshot.getValue();
//                        HashMap<String, Boolean> chatParticipantes = (HashMap<String, Boolean>) chat;
//                        ArrayList<String> participantes = new ArrayList<>();
//                        for(String key : chatParticipantes.keySet()){
//                            participantes.add(key);
//                        }
//                        String chatID = chatUsersSnapshot.getKey();
//                        Log.d(TAG, "CHAT COM PACIENTE " + chat.toString());
//                        Log.d(TAG, "CHATID " + chatID);
//
//                        ChatFirebase chatPaciente = new ChatFirebase(chatID, participantes);
//                        chatsDoPaciente.add(chatPaciente);
//
//                    }
//                }
//
//                userLoggedIn.setChatsFirebase(chatsDoPaciente);
//                // AVISANDO QUE PEGOUS OS CHATS PARA QUE O COACHCHATFRAG POSSA ATUALIZAR A VIEW
//                coachChatInterface.receivedChatFirebase();
//                grupoInterface.receivedChatFirebase();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                // Getting Post failed, log a message
//                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
//                // ...
//            }
//        };
//
//        chatUsersRef.addListenerForSingleValueEvent(chatListener);
//    }

    @Override
    public void receivedChatFirebase() {

        //Toast.makeText(getActivity(), "ATUALIZANDO GRUPO", Toast.LENGTH_LONG).show();

        userLoggedIn = UserLoggedIn.getUserLoggedIn(getContext());

        // VOU PEGAR OS CHATS DO PACIENTE PARA PEGAR O ID E CARREGAR AS MENSAGENS
        // VAI RETORNAR UMA LISTA ENTAO VAMOS PEGAR SO O PRIMEIRO CHAT AGORA
        ArrayList<ChatFirebase> chatsPaciente = userLoggedIn.getChatsFirebase();

        // SE A QTD DE CHATS DO PACIENTE FOR MAIOR QUE UM ENTAO ELE TA EM UM GRUPO
        if(chatsPaciente.size() > 1) {
            //String chatID = chatsPaciente.get(0).getChatID();

            String chatGrupoID = null;
            // VOU FAZER UM FOR PELAS CONVERSAS DA PESSOA PARA SABER QUAL DELAS EH DE GRUPO
            // SE A QTD DE PARTICIPANTES FOR MAIOR QUE 2 ENTAO EH UMA CONVERSA DE GRUPO
            for (int i = 0; i < chatsPaciente.size(); i++) {
                if (chatsPaciente.get(i).getParticipantes().size() > 2) {
                    chatGrupoID = chatsPaciente.get(i).getChatID();
                }
            }


            if (chatGrupoID != null) {

                Log.d(TAG, "SETANDO O MESSAGES_CHILD: ");
                MESSAGES_CHILD = "chatMessages/"+chatGrupoID;


                mFirebaseAdapter = new FirebaseRecyclerAdapter<Message,
                        MessageViewHolder>(
                        Message.class,
                        R.layout.item_message,
                        MessageViewHolder.class,
                        mFirebaseDatabaseReference.child(MESSAGES_CHILD)) {

                    @Override
                    protected void populateViewHolder(MessageViewHolder viewHolder,
                                                      Message friendlyMessage, int position) {


                        String key = this.getRef(position).getKey();
                        Log.d(TAG, "MessageKey:" + key);

                        //Armazenando msgKey para nao criar uma notificacao para ela
                        NotificationHandler.keys_messagesAlreadyReceived.add(key);


                        mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                        viewHolder.messageTextView.setText(friendlyMessage.getText());
                        viewHolder.messengerTextView.setText(friendlyMessage.getNameSender());
                        if (friendlyMessage.getPhotoUrl() == null) {
                            viewHolder.messengerImageView
                                    .setImageDrawable(ContextCompat
                                            .getDrawable(getActivity(),
                                                    R.drawable.ic_account_circle_black_36dp));
                        } else {
                            Glide.with(getActivity())
                                    .load(friendlyMessage.getPhotoUrl())
                                    .into(viewHolder.messengerImageView);
                        }
                    }
                };

                Toast.makeText(getContext(),"Atualizando chat", Toast.LENGTH_SHORT).show();

                //AQUI NAO TEM HAVER COM POPULAR O ADAPTER DE ACORDO COM O BANCO MAS SIM
                // EM COMO O ADPATER VAI SE COMPORTAR QD O USUARIO TIVER SCROLLING E TAL

                //Register a new observer to listen for data changes
                //RecyclerView.AdapterDataObserver -> Observer base class for watching changes to an RecyclerView.Adapter
                mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                    @Override
                    public void onItemRangeInserted(int positionStart, int itemCount) {
                        super.onItemRangeInserted(positionStart, itemCount);
                        int friendlyMessageCount = mFirebaseAdapter.getItemCount();
                        int lastVisiblePosition =
                                mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                        // If the recycler view is initially being loaded or the
                        // user is at the bottom of the list, scroll to the bottom
                        // of the list to show the newly added message.
                        if (lastVisiblePosition == -1 ||
                                (positionStart >= (friendlyMessageCount - 1) &&
                                        lastVisiblePosition == (positionStart - 1))) {
                            mMessageRecyclerView.scrollToPosition(positionStart);
                        }
                    }
                });

                mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
                //NAO ESQUECE DE SETAR O ADAPTER!!
                mMessageRecyclerView.setAdapter(mFirebaseAdapter);

            }
        }

    }

    public void sendMessageFirebase(String messageText, String username, String photoUrl){

        FirebaseUser firebaseUser = userLoggedIn.getCurrentFirebaseUser();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String firebaseUID = user.getUid();

        Message friendlyMessage = new Message(messageText, username, photoUrl, firebaseUID);
        DatabaseReference ref_addedChild = mFirebaseDatabaseReference.child(MESSAGES_CHILD).push();

        // http://stackoverflow.com/questions/37094631/get-the-pushed-id-for-specific-value-in-firebase-android
        // A terceira reposta resolveu o problema de que tava pegando uniqueID diferentes

        //Pegando a uniqueID da msg pra ser parametro da Notification e ver se tem q criar ela ou nao
        String messageKey = ref_addedChild.getKey();
        //Salvando no banco
        ref_addedChild.setValue(friendlyMessage);
        Log.d(TAG, "messageKey SEND:" + messageKey);


        // Agora eu vou atualizar a arvore /chats/ setando o last_message

        DatabaseReference chatRef = mFirebaseDatabaseReference.child(CHATS_CHILD);

        //Pegando o chatID
        String chatID = MESSAGES_CHILD.split("/")[1];
        Log.d(TAG, "Chat reference ID:" + chatID);

        LastMessageInfo lastMessageInfo = new LastMessageInfo(messageText, username);

        DatabaseReference chatPacRef = chatRef.child(chatID);

        chatPacRef.child("last_Message").setValue(lastMessageInfo);


        //Tentando mandar a notification pro banco
        //ERA PRA SER O USERNAME DE QUEM TA RECEBENDO MAS COMO GAMBIARRA(JA Q NAO TO FAZENDO CHAT POR TOPIC) TO MANDANDO O USER QUE ENVIA
        sendNotificationToUser(username, messageText, messageKey);
        mMessageEditText.setText("");
    }


    public void sendNotificationToUser(String user, String message, String messageKey) {

        Map notification = new HashMap<>();
        notification.put("username", user);
        notification.put("message", message);
        notification.put("messageKey", messageKey);

        //Gambiarra pra ele nao receber notificacao dele mesmo
        NotificationHandler.current_user = user;

        mFirebaseDatabaseReference.child(NOTIFICATIONS_CHILD).push().setValue(notification);
    }



    @Override
    public void onStart() {
        super.onStart();
        //mFirebaseAuth.addAuthStateListener(mAuthListener);
        //mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mAuthListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthListener);
        }

//        if(mGoogleApiClient.isConnected()){
//            mGoogleApiClient.disconnect();
//        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ForegroundHandler.activityResumed();
    }

    @Override
    public void onPause() {
        super.onPause();
        ForegroundHandler.activityPaused();
    }

//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }
//
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // PEGANDO O LISTENER A PARTIR DO CONTEXT DO HOMEACTIVITY
        if (context instanceof Interfaces.OnFragmentCreatedListener) {
            createdFragListener = (Interfaces.OnFragmentCreatedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        createdFragListener = null;
    }
//
//    /**
//     * This interface must be implemented by activities that contain this
//     * fragment to allow an interaction in this fragment to be communicated
//     * to the activity and potentially other fragments contained in that
//     * activity.
//     * <p>
//     * See the Android Training lesson <a href=
//     * "http://developer.android.com/training/basics/fragments/communicating.html"
//     * >Communicating with Other Fragments</a> for more information.
//     */
//    public interface OnFragmentCreated {
//        // TODO: Update argument type and name
//        void fragmentCreated();
//    }
}
