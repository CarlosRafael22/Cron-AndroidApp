package chron.carlosrafael.chatapp;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chron.carlosrafael.chatapp.Fragmentos.CoachChatFragment;
import chron.carlosrafael.chatapp.Fragmentos.GrupoFragment;
import chron.carlosrafael.chatapp.Fragmentos.HomeFragment;
import chron.carlosrafael.chatapp.Fragmentos.ReceitaFragment;
import chron.carlosrafael.chatapp.Fragmentos.dummy.DummyContent;
import chron.carlosrafael.chatapp.Models.Chat;
import chron.carlosrafael.chatapp.Models.ChatFirebase;
import chron.carlosrafael.chatapp.Models.Receita;

import static chron.carlosrafael.chatapp.HomeActivity.BASE_URL;

public class HomeActivity extends AppCompatActivity implements HomeFragment.OnFragmentInteractionListener, ReceitaFragment.OnListFragmentInteractionListener, Interfaces.OnFragmentCreatedListener{

    private static final String TAG = "HomeActivity";

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the
     * three primary sections of the app. We use a {@link android.support.v4.app.FragmentPagerAdapter}
     * derivative, which will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    ViewPagerAdapter mViewPagerAdapter;

    /**
     * The {@link ViewPager} that will display the three primary sections of the app, one at a
     * time.
     */
    ViewPager mViewPager;

    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mFirebaseDatabaseReference;

    //Usado pra ele so pegar uma instancia e nao dar o erro no setPersistenceEnabled()
    private static boolean firebaseAccessed = false;

    private UserLoggedIn userLoggedIn;

    ChatFirebaseReceivedInterface coachChatInterface;
    ChatFirebaseReceivedInterface grupoInterface;

    // Mantem a conta de quantos Fragments foram criados para que ao carregar todos ele pega Chats
    private static int FRAGMENTS_TO_BE_CREATED;
    private static int FRAGMENTS_CREATED = 0;

    public static final String BASE_URL = "http://54.202.76.189:8000/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Calls to setPersistenceEnabled() must be made before any other usage of FirebaseDatabase instance
        //Tentando setar pra que ele possa armazenar as mensagens localmente
        if(!firebaseAccessed){
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            firebaseAccessed = true;
        }

//        chatsInterface = new ChatFirebaseReceivedInterface() {
//            @Override
//            public void receivedChatFirebase() {
//
//            }
//        };

        final ActionBar actionBar = getSupportActionBar();

