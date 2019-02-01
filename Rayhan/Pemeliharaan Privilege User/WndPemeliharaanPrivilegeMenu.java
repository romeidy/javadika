package id.co.collega.ifrs.master;


import java.util.ArrayList;
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
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treechildren;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Treerow;
import org.zkoss.zul.Window;

import id.co.collega.ifrs.common.DTOMap;
import id.co.collega.ifrs.common.FunctionUtils;
import id.co.collega.ifrs.common.JdbcTemplate;
import id.co.collega.ifrs.master.service.MasterServices;
import id.co.collega.ifrs.util.ComponentUtil;
import id.co.collega.ifrs.util.MessageBox;
import id.co.collega.v7.seed.config.AuthenticationService;
import id.co.collega.v7.ui.component.DialogUtil;
import id.co.collega.v7.ui.component.DialogUtil.PopupMode;

@org.springframework.stereotype.Component
@Scope("execution")
public class WndPemeliharaanPrivilegeMenu extends SelectorComposer<Component>{
	@Wire Window wnd;
	@Wire Combobox cmbRoleId;
	@Wire Combobox cmbUserId;

	@Wire Button btnSave;
	@Wire Button btnDelete;
	@Wire Button btnReset;
	
	@Wire Listbox listMenu;
	
	@Autowired MasterServices masterService;
	@Autowired AuthenticationService authService;
	
	@Autowired JdbcTemplate jt2;
	
