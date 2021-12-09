package com.psi.ciclodias.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.psi.ciclodias.R;
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
                Toast.makeText(getActivity(), "no clicked", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        }).setCancelable(false);

        return builder.create();
    }



    /*
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_confirmar_saida_dialog, container, false);
        intent = new Intent(getActivity(), ResultsTrainingActivity.class);

        Button btNao = view.findViewById(R.id.btDialogNao);
        Button btSim = view.findViewById(R.id.btDialogSim);
        btNao.setOnClickListener(this);
        btSim.setOnClickListener(this);
        setCancelable (false);

        return view;
    }

    @Override
    public void onClick(View view) {
        if (view.getId () == R.id.btDialogSim) {
            mapFragment.getInstancia().onMyDestroy();
            startActivity(intent);
        }
        if (view.getId () == R.id.btDialogNao)
            Toast.makeText(getActivity(), "no clicked", Toast.LENGTH_SHORT).show();
        dismiss();
    }
*/
}