package ao.znt.econ.inicio;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.widget.CheckBox;
import android.widget.EditText;
import ao.znt.econ.R;
import android.widget.Toast;
import android.content.Intent;
import ao.znt.econ.MainActivity;
import ao.znt.econ.dao.DatabaseHelper;
import ao.znt.econ.dao.UsuarioDao;
import ao.znt.econ.modelos.Usuario;
import ao.znt.econ.util.Unica;
import ao.znt.econ.util.Util;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public class Login extends Activity {
    private SharedPreferences shar;
    private CheckBox checkBox;
    private Usuario usuarioAux,usuario;
    EditText edUsuario, edSenha;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        checkBox = findViewById(R.id.checkboxLembrarDaSenha);
        edUsuario = findViewById(R.id.login_layoutEditTextEmail);
        edSenha = findViewById(R.id.login_layoutEditTextPassWord);


        try (DatabaseHelper helper = new DatabaseHelper(Login.this)) {
            UsuarioDao usDao = new UsuarioDao(helper.getConnectionSource());
            usuarioAux = usDao.queryForId(1);
        } catch (SQLException e) {
            mostra(getString(R.string.Erro_ao_acessar_os_dados)+e.getMessage());
        }

        shar = getSharedPreferences("zntshare", Context.MODE_PRIVATE);
        if (!(shar.getString("usuario", "n encontrado").equals("n encontrado"))) {
            //nenhum dado encontrado
            String nome = shar.getString("usuario", "n encontrado");
            String senha = shar.getString("senha", "n encontrado");
            boolean checked = shar.getBoolean("checked", false);

            try {
                if(usuarioAux.getNome().equals(nome) && usuarioAux.getSenha().equals(Util.cripto(senha))){
                    edUsuario.setText(nome);
                    edSenha.setText(senha);
                    checkBox.setChecked(checked);
                }else if (checked){
                    edUsuario.setText(usuarioAux.getNome());
                    //edSenha.setText(senha);
                    checkBox.setChecked(true);
                }
            } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
                mostra(getString(R.string.Erro_ao_acessar_os_dados)+e.getMessage());
            }
        }

        usuario = Unica.getInstance();
        if(usuarioAux!=null){
            //textCadastroUpgrate.setText("editar dados");
            usuario.setNome(usuarioAux.getNome());
            usuario.setNome(usuarioAux.getNome());
            usuario.setSenha(usuarioAux.getSenha());
            usuario.setRendaMensal(usuarioAux.getRendaMensal());
            usuario.setMoeda(usuarioAux.getMoeda());
        }else {
            Toast.makeText(getBaseContext(), R.string.cadastra_se_primeiro,Toast.LENGTH_SHORT).show();
            //ir para tela de cadastr
            irParaCadastro();
        }
        findViewById(R.id.login_layoutRelativeLayoutBtn).setOnClickListener(p1 -> {
            String nome = edUsuario.getText().toString();
            String senha = edSenha.getText().toString();

            try {
                if(valida(nome,senha)){
                    Toast.makeText(getApplication(), R.string.usuario_logado, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Login.this, MainActivity.class));

                    SharedPreferences.Editor ed = shar.edit();
                    if(checkBox.isChecked()){
                        ed.putString("usuario", nome);
                        ed.putString("senha", senha);
                        ed.putBoolean("checked",true);

                    }else {
                        ed.remove("usuario");
                        ed.remove("senha");
                        ed.remove("checked");
                    }
                    ed.apply();
                    finish();
                }else{
                    if(nome.isEmpty())
                    edUsuario.setError(getString(R.string.seu_nome));
                    else if(senha.isEmpty())
                    edSenha.setError(getString(R.string.digite_a_senha));
                }
            } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
                mostra(getString(R.string.Erro_ao_acessar_os_dados)+e.getMessage());
            }
        });
    }
    public boolean valida(String nome,String senha) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        if(nome.isEmpty() || senha.isEmpty())
            return false;//verificar as credencias
        else if (nome.equals(usuario.getNome())&& Util.cripto(senha).equals(usuario.getSenha()))
            return true;
        else
            Toast.makeText(getBaseContext(), R.string.Dados_INCORRETO,Toast.LENGTH_SHORT).show();
            return false;
    }
    private void irParaCadastro(){
        startActivity(new Intent(Login.this,CadastroActivity.class));
        finish();
    }
    @Override
    public void onBackPressed() {
        Snackbar snackbar = Snackbar
                .make(findViewById(R.id.scrowView_login), R.string.sair_do_economize, Snackbar.LENGTH_LONG)
                .setAction("OK", v -> finish());
        snackbar.setActionTextColor(Color.YELLOW);
        snackbar.show();
    }
    private void mostra(String ms){
        Toast.makeText(getBaseContext(),ms,Toast.LENGTH_SHORT).show();
    }
}