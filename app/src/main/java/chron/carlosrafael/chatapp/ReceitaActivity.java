package chron.carlosrafael.chatapp;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import chron.carlosrafael.chatapp.Models.Ingrediente;
import chron.carlosrafael.chatapp.Models.Parte_da_Receita;
import chron.carlosrafael.chatapp.Models.Passo_da_Receita;
import chron.carlosrafael.chatapp.Models.Receita;

public class ReceitaActivity extends AppCompatActivity {

    private TextView receita_id;
    private TextView nome_da_receita;
    private TextView categoria;
    private TextView tempo_de_preparo;
    private TextView nivel_de_dificuldade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_receita);

        // Pegando o JSON da receita que foi passada no intent para dps converter no Objeto e extrair os dados
        Gson gson = new Gson();
        Receita receita = gson.fromJson(getIntent().getStringExtra("receita"), Receita.class);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Setando o titulo da Activity para o nome da receita
        actionBar.setTitle(receita.getNome_receita());

        LinearLayout activity_receita = (LinearLayout) settingViewProgramatically(this, receita);

//        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View activityView = inflater.inflate(R.layout.activity_receita, null);
//
////        ViewGroup viewGroup = (ViewGroup) ((ViewGroup) this
////                .findViewById(R.id.activity_receita)).getChildAt(0);
//
//        LinearLayout activity_receita = (LinearLayout) activityView.findViewById(R.id.activity_receita);
//
//
////        receita_id = (TextView) activityView.findViewById(R.id.receita_id);
////        nome_da_receita = (TextView) activityView.findViewById(R.id.nome_da_receita);
////        categoria = (TextView) activityView.findViewById(R.id.categoria);
////        tempo_de_preparo = (TextView) activityView.findViewById(R.id.tempo_de_preparo);
////        nivel_de_dificuldade = (TextView) activityView.findViewById(R.id.nivel_de_dificuldade);
//
//        // Pegando o JSON da receita que foi passada no intent para dps converter no Objeto e extrair os dados
//        Gson gson = new Gson();
//        Receita receita = gson.fromJson(getIntent().getStringExtra("receita"), Receita.class);
//
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);
//
//        // Setando o titulo da Activity para o nome da receita
//        actionBar.setTitle(receita.getNome_receita());
//
//
//        // Setando as views com conteudo
//        receita_id.setText(String.valueOf(receita.getId()));
//        nome_da_receita.setText(String.valueOf(receita.getNome_receita()));
//        categoria.setText(String.valueOf(receita.getCategoria()));
//        tempo_de_preparo.setText(String.valueOf(receita.getTempo_de_preparo()));
//        nivel_de_dificuldade.setText(String.valueOf(receita.getNivel_de_dificuldade()));
//
//        /*
//        ((ViewGroup)receita_id.getParent()).removeView(receita_id);
//        activity_receita.addView(receita_id, 0);
//        ((ViewGroup)nome_da_receita.getParent()).removeView(nome_da_receita);
//        activity_receita.addView(nome_da_receita, 1);
//        ((ViewGroup)categoria.getParent()).removeView(categoria);
//        activity_receita.addView(categoria, 2);
//        */
//
//        CardView titleCard = createTitleCardView(this, receita_id, nome_da_receita, categoria);
//        activity_receita.addView(titleCard, 0);
//
//        ((ViewGroup)tempo_de_preparo.getParent()).removeView(tempo_de_preparo);
//        activity_receita.addView(tempo_de_preparo, 1);
//        ((ViewGroup)nivel_de_dificuldade.getParent()).removeView(nivel_de_dificuldade);
//        activity_receita.addView(nivel_de_dificuldade, 2);
//
//        //LinearLayout activity_receita = (LinearLayout) findViewById(R.id.activity_receita);
//
//
//        // Criando a view das subpartes, modo de preparo e ingredientes dinamicamente
//        for(int s=0;s<receita.getSubpartes().size();s++){
//
//            Parte_da_Receita receita_subparte = receita.getSubpartes().get(s);
//
//            LinearLayout subparteLayout = new LinearLayout(this);
//            subparteLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
//            subparteLayout.setOrientation(LinearLayout.VERTICAL);
//
//            TextView nome_da_subparte = new TextView(this);
//            nome_da_subparte.setText(receita_subparte.getNome_da_parte_receita());
//
//            // ADICIONANDO A VIEW
//            subparteLayout.addView(nome_da_subparte);
//
//            TextView ingredientes_label_txtView = new TextView(this);
//            ingredientes_label_txtView.setText("Ingredientes");
//
//            // ADICIONANDO A VIEW
//            subparteLayout.addView(ingredientes_label_txtView);
//
//
//            // LAYOUT DA LISTA DE INGREDIENTES DESSA PARTE
//            LinearLayout ingredientesLayout = new LinearLayout(this);
//
//            ingredientesLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
//            ingredientesLayout.setOrientation(LinearLayout.VERTICAL);
//
//            // PEGANDO A LISTA DE INGREDIENTES
//            for(int i=0;i<receita_subparte.getIngredientes().size();i++){
//                Ingrediente ingrediente = receita_subparte.getIngredientes().get(i);
//
//                TextView ingrediente_TxtView = new TextView(this);
//
//                String text = ingrediente.getQuantidade() + " " + ingrediente.getNome_ingrediente();
//                ingrediente_TxtView.setText(text);
//
//                // ADICIONANDO A VIEW
//                ingredientesLayout.addView(ingrediente_TxtView);
//
//            }
//
//            // ADICIONANDO A VIEW
//            subparteLayout.addView(ingredientesLayout);
//
//
//            TextView modo_de_preparo_label_txtView = new TextView(this);
//            modo_de_preparo_label_txtView.setText("Modo de preparo");
//
//            // ADICIONANDO A VIEW
//            subparteLayout.addView(modo_de_preparo_label_txtView);
//
//
//            // LAYOUT DA LISTA DE INGREDIENTES DESSA PARTE
//            LinearLayout modo_de_preparoLayout = new LinearLayout(this);
//
//            modo_de_preparoLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
//            modo_de_preparoLayout.setOrientation(LinearLayout.VERTICAL);
//
//            // PEGANDO A LISTA DE PASSOS DA RECEITA
//            for(int p=0;p<receita_subparte.getModo_de_preparo().size();p++){
//                Passo_da_Receita passo_da_receita = receita_subparte.getModo_de_preparo().get(p);
//
//                TextView passo_TxtView = new TextView(this);
//
//                String text = passo_da_receita.getDescricao();
//                passo_TxtView.setText(text);
//
//                // ADICIONANDO A VIEW
//                modo_de_preparoLayout.addView(passo_TxtView);
//
//            }
//
//            // ADICIONANDO A VIEW
//            subparteLayout.addView(modo_de_preparoLayout);
//
//
//            //RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) activity_receita.getLayoutParams();
////            params.addRule(RelativeLayout.BELOW, R.id.nome_receita_div);
//
////            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,
////                    LayoutParams.MATCH_PARENT);
//            //params.addRule(RelativeLayout.BELOW, R.id.nome_receita_div);
//
//            //viewGroup.addView(subparteLayout, params);
//            //activity_receita.addView(subparteLayout);
//            activity_receita.addView(subparteLayout, 3);
//        }

        //ViewGroup main_view = (ViewGroup) findViewById(R.id.activity_receita);
        //activity_receita.addView(viewGroup);
