package com.psi.ciclodias.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.psi.ciclodias.view.InProgressTrainingActivity;
import com.psi.ciclodias.view.ResultsTrainingActivity;
import com.psi.ciclodias.view.mapFragment;

public class ConfirmarSaidaDialogFragment extends DialogFragment {
    Intent intent;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //return super.onCreateDialog(savedInstanceState);

        // Classe default para construir a Alert Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Construi a Alert Dialog (Mensagem, Botão Positivo e Botão Negativo) e não permite cancelar
        builder.setMessage("Deseja terminar o treino?").setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Termina a sessão de treino e redireciona o utilizador para ver o resumo
                intent = new Intent(getActivity(), ResultsTrainingActivity.class);
                mapFragment.getInstancia().onMyDestroy();
                startActivity(intent);
                getActivity().finish();
            }
        }).setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Redireciona o utilizador para a sessão de treino
                intent = new Intent(getActivity(), InProgressTrainingActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        }).setCancelable(false);

        // Cria a Dialog
        return builder.create();
    }
}