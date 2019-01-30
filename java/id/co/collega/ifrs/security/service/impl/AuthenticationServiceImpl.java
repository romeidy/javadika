package id.co.collega.ifrs.security.service.impl;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.zkoss.zk.ui.WrongValueException;

import com.jet.gand.services.GlobalVariable;

import id.co.collega.ifrs.common.Cryptograph;
import id.co.collega.ifrs.common.DTOMap;
import id.co.collega.ifrs.common.JdbcTemplate;
import id.co.collega.ifrs.gateway.GatewayFunction;
import id.co.collega.ifrs.security.model.MenuItem;
import id.co.collega.v7.security.UserDetails;
import id.co.collega.v7.security.UserDetailsImpl;
import id.co.collega.v7.seed.config.AuthenticationService;
import id.co.collega.v7.ui.component.MenuTreeItem;

@Service
public class AuthenticationServiceImpl implements AuthenticationService{
//	protected Logger logger = LoggerFactory.getLogger("gateway-logger");
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	JdbcTemplate jt;
	@Autowired
	GatewayFunction gateway;
	
	public DTOMap doLogin(String user, String pass) {
		DTOMap userMap = (DTOMap) jt.queryObject("SELECT * FROM MST_USER WHERE USERID=? ", new Object[]{user},new DTOMap());
		DTOMap sysHost = (DTOMap) jt.queryObject("SELECT * FROM CFG_SYS",new DTOMap());
		try {
			if (userMap == null) {
				throw new WrongValueException("User not found");
			} else {
				if(userMap.getInt("STATUS") == 2){
					throw new WrongValueException("User is not active");
				}else if(userMap.getInt("AMTFAIL") >= userMap.getInt("LMTFAIL")){
					throw new WrongValueException("User have exceeded the login error");
				} else if (!userMap.getString("PWD").equals(Cryptograph.MD5(pass))){
						jt.update("UPDATE MST_USER SET AMTFAIL = AMTFAIL+1 WHERE USERID = ?", new Object[]{user});
						throw new WrongValueException("Wrong password");
				} else {
					jt.update("UPDATE MST_USER SET AMTFAIL = 0, LASTLOGIN = ? WHERE USERID = ?", new Object[]{new Date(), user});
					
					DTOMap paramLogin = new DTOMap();
					paramLogin.put("USERMAP", userMap);
//					
					setSystemHost();
					setCfgSystemHost();
					setUserMaster(user);
					//setWorkflow(user);
					
//					paramLogin.put("BRANCHMAP", getBranchMap(userMap.getString("BRANCHID")));
//					paramLogin.put("CFG_SYS", getSystemConfig());
//					paramLogin.put("ROLESMAP", getRolesMap(userMap.getString("ROLEID")));
					return paramLogin;
				}
			}
		} catch (DataAccessException e) {
			e.printStackTrace();
			throw new WrongValueException("Error : " + e.getMessage());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new WrongValueException("Error : " + e.getMessage());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new WrongValueException("Error : " + e.getMessage());
		}
	}

	private void setSystemHost() {
		DTOMap sys = (DTOMap) jt.queryObject("SELECT * FROM CFG_SYS ", new DTOMap());
		GlobalVariable.getInstance().put("syshost", sys);
	}
	
	//add 17/04/2018
	private void setCfgSystemHost() {
		DTOMap sys = (DTOMap) jt.queryObject("SELECT * FROM CFG_SYS ", new DTOMap());
		GlobalVariable.getInstance().put("cfgsys", sys);
	}
	
	private void setUserMaster(String user){
		//System.out.println("sql  : SELECT * FROM MST_USER m  INNER JOIN CFG_ROLE c ON m.roleid= c.ROLEID WHERE m.USERID=? " +user);
		DTOMap sys = (DTOMap) jt.queryObject("SELECT * FROM MST_USER  WHERE USERID=? ", new Object[]{user},new DTOMap());		
		GlobalVariable.getInstance().put("USER_MASTER", sys);
		setBranchMap(sys.getString("KD_CAB"));
	}

	private void setBranchMap(String branchId){
		DTOMap branchMap = (DTOMap) jt.queryObject("SELECT DISTINCT A.KD_CAB, A.* FROM CFG_CABANG A "
				+ "									WHERE A.KD_CAB=? "
				+ "										AND A.TGL_POS IN (SELECT MAX(B.TGL_POS) FROM CFG_CABANG B) ", 
				new Object[]{branchId},new DTOMap());
		GlobalVariable.getInstance().put("branchMap", branchMap);
	}
	
