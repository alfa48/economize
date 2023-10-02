package ao.znt.econ.ui;

import android.content.Intent;
import android.os.Bundle;
import ao.znt.econ.*;

import ao.znt.econ.dao.DatabaseHelper;
import ao.znt.econ.dao.CompraDao;
import java.sql.SQLException;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.support.design.widget.Snackbar;
import android.graphics.Color;

import ao.znt.econ.dao.OrcamentoDao;
import ao.znt.econ.modelos.Orcamento;
import ao.znt.econ.util.Util;

import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Arrays;
import java.util.Objects;

public class Compra extends AppCompatActivity {

	DatabaseHelper helper;
	CompraDao dao;
	OrcamentoDao orcDao;
	private Spinner spCategoria;
	private ao.znt.econ.modelos.Compra compraCorrente;
	private ArrayList<ao.znt.econ.modelos.Orcamento> orcamentos;

	private EditText edDescricao,edPreco,edQuantidade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.compra);

		new Util(this);
		setSupportActionBar(findViewById(R.id.toolbarCompra));
		Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

		helper = new DatabaseHelper(Compra.this);
		try {
			dao = new CompraDao(helper.getConnectionSource());
			orcDao = new OrcamentoDao(helper.getConnectionSource());
			orcamentos = (ArrayList<Orcamento>) orcDao.queryForAll();
		} catch (SQLException e) { mostraA(getString(R.string.Erro_ao_acessar_os_dados)+e.getMessage()); }

		edDescricao = findViewById(R.id.compraEditTextDescricao);
		edPreco = findViewById(R.id.compraEditTextPreco);
		edQuantidade = findViewById(R.id.compraEditTextQuantidade);
		edQuantidade.setText("1");
		spCategoria = findViewById(R.id.compraSpinnerOrcamento);
		ArrayAdapter adapterSpinner = ArrayAdapter.createFromResource(this, R.array.categorias, android.R.layout.simple_list_item_1);
		spCategoria.setAdapter(adapterSpinner);

		//Para editar
		if (paraEditar()){
			Bundle cofre = getIntent().getExtras();
			long id = Long.parseLong(cofre.getString(Util.CHAVE_COMPRA_ID));
			try {
				compraCorrente = dao.queryForId((int) id);
			} catch (SQLException throwables) {
				throwables.printStackTrace();
			}
			spCategoria.setSelection(compraCorrente.getCategoria(),true);
			edDescricao.setText(compraCorrente.getDescricao());
			edQuantidade.setText(""+compraCorrente.getQuantidade());
			edPreco.setText(""+compraCorrente.getPreco());
		}

		findViewById(R.id.compraLinearLayoutBtnGuardar).setOnClickListener(v -> {
			//Validar os campos
			try {
				if (isValidado()) {
					//VALIVALIDAÇÃO
					String descricao = edDescricao.getText().toString();
					int quantidade = Integer.parseInt(edQuantidade.getText().toString());
					double preco = Double.parseDouble(edPreco.getText().toString());
					int categoria = spCategoria.getSelectedItemPosition();

					ao.znt.econ.modelos.Compra c = new ao.znt.econ.modelos.Compra(descricao, preco, quantidade);
					c.setCategoria(categoria);

						if (existe(orcamentos, c.getCategoria())!=null) {
							Orcamento orc = existe(orcamentos, c.getCategoria());
							if(saldoSuficiente(c, orc != null ? orc : new Orcamento())){
								orc.setCompras(Arrays.asList(c));
								//Fazer o debito e atualizar o orc NOTA:se estiver a ser uma edicao, devolve os valores e continua..
								if(paraEditar() && compraCorrente != null) {
									devolve(orc, compraCorrente.getPreco() * compraCorrente.getQuantidade());
									dao.delete(compraCorrente);
									orc.getCompras().remove(compraCorrente);
								}
								debito(orc, c.getPreco() * c.getQuantidade());
								int resultOrc = orcDao.update(orc);
								if (resultOrc == 1) {
									c.setOrc(orc);
									int resultCom = dao.create(c);
									if (resultCom == 1) {
										mostraA(getString(R.string.conprado));
										clean();
									}
								}
							}else{mostraA(getString(R.string.dinheiro_insuficiente));}
						}else{
							Snackbar snackbar = mostraA(getString(R.string.faca_primeiro_um_orcamento_para_a_categoria)+Util.getCategoria(c.getCategoria()));
							snackbar.setActionTextColor(Color.YELLOW)
							.setAction("OK", v1 -> {
								finish();
								//Ir p novo orcamento
								Intent i = new Intent(Compra.this,ao.znt.econ.ui.Orcamento.class);
								i.putExtra(Util.CHAVE_CATEGORIA,""+categoria);
								startActivity(i);
							});
						}
				}
			} catch (SQLException e) { mostraA(getString(R.string.Erro_ao_acessar_os_dados)); }
		});
    }//onCreate
	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	private boolean isValidado() {
		if (edDescricao.getText().toString().isEmpty()) {
			edDescricao.setError(getString(R.string.preencher_com_o_nome_do_produto));
			return false;
		} if (edPreco.getText().toString().isEmpty()) {
			edPreco.setError(getString(R.string.preencher_com_o_preco));
			return false;
		} if (edQuantidade.getText().toString().isEmpty()) {
			edQuantidade.setError(getString(R.string.quantos_itens_compraste));
			return false;
		} else { return true; }
	}
	public void debito(ao.znt.econ.modelos.Orcamento orc, double valor) {
		orc.setSaldo(orc.getSaldo() - valor);
		orc.setSaldoGasto(orc.getSaldoGasto()+valor);
	}
	public void devolve(ao.znt.econ.modelos.Orcamento orc, double valor) {
		orc.setSaldo(orc.getSaldo() + valor);
		orc.setSaldoGasto(orc.getSaldoGasto()-valor);
	}
	private ao.znt.econ.modelos.Orcamento existe(List<ao.znt.econ.modelos.Orcamento> orcs, int categoria) {
		Calendar c = Calendar.getInstance();
		int mes = c.get(Calendar.MONTH);
		int mesActual = mes + 1;

		for (ao.znt.econ.modelos.Orcamento orc: orcs){
			if ((Util.getMes(orc.getData()) == mesActual) && (orc.getCategoria()==categoria)) {
				return orc;
	 		}
		}
		return null;
    }
	private boolean saldoSuficiente(ao.znt.econ.modelos.Compra c, ao.znt.econ.modelos.Orcamento orc) {
		//saldo é suficiente?
		return (c.getPreco() * c.getQuantidade()) <= orc.getSaldo();
	}
	private Snackbar mostraA(String ms){
		Snackbar snackbar = Snackbar
				.make(findViewById(R.id.linear_layout_compra), ms, Snackbar.LENGTH_LONG);
		snackbar.show();
		return snackbar;
	}
	private void clean(){
		edDescricao.setText("");
		edPreco.setText("");
		edQuantidade.setText("1");
	}
	@Override
	public void onBackPressed() { finish(); }
	private boolean paraEditar(){

		if(getIntent().getExtras()!=null){
			return getIntent().getExtras().getString(Util.CHAVE_COMPRA_ID) != null;
		}
    	return false;
	}
}
