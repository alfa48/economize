package ao.znt.econ.dao;
import com.j256.ormlite.dao.BaseDaoImpl;
import java.sql.SQLException;
import com.j256.ormlite.support.ConnectionSource;
import java.util.Spliterator;

public class CompraDao extends BaseDaoImpl<ao.znt.econ.modelos.Compra, Integer> {


	@Override
	public Spliterator<ao.znt.econ.modelos.Compra> spliterator() {
		return null;
	}



    public CompraDao(ConnectionSource cs) throws SQLException{
		super(ao.znt.econ.modelos.Compra.class);
		setConnectionSource(cs);
		initialize();
	}
}
