package chron.carlosrafael.chatapp.Fragmentos;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import chron.carlosrafael.chatapp.ForegroundHandler;
import chron.carlosrafael.chatapp.MainActivity;
import chron.carlosrafael.chatapp.Message;
import chron.carlosrafael.chatapp.MessageViewHolder;
import chron.carlosrafael.chatapp.Models.Chat;
import chron.carlosrafael.chatapp.NotificationHandler;
import chron.carlosrafael.chatapp.R;
import chron.carlosrafael.chatapp.SignInActivity;
import chron.carlosrafael.chatapp.UserLoggedIn;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 *
 * to handle interaction events.
 * Use the {@link CoachChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CoachChatFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    //Variables for the GoogleAPIService course to keep track of the sign in status
    private int mSignInProgress;
    private static final int STATE_SIGNED_IN = 0;
    private static final int STATE_SIGN_IN = 1;
    private static final int STATE_PROGRESS = 2;

    //Usado pra ele so pegar uma instancia e nao dar o erro no setPersistenceEnabled()
    private static boolean firebaseAccessed = false;


    private static final String TAG = "MainActivity";
    public static final String MESSAGES_CHILD = "chatMessages/c14p18";
    public static final String CHATS_CHILD = "chats";
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

    private GoogleApiClient mGoogleApiClient;

    private String mUsername;
    private String mPhotoUrl;


    private UserLoggedIn userLoggedIn;

    public static final String BASE_URL = "http://54.202.76.189:8000/";

    //private OnFragmentInteractionListener mListener;

    public CoachChatFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CoachChatFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CoachChatFragment newInstance(String param1, String param2) {
        CoachChatFragment fragment = new CoachChatFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_coach_chat, container, false);

        userLoggedIn = UserLoggedIn.getUserLoggedIn(getContext());

//        Log.d(TAG, "Chamando Task: ");
//        GetChatsTask getChatsTask = new GetChatsTask(getActivity());
//        //getChatsTask.execute();
//        getChatsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


        //Primeiro vai ver se foi aberto por causa de um Intent
        //Se for ele checa se esse intent veio da Notification
        //Se tiver vindo ele para de acumular as mensagens da BigStyle Notification
        Intent intent = getActivity().getIntent();
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


        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .enableAutoManage(getActivity() /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        // Initialize ProgressBar and RecyclerView.
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        mMessageRecyclerView = (RecyclerView) rootView.findViewById(R.id.messageRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(getContext());
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

//        //TENTANDO SUBSCREVER PARA O TOPIC DA CONVERSA
//        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_NAME);


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


        userInfoTxtView = (TextView) rootView.findViewById(R.id.userInfoTxtView);

        // Pegando algumas infos do UserLoggedIn para mostrar no chat e testar
        JSONObject user = userLoggedIn.getDjangoUser();
        if(user != null){
            try {
                String username = user.getJSONObject("user").getString("username");
                String pacienteId = user.getJSONObject("user").getString("pacienteId");

                userInfoTxtView.setText(username+" com id: "+pacienteId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        return rootView;
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
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and a connection to Google APIs
        // could not be established. Display an error message, or handle
        // the failure silently
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(getContext(), "Google Play Services error.", Toast.LENGTH_SHORT).show();

    }

//    @Override
//    public void onConnectionSuspended(int cause) {
//        mGoogleApiClient.connect();
//        Log.d(TAG, "onConnectionSuspended:" + cause);
//
//    }

    @Override
    public void onStart() {
        super.onStart();
        //mFirebaseAuth.addAuthStateListener(mAuthListener);
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mAuthListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthListener);
        }

        if(mGoogleApiClient.isConnected()){
            mGoogleApiClient.disconnect();
        }
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

//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }


    public class GetChatsTask extends AsyncTask<String, Void, Void> {

        public Activity callingActivity;

        public GetChatsTask(Activity activity) {
            this.callingActivity = activity;
        }

        @Override
        protected Void doInBackground(String... params) {

            // TODO: attempt authentication against a network service.

            if (params.length == 0) {
                return null;
            }
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            Log.d(TAG, "Background do Task: ");

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String serverResponseStr = null;

            try {

                String pacienteUsername = "";
                try {
                    pacienteUsername = userLoggedIn.getDjangoUser().getJSONObject("user").getString("username");
                    Log.d(TAG, "PacienteUsername: " + pacienteUsername);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                URL url = new URL(BASE_URL + "api/chats/paciente/" + pacienteUsername);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream;
                StringBuffer buffer;

                int httpResponseCode = urlConnection.getResponseCode();
                if (httpResponseCode == 200) {
                    inputStream = urlConnection.getInputStream();
                    buffer = readBuffer(reader, inputStream);
                } else {
                    //Toast.makeText(getApplicationContext(), "OLha a merda pai, "+httpResponseCode,Toast.LENGTH_SHORT).show();
                    Log.v("BRONCA", "REsponse code: " + httpResponseCode);
                    InputStream errorInputStream = urlConnection.getErrorStream();
                    buffer = readBuffer(reader, errorInputStream);
                }

                serverResponseStr = buffer.toString();

                Log.v(TAG, "Server Response JSON String: " + serverResponseStr);

                //Chegou a resposta do servidor como String mas agora tem que transformar em JSON e criar os Objetos Activities

                try {
                    JSONArray jsonArrayResponse = new JSONArray(serverResponseStr);

                    // Recebi um Json com todos os chats que o user esta participando
                    // Vou transformar no objeto Chat para facilitar manipulacao
                    for (int i = 0; i < jsonArrayResponse.length(); i++) {
                        JSONObject chatJson = jsonArrayResponse.getJSONObject(i);
                        String chatStr = chatJson.toString();

                        Gson gson = new Gson();
                        JsonElement chatJsonElem = gson.fromJson(chatStr, JsonElement.class);

                        Chat chat = gson.fromJson(chatJsonElem, Chat.class);

                    }

                    //userLoggedIn.setDjangoUser(jsonResponse);
                    //Toast.makeText(getApplicationContext(), jsonResponse.getJSONObject("user").getString("username"), Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("My App", "Could not parse malformed JSON: \"" + serverResponseStr + "\"");
                }

            } catch (IOException e) {

                Log.e("FUDEU", "Error ", e);

                // If the code didn't successfully get the weather data, there's no point in attemping

                // to parse it.

                return null;

            } finally {

                if (urlConnection != null) {

                    urlConnection.disconnect();

                }

                if (reader != null) {

                    try {

                        reader.close();

                    } catch (final IOException e) {

                        //Log.e(LOG_TAG, "Error closing stream", e);

                        e.printStackTrace();

                    }

                }

            }

            //return "Valeu";
            return null;

        }

//        @Override
//        protected void onPostExecute(LoginParams paramsToFirebase) {
//            super.onPostExecute(paramsToFirebase);
////            Toast.makeText(getApplicationContext(), serverResponse, Toast.LENGTH_LONG).show();
////            startActivity(new Intent(SignInActivity.this, MainActivity.class));
//            FirebaseLogin firebaseLogin = new FirebaseLogin(this.callingActivity);
//            Log.v(TAG, "Mandando para a segunda thread");
//            firebaseLogin.execute(paramsToFirebase);
//
//        }


    }


    private String getPostDataString(ContentValues loginValues) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (Map.Entry<String, Object> entry : loginValues.valueSet()) {
            String key = entry.getKey(); // name
            String value = entry.getValue().toString(); // value

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
        }

        return result.toString();
    }


    public StringBuffer readBuffer(BufferedReader reader, InputStream inputStream) {


        StringBuffer buffer = new StringBuffer();

        if (inputStream == null) {

            // Nothing to do.

            return null;

        }

        reader = new BufferedReader(new InputStreamReader(inputStream));


        String line;

        try {

            while ((line = reader.readLine()) != null) {

                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)

                // But it does make debugging a *lot* easier if you print out the completed

                // buffer for debugging.

                buffer.append(line + "\n");

            }

        } catch (IOException e) {

            e.printStackTrace();

        }


        if (buffer.length() == 0) {

            // Stream was empty.  No point in parsing.

            return null;

        }


        return buffer;

    }
}


@IgnoreExtraProperties
class LastMessageInfo{
    public String text;
    public String nameSender;

    public LastMessageInfo(){

    }

    public LastMessageInfo(String text, String nameSender){
        this.text = text;
        this.nameSender = nameSender;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getNameSender() {
        return nameSender;
    }

    public void setNameSender(String nameSender) {
        this.nameSender = nameSender;
    }
}
