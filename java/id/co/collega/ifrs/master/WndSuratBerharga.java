package id.co.collega.ifrs.master;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
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
import id.co.collega.ifrs.common.FunctionUtils;
import id.co.collega.ifrs.common.JdbcTemplate;
import id.co.collega.ifrs.master.service.MasterServices;
import id.co.collega.ifrs.util.ComponentUtil;
import id.co.collega.ifrs.util.MessageBox;
import id.co.collega.v7.seed.config.AuthenticationService;

@org.springframework.stereotype.Component
@Scope("execution")
public class WndSuratBerharga extends SelectorComposer<Component>{
	@Autowired
	AuthenticationService auth;
	
	@Wire Listbox list;
	
	@Wire Textbox txtNoRekening;
	@Wire Combobox cmbJenisSurat;
	@Wire Textbox txtNamaObligasi;
	@Wire Combobox cmbPasar;
	@Wire Decimalbox decNominal;
	@Wire Intbox intTenor;
	@Wire Radiogroup radioJenisTenor;
	@Wire Datebox dtTglBeli;
	@Wire Datebox dtTglJTempo;
	@Wire Decimalbox decKupon;
	@Wire Textbox txtPembelianDari;
	@Wire Decimalbox decYtm;
	@Wire Decimalbox decHargabeli;
	@Wire Textbox txtRating;
	@Wire Textbox txtRatingRelease;
	@Wire Datebox dtRatingPeriodFrom;
	@Wire Datebox dtRatingPeriodTill;
	@Wire Textbox txtRatingLainnya;
	@Wire Decimalbox decNilaiWajarAkhir;
	@Wire Decimalbox decNilaiMtmAkhir;
	@Wire Datebox dtTglPosisi;
	
	@Wire Button firstPage;
    @Wire Button previous;
    @Wire Button next;
    @Wire Button lastPage;
    @Wire Button btnSave;
    @Wire Button btnDelete;
    @Wire Button btnReset;
    @Wire Button btnEdit;
    
    @Wire Label perData;
    @Wire Label perPage;
    @Wire Label allData;
    @Wire Label labJenisTenor;

    @Autowired MasterServices masterService;
    
    @Autowired JdbcTemplate jt2;
    
    DecimalFormat dfGlobal = new DecimalFormat("#,##0.00");
	
	@Wire Window wnd;
	
	Boolean onLoad = false;

