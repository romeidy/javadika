package id.co.collega.ifrs.master;

import java.util.Date;
import java.util.List;

import net.sf.ehcache.search.parser.InteractiveCmd.Cmd;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.jet.gand.services.GlobalVariable;

import id.co.collega.ifrs.common.Cryptograph;
import id.co.collega.ifrs.common.DTOMap;
import id.co.collega.ifrs.master.service.MasterServices;
import id.co.collega.ifrs.util.ComponentUtil;
import id.co.collega.ifrs.util.MessageBox;
import id.co.collega.v7.seed.config.AuthenticationService;

@org.springframework.stereotype.Component
@Scope("execution")
public class WndDialogGantiPassword extends SelectorComposer<Component>{

	@Wire Window wnd;
	@Wire Textbox txtUserId;
	@Wire Textbox txtPassLama;
	@Wire Textbox txtPassBaru;
	@Wire Textbox txtKonfPassBaru;
	@Wire Button btnSave;
	
	@Autowired MasterServices masterService;
	@Autowired AuthenticationService authService;
	
	Boolean onLoad = false;
	
	
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		
		btnSave.addEventListener(Events.ON_CLICK, new EventListener() {
            public void onEvent(Event event) throws Exception {
        		doSave();
            }
        });
		
		ComponentUtil.setValue(txtUserId, authService.getUserDetails().getUserId());
	}
	
	private void doSave(){
		if (doValidation()) {
			doUpdate();
		}
	}
	
	
	private void doUpdate(){
		try {
			DTOMap datas = new DTOMap();
			datas.put("USERID", ComponentUtil.getValue(txtUserId));
			datas.put("PWD", Cryptograph.MD5((String) ComponentUtil.getValue(txtPassBaru)));
			datas.put("UPDUSER", authService.getUserDetails().getUserId());
			datas.put("UPDDATE", new Date());
			datas.put("PK", "USERID");
			masterService.updateData(datas, "MST_USER");
			MessageBox.showInformation("Akun Anda berhasil di update");
			wnd.detach();
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.showError(e.getMessage());
		}
	}
	
	
	private boolean doValidation(){
		if (!ComponentUtil.getValue(txtPassBaru).equals(ComponentUtil.getValue(txtKonfPassBaru))) {
			throw new WrongValueException(txtKonfPassBaru, "Password tidak sama.");
		}else if (ComponentUtil.getValue(txtPassBaru).toString().length()<8) {
			throw new WrongValueException(txtPassBaru, "Password minimal 8 karakter");
		}else if (ComponentUtil.getValue(txtPassBaru).equals("password")) {
			throw new WrongValueException(txtPassBaru, "Password terlalu mudah");
		}
		return true;
	}
}
