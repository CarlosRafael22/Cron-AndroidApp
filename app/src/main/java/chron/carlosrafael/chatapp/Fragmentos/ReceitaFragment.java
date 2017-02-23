package chron.carlosrafael.chatapp.Fragmentos;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import chron.carlosrafael.chatapp.Adapters.ReceitaRecyclerViewAdapter;
import chron.carlosrafael.chatapp.Interfaces;
import chron.carlosrafael.chatapp.Models.Ingrediente;
import chron.carlosrafael.chatapp.Models.Parte_da_Receita;
import chron.carlosrafael.chatapp.Models.Passo_da_Receita;
import chron.carlosrafael.chatapp.Models.Receita;
import chron.carlosrafael.chatapp.R;
import chron.carlosrafael.chatapp.Fragmentos.dummy.DummyContent;
import chron.carlosrafael.chatapp.Fragmentos.dummy.DummyContent.DummyItem;
import chron.carlosrafael.chatapp.Utils.ServerRequests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ReceitaFragment extends Fragment {

    private static final String TAG = "ReceitaFragment";
    private static final String BASE_URL = "http://54.202.76.189:8000/";

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    // Serve para avisar o HomeActivity que o fragmento foi criado
    private Interfaces.OnFragmentCreatedListener createdFragListener;

    // Usada para fazer com que avise ao HomeActivity que o fragmento foi criado pela primeira vez
    // O onCreate e onCreateView sao executados todas as vezes q o Fragmento vai ser visto, porem
    // so queremos que ele mande o aviso na primeira vez que eh executado para pegar os chats
    private static boolean CREATED_FOR_THE_FIRST_TIME = false;


    // AS RECEITAS PEGAS PELO VOLLEY
    private List<Receita> receitas = new ArrayList<>();
    private ReceitaRecyclerViewAdapter adapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ReceitaFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ReceitaFragment newInstance(int columnCount) {
        ReceitaFragment fragment = new ReceitaFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

        // Tentando pegar as receitas do servidor aqui
        Log.v(TAG, "INDO PEGAR AS RECEITAS");
        getReceitasVolley();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_receita_list, container, false);

        adapter = new ReceitaRecyclerViewAdapter(receitas, mListener);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(adapter);
        }

        if (!CREATED_FOR_THE_FIRST_TIME) {
            // Avisa para o HomeActivity que o fragment acabou de ser criado
            createdFragListener.onFragmentCreated(this.getClass().getSimpleName());

            // Seta para true para que ele nao avise mais ao HomeActivity que foi criado e assim
            // nao execute o onFragmentCreated no HomeActivity
            CREATED_FOR_THE_FIRST_TIME = true;
        }

        return view;
    }


    public void getReceitasVolley(){

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url =BASE_URL+"api/receitas";
        Log.v(TAG, "CHAMOU VOLLEY RECEITAS");

        JsonArrayRequest jsArrayRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        //volleyInfo.setText("Response: " + response.toString());

                        // IRA ARMAZENAR AS RECEITAS RETORNADAS DA REQUISICAO AO SERVIDOR
                        ArrayList<Receita> receitasRetornadas = new ArrayList<>();

                        for(int r=0;r<response.length();r++){
                            try {
                                JSONObject jsonObject = response.getJSONObject(r);

                                int id_receita = jsonObject.getInt("id");
                                String nome_receita = jsonObject.getString("nome_receita");
                                String foto_da_receita = jsonObject.getString("foto_da_receita");
                                String url_da_imagem = jsonObject.getString("url_da_imagem");
                                String categoria = jsonObject.getString("categoria");
                                int tempo_de_preparo = jsonObject.getInt("tempo_de_preparo");
                                int nivel_de_dificuldade = jsonObject.getInt("nivel_de_dificuldade");

                                // Vou criar cada um dos objetos para dps criar a Receita mesmo
                                JSONArray subpartesLista = jsonObject.getJSONArray("subpartes");

                                // IRA ARMAZENAR AS SUBPARTES DA RECEITA
                                ArrayList<Parte_da_Receita> subpartesDaReceita = new ArrayList<>();

                                for(int s=0;s<subpartesLista.length();s++){
                                    JSONObject subparte = subpartesLista.getJSONObject(s);

                                    int subparte_id = subparte.getInt("id");
                                    String nome_da_parte = subparte.getString("nome_da_parte");

                                    /////////////////////////////////// INGREDIENTES /////////////////////////////
                                    // PEGANDO OS INGREDIENTES AGORA
                                    JSONArray ingredientesLista = subparte.getJSONArray("ingredientes");

                                    // IRA ARMAZENAR OS INGREDIENTES DA SUBPARTE
                                    ArrayList<Ingrediente> ingredientesSubparte = new ArrayList<>();

                                    for(int i=0;i<ingredientesLista.length();i++ ){
                                        JSONObject ingredienteJSON = ingredientesLista.getJSONObject(i);

                                        String quantidade = ingredienteJSON.getString("quantidade");
                                        String nome_ingrediente = ingredienteJSON.getString("nome_ingrediente");
                                        int ingrediente_id = ingredienteJSON.getInt("id");

                                        // CRIANDO OBJETO INGREDIENTE
                                        Ingrediente ingrediente = new Ingrediente(ingrediente_id, nome_ingrediente, quantidade);
                                        ingredientesSubparte.add(ingrediente);
                                    }

                                    /////////////////////////////////////////////////////////////////////////////////


                                    /////////////////////////////////// MODO DE PREPARO /////////////////////////////
                                    // PEGANDO OS INGREDIENTES AGORA
                                    JSONArray modo_de_preparo = subparte.getJSONArray("modo_de_preparo");

                                    // IRA ARMAZENAR OS INGREDIENTES DA SUBPARTE
                                    ArrayList<Passo_da_Receita> modo_de_preparoSubparte = new ArrayList<>();

                                    for(int p=0;p<modo_de_preparo.length();p++ ){
                                        JSONObject passo_PreparoJSON = modo_de_preparo.getJSONObject(p);

                                        String descricao_passo_preparo = passo_PreparoJSON.getString("descricao");
                                        int passo_preparo_id = passo_PreparoJSON.getInt("id");

                                        // CRIANDO OBJETO INGREDIENTE
                                        Passo_da_Receita passo_preparo = new Passo_da_Receita(passo_preparo_id, descricao_passo_preparo);
                                        modo_de_preparoSubparte.add(passo_preparo);
                                    }

                                    /////////////////////////////////////////////////////////////////////////////////

                                    // CRIANDO O PARTE_DA_RECEITA OBJECT
                                    Parte_da_Receita parte_da_receita = new Parte_da_Receita(subparte_id,ingredientesSubparte, modo_de_preparoSubparte, nome_da_parte);
                                    subpartesDaReceita.add(parte_da_receita);


                                }
                                // ADICIONADO TODAS AS PARTES NO ARRAYLIST, CRIAMOS O OBJETO RECEITA
                                Receita receita = new Receita(id_receita, nome_receita, categoria, tempo_de_preparo, nivel_de_dificuldade, subpartesDaReceita);
                                receitasRetornadas.add(receita);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        Log.v(TAG, "ATUALIZOU AS RECEITAS");
                        receitas = receitasRetornadas;

                        // TEM QUE FAZER ISSO PARA QUE ADICIONEMOS TODAS AS RECEITAS RETORNADAS NA LISTA DE RECEITAS DO ADAPTER
                        // E DPS ELE CHAMA O NOTIFYDATASETCHANGE PARA CRIAR O RECYCLERVIEW DE NOVO
                        adapter.refresh(receitas);
                        //return receitasRetornadas;

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub

                    }
                });
        // Add the request to the RequestQueue.
        queue.add(jsArrayRequest);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ReceitaFragment.OnListFragmentInteractionListener) {
            mListener = (ReceitaFragment.OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }


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
        mListener = null;
        createdFragListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Receita receita);
    }


    public class GetReceitasTask extends AsyncTask<Void, Void, JSONObject> {

        public Activity callingActivity;

        public GetReceitasTask(Activity activity) {
            this.callingActivity = activity;
            Log.d(TAG, "Background RECEITAS Constructor ");
        }

        @Override
        protected JSONObject doInBackground(Void... params) {

            if (android.os.Debug.isDebuggerConnected())
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
