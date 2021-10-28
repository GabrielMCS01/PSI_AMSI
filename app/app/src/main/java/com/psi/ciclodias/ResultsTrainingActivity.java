package com.psi.ciclodias;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.psi.ciclodias.databinding.ActivityProfileBinding;
import com.psi.ciclodias.databinding.ActivityResultsTrainingBinding;

public class ResultsTrainingActivity extends AppCompatActivity {
    private ActivityResultsTrainingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Recebe os IDs da Activity Results Training
        binding = ActivityResultsTrainingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btGuardarResumo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Código para guardar na base de dados

                Intent intent = new Intent(getApplicationContext(), MainPageActivity.class);
                startActivity(intent);
                finish();
            }
        });

        binding.btSairResumo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean respostaUser = true; // Mudar para false e depois perguntar ao USER

                // Confirmar ao utilizador se não quer mesmo guardar os dados
                if (respostaUser){
                    Intent intent = new Intent(getApplicationContext(), MainPageActivity.class);
                    startActivity(intent);
                    finish();
                }
                else return;
            }
        });
    }
}