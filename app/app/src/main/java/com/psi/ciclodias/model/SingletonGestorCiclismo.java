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
import com.psi.ciclodias.listeners.CiclismoListener;
import com.psi.ciclodias.listeners.CreateCiclismoListener;
import com.psi.ciclodias.listeners.ListaCiclismoListener;
import com.psi.ciclodias.listeners.LoginListener;
import com.psi.ciclodias.listeners.PerfilListener;
import com.psi.ciclodias.listeners.RegistoListener;
import com.psi.ciclodias.utils.CiclismoJsonParser;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SingletonGestorCiclismo {
    // Variáveis globais para colocar no URL
    public static final String ID = "id";
    public static final String TOKEN = "token";

    // URLs utilizados para acesso á API
    private static final String URL_LOGIN = "http://ciclodias.duckdns.org/admin/v1/login/login";
    private static final String URL_REGISTO = "http://ciclodias.duckdns.org/admin/v1/registo/signup";
    private static final String URL_CICLISMO = "http://ciclodias.duckdns.org/admin/v1/ciclismo";
    private static final String URL_USER = "http://ciclodias.duckdns.org/admin/v1/user";

    // Arraylist com todos as atividades do utilizador
    public ArrayList<Ciclismo> ArrCiclismo;

    // Arraylist com todas as atividades do utilizador que não estão sincronizadas
    public ArrayList<Ciclismo> ArrCiclismoUnSync = new ArrayList<>();

    // Variável do tipo DB
    private DBHelp bd;

    // Volley
    private static RequestQueue volleyQueue = null;

    // Instancia vazia da singleton
    private static SingletonGestorCiclismo instancia = null;

    // Listeners
    private LoginListener loginListener = null;
    private ListaCiclismoListener listaCiclismoListener = null;
    private PerfilListener perfilListener = null;
    private RegistoListener registoListener = null;
    private CreateCiclismoListener createCiclismoListener = null;
    private CiclismoListener ciclismoListener = null;

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
    }

    // --------------------------- Métodos para BD Local -------------------------------------------
    // Adiciona um treino á BD local
    public void adicionarCiclismoBD(Ciclismo novo) {
        bd.AdicionarCiclismoDB(novo);
    }

    public long adicionarCiclismoDBUnSync(Ciclismo novo){
        return bd.AdicionarCiclismoDBUnSync(novo);
    }

    // Atualiza um treino na BD local
    public void atualizarCiclismoDB(long id, String nome) {
        bd.editarCiclismoDB(id, nome);
    }

    // Remove um treino da DB local
    public void apagarCiclismoDB(long id) {
        bd.apagarCiclismoDB(id);
    }

    public void apagarCiclismoDBAll() {
        bd.apagarCiclismoDBAll();
    }

    // Recebe os treinos do utilizador
    public Ciclismo getCiclismo(int id) {
        Ciclismo ciclismo = ArrCiclismo.get(id);

        return ciclismo;
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
        ArrCiclismo = bd.getListaCiclismoDB();

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
                            registoListener.createUser(CiclismoJsonParser.parserJsonSuccess(response));
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
    public void EditUser(final Map<String, String> dadosEdicao, final Context context) {
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
                            perfilListener.editUser(CiclismoJsonParser.parserJsonSuccess(response));
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

    // Apaga o utilizador
    public void DeleteUser(final Context context) {
        if (!CiclismoJsonParser.isInternetConnection(context)) {
            Toast.makeText(context, R.string.no_internet, Toast.LENGTH_SHORT).show();
        } else {
            SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);

            String url = URL_USER + "/" + sharedPreferences.getString(ID, "") + "?access-token=" + sharedPreferences.getString(TOKEN, "");

            StringRequest req = new StringRequest(
                    Request.Method.DELETE,
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (CiclismoJsonParser.parserJsonSuccess(response)) {
                                perfilListener.removeUser(true);
                            }
                            else perfilListener.removeUser(false);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(context, "Erro: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
            );

            volleyQueue.add(req);
        }
    }

    // ------------------------------------- CICLISMO ----------------------------------------------
    public void getListaCiclismoAPI(final Context context) {
        if (!CiclismoJsonParser.isInternetConnection(context)) {
            Toast.makeText(context, R.string.no_internet, Toast.LENGTH_SHORT).show();
        } else {
            // Verifica se a BD local está vazia (Login pela primeira vez ou sem treinos)
            if (getArrCiclismo().size() == 0) {
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

                                // Recebe todos os treinos da API e cria-os na BD local
                                for (Ciclismo c : ArrCiclismo) {
                                    c = new Ciclismo(c.getId(), c.getNome_percurso(), c.getDuracao(), c.getDistancia(), c.getVelocidade_media(), c.getVelocidade_maxima(), c.getVelocidade_grafico(), c.getRota(), c.getData_treino());
                                    adicionarCiclismoBD(c);
                                }

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
            // Verifica se existem treinos para sincronizar com a API
            else if (ArrCiclismoUnSync.size() != 0){
                SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);

                String url = URL_CICLISMO + "/sync?access-token=" + sharedPreferences.getString(TOKEN, "");

                JSONArray json = CiclismoJsonParser.createJsonArray(ArrCiclismoUnSync);

                System.out.println(json);

                JsonArrayRequest req = new JsonArrayRequest(
                        Request.Method.POST,
                        url,
                        json,
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                ArrCiclismo = CiclismoJsonParser.parserJsonListaCiclismo(response);

                                // Apaga os treinos todos
                                apagarCiclismoDBAll();

                                for (Ciclismo c : ArrCiclismo) {
                                    c = new Ciclismo(c.getId(), c.getNome_percurso(), c.getDuracao(), c.getDistancia(), c.getVelocidade_media(), c.getVelocidade_maxima(), c.getVelocidade_grafico(), c.getRota(), c.getData_treino());
                                    adicionarCiclismoBD(c);
                                }

                                ArrCiclismoUnSync = new ArrayList<>();

                                if (listaCiclismoListener != null) {
                                    listaCiclismoListener.onRefreshListaLivros(ArrCiclismo);
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(context, "Erro: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                );

                volleyQueue.add(req);
            }
        }
    }



    // Cria um Ciclismo
    public void AddCiclismo(final Map<String, String> dadosCiclismo,final Context context) {
        if (!CiclismoJsonParser.isInternetConnection(context)) {
            // Cria localmente se não tiver Internet
            Toast.makeText(context, R.string.no_internet, Toast.LENGTH_SHORT).show();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
            String date = sdf.format(new Date());

            Ciclismo c = new Ciclismo(dadosCiclismo.get("nome_percurso"),
                    Integer.parseInt(dadosCiclismo.get("duracao")),
                    Integer.parseInt(dadosCiclismo.get("distancia")),
                    Float.parseFloat(dadosCiclismo.get("velocidade_media")),
                    Float.parseFloat(dadosCiclismo.get("velocidade_maxima")),
                    null,
                    dadosCiclismo.get("rota"),
                    date);

            c.setUser_id_ciclismo(-1);

            bd.AdicionarCiclismoDBUnSync(c);

            ArrCiclismo = getArrCiclismo();
            // Adiciona no Arraylist do treinos para sincronizar
            ArrCiclismoUnSync.add(c);

            createCiclismoListener.createCiclismo(c);
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

    // Edita o nome da sessão de treino
    public void EditCiclismo(final String dadosEdicao, long id, final Context context) {
        if (!CiclismoJsonParser.isInternetConnection(context)) {
            Toast.makeText(context, R.string.no_internet, Toast.LENGTH_SHORT).show();
        } else {
            SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);

            String url = URL_CICLISMO + "/" + id + "?access-token=" + sharedPreferences.getString(TOKEN, "");

            StringRequest req = new StringRequest(
                    Request.Method.PUT,
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (CiclismoJsonParser.parserJsonSuccess(response)) {
                                atualizarCiclismoDB(id, dadosEdicao);

                                ciclismoListener.editCiclismo(true);
                            }
                            else ciclismoListener.editCiclismo(false);
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
                    Map<String, String> params = new HashMap<>();
                    params.put("nome_percurso", dadosEdicao);

                    return params;
                }
            };

            volleyQueue.add(req);
        }
    }

    // Apaga uma sessão de treino
    public void DeleteCiclismo(long id, final Context context) {
        if (!CiclismoJsonParser.isInternetConnection(context)) {
            Toast.makeText(context, R.string.no_internet, Toast.LENGTH_SHORT).show();
        } else {
            SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);

            String url = URL_CICLISMO + "/" + id + "?access-token=" + sharedPreferences.getString(TOKEN, "");

            StringRequest req = new StringRequest(
                    Request.Method.DELETE,
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (CiclismoJsonParser.parserJsonSuccess(response)) {
                                apagarCiclismoDB(id);

                                ciclismoListener.removeCiclismo(true);
                            }
                            else ciclismoListener.removeCiclismo(false);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(context, "Erro: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
            );
            volleyQueue.add(req);
        }
    }


    // ------------------------------------- LOGIN -------------------------------------------------
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

    // --------------------------------- Setters dos Listeners -------------------------------------
    // Encapsulamento
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

    public void setCiclismoListener(CiclismoListener ciclismoListener) {
        this.ciclismoListener = ciclismoListener;
    }

    // ---------------------------------- OUTROS METODOS -------------------------------------------
    // Preencher o array Unsync quando se entra na aplicação para verificar se existem treinos que
    // ainda não foram enviados para a API
    public void PreencherArrCiclismoUnsync() {
        if(ArrCiclismoUnSync.size() == 0) {
            ArrayList<Ciclismo> arrayList = new ArrayList<>();
            arrayList = bd.getListaCiclismoDB();

            if (arrayList.size() != 0) {
                for (Ciclismo ciclismo : arrayList) {
                    if (ciclismo.getUser_id_ciclismo() == -1) {
                        ArrCiclismoUnSync.add(ciclismo);
                    }
                }
            }
        }
    }
}
