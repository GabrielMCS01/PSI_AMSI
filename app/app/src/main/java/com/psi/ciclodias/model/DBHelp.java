package com.psi.ciclodias.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBHelp extends SQLiteOpenHelper {
    // Nome da DB e versão da DB SQLITE
    private static final String DB_NOME = "projectdb";
    private static final int VERSAO = 1;

    // Tabela Ciclismo
    private static final String TABELA_CICLISMO = "ciclismo";
    private static final String ID_CICLISMO = "id";
    private static final String NOME_PERCURSO = "nome_percurso";
    private static final String DURACAO = "duracao";
    private static final String DISTANCIA = "distancia";
    private static final String VELOCIDADE_MEDIA = "velocidade_media";
    private static final String VELOCIDADE_MAXIMA = "velocidade_maxima";
    private static final String VELOCIDADE_GRAFICO = "velocidade_grafico";
    private static final String ROTA = "rota";
    private static final String DATA_TREINO = "data_treino";
    private static final String USER_ID_CICLISMO = "user_id";

    // Tabela USER_INFO
    private static final String TABELA_USERINFO = "user_info";
    private static final String ID_USERINFO = "id";
    private static final String PRIMEIRO_NOME = "primeiro_nome";
    private static final String ULTIMO_NOME = "ultimo_nome";
    private static final String DATA_NASCIMENTO = "data_nascimento";
    private static final String USER_ID_USERINFO = "user_id";

    // Tabela USER
    private static final String USERNAME = "username";
    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";

    private SQLiteDatabase bd;

    // URL Padrão
    private String url ="http://localhost/PSI_PlatSI/app/backend/web/v1";

    public DBHelp(Context context) {
        super(context, DB_NOME, null, VERSAO);

        // Obtem a bd na qual se pretende trabalhar
        bd = getWritableDatabase();
    }

    // Crias as tabelas na DB enviada
    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL = "CREATE TABLE " + TABELA_CICLISMO + "(" +
                ID_CICLISMO + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NOME_PERCURSO + " TEXT, " +
                DURACAO + " NUMERIC NOT NULL, " +
                DISTANCIA + " REAL NOT NULL, " +
                VELOCIDADE_MEDIA + " REAL NOT NULL, " +
                VELOCIDADE_MAXIMA + " REAL NOT NULL, " +
                VELOCIDADE_GRAFICO + " TEXT, " +
                ROTA + " TEXT, " +
                DATA_TREINO + " NUMERIC NOT NULL, " +
                USER_ID_CICLISMO + " INTEGER NOT NULL)";
        db.execSQL(SQL);
    }

    // Apaga as tabelas existentes e recria-as no método onCreate
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String SQL = "DROP TABLE IF EXISTS " + TABELA_CICLISMO;
        db.execSQL(SQL);

        this.onCreate(db);
    }

    // ------------------------------------- Ciclismo -----------------------------------------------------------------
    // Cria uma nova sessão de treino
    public Ciclismo AdicionarCiclismoDB(Ciclismo ciclismo){
        ContentValues valores = new ContentValues();

        valores.put(NOME_PERCURSO, ciclismo.getNome_percurso());
        valores.put(DURACAO, "" + ciclismo.getDuracao());
        valores.put(DISTANCIA, ciclismo.getDistancia());
        valores.put(VELOCIDADE_MEDIA, ciclismo.getVelocidade_media());
        valores.put(VELOCIDADE_MAXIMA, ciclismo.getVelocidade_maxima());
        valores.put(VELOCIDADE_GRAFICO, ciclismo.getVelocidade_grafico());
        valores.put(ROTA, ciclismo.getRota());
        valores.put(DATA_TREINO, "" + ciclismo.getData_treino());

        long id = bd.insert(TABELA_CICLISMO, null, valores);

        // Se devolver -1 é porque não conseguiu inserir
        if (id != -1){
            ciclismo.setId(id);
            return ciclismo;
        }

        return null;
    }

    // Retorna todas as sessões de treino do utilizador
    public ArrayList<Ciclismo> getListaCiclismoDB(){
        ArrayList<Ciclismo> lista = new ArrayList<>();

        // Query á tabela toda
        Cursor cursor = bd.query(TABELA_CICLISMO, new String[] {ID_CICLISMO, NOME_PERCURSO, DURACAO, DISTANCIA, VELOCIDADE_MEDIA,
                        VELOCIDADE_MAXIMA, VELOCIDADE_GRAFICO, ROTA, DATA_TREINO, USER_ID_CICLISMO},
                null, null, null, null, null);

        // Se o encontrar algum faz
        if(cursor.moveToFirst()){
            // Faz enquanto ainda houve mais treinos
            do {
                Ciclismo aux = new Ciclismo(cursor.getString(0),
                        // Tipo de data está em String por agora
                        cursor.getString(1),
                        // ---------------------------------------
                        cursor.getFloat(2),
                        cursor.getFloat(3),
                        cursor.getFloat(4),
                        cursor.getString(5),
                        cursor.getString(6));
                lista.add(aux);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return lista;
    }

    // Retorna uma sessão de treino detalhada
    public Ciclismo getCiclismoDB(){
        // Query á tabela para procurar os detalhes do treino selecionado
        Cursor cursor = bd.query(TABELA_CICLISMO, new String[] {ID_CICLISMO, NOME_PERCURSO, DURACAO, DISTANCIA, VELOCIDADE_MEDIA,
                        VELOCIDADE_MAXIMA, VELOCIDADE_GRAFICO, ROTA, DATA_TREINO, USER_ID_CICLISMO},
                null, null, null, null, null);

        // Se encontrar algum treino faz
        if(cursor.moveToFirst()){
            Ciclismo ciclismo = new Ciclismo(cursor.getString(0),
                // Tipo de data está em String por agora
                cursor.getString(1),
                // ---------------------------------------
                cursor.getFloat(2),
                cursor.getFloat(3),
                cursor.getFloat(4),
                cursor.getString(5),
                cursor.getString(6));

                cursor.close();

                return ciclismo;
        }

        cursor.close();
        return null;
    }

    // Edita uma sessão de treino
    public boolean editarCiclismoDB(Ciclismo ciclismo){
        ContentValues valores = new ContentValues();

        // O utilizadr apenas pode editar o nome do percurso
        valores.put(NOME_PERCURSO, ciclismo.getNome_percurso());

        // Retorna na Tabela Ciclismo o resultado de editar o treino com o ID enviado
        return bd.update(TABELA_CICLISMO, valores, ID_CICLISMO + " = ?", new String[] {"" + ciclismo.getId()}) > 0;
    }

    // Apaga uma sessão de treino
    public boolean apagarCiclismoDB(long id){
        // Retorna na Tabela Ciclismo o resultado de apagar o treino com o ID enviado
        return bd.delete(TABELA_CICLISMO, ID_CICLISMO + " = ?", new String[] {"" + id}) == 1;
    }


    // ---------------------------------------------- USER -----------------------------------------------------------------------
    // Código para adaptar para quando se implementar a API

    // Cria um utilizador enviando os dados inseridos para a API
    /*public User AdicionarUserDB(User user){
        ContentValues valores = new ContentValues();

        // Campos preenchidas na APP android a criar um USER
        valores.put(USERNAME, user.getUsername());
        valores.put(EMAIL, user.getEmail());
        valores.put(PASSWORD, user.getPassword());
        valores.put(PRIMEIRO_NOME, user.getPrimeiro_nome());
        valores.put(ULTIMO_NOME, user.getUltimo_nome());

        // Enviar os dados para a API

        return null;
    }

    // Edita o próprio utilizador
    public boolean editarUserDB(User user){
        ContentValues valores = new ContentValues();

        // O utilizadr pode editar o seu Email, DataNascimento, PrimeiroNome e UltimoNome
        valores.put(EMAIL, user.getEmail());
        valores.put(DATA_NASCIMENTO, user.getData_nascimento());
        valores.put(PRIMEIRO_NOME, user.getPrimeiro_nome());
        valores.put(ULTIMO_NOME, user.getUltimo_nome());

        // Envia dados para a API

        return true;
    }

    // Apaga o utilizador com login feito (auth_key)
    public boolean apagarUserDB(long id){
        // Retorna na Tabela Ciclismo o resultado de apagar o treino com o ID enviado

        return true;
    }*/


}
