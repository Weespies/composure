package uk.lug.dao.handlers;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.lug.dao.records.IdProvider;
import uk.lug.dao.records.PersonRecord;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public abstract class AbstractDao<T extends IdProvider, I extends Object> {
	protected Dao<T, I> dao;

	public void init() throws SQLException {
		ConnectionSource connection = HypersonicConnection.getConnection();
		dao = DaoManager.createDao(connection, getBeanClass());
		TableUtils.createTableIfNotExists(connection, getBeanClass());
	}


	public void delete(T bean) throws SQLException {
		dao.delete(bean);
	}

	public void delete(T[] beans) throws SQLException {
		dao.delete(Arrays.asList(beans));
	}
	
	
	public T save(T bean) throws SQLException {
		if (bean.getId() == null) {
			dao.create(bean);
		} else {
			dao.update(bean);
		}
		return bean;
	}
	
	public T getForId(Long id) throws SQLException {
		Map<String,Object> fields = new HashMap<String,Object>();
		fields.put("ID",id);
		List<T> ret = dao.queryForFieldValues(fields);
		if ( ret.isEmpty() ){
			throw new SQLException("No results for id "+id);
		}
		return ret.get(0);
	}

	protected abstract Class<T> getBeanClass() ;

	public AbstractDao() {
		super();
	}
	
	public List<T> readAll() throws SQLException {
		return dao.queryForAll();
	}


}