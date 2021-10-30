package com.psi.ciclodias.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.psi.ciclodias.R;

public class MainPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        // ----------------------- Inicio da Bottom-navbar --------------------------------
        Fragment fragment = new BottomNavBarFragment();

        if(fragment != null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.navBarMainPage, fragment)
                    .commit();
        }
        // ------------------------ Fim da Bottom-navbar -----------------------------------
    }


}