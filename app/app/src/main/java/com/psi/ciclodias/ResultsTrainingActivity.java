package com.psi.ciclodias;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ResultsTrainingActivity extends AppCompatActivity {
    private TextView tempo, distancia, velMedia, pordefenir;
    private Button btGuardar, btSair;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results_training);

        tempo = findViewById(R.id.tvTempoResumo);
        distancia = findViewById(R.id.tvDistanciaResumo);
        velMedia = findViewById(R.id.tvVelMediaResumo);
        pordefenir = findViewById(R.id.tvPorDefenirResumo);
        btGuardar = findViewById(R.id.btGuardarResumo);
        btSair = findViewById(R.id.btSairResumo);

        btGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Codigo para guardar
                Intent intent = new Intent(getApplicationContext(), MainPageActivity.class);
                startActivity(intent);
            }
        });

        btSair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Confirmar ao utilizador se não quer mesmo guardar os dados
                Intent intent = new Intent(getApplicationContext(), MainPageActivity.class);
                startActivity(intent);
            }
        });
    }
}