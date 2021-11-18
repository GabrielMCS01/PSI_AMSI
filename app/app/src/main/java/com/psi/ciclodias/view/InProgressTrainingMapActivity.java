package com.psi.ciclodias.view;

import android.app.Application;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.psi.ciclodias.R;
import com.psi.ciclodias.databinding.ActivityInProgressTrainingMapBinding;

public class InProgressTrainingMapActivity extends AppCompatActivity {

    private ActivityInProgressTrainingMapBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityInProgressTrainingMapBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());


        mapFragment.getInstancia().mapBinding = binding;

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

        binding.btVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Abrir o fragmento do Mapbox
                Intent intent = new Intent(getApplicationContext(), InProgressTrainingActivity.class);
                startActivity(intent);
                mapFragment.getInstancia().mapBinding = null;
                finish();
            }
        });


    }

}