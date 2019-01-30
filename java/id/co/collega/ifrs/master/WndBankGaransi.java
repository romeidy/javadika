package id.co.collega.ifrs.master;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import id.co.collega.ifrs.common.DTOMap;
import id.co.collega.ifrs.common.FunctionUtils;
import id.co.collega.ifrs.common.JdbcTemplate;
import id.co.collega.ifrs.master.service.MasterServices;
import id.co.collega.ifrs.util.ComponentUtil;
import id.co.collega.ifrs.util.MessageBox;
import id.co.collega.v7.seed.config.AuthenticationService;

import org.hibernate.event.internal.OnUpdateVisitor;
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
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.jet.gand.services.GlobalVariable;

@org.springframework.stereotype.Component
@Scope("execution")
public class WndBankGaransi extends SelectorComposer<Component>{
	@Wire	Window wnd;
	
	@Wire Datebox dateTglPosisi;
	
	@Wire Combobox cmbProduk;
	@Wire Combobox cmbCabang;
	@Wire Listbox list;
	@Wire Textbox txtNoRek;
	@Wire Decimalbox decEndBalance;

	@Wire Button btnReset;
	@Wire Button btnSave;
	@Wire Button btnDelete;
	
	@Autowired MasterServices masterService;

	@Autowired AuthenticationService authService;
	
	@Autowired JdbcTemplate jt2;

	private boolean onLoad = false;
	
	private Date openDate;
	private String version;

	
	@Override
	public void doAfterCompose(Component comp) throws Exception  {
		super.doAfterCompose(comp);
		
		btnSave.addEventListener(Events.ON_CLICK, new EventListener() {
			public void onEvent(Event event) throws Exception {
				doSave();
			}
		});

		btnReset.addEventListener(Events.ON_CLICK, new EventListener() {
			public void onEvent(Event event) throws Exception {
				doReset();
			}
		});

		btnDelete.addEventListener(Events.ON_CLICK, new EventListener() {
			public void onEvent(Event event) throws Exception {
				doDeleteData();
			}
		});
		
		list.addEventListener(Events.ON_DOUBLE_CLICK, new EventListener() {
			public void onEvent(Event event) throws Exception {

				Listitem item = list.getSelectedItem();

				if (item != null) {
					DTOMap data = (DTOMap) item.getAttribute("DATA");
					doEdit(data);
				}
			}
		});
		
		doReset();
	}
	
	private void doSave() {
		if(doValidation()) {
			doBeforeInsert();
		};
	}
	
	/*		How to insert into BGaransi_Master : 
	 * 		Jika tidak ada record, insert dengan version 0
	 *		Jika noacc ada dengan version 0, insert version 1.
	 *		jika noacc ada dengan version 1, delete yang version 1 lalu insert ulang dengan version 1. 
	 */	
	private void doBeforeInsert(){	
		DTOMap bGaransi0 = new DTOMap();
		bGaransi0.put("ACCNBR", ComponentUtil.getValue(txtNoRek));
		bGaransi0.put("VERSION", "0");
			if(masterService.isExist(bGaransi0, "BGARANSI_MASTER")) {			
				DTOMap bGaransi1 = new DTOMap();
				bGaransi1.put("ACCNBR", ComponentUtil.getValue(txtNoRek));
				bGaransi1.put("VERSION", "1");
				if(masterService.isExist(bGaransi1, "BGARANSI_MASTER")) {	
					doDelete("1");
					doInsert("1");				
				} else {
					doInsert("1");
				}
			} else {
				doInsert("0");
			}
	};
	
	
	private void doDelete(String version){
		DTOMap bGaransi = new DTOMap();
		bGaransi.put("ACCNBR", ComponentUtil.getValue(txtNoRek));
		bGaransi.put("VERSION", version);
		bGaransi.put("PK", "ACCNBR,VERSION");
		masterService.deleteData(bGaransi, "BGARANSI_MASTER");
	}
	
