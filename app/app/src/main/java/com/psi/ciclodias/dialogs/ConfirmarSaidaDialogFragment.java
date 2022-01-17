package com.psi.ciclodias.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.widget.Toast;

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
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Deseja terminar o treino?").setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                intent = new Intent(getActivity(), ResultsTrainingActivity.class);
                mapFragment.getInstancia().onMyDestroy();
                startActivity(intent);
                getActivity().finish();
            }
        }).setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                intent = new Intent(getActivity(), InProgressTrainingActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        }).setCancelable(false);

        return builder.create();
    }
}