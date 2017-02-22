package chron.carlosrafael.chatapp.Utils;

import android.app.Activity;
import android.content.ContentValues;
import android.os.AsyncTask;
import android.util.Log;

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
import java.util.Map;

import chron.carlosrafael.chatapp.Models.Chat;
import chron.carlosrafael.chatapp.SignInActivity;

/**
 * Created by CarlosRafael on 20/02/2017.
 */

public class ServerRequests {

    private static final String TAG = "ServerRequests";
    private static final String BASE_URL = "http://54.202.76.189:8000/";

    public static class GetReceitasTask extends AsyncTask<Void, Void, JSONObject> {

        public Activity callingActivity;

        public GetReceitasTask(Activity activity){
            this.callingActivity = activity;
            Log.d(TAG, "Background RECEITAS Constructor ");
        }

        @Override
        protected JSONObject doInBackground(Void... params) {

            if(android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();

            // TODO: attempt authentication against a network service.


            if (params.length == 0) {

                return null;

            }


            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            Log.d(TAG, "Background RECEITAS: ");

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String serverResponseStr = null;

            try {

                URL url = new URL(BASE_URL + "api/receitas/");

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

                        //Chat chat = gson.fromJson(chatJsonElem, Chat.class);

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
//        protected void onPostExecute(SignInActivity.LoginParams paramsToFirebase) {
//            super.onPostExecute(paramsToFirebase);
////            Toast.makeText(getApplicationContext(), serverResponse, Toast.LENGTH_LONG).show();
////            startActivity(new Intent(SignInActivity.this, MainActivity.class));
//            SignInActivity.FirebaseLogin firebaseLogin = new SignInActivity.FirebaseLogin(this.callingActivity);
//            Log.v(TAG, "Mandando para a segunda thread");
//            firebaseLogin.execute(paramsToFirebase);
//
//        }

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



}
