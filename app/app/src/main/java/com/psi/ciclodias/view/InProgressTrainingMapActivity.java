package com.psi.ciclodias.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.psi.ciclodias.R;
import com.psi.ciclodias.databinding.ActivityInProgressTrainingMapBinding;
import com.psi.ciclodias.dialogs.ConfirmarSairDialogFragment;
import com.psi.ciclodias.model.Chronometer;

public class InProgressTrainingMapActivity extends AppCompatActivity {
    private ActivityInProgressTrainingMapBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityInProgressTrainingMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Atribuição do binding para o MapFragment poder alterar os dados
        mapFragment.getInstancia().mapBinding = binding;

        // Falso devido a já existir um cronometro da activity anterior, para não criar um novo
        Chronometer.getInstancia(false).mapBinding = binding;

        // Atualiza os dados das TextBoxs
        mapFragment.getInstancia().setData();

        // Carrega o fragmento do mapa
        Fragment mapfragment = mapFragment.getInstancia();

        if(mapfragment != null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.mapViewTraining, mapfragment, "MAPTAG")
                    .commit();
        }

        // Premir para pausar o treino
        binding.btPausaTreino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Mantenha o botão premido para pausar o treino", Toast.LENGTH_LONG).show();
            }
        });

        // Redireciona o utilizador para a activity de Pausa do treino
        binding.btPausaTreino.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                getSupportFragmentManager().beginTransaction().remove(mapfragment).commit();
                Intent intent = new Intent(getApplicationContext(), PausedTrainingActivity.class);
                startActivity(intent);
                // Remove os bindings para não atualizar mais esta activity
                mapFragment.getInstancia().mapBinding = null;
                Chronometer.getInstancia(false).stopVariable = true;
                mapFragment.getInstancia().resumeTimer = true;
                finish();
                return false;
            }
        });

        // Dialog para terminar o treino
        binding.btTerminarTreino.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                // Remove o mapa da activity,
                getSupportFragmentManager().beginTransaction().remove(mapfragment).commit();

                DialogFragment newFragment = new ConfirmarSairDialogFragment();
                newFragment.show(getSupportFragmentManager(), getString(R.string.txtDialog));

                return false;
            }
        });

        // Premir para terminar o treino
        binding.btTerminarTreino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {// Mudar para false e depois perguntar ao USER
                Toast.makeText(getApplicationContext(), "Mantenha o botão premido para terminar o treino", Toast.LENGTH_LONG).show();
            }
        });

        // Botão para voltar á página inicial do treino em progresso
        binding.btVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), InProgressTrainingActivity.class);
                startActivity(intent);
                // Remove os bindings para não atualizar mais esta activity
                mapFragment.getInstancia().mapBinding = null;
                Chronometer.getInstancia(false).mapBinding = null;
                finish();
            }
        });

        // Botão para atualizar a câmera para a localização atual
        binding.fabTrainingMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapFragment.getInstancia().updateCamera(mapFragment.getInstancia().actualLocation);
            }
        });
    }

}