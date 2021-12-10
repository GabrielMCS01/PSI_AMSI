package com.psi.ciclodias.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.psi.ciclodias.model.SingletonGestorCiclismo;
import com.psi.ciclodias.view.LoginActivity;

import java.util.ArrayList;

public class ConfirmarLogoutDialogFragment extends DialogFragment {

    public static final String TOKEN = "token";
    public static final String USER = "user";
    private static final String ID = "id";
    private static final String PRIMEIRO_NOME = "primeiro_nome";
    private static final String ULTIMO_NOME = "ultimo_nome";
    private static final String DATA_NASCIMENTO = "data_nascimento";


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Deseja terminar sessão?").setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SharedPreferences sharedPreferences = getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putString(TOKEN, "null");
                editor.putString(USER, "null");
                editor.putString(ID, "null");
                editor.putString(PRIMEIRO_NOME, "null");
                editor.putString(ULTIMO_NOME, "null");
                editor.putString(DATA_NASCIMENTO, "null");
                editor.apply();

                SingletonGestorCiclismo.getInstancia(getContext()).ArrCiclismo = new ArrayList<>();
                SingletonGestorCiclismo.getInstancia(getContext()).ArrCiclismoUnSync = new ArrayList<>();
                SingletonGestorCiclismo.getInstancia(getContext()).apagarCiclismoDBAll();

                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        }).setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getActivity(), ":( ", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        }).setCancelable(false);


        return builder.create();
    }
}
