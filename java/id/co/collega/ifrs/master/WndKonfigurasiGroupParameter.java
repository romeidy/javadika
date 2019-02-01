package id.co.collega.ifrs.master;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;

import id.co.collega.v7.seed.controller.SelectorComposer;

import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
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
public class WndKonfigurasiGroupParameter extends SelectorComposer<Component> {

	@Wire Window wnd;
	@Wire Listbox listbox;
	@Wire Intbox txtKdGroup;
	@Wire Textbox txtKeterangan;
	
	@Wire Button btnTambah;
	@Wire Button btnEdit;
	@Wire Button btnDelete;
	@Wire Button btnSimpan;
	@Wire Button btnReset;
	
	@Autowired MasterServices masterService;
	@Autowired AuthenticationService authService;
	
	static String model = "";
	
	private boolean onLoad = false;
	
	String Aksi;

	public void doSaveOrUpdate() {
		if (validation()) {	
			if (onLoad){
				if (checkPrivUpdate()) {
					Messagebox.show("Apakah Anda Yakin mau mengupdate data ini .. ?", "KONFIRMASI",
							Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, new EventListener<Event>() {
								@Override
								public void onEvent(Event e) throws Exception {
									if (Messagebox.ON_OK.equals(e.getName())) {
										doBeforeSave();
									} else if (Messagebox.ON_CANCEL.equals(e.getName())) {
										
									}
								}
							});	
				}
			}else{
				if (checkPrivInsert()) {

					Messagebox.show("Apakah Anda Yakin mau menyimpan data ini .. ?", "KONFIRMASI",
							Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, new EventListener<Event>() {
								@Override
								public void onEvent(Event e) throws Exception {
									if (Messagebox.ON_OK.equals(e.getName())) {
										doBeforeSave();	
									} else if (Messagebox.ON_CANCEL.equals(e.getName())) {
										
									}
								}
							});
				}
			}
		}
	}

	public void doSave() {
		Integer parm = (Integer) ComponentUtil.getValue(txtKdGroup);
		if (parm != null) {
			DTOMap key = (DTOMap) masterService.getMapMaster("select PARMGRP from REF_PARM where PARMGRP=?", new Object[] { parm });
			if (key != null){
				throw new WrongValueException(txtKdGroup, "Kode Grup Sudah Ada");
			}else{
				DTOMap map = new DTOMap();
				map.put("PARMGRP", ComponentUtil.getValue(txtKdGroup));
				map.put("GRPNM", ComponentUtil.getValue(txtKeterangan));
				masterService.insertData(map, "REF_PARM");
				Aksi = "Penambahan data "+ ComponentUtil.getValue(txtKdGroup) +", "+ComponentUtil.getValue(txtKeterangan);
				doLogAktfitas(Aksi);
				MessageBox.showInformation("Data Berhasil Disimpan");
				doReset();
			}
		}
	}

	public void doUpdate() {
		DTOMap map = new DTOMap();
		map.put("PARMGRP", ComponentUtil.getValue(txtKdGroup));
		map.put("GRPNM", ComponentUtil.getValue(txtKeterangan));
		map.put("PK", "PARMGRP");
		masterService.updateData(map, "REF_PARM");
		masterService.insertData(map, "REF_PARM");
		Aksi = "Perubahan pad data "+ ComponentUtil.getValue(txtKdGroup) +", "+ComponentUtil.getValue(txtKeterangan);
		doLogAktfitas(Aksi);
		MessageBox.showInformation("Data Berhasil Diupdate");
		doReset();
	}

