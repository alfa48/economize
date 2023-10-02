package ao.znt.econ.ui;

import ao.znt.econ.*;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import ao.znt.econ.dao.DatabaseHelper;
import ao.znt.econ.dao.OrcamentoDao;
import ao.znt.econ.dao.UsuarioDao;
import ao.znt.econ.modelos.Usuario;
import ao.znt.econ.util.Unica;
import ao.znt.econ.util.Util;

import java.sql.SQLException;

import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.LinearLayout;
import android.support.design.widget.Snackbar;
import android.graphics.Color;
import android.widget.Toast;

public class Orcamento extends AppCompatActivity {
	private DatabaseHelper helper;
	private OrcamentoDao orcamentoDao;
	private UsuarioDao usuarioDao;
	EditText edDescricao, edSaldo;
	Spinner spCategoria;
	private LinearLayout layout;
	private ao.znt.econ.modelos.Orcamento orcamentoCorrente = null;//orcamento da edicao, vindo da tela principal

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.orcamento);
        new Util(this);

		setSupportActionBar(findViewById(R.id.toolbarOrcamento));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		helper = new DatabaseHelper(Orcamento.this);
		try {
			orcamentoDao = new OrcamentoDao(helper.getConnectionSource());
			usuarioDao = new UsuarioDao(helper.getConnectionSource());
		} catch (SQLException e) { e.printStackTrace(); }

		layout = findViewById(R.id.orcamentoLinearLayout);
		ArrayAdapter adapterSpinner = ArrayAdapter.createFromResource(this, R.array.categorias, android.R.layout.simple_list_item_1);
	    spCategoria = findViewById(R.id.orcamentoSpinner);
		spCategoria.setAdapter(adapterSpinner);
		edDescricao = findViewById(R.id.orcamentoEditTextDescricao);
		edSaldo = findViewById(R.id.orcamentoEditTextSaldo);

		//Para editar
		if(getIntent().getExtras()!=null){
			Bundle cofre = getIntent().getExtras();
			if(cofre.getString(Util.CHAVE_ORCAMENTO_ID) != null){
				long id = Long.parseLong(cofre.getString(Util.CHAVE_ORCAMENTO_ID));
				try {
					orcamentoCorrente = orcamentoDao.queryForId((int) id);
				} catch (SQLException throwables) {
					throwables.printStackTrace();
				}
				spCategoria.setSelection(orcamentoCorrente.getCategoria());
				edDescricao.setText(orcamentoCorrente.getDescricao());
				edSaldo.setText(""+orcamentoCorrente.getSaldo());
			}
		}
		//Vem da compra, seta categoria de acordo com a compra sem orc defenido
		if(getIntent().getExtras()!=null){
			if(getIntent().getExtras().getString(Util.CHAVE_CATEGORIA) != null){
				    Bundle cofre = getIntent().getExtras();
					int valor1 = Integer.parseInt(cofre.getString(Util.CHAVE_CATEGORIA));
					spCategoria.setSelection(valor1);
		  }
		}
		findViewById(R.id.orcamentoLinearLayoutBtnGuardar).setOnClickListener(v -> {
			//Validae campos
			int categoria;
			String descricao;
			double saldo;
			try {
				if (verificaCampos()) {
					categoria = spCategoria.getSelectedItemPosition();
					descricao = edDescricao.getText().toString();
					saldo = Double.parseDouble(edSaldo.getText().toString());
					ao.znt.econ.modelos.Orcamento orcamento = new ao.znt.econ.modelos.Orcamento(descricao, saldo);
					orcamento.setCategoria(categoria);

					Usuario usuario = Unica.getInstance();
					if (saldoSuficiente(orcamento.getSaldo(), usuario.getRendaMensal())) {
						Usuario usuario1 = usuarioDao.queryForId(1);

							if (existeOrc(orcamento)) {
								//definir Orc
								int result2 = orcamentoDao.create(orcamento);
								if (result2 == 1) {
									usuario = Unica.getInstance();
									double saldoActual = (usuario.getRendaMensal() - orcamento.getSaldo());
									usuario1.setRendaMensal(saldoActual);
									usuario.setRendaMensal(saldoActual);
									int result1 = usuarioDao.update(usuario1);
									if (result1 == 1) {
										mostraAlerta(Util.getCategoria(orcamento.getCategoria())+getString(R.string.orcamento_feito));
										clean();
									}
								} else { }
							} else {mostraAlerta(getString(R.string.para_este_mes_ja_existe_orcamento)+Util.getCategoria(orcamento.getCategoria()));}
					} else { mostraAlerta(getString(R.string.dinheiro_insuficiente)); }
				} else {}
			} catch (SQLException e) {mostraAlerta(getString(R.string.erro_ao_definir_orcamento)); }
		});
		}
	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    	switch (item.getItemId()){
			case android.R.id.home:onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	private boolean verificaCampos(){
		if (edSaldo.getText().toString().isEmpty()) {
			edSaldo.setError(getString(R.string.preencher_este_campo));
			return false;
		} if (spCategoria.getSelectedItem().toString().isEmpty()) {
			spCategoria.setFocusable(true);
			//Nitificação ou Alerta
			return false;
		}
		return true;
	}
	private boolean saldoSuficiente(double saldo,double renda){
    	return saldo < renda;
	}
	private boolean existeOrc(ao.znt.econ.modelos.Orcamento orcamento) throws SQLException {
		for(ao.znt.econ.modelos.Orcamento orc : orcamentoDao.queryForAll()){
			if(Util.getMes(orc.getData()) == Util.getMes(orcamento.getData())&& orc.getCategoria()==orcamento.getCategoria()){
				if(cumpreRequisitosDeEdicao()){
					if(orcamento.getCategoria()==orcamentoCorrente.getCategoria()||existe(orcamento)){
						editarDevolverDinheroNaCarteira();
						return true;
					}
				}
				return false;
			}
		}
    	return true;
	}
	private boolean existe(ao.znt.econ.modelos.Orcamento orcamento) throws SQLException {
		for(ao.znt.econ.modelos.Orcamento orc : orcamentoDao.queryForAll()){
			if(Util.getMes(orc.getData()) == Util.getMes(orcamento.getData())&& orc.getCategoria()==orcamento.getCategoria()){
				return false;
			}
		}
    	return true;
	}
	@Override
	public void onBackPressed() {
		finish();
	}
	private boolean cumpreRequisitosDeEdicao(){
		if(getIntent().getExtras()!=null){
			Bundle cofre = getIntent().getExtras();
			return cofre.getString(Util.CHAVE_ORCAMENTO_ID) != null;
		}
		return false;
	}
	private void editarDevolverDinheroNaCarteira() {
		//caso procigas com a edicao, devolver o dinhero na carteira
		Usuario usuario = Unica.getInstance();
		try {//apaga o orcamento e devolva os valores
			Usuario usuario1 = usuarioDao.queryForId(1);
			usuario1.setRendaMensal(usuario.getRendaMensal() + orcamentoCorrente.getSaldo());
			usuario.setRendaMensal(usuario.getRendaMensal() + orcamentoCorrente.getSaldo());
			int result = orcamentoDao.delete(orcamentoCorrente);
			if (result == 1)
				usuarioDao.update(usuario1);
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}
	}
	private Snackbar mostraAlerta(String ms){
		Snackbar snackbar = Snackbar
				.make(findViewById(R.id.orcamentoLinearLayout),ms, Snackbar.LENGTH_LONG);
		snackbar.show();
		return snackbar;
	}
	private void clean(){
    	edDescricao.setText("");
    	edSaldo.setText("");
	}
}