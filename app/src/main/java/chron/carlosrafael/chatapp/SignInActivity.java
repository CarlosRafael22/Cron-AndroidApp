package chron.carlosrafael.chatapp;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
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
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executor;

import javax.net.ssl.HttpsURLConnection;

import chron.carlosrafael.chatapp.Models.Chat;

public class SignInActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;

    private SignInButton signInButton;
    private EditText usermailOrEmailEditText;
    private EditText passwordEditText;
    private Button loginLocalButton;


    //Vai ser usado pra Sign In nos servicos do Google -> no caso, logar pelo Google agora
    private GoogleApiClient mGoogleApiClient;

    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;

    private FirebaseAuth.AuthStateListener mAuthListener;

    //private static final String BASE_URL = "http://10.0.2.2:8000/";
    //private static final String BASE_URL = "http://192.168.25.4:8000/";
    private static final String BASE_URL = "http://54.202.76.189:8000/";

    private UserLoggedIn userLoggedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        userLoggedIn = UserLoggedIn.getUserLoggedIn(getApplicationContext());

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    Log.d(TAG, "VAI MUDAR ACTIVITY PELO LISTENER " + user.getUid());
                    // Mandando para a main Activity
                    startActivity(new Intent(SignInActivity.this, HomeActivity.class));
                    finish();
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };


        // 1 - Configure Google Sign In
        /* In your sign-in activity's onCreate method, configure Google Sign-In to request the user data required by your app.
        For example, to configure Google Sign-In to request users' ID and basic profile information, create a GoogleSignInOptions object with the DEFAULT_SIGN_IN parameter.
        To request users' email addresses as well, create the GoogleSignInOptions object with the requestEmail option.*/
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.default_web_client_id))
//                .requestEmail()
//                .build();
//        // 2 - Then, also in your sign-in activity's onCreate method, create a GoogleApiClient object with access to the Google Sign-In API and the options you specified.
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
//                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
//                .build();

        // Initialize FirebaseAuth
        mFirebaseAuth = FirebaseAuth.getInstance();


        // Pegando referencias de todos os UI elements
        usermailOrEmailEditText = (EditText) findViewById(R.id.usernameOrEmailEditText);

        passwordEditText = (EditText) findViewById(R.id.passwordEditText);

        loginLocalButton = (Button) findViewById(R.id.logInButton);

//        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
//
//        signInButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                signInGoogle();
//            }
//        });

        loginLocalButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String usernameOrEmail = usermailOrEmailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                Log.d("INPUTS", "inputs passados:" + password + " " + usernameOrEmail);
                signInLocal(usernameOrEmail, password);
            }
        });

    }


    private void signInLocal(String usernameOrEmail, String password) {

        LoginParams loginCred = new LoginParams(usernameOrEmail, password);

//        mFirebaseAuth.signInWithEmailAndPassword(usernameOrEmail, password)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
//                        Log.d(TAG, "FIREBASE:" + task);
//
//                        // If sign in fails, display a message to the user. If sign in succeeds
//                        // the auth state listener will be notified and logic to handle the
//                        // signed in user can be handled in the listener.
//                        if (!task.isSuccessful()) {
//                            Log.w(TAG, "signInWithEmail:failed", task.getException());
//                            Toast.makeText(SignInActivity.this, "Deu merda no signIn",
//                                    Toast.LENGTH_SHORT).show();
//                        } else {
//                            Toast.makeText(SignInActivity.this, "Entrou",
//                                    Toast.LENGTH_SHORT).show();
//                        }
//
//
//                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                        Log.d(TAG, "FirebaseUser: " + user);
//                        if (user != null) {
//                            // Name, email address, and profile photo Url
//                            String name = user.getDisplayName();
//                            String email = user.getEmail();
//                            Uri photoUrl = user.getPhotoUrl();
//
//                            // Check if user's email is verified
//                            boolean emailVerified = user.isEmailVerified();
//
//                            // The user's ID, unique to the Firebase project. Do NOT use this value to
//                            // authenticate with your backend server, if you have one. Use
//                            // FirebaseUser.getToken() instead.
//                            String uid = user.getUid();
//                        }
//
//                        userLoggedIn.setCurrentFirebaseUser(user);
//                        Log.d(TAG, "FirebaseUser UserloggedIn: " + userLoggedIn.getCurrentFirebaseUser());
//
//                    }
//                });

        LoginTask loginTask = new LoginTask(this);
        loginTask.execute(loginCred);

    }

//    //Starting the intent prompts the user to select a Google account to sign in with.
//    // If you requested scopes beyond profile, email, and openid, the user is also prompted to grant access to the requested resources.
//    private void signInGoogle() {
////        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
////        startActivityForResult(signInIntent, RC_SIGN_IN);
//        startActivity(new Intent(SignInActivity.this, HomeActivity.class));
//        finish();
//    }


