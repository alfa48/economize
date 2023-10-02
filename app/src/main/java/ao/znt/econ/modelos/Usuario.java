package ao.znt.econ.modelos;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="usuario")
public class Usuario {
    @DatabaseField(generatedId=true)
    private long id;
    @DatabaseField
    private String senha;
    @DatabaseField
    private String nome;
    @DatabaseField
    private double rendaMensal;
    @DatabaseField
    private int moeda;

    public Usuario(){}
    public Usuario(String nome,String senha){
        this.nome=nome;
        this.senha = senha;
    }
    public double getRendaMensal() {
        return rendaMensal;
    }
    public void setRendaMensal(double rendaMensal) {
        this.rendaMensal = rendaMensal;
    }
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSenha() {
        return senha;
    }

    public String getNome() {
        return nome;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setMoeda(int moeda) { this.moeda = moeda; }

    public int getMoeda() { return moeda; }
}
