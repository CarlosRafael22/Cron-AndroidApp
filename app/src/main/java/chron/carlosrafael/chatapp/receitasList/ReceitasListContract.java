package chron.carlosrafael.chatapp.receitasList;

import java.util.ArrayList;
import java.util.List;

import chron.carlosrafael.chatapp.Models.Receita;

/**
 * Created by CarlosRafael on 01/03/2017.
 */

public interface ReceitasListContract {

//    Metodos que a View vai ter que implementar para mostrar ao Usuario
    // Esses metodos seram chamados pelo Presenter
    interface View {

        void showReceitas(List<Receita> receitas);

        void showReceitaDetail(Receita receita);
    }

    // Metodos que o Presenter vai ter que implementar para lidar com o Model
    // Esses metodos serao chamados pela View
    interface UserActionsListener {

        void loadReceitas();

        void showReceita(Receita receita);

    }
}
