package com.psi.ciclodias.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
}
