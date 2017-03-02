package chron.carlosrafael.chatapp.receitasList;

import android.animation.RectEvaluator;
import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import chron.carlosrafael.chatapp.Models.Receita;
import chron.carlosrafael.chatapp.Utils.DatabaseHandler;

import static org.mockito.Mockito.verify;

/**
 * Created by CarlosRafael on 01/03/2017.
 */

public class ReceitasListPresenterTest {

    // Receitas fakes para serem carregadas
    private List<Receita> receitas  = new ArrayList<>();
    private Receita rec1 = new Receita(1, "Chocolate Cake", "Bolos", 45, 2, null);
    private Receita rec2 = new Receita(2, "Brigadeiro", "Doces", 25, 1, null);


    ReceitasListPresenter receitasListPresenter;

    @Mock
    ReceitasListContract.View receitasListView;

    @Mock
    ReceitasListFragment receitasListFragment;

    @Mock
    Context appContext;

    DatabaseHandler databaseHandler = DatabaseHandler.getDatabaseHandler(appContext);


    @Before
    public void setupReceitasListPresenter() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);

        receitas.add(rec1);
        receitas.add(rec2);

        // Get a reference to the class under test
        receitasListPresenter = new ReceitasListPresenter(databaseHandler, receitasListFragment, appContext);
    }

    @Test
    public void showReceitas(){

        // Testando se o Presenter carregou as Receitas

        //receitasListPresenter.loadReceitas();

        // E a View mostra todas as receitas

        verify(receitasListFragment).showReceitas(receitas);
    }
}
