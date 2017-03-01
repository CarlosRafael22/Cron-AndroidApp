package chron.carlosrafael.chatapp.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import chron.carlosrafael.chatapp.Models.Ingrediente;
import chron.carlosrafael.chatapp.Models.Parte_da_Receita;
import chron.carlosrafael.chatapp.Models.Passo_da_Receita;
import chron.carlosrafael.chatapp.Models.Receita;

/**
 * Created by CarlosRafael on 22/02/2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    // Logcat tag
    private static final String LOG = "DatabaseHandler";

    // Database Version
    private static int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "EleveAppDatabase";


    // Table Names
    private static final String TABLE_RECEITA = "receitas";
    private static final String TABLE_INGREDIENTE = "ingredientes";
    private static final String TABLE_PASSO_DA_RECEITA = "passos_da_receita";
    private static final String TABLE_PARTE_DA_RECEITA = "partes_da_receita";
    private static final String TABLE_PARTE_INGREDIENTES = "parte_ingredientes";
    private static final String TABLE_RECEITA_PARTES = "receita_partes";
    private static final String TABLE_PARTE_MODO_DE_PREPARO = "receita_modo_de_preparo";

//    // Common column names
//    private static final String KEY_ID = "id";
//    private static final String KEY_CREATED_AT = "created_at";

    // INGREDIENTE Table - column nmaes
    private static final String ID_INGREDIENTE = "id_ingrediente";
    private static final String NOME_INGREDIENTE = "nome_ingrediente";
    private static final String QUANTIDADE_INGREDIENTE = "quantidade_ingrediente";

    // PASSO DA RECEITA Table - column nmaes
    private static final String ID_PASSO_DA_RECEITA = "id_passo_da_receita";
    private static final String DESCRICAO_PASSO_DA_RECEITA = "descricao_passo_da_receita";

    // PARTE DA RECEITA Table - column nmaes
    private static final String ID_PARTE_DA_RECEITA = "id_parte_da_receita";
    private static final String NOME_PARTE_DA_RECEITA = "nome_parte_da_receita";

    // PARTE_INGREDIENTES Table - column names
    private static final String PARTE_INGREDIENTES_ID = "parte_ingredientes_id";
    private static final String PARTE_INGREDIENTES_PARTE_ID = "parte_ingredientes_parte_id";
    private static final String PARTE_INGREDIENTES_INGREDIENTE_ID = "parte_ingredientes_ingrediente_id";

    // RECEITA Table - column names
    private static final String ID_RECEITA = "id_receita";
    private static final String NOME_RECEITA = "nome_receita";
    private static final String CATEGORIA_RECEITA = "categoria_receita";
    private static final String TEMPO_PREPARO_RECEITA = "tempo_preparo_receita";
    private static final String NIVEL_DIFICULDADE_RECEITA = "nivel_dificuldade_receita";

    // RECEITA_PARTES Table - column names
    private static final String RECEITA_PARTES_ID = "receita_partes_id";
    private static final String RECEITA_PARTES_ID_RECEITA = "receita_partes_id_receita";
    private static final String RECEITA_PARTES_ID_PARTE = "receita_partes_id_parte";

    // RECEITA_MODO_DE_PREPARO Table - column names
    private static final String PARTE_MODO_DE_PREPARO_ID = "parte_modo_de_preparo_id";
    private static final String PARTE_MODO_DE_PREPARO_ID_PARTE = "parte_modo_de_preparo_id_parte";
    private static final String PARTE_MODO_DE_PREPARO_ID_PASSO = "parte_modo_de_preparo_id_passo";


    //VAI VIRAR UM SINGLETON PRA TER SO UM NO PROGRAMA
    private static DatabaseHandler databaseHandler;

    private DatabaseHandler(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    public static DatabaseHandler getDatabaseHandler(Context context){
        if(databaseHandler == null){
            databaseHandler = new DatabaseHandler(context);
        }
        return databaseHandler;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_INGREDIENTE_TABLE = "CREATE TABLE " + TABLE_INGREDIENTE + "(" + ID_INGREDIENTE + " INTEGER PRIMARY KEY, " +
                NOME_INGREDIENTE + " VARCHAR(100) NOT NULL, " + QUANTIDADE_INGREDIENTE + " VARCHAR(20)" + ")";

        db.execSQL(CREATE_INGREDIENTE_TABLE);

        String CREATE_PASSO_DA_RECEITA_TABLE = "CREATE TABLE " + TABLE_PASSO_DA_RECEITA + "(" + ID_PASSO_DA_RECEITA + " INTEGER PRIMARY KEY, " +
                DESCRICAO_PASSO_DA_RECEITA + " VARCHAR(200) NOT NULL " + ")";

        db.execSQL(CREATE_PASSO_DA_RECEITA_TABLE);

        String CREATE_PARTE_DA_RECEITA_TABLE = "CREATE TABLE " + TABLE_PARTE_DA_RECEITA + "(" + ID_PARTE_DA_RECEITA + " INTEGER PRIMARY KEY, " +
                NOME_PARTE_DA_RECEITA + " VARCHAR(20) NOT NULL " + ")";

        db.execSQL(CREATE_PARTE_DA_RECEITA_TABLE);

        String CREATE_PARTE_INGREDIENTES_TABLE = "CREATE TABLE " + TABLE_PARTE_INGREDIENTES + "(" + PARTE_INGREDIENTES_ID + " INTEGER PRIMARY KEY, " +
                PARTE_INGREDIENTES_INGREDIENTE_ID + " INTEGER NOT NULL, " + PARTE_INGREDIENTES_PARTE_ID + " INTEGER NOT NULL " + ")";

        db.execSQL(CREATE_PARTE_INGREDIENTES_TABLE);

        String CREATE_RECEITA_TABLE = "CREATE TABLE " + TABLE_RECEITA + "(" + ID_RECEITA + " INTEGER PRIMARY KEY, " +
                NOME_RECEITA + " VARCHAR(100) NOT NULL, " + CATEGORIA_RECEITA + " VARCHAR(100) NOT NULL, " +
                TEMPO_PREPARO_RECEITA + " INTEGER NOT NULL, " + NIVEL_DIFICULDADE_RECEITA + " INTEGER NOT NULL " + ")";

        db.execSQL(CREATE_RECEITA_TABLE);

        String CREATE_RECEITA_PARTES_TABLE = "CREATE TABLE " + TABLE_RECEITA_PARTES + "(" + RECEITA_PARTES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                RECEITA_PARTES_ID_PARTE + " INTEGER NOT NULL, " + RECEITA_PARTES_ID_RECEITA + " INTEGER NOT NULL " + ")";

        db.execSQL(CREATE_RECEITA_PARTES_TABLE);

        String CREATE_PARTE_MODO_DE_PREPARO_TABLE = "CREATE TABLE " + TABLE_PARTE_MODO_DE_PREPARO + "(" + PARTE_MODO_DE_PREPARO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                PARTE_MODO_DE_PREPARO_ID_PARTE + " INTEGER NOT NULL, " + PARTE_MODO_DE_PREPARO_ID_PASSO + " INTEGER NOT NULL " + ")";

        db.execSQL(CREATE_PARTE_MODO_DE_PREPARO_TABLE);

        Log.v("TAG", "CRIOU O BANDO E TABELAS");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECEITA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INGREDIENTE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PARTE_DA_RECEITA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PASSO_DA_RECEITA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PARTE_INGREDIENTES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECEITA_PARTES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PARTE_MODO_DE_PREPARO);

        DATABASE_VERSION++;

        // create new tables
        onCreate(db);

    }



    /////////////////////////////// INGREDIENTE METHODS ////////////////////////////////////////////
    public long addIngrediente(Ingrediente ingrediente){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ID_INGREDIENTE, ingrediente.getId());
        values.put(NOME_INGREDIENTE, ingrediente.getNome_ingrediente());
        values.put(QUANTIDADE_INGREDIENTE,ingrediente.getQuantidade());

        //when inserting in a row returns a long
        // insert() returns the row ID of the newly inserted row, or -1 if an error occurred
        long ingrediente_row_id = db.insert(TABLE_INGREDIENTE, null, values);
        Log.v("ADD INGREDIENTE ", String.valueOf(ingrediente_row_id));

//        // Putting the user's row id to be the User object's id
//        String id_string = Long.toString(user_id);
//        user.setId(id_string);

        return ingrediente_row_id;
    }

    public Ingrediente getIngrediente(int id){
        SQLiteDatabase db = this.getReadableDatabase();

        //String selectQuery = "SELECT * FROM " + TABLE_USER + " WHERE " + USER_ID + " = " + user_id;
        String selectQuery = "SELECT * FROM " + TABLE_INGREDIENTE + " WHERE " + ID_INGREDIENTE + " = " + "'" + id + "'";

        Log.v("GET_ING DATABASE", selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);

        Ingrediente ingrediente_retrieved;
        if (cursor != null && cursor.moveToFirst()){
            //cursor.moveToFirst();

            //WHEN RETRIEVING A USER FROM DATABASE WE HAVE TO CREATE A NEW USER WITH THE VALUES
            //OF THE FIELDS THIS USER HAS
            int id_returned = cursor.getInt(cursor.getColumnIndex(ID_INGREDIENTE));
            String nome_ingrediente_returned = cursor.getString(cursor.getColumnIndex(NOME_INGREDIENTE));
            String qtd_ingrediente_returned = cursor.getString(cursor.getColumnIndex(QUANTIDADE_INGREDIENTE));

            ingrediente_retrieved = new Ingrediente(id_returned, nome_ingrediente_returned, qtd_ingrediente_returned);

//            //getting its id
//            String id_string = Integer.toString(cursor.getInt(cursor.getColumnIndex(USER_ID)));
//            ingrediente_retrieved.setId(id_string);
        }else{
            //THERE IS NO USER WITH THIS FIELDS SO RETURN NULL THAT WE WILL HANDLE IT IN THE ACTIVITY
            ingrediente_retrieved = null;
        }
        cursor.close();

        return ingrediente_retrieved;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /////////////////////////////// PASSO_DA_RECEITA METHODS //////////////////////////////////////////////////////////////////////
    public long addPassoDaReceita(Passo_da_Receita passo){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ID_PASSO_DA_RECEITA, passo.getId());
        values.put(DESCRICAO_PASSO_DA_RECEITA, passo.getDescricao());

        //when inserting in a row returns a long
        // insert() returns the row ID of the newly inserted row, or -1 if an error occurred
        long passo_row_id = db.insert(TABLE_PASSO_DA_RECEITA, null, values);
        Log.v("ADD PASSO_REC ", String.valueOf(passo_row_id));

        return passo_row_id;
    }


    public Passo_da_Receita getPassoDaReceita(int id){
        SQLiteDatabase db = this.getReadableDatabase();

        //String selectQuery = "SELECT * FROM " + TABLE_USER + " WHERE " + USER_ID + " = " + user_id;
        String selectQuery = "SELECT * FROM " + TABLE_PASSO_DA_RECEITA + " WHERE " + ID_PASSO_DA_RECEITA + " = " + "'" + id + "'";

        Log.v("GET_PASSO DATABASE", selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);

        Passo_da_Receita passo_retrieved;
        if (cursor != null && cursor.moveToFirst()){
            //cursor.moveToFirst();

            //WHEN RETRIEVING A USER FROM DATABASE WE HAVE TO CREATE A NEW USER WITH THE VALUES
            //OF THE FIELDS THIS USER HAS
            int id_returned = cursor.getInt(cursor.getColumnIndex(ID_PASSO_DA_RECEITA));
            String descricao_returned = cursor.getString(cursor.getColumnIndex(DESCRICAO_PASSO_DA_RECEITA));

            passo_retrieved = new Passo_da_Receita(id_returned, descricao_returned);

        }else{
            //THERE IS NO USER WITH THIS FIELDS SO RETURN NULL THAT WE WILL HANDLE IT IN THE ACTIVITY
            passo_retrieved = null;
        }
        cursor.close();

        return passo_retrieved;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /////////////////////////////// PARTE_INGREDIENTES METHODS //////////////////////////////////////////////////////////////////////
    public long addParte_Ingredientes(int id_parte, int id_ingrediente){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PARTE_INGREDIENTES_PARTE_ID, id_parte);
        values.put(PARTE_INGREDIENTES_INGREDIENTE_ID, id_ingrediente);

        //when inserting in a row returns a long
        // insert() returns the row ID of the newly inserted row, or -1 if an error occurred
        long parte_ingrediente_row_id = db.insert(TABLE_PARTE_INGREDIENTES, null, values);
        Log.v("ADD PT_INGREDIENTE ", String.valueOf(parte_ingrediente_row_id));

        return parte_ingrediente_row_id;
    }

    public ArrayList<Ingrediente> getIngredientesParte(int id_parte){
        SQLiteDatabase db = this.getReadableDatabase();

        //String selectQuery = "SELECT * FROM " + TABLE_USER + " WHERE " + USER_ID + " = " + user_id;
        String selectQuery = "SELECT * FROM " + TABLE_PARTE_INGREDIENTES + " WHERE " + PARTE_INGREDIENTES_PARTE_ID + " = " + "'" + id_parte + "'";

        Log.v("GET_PT_INGR DB", selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);

        ArrayList<Ingrediente> ingredienteList = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()){
            do {

                int ingrediente_id = (cursor.getInt((cursor.getColumnIndex(PARTE_INGREDIENTES_INGREDIENTE_ID))));

                // COM O ID DO PASSO EU VOU PEGAR O MESMO NA SUA TABELA
                Ingrediente ingrediente = getIngrediente(ingrediente_id);

                // adding to tags list
                ingredienteList.add(ingrediente);
            } while (cursor.moveToNext());

        }

        cursor.close();

        return ingredienteList;
    }

    /////////////////////////////// PARTE_MODO_DE_PREPARO METHODS //////////////////////////////////////////////////////////////////////

    public long addParte_Modo_de_Preparo(int id_parte, int id_passo){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PARTE_MODO_DE_PREPARO_ID_PARTE, id_parte);
        values.put(PARTE_MODO_DE_PREPARO_ID_PASSO, id_passo);

        //when inserting in a row returns a long
        // insert() returns the row ID of the newly inserted row, or -1 if an error occurred
        long parte_passo_row_id = db.insert(TABLE_PARTE_MODO_DE_PREPARO, null, values);
        Log.v("ADD PT_MODO_PREPARO ", String.valueOf(parte_passo_row_id));

        return parte_passo_row_id;
    }

    public ArrayList<Passo_da_Receita> getModoPreparoParte(int id_parte){
        SQLiteDatabase db = this.getReadableDatabase();

        //String selectQuery = "SELECT * FROM " + TABLE_USER + " WHERE " + USER_ID + " = " + user_id;
        String selectQuery = "SELECT * FROM " + TABLE_PARTE_MODO_DE_PREPARO + " WHERE " + PARTE_MODO_DE_PREPARO_ID_PARTE + " = " + "'" + id_parte + "'";

        Log.v("GET_PT_MODO_PREP DB", selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);

        ArrayList<Passo_da_Receita> modo_de_preparo = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()){
            do {

                int passo_id = (cursor.getInt((cursor.getColumnIndex(PARTE_MODO_DE_PREPARO_ID_PASSO))));

                // COM O ID DO PASSO EU VOU PEGAR O MESMO NA SUA TABELA
                Passo_da_Receita passo = getPassoDaReceita(passo_id);

                // adding to tags list
                modo_de_preparo.add(passo);
            } while (cursor.moveToNext());

        }

        cursor.close();

        return modo_de_preparo;
    }

    /////////////////////////////// PARTE_DA_RECEITA METHODS //////////////////////////////////////////////////////////////////////

    public long addParteDaReceita(Parte_da_Receita parte){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ID_PARTE_DA_RECEITA, parte.getId());
        values.put(NOME_PARTE_DA_RECEITA, parte.getNome_da_parte_receita());

        //when inserting in a row returns a long
        // insert() returns the row ID of the newly inserted row, or -1 if an error occurred
        long parte_row_id = db.insert(TABLE_PARTE_DA_RECEITA, null, values);
        Log.v("ADD PARTE_REC ", String.valueOf(parte_row_id));

        // CRIANDO OS INGREDIENTES DESSA PARTE
        List<Ingrediente> ingredienteList =parte.getIngredientes();
        for(int i=0;i<ingredienteList.size();i++){
            Ingrediente ingrediente = ingredienteList.get(i);

            addIngrediente(ingrediente);
            addParte_Ingredientes(parte.getId(), ingrediente.getId());
        }

        // CRIANDO OS INGREDIENTES DESSA PARTE
        List<Passo_da_Receita> modo_de_preparoList = parte.getModo_de_preparo();
        for(int i=0;i<modo_de_preparoList.size();i++){
            Passo_da_Receita passo = modo_de_preparoList.get(i);

            addPassoDaReceita(passo);
            addParte_Modo_de_Preparo(parte.getId(), passo.getId());
        }

        return parte_row_id;
    }


    public Parte_da_Receita getParteDaReceita(int id){
        SQLiteDatabase db = this.getReadableDatabase();

        //String selectQuery = "SELECT * FROM " + TABLE_USER + " WHERE " + USER_ID + " = " + user_id;
        String selectQuery = "SELECT * FROM " + TABLE_PARTE_DA_RECEITA + " WHERE " + ID_PARTE_DA_RECEITA + " = " + "'" + id + "'";

        Log.v("GET_PARTE DATABASE", selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);

        Parte_da_Receita parte_retrieved;
        if (cursor != null && cursor.moveToFirst()){

            //PEGANDO TODOS OS INGREDIENTES DA PARTE
            ArrayList<Ingrediente> ingredientesParte = getIngredientesParte(id);

            // PEGANDO O MODO DE PREPARO DA PARTE
            ArrayList<Passo_da_Receita> modo_de_preparoParte = getModoPreparoParte(id);

            String nome_parte = cursor.getString(cursor.getColumnIndex(NOME_PARTE_DA_RECEITA));

            parte_retrieved = new Parte_da_Receita(id, ingredientesParte, modo_de_preparoParte, nome_parte);

        }else{
            //THERE IS NO USER WITH THIS FIELDS SO RETURN NULL THAT WE WILL HANDLE IT IN THE ACTIVITY
            parte_retrieved = null;
        }
        cursor.close();

        return parte_retrieved;
    }



    /////////////////////////////// RECEITA_PARTES METHODS //////////////////////////////////////////////////////////////////////

    public long addReceita_Partes(int id_receita, int id_parte){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(RECEITA_PARTES_ID_RECEITA, id_receita);
        values.put(RECEITA_PARTES_ID_PARTE, id_parte);

        //when inserting in a row returns a long
        // insert() returns the row ID of the newly inserted row, or -1 if an error occurred
        long receita_parte_row_id = db.insert(TABLE_RECEITA_PARTES, null, values);
        Log.v("ADD RECEITA_PARTES ", String.valueOf(receita_parte_row_id));

        return receita_parte_row_id;
    }


    public ArrayList<Parte_da_Receita> getAllPartesDaReceita(int id_receita){
        SQLiteDatabase db = this.getReadableDatabase();

        //String selectQuery = "SELECT * FROM " + TABLE_USER + " WHERE " + USER_ID + " = " + user_id;
        String selectQuery = "SELECT * FROM " + TABLE_RECEITA_PARTES + " WHERE " + RECEITA_PARTES_ID_RECEITA + " = " + "'" + id_receita + "'";

        Log.v("GET_REC_PARTES DATABASE", selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);

        ArrayList<Parte_da_Receita> partes_da_receita = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()){

            do {
                int parte_id = (cursor.getInt((cursor.getColumnIndex(RECEITA_PARTES_ID_PARTE))));

                // COM O ID DA PARTE EU VOU PEGAR O MESMO NA SUA TABELA
                Parte_da_Receita parte = getParteDaReceita(parte_id);

                // adding to tags list
                partes_da_receita.add(parte);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return partes_da_receita;
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    /////////////////////////////// RECEITA METHODS /////////////////////////////////////////////////////////////////////////////

    public long addReceita(Receita receita){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ID_RECEITA, receita.getId());
        values.put(NOME_RECEITA, receita.getNome_receita());
        values.put(CATEGORIA_RECEITA, receita.getCategoria());
        values.put(NIVEL_DIFICULDADE_RECEITA, receita.getNivel_de_dificuldade());
        values.put(TEMPO_PREPARO_RECEITA, receita.getTempo_de_preparo());

        //when inserting in a row returns a long
        // insert() returns the row ID of the newly inserted row, or -1 if an error occurred
        long receita_row_id = db.insert(TABLE_RECEITA, null, values);
        Log.v("ADD RECEITA ", String.valueOf(receita_row_id));

        // CRIANDO AS PARTES DESSA RECEITA
        ArrayList<Parte_da_Receita> subpartesList = receita.getSubpartes();
        for(int i=0;i<subpartesList.size();i++){
            Parte_da_Receita subparte = subpartesList.get(i);

            addParteDaReceita(subparte);
            addReceita_Partes(receita.getId(), subparte.getId());
        }

        return receita_row_id;
    }


    public ArrayList<Receita> getAllReceitas(){
        SQLiteDatabase db = this.getReadableDatabase();

        //String selectQuery = "SELECT * FROM " + TABLE_USER + " WHERE " + USER_ID + " = " + user_id;
        String selectQuery = "SELECT * FROM " + TABLE_RECEITA;

        Log.v("GET_ALL_RECS DATABASE", selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);

        ArrayList<Receita> todas_receitas = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()){

            do {

                int receita_id = cursor.getInt(cursor.getColumnIndex(ID_RECEITA));
                String nome_receita = cursor.getString(cursor.getColumnIndex(NOME_RECEITA));
                String categoria_receita = cursor.getString(cursor.getColumnIndex(CATEGORIA_RECEITA));
                int tempo_preparo_receita = cursor.getInt(cursor.getColumnIndex(TEMPO_PREPARO_RECEITA));
                int nivel_de_dificuldade = cursor.getInt(cursor.getColumnIndex(NIVEL_DIFICULDADE_RECEITA));

                // COM O ID DA RECEITA EU VOU PEGAR TODAS AS PARTES DELA NA TABELA
                ArrayList<Parte_da_Receita> partes_receita = getAllPartesDaReceita(receita_id);

                Receita receita = new Receita(receita_id, nome_receita, categoria_receita, tempo_preparo_receita, nivel_de_dificuldade, partes_receita);
                // adding to tags list
                todas_receitas.add(receita);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return todas_receitas;
    }


    public ArrayList<Integer> getReceitasIds(){
        SQLiteDatabase db = this.getReadableDatabase();

        //String selectQuery = "SELECT * FROM " + TABLE_USER + " WHERE " + USER_ID + " = " + user_id;
        String selectQuery = "SELECT " + ID_RECEITA + " FROM " + TABLE_RECEITA ;

        Log.v("GET_IDS REC DATABASE", selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);

        ArrayList<Integer> receitas_ids = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()){

            do {

                int receita_id = cursor.getInt(cursor.getColumnIndex(ID_RECEITA));

                receitas_ids.add(receita_id);

            } while (cursor.moveToNext());
        }

        cursor.close();

        return receitas_ids;
    }



}
