package com.psi.ciclodias.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.psi.ciclodias.R;
import com.psi.ciclodias.databinding.ActivityProfileBinding;
import com.psi.ciclodias.dialogs.ConfirmarApagarUserDialogFragment;
import com.psi.ciclodias.dialogs.InserirDataFragment;
import com.psi.ciclodias.listeners.PerfilListener;
import com.psi.ciclodias.model.SingletonGestorCiclismo;
import com.psi.ciclodias.utils.Converter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity implements PerfilListener, InserirDataFragment.DateDialogListener, ConfirmarApagarUserDialogFragment.ApagarPerfilListener {
    private ActivityProfileBinding binding;

    private static final String TOKEN = "token";
    public static final String USER = "user";
    private static final String ID = "id";
    private static final String PRIMEIRO_NOME = "primeiro_nome";
    private static final String ULTIMO_NOME = "ultimo_nome";
    private static final String DATA_NASCIMENTO = "data_nascimento";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        SingletonGestorCiclismo.getInstancia(this).setPerfilListener(this);

        // Carrega os dados iniciais no perfil
        dadosPerfil();

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

        // Botão para guardar as alterações feitas ao utilizador
        binding.btGuardarAlteracoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Objeto com todos os dados editáveis do perfil
                Map<String, String> params = new HashMap<>();

                params.put("primeiro_nome", binding.etPrimeiroNomePerfil.getText().toString());
                params.put("ultimo_nome", binding.etUltimoNomePerfil.getText().toString());
                params.put("data_nascimento", binding.etDataNascimentoPerfil.getText().toString());

                SingletonGestorCiclismo.getInstancia(getApplicationContext()).EditUser(params, getApplicationContext());
            }
        });

        // Botão que carrega a dialog fragment para escolher a data de nascimento
        binding.ibDataNascimento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialogFragment = new InserirDataFragment();
                dialogFragment.show(getSupportFragmentManager(), "dialog");
            }
        });
    }

    // Carrega a action bar com o botão de menu para apagar o utilizador
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.delete, menu);
        return true;
    }

    // Verifica qual foi o item selecionado na action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // Se o item selecionado foi o de apagar o utilizador, é carregada uma dialog de confirmação
        if (id == R.id.actionDelete) {
            DialogFragment dialogFragment = new ConfirmarApagarUserDialogFragment();
            dialogFragment.show(getSupportFragmentManager(), "dialog");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Preencher o perfil com dados do utilizador
    private void dadosPerfil() {
        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);

        // Dados do utilizador
        binding.ivFotoPerfil.setImageResource(R.drawable.ic_launcher_foreground);
        binding.tvNomePerfil.setText(sharedPreferences.getString(PRIMEIRO_NOME, "") + " " + sharedPreferences.getString(ULTIMO_NOME, "s"));
        binding.etPrimeiroNomePerfil.setText(sharedPreferences.getString(PRIMEIRO_NOME, ""));
        binding.etUltimoNomePerfil.setText(sharedPreferences.getString(ULTIMO_NOME, "s"));
        binding.etDataNascimentoPerfil.setText(sharedPreferences.getString(DATA_NASCIMENTO, ""));

        // Dados das atividades
        binding.tvDistanciaPerfil.setText(Converter.distanceFormat(SingletonGestorCiclismo.getInstancia(this).getDistancia()));
        binding.tvTempoPerfil.setText(Converter.hourFormat(SingletonGestorCiclismo.getInstancia(this).getDuracao()));
        binding.tvVelMediaPerfil.setText(Converter.velocityFormat(SingletonGestorCiclismo.getInstancia(this).getVelocidadeMedia()));
        binding.tvVelMaxPerfil.setText(Converter.velocityFormat(SingletonGestorCiclismo.getInstancia(this).getVelocidadeMaxima()));
    }

    // Preenche o perfil com os dados atualizados do perfil
    @Override
    public void perfilDados(Map<String, String> dadosUser) {
        // Guarda os dados da API na Shared Preferences
        SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(dadosUser.get("mensagem") == null) {
            editor.putString(PRIMEIRO_NOME, dadosUser.get("primeiro_nome"));
            editor.putString(ULTIMO_NOME, dadosUser.get("ultimo_nome"));

            // Se a data de nascimento for NULL retorna vazio
            if (!dadosUser.get("data_nascimento").equals("nulo")) {
                editor.putString(DATA_NASCIMENTO, dadosUser.get("data_nascimento"));
            } else {
                editor.putString(DATA_NASCIMENTO, "");
            }

            editor.apply();
        }else{
            Toast.makeText(this, dadosUser.get("mensagem"), Toast.LENGTH_SHORT).show();
        }
        dadosPerfil();
    }

    @Override
    public void editUser(Boolean success) {
        // TOAST que os dados foram guardados com sucesso ou com insucesso
        if (!success) Toast.makeText(getApplicationContext(), R.string.txtGuardadoSemSucesso, Toast.LENGTH_SHORT).show();
        else Toast.makeText(getApplicationContext(), R.string.txtGuardadoSucesso, Toast.LENGTH_SHORT).show();
    }

    // Recebe a resposta da API e faz edições localmente
    @Override
    public void removeUser(Boolean success) {
        // Caso não remova o utilizador envia um TOAST a informar que ocorreu um erro
        if (!success) {
            Toast.makeText(getApplicationContext(), R.string.txtUserNaoRemovido, Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getApplicationContext(), R.string.txtUserRemovido, Toast.LENGTH_SHORT).show();

            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putString(TOKEN, "null");
            editor.putString(USER, "null");
            editor.putString(ID, "null");
            editor.putString(PRIMEIRO_NOME, "null");
            editor.putString(ULTIMO_NOME, "null");
            editor.putString(DATA_NASCIMENTO, "null");
            editor.apply();

            SingletonGestorCiclismo.getInstancia(this).ArrCiclismo = new ArrayList<>();
            SingletonGestorCiclismo.getInstancia(this).ArrCiclismoUnSync = new ArrayList<>();
            SingletonGestorCiclismo.getInstancia(this).apagarCiclismoDBAll();

            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        String dataNascimento = year + "-" + month + "-" + day;

        binding.etDataNascimentoPerfil.setText(dataNascimento);
    }

    @Override
    public void onApagarClick() {
        SingletonGestorCiclismo.getInstancia(this).DeleteUser(getApplicationContext());
    }
}