//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        //In the activity's onActivityResult method, retrieve the sign-in result with getSignInResultFromIntent
//
//        super.onActivityResult(requestCode, resultCode, data);
//
//        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
//        if (requestCode == RC_SIGN_IN) {
//            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
//            handleSignInResult(result);
//        }
//    }


    //After you retrieve the sign-in result, you can check if sign-in succeeded with the isSuccess method.
    // If sign-in succeeded, you can call the getSignInAccount method to get a GoogleSignInAccount object that contains information about the signed-in user, such as the user's name.
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            //updateUI(true);

            // Google Sign In was successful, authenticate with Firebase
            GoogleSignInAccount account = result.getSignInAccount();
            firebaseAuthWithGoogle(account);
        } else {
            // Signed out, show unauthenticated UI.
            //updateUI(false);
            Log.e(TAG, "Google Sign In failed.");
        }
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGooogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(SignInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            startActivity(new Intent(SignInActivity.this, HomeActivity.class));
                            finish();
                        }
                    }
                });
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthListener);
        //mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mAuthListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthListener);
        }

    }


    private class LoginParams {

        String usernameOrEmail;
        String password;
        String serverResponse;

        LoginParams(String usernameOrEmail, String password) {
            this.usernameOrEmail = usernameOrEmail;
            this.password = password;
        }

        public String getServerResponse() {
            return serverResponse;
        }

        public void setServerResponse(String serverResponse) {
            this.serverResponse = serverResponse;
        }
    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * <p>
     * the user.
     */

    public class LoginTask extends AsyncTask<LoginParams, Void, LoginParams> {

        public Activity callingActivity;

        public LoginTask(Activity activity){
            this.callingActivity = activity;
        }

        @Override
        protected LoginParams doInBackground(LoginParams... params) {

            // TODO: attempt authentication against a network service.

            if (params.length == 0) {

                return null;

            }

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;


            // Will contain the raw JSON response as a string.
            String serverResponseStr = null;

            try {

                URL url = new URL(BASE_URL + "api-token-auth/");

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);

                ContentValues loginValues = new ContentValues();
                loginValues.put("email_or_username", params[0].usernameOrEmail);
                loginValues.put("password", params[0].password);

                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(loginValues));

                writer.flush();
                writer.close();
                os.close();

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
                    JSONObject jsonResponse = new JSONObject(serverResponseStr);
                    userLoggedIn.setDjangoUser(jsonResponse);
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

            LoginParams paramsToFirebase = new LoginParams(params[0].usernameOrEmail,params[0].password);
            paramsToFirebase.setServerResponse(serverResponseStr);
            return paramsToFirebase;


        }

        @Override
        protected void onPostExecute(LoginParams paramsToFirebase) {
            super.onPostExecute(paramsToFirebase);
//            Toast.makeText(getApplicationContext(), serverResponse, Toast.LENGTH_LONG).show();
//            startActivity(new Intent(SignInActivity.this, MainActivity.class));
            FirebaseLogin firebaseLogin = new FirebaseLogin(this.callingActivity);
            Log.v(TAG, "Mandando para a segunda thread");
            firebaseLogin.execute(paramsToFirebase);

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




    public class FirebaseLogin extends AsyncTask<LoginParams, Void, Boolean> {

        public Activity callingActivity;

        public FirebaseLogin(Activity activity){
            this.callingActivity = activity;
        }

        @Override
        protected Boolean doInBackground(LoginParams... params) {

            mFirebaseAuth.signInWithEmailAndPassword(params[0].usernameOrEmail, params[0].password)
                    .addOnCompleteListener(this.callingActivity, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                            Log.d(TAG, "FIREBASE:" + task);

                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                Log.w(TAG, "signInWithEmail:failed", task.getException());
                                Toast.makeText(SignInActivity.this, "Deu merda no signIn",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SignInActivity.this, "Entrou",
                                        Toast.LENGTH_SHORT).show();
                            }


                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            Log.d(TAG, "FirebaseThread FirebaseUser: " + user);
                            if (user != null) {
                                // Name, email address, and profile photo Url
                                String name = user.getDisplayName();
                                String email = user.getEmail();
                                Uri photoUrl = user.getPhotoUrl();

                                // Check if user's email is verified
                                boolean emailVerified = user.isEmailVerified();

                                // The user's ID, unique to the Firebase project. Do NOT use this value to
                                // authenticate with your backend server, if you have one. Use
                                // FirebaseUser.getToken() instead.
                                String uid = user.getUid();
                            }

                            userLoggedIn.setCurrentFirebaseUser(user);
                            Log.d(TAG, "FirebaseUser UserloggedIn: " + userLoggedIn.getCurrentFirebaseUser());

                        }
                    });

            return true;

        }

        @Override
        protected void onPostExecute(Boolean response) {
            Toast.makeText(getApplicationContext(), "Rodou a segunda thread", Toast.LENGTH_LONG).show();
//            GetChatsTask getChatsTask = new GetChatsTask(this.callingActivity);
//            getChatsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//            startActivity(new Intent(SignInActivity.this, HomeActivity.class));
//            finish();

        }

    }



    public class GetChatsTask extends AsyncTask<String, Void, Void> {

        public Activity callingActivity;

        public GetChatsTask(Activity activity) {
            this.callingActivity = activity;
            Log.d(TAG, "Constructor do Task: ");
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

