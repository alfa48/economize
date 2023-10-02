package ao.znt.econ.adapters;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import ao.znt.econ.*;

import android.view.ContextMenu;
import android.view.ViewGroup;
import android.support.v7.widget.CardView;
import android.view.View;

import java.util.Calendar;
import java.util.List;
import ao.znt.econ.modelos.Orcamento;
import ao.znt.econ.util.Util;

import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.LayoutInflater;

public class RecicleViewsAdapter extends  RecyclerView.Adapter<RecicleViewsAdapter.ClubViewHolder> {

    private final List<Orcamento> orcs;
    private int position;

    public void setPosition(int position) {
        this.position = position;
    }
    public int getPosition() {
        return position;
    }

    public RecicleViewsAdapter(List<Orcamento> orcs) {
        this.orcs = orcs;
    } 
    @NonNull
    @Override
    public RecicleViewsAdapter.ClubViewHolder onCreateViewHolder(ViewGroup p1, int p2) {                        
        View view = LayoutInflater.from(p1.getContext()).inflate(R.layout.item, p1, false);

        return new ClubViewHolder(view);
    }
    @Override
    public void onBindViewHolder(RecicleViewsAdapter.ClubViewHolder holder,int position) {
        ao.znt.econ.modelos.Orcamento orc = orcs.get(position);

            holder.orcDescricao.setText(orc.descricao);
            holder.tvCategoria.setText(Util.getCategoria(orc.getCategoria()));
            holder.orcSaldo.setText(String.valueOf(orc.saldo));
            holder.tvValorGasto.setText(String.valueOf(orc.saldoGasto));
            holder.tvData.setText(Util.getMesString(Util.getMes((orc.getData() != null)? orc.getData(): Calendar.getInstance().getTime())));
            holder.linearLayout.setOnLongClickListener(v -> { setPosition(holder.getAdapterPosition());return false; });
    }
    @Override
    public void onViewRecycled(RecicleViewsAdapter.ClubViewHolder holder) {
        holder.itemView.setOnLongClickListener(null);
        super.onViewRecycled(holder);
    }
    @Override
    public int getItemCount() {
        return orcs.size();
    }
    //atalizar a lista
    public void updateList(List<Orcamento> itens){
        if(itens != null){
            orcs.clear();
            orcs.addAll(itens);
            notifyDataSetChanged();
        }
    }
    public static class ClubViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{
        CardView cardView;
        LinearLayout linearLayout;
        TextView orcDescricao, orcSaldo, tvCategoria,tvValorGasto, tvData;

        public ClubViewHolder(View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardView1);
            linearLayout = itemView.findViewById(R.id.linerlayout_item);
            tvCategoria = itemView.findViewById(R.id.itemTextViewCategoria_orcamento);
            orcDescricao = itemView.findViewById(R.id.cardTextView1descricao_orcamento);
            orcSaldo = itemView.findViewById(R.id.cardTextView1Saldo);
            tvData = itemView.findViewById(R.id.itemTextViewOrcamentoData);
            tvValorGasto = itemView.findViewById(R.id.itemTextViewOrcamentoGasto);
            itemView.setOnCreateContextMenuListener(this);
        }
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(menu.NONE,0,menu.NONE,R.string.editar);
            menu.add(menu.NONE,1,menu.NONE,R.string.deletar);
            menu.add(menu.NONE,2,menu.NONE,R.string.compras);
        }
    }
}
