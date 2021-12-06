package com.psi.ciclodias.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.psi.ciclodias.R;
import com.psi.ciclodias.model.Ciclismo;

import java.util.ArrayList;

public class ListaCiclismoAdapter extends BaseAdapter {
    private Context contexto;
    private ArrayList<Ciclismo> ArrCiclismo;

    // Incorporar o item
    private LayoutInflater inflater;

    public ListaCiclismoAdapter(Context contexto, ArrayList<Ciclismo> ArrCiclismo) {
        this.contexto = contexto;
        this.ArrCiclismo = ArrCiclismo;
    }

    @Override
    public int getCount() {
        return ArrCiclismo.size();
    }

    @Override
    public Object getItem(int i) {
        // Retorna o objeto livro
        return ArrCiclismo.get(i);
    }

    @Override
    public long getItemId(int i) {
        // Retorna apenas o ID
        return ArrCiclismo.get(i).getId();
    }

    @Override
    public View getView(int i, View convertview, ViewGroup viewGroup) {
        // Receber os dados do layout inflate (livro)
        if (inflater == null){
            inflater = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        // Primeira incorporação do layout
        if (convertview == null) {
            convertview = inflater.inflate(R.layout.item_ciclismo_cartao, null);
        }

        ViewHolderLista holder = new ViewHolderLista(convertview);
        holder.update(ArrCiclismo.get(i));

        return convertview;
    }

    private class ViewHolderLista{
        private TextView tvTitulo, tvSerie, tvAno, tvAutor;
        private ImageView ivCapa;

        public ViewHolderLista(View view){
            /*tvTitulo = view.findViewById(R.id.tvTituloItem);
            tvAutor = view.findViewById(R.id.tvAutorItem);
            tvSerie = view.findViewById(R.id.tvSerieItem);
            tvAno = view.findViewById(R.id.tvAnoItem);
            ivCapa = view.findViewById(R.id.ivCapaItem);*/
        }

        // Preencher cada item com dados
        public void update(Ciclismo ciclismo){
            /*tvTitulo.setText(livro.getTitulo());
            tvSerie.setText(livro.getSerie());
            tvAutor.setText(livro.getAutor());
            tvAno.setText("" + livro.getAno());
            //ivCapa.setImageResource(livro.getCapa());
            // Descarrega a imagem
            Glide.with(contexto)
                    .load(livro.getCapa())
                    .placeholder(R.drawable.logoipl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(ivCapa);*/
        }
    }
}
