package com.psi.ciclodias;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.psi.ciclodias.databinding.ActivityPausedTrainingBinding;
import com.psi.ciclodias.databinding.ActivityResultsTrainingBinding;

public class PausedTrainingActivity extends AppCompatActivity {
    private ActivityPausedTrainingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Recebe os IDs da Activity Results Training
        binding = ActivityPausedTrainingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btRetomarTreinoPausa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Retoma atividade
                Intent intent = new Intent(getApplicationContext(), InProgressTrainingActivity.class);
                startActivity(intent);
                finish();
            }
        });

        binding.btTerminarTreinoPausa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Enviar os dados para a outra activity
                // Fazer cenas
                Intent intent = new Intent(getApplicationContext(), ResultsTrainingActivity.class);
                startActivity(intent);
                // Terminar a ativity da pausa e da sessão do treino
                //finish();
                finish();
            }
        });


    }
}