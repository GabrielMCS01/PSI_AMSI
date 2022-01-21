package com.psi.ciclodias.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.psi.ciclodias.databinding.ActivityResultsTrainingBinding;
import com.psi.ciclodias.model.SingletonGestorCiclismo;
import com.psi.ciclodias.utils.CiclismoJsonParser;
import com.psi.ciclodias.view.MainPageActivity;
import com.psi.ciclodias.view.ResultsTrainingActivity;
import com.psi.ciclodias.view.mapFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ConfirmarGuardarDialogFragment extends DialogFragment {


    ActivityResultsTrainingBinding binding;

    public ConfirmarGuardarDialogFragment(ActivityResultsTrainingBinding resultsBinding){
        binding = resultsBinding;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //return super.onCreateDialog(savedInstanceState);

        // Classe default para construir a Alert Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Construi a Alert Dialog (Mensagem, Botão Positivo e Botão Negativo) e não permite cancelar
        builder.setMessage("Deseja sair e guardar o treino? - Ligue a internet antes de o fazer.").setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Map<String, String> dadosCiclismo = new HashMap<String, String>();

                int distance = (int) mapFragment.getInstancia().distance;
                // Recebe os dados do treino
                dadosCiclismo.put("nome_percurso", binding.etNomeTreino.getText().toString());
                dadosCiclismo.put("duracao", String.valueOf(mapFragment.getInstancia().time));
                dadosCiclismo.put("distancia", String.valueOf(distance));
                dadosCiclismo.put("velocidade_media", String.valueOf(mapFragment.getInstancia().velocityMean));
                dadosCiclismo.put("velocidade_maxima", String.valueOf(mapFragment.getInstancia().velocityMax));
                dadosCiclismo.put("velocidade_grafico", CiclismoJsonParser.createJsonVelocity(mapFragment.getInstancia().arrayVelocity).toString());
                dadosCiclismo.put("rota", mapFragment.getInstancia().routeString);

                // Redefine os dados do fragment para 0 de modo ao próximo treino não ter dados erráticos
                mapFragment.getInstancia().distance = 0;
                mapFragment.getInstancia().velocityMax = 0;
                mapFragment.getInstancia().velocityMean = 0;
                mapFragment.getInstancia().time = 0;
                mapFragment.getInstancia().velocityInstant = 0;
                mapFragment.getInstancia().arrayVelocity = new ArrayList<>();

                // Método para adicionar o treino na API
                SingletonGestorCiclismo.getInstancia(getActivity().getApplicationContext()).AddCiclismo(dadosCiclismo, getActivity().getApplicationContext());
                dismiss();
            }
        }).setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Mantem o utilizador no resumo
                dismiss();
            }
        }).setCancelable(false);

        // Cria a Dialog
        return builder.create();
    }
}
