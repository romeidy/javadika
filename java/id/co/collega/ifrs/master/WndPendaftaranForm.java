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
import org.zkoss.zul.Comboitem;
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
public class WndPendaftaranForm extends SelectorComposer<Component>{
	
	@Wire Window wnd;
	@Wire Textbox txtFormId;
	@Wire Textbox txtFormNm;
	@Wire Textbox txtZulFile;
	@Wire Listbox list;
	@Wire Button btnSave;
	@Wire Button btnDelete;
	@Wire Button btnReset;
	
	@Autowired MasterServices masterService;
	@Autowired AuthenticationService authService;
	
	Boolean onLoad = false;
	
	String Aksi;
	
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		txtFormId.addEventListener(Events.ON_OK, new EventListener() {
            public void onEvent(Event event) throws Exception {
        		doSearch();
            }
        });
		
		txtFormNm.addEventListener(Events.ON_OK, new EventListener() {
            public void onEvent(Event event) throws Exception {
        		doSearchByName();
            }
        });
		
		txtZulFile.addEventListener(Events.ON_OK, new EventListener() {
            public void onEvent(Event event) throws Exception {
        		doSearchByZul();
            }
        });
		
		list.addEventListener(Events.ON_DOUBLE_CLICK, new EventListener() {
            public void onEvent(Event event) throws Exception {
        		doEdit();
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
		cekBtnUpdate();
		doReset();
	}
	
	private void cekBtnUpdate() {
		if (authService.getUserDetails().getActiveRole().equals("05")) {
			btnSave.setDisabled(true);
		} else {
			btnSave.setDisabled(false);
		}	
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

	
	public void doSave(){
		if (doValidation()) {
			if(onLoad){
				if (checkPrivUpdate()) {
					doBeforeUpdate();
				}
				//doUpdate();
			}else{
				if (checkPrivInsert()) {
					doBeforeInsert();	
				}
				//doInsert();
			}
		}
	}
	private void doBeforeDelete() {
		if (isSpvActive()) {
			Map<String, Object> mapss = new HashMap<String, Object>(); 
			DTOMap map = new DTOMap();
			String nmMenu="Pemeliharaan Pendaftaran Form";
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
						doDelete();	
						cekBtnUpdate();						
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
			cekBtnUpdate();						
			doReset();	
		}
	}
	
	private void doBeforeInsert() {
		if (isSpvActive()) {
			Map<String, Object> mapss = new HashMap<String, Object>(); 
			DTOMap map = new DTOMap();
			String nmMenu="Pemeliharaan Pendaftaran Form";
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
						doReset();
					
					}else {
						MessageBox.showInformation("Data Tidak Bisa Di Simpan");
						cekBtnUpdate();						
						doReset();						
					}
					
				}
			}, mapss);
		}else {
			doInsert();
			cekBtnUpdate();						
			doReset();	
		}
	}
	
	private void doBeforeUpdate() {
		if (isSpvActive()) {
			Map<String, Object> mapss = new HashMap<String, Object>(); 
			DTOMap map = new DTOMap();
			String nmMenu="Pemeliharaan Pendaftaran Form";
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
						doReset();
					
					}else {
						MessageBox.showInformation("Data Tidak Bisa Di Simpan");
						cekBtnUpdate();						
						doReset();						
					}
					
				}
			}, mapss);
		}else {
			doUpdate();	
			cekBtnUpdate();						
			doReset();	
		}
		
	}
	
	
	private void doEdit(){
		Listitem item = list.getSelectedItem();
		if (item != null){
			DTOMap data = (DTOMap)item.getAttribute("DATA");
			ComponentUtil.setValue(txtFormId, data.get("FORM_ID"));
			ComponentUtil.setValue(txtFormNm, data.get("NAMA"));
			ComponentUtil.setValue(txtZulFile, data.get("ZUL_FILE"));
			onLoad = true;
			txtFormId.setDisabled(true);
			btnDelete.setDisabled(false);
		}
	}
	
	private void doSearch(){
		DTOMap datas = new DTOMap();
		datas.put("FORM_ID", ComponentUtil.getValue(txtFormId));
		doRefreshTable(masterService.getDataMasterLikeById(datas, "SYS_FORM"));
	}
	
	private void doSearchByName(){
		DTOMap datas = new DTOMap();
		datas.put("NAMA", ComponentUtil.getValue(txtFormNm));
		doRefreshTable(masterService.getDataMasterLikeById(datas, "SYS_FORM"));
	}
	
	private void doSearchByZul(){
		DTOMap datas = new DTOMap();
		datas.put("ZUL_FILE", ComponentUtil.getValue(txtZulFile));
		doRefreshTable(masterService.getDataMasterLikeById(datas, "SYS_FORM"));
	}
	
	private void doInsert(){
		try {
			DTOMap datas = new DTOMap();
			datas.put("FORM_ID", ComponentUtil.getValue(txtFormId));
			if (!masterService.isExist(datas, "SYS_FORM")) {
				datas.put("NAMA", ComponentUtil.getValue(txtFormNm));
				datas.put("ZUL_FILE", ComponentUtil.getValue(txtZulFile));
				masterService.insertData(datas, "SYS_FORM");
				Aksi = "Penambahan pada data FORM ID"+ ComponentUtil.getValue(txtFormId) +", "+ComponentUtil.getValue(txtFormNm);
				doLogAktfitas(Aksi);
				MessageBox.showInformation("Data berhasil di simpan");
			}else{
				MessageBox.showError("Data telah ada di database");
			}
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.showError(e.getMessage());
		}
		doReset();
	}
	
	private void doUpdate(){
		try {
			DTOMap datas = new DTOMap();
			datas.put("FORM_ID", ComponentUtil.getValue(txtFormId));
			datas.put("NAMA", ComponentUtil.getValue(txtFormNm));
			datas.put("ZUL_FILE", ComponentUtil.getValue(txtZulFile));
			datas.put("PK", "FORM_ID");
			masterService.updateData(datas, "SYS_FORM");
			Aksi = "Perubahan pada data FORM ID"+ ComponentUtil.getValue(txtFormId) +", "+ComponentUtil.getValue(txtFormNm);
			doLogAktfitas(Aksi);
			MessageBox.showInformation("Data berhasil di update");
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.showError(e.getMessage());
		}
		doReset();
	}
	
	private void doDelete(){
		try {
			
			final DTOMap datas = new DTOMap();
			datas.put("FORM_ID", ComponentUtil.getValue(txtFormId));
			datas.put("PK", "FORM_ID");			
			masterService.deleteData(datas, "SYS_FORM");
			Aksi = "Penghapusan pada data FORM ID "+ ComponentUtil.getValue(txtFormId) +", "+ComponentUtil.getValue(txtFormNm);
			doLogAktfitas(Aksi);
			MessageBox.showInformation("Data berhasil di hapus");
			
			/*Messagebox.show("Apakah Anda Yakin mau menghapus data ini .. ?", "KONFIRMASI", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, new EventListener<Event>() {

				@Override
				public void onEvent(Event e) throws Exception {
					if (Messagebox.ON_OK.equals(e.getName())) {
						masterService.deleteData(datas, "SYS_FORM");
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
	
	private void doReset(){
		ComponentUtil.clear(wnd);
		doRefreshTable(masterService.getDataMasterOrderById("FORM_ID ", "SYS_FORM", false));
		ComponentUtil.setValue(txtFormId, getFormID());
		
		onLoad = false;
		txtFormId.setDisabled(true);
		btnDelete.setDisabled(true);
		txtFormNm.setFocus(true);
	}
	
	private void doRefreshTable(List<DTOMap> datas){
		list.getItems().clear();
		if (datas != null && datas.size() > 0){
			for(DTOMap dtoResult : datas){
				Listitem item = new Listitem();
				item.setAttribute("DATA", dtoResult);
				item.appendChild(new Listcell(dtoResult.getString("FORM_ID")));
				item.appendChild(new Listcell(dtoResult.getString("NAMA")));
				item.appendChild(new Listcell(dtoResult.getString("ZUL_FILE")));
				list.appendChild(item);
			}
		}
	}
	
	private String getFormID(){
		try {
			DTOMap result =(DTOMap) masterService.getMapMaster("SELECT max(CAST(FORM_ID AS INTEGER))+1 AS FORM_ID FROM SYS_FORM WHERE FORM_ID NOT IN('999','988','987','986','985')", new Object[]{});
			return result.get("FORM_ID").toString();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("FORM ID KOSONG");
			return null;
		}
	}
	
	private boolean doValidation(){
		if (ComponentUtil.getValue(txtFormId) == null || ComponentUtil.getValue(txtFormId).equals("")) {
			throw new WrongValueException(txtFormId, "Form id harus diisi.");
		}
		return true;
	}
}
