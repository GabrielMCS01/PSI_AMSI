package com.psi.ciclodias.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.psi.ciclodias.R;
import com.psi.ciclodias.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private String username, password;
    private boolean isEmpty = false;

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
                username = binding.etLoginUsername.getText().toString();
                password = binding.etLoginPassword.getText().toString();

                // Verifica se campos estão vazios
                isEmptyLogin();

                // Fazer a verificação se o login está correto
                //if (username.matches() && password.matches())

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

    public void isEmptyLogin(){
        if(username.isEmpty()) {
            binding.etLoginUsername.setError(getString(R.string.txtErrorCampoVazio));
            isEmpty = true;
        }
        if(password.isEmpty()) {
            binding.etLoginPassword.setError(getString(R.string.txtErrorCampoVazio));
            isEmpty = true;
        }
    }

}