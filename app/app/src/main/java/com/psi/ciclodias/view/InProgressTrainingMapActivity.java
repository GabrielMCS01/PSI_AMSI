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
import com.psi.ciclodias.dialogs.ConfirmarSaidaDialogFragment;
import com.psi.ciclodias.model.Chronometer;

public class InProgressTrainingMapActivity extends AppCompatActivity {

    private ActivityInProgressTrainingMapBinding binding;
    public static boolean RESPOSTA_USER = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityInProgressTrainingMapBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());


        mapFragment.getInstancia().mapBinding = binding;
        Chronometer.getInstancia(false).mapBinding = binding;

        mapFragment.getInstancia().setData();


        Fragment mapfragment = mapFragment.getInstancia();

        if(mapfragment != null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.mapViewTraining, mapfragment, "MAPTAG")
                    .commit();
        }

        binding.btPausaTreino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Mantenha o botão premido para pausar o treino", Toast.LENGTH_LONG).show();
            }
        });


        binding.btPausaTreino.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                getSupportFragmentManager().beginTransaction().remove(mapfragment).commit();
                // Pausa no treino
                Intent intent = new Intent(getApplicationContext(), PausedTrainingActivity.class);
                startActivity(intent);
                mapFragment.getInstancia().mapBinding = null;
                Chronometer.getInstancia(false).stopVariable = true;
                mapFragment.getInstancia().resumeTimer = true;
                finish();
                return false;
            }
        });

        binding.btTerminarTreino.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                getSupportFragmentManager().beginTransaction().remove(mapfragment).commit();

                // Confirmar ao utilizador se não quer mesmo guardar os dados
                DialogFragment newFragment = new ConfirmarSaidaDialogFragment();
                newFragment.show(getSupportFragmentManager(), getString(R.string.txtDialog));

                return false;
            }
        });

        binding.btTerminarTreino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {// Mudar para false e depois perguntar ao USER
                Toast.makeText(getApplicationContext(), "Mantenha o botão premido para terminar o treino", Toast.LENGTH_LONG).show();
            }
        });

        binding.btVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Abrir o fragmento do Mapbox
                Intent intent = new Intent(getApplicationContext(), InProgressTrainingActivity.class);
                startActivity(intent);
                mapFragment.getInstancia().mapBinding = null;
                Chronometer.getInstancia(false).mapBinding = null;
                finish();
            }
        });

        binding.fabTrainingMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapFragment.getInstancia().updateCamera(mapFragment.getInstancia().actualLocation);
            }
        });
    }

}