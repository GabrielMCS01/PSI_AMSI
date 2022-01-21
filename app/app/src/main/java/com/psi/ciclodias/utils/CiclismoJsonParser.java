package com.psi.ciclodias.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.gson.JsonArray;
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
    public static ArrayList<Ciclismo> parserJsonListaCiclismo(String resposta) {
        ArrayList<Ciclismo> lista = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(resposta);

            // Caso a resposta seja sucesso faz
            if(jsonObject.getBoolean("success")) {
                JSONArray array = jsonObject.getJSONArray("ciclismo");

                // Para cada treino do utilizador faz
                for (int i = 0; i < array.length(); i++) {
                    JSONObject jsonCiclismo = array.getJSONObject(i);

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

                    // Cria um novo treino com os dados recebidos desse treino
                    Ciclismo ciclismo = new Ciclismo(id, nome_percurso, duracao, distancia, velocidade_media, velocidade_maxima, velocidade_grafico, rota, data_treino);

                    // Adiciona o treino á lista de treinos
                    lista.add(ciclismo);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return lista;
    }

    // Guardar um Treino que veio da API
    public static Ciclismo parserJsonCriaCiclismo(String response) {
        try {
            JSONObject json = new JSONObject(response);

            // Se a API guardou os dados do treino com sucesso
            if(json.getBoolean("success")) {
                JSONObject treino = json.getJSONObject("ciclismo");

                // Recebe os valores JSON vindos da API
                int id = treino.getInt("id");
                String nome_percurso = treino.getString("nome_percurso");
                int duracao = treino.getInt("duracao");
                int distancia = treino.getInt("distancia");
                double velocidade_media = treino.getDouble("velocidade_media");
                double velocidade_maxima = treino.getDouble("velocidade_maxima");

                // velocidade grafico é JSON
                String velocidade_grafico = treino.getString("velocidade_grafico");
                String rota = treino.getString("rota");
                String data_treino = treino.getString("data_treino");

                // Cria um treino com os dados desse treino
                Ciclismo ciclismo = new Ciclismo(id, nome_percurso, duracao, distancia, velocidade_media, velocidade_maxima, velocidade_grafico, rota, data_treino);

                return ciclismo;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    // ----------------------------------- lOGIN ---------------------------------------------------
    // Retorna o token de login do utilizador que fez login
    public static Map<String, String> parserJsonLogin(String resposta) {
        Map<String, String> dadosUser = new HashMap<String, String>();

        try {
            JSONObject jsonLogin = new JSONObject(resposta);

            // Se o pedido á API foi feito com sucesso
            if (jsonLogin.getBoolean("success")) {
                dadosUser.put("id", jsonLogin.getString("id"));
                dadosUser.put("token", jsonLogin.getString("token"));
                dadosUser.put("primeiro_nome", jsonLogin.getString("primeiro_nome"));
                dadosUser.put("ultimo_nome", jsonLogin.getString("ultimo_nome"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return dadosUser;
    }

    // ----------------------------------- INTERNET ------------------------------------------------
    public static boolean isInternetConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    // ------------------------------------- USER --------------------------------------------------
    // Retorna os dados do utilizador para preencher no perfil
    public static Map<String, String> ParserUserDados(String resposta) {
        Map<String, String> dadosUser = new HashMap<String, String>();

        try {
            JSONObject jsonUser = new JSONObject(resposta);

            // Se a API devolveu dados do utilizador com sucesso
            if(jsonUser.getBoolean("success")) {
                dadosUser.put("primeiro_nome", jsonUser.getString("primeiro_nome"));
                dadosUser.put("ultimo_nome", jsonUser.getString("ultimo_nome"));
                dadosUser.put("data_nascimento", jsonUser.getString("data_nascimento"));
            }else {
                dadosUser.put("mensagem", jsonUser.getString("mensagem"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return dadosUser;
    }

    // Parser default para receber se o pedido foi feito á API com sucesso ou não
    public static Boolean parserJsonSuccess(String response) {
        boolean success = false;

        try {
            JSONObject json = new JSONObject(response);

            success = json.getBoolean("success");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return success;
    }

    // Cria um JSON array com uma lista de treinos
    public static JSONArray createJsonArray(ArrayList<Ciclismo> ciclismoArrayList) {
        JSONArray jsonArray = new JSONArray();
        try {
            for (Ciclismo ciclismo : ciclismoArrayList) {
                JSONObject json = new JSONObject();
                json.put("nome_percurso", ciclismo.getNome_percurso());
                json.put("duracao", ciclismo.getDuracao());
                json.put("distancia", ciclismo.getDistancia());
                json.put("velocidade_media", ciclismo.getVelocidade_media());
                json.put("velocidade_maxima", ciclismo.getVelocidade_maxima());
                json.put("velocidade_grafico", ciclismo.getVelocidade_grafico());
                if(ciclismo.getRota() == null){
                    json.put("rota", "");
                }else {
                    json.put("rota", ciclismo.getRota());
                }
                json.put("data_treino", ciclismo.getData_treino());

                jsonArray.put(json);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonArray;
    }

    //-------------------------------------- Velocidade --------------------------------------------
    // Converte arraylist para json
    public static JsonArray createJsonVelocity(ArrayList<Float> arrayList){
        // Cria um JSON array com todas as velocidades do treino guardadas
        JsonArray jsonArray = new JsonArray();

        for(float velocity: arrayList){
            jsonArray.add(velocity);
        }

        return jsonArray;
    }

    //-------------------------------------- Publicação --------------------------------------------
    // Verifica se publicação foi criada com sucesso para devolver uma mensagem no ecrã
    public static String parserJsonCriaPublicacao(String response) {
        try {
            JSONObject json = new JSONObject(response);

            if(json.getBoolean("success")){
                return "Publicação Criada";
            }else{
                return json.getString("mensagem");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "Erro";
    }
}
