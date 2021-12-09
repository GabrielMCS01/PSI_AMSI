package com.psi.ciclodias.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.psi.ciclodias.R;
import com.psi.ciclodias.adapters.RecyclerCiclismoAdapter;
import com.psi.ciclodias.listeners.ListaCiclismoListener;
import com.psi.ciclodias.model.Ciclismo;
import com.psi.ciclodias.model.SingletonGestorCiclismo;

import java.util.ArrayList;

public class MainPageActivity extends AppCompatActivity implements ListaCiclismoListener {
    public static final String TOKEN = "token";
    public static final String USER = "user";
    private static final String ID = "id";
    private static final String PRIMEIRO_NOME = "primeiro_nome";
    private static final String ULTIMO_NOME = "ultimo_nome";
    private static final String DATA_NASCIMENTO = "data_nascimento";
    private String user, token, primeiro_nome, ultimo_nome, id;
    private RecyclerCiclismoAdapter adaptador;
    private RecyclerView rvCiclismo;
    private RecyclerView.LayoutManager layoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        // Preenche as shared Preferences
        PreencherSharedPreferences();

        // Instancia da Singleton para poder ser utilizada em qualquer parte do código
        SingletonGestorCiclismo.getInstancia(this).setListaCiclismoListener(this);

        // RecyclerView
        rvCiclismo = findViewById(R.id.rvCiclismo);
        layoutManager = new LinearLayoutManager(this);
        rvCiclismo.setLayoutManager(layoutManager);

        // Recebe os ciclismos da BD local
        adaptador = new RecyclerCiclismoAdapter(this, SingletonGestorCiclismo.getInstancia(this).getArrCiclismo());
        rvCiclismo.setAdapter(adaptador);
        rvCiclismo.setItemAnimator(new DefaultItemAnimator());

        // Recebe os ciclismos da API
        SingletonGestorCiclismo.getInstancia(this).getListaCiclismoAPI(this);

        // ----------------------- Inicio da Bottom-navbar --------------------------------
        BottomNavBarFragment fragment = new BottomNavBarFragment();

        if(fragment != null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.navBarMainPage, fragment)
                    .commit();
            fragment.lockHome = true;
        }
        // ------------------------ Fim da Bottom-navbar -----------------------------------
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.actionLogout) {
            SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putString(TOKEN, "null");
            editor.putString(USER, "null");
            editor.putString(ID, "null");
            editor.putString(PRIMEIRO_NOME, "null");
            editor.putString(ULTIMO_NOME, "null");
            editor.putString(DATA_NASCIMENTO, "null");
            editor.apply();

            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Preencher a Shared Preferences "user"
    private void PreencherSharedPreferences() {
        // Verifica se a activity anterior é a de login, para receber os dados e preencher na SHARED
        // PREFERENCES, principalmente o TOKEN que é utilizado para fazer os REQUESTS
        if (getIntent().getStringExtra(LoginActivity.ID) != null) {
            // Recebe os valores das variáveis globais enviadas pelo loginActivity
            id = getIntent().getStringExtra(LoginActivity.ID);
            primeiro_nome = getIntent().getStringExtra(LoginActivity.PRIMEIRO_NOME);
            ultimo_nome = getIntent().getStringExtra(LoginActivity.ULTIMO_NOME);
            user = getIntent().getStringExtra(LoginActivity.USER_LOGIN);
            token = getIntent().getStringExtra(LoginActivity.TOKEN_LOGIN);

            // Cria uma instância das SHARED PREFERENCES
            SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            // Preenche os dados na SHARED PREFERENCES
            editor.putString(TOKEN, token);
            editor.putString(USER, user);
            editor.putString(ID, id);
            editor.putString(PRIMEIRO_NOME, primeiro_nome);
            editor.putString(ULTIMO_NOME, ultimo_nome);
            editor.apply();

            System.out.println(primeiro_nome);
            System.out.println(token);
        }
    }


    // Função que recebe os treinos da API e coloca na RecylerViewE
    @Override
    public void onRefreshListaLivros(ArrayList<Ciclismo> lista) {
        adaptador = new RecyclerCiclismoAdapter(this, lista);

        rvCiclismo.setAdapter(adaptador);
        rvCiclismo.setItemAnimator(new DefaultItemAnimator());
    }
}