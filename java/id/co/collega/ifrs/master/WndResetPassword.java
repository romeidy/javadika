package id.co.collega.ifrs.master;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Textbox;

import id.co.collega.ifrs.common.Cryptograph;
import id.co.collega.ifrs.common.DTOMap;
import id.co.collega.ifrs.master.service.MasterServices;
import id.co.collega.ifrs.util.MessageBox;
import id.co.collega.v7.seed.config.AuthenticationService;

@org.springframework.stereotype.Component
@Scope("execution")
public class WndResetPassword extends SelectorComposer<Component>{
	
	@Wire Textbox txtUserId;
	@Wire Textbox txtNama;
	@Wire Button btnReset;
	@Wire Button btnCancel;
	
	@Autowired MasterServices masterService;
	@Autowired AuthenticationService authService;
	
	Boolean onLoad = false;
	@Wire DTOMap dataUser;
	
	
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		txtUserId.addEventListener(Events.ON_OK, new EventListener() {
			public void onEvent(Event event) throws Exception {
				getDataUser(txtUserId.getValue());
			}
		});
		
		btnCancel.addEventListener(Events.ON_CLICK, new EventListener() {
			public void onEvent(Event event) throws Exception {
				doReset();
			}
		});
		
		btnReset.addEventListener(Events.ON_CLICK, new EventListener() {
			public void onEvent(Event event) throws Exception {
				doResetPassword();
			}
		});
	}
	
	private void getDataUser(String userId){
		dataUser = (DTOMap) masterService.getMapMaster("SELECT * FROM MST_USER WHERE USERID = ?", new Object[]{ userId });
	
		txtNama.setValue(dataUser.getString("USERNM"));
	}
	
	private void doResetPassword(){
		try { 
			dataUser.put("PWD", Cryptograph.MD5(txtUserId.getValue()));
			dataUser.put("PK", "USERID");
			masterService.updateData(dataUser, "MST_USER");
 			MessageBox.showInformation("Password berhasil direset, Password sesuai dengan nama user yang anda entry.");
 			doReset();
		} catch (Exception ex) {
			MessageBox.showError(ex.getMessage());
		}
	}
	
	private void doReset(){
		txtNama.setValue("");
		txtUserId.setValue("");
	}
	
	
}
