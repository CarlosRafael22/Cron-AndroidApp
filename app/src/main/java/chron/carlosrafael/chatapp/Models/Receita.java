package chron.carlosrafael.chatapp.Models;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by CarlosRafael on 20/02/2017.
 */

public class Receita {

    private int id;
    private String nome_receita;
    private Bitmap foto_da_receita;
    private String url_da_imagem;
    private String categoria;
    private int tempo_de_preparo;
    private int nivel_de_dificuldade;

    private ArrayList<Parte_da_Receita> subpartes;

    public Receita(){

    }

    // Sem as infos da foto
    public Receita(int id, String nome_receita, String categoria, int tempo_de_preparo, int nivel_de_dificuldade, ArrayList<Parte_da_Receita> subpartes) {
        this.id = id;
        this.nome_receita = nome_receita;
        this.categoria = categoria;
        this.tempo_de_preparo = tempo_de_preparo;
        this.nivel_de_dificuldade = nivel_de_dificuldade;
        this.subpartes = subpartes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome_receita() {
        return nome_receita;
    }

    public void setNome_receita(String nome_da_receita) {
        this.nome_receita = nome_da_receita;
    }

    public Bitmap getFoto_da_receita() {
        return foto_da_receita;
    }

    public void setFoto_da_receita(Bitmap foto_da_receita) {
        this.foto_da_receita = foto_da_receita;
    }

    public String getUrl_da_imagem() {
        return url_da_imagem;
    }

    public void setUrl_da_imagem(String url_da_imagem) {
        this.url_da_imagem = url_da_imagem;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public int getTempo_de_preparo() {
        return tempo_de_preparo;
    }

    public void setTempo_de_preparo(int tempo_de_preparo) {
        this.tempo_de_preparo = tempo_de_preparo;
    }

    public int getNivel_de_dificuldade() {
        return nivel_de_dificuldade;
    }

    public void setNivel_de_dificuldade(int nivel_de_dificuldade) {
        this.nivel_de_dificuldade = nivel_de_dificuldade;
    }

    public ArrayList<Parte_da_Receita> getSubpartes() {
        return subpartes;
    }

    public void setSubpartes(ArrayList<Parte_da_Receita> subpartes) {
        this.subpartes = subpartes;
    }
}
