package id.co.collega.ifrs.master;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.ehcache.search.parser.InteractiveCmd.Cmd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import id.co.collega.v7.seed.controller.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.jet.gand.services.GlobalVariable;

import id.co.collega.ifrs.common.Cryptograph;
import id.co.collega.ifrs.common.DTOMap;
import id.co.collega.ifrs.master.service.MasterServices;
import id.co.collega.ifrs.util.ComponentUtil;
import id.co.collega.ifrs.util.MessageBox;
import id.co.collega.v7.seed.config.AuthenticationService;
import id.co.collega.v7.ui.component.DialogUtil;
import id.co.collega.v7.ui.component.DialogUtil.PopupMode;

@org.springframework.stereotype.Component
@Scope("execution")
public class WndPemeliharaanUser extends SelectorComposer<Component>{
	
	private static final Logger log=LoggerFactory.getLogger(WndPemeliharaanUser.class);

	@Wire Window wnd;
	@Wire Combobox cmbRole;
	@Wire Combobox cmbBranch;
	@Wire Textbox txtUserId;
	@Wire Textbox txtUserNmUser;
	@Wire Textbox txtPassword;
	@Wire Textbox txtUserPPATK;
	@Wire Textbox txtCabangKonsol;
	@Wire Intbox txtJmlFail;
	@Wire Intbox txtLmtFail;
	@Wire Radiogroup grpStatus;
	@Wire Radiogroup grpOtorisasi;
	@Wire Listbox list;
	@Wire Button btnSave;
	@Wire Button btnDelete;
	@Wire Button btnReset;
	@Wire Button btnOverwrite;
	
	@Autowired MasterServices masterService;
	@Autowired AuthenticationService authService;
	
	Boolean onLoad = false;
	
	DTOMap mapUser=(DTOMap)GlobalVariable.getInstance().get("USER_MASTER");
	DTOMap mapSys=(DTOMap)GlobalVariable.getInstance().get("cfgsys");
	String Aksi;
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		txtUserId.addEventListener(Events.ON_OK, new EventListener() {
            public void onEvent(Event event) throws Exception {
        		doSearch();
            }
        });
		
		list.addEventListener(Events.ON_DOUBLE_CLICK, new EventListener() {
            public void onEvent(Event event) throws Exception {
            	Listitem item = list.getSelectedItem();
            	
            	if(item!=null){
            		DTOMap data = (DTOMap)item.getAttribute("DATA");
            		doEdit(data);
            		//doBeforeEdit(data);
            	}
            }
        });
		
		btnSave.addEventListener(Events.ON_CLICK, new EventListener() {
            public void onEvent(Event event) throws Exception {
        		doSave();
            }
        });
		
		btnDelete.addEventListener(Events.ON_CLICK, new EventListener() {
            public void onEvent(Event event) throws Exception {
        		//doDelete();
            	if (checkPrivDelete()) {
                	doBeforeDelete();	
				}
            }
        });
		
		btnReset.addEventListener(Events.ON_CLICK, new EventListener() {
            public void onEvent(Event event) throws Exception {
        		doReset();
            }
        });
		
		btnOverwrite.addEventListener(Events.ON_CLICK, new EventListener() {
            public void onEvent(Event event) throws Exception {
        		doResetPassword();
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
				getDataByUserNm();
			}
		});
		
		doLoadCombo();
		doLoadComboCabang();
		doReset();
		cekBtnUpdate();
		
