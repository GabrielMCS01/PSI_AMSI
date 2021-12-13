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
import com.psi.ciclodias.model.Chronometer;

public class PausedTrainingActivity extends AppCompatActivity {
    private ActivityPausedTrainingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Recebe os IDs da Activity Results Training
        binding = ActivityPausedTrainingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mapFragment.getInstancia().pausedBinding = binding;
        mapFragment.getInstancia().setData();

        Fragment mapfragment = mapFragment.getInstancia();

        if(mapfragment != null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.mapViewTraining, mapfragment, "MAPTAG")
                    .commit();
        }

        binding.btRetomarTreinoPausa.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                // Retoma atividade
                Intent intent = new Intent(getApplicationContext(), InProgressTrainingActivity.class);
                startActivity(intent);
                finish();
                return false;
            }
        });
        binding.btRetomarTreinoPausa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Mantenha o botão premido para retomar o treino", Toast.LENGTH_LONG).show();
            }
        });

        binding.btTerminarTreinoPausa.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                getSupportFragmentManager().beginTransaction().remove(mapfragment).commit();

                // Terminar a ativity da pausa e da sessão do treino
                DialogFragment dialogFragment = new ConfirmarSaidaDialogFragment();
                dialogFragment.show(getSupportFragmentManager(), "dialog");
                return false;
            }
        });

        binding.btTerminarTreinoPausa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Mantenha o botão premido para terminar o treino", Toast.LENGTH_LONG).show();
            }
        });


    }

}