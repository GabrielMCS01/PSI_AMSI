package com.psi.ciclodias.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Date;
import java.time.LocalDate;

public class DBHelp extends SQLiteOpenHelper {
    private static final String DB_NOME = "projectdb";
    private static final int VERSAO = 1;

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

    private static final String TABELA_USERINFO = "user_info";
    private static final String ID_USERINFO = "id";
    private static final String PRIMEIRO_NOME = "primeiro_nome";
    private static final String ULTIMO_NOME = "ultimo_nome";
    private static final String DATA_NASCIMENTO = "data_nascimento";
    private static final String USER_ID_USERINFO = "user_id";

    private static final String USERNAME = "username";
    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";

    private SQLiteDatabase bd;

    public DBHelp(Context context) {
        super(context, DB_NOME, null, VERSAO);

        // Obtem a bd na qual se pretende trabalhar
        bd = getWritableDatabase();
    }

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

        String SQL1 = "CREATE TABLE " + TABELA_USERINFO + "(" +
                ID_USERINFO + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PRIMEIRO_NOME + " TEXT NOT NULL, " +
                ULTIMO_NOME + " TEXT NOT NULL, " +
                DATA_NASCIMENTO + " NUMERIC, " +
                USER_ID_USERINFO + " INTEGER NOT NULL)";
        db.execSQL(SQL1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String SQL = "DROP TABLE IF EXISTS " + TABELA_CICLISMO;
        String SQL1 = "DROP TABLE IF EXISTS " + TABELA_USERINFO;
        db.execSQL(SQL);
        db.execSQL(SQL1);

        this.onCreate(db);
    }

    // métodos CRUD
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

    public boolean editarCiclismoDB(Ciclismo ciclismo){
        ContentValues valores = new ContentValues();

        // O utilizadr apenas pode editar o nome do percurso
        valores.put(NOME_PERCURSO, ciclismo.getNome_percurso());

        // Retorna na Tabela Ciclismo o resultado de editar o treino com o ID enviado
        return bd.update(TABELA_CICLISMO, valores, ID_CICLISMO + " = ?", new String[] {"" + ciclismo.getId()}) > 0;
    }

    public boolean apagarCiclismoDB(long id){
        // Retorna na Tabela Ciclismo o resultado de apagar o treino com o ID enviado
        return bd.delete(TABELA_CICLISMO, ID_CICLISMO + " = ?", new String[] {"" + id}) == 1;
    }


    // métodos CRUD
    public User AdicionarUserDB(User user){
        ContentValues valores = new ContentValues();

        valores.put(USERNAME, user.getUsername());
        valores.put(EMAIL, user.getEmail());
        valores.put(PASSWORD, user.getPassword());
        valores.put(PRIMEIRO_NOME, user.getPrimeiro_nome());
        valores.put(ULTIMO_NOME, user.getUltimo_nome());

        long id = bd.insert(TABELA_CICLISMO, null, valores);

        // Se devolver -1 é porque não conseguiu inserir
        if (id != -1){
            user.setId(id);
            return user;
        }

        return null;
    }

    public User getUser(){
        User user;
        // Query á tabela toda
        Cursor cursor = bd.query(TABELA_CICLISMO, new String[] {USER_ID_USERINFO},
                null, null, null, null, null);

        // Se o encontrar algum faz
        if(cursor.moveToFirst()){
            // Faz enquanto ainda houve mais treinos
            user = new User(cursor.getString(0),
            cursor.getString(1),
            // ---------------------------------------
            cursor.getString(2),
            cursor.getString(3),
            cursor.getString(4));

            cursor.close();

            return user;
        }

        cursor.close();
        return null;
    }
}