	Boolean onLoad = false;
	
	
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		cmbRoleId.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
//        		doSearch();
            	doLoadUserId();
//            	doSearchByRole();
            }
        });

		cmbUserId.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
        		doSearchByUserId();
            }
        });
		
		btnSave.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
        		//doSave();
            	doBeforeSave();
            }
        });
		
		btnReset.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
        		doReset();
            }
        });
            
		doLoadRoleId();
		doReset();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void doSearchByUserId(){
		if (ComponentUtil.getValue(cmbUserId) != null) {
			String user = (String) ComponentUtil.getValue(cmbUserId);	
			if (user != null) {				
					List<DTOMap> listPrivMenu = masterService.getPrivItemsByUser((String) ComponentUtil.getValue(cmbUserId));
					if (listPrivMenu != null) {
						listMenu.getItems().clear();
						
						for (DTOMap cfg_user_priv_menu : listPrivMenu) {							
							int flgInsert = 0;
							int flgUpdate = 0;
							int flgDelete = 0;
							
							Listitem item = new Listitem();
							final Listcell lcInsert = new Listcell();
							final Listcell lcUpdate= new Listcell();
							final Listcell lcDelete= new Listcell();

							final Checkbox cbInsert = new Checkbox();
							final Checkbox cbUpdate = new Checkbox();							
							final Checkbox cbDelete = new Checkbox();
							
							if (cfg_user_priv_menu.getInt("FLGINSERT") != null) {
								flgInsert =  cfg_user_priv_menu.getInt("FLGINSERT");								
							} 
							if (cfg_user_priv_menu.getInt("FLGUPDATE") != null) {
								flgUpdate = cfg_user_priv_menu.getInt("FLGUPDATE");								
							} 
							if (cfg_user_priv_menu.getInt("FLGDELETE") != null) {
								flgDelete = cfg_user_priv_menu.getInt("FLGDELETE");
							}
							
							item.setAttribute("DATA", cfg_user_priv_menu);
							item.appendChild(new Listcell(cfg_user_priv_menu.getString("MENUID")));
							item.appendChild(new Listcell(cfg_user_priv_menu.getString("NAME")));
							
							if (flgInsert == 0) {
								cbInsert.setChecked(false);
								lcInsert.setValue(0);
							} else {
								cbInsert.setChecked(true);
								lcInsert.setValue(1);
							}
							cbInsert.setParent(lcInsert);							
							cbInsert.addEventListener(Events.ON_CHECK, new EventListener<Event>() {
					            public void onEvent(Event event) throws Exception {
					        		if(cbInsert.isChecked()){
					        			lcInsert.setValue(1);
					        		} else {
					        			lcInsert.setValue(0);
					        		}
					            }
					        });
							item.appendChild(lcInsert);
							

							if (flgUpdate == 0) {
								cbUpdate.setChecked(false);
								lcUpdate.setValue(0);
							} else {
								cbUpdate.setChecked(true);
								lcUpdate.setValue(1);
							}
							cbUpdate.setParent(lcUpdate);
							cbUpdate.addEventListener(Events.ON_CHECK, new EventListener<Event>() {
					            public void onEvent(Event event) throws Exception {
					        		if(cbUpdate.isChecked()){
					        			lcUpdate.setValue(1);
					        		} else {
					        			lcUpdate.setValue(0);
					        		}
					            }
					        });
							item.appendChild(lcUpdate);
							
							if (flgDelete == 0) {
								cbDelete.setChecked(false);
								lcDelete.setValue(0);
							} else {
								cbDelete.setChecked(true);
								lcDelete.setValue(1);
							}
							cbDelete.setParent(lcDelete);
							cbDelete.addEventListener(Events.ON_CHECK, new EventListener<Event>() {
					            public void onEvent(Event event) throws Exception {
					        		if(cbDelete.isChecked()){
					        			lcDelete.setValue(1);
					        		} else {
					        			lcDelete.setValue(0);
					        		}
					            }
					        });
							item.appendChild(lcDelete);
							
							listMenu.appendChild(item);
						}
					} else {
						System.out.println("Tidak ada data");
					}
			}	
			}	
	}

	private void doSave(){
		int flgInsert = 0;
		int flgUpdate = 0;
		int flgDelete = 0;
		
		DTOMap datas = new DTOMap();
		datas.put("USERID", ComponentUtil.getValue(cmbUserId));
		datas.put("PK", "USERID");
		masterService.deleteData(datas, "CFG_USER_PRV_MENU ");
		
		for (Object obj : listMenu.getItems()) {
			Listitem item = (Listitem) obj;

			DTOMap data = (DTOMap) item.getAttribute("DATA");	
			if(data != null) {
				
				datas = new DTOMap();
				datas.put("USERID", ComponentUtil.getValue(cmbUserId));
				datas.put("MENUID", data.get("MENUID"));
				
//				Cara untuk mengambil value dari cell ke-x. 
				for (Object cell : ((Listitem) item).getChildren()) {
					if(((Listcell) cell).getColumnIndex()==2){
						flgInsert = ((Listcell) cell).getValue();	
					}
					if(((Listcell) cell).getColumnIndex()==3){
						flgUpdate = ((Listcell) cell).getValue();	
					}
					if(((Listcell) cell).getColumnIndex()==4){
						flgDelete = ((Listcell) cell).getValue();
					}
				}
				datas.put("FLGINSERT", flgInsert);
				datas.put("FLGUPDATE", flgUpdate);
				datas.put("FLGDELETE", flgDelete);
				masterService.insertData(datas, "CFG_USER_PRV_MENU");				
			}
		}
		MessageBox.showInformation("Data berhasil di simpan");
}
	
	
	private void doLoadRoleId() {
		cmbRoleId.getItems().clear();
		cmbRoleId.setSelectedIndex(-1);
		List<DTOMap> listWewenang = masterService.getDataMaster("CFG_ROLE");
		for (DTOMap wwn : listWewenang) {
			Comboitem item = new Comboitem();
			item.setLabel(wwn.getString("ROLEID") + " - " + wwn.getString("ROLENM"));
			item.setValue(wwn.getString("ROLEID"));
			cmbRoleId.appendChild(item);
		}
	}
	
	private void doLoadUserId() {				
		cmbUserId.getItems().clear();
		cmbUserId.setValue(null);
		
		String roleId = (String) ComponentUtil.getValue(cmbRoleId);
		List<DTOMap> listUserId = (List<DTOMap>) masterService.getDataMaster(" SELECT USERID,USERNM from MST_USER WHERE ROLEID = ?"
								,new Object[]{roleId});
		
		for (DTOMap map : listUserId) {
				Comboitem item = new Comboitem();
				item.setLabel(map.getString("USERID") + " - " + map.getString("USERNM"));
				item.setValue(map.getString("USERID"));
				cmbUserId.appendChild(item);
			}
	}
	
	private void doBeforeSave() {		
		if (doValidation()) {
			Messagebox.show("Apakah Anda Yakin menyimpan data ini .. ?",
					"KONFIRMASI", Messagebox.OK | Messagebox.CANCEL,
					Messagebox.QUESTION, new EventListener<Event>() {
						@Override
						public void onEvent(Event e) throws Exception {
							if (Messagebox.ON_OK.equals(e.getName())) {
								doSave();
//								cekBtnUpdate();
							}  else if (Messagebox.ON_CANCEL.equals(e.getName())) {
								
							}
						}
				});
			}
		}
		
	private boolean doValidation() {
		if (ComponentUtil.getValue(cmbRoleId) == null
				|| ComponentUtil.getValue(cmbRoleId).equals("")) {
			throw new WrongValueException(cmbRoleId,
					"Role Id harus dipilih.");
		} else if (ComponentUtil.getValue(cmbUserId) == null
				|| ComponentUtil.getValue(cmbUserId).equals("")) {
			throw new WrongValueException(cmbUserId,
					"User Id harus dipilih.");
		}		
		return true;
	}
	
	private void doReset(){
		ComponentUtil.clear(wnd);
		listMenu.getItems().clear();
//		tree.clear();
		cmbRoleId.setSelectedIndex(-1);
	}
}
