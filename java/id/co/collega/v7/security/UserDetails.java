package id.co.collega.v7.security;

import id.co.collega.v7.core.model.Supervisor;
import id.co.collega.v7.security.model.NamingGrantedAuthority;

import java.util.List;

public interface UserDetails {

    String getPassword();

    String getActiveRole();

    void setActiveRole(String roleCode);

    String getBranchId();

    void setBranchId(String branchId);

    boolean isAdmin();

    String getUserId();

    String getUserName();

    List<Supervisor> getSupervisors();

    List<NamingGrantedAuthority> getAuthorities();
    
    //String getRoleSPV();


}
