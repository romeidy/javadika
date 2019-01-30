package id.co.collega.ifrs.common;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;

import org.springframework.jdbc.core.RowMapper;

public class DTOMap implements Serializable, RowMapper{
	private static final long serialVersionUID = -8840406844877458198L;
	public HashMap<String, Object> map = new HashMap<String, Object>();

	public HashMap<String, Object> getMap() {
		return map;
	}

	public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		DTOMap dto=new DTOMap();
		int rowCount = rs.getMetaData().getColumnCount();
		for (int i = 1; i <= rowCount; i++) {
			if (rs.getObject(i) instanceof Blob) {
				byte[] arrayBytes = ((Blob) rs.getObject(i)).getBytes(1, (int) ((Blob)rs.getObject(i)).length());
				dto.map.put(rs.getMetaData().getColumnLabel(i), arrayBytes);
			}else if (rs.getObject(i) instanceof Clob) {
				String arrayBytes = ((Clob) rs.getObject(i)).getSubString(1, (int) ((Clob)rs.getObject(i)).length());
				dto.map.put(rs.getMetaData().getColumnLabel(i), arrayBytes);
			}else{
				dto.map.put(rs.getMetaData().getColumnLabel(i), rs.getObject(i));
			}
		}
		return dto;
	}
	
	public byte[] getBytes(String name){
		return ((byte [])map.get(name));
	}
	
	public void put(String name, Object o){
		map.put(name, o);
	}
	
	public Object get(String name){
		return map.get(name);
	}
	
	public String getString(String name){
		return (String)map.get(name);
	}
	
	public Integer getInt(String name){
		return (Integer)map.get(name);
	}
	
	public Date getDate(String name){
		return (Date)map.get(name);
	}
	
	public BigDecimal getBigDecimal(String name){
		return (BigDecimal)map.get(name);
	}
	
	public Double getDouble(String name){
		return (Double)map.get(name);
	}
	
	public void copyItem(String key, DTOMap map){
		this.put(key, map.get(key));
	}

	public boolean getSelected(String string) {
		// TODO Auto-generated method stub
		return false;
	}
}
