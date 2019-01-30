package id.co.collega.ifrs.common;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class JdbcTemplate extends org.springframework.jdbc.core.JdbcTemplate {
	private List<DTOMap> listSql=new ArrayList<DTOMap>();
	
	public JdbcTemplate(DataSource ds) {
		super(ds);
	}
	
	public Object queryObject(String sql, Object[] param, RowMapper mapper){
		try{
			return this.queryForObject(sql,param,mapper);
		} catch(EmptyResultDataAccessException e){
			return null;
		}
	}
	
	@SuppressWarnings("rawtypes")
	public Object queryObject(String sql, Object[] param, Class clazz){
		try{
			return this.queryForObject(sql,param,clazz);
		} catch(EmptyResultDataAccessException e){
			return null;
		}
	}
	
	public Object queryObject(String sql, RowMapper mapper){
		try{
			return this.queryForObject(sql,mapper);
		} catch(EmptyResultDataAccessException e){
			return null;
		}
	}
	
	@SuppressWarnings("rawtypes")
	public Object queryObject(String sql, Class clazz){
		try{
			return this.queryForObject(sql,clazz);
		} catch(EmptyResultDataAccessException e){
			return null;
		}
	}
	
	@SuppressWarnings("rawtypes")
	public List query(String sql, Object[] args, RowMapper rowMapper) throws DataAccessException {
		try{
			return super.query(sql, args, rowMapper);
		} catch(EmptyResultDataAccessException e){
			return new ArrayList();
		}
	}
	
	public List<String> getPrimaryKey(String tblName){
		try{
			Connection conn=this.getDataSource().getConnection();
			List<String> list=new ArrayList<String>();
			ResultSet rs=conn.getMetaData().getPrimaryKeys(null, null, tblName);
			while(rs.next()){
				list.add(rs.getString("COLUMN_NAME"));
			}
			return list;
		} catch (Exception e){
			throw new RuntimeException(e);
		}
	}
	
	public List<String> getNotNullColumns(String tblName){
		try{
			Connection conn=this.getDataSource().getConnection();
			List<String> list=new ArrayList<String>();
			ResultSet rs=conn.getMetaData().getColumns(null, null, tblName, null);
			while(rs.next()){
				if (rs.getInt("NULLABLE")==	DatabaseMetaData.attributeNoNulls){ 
					list.add(rs.getString("COLUMN_NAME"));
				}
			}
			return list;
		} catch (Exception e){
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	public int update(String sql, Object[] args, Boolean isCommit) throws DataAccessException, SQLException {
		Integer retVal = super.update(sql, args);
		if(isCommit)
			commit();
		return retVal;
	}
	
	public DTOMap insertDataQuery(DTOMap data, String tblName) {
		List<Object> param = new ArrayList<Object>();
		String sql = "Insert Into " + tblName + " (";
		for (String key : data.map.keySet()) {
			if(data.get(key) != null){
				sql += key + ",";
				param.add(data.get(key));
			}
		}
		sql = sql.substring(0, sql.length() - 1) + ") Values(";
		for (int i = 0; i < param.size(); i++) {
			sql += "?,";
		}
		sql = sql.substring(0, sql.length() - 1) + ")";
		Object[] paramO = param.toArray(new Object[param.size()]);
		
		DTOMap result=new DTOMap();
		result.put("QUERY", sql);
		result.put("PARAM", paramO);
		return result;
	}
	
	public DTOMap updateDataQuery(DTOMap dtoMap, String tblName) {
		List<String> whereCol=getPrimaryKey(tblName);
		if (whereCol.size()==0){
			whereCol=Arrays.asList(dtoMap.getString("PK").split(","));
		}
		dtoMap.map.remove("PK");
		List<Object> param=new ArrayList<Object>();
		String sql="Update "+tblName+" Set ";
		for (String key : dtoMap.map.keySet()) {
			if (!whereCol.contains(key)){
				if(dtoMap.get(key) != null){
					sql+=key+"=?,";
					param.add(dtoMap.get(key));
				}
			}
		}
		sql=sql.substring(0, sql.length()-1)+" Where ";
		
		for (String wc : whereCol) {
			sql+=wc+"=? and ";
			param.add(dtoMap.get(wc));
		}
		sql=sql.substring(0, sql.length()-4);
		Object[] paramO=param.toArray(new Object[param.size()]);
		
		DTOMap result=new DTOMap();
		result.put("QUERY", sql);
		result.put("PARAM", paramO);
		return result;
	}
	
	public DTOMap deleteDataQuery(DTOMap dtoMap, String tblName){
		List<String> whereCol=getPrimaryKey(tblName);
		if (whereCol.size()==0){
			whereCol=Arrays.asList(dtoMap.getString("PK").split(","));
		}
		dtoMap.map.remove("PK");
		List<Object> param=new ArrayList<Object>();
		String sql="Delete "+tblName+" Where ";
		for (String wc : whereCol) {
			if (dtoMap.get(wc)!=null){
				sql+=wc+"=? and ";
				param.add(dtoMap.get(wc));
			}
		}
		sql=sql.substring(0, sql.length()-4);
		Object[] paramO=param.toArray(new Object[param.size()]);
		
		DTOMap result=new DTOMap();
		result.put("QUERY", sql);
		result.put("PARAM", paramO);
		return result;
	}
	
	public DTOMap addQuery(String sql, Object[] param){
		DTOMap map=new DTOMap();
		map.put("QUERY",sql);
		map.put("PARAM",param);
		return map;
	}
	
	public void addSql(DTOMap query){
		this.listSql.add(query);
	}
	
	public void addSql(String sql, Object[] param){
		DTOMap map=new DTOMap();
		map.put("QUERY",sql);
		map.put("PARAM",param);
		this.listSql.add(map);
	}
	
	public void resetListSql(){
		listSql.clear();
	}
	
	public void executes(){
		executes(listSql);
	}
	
	public void executes(List<DTOMap> listSql){
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		PlatformTransactionManager transactionManager=new DataSourceTransactionManager(this.getDataSource());
		TransactionStatus status = transactionManager.getTransaction(def);
		
		try {
			for (DTOMap sql : listSql) {
				String query=sql.getString("QUERY");
				Object[] param=(Object[])sql.get("PARAM");
				if (param!=null)this.update(query, param);
				else this.update(query);
			}
		} catch(Exception e){
			transactionManager.rollback(status);
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		transactionManager.commit(status);
	}
	
	public void commit() throws SQLException{
		super.getDataSource().getConnection().commit();
	}
	
	@SuppressWarnings("unchecked")
	public List<DTOMap> getDataMaster(String table) {
		return this.query("SELECT * FROM " + table, new DTOMap());
	}
	
	public int updateData(DTOMap dtoMap, String tblName) {
		List<String> whereCol = Arrays.asList(dtoMap.getString("PK").split(","));
		dtoMap.map.remove("PK");
		List<Object> object = new ArrayList<Object>();
		String sql = "UPDATE "+tblName+" SET ";
		for (String key : dtoMap.map.keySet()) {
			if (!whereCol.contains(key)){
				sql += key + " = ?,";
				object.add(dtoMap.get(key));
			}
		}
		sql = sql.substring(0, sql.length()-1) + " WHERE ";
		
		for (String wc : whereCol) {
			sql += wc + " = ? AND ";
			object.add(dtoMap.get(wc.trim()));
		}
		sql=sql.substring(0, sql.length()-4);
		Object[] param = object.toArray(new Object[object.size()]);
		return this.update(sql,param);
	}
	
	public int insertData(DTOMap dtoMap, String tblName) {
		List<Object> object = new ArrayList<Object>();
		String sql = "INSERT INTO " + tblName + " (";
		for (String key : dtoMap.map.keySet()) {
			if(dtoMap.get(key)!=null){
				sql += key + ",";
				object.add(dtoMap.get(key));
			}
		}
		sql = sql.substring(0, sql.length() - 1) + ") VALUES(";
		for (int i = 0; i < object.size(); i++) {
			sql += "?,";
		}
		sql = sql.substring(0, sql.length() - 1) + ")";

		Object[] param = object.toArray(new Object[object.size()]);
		return this.update(sql, param);
	}
	
	public int deleteData(DTOMap dtoMap, String tblName){
		List<String> whereCol = Arrays.asList(dtoMap.getString("PK").split(","));
		dtoMap.map.remove("PK");
		List<Object> object = new ArrayList<Object>();
		String sql = "DELETE FROM "+tblName+" WHERE ";
		for (String wc : whereCol) {
			if (dtoMap.get(wc.trim())!=null){
				sql += wc + " = ? AND ";
				object.add(dtoMap.get(wc.trim()));
			}
		}
		sql = sql.substring(0, sql.length()-4);
		Object[] param = object.toArray(new Object[object.size()]);
		return this.update(sql,param);
	}

	@SuppressWarnings("unchecked")
	public List<DTOMap> getDataMasterById(DTOMap dtoMap, String tblName) {
		List<Object> object = new ArrayList<Object>();
		String sql = "SELECT * FROM "+tblName+" WHERE ";
		for (String wc : dtoMap.map.keySet()) {
			if (dtoMap.get(wc)!=null){
				sql += wc + " = ? AND ";
				object.add(dtoMap.get(wc));
			}
		}
		sql = sql.substring(0, sql.length()-4);
		Object[] param = object.toArray(new Object[object.size()]);
		return this.query(sql,param, new DTOMap());
	}

	@SuppressWarnings("unchecked")
	public List<DTOMap> getDataMasterLikeById(DTOMap dtoMap, String tblName) {
		String sql = "SELECT * FROM "+tblName+" WHERE ";
		for (String wc : dtoMap.map.keySet()) {
			if (dtoMap.get(wc)!=null){
				sql += "UPPER("+wc+")" + " LIKE '%"+dtoMap.get(wc).toString().toUpperCase()+"%' AND ";
			}
		}
		sql = sql.substring(0, sql.length()-4);
		return this.query(sql, new DTOMap());
	}

	public boolean isExist(DTOMap dtoMap, String table) {
		List<Object> object = new ArrayList<Object>();
		String sql = "SELECT COUNT(1) FROM "+table+" WHERE ";
		for (String wc : dtoMap.map.keySet()) {
			if (dtoMap.get(wc)!=null){
				sql += wc + " = ? AND ";
				object.add(dtoMap.get(wc));
			}
		}
		sql = sql.substring(0, sql.length()-4);
		Object[] param = object.toArray(new Object[object.size()]);
		return this.queryForObject(sql,param, Integer.class) > 0 ? true : false;
	}

	@SuppressWarnings("unchecked")
	public List<DTOMap> getDataMasterOrderById(String orderId, String tblName, Boolean isAsc) {
		return this.query("SELECT * FROM "+tblName+" ORDER BY " + orderId + (isAsc ? " ASC" : " DESC"), new DTOMap());
	}

	public DTOMap getMapMasterById(DTOMap dtoMap, String tblName) {
		try {
			List<Object> object = new ArrayList<Object>();
			String sql = "SELECT * FROM "+tblName+" WHERE ";
			for (String wc : dtoMap.map.keySet()) {
				if (dtoMap.get(wc)!=null){
					sql += wc + " = ? AND ";
					object.add(dtoMap.get(wc));
				}
			}
			sql = sql.substring(0, sql.length()-4);
			Object[] param = object.toArray(new Object[object.size()]);
			return (DTOMap)this.queryObject(sql, param, new DTOMap());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<DTOMap> getMenuItemsByRole(String role) {
        String sql = "SELECT A.MENU_ID, A.NAME,A.FORM_ID,A.MENU_PARENT, CASE WHEN A.MENU_ID = B.MENU_ID THEN '1' ELSE '0' END AS PICK, SEQ " +
			" FROM CFG_MENU A LEFT JOIN CFG_ROLE_MENU B ON B.MENU_ID = A.MENU_ID  AND B.ROLE_ID = ? ORDER BY MENU_ID";
		return this.query(sql, new Object[]{role}, new DTOMap());
	}

	@SuppressWarnings("unchecked")
	public List<DTOMap> getDataMaster(String sql, Object[] param) {
		return this.query(sql, param, new DTOMap());
	}

	public DTOMap getMapMaster(String sql, Object[] param) {
		try {
			return (DTOMap)this.queryObject(sql, param, new DTOMap());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public DTOMap getParameter(Integer grup){
		List<DTOMap> listSysParm = query("SELECT * FROM CFG_PARM WHERE PARMGRP = ? ", new Object[]{grup}, new DTOMap());
		DTOMap dtoSysParm = new DTOMap();
		for (DTOMap dtoResult : listSysParm) {
			dtoSysParm.put(dtoResult.getString("PARMNM").toUpperCase(), dtoResult.getString("VALUE"));
		}
		return dtoSysParm;
	}
}
