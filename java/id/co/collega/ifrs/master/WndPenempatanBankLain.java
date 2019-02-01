package id.co.collega.ifrs.master;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import id.co.collega.ifrs.common.DTOMap;
import id.co.collega.ifrs.common.FunctionUtils;
import id.co.collega.ifrs.common.JdbcTemplate;
import id.co.collega.ifrs.master.service.MasterServices;
import id.co.collega.ifrs.util.ComponentUtil;
import id.co.collega.ifrs.util.MessageBox;
//import id.co.collega.v7.ef.widget.Datebox;
//import id.co.collega.v7.rte.runtime.common.renderer.components.Decimalbox;


import id.co.collega.v7.seed.config.AuthenticationService;

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
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.jet.gand.services.GlobalVariable;

@org.springframework.stereotype.Component
@Scope("execution")
public class WndPenempatanBankLain extends SelectorComposer<Component> {

	@Wire	Window wnd;
	
	@Wire	Combobox cmbJnsPenempatan;
	@Wire Datebox dtTglPosisi;
	@Wire Datebox dtTglPenanaman;
	@Wire Decimalbox decNominal;
	@Wire Decimalbox decBunga;
	@Wire Decimalbox decSaldoAkhir;
	
	@Wire Textbox txtNoRekening;
	@Wire Textbox txtNamaBank;
	@Wire Intbox inTenor;
	@Wire Listbox list;
	
	@Wire Button btnSave;
	@Wire Button btnEdit;
	@Wire Button btnReset;
	@Wire Button btnDelete;
	
	@Autowired MasterServices masterService;

	@Autowired AuthenticationService authService;
	
	@Autowired JdbcTemplate jt2;

	//
	private boolean onLoad = false;

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	String Aksi;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		btnSave.addEventListener(Events.ON_CLICK, new EventListener() {
			public void onEvent(Event event) throws Exception {
				doSave();
			}
		});

		btnReset.addEventListener(Events.ON_CLICK, new EventListener() {
			public void onEvent(Event event) throws Exception {
				doTotalReset();
			}
		});