	private void doInsert(final String version){
			try {
				Messagebox.show("Apakah Anda Yakin menyimpan data ini .. ?",
					"KONFIRMASI", Messagebox.OK | Messagebox.CANCEL,
					Messagebox.QUESTION, new EventListener<Event>() {
						@Override
						public void onEvent(Event e) throws Exception {
							if (Messagebox.ON_OK.equals(e.getName())) {
								DTOMap bGaransiMaster = new DTOMap();				
								bGaransiMaster.put("TGL_POS", ComponentUtil.getValue(dateTglPosisi));
								bGaransiMaster.put("PRODID", ComponentUtil.getValue(cmbProduk));
								bGaransiMaster.put("BRANCHID",ComponentUtil.getValue(cmbCabang));
								bGaransiMaster.put("ACCNBR",ComponentUtil.getValue(txtNoRek));
								bGaransiMaster.put("ENDBAL",ComponentUtil.getValue(decEndBalance));
								bGaransiMaster.put("ACCSTS", 1);
								bGaransiMaster.put("VERSION", version);
								bGaransiMaster.put("CREATED_DATE", new Date());
								bGaransiMaster.put("CREATED_BY", authService.getUserDetails().getUserId());
								masterService.insertData(bGaransiMaster,"BGARANSI_MASTER");
								MessageBox.showInformation("Data berhasil di simpan.");
								doReset();
							} else if (Messagebox.ON_CANCEL.equals(e.getName())) {
										
							}
						}
					});
			} catch (Exception e) {
				e.printStackTrace();
				MessageBox.showError(e.getMessage());
			}
	}
	
	public void doDeleteData() {
		final Listitem item = list.getSelectedItem();
		if (item != null)
			Messagebox.show("Apakah Anda Yakin mau menghapus data ini .. ?",
					"KONFIRMASI", Messagebox.OK | Messagebox.CANCEL,
					Messagebox.QUESTION, new EventListener<Event>() {
						@Override
						public void onEvent(Event e) throws Exception {
							if (Messagebox.ON_OK.equals(e.getName())) {
								DTOMap bGaransi_Master = (DTOMap) item.getAttribute("DATA");
								bGaransi_Master.put("TGL_POS",ComponentUtil.getValue(dateTglPosisi));
								bGaransi_Master.put("PRODID", ComponentUtil.getValue(cmbProduk));
								bGaransi_Master.put("ACCNBR",ComponentUtil.getValue(txtNoRek));
								bGaransi_Master.put("VERSION", version);
								bGaransi_Master.put("PK", "TGL_POS,PRODID,ACCNBR");
								masterService.deleteData(bGaransi_Master, "BGARANSI_MASTER");
								item.detach();
								MessageBox.showInformation("Data Berhasil Dihapus");
								doReset();
							} else if (Messagebox.ON_CANCEL.equals(e.getName())) {
								
							}
						}
					});
	}
	
	private void doEdit(DTOMap data) {
//		btnSave.setLabel("Update");
		btnSave.setDisabled(true);
		btnReset.setDisabled(false);
		if (data != null) {
			ComponentUtil.setValue(dateTglPosisi, data.getDate("TGL_POS"));
			ComponentUtil.setValue(cmbProduk, data.get("PRODID"));
			ComponentUtil.setValue(cmbCabang, data.get("BRANCHID"));
			ComponentUtil.setValue(txtNoRek, data.get("ACCNBR"));
			ComponentUtil.setValue(decEndBalance, data.getBigDecimal("ENDBAL"));
			version = data.getString("VERSION");
			onLoad = true;
			
			btnDelete.setDisabled(false);
		}
	}
	
	private boolean doValidation() {
		if (ComponentUtil.getValue(cmbProduk) == null
				|| ComponentUtil.getValue(cmbProduk).equals("")) {
			throw new WrongValueException(cmbProduk,
					"Produk harus dipilih.");
		} else if (ComponentUtil.getValue(cmbCabang) == null
				|| ComponentUtil.getValue(cmbCabang).equals("")) {
			throw new WrongValueException(cmbCabang,
					"Cabang harus dipilih.");
		} else if (ComponentUtil.getValue(txtNoRek) == null
				|| ComponentUtil.getValue(txtNoRek).equals("")) {
			throw new WrongValueException(txtNoRek,
					"No. Rekening harus diisi.");
		} 
		if (decEndBalance.getValue() !=null){
			Double zero = 0.00;
			BigDecimal endBal = (BigDecimal) ComponentUtil.getValue(decEndBalance);
			double endBalance = endBal.doubleValue();
			if(endBalance <= zero){
		        throw new WrongValueException(decEndBalance, 
		        		"Saldo akhir tidak boleh 0.");					
			}					
		} else { 
			throw new WrongValueException(decEndBalance,
					"Saldo Akhir harus diisi.");
		}
		return true;
	}
	
