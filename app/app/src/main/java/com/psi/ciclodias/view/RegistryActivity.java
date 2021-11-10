package com.psi.ciclodias.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;

import com.psi.ciclodias.R;
import com.psi.ciclodias.databinding.ActivityRegistryBinding;

public class RegistryActivity extends AppCompatActivity {
    private ActivityRegistryBinding binding;
    private String primeiroNome, ultimoNome, email, username, password, confirmarPassword;
    private boolean isEmpty = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRegistryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btRegistarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Variáveis para enviar os dados inseridos para a API
                primeiroNome = binding.etPrimeiroNome.getText().toString();
                ultimoNome = binding.etUltimoNome.getText().toString();
                email = binding.etRegistoEmail.getText().toString();
                username = binding.etUsername.getText().toString();
                password = binding.etRegistoPassword.getText().toString();
                confirmarPassword = binding.etConfirmarPassword.getText().toString();

                // Verifica se os campos estão vazios
                isEmpty = false;
                isEmptyRegisto();

                // Se não existir campos vazios faz
                if (!isEmpty) {
                    // Se o email e a password estiverem válidas faz
                    if (isMailValida(email) && isPasswordValida(password, confirmarPassword)) {
                        // Verificar se o email e o Username já existe na base de dados
                       // if (email.equals() && username.equals())

                        // Enviar dados para a base de dados
                        finish();
                    }
                }
            }
        });
    }

    private boolean isMailValida(String email){
        if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return true;
        }
        else {
            binding.etRegistoEmail.setError(getString(R.string.txtErrorEmail));
            return false;
        }
    }

    private boolean isPasswordValida(String password, String confirmarPassword){
        if (password.length() >= 8)
            if (password.equals(confirmarPassword))
                return true;
            else {
                binding.etConfirmarPassword.setError(getString(R.string.txtErrorConfirmarPassword));
                return false;
            }
        else {
            binding.etRegistoPassword.setError(getString(R.string.txtErrorPasswordTamanho));
            return false;
        }
    }

    public void isEmptyRegisto(){
        if(primeiroNome.isEmpty()){
            binding.etPrimeiroNome.setError(getString(R.string.txtErrorCampoVazio));
            isEmpty = true;
        }
        if(ultimoNome.isEmpty()) {
            binding.etUltimoNome.setError(getString(R.string.txtErrorCampoVazio));
            isEmpty = true;
        }
        if(email.isEmpty()) {
            binding.etRegistoEmail.setError(getString(R.string.txtErrorCampoVazio));
            isEmpty = true;
        }
        if(username.isEmpty()) {
            binding.etUsername.setError(getString(R.string.txtErrorCampoVazio));
            isEmpty = true;
        }
        if(password.isEmpty()) {
            binding.etRegistoPassword.setError(getString(R.string.txtErrorCampoVazio));
            isEmpty = true;
        }
        if(confirmarPassword.isEmpty()) {
            binding.etConfirmarPassword.setError(getString(R.string.txtErrorCampoVazio));
            isEmpty = true;
        }
    }

}