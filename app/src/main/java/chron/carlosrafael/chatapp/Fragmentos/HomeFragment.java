package chron.carlosrafael.chatapp.Fragmentos;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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

import chron.carlosrafael.chatapp.Interfaces;
import chron.carlosrafael.chatapp.R;
import chron.carlosrafael.chatapp.SignInActivity;
import chron.carlosrafael.chatapp.UserLoggedIn;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    // Serve para avisar o HomeActivity que o fragmento foi criado
    private Interfaces.OnFragmentCreatedListener createdFragListener;

    // Usada para fazer com que avise ao HomeActivity que o fragmento foi criado pela primeira vez
    // O onCreate e onCreateView sao executados todas as vezes q o Fragmento vai ser visto, porem
    // so queremos que ele mande o aviso na primeira vez que eh executado para pegar os chats
    private static boolean CREATED_FOR_THE_FIRST_TIME = false;

    private Button homeButton;

    private TextView userInfo;
    private TextView volleyInfo;

    UserLoggedIn userLoggedIn;

    private static final String TAG = "HomeFragment";
    private static final String BASE_URL = "http://54.202.76.189:8000/";

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        userLoggedIn = UserLoggedIn.getUserLoggedIn(getContext());

        homeButton = (Button) rootView.findViewById(R.id.homeButton);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onFragmentInteraction(Uri.parse("http://www.google.com"));
            }
        });


        userInfo = (TextView) rootView.findViewById(R.id.userInfo);
        volleyInfo = (TextView) rootView.findViewById(R.id.volleyInfo);

        // Pegando algumas infos do UserLoggedIn para mostrar no chat e testar
        FirebaseUser firebaseUser = userLoggedIn.getCurrentFirebaseUser();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){

            String username = user.getDisplayName();
            String firebaseId = user.getUid();

            userInfo.setText(username+" com id: "+firebaseId);

        }

        //getVolley(volleyInfo);
        getReceitasVolley(volleyInfo);

        if(!CREATED_FOR_THE_FIRST_TIME){
            // Avisa para o HomeActivity que o fragment acabou de ser criado
            createdFragListener.onFragmentCreated(this.getClass().getSimpleName());

            // Seta para true para que ele nao avise mais ao HomeActivity que foi criado e assim
            // nao execute o onFragmentCreated no HomeActivity
            CREATED_FOR_THE_FIRST_TIME = true;
        }


        return rootView;
    }


    public void getVolley(final TextView volleyInfo){

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url ="http://www.google.com";

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        volleyInfo.setText("Response is: "+ response.substring(0,500));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                volleyInfo.setText("That didn't work!");
            }
        });
// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }


    public void getReceitasVolley(final TextView volleyInfo){

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url =BASE_URL+"api/receitas";
        Log.w(TAG, "CHAMOU VOLLEY RECEITAS");

        JsonArrayRequest jsObjRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        volleyInfo.setText("Response: " + response.toString());
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub

                    }
                });
        // Add the request to the RequestQueue.
        queue.add(jsObjRequest);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
            //createdFragListener = (Interfaces.OnFragmentCreatedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