        // Create the adapter that will return a fragment for each of the three primary sections
        // of the app.
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.viewpager);

        // use a number higher than half your fragments.
        // Set the number of pages that should be retained to either side of the current page in the view hierarchy in an idle state.
        mViewPager.setOffscreenPageLimit(3);
        // Usando para que ele carregue todos os fragments de uma vez e nao tenha q esperar carregar os chats

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new HomeFragment(), "Home");
        adapter.addFragment(new ReceitaFragment(), "Receita");
        CoachChatFragment coachChatFragment = new CoachChatFragment();
        adapter.addFragment(coachChatFragment, "Coach Chat");
        GrupoFragment grupoFragment = new GrupoFragment();
        adapter.addFragment(grupoFragment, "Grupo");

        // Setando a qtd de fragments do HomeActivity
        FRAGMENTS_TO_BE_CREATED = adapter.getCount();

        mViewPager.setAdapter(adapter);

        // COMO COACHCHATFRAGMENT IMPLEMENTA CHATRECEIVEDINTERFACE ENTAO INICIALIZAMOS O CHATSINTERFACE ASSIM
        coachChatInterface = coachChatFragment;
        grupoInterface = grupoFragment;


        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        // When swiping between pages, select the
                        // corresponding tab.
                        getSupportActionBar().setSelectedNavigationItem(position);
                    }
                });


        // Specify that tabs should be displayed in the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create a tab listener that is called when the user changes tabs.
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                // show the given tab
                // When the tab is selected, switch to the
                // corresponding page in the ViewPager.
                mViewPager.setCurrentItem(tab.getPosition());
            }

            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
                // hide the given tab
            }

            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
                // probably ignore this event
            }
        };

        // Add 3 tabs, specifying the tab's text and TabListener
        // Create tabs
        actionBar.addTab(actionBar.newTab().setTag("Home").setText("Home").setTabListener(tabListener));
        actionBar.addTab(actionBar.newTab().setTag("Receita").setText("Receita").setTabListener(tabListener));
        actionBar.addTab(actionBar.newTab().setTag("Coach Chat").setText("Coach Chat").setTabListener(tabListener));
        actionBar.addTab(actionBar.newTab().setTag("Grupo").setText("Grupo").setTabListener(tabListener));


        // DPS QUE SETOU TODA A VIEW A GNT LIDA COM O RESTO
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        userLoggedIn = UserLoggedIn.getUserLoggedIn(getApplicationContext());
        if (userLoggedIn.getDjangoUser() != null) {
            try {
                Log.v(TAG, userLoggedIn.toString());
                Toast.makeText(getApplicationContext(), userLoggedIn.getDjangoUser().getJSONObject("user").getString("username"), Toast.LENGTH_LONG).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        Log.d(TAG, "FirebaseUser: " + mFirebaseUser);
        Log.d(TAG, "FirebaseUser UserloggedIn: " + userLoggedIn.getCurrentFirebaseUser());
        Log.d(TAG, "UserloggedIn: " + userLoggedIn.getDjangoUser());
        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            //return;
        } else {
//            mUsername = mFirebaseUser.getDisplayName();
//            if (mFirebaseUser.getPhotoUrl() != null) {
//                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
//            }
            Log.d(TAG, "Chamando Chat TASK: ");
//            GetChatsTask getChatsTask = new GetChatsTask(this);
//            getChatsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            JSONObject json = userLoggedIn.getDjangoUser();
//            // VOU PEGAR OS CHATS DO PACIENTE PELO FIREBASE JA QUE COM A THREAD ESTA DANDO PROBLEMA
//            getChatsUser();
            Log.d(TAG, "Ta logado no HomeActivity: ");
        }

//         Log.d(TAG, "Chamando Task: ");
//        GetChatsTask getChatsTask = new GetChatsTask(this);
//        //getChatsTask.execute();
//        getChatsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                SignOutUser();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void SignOutUser() {

        mFirebaseAuth.getInstance().signOut();

        // Atualizando o Objeto que colocamos as infos do usuario logado atualmente
        userLoggedIn.setDjangoUser(null);
        userLoggedIn.setCurrentFirebaseUser(null);
        Log.d(TAG, "User loggedOut: " + userLoggedIn.getCurrentFirebaseUser());

        startActivity(new Intent(HomeActivity.this, SignInActivity.class));
        finish();

    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        // The user selected the headline of an article from the HeadlinesFragment
        // Do something here to display that article
        Toast.makeText(getApplicationContext(), uri.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFragmentCreated(String fragmentName) {

        Log.d(TAG, "Fragment criado: " + fragmentName);
        FRAGMENTS_CREATED++;

        // Se ja criou todos os fragments entao a gnt manda pegar os chats
        // Vou pedir pra ele pegar os chats qd ja tiver carregado duas views, a Home e o CoachChat
        // ja que ele so cria o GrupoFrag qd ja estivermos no CoachChat. Com isso a gnt ainda tem q esperar carregar
        if(FRAGMENTS_CREATED == FRAGMENTS_TO_BE_CREATED){
            getChatsUser();
        }

    }

    @Override
    public void onListFragmentInteraction(Receita receita) {
        Toast.makeText(getApplicationContext(),receita.getNome_receita(), Toast.LENGTH_SHORT).show();

        // Transformando o Objeto Receita em Json para dps passar para a Activity
        Gson receitaGson = new Gson();
        String receitaJson = receitaGson.toJson(receita);

        Intent intent = new Intent(this, ReceitaActivity.class);
        intent.putExtra("receita", receitaJson);
        startActivity(intent);
    }

//    @Override
//    public void fragmentCreated() {
//
//        Log.d(TAG, "TERMINOU OS FRAGMENTS, VOU PEGAR OS CHATS");
//        // VOU PEGAR OS CHATS DO PACIENTE PELO FIREBASE JA QUE COM A THREAD ESTA DANDO PROBLEMA
//        getChatsUser();
//    }

//    @Override
//    public void receivedChatFirebase() {
//        // The user selected the headline of an article from the HeadlinesFragment
//        // Do something here to display that article
//        Toast.makeText(getApplicationContext(), "Recebi os chats", Toast.LENGTH_SHORT).show();
//    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

    }


    public void getChatsUser(){


//        String pacienteUsername = "";
//        try {
//            pacienteUsername = userLoggedIn.getDjangoUser().getJSONObject("user").getString("username")
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        DatabaseReference chatUsersRef = mFirebaseDatabaseReference.child("chatUsers");
        ValueEventListener chatListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                String pacienteUsername = "None";

                try {
                    if(userLoggedIn.getDjangoUser() != null)
                        pacienteUsername = userLoggedIn.getDjangoUser().getJSONObject("user").getString("username");
                    else
                        Log.d(TAG, "DJANGO USER EH NULL ");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user != null) {
                    pacienteUsername = user.getDisplayName();
                    Log.d(TAG, "TEM FIREBASE USER NO GETCHAT ");
                }else {
                    Log.d(TAG, "NAO TEM FIREBASE USER NO GETCHAT");
                }


                // IRA ARMAZENAR NO ARRAYLIST OS CHATS EM QUE O PACIENTE PARTICIPA
                // PARA DPS SALVAR NO USERLOGGEDIN
                ArrayList<ChatFirebase> chatsDoPaciente = new ArrayList<>();

                for (DataSnapshot chatUsersSnapshot: dataSnapshot.getChildren()) {
                    // TODO: handle the post
                    if(chatUsersSnapshot.hasChild(pacienteUsername)){

                        Object chat = chatUsersSnapshot.getValue();
                        HashMap<String, Boolean> chatParticipantes = (HashMap<String, Boolean>) chat;
                        ArrayList<String> participantes = new ArrayList<>();
                        for(String key : chatParticipantes.keySet()){
                            participantes.add(key);
                        }
                        String chatID = chatUsersSnapshot.getKey();
                        Log.d(TAG, "CHAT COM PACIENTE " + chat.toString());
                        Log.d(TAG, "CHATID " + chatID);

                        ChatFirebase chatPaciente = new ChatFirebase(chatID, participantes);
                        chatsDoPaciente.add(chatPaciente);

                    }
                }

                userLoggedIn.setChatsFirebase(chatsDoPaciente);
                // AVISANDO QUE PEGOUS OS CHATS PARA QUE O COACHCHATFRAG POSSA ATUALIZAR A VIEW
                coachChatInterface.receivedChatFirebase();
                grupoInterface.receivedChatFirebase();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };

        chatUsersRef.addListenerForSingleValueEvent(chatListener);
    }




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
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if(user != null){
                        pacienteUsername = user.getDisplayName();
                    }
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

    public interface ChatFirebaseReceivedInterface{
        void receivedChatFirebase();
    }
}



