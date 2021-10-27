package com.psi.ciclodias;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.psi.ciclodias.databinding.ActivityProfileBinding;

public class ProfileActivity extends AppCompatActivity {
    private ActivityProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Recebe os IDs da Activity Profile
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // ----------------------- Inicio da Bottom-navbar --------------------------------
        Fragment fragment = new BottomNavBarFragment();

        if(fragment != null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.navBarMainPage, fragment)
                    .commit();
        }
        // ------------------------ Fim da Bottom-navbar -----------------------------------

        // Atribuir ás textviews os dados na base de dados do utilizador
        binding.ivFotoPerfil.setImageResource(R.drawable.ic_launcher_foreground);
        binding.tvNomePerfil.setText("Nome Provisório");
        binding.etEmailPerfil.setText("exemplo@mail.com");
        binding.etUserPerfil.setText("Nome Utilizador Provisório");
        binding.etDataNascimentoPerfil.setText("30-01-2001");

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
}