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
public class WndPemeliharaanAkun extends SelectorComposer<Component>{

	@Wire Window wnd;
	@Wire Combobox cmbRole;
	@Wire Combobox cmbBranch;
	@Wire Textbox txtUserId;
	@Wire Textbox txtUserNmUser;
	@Wire Textbox txtPassword;
	@Wire Textbox txtPassword2;
	@Wire Textbox txtUserPPATK;
	@Wire Textbox txtCabangKonsol;
	@Wire Button btnSave;
	@Wire Button btnReset;
	@Wire Row rowPPATK;
	
	@Autowired MasterServices masterService;
	@Autowired AuthenticationService authService;
	
	Boolean onLoad = false;
	
	
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		
		txtUserId.addEventListener(Events.ON_OK, new EventListener() {
            public void onEvent(Event event) throws Exception {
        		doSearch();
            }
        });
		
		
		btnSave.addEventListener(Events.ON_CLICK, new EventListener() {
            public void onEvent(Event event) throws Exception {
        		doSave();
            }
        });
		
		
		cmbRole.addEventListener(Events.ON_SELECT, new EventListener() {
			public void onEvent(Event arg0) throws Exception {
				String kodeKantorPusat = cmbRole.getValue().substring(0, 2).trim();
				if ("01".equals(kodeKantorPusat)){
					txtUserPPATK.setDisabled(false);
				} else {
					txtUserPPATK.setDisabled(true);
					txtUserPPATK.setValue("");
				}
			}
		});
		
		cmbBranch.addEventListener(Events.ON_SELECT, new EventListener() {
			public void onEvent(Event arg0) throws Exception {
				ComponentUtil.setValue(txtCabangKonsol, getNmCabangKonsol());
			}
		});
		
