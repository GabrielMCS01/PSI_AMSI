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

        // Recebe os IDs da Activity Results Training
        binding = ActivityDetalhesTreinoMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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

        SingletonGestorCiclismo.getInstancia(getApplicationContext()).setPublicacaoListener(this);

        // Recebe a posição do treino selecionada na recycler view (DB local)
        Intent intent = getIntent();
        position = intent.getIntExtra(POSITION_TREINO, -1);

        // Recebe o treino com este ID
        Ciclismo ciclismo = SingletonGestorCiclismo.getInstancia(this).getCiclismo(position);

        id = (int)ciclismo.getId();

        // Preencher as textViews com os dados do treino
        binding.tvDistanciaDetalhes.setText(Converter.distanceFormat(ciclismo.getDistancia()));
        binding.tvTempoDetalhes.setText(Converter.hourFormat(ciclismo.getDuracao()));
        binding.tvVelMaxDetalhes.setText(Converter.velocityFormat(ciclismo.getVelocidade_maxima()));
        binding.tvVelMediaDetalhes.setText(Converter.velocityFormat(ciclismo.getVelocidade_media()));
        binding.etNomeTreinoDetalhes.setText(ciclismo.getNome_percurso());


        binding.btVoltarDetalhes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapFragment.getInstancia().onMyDestroy();
                Intent intent = new Intent(getApplicationContext(), MainPageActivity.class);
                startActivity(intent);
                finish();
            }
        });

        binding.btGuardarDetalhes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nome = binding.etNomeTreinoDetalhes.getText().toString();

                SingletonGestorCiclismo.getInstancia(getApplicationContext()).EditCiclismo(nome, ciclismo.getId(), getApplicationContext());
            }
        });


        binding.btPublicarTreino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                System.out.println("ID: " + ciclismo.getId());
                SingletonGestorCiclismo.getInstancia(getApplicationContext()).publicar(ciclismo.getId(), getApplicationContext());
            }
        });

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.delete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.actionDelete) {
            DialogFragment dialogFragment = new ConfirmarApagarTreinoDialogFragment();
            dialogFragment.show(getSupportFragmentManager(), "dialog");

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void editCiclismo(Boolean success) {
        // TOAST que os dados foram guardados com sucesso ou com insucesso
        if (!success) {
            Toast.makeText(getApplicationContext(), R.string.txtGuardadoSemSucesso, Toast.LENGTH_SHORT).show();
        }

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
        if (!success) {
            Toast.makeText(getApplicationContext(), R.string.txtCiclismoNaoRemovido, Toast.LENGTH_SHORT).show();
        }

        else {
            Toast.makeText(getApplicationContext(), R.string.txtCiclismoRemovido, Toast.LENGTH_SHORT).show();
            mapFragment.getInstancia().onMyDestroy();
            Intent intent = new Intent(getApplicationContext(), MainPageActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onApagarClick() {
        SingletonGestorCiclismo.getInstancia(this).DeleteCiclismo(id, getApplicationContext());
    }

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


    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Fragment mapfragment = mapFragment.getInstancia();

                    if(mapfragment != null){
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.mapViewResult, mapfragment)
                                .commit();
                    }
                } else {
                    Intent intent = new Intent(getApplicationContext(), MainPageActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

    @Override
    public void criarPublicacao(String mensagem) {

        Toast.makeText(getApplicationContext(), mensagem, Toast.LENGTH_LONG).show();
    }
}