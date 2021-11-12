package com.psi.ciclodias.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.psi.ciclodias.R;
import com.psi.ciclodias.databinding.ActivityStartTrainingBinding;

public class StartTrainingActivity extends AppCompatActivity {
    private ActivityStartTrainingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Recebe os IDs da Activity Start Training
        binding = ActivityStartTrainingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // ----------------------- Inicio da Bottom-navbar --------------------------------
        Fragment fragment = new BottomNavBarFragment();

        if(fragment != null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.navBarMainPage, fragment)
                    .commit();
        }
        // ------------------------ Fim da Bottom-navbar -----------------------------------

        binding.btComecarTreino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Iniciar o treino
                Intent intent = new Intent(getApplicationContext(), InProgressTrainingActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}