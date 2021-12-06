package com.psi.ciclodias.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.psi.ciclodias.model.Ciclismo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CiclismoJsonParser {
    // Consulta á API para devolver todos os livros da API
    public static ArrayList<Ciclismo> parserJsonListaLivros(JSONArray resposta){
        ArrayList<Ciclismo> lista = new ArrayList<>();

        try {
            for (int i = 0; i < resposta.length(); i++) {
                JSONObject jsonCiclismo = resposta.getJSONObject(i);

                // Converte os dados JSON para as variáveis locais para a criação da Atividade (Ciclismo)
                int id = jsonCiclismo.getInt("id");
                String nome_percurso = jsonCiclismo.getString("nome_percurso");
                int duracao = jsonCiclismo.getInt("duracao");
                int distancia = jsonCiclismo.getInt("distancia");
                double velocidade_media = jsonCiclismo.getDouble("velocidade_media");
                double velocidade_maxima = jsonCiclismo.getDouble("velocidade_maxima");

                // velocidade grafico é JSON
                String velocidade_grafico = jsonCiclismo.getString("velocidade_grafico");
                String rota = jsonCiclismo.getString("rota");

                Ciclismo ciclismo = new Ciclismo(id, nome_percurso, duracao, distancia, velocidade_media, velocidade_maxima, velocidade_grafico, rota);
                lista.add(ciclismo);
            }

        }catch (JSONException e) {
            e.printStackTrace();
        }

        return lista;
    }

    // Retorna um Livro da API
    public static Ciclismo parserJsonCiclismo(String resposta){
        Ciclismo ciclismo = null;

        try {
            JSONObject jsonCiclismo = new JSONObject(resposta);

            // Converte os dados JSON para as variáveis locais para a criação da Atividade (Ciclismo)
            int id = jsonCiclismo.getInt("id");
            String nome_percurso = jsonCiclismo.getString("nome_percurso");
            int duracao = jsonCiclismo.getInt("duracao");
            int distancia = jsonCiclismo.getInt("distancia");
            double velocidade_media = jsonCiclismo.getDouble("velocidade_media");
            double velocidade_maxima = jsonCiclismo.getDouble("velocidade_maxima");

            // velocidade grafico é JSON
            String velocidade_grafico = jsonCiclismo.getString("velocidade_grafico");
            String rota = jsonCiclismo.getString("rota");

            ciclismo = new Ciclismo(id, nome_percurso, duracao, distancia, velocidade_media, velocidade_maxima, velocidade_grafico, rota);
        }catch (JSONException e) {
            e.printStackTrace();
        }

        return ciclismo;
    }

    // Retorna o token de login
    public static String parserJsonLogin(String resposta){
        String token = null;
        try{
            JSONObject jsonLogin = new JSONObject(resposta);
            if(jsonLogin.getBoolean("success")){
                token = jsonLogin.getString("token");
            }

        }catch (JSONException e){
            e.printStackTrace();
        }

        return token;
    }

    public static boolean isInternetConnection(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }
}
