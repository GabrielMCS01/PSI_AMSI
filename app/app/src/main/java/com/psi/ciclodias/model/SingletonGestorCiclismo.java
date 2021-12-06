package com.psi.ciclodias.model;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.psi.ciclodias.R;
import com.psi.ciclodias.listeners.ListaCiclismoListener;
import com.psi.ciclodias.listeners.LoginListener;
import com.psi.ciclodias.utils.CiclismoJsonParser;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SingletonGestorCiclismo {
    private static final String URL_LOGIN = "http://ciclodias.duckdns.org/admin/v1/login/login";
    private static final String URL_REGISTO = "http://ciclodias.duckdns.org/admin/v1/registo/signup";
    private static final String URL_CICLISMO = "http://amsi.dei.estg.ipleiria.pt/api/livros";

    // Array com todos as atividades do utilizador
    ArrayList<Ciclismo> ArrCiclismo;

    // Variável do tipo DB
    private DBHelp bd;

    // Volley
    private static RequestQueue volleyQueue = null;

    private static SingletonGestorCiclismo instancia = null;

    // Listeners
    private LoginListener loginListener = null;
    private ListaCiclismoListener listaCiclismoListener = null;

    // Faz de forma sincronizada
    public static synchronized SingletonGestorCiclismo getInstancia(Context context){
        if (instancia == null){
            instancia = new SingletonGestorCiclismo(context);
            volleyQueue = Volley.newRequestQueue(context);
        }
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
        ArrCiclismo.add(new Ciclismo(1,"Voltinha do banana", 800, 3000, 30.1, 30.7, null, null));
        ArrCiclismo.add(new Ciclismo(2,"Voltinha do forex", 970, 2780, 30.1, 30.7, null, null));
        ArrCiclismo.add(new Ciclismo(3,"Voltinha do banana", 3500, 12000, 30.1, 30.7, null, null));
        ArrCiclismo.add(new Ciclismo(4,"Voltinha do banana", 4221, 13603, 30.1, 30.7, null, null));
        ArrCiclismo.add(new Ciclismo(5,"Voltinha do banana", 5000, 14912, 30.1, 30.7, null, null));
        ArrCiclismo.add(new Ciclismo(6,"Voltinha do banana", 6182, 20192, 30.1, 30.7, null, null));
    }

    // Adiciona um treino á BD local
    public void adicionarCiclismoBD(Ciclismo novo){
        bd.AdicionarCiclismoDB(novo);
    }

    // Atualiza um treino na BD local
    public void atualizarCiclismoDB(Ciclismo ciclismo){
        bd.editarCiclismoDB(ciclismo);
    }

    // Remove um treino da DB local
    public void apagarCiclismoDB(long id){
        bd.apagarCiclismoDB(id);
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

    // APIs
    public void getListaCiclismoAPI(final Context context){
        if(!CiclismoJsonParser.isInternetConnection(context)){
            Toast.makeText(context, R.string.no_internet, Toast.LENGTH_SHORT).show();
        }
        else{
            JsonArrayRequest req = new JsonArrayRequest(
                    Request.Method.GET,
                    URL_CICLISMO,
                    null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            ArrCiclismo = CiclismoJsonParser.parserJsonListaLivros(response);

                            if(listaCiclismoListener != null){
                                listaCiclismoListener.onRefreshListaLivros(ArrCiclismo);
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(context, "Erro: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            volleyQueue.add(req);
        }
    }


    public void loginAPI(final String username, final String password, final Context context){
        if(!CiclismoJsonParser.isInternetConnection(context)){
            Toast.makeText(context, R.string.no_internet, Toast.LENGTH_SHORT).show();
        }
        else{
            StringRequest req = new StringRequest(
                    Request.Method.POST,
                    URL_LOGIN,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            loginListener.onValidateLogin(CiclismoJsonParser.parserJsonLogin(response), username);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(context, "Erro: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
            ){
                @Nullable
                @Override
                protected Map<String, String> getParams(){
                    Map<String, String> params = new HashMap<String, String>();

                    params.put("username", username);
                    params.put("password", password);

                    return params;
                }
            };

            volleyQueue.add(req);
        }
    }

    public void setLoginListener(LoginListener loginListener) {
        this.loginListener = loginListener;
    }

    public void setListaCiclismoListener(ListaCiclismoListener listaCiclismoListener) {
        this.listaCiclismoListener = listaCiclismoListener;
    }
}
