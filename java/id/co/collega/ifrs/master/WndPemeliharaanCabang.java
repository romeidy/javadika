package id.co.collega.ifrs.master;


import java.util.ArrayList;
import java.util.List;

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
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;

import id.co.collega.ifrs.common.Cryptograph;
import id.co.collega.ifrs.common.DTOMap;
import id.co.collega.ifrs.common.JdbcTemplate;
import id.co.collega.ifrs.master.service.MasterServices;
import id.co.collega.ifrs.util.ComponentUtil;
import id.co.collega.ifrs.util.MessageBox;
import id.co.collega.v7.seed.config.AuthenticationService;

@org.springframework.stereotype.Component
@Scope("execution")
public class WndPemeliharaanCabang extends SelectorComposer<Component>{
	
	@Wire Textbox txtAlamat;
	@Wire Combobox cmbCabang;
	@Wire Combobox cmbProvinsi;
	@Wire Combobox cmbKota;
	@Wire Listbox listCabangMapping;
	@Wire Button btnSave;
	
	@Autowired MasterServices masterService;
	@Autowired AuthenticationService authService;
	@Autowired JdbcTemplate jt;
	
	Boolean onLoad = false;
	@Wire DTOMap dataUser;
	
	
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		btnSave.addEventListener(Events.ON_CLICK, new EventListener() {
			public void onEvent(Event event) throws Exception {
				doSave();
			}
		});
		
		listCabangMapping.addEventListener(Events.ON_CLICK, new EventListener() {
			public void onEvent(Event event) throws Exception {
				doEdit();
			}
		});
		
		cmbCabang.addEventListener(Events.ON_CHANGE, new EventListener() {
			public void onEvent(Event event) throws Exception {
				DTOMap dto = (DTOMap)cmbCabang.getSelectedItem().getAttribute("DATA");
				if(dto.getString("ALAMAT")!=null){
					ComponentUtil.setValue(txtAlamat, dto.getString("ALAMAT")+" \n KOTA:"+dto.getString("KOTA"));
				}
			}
		});
		
		cmbProvinsi.addEventListener(Events.ON_CHANGE, new EventListener() {
			public void onEvent(Event event) throws Exception {
				loadDataKotaPPATK();
			}
		});
		
