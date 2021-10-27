package com.psi.ciclodias;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;

import com.psi.ciclodias.databinding.ActivityRegistryBinding;

public class RegistryActivity extends AppCompatActivity {
    private ActivityRegistryBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_registry);

        binding = ActivityRegistryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btRegistarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Variáveis para enviar os dados inseridos para a API
                String primeiroNome = binding.etPrimeiroNome.getText().toString();
                String ultimoNome = binding.etUltimoNome.getText().toString();
                String email = binding.etRegistoEmail.getText().toString();
                String confirmarEmail = binding.etConfirmarEmail.getText().toString();
                String password = binding.etRegistoPassword.getText().toString();
                String confirmarPassword = binding.etConfirmarPassword.getText().toString();

                if (isMailValida(email, confirmarEmail) && isPasswordValida(password, confirmarPassword)) {
                    // Verificar se o email já existe na base de dados
                    // Verificar o resto dos dados
                    // Enviar dados para a base de dados
                    finish();
                }
                else{
                    // Outro erro
                    //binding.etConfirmarPassword.setError(getString(R.string.txtErrorConfirmarPassword));
                }
            }
        });
    }


    private boolean isMailValida(String email, String confirmarEmail){
        if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            if (email.equals(confirmarEmail))
                return true;
            else
                binding.etConfirmarEmail.setError(getString(R.string.txtErrorConfirmarEmail));
                return false;
        }
        else {
            binding.etRegistoEmail.setError(getString(R.string.txtErrorEmail));
            return false;
        }
    }

    private boolean isPasswordValida(String password, String confirmarPassword){
        if (password.length() > 6)
            if (password.equals(confirmarPassword))
                return true;
            else {
                binding.etConfirmarPassword.setError(getString(R.string.txtErrorConfirmarPassword));
                return false;
            }
        else {
            binding.etRegistoPassword.setError(getString(R.string.txtErrorPassword));
            return false;
        }
    }

}