package chron.carlosrafael.chatapp;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import chron.carlosrafael.chatapp.Fragmentos.ReceitaFragment;
import chron.carlosrafael.chatapp.Models.Receita;
import chron.carlosrafael.chatapp.Utils.DatabaseHandler;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("chron.carlosrafael.chatapp", appContext.getPackageName());
    }

    @Test
    public void getReceitasServidor() throws Exception{

        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        ReceitaFragment receitaFragment = new ReceitaFragment();

        DatabaseHandler databaseHandler = DatabaseHandler.getDatabaseHandler(appContext);

        //ArrayList<Receita> receitas = databaseHandler.getAllReceitas();
        receitaFragment.getReceitasVolley();

        //assertEquals(receitas.size(), 3);
    }

    @Test
    public void getReceitasIds() throws Exception{

        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        DatabaseHandler databaseHandler = DatabaseHandler.getDatabaseHandler(appContext);

        ArrayList<Integer> receitas_ids = databaseHandler.getReceitasIds();

        assertEquals(receitas_ids.size(), 3);
    }

    @Test
    public void getAllReceitas() throws Exception{

        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        DatabaseHandler databaseHandler = DatabaseHandler.getDatabaseHandler(appContext);

        ArrayList<Receita> receitas = databaseHandler.getAllReceitas();

        assertEquals(receitas.size(), 3);
    }
}
