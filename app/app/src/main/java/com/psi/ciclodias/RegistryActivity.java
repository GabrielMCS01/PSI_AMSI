package com.psi.ciclodias;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RegistryActivity extends AppCompatActivity {
    private EditText etPrimeiroNome, etUltimoNome, etEmail, etConfirmarEmail, etPassword, etConfirmarPassword;
    private Button btRegistarse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registry);

        etPrimeiroNome = findViewById(R.id.etPrimeiroNome);
        etUltimoNome = findViewById(R.id.etUltimoNome);
        etEmail = findViewById(R.id.etRegistoEmail);
        etConfirmarEmail = findViewById(R.id.etConfirmarEmail);
        etPassword = findViewById(R.id.etRegistoPassword);
        etConfirmarPassword = findViewById(R.id.etConfirmarPassword);
        btRegistarse = findViewById(R.id.btRegistarse);

        btRegistarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);

                String primeiroNome = etPrimeiroNome.getText().toString();
                String ultimoNome = etUltimoNome.getText().toString();
                String email = etEmail.getText().toString();
                String confirmarEmail = etConfirmarEmail.getText().toString();
                String password = etPassword.getText().toString();
                String confirmarPassword = etConfirmarPassword.getText().toString();

                if (email.equals(confirmarEmail)) {
                    if (password.equals(confirmarPassword)) {
                        // Verificar o resto dos dados
                        // Enviar dados para a base de dados

                        startActivity(intent);
                    }
                    else {
                        etConfirmarPassword.setError(getString(R.string.txtErrorConfirmarPassword));
                    }
                }
                else {
                    etConfirmarEmail.setError(getString(R.string.txtErrorConfirmarEmail));
                }
            }
        });

    }


}