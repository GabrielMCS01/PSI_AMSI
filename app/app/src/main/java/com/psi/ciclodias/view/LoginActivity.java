package com.psi.ciclodias.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.psi.ciclodias.R;
import com.psi.ciclodias.databinding.ActivityLoginBinding;
import com.psi.ciclodias.listeners.LoginListener;
import com.psi.ciclodias.model.SingletonGestorCiclismo;

import java.util.Map;

public class LoginActivity extends AppCompatActivity implements LoginListener {
    public static final String ID = "id";
    public static final String PRIMEIRO_NOME = "primeiro_nome";
    public static final String ULTIMO_NOME = "ultimo_nome";
    public static final String USER_LOGIN = "user_login";
    public static final String TOKEN_LOGIN = "token_login";
    private ActivityLoginBinding binding;
    private boolean isEmpty = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);

        // Verifica se o User ja fez login na aplicação quando saiu
        if (!sharedPreferences.getString(ID, "").equals("null")){
            Intent intentMain = new Intent(this, MainPageActivity.class);
            startActivity(intentMain);
            finish();
        }

        // Recebe os IDs da Activity Results Training
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Para poder ser chamado quando quisermos
        SingletonGestorCiclismo.getInstancia(this).setLoginListener(this);



        binding.btEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                efetuarLogin(view);
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

    private void efetuarLogin(View v){
        isEmpty = false;

        // Recebe os dados das editViews
        String username = binding.etLoginUsername.getText().toString();
        String password = binding.etLoginPassword.getText().toString();

        // Verifica se os campos estão vazios
        isEmptyLogin(username, password);

        // Se os campos estiverem preenchidos verifica o Login na API
        if(!isEmpty) {
            SingletonGestorCiclismo.getInstancia(this).loginAPI(username, password, this);
        }
    }

    public void isEmptyLogin(String username, String password){
        if(username.isEmpty()) {
            binding.etLoginUsername.setError(getString(R.string.txtErrorCampoVazio));
            isEmpty = true;
        }
        if(password.isEmpty()) {
            binding.etLoginPassword.setError(getString(R.string.txtErrorCampoVazio));
            isEmpty = true;
        }
    }

    // Valida o login e inicia o Menu Principal
    @Override
    public void onValidateLogin(Map<String, String> dadosUser, String username) {
        if(dadosUser.get("token") != null){
            Intent intentMain = new Intent(this, MainPageActivity.class);

            // Enviar os dados do utilizador para a SHARED PREFERENCES
            intentMain.putExtra(ID, dadosUser.get("id"));
            intentMain.putExtra(PRIMEIRO_NOME, dadosUser.get("primeiro_nome"));
            intentMain.putExtra(ULTIMO_NOME, dadosUser.get("ultimo_nome"));
            intentMain.putExtra(USER_LOGIN, username);
            intentMain.putExtra(TOKEN_LOGIN, dadosUser.get("token"));
            startActivity(intentMain);
        }
        else{
            Toast.makeText(this, R.string.login_invalido, Toast.LENGTH_SHORT).show();
            binding.etLoginPassword.setText("");
        }
    }
}