package ao.znt.econ.dao;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import com.j256.ormlite.support.ConnectionSource;
import android.content.Context;
import com.j256.ormlite.table.TableUtils;
import java.sql.SQLException;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	private static final String DATABASE_NAME ="zntEcon.db";
	private static final int DATABASE_VERSION = 3;
	
	public DatabaseHelper(Context context){
		super(context,DATABASE_NAME,null,DATABASE_VERSION);
	}
	@Override
	public void onCreate(SQLiteDatabase p1, ConnectionSource p2) {
		try {
			TableUtils.createTable(p2, ao.znt.econ.modelos.Compra.class);
			TableUtils.createTable(p2, ao.znt.econ.modelos.Orcamento.class);
			TableUtils.createTable(p2, ao.znt.econ.modelos.Usuario.class);
		} catch (SQLException e) {e.printStackTrace();}
	}

	@Override
	public void onUpgrade(SQLiteDatabase p1, ConnectionSource p2, int p3, int p4) {
		
		try {
			TableUtils.dropTable(p2, ao.znt.econ.modelos.Compra.class,true);
			TableUtils.dropTable(p2, ao.znt.econ.modelos.Orcamento.class,true);
			TableUtils.dropTable(p2, ao.znt.econ.modelos.Usuario.class,true);
			
			onCreate(p1,p2);
		} catch (SQLException e) {e.printStackTrace();}
	}

	@Override
	public void close() {
		super.close();
	}

}
