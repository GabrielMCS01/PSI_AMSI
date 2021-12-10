package com.psi.ciclodias.model;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.psi.ciclodias.listeners.CreateCiclismoListener;
import com.psi.ciclodias.listeners.ListaCiclismoListener;
import com.psi.ciclodias.listeners.LoginListener;
import com.psi.ciclodias.listeners.PerfilListener;
import com.psi.ciclodias.listeners.RegistoListener;
import com.psi.ciclodias.utils.CiclismoJsonParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SingletonGestorCiclismo {
    // Variavel para colocar o num do treino selecionado no link
    public static final String ID_TREINO = "id_treino";

    // Variáveis globais para colocar no URL
    public static final String ID = "id";
    public static final String TOKEN = "token";


    // URLs utilizados para acesso á API
    private static final String URL_LOGIN = "http://ciclodias.duckdns.org/admin/v1/login/login";
    private static final String URL_REGISTO = "http://ciclodias.duckdns.org/admin/v1/registo/signup";
    private static final String URL_CICLISMO = "http://ciclodias.duckdns.org/admin/v1/ciclismo";
    private static final String URL_USER = "http://ciclodias.duckdns.org/admin/v1/user";

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
    private PerfilListener perfilListener = null;
    private RegistoListener registoListener = null;
    private CreateCiclismoListener createCiclismoListener = null;

    // Faz de forma sincronizada
    public static synchronized SingletonGestorCiclismo getInstancia(Context context) {
        if (instancia == null) {
            instancia = new SingletonGestorCiclismo(context);
            volleyQueue = Volley.newRequestQueue(context);
        }
        return instancia;
    }

    // Cria a Base de dados local e recebe os todos os treinos do utilizador
    private SingletonGestorCiclismo(Context context) {
        bd = new DBHelp(context);
        ArrCiclismo = bd.getListaCiclismoDB();
        //gerarDadosDinamicos();
    }

    private void gerarDadosDinamicos() {
        ArrCiclismo = new ArrayList<>();
        ArrCiclismo.add(new Ciclismo(1, "Voltinha do banana", 800, 3000, 30.1, 30.7, null, null));
        ArrCiclismo.add(new Ciclismo(2, "Voltinha do forex", 970, 2780, 30.1, 30.7, null, null));
        ArrCiclismo.add(new Ciclismo(3, "Voltinha do banana", 3500, 12000, 30.1, 30.7, null, null));
        ArrCiclismo.add(new Ciclismo(4, "Voltinha do banana", 4221, 13603, 30.1, 30.7, null, null));
        ArrCiclismo.add(new Ciclismo(5, "Voltinha do banana", 5000, 14912, 30.1, 30.7, null, null));
        ArrCiclismo.add(new Ciclismo(6, "Voltinha do banana", 6182, 20192, 30.1, 30.7, null, null));
    }

    // --------------------------- Métodos para BD Local -------------------------------------------
    // Adiciona um treino á BD local
    public void adicionarCiclismoBD(Ciclismo novo) {
        bd.AdicionarCiclismoDB(novo);
    }

    // Atualiza um treino na BD local
    public void atualizarCiclismoDB(Ciclismo ciclismo) {
        bd.editarCiclismoDB(ciclismo);
    }

    // Remove um treino da DB local
    public void apagarCiclismoDB(long id) {
        bd.apagarCiclismoDB(id);
    }

    // Recebe os treinos do utilizador
    private Ciclismo getCiclismo(long id) {
        for (Ciclismo c : ArrCiclismo) {
            if (c.getId() == id)
                return c;
        }

        return null;
    }

    // Retorna a distância dos percursos realizados pelo utilizador (BD local)
    public int getDistancia() {
        int distancia = 0;

        for (Ciclismo c : ArrCiclismo) {
            distancia += c.getDistancia();
        }

        return distancia;
    }

    // Retorna a duração dos percursos realizados pelo utilizador (BD local)
    public int getDuracao() {
        int duracao = 0;

        for (Ciclismo c : ArrCiclismo) {
            duracao += c.getDuracao();
        }

        return duracao;
    }

    // Retorna a velocidade média dos percursos realizados pelo utilizador (BD local)
    public float getVelocidadeMedia() {
        float velMedia = 0;

        for (Ciclismo c : ArrCiclismo) {
            velMedia += c.getVelocidade_media();
        }

        return velMedia / ArrCiclismo.size();
    }

    // Retorna a velocidade máxima dos percursos realizados pelo utilizador (BD local)
    public double getVelocidadeMaxima() {
        double velMaxima = 0;

        for (Ciclismo c : ArrCiclismo) {
            if (velMaxima < c.getVelocidade_maxima()) {
                velMaxima = c.getVelocidade_maxima();
            }
        }

        return velMaxima;
    }

    // Devolve o array ciclismo com todos os treinos dp utilizador 
    public ArrayList<Ciclismo> getArrCiclismo() {
        return ArrCiclismo;
    }

    // ---------------------------------------------------------------------------------------------
    // ------------------------------------- API ---------------------------------------------------
    // ------------------------------------ USER ---------------------------------------------------
    public void getUserDados(final Context context) {
        if (!CiclismoJsonParser.isInternetConnection(context)) {
            Toast.makeText(context, R.string.no_internet, Toast.LENGTH_SHORT).show();
        } else {
            SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);

            String url = URL_USER + "/" + sharedPreferences.getString(ID, "") + "?access-token=" + sharedPreferences.getString(TOKEN, "");

            StringRequest req = new StringRequest(
                    Request.Method.GET,
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            perfilListener.perfilDados(CiclismoJsonParser.ParserUserDados(response));
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

    // Cria um utilizador
    public void CreateUser(final Map<String, String> dadosRegisto,final Context context) {
        if (!CiclismoJsonParser.isInternetConnection(context)) {
            Toast.makeText(context, R.string.no_internet, Toast.LENGTH_SHORT).show();
        } else {
            StringRequest req = new StringRequest(
                    Request.Method.POST,
                    URL_REGISTO,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            registoListener.createUser(CiclismoJsonParser.parserJsonRegisto(response));
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(context, "Erro: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
            ) {
                @Nullable
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = dadosRegisto;

                    return params;
                }
            };

            volleyQueue.add(req);
        }
    }

    // Edita o utilizador
    public void EditUser(final Map<String, String> dadosEdicao,final Context context) {
        if (!CiclismoJsonParser.isInternetConnection(context)) {
            Toast.makeText(context, R.string.no_internet, Toast.LENGTH_SHORT).show();
        } else {
            SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);

            String url = URL_USER + "/" + sharedPreferences.getString(ID, "") + "?access-token=" + sharedPreferences.getString(TOKEN, "");

            StringRequest req = new StringRequest(
                    Request.Method.PUT,
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            perfilListener.editUser(CiclismoJsonParser.parserJsonEditUser(response));
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(context, "Erro: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
            ) {
                @Nullable
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = dadosEdicao;

                    return params;
                }
            };

            volleyQueue.add(req);
        }
    }

    // ------------------------------------- CICLISMO ----------------------------------------------
    public void getListaCiclismoAPI(final Context context) {
        if (!CiclismoJsonParser.isInternetConnection(context)) {
            Toast.makeText(context, R.string.no_internet, Toast.LENGTH_SHORT).show();
        } else {
            SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);

            String url = URL_CICLISMO + "?access-token=" + sharedPreferences.getString(TOKEN, "");

            JsonArrayRequest req = new JsonArrayRequest(
                    Request.Method.GET,
                    url,
                    null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            ArrCiclismo = CiclismoJsonParser.parserJsonListaCiclismo(response);

                            if (listaCiclismoListener != null) {
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

    // Cria um utilizador
    public void AddCiclismo(final Map<String, String> dadosCiclismo,final Context context) {
        if (!CiclismoJsonParser.isInternetConnection(context)) {
            Toast.makeText(context, R.string.no_internet, Toast.LENGTH_SHORT).show();
        } else {
            SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);

            String url = URL_CICLISMO + "?access-token=" + sharedPreferences.getString(TOKEN, "");

            StringRequest req = new StringRequest(
                    Request.Method.POST,
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            createCiclismoListener.createCiclismo(CiclismoJsonParser.parserJsonCriaCiclismo(response));
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(context, "Erro: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
            ) {
                @Nullable
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = dadosCiclismo;

                    return params;
                }
            };

            volleyQueue.add(req);
        }
    }


    // Login pela API
    // Envia uma resposta ao LoginListener onde o login é validado
    public void loginAPI(final String username, final String password, final Context context) {
        if (!CiclismoJsonParser.isInternetConnection(context)) {
            Toast.makeText(context, R.string.no_internet, Toast.LENGTH_SHORT).show();
        } else {
            StringRequest req = new StringRequest(
                    Request.Method.POST,
                    URL_LOGIN,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            System.out.println(response);
                            loginListener.onValidateLogin(CiclismoJsonParser.parserJsonLogin(response), username);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(context, "Erro: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
            ) {
                @Nullable
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();

                    params.put("username", username);
                    params.put("password", password);

                    return params;
                }
            };

            volleyQueue.add(req);
        }
    }

    // Encapsulamento do LoginListener
    public void setLoginListener(LoginListener loginListener) {
        this.loginListener = loginListener;
    }

    public void setListaCiclismoListener(ListaCiclismoListener listaCiclismoListener) {
        this.listaCiclismoListener = listaCiclismoListener;
    }

    public void setPerfilListener(PerfilListener perfilListener) {
        this.perfilListener = perfilListener;
    }

    public void setRegistoListener(RegistoListener registoListener) {
        this.registoListener = registoListener;
    }

    public void setCreateCiclismoListener(CreateCiclismoListener createCiclismoListener) {
        this.createCiclismoListener = createCiclismoListener;
    }
}
