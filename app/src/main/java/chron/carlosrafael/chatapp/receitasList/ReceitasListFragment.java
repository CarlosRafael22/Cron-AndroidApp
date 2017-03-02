package chron.carlosrafael.chatapp.receitasList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import chron.carlosrafael.chatapp.Adapters.ReceitaRecyclerViewAdapter;
import chron.carlosrafael.chatapp.Interfaces;
import chron.carlosrafael.chatapp.Models.Receita;
import chron.carlosrafael.chatapp.R;
import chron.carlosrafael.chatapp.ReceitaActivity;
import chron.carlosrafael.chatapp.Utils.DatabaseHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ReceitasListFragment extends Fragment implements ReceitasListContract.View{

    // Tem que ter o Listener para mandar para o Presenter
    private ReceitasListContract.UserActionsListener presenterActionsListener;

    private static final String TAG = "ReceitaFragment";
    private static final String BASE_URL = "http://54.202.76.189:8000/";

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    private ReceitaRecyclerViewAdapter.ReceitaClickedItemListener receitaClickedItemListener;

    // Serve para avisar o HomeActivity que o fragmento foi criado
    private Interfaces.OnFragmentCreatedListener createdFragListener;

    private OnListFragmentInteractionListener mListener;

    // Usada para fazer com que avise ao HomeActivity que o fragmento foi criado pela primeira vez
    // O onCreate e onCreateView sao executados todas as vezes q o Fragmento vai ser visto, porem
    // so queremos que ele mande o aviso na primeira vez que eh executado para pegar os chats
    private static boolean CREATED_FOR_THE_FIRST_TIME = false;


    // AS RECEITAS PEGAS PELO VOLLEY
    private List<Receita> receitas = new ArrayList<>();
    private ReceitaRecyclerViewAdapter adapter;


    private DatabaseHandler databaseHandler;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ReceitasListFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ReceitasListFragment newInstance(int columnCount) {
        ReceitasListFragment fragment = new ReceitasListFragment();
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

        databaseHandler = DatabaseHandler.getDatabaseHandler(getContext());

        // Inicializando o Presenter
        presenterActionsListener = new ReceitasListPresenter(databaseHandler, this, getContext());

        // Tentando pegar as receitas do servidor aqui
        Log.v(TAG, "INDO PEGAR AS RECEITAS");
        //getReceitasVolley();
        //getReceitasLocal(adapter);
        presenterActionsListener.loadReceitas();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_receita_list, container, false);

        /**
         * Listener for clicks on notes in the RecyclerView.
         */
        ReceitaRecyclerViewAdapter.ReceitaClickedItemListener receitaClickedItemListener = new ReceitaRecyclerViewAdapter.ReceitaClickedItemListener() {
            @Override
            public void onReceitaClicked(Receita clickedReceita) {
                presenterActionsListener.showReceita(clickedReceita);
            }
        };

        mListener = new OnListFragmentInteractionListener() {
            @Override
            public void onListFragmentInteraction(Receita receita) {

            }
        };

        adapter = new ReceitaRecyclerViewAdapter(receitas, mListener, receitaClickedItemListener);

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

        Log.v(TAG, "INDO PEGAR AS RECEITAS");
        //getReceitasVolley();
        //getReceitasLocal(adapter);

        if (!CREATED_FOR_THE_FIRST_TIME) {
            // Avisa para o HomeActivity que o fragment acabou de ser criado
            createdFragListener.onFragmentCreated(this.getClass().getSimpleName());

            // Seta para true para que ele nao avise mais ao HomeActivity que foi criado e assim
            // nao execute o onFragmentCreated no HomeActivity
            CREATED_FOR_THE_FIRST_TIME = true;
        }

        return view;
    }

    public void getReceitasLocal(ReceitaRecyclerViewAdapter adapter){

        ArrayList<Receita> todas_receitas = databaseHandler.getAllReceitas();

        adapter.refresh(todas_receitas);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnListFragmentInteractionListener) {
//            mListener = (OnListFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnListFragmentInteractionListener");
//        }


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

    @Override
    public void showReceitas(List<Receita> receitas) {

         // TEM QUE FAZER ISSO PARA QUE ADICIONEMOS TODAS AS RECEITAS RETORNADAS NA LISTA DE RECEITAS DO ADAPTER
         // E DPS ELE CHAMA O NOTIFYDATASETCHANGE PARA CRIAR O RECYCLERVIEW DE NOVO
         adapter.refresh(receitas);

    }

    @Override
    public void showReceitaDetail(Receita receita) {

        Intent intent = new Intent(getContext(), ReceitaActivity.class);

        Gson gson = new Gson();
        String JsonReceita = gson.toJson(receita);

        intent.putExtra("receita", JsonReceita );

        startActivity(intent);
    }
}
