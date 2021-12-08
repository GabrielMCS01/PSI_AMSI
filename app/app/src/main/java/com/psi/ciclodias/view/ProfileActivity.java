package com.psi.ciclodias.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.psi.ciclodias.R;
import com.psi.ciclodias.databinding.ActivityProfileBinding;
import com.psi.ciclodias.listeners.PerfilListener;
import com.psi.ciclodias.model.SingletonGestorCiclismo;

import java.util.Map;

public class ProfileActivity extends AppCompatActivity implements PerfilListener {
    private ActivityProfileBinding binding;

    public static final String USER = "user";
    private static final String ID = "id";
    private static final String PRIMEIRO_NOME = "primeiro_nome";
    private static final String ULTIMO_NOME = "ultimo_nome";
    private static final String DATA_NASCIMENTO = "data_nascimentoS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Recebe os IDs da Activity Profile
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SingletonGestorCiclismo.getInstancia(this).setPerfilListener(this);

        // Chama a função da API para receber os dados do utilizador
        SingletonGestorCiclismo.getInstancia(this).getUserDados(this);

        // ----------------------- Inicio da Bottom-navbar --------------------------------
       BottomNavBarFragment fragment = new BottomNavBarFragment();

        if(fragment != null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.navBarMainPage, fragment)
                    .commit();
            fragment.lockPerfil = true;
        }
        // ------------------------ Fim da Bottom-navbar -----------------------------------

        binding.btGuardarAlteracoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean sucesso = false;
                // Codigo para guardar na BD as alterações



                // POP-UP que os dados foram guardados com sucesso ou com insucesso
                if (!sucesso) Toast.makeText(getApplicationContext(), R.string.txtGuardadoSemSucesso, Toast.LENGTH_SHORT).show();
                else Toast.makeText(getApplicationContext(), R.string.txtGuardadoSucesso, Toast.LENGTH_SHORT).show();
            }
        });

    }

    // Preencher o perfil com dados do utilizador
    private void dadosExemploPerfil() {
        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);

        // Dados do utilizador
        binding.ivFotoPerfil.setImageResource(R.drawable.ic_launcher_foreground);
        binding.tvNomePerfil.setText(sharedPreferences.getString(PRIMEIRO_NOME, "") + " " + sharedPreferences.getString(ULTIMO_NOME, "s"));
        binding.etPrimeiroNomePerfil.setText(sharedPreferences.getString(PRIMEIRO_NOME, ""));
        binding.etUltimoNomePerfil.setText(sharedPreferences.getString(ULTIMO_NOME, "s"));
        binding.etDataNascimentoPerfil.setText(sharedPreferences.getString(DATA_NASCIMENTO, ""));

        // Dados das atividades
        binding.tvDistanciaPerfil.setText("Distância: " + SingletonGestorCiclismo.getInstancia(this).getDistancia() + " m");
        binding.tvTempoPerfil.setText("Tempo: " + SingletonGestorCiclismo.getInstancia(this).getDuracao() + " s");
        binding.tvVelMediaPerfil.setText("Vel Média: " + SingletonGestorCiclismo.getInstancia(this).getVelocidadeMedia() + " km/h");
        binding.tvVelMaxPerfil.setText("Vel Máxima: " + SingletonGestorCiclismo.getInstancia(this).getVelocidadeMaxima() + " km/h");
    }

    // Preenche o perfil com os dados atualizados do perfil
    @Override
    public void perfilDados(Map<String, String> dadosUser) {
        // Guarda os dados da API na Shared Preferences
        SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PRIMEIRO_NOME, dadosUser.get("primeiro_nome"));
        editor.putString(ULTIMO_NOME, dadosUser.get("ultimo_nome"));

        // Se a data de nascimento for NULL retorna vazio
        if (dadosUser.get("data_nascimento").equals(null)){
            editor.putString(DATA_NASCIMENTO, dadosUser.get("data_nascimento")); }
        else {
            editor.putString(DATA_NASCIMENTO, "");
        }

        editor.apply();

        dadosExemploPerfil();
    }
}