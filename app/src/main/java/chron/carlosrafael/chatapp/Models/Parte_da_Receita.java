package chron.carlosrafael.chatapp.Models;

import java.util.ArrayList;

/**
 * Created by CarlosRafael on 20/02/2017.
 */

public class Parte_da_Receita{

    private int id;
    private String nome_da_parte_receita;
    private ArrayList<Ingrediente> ingredientes;
    private ArrayList<Passo_da_Receita> modo_de_preparo;

    public Parte_da_Receita(){

    }

    public Parte_da_Receita(int id, ArrayList<Ingrediente> ingredientes, ArrayList<Passo_da_Receita> modo_de_preparo, String nome_da_parte_receita){
        this.id = id;
        this.ingredientes = ingredientes;
        this.modo_de_preparo = modo_de_preparo;
        this.nome_da_parte_receita = nome_da_parte_receita;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<Passo_da_Receita> getModo_de_preparo() {
        return modo_de_preparo;
    }

    public void setModo_de_preparo(ArrayList<Passo_da_Receita> modo_de_preparo) {
        this.modo_de_preparo = modo_de_preparo;
    }

    public ArrayList<Ingrediente> getIngredientes() {
        return ingredientes;
    }

    public void setIngredientes(ArrayList<Ingrediente> ingredientes) {
        this.ingredientes = ingredientes;
    }

    public String getNome_da_parte_receita() {
        return nome_da_parte_receita;
    }

    public void setNome_da_parte_receita(String nome_da_parte_receita) {
        this.nome_da_parte_receita = nome_da_parte_receita;
    }
}