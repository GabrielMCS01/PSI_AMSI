package com.psi.ciclodias;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class PausedTrainingActivity extends AppCompatActivity {
    private Button btRetomarTreino, btTerminarTreino;
    private TextView tempo, distancia, velMedia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paused_training);

        btRetomarTreino = findViewById(R.id.btRetomarTreinoPausa);
        btTerminarTreino = findViewById(R.id.btTerminarTreinoPausa);
        tempo = findViewById(R.id.tvTempoPausa);
        distancia = findViewById(R.id.tvDistanciaPausa);
        velMedia = findViewById(R.id.tvVelMediaPausa);

        btRetomarTreino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                onResume();
            }
        });

        btTerminarTreino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Enviar os dados para a outra activity
                // Fazer cenas
                Intent intent = new Intent(getApplicationContext(), ResultsTrainingActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }
}