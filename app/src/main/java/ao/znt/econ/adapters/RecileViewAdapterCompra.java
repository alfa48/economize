package ao.znt.econ.adapters;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import ao.znt.econ.*;
import android.view.ViewGroup;
import android.support.v7.widget.CardView;
import android.view.View;
import java.util.List;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.LayoutInflater;
import ao.znt.econ.modelos.Compra;
import ao.znt.econ.util.Util;

public class RecileViewAdapterCompra extends  RecyclerView.Adapter<RecileViewAdapterCompra.ClubViewHolder> {

	private final List<Compra>compras;
	private int position;

	public int getPosition() { return position; }
	public void setPosition(int position) { this.position = position; }

	public RecileViewAdapterCompra(List<Compra> compras) {
		this.compras = compras;     
	}
	@NonNull
	@Override
	public RecileViewAdapterCompra.ClubViewHolder  onCreateViewHolder(ViewGroup p1, int p2) {                        
		View view = LayoutInflater.from(p1.getContext()).inflate(R.layout.item_compra, p1, false);
		return new ClubViewHolder(view);
	}
	@Override
	public void onBindViewHolder(RecileViewAdapterCompra.ClubViewHolder holder, int position) {
		holder.compraDescricao.setText(compras.get(position).descricao); 
		holder.compraQuantidade.setText(String.valueOf(compras.get(position).quantidade));
		holder.compraPreco.setText(String.valueOf(compras.get(position).preco));
		//gerar data umanizada
		String dataHumanizada = Util.getDataHumanizada(compras.get(position).getDataDaCompra());
		holder.compraData.setText(dataHumanizada);

		holder.linearLayout.setOnLongClickListener(v -> { setPosition(holder.getAdapterPosition());return false; });
	}
	@Override
	public void onViewRecycled(RecileViewAdapterCompra.ClubViewHolder holder) {
		holder.itemView.setOnLongClickListener(null);
		super.onViewRecycled(holder);
	}
	@Override
	public int getItemCount() {
		return compras.size();
	}
	//atalizar a lista
	public void updateList(List<Compra> itens) {
		if (itens != null) {
			compras.clear();
			compras.addAll(itens);
			notifyDataSetChanged();
		}
	}
	public static class ClubViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
		CardView cardView;
		LinearLayout linearLayout;
		TextView compraDescricao, compraPreco, compraData, compraQuantidade;

		ClubViewHolder(View itemView) {
			super(itemView);
			cardView = itemView.findViewById(R.id.cardViewCompra);
			linearLayout = itemView.findViewById(R.id.linearlayoutCompra);
			compraDescricao = itemView.findViewById(R.id.cardTextViewCompraDescricao);
			compraPreco = itemView.findViewById(R.id.cardTextViewCompraPreco);
			compraData = itemView.findViewById(R.id.cardTextViewCompraData);
			compraQuantidade = itemView.findViewById(R.id.cardTextViewCompraQuantidade);
			itemView.setOnCreateContextMenuListener(this);
		}
		@Override
		public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
			menu.add(menu.NONE,0,menu.NONE,R.string.editar);
			menu.add(menu.NONE,1,menu.NONE,R.string.deletar);
		}
	}
}
