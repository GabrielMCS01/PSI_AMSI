package com.psi.ciclodias;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class BottomNavBarFragment extends Fragment {
    private ImageButton btHome, btTreino, btPerfil;

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
                Intent intent = new Intent(getContext(), MainPageActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        btTreino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), StartTrainingActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        btPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ProfileActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        return view;
    }
}