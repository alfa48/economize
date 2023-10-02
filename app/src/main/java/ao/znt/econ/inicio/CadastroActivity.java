package ao.znt.econ.inicio;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Objects;

import ao.znt.econ.R;
import ao.znt.econ.dao.DatabaseHelper;
import ao.znt.econ.dao.UsuarioDao;
import ao.znt.econ.modelos.Usuario;
import ao.znt.econ.util.Unica;
import ao.znt.econ.util.Util;

public class CadastroActivity extends AppCompatActivity {

    private String telaPrev;
    private TextView textCadastro;
    private EditText edNome,edPass,edRPass,edRendaMensal;
    private Spinner spinnermoeda;
    private LinearLayout llyrenda;
    private UsuarioDao usDao;
    private String nome,senha;
    private int moeda;
    private double renda;
    private DatabaseHelper helper;
    private Usuario usuario;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        setSupportActionBar(findViewById(R.id.toolbarCadastro));
        new Util(this);
        helper = new DatabaseHelper(CadastroActivity.this);
        try {
            usDao = new UsuarioDao(helper.getConnectionSource());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        inicializaElementos();

        if(getIntent().getExtras()!=null){
            //por a seta home
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

            Bundle cofre = getIntent().getExtras();
            telaPrev = cofre.getString(Util.CHAVE_TELA);
            llyrenda.setVisibility(View.GONE);
            edRendaMensal.setText("1");//este campo é oculto quando é editado mas precisa conter algo para passar pela verivicacao
            textCadastro.setText(R.string.editar);
        }

        if(telaPrev!=null)//Só quando estiver a ser editado
             initEdit();
        findViewById(R.id.cadastro_layoutRelativeLayoutBtn).setOnClickListener(v -> {
            if(camposPenchido(edNome)&&camposPenchidoSenha(edPass)&&camposPenchido(edRPass)&&camposPenchido(edRendaMensal)){
                if(edPass.getText().toString().equals(edRPass.getText().toString())){
                    //Validar nome depois continua..
                    nome = edNome.getText().toString();
                    renda = Double.parseDouble(edRendaMensal.getText().toString());
                    senha = edPass.getText().toString();
                    moeda = Util.getMoeda(spinnermoeda.getSelectedItem().toString());

                    if(telaPrev!=null&&telaPrev.equals(Util.VALOR_TELA_MAIN))
                        alertDialog();
                    else
                    crisrUsuario(nome,senha,renda,moeda);

                }else{ Snackbar snackbar = Snackbar.make(findViewById(R.id.scrowView_cadastro), R.string.senhas_inconpativeis, Snackbar.LENGTH_LONG);
                    snackbar.show();}
            }

        });
    }
    private void initUsuario(String nome,String senha) {
        try {
            usuario = Unica.getInstance();
            usuario.setNome(nome);
            usuario.setSenha(Util.cripto(senha));
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    private void crisrUsuario(String nome,String senha,double renda,int moeda) {
        initUsuario(nome, senha);
        usuario.setRendaMensal(renda);
        usuario.setMoeda(moeda);
        try {
           int result = usDao.create(usuario);
           if(result == 1){
               Snackbar snackbar = Snackbar.make(findViewById(R.id.scrowView_cadastro), R.string.usuario_criado, Snackbar.LENGTH_LONG);
               snackbar.show();
               mudarParaTelaLogin();
           }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    private boolean editUsuario(String nome,String senha,int moeda) {
        try {
            Usuario usuario1 = usDao.queryForId(1);
            usuario1.setSenha(senha);
            usuario1.setNome(nome);
            usuario1.setMoeda(moeda);
            //usuario1.setRendaMensal(renda);
            initUsuario(nome,senha);
            usuario.setId(usuario1.getId());
            int result = usDao.update(usuario);
            if(result == 1)
                return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            helper.close();
        }
        return false;
    }
    private void mudarParaTelaLogin(){
        helper.close();
        startActivity(new Intent(CadastroActivity.this,Login.class));
        finish();
    }
    private void inicializaElementos() {
        edPass = findViewById(R.id.cadastro_layoutEditTextPassWord);
        edNome = findViewById(R.id.cadastro_layoutEditTextNome);
        edRPass = findViewById(R.id.cadastro_layoutEditTextRepitaPassWord);
        textCadastro = findViewById(R.id.textoCadastro);
        edRendaMensal = findViewById(R.id.cadastro_layoutEditTextValorDeRendaMensal);
        llyrenda = findViewById(R.id.Cadastro_LinearlayoutDaRenda);
        spinnermoeda = findViewById(R.id.cadastro_spinner_moeda);

        ArrayAdapter adapter = ArrayAdapter.createFromResource(
                this,
                R.array.moedas,
                android.R.layout.simple_spinner_item);
        //alterar a fonte de dados(adapter) do Spinner
        spinnermoeda.setAdapter(adapter);

    }
    private boolean camposPenchido(EditText ed){
        if(!ed.getText().toString().isEmpty()){
            return true;
        }
        alertaCampovasio(ed);
        return false;
    }
    private boolean camposPenchidoSenha(EditText edSenha){
        if(!edSenha.getText().toString().isEmpty()){
            if(!Util.validaSenha(edSenha.getText().toString())) {
                edSenha.setError(getString(R.string.limite_digitos));
                return false;
            }
            return true;
        }
        alertaCampovasio(edSenha);
        return false;
    }

    private void alertaCampovasio(EditText ed){
        ed.setError(getString(R.string.campo_obrigatorio));
    }
    private void initEdit(){
        edNome.setHint(R.string.altera_user);
        edRPass.setHint(getString(R.string.confirma_senha));
        edPass.setHint(getString(R.string.nova_senha));
        //edNome.setHint("altera nome");
    }
    private void alertDialog() {
        Snackbar snackbar = Snackbar
                .make(findViewById(R.id.scrowView_cadastro), R.string.quer_alter_dados, Snackbar.LENGTH_LONG)
                .setAction("OK", v1 -> {
                    boolean result = editUsuario(nome,senha,moeda);
                    if(result){
                        Snackbar snackbar1 = Snackbar.make(v1, R.string.dados_alter_sucesso, Snackbar.LENGTH_LONG);
                        snackbar1.show();
                        new Handler().postDelayed(this::finish, 1000);
                    }
                });
        snackbar.setActionTextColor(Color.YELLOW);
        snackbar.show();
    }
}