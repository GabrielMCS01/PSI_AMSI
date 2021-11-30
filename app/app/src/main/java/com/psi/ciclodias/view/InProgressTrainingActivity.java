package com.psi.ciclodias.view;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import android.app.DialogFragment;

import com.psi.ciclodias.R;
import com.psi.ciclodias.databinding.ActivityInProgressTrainingBinding;
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
                Intent intent = new Intent(getApplicationContext(), PausedTrainingActivity.class);
                startActivity(intent);
                mapFragment.getInstancia().trainingBinding = null;
                Chronometer.getInstancia().stopVariable = true;
                mapFragment.getInstancia().resumeTimer = true;
                finish();
            }
        });

        binding.btTerminarTreino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean respostaUser = false; // Mudar para false e depois perguntar ao USER

                FragmentManager fm = getFragmentManager();
                // Confirmar ao utilizador se não quer mesmo guardar os dados
                DialogFragment newFragment = ConfirmarSaidaDialogFragment.newInstance();
                newFragment.show(fm, getString(R.string.txtDialog));

                /*if (respostaUser) {
                    // Enviar os dados para a outra Activity
                    Intent intent = new Intent(getApplicationContext(), ResultsTrainingActivity.class);
                    startActivity(intent);
                    finish();
                }*/
            }
        });

        binding.btMapaTreino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Abrir o fragmento do Mapbox
                Intent intent = new Intent(getApplicationContext(), InProgressTrainingMapActivity.class);
                startActivity(intent);
                mapFragment.getInstancia().trainingBinding = null;
                Chronometer.getInstancia().trainingBinding = null;
                finish();
            }
        });

    }
}