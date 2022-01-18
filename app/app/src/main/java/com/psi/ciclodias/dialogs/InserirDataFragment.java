package com.psi.ciclodias.dialogs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class InserirDataFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Cria um calendário com o Ano, Mês e dia
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    // Atribui a data correta
    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        // Mês começa no zero então adicionamos sempre +1
        month++;
        dateDialogListener.onDateSet(datePicker, year, month, day);
    }

    // Listener com os métodos para implementar
    public interface DateDialogListener{
        public void onDateSet(DatePicker datePicker, int year, int month, int day);
    }

    DateDialogListener dateDialogListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            dateDialogListener = (DateDialogListener) context;
        }catch (ClassCastException e){

        }
    }
}