	public void doResetTotal() {
		doRefreshTable();
		doReset();
		txtKdGroup.setDisabled(false);
		txtKdGroup.setFocus(true);
	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		doRefreshTable();
		txtKdGroup.addEventListener(Events.ON_OK, new EventListener<Event>() {
			public void onEvent(Event e) throws Exception {
				Integer parm = (Integer) ComponentUtil.getValue(txtKdGroup);
				if (parm != null) {
					DTOMap key = (DTOMap) masterService.getMapMaster("select PARMGRP from REF_PARM where PARMGRP=?",
							new Object[] { parm });
					if (key != null)
						throw new WrongValueException(txtKdGroup, "Kode Grup Sudah Ada");

				}
			}
		});
		txtKdGroup.addEventListener(Events.ON_BLUR, new EventListener<Event>() {
			public void onEvent(Event e) throws Exception {
				Integer parm = (Integer) ComponentUtil.getValue(txtKdGroup);
				if (parm != null) {

					DTOMap key = (DTOMap) masterService.getMapMaster("select PARMGRP from REF_PARM where PARMGRP=?",
							new Object[] { parm });
					if (key != null)
						throw new WrongValueException(txtKdGroup, "Kode Grup Sudah Ada");

				}
			}
		});
		btnEdit.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(Event e) throws Exception {
				doEdit();
			}
		});
		btnDelete.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(Event e) throws Exception {
				doBeforeDelete();
			}
		});

		btnTambah.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(Event e) throws Exception {
				doResetTotal();
			}
		});
		listbox.addEventListener(Events.ON_DOUBLE_CLICK, new EventListener<Event>() {
			public void onEvent(Event e) throws Exception {
				doEdit();
			}
		});
		btnSimpan.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(Event e) throws Exception {
				doSaveOrUpdate();
				
			}
		});
		btnReset.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(Event e) throws Exception {
				doResetTotal();
			}
		});
	}

	public void doReset() {
		doRefreshTable();
		ComponentUtil.setValue(txtKdGroup, null);
		ComponentUtil.setValue(txtKeterangan, null);
		txtKdGroup.setDisabled(false);
		((Button)btnDelete).setDisabled(true);
		btnSimpan.setLabel("Save");
		onLoad = false;
	}

	public void doEdit() {
		Listitem item = listbox.getSelectedItem();
		if (item != null) {
			DTOMap data = (DTOMap) item.getAttribute("DATA");
			ComponentUtil.setValue(txtKdGroup, data.getInt("PARMGRP"));
			ComponentUtil.setValue(txtKeterangan, data.getString("GRPNM"));
			onLoad = true;
			txtKdGroup.setDisabled(true);
			((Button)btnDelete).setDisabled(false);
			btnSimpan.setLabel("Update");
			Textbox txt = (Textbox) txtKeterangan;
			txt.setFocus(true);
		}
	}

	public void doDelete() {
		final Listitem item = listbox.getSelectedItem();
		DTOMap data = (DTOMap) item.getAttribute("DATA");
		data.put("PK", "PARMGRP");
		masterService.deleteData(data, "REF_PARM");
		Aksi = "Penghapusan data "+ ComponentUtil.getValue(txtKdGroup) +" "+ComponentUtil.getValue(txtKeterangan);
		doLogAktfitas(Aksi);
		item.detach();
		MessageBox.showInformation("Data Berhasil Dihapus");
		doReset();
	}

	public void doRefreshTable() {
		Integer parmgrp = (Integer) ComponentUtil.getValue(txtKdGroup);
		List listParm = masterService.getDataMaster("Select PARMGRP, GRPNM From REF_PARM ORDER BY PARMGRP,GRPNM", new Object[] {});
		listbox.getItems().clear();
		if (listParm.size() > 0) {
			
			for (Object o : listParm) {
				DTOMap parm = (DTOMap) o;
				Listitem item = new Listitem();
				item.setAttribute("DATA", parm);
				item.appendChild(new Listcell(parm.getInt("PARMGRP").toString()));
				item.appendChild(new Listcell(parm.getString("GRPNM")));
				listbox.appendChild(item);
			}
		}
	}

	public boolean validation() {
		boolean isValid = true;
		if (isValid) {
			List wrongValue = new ArrayList(0);
			Integer parmgrp = (Integer) ComponentUtil.getValue(txtKdGroup);
			if (parmgrp == null) {
				wrongValue.add(new WrongValueException(txtKdGroup, "Kode Grup Harus Diisi"));
			}
			String grpnm = (String) ComponentUtil.getValue(txtKeterangan);
			if (grpnm == null) {
				wrongValue.add(new WrongValueException(txtKeterangan, "Keterangan Harus Diisi"));
			}
			if (wrongValue.size() > 0) {
				throw new WrongValuesException(
						(WrongValueException[]) wrongValue.toArray(new WrongValueException[wrongValue.size()]));
			}
		}
		return isValid;
	}
	private void doBeforeDelete(){

		Messagebox.show("Apakah Anda Yakin mau menghapus data ini .. ?", "KONFIRMASI",
			Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, new EventListener<Event>() {
				@Override
				public void onEvent(Event e) throws Exception {
					if (Messagebox.ON_OK.equals(e.getName())) {
						Map<String, Object> mapss = new HashMap<String, Object>(); 
						DTOMap map = new DTOMap();
						String nmMenu="Konfigurasi group paramter";
						map.put("NM_MENU", nmMenu);
						mapss.put("data", map);
						
						DialogUtil.showPopupDialog("/page/dialog/WndDialogValidasiUser.zul", "Otorisasi Perubahaan " + nmMenu, getSelf(), PopupMode.OK_CLOSE, new EventListener<Event>() {

							@Override
							public void onEvent(Event arg0) throws Exception {
								String returnValue = (String) arg0.getData();
								if (returnValue.equals("Berhasil")) {
									if (onLoad) {
										doDelete();
									}
								}
								
							}
						}, null, mapss);
					} else if (Messagebox.ON_CANCEL.equals(e.getName())) {
						
					}

				}

			});
	}
	private void doBeforeSave() {
		if (validation()) {
			Map<String, Object> mapss = new HashMap<String, Object>(); 
			DTOMap map = new DTOMap();
			String nmMenu="Konfigurasi group paramter";
			map.put("NM_MENU", nmMenu);
			mapss.put("data", map);
			
			DialogUtil.showPopupDialog("/page/dialog/WndDialogValidasiUser.zul", "Otorisasi Perubahaan "+ nmMenu, getSelf(), PopupMode.OK_CLOSE, new EventListener<Event>() { 

				@Override
				public void onEvent(Event arg0) throws Exception {
					String returnValue = (String) arg0.getData();
					if (returnValue.equals("Berhasil")) {
						if (onLoad) {
							doUpdate();
						}else{
							doSave();
						}
					}
					
				}
			}, null, mapss);	
		}
	}
	/*public void afterSave() {
		DTOMap map = (DTOMap) tblColumnsData.get("REF_PARM");
		DTOMap logActivty = new DTOMap();
		logActivty.put("USERID", dataSession.userMap.get("USERID"));
		logActivty.put("MENUID", "UM003100");
		logActivty.put("DATE", new Date());
		logActivty.put("BRANCHID", dataSession.userMap.get("BRANCHID"));
		logActivty.put("DESCRIPTION", "Membuat Pemeliharaan Grup Parameter dengan Kode Grup " + map.get("PARMGRP"));
		getJdbcTemplate().insertData(logActivty, "LOG_ACTIVITY");

	}

	public void afterUpdate() {
		DTOMap map = (DTOMap) tblColumnsData.get("REF_PARM");
		DTOMap logActivty = new DTOMap();
		logActivty.put("USERID", dataSession.userMap.get("USERID"));
		logActivty.put("MENUID", "UM003100");
		logActivty.put("DATE", new Date());
		logActivty.put("BRANCHID", dataSession.userMap.get("BRANCHID"));
		logActivty.put("DESCRIPTION", "Mengubah Pemeliharaan Grup Parameter dengan Kode Grup " + map.get("PARMGRP"));
		getJdbcTemplate().insertData(logActivty, "LOG_ACTIVITY");
	}*/
}
