package ao.znt.econ.ui;


import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import ao.znt.econ.*;
import android.content.Context;

import ao.znt.econ.dao.UsuarioDao;
import ao.znt.econ.modelos.Orcamento;
import java.util.ArrayList;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;
import ao.znt.econ.adapters.RecicleViewsAdapter;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.AppBarLayout;
import android.view.MenuItem;
import android.content.Intent;

import ao.znt.econ.dao.DatabaseHelper;
import ao.znt.econ.dao.OrcamentoDao;
import ao.znt.econ.modelos.Usuario;
import ao.znt.econ.util.Unica;
import ao.znt.econ.util.Util;

import java.sql.SQLException;

public class OrcList extends AppCompatActivity {
	RecicleViewsAdapter adapter;
	RecyclerView recyclerView;
    Context context;
	private OrcamentoDao dao;
	private UsuarioDao usuarioDao;
	private int size;
	
    public ArrayList<Orcamento> orcamentos; 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.scrolling_orcs);

		DatabaseHelper helper = new DatabaseHelper(OrcList.this);
		try {
			usuarioDao = new UsuarioDao(helper.getConnectionSource());
			dao = new OrcamentoDao(helper.getConnectionSource());
			size = dao.queryForAll().size();
		} catch (SQLException e) { mostraAlerta(getString(R.string.Erro_ao_acessar_os_dados)); }
		
		final Toolbar mToolbar = findViewById(R.id.toolbarListOrcs);
		if(size==0) mToolbar.setTitle(R.string.faca_seu_primeiro_orcamento);
        setSupportActionBar(mToolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			
		recyclerView = findViewById(R.id.recyclerViewListOrcs);
        recyclerView.setHasFixedSize(true); 
		
		LinearLayoutManager layoutManager = new LinearLayoutManager(context); 
        recyclerView.setLayoutManager(layoutManager); 

        initializeData();
		adapter = new RecicleViewsAdapter(orcamentos);
        initializeAdapter(); 
		
		
		AppBarLayout mAppBarLayout =  findViewById(R.id.app_bar_list_orc);
		
		findViewById(R.id.fab_add_orc).setOnClickListener(p1 -> {
			//ir para tels add orcs
			startActivity(new Intent(OrcList.this, ao.znt.econ.ui.Orcamento.class));
		});
    }
	private void initializeData(){
		try {
			orcamentos = (ArrayList<Orcamento>) dao.queryForAll();
		} catch (SQLException e) { mostraAlerta(getString(R.string.Erro_ao_acessar_os_dados)); }
    }
    private void initializeAdapter(){
                adapter.updateList(orcamentos);
                recyclerView.setAdapter(adapter);
    }
	@Override
	protected void onResume() {
		super.onResume();
		initializeData();
		initializeAdapter();
	}
	@Override
	public boolean onContextItemSelected(@NonNull MenuItem item) {
		int position = adapter.getPosition();
		Orcamento orc = orcamentos.get(position);
		switch (item.getItemId()){
			case 0:irParaTelaDeOrcamentoEditar(orc);
				return true;
			case 1:alertDialogDelete(orc);
				return true;
			case 2:listarComprasDo(orc);
				return true;
			default:return super.onContextItemSelected(item);
		}
	}
	private void irParaTelaDeOrcamentoEditar(Orcamento orc) {
		if(orc.getSaldoGasto() > 0) mostraAlerta(getString(R.string.nao_pode_alterar_orc_em_uso));
		else {
			Intent intent = new Intent(this,ao.znt.econ.ui.Orcamento.class);
			intent.putExtra(Util.CHAVE_ORCAMENTO_ID,""+orc.getId());
			startActivity(intent);
		}
	}
	private void deletar(Orcamento orc) {
		try {
			if(orc.getSaldoGasto() > 0) mostraAlerta(getString(R.string.nao_deletar_orc_em_uso));
			else {
				double saldoReposto = orc.getSaldo();
				Usuario usuario = Unica.getInstance();
				int result = dao.delete(orc);
				if(result==1){
					mostraAlerta(getString(R.string.orc_deletado));
					usuario.setRendaMensal(usuario.getRendaMensal()+saldoReposto);
					usuarioDao.update(usuario);
					onResume();
				}
			}
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}
	}
	private void mostraAlerta(String ms){
		Snackbar snackbar = Snackbar
				.make(findViewById(R.id.list_orc_layoutRelativeLayout),ms, Snackbar.LENGTH_LONG);
		snackbar.show();
	}
	private void alertDialogDelete(ao.znt.econ.modelos.Orcamento orc) {

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setCancelable(false);
		alertDialogBuilder.setMessage(R.string.deseja_deletar_o_orc);
		alertDialogBuilder.setTitle(getString(R.string.alerta));
		alertDialogBuilder.setNegativeButton(R.string.cancelar,(d,w)-> d.dismiss());
		alertDialogBuilder.setPositiveButton("Ok",(d,w)-> deletar(orc));
		alertDialogBuilder.create().show();
	}
	private void listarComprasDo(ao.znt.econ.modelos.Orcamento orcamento){
		Intent intent = new Intent(this,ao.znt.econ.ui.CompraList.class);
		intent.putExtra(Util.CHAVE_ORCAMENTO_LISTAR_COMPRAS,Util.VALOR_ORCAMENTO_LISTAR_COMPRAS);
		intent.putExtra(Util.CHAVE_ORCAMENTO_ID,""+orcamento.getId());
		startActivity(intent);
	}
}