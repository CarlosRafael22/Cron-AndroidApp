package chron.carlosrafael.chatapp.Models;

/**
 * Created by CarlosRafael on 20/02/2017.
 */

public class Passo_da_Receita{

    private int id;
    private String descricao;

    public Passo_da_Receita(){

    }

    public Passo_da_Receita(int id, String descricao){
        this.id = id;
        this.descricao = descricao;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}

