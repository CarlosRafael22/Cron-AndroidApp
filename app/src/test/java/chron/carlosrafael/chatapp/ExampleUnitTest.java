package chron.carlosrafael.chatapp;

import android.content.Context;

import org.junit.After;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;

import chron.carlosrafael.chatapp.Fragmentos.ReceitaFragment;
import chron.carlosrafael.chatapp.Models.Receita;
import chron.carlosrafael.chatapp.Utils.DatabaseHandler;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void getReceitasServidor(){
        ReceitaFragment receitaFragment = Mockito.mock(ReceitaFragment.class);

        receitaFragment.getReceitasVolley();
    }

    @Mock
    DatabaseHandler databaseHandler;

    @After
    public void checkReceitasInDatabase(){

        Context appContext = Mockito.mock(Context.class);

        databaseHandler = DatabaseHandler.getDatabaseHandler(appContext);
        ArrayList<Receita> receitas = databaseHandler.getAllReceitas();

        assertEquals(receitas.size(), 3);
    }
}