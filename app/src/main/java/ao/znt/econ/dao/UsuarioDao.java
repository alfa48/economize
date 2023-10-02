package ao.znt.econ.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.Spliterator;
import ao.znt.econ.modelos.Usuario;

public class UsuarioDao extends BaseDaoImpl<Usuario, Integer> {

    @Override
    public Spliterator<Usuario> spliterator() {
        return null;
    }

    public UsuarioDao(ConnectionSource cs) throws SQLException {
        super(ao.znt.econ.modelos.Usuario.class);
        setConnectionSource(cs);
        initialize();
    }
}