		loadData();
	}
	
	private void doSave(){
		try {
			DTOMap dto = new DTOMap();
			DTOMap data = (DTOMap)cmbCabang.getSelectedItem().getAttribute("DATA");
			dto.put("KD_CAB_KONSOL", data.getString("KD_CAB_KONSOL"));
			dto.put("KD_CAB", data.getString("KD_CAB"));
			dto.put("KD_KOTA", ComponentUtil.getValue(cmbKota));
			dto.put("KD_PROV", ComponentUtil.getValue(cmbProvinsi));
			dto.put("PK", "KD_CAB_KONSOL,KD_CAB");
			masterService.deleteData(dto, "D_CABANG_MAPPING");
			masterService.insertData(dto, "D_CABANG_MAPPING");
			
			loadDataCabangMapping();
			MessageBox.showInformation("Data berhasil disimpan");
			cmbCabang.setDisabled(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void doEdit(){
		if(listCabangMapping.getSelectedItem()!=null){
			DTOMap dto = (DTOMap)listCabangMapping.getSelectedItem().getAttribute("DATA");
			ComponentUtil.setValue(cmbCabang, dto.getString("KD_CAB"));
			ComponentUtil.setValue(txtAlamat, dto.getString("ALAMAT"));
			ComponentUtil.setValue(cmbProvinsi, dto.getString("KD_PROVINSI"));
			
			loadDataKotaPPATK();
			ComponentUtil.setValue(cmbKota, dto.getString("KD_KOTA"));
			
			cmbCabang.setDisabled(true);
		}
	}
	
	private List<DTOMap> getListCabangMapping(){
		List<DTOMap> dto = new ArrayList<DTOMap>();
		try {
			dto = masterService.getDataMaster(""
					+ " SELECT A.KD_CAB_KONSOL, A.KD_CAB, A.NM_CAB, A.ALAMAT, C.ID_PARM AS KD_PROVINSI, C.URAIAN AS NM_PROVINSI, D.ID_PARM AS KD_KOTA, D.URAIAN AS NM_KOTA FROM D_CABANG A "
					+ " LEFT JOIN D_CABANG_MAPPING B ON A.KD_CAB=B.KD_CAB "
					+ " LEFT JOIN APUPPT_PARM C ON C.KD_GROUP='00010' AND B.KD_PROV=C.ID_PARM "
					+ " LEFT JOIN APUPPT_PARM D ON D.KD_GROUP='00011' AND B.KD_KOTA=D.ID_PARM "
					+ " ORDER BY A.KD_CAB, A.KD_CAB_KONSOL "
					, new Object[]{});
			return dto;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private List<DTOMap> getCabang(){
		List<DTOMap> dto = new ArrayList<DTOMap>();
		try {
			dto = masterService.getDataMaster("SELECT * FROM D_CABANG", new Object[]{});
			return dto;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private List<DTOMap> getKotaPPATK(){
		List<DTOMap> dto = new ArrayList<DTOMap>();
		try {
			dto = masterService.getDataMaster("SELECT ID_PARM, URAIAN FROM APUPPT_PARM WHERE KD_GROUP='00011' AND (SUBSTR(ID_PARM,1,2)=? OR SUBSTR(ID_PARM,1,2)='99') ORDER BY ID_PARM", new Object[]{ComponentUtil.getValue(cmbProvinsi)});
			return dto;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private List<DTOMap> getProvinsiPPATK(){
		List<DTOMap> dto = new ArrayList<DTOMap>();
		try {
			dto = masterService.getDataMaster("SELECT ID_PARM, URAIAN FROM APUPPT_PARM WHERE KD_GROUP='00010'", new Object[]{});
			return dto;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private void loadDataCabangMapping(){
		try {
			listCabangMapping.getItems().clear();
			for (DTOMap dto : getListCabangMapping()) {
				Listitem item = new Listitem();
				item.setAttribute("DATA", dto);
				item.appendChild(new Listcell(dto.getString("KD_CAB_KONSOL")));
				item.appendChild(new Listcell(dto.getString("KD_CAB")));
				item.appendChild(new Listcell(dto.getString("NM_CAB")));
				item.appendChild(new Listcell(dto.getString("ALAMAT")));
				item.appendChild(new Listcell(dto.getString("NM_PROVINSI")));
				item.appendChild(new Listcell(dto.getString("NM_KOTA")));
				listCabangMapping.appendChild(item);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void loadDataCabang(){
		try {
			for (DTOMap dto : getCabang()) {
				Comboitem item = new Comboitem();
				item.setLabel(dto.getString("KD_CAB")+" - "+dto.getString("NM_CAB"));
				item.setValue(dto.getString("KD_CAB"));
				item.setAttribute("DATA", dto);
				cmbCabang.appendChild(item);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void loadDataKotaPPATK(){
		try {
			cmbKota.getItems().clear();
			for (DTOMap dto : getKotaPPATK()) {
				Comboitem item = new Comboitem();
				item.setLabel(dto.getString("URAIAN"));
				item.setValue(dto.getString("ID_PARM"));
				cmbKota.appendChild(item);
				cmbKota.setSelectedIndex(-1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void loadDataProvinsiPPATK(){
		try {
			for (DTOMap dto : getProvinsiPPATK()) {
				Comboitem item = new Comboitem();
				item.setLabel(dto.getString("URAIAN"));
				item.setValue(dto.getString("ID_PARM"));
				cmbProvinsi.appendChild(item);
				cmbProvinsi.setSelectedIndex(-1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void loadData(){
		loadDataCabang();
		loadDataProvinsiPPATK();
		loadDataKotaPPATK();
		loadDataCabangMapping();
	}
	
	private void doReset(){
	}
}