		DTOMap dto = (DTOMap)GlobalVariable.getInstance().get("USER_MASTER");
	}
	
	/*private boolean isSpvActive() {
		try {
			String sql="Select * from CFG_LTKT";
			DTOMap map=(DTOMap)masterService.getMapMaster(sql, null);
			if(map.getString("ISSUPERVISI_PUSAT").equals("1")) {
				return true;
			}
			return false;
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		
	}
	private void cekBtnUpdate() {
		if(isSpvActive() && authService.getUserDetails().getActiveRole().equals("05")) {
			btnSave.setDisabled(true);
		} else {
			btnSave.setDisabled(false);
		}		
	}*/
	private boolean isSpvActive() {
		try {
			String sql;
			sql = "Select * from CFG_LTKT";
			DTOMap map = (DTOMap) masterService.getMapMaster(sql, null);
			sql = "SELECT ROLEID FROM MST_USER WHERE ROLEID='05'";
			List<DTOMap> mapUser = masterService.getDataMaster(sql, null);
			if (map.getString("ISSUPERVISI_PUSAT").equals("1") && !mapUser.isEmpty()) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	private void cekBtnUpdate() {
		if (authService.getUserDetails().getActiveRole().equals("05")) {
			btnSave.setDisabled(true);
		} else {
			btnSave.setDisabled(false);
		}
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
				+ "															WHERE TGL_POS IN (SELECT MAX(TGL_POS) FROM CFG_CABANG)", null);
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
			if(onLoad){
				if (checkPrivUpdate()) {
					doCheckFirstUpdate();
				}
				//doUpdate();
			}else{
				if (checkPrivInsert()) {
					doBeforeInsert();
				}
				//doInsert();
			}
			
			//doInsertWorkflow();
			//doReset();
		}
	}
	
	private void doBeforeEdit(final DTOMap data) {
		if (isSpvActive()) {				
			Map<String, Object> mapss = new HashMap<String, Object>(); 
			DTOMap map = new DTOMap();
			String nmMenu="Pemeliharaan User";
			map.put("NM_MENU", nmMenu);				
			map.put("USERID_INPUT", authService.getUserDetails().getUserId());
			mapss.put("data", map);
			
			
			
			DialogUtil.showPopupDialogCloseOnly("/page/dialog/WndDialogAskSpv.zul",
					"Otorisasi Perubahaan " + nmMenu, getSelf(), new EventListener<Event>() {

						@Override
						public void onEvent(Event arg0) throws Exception,NullPointerException {
							
							String returnValue = (String) arg0.getData();								
							if (returnValue.equals("Berhasil")) {									
								//doReset();
								//doInsert();
								//cekBtnUpdate();
								doEdit(data);
							}else if(returnValue.equals("gagal")) {
								MessageBox.showInformation("Data batal Di Simpan");
							}else {
								MessageBox.showInformation("Data Tidak Bisa Di Simpan");
							}

						}
					}, mapss);

		} else {
			/*doInsert();
			cekBtnUpdate();*/
			doEdit(data);
		}
	}
	
	
	private void doEdit(DTOMap data){
		btnOverwrite.setDisabled(true);
		if (data != null){
			ComponentUtil.setValue(cmbRole, data.get("ROLEID"));
			ComponentUtil.setValue(cmbBranch, data.get("KD_CAB"));
			ComponentUtil.setValue(txtUserId, data.get("USERID"));
			ComponentUtil.setValue(txtUserNmUser, data.get("USERNM"));
			ComponentUtil.setValue(txtJmlFail, data.get("AMTFAIL"));
			ComponentUtil.setValue(txtLmtFail, data.get("LMTFAIL"));
			ComponentUtil.setValue(grpStatus, data.get("STATUS"));
			ComponentUtil.setValue(txtUserPPATK, data.get("USERID_PPATK"));
			ComponentUtil.setValue(grpOtorisasi, data.get("FLGSPV"));
			ComponentUtil.setValue(txtCabangKonsol, getNmCabangKonsol());
			
			onLoad = true;
			txtUserId.setDisabled(true);
			btnDelete.setDisabled(false);
			btnDelete.setVisible(true);
			btnOverwrite.setVisible(true);
			
			String kodeKantorPusat = cmbRole.getValue().substring(0, 2).trim();
			if ("01".equals(kodeKantorPusat)){
				txtUserPPATK.setDisabled(false);
			} else {
				txtUserPPATK.setDisabled(true);
			}
			
			btnOverwrite.setDisabled(false);
		}
	}
	
	private void doSearch(){
		DTOMap datas = new DTOMap();
		datas = masterService.getMapMaster("SELECT * FROM MST_USER WHERE USERID=?", new Object[]{ComponentUtil.getValue(txtUserId)});
		doEdit(datas);
	}
	private void doBeforeInsert() {
		if (isSpvActive()) {				
			Map<String, Object> mapss = new HashMap<String, Object>(); 
			DTOMap map = new DTOMap();
			String nmMenu="Pemeliharaan User";
			map.put("NM_MENU", nmMenu);				
			map.put("USERID_INPUT", authService.getUserDetails().getUserId());
			mapss.put("data", map);
			
			DialogUtil.showPopupDialog("/page/dialog/WndDialogAskSpv.zul", "Otorisasi Perubahaan " + nmMenu, getSelf(), PopupMode.OK_CLOSE, new EventListener<Event>() {

				@Override
				public void onEvent(Event arg0) throws Exception {
					String returnValue = (String) arg0.getData();
					if (returnValue.equals("Berhasil")) {
						//doReset();
						//MessageBox.showInformation("Data Berhasil Di Simpan");
						doInsert();
						cekBtnUpdate();
						doInsertWorkflow();
						doReset();
					}
					
				}
			}, new EventListener<Event>() {

				@Override
				public void onEvent(Event arg0) throws Exception {
					String returnValue = (String) arg0.getData();
					if(returnValue.equals("gagal")) {
						MessageBox.showInformation("Data batal Di Simpan");
						cekBtnUpdate();
						doInsertWorkflow();
						doReset();
					}else {
						MessageBox.showInformation("Data Tidak Bisa Di Simpan");
						cekBtnUpdate();
						doInsertWorkflow();
						doReset();					
					}
					
				}
			}, mapss);
			

			/*DialogUtil.showPopupDialogCloseOnly("/page/dialog/WndDialogAskSpv.zul",
					"Otorisasi Perubahaan " + nmMenu, getSelf(), new EventListener<Event>() {

						@Override
						public void onEvent(Event arg0) throws Exception {
							
							String returnValue = (String) arg0.getData();	
							if (returnValue.equals("Berhasil")) {
								doInsert();
								cekBtnUpdate();
								doInsertWorkflow();
								doReset();
							}else if(returnValue.equals("gagal")) {
								MessageBox.showInformation("Data batal Di Simpan");
							}else {
								MessageBox.showInformation("Data Tidak Bisa Di Simpan");
							}

						}
					}, mapss);*/

		} else {
			doInsert();
			cekBtnUpdate();
			doInsertWorkflow();
			doReset();
		}
	}
	
	
	private void doInsert(){
		try {
			DTOMap datas = new DTOMap();
			datas.put("USERID", ComponentUtil.getValue(txtUserId));
			log.info("USER ID : {}", ComponentUtil.getValue(txtUserId));
			if (!masterService.isExist(datas, "MST_USER")) {
				datas.put("ROLEID", ComponentUtil.getValue(cmbRole));
				datas.put("KD_CAB", ComponentUtil.getValue(cmbBranch));
				datas.put("USERNM", ComponentUtil.getValue(txtUserNmUser));
				datas.put("PWD", Cryptograph.MD5((String) ComponentUtil.getValue(txtUserId)));
				datas.put("AMTFAIL", ComponentUtil.getValue(txtJmlFail));
				datas.put("LMTFAIL", ComponentUtil.getValue(txtLmtFail));
				datas.put("STATUS", ComponentUtil.getValue(grpStatus));
				datas.put("CRTUSER", authService.getUserDetails().getUserId());
				datas.put("CRTDATE", new Date());
				datas.put("USERID_PPATK", ComponentUtil.getValue(txtUserPPATK));
				datas.put("FLGSPV", ComponentUtil.getValue(grpOtorisasi));
				masterService.insertData(datas, "MST_USER");
				Aksi = "Penambahan pada data USERID "+ ComponentUtil.getValue(txtUserId) +" "+ComponentUtil.getValue(txtUserNmUser);
				doLogAktfitas(Aksi);
				MessageBox.showInformation("Data berhasil di simpan.\nPassword sama dengan user id.");
			}else{
				MessageBox.showError("Data telah ada di database");
			}
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.showError(e.getMessage());
		}
	}
	
	private void doInsertWorkflow(){
		try {
			DTOMap datas = new DTOMap();
				datas.put("ROLEID", ComponentUtil.getValue(cmbRole));
				datas.put("KD_CAB", ComponentUtil.getValue(cmbBranch));
				
				if(ComponentUtil.getValue(cmbRole).equals("01")){
					datas.put("KD_TASK", "03");
					datas.put("NEXT_TASK", "69");
					datas.put("PREV_TASKID", "02");
				}else if(ComponentUtil.getValue(cmbRole).equals("02")){
					datas.put("KD_TASK", "02");
					datas.put("NEXT_TASK", "03");
					datas.put("PREV_TASKID", "01");
				}if(ComponentUtil.getValue(cmbRole).equals("03")){
					datas.put("KD_TASK", "01");
					datas.put("NEXT_TASK", "02");
					datas.put("PREV_TASKID", "00");
				}
				
				masterService.update("DELETE REF_WORKFLOW WHERE ROLEID=? AND KD_CAB=?", new Object[]{ComponentUtil.getValue(cmbRole),ComponentUtil.getValue(cmbBranch)});
				masterService.insertData(datas, "REF_WORKFLOW");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void doVisible() {
		btnDelete.setVisible(false);
		btnOverwrite.setVisible(false);
	}
	
	private void doCheckFirstUpdate() {
		if (isSpvActive()) {				
			Map<String, Object> mapss = new HashMap<String, Object>(); 
			DTOMap map = new DTOMap();
			String nmMenu="Pemeliharaan User";
			map.put("NM_MENU", nmMenu);				
			map.put("USERID_INPUT", authService.getUserDetails().getUserId());
			mapss.put("data", map);
			
			DialogUtil.showPopupDialog("/page/dialog/WndDialogAskSpv.zul", "Otorisasi Perubahaan " + nmMenu, getSelf(), PopupMode.OK_CLOSE, new EventListener<Event>() {

				@Override
				public void onEvent(Event arg0) throws Exception {
					String returnValue = (String) arg0.getData();
					if (returnValue.equals("Berhasil")) {
						//doReset();
						//MessageBox.showInformation("Data Berhasil Di Simpan");
						doUpdate();
						cekBtnUpdate();
						doInsertWorkflow();
						doReset();
						doVisible();
						
					}
					
				}
			}, new EventListener<Event>() {

				@Override
				public void onEvent(Event arg0) throws Exception {
					String returnValue = (String) arg0.getData();
					if(returnValue.equals("gagal")) {
						MessageBox.showInformation("Data batal Di Simpan");
						cekBtnUpdate();
						doInsertWorkflow();
						doReset();
						doVisible();
					}else {
						MessageBox.showInformation("Data Tidak Bisa Di Simpan");
						cekBtnUpdate();
						doInsertWorkflow();
						doReset();	
						doVisible();
					}
					
				}
			}, mapss);

			/*DialogUtil.showPopupDialogCloseOnly("/page/dialog/WndDialogAskSpv.zul",
					"Otorisasi Perubahaan " + nmMenu, getSelf(), new EventListener<Event>() {

						@Override
						public void onEvent(Event arg0) throws Exception {
							
							String returnValue = (String) arg0.getData();
							
							if (returnValue.equals("Berhasil")) {									
								//doReset();
								doUpdate();
								cekBtnUpdate();
								doInsertWorkflow();
								doReset();
							}else if(returnValue.equals("gagal")) {
								MessageBox.showInformation("Data batal Di Simpan");
							}else {
								MessageBox.showInformation("Data Tidak Bisa Di Simpan");
							}

						}
					}, mapss);*/

		} else {
			doUpdate();
			cekBtnUpdate();
			doInsertWorkflow();
			doReset();
			doVisible();
		}
	}
	
	private boolean doUpdate(){
		
			try {
				DTOMap datas = new DTOMap();
				datas.put("USERID", ComponentUtil.getValue(txtUserId));
				datas.put("KD_CAB", ComponentUtil.getValue(cmbBranch));
				datas.put("ROLEID", ComponentUtil.getValue(cmbRole));
				datas.put("USERNM", ComponentUtil.getValue(txtUserNmUser));
				datas.put("AMTFAIL", ComponentUtil.getValue(txtJmlFail));
				datas.put("LMTFAIL", ComponentUtil.getValue(txtLmtFail));
				datas.put("STATUS", ComponentUtil.getValue(grpStatus));
				datas.put("UPDUSER", authService.getUserDetails().getUserId());
				datas.put("UPDDATE", new Date());
				datas.put("USERID_PPATK", ComponentUtil.getValue(txtUserPPATK));
				datas.put("FLGSPV", ComponentUtil.getValue(grpOtorisasi));
				datas.put("PK", "USERID");
				masterService.updateData(datas, "MST_USER");
				Aksi = "Perubahan pad data USERID "+ ComponentUtil.getValue(txtUserId) +" "+ComponentUtil.getValue(txtUserNmUser);
				doLogAktfitas(Aksi);
				MessageBox.showInformation("Data berhasil di update");
			} catch (Exception e) {
				e.printStackTrace();
				MessageBox.showError(e.getMessage());
			}
			return true;
			
			
			/*Map<String, Object> mapss = new HashMap<String, Object>();
			DTOMap datas = new DTOMap();
			datas.put("USERID", ComponentUtil.getValue(txtUserId));
			datas.put("KD_CAB", ComponentUtil.getValue(cmbBranch));
			datas.put("ROLEID", ComponentUtil.getValue(cmbRole));
			datas.put("USERNM", ComponentUtil.getValue(txtUserNmUser));
			datas.put("AMTFAIL", ComponentUtil.getValue(txtJmlFail));
			datas.put("LMTFAIL", ComponentUtil.getValue(txtLmtFail));
			datas.put("STATUS", ComponentUtil.getValue(grpStatus));
			datas.put("UPDUSER", authService.getUserDetails().getUserId());
			datas.put("UPDDATE", new Date());
			datas.put("USERID_PPATK", ComponentUtil.getValue(txtUserPPATK));
			datas.put("PK", "USERID");
			
			String nmMenu="Pemeliharaan User";
			datas.put("NM_MENU", nmMenu);
			datas.put("MODUL", "Pemeliharaan User");
			mapss.put("data", datas);
			
			DialogUtil.showPopupDialogCloseOnly("/page/dialog/WndDialogAskSpv.zul", "Otorisasi Perubahaan "+nmMenu, getSelf(), new EventListener<Event>() {

				@Override
				public void onEvent(Event arg0) throws Exception {
					//doLoadData();	
					doInsertWorkflow();
					doReset();
				}
			}, mapss);
			
			doInsertWorkflow();
			doReset();*/
			
			
			/*masterService.updateData(datas, "MST_USER");
			MessageBox.showInformation("Data berhasil di update");*/
		
	}
	private void doBeforeDelete() {
		if (isSpvActive()) {				
			Map<String, Object> mapss = new HashMap<String, Object>(); 
			DTOMap map = new DTOMap();
			String nmMenu="Pemeliharaan User";
			map.put("NM_MENU", nmMenu);				
			map.put("USERID_INPUT", authService.getUserDetails().getUserId());
			mapss.put("data", map);
			
			DialogUtil.showPopupDialog("/page/dialog/WndDialogAskSpv.zul", "Otorisasi Perubahaan " + nmMenu, getSelf(), PopupMode.OK_CLOSE, new EventListener<Event>() {

				@Override
				public void onEvent(Event arg0) throws Exception {
					String returnValue = (String) arg0.getData();
					if (returnValue.equals("Berhasil")) {
						//doReset();
						doDelete();
						cekBtnUpdate();
						doInsertWorkflow();
						doReset();
						doVisible();						
					}
					
				}
			}, new EventListener<Event>() {

				@Override
				public void onEvent(Event arg0) throws Exception {
					String returnValue = (String) arg0.getData();
					if(returnValue.equals("gagal")) {
						MessageBox.showInformation("Data batal Di Simpan");
						cekBtnUpdate();
						doInsertWorkflow();
						doReset();
					}else {
						MessageBox.showInformation("Data Tidak Bisa Di Simpan");	
						cekBtnUpdate();
						doInsertWorkflow();
						doReset();
					}
					
				}
			}, mapss);
			
			

			/*DialogUtil.showPopupDialogCloseOnly("/page/dialog/WndDialogAskSpv.zul",
					"Otorisasi Perubahaan " + nmMenu, getSelf(), new EventListener<Event>() {

						@Override
						public void onEvent(Event arg0) throws Exception {
							
							String returnValue = (String) arg0.getData();								
							if (returnValue.equals("Berhasil")) {									
								//doReset();
								doDelete();
								cekBtnUpdate();
							}else if(returnValue.equals("gagal")) {
								MessageBox.showInformation("Data batal Di Simpan");
							}else {
								MessageBox.showInformation("Data Tidak Bisa Di Simpan");
							}

						}
					}, mapss);*/

		} else {
			doDelete();
			cekBtnUpdate();
			doVisible();
			
		}
	}
	
	
	private void doDelete(){
		try {
			DTOMap datas = new DTOMap();
			datas.put("USERID", ComponentUtil.getValue(txtUserId));
			datas.put("PK", "USERID");
			masterService.deleteData(datas, "MST_USER");
			Aksi = "Penghapusan pada data USERID "+ ComponentUtil.getValue(txtUserId) +" "+ComponentUtil.getValue(txtUserNmUser);
			doLogAktfitas(Aksi);
			MessageBox.showInformation("Data berhasil di hapus");
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.showError(e.getMessage());
		}
		doReset();
	}
	
	private void getDataByUserNm(){
		System.out.println("cabang kd ---- "+ComponentUtil.getValue(cmbBranch)+" }} "+cmbBranch.getSelectedIndex());
		String where = "";
		if(ComponentUtil.getValue(cmbBranch) == null || ComponentUtil.getValue(cmbBranch).equals("") || cmbBranch.getSelectedIndex()==0){
			where = "";
		}else{
			where = " WHERE A.KD_CAB = '"+ComponentUtil.getValue(cmbBranch)+"' ";
		}
		
		try {
			List<DTOMap> listData = masterService.getDataMaster("SELECT A.*, B.NM_CAB, C.ROLENM "
					+ " FROM MST_USER A"
					+ " LEFT OUTER JOIN CFG_CABANG B ON A.KD_CAB=B.KD_CAB AND B.TGL_POS IN(SELECT MAX(TGL_POS) FROM CFG_CABANG) "
					+ " LEFT OUTER JOIN CFG_ROLE C ON A.ROLEID=C.ROLEID"+where, new Object[]{});
			doRefreshTable(listData);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private void doReset(){
		ComponentUtil.clear(wnd);
		getDataByUserNm();
//		list.getItems().clear();
		txtUserNmUser.setValue("");
		onLoad = false;
		txtUserId.setDisabled(false);
		btnDelete.setDisabled(true);
		txtUserId.setFocus(true);
		ComponentUtil.setValue(txtJmlFail, 0);
		ComponentUtil.setValue(txtLmtFail, 9);
		ComponentUtil.setValue(cmbRole, "03");
	}
	
	private void doRefreshTable(List<DTOMap> datas){
		list.getItems().clear();
		if (datas != null && datas.size() > 0){
			for(DTOMap dtoResult : datas){
				Listitem item = new Listitem();
				item.setAttribute("DATA", dtoResult);
				item.appendChild(new Listcell(dtoResult.getString("USERID")));
				item.appendChild(new Listcell(dtoResult.getString("USERNM")));
				item.appendChild(new Listcell(dtoResult.getString("ROLENM")));
				item.appendChild(new Listcell(dtoResult.getString("NM_CAB")));
				list.appendChild(item);
			}
		}
	}
	
	private boolean doValidation(){
		if (ComponentUtil.getValue(txtUserId) == null || ComponentUtil.getValue(txtUserId).equals("")) {
			throw new WrongValueException(txtUserId, "User id harus diisi.");
		}else if (ComponentUtil.getValue(txtUserNmUser) == null || ComponentUtil.getValue(txtUserNmUser).equals("")) {
			throw new WrongValueException(txtUserNmUser, "Username harus diisi.");
		}else if (ComponentUtil.getValue(cmbBranch) == null || ComponentUtil.getValue(cmbBranch).equals("")) {
			throw new WrongValueException(cmbBranch, "Cabang harus diisi.");
		}else if (ComponentUtil.getValue(cmbRole) == null || ComponentUtil.getValue(cmbRole).equals("")) {
			throw new WrongValueException(cmbRole, "Role id harus diisi.");
		}else if (ComponentUtil.getValue(txtUserPPATK) == null && "01".equals(cmbRole.getValue().substring(0, 2).trim())){
			throw new WrongValueException(txtUserPPATK, "ID User PPATK harus diisi.");
		}else if (ComponentUtil.getValue(txtJmlFail) == null || ComponentUtil.getValue(txtJmlFail).equals("")) {
			throw new WrongValueException(txtJmlFail, "Jumlah Toleransi harus diisi.");
		}else if (ComponentUtil.getValue(txtLmtFail) == null || ComponentUtil.getValue(txtLmtFail).equals("")) {
			throw new WrongValueException(txtLmtFail, "Batas Toleransi harus diisi.");
		}
		return true;
	}
	
	private String getNmCabangKonsol(){
		String result = "";
		
		DTOMap dto = (DTOMap)masterService.getMapMaster("SELECT * FROM CFG_CABANG "
				+ "											WHERE KD_CAB=(SELECT KD_CAB_KONSOL "
				+ "															FROM CFG_CABANG "
				+ "															WHERE KD_CAB=? AND TGL_POS IN (SELECT MAX(TGL_POS) FROM CFG_CABANG))"
				+ "													AND TGL_POS IN (SELECT MAX(TGL_POS) FROM CFG_CABANG)", new Object[]{ComponentUtil.getValue(cmbBranch)});

		if(dto!=null){
			result=dto.getString("KD_CAB_KONSOL")+" - "+dto.getString("NM_CAB");
		}
		
		return result;
	}
	
	private void doResetPassword(){
		try { 
			if(ComponentUtil.getValue(txtUserId)!=null){
				DTOMap dto = new DTOMap();
				dto.put("USERID", ComponentUtil.getValue(txtUserId));
				dto.put("PWD", Cryptograph.MD5(txtUserId.getValue()));
				dto.put("PK", "USERID");
				masterService.updateData(dto, "MST_USER");
				MessageBox.showInformation("Password berhasil direset, Password sesuai dengan User ID yang anda entry");
				doReset();
			}
		} catch (Exception ex) {
			MessageBox.showError(ex.getMessage());
		}
	}
	
}