//        ((ViewGroup)viewGroup.getParent()).removeView(viewGroup);
        this.setContentView(activity_receita);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //startActivity(new Intent(this, HomeActivity.class));
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }


    public CardView createTitleCardView(Context context, TextView receita_id, TextView nome_da_receita, TextView categoria){

        // Initialize a new CardView
        CardView card = new CardView(context);

        // Set the CardView layoutParams
        LayoutParams params = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
        );
        card.setLayoutParams(params);

        // Set CardView corner radius
        card.setRadius(9);

        // Set cardView content padding
        card.setContentPadding(15, 15, 15, 15);

        // Set a background color for CardView
        card.setCardBackgroundColor(Color.parseColor("#FFC6D6C3"));

        // Set the CardView maximum elevation
        card.setMaxCardElevation(15);

        // Set CardView elevation
        card.setCardElevation(9);

        // Initialize a new TextView to put in CardView
//        TextView tv = new TextView(mContext);
//        tv.setLayoutParams(params);
//        tv.setText("CardView\nProgrammatically");
//        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
//        tv.setTextColor(Color.RED);

        // Put the TextView in CardView
        ((ViewGroup)receita_id.getParent()).removeView(receita_id);
        card.addView(receita_id);
        ((ViewGroup)nome_da_receita.getParent()).removeView(nome_da_receita);
        card.addView(nome_da_receita);
        ((ViewGroup)categoria.getParent()).removeView(categoria);
        card.addView(categoria);

        // Finally, add the CardView in root layout
        //mRelativeLayout.addView(card);
        return card;

    }


    public CardView createTitleCard(Context context, Receita receita){

        // Initialize a new CardView
        CardView card = new CardView(context);

        // Set the CardView layoutParams
        LayoutParams params = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
        );
        card.setLayoutParams(params);

        // Set CardView corner radius
        card.setRadius(9);

        // Set cardView content padding
        card.setContentPadding(15, 15, 15, 15);

        // Set a background color for CardView
        card.setCardBackgroundColor(Color.parseColor("#FFC6D6C3"));

        // Set the CardView maximum elevation
        card.setMaxCardElevation(15);

        // Set CardView elevation
        card.setCardElevation(9);

        /// CRIANDO O RELATIVELAYOUT
        RelativeLayout relativeLayout = new RelativeLayout(context);
        RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        relativeLayout.setLayoutParams(relativeParams);


        // Initialize a new TextView to put in CardView
        TextView tvId = new TextView(context);
        tvId.setId(R.id.receita_id);
        tvId.setLayoutParams(params);
        tvId.setText(String.valueOf(receita.getId()));
        tvId.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
        tvId.setTextColor(Color.GREEN);

        TextView tvNome = new TextView(context);
        tvNome.setLayoutParams(params);
        tvNome.setText(receita.getNome_receita());
        tvNome.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
        tvNome.setTextColor(Color.RED);

        TextView tvCategoria = new TextView(context);
        tvCategoria.setLayoutParams(params);
        tvCategoria.setText(receita.getCategoria());
        tvCategoria.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
        tvCategoria.setTextColor(Color.BLUE);

        // Adicionando os TxtViews no RelativeLayout
        relativeLayout.addView(tvId);

        RelativeLayout.LayoutParams tvNomeParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        tvNomeParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        tvNomeParams.addRule(RelativeLayout.LEFT_OF, tvId.getId());
        tvNome.setLayoutParams(tvNomeParams);

        relativeLayout.addView(tvNome);


        RelativeLayout.LayoutParams tvCategoriaParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        tvCategoriaParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        tvCategoriaParams.addRule(RelativeLayout.BELOW, tvId.getId());
        tvCategoria.setLayoutParams(tvCategoriaParams);

        relativeLayout.addView(tvCategoria);

        card.addView(relativeLayout);

        return card;

    }


    public CardView createTempoPreparoCard(Context context, Receita receita){

        // Initialize a new CardView
        CardView card = new CardView(context);

        // Set the CardView layoutParams
        LayoutParams params = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
        );
        card.setLayoutParams(params);

        // Set CardView corner radius
        card.setRadius(9);

        // Set cardView content padding
        card.setContentPadding(15, 15, 15, 15);

        // Set a background color for CardView
        card.setCardBackgroundColor(Color.parseColor("#FFC6D6C3"));

        // Set the CardView maximum elevation
        card.setMaxCardElevation(15);

        // Set CardView elevation
        card.setCardElevation(9);

        /// CRIANDO O RELATIVELAYOUT
        RelativeLayout relativeLayout = new RelativeLayout(context);
        RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        relativeLayout.setLayoutParams(relativeParams);


        // Initialize a new TextView to put in CardView
        TextView tvTempoPreparo = new TextView(context);
        tvTempoPreparo.setId(R.id.tempo_de_preparo);
        tvTempoPreparo.setLayoutParams(params);
        tvTempoPreparo.setText(String.valueOf(receita.getTempo_de_preparo()) + " minutos");
        tvTempoPreparo.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
        tvTempoPreparo.setTextColor(Color.GREEN);

        TextView tvDificuldade = new TextView(context);
        tvDificuldade.setLayoutParams(params);
        tvDificuldade.setText("Nivel " + String.valueOf(receita.getNivel_de_dificuldade()));
        tvDificuldade.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
        tvDificuldade.setTextColor(Color.RED);


        // Adicionando os TxtViews no RelativeLayout
        relativeLayout.addView(tvTempoPreparo);

        RelativeLayout.LayoutParams tvDificuldadeParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        tvDificuldadeParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        tvDificuldadeParams.addRule(RelativeLayout.LEFT_OF, tvTempoPreparo.getId());
        tvDificuldade.setLayoutParams(tvDificuldadeParams);

        relativeLayout.addView(tvDificuldade);

        card.addView(relativeLayout);

        return card;

    }


    public ViewGroup createSubpartesView(Context context, Receita receita){

        LinearLayout subpartesView = new LinearLayout(context);

        // Criando a view das subpartes, modo de preparo e ingredientes dinamicamente
        for(int s=0;s<receita.getSubpartes().size();s++){

            Parte_da_Receita receita_subparte = receita.getSubpartes().get(s);

            LinearLayout subparteLayout = new LinearLayout(this);
            subparteLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            subparteLayout.setOrientation(LinearLayout.VERTICAL);

            TextView nome_da_subparte = new TextView(this);
            nome_da_subparte.setText(receita_subparte.getNome_da_parte_receita());

            // ADICIONANDO A VIEW
            subparteLayout.addView(nome_da_subparte);

            TextView ingredientes_label_txtView = new TextView(this);
            ingredientes_label_txtView.setText("Ingredientes");

            // ADICIONANDO A VIEW
            subparteLayout.addView(ingredientes_label_txtView);


            // LAYOUT DA LISTA DE INGREDIENTES DESSA PARTE
            LinearLayout ingredientesLayout = new LinearLayout(this);

            ingredientesLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            ingredientesLayout.setOrientation(LinearLayout.VERTICAL);

            // PEGANDO A LISTA DE INGREDIENTES
            for(int i=0;i<receita_subparte.getIngredientes().size();i++){
                Ingrediente ingrediente = receita_subparte.getIngredientes().get(i);

                TextView ingrediente_TxtView = new TextView(this);

                String text = ingrediente.getQuantidade() + " " + ingrediente.getNome_ingrediente();
                ingrediente_TxtView.setText(text);

                // ADICIONANDO A VIEW
                ingredientesLayout.addView(ingrediente_TxtView);

            }

            // ADICIONANDO A VIEW
            subparteLayout.addView(ingredientesLayout);


            TextView modo_de_preparo_label_txtView = new TextView(this);
            modo_de_preparo_label_txtView.setText("Modo de preparo");

            // ADICIONANDO A VIEW
            subparteLayout.addView(modo_de_preparo_label_txtView);


            // LAYOUT DA LISTA DE INGREDIENTES DESSA PARTE
            LinearLayout modo_de_preparoLayout = new LinearLayout(this);

            modo_de_preparoLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            modo_de_preparoLayout.setOrientation(LinearLayout.VERTICAL);

            // PEGANDO A LISTA DE PASSOS DA RECEITA
            for(int p=0;p<receita_subparte.getModo_de_preparo().size();p++){
                Passo_da_Receita passo_da_receita = receita_subparte.getModo_de_preparo().get(p);

                TextView passo_TxtView = new TextView(this);

                String text = passo_da_receita.getDescricao();
                passo_TxtView.setText(text);

                // ADICIONANDO A VIEW
                modo_de_preparoLayout.addView(passo_TxtView);

            }

            // ADICIONANDO A VIEW
            subparteLayout.addView(modo_de_preparoLayout);


            //RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) activity_receita.getLayoutParams();
//            params.addRule(RelativeLayout.BELOW, R.id.nome_receita_div);

//            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,
//                    LayoutParams.MATCH_PARENT);
            //params.addRule(RelativeLayout.BELOW, R.id.nome_receita_div);

            //viewGroup.addView(subparteLayout, params);
            //activity_receita.addView(subparteLayout);
            subpartesView.addView(subparteLayout);
        }

        return subpartesView;

    }


    public ViewGroup settingViewProgramatically(Context context, Receita receita){

        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View activityView = inflater.inflate(R.layout.activity_receita, null);
        LinearLayout activity_receita = (LinearLayout) activityView.findViewById(R.id.activity_receita);


        LinearLayout titleLayout = new LinearLayout(this);
        titleLayout.setPadding(0,5,0,10);
        CardView titleCard = createTitleCard(context, receita);
        titleCard.setPadding(5,5,5,5);


        titleLayout.addView(titleCard);
        activity_receita.addView(titleLayout);

        // PEGANDO O CARD COM TEMPO PREPARO E DIFICULDADE
        LinearLayout tempoLayout = new LinearLayout(this);
        tempoLayout.setPadding(0,5,0,10);
        CardView tempoPreparoCard = createTempoPreparoCard(context, receita);
        tempoPreparoCard.setPadding(5,5,5,5);

        tempoLayout.addView(tempoPreparoCard);
        activity_receita.addView(tempoLayout);

        // PEGANDO A VIEW COM OS INGREDIENTES E MODO DE PREPARO

        LinearLayout subpartesView = (LinearLayout) createSubpartesView(context, receita);
        activity_receita.addView(subpartesView);

        return activity_receita;
    }

}
