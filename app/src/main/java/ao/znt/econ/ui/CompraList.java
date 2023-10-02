package ao.znt.econ.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import ao.znt.econ.*;
import java.util.ArrayList;
import android.support.v7.widget.RecyclerView;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import ao.znt.econ.adapters.RecileViewAdapterCompra;
import ao.znt.econ.dao.OrcamentoDao;
import ao.znt.econ.modelos.Compra;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.content.Intent;


import ao.znt.econ.dao.DatabaseHelper;
import ao.znt.econ.dao.CompraDao;
import ao.znt.econ.util.Util;

import java.sql.SQLException;
import java.util.Objects;

public class CompraList extends AppCompatActivity {

	RecileViewAdapterCompra adapter;
	RecyclerView recyclerView;
    Context context; 
    public ArrayList<Compra> compras;
	private CompraDao dao;
	private OrcamentoDao orcDao;
	private Toolbar mToolbar;
	private long id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.scrolling_compras);

		DatabaseHelper helper = new DatabaseHelper(CompraList.this);
		try { dao = new CompraDao(helper.getConnectionSource()); orcDao = new OrcamentoDao(helper.getConnectionSource()); } catch (SQLException e) { e.printStackTrace(); }


		mToolbar = findViewById(R.id.toolbarListCompras);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
		
		recyclerView = findViewById(R.id.recyclerViewListCompras);
        recyclerView.setHasFixedSize(true); 
		
		LinearLayoutManager layoutManager = new LinearLayoutManager(context); 
        recyclerView.setLayoutManager(layoutManager); 

        initializeData();
		adapter = new RecileViewAdapterCompra(compras);
		initializeAdapter();

		findViewById(R.id.app_bar_list_compra);

		findViewById(R.id.fab_add_compra).setOnClickListener(p1 -> {
			//ir para a tela de add compra
			startActivity(new Intent(CompraList.this, ao.znt.econ.ui.Compra.class));
		});
    }
	@Override
	protected void onResume() {
		super.onResume();
		initializeData();
		initializeAdapter();
	}
	private void initializeData(){
		try {
			if(orcDao != null && listarComprasEspecificas()){
				ao.znt.econ.modelos.Orcamento orcamento = orcDao.queryForId((int) id);
				compras = new ArrayList<>(orcamento.getCompras());
				mToolbar.setTitle(getString(R.string.categoria)+Util.getCategoria(orcamento.getCategoria()));
			} else compras = (ArrayList<ao.znt.econ.modelos.Compra>) dao.queryForAll();

		} catch (SQLException e) { e.printStackTrace(); }
    }
        private void initializeAdapter(){
    	    adapter.updateList(compras);
			recyclerView.setAdapter(adapter);
      }
      public boolean listarComprasEspecificas(){
		  //LISTAR COMPRAS DE UM ORC ESPECIFICO
		  if(getIntent().getExtras() != null){
			  Bundle cofre = getIntent().getExtras();
			  if(cofre.getString(Util.CHAVE_ORCAMENTO_ID) != null){
				  if(cofre.getString(Util.CHAVE_ORCAMENTO_LISTAR_COMPRAS) != null){
					  if(cofre.getString(Util.CHAVE_ORCAMENTO_LISTAR_COMPRAS).equals(cofre.getString(Util.VALOR_ORCAMENTO_LISTAR_COMPRAS))){
						id = Long.parseLong(cofre.getString(Util.CHAVE_ORCAMENTO_ID));
					  	return true;
					  }
				  }
			  }
		  }
		  return false;
	  }
	@Override
	public boolean onContextItemSelected(@NonNull MenuItem item) {
		int position = adapter.getPosition();
		ao.znt.econ.modelos.Compra compra = compras.get(position);
		switch (item.getItemId()){
			case 0:irParaTelaDeCompraEditar(compra);
				return true;
			case 1:alertDialogDelete(compra);
				return true;
			default:return super.onContextItemSelected(item);
		}
	}
	private void irParaTelaDeCompraEditar(ao.znt.econ.modelos.Compra compra) {
			Intent intent = new Intent(this,ao.znt.econ.ui.Compra.class);
			intent.putExtra(Util.CHAVE_COMPRA_ID,""+compra.getId());
			startActivity(intent);
	}
	private void deletar(ao.znt.econ.modelos.Compra compra) {
		try {
			double saldoReposto = compra.getPreco();
			int result = dao.delete(compra);
			if (result == 1) {
				ao.znt.econ.modelos.Orcamento orcamento = orcDao.queryForId((int) compra.getOrc().getId());
			     orcamento.setSaldo(orcamento.getSaldo() + saldoReposto);
			     orcamento.setSaldoGasto(orcamento.getSaldoGasto()-saldoReposto);
				 orcamento.getCompras().remove(compra);
			     int result2 = orcDao.update(orcamento);
			     if(result2 == 1) mostraAlerta(getString(R.string.dinheiro_reposto));
			     onResume();
			}
		} catch (SQLException throwables) { throwables.printStackTrace(); }
	}
	private void alertDialogDelete(ao.znt.econ.modelos.Compra compra) {

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setCancelable(false);
		alertDialogBuilder.setMessage(R.string.deseja_descartar_o_compra);
		alertDialogBuilder.setTitle(getString(R.string.alerta));
		alertDialogBuilder.setNegativeButton(R.string.cancelar,(d,w)-> d.dismiss());
		alertDialogBuilder.setPositiveButton("Ok",(d,w)-> deletar(compra));
		alertDialogBuilder.create().show();
	}
	private Snackbar mostraAlerta(String ms){
		Snackbar snackbar = Snackbar
				.make(findViewById(R.id.list_compra_layoutRelativeLayout),ms, Snackbar.LENGTH_LONG);
		snackbar.show();
		return snackbar;
	}
}