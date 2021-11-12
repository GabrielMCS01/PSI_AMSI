package com.psi.ciclodias.view;

import android.content.Intent;
import android.os.Bundle;

import android.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.psi.ciclodias.R;

public class ConfirmarSaidaDialogFragment extends DialogFragment implements View.OnClickListener {
    Intent intent;

    static ConfirmarSaidaDialogFragment newInstance() {
        return new ConfirmarSaidaDialogFragment();
    }

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
        if (view.getId () == R.id.btDialogSim)
            startActivity(intent);

        if (view.getId () == R.id.btDialogNao)
            Toast.makeText(getActivity(), "no clicked", Toast.LENGTH_SHORT).show();
        dismiss();
    }

}