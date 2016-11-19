package chron.carlosrafael.chatapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks{
//You may also specify an optional implementation for the ConnectionCallbacks interface if your app needs to know when the automatically managed connection is established or suspended

    //Variables for the GoogleAPIService course to keep track of the sign in status
    private int mSignInProgress;
    private static final int STATE_SIGNED_IN = 0;
    private static final int STATE_SIGN_IN = 1;
    private static final int STATE_PROGRESS = 2;

    //Usado pra ele so pegar uma instancia e nao dar o erro no setPersistenceEnabled()
    private static boolean firebaseAccessed = false;


    private static final String TAG = "MainActivity";
    public static final String MESSAGES_CHILD = "messages";
    public static final String NOTIFICATIONS_CHILD = "notificationRequests";
    private static final int REQUEST_INVITE = 1;
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 10;
    public static final String ANONYMOUS = "anonymous";
    private static final String MESSAGE_SENT_EVENT = "message_sent";
    public static final String TOPIC_NAME = "/topics/chat1_the_eu";

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

    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    //    Initialize the Firebase Realtime Database and add a listener to handle changes made to the data. Update the RecyclerView adapter so new messages will be shown.*/
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<Message, MessageViewHolder>
            mFirebaseAdapter;

    private GoogleApiClient mGoogleApiClient;

    private String mUsername;
    private String mPhotoUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Primeiro vai ver se foi aberto por causa de um Intent
        //Se for ele checa se esse intent veio da Notification
        //Se tiver vindo ele para de acumular as mensagens da BigStyle Notification
        Intent intent = getIntent();
        if(intent != null && intent.getExtras() != null){
            Log.d(TAG, "Identificou Intent!");
            if(intent.getBooleanExtra("fromNotification", false)){
                NotificationHandler.emptyNotificationStack();
                Log.d(TAG, "Era pra ter limpado!");
            }
        }

        //TODA VEZ QUE ELE ENTRAR ELE VAI LIMPAR O NotificationHandler.keys_messagesAlreadyReceived
        // PARA NAO FICAR ACUMULANDO AS KEYS DAS MSGS Q CHEGARAM
        NotificationHandler.keys_messagesAlreadyReceived.clear();
        Log.d(TAG, "NotificationList ZEROU: " + NotificationHandler.keys_messagesAlreadyReceived.size());


        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        } else {
            mUsername = mFirebaseUser.getDisplayName();
            if (mFirebaseUser.getPhotoUrl() != null) {
                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            }
        }


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        // Initialize ProgressBar and RecyclerView.
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mMessageRecyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);



        //Calls to setPersistenceEnabled() must be made before any other usage of FirebaseDatabase instance
        //Tentando setar pra que ele possa armazenar as mensagens localmente
        if(!firebaseAccessed){
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            firebaseAccessed = true;
        }


        // New child entries
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        //TENTANDO SUBSCREVER PARA O TOPIC DA CONVERSA
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_NAME);


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
                viewHolder.messengerTextView.setText(friendlyMessage.getName());
                if (friendlyMessage.getPhotoUrl() == null) {
                    viewHolder.messengerImageView
                            .setImageDrawable(ContextCompat
                                    .getDrawable(MainActivity.this,
                                            R.drawable.ic_account_circle_black_36dp));
                } else {
                    Glide.with(MainActivity.this)
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




        //PEGANDO CONTEUDO Q FOI DIGITADO PARA SER ENVIADO
        mMessageEditText = (EditText) findViewById(R.id.messageEditText);
        //mSendButton.setEnabled(true);

        mSendButton = (Button) findViewById(R.id.sendButton);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessageFirebase(mMessageEditText.getText().toString(),
                        mUsername,
                        mPhotoUrl);
            }
        });



    }



    public void sendMessageFirebase(String messageText, String username, String photoUrl){

        Message friendlyMessage = new Message(messageText, username, photoUrl);
        DatabaseReference ref_addedChild = mFirebaseDatabaseReference.child(MESSAGES_CHILD).push();

        // http://stackoverflow.com/questions/37094631/get-the-pushed-id-for-specific-value-in-firebase-android
        // A terceira reposta resolveu o problema de que tava pegando uniqueID diferentes

        //Pegando a uniqueID da msg pra ser parametro da Notification e ver se tem q criar ela ou nao
        String messageKey = ref_addedChild.getKey();
        //Salvando no banco
        ref_addedChild.setValue(friendlyMessage);
        Log.d(TAG, "messageKey SEND:" + messageKey);

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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                mFirebaseAuth.signOut();
                //Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                mUsername = ANONYMOUS;
                startActivity(new Intent(this, SignInActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and a connection to Google APIs
        // could not be established. Display an error message, or handle
        // the failure silently
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();

    }

//    @Override
//    public void onConnectionSuspended(int cause) {
//        mGoogleApiClient.connect();
//        Log.d(TAG, "onConnectionSuspended:" + cause);
//
//    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(mGoogleApiClient.isConnected()){
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ForegroundHandler.activityResumed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ForegroundHandler.activityPaused();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected:" );

        mSignInProgress = STATE_SIGNED_IN;
    }

    @Override
    public void onConnectionSuspended(int cause) {
        mGoogleApiClient.connect();
        Log.d(TAG, "onConnectionSuspended:" + cause);
    }
}
