package com.psi.ciclodias.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class ConfirmarApagarUserDialogFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //return super.onCreateDialog(savedInstanceState);

        // Classe default para construir a Alert Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Construi a Alert Dialog (Mensagem, Botão Positivo e Botão Negativo) e não permite cancelar
        builder.setMessage("Deseja apagar o seu perfil de utilizador?").setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                apagarPerfilListener.onApagarClick();
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

    ApagarPerfilListener apagarPerfilListener;

    // Método para apagar o utilizador
    public interface ApagarPerfilListener{
        void onApagarClick();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            apagarPerfilListener = (ConfirmarApagarUserDialogFragment.ApagarPerfilListener) context;
        }catch (ClassCastException e){

        }
    }
}