	private void doReset() {
		ComponentUtil.clear(wnd);

		btnSave.setDisabled(false);
		btnReset.setDisabled(false);
		btnDelete.setDisabled(true);
		
		doLoadTglPosisi();
		doLoadDataCabang();
		doLoadDataProduk();
		doLoadTable();
	}
	
	private void doLoadTglPosisi(){
		DTOMap map = (DTOMap) GlobalVariable.getInstance().get("syshost");
		openDate=map.getDate("OPEN_DATE");
		dateTglPosisi.setValue(openDate);
	}
	
	private void doLoadDataProduk() {
		List<DTOMap> listProduk=masterService.getDataMaster(" SELECT PARMID,PARMNM "
				+ "												FROM CFG_PARM "
				+ "												WHERE PARMGRP=1 ORDER BY PARMID  ",new Object[]{});
			cmbProduk.getItems().clear();
		if (listProduk.size() > 0) {
			Comboitem ciPrd=new Comboitem();			
			cmbProduk.appendChild(ciPrd);
			for (DTOMap dtoMap : listProduk) {
				ciPrd=new Comboitem();
				ciPrd.setLabel(dtoMap.getString("PARMID")+" - "+dtoMap.getString("PARMNM"));
				ciPrd.setValue(dtoMap.getString("PARMID"));
				cmbProduk.appendChild(ciPrd);
			}
			cmbProduk.setSelectedIndex(0);
		}
	}
	
	private void doLoadDataCabang() {
		List<DTOMap> listCabang=masterService.getDataMaster(" SELECT DISTINCT KD_CAB,NM_CAB "
				+ "												FROM CFG_CABANG ORDER BY KD_CAB  ",new Object[]{});
		cmbCabang.getItems().clear();
		if (listCabang.size() > 0) {
			Comboitem ciCab=new Comboitem();
			cmbCabang.appendChild(ciCab);
			for (DTOMap dtoMap : listCabang) {
				ciCab=new Comboitem();
				ciCab.setLabel(dtoMap.getString("KD_CAB")+" - "+dtoMap.getString("NM_CAB"));
				ciCab.setValue(dtoMap.getString("KD_CAB"));
				cmbCabang.appendChild(ciCab);
			}
			cmbCabang.setSelectedIndex(0);
		}
		
	}
	
	private void doLoadTable() {
		List dataSet = jt2
				.query("SELECT A.TGL_POS, A.PRODID, A.BRANCHID, A.ACCNBR, A.ENDBAL, A.VERSION, B.PARMNM AS STATUS FROM BGARANSI_MASTER A "
						+ "LEFT OUTER JOIN CFG_PARM B ON B.PARMGRP = 16 AND  CAST(B.PARMID AS INT) = A.ACCSTS",
						new Object[] {}, new DTOMap());
		list.getItems().clear();
		if (dataSet != null && dataSet.size() > 0) {
			for (Object o : dataSet) {
				DTOMap bGaransiMaster = (DTOMap) o;
				Listitem item = new Listitem();
				item.setAttribute("DATA", bGaransiMaster);
				item.appendChild(new Listcell(bGaransiMaster.getDate("TGL_POS").toString()));
				item.appendChild(new Listcell(bGaransiMaster.getString("PRODID")));
				item.appendChild(new Listcell(bGaransiMaster.getString("BRANCHID")));
				item.appendChild(new Listcell(bGaransiMaster.getString("STATUS")));
				item.appendChild(new Listcell(bGaransiMaster.getString("ACCNBR")));
				item.appendChild(new Listcell(FunctionUtils.moneyToText(bGaransiMaster.getBigDecimal("ENDBAL"))));
				item.appendChild(new Listcell(bGaransiMaster.getString("VERSION")));
				
				list.appendChild(item);
			}
		}
	}
}
