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
import com.psi.ciclodias.view.MainPageActivity;
import com.psi.ciclodias.view.ResultsTrainingActivity;
import com.psi.ciclodias.view.mapFragment;

import java.util.ArrayList;

public class ConfirmarNaoGuardarDialogFragment extends DialogFragment {

    Intent intent;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //return super.onCreateDialog(savedInstanceState);

        // Classe default para construir a Alert Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Construi a Alert Dialog (Mensagem, Botão Positivo e Botão Negativo) e não permite cancelar
        builder.setMessage("Deseja sair sem guardar o treino?").setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Sai do resumo e redireciona o utilizador para o MainMenu
                intent = new Intent(getActivity(), MainPageActivity.class);
                mapFragment.getInstancia().distance = 0;
                mapFragment.getInstancia().velocityMax = 0;
                mapFragment.getInstancia().velocityMean = 0;
                mapFragment.getInstancia().time = 0;
                mapFragment.getInstancia().velocityInstant = 0;
                mapFragment.getInstancia().arrayVelocity = new ArrayList<>();

                mapFragment.getInstancia().onMyDestroy();
                startActivity(intent);
                getActivity().finish();
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
