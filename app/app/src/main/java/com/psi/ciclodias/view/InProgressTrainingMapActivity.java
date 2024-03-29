package com.psi.ciclodias.view;

import android.app.Application;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.psi.ciclodias.R;
import com.psi.ciclodias.databinding.ActivityInProgressTrainingMapBinding;
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
        Chronometer.getInstancia().mapBinding = binding;

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
                // Pausa no treino
                Intent intent = new Intent(getApplicationContext(), PausedTrainingActivity.class);
                startActivity(intent);
                mapFragment.getInstancia().mapBinding = null;
                Chronometer.getInstancia().stopVariable = true;
                mapFragment.getInstancia().resumeTimer = true;
                finish();
            }
        });

        binding.btTerminarTreino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {// Mudar para false e depois perguntar ao USER

                getSupportFragmentManager().beginTransaction().remove(mapfragment).commit();
                FragmentManager fm = getFragmentManager();

                // Confirmar ao utilizador se não quer mesmo guardar os dados
                DialogFragment newFragment = ConfirmarSaidaDialogFragment.newInstance();
                newFragment.show(fm, getString(R.string.txtDialog));
                }

        });

        binding.btVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Abrir o fragmento do Mapbox
                Intent intent = new Intent(getApplicationContext(), InProgressTrainingActivity.class);
                startActivity(intent);
                mapFragment.getInstancia().mapBinding = null;
                Chronometer.getInstancia().mapBinding = null;
                finish();
            }
        });
    }

}