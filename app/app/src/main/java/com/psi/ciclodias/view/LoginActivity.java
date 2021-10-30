package com.psi.ciclodias.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.psi.ciclodias.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Recebe os IDs da Activity Results Training
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainPageActivity.class);

                // Recebe os itens das editViews
                String email = binding.etLoginEmail.getText().toString();
                String password = binding.etLoginPassword.getText().toString();

                // Fazer a verificação se o login está correto
                //if (email.matches() && password.matches())

                startActivity(intent);
                finish();
            }
        });

        binding.btRegistar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegistryActivity.class);
                startActivity(intent);
            }
        });

    }
}