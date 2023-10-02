package ao.znt.econ.modelos;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.util.Date;
import com.j256.ormlite.field.DataType;
import java.util.Calendar;

@DatabaseTable(tableName="compra")
public class Compra {
	@DatabaseField(generatedId=true)
	private long id;
	@DatabaseField
    public String descricao;
	@DatabaseField
    public int categoria;
	@DatabaseField
	public double preco;
	@DatabaseField
	public int quantidade;
    @DatabaseField(dataType = DataType.DATE_LONG)
	private Date dataDaCompra;
	@DatabaseField(foreign=true)
	private ao.znt.econ.modelos.Orcamento orc;

	public Compra(){}
	public Compra(String descricao, double preco, int quantidade) {
		this.descricao = descricao;
		this.preco = preco;
		this.quantidade = quantidade;
		this.dataDaCompra = Calendar.getInstance().getTime();
	}

	public void setCategoria(int categoria) {
		this.categoria = categoria;
	}

	public int getCategoria() {
		return categoria;
	}

	public void setOrc(ao.znt.econ.modelos.Orcamento orc) {
		this.orc = orc;
	}

	public ao.znt.econ.modelos.Orcamento getOrc() {
		return orc;
	}
public void setDataDaCompra(Date dataDaCompra) {
		this.dataDaCompra = dataDaCompra;
	}

	public Date getDataDaCompra() {
		return dataDaCompra;
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

	public void setPreco(double preco) {
		this.preco = preco;
	}

	public double getPreco() {
		return preco;
	}

	public void setQuantidade(int quantidade) {
		this.quantidade = quantidade;
	}

	public int getQuantidade() {
		return quantidade;
	}
}
