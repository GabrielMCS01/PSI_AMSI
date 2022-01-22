package com.psi.ciclodias.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.psi.ciclodias.databinding.ActivityInProgressTrainingBinding;
import com.psi.ciclodias.dialogs.ConfirmarSairDialogFragment;
import com.psi.ciclodias.model.Chronometer;


public class InProgressTrainingActivity extends AppCompatActivity {
    private ActivityInProgressTrainingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityInProgressTrainingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Atribuição do binding para o MapFragment poder alterar os dados
        mapFragment.getInstancia().trainingBinding = binding;
        mapFragment.getInstancia().setData();

        // Premir para pausar o treino
        binding.btPausaTreino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Mantenha o botão premido para pausar o treino", Toast.LENGTH_LONG).show();
            }
        });

        // Dialog para terminar o treino
        binding.btTerminarTreino.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                DialogFragment newFragment = new ConfirmarSairDialogFragment();
                newFragment.show(getSupportFragmentManager(), "dialog");
                return false;
            }
        });

        // Premir para terminar o treino
        binding.btTerminarTreino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Mantenha o botão premido para terminar o treino", Toast.LENGTH_LONG).show();
            }
        });

        // Redireciona para a activity de pausa do treino
        binding.btPausaTreino.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PausedTrainingActivity.class);
                startActivity(intent);
                // Remove os bindings para não atualizar mais esta activity
                mapFragment.getInstancia().trainingBinding = null;
                Chronometer.getInstancia(false).stopVariable = true;
                // Informa que já existe um cronometro em progresso
                mapFragment.getInstancia().resumeTimer = true;
                finish();
                return false;
            }
        });

        // Redireciona o utilizador para a activity de treino em progresso com mapa
        binding.btMapaTreino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), InProgressTrainingMapActivity.class);
                startActivity(intent);
                // Remove os bindings para não atualizar mais esta activity
                mapFragment.getInstancia().trainingBinding = null;
                Chronometer.getInstancia(false).trainingBinding = null;
                finish();
            }
        });

    }
}