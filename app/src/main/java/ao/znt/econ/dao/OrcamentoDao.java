package ao.znt.econ.dao;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import java.sql.SQLException;
import java.util.Spliterator;

public class OrcamentoDao extends BaseDaoImpl<ao.znt.econ.modelos.Orcamento, Integer> {

	@Override
	public Spliterator<ao.znt.econ.modelos.Orcamento> spliterator() {
		return null;
	}

    
    
    public OrcamentoDao(ConnectionSource cs) throws SQLException{
		super(ao.znt.econ.modelos.Orcamento.class);
		setConnectionSource(cs);
		initialize();
	}
}
