package chron.carlosrafael.chatapp.Utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by CarlosRafael on 22/02/2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    // Logcat tag
    private static final String LOG = "DatabaseHandler";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "EleveAppDatabase";


    // Table Names
    private static final String TABLE_RECEITA = "receitas";
    private static final String TABLE_INGREDIENTE = "ingredientes";
    private static final String TABLE_PASSO_DA_RECEITA = "passos_da_receita";
    private static final String TABLE_PARTE_DA_RECEITA = "partes_da_receita";
    private static final String TABLE_PARTE_INGREDIENTES = "parte_ingredientes";
    private static final String TABLE_RECEITA_PARTES = "receita_partes";

    // Common column names
    private static final String KEY_ID = "id";
    private static final String KEY_CREATED_AT = "created_at";

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
                NOME_INGREDIENTE + " VARCHAR(100) NOT NULL, " + QUANTIDADE_INGREDIENTE + " VARCHAR(10) NOT NULL" + ")";

        db.execSQL(CREATE_INGREDIENTE_TABLE);

        String CREATE_PASSO_DA_RECEITA_TABLE = "CREATE TABLE " + TABLE_PASSO_DA_RECEITA + "(" + ID_PASSO_DA_RECEITA + " INTEGER PRIMARY KEY, " +
                DESCRICAO_PASSO_DA_RECEITA + " VARCHAR(200) NOT NULL " + ")";

        db.execSQL(CREATE_PASSO_DA_RECEITA_TABLE);

        String CREATE_PARTE_DA_RECEITA_TABLE = "CREATE TABLE " + TABLE_PARTE_DA_RECEITA + "(" + ID_PARTE_DA_RECEITA + " INTEGER PRIMARY KEY, " +
                NOME_PARTE_DA_RECEITA + " VARCHAR(20) NOT NULL " + ")";

        db.execSQL(CREATE_PARTE_DA_RECEITA_TABLE);

        String CREATE_PARTE_INGREDIENTES_TABLE = "CREATE TABLE " + TABLE_PARTE_INGREDIENTES + "(" + PARTE_INGREDIENTES_ID + " INTEGER PRIMARY KEY, " +
                PARTE_INGREDIENTES_INGREDIENTE_ID + " INTEGER NOT NULL " + PARTE_INGREDIENTES_PARTE_ID + " INTEGER NOT NULL " + ")";

        db.execSQL(CREATE_PARTE_INGREDIENTES_TABLE);

        String CREATE_RECEITA_TABLE = "CREATE TABLE " + TABLE_RECEITA + "(" + ID_RECEITA + " INTEGER PRIMARY KEY, " +
                NOME_RECEITA + " VARCHAR(100) NOT NULL " + CATEGORIA_RECEITA + " VARCHAR(100) NOT NULL " +
                TEMPO_PREPARO_RECEITA + " INTEGER NOT NULL " + NIVEL_DIFICULDADE_RECEITA + " INTEGER NOT NULL " + ")";

        db.execSQL(CREATE_RECEITA_TABLE);

        String CREATE_RECEITA_PARTES_TABLE = "CREATE TABLE " + TABLE_RECEITA_PARTES + "(" + RECEITA_PARTES_ID + " INTEGER PRIMARY KEY, " +
                RECEITA_PARTES_ID_PARTE + " INTEGER NOT NULL " + RECEITA_PARTES_ID_RECEITA + " INTEGER NOT NULL " + ")";

        db.execSQL(CREATE_RECEITA_PARTES_TABLE);

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

        // create new tables
        onCreate(db);

    }
}