	@SuppressWarnings("unchecked")
	public Collection<MenuTreeItem> getMenuItems() {
		List<MenuTreeItem> menuItems = new ArrayList<MenuTreeItem>();
        String sql = "SELECT A.*, (SELECT ZUL_FILE FROM SYS_FORM C WHERE A.FORM_ID = C.FORM_ID) AS ZUL_FILE FROM CFG_MENU A ORDER BY A.MENU_ID,A.MENU_PARENT,A.SEQ";
		List<DTOMap> menus = jt.query(sql, new DTOMap());
		for (DTOMap menu : menus) {
			menuItems.add(new MenuItem(menu.getString("MENU_ID"), menu.getString("NAME"), menu.getString("ZUL_FILE"), 
					(menu.get("ZUL_FILE") == null || menu.getString("ZUL_FILE").equals("")) ? false : true , menu.getString("MENU_PARENT"), menu.getString("FORM_ID")));
		}
		return menuItems;
	}

	@SuppressWarnings("unchecked")
	public Collection<MenuTreeItem> getMenuItemsByRole(String role) {
		List<MenuTreeItem> menuItems = new ArrayList<MenuTreeItem>();
		String sql = "SELECT A.*, B.*, (SELECT ZUL_FILE FROM SYS_FORM C WHERE A.FORM_ID = C.FORM_ID) AS ZUL_FILE FROM "
				+ "CFG_MENU A, CFG_ROLE_MENU B WHERE B.MENU_ID = A.MENU_ID AND B.ROLE_ID = ? ORDER BY A.MENU_ID,A.MENU_PARENT,A.SEQ";
		List<DTOMap> menus = jt.query(sql, new Object[]{role}, new DTOMap());
		for (DTOMap menu : menus) {
			menuItems.add(new MenuItem(menu.getString("MENU_ID"), menu.getString("NAME"), menu.getString("ZUL_FILE"), 
					(menu.get("ZUL_FILE") == null || menu.getString("ZUL_FILE").equals("")) ? false : true , menu.getString("MENU_PARENT"), menu.getString("FORM_ID")));
		}
		return menuItems;
	}
	
	@SuppressWarnings("unchecked")
	public Collection<MenuTreeItem> getMenuItemsByUser(String user, String role) {
		List<MenuTreeItem> menuItems = new ArrayList<MenuTreeItem>();
		String sql = "SELECT A.*, B.*, (SELECT ZUL_FILE FROM SYS_FORM C WHERE A.FORM_ID = C.FORM_ID) AS ZUL_FILE FROM "
				+ "CFG_MENU A, CFG_USER_ROLE_MENU B WHERE B.MENUID = A.MENU_ID AND B.USERID = ? AND B.ROLEID = ? ORDER BY A.MENU_ID,A.MENU_PARENT,A.SEQ";
		List<DTOMap> menus = jt.query(sql, new Object[]{user, role}, new DTOMap());
		for (DTOMap menu : menus) {
			menuItems.add(new MenuItem(menu.getString("MENU_ID"), menu.getString("NAME"), menu.getString("ZUL_FILE"), 
					(menu.get("ZUL_FILE") == null || menu.getString("ZUL_FILE").equals("")) ? false : true , menu.getString("MENU_PARENT"), menu.getString("FORM_ID")));
		}
		return menuItems;
	}
	
	@Override
	public UserDetails getUserDetails() {
		return (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}
	
	@Override
	public UserDetailsImpl getUserCore(String userId, String password) {
		try {
			UserDetailsImpl userDetails = new UserDetailsImpl();
			DTOMap userCore = gateway.loginUserCore(userId, password);
			DTOMap result = (DTOMap) userCore.get("result");
			if(userCore.getInt("statusId") == 1){
				DTOMap userMap = (DTOMap) result.get("USERMAP");
				DTOMap cfgParam = (DTOMap) result.get("CFG_PARM");
//				userDetails.setActiveRole(activeRole);
////				userDetails.setAuthorities(authorities);
				userDetails.setUserId(userMap.getString("USERID"));
			    userDetails.setUserName(userMap.getString("USERNM"));
			    userDetails.setPassword(userMap.getString("PWD"));
			    userDetails.setBranchId(userMap.getString("BRANCHID"));
			    userDetails.setActiveRole(cfgParam.getString("HT_ROLEID"));
			    //userDetails.setRoleSPV(userMap.getString("ROLESPV"));
			    return userDetails;
			}else{
				logger.info("User not found");
				throw new BadCredentialsException("User not found");
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new BadCredentialsException("Sorry your request cannot be process");
		}
		
		
	}
	
	@SuppressWarnings("unchecked")
	public List<DTOMap> getRolesMap(String role){
		return jt.query("SELECT * FROM CFG_ROLE WHERE ROLEID = ?", new Object[] { role }, new DTOMap());
	}
	
	@SuppressWarnings("unchecked")
	public DTOMap getSystemConfig(){
		try {
			return (DTOMap) jt.queryForObject("SELECT * FROM CFG_SYS", new DTOMap());
		} catch (Exception e) {
			return null;
		}
	}
}

