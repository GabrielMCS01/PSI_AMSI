package com.psi.ciclodias.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.psi.ciclodias.databinding.ActivityInProgressTrainingBinding;
import com.psi.ciclodias.dialogs.ConfirmarSaidaDialogFragment;
import com.psi.ciclodias.model.Chronometer;


public class InProgressTrainingActivity extends AppCompatActivity {
    private ActivityInProgressTrainingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Recebe os IDs da Activity Results Training
        binding = ActivityInProgressTrainingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mapFragment.getInstancia().trainingBinding = binding;
        mapFragment.getInstancia().setData();

        binding.btPausaTreino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Pausa no treino
                Toast.makeText(getApplicationContext(), "Mantenha o botão premido para pausar o treino", Toast.LENGTH_LONG).show();
            }
        });

        binding.btTerminarTreino.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                DialogFragment newFragment = new ConfirmarSaidaDialogFragment();
                newFragment.show(getSupportFragmentManager(), "dialog");
                return false;
            }
        });
        binding.btTerminarTreino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Mantenha o botão premido para terminar o treino", Toast.LENGTH_LONG).show();
            }
        });

        binding.btPausaTreino.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PausedTrainingActivity.class);
                startActivity(intent);
                mapFragment.getInstancia().trainingBinding = null;
                Chronometer.getInstancia(false).stopVariable = true;
                mapFragment.getInstancia().resumeTimer = true;
                finish();
                return false;
            }
        });
        binding.btMapaTreino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Abrir o fragmento do Mapbox
                Intent intent = new Intent(getApplicationContext(), InProgressTrainingMapActivity.class);
                startActivity(intent);
                mapFragment.getInstancia().trainingBinding = null;
                Chronometer.getInstancia(false).trainingBinding = null;
                finish();
            }
        });

    }
}