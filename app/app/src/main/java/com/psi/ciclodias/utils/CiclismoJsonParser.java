package com.psi.ciclodias.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.psi.ciclodias.model.Ciclismo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CiclismoJsonParser {
    // --------------------------------- CICLISMO --------------------------------------------------
    // Consulta á API para devolver todos os treinos do utilizador da API
    public static ArrayList<Ciclismo> parserJsonListaCiclismo(JSONArray resposta){
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
                String data_treino = jsonCiclismo.getString("data_treino");

                Ciclismo ciclismo = new Ciclismo(id, nome_percurso, duracao, distancia, velocidade_media, velocidade_maxima, velocidade_grafico, rota, data_treino);
                lista.add(ciclismo);
            }

        }catch (JSONException e) {
            e.printStackTrace();
        }

        return lista;
    }

    // Guarda um Treino na API
    public static String parserJsonCriaCiclismo(String resposta){
        return resposta;
    }

    // ----------------------------------- lOGIN ---------------------------------------------------
    // Retorna o token de login
    public static Map<String, String> parserJsonLogin(String resposta){
        Map<String, String> dadosUser = new HashMap<String, String>();

        try{
            JSONObject jsonLogin = new JSONObject(resposta);
            if(jsonLogin.getBoolean("success")){
                dadosUser.put("id", jsonLogin.getString("id"));
                dadosUser.put("token", jsonLogin.getString("token"));
                dadosUser.put("primeiro_nome", jsonLogin.getString("primeiro_nome"));
                dadosUser.put("ultimo_nome", jsonLogin.getString("ultimo_nome"));
            }

        }catch (JSONException e){
            e.printStackTrace();
        }

        return dadosUser;
    }

    // ----------------------------------- INTERNET ------------------------------------------------
    public static boolean isInternetConnection(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    // ------------------------------------- USER --------------------------------------------------
    // Retorna os dados utilizador para preencher no perfil
    public static Map<String, String> ParserUserDados(String resposta){
        Map<String, String> dadosUser = new HashMap<String, String>();

        try{
            JSONObject jsonUser = new JSONObject(resposta);

            dadosUser.put("primeiro_nome", jsonUser.getString("primeiro_nome"));
            dadosUser.put("ultimo_nome", jsonUser.getString("ultimo_nome"));
            dadosUser.put("data_nascimento", jsonUser.getString("data_nascimento"));

        }catch (JSONException e){
            e.printStackTrace();
        }

        return dadosUser;
    }
}
