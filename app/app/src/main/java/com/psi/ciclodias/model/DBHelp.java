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

    private SQLiteDatabase bd;

    public DBHelp(Context context) {
        super(context, DB_NOME, null, VERSAO);

        // Obtém a BD na qual se pretende trabalhar
        bd = getWritableDatabase();
    }

    // Cria a tabela na DB criada
    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL = "CREATE TABLE " + TABELA_CICLISMO + "(" +
                ID_CICLISMO + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NOME_PERCURSO + " TEXT, " +
                DURACAO + " INTEGER NOT NULL, " +
                DISTANCIA + " INTEGER NOT NULL, " +
                VELOCIDADE_MEDIA + " REAL NOT NULL, " +
                VELOCIDADE_MAXIMA + " REAL NOT NULL, " +
                VELOCIDADE_GRAFICO + " TEXT, " +
                ROTA + " TEXT, " +
                DATA_TREINO + " NUMERIC, " +
                USER_ID_CICLISMO + " INTEGER)";
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
    public void AdicionarCiclismoDB(Ciclismo ciclismo){
        ContentValues valores = new ContentValues();

        valores.put(ID_CICLISMO, ciclismo.getId());
        valores.put(NOME_PERCURSO, ciclismo.getNome_percurso());
        valores.put(DURACAO, ciclismo.getDuracao());
        valores.put(DISTANCIA, ciclismo.getDistancia());
        valores.put(VELOCIDADE_MEDIA, ciclismo.getVelocidade_media());
        valores.put(VELOCIDADE_MAXIMA, ciclismo.getVelocidade_maxima());
        valores.put(VELOCIDADE_GRAFICO, ciclismo.getVelocidade_grafico());
        valores.put(ROTA, ciclismo.getRota());
        valores.put(DATA_TREINO, ciclismo.getData_treino());
        valores.put(USER_ID_CICLISMO, ciclismo.getUser_id_ciclismo());

        bd.insert(TABELA_CICLISMO, null, valores);
    }

    // Cria uma nova sessão de treino que irá ficar por sincronizar
    public long AdicionarCiclismoDBUnSync(Ciclismo ciclismo) {
        ContentValues valores = new ContentValues();

        valores.put(ID_CICLISMO, ciclismo.getId());
        valores.put(NOME_PERCURSO, ciclismo.getNome_percurso());
        valores.put(DURACAO, ciclismo.getDuracao());
        valores.put(DISTANCIA, ciclismo.getDistancia());
        valores.put(VELOCIDADE_MEDIA, ciclismo.getVelocidade_media());
        valores.put(VELOCIDADE_MAXIMA, ciclismo.getVelocidade_maxima());
        valores.put(VELOCIDADE_GRAFICO, ciclismo.getVelocidade_grafico());
        valores.put(ROTA, ciclismo.getRota());
        valores.put(DATA_TREINO, ciclismo.getData_treino());
        valores.put(USER_ID_CICLISMO, ciclismo.getUser_id_ciclismo());

        return bd.insert(TABELA_CICLISMO, null, valores);
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
            // Faz enquanto ainda houver mais treinos
            do {
                Ciclismo aux = new Ciclismo(cursor.getLong(0),
                        cursor.getString(1),
                        cursor.getInt(2),
                        cursor.getInt(3),
                        cursor.getFloat(4),
                        cursor.getFloat(5),
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getString(8));
                aux.setUser_id_ciclismo(cursor.getLong(9));
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
            Ciclismo ciclismo = new Ciclismo(cursor.getLong(0),
                    cursor.getString(1),
                    cursor.getInt(2),
                    cursor.getInt(3),
                    cursor.getFloat(4),
                    cursor.getFloat(5),
                    cursor.getString(6),
                    cursor.getString(7),
                    cursor.getString(8));
                ciclismo.setUser_id_ciclismo(cursor.getLong(9));
                cursor.close();

                return ciclismo;
        }

        cursor.close();
        return null;
    }

    // Edita uma sessão de treino
    public boolean editarCiclismoDB(long id, String nome){
        ContentValues valores = new ContentValues();

        // O utilizador apenas pode editar o nome do percurso
        valores.put(NOME_PERCURSO, nome);

        // Retorna na Tabela Ciclismo o resultado de editar o treino com o ID enviado
        return bd.update(TABELA_CICLISMO, valores, ID_CICLISMO + " = ?", new String[] {"" + id}) > 0;
    }

    // Apaga uma sessão de treino
    public boolean apagarCiclismoDB(long id){
        // Retorna na Tabela Ciclismo o resultado de apagar o treino com o ID enviado
        return bd.delete(TABELA_CICLISMO, ID_CICLISMO + " = ?", new String[] {"" + id}) == 1;
    }

    public void apagarCiclismoDBAll() {
        bd.delete(TABELA_CICLISMO, null, null);
    }
}
