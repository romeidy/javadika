package id.co.collega.ifrs.master;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.jet.gand.services.GlobalVariable;

import id.co.collega.ifrs.common.DTOMap;
import id.co.collega.ifrs.master.service.MasterServices;
import id.co.collega.ifrs.util.ComponentUtil;
import id.co.collega.ifrs.util.MessageBox;
import id.co.collega.v7.seed.config.AuthenticationService;
import id.co.collega.v7.ui.component.DialogUtil;
import id.co.collega.v7.ui.component.DialogUtil.PopupMode;

@org.springframework.stereotype.Component
@Scope("execution")
public class WndPemeliharaanWewenang extends SelectorComposer<Component>{
	@Wire Window wnd;
	@Wire Textbox txtRoleId;
	@Wire Textbox txtKeteranganWewenang;
	@Wire Listbox list;
	@Wire Button btnSave;
	@Wire Button btnDelete;
	@Wire Button btnReset;
	@Wire Combobox cmbRoleSpv;
	
	@Autowired MasterServices masterService;
	@Autowired AuthenticationService authService;
	
	Boolean onLoad = false;
	
	String Aksi;
	
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		txtRoleId.addEventListener(Events.ON_OK, new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
        		doSearch();
            }
        });
		
		
		list.addEventListener(Events.ON_DOUBLE_CLICK, new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
        		doEdit();
            }
        });
		
		btnSave.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
        		doSave();
            }
        });
		
		btnDelete.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
        		//doDelete();
            	if (checkPrivDelete()) {
                	doBeforeDelete();	
				}
            }
        });
		
		btnReset.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
        		doReset();
            }
        });
		
		doReset();
		cekBtnUpdate();
	}
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
	
	
	private void doSave(){
		if (doValidation()) {
			if(onLoad){
				if (checkPrivUpdate()) {
					doBeforeUpdate();	
				}
				//doUpdate();
			}else{
				if (checkPrivInsert()) {
					doBoforeInsert();	
				}
				//doInsert();
			}
		}
	}
	
	private void doBoforeInsert() {
		if (isSpvActive()) {
			Map<String, Object> mapss = new HashMap<String, Object>(); 
			DTOMap map = new DTOMap();
			String nmMenu="Pemeliharaan Wewenang";
			map.put("NM_MENU", nmMenu);				
			map.put("USERID_INPUT", authService.getUserDetails().getUserId());
			mapss.put("data", map);
			
			DialogUtil.showPopupDialog("/page/dialog/WndDialogAskSpv.zul", "Otorisasi Perubahaan " + nmMenu, getSelf(), PopupMode.OK_CLOSE, new EventListener<Event>() {

				@Override
				public void onEvent(Event arg0) throws Exception {
					String returnValue = (String) arg0.getData();
					if (returnValue.equals("Berhasil")) {
						save();	
						doReset();
						cekBtnUpdate();
					}
					
				}
			}, new EventListener<Event>() {

				@Override
				public void onEvent(Event arg0) throws Exception {
					String returnValue = (String) arg0.getData();
					if(returnValue.equals("gagal")) {
						MessageBox.showInformation("Data batal Di Simpan");
						cekBtnUpdate();						
						doReset();
						
					}else {
						MessageBox.showInformation("Data Tidak Bisa Di Simpan");
						cekBtnUpdate();
						doReset();
					}
					
				}
			}, mapss);
		}else {
			save();	
			doReset();
			cekBtnUpdate();
		}
	}
	
	private void doBeforeUpdate() {
		if (isSpvActive()) {
			Map<String, Object> mapss = new HashMap<String, Object>(); 
			DTOMap map = new DTOMap();
			String nmMenu="Pemeliharaan Wewenang";
			map.put("NM_MENU", nmMenu);				
			map.put("USERID_INPUT", authService.getUserDetails().getUserId());
			mapss.put("data", map);
			
			DialogUtil.showPopupDialog("/page/dialog/WndDialogAskSpv.zul", "Otorisasi Perubahaan " + nmMenu, getSelf(), PopupMode.OK_CLOSE, new EventListener<Event>() {

				@Override
				public void onEvent(Event arg0) throws Exception {
					String returnValue = (String) arg0.getData();
					if (returnValue.equals("Berhasil")) {
						save();	
						doReset();
						cekBtnUpdate();
					}
					
				}
			}, new EventListener<Event>() {

				@Override
				public void onEvent(Event arg0) throws Exception {
					String returnValue = (String) arg0.getData();
					if(returnValue.equals("gagal")) {
						MessageBox.showInformation("Data batal Di Simpan");
						cekBtnUpdate();						
						doReset();
						
					}else {
						MessageBox.showInformation("Data Tidak Bisa Di Simpan");
						cekBtnUpdate();
						doReset();
					}
					
				}
			}, mapss);
		}else {
			save();	
			doReset();
			cekBtnUpdate();
		}
	}
	
	private void save() {
		DTOMap datas = new DTOMap();
		datas.put("ROLEID", ComponentUtil.getValue(txtRoleId));
		if(masterService.isExist(datas, "CFG_ROLE")) {
			datas.put("PK", "ROLEID");
			datas.put("ROLENM", ComponentUtil.getValue(txtKeteranganWewenang));
			datas.put("ROLESPV", ComponentUtil.getValue(cmbRoleSpv));
			masterService.updateData(datas, "CFG_ROLE");
			Aksi = "Perubahan data Role Id"+ ComponentUtil.getValue(txtRoleId) +", "+ComponentUtil.getValue(txtKeteranganWewenang);
			doLogAktfitas(Aksi);
			MessageBox.showInformation("Data Berhasil di Ubah");
		}else {
			datas.put("ROLENM", ComponentUtil.getValue(txtKeteranganWewenang) );
			datas.put("ROLESPV", ComponentUtil.getValue(cmbRoleSpv) );
			masterService.insertData(datas, "CFG_ROLE");
			Aksi = "Penambahan data Role Id"+ ComponentUtil.getValue(txtRoleId) +", "+ComponentUtil.getValue(txtKeteranganWewenang);
			doLogAktfitas(Aksi);
			MessageBox.showInformation("Data Berhasil Di Simpan");
		}
	}
	
	private void doEdit(){
		Listitem item = list.getSelectedItem();
		if (item != null){
			DTOMap data = (DTOMap)item.getAttribute("DATA");
			ComponentUtil.setValue(txtRoleId, data.get("ROLEID"));
			ComponentUtil.setValue(txtKeteranganWewenang, data.get("ROLENM"));
			ComponentUtil.setValue(cmbRoleSpv, data.get("ROLESPV"));
			onLoad = true;
			txtRoleId.setDisabled(true);
			btnDelete.setDisabled(false);
		}
	}
	
	private void doSearch(){
		DTOMap datas = new DTOMap();
		datas.put("ROLEID", ComponentUtil.getValue(txtRoleId));
		doRefreshTable(masterService.getDataMasterById(datas, "CFG_ROLE"));
	}
	
	/*private void doInsert(){
		try {
			Map<String, Object> mapss = new HashMap<String, Object>();
			DTOMap datas = new DTOMap();
			datas.put("ROLEID", ComponentUtil.getValue(txtRoleId));
			//if (!masterService.isExist(datas, "CFG_ROLE")) {
				datas.put("ROLENM", ComponentUtil.getValue(txtKeteranganWewenang));
				datas.put("ROLESPV", ComponentUtil.getValue(cmbRoleSpv));
				String nmMenu="Pemeliharaan Wewenang";
				datas.put("NM_MENU", nmMenu);
				datas.put("MODUL", "Pemeliharaan Wewenang");
				mapss.put("data", datas);
				
				DialogUtil.showPopupDialogCloseOnly("/page/dialog/WndDialogAskSpv.zul", "Otorisasi Perubahaan "+nmMenu, getSelf(), new EventListener<Event>() {

					@Override
					public void onEvent(Event arg0) throws Exception {
						//doLoadData();	
						//doInsertWorkflow();
						doReset();
					}
				}, mapss);
				
				//masterService.insertData(datas, "CFG_ROLE");
				//MessageBox.showInformation("Data berhasil di simpan");
				
			}else{
				MessageBox.showError("Data telah ada di database");
			}
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.showError(e.getMessage());
		}
		doReset();
	}*/
	
	//private void doUpdate(){
		/*try {
			Map<String, Object> mapss = new HashMap<String, Object>();
			DTOMap datas = new DTOMap();
			datas.put("ROLEID", ComponentUtil.getValue(txtRoleId));
			datas.put("ROLENM", ComponentUtil.getValue(txtKeteranganWewenang));
			datas.put("ROLESPV", ComponentUtil.getValue(cmbRoleSpv));
			datas.put("PK", "ROLEID");
			String nmMenu="Pemeliharaan Wewenang";
			datas.put("NM_MENU", nmMenu);
			datas.put("MODUL", "Pemeliharaan Wewenang");
			mapss.put("data", datas);
			DialogUtil.showPopupDialogCloseOnly("/page/dialog/WndDialogAskSpv.zul", "Otorisasi Perubahaan "+nmMenu, getSelf(), new EventListener<Event>() {

				@Override
				public void onEvent(Event arg0) throws Exception {
					//doLoadData();	
					//doInsertWorkflow();
					doReset();
				}
			}, mapss);
			

			//masterService.updateData(datas, "CFG_ROLE");
			//MessageBox.showInformation("Data berhasil di update");
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.showError(e.getMessage());
		}*/
		//doReset();
	//}
	private void doBeforeDelete() {
		if (isSpvActive()) {
			Map<String, Object> mapss = new HashMap<String, Object>(); 
			DTOMap map = new DTOMap();
			String nmMenu="Delete  Pemeliharaan Wewenang";
			map.put("NM_MENU", nmMenu);				
			map.put("USERID_INPUT", authService.getUserDetails().getUserId());
			mapss.put("data", map);
			
			DialogUtil.showPopupDialog("/page/dialog/WndDialogAskSpv.zul", "Otorisasi Perubahaan " + nmMenu, getSelf(), PopupMode.OK_CLOSE, new EventListener<Event>() {

				@Override
				public void onEvent(Event arg0) throws Exception {
					String returnValue = (String) arg0.getData();
					if (returnValue.equals("Berhasil")) {
						doDelete();
						doReset();
						cekBtnUpdate();
					}
					
				}
			}, new EventListener<Event>() {

				@Override
				public void onEvent(Event arg0) throws Exception {
					String returnValue = (String) arg0.getData();
					if(returnValue.equals("gagal")) {
						MessageBox.showInformation("Data batal Di Simpan");
						cekBtnUpdate();						
						doReset();
						
					}else {
						MessageBox.showInformation("Data Tidak Bisa Di Simpan");
						cekBtnUpdate();
						doReset();
					}
					
				}
			}, mapss);
		}else {
			doDelete();
			doReset();
			cekBtnUpdate();
		}
	}
	
	
	private void doDelete(){
		try {
			final DTOMap datas = new DTOMap();
			datas.put("ROLEID", ComponentUtil.getValue(txtRoleId));
			datas.put("PK", "ROLEID");
			masterService.deleteData(datas, "CFG_ROLE");
			Aksi = "Penghapusan data Role Id"+ ComponentUtil.getValue(txtRoleId) +", "+ComponentUtil.getValue(txtKeteranganWewenang);
			doLogAktfitas(Aksi);
			MessageBox.showInformation("Data berhasil di hapus");
			
			
			
			/*Messagebox.show("Apakah Anda Yakin mau menghapus data ini .. ?", "KONFIRMASI", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, new EventListener<Event>() {

				@Override
				public void onEvent(Event e) throws Exception {
					if (Messagebox.ON_OK.equals(e.getName())) {
						masterService.deleteData(datas, "CFG_ROLE");
						doReset();
					}else if(Messagebox.ON_CANCEL.equals(e.getName())){
						doReset();
					}
					
				}
				
			});	*/
			
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.showError(e.getMessage());
		}
		//doReset();
	}
	
	private void doReset(){//
		ComponentUtil.clear(wnd);
		doRefreshTable(masterService.getDataMaster("CFG_ROLE"));
		onLoad = false;
		txtRoleId.setDisabled(false);
		btnDelete.setDisabled(true);
		txtRoleId.setFocus(true);
		cmbRoleSpv.setSelectedIndex(0);
	}
	
	private void doRefreshTable(List<DTOMap> datas){
		list.getItems().clear();
		if (datas != null && datas.size() > 0){
			for(DTOMap dtoResult : datas){
				Listitem item = new Listitem();
				item.setAttribute("DATA", dtoResult);
				item.appendChild(new Listcell(dtoResult.getString("ROLEID")));
				item.appendChild(new Listcell(dtoResult.getString("ROLENM")));
				item.appendChild(new Listcell(dtoResult.getString("ROLESPV")));
				list.appendChild(item);
			}
		}
	}
	
	private boolean doValidation(){
		if (ComponentUtil.getValue(txtRoleId) == null || ComponentUtil.getValue(txtRoleId).equals("")) {
			throw new WrongValueException(txtRoleId, "Role ID harus diisi.");
		}else if (ComponentUtil.getValue(txtKeteranganWewenang) == null || ComponentUtil.getValue(txtKeteranganWewenang).equals("")) {
			throw new WrongValueException(txtKeteranganWewenang, "Keterangan harus diisi.");
		}else if(ComponentUtil.getValue(cmbRoleSpv)==null || ComponentUtil.getValue(cmbRoleSpv).equals("")) {
			throw new WrongValueException(cmbRoleSpv, "Role SuperVision harus di isi.");
		}
		return true;
	}
}
