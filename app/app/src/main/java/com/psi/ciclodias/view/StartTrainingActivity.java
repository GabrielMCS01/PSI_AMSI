package com.psi.ciclodias.view;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import com.psi.ciclodias.R;
import com.psi.ciclodias.databinding.ActivityStartTrainingBinding;

public class StartTrainingActivity extends AppCompatActivity {
    private ActivityStartTrainingBinding binding;
    private boolean startTraining = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityStartTrainingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Verifica se a aplicação tem permissões de localização
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Carrega o fragment do mapa
            Fragment mapfragment = mapFragment.getInstancia();
            mapFragment.getInstancia().startBinding = binding;
            if (mapfragment != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mapViewStartTraining, mapfragment)
                        .commit();
            }
        } else {
            // Pede as permissões de localização
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        // ----------------------- Inicio da Bottom-navbar --------------------------------
        BottomNavBarFragment fragment = new BottomNavBarFragment();

        if (fragment != null) {
            fragment.lockTraining = true;
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.navBarMainPage, fragment)
                    .commit();
        }
        // ------------------------ Fim da Bottom-navbar -----------------------------------
        // Botão para iniciar o treino
        binding.btComecarTreino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Iniciar o treino
                startTraining = true;
                mapFragment.getInstancia().startBinding = null;
                Intent intent = new Intent(getApplicationContext(), InProgressTrainingActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Botão para atualizar a câmera para localização atual
        binding.floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapFragment.getInstancia().updateCamera(mapFragment.getInstancia().actualLocation);
            }
        });
    }

    // Pede as permissões de localização
    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                // Se for permitida
                if (isGranted) {
                    // Carrega o fragment do mapa
                    Fragment mapfragment = mapFragment.getInstancia();
                    mapFragment.getInstancia().startBinding = binding;
                    if (mapfragment != null) {
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.mapViewStartTraining, mapfragment)
                                .commit();
                    }
                }
                // Caso contrário volta para o menu principal
                else {
                    Intent intent = new Intent(getApplicationContext(), MainPageActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

    // Se clicar para voltar atrás
    @Override
    public void onBackPressed() {
        if(startTraining) {
            startTraining = false;
        }else {
            mapFragment.getInstancia().onMyDestroy();
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mapFragment.getInstancia().mapboxNavigation != null) {
            if(startTraining) {
                startTraining = false;
            }else{
                mapFragment.getInstancia().startBinding = null;
                mapFragment.getInstancia().onMyDestroy();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mapFragment.getInstancia().startNavigation();
        }
    }
}