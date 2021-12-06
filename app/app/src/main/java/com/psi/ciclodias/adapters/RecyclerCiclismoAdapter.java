package com.psi.ciclodias.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.psi.ciclodias.R;
import com.psi.ciclodias.model.Ciclismo;

import java.util.ArrayList;

public class RecyclerCiclismoAdapter extends RecyclerView.Adapter<RecyclerCiclismoAdapter.ViewHolderCiclismo> {
    private Context context;
    private ArrayList<Ciclismo> listaCiclismo;

    // Contrutor que recebe o contexto e a lista com as atividades do utilizador
    public RecyclerCiclismoAdapter(Context context, ArrayList<Ciclismo> lista){
        this.context = context;
        this.listaCiclismo = lista;
    }

    @NonNull
    @Override
    public RecyclerCiclismoAdapter.ViewHolderCiclismo onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ciclismo_cartao, parent, false);

        return new ViewHolderCiclismo(item);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerCiclismoAdapter.ViewHolderCiclismo holder, int position) {
        Ciclismo ciclismo = listaCiclismo.get(position);

        holder.update(ciclismo);
    }

    @Override
    public int getItemCount() {
        return listaCiclismo.size();
    }

    public class ViewHolderCiclismo extends RecyclerView.ViewHolder {
        private TextView tvNomeAtividade, tvDuracao, tvDistancia, tvVelMedia;

        public ViewHolderCiclismo(@NonNull View itemView) {
            super(itemView);
            tvNomeAtividade = itemView.findViewById(R.id.tvNomeAtividadeCard);
            tvDuracao = itemView.findViewById(R.id.tvDuracaoCard);
            tvDistancia = itemView.findViewById(R.id.tvDistanciaCard);
            tvVelMedia = itemView.findViewById(R.id.tvVelMediaCard);
        }

        public void update(Ciclismo ciclismo) {
            tvNomeAtividade.setText(ciclismo.getNome_percurso());
            tvDuracao.setText("" + ciclismo.getDuracao());
            tvDistancia.setText("" + ciclismo.getDistancia());
            tvVelMedia.setText("" + ciclismo.getVelocidade_media());
        }
    }
}
