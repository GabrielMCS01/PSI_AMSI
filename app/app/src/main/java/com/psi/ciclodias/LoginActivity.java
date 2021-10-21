package com.psi.ciclodias;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {
    private Button btEntrar, btRegisto;
    private EditText tvEmail, tvPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btEntrar = findViewById(R.id.btEntrar);
        btRegisto = findViewById(R.id.btRegistar);
        tvEmail = findViewById(R.id.etLoginEmail);
        tvPassword = findViewById(R.id.etLoginPassword);

        btEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainPageActivity.class);

                // Recebe os itens das editViews
                String email = tvEmail.getText().toString();
                String password = tvPassword.getText().toString();

                // fazer a verificação se o login está correto
                //if (email.matches())

                startActivity(intent);
            }
        });

        btRegisto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegistryActivity.class);
                startActivity(intent);
            }
        });

    }
}