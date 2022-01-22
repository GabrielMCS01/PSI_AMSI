package com.psi.ciclodias.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.psi.ciclodias.R;
import com.psi.ciclodias.databinding.ActivityResultsTrainingBinding;
import com.psi.ciclodias.dialogs.ConfirmarGuardarDialogFragment;
import com.psi.ciclodias.dialogs.ConfirmarNaoGuardarDialogFragment;
import com.psi.ciclodias.listeners.CreateCiclismoListener;
import com.psi.ciclodias.model.Ciclismo;
import com.psi.ciclodias.model.SingletonGestorCiclismo;
import com.psi.ciclodias.utils.CiclismoJsonParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ResultsTrainingActivity extends AppCompatActivity implements CreateCiclismoListener {
    private ActivityResultsTrainingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityResultsTrainingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Envia o binding da activity de resultados para os dados do treino serem mostrados nesta activity
        mapFragment.getInstancia().getResults(binding);

        // Permite ao método para criar ciclismo listener da singleton aceder a esta activity
        SingletonGestorCiclismo.getInstancia(this).setCreateCiclismoListener(this);

        // Carrega o fragment do mapa
        Fragment mapfragment = mapFragment.getInstancia();

        if (mapfragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.mapViewResult, mapfragment)
                    .commit();
        }

        // Botão para Criar o treino na DB
        binding.btGuardarResumo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Confirmar ao utilizador se quer guardar os dados
                DialogFragment guardar = new ConfirmarGuardarDialogFragment(binding);
                guardar.show(getSupportFragmentManager(), "dialog");
            }
        });

        // Botão para sair sem guardar o treino
        binding.btSairResumo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Confirmar ao utilizador se não quer guardar os dados
                DialogFragment naoGuardar = new ConfirmarNaoGuardarDialogFragment();
                naoGuardar.show(getSupportFragmentManager(), "dialog");
            }
        });
    }

    // Resposta ao criar o treino
    @Override
    public void createCiclismo(Ciclismo ciclismo) {
        // Se o treino tiver um ID diferente -1, o treino foi guardado com sucesso
        if (ciclismo.getId() != -1) {
            // Guarda o treino na DB local
            SingletonGestorCiclismo.getInstancia(this).adicionarCiclismoBD(ciclismo);

            Toast.makeText(getApplicationContext(), "Treino Guardado com sucesso", Toast.LENGTH_SHORT).show();

            mapFragment.getInstancia().onMyDestroy();

            // Redireciona o Utilizador para a página principal
            Intent intent = new Intent(getApplicationContext(), MainPageActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(getApplicationContext(), "Treino não foi guardado", Toast.LENGTH_SHORT).show();
        }
    }
}