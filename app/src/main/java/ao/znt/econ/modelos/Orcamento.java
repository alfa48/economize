package ao.znt.econ.modelos;
import android.content.res.Resources;

import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.DataType;
import java.util.Date;
import java.util.Calendar;
import com.j256.ormlite.field.ForeignCollectionField;
import java.util.Collection;

import ao.znt.econ.util.Util;


@DatabaseTable(tableName="orcamento")
public class Orcamento {
	@DatabaseField(generatedId=true)
	private long id;
	@DatabaseField
    public String descricao;
	@DatabaseField
    public int categoria;
	@DatabaseField
    public double saldo;
	@DatabaseField
	public double saldoGasto;
    @DatabaseField(dataType = DataType.DATE_LONG)
	private Date data;
	@ForeignCollectionField
	private Collection<ao.znt.econ.modelos.Compra> compras;

	public Orcamento(){}
    public Orcamento(String descricao, double saldo) {
        this.descricao = descricao;
        this.saldo = saldo;
		this.data = Calendar.getInstance().getTime();
    }

	public void setCategoria(int categoria) {
		this.categoria = categoria;
	}

	public int getCategoria() {
		return categoria;
	}

	public void setCompras(Collection<ao.znt.econ.modelos.Compra> compras) {
		this.compras = compras;
	}

	public Collection<ao.znt.econ.modelos.Compra> getCompras() {
		return compras;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public Date getData() {
		return data;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setSaldo(double saldo) {
		this.saldo = saldo;
	}

	public double getSaldo() {
		return saldo;
	}

	public void setSaldoGasto(double saldoGasto) {
		this.saldoGasto = saldoGasto;
	}
	public double getSaldoGasto() {
		return saldoGasto;
	}
}
