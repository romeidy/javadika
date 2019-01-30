package id.co.collega.ifrs.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import id.co.collega.ifrs.common.DTOMap;
import id.co.collega.v7.security.AbstractAuthenticationProvider;
import id.co.collega.v7.security.UserDetails;
import id.co.collega.v7.security.UserDetailsImpl;
import id.co.collega.v7.seed.config.AuthenticationService;

@Component
@Primary
@Service
public class AuthProvider extends AbstractAuthenticationProvider{
	protected Logger logger = LoggerFactory.getLogger("gateway-logger");
	
	@Autowired
	AuthenticationService authService;
	
	@Override
    public UserDetails doAuthenticate(Authentication authentication) throws AuthenticationException {
		try{	
			String user = authentication.getName();
			String pass = (String) authentication.getCredentials();
			DTOMap login = authService.doLogin(user, pass);
			DTOMap  userMap = (DTOMap)login.get("USERMAP");
			
			UserDetailsImpl userDetails = new UserDetailsImpl();
	        userDetails.setUserId(userMap.getString("USERID"));
	        userDetails.setUserName(userMap.getString("USERNM"));
	        userDetails.setPassword(userMap.getString("PWD"));
	        userDetails.setBranchId(userMap.getString("KD_CAB"));
	        userDetails.setActiveRole(userMap.getString("ROLEID"));
	        //userDetails.setRoleSPV(userMap.getString("ROLESPV"));
	        
//			String user = authentication.getName();
//			String pass = (String) authentication.getCredentials();
//
////			DTOMap login = authService.doLogin(user, pass);
//			UserDetailsImpl userDetails = new UserDetailsImpl();
//			
////			if(login.get("USERMAP") != null){
////				DTOMap  userMap = (DTOMap)login.get("USERMAP");
////				
////		        userDetails.setUserId(userMap.getString("USERID"));
////		        userDetails.setUserName(userMap.getString("USERNM"));
////		        userDetails.setPassword(userMap.getString("PWD"));
////		        userDetails.setBranchId(userMap.getString("KD_CAB"));
////		        userDetails.setActiveRole(userMap.getString("ROLEID"));
////			}else{
//				userDetails = authService.getUserCore(user, pass);
////			}
			
	        
	        
//	        List<NamingGrantedAuthority> authorities = new ArrayList<NamingGrantedAuthority>();
//	    	List<DTOMap> roles = (List<DTOMap>)login.get("ROLESMAP");
//			for (DTOMap role : roles) {
//				authorities.add(new NamingGrantedAuthority(role.getString("ROLEID"), role.getString("ROLENM")));
//			}
//	        userDetails.setAuthorities(authorities);
	        
//	        GlobalVariable.getInstance().put(GlobalVariable.BTN_CARI, "../image/cari.png");
	        return userDetails;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BadCredentialsException(e.getMessage());
		}
    }
}
