package com.psi.ciclodias;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartTrainingActivity extends AppCompatActivity {
    private Button btComecarTreino;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_training);

        btComecarTreino = findViewById(R.id.btComecarTreino);

        btComecarTreino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Iniciar a outra atividade, iniciar dados?

                Intent intent = new Intent(getApplicationContext(), InProgressTrainingActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}