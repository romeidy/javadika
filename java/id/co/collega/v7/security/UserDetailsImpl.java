package id.co.collega.v7.security;

import id.co.collega.v7.core.model.Supervisor;
import id.co.collega.v7.security.model.NamingGrantedAuthority;

import java.util.List;

public class UserDetailsImpl implements UserDetails {

    String password;

    String activeRole;

    String branchId;

    String userId;

    String userName;
    
    String roleSPV;

    List<NamingGrantedAuthority> authorities;

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setActiveRole(String activeRole) {
        this.activeRole = activeRole;
    }

    @Override
    public String getActiveRole() {
        return activeRole;
    }

    public void setAuthorities(List<NamingGrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    @Override
    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    @Override
    public boolean isAdmin() {
        return false;
    }

    @Override
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public List<Supervisor> getSupervisors() {
        return null;
    }

    @Override
    public List<NamingGrantedAuthority> getAuthorities() {
        return authorities;
    }

	/*@Override
	public String getRoleSPV() {
		// TODO Auto-generated method stub
		return roleSPV;
	}
	
	
	public void setRoleSPV(String roleSPV) {
		this.roleSPV = roleSPV;
	}*/

}
