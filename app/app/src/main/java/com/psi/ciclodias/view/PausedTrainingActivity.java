package com.psi.ciclodias.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.psi.ciclodias.R;
import com.psi.ciclodias.databinding.ActivityPausedTrainingBinding;
import com.psi.ciclodias.dialogs.ConfirmarSaidaDialogFragment;

public class PausedTrainingActivity extends AppCompatActivity {
    private ActivityPausedTrainingBinding binding;
    private Fragment mapfragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPausedTrainingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Atribuição do binding para o MapFragment poder alterar os dados
        mapFragment.getInstancia().pausedBinding = binding;
        mapFragment.getInstancia().setData();

        // Carrega o fragment do mapa
        mapfragment = mapFragment.getInstancia();

        if(mapfragment != null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.mapViewTraining, mapfragment, "MAPTAG")
                    .commit();
        }

        // Botão para retomar o treino
        binding.btRetomarTreinoPausa.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                // Retira o binding para não atualizar mais os dados desta activity
                mapFragment.getInstancia().pausedBinding = null;
                // Redireciona para a activity
                Intent intent = new Intent(getApplicationContext(), InProgressTrainingActivity.class);
                startActivity(intent);
                finish();
                return false;
            }
        });

        // Premir o botão de Pausa
        binding.btRetomarTreinoPausa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Mantenha o botão premido para retomar o treino", Toast.LENGTH_LONG).show();
            }
        });

        // Botão que carrega uma dialogBox para terminar o treino
        binding.btTerminarTreinoPausa.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                // Remove o mapa da activity
                getSupportFragmentManager().beginTransaction().remove(mapfragment).commit();

                // Terminar a ativity da pausa e da sessão do treino
                DialogFragment dialogFragment = new ConfirmarSaidaDialogFragment();
                dialogFragment.show(getSupportFragmentManager(), "dialog");

                return true;
            }
        });

        // Premir o botão de Terminar o treino
        binding.btTerminarTreinoPausa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Mantenha o botão premido para terminar o treino", Toast.LENGTH_LONG).show();
            }
        });

        // Botão para atualizar a câmera para a localização atual
        binding.fabTrainingPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapFragment.getInstancia().updateCamera(mapFragment.getInstancia().actualLocation);
            }
        });
    }

}