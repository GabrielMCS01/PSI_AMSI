package com.psi.ciclodias.model;

import android.content.Context;

import com.psi.ciclodias.R;

import java.util.ArrayList;

public class SingletonGestorCiclismo {

    ArrayList<Ciclismo> ArrCiclismo;

    // Variável do tipo DB
    private DBHelp bd;

    private static SingletonGestorCiclismo instancia = null;

    // Faz de forma sincronizada
    public static synchronized SingletonGestorCiclismo getInstancia(Context context){
        if (instancia == null){ instancia = new SingletonGestorCiclismo(context);}
        return instancia;
    }

    // Cria a base de dados local e recebe os todos os treinos do utilizador
    private SingletonGestorCiclismo(Context context){
        bd = new DBHelp(context);
        ArrCiclismo = bd.getListaCiclismoDB();
        gerarDadosDinamicos();
    }

    private void gerarDadosDinamicos() {
        ArrCiclismo = new ArrayList<>();
        ArrCiclismo.add(new Ciclismo("Voltinha do banana", "00:15:30", 20.1f, 30.1f, 30.7f, null, null));
        ArrCiclismo.add(new Ciclismo("Voltinha do banana", "00:15:30", 20.1f, 30.1f, 30.7f, null, null));
        ArrCiclismo.add(new Ciclismo("Voltinha do banana", "00:15:30", 20.1f, 30.1f, 30.7f, null, null));
        ArrCiclismo.add(new Ciclismo("Voltinha do banana", "00:15:30", 20.1f, 30.1f, 30.7f, null, null));
        ArrCiclismo.add(new Ciclismo("Voltinha do banana", "00:15:30", 20.1f, 30.1f, 30.7f, null, null));
        ArrCiclismo.add(new Ciclismo("Voltinha do banana", "00:15:30", 20.1f, 30.1f, 30.7f, null, null));
    }

    // Adiciona um treino á BD local
    public void adicionarCiclismoBD(Ciclismo novo){
        Ciclismo ciclismo = bd.AdicionarCiclismoDB(novo);
        if(ciclismo != null) {
            ArrCiclismo.add(ciclismo);
        }
    }

    // Atualiza um treino na BD local
    public void atualizarCiclismoDB(Ciclismo ciclismo){
        if(bd.editarCiclismoDB(ciclismo)) {
            Ciclismo original = getCiclismo(ciclismo.getId());

            // Caso exista um item para editar
            if (original != null) {
                original.setNome_percurso(ciclismo.getNome_percurso());
                original.setDuracao(ciclismo.getDuracao());
                original.setDistancia(ciclismo.getDistancia());
                original.setVelocidade_media(ciclismo.getVelocidade_media());
            }
        }
    }

    // Remove um treino da DB local
    public void apagarCiclismoDB(long id){
        if(bd.apagarCiclismoDB(id)) {
            Ciclismo ciclismo = getCiclismo(id);
            if (ciclismo != null) {
                ArrCiclismo.remove(ciclismo);
            }
        }
    }

    // Recebe os treinos do utilizador
    private Ciclismo getCiclismo(long id){
        for (Ciclismo c: ArrCiclismo){
            if (c.getId() == id)
                return c;
        }

        return null;
    }

    // Devolve o array ciclismo com todos os treinos dp utilizador 
    public ArrayList<Ciclismo> getArrCiclismo(){
        return ArrCiclismo;
    }
}
