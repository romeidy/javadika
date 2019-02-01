package id.co.collega.ifrs.master;

import java.util.Collections;
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
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treechildren;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Treerow;
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
public class WndPemeliharaanMenu extends SelectorComposer<Component>{
	@Wire Window wnd;
	@Wire Textbox txtMenuId;
	@Wire Textbox txtMenuNm;
	@Wire Textbox txtFormId;
	@Wire Textbox txtFormNm;
	@Wire Textbox txtParentId;
	@Wire Textbox txtParentNm;
	@Wire Intbox txtSeq;
	@Wire Textbox txtParameters;
	@Wire Tree tree;
	@Wire Button btnSearch;
	@Wire Button btnSave;
	@Wire Button btnDelete;
	@Wire Button btnReset;
	
	@Autowired MasterServices masterService;
	@Autowired AuthenticationService authService;
	
	Boolean onLoad = false;
	
	String Aksi;
	
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		txtFormId.addEventListener(Events.ON_OK, new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
        		doSearchForm();
            }
        });
		
		txtParentId.addEventListener(Events.ON_OK, new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
        		doSearchParent();
            }
        });
		
		tree.addEventListener(Events.ON_DOUBLE_CLICK, new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
        		doEdit();
            }
        });
		
		btnSearch.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
        		doSearch();
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
		cekBtnUpdate();
		doReset();
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
					doBeforeInsert();	
				}
				//doInsert();
			}
		}
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
	private void doBeforeUpdate() {
		if (isSpvActive()) {
			Map<String, Object> mapss = new HashMap<String, Object>(); 
			DTOMap map = new DTOMap();
			String nmMenu="Pemeliharaan Pemeliharaan Menu";
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
	
	
	private void doEdit(){
		Tree trees = tree;
		Treeitem item = trees.getSelectedItem();
		if (item != null){
			String menuId = ((String)item.getLabel()).split("-")[0];
			DTOMap datas = new DTOMap();
			datas.put("MENU_ID", menuId);
			DTOMap menus = masterService.getMapMasterById(datas, "CFG_MENU");
			if (menus != null) {
				ComponentUtil.setValue(txtMenuId, menus.get("MENU_ID"));
				ComponentUtil.setValue(txtMenuNm, menus.get("NAME"));
				ComponentUtil.setValue(txtFormId, menus.get("FORM_ID"));
				doSearchForm();
				ComponentUtil.setValue(txtParentId, menus.get("MENU_PARENT"));
				doSearchParent();
				ComponentUtil.setValue(txtSeq, menus.get("SEQ"));
				ComponentUtil.setValue(txtParameters, menus.get("PARAM"));
				
				onLoad = true;
				txtMenuId.setDisabled(true);
				btnDelete.setDisabled(false);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void doSearch(){
		DialogUtil.showPopupDialog("/page/dialog/WndDialogBrowseForm.zul", "Browse Form", getSelf(), DialogUtil.PopupMode.OK_CLOSE, new EventListener<Event>() {
			public void onEvent(Event event) throws Exception {
				DTOMap map = (DTOMap) event.getData();
				if (map != null && !map.map.isEmpty()) {
					ComponentUtil.setValue(txtFormId, ((DTOMap)event.getData()).getString("FORM_ID"));
					doSearchForm();
				}
            }
		}, null, Collections.EMPTY_MAP);
	}
	
	private void doSearchForm(){
		if (ComponentUtil.getValue(txtFormId) != null && !ComponentUtil.getValue(txtFormId).equals("")) {
			DTOMap datas = new DTOMap();
			datas.put("FORM_ID", ComponentUtil.getValue(txtFormId));
			DTOMap result = masterService.getMapMasterById(datas, "SYS_FORM");
			if (result != null) {
				ComponentUtil.setValue(txtFormNm, result.get("NAMA"));
			}
		}
	}
	
	private void doSearchParent(){
		if (ComponentUtil.getValue(txtParentId) != null && !ComponentUtil.getValue(txtParentId).equals("")) {
			DTOMap datas = new DTOMap();
			datas.put("MENU_ID", ComponentUtil.getValue(txtParentId));
			DTOMap result = masterService.getMapMasterById(datas, "CFG_MENU");
			if (result != null) {
				ComponentUtil.setValue(txtParentNm, result.get("NAME"));
			}
		}
	}
	
	private void doInsert(){
		try {
			DTOMap datas = new DTOMap();
			datas.put("MENU_ID", ComponentUtil.getValue(txtMenuId));
			if (!masterService.isExist(datas, "CFG_MENU")) {
				datas.put("NAME", ComponentUtil.getValue(txtMenuNm));
				datas.put("FORM_ID", ComponentUtil.getValue(txtFormId));
				datas.put("MENU_PARENT", ComponentUtil.getValue(txtParentId));
				datas.put("SEQ", ComponentUtil.getValue(txtSeq));
				datas.put("PARAM", ComponentUtil.getValue(txtParameters));
				masterService.insertData(datas, "CFG_MENU");
				Aksi = "Penambahan data Menu Id "+ ComponentUtil.getValue(txtMenuId) +", "+ComponentUtil.getValue(txtMenuNm);
				doLogAktfitas(Aksi);
				MessageBox.showInformation("Data berhasil di simpan");
			}else{
				MessageBox.showError("Data telah ada di database");
			}
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.showError(e.getMessage());
		}
//		doReset();
	}
	
	private void doUpdate(){
		try {
			DTOMap datas = new DTOMap();
			datas.put("MENU_ID", ComponentUtil.getValue(txtMenuId));
			datas.put("NAME", ComponentUtil.getValue(txtMenuNm));
			datas.put("FORM_ID", ComponentUtil.getValue(txtFormId));
			datas.put("MENU_PARENT", ComponentUtil.getValue(txtParentId));
			datas.put("SEQ", ComponentUtil.getValue(txtSeq));
			datas.put("PARAM", ComponentUtil.getValue(txtParameters));
			datas.put("PK", "MENU_ID");
			masterService.updateData(datas, "CFG_MENU");
			Aksi = "Perubahan data Menu Id "+ ComponentUtil.getValue(txtMenuId) +", "+ComponentUtil.getValue(txtMenuNm);
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
			datas.put("MENU_ID", ComponentUtil.getValue(txtMenuId));
			datas.put("PK", "MENU_ID");
			masterService.deleteData(datas, "CFG_MENU");
			MessageBox.showInformation("Data berhasil di hapus");
			Aksi = "Penghapusan data Menu Id "+ ComponentUtil.getValue(txtMenuId) +", "+ComponentUtil.getValue(txtMenuNm);
			doLogAktfitas(Aksi);
			/*Messagebox.show("Apakah Anda Yakin mau menghapus data ini .. ?", "KONFIRMASI", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, new EventListener<Event>() {

				@Override
				public void onEvent(Event e) throws Exception {
					if (Messagebox.ON_OK.equals(e.getName())) {
						masterService.deleteData(datas, "CFG_PENGUMUMAN");
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
		doReset();
	}
	
	private void doReset(){
		ComponentUtil.clear(wnd);
		onLoad = false;
		txtMenuId.setDisabled(false);
		btnDelete.setDisabled(true);
		doRefreshTable(masterService.getDataMasterOrderById("MENU_ID", "CFG_MENU", true));
		txtMenuId.setFocus(true);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void doRefreshTable(List<DTOMap> datas){
		tree.getChildren().clear();
		Treechildren root = new Treechildren();
		HashMap mapTree = new HashMap();
		for (DTOMap menuMap : datas) {
			Treeitem item = new Treeitem();
			Treerow rowTree = new Treerow();
			mapTree.put(menuMap.getString("MENU_ID"), item);
			item.appendChild(rowTree);
			item.setAttribute("MENU_ID", menuMap.getString("MENU_ID"));
			rowTree.appendChild(new Treecell(menuMap.getString("MENU_ID")+" - "+menuMap.getString("NAME")+" - "+menuMap.getInt("SEQ")));
			if (menuMap.get("MENU_PARENT") != null) {
				Treeitem itemParent = (Treeitem) mapTree.get(menuMap.getString("MENU_PARENT"));
				Treechildren parent = (Treechildren) itemParent.getAttribute("PARENT");
				if (parent == null) {
					parent = new Treechildren();
					itemParent.appendChild(parent);
					itemParent.setOpen(false);
					itemParent.setAttribute("PARENT", parent);
				}
				parent.appendChild(item);
			} else {
				root.appendChild(item);
			}
		}
		tree.appendChild(root);
	}
	
	private boolean doValidation(){
		if (ComponentUtil.getValue(txtMenuId) == null || ComponentUtil.getValue(txtMenuId).equals("")) {
			throw new WrongValueException(txtMenuId, "Menu id harus diisi.");
		}else if (ComponentUtil.getValue(txtMenuNm) == null || ComponentUtil.getValue(txtMenuNm).equals("")) {
			throw new WrongValueException(txtMenuNm, "Nama menu harus diisi.");
		}
		return true;
	}
}