		doLoadCombo();
		doLoadComboCabang();
		doSearch();
	}
	
	@SuppressWarnings("unchecked")
	private void doLoadCombo() {
		cmbRole.getItems().clear();
		cmbRole.setSelectedIndex(-1);
		List<DTOMap> listData = (List<DTOMap>) masterService.getDataMaster("SELECT * FROM CFG_ROLE WHERE ROLEID <> 'XX'", null);
		Comboitem item = new Comboitem();
		cmbRole.appendChild(item);
		for (DTOMap map : listData) {
			item = new Comboitem();
			item.setLabel(map.getString("ROLEID") + " - " + map.getString("ROLENM"));
			item.setValue(map.getString("ROLEID"));
			cmbRole.appendChild(item);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void doLoadComboCabang() {
		cmbBranch.getItems().clear();
		cmbBranch.setSelectedIndex(-1);
		List<DTOMap> listData = (List<DTOMap>) masterService.getDataMaster("SELECT * FROM CFG_CABANG "
				+ "								WHERE TGL_POS IN (SELECT MAX(TGL_POS) FROM CFG_CABANG)", null);
		Comboitem item = new Comboitem();
		cmbBranch.appendChild(item);
		for (DTOMap map : listData) {
			item = new Comboitem();
			item.setLabel(map.getString("KD_CAB") + " - " + map.getString("NM_CAB"));
			item.setValue(map.getString("KD_CAB"));
			cmbBranch.appendChild(item);
		}
	}
	
	private void doSave(){
		if (doValidation()) {
			doUpdate();
		}
	}
	
	private void doEdit(DTOMap data){
		if (data != null){
			ComponentUtil.setValue(cmbRole, data.get("ROLEID"));
			ComponentUtil.setValue(cmbBranch, data.get("KD_CAB"));
			ComponentUtil.setValue(txtUserId, data.get("USERID"));
			ComponentUtil.setValue(txtUserNmUser, data.get("USERNM"));
			ComponentUtil.setValue(txtUserPPATK, data.get("USERID_PPATK"));
			onLoad = true;
			txtUserId.setDisabled(true);
			
			String kodeKantorPusat = cmbRole.getValue().substring(0, 2).trim();
			if ("99".equals(kodeKantorPusat)){
				rowPPATK.setVisible(true);
				txtUserPPATK.setDisabled(false);
			} else {
				rowPPATK.setVisible(false);
				txtUserPPATK.setDisabled(true);
			}
			
			ComponentUtil.setValue(txtCabangKonsol, getNmCabangKonsol());
		}
	}
	
	private void doSearch(){
		DTOMap datas = new DTOMap();
		datas = masterService.getMapMaster("SELECT * FROM MST_USER WHERE USERID=?", new Object[]{authService.getUserDetails().getUserId()});
		doEdit(datas);
	}
	
	
	private void doUpdate(){
		try {
			DTOMap datas = new DTOMap();
			datas.put("USERID", ComponentUtil.getValue(txtUserId));
			datas.put("KD_CAB", ComponentUtil.getValue(cmbBranch));
			datas.put("ROLEID", ComponentUtil.getValue(cmbRole));
			datas.put("USERNM", ComponentUtil.getValue(txtUserNmUser));
			datas.put("PWD", Cryptograph.MD5((String) ComponentUtil.getValue(txtPassword)));
			datas.put("UPDUSER", authService.getUserDetails().getUserId());
			datas.put("UPDDATE", new Date());
			datas.put("USERID_PPATK", ComponentUtil.getValue(txtUserPPATK));
			datas.put("PK", "USERID");
			masterService.updateData(datas, "MST_USER");
			MessageBox.showInformation("Akun Anda berhasil di update");
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.showError(e.getMessage());
		}
	}
	
	
	private boolean doValidation(){
		if (ComponentUtil.getValue(txtUserId) == null || ComponentUtil.getValue(txtUserId).equals("")) {
			throw new WrongValueException(txtUserId, "User id harus diisi.");
		}else if (!ComponentUtil.getValue(txtPassword).equals(ComponentUtil.getValue(txtPassword2))) {
			throw new WrongValueException(txtPassword2, "Password tidak sama.");
		}else if (ComponentUtil.getValue(txtPassword).toString().length()<7) {
			throw new WrongValueException(txtPassword, "Password minimal 7 karakter");
		}else if (ComponentUtil.getValue(txtPassword).equals("password")) {
			throw new WrongValueException(txtPassword, "Password terlalu mudah");
		}else if (ComponentUtil.getValue(txtUserNmUser) == null || ComponentUtil.getValue(txtUserNmUser).equals("")) {
			throw new WrongValueException(txtUserNmUser, "Username harus diisi.");
		}else if (ComponentUtil.getValue(cmbBranch) == null || ComponentUtil.getValue(cmbBranch).equals("")) {
			throw new WrongValueException(cmbBranch, "Cabang harus diisi.");
		}else if (ComponentUtil.getValue(cmbRole) == null || ComponentUtil.getValue(cmbRole).equals("")) {
			throw new WrongValueException(cmbRole, "Role id harus diisi.");
		}else if (ComponentUtil.getValue(txtUserPPATK) == null && "01".equals(cmbRole.getValue().substring(0, 2).trim())){
			throw new WrongValueException(txtUserPPATK, "ID User PPATK harus diisi.");
		}
		return true;
	}
	
	private String getNmCabangKonsol(){
		String result = "";
		
		DTOMap dto = (DTOMap)masterService.getMapMaster("SELECT * FROM CFG_CABANG "
				+ "										WHERE KD_CAB=(SELECT KD_CAB_KONSOL "
				+ "														FROM CFG_CABANG "
				+ "														WHERE KD_CAB=? "
				+ "															AND TGL_POS IN (SELECT MAX(TGL_POS) FROM CFG_CABANG))"
				+ "												AND TGL_POS IN (SELECT MAX(TGL_POS) FROM CFG_CABANG)", new Object[]{ComponentUtil.getValue(cmbBranch)});

		if(dto!=null){
			result=dto.getString("KD_CAB_KONSOL")+" - "+dto.getString("NM_CAB");
		}
		
		return result;
	}
}
