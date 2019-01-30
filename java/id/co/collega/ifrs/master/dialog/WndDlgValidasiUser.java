package id.co.collega.ifrs.master.dialog;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;

import id.co.collega.ifrs.common.Cryptograph;
import id.co.collega.ifrs.common.DTOMap;
import id.co.collega.ifrs.master.service.MasterServices;
import id.co.collega.ifrs.util.ComponentUtil;
import id.co.collega.ifrs.util.MessageBox;
import id.co.collega.v7.seed.config.AuthenticationService;
import id.co.collega.v7.ui.component.composite.PopupController;

@org.springframework.stereotype.Component
@Scope("execution")
public class WndDlgValidasiUser extends PopupController<Grid> {

	@Autowired
	AuthenticationService auth;
	@Autowired
	MasterServices masterService;

	// @Autowired JdbcTemplate jt1;

	@Wire
	Textbox txtUserID;
	@Wire
	Textbox txtPassword;
	@Wire
	Grid gridUser;
	@Wire
	Grid gridLsUser;
	@Wire
	Button btnLogin;
	@Wire
	Button btnCancel;

	@Wire
	Listbox list;

	DTOMap mapFromParent = null;
	
	String user, pass,returnValue="gagal" ;

	public void doAfterCompose(Grid comp) throws Exception {
		super.doAfterCompose(comp);

		Map<String, Object> data = (Map<String, Object>) Executions.getCurrent().getArg();
		mapFromParent = (DTOMap) data.get("data");

		list.addEventListener(Events.ON_DOUBLE_CLICK, new EventListener<Event>() {

			@Override
			public void onEvent(Event arg0) throws Exception {
				doLoadUser();

			}
		});
		btnLogin.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

			@Override
			public void onEvent(Event arg0) throws Exception {
				doCekLogin();

			}
		});
		doLoad();
		

	}
	private void doLoadUser() {
		DTOMap map = null;
		map = (DTOMap) list.getSelectedItem().getAttribute("DATAUSER");
		ComponentUtil.setValue(txtUserID, map.getString("USERID"));
		txtUserID.setDisabled(true);
		// txtKeterangan.focus();
		txtPassword.focus();
	}

	public void doCekLogin() throws NoSuchAlgorithmException, UnsupportedEncodingException {
		
		try {
			user = ComponentUtil.getValue(txtUserID).toString();
		}catch(NullPointerException e) {
			e.printStackTrace();
			MessageBox.showInformation("UserID Tidak boleh Kosong");
		}
		
		try {
			pass = ComponentUtil.getValue(txtPassword).toString();
		}catch(NullPointerException e) {
			e.printStackTrace();
			MessageBox.showInformation("Password  Tidak boleh Kosong");
		}
		
		
		try {
			DTOMap userMap = masterService.getMapMaster("SELECT * FROM MST_USER WHERE USERID ='" + user + "' ",
					null);
			if (userMap.getInt("STATUS") == 2) {
				throw new WrongValueException(txtUserID, "User Id tidak Aktif");
			}else if (userMap.getInt("AMTFAIL") >= userMap.getInt("LMTFAIL")) {
				MessageBox.showInformation("User have exceeded the login error");
			} else if (!userMap.get("PWD").equals(Cryptograph.MD5(pass))) {
				masterService.update("UPDATE MST_USER SET AMTFAIL = AMTFAIL+1 WHERE USERID = '" + user + "' ",
						null);
				throw new WrongValueException(txtPassword, "password Salah");
			}else {						
				try {
					returnValue="Berhasil";
					fireEventPopupButton();
					
					
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		} catch (NullPointerException e) {
			e.printStackTrace();
			MessageBox.showInformation("Data Tidak boleh Kosong");
		}

	}
	private void doLoad(){
		try{
			String sql = "SELECT * FROM MST_USER WHERE FLGSPV = 1";
			List<DTOMap> lsUser = masterService.getDataMaster(sql, null);
			txtUserID.focus();
			if (lsUser.isEmpty() || lsUser == null) {
				MessageBox.showInformation("Data Tidak Ditemukan");
				list.getItems().clear();
			} else {
				list.getItems().clear();
				for (DTOMap map : lsUser) {
					int x = 1;
					Listitem item = new Listitem();
					item.setAttribute("DATAUSER", map);
					item.appendChild(new Listcell(String.valueOf(x++)));
					item.appendChild(new Listcell(map.getString("USERID")));
					item.appendChild(new Listcell(map.getString("USERNM")));
					list.appendChild(item);
				}
			}	
		} catch (NullPointerException e) {
			e.printStackTrace();
			MessageBox.showInformation("Data Tidak boleh Kosong");
		}

	}
	@Override
	public Object returnValue() {
		// TODO Auto-generated method stub
		return returnValue;
	}

}
