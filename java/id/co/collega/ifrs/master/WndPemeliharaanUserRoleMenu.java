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
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treechildren;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Treerow;
import org.zkoss.zul.Window;

import id.co.collega.ifrs.common.DTOMap;
import id.co.collega.ifrs.common.JdbcTemplate;
import id.co.collega.ifrs.master.service.MasterServices;
import id.co.collega.ifrs.util.ComponentUtil;
import id.co.collega.ifrs.util.MessageBox;
import id.co.collega.v7.seed.config.AuthenticationService;
import id.co.collega.v7.ui.component.DialogUtil;
import id.co.collega.v7.ui.component.DialogUtil.PopupMode;

@org.springframework.stereotype.Component
@Scope("execution")
public class WndPemeliharaanUserRoleMenu extends SelectorComposer<Component>{
	@Wire Window wnd;
	@Wire Combobox cmbRoleId;
	@Wire Combobox cmbUserId;
	@Wire Tree tree;
	@Wire Button btnSave;
	@Wire Button btnDelete;
	@Wire Button btnReset;
	
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
            	doSearchByRole();
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
		cekBtnUpdate();
	}
	
	private void cekBtnUpdate() {
		if (authService.getUserDetails().getActiveRole().equals("05")) {
			btnSave.setDisabled(true);
		} else {
			btnSave.setDisabled(false);
		}
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
		tree.clear();
		
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
			if (isSpvActive()) {
				Map<String, Object> mapss = new HashMap<String, Object>(); 
				DTOMap map = new DTOMap();
				String nmMenu="Pemeliharaan Role User";
				map.put("NM_MENU", nmMenu);				
				map.put("USERID_INPUT", authService.getUserDetails().getUserId());
				mapss.put("data", map);
				
				DialogUtil.showPopupDialog("/page/dialog/WndDialogAskSpv.zul", "Otorisasi Perubahaan " + nmMenu, getSelf(), PopupMode.OK_CLOSE, new EventListener<Event>() {
					@Override
					public void onEvent(Event arg0) throws Exception {
						String returnValue = (String) arg0.getData();
						if (returnValue.equals("Berhasil")) {
							doSave();
							cekBtnUpdate();
						}
						
					}
				}, new EventListener<Event>() {

					@Override
					public void onEvent(Event arg0) throws Exception {
						String returnValue = (String) arg0.getData();
						if(returnValue.equals("gagal")) {
							MessageBox.showInformation("Data batal Di Simpan");
							doReset();
						}else {
							MessageBox.showInformation("Data Tidak Bisa Di Simpan");
							doReset();
						}
						
					}
				}, mapss);
			}else {
				doSave();
				cekBtnUpdate();
			}
		}
	}
	
	private void doSave(){
			DTOMap datas = new DTOMap();
			datas.put("ROLEID", ComponentUtil.getValue(cmbRoleId));
			datas.put("USERID", ComponentUtil.getValue(cmbUserId));
			datas.put("PK", "USERID, ROLEID");
			masterService.deleteData(datas, "CFG_USER_ROLE_MENU ");
			
			for (Object obj : tree.getItems()) {
				Treeitem item = (Treeitem) obj;
				if (item.isSelected()) {
					datas = new DTOMap();
					datas.put("USERID", ComponentUtil.getValue(cmbUserId));
					datas.put("ROLEID", ComponentUtil.getValue(cmbRoleId));
					datas.put("MENUID", (String)item.getValue());
					masterService.insertData(datas, "CFG_USER_ROLE_MENU");
				}
			}
			MessageBox.showInformation("Data berhasil di simpan");
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
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void doSearchByUserId(){

		if (ComponentUtil.getValue(cmbUserId) != null) {
			String user = (String) ComponentUtil.getValue(cmbUserId);	
			if (user != null) {				
				List dataUserRole = jt2
						.query("SELECT * FROM CFG_USER_ROLE_MENU WHERE USERID = ? ",
								new Object[] { user}, new DTOMap());
				
				if (dataUserRole != null && dataUserRole.size() > 0) {
					tree.getChildren().clear();					
					Treechildren root = new Treechildren();

					System.out.println("ada data" + dataUserRole.size());
					List<DTOMap> listMenu = masterService.getMenuItemsByUser((String) ComponentUtil.getValue(cmbUserId));
					
					HashMap mapTree = new HashMap();
					for (DTOMap menuMap : listMenu) {
						Treeitem item = new Treeitem();
						item.addEventListener(Events.ON_CLICK, new EventListener() {
							public void onEvent(Event e) throws Exception {
								doCheck((Treeitem)e.getTarget());
							}
						});
						Treerow rowTree = new Treerow();
						
						mapTree.put(menuMap.getString("MENU_ID"), item);
						item.setValue(menuMap.getString("MENU_ID"));
						item.appendChild(rowTree);
						item.setAttribute("MENU_ID", menuMap.getString("MENU_ID"));
						rowTree.appendChild(new Treecell(menuMap.getString("MENU_ID")+" - "+menuMap.getString("NAME")));
						if(!menuMap.getString("PICK").equals("0")){
							item.setSelected(true);
						} else {
							item.setSelected(false);					
						}

						if (menuMap.get("MENU_PARENT") != null) {
							Treeitem itemParent = (Treeitem) mapTree.get(menuMap.getString("MENU_PARENT"));
							if (itemParent==null) continue;
							Treechildren child = (Treechildren) itemParent.getAttribute("PARENT");
							if (child == null) {
								child = new Treechildren();
								itemParent.appendChild(child);
								itemParent.setAttribute("PARENT", child);
							}
							child.appendChild(item);
						} else {
							root.appendChild(item);
						}
					tree.appendChild(root);				
					}
				} else {
					doSearchByRole();
					System.out.println("tidak ada data");
				}		
			}	
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void doSearchByRole(){
		tree.getChildren().clear();
		Treechildren root = new Treechildren();
		
			List<DTOMap> listMenu = masterService.getMenuItemsByRole((String) ComponentUtil.getValue(cmbRoleId));
			HashMap mapTree = new HashMap();

				for (DTOMap menuMap : listMenu) {
					Treeitem item = new Treeitem();
					item.addEventListener(Events.ON_CLICK, new EventListener() {
						public void onEvent(Event e) throws Exception {
							doCheck((Treeitem)e.getTarget());
						}
					});
					Treerow rowTree = new Treerow();
					
					mapTree.put(menuMap.getString("MENU_ID"), item);
					item.setValue(menuMap.getString("MENU_ID"));
					item.appendChild(rowTree);
					item.setAttribute("MENU_ID", menuMap.getString("MENU_ID"));
					rowTree.appendChild(new Treecell(menuMap.getString("MENU_ID")+" - "+menuMap.getString("NAME")));
					if(!menuMap.getString("PICK").equals("0")){
						item.setSelected(true);
					} else {
						item.setSelected(false);
					}
					if (menuMap.get("MENU_PARENT") != null) {
						Treeitem itemParent = (Treeitem) mapTree.get(menuMap.getString("MENU_PARENT"));
						if (itemParent==null) continue;
						Treechildren parent = (Treechildren) itemParent.getAttribute("PARENT");
						if (parent == null) {
							parent = new Treechildren();
							itemParent.appendChild(parent);
							itemParent.setAttribute("PARENT", parent);
						}
						parent.appendChild(item);
					} else {
						root.appendChild(item);
					}
				tree.appendChild(root);
			}	
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void doCheck(Treeitem item){
		List childrens = new ArrayList();
		childrens.add(item.getChildren());
		for (int i = 0; i < childrens.size(); i++) {
			List children=(List)childrens.get(i);
			for (Object obj : (List)children) {
				if (obj instanceof Treeitem){
					((Treeitem)obj).setSelected(item.isSelected());
					childrens.add(((Treeitem)obj).getChildren());
				} else if (obj instanceof Treechildren){
					childrens.add(((Treechildren)obj).getChildren());
				}
			}
		}
		
		if (item.isSelected()){
			Component cmp = item.getParent();
			while(cmp != null){
				if (cmp instanceof Treeitem)
					((Treeitem)cmp).setSelected(true);
				cmp=cmp.getParent();
			}
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
		tree.clear();
		cmbRoleId.setSelectedIndex(-1);
		cekBtnUpdate();
	}
	
}
