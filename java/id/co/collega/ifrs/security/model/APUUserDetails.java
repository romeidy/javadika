package id.co.collega.ifrs.security.model;

import java.util.ArrayList;
import java.util.List;

import id.co.collega.ifrs.common.DTOMap;
import id.co.collega.v7.core.model.SimpleSupervisor;
import id.co.collega.v7.core.model.Supervisor;
import id.co.collega.v7.security.UserDetails;
import id.co.collega.v7.security.model.NamingGrantedAuthority;

public class APUUserDetails implements UserDetails{

	DTOMap paramLogin;
	boolean isAdmin;
	String activeRole;
	String branchId;
	String password;
	String userId;
	String userName;
	String roleSPV;
	

	List<Supervisor> supervisors = new ArrayList<Supervisor>();
	List<NamingGrantedAuthority> authorities = new ArrayList<NamingGrantedAuthority>();
	
	@Override
	public String getActiveRole() {
		return activeRole;
	}

	@Override
	public List<NamingGrantedAuthority> getAuthorities() {
		return authorities;
	}
	
	public void setAuthorities() {
		List<DTOMap> roles = (List<DTOMap>)getParamLogin().get("ROLESMAP");
		for (DTOMap role : roles) {
			getAuthorities().add(new NamingGrantedAuthority(role.getString("ROLEID"), role.getString("ROLENM")));
		}
	}

	public void setParamLogin(DTOMap paramLogin){
		this.paramLogin = paramLogin;
	}
	
	public DTOMap getParamLogin() {
		return paramLogin;
	} 	

	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}

	public List<Supervisor> getSupervisors() {
		return supervisors;
	}

	public void setSupervisors(List<Supervisor> supervisors) {
		List<DTOMap> supervisi = (List<DTOMap>) getUserMap().get("SPVMAP");
		for (DTOMap spv : supervisi) {
			SimpleSupervisor sSpv = new SimpleSupervisor();
			sSpv.setSpvId(spv.getString("USERID"));
			sSpv.setName(spv.getString("USERNM"));
			getSupervisors().add(sSpv);
		}
	}

	public String getUserId() {
		return userId;
	}
	
	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Override
	public String getUserName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isAdmin() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setActiveRole(String role) {
		this.activeRole = role;
		
	}

	public String getBranchId() {
		return branchId;
	}

	public void setBranchId(String branch) {
		this.branchId = branch;
	}
	public String getRoleSPV() {
		return roleSPV;
	}

	public void setRoleSPV(String roleSPV) {
		this.roleSPV = roleSPV;
	}
	
	public DTOMap getCfgSys() {
		return (DTOMap) getParamLogin().get("CFG_SYS");
	}

	public DTOMap getCfgParm() {
		return (DTOMap) getParamLogin().get("CFG_PARM");
	}

	public DTOMap getUserMap() {
		return (DTOMap) getParamLogin().get("USERMAP");
	}

	public DTOMap getSysParm() {
		return (DTOMap) getParamLogin().get("SYS_PARM");
	}

}
