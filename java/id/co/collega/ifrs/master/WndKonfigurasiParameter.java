package id.co.collega.ifrs.master;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.zkoss.chart.Charts;
import org.zkoss.chart.Legend;
import org.zkoss.chart.PlotLine;
import org.zkoss.chart.model.CategoryModel;
import org.zkoss.chart.model.DefaultCategoryModel;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Chart;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Div;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.jet.gand.services.GlobalVariable;

import id.co.collega.ifrs.common.DTOMap;
import id.co.collega.ifrs.common.JdbcTemplate;
import id.co.collega.ifrs.master.service.MasterServices;
import id.co.collega.ifrs.util.ComponentUtil;
import id.co.collega.ifrs.util.MessageBox;
import id.co.collega.v7.ef.common.DataSession;
import id.co.collega.v7.seed.config.AuthenticationService;
import id.co.collega.v7.ui.component.DialogUtil;
import id.co.collega.v7.ui.component.DialogUtil.PopupMode;

@org.springframework.stereotype.Component
@Scope("execution")
public class WndKonfigurasiParameter extends SelectorComposer<Component> {
	

    @Autowired AuthenticationService auth;
    
    @Wire Div moldingPaging;
    
    @Wire Combobox cmbKodeGrup;
    
    @Wire Textbox txtKodeParameter;
    @Wire Textbox txtCatatan;
    @Wire Textbox txtDeskripsi;
    @Wire Textbox txtKodeLain;
    @Wire Textbox txtCatatanLainLain;
    
    @Wire Intbox intNoUrut;
    @Wire Intbox insertPage;
    @Wire Listbox listbox;
    
    
    @Wire Button btnTambah;
    @Wire Button btnEdit;
    @Wire Button btnDelete;
    @Wire Button btnSimpan;
    @Wire Button btnReset;
    
    @Wire Button firstPage;
    @Wire Button previous;
    @Wire Button next;
    @Wire Button lastPage;
    
    @Wire Label perData;
    @Wire Label perPage;
    @Wire Label allData;
    
    @Wire Radiogroup radioStatus;
    
    @Wire Window wnd;
    
    @Wire Button btnExport;

    private List<DTOMap> listdto;

    @Autowired MasterServices masterService;
    
    public DataSession dataSession;
    
    @Autowired JdbcTemplate jt2;

	private boolean onLoad=false;
	private Integer manyRow;
	private Integer page;
	public List listAllRow=new ArrayList<>();
	private DTOMap CFG_SYS=(DTOMap) GlobalVariable.getInstance().get("cfgsys");
	private DTOMap USER_MASTER=(DTOMap) GlobalVariable.getInstance().get("USER_MASTER");

