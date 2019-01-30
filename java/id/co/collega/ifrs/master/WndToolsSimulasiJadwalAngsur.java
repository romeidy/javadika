package id.co.collega.ifrs.master;


import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Window;

import id.co.collega.ifrs.common.DTOMap;
import id.co.collega.ifrs.common.FunctionUtils;
import id.co.collega.ifrs.common.JRreportWindow;
import id.co.collega.ifrs.common.JadwalAngsur;
import id.co.collega.ifrs.common.JdbcTemplate;
import id.co.collega.ifrs.master.service.MasterServices;
import id.co.collega.ifrs.util.ComponentUtil;
import id.co.collega.v7.ef.common.DataSession;
import id.co.collega.v7.seed.config.AuthenticationService;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@org.springframework.stereotype.Component
@Scope("execution")
public class WndToolsSimulasiJadwalAngsur  extends SelectorComposer<Component>{

	public JadwalAngsur jadwalAngsur=new JadwalAngsur();
	private List<DTOMap> listAngsuran =new ArrayList<DTOMap>(0);;
	private double irr = 0, irrAtribusi=0, irrDiskon=0;
	private double totPokok = 0, totBunga = 0, totAngsur = 0;
	private double estimasiArusKasTot = 0;
	private double bungaKonversiTot = 0;
	private double angsuranPokokTot = 0;
	private double angsuranBungaTot = 0;
	private double selisihBungaKontraktualTot = 0;
	private double estimasiArusKasAtribusiTot = 0;
	private double nilaiKiniArusKasTot = 0;
	private double nilaiKini = 0;
	private double bungaAtribusiTot = 0;
	private double amortisasiTot = 0;
	private double estimasiArusKasDiscountTot = 0;
	private double bungaDiscountTot = 0;
	private double amortisasiDiscountTot = 0;
	private DTOMap tipeBunga = new DTOMap();
	private double irrSemua = 0;
	
	@Autowired Environment env;
	@Autowired JdbcTemplate jt;
	@Autowired AuthenticationService auth;
	
	public DataSession dataSession;
	@Wire Window 	wnd;
	@Wire Datebox	txtTglAkhir;
	@Wire Datebox	txtTglMulai;
	@Wire Intbox 	txtTenor;
	@Wire Intbox	txtPeriodPokok;
	@Wire Intbox 	txtPeriodBunga;
	@Wire Intbox 	txtTenggangPokok;
	@Wire Intbox 	txtTenggangBunga;
	@Wire Intbox 	txtDistGrpBunga;
	@Wire Radiogroup	radPokok;
	@Wire Radiogroup	radBunga;
	@Wire Combobox		cmbJnsBunga;
	@Wire Combobox		cmbHariBunga;
	@Wire Tab			tab1;
	@Wire Radiogroup	STS_CIA;
	
	@Wire Decimalbox	txtPlafon;
	@Wire Decimalbox 	decNominalProvisi;
	@Wire Decimalbox	decBiayaPerolehan;
	@Wire Decimalbox	txtDiscount;
	@Wire Decimalbox	txtBunga;
	@Wire Decimalbox	decIrr;
	@Wire Decimalbox	decIrrKonversi;
	@Wire Decimalbox	decIrrAttribusi;
	@Wire Decimalbox	decIrrDiskonRate;
	@Wire Decimalbox	decSBEAtribusi;
	@Wire Decimalbox	decSBEKonversi;
	@Wire Decimalbox	decSBEDiskon;
	@Wire Decimalbox	decSBE;
	@Wire Decimalbox	decTotBunga;
	@Wire Decimalbox	decTotPokok;
	@Wire Decimalbox	decTotJmlAngsur;
	
	@Wire Label	decArus1;
	@Wire Label	decArus2;
	@Wire Label	decArus3;
	@Wire Label	decArus4;
	@Wire Label	decArus5;
	@Wire Label	decArus6;
	@Wire Label	decArus7;
	@Wire Label	decArus8;
	@Wire Label	decArus9;
	
	@Wire Label	dec1;
	@Wire Label	dec3;
	@Wire Label	dec4;
	@Wire Label	dec5;
	@Wire Label	dec6;
	@Wire Label	dec9;
	@Wire Label	dec11;
	@Wire Label	dec12;
	@Wire Label	dec15;
	@Wire Label	dec16;
	@Wire Label	dec18;
	@Wire Label	dec19;
	
	@Wire Groupbox		grupArusDiskonRate;
	@Wire Groupbox		grupArusAttribusi;
	
