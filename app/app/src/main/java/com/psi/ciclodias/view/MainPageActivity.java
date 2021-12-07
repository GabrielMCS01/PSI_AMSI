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
    private String user, token, primeiro_nome, ultimo_nome, id;
    private RecyclerCiclismoAdapter adaptador;
    private RecyclerView rvCiclismo;
    private RecyclerView.LayoutManager layoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        PreencherSharedPreferences();

        SingletonGestorCiclismo.getInstancia(this).setListaCiclismoListener(this);


        rvCiclismo = findViewById(R.id.rvCiclismo);
        layoutManager = new LinearLayoutManager(this);
        rvCiclismo.setLayoutManager(layoutManager);

        adaptador = new RecyclerCiclismoAdapter(this, SingletonGestorCiclismo.getInstancia(this).getArrCiclismo());

        rvCiclismo.setAdapter(adaptador);

        rvCiclismo.setItemAnimator(new DefaultItemAnimator());

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
        if (id == R.id.actionSettings) {
            Intent intent = new Intent(this, ConfigurationsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void PreencherSharedPreferences() {
        id = getIntent().getStringExtra(LoginActivity.ID);
        primeiro_nome = getIntent().getStringExtra(LoginActivity.PRIMEIRO_NOME);
        ultimo_nome = getIntent().getStringExtra(LoginActivity.ULTIMO_NOME);
        user = getIntent().getStringExtra(LoginActivity.USER_LOGIN);
        token = getIntent().getStringExtra(LoginActivity.TOKEN_LOGIN);

        // SHARED PREFERENCES
        SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TOKEN, token);
        editor.putString(USER, user);
        editor.putString(ID, id);
        editor.putString(PRIMEIRO_NOME, primeiro_nome);
        editor.putString(ULTIMO_NOME, ultimo_nome);
        editor.apply();

        System.out.println(primeiro_nome);
        System.out.println(token);
    }


    @Override
    public void onRefreshListaLivros(ArrayList<Ciclismo> lista) {
        adaptador = new RecyclerCiclismoAdapter(this, lista);

        rvCiclismo.setAdapter(adaptador);
        rvCiclismo.setItemAnimator(new DefaultItemAnimator());
    }
}