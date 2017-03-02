package chron.carlosrafael.chatapp;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import chron.carlosrafael.chatapp.Fragmentos.ReceitaFragment;
import chron.carlosrafael.chatapp.Models.Receita;
import chron.carlosrafael.chatapp.Utils.DatabaseHandler;

import static org.junit.Assert.*;

/**
 * Created by CarlosRafael on 28/02/2017.
 */

public class ReceitaFragmentTest {

    @Mock
    private Context context;

    private ReceitaFragment receitaFragment;

    private DatabaseHandler databaseHandler;


    @Before
    public void setupReceitaFragment(){

        MockitoAnnotations.initMocks(this);

        receitaFragment = new ReceitaFragment();

        databaseHandler = DatabaseHandler.getDatabaseHandler(context);

    }

    @Test
    public void getReceitasVolley(){

        //receitaFragment.getReceitasVolley();

        // Vendo se botou no banco
        ArrayList<Receita> receitas = databaseHandler.getAllReceitas();

        assertEquals(receitas.size(), 3);

    }
}
