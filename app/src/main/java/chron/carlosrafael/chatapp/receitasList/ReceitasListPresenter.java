package chron.carlosrafael.chatapp.receitasList;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import chron.carlosrafael.chatapp.Models.Ingrediente;
import chron.carlosrafael.chatapp.Models.Parte_da_Receita;
import chron.carlosrafael.chatapp.Models.Passo_da_Receita;
import chron.carlosrafael.chatapp.Models.Receita;
import chron.carlosrafael.chatapp.Utils.DatabaseHandler;

import static com.firebase.ui.auth.util.Preconditions.checkNotNull;
import static com.firebase.ui.auth.util.Preconditions.checkValidStyle;

/**
 * Created by CarlosRafael on 01/03/2017.
 */

public class ReceitasListPresenter implements ReceitasListContract.UserActionsListener {

    private static final String TAG = "ReceitaFragment";
    private static final String BASE_URL = "http://54.202.76.189:8000/";

    // O Presenter lida com a relacao do Model e da View entao tem q ter os dois
    private ReceitasListContract.View receitasListView;

    // O Model vai ser representado pelo DatabaseHandler ja que ele eh o ponto de acesso aos dados
    private DatabaseHandler databaseHandler;

    // Vai precisar ter o contexto do app para fazer a requisicao com o Volley
    private Context appContext;

    public ReceitasListPresenter(@NonNull DatabaseHandler databaseHandler, @NonNull ReceitasListContract.View receitasListView, Context appContext){
        this.receitasListView = checkNotNull(receitasListView, "receitasListView cannot be null!");
        this.databaseHandler = checkNotNull(databaseHandler, "databaseHandler cannot be null!");;
        this.appContext = appContext;
    }

    @Override
    public void loadReceitas() {

        // Carrega as receitas
        getReceitasVolley();

        // Receitas fakes para serem carregadas
//        List<Receita> receitas  = new ArrayList<>();
//        Receita rec1 = new Receita(1, "Chocolate Cake", "Bolos", 45, 2, null);
//        Receita rec2 = new Receita(2, "Brigadeiro", "Doces", 25, 1, null);
//
//        receitas.add(rec1);
//        receitas.add(rec2);
//
//        receitasListView.showReceitas(receitas);

    }

    @Override
    public void showReceita(Receita receita) {

        // Chamando a View para mostrar essa
        Toast.makeText(appContext, receita.getNome_receita(), Toast.LENGTH_SHORT).show();
        receitasListView.showReceitaDetail(receita);

    }


    public void getReceitasVolley(){


        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(appContext);
        String url =BASE_URL+"api/receitas";
        Log.v(TAG, "CHAMOU VOLLEY RECEITAS");

        JsonArrayRequest jsArrayRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        //volleyInfo.setText("Response: " + response.toString());

                        // IRA ARMAZENAR AS RECEITAS RETORNADAS DA REQUISICAO AO SERVIDOR
                        List<Receita> receitasRetornadas = new ArrayList<>();

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

                                    for(int i=0;i<ingredientesLista.length();i++){
                                        JSONObject ingredienteJSON = ingredientesLista.getJSONObject(i);

                                        String quantidade = ingredienteJSON.getString("quantidade");
                                        String nome_ingrediente = ingredienteJSON.getString("nome_ingrediente");
                                        int ingrediente_id = ingredienteJSON.getInt("id");

                                        // CRIANDO OBJETO INGREDIENTE
                                        Ingrediente ingrediente = new Ingrediente(ingrediente_id, nome_ingrediente, quantidade);
//                                        // ADICIONANDO INGREDIENTES AO BANCO
//                                        databaseHandler.addIngrediente(ingrediente);

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

                                        // CRIANDO OBJETO PASSO_DA_RECEITA
                                        Passo_da_Receita passo_preparo = new Passo_da_Receita(passo_preparo_id, descricao_passo_preparo);
//                                        // ADICIONANDO PASSO AO BANCO
//                                        databaseHandler.addPassoDaReceita(passo_preparo);

                                        modo_de_preparoSubparte.add(passo_preparo);
                                    }

                                    /////////////////////////////////////////////////////////////////////////////////

                                    // CRIANDO O PARTE_DA_RECEITA OBJECT
                                    Parte_da_Receita parte_da_receita = new Parte_da_Receita(subparte_id,ingredientesSubparte, modo_de_preparoSubparte, nome_da_parte);
//                                    // ADICIONANDO PARTE AO BANCO
//                                    databaseHandler.addParteDaReceita(parte_da_receita);

                                    subpartesDaReceita.add(parte_da_receita);


                                }
                                // ADICIONADO TODAS AS PARTES NO ARRAYLIST, CRIAMOS O OBJETO RECEITA
                                Receita receita = new Receita(id_receita, nome_receita, categoria, tempo_de_preparo, nivel_de_dificuldade, subpartesDaReceita);
                                // ADICIONANDO RECEITAS AO BANCO
                                databaseHandler.addReceita(receita);

                                receitasRetornadas.add(receita);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        Log.v(TAG, "ATUALIZOU AS RECEITAS");

                        // Manda a View mostrar as receitas pegas
                        receitasListView.showReceitas(receitasRetornadas);
//                        receitas = receitasRetornadas;
//
//                        // TEM QUE FAZER ISSO PARA QUE ADICIONEMOS TODAS AS RECEITAS RETORNADAS NA LISTA DE RECEITAS DO ADAPTER
//                        // E DPS ELE CHAMA O NOTIFYDATASETCHANGE PARA CRIAR O RECYCLERVIEW DE NOVO
//                        adapter.refresh(receitas);
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
}
