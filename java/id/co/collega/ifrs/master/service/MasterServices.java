package id.co.collega.ifrs.master.service;

import java.sql.Connection;
import java.util.List;

import id.co.collega.ifrs.common.DTOMap;

public interface MasterServices {
	List<DTOMap> getDataMaster(String table);
	List<DTOMap> getDataMaster(String sql, Object[] param);
	List<DTOMap> getDataMasterById(DTOMap datas, String table);
	List<DTOMap> getDataMasterLikeById(DTOMap datas, String table);
	List<DTOMap> getDataMasterOrderById(String orderId, String table, Boolean isAsc);
	List<DTOMap> getMenuItemsByRole(String role);
	List<DTOMap> getMenuItemsByUser(String user);
	DTOMap getMapMaster(String sql, Object[] param);
	DTOMap getMapMasterById(DTOMap datas, String table);
	DTOMap getParameter(int parmGrp);
	int insertData(DTOMap datas, String table);
	int updateData(DTOMap datas, String table);
	int deleteData(DTOMap datas, String table);
	int update(String sql, Object[] param);
	boolean isExist(DTOMap datas, String table);
	Connection getConnection();
}
