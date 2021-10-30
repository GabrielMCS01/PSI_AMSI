package com.psi.ciclodias.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.psi.ciclodias.databinding.ActivityStartTrainingBinding;

public class StartTrainingActivity extends AppCompatActivity {
    private ActivityStartTrainingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Recebe os IDs da Activity Start Training
        binding = ActivityStartTrainingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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