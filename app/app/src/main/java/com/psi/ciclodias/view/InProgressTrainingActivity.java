package com.psi.ciclodias.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.psi.ciclodias.databinding.ActivityInProgressTrainingBinding;

public class InProgressTrainingActivity extends AppCompatActivity {
    private ActivityInProgressTrainingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Recebe os IDs da Activity Results Training
        binding = ActivityInProgressTrainingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btPausaTreino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Pausa no treino
                Intent intent = new Intent(getApplicationContext(), PausedTrainingActivity.class);
                startActivity(intent);
                finish();
            }
        });

        binding.btTerminarTreino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean respostaUser = true; // Mudar para false e depois perguntar ao USER

                // Confirmar ao utilizador se não quer mesmo guardar os dados
                if (respostaUser) {
                    // Enviar os dados para a outra Activity
                    Intent intent = new Intent(getApplicationContext(), ResultsTrainingActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        binding.btMapaTreino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Abrir o fragmento do Mapbox
                Intent intent = new Intent(getApplicationContext(), mapFragment.class);
                startActivity(intent);
            }
        });

    }
}