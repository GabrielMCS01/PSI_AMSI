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
    public static final String USER = "user_login";
    public static final String TOKEN = "token_login";
    private ActivityLoginBinding binding;
    private boolean isEmpty = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Recebe as SharedPreferences do utilizador
        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);

        // Verifica se o User ja fez login na aplicação quando saiu
        if (!sharedPreferences.getString(ID, "null").equals("null")){
            // Redireciona o utilizador para a página principal
            Intent intentMain = new Intent(this, MainPageActivity.class);
            startActivity(intentMain);
            finish();
        }

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Para poder ser chamado quando quisermos
        SingletonGestorCiclismo.getInstancia(this).setLoginListener(this);

        // Efetua o login do utilizador
        binding.btEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                efetuarLogin(view);
            }
        });

        // Chama a activity para fazer o registo de um utilizador
        binding.btRegistar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegistryActivity.class);
                startActivity(intent);
            }
        });

    }

    // Função para fazer o login do utilizador
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

    // Verifica se os campos estão vazios
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
        // Se receber o token do login
        if(dadosUser.get("token") != null){
            // Redireciona o utilizador para a página principal
            Intent intentMain = new Intent(this, MainPageActivity.class);

            // Enviar os dados do utilizador para a SHARED PREFERENCES
            intentMain.putExtra(ID, dadosUser.get("id"));
            intentMain.putExtra(PRIMEIRO_NOME, dadosUser.get("primeiro_nome"));
            intentMain.putExtra(ULTIMO_NOME, dadosUser.get("ultimo_nome"));
            intentMain.putExtra(USER, username);
            intentMain.putExtra(TOKEN, dadosUser.get("token"));
            startActivity(intentMain);
            finish();
        }
        // Dados inválidos
        else{
            Toast.makeText(this, R.string.login_invalido, Toast.LENGTH_SHORT).show();
            binding.etLoginPassword.setText("");
        }
    }
}