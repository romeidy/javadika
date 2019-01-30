package id.co.collega.ifrs.master.service.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import id.co.collega.ifrs.common.DTOMap;
import id.co.collega.ifrs.master.service.MasterServices;

@Service
public class MasterServicesImpl implements MasterServices{
	@Autowired
	JdbcTemplate jt;
	
	@SuppressWarnings("unchecked")
	public List<DTOMap> getDataMaster(String table) {
		return jt.query("SELECT * FROM " + table, new DTOMap());
	}
	
	public int updateData(DTOMap dtoMap, String tblName) {
		List<String> whereCol = Arrays.asList(dtoMap.getString("PK").split(","));
		dtoMap.map.remove("PK");
		List<Object> object = new ArrayList<Object>();
		String sql = "UPDATE "+tblName+" SET ";
		for (String key : dtoMap.map.keySet()) {
			if (!whereCol.contains(key)){
				if(dtoMap.get(key) != null){
					sql += key + " = ?,";
					object.add(dtoMap.get(key));
				}
			}
		}
		sql = sql.substring(0, sql.length()-1) + " WHERE ";
		
		for (String wc : whereCol) {
			sql += wc + " = ? AND ";
			object.add(dtoMap.get(wc.trim()));
		}
		sql=sql.substring(0, sql.length()-4);
		Object[] param = object.toArray(new Object[object.size()]);
		return jt.update(sql,param);
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
		return jt.update(sql, param);
	
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
		return jt.update(sql,param);
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
		return jt.query(sql,param, new DTOMap());
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
		return jt.query(sql, new DTOMap());
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
		return jt.queryForObject(sql,param, Integer.class) > 0 ? true : false;
	}

	@SuppressWarnings("unchecked")
	public List<DTOMap> getDataMasterOrderById(String orderId, String tblName, Boolean isAsc) {
		return jt.query("SELECT * FROM "+tblName+" ORDER BY " + orderId + (isAsc ? " ASC" : "DESC"), new DTOMap());
	}

	@SuppressWarnings("unchecked")
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
			return (DTOMap)jt.queryForObject(sql, param, new DTOMap());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<DTOMap> getMenuItemsByRole(String role) {
        String sql = "SELECT A.MENU_ID, A.NAME,A.FORM_ID,A.MENU_PARENT, CASE WHEN A.MENU_ID = B.MENU_ID THEN '1' ELSE '0' END AS PICK, SEQ " +
			" FROM CFG_MENU A LEFT JOIN CFG_ROLE_MENU B ON B.MENU_ID = A.MENU_ID  AND B.ROLE_ID =? ORDER BY 1,3,5";
		return jt.query(sql, new Object[]{role}, new DTOMap());
	}

	@SuppressWarnings("unchecked")
	public List<DTOMap> getMenuItemsByUser(String user) {
        String sql = "SELECT A.MENU_ID, A.NAME,A.FORM_ID,A.MENU_PARENT, "
        		+ "CASE WHEN A.MENU_ID = B.MENUID THEN '1' ELSE '0' END AS PICK, "
        		+ "SEQ FROM CFG_MENU A LEFT JOIN CFG_USER_ROLE_MENU B ON B.MENUID = "
        		+ "A.MENU_ID AND B.USERID = ? ORDER BY 1,3,5";
		return jt.query(sql, new Object[]{user}, new DTOMap());
	}
	
	public int update(String sql, Object[] param) {
		return jt.update(sql, param);
	}
	
	@SuppressWarnings("unchecked")
	public List<DTOMap> getDataMaster(String sql, Object[] param) {
		return jt.query(sql, param, new DTOMap());
	}

	@SuppressWarnings("unchecked")
	public DTOMap getMapMaster(String sql, Object[] param) {
		try {
			return (DTOMap)jt.queryForObject(sql, param, new DTOMap());
		} catch (EmptyResultDataAccessException e) {
//			e.printStackTrace();
			return null;
		}
	}
	
	public Connection getConnection() {
		try {
			return jt.getDataSource().getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public DTOMap getParameter(int parmGrp) {
		DTOMap dtoSysParm = new DTOMap();
		List<DTOMap> listSysParm = jt.query("SELECT PARMID, PARMNM, VALUE FROM CFG_PARM WHERE PARMGRP = ? ", new Object[]{parmGrp}, new DTOMap());
		for (DTOMap dtoResult : listSysParm) {
			dtoSysParm.put(dtoResult.getString("PARMNM").toUpperCase(), dtoResult.getString("VALUE"));
		}
		return dtoSysParm;
	}
}
