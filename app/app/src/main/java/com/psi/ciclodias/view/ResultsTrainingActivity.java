package com.psi.ciclodias.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.psi.ciclodias.databinding.ActivityResultsTrainingBinding;

public class ResultsTrainingActivity extends AppCompatActivity {
    private ActivityResultsTrainingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Recebe os IDs da Activity Results Training
        binding = ActivityResultsTrainingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mapFragment.getInstancia().getResults(binding);

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

    private void dadosExemploResultsTraining() {
        binding.tvDistanciaResumo.setText("Distância: 72.5KM");
        binding.tvTempoResumo.setText("Tempo: 01:21:32");
        binding.tvVelMediaResumo.setText("Vel Média: 12.8 KM/H");
        binding.tvVelMaxResumo.setText("Vel Máxima: 30.1 KM/H");
    }

}