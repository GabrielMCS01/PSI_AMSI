package com.psi.ciclodias.view;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import android.app.DialogFragment;
import android.widget.Chronometer;

import com.psi.ciclodias.R;
import com.psi.ciclodias.databinding.ActivityInProgressTrainingBinding;


public class InProgressTrainingActivity extends AppCompatActivity {
    private ActivityInProgressTrainingBinding binding;
    private Chronometer chronometer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Recebe os IDs da Activity Results Training
        binding = ActivityInProgressTrainingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mapFragment.getInstancia().binding = binding;

        startTimer();

        binding.btPausaTreino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Pausa no treino
                Intent intent = new Intent(getApplicationContext(), PausedTrainingActivity.class);
                startActivity(intent);
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

                if (respostaUser) {
                    // Enviar os dados para a outra Activity
                    Intent intent = new Intent(getApplicationContext(), ResultsTrainingActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        binding.btMapaTreino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Abrir o fragmento do Mapbox
                Intent intent = new Intent(getApplicationContext(), mapFragment.class);
                startActivity(intent);
            }
        });

    }

    private void startTimer() {

        chronometer = binding.tvDuracaoTreino;
        chronometer.start();
    }
}