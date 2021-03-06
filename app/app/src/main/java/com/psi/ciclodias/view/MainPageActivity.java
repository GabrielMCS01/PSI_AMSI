package com.psi.ciclodias.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.psi.ciclodias.R;
import com.psi.ciclodias.adapters.RecyclerCiclismoAdapter;
import com.psi.ciclodias.model.SingletonGestorCiclismo;

public class MainPageActivity extends AppCompatActivity {
    private RecyclerCiclismoAdapter adaptador;
    private RecyclerView rvCiclismo;
    private RecyclerView.LayoutManager layoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        rvCiclismo = findViewById(R.id.rvCiclismo);
        layoutManager = new LinearLayoutManager(this);
        rvCiclismo.setLayoutManager(layoutManager);

        adaptador = new RecyclerCiclismoAdapter(this, SingletonGestorCiclismo.getInstancia(this).getArrCiclismo());

        rvCiclismo.setAdapter(adaptador);

        rvCiclismo.setItemAnimator(new DefaultItemAnimator());

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


}