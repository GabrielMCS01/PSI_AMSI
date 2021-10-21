package com.psi.ciclodias;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class InProgressTrainingActivity extends AppCompatActivity {
    private TextView duracao, velInstantanea, velMedia, distancia;
    private Button btPausa, btTerminar, btMapa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_progress_training);

        duracao = findViewById(R.id.tvDuracaoTreino);
        velInstantanea = findViewById(R.id.tvVelInstantaneaTreino);
        velMedia = findViewById(R.id.tvVelMediaTreino);
        distancia = findViewById(R.id.tvDistanciaTreino);
        btPausa = findViewById(R.id.btPausaTreino);
        btTerminar = findViewById(R.id.btTerminarTreino);
        btMapa = findViewById(R.id.btMapaTreino);

        btPausa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Pausa no treino
                Intent intent = new Intent(getApplicationContext(), PausedTrainingActivity.class);
                startActivity(intent);
            }
        });

        btTerminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Confirmar ao utilizador se pretende mesmo terminar o treino
                Intent intent = new Intent(getApplicationContext(), ResultsTrainingActivity.class);
                startActivity(intent);
            }
        });

        btMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Abrir o fragmento do Mapbox
                Intent intent = new Intent(getApplicationContext(), mapFragment.class);
                startActivity(intent);
            }
        });

    }
}