		btnDelete.addEventListener(Events.ON_CLICK, new EventListener() {
			public void onEvent(Event event) throws Exception {
				if (checkPrivDelete()) {
					doDelete();	
				}
			}
		});

		
		cmbJnsPenempatan.addEventListener(Events.ON_CHANGE,
				new EventListener() {
					public void onEvent(Event event) throws Exception {
						doLoadTable();
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

		decBunga.addEventListener(Events.ON_BLUR, new EventListener() {
			public void onEvent(Event event) throws Exception {
				
//				Validasi Bunga jika lebih dari 100 maka error, jika kosong maka set 0.0000
				double maxBunga = 100.0000;
				if (decBunga.getValue() !=null){
					BigDecimal bunga = (BigDecimal) ComponentUtil.getValue(decBunga);
					double dBunga = bunga.doubleValue();
					if(dBunga > maxBunga){
				        throw new WrongValueException(decBunga, "Bunga tidak boleh lebih dari 100%.");					
					}					
				} else { 
					ComponentUtil.setValue(decBunga, 0.0000);
				}
			}
		});
		
		btnEdit.addEventListener(Events.ON_CLICK, new EventListener() {
			public void onEvent(Event event) throws Exception {
				Listitem item = list.getSelectedItem();

				if (item != null) {
					DTOMap data = (DTOMap) item.getAttribute("DATA");
					doEdit(data);
				}
			}
		});
		
//		doLoadTglPosisi();
//		doLoadJenisPenempatan();		
		doTotalReset();
	}

	private void doReset() {

		ComponentUtil.setValue(txtNoRekening, null);
		ComponentUtil.setValue(dtTglPenanaman, null);
		ComponentUtil.setValue(txtNamaBank, null);
		ComponentUtil.setValue(decNominal, null);
		ComponentUtil.setValue(decBunga, null);
		ComponentUtil.setValue(decSaldoAkhir, null);
		ComponentUtil.setValue(inTenor, null);
		
		decBunga.setFormat("##0.0000");
		onLoad = false;

		txtNoRekening.setDisabled(false);
		cmbJnsPenempatan.setDisabled(false);
		
		btnSave.setDisabled(false);
		btnReset.setDisabled(false);
		btnDelete.setDisabled(true);

		btnSave.setLabel("Save");
		
		doLoadTable();
	}
	
	private void doTotalReset() {
		ComponentUtil.clear(wnd);

		txtNoRekening.setDisabled(false);
		cmbJnsPenempatan.setDisabled(false);
		 
		onLoad = false;

		btnSave.setDisabled(false);
		btnReset.setDisabled(false);
		btnDelete.setDisabled(true);
	
		btnSave.setLabel("Save");

		list.getItems().clear();
		
//		doLoadTable();
		doLoadTglPosisi();
		doLoadJenisPenempatan();
	}	

	@SuppressWarnings("unchecked")
	private void doLoadJenisPenempatan() {
		cmbJnsPenempatan.getItems().clear();
		cmbJnsPenempatan.setSelectedIndex(-1);
		List<DTOMap> listData = (List<DTOMap>) masterService.getDataMaster(
				"select parmid, parmnm from cfg_parm where parmgrp=22",
				null);
		Comboitem item = new Comboitem();
		cmbJnsPenempatan.appendChild(item);
		for (DTOMap map : listData) {
			item = new Comboitem();
			item.setLabel(map.getString("parmid") + " - " + map.getString("parmnm"));
			item.setValue(map.getString("parmid"));
			cmbJnsPenempatan.appendChild(item);
		}
	}

	@SuppressWarnings("unchecked")
	private void doLoadTglPosisi() {
		DTOMap datas = new DTOMap();
		datas = masterService.getMapMaster("SELECT OPEN_DATE FROM CFG_SYS",
				null);
		ComponentUtil.setValue(dtTglPosisi, datas.getDate("OPEN_DATE"));
	}

	private void doSave() {
		if (doValidation()) {
			if (onLoad) {
				if (checkPrivUpdate()) {
					doUpdate();	
				}
			} else {
				if (checkPrivInsert()) {
					doInsert();	
				}
			}
		} else {
			System.out.println("validation return false");
		}
	}

	private void doInsert() {
		try {
					Messagebox.show("Apakah Anda Yakin menyimpan data ini .. ?",
							"KONFIRMASI", Messagebox.OK | Messagebox.CANCEL,
							Messagebox.QUESTION, new EventListener<Event>() {
								@Override
								public void onEvent(Event e) throws Exception {
									if (Messagebox.ON_OK.equals(e.getName())) {
										DTOMap plc_master = new DTOMap();				
										plc_master.put("TGL_POS", ComponentUtil.getValue(dtTglPosisi));
										plc_master.put("PLCID", ComponentUtil.getValue(cmbJnsPenempatan));
										plc_master.put("ACCNBR",ComponentUtil.getValue(txtNoRekening));
										plc_master.put("PLCDT",ComponentUtil.getValue(dtTglPenanaman));
										plc_master.put("BANKNM",ComponentUtil.getValue(txtNamaBank));
										plc_master.put("NOMINAL",ComponentUtil.getValue(decNominal));
										plc_master.put("PLCPERIOD",ComponentUtil.getValue(inTenor));
										plc_master.put("PLCINT",ComponentUtil.getValue(decBunga));
										plc_master.put("ENDBAl",ComponentUtil.getValue(decSaldoAkhir));
										plc_master.put("CRTDATE", new Date());
										plc_master.put("CRTUSER", authService.getUserDetails().getUserId());
										masterService.insertData(plc_master,"PLC_MASTER");
										Aksi = "Penambahan data No Rekening "+ComponentUtil.getValue(cmbJnsPenempatan);
										doLogAktfitas(Aksi);
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

	public void doUpdate() {
			Messagebox.show("Apakah Anda Yakin update data ini .. ?",
					"KONFIRMASI", Messagebox.OK | Messagebox.CANCEL,
					Messagebox.QUESTION, new EventListener<Event>() {
						@Override
						public void onEvent(Event e) throws Exception {
							if (Messagebox.ON_OK.equals(e.getName())) {
								DTOMap plc_master = new DTOMap();								
								plc_master.put("TGL_POS", ComponentUtil.getValue(dtTglPosisi));
								plc_master.put("PLCID", ComponentUtil.getValue(cmbJnsPenempatan));
								plc_master.put("ACCNBR",ComponentUtil.getValue(txtNoRekening));
								plc_master.put("PLCDT",ComponentUtil.getValue(dtTglPenanaman));
								plc_master.put("BANKNM",ComponentUtil.getValue(txtNamaBank));
								plc_master.put("NOMINAL",ComponentUtil.getValue(decNominal));
								plc_master.put("PLCPERIOD",ComponentUtil.getValue(inTenor));
								plc_master.put("PLCINT",ComponentUtil.getValue(decBunga));
								plc_master.put("ENDBAL",ComponentUtil.getValue(decSaldoAkhir));
								plc_master.put("UPDDATE", new Date());								
								plc_master.put("CRTUSER", authService.getUserDetails().getUserId());
								plc_master.put("PK", "TGL_POS,PLCID,ACCNBR");
								
								// GlobalVariable.get will shutdown the database services. 
								//	datas.put("CRTUSER", GlobalVariable.getInstance().get("USER_MASTER"));
								
								masterService.updateData(plc_master, "PLC_MASTER");
								Aksi = "Perubahan data No Rekening "+ComponentUtil.getValue(cmbJnsPenempatan);
								doLogAktfitas(Aksi);
								MessageBox.showInformation("Data berhasil diupdate.");														
								doReset();
							} else if (Messagebox.ON_CANCEL.equals(e.getName())) {
								
							}
						}
					});
	}

	public void doDelete() {
		final Listitem item = list.getSelectedItem();
		if (item != null)
			Messagebox.show("Apakah Anda Yakin mau menghapus data ini .. ?",
					"KONFIRMASI", Messagebox.OK | Messagebox.CANCEL,
					Messagebox.QUESTION, new EventListener<Event>() {
						@Override
						public void onEvent(Event e) throws Exception {
							if (Messagebox.ON_OK.equals(e.getName())) {
								DTOMap plc_master = (DTOMap) item.getAttribute("DATA");
								plc_master.put("TGL_POS",ComponentUtil.getValue(dtTglPosisi));
								plc_master.put("PLCID", ComponentUtil.getValue(cmbJnsPenempatan));
								plc_master.put("ACCNBR",ComponentUtil.getValue(txtNoRekening));
								plc_master.put("PK", "TGL_POS,PLCID,ACCNBR");
								masterService.deleteData(plc_master, "PLC_MASTER");
								Aksi = "Penghapusan data No Rekening "+ComponentUtil.getValue(cmbJnsPenempatan);
								doLogAktfitas(Aksi);
								item.detach();
								MessageBox.showInformation("Data Berhasil Dihapus");
								doReset();
							} else if (Messagebox.ON_CANCEL.equals(e.getName())) {
								
							}
						}
					});
	}

	private boolean doValidation() {
		DTOMap plc_master = new DTOMap();
		plc_master.put("TGL_POS", ComponentUtil.getValue(dtTglPosisi));
		plc_master.put("PLCID", ComponentUtil.getValue(cmbJnsPenempatan));
		plc_master.put("ACCNBR", ComponentUtil.getValue(txtNoRekening));
		
		if (ComponentUtil.getValue(cmbJnsPenempatan) == null
				|| ComponentUtil.getValue(cmbJnsPenempatan).equals("")) {
			throw new WrongValueException(cmbJnsPenempatan,
					"Jenis Penempatan harus diisi.");
		} else if (ComponentUtil.getValue(txtNoRekening) == null
				|| ComponentUtil.getValue(txtNoRekening).equals("")) {
			throw new WrongValueException(txtNoRekening,
					"No Rekening harus diisi.");
		}
		if (!onLoad){
			if(masterService.isExist(plc_master, "PLC_MASTER")) {	
				throw new WrongValueException(txtNoRekening,
						"No Rekening sudah ada.");
			}
		} else {
			return true;			
		}
		return true;
	}
	
	

	private void doEdit(DTOMap data) {
		btnSave.setLabel("Update");
		btnReset.setDisabled(false);
		if (data != null) {
			ComponentUtil.setValue(dtTglPosisi, data.getDate("TGL_POS"));
			ComponentUtil.setValue(cmbJnsPenempatan, data.get("PLCID"));
			ComponentUtil.setValue(txtNoRekening, data.get("ACCNBR"));
			ComponentUtil.setValue(txtNamaBank, data.get("BANKNM"));
			ComponentUtil.setValue(dtTglPenanaman, data.getDate("PLCDT"));
			inTenor.setValue(data.getInt("PLCPERIOD"));
			decBunga.setValue(data.getBigDecimal("PLCINT"));
			decNominal.setValue(data.getBigDecimal("NOMINAL"));
			decSaldoAkhir.setValue(data.getBigDecimal("ENDBAL"));

			onLoad = true;

			cmbJnsPenempatan.setDisabled(true);
			txtNoRekening.setDisabled(true);
			
			btnDelete.setDisabled(false);
		}
	}

	private void doLoadTable() {
		String plcid = (String) ComponentUtil.getValue(cmbJnsPenempatan);
		List dataSet = jt2
				.query("SELECT TGL_POS, PLCID, ACCNBR, PLCDT, BANKNM, NOMINAL, PLCPERIOD, PLCINT, ENDBAL FROM PLC_MASTER WHERE PLCID =? ",
						new Object[] { plcid }, new DTOMap());
		list.getItems().clear();
		if (dataSet != null && dataSet.size() > 0) {
			for (Object o : dataSet) {
				DTOMap plc_master = (DTOMap) o;
				Listitem item = new Listitem();
				item.setAttribute("DATA", plc_master);
				item.appendChild(new Listcell(plc_master.getDate("TGL_POS").toString()));
				item.appendChild(new Listcell(plc_master.getString("PLCID")));
				item.appendChild(new Listcell(plc_master.getString("ACCNBR")));
				item.appendChild(new Listcell(plc_master.getDate("PLCDT").toString()));
				item.appendChild(new Listcell(plc_master.getString("BANKNM")));
				item.appendChild(new Listcell(FunctionUtils.moneyToText(plc_master.getBigDecimal("NOMINAL"))));
				item.appendChild(new Listcell(plc_master.getInt("PLCPERIOD").toString()));
				item.appendChild(new Listcell(FunctionUtils.moneyToText(plc_master.getBigDecimal("PLCINT"))));
				item.appendChild(new Listcell(FunctionUtils.moneyToText(plc_master.getBigDecimal("ENDBAL"))));
				list.appendChild(item);
			}
		}
	}
	
}