    @Override
	public void doAfterCompose(Component comp) throws Exception  {
		super.doAfterCompose(comp);
		
		((Textbox)txtKodeParameter).setDisabled(true);
		((Textbox)txtKodeLain).setDisabled(true);
		((Textbox)txtDeskripsi).setDisabled(true);
		((Textbox)txtCatatan).setDisabled(true);
		((Intbox)intNoUrut).setDisabled(true);
		((Textbox)txtCatatanLainLain).setDisabled(true);
		
		loadREF_PARM();
		
		txtKodeParameter.addEventListener(Events.ON_OK,new EventListener<Event>(){
			public void onEvent(Event e) throws Exception {
				Integer parmgrup=(Integer) ComponentUtil.getValue(cmbKodeGrup);
				String parmid=(String)ComponentUtil.getValue(txtKodeParameter);
				if(parmgrup!=null){
					if(parmid!=null){
						DTOMap key=(DTOMap)jt2.queryObject("select PARMGRP, PARMID from CFG_PARM where PARMGRP=? AND PARMID=?", new Object[]{parmgrup,parmid}, new DTOMap());
						if (key!=null)
							throw new WrongValueException(txtKodeParameter, "Kode Sandi Sudah Ada. Entry Kode Sandi yang Lain");
					}
				}	
			}
		});
		txtKodeParameter.addEventListener(Events.ON_BLUR,new EventListener<Event>(){
			public void onEvent(Event e) throws Exception {
				Integer parmgrup=(Integer) ComponentUtil.getValue(cmbKodeGrup);
				String parmid=(String)ComponentUtil.getValue(txtKodeParameter);
				if(parmgrup!=null){
					if(parmid!=null){
						DTOMap key=(DTOMap)jt2.queryObject("select PARMGRP, PARMID from CFG_PARM where PARMGRP=? AND PARMID=?", new Object[]{parmgrup,parmid}, new DTOMap());
						if (key!=null)
							throw new WrongValueException(txtKodeParameter, "Kode Sandi Sudah Ada. Entry Kode Sandi yang Lain");
					}
				}	
			}
		});
		listbox.addEventListener(Events.ON_DOUBLE_CLICK,new EventListener<Event>(){
			public void onEvent(Event e) throws Exception {
				doEdit();
			}
		});
		btnEdit.addEventListener(Events.ON_CLICK,new EventListener<Event>(){
			public void onEvent(Event e) throws Exception {
				doEdit();
			}
		});
		btnDelete.addEventListener(Events.ON_CLICK,new EventListener<Event>(){
			public void onEvent(Event e) throws Exception {
				doBeforeDelete();
			}
		});
		cmbKodeGrup.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {
			public void onEvent(Event arg0) throws Exception {
				doRefreshTable();
			}
		});
		btnTambah.addEventListener(Events.ON_CLICK,new EventListener<Event>(){
			public void onEvent(Event e) throws Exception {
				doTambah();
			}
		});
		btnSimpan.addEventListener(Events.ON_CLICK,new EventListener<Event>(){
			public void onEvent(Event e) throws Exception {
				doSaveOrUpdate();
			}
		});
		btnReset.addEventListener(Events.ON_CLICK,new EventListener<Event>(){
			public void onEvent(Event e) throws Exception {
				doResetTotal();
			}
		});
		manyRow=20;
		page=1;
		firstPage.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(Event e) throws Exception {
//				Listbox listbox = (Listbox) listbox;
				listbox.getItems().clear();
				((Button)previous).setDisabled(true);
				((Button)firstPage).setDisabled(true);
				((Button)next).setDisabled(false);
				((Button)lastPage).setDisabled(false);
				page = 1;
				showListPerPage(listbox,1,page*manyRow);
				ComponentUtil.setValue(perData,"[ 1 - "+String.valueOf(page*manyRow));
			}
		});
		previous.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(Event e) throws Exception {
//				Listbox listbox = (Listbox) listbox;
				listbox.getItems().clear();
				page -= 1;
				if(page.intValue() < 1) page = 1;
				if(page.intValue() == 1){
					//berarti first page
					((Button)previous).setDisabled(true);
					((Button)firstPage).setDisabled(true);
					showListPerPage(listbox,1,page*manyRow);
					ComponentUtil.setValue(perData,"[ 1 - "+String.valueOf(page*manyRow));
				}else {
					((Button)previous).setDisabled(false);
					((Button)firstPage).setDisabled(false);
					showListPerPage(listbox,((page-1)*manyRow)+1,page*manyRow);
					ComponentUtil.setValue(perData,"[ "+String.valueOf(((page-1)*manyRow)+1)+" - "+String.valueOf(page*manyRow));
				}
				((Button)next).setDisabled(false);
				((Button)lastPage).setDisabled(false);
			}
		});
		next.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(Event e) throws Exception {
//				Listbox listbox = (Listbox) listbox;
				listbox.getItems().clear();
				page += 1;
				if(page.intValue() > ((listAllRow.size()-1)/manyRow)+1) page = ((listAllRow.size()-1)/manyRow)+1;
				if(page.intValue() == ((listAllRow.size()-1)/manyRow)+1){
					//berarti last page
					((Button)next).setDisabled(true);
					((Button)lastPage).setDisabled(true);
					showListPerPage(listbox,((page-1)*manyRow)+1,listAllRow.size());
					ComponentUtil.setValue(perData,"[ "+String.valueOf(((page-1)*manyRow)+1)+" - "+String.valueOf(listAllRow.size()));
				}else{
					((Button)next).setDisabled(false);
					((Button)lastPage).setDisabled(false);
					showListPerPage(listbox,((page-1)*manyRow)+1,page*manyRow);
					ComponentUtil.setValue(perData,"[ "+String.valueOf(((page-1)*manyRow)+1)+" - "+String.valueOf(page*manyRow));
				}
				((Button)previous).setDisabled(false);
				((Button)firstPage).setDisabled(false);
			}
		});
		lastPage.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(Event e) throws Exception {
//				Listbox listbox = (Listbox) listbox;
				listbox.getItems().clear();
				page = ((listAllRow.size()-1)/manyRow)+1;
				((Button)previous).setDisabled(false);
				((Button)firstPage).setDisabled(false);
				((Button)next).setDisabled(true);
				((Button)lastPage).setDisabled(true);
				showListPerPage(listbox,((page-1)*manyRow)+1,listAllRow.size());
				ComponentUtil.setValue(perData,"[ "+String.valueOf(((page-1)*manyRow)+1)+" - "+String.valueOf(listAllRow.size()));
			}
		});
		insertPage.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {
			public void onEvent(Event e) throws Exception {
//				Listbox listbox = (Listbox) listbox;
				Integer insertPageData =(Integer)ComponentUtil.getValue(insertPage);
				if(insertPageData.intValue() > 0){
					if(insertPageData.intValue() <= ((listAllRow.size()-1)/manyRow)+1){
						listbox.getItems().clear();
						page = insertPageData;
						if(insertPageData.intValue() == 1){
							//berarti first page
							((Button)previous).setDisabled(true);
							((Button)firstPage).setDisabled(true);
							((Button)next).setDisabled(false);
							((Button)lastPage).setDisabled(false);
							showListPerPage(listbox,1,page*manyRow);
							ComponentUtil.setValue(perData,"[ 1 - "+String.valueOf(page*manyRow));
						}else if(insertPageData.intValue() == ((listAllRow.size()-1)/manyRow)+1){
							//berarti last page
							((Button)previous).setDisabled(false);
							((Button)firstPage).setDisabled(false);
							((Button)next).setDisabled(true);
							((Button)lastPage).setDisabled(true);
							showListPerPage(listbox,((page-1)*manyRow)+1,listAllRow.size());
							ComponentUtil.setValue(perData,"[ "+String.valueOf(((page-1)*manyRow)+1)+" - "+String.valueOf(listAllRow.size()));
						}else{
							((Button)previous).setDisabled(false);
							((Button)firstPage).setDisabled(false);
							((Button)next).setDisabled(false);
							((Button)lastPage).setDisabled(false);
							showListPerPage(listbox,((page-1)*manyRow)+1,page*manyRow);
							ComponentUtil.setValue(perData,"[ "+String.valueOf(((page-1)*manyRow))+1+" - "+String.valueOf(page*manyRow));
						}
					}
				}
			}
		});
	}
    
	public void loadREF_PARM() {
		List<DTOMap> listData = (List<DTOMap>) masterService.getDataMaster(
				" SELECT PARMGRP,GRPNM  FROM REF_PARM ORDER BY PARMGRP",
				new Object[] {} );
		Comboitem ciGrpParm = new Comboitem();
		if (listData.size() > 0) {
			for (DTOMap map : listData) {
				ciGrpParm = new Comboitem();
				ciGrpParm.setLabel(map.getInt("PARMGRP") + " - " + map.getString("GRPNM"));
				ciGrpParm.setValue(map.getInt("PARMGRP"));
				cmbKodeGrup.appendChild(ciGrpParm);
			}
		}
	}

	public void doResetTotal(){
//		Listbox listbox=(Listbox)listbox;
		listbox.getItems().clear();
		((Combobox)cmbKodeGrup).setDisabled(false);
		((Textbox)txtKodeParameter).setDisabled(true);
		((Textbox)txtKodeLain).setDisabled(true);
		((Textbox)txtDeskripsi).setDisabled(true);
		((Textbox)txtCatatan).setDisabled(true);
		((Intbox)intNoUrut).setDisabled(true);
		((Textbox)txtCatatanLainLain).setDisabled(true);
		ComponentUtil.setValue(radioStatus,1);
		doReset();
		btnSimpan.setLabel("Save");
	}

	public void doReset(){
//		Listbox listbox=(Listbox)listbox;
		listbox.getItems().clear();
		((Button)next).setDisabled(false);
		((Button)lastPage).setDisabled(false);
		doRefreshTable();
		ComponentUtil.setValue(txtKodeLain,null);
		ComponentUtil.setValue(txtKodeParameter,null);
		((Textbox)txtKodeParameter).setDisabled(false);
		((Textbox)txtKodeLain).setDisabled(false);
		((Textbox)txtDeskripsi).setDisabled(false);
		((Textbox)txtCatatan).setDisabled(false);
		((Intbox)intNoUrut).setDisabled(false);
		((Textbox)txtCatatanLainLain).setDisabled(false);
		ComponentUtil.setValue(txtDeskripsi,null);
		ComponentUtil.setValue(txtCatatan,null);
		ComponentUtil.setValue(intNoUrut,null);
		ComponentUtil.setValue(txtCatatanLainLain,null);
		((Button)btnDelete).setDisabled(true);
		
		onLoad=false;
		btnSimpan.setLabel("Save");
	}
	
	public void doTambah() {
		if (ComponentUtil.getValue(cmbKodeGrup) == null) {
			throw new WrongValueException(cmbKodeGrup, "Kode Grup harus diisi terlebih dulu.");
		} else {
//			Listbox listbox = (Listbox) listbox;
			listbox.getItems().clear();
			doRefreshTable();
			ComponentUtil.setValue(txtKodeLain, null);
			ComponentUtil.setValue(txtKodeParameter, null);
			((Textbox)txtKodeParameter).setDisabled(false);
			((Textbox)txtKodeLain).setDisabled(false);
			((Textbox)txtDeskripsi).setDisabled(false);
			((Textbox)txtCatatan).setDisabled(false);
			((Intbox)intNoUrut).setDisabled(false);
			((Textbox)txtCatatanLainLain).setDisabled(false);
			ComponentUtil.setValue(txtDeskripsi, null);
			ComponentUtil.setValue(txtCatatan, null);
			ComponentUtil.setValue(intNoUrut, null);
			ComponentUtil.setValue(txtCatatanLainLain, null);
			Textbox txt = (Textbox) txtKodeParameter;
			txt.setFocus(true);
		}
	}

	public void doSaveOrUpdate() {
		if (validation()) {	
			if (onLoad){
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
			}else{
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

	public void doUpdate() {
		DTOMap parm = new DTOMap();
		parm.put("PARMGRP", ComponentUtil.getValue(cmbKodeGrup));
		parm.put("PARMID", ComponentUtil.getValue(txtKodeParameter));
		parm.put("PARMNM", ComponentUtil.getValue(txtDeskripsi));
		parm.put("PARMNMOTH", ComponentUtil.getValue(txtCatatan));
		parm.put("UPDDATE", CFG_SYS.getDate("OPEN_DATE"));
		parm.put("UPDUSER", USER_MASTER.getString("USERID"));
		parm.put("VIEWORD", ComponentUtil.getValue(intNoUrut));
		parm.put("VIEWORDNM", ComponentUtil.getValue(txtCatatanLainLain));
		parm.put("STATUS", ComponentUtil.getValue(radioStatus));
		parm.put("STSDT", CFG_SYS.getDate("OPEN_DATE"));
		parm.put("PARMIDOTH", ComponentUtil.getValue(txtKodeLain));
		parm.put("PK", "PARMID,PARMGRP");
		jt2.updateData(parm, "CFG_PARM");
		MessageBox.showInformation("Data berhasil di-update.");
		doReset();
	}

	public void doSave() {
		Integer parmgrup = (Integer) ComponentUtil.getValue(cmbKodeGrup);
		String parmid = (String) ComponentUtil.getValue(txtKodeParameter);
		if (parmgrup != null) {
			if (parmid != null) {
				DTOMap key = (DTOMap) jt2.queryObject(
						"select PARMGRP, PARMID from CFG_PARM where PARMGRP=? AND PARMID=?",
						new Object[] { parmgrup, parmid }, new DTOMap());
				if (key != null)
					throw new WrongValueException(txtKodeParameter,
							"Data Sudah Ada. Entry Kode Parameter yang Lain");
			}
		}
		DTOMap map = new DTOMap();
		map.put("PARMGRP", parmgrup);
		map.put("PARMID", ComponentUtil.getValue(txtKodeParameter));
		map.put("PARMNM", ComponentUtil.getValue(txtDeskripsi));
		map.put("PARMNMOTH", ComponentUtil.getValue(txtCatatan));
		map.put("CRTDATE", CFG_SYS.getDate("OPEN_DATE"));
		map.put("CRTUSER", USER_MASTER.getString("USERID"));
		map.put("VIEWORD", ComponentUtil.getValue(intNoUrut));
		map.put("VIEWORDNM", ComponentUtil.getValue(txtCatatanLainLain));
		map.put("STATUS", ComponentUtil.getValue(radioStatus));
		map.put("STSDT", CFG_SYS.getDate("OPEN_DATE"));
		map.put("PARMIDOTH", ComponentUtil.getValue(txtKodeLain));
		jt2.insertData(map, "CFG_PARM");
		MessageBox.showInformation("Data berhasil di-simpan.");
		doReset();
	}

	protected void showListPerPage(Listbox listbox, Integer awal, Integer Akhir) {
		ComponentUtil.setValue(insertPage, page);
		if(listAllRow.size() > manyRow){
			((Div)moldingPaging).setVisible(true);
		}else{
			((Div)moldingPaging).setVisible(false);
		}
		for (int i = awal.intValue()-1; i < Akhir.intValue(); i++) {
			Listitem item= (Listitem) listAllRow.get(i);
			listbox.appendChild(item);
		}
	}

	public void doEdit(){
//		Listbox listbox=(Listbox)listbox;
		Listitem item=listbox.getSelectedItem();
		if (item!=null){
			DTOMap map=(DTOMap)item.getAttribute("DATA");
			ComponentUtil.setValue(cmbKodeGrup,map.getInt("PARMGRP"));
			ComponentUtil.setValue(txtKodeParameter,map.getString("PARMID"));
			ComponentUtil.setValue(txtKodeLain,map.getString("PARMIDOTH"));
			ComponentUtil.setValue(txtDeskripsi,map.getString("PARMNM"));
			ComponentUtil.setValue(txtCatatan,map.getString("PARMNMOTH"));
			ComponentUtil.setValue(intNoUrut,map.getInt("VIEWORD"));
			ComponentUtil.setValue(txtCatatanLainLain,map.getString("VIEWORDNM"));
			ComponentUtil.setValue(radioStatus,map.getInt("STATUS"));
			btnSimpan.setLabel("Update");
			((Combobox)cmbKodeGrup).setDisabled(true);
			((Textbox)txtKodeParameter).setDisabled(true);
			((Textbox)txtKodeLain).setDisabled(false);
			((Textbox)txtDeskripsi).setDisabled(false);
			((Textbox)txtCatatan).setDisabled(false);
			((Intbox)intNoUrut).setDisabled(false);
			((Textbox)txtCatatanLainLain).setDisabled(false);
			((Button)btnDelete).setDisabled(false);
			Textbox txt = (Textbox) txtKodeLain;
			txt.setFocus(true);
			onLoad=true;
		}
	}
	
	public void doDelete() {
		// Listbox listbox=(Listbox)listbox;
		final Listitem item = listbox.getSelectedItem();
		if (item != null) {
			DTOMap data = (DTOMap) item.getAttribute("DATA");
			data.put("PARMGRP", ComponentUtil.getValue(cmbKodeGrup));
			data.put("PARMID", ComponentUtil.getValue(txtKodeParameter));
			data.put("PK", "PARMID,PARMGRP");
			jt2.deleteData(data, "CFG_PARM");
			item.detach();
			MessageBox.showInformation("Data Berhasil Dihapus");
			doReset();
		}
	}

	public void doRefreshTable(){
		Integer PARMGRP=(Integer)ComponentUtil.getValue(cmbKodeGrup);
//		Listbox listbox=(Listbox)listbox;
		List listParm=jt2.query("Select * From CFG_PARM Where PARMGRP=? ORDER BY 2,12,9",new Object[]{PARMGRP},new DTOMap());
		listAllRow.clear();
		listbox.getItems().clear();
		page=1;
		
		if (listParm.size()>0){
			for(Object o : listParm){
				DTOMap parm = (DTOMap)o;
				Listitem item=new Listitem();
				item.setAttribute("DATA",parm);
				item.appendChild(new Listcell(parm.getInt("PARMGRP").toString()));
				item.appendChild(new Listcell(parm.getString("PARMID")));
				item.appendChild(new Listcell(parm.getString("PARMNM")));
				item.appendChild(new Listcell(parm.getString("PARMIDOTH")));
				item.appendChild(new Listcell(parm.getString("PARMNMOTH")));
				item.appendChild(new Listcell(parm.getInt("VIEWORD")==null?"":parm.getInt("VIEWORD").toString()));
				item.appendChild(new Listcell(parm.getString("VIEWORDNM")));
				String status = "Tidak Aktif";
				if(parm.getInt("STATUS") == 1)
				status="Aktif";
				item.appendChild(new Listcell(status));
				listAllRow.add(item);
			}
		}
		if(listAllRow.size() > manyRow){
			showListPerPage(listbox,1,manyRow);
			ComponentUtil.setValue(perData,"[ 1 - "+manyRow.toString());
		}else{
			showListPerPage(listbox,1,listAllRow.size());
			ComponentUtil.setValue(perData,"[ 1 - "+String.valueOf(listAllRow.size()));
		}
		ComponentUtil.setValue(perPage,"/ "+String.valueOf(((listAllRow.size()-1)/manyRow)+1));
		ComponentUtil.setValue(allData," / "+String.valueOf(listAllRow.size())+" ]");
		
		((Button)firstPage).setDisabled(true);
		((Button)previous).setDisabled(true);
		
		((Button)next).setDisabled(false);
		((Button)lastPage).setDisabled(false);
		
		((Textbox)txtKodeParameter).setDisabled(true);
		((Textbox)txtKodeLain).setDisabled(true);
		((Textbox)txtDeskripsi).setDisabled(true);
		((Textbox)txtCatatan).setDisabled(true);
		((Textbox)txtCatatanLainLain).setDisabled(true);
		((Intbox)intNoUrut).setDisabled(true);
	}

	public boolean validation(){
		boolean isValid=true;
		if (isValid){
			List wrongValue=new ArrayList(0);
			String parmid=(String)ComponentUtil.getValue(txtKodeParameter);
			if(parmid==null){
				wrongValue.add( new WrongValueException(txtKodeParameter, "Kode Parameter harus diisi. "));
			}
			String parmnm=(String)ComponentUtil.getValue(txtDeskripsi);
			if(parmnm==null){
				wrongValue.add(new WrongValueException(txtDeskripsi, "Deskripsi harus diisi. "));
			}
			String moth=(String)ComponentUtil.getValue(txtCatatan);
			if (wrongValue.size()>0){
				throw new WrongValuesException((WrongValueException[]) wrongValue.toArray(new WrongValueException[wrongValue.size()]));
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
}
