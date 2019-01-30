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
public class WndChangePassword extends SelectorComposer<Component>{
	
	@Wire Textbox txtPassOld;
	@Wire Textbox txtPassNew;
	@Wire Textbox txtPassConf;
	@Wire Button btnChange;
	@Wire Button btnCancel;
	
	@Autowired MasterServices masterService;
	@Autowired AuthenticationService authService;
	
	Boolean onLoad = false;
	@Wire DTOMap dataUser;
	
	
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		btnChange.addEventListener(Events.ON_CLICK, new EventListener() {
			public void onEvent(Event event) throws Exception {
				doChangePassword();
			}
		});
		
		btnCancel.addEventListener(Events.ON_CLICK, new EventListener() {
			public void onEvent(Event event) throws Exception {
				doReset();
			}
		});
	}
	
	private void doChangePassword(){
		try { 
			dataUser = (DTOMap) masterService.getMapMaster("SELECT * FROM MST_USER WHERE USERID = ?", new Object[]{ authService.getUserDetails().getUserId() });
			if (!dataUser.getString("PWD").equals(Cryptograph.MD5(txtPassOld.getValue()))) {
				MessageBox.showInformation("Password Lama yang anda masukan salah.");
			}else if (!txtPassNew.getValue().equals(txtPassConf.getValue())) {
				MessageBox.showInformation("Password Baru dan Konfirmasi password berbeda.");
			}else{
				dataUser.put("PWD", Cryptograph.MD5(txtPassNew.getValue()));
				dataUser.put("PK", "USERID");
				masterService.updateData(dataUser, "MST_USER");
				MessageBox.showInformation("Password berhasil diubah.");
			}
 			doReset();
		} catch (Exception ex) {
			MessageBox.showError(ex.getMessage());
		}
	}
	
	
	private void doReset(){
		txtPassOld.setValue("");
		txtPassNew.setValue("");
		txtPassConf.setValue("");
	}
	
	
}
