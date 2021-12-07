package com.psi.ciclodias.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.psi.ciclodias.R;
import com.psi.ciclodias.databinding.ActivityProfileBinding;

public class ProfileActivity extends AppCompatActivity {
    private ActivityProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Recebe os IDs da Activity Profile
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dadosExemploPerfil();

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
        // Dados do utilizador
        binding.ivFotoPerfil.setImageResource(R.drawable.ic_launcher_foreground);
        binding.tvNomePerfil.setText("Nome Provisório");
        binding.etPrimeiroNomePerfil.setText("exemplo@mail.com");
        binding.etUltimoNomePerfil.setText("Nome Utilizador Provisório");
        binding.etDataNascimentoPerfil.setText("30-01-2001");

        // Dados das atividades
        binding.tvDistanciaPerfil.setText("Distância: 5980.1KM");
        binding.tvTempoPerfil.setText("Tempo: 73:20:30");
        binding.tvVelMedia.setText("Vel Média: 16.2 KM/H");
        binding.tvVelMaxPerfil.setText("Vel Máxima: 30.1 KM/H");
    }

}