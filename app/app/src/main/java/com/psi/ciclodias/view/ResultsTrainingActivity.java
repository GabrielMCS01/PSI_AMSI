package com.psi.ciclodias.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.psi.ciclodias.R;
import com.psi.ciclodias.databinding.ActivityResultsTrainingBinding;
import com.psi.ciclodias.listeners.CreateCiclismoListener;
import com.psi.ciclodias.model.Ciclismo;
import com.psi.ciclodias.model.SingletonGestorCiclismo;

import java.util.HashMap;
import java.util.Map;

public class ResultsTrainingActivity extends AppCompatActivity implements CreateCiclismoListener {
    private ActivityResultsTrainingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Recebe os IDs da Activity Results Training
        binding = ActivityResultsTrainingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mapFragment.getInstancia().getResults(binding);

        SingletonGestorCiclismo.getInstancia(this).setCreateCiclismoListener(this);

        Fragment mapfragment = mapFragment.getInstancia();

        if(mapfragment != null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.mapViewResult, mapfragment)
                    .commit();
        }

        binding.btGuardarResumo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, String> dadosCiclismo = new HashMap<String, String>();

                int distance = (int) mapFragment.getInstancia().distance;
                // Recebe os dados do treino
                dadosCiclismo.put("nome_percurso", binding.etNomeTreino.getText().toString());
                dadosCiclismo.put("duracao", String.valueOf(mapFragment.getInstancia().time));
                dadosCiclismo.put("distancia", String.valueOf(distance));
                dadosCiclismo.put("velocidade_media", String.valueOf(mapFragment.getInstancia().velocityMean));
                dadosCiclismo.put("velocidade_maxima", String.valueOf(mapFragment.getInstancia().velocityMax));
                //dadosCiclismo.put("velocidade_grafico", null);
                dadosCiclismo.put("rota", mapFragment.getInstancia().routeString);

                SingletonGestorCiclismo.getInstancia(getApplicationContext()).AddCiclismo(dadosCiclismo, getApplicationContext());
            }
        });

        // PRECISA DE MAIS COISAS!
        binding.btSairResumo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapFragment.getInstancia().onMyDestroy();
                boolean respostaUser = true; // Mudar para false e depois perguntar ao USER

                // Confirmar ao utilizador se não quer mesmo guardar os dados
                if (respostaUser){
                    mapFragment.getInstancia().onMyDestroy();
                    Intent intent = new Intent(getApplicationContext(), MainPageActivity.class);
                    startActivity(intent);
                    finish();
                }
                else return;
            }
        });
    }

    @Override
    public void createCiclismo(Ciclismo ciclismo) {
        if (ciclismo.getId() != -1) {

            SingletonGestorCiclismo.getInstancia(this).adicionarCiclismoBD(ciclismo);

            Toast.makeText(getApplicationContext(), "Treino Guardado com sucesso", Toast.LENGTH_SHORT).show();

            mapFragment.getInstancia().onMyDestroy();
            Intent intent = new Intent(getApplicationContext(), MainPageActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            Toast.makeText(getApplicationContext(), "Treino não foi guardado", Toast.LENGTH_SHORT).show();
        }
    }
}