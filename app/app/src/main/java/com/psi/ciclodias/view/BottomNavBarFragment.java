package com.psi.ciclodias.view;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.psi.ciclodias.R;

public class BottomNavBarFragment extends Fragment {
    private ImageButton btHome, btTreino, btPerfil;
    public boolean lockHome = false, lockTraining = false, lockPerfil = false;
    private int count = 0;

    public BottomNavBarFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bottom_nav_bar, container, false);

        btHome = view.findViewById(R.id.imgBtHome);
        btTreino = view.findViewById(R.id.imgBtTreino);
        btPerfil = view.findViewById(R.id.imgBtPerfil);

        btHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Se o utilizador estiver numa activity diferente da que carregou para ir
                if(!lockHome) {
                    // Caso tenha uma sessão de treino em progresso, esta é destruida
                    if(lockTraining){
                        mapFragment.getInstancia().onMyDestroy();
                    }
                    Intent intent = new Intent(getContext(), MainPageActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        });

        btTreino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Se o utilizador estiver numa activity diferente da que carregou para ir
                if(!lockTraining) {
                    Intent intent = new Intent(getContext(), StartTrainingActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }else{
                    if(count == 75) {
                        Toast.makeText(getContext(), "I told you!", Toast.LENGTH_LONG).show();
                        getActivity().finish();
                    }else if(count == 50){
                        Toast.makeText(getContext(), "STOP!!!!", Toast.LENGTH_LONG).show();
                    }else if(count == 25) {
                        Toast.makeText(getContext(), "Stop!", Toast.LENGTH_LONG).show();
                    }
                    count++;
                }
            }
        });

        btPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Se o utilizador estiver numa activity diferente da que carregou para ir
                if(!lockPerfil) {
                    // Caso tenha uma sessão de treino em progresso, esta é destruida
                    if(lockTraining){
                        mapFragment.getInstancia().onMyDestroy();
                    }
                    Intent intent = new Intent(getContext(), ProfileActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        });
        return view;
    }
}