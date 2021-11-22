package com.psi.ciclodias.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.psi.ciclodias.databinding.ActivityPausedTrainingBinding;

public class PausedTrainingActivity extends AppCompatActivity {
    private ActivityPausedTrainingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Recebe os IDs da Activity Results Training
        binding = ActivityPausedTrainingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mapFragment.getInstancia().pausedBinding = binding;

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

    private void dadosExemploPausedTraining() {
        binding.tvDistanciaPausa.setText("Distância: 6.8KM");
        binding.tvTempoPausa.setText("Tempo: 00:20:52");
        binding.tvVelMediaPausa.setText("Vel Média: 8.1 KM/H");
        binding.tvVelMaxPausa.setText("Vel Máxima: 20.2 KM/H");
    }

}