	@Wire Button btnSimpan;
	@Wire Listbox listTransaksiTunai;
	@Wire Radiogroup grpStatus;
	@Wire DTOMap workflow;
	@Wire Label lbCabKon;
	@Wire Listbox listJadwalAngsur=new Listbox(); 
	@Wire Listbox listJangkaWaktu =new Listbox();
	@Wire Listbox listArusKasKonversi=new Listbox();
	@Wire Listbox listArusKasAttribusi=new Listbox();
	@Wire Listbox listArusKasDiskonRate=new Listbox();
	@Wire Listbox listboxArusKas=new Listbox();
	@Wire Label lbCab;
	
	
	@Autowired MasterServices masterService;
	Boolean onLoad = false;
	boolean isAngsImpr=false;
	private List<DTOMap> map = new ArrayList<DTOMap>(0);
	private List<DTOMap> listdatatunai;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception  {
		super.doAfterCompose(comp);
		
		List<DTOMap> listData = (List<DTOMap>) masterService.getDataMaster(" SELECT PARMID,PARMNM,PARMIDOTH from CFG_PARM "
				+ "																WHERE PARMGRP=15 ORDER BY PARMID "
								,new Object[]{});
		Comboitem ciJnsBunga = new Comboitem();
		if (listData.size() > 0) {
			for (DTOMap map : listData) {
				ciJnsBunga = new Comboitem();
				ciJnsBunga.setLabel(map.getString("PARMID") + " - "+ map.getString("PARMNM"));
				ciJnsBunga.setValue(map.getString("PARMIDOTH"));
				cmbJnsBunga.appendChild(ciJnsBunga);
			}
		}
		
		Comboitem ciHrBunga = new Comboitem();
		ciHrBunga.setLabel("360");
		ciHrBunga.setValue(360);
		cmbHariBunga.appendChild(ciHrBunga);
		cmbHariBunga.setSelectedIndex(0);
		
		txtBunga.setValue(BigDecimal.ZERO);
		txtDiscount.setValue(BigDecimal.ZERO);
		txtPlafon.setValue(BigDecimal.ZERO);
		decNominalProvisi.setValue(BigDecimal.ZERO);
		decBiayaPerolehan.setValue(BigDecimal.ZERO);
		
		ComponentUtil.setValue(txtTenor, 12);
//		ComponentUtil.setValue(txtTglMulai, dataSession.getOpenDate());
//		setJatuhTempo();

		ComponentUtil.setValue(txtPeriodPokok, 1);
		ComponentUtil.setValue(txtPeriodBunga, 1);

		ComponentUtil.setValue(txtTenggangPokok, 0);
		ComponentUtil.setValue(txtTenggangBunga, 0);
		ComponentUtil.setValue(txtDistGrpBunga, 0);

		cmbJnsBunga.addEventListener(Events.ON_SELECT, new EventListener<Event>() {
			public void onEvent(Event e) throws Exception {
				String typeInt = (String) ComponentUtil.getValue(cmbJnsBunga);
				if(typeInt != null){
					setDetailTipeBunga(typeInt);
				}
			}
		});
		tab1.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(Event e) throws Exception {
				doProses();
			}
		});
		txtTenor.addEventListener(Events.ON_BLUR, new EventListener<Event>() {
			public void onEvent(Event e) throws Exception {
				setJatuhTempo();
			}
		});
		STS_CIA.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(Event e) throws Exception {
				setJatuhTempo();
			}
		});
		txtTglMulai.addEventListener(Events.ON_BLUR, new EventListener<Event>() {
			public void onEvent(Event e) throws Exception {
				setJatuhTempo();
			}
		});
		radPokok.addEventListener("onCheck", new EventListener<Event>() {
			public void onEvent(Event e) throws Exception {
				if (ComponentUtil.getValue(radPokok).equals(2)){
					txtPeriodPokok.setDisabled(false);
				}else{
					txtPeriodPokok.setDisabled(true);
				}
			}
		});
		radBunga.addEventListener("onCheck", new EventListener<Event>() {
			public void onEvent(Event e) throws Exception {
				if (ComponentUtil.getValue(radBunga).equals(2)){
					txtPeriodBunga.setDisabled(false);
				}else{
					txtPeriodBunga.setDisabled(true);
				}
			}
		});

		txtTenggangPokok.addEventListener(Events.ON_OK, new EventListener<Event>() {
			public void onEvent(Event e) throws Exception {
				Integer tenggangPokok = (Integer) ComponentUtil.getValue(txtTenggangPokok);
				Integer tenor = (Integer) ComponentUtil.getValue(txtTenor);
				Integer periode = (Integer) ComponentUtil.getValue(txtPeriodPokok);
				Integer radioBtn = (Integer)ComponentUtil.getValue(radPokok);

				if (tenggangPokok == null) {
					ComponentUtil.setValue(txtTenggangPokok,0);
				} else {
					if (radioBtn == 2) {
						Integer sisawaktu = tenor.intValue() - tenggangPokok.intValue();
						if (tenggangPokok > tenor) {
							throw new WrongValueException(txtTenggangPokok, "Masa Tenggang Pokok Harus < Jangka Waktu");
						} else if (periode > sisawaktu) {
							throw new WrongValueException(txtTenggangPokok, "Sisa Jangka Waktu Harus >= Periode Pembayaran");
						}
					}
				}
			}
		});

		txtTenggangPokok.addEventListener(Events.ON_BLUR, new EventListener<Event>() {
			public void onEvent(Event e) throws Exception {
				Integer tenggangPokok = (Integer) ComponentUtil.getValue(txtTenggangPokok);
				Integer tenor = (Integer) ComponentUtil.getValue(txtTenor);
				Integer periode = (Integer) ComponentUtil.getValue(txtPeriodPokok);
				Integer radioBtn = (Integer) ComponentUtil.getValue(radPokok);

				if (tenggangPokok == null) {
					ComponentUtil.setValue(txtTenggangPokok,0);
				} else {
					if (radioBtn == 2) {
						Integer sisawaktu = tenor.intValue() - tenggangPokok.intValue();
						if (tenggangPokok > tenor) {
							throw new WrongValueException(txtTenggangPokok, "Masa Tenggang Pokok Harus <= Jangka Waktu");
						} else if (periode > sisawaktu) {
							throw new WrongValueException(txtTenggangPokok, "Sisa Jangka Waktu >= Periode Pembayaran");
						}
					}
				}
			}
		});

		txtDistGrpBunga.addEventListener(Events.ON_BLUR, new EventListener<Event>() {
			public void onEvent(Event arg0) throws Exception {
				Integer distGrpBunga = (Integer) ComponentUtil.getValue(txtDistGrpBunga);
				if (distGrpBunga == null)
					ComponentUtil.setValue(txtDistGrpBunga,0);
			}
		});
		// -----------------------------------------------------------------------------------
		txtTenggangBunga.addEventListener(Events.ON_OK, new EventListener<Event>() {
			public void onEvent(Event e) throws Exception {
				Integer tenggangBunga = (Integer) ComponentUtil.getValue(txtTenggangBunga);
				Integer tenor = (Integer) ComponentUtil.getValue(txtTenor);
				Integer periode = (Integer) ComponentUtil.getValue(txtPeriodBunga);
				Integer radioBtn = (Integer) ComponentUtil.getValue(radBunga);

				if (tenggangBunga == null) {
					ComponentUtil.setValue(txtTenggangBunga,0);
				} else {
					if (radioBtn == 2) {
						Integer sisawaktu = tenor.intValue() - tenggangBunga.intValue();
						Integer sisabagi = sisawaktu.intValue() % periode.intValue();

						if (tenggangBunga > tenor) {
							throw new WrongValueException(txtTenggangBunga, "Masa Tunda Bunga Harus < Jangka Waktu");
						} else if (periode > sisawaktu) {
							throw new WrongValueException(txtTenggangBunga, "Sisa Jangka Waktu Harus > Periode Pembayaran");
						} else if (sisabagi != 0) {
							throw new WrongValueException(txtTenggangBunga,
									"Sisa Bagi (Tenor - Masa Tunda Bunga) % Interval Pembayaran <> 0");
						}
					}
				}
			}
		});

		txtTenggangBunga.addEventListener(Events.ON_BLUR, new EventListener<Event>() {
			public void onEvent(Event e) throws Exception {
				Integer tenggangBunga = (Integer) ComponentUtil.getValue(txtTenggangBunga);
				Integer tenor = (Integer) ComponentUtil.getValue(txtTenor);
				Integer periode = (Integer) ComponentUtil.getValue(txtPeriodBunga);
				Integer radioBtn = (Integer) ComponentUtil.getValue(radBunga);

				if (tenggangBunga == null) {
					ComponentUtil.setValue(txtTenggangBunga,0);
				} else {
					if (radioBtn == 2) {
						Integer sisawaktu = tenor.intValue() - tenggangBunga.intValue();
						if (tenggangBunga > tenor) {
							throw new WrongValueException(txtTenggangBunga, "Masa Tunda Bunga Harus < Jangka Waktu");
						} else if (periode > sisawaktu) {
							throw new WrongValueException(txtTenggangBunga, "Sisa Jangka Waktu Harus > Periode Pembayaran");
						}
					}
				}
			}
		});

		// -----------------------------------------------------------------------------------
		txtPeriodPokok.addEventListener(Events.ON_OK, new EventListener<Event>() {
			public void onEvent(Event e) throws Exception {
				Integer Period = (Integer) ComponentUtil.getValue(txtPeriodPokok);
				Integer JP = (Integer) ComponentUtil.getValue(radPokok);
				if (Period != null) {
					if (JP.equals(2)) {
						if (Period < 1 || Period > 999) {
							throw new WrongValueException(txtPeriodPokok,
									"Periode Pembayaran Pokok Harus 1 s.d 999");
						}
					} else {
						if (Period < 1 || Period > 999) {
							throw new WrongValueException(txtPeriodPokok,
									"Periode Pembayaran Pokok Harus 1 s.d 999");
						}
					}
				} else {
					if (JP.equals(2)) {
						ComponentUtil.setValue(txtPeriodPokok,1);
					} else {
						ComponentUtil.setValue(txtPeriodPokok,0);
					}
				}
			}
		});

		txtPeriodPokok.addEventListener(Events.ON_BLUR, new EventListener<Event>() {
			public void onEvent(Event e) throws Exception {
				Integer Period = (Integer) ComponentUtil.getValue(txtPeriodPokok);
				Integer JP = (Integer) ComponentUtil.getValue(radPokok);
				if (Period != null) {
					if (JP.equals(2)) {
						if (Period < 1 || Period > 999) {
							throw new WrongValueException(txtPeriodPokok,
									"Periode Pembayaran Pokok Harus 1 s.d 999");
						}
					} else {
						if (Period < 1 || Period > 999) {
							throw new WrongValueException(txtPeriodPokok,
									"Periode Pembayaran Pokok Harus 1 s.d 999");
						}
					}
				} else {
					if (JP.equals(2)) {
						ComponentUtil.setValue(txtPeriodPokok,1);
					} else {
						ComponentUtil.setValue(txtPeriodPokok,0);
					}
				}
			}
		});
		// -----------------------------------------------------------------------------------
		txtPeriodBunga.addEventListener(Events.ON_OK, new EventListener<Event>() {
			public void onEvent(Event e) throws Exception {
				Integer PeriodBunga = (Integer) ComponentUtil.getValue(txtPeriodBunga);
				Integer JP = (Integer) ComponentUtil.getValue(radBunga);
				if (PeriodBunga != null) {
					if (JP.equals(2)) {
						if (PeriodBunga < 1 || PeriodBunga > 999) {
							throw new WrongValueException(wnd.getFellow("intPeriodBunga"),
									"Periode Pembayaran Bunga Harus 1 s.d 999");
						}
					} else {
						if (PeriodBunga < 1 || PeriodBunga > 999) {
							throw new WrongValueException(wnd.getFellow("intPeriodBunga"),
									"Periode Pembayaran Bunga Harus 1 s.d 999");
						}
					}
				} else {
					if (JP.equals(2)) {
						ComponentUtil.setValue(txtPeriodBunga,1);
					} else {
						ComponentUtil.setValue(txtPeriodBunga,0);
					}
				}
			}
		});

		txtPeriodBunga.addEventListener(Events.ON_BLUR, new EventListener<Event>() {
			public void onEvent(Event e) throws Exception {
				Integer PeriodBunga = (Integer) ComponentUtil.getValue(txtPeriodBunga);
				Integer JP = (Integer) ComponentUtil.getValue(radBunga);
				if (PeriodBunga != null) {
					if (JP.equals(2)) {
						if (PeriodBunga < 1 || PeriodBunga > 999) {
							throw new WrongValueException(txtPeriodBunga,
									"Periode Pembayaran Bunga Harus 1 s.d 999");
						}
					} else {
						if (PeriodBunga < 1 || PeriodBunga > 999) {
							throw new WrongValueException(txtPeriodBunga,
									"Periode Pembayaran Bunga Harus 1 s.d 999");
						}
					}
				} else {
					if (JP.equals(2)) {
						ComponentUtil.setValue(txtPeriodBunga,1);
					} else {
						ComponentUtil.setValue(txtPeriodBunga,0);
					}
				}
			}
		});
		
		txtPlafon.addEventListener(Events.ON_BLUR, new EventListener<Event>() {
			public void onEvent(Event e) throws Exception {
				BigDecimal decPlafon = (BigDecimal) ComponentUtil.getValue(txtPlafon);
				if(decPlafon != null){
					BigDecimal nominalProvisi = (BigDecimal) ComponentUtil.getValue(decNominalProvisi);
					if(nominalProvisi == null)nominalProvisi= new BigDecimal(0);
					BigDecimal biayaPerolehan = (BigDecimal) ComponentUtil.getValue(decBiayaPerolehan);
					if(biayaPerolehan == null)biayaPerolehan= new BigDecimal(0);
					if(decPlafon.doubleValue() < (nominalProvisi.doubleValue()-biayaPerolehan.doubleValue())){
						throw new WrongValueException(txtPlafon,
						"Nilai Maksimum Kredit Lebih Kecil Dari Provisi dan Biaya");
					}
				}
			}
		});
		decNominalProvisi.addEventListener(Events.ON_BLUR, new EventListener<Event>() {
			public void onEvent(Event e) throws Exception {
				BigDecimal nominalProvisi = (BigDecimal) ComponentUtil.getValue(decNominalProvisi);
				if(nominalProvisi != null){
					BigDecimal decPlafon = (BigDecimal) ComponentUtil.getValue(txtPlafon);
					if(decPlafon == null)decPlafon= new BigDecimal(0);
					BigDecimal biayaPerolehan = (BigDecimal) ComponentUtil.getValue(decBiayaPerolehan);
					if(biayaPerolehan == null)biayaPerolehan= new BigDecimal(0);
					if((nominalProvisi.subtract(biayaPerolehan).abs().doubleValue()) > (decPlafon.doubleValue())){
						throw new WrongValueException(decNominalProvisi,
						"Nilai Provisi Lebih Besar Dari Nilai Maksimum Kredit");
					}
				}
			}
		});
		decBiayaPerolehan.addEventListener(Events.ON_BLUR, new EventListener<Event>() {
			public void onEvent(Event e) throws Exception {
				BigDecimal biayaPerolehan = (BigDecimal) ComponentUtil.getValue(decBiayaPerolehan);
				if(decBiayaPerolehan != null){
					BigDecimal decPlafon = (BigDecimal) ComponentUtil.getValue(txtPlafon);
					if(decPlafon == null)decPlafon= new BigDecimal(0);
					BigDecimal nominalProvisi = (BigDecimal) ComponentUtil.getValue(decNominalProvisi);
					if(nominalProvisi == null)nominalProvisi= new BigDecimal(0);
					if((nominalProvisi.subtract(biayaPerolehan).abs().doubleValue()) > (decPlafon.doubleValue())){
						throw new WrongValueException(decBiayaPerolehan,
						"Nilai Biaya Lebih Besar Dari Nilai Maksimum Kredit");
					}
				}
			}
		});
	}
	
	private void setJatuhTempo(){
		Date tglMulai = (Date) ComponentUtil.getValue(txtTglMulai);
		Integer tenor = (Integer) ComponentUtil.getValue(txtTenor);
		Integer stsCIA = (Integer) ComponentUtil.getValue(STS_CIA);
		if (tglMulai != null && tenor != null) {
			if (tipeBunga!=null) {
				if( ("I".equals(tipeBunga.getString("KINDINT")) || stsCIA == 1)){
					tenor = tenor - 1;
				}
			}
			Calendar cal = Calendar.getInstance();
			cal.setTime(tglMulai);
			cal.add(Calendar.MONTH, tenor);
			ComponentUtil.setValue(txtTglAkhir,cal.getTime());
		} else {
			ComponentUtil.setValue(txtTglAkhir,null);
		}
	}
	
	private void setDetailTipeBunga(String typeInt){
		tipeBunga = (DTOMap) masterService.getMapMaster(
				"SELECT PARMIDOTH AS KINDINT,VIEWORD AS TYPEINT "
				+ "	FROM CFG_PARM "
				+ "	WHERE PARMGRP=15 "
				+ "		AND PARMIDOTH=? "
				+ "		AND PARMIDOTH <>'K' ", 
				new Object[] {typeInt});
	}

	public void doProses() {
		if (validation()) {
			isAngsImpr=false;
//			jadwalAngsur = (EngineScript) createEngineClass("JadwalAngsur");
			String jnsBunga=(String)ComponentUtil.getValue(cmbJnsBunga);
			Date tglMulai = (Date) ComponentUtil.getValue(txtTglMulai);
			int jangkaWaktu = (Integer) ComponentUtil.getValue(txtTenor);
			int periodePokok = jangkaWaktu;
			if (!ComponentUtil.getValue(radPokok).equals(0))
				periodePokok = (Integer) ComponentUtil.getValue(txtPeriodPokok);
			int periodeBunga = jangkaWaktu;
			if (!ComponentUtil.getValue(radBunga).equals(0))
				periodeBunga = (Integer) ComponentUtil.getValue(txtPeriodBunga);
			int tenggangPokok = (Integer) ComponentUtil.getValue(txtTenggangPokok);
			int tenggangBunga = (Integer) ComponentUtil.getValue(txtTenggangBunga);
			int distGrpBunga = (Integer) ComponentUtil.getValue(txtDistGrpBunga);
			BigDecimal plafond = (BigDecimal) ComponentUtil.getValue(txtPlafon);
			BigDecimal provFee = (BigDecimal) ComponentUtil.getValue(decNominalProvisi);
			BigDecimal getFee = (BigDecimal) ComponentUtil.getValue(decBiayaPerolehan);
			BigDecimal bunga = (BigDecimal) ComponentUtil.getValue(txtBunga);
			BigDecimal discount = (BigDecimal) ComponentUtil.getValue(txtDiscount);
//			System.out.println("cmbHariBunga"+ComponentUtil.getValue(cmbHariBunga));
//			jadwalAngsur.jumlahHariTahun= (int) ComponentUtil.getValue(cmbHariBunga);
			System.out.println("Jenis : "+FunctionUtils.getDigitAt(tipeBunga.getInt("TYPEINT"), 3));
			System.out.println("Round : "+FunctionUtils.getDigitAt(tipeBunga.getInt("TYPEINT"), 2));
			System.out.println("StsRound : "+FunctionUtils.getDigitAt(tipeBunga.getInt("TYPEINT"), 1));
			jadwalAngsur.setStatusCIA((Integer)ComponentUtil.getValue(STS_CIA));
			listAngsuran = jadwalAngsur.doGenerateAngsuran(tglMulai, jangkaWaktu, periodePokok, periodeBunga, tenggangPokok, tenggangBunga,
					distGrpBunga, plafond, provFee, getFee, bunga.subtract(discount), discount, tipeBunga.getString("KINDINT"), 
					FunctionUtils.getDigitAt(tipeBunga.getInt("TYPEINT"), 3),
					FunctionUtils.getDigitAt(tipeBunga.getInt("TYPEINT"), 2), 
					FunctionUtils.getDigitAt(tipeBunga.getInt("TYPEINT"), 1));
			irr = jadwalAngsur.getIrr() * 100;
			irrAtribusi = jadwalAngsur.getIrrAtribusi() * 100;
			irrDiskon = jadwalAngsur.getIrrDiskon() * 100;
			System.out.println("BANYAK DATA= "+listAngsuran.size());
			doSetAngsur(listAngsuran);
			
			
//			int grp = 0;
//			double a = 0;
			
			double idec_bungaeff = 0;
			if(irrDiskon > 0){
				idec_bungaeff = irrDiskon;
			}else if(irrAtribusi > 0){
				idec_bungaeff = irrAtribusi;
			}else if(irr > 0){
				idec_bungaeff = irr;
			}
			isAngsImpr = true;
			irrSemua = idec_bungaeff;
			listAngsuran = jadwalAngsur.doGenerateAngsuranImpairment(tglMulai, jangkaWaktu, periodePokok, periodeBunga, tenggangPokok, tenggangBunga,
					distGrpBunga, plafond, provFee, getFee, bunga.subtract(discount), discount, tipeBunga.getString("KINDINT"), 
					FunctionUtils.getDigitAt(tipeBunga.getInt("TYPEINT"), 3),
					FunctionUtils.getDigitAt(tipeBunga.getInt("TYPEINT"), 2), 
					FunctionUtils.getDigitAt(tipeBunga.getInt("TYPEINT"), 1),
					new BigDecimal(irrSemua));
			doSetAngsur(listAngsuran);
		}
	}
	
	/*public void doSetAngsurImp(List listArusKasKonversi,String JenisBunga) {
		listboxJadwalAngsurImpair = (Listbox) wndEngine.getFellow("listJadwalAngsurBaru");
		listboxJadwalAngsurImpair.getItems().clear();
		Listbox listboxJangkaWaktu = (Listbox) wndEngine.getFellow("listJangkaWaktuImpairBaru");
		listboxJangkaWaktu.getItems().clear();
		listBoxArusKasImpair = (Listbox) wndEngine.getFellow("listArusKasImpairBaru");
		listBoxArusKasImpair.getItems().clear();
		wndEngine.setFellowValue("decIrrImpairBaru", new BigDecimal(0));
		totPokok = 0;
		totBunga = 0;
		totAngsur = 0;
		estimasiArusKasTot = 0;
		bungaKonversiTot = 0;
		angsuranPokokTot = 0;
		angsuranBungaTot = 0;
		selisihBungaKontraktualTot = 0;
		nilaiKiniArusKasTot = 0;
		estimasiArusKasDiscountTot = 0;
		bungaDiscountTot = 0;
		amortisasiDiscountTot = 0;
		estimasiArusKasAtribusiTot = 0;
		bungaAtribusiTot = 0;
		amortisasiTot = 0;
		nilaiKini = 0;
		int i = 0;
		saldoMustNol = new BigDecimal(0);
		if(JenisBunga.equals("0000")){
			saldoMustNol = (BigDecimal) wndEngine.getFellowValue("decPlafodBaru");
		}
		kindINT = (String) getJdbcTemplate().queryObject(
				"Select KINDINT From CFG_INTTYPEROUND Where TYPEINTID = ? ", new Object[] { MST_LOAN.get("TYPEINTID") }, String.class);
		double totalBungaKonversi=0;
		for (Object o : listArusKasKonversi) {
			DTOMap dtoMap = (DTOMap) o;
			Listitem itemAngsur = new Listitem();
			//itemAngsur.setId("Jadwal"+dtoMap.get("jadwalAngsurKe"));
			itemAngsur.setAttribute("DATA", dtoMap);
			itemAngsur.appendChild(new Listcell(dtoMap.get("jadwalAngsurKe")));
			itemAngsur.appendChild(new Listcell(dtoMap.get("tglJadwal")));
			//Decimal Untuk Pokok
			final Decimalbox decimalboxPokok = new Decimalbox();
			if(!JenisBunga.equals("0000")){
				decimalboxPokok.setDisabled(true);
			}else{
				decimalboxPokok.setDisabled(false);
			}
			decimalboxPokok.setStyle("text-align:right;");
			decimalboxPokok.setWidth("95%");
			decimalboxPokok.setFormat("#,##0");
			decimalboxPokok.setId("Pokok"+dtoMap.get("jadwalAngsurKe"));
			decimalboxPokok.setValue(dtoMap.getBigDecimal("angsuranPokok"));
			
			org.zkoss.zul.Listcell lcPokok = new org.zkoss.zul.Listcell();
			lcPokok.appendChild(decimalboxPokok);
			itemAngsur.appendChild(lcPokok);
			//Decimal Untuk Pokok
			//Decimal Untuk Bunga
			final Decimalbox decimalboxBunga = new Decimalbox();
			if(!JenisBunga.equals("0000")){
				decimalboxBunga.setDisabled(true);
			}else{
				decimalboxBunga.setDisabled(false);
			}
			decimalboxBunga.setStyle("text-align:right;");
			decimalboxBunga.setWidth("95%");
			decimalboxBunga.setFormat("#,##0");
			decimalboxBunga.setId("Bunga"+dtoMap.get("jadwalAngsurKe"));
			decimalboxBunga.setValue(dtoMap.getBigDecimal("angsuranBunga"));
			org.zkoss.zul.Listcell lcBunga = new org.zkoss.zul.Listcell();
			lcBunga.appendChild(decimalboxBunga);
			itemAngsur.appendChild(lcBunga);
			//Decimal Untuk Bunga
			
			//Dikosongkan
			if(JenisBunga.equals("0000")){
				//listener
				decimalboxPokok.addEventListener("onBlur", new EventListener() {
					public void onEvent(Event e) throws Exception {
						BigDecimal pokok = decimalboxPokok.getValue();
						if(pokok==null){
							pokok = new BigDecimal(0);
							decimalboxPokok.setValue(pokok);
						}
						if(pokok.doubleValue() < 0){
							throw new WrongValueException(decimalboxPokok, "Nilai Pokok Tidak Boleh Negatif.");
						}
					}
				});
				decimalboxPokok.addEventListener("onChange", new EventListener() {
					public void onEvent(Event e) throws Exception {
						BigDecimal pokok = decimalboxPokok.getValue();
						if(pokok==null){
							pokok = new BigDecimal(0);
							decimalboxPokok.setValue(pokok);
						}
						if(pokok.doubleValue() < 0){
							throw new WrongValueException(decimalboxPokok, "Nilai Pokok Tidak Boleh Negatif.");
						}
						
						String sid=decimalboxPokok.getId();
						sid=sid.replace("Pokok", "");
						Integer id = Integer.valueOf(sid);
						
						hitungAngsuran(id,pokok,"Pokok");
					}
				});
				
				decimalboxBunga.addEventListener("onBlur", new EventListener() {
					public void onEvent(Event e) throws Exception {
						BigDecimal bunga = decimalboxBunga.getValue();
						if(bunga==null){
							bunga = new BigDecimal(0);
							decimalboxBunga.setValue(bunga);
						}
						if(bunga.doubleValue() < 0){
							throw new WrongValueException(decimalboxBunga, "Nilai Bunga Tidak Boleh Negatif.");
						}
					}
				});
				decimalboxBunga.addEventListener("onChange", new EventListener() {
					public void onEvent(Event e) throws Exception {
						BigDecimal bunga = decimalboxBunga.getValue();
						if(bunga==null){
							bunga = new BigDecimal(0);
							decimalboxBunga.setValue(bunga);
						}
						if(bunga.doubleValue() < 0){
							throw new WrongValueException(decimalboxBunga, "Nilai Bunga Tidak Boleh Negatif.");
						}
						
						String sid=decimalboxBunga.getId();
						sid=sid.replace("Bunga", "");
						Integer id = Integer.valueOf(sid);
						hitungAngsuran(id,bunga,"Bunga");
					}
				});
				//listener
				if(dtoMap.getBigDecimal("angsuranPokok").doubleValue()==0){
					decimalboxPokok.setDisabled(true);
				}else{
					decimalboxPokok.setValue(new BigDecimal(0));
					dtoMap.put("angsuranPokok",new BigDecimal(0));
				}
				if(dtoMap.getBigDecimal("angsuranBunga").doubleValue()==0){
					decimalboxBunga.setDisabled(true);
				}else{
					decimalboxBunga.setValue(new BigDecimal(0));
					dtoMap.put("angsuranBunga",new BigDecimal(0));
				}
				BigDecimal totalAngsuran = new BigDecimal(0);
				BigDecimal saldoTeoritis = (BigDecimal) wndEngine.getFellowValue("decPlafodBaru");
				dtoMap.put("totalAngsuran",totalAngsuran);
				dtoMap.put("saldoTeoritis",saldoTeoritis);
				
				if(i==0){
					dtoMap.put("SALDO_AWAL_KONVERSI", BigDecimal.ZERO);
					dtoMap.put("SALDO_AKHIR_KONVERSI", saldoTeoritis);
				}else{
					Listitem itemSebelum=listboxJadwalAngsurImpair.getItemAtIndex(i-1);
					DTOMap angsurSebelum = (DTOMap) itemSebelum.getAttribute("DATA");
					dtoMap.put("SALDO_AWAL_KONVERSI", angsurSebelum.getBigDecimal("SALDO_AKHIR_KONVERSI"));
					dtoMap.put("SALDO_AKHIR_KONVERSI",angsurSebelum.getBigDecimal("SALDO_AKHIR_KONVERSI"));
				}
				dtoMap.put("ESTIMASI_ARUS", BigDecimal.ZERO);
				dtoMap.put("BUNGA_KONVERSI", BigDecimal.ZERO);
				dtoMap.put("POKOK_ARUS",BigDecimal.ZERO);
				dtoMap.put("BUNGA_ARUS",BigDecimal.ZERO);
				dtoMap.put("SELISIH_BUNGA_KONTRAKTUAL",BigDecimal.ZERO);
				dtoMap.put("NILAI_KINI_ARUS", BigDecimal.ZERO);
			}
			//Dikosongkan
			
			
			
			itemAngsur.appendChild(new Listcell(dtoMap.getBigDecimal("totalAngsuran")));
			itemAngsur.appendChild(new Listcell(dtoMap.getBigDecimal("saldoTeoritis")));
			listboxJadwalAngsurImpair.appendChild(itemAngsur);

			Listitem jangkaWaktu = new Listitem();
			jangkaWaktu.appendChild(new Listcell(dtoMap.get("jadwalAngsurKe")));
			jangkaWaktu.appendChild(new Listcell(dtoMap.get("tglJadwal")));
			listboxJangkaWaktu.appendChild(jangkaWaktu);

			Listitem itemArus = new Listitem();
			itemArus.appendChild(new Listcell(dtoMap.getBigDecimal("ESTIMASI_ARUS")));
			if(dtoMap.getInt("jadwalAngsurKe") == 0){
				itemArus.appendChild(new Listcell(new BigDecimal (0.00)));
				dtoMap.put("NILAI_KINI_ARUS",new BigDecimal (0.00));
			}else{			
				itemArus.appendChild(new Listcell(dtoMap.getBigDecimal("NILAI_KINI_ARUS")));	
			}
			
			itemArus.appendChild(new Listcell(dtoMap.getBigDecimal("SALDO_AWAL_KONVERSI")));
			itemArus.appendChild(new Listcell(dtoMap.getBigDecimal("BUNGA_KONVERSI")));
			itemArus.appendChild(new Listcell(dtoMap.getBigDecimal("POKOK_ARUS")));
			itemArus.appendChild(new Listcell(dtoMap.getBigDecimal("BUNGA_ARUS")));
			itemArus.appendChild(new Listcell(dtoMap.getBigDecimal("SELISIH_BUNGA_KONTRAKTUAL")));
			itemArus.appendChild(new Listcell(dtoMap.getBigDecimal("SALDO_AKHIR_KONVERSI")));
			itemArus.appendChild(new Listcell(dtoMap.getBigDecimal("saldoTeoritis")));
			itemArus.appendChild(new Listcell(new BigDecimal(0)));
			itemArus.appendChild(new Listcell(new BigDecimal(0)));


			estimasiArusKasTot += dtoMap.getBigDecimal("ESTIMASI_ARUS").doubleValue();
			bungaKonversiTot += dtoMap.getBigDecimal("BUNGA_KONVERSI").doubleValue();
			
			nilaiKini += dtoMap.getBigDecimal("NILAI_KINI_ARUS").doubleValue();
			angsuranPokokTot += dtoMap.getBigDecimal("POKOK_ARUS").doubleValue();
			angsuranBungaTot += dtoMap.getBigDecimal("BUNGA_ARUS").doubleValue();
			selisihBungaKontraktualTot += dtoMap.getBigDecimal("SELISIH_BUNGA_KONTRAKTUAL").doubleValue();
			listBoxArusKasImpair.appendChild(itemArus);
			totPokok += dtoMap.getBigDecimal("angsuranPokok").doubleValue();
			totBunga += dtoMap.getBigDecimal("angsuranBunga").doubleValue();
			totAngsur += dtoMap.getBigDecimal("totalAngsuran").doubleValue();
			i++;
		}
		DecimalFormat df = new DecimalFormat("#,##0.00");
		wndEngine.setFellowValue("decIrrImpairBaru", new BigDecimal(irrSemua).setScale(8, BigDecimal.ROUND_HALF_UP));
		wndEngine.setFellowValue("decTotPokok", BigDecimal.valueOf(totPokok));
		wndEngine.setFellowValue("decTotBunga", BigDecimal.valueOf(totBunga));
		wndEngine.setFellowValue("decTotJmlAngsur", BigDecimal.valueOf(totAngsur));
		wndEngine.setFellowValue("decNilaiWajarBaru", BigDecimal.valueOf(nilaiKini));

		
		BigDecimal nilaiWajarBaru = (BigDecimal)wndEngine.getFellowValue("decNilaiWajarBaru");
		BigDecimal nilaiWajarLama = (BigDecimal)wndEngine.getFellowValue("decNilaiWajarLama");
		
		double selisih = 0;
		selisih = nilaiWajarLama.doubleValue() - nilaiWajarBaru.doubleValue();
		
		if(selisih > 0){
			wndEngine.setFellowValue("decCKPNIndividu", new BigDecimal(selisih));
			wndEngine.setFellowValue("decSelisihNPV", new BigDecimal(0.00));
		}else if(selisih < 0){
			wndEngine.setFellowValue("decCKPNIndividu", new BigDecimal(0.00));
			wndEngine.setFellowValue("decSelisihNPV", new BigDecimal(selisih*-1));
		}else{
			wndEngine.setFellowValue("decCKPNIndividu", new BigDecimal(0.00));
			wndEngine.setFellowValue("decSelisihNPV", new BigDecimal(0.00));
		}
		for(Listitem itemCkpn : listBoxArusKasImpair.getItems()){
			((Listcell) itemCkpn.getChildren().get(9)).setLabel(df.format((BigDecimal)wndEngine.getFellowValue("decSelisihNPV")));
			((Listcell) itemCkpn.getChildren().get(10)).setLabel(df.format((BigDecimal)wndEngine.getFellowValue("decCKPNIndividu")));
		}
		
		wndEngine.setFellowValue("dec1Baru", df.format(estimasiArusKasTot));
		wndEngine.setFellowValue("dec2Baru", df.format(nilaiKini));
		wndEngine.setFellowValue("dec4Baru", df.format(bungaKonversiTot));
		wndEngine.setFellowValue("dec5Baru", df.format(angsuranPokokTot));
		wndEngine.setFellowValue("dec6Baru", df.format(angsuranBungaTot));
		wndEngine.setFellowValue("dec7Baru", df.format(selisihBungaKontraktualTot));
	}*/

	public void doSetAngsur(List listArusKas) {
		BigDecimal provFee = (BigDecimal) ComponentUtil.getValue(decNominalProvisi);
		BigDecimal getFee = (BigDecimal) ComponentUtil.getValue(decBiayaPerolehan);
		BigDecimal discount = (BigDecimal) ComponentUtil.getValue(txtDiscount);

		boolean isVisibleAttribusi = provFee.doubleValue() - getFee.doubleValue() != 0;

		SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");

		boolean isVisibleDiskonRate = discount.doubleValue() > 0;
		boolean isVisible = provFee.doubleValue() - getFee.doubleValue() != 0;
		
		if (!isAngsImpr) {
			if (listJadwalAngsur.getItemCount() > 0) {
				listJadwalAngsur.getItems().clear();				
			}
			if (listJangkaWaktu.getItemCount() > 0) {
				listJangkaWaktu.getItems().clear();
			}
			if (listArusKasKonversi.getItemCount() > 0) {
				listArusKasKonversi.getItems().clear();
			}
			decIrrKonversi.setValue(new BigDecimal(0));
			if (listArusKasAttribusi.getItemCount()>0) {
				listArusKasAttribusi.getItems().clear();
			}
			decIrrAttribusi.setValue(new BigDecimal(0));
			if (listArusKasDiskonRate.getItemCount() > 0) {
				listArusKasDiskonRate.getItems().clear();
			}
			decIrrDiskonRate.setValue(new BigDecimal(0));
		}else{
			if (listboxArusKas.getItemCount() > 0) {
				listboxArusKas.getItems().clear();
			}
		}
		decSBE.setValue(BigDecimal.valueOf(irrSemua).setScale(8, RoundingMode.HALF_UP));

		decSBEKonversi.setValue(BigDecimal.valueOf(irr).setScale(8, RoundingMode.HALF_UP));
		decSBEAtribusi.setValue(BigDecimal.valueOf(irrAtribusi).setScale(8, RoundingMode.HALF_UP));
		decSBEDiskon.setValue(BigDecimal.valueOf(irrDiskon).setScale(8, RoundingMode.HALF_UP));
		
		decIrrKonversi.setValue(BigDecimal.valueOf(irr).setScale(8, BigDecimal.ROUND_HALF_UP));
		decIrrAttribusi.setValue(BigDecimal.valueOf(irrAtribusi).setScale(8, BigDecimal.ROUND_HALF_UP));
		decIrrDiskonRate.setValue(BigDecimal.valueOf(irrDiskon).setScale(8, BigDecimal.ROUND_HALF_UP));
		
		if(isVisibleAttribusi){
			grupArusAttribusi.setVisible(true);
			// ((Listhead)listHeadArusAttribusi")).setStyle("overflow:auto;");
		}
		else{
			grupArusAttribusi.setVisible(true);
		}
		if(isVisibleDiskonRate){
			grupArusDiskonRate.setVisible(true);
			//  ((Listhead)listHeadArusDiskonRate")).setStyle("overflow:auto;");
		}else{
			grupArusDiskonRate.setVisible(true);
		}
		
		int periodeBunga = (Integer) ComponentUtil.getValue(txtPeriodBunga);
		totPokok = 0;
		totBunga = 0;
		totAngsur = 0;
		estimasiArusKasTot = 0;
		bungaKonversiTot = 0;
		angsuranPokokTot = 0;
		angsuranBungaTot = 0;
		selisihBungaKontraktualTot = 0;
		nilaiKiniArusKasTot = 0;
		estimasiArusKasDiscountTot = 0;
		bungaDiscountTot = 0;
		amortisasiDiscountTot = 0;
		estimasiArusKasAtribusiTot = 0;
		bungaAtribusiTot = 0;
		amortisasiTot = 0;
		for (Object o : listArusKas) {
			DTOMap dtoMap = (DTOMap) o;
			if (!isAngsImpr) {
				Listitem itemAngsur = new Listitem();
				itemAngsur.setAttribute("DATA", dtoMap);
				itemAngsur.appendChild(new Listcell(dtoMap.getInt("jadwalAngsurKe").toString()));
				itemAngsur.appendChild(new Listcell(sdf.format(dtoMap.getDate("tglJadwal"))));
				itemAngsur.appendChild(new Listcell(FunctionUtils.moneyToText(dtoMap.getBigDecimal("angsuranPokok"))));
				itemAngsur.appendChild(new Listcell(FunctionUtils.moneyToText(dtoMap.getBigDecimal("angsuranBunga"))));
				itemAngsur.appendChild(new Listcell(FunctionUtils.moneyToText(dtoMap.getBigDecimal("totalAngsuran"))));
				itemAngsur.appendChild(new Listcell(FunctionUtils.moneyToText(dtoMap.getBigDecimal("saldoTeoritis"))));
				listJadwalAngsur.appendChild(itemAngsur);
				
				Listitem jangkaWaktu = new Listitem();
				jangkaWaktu.appendChild(new Listcell(dtoMap.getInt("jadwalAngsurKe").toString()));
				jangkaWaktu.appendChild(new Listcell(sdf.format(dtoMap.getDate("tglJadwal"))));
				listJangkaWaktu.appendChild(jangkaWaktu);
				
				Listitem itemArus = new Listitem();
				itemArus.appendChild(new Listcell(FunctionUtils.moneyToText(dtoMap.getBigDecimal("ESTIMASI_ARUS"))));
				itemArus.appendChild(new Listcell(FunctionUtils.moneyToText(dtoMap.getBigDecimal("SALDO_AWAL_KONVERSI"))));
				itemArus.appendChild(new Listcell(FunctionUtils.moneyToText(dtoMap.getBigDecimal("BUNGA_KONVERSI"))));
				itemArus.appendChild(new Listcell(FunctionUtils.moneyToText(dtoMap.getBigDecimal("POKOK_ARUS"))));
				itemArus.appendChild(new Listcell(FunctionUtils.moneyToText(dtoMap.getBigDecimal("BUNGA_ARUS"))));
				itemArus.appendChild(new Listcell(FunctionUtils.moneyToText(dtoMap.getBigDecimal("SELISIH_BUNGA_KONTRAKTUAL"))));
				itemArus.appendChild(new Listcell(FunctionUtils.moneyToText(dtoMap.getBigDecimal("SALDO_AKHIR_KONVERSI"))));
				itemArus.appendChild(new Listcell(FunctionUtils.moneyToText(dtoMap.getBigDecimal("saldoTeoritis"))));
				
				System.out.println("estimasiArusKasTot "+ dtoMap.getBigDecimal("ESTIMASI_ARUS"));
				
				estimasiArusKasTot += dtoMap.getBigDecimal("ESTIMASI_ARUS").doubleValue();
				bungaKonversiTot += dtoMap.getBigDecimal("BUNGA_KONVERSI").doubleValue();
				angsuranPokokTot += dtoMap.getBigDecimal("POKOK_ARUS").doubleValue();
				angsuranBungaTot += dtoMap.getBigDecimal("BUNGA_ARUS").doubleValue();
				selisihBungaKontraktualTot += dtoMap.getBigDecimal("SELISIH_BUNGA_KONTRAKTUAL").doubleValue();
				listArusKasKonversi.appendChild(itemArus);
				
				//Attribusi
				if (isVisibleAttribusi) {
					// Attribusi
					Listitem itemArusAttribusi = new Listitem();
					itemArusAttribusi.appendChild(new Listcell(FunctionUtils.moneyToText(dtoMap.getBigDecimal("ESTIMASI_ARUS_ATRIBUSI"))));
					itemArusAttribusi.appendChild(new Listcell(FunctionUtils.moneyToText(dtoMap.getBigDecimal("SALDO_AWAL_ARUS"))));
					itemArusAttribusi.appendChild(new Listcell(FunctionUtils.moneyToText(dtoMap.getBigDecimal("BUNGA_ATRIBUSI"))));
					itemArusAttribusi.appendChild(new Listcell(FunctionUtils.moneyToText(dtoMap.getBigDecimal("AMORTISASI_BIAYA_ATRIBUSI"))));
					itemArusAttribusi.appendChild(new Listcell(FunctionUtils.moneyToText(dtoMap.getBigDecimal("SALDO_AKHIR_ARUS"))));
					itemArusAttribusi.appendChild(new Listcell(FunctionUtils.moneyToText(dtoMap.getBigDecimal("SISA_ATRIBUSI"))));
					estimasiArusKasAtribusiTot += dtoMap.getBigDecimal("ESTIMASI_ARUS_ATRIBUSI").doubleValue();
					bungaAtribusiTot += dtoMap.getBigDecimal("BUNGA_ATRIBUSI").doubleValue();
					amortisasiTot += dtoMap.getBigDecimal("AMORTISASI_BIAYA_ATRIBUSI").doubleValue();
					listArusKasAttribusi.appendChild(itemArusAttribusi);
				}
				
				//Diskon
				if(isVisibleDiskonRate){
					// Diskon
					Listitem itemArusDiskon = new Listitem();
					itemArusDiskon.appendChild(new Listcell(FunctionUtils.moneyToText(dtoMap.getBigDecimal("NILAI_KINI_ARUS"))));
					itemArusDiskon.appendChild(new Listcell(FunctionUtils.moneyToText(dtoMap.getBigDecimal("ETSIMASI_ARUS_DISCOUNT"))));
					itemArusDiskon.appendChild(new Listcell(FunctionUtils.moneyToText(dtoMap.getBigDecimal("SALDO_AWAL_DISCOUNT"))));
					itemArusDiskon.appendChild(new Listcell(FunctionUtils.moneyToText(dtoMap.getBigDecimal("BUNGA_DISCOUNT"))));
					itemArusDiskon.appendChild(new Listcell(FunctionUtils.moneyToText(dtoMap.getBigDecimal("AMORTISASI_DISCOUNT"))));
					itemArusDiskon.appendChild(new Listcell(FunctionUtils.moneyToText(dtoMap.getBigDecimal("SALDO_AKHIR_DISCOUNT"))));
					itemArusDiskon.appendChild(new Listcell(FunctionUtils.moneyToText(dtoMap.getBigDecimal("SALDO_DISKON"))));
					nilaiKiniArusKasTot += dtoMap.getBigDecimal("NILAI_KINI_ARUS").doubleValue();
					estimasiArusKasDiscountTot += dtoMap.getBigDecimal("ETSIMASI_ARUS_DISCOUNT").doubleValue();
					bungaDiscountTot += dtoMap.getBigDecimal("BUNGA_DISCOUNT").doubleValue();
					amortisasiDiscountTot += dtoMap.getBigDecimal("AMORTISASI_DISCOUNT").doubleValue();
					listArusKasDiskonRate.appendChild(itemArusDiskon);
				}
				
				totPokok += dtoMap.getBigDecimal("angsuranPokok").doubleValue();
				totBunga += dtoMap.getBigDecimal("angsuranBunga").doubleValue();
				totAngsur += dtoMap.getBigDecimal("totalAngsuran").doubleValue();
				
			}else{
				
				Listitem itemArus = new Listitem();
				itemArus.appendChild(new Listcell(FunctionUtils.moneyToText(dtoMap.getBigDecimal("ESTIMASI_ARUS"))));
				if(dtoMap.getInt("jadwalAngsurKe") == 0){
					itemArus.appendChild(new Listcell(FunctionUtils.moneyToText(new BigDecimal (0.00))));
					dtoMap.put("NILAI_KINI_ARUS",new BigDecimal (0.00));
				}else{			
					itemArus.appendChild(new Listcell(FunctionUtils.moneyToText(dtoMap.getBigDecimal("NILAI_KINI_ARUS"))));	
				}
				
				itemArus.appendChild(new Listcell(FunctionUtils.moneyToText(dtoMap.getBigDecimal("SALDO_AWAL_KONVERSI"))));
				itemArus.appendChild(new Listcell(FunctionUtils.moneyToText(dtoMap.getBigDecimal("BUNGA_KONVERSI"))));
				itemArus.appendChild(new Listcell(FunctionUtils.moneyToText(dtoMap.getBigDecimal("POKOK_ARUS"))));
				itemArus.appendChild(new Listcell(FunctionUtils.moneyToText(dtoMap.getBigDecimal("BUNGA_ARUS"))));
				itemArus.appendChild(new Listcell(FunctionUtils.moneyToText(dtoMap.getBigDecimal("SELISIH_BUNGA_KONTRAKTUAL"))));
				itemArus.appendChild(new Listcell(FunctionUtils.moneyToText(dtoMap.getBigDecimal("SALDO_AKHIR_KONVERSI"))));
				itemArus.appendChild(new Listcell(FunctionUtils.moneyToText(dtoMap.getBigDecimal("saldoTeoritis"))));

				estimasiArusKasTot += dtoMap.getBigDecimal("ESTIMASI_ARUS").doubleValue();
				bungaKonversiTot += dtoMap.getBigDecimal("BUNGA_KONVERSI").doubleValue();
				
				nilaiKini += dtoMap.getBigDecimal("NILAI_KINI_ARUS").doubleValue();
				angsuranPokokTot += dtoMap.getBigDecimal("POKOK_ARUS").doubleValue();
				angsuranBungaTot += dtoMap.getBigDecimal("BUNGA_ARUS").doubleValue();
				selisihBungaKontraktualTot += dtoMap.getBigDecimal("SELISIH_BUNGA_KONTRAKTUAL").doubleValue();
				listboxArusKas.appendChild(itemArus);
				totPokok += dtoMap.getBigDecimal("angsuranPokok").doubleValue();
				totBunga += dtoMap.getBigDecimal("angsuranBunga").doubleValue();
				totAngsur += dtoMap.getBigDecimal("totalAngsuran").doubleValue();
				
			}
		}
		DecimalFormat df = new DecimalFormat("#,##0.00");
		if (isAngsImpr) {
			decIrr.setValue(new BigDecimal(irrSemua).setScale(8, BigDecimal.ROUND_HALF_UP));
			
			ComponentUtil.setValue(decArus1, df.format(estimasiArusKasTot));
			ComponentUtil.setValue(decArus2, df.format(nilaiKini));
			ComponentUtil.setValue(decArus4, df.format(bungaKonversiTot));
			ComponentUtil.setValue(decArus5, df.format(angsuranPokokTot));
			ComponentUtil.setValue(decArus6, df.format(angsuranBungaTot));
			ComponentUtil.setValue(decArus7, df.format(selisihBungaKontraktualTot));
		} else {
			decTotPokok.setValue(BigDecimal.valueOf(totPokok));
			decTotBunga.setValue(BigDecimal.valueOf(totBunga));
			decTotJmlAngsur.setValue(BigDecimal.valueOf(totAngsur));
			
			
			ComponentUtil.setValue(dec1,df.format(estimasiArusKasTot));
			ComponentUtil.setValue(dec3,df.format(bungaKonversiTot));
			ComponentUtil.setValue(dec4,df.format(angsuranPokokTot));
			ComponentUtil.setValue(dec5,df.format(angsuranBungaTot));
			ComponentUtil.setValue(dec6,df.format(selisihBungaKontraktualTot));
			
			ComponentUtil.setValue(dec9,df.format(estimasiArusKasAtribusiTot));
			ComponentUtil.setValue(dec11,df.format(bungaAtribusiTot));
			ComponentUtil.setValue(dec12,df.format(amortisasiTot));
			
			ComponentUtil.setValue(dec15,df.format(nilaiKiniArusKasTot));
			ComponentUtil.setValue(dec16,df.format(estimasiArusKasDiscountTot));
			ComponentUtil.setValue(dec18,df.format(bungaDiscountTot));
			ComponentUtil.setValue(dec19,df.format(amortisasiDiscountTot));
		}
	}

	public void doReset() {
//		super.doReset();
		
		ComponentUtil.setValue(txtTglMulai,dataSession.getOpenDateFE());
		//txtTglAkhirComponentUtil.setValue( getOpenDateFrontEnd());
		ComponentUtil.setValue(txtTenor,12);
		
		//Listbox listboxJadwalAngsur = (Listbox) wndEngine.getFellow("listbox");
		//Listbox listboxArus = (Listbox) wndEngine.getFellow("listBoxArus");

		//listboxJadwalAngsur.getItems().clear();
		//listboxArus.getItems().clear();

		ComponentUtil.setValue(txtPeriodPokok,1);
		ComponentUtil.setValue(txtPeriodBunga,1);

		ComponentUtil.setValue(txtTenggangPokok,0);
		ComponentUtil.setValue(txtTenggangBunga,0);
		ComponentUtil.setValue(txtDistGrpBunga,0);
		setJatuhTempo();
		if (listJadwalAngsur.getItemCount() > 0) {
			listJadwalAngsur.getItems().clear();
		}
		if (listJangkaWaktu.getItemCount()>0) {
			listJangkaWaktu.getItems().clear();
		}
		if (listArusKasKonversi.getItemCount()>0) {
			listArusKasKonversi.getItems().clear();
		}
		ComponentUtil.setValue(decIrrKonversi,new BigDecimal(0));
		if (listArusKasAttribusi.getItemCount()>0) {
			listArusKasAttribusi.getItems().clear();
		}
		ComponentUtil.setValue(decIrrAttribusi,new BigDecimal(0));
		if (listArusKasDiskonRate.getItemCount()>0) {
			listArusKasDiskonRate.getItems().clear();
		}
		
		DecimalFormat df = new DecimalFormat("#,##0.00");
		
		ComponentUtil.setValue(dec1,df.format(0));
		ComponentUtil.setValue(dec3,df.format(0));
		ComponentUtil.setValue(dec4,df.format(0));
		ComponentUtil.setValue(dec5,df.format(0));
		ComponentUtil.setValue(dec6,df.format(0));
		
		ComponentUtil.setValue(dec9,df.format(0));
		ComponentUtil.setValue(dec11,df.format(0));
		ComponentUtil.setValue(dec12,df.format(0));

		ComponentUtil.setValue(dec15,df.format(0));
		ComponentUtil.setValue(dec16,df.format(0));
		ComponentUtil.setValue(dec18,df.format(0));
		ComponentUtil.setValue(dec19,df.format(0));
		
	}

	public boolean validation() {
		boolean isValid = true;
		List wrongValue = new ArrayList(0);
		if (isValid) {
			BigDecimal max = (BigDecimal) ComponentUtil.getValue(txtPlafon);
			if ((max == null) || (max.doubleValue() == 0.00)) {
				isValid = false;
				wrongValue.add(new WrongValueException(txtPlafon, "Maksimum Kredit Harus Diisi"));
			}

			Date strtdt = (Date) ComponentUtil.getValue(txtTglMulai);
			if (strtdt == null) {
				isValid = false;
				wrongValue.add(new WrongValueException(txtTglMulai, "Tanggal Mulai Harus Diisi"));
			}

			String SukuBunga = (String) ComponentUtil.getValue(cmbJnsBunga);
			if (SukuBunga == null) {
				isValid = false;
				wrongValue.add(new WrongValueException(cmbJnsBunga, "Suku Bunga Harus Diisi"));
			}

			BigDecimal IntRate = (BigDecimal) ComponentUtil.getValue(txtBunga);
			if (IntRate == null) {
				isValid = false;
				wrongValue.add(new WrongValueException(txtBunga, "Suku Bunga Harus Diisi"));
			}

			Integer tenggangPokok = (Integer) ComponentUtil.getValue(txtTenggangPokok);
			Integer tenor = (Integer) ComponentUtil.getValue(txtTenor);
			if (tenggangPokok != null) {
				if (tenggangPokok > tenor) {
					isValid = false;
					wrongValue.add(new WrongValueException(txtTenggangPokok,
							"Masa Tenggang Pokok Harus < Jangka Waktu"));
				}
			} else {
				isValid = false;
				wrongValue.add(new WrongValueException(txtTenggangPokok, "Masa Tenggang Pokok Harus Diisi"));
			}

			Integer tenggangBunga = (Integer) ComponentUtil.getValue(txtTenggangBunga);
			if (tenggangBunga != null) {
				if (tenggangBunga > tenor) {
					isValid = false;
					wrongValue.add(new WrongValueException(txtTenggangBunga,
							"Masa Tunda Bunga Harus < Jangka Waktu"));
				}
			} else {
				isValid = false;
				wrongValue.add(new WrongValueException(txtTenggangBunga, "Masa Tunda Bunga Harus Diisi"));
			}
			
			Integer HariBunga = (Integer) ComponentUtil.getValue(cmbHariBunga);
			if (HariBunga == null) {
				isValid = false;
				wrongValue.add(new WrongValueException(cmbHariBunga, "Hari Bunga Harus Diisi"));
			}

			if (wrongValue.size() > 0) {
				throw new WrongValuesException((WrongValueException[]) wrongValue.toArray(new WrongValueException[wrongValue.size()]));
			}
			
			Integer periodPokok = (Integer) ComponentUtil.getValue(txtPeriodPokok);
			Integer periodBunga = (Integer) ComponentUtil.getValue(txtPeriodBunga);
			if (12 % periodPokok != 0 || periodPokok == 2)
				throw new WrongValueException(txtPeriodPokok, "Period Pokok harus bernilai (1, 3, 4, 6, 12)");
			if (12 % periodBunga != 0 || periodBunga == 2)
				throw new WrongValueException(txtPeriodBunga, "Period Bunga harus bernilai (1, 3, 4, 6, 12)");
			if (SukuBunga != null) {
				if (SukuBunga.charAt(0) == 'B') {
					if (periodPokok > 1)
						throw new WrongValueException(txtPeriodPokok, "Periode Pokok Harus 1");
					if (periodBunga > 1)
						throw new WrongValueException(txtPeriodBunga, "Periode Bunga Harus 1");
				}
				//if ((SukuBunga.charAt(0) == 'A' || SukuBunga.charAt(0) == 'D') && periodPokok.intValue() != periodBunga.intValue()) {
			//		throw new WrongValueException(txtPeriodBunga, "Periode Pembayaran Bunga Harus = Pokok");
				//} else if (periodPokok.intValue() != periodBunga.intValue() && periodBunga > 1) {
			//		throw new WrongValueException(txtPeriodBunga, "Periode Pembayaran Bunga Harus = Pokok atau 1");
				//}
			}
			if (periodBunga > periodPokok) {
				throw new WrongValueException(txtPeriodBunga,
						"Periode Pembayaran Bunga Harus <= Pokok");
			}

			Integer distTenggangBunga = (Integer) ComponentUtil.getValue(txtDistGrpBunga);
			if (tenggangBunga == 0 && distTenggangBunga > 0)
				throw new WrongValueException(txtDistGrpBunga, "Harus 0 Jika Masa Tunda Bunga 0");
			if (periodPokok > 1 && tenggangPokok > 0)
				throw new WrongValueException(txtTenggangPokok, "Harus 0 Jika Periode Pokok > 1");
			if (periodBunga > 1 && tenggangBunga > 0)
				throw new WrongValueException(txtTenggangBunga, "Harus 0 Jika Periode Bunga > 1");
			if (SukuBunga.charAt(0) == 'B') {
				if (tenggangPokok > 0)
					throw new WrongValueException(txtTenggangPokok, "Tenggang Pokok Harus 0 Untuk Anuitas Tahunan");
				if (tenggangBunga > 0)
					throw new WrongValueException(txtTenggangBunga, "Tunda Bunga Harus 0 Untuk Anuitas Tahunan");
				if (distTenggangBunga > 0)
					throw new WrongValueException(txtDistGrpBunga, "Tunda Bunga Harus 0 Untuk Anuitas Tahunan.");
			}
			if (SukuBunga.charAt(0) != 'B' && tenggangPokok.intValue() != tenggangBunga.intValue()) {
				//throw new WrongValueException(txtTenggangBunga"), "Masa Tunda Bunga Harus = Pokok");
			}

			int maxDistBunga = tenor - tenggangBunga;
			if (distTenggangBunga > maxDistBunga)
				throw new WrongValueException(txtDistGrpBunga, "Maksimum Distribusi Tunda Bunga " + maxDistBunga
						+ " bulan");
			if (distTenggangBunga > 0
					&& (SukuBunga.charAt(0) == 'A' || SukuBunga.charAt(0) == 'C' || SukuBunga.charAt(0) == 'D' || SukuBunga.charAt(0) == 'E')
					&& maxDistBunga != distTenggangBunga)
				throw new WrongValueException(txtDistGrpBunga, "Distribusi Tunda Bunga Harus " + maxDistBunga + " bulan");
			
			BigDecimal discount=(BigDecimal)ComponentUtil.getValue(txtDiscount);
			if(discount.doubleValue()>0 && (tenggangPokok>0 || tenggangBunga>0))
				throw new WrongValueException(txtDiscount, "Diskon Bunga Harus 0 Jika Ada Tenggang Pokok/Bunga.");
		}
		return isValid;
	}

	public void doPrint() {
		doProses();

		HashMap param = new HashMap();
		DecimalFormat df = new DecimalFormat("###.00");
		param.put("jnsBunga", cmbJnsBunga.getText());
		param.put("persenBunga", txtBunga.getText());
		param.put("bungaDiscount", txtDiscount.getText());
		param.put("jangkaWkt", txtTenor.getText());
		param.put("tglMulai", txtTglMulai.getText());
		param.put("tglJthTempo", txtTglAkhir.getText());
		param.put("plafond", txtPlafon.getText());
		param.put("nomProvisi", decNominalProvisi.getText());
		param.put("biayaPerolehan", decBiayaPerolehan.getText());
		param.put("periodPokok", txtPeriodPokok.getText());
		param.put("periodBunga", txtPeriodBunga.getText());
		param.put("grpPokok", txtTenggangPokok.getText());
		param.put("grpBunga", txtTenggangBunga.getText());
		param.put("distGrpBunga", txtDistGrpBunga.getText());
		param.put("totPokok", ComponentUtil.getValue(decTotPokok));
		param.put("totBunga", ComponentUtil.getValue(decTotBunga));
		param.put("totAngsuran", ComponentUtil.getValue(decTotJmlAngsur));
//		param.put("totSaldo", decTotSaldo.getText());
		param.put("decSBEKonversi", decSBEKonversi.getText());
		param.put("decSBEAtribusi", decSBEAtribusi.getText());
		param.put("printedBy", dataSession.userMap.getString("USERID"));
		param.put("tglCetak", dataSession.getOpenDate());
		param.put("namaCabang", dataSession.branchMap.getString("BRANCHNM"));

		JRreportWindow jRreportWindow = new JRreportWindow(wnd, true, param, "/jasper/ReportSimulasiJadwalAngsur.jasper",
				new JRBeanCollectionDataSource(listAngsuran), "pdf", null);
	}

	public void doExport() {
		doProses();
		BigDecimal provFee = (BigDecimal) ComponentUtil.getValue(decNominalProvisi);
		BigDecimal getFee = (BigDecimal) ComponentUtil.getValue(decBiayaPerolehan);
		BigDecimal discount = (BigDecimal) ComponentUtil.getValue(txtDiscount);

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFDataFormat hdf = workbook.createDataFormat();
		HSSFCellStyle moneyStyle = workbook.createCellStyle();
		moneyStyle.setDataFormat(hdf.getFormat("#,##0.00"));
		moneyStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);

		HSSFSheet sheet = workbook.createSheet("Jadwal Angsur");
		HSSFRow row = sheet.createRow(0);
		row.createCell((short) 1).setCellValue(new HSSFRichTextString("Jenis Bunga"));
		row.createCell((short) 2).setCellValue(new HSSFRichTextString(cmbJnsBunga.getText()));
		row.createCell((short) 5).setCellValue(new HSSFRichTextString("Jangka Waktu"));
		row.createCell((short) 6).setCellValue(new HSSFRichTextString(txtTenor.getText()));
		row.createCell((short) 7).setCellValue(new HSSFRichTextString("Bulan"));

		row = sheet.createRow(1);
		row.createCell((short) 1).setCellValue(new HSSFRichTextString("Tanggal Mulai"));
		row.createCell((short) 2).setCellValue(new HSSFRichTextString(txtTglMulai.getText()));
		row.createCell((short) 5).setCellValue(new HSSFRichTextString("Tanggal Jatuh Tempo"));
		row.createCell((short) 6).setCellValue(new HSSFRichTextString(txtTglAkhir.getText()));

		row = sheet.createRow(2);
		row.createCell((short) 1).setCellValue(new HSSFRichTextString("Suku Bunga"));
		row.createCell((short) 2).setCellValue(new HSSFRichTextString(txtBunga.getText()));
		row.createCell((short) 3).setCellValue(new HSSFRichTextString("%p.a"));
		row.createCell((short) 5).setCellValue(new HSSFRichTextString("Maksimum Kredit"));
		row.createCell((short) 6).setCellValue(new HSSFRichTextString(txtPlafon.getText()));

		row = sheet.createRow(3);
		row.createCell((short) 1).setCellValue(new HSSFRichTextString("Bunga Discount"));
		row.createCell((short) 2).setCellValue(new HSSFRichTextString(txtDiscount.getText()));
		row.createCell((short) 3).setCellValue(new HSSFRichTextString("%p.a"));
		row.createCell((short) 5).setCellValue(new HSSFRichTextString("Masa Tenggang Pokok"));
		row.createCell((short) 6).setCellValue(new HSSFRichTextString(txtTenggangPokok.getText()));
		row.createCell((short) 7).setCellValue(new HSSFRichTextString("Bulan"));

		row = sheet.createRow(4);
		row.createCell((short) 1).setCellValue(new HSSFRichTextString("Pembayaran Pokok"));
		row.createCell((short) 2).setCellValue(new HSSFRichTextString(txtPeriodPokok.getText()));
		row.createCell((short) 3).setCellValue(new HSSFRichTextString("Bulan"));
		row.createCell((short) 5).setCellValue(new HSSFRichTextString("Masa Tunda Bunga"));
		row.createCell((short) 6).setCellValue(new HSSFRichTextString(txtTenggangBunga.getText()));
		row.createCell((short) 7).setCellValue(new HSSFRichTextString("Bulan"));

		row = sheet.createRow(5);
		row.createCell((short) 1).setCellValue(new HSSFRichTextString("Pembayaran Bunga"));
		row.createCell((short) 2).setCellValue(new HSSFRichTextString(txtPeriodBunga.getText()));
		row.createCell((short) 3).setCellValue(new HSSFRichTextString("Bulan"));
		row.createCell((short) 5).setCellValue(new HSSFRichTextString("Distribusi Tunda Bunga"));
		row.createCell((short) 6).setCellValue(new HSSFRichTextString(txtDistGrpBunga.getText()));
		row.createCell((short) 7).setCellValue(new HSSFRichTextString("Bulan"));

		row = sheet.createRow(6);
		row.createCell((short) 1).setCellValue(new HSSFRichTextString("Nominal Provisi"));
		row.createCell((short) 2).setCellValue(new HSSFRichTextString(decNominalProvisi.getText()));
		row.createCell((short) 5).setCellValue(new HSSFRichTextString("SBE Konversi"));
		row.createCell((short) 6).setCellValue(new HSSFRichTextString(decSBEKonversi.getText()));

		row = sheet.createRow(7);
		row.createCell((short) 1).setCellValue(new HSSFRichTextString("Biaya Perolehan"));
		row.createCell((short) 2).setCellValue(new HSSFRichTextString(decBiayaPerolehan.getText()));
		row.createCell((short) 5).setCellValue(new HSSFRichTextString("SBE Atribusi"));
		row.createCell((short) 6).setCellValue(new HSSFRichTextString(decSBEAtribusi.getText()));

		row = sheet.createRow(9);
		row.createCell((short) 2).setCellValue(new HSSFRichTextString("Bulan Ke"));
		row.createCell((short) 3).setCellValue(new HSSFRichTextString("Tanggal"));
		row.createCell((short) 4).setCellValue(new HSSFRichTextString("Angsuran Pokok"));
		row.createCell((short) 5).setCellValue(new HSSFRichTextString("Angsur Bunga"));
		row.createCell((short) 6).setCellValue(new HSSFRichTextString("Jumlah Angsur"));
		row.createCell((short) 7).setCellValue(new HSSFRichTextString("Saldo"));

		int rowNum = 10;
		for (int i = 0; i < listAngsuran.size(); i++) {
			DTOMap angsur = (DTOMap) listAngsuran.get(i);
			row = sheet.createRow(rowNum);
			row.createCell((short) 2).setCellValue(angsur.getInt("jadwalAngsurKe"));
			row.createCell((short) 3).setCellValue(new HSSFRichTextString(sdf.format(angsur.getDate("tglJadwal"))));
			row.createCell((short) 4).setCellValue(angsur.getBigDecimal("angsuranPokok").doubleValue());
			row.getCell((short) 4).setCellStyle(moneyStyle);
			row.createCell((short) 5).setCellValue(angsur.getBigDecimal("angsuranBunga").doubleValue());
			row.getCell((short) 5).setCellStyle(moneyStyle);
			row.createCell((short) 6).setCellValue(angsur.getBigDecimal("totalAngsuran").doubleValue());
			row.getCell((short) 6).setCellStyle(moneyStyle);
			row.createCell((short) 7).setCellValue(angsur.getBigDecimal("saldoTeoritis").doubleValue());
			row.getCell((short) 7).setCellStyle(moneyStyle);
			rowNum++;
		}
		row = sheet.createRow(rowNum);
		row.createCell((short) 3).setCellValue(new HSSFRichTextString("Total :"));
		row.createCell((short) 4).setCellValue(totPokok);
		row.getCell((short) 4).setCellStyle(moneyStyle);
		row.createCell((short) 5).setCellValue(totBunga);
		row.getCell((short) 5).setCellStyle(moneyStyle);
		row.createCell((short) 6).setCellValue(totAngsur);
		row.getCell((short) 6).setCellStyle(moneyStyle);

		for (short j = 1; j <= 7; j++)
			sheet.autoSizeColumn(j);

		sheet = workbook.createSheet("Arus Kas");
		row = sheet.createRow(1);
		row.createCell((short) 1).setCellValue(new HSSFRichTextString("Bulan Ke"));
		row.createCell((short) 2).setCellValue(new HSSFRichTextString("Tanggal"));
		row.createCell((short) 3).setCellValue(new HSSFRichTextString("Estimasi Arus Kas"));
		row.createCell((short) 4).setCellValue(new HSSFRichTextString("Awal Arus Kas Konversi"));
		row.createCell((short) 5).setCellValue(new HSSFRichTextString("Bunga SBE Konversi"));
		row.createCell((short) 6).setCellValue(new HSSFRichTextString("Tagihan Pokok"));
		row.createCell((short) 7).setCellValue(new HSSFRichTextString("Tagihan Bunga"));
		row.createCell((short) 8).setCellValue(new HSSFRichTextString("Selisih Bunga"));
		row.createCell((short) 9).setCellValue(new HSSFRichTextString("Akhir Arus Kas Konversi"));
		row.createCell((short) 10).setCellValue(new HSSFRichTextString("Saldo Teoritis"));
		
		int x=11;
		if(provFee.doubleValue()-getFee.doubleValue()!=0){
			row.createCell((short) x++).setCellValue(new HSSFRichTextString("Estimasi Arus Kas"));
			row.createCell((short) x++).setCellValue(new HSSFRichTextString("Awal Arus Kas Atribusi"));
			row.createCell((short) x++).setCellValue(new HSSFRichTextString("Bunga SBE Atribusi"));
			row.createCell((short) x++).setCellValue(new HSSFRichTextString("Amortisasi Atribusi"));
			row.createCell((short) x++).setCellValue(new HSSFRichTextString("Akhir Arus Kas Atribusi"));
			row.createCell((short) x++).setCellValue(new HSSFRichTextString("Saldo Atribusi"));
		}
		
		if(discount.doubleValue()>0){
			row.createCell((short) x++).setCellValue(new HSSFRichTextString("Nilai Kini Arus Kas"));
			row.createCell((short) x++).setCellValue(new HSSFRichTextString("Estimasi Arus Kas"));
			row.createCell((short) x++).setCellValue(new HSSFRichTextString("Awal Arus Kas Diskon"));
			row.createCell((short) x++).setCellValue(new HSSFRichTextString("Bunga SBE Diskon"));
			row.createCell((short) x++).setCellValue(new HSSFRichTextString("Amortisasi Diskon"));
			row.createCell((short) x++).setCellValue(new HSSFRichTextString("Akhir Arus Kas Diskon"));
			row.createCell((short) x++).setCellValue(new HSSFRichTextString("Saldo Diskon"));
		}

		rowNum = 2;
		for (int i = 0; i < listAngsuran.size(); i++) {
			DTOMap angsur = (DTOMap) listAngsuran.get(i);
			row = sheet.createRow(rowNum);
			row.createCell((short) 1).setCellValue(angsur.getInt("jadwalAngsurKe"));
			row.createCell((short) 2).setCellValue(new HSSFRichTextString(sdf.format(angsur.getDate("tglJadwal"))));
			row.createCell((short) 3).setCellValue(angsur.getBigDecimal("ESTIMASI_ARUS").doubleValue());
			row.getCell((short) 3).setCellStyle(moneyStyle);
			row.createCell((short) 4).setCellValue(angsur.getBigDecimal("SALDO_AWAL_KONVERSI").doubleValue());
			row.getCell((short) 4).setCellStyle(moneyStyle);
			row.createCell((short) 5).setCellValue(angsur.getBigDecimal("BUNGA_KONVERSI").doubleValue());
			row.getCell((short) 5).setCellStyle(moneyStyle);
			row.createCell((short) 6).setCellValue(angsur.getBigDecimal("POKOK_ARUS").doubleValue());
			row.getCell((short) 6).setCellStyle(moneyStyle);
			row.createCell((short) 7).setCellValue(angsur.getBigDecimal("BUNGA_ARUS").doubleValue());
			row.getCell((short) 7).setCellStyle(moneyStyle);
			row.createCell((short) 8).setCellValue(angsur.getBigDecimal("SELISIH_BUNGA_KONTRAKTUAL").doubleValue());
			row.getCell((short) 8).setCellStyle(moneyStyle);
			row.createCell((short) 9).setCellValue(angsur.getBigDecimal("SALDO_AKHIR_KONVERSI").doubleValue());
			row.getCell((short) 9).setCellStyle(moneyStyle);
			row.createCell((short) 10).setCellValue(angsur.getBigDecimal("saldoTeoritis").doubleValue());
			row.getCell((short) 10).setCellStyle(moneyStyle);			
			
			x=11;
			// Attribusi
			if(provFee.doubleValue()-getFee.doubleValue()!=0){
				row.createCell((short) x).setCellValue(angsur.getBigDecimal("ESTIMASI_ARUS_ATRIBUSI").doubleValue());
				row.getCell((short) x++).setCellStyle(moneyStyle);
				row.createCell((short) x).setCellValue(angsur.getBigDecimal("SALDO_AWAL_ARUS").doubleValue());
				row.getCell((short) x++).setCellStyle(moneyStyle);
				row.createCell((short) x).setCellValue(angsur.getBigDecimal("BUNGA_ATRIBUSI").doubleValue());
				row.getCell((short) x++).setCellStyle(moneyStyle);
				row.createCell((short) x).setCellValue(angsur.getBigDecimal("AMORTISASI_BIAYA_ATRIBUSI").doubleValue());
				row.getCell((short) x++).setCellStyle(moneyStyle);
				row.createCell((short) x).setCellValue(angsur.getBigDecimal("SALDO_AKHIR_ARUS").doubleValue());
				row.getCell((short) x++).setCellStyle(moneyStyle);
				row.createCell((short) x).setCellValue(angsur.getBigDecimal("SISA_ATRIBUSI").doubleValue());
				row.getCell((short) x++).setCellStyle(moneyStyle);
			}

			// Discount
			if(discount.doubleValue()>0){
				row.createCell((short) x).setCellValue(angsur.getBigDecimal("NILAI_KINI_ARUS").doubleValue());
				row.getCell((short) x++).setCellStyle(moneyStyle);
				row.createCell((short) x).setCellValue(angsur.getBigDecimal("ETSIMASI_ARUS_DISCOUNT").doubleValue());
				row.getCell((short) x++).setCellStyle(moneyStyle);
				row.createCell((short) x).setCellValue(angsur.getBigDecimal("SALDO_AWAL_DISCOUNT").doubleValue());
				row.getCell((short) x++).setCellStyle(moneyStyle);
				row.createCell((short) x).setCellValue(angsur.getBigDecimal("BUNGA_DISCOUNT").doubleValue());
				row.getCell((short) x++).setCellStyle(moneyStyle);
				row.createCell((short) x).setCellValue(angsur.getBigDecimal("AMORTISASI_DISCOUNT").doubleValue());
				row.getCell((short) x++).setCellStyle(moneyStyle);
				row.createCell((short) x).setCellValue(angsur.getBigDecimal("SALDO_AKHIR_DISCOUNT").doubleValue());
				row.getCell((short) x++).setCellStyle(moneyStyle);
				row.createCell((short) x).setCellValue(angsur.getBigDecimal("SALDO_DISKON").doubleValue());
				row.getCell((short) x++).setCellStyle(moneyStyle);
			}

			rowNum++;
		}
		row = sheet.createRow(rowNum);
		row.createCell((short) 2).setCellValue(new HSSFRichTextString("Total :"));
		row.createCell((short) 3).setCellValue(estimasiArusKasTot);
		row.getCell((short) 3).setCellStyle(moneyStyle);
		row.createCell((short) 5).setCellValue(bungaKonversiTot);
		row.getCell((short) 5).setCellStyle(moneyStyle);
		row.createCell((short) 6).setCellValue(angsuranPokokTot);
		row.getCell((short) 6).setCellStyle(moneyStyle);
		row.createCell((short) 7).setCellValue(angsuranBungaTot);
		row.getCell((short) 7).setCellStyle(moneyStyle);
		row.createCell((short) 8).setCellValue(selisihBungaKontraktualTot);
		row.getCell((short) 8).setCellStyle(moneyStyle);
		
		if(provFee.doubleValue()-getFee.doubleValue()!=0){
			row.createCell((short) 11).setCellValue(estimasiArusKasAtribusiTot);
			row.getCell((short) 11).setCellStyle(moneyStyle);
			row.createCell((short) 13).setCellValue(bungaAtribusiTot);
			row.getCell((short) 13).setCellStyle(moneyStyle);
			row.createCell((short) 14).setCellValue(amortisasiTot);
			row.getCell((short) 14).setCellStyle(moneyStyle);
		}
		
		x=provFee.doubleValue()-getFee.doubleValue()==0?11:17;
		if(discount.doubleValue()>0){
			row.createCell((short) x).setCellValue(nilaiKiniArusKasTot);
			row.getCell((short) x++).setCellStyle(moneyStyle);
			row.createCell((short) x).setCellValue(estimasiArusKasDiscountTot);
			row.getCell((short) x++).setCellStyle(moneyStyle);
			row.createCell((short) ++x).setCellValue(bungaDiscountTot);
			row.getCell((short) x++).setCellStyle(moneyStyle);
			row.createCell((short) x).setCellValue(amortisasiDiscountTot);
			row.getCell((short) x).setCellStyle(moneyStyle);
		}
		
		for (short j = 1; j <= 24; j++)
			sheet.autoSizeColumn(j);

		try {
			FileOutputStream fOut = new FileOutputStream("simulasi_jadwal_angsur.xls");
			// Write the Excel sheet
			workbook.write(fOut);
			fOut.flush();

			// Done deal. Close it.
			fOut.close();
			File file = new File("simulasi_jadwal_angsur.xls");
			Filedownload.save(file, "XLS");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}