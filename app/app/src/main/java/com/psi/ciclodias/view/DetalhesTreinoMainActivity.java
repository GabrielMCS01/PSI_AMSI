package com.psi.ciclodias.view;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.psi.ciclodias.R;
import com.psi.ciclodias.databinding.ActivityDetalhesTreinoMainBinding;
import com.psi.ciclodias.dialogs.ConfirmarApagarTreinoDialogFragment;
import com.psi.ciclodias.listeners.CiclismoListener;
import com.psi.ciclodias.listeners.PublicacaoListener;
import com.psi.ciclodias.listeners.RotaListener;
import com.psi.ciclodias.model.Ciclismo;
import com.psi.ciclodias.model.SingletonGestorCiclismo;
import com.psi.ciclodias.utils.Converter;

public class DetalhesTreinoMainActivity extends AppCompatActivity implements CiclismoListener, ConfirmarApagarTreinoDialogFragment.ApagarTreinoListener, RotaListener, PublicacaoListener {
    private ActivityDetalhesTreinoMainBinding binding;
    public static String POSITION_TREINO = "position";
    public int position, id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDetalhesTreinoMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Caso tenha permissão para localização, carrega o mapa
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Fragment mapfragment = mapFragment.getInstancia();

            if(mapfragment != null){
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mapViewResult, mapfragment)
                        .commit();

                mapFragment.getInstancia().isDetails = true;
                SingletonGestorCiclismo.getInstancia(this).setCiclismoListener(this);
                mapFragment.getInstancia().setRotaListener(this);
            }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        // Instancia para poder criar uma publicação
        SingletonGestorCiclismo.getInstancia(getApplicationContext()).setPublicacaoListener(this);

        // Recebe a posição do treino selecionada na recycler view (DB local)
        Intent intent = getIntent();
        position = intent.getIntExtra(POSITION_TREINO, -1);

        // Recebe o treino com este ID
        Ciclismo ciclismo = SingletonGestorCiclismo.getInstancia(this).getCiclismo(position);

        // Recebe e converte o ID do treino
        id = (int)ciclismo.getId();

        // Preencher as textViews com os dados do treino
        binding.tvDistanciaDetalhes.setText(Converter.distanceFormat(ciclismo.getDistancia()));
        binding.tvTempoDetalhes.setText(Converter.hourFormat(ciclismo.getDuracao()));
        binding.tvVelMaxDetalhes.setText(Converter.velocityFormat(ciclismo.getVelocidade_maxima()));
        binding.tvVelMediaDetalhes.setText(Converter.velocityFormat(ciclismo.getVelocidade_media()));
        binding.etNomeTreinoDetalhes.setText(ciclismo.getNome_percurso());
        binding.tvData.setText(ciclismo.getData_treino());

        // Volta para o menu principal
        binding.btVoltarDetalhes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapFragment.getInstancia().onMyDestroy();
                Intent intent = new Intent(getApplicationContext(), MainPageActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Botão para guardar as alterações no nome do treino
        binding.btGuardarDetalhes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nome = binding.etNomeTreinoDetalhes.getText().toString();

                SingletonGestorCiclismo.getInstancia(getApplicationContext()).EditCiclismo(nome, ciclismo.getId(), getApplicationContext());
            }
        });

        // Botão para publicar o treino
        binding.btPublicarTreino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("ID: " + ciclismo.getId());
                SingletonGestorCiclismo.getInstancia(getApplicationContext()).publicar(ciclismo.getId(), getApplicationContext());
            }
        });

    }

    // Adiciona o botão na action bar para apagar o treino
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.delete, menu);
        return true;
    }

    // Se algum item da action bar for selecionado faz
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        // Recebe o ID do item selecionado
        int id = item.getItemId();

        // Se o item selecionado for o botão para apagar o treino faz
        if (id == R.id.actionDelete) {
            DialogFragment dialogFragment = new ConfirmarApagarTreinoDialogFragment();
            dialogFragment.show(getSupportFragmentManager(), "dialog");

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void editCiclismo(Boolean success) {
        // Se os dados não foram guardados
        if (!success) {
            Toast.makeText(getApplicationContext(), R.string.txtGuardadoSemSucesso, Toast.LENGTH_SHORT).show();
        }
        // Dados foram guardados com sucesso
        else {
            Toast.makeText(getApplicationContext(), R.string.txtGuardadoSucesso, Toast.LENGTH_SHORT).show();
            mapFragment.getInstancia().onMyDestroy();
            Intent intent = new Intent(getApplicationContext(), MainPageActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void removeCiclismo(Boolean success) {
        // Treino não foi apagado
        if (!success) {
            Toast.makeText(getApplicationContext(), R.string.txtCiclismoNaoRemovido, Toast.LENGTH_SHORT).show();
        }
        // Treino apagado com sucesso
        else {
            Toast.makeText(getApplicationContext(), R.string.txtCiclismoRemovido, Toast.LENGTH_SHORT).show();
            mapFragment.getInstancia().onMyDestroy();
            Intent intent = new Intent(getApplicationContext(), MainPageActivity.class);
            startActivity(intent);
            finish();
        }
    }

    // Botão na action bar para apagar o treino
    @Override
    public void onApagarClick() {
        SingletonGestorCiclismo.getInstancia(this).DeleteCiclismo(id, getApplicationContext());
    }

    // Desenha a rota do treino realizado pelo utilizador no mapa
    @Override
    public void setRoute() {
        Ciclismo ciclismo = SingletonGestorCiclismo.getInstancia(this).getCiclismo(position);

        // Rota pode ser nula e dá crash na aplicação
        String rota = ciclismo.getRota();
        System.out.println(rota);
        if (rota == null){
            rota = "null";
        }

        mapFragment.getInstancia().setRoute(rota, this);
    }


    @Override
    public void onBackPressed() {
        mapFragment.getInstancia().onMyDestroy();
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mapFragment.getInstancia().mapboxNavigation != null) {
            mapFragment.getInstancia().onMyDestroy();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mapFragment.getInstancia().startNavigation();
            mapFragment.getInstancia().isDetails = true;
            mapFragment.getInstancia().setRotaListener(this);
        }
    }

    // Resposta do dialog para permissões de localização
    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                // Se já tiver permissões, carrega o fragmento do mapa
                if (isGranted) {
                    Fragment mapfragment = mapFragment.getInstancia();

                    if(mapfragment != null){
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.mapViewResult, mapfragment)
                                .commit();
                    }
                }
                // Caso contrário, o utilizador volta para a página principal
                else {
                    Intent intent = new Intent(getApplicationContext(), MainPageActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

    @Override
    public void criarPublicacao(String mensagem) {
        // TOAST a informar se o treino foi publicado ou não
        Toast.makeText(getApplicationContext(), mensagem, Toast.LENGTH_LONG).show();
    }
}