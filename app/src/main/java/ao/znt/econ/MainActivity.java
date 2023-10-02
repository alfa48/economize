package ao.znt.econ;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;

import ao.znt.econ.adapters.RecicleViewsAdapter;
import ao.znt.econ.dao.DatabaseHelper;
import ao.znt.econ.dao.OrcamentoDao;
import ao.znt.econ.dao.UsuarioDao;
import ao.znt.econ.inicio.CadastroActivity;
import ao.znt.econ.modelos.Orcamento;
import ao.znt.econ.modelos.Usuario;
import ao.znt.econ.ui.CompraList;
import ao.znt.econ.ui.Estatistica;
import ao.znt.econ.ui.OrcList;
import ao.znt.econ.util.Unica;
import ao.znt.econ.util.Util;
import ss.com.bannerslider.Slider;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

	RecicleViewsAdapter adapter;
    RecyclerView recyclerView; 
    Context context; 
    public ArrayList<Orcamento> orcamentos;
    private TextView txNomeCompleto,txSaldo,txMoeda;
	private OrcamentoDao OrcDao;
	private UsuarioDao usuarioDao;
	private Usuario usuario;
	private NavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Util(this);
		usuario = Unica.getInstance();
		DatabaseHelper helper = new DatabaseHelper(MainActivity.this);
		try{
			usuarioDao = new UsuarioDao(helper.getConnectionSource());
			OrcDao = new OrcamentoDao(helper.getConnectionSource());
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			helper.close();
		}

		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		//Banner
		Slider.init(new BunnerSlier.PicassoImageservice(this));
		Slider slider = findViewById(R.id.banner);
		slider.setAdapter(new BunnerSlier.MainSliderAdapter());

		recyclerView =  findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
       // this.registerForContextMenu(recyclerView);
        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
				this,
				drawer,toolbar,
				R.string.navigation_drawer_open,
				R.string.navigation_drawer_close);

        //drawer.setDrawerListener(toggle);
        toggle.syncState();

		navigationView = findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);

		initViewsGaveta();

		LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

        initializeData();
		adapter = new RecicleViewsAdapter(orcamentos);
		initializeAdapter();

		registerForContextMenu(recyclerView);

		findViewById(R.id.activity_mainRelativeLayoutBtn1).setOnClickListener(view ->
			startActivity(new Intent(this,ao.znt.econ.ui.Compra.class)));

	    findViewById(R.id.activity_mainRelativeLayoutBtn).setOnClickListener(view ->
			startActivity(new Intent(this,ao.znt.econ.ui.Orcamento.class)));
		}
		@SuppressLint("NonConstantResourceId")
		@Override
		public boolean onNavigationItemSelected(MenuItem item) {
			    // Handle navigation view item clicks here.
			    switch (item.getItemId()){
					case R.id.nav_orc_list:
						startActivity(new Intent(this, OrcList.class));
						break;
					case R.id.nav_compra_list:
						startActivity(new Intent(this, CompraList.class));
						break;
					case R.id.nav_chart_page:
						startActivity(new Intent(this, Estatistica.class));
						break;
					case R.id.nav_sobre: alertDialogInf();
					    break;
					case R.id.nav_plus_saldo:alertDialogPlusSaldo();
						break;
					case R.id.nav_edit:editarDasos();
						break;
					case R.id.nav_pulitica_privacidade: irParaPoliticaDePrivacidade();
				}
		    DrawerLayout drawer = findViewById(R.id.drawer_layout);
			drawer.closeDrawer(GravityCompat.START); 
			return true; 
	}
	private void initViewsGaveta(){
		txNomeCompleto = navigationView.getHeaderView(0).findViewById(R.id.nomeCompleto);
		txSaldo = navigationView.getHeaderView(0).findViewById(R.id.saldoMensal);
		txMoeda = navigationView.getHeaderView(0).findViewById(R.id.header_moeda);
		initDadosGaveta();
	}
	private void initDadosGaveta(){
		txNomeCompleto.setText(usuario.getNome());
		txSaldo.setText(""+usuario.getRendaMensal());
		txMoeda.setText(Util.MOEDA(usuario.getMoeda()));
	}
	private  void irParaPoliticaDePrivacidade(){
    	Intent intent = new Intent(this,PoliticaDePrivacidadeActivity.class);
    	startActivity(intent);
	}
	private void editarDasos() {
    	Intent intent = new Intent(this, CadastroActivity.class);
    	intent.putExtra(Util.CHAVE_TELA,Util.VALOR_TELA_MAIN);
		startActivity(intent);
		//finish();
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
	private void alertDialogInf() {
		LayoutInflater li = LayoutInflater.from(this);
		View promptsView = li.inflate(R.layout.alert_dialog_inf, null);
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setCancelable(false);
		alertDialogBuilder.setNegativeButton("Ok",(d,w)-> d.dismiss());
		alertDialogBuilder.setView(promptsView);
        alertDialogBuilder.create().show();
	}
	private void alertDialogPlusSaldo() {
		LayoutInflater li = LayoutInflater.from(this);
		View promptsView = li.inflate(R.layout.alert_dialog_plus, null);
		EditText editText = promptsView.findViewById(R.id.alert_dialog_plusSaldo);
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setView(promptsView);
		alertDialogBuilder.setCancelable(false);
		alertDialogBuilder.setNegativeButton(R.string.cancelar,(dialog, which) -> dialog.dismiss());
		alertDialogBuilder.setPositiveButton(R.string.adicionar,(dialog, whith)->{
			if(!editText.getText().toString().isEmpty()){
				double plussaldo = Double.parseDouble(editText.getText().toString());
				adicionarSaldo(plussaldo);
				onResume();
				//dialog.dismiss();
			}
		});
		alertDialogBuilder.create().show();
	}
	@Override
	public void onBackPressed() {
    	Snackbar snackbar = mostraAlerta(getString( R.string.sair_da_app))
				.setAction("OK", v -> finish());
			snackbar.setActionTextColor(Color.YELLOW);
			//snackbar.show();

	}
	private void adicionarSaldo(double saldoplus){
    	usuario.setRendaMensal(usuario.getRendaMensal()+saldoplus);
		try {
			usuarioDao.update(usuario);
			mostraAlerta(getString(R.string.dinheiro_add));
		} catch (SQLException throwables) {
			mostraAlerta(getString(R.string.err_add_dinher));
			//throwables.printStackTrace();
		}
	}
	private Snackbar mostraAlerta(String ms){
		Snackbar snackbar = Snackbar
				.make(findViewById(R.id.drawer_layout),ms, Snackbar.LENGTH_LONG);
		snackbar.show();
		return snackbar;
	}
	private void initializeAdapter(){
    	adapter.updateList(orcamentos);
		recyclerView.setAdapter(adapter);
	}
	private void initializeData(){
		try {
			orcamentos = (ArrayList<Orcamento>) OrcDao.queryForAll();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	@Override
	protected void onResume() {
		super.onResume();
		//usar o bundle para saber se as telas que retornaram será necessário
		// recarregar os dados oudeixar como está,
		// if(bundle != null) inicializa e inicializaAdapter e outros
		initializeData();
		initializeAdapter();
		//Home
		LinearLayout liin = findViewById(R.id.layout_inicial_invisible);
		if(adapter.getItemCount()==0){//se a lista de orcamentos for vasia
			liin.setVisibility(View.VISIBLE);//torna visivel um layout que contem um btn e um texto
			recyclerView.setVisibility(View.GONE);//oculta a lista
		}else{
			recyclerView.setVisibility(View.VISIBLE);
			liin.setVisibility(View.GONE);
		}
		initDadosGaveta();
	}
	public void primeiroOrcamento(View view){
    	startActivity(new Intent(MainActivity.this, ao.znt.econ.ui.Orcamento.class));
    }
	@Override
	public boolean onContextItemSelected(MenuItem item) {
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
				int result = OrcDao.delete(orc);
				if(result==1){
					mostraAlerta(getString(R.string.orc_deletado));
					usuario.setRendaMensal(usuario.getRendaMensal()+saldoReposto);
					usuarioDao.update(usuario);
					orcamentos.remove(orc);
					onResume();
				}
			}
		} catch (SQLException throwables) { throwables.printStackTrace(); }
	}
	private void listarComprasDo(ao.znt.econ.modelos.Orcamento orcamento){
        Intent intent = new Intent(this,ao.znt.econ.ui.CompraList.class);
        intent.putExtra(Util.CHAVE_ORCAMENTO_LISTAR_COMPRAS,Util.VALOR_ORCAMENTO_LISTAR_COMPRAS);
        intent.putExtra(Util.CHAVE_ORCAMENTO_ID,""+orcamento.getId());
        startActivity(intent);
    }
}