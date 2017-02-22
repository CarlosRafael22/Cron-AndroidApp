package chron.carlosrafael.chatapp.Models;

/**
 * Created by CarlosRafael on 20/02/2017.
 */

public class Ingrediente{

    private int id;
    private String quantidade;
    private String nome_ingrediente;

    public Ingrediente(){

    }

    public Ingrediente(int id, String nome_ingrediente, String quantidade){
        this.id = id;
        this.nome_ingrediente = nome_ingrediente;
        this.quantidade = quantidade;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome_ingrediente() {
        return nome_ingrediente;
    }

    public void setNome_ingrediente(String nome_ingrediente) {
        this.nome_ingrediente = nome_ingrediente;
    }

    public String getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(String quantidade) {
        this.quantidade = quantidade;
    }
}