	String Aksi;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception  {
		super.doAfterCompose(comp);
		
		cmbJenisSurat.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {
			public void onEvent(Event e) throws Exception {
				doTable();
				btnEdit.setDisabled(false);
			}
		});
		btnSave.addEventListener(Events.ON_CLICK, new EventListener() {
            public void onEvent(Event event) throws Exception {
        		doSave();
            }
        });
		btnDelete.addEventListener(Events.ON_CLICK, new EventListener() {
            public void onEvent(Event event) throws Exception {
            	if (checkPrivDelete()) {
            		doDelete();	
				}
            }
        });
		btnEdit.addEventListener(Events.ON_CLICK, new EventListener() {
            public void onEvent(Event event) throws Exception {
            	Listitem item = list.getSelectedItem();
            	
            	if(item!=null){
            		DTOMap data = (DTOMap)item.getAttribute("DATA");
            		doEdit(data);
            		btnSave.setLabel("Update");
            		btnDelete.setDisabled(false);
            	};
            }
        });
		btnReset.addEventListener(Events.ON_CLICK, new EventListener() {
            public void onEvent(Event event) throws Exception {
        		doResetAll();
        		btnSave.setLabel("Save");
            }
        });
		dtTglBeli.addEventListener(Events.ON_CHANGE, new EventListener() {
            public void onEvent(Event event) throws Exception {
				doLoadTglJtempo();
            }
        });
		radioJenisTenor.addEventListener(Events.ON_CLICK, new EventListener() {
            public void onEvent(Event event) throws Exception {
            	Integer jenisTenor=(Integer)ComponentUtil.getValue(radioJenisTenor);
            	if(jenisTenor == 01){
            		labJenisTenor.setValue("Hari");
            	}else{
            		labJenisTenor.setValue("Tahun");
            	}
            }
        });
		list.addEventListener(Events.ON_DOUBLE_CLICK, new EventListener() {
            public void onEvent(Event event) throws Exception {
            	Listitem item = list.getSelectedItem();
            	
            	if(item!=null){
            		DTOMap data = (DTOMap)item.getAttribute("DATA");
            		doEdit(data);
            		btnEdit.setDisabled(false);
            		btnSave.setLabel("Update");
            		btnDelete.setDisabled(false);
            		txtNoRekening.setDisabled(true);
            	}
            }
        });
		decKupon.addEventListener(Events.ON_BLUR, new EventListener() {
            public void onEvent(Event event) throws Exception {
        		double maxPersen = 100.0000;
        		if (decKupon.getValue() != null) {
					BigDecimal bdPersen = (BigDecimal) ComponentUtil.getValue(decKupon);
					double dPersen = bdPersen.doubleValue();
					if (dPersen > maxPersen) {
						throw new WrongValueException(decKupon,"Besar Kupon tidak boleh Melebihi 100%");
					}
				}
            }
        });
		decHargabeli.addEventListener(Events.ON_BLUR, new EventListener() {
            public void onEvent(Event event) throws Exception {
        		double maxPersen = 100.00;
        		if (decHargabeli.getValue() != null) {
					BigDecimal bdPersen = (BigDecimal) ComponentUtil.getValue(decHargabeli);
					double dPersen = bdPersen.doubleValue();
					if (dPersen > maxPersen) {
						throw new WrongValueException(decHargabeli,"Harga Beli tidak boleh Melebihi 100%");
					}
				}
            }
        });
		decYtm.addEventListener(Events.ON_BLUR, new EventListener() {
            public void onEvent(Event event) throws Exception {
        		double maxPersen = 100.00;
        		if (decYtm.getValue() != null) {
					BigDecimal bdPersen = (BigDecimal) ComponentUtil.getValue(decYtm);
					double dPersen = bdPersen.doubleValue();
					if (dPersen > maxPersen) {
						throw new WrongValueException(decYtm,"YTM tidak boleh Melebihi 100%");
					}
				}
            }
        });
		intTenor.addEventListener(Events.ON_BLUR, new EventListener() {
            public void onEvent(Event event) throws Exception {
        		doLoadTglJtempo();
            }
        });
		doLoadComboJenisSurat();
		doLoadTglPos();
	}
	
	public void doTable(){
		String SBID1=(String)ComponentUtil.getValue(cmbJenisSurat);
		list.getItems().clear();
		List listParm=jt2.query("Select * From SB_MASTER where SBID=?",new Object[]{SBID1},new DTOMap());
		if (listParm.size()>0){
			System.out.println(listParm.size());
			for(Object o : listParm){
				DTOMap parm = (DTOMap) o;
				Listitem item = new Listitem();
				item.setAttribute("DATA",parm);
				item.appendChild(new Listcell(parm.getDate("TGL_POS").toString()));
				item.appendChild(new Listcell(parm.getString("ACCNBR")));
				item.appendChild(new Listcell(parm.getString("SBID")));
				item.appendChild(new Listcell(parm.getDate("SBSTRDT").toString()));
				item.appendChild(new Listcell(parm.getDate("SBDUEDT").toString()));
				item.appendChild(new Listcell(parm.getInt("FLGPERIOD").toString()));
				item.appendChild(new Listcell(parm.getInt("SBPERIOD").toString()));
				item.appendChild(new Listcell(parm.getString("MARKETID")));
				item.appendChild(new Listcell(FunctionUtils.moneyToText(parm.getBigDecimal("COUPONPRC"))));
				item.appendChild(new Listcell(FunctionUtils.moneyToText(parm.getBigDecimal("BUYPRICENOM"))));
				item.appendChild(new Listcell(parm.getString("BUYFROMDESC")));
				item.appendChild(new Listcell(FunctionUtils.moneyToText(parm.getBigDecimal("BUYYTMPRC"))));
				item.appendChild(new Listcell(FunctionUtils.moneyToText(parm.getBigDecimal("BUYPRICEPRC"))));
				item.appendChild(new Listcell(parm.getString("RATINGPFD")));
				item.appendChild(new Listcell(parm.getString("RATINGPFDRLS")));
				item.appendChild(new Listcell(parm.getDate("RATINGFROMDT").toString()));
				item.appendChild(new Listcell(parm.getDate("RATINGTILLDT").toString()));
				item.appendChild(new Listcell(parm.getString("RATINGOTH")));
				item.appendChild(new Listcell(FunctionUtils.moneyToText(parm.getBigDecimal("FAIRVALUENOM"))));
				item.appendChild(new Listcell(FunctionUtils.moneyToText(parm.getBigDecimal("MTMNOM"))));
				list.appendChild(item);
			}
		}
	}
	public void doSave(){
		if (validation()){
			if(onLoad){
				if (checkPrivUpdate()) {
					doUpdate();	
				}
			}else{
				if (checkPrivInsert()) {
					doInsert();	
				}
			}
		}
	}
	
	public void doInsert(){
		try{
		if (validation()) {
			DTOMap SB_MASTER = new DTOMap();
			SB_MASTER.put("TGL_POS", ComponentUtil.getValue(dtTglPosisi));
			SB_MASTER.put("SBID", ComponentUtil.getValue(cmbJenisSurat));
			SB_MASTER.put("ACCNBR", ComponentUtil.getValue(txtNoRekening));
			SB_MASTER.put("SBSTRDT", ComponentUtil.getValue(dtTglBeli));
			SB_MASTER.put("SBDUEDT", ComponentUtil.getValue(dtTglJTempo));
			SB_MASTER.put("FLGPERIOD", ComponentUtil.getValue(intTenor));
			SB_MASTER.put("SBPERIOD", ComponentUtil.getValue(radioJenisTenor));
			SB_MASTER.put("MARKETID", ComponentUtil.getValue(cmbPasar));
			SB_MASTER.put("COUPONPRC", ComponentUtil.getValue(decKupon));
			SB_MASTER.put("BUYPRICENOM", ComponentUtil.getValue(decNominal));
			SB_MASTER.put("BUYFROMDESC", ComponentUtil.getValue(txtPembelianDari));
			SB_MASTER.put("BUYYTMPRC", ComponentUtil.getValue(decYtm));
			SB_MASTER.put("BUYPRICEPRC", ComponentUtil.getValue(decHargabeli));
			SB_MASTER.put("RATINGPFD", ComponentUtil.getValue(txtRating));
			SB_MASTER.put("RATINGPFDRLS", ComponentUtil.getValue(txtRatingRelease));
			SB_MASTER.put("RATINGFROMDT", ComponentUtil.getValue(dtRatingPeriodFrom));
			SB_MASTER.put("RATINGTILLDT", ComponentUtil.getValue(dtRatingPeriodTill));
			SB_MASTER.put("RATINGOTH", ComponentUtil.getValue(txtRatingLainnya));
			SB_MASTER.put("FAIRVALUENOM", ComponentUtil.getValue(decNilaiWajarAkhir));
			SB_MASTER.put("MTMNOM", ComponentUtil.getValue(decNilaiMtmAkhir));
			SB_MASTER.put("CRTUSER",auth.getUserDetails().getUserId());
			SB_MASTER.put("CRTDATE", new Date());
			masterService.insertData(SB_MASTER, "SB_MASTER");
			Aksi = "Penambahan data "+ ComponentUtil.getValue(cmbJenisSurat) +", "+ComponentUtil.getValue(txtNoRekening);
			doLogAktfitas(Aksi);
			MessageBox.showInformation("Data berhasil di-simpan.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.showError(e.getMessage());
		}
		doReset();
	}
	
	private void doUpdate(){
		try{
			DTOMap SB_MASTER = new DTOMap();
			SB_MASTER.put("TGL_POS", ComponentUtil.getValue(dtTglPosisi));
			SB_MASTER.put("SBID", ComponentUtil.getValue(cmbJenisSurat));
			SB_MASTER.put("ACCNBR", ComponentUtil.getValue(txtNoRekening));
			SB_MASTER.put("SBSTRDT", ComponentUtil.getValue(dtTglBeli));
			SB_MASTER.put("SBDUEDT", ComponentUtil.getValue(dtTglJTempo));
			SB_MASTER.put("FLGPERIOD", ComponentUtil.getValue(intTenor));
			SB_MASTER.put("SBPERIOD", ComponentUtil.getValue(radioJenisTenor));
			SB_MASTER.put("MARKETID", ComponentUtil.getValue(cmbPasar));
			SB_MASTER.put("COUPONPRC", ComponentUtil.getValue(decKupon));
			SB_MASTER.put("BUYPRICENOM", ComponentUtil.getValue(decNominal));
			SB_MASTER.put("BUYFROMDESC", ComponentUtil.getValue(txtPembelianDari));
			SB_MASTER.put("BUYYTMPRC", ComponentUtil.getValue(decYtm));
			SB_MASTER.put("BUYPRICEPRC", ComponentUtil.getValue(decHargabeli));
			SB_MASTER.put("RATINGPFD", ComponentUtil.getValue(txtRating));
			SB_MASTER.put("RATINGPFDRLS", ComponentUtil.getValue(txtRatingRelease));
			SB_MASTER.put("RATINGFROMDT", ComponentUtil.getValue(dtRatingPeriodFrom));
			SB_MASTER.put("RATINGTILLDT", ComponentUtil.getValue(dtRatingPeriodTill));
			SB_MASTER.put("RATINGOTH", ComponentUtil.getValue(txtRatingLainnya));
			SB_MASTER.put("FAIRVALUENOM", ComponentUtil.getValue(decNilaiWajarAkhir));
			SB_MASTER.put("MTMNOM", ComponentUtil.getValue(decNilaiMtmAkhir));
			SB_MASTER.put("UPDUSER", auth.getUserDetails().getUserId());
			SB_MASTER.put("UPDDATE", new Date());
			SB_MASTER.put("PK", "TGL_POS,SBID,ACCNBR");
			masterService.updateData(SB_MASTER, "SB_MASTER");
			Aksi = "Perubahan data "+ ComponentUtil.getValue(cmbJenisSurat) +", "+ComponentUtil.getValue(txtNoRekening);
			doLogAktfitas(Aksi);
			MessageBox.showInformation("Data berhasil di-update.");
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.showError(e.getMessage());
		}
		doReset();
	}
	private void doEdit(DTOMap data){
		if (data != null){
			if (data.get("SBID") == "01") {
				ComponentUtil.setValue(txtNamaObligasi, "Obligasi Korporasi");
			}else if(data.get("SBID") == "02"){
				ComponentUtil.setValue(txtNamaObligasi, "NCD");
			}else if(data.get("SBID") == "03"){
				ComponentUtil.setValue(txtNamaObligasi, "MTN");
			}else if(data.get("SBID") == "04"){
				ComponentUtil.setValue(txtNamaObligasi, "SDBI");
			}else if(data.get("SBID") == "05"){
				ComponentUtil.setValue(txtNamaObligasi, "Obligasi Negara");
			}else if(data.get("SBID") == "06"){
				ComponentUtil.setValue(txtNamaObligasi, "Reksadana");
			}
			ComponentUtil.setValue(txtNoRekening, data.get("ACCNBR"));
			ComponentUtil.setValue(cmbPasar, data.get("MARKETID"));
			decNominal.setValue(data.getBigDecimal("BUYPRICENOM"));
			ComponentUtil.setValue(intTenor, data.get("FLGPERIOD"));
			ComponentUtil.setValue(dtTglBeli, data.get("SBSTRDT"));
			ComponentUtil.setValue(dtTglJTempo, data.get("SBDUEDT"));
			ComponentUtil.setValue(txtPembelianDari, data.get("BUYFROMDESC"));
			decKupon.setValue(data.getBigDecimal("COUPONPRC"));
			decYtm.setValue(data.getBigDecimal("BUYYTMPRC"));
			decHargabeli.setValue(data.getBigDecimal("BUYPRICEPRC"));
			ComponentUtil.setValue(txtRating, data.get("RATINGPFD"));
			ComponentUtil.setValue(txtRatingRelease, data.get("RATINGPFDRLS"));
			ComponentUtil.setValue(dtRatingPeriodFrom, data.get("RATINGFROMDT"));
			ComponentUtil.setValue(dtRatingPeriodTill, data.get("RATINGTILLDT"));
			ComponentUtil.setValue(txtRatingLainnya, data.get("RATINGOTH"));
			decNilaiWajarAkhir.setValue(data.getBigDecimal("FAIRVALUENOM"));
			decNilaiMtmAkhir.setValue(data.getBigDecimal("MTMNOM"));
			System.out.println(data.get("MARKETID"));
			
			onLoad = true;
			
		}else{
			MessageBox.showInformation("UPDATE GAGAL");
		}
	}
	
	private void doSearch(){
		DTOMap SB_MASTER = new DTOMap();
		SB_MASTER = masterService.getMapMaster("SELECT * FROM SB_MASTER WHERE ACCNBR=?", new Object[]{ComponentUtil.getValue(txtNoRekening)});
		doEdit(SB_MASTER);
	}
	
	public boolean validation(){

		DTOMap SB_MASTER = new DTOMap();
		SB_MASTER.put("TGL_POS", ComponentUtil.getValue(dtTglPosisi));
		SB_MASTER.put("SBID", ComponentUtil.getValue(cmbJenisSurat));
		SB_MASTER.put("ACCNBR", ComponentUtil.getValue(txtNoRekening));
		
		if (ComponentUtil.getValue(cmbJenisSurat) == null || ComponentUtil.getValue(cmbJenisSurat).equals("")) {
			throw new WrongValueException(cmbJenisSurat, "Jenis Surat Berharga harus diisi.");
		}else if (ComponentUtil.getValue(txtNoRekening) == null || ComponentUtil.getValue(txtNoRekening).equals("")) {
			throw new WrongValueException(txtNoRekening, "No Rekening harus diisi.");
		}else if (ComponentUtil.getValue(dtTglBeli) == null || ComponentUtil.getValue(dtTglBeli).equals("")) {
			throw new WrongValueException(dtTglBeli, "Tanggal Mulai Surat harus diisi.");
		}else if (ComponentUtil.getValue(dtTglJTempo) == null || ComponentUtil.getValue(dtTglJTempo).equals("")) {
			throw new WrongValueException(dtTglJTempo, "Tanggal Jatuh Tempo harus diisi.");
		}
		
		if (!onLoad) {
			if(masterService.isExist(SB_MASTER, "SB_MASTER")){
				throw new WrongValueException(txtNoRekening, "No. Rekening Sudah Ada");
			}
		}
		return true;
	}
	
	private void doDelete(){
		final Listitem item = list.getSelectedItem();
		if (item != null)
		Messagebox.show("Apakah Anda Yakin mau menghapus data ini .. ?",
			"KONFIRMASI", Messagebox.OK | Messagebox.CANCEL,
			Messagebox.QUESTION, new EventListener<Event>() {
				@Override
				public void onEvent(Event e) throws Exception {
					if (Messagebox.ON_OK.equals(e.getName())) {

						DTOMap SB_MASTER = new DTOMap();
						SB_MASTER.put("TGL_POS",ComponentUtil.getValue(dtTglPosisi));
						SB_MASTER.put("SBID", ComponentUtil.getValue(cmbJenisSurat));
						SB_MASTER.put("ACCNBR", ComponentUtil.getValue(txtNoRekening));
						SB_MASTER.put("PK", "TGL_POS,SBID,ACCNBR");
						masterService.deleteData(SB_MASTER, "SB_MASTER");
						item.detach();
						Aksi = "Penghapusan data "+ ComponentUtil.getValue(cmbJenisSurat) +", "+ComponentUtil.getValue(txtNoRekening);
						doLogAktfitas(Aksi);
						MessageBox.showInformation("Data Berhasil Dihapus");
						doReset();
					} else if (Messagebox.ON_CANCEL.equals(e.getName())) {
						
					}
				}
			});
	}
	
	public void doReset(){
		ComponentUtil.setValue(txtNoRekening,null);
		ComponentUtil.setValue(txtNamaObligasi, null);
		ComponentUtil.setValue(decNominal, null);
		ComponentUtil.setValue(intTenor,null);
		ComponentUtil.setValue(dtTglBeli,null);
		ComponentUtil.setValue(dtTglJTempo,null);
		ComponentUtil.setValue(txtPembelianDari,null);
		ComponentUtil.setValue(decKupon, null);
		ComponentUtil.setValue(decYtm, null);
		ComponentUtil.setValue(decHargabeli, null);
		ComponentUtil.setValue(txtRating,"");
		ComponentUtil.setValue(txtRatingRelease,"");
		ComponentUtil.setValue(dtRatingPeriodFrom,"");
		ComponentUtil.setValue(dtRatingPeriodTill,"");
		ComponentUtil.setValue(txtRatingLainnya,"");
		ComponentUtil.setValue(decNilaiWajarAkhir, null);
		ComponentUtil.setValue(decNilaiMtmAkhir, null);
		
		doTable();
		
		decKupon.setFormat("##0.0000");
		btnDelete.setDisabled(true);
		txtNoRekening.setDisabled(false);
	}
	
	private void doLoadComboJenisSurat() {
		cmbJenisSurat.getItems().clear();
		cmbJenisSurat.setSelectedIndex(-1);
		List<DTOMap> listData = (List<DTOMap>) masterService.getDataMaster("SELECT PARMID, PARMNM from CFG_PARM Where PARMGRP=23", null);
		Comboitem item = new Comboitem();
		cmbJenisSurat.appendChild(item);
		for (DTOMap map : listData) {
			item = new Comboitem();
			item.setLabel(map.getString("PARMID") + " - " + map.getString("PARMNM"));
			item.setValue(map.getString("PARMID"));
			cmbJenisSurat.appendChild(item);
		}
	}
	private void doLoadTglPos(){
		DTOMap SB_MASTER = new DTOMap();
		SB_MASTER = masterService.getMapMaster("SELECT OPEN_DATE FROM CFG_SYS", null);
		ComponentUtil.setValue(dtTglPosisi, SB_MASTER.getDate("OPEN_DATE"));
		
	}
	private void doResetAll(){
		ComponentUtil.clear(wnd);
		doLoadTglPos();
		decKupon.setFormat("##0.0000");
		txtNoRekening.setDisabled(false);
		cmbJenisSurat.setDisabled(false);
	}
	private void doLoadTglJtempo() throws ParseException{
		Date tglMulai = (Date) ComponentUtil.getValue(dtTglBeli);
		Integer Tenor=(Integer)ComponentUtil.getValue(intTenor);
		Integer jenisTenor=(Integer)ComponentUtil.getValue(radioJenisTenor);
		
		if (Tenor == null) {
    		throw new WrongValueException(intTenor,"Isi Tenor terlebih dahulu");
		}
		
		if (tglMulai == null) {
    		throw new WrongValueException(dtTglBeli,"Tgl. Mulai harus diisi");
		}
		
		Calendar calendar = Calendar.getInstance();
		
		calendar.setTime(tglMulai);
		if (jenisTenor == 1) {
			calendar.set(Calendar.DATE,tglMulai.getDate()+Tenor);
		}else{
			calendar.set(Calendar.YEAR,calendar.get(Calendar.YEAR)+Tenor);
		}
		ComponentUtil.setValue(dtTglJTempo, calendar.getTime());
	}
}
