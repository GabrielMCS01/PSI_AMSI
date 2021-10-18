package com.psi.ciclodias;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toolbar;

public class ProfileActivity extends AppCompatActivity {
    private EditText nomeUtilizador, email, dataNascimento;
    private Button btGuardaAlteracoes;
    private TextView nomeCompleto, distancia, velMedia, tempo, pordefenir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        nomeUtilizador = findViewById(R.id.etUserPerfil);
        email = findViewById(R.id.etEmailPerfil);
        dataNascimento = findViewById(R.id.etDataNascimentoPerfil);
        btGuardaAlteracoes = findViewById(R.id.btGuardarAlteracoes);
        nomeCompleto = findViewById(R.id.tvNomePerfil);
        distancia = findViewById(R.id.tvDistanciaPerfil);
        velMedia = findViewById(R.id.tvVelMedia);
        tempo = findViewById(R.id.tvTempoPerfil);
        pordefenir = findViewById(R.id.textView8);

        btGuardaAlteracoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    private void setSupportActionBar(Toolbar myToolbar) {
    }
}