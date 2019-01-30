package id.co.collega.v7.seed.config;


import java.util.Collection;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import id.co.collega.ifrs.common.DTOMap;
import id.co.collega.v7.security.UserDetails;
import id.co.collega.v7.security.UserDetailsImpl;
import id.co.collega.v7.ui.component.MenuTreeItem;

//import org.springframework.context.annotation.Primary;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.stereotype.Component;
@Component
@Primary
public interface AuthenticationService{
      DTOMap doLogin(String user, String pass);
      Collection<MenuTreeItem> getMenuItems();
  	
  	  Collection<MenuTreeItem> getMenuItemsByRole(String role);
  	  Collection<MenuTreeItem> getMenuItemsByUser(String user, String role);
  	  
      UserDetails getUserDetails();
      
      UserDetailsImpl getUserCore(String userId, String password);  
    	
    	
    	
//    	if("s9999".equals(authentication.getName()) && "P@ssw0rd".equals(authentication.getCredentials())){
//            UserDetailsImpl userDetails = new UserDetailsImpl();
//            userDetails.setUserId("s9999");
//            userDetails.setUserName("User Name");
//            userDetails.setPassword("password");
//            userDetails.setBranchId("000");
//            return userDetails;
//        }else{
//            throw new BadCredentialsException("Invalid user");
//        }
    
}
