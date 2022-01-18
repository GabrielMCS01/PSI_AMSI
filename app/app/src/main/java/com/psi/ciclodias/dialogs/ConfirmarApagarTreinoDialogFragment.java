package com.psi.ciclodias.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class ConfirmarApagarTreinoDialogFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //return super.onCreateDialog(savedInstanceState);

        // Classe default para construir a Alert Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Construi a Alert Dialog (Mensagem, Botão Positivo e Botão Negativo) e não permite cancelar
        builder.setMessage("Deseja apagar a sessão de treino?").setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                apagarTreinoListener.onApagarClick();
            }
        }).setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dismiss();
            }
        }).setCancelable(false);

        // Cria a Dialog
        return builder.create();
    }

    ApagarTreinoListener apagarTreinoListener;

    // Método para apagar o treino
    public interface ApagarTreinoListener{
        void onApagarClick();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            apagarTreinoListener = (ApagarTreinoListener) context;
        }catch (ClassCastException e){

        }
    }
}
