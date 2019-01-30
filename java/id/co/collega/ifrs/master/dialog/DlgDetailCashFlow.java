package id.co.collega.ifrs.master.dialog;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;

import id.co.collega.ifrs.common.Cryptograph;
import id.co.collega.ifrs.common.DTOMap;
import id.co.collega.ifrs.common.FunctionUtils;
import id.co.collega.ifrs.master.service.MasterServices;
import id.co.collega.ifrs.util.ComponentUtil;
import id.co.collega.ifrs.util.MessageBox;
import id.co.collega.v7.seed.config.AuthenticationService;
import id.co.collega.v7.ui.component.composite.PopupController;

@org.springframework.stereotype.Component
@Scope("execution")
public class DlgDetailCashFlow extends PopupController<Grid> {

	@Autowired
	AuthenticationService auth;
	@Autowired
	MasterServices masterService;

	@Wire
	Textbox txtNoRekening;
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
	@Wire Tab			tabArusKas;
	@Wire Radiogroup	STS_CIA;
	
	@Wire Decimalbox	txtPlafon;
	@Wire Decimalbox 	decNominalProvisi;
	@Wire Decimalbox	decBiayaPerolehan;
	@Wire Decimalbox	txtDiscount;
	@Wire Decimalbox	txtBunga;
	@Wire Decimalbox	decIrr;
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

	@Wire Listbox listArusKas;
	
	DTOMap mapFromParent = new DTOMap();
	
	String user, pass, keterangan, returnValue="gagal" ;
	private double estimasiArusKasTot=0;
	private double bungaKonversiTot=0;
	private double nilaiKini=0;
	private double angsuranPokokTot=0;
	private double angsuranBungaTot=0;
	private double selisihBungaKontraktualTot=0;
	private BigDecimal SBE=BigDecimal.ZERO;
	private double totPokok=0;
	private double totBunga=0;
	private double totAngsur=0;

	public void doAfterCompose(Grid comp) throws Exception {
		super.doAfterCompose(comp);
		Map<String, Object> data = (Map<String, Object>) Executions.getCurrent().getArg();
		mapFromParent = (DTOMap) data.get("data");
		
		doLoadDataRek(mapFromParent.getString("ACCNBR"),mapFromParent.getString("TGL_POS"));
		doLoadCashFlow(mapFromParent.getString("ACCNBR"),mapFromParent.getString("TGL_POS"));
		
		List<DTOMap> listJnsBunga = (List<DTOMap>) masterService.getDataMaster(" SELECT PARMID,PARMNM,PARMIDOTH from CFG_PARM "
				+ "																WHERE PARMGRP=15 ORDER BY PARMID "
								,new Object[]{});
		Comboitem ciJnsBunga = new Comboitem();
		if (listJnsBunga.size() > 0) {
			for (DTOMap map : listJnsBunga) {
				ciJnsBunga = new Comboitem();
				ciJnsBunga.setLabel(map.getString("PARMID") + " - "+ map.getString("PARMNM"));
				ciJnsBunga.setValue(map.getString("PARMIDOTH"));
				cmbJnsBunga.appendChild(ciJnsBunga);
			}
		}
	}
	
	private void doLoadDataRek(String noRek,String TGL_POS) {
		DTOMap dataRek=masterService.getMapMaster(" SELECT 	INTTYPE,LNPERIOD,LNSTRDT,LNDUEDT, "
				+ "											PLAFOND,INTRATE,SBE_ANNUAL"
				+ "									FROM LOAN_MASTER "
				+ "									WHERE TGL_POS=?"
				+ "										AND ACCNBR=?", new Object[]{TGL_POS,noRek});
		if (dataRek!=null) {
			ComponentUtil.setValue(txtNoRekening, noRek);
			ComponentUtil.setValue(cmbJnsBunga, dataRek.getString("INTTYPE"));
			ComponentUtil.setValue(txtTenor, dataRek.getInt("LNPERIOD"));
			ComponentUtil.setValue(txtTglMulai, dataRek.getDate("LNSTRDT"));
			ComponentUtil.setValue(txtTglAkhir, dataRek.getDate("LNDUEDT"));
			txtPlafon.setValue(dataRek.getBigDecimal("PLAFOND"));
			txtBunga.setValue(dataRek.getBigDecimal("INTRATE"));
		}
	}

	private void doLoadCashFlow(String noRek,String TGL_POS) {
		SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
		
		if (listArusKas.getItemCount() > 0) {
			listArusKas.getItems().clear();
		}
		
		estimasiArusKasTot = 0;
		bungaKonversiTot = 0;
		angsuranPokokTot = 0;
		angsuranBungaTot = 0;
		selisihBungaKontraktualTot = 0;
		
		String sql = "SELECT * FROM CASHFLOW	"
				+ " WHERE ACCNBR=? AND TGL_POS=? ";
		System.out.println("noRek ="+noRek+"TGL_POS="+TGL_POS);
		List<DTOMap> lsCashFlow = masterService.getDataMaster(sql, new Object[]{noRek,TGL_POS});
		if (lsCashFlow.isEmpty() || lsCashFlow == null) {
			MessageBox.showInformation("Data Tidak Ditemukan");
		} else {
			for (DTOMap dtoMap : lsCashFlow) {
				int x = 1;
				Listitem liArusKas = new Listitem();
				liArusKas.appendChild(new Listcell(dtoMap.getInt("INSTL_SEQ").toString()));
				liArusKas.appendChild(new Listcell(sdf.format(dtoMap.getDate("TGL_ANGSUR"))));
				
				liArusKas.appendChild(new Listcell(FunctionUtils.moneyToText(dtoMap.getBigDecimal("ESTIMASI"))));
				if(dtoMap.getInt("INSTL_SEQ") == 0){
					liArusKas.appendChild(new Listcell(FunctionUtils.moneyToText(new BigDecimal (0.00))));
					dtoMap.put("NILAI_KINI_ARUS",new BigDecimal (0.00));
				}else{			
					liArusKas.appendChild(new Listcell(FunctionUtils.moneyToText(dtoMap.getBigDecimal("NILAI_KINI"))));	
				}
				
				liArusKas.appendChild(new Listcell(FunctionUtils.moneyToText(dtoMap.getBigDecimal("SALDO_AWAL"))));
				liArusKas.appendChild(new Listcell(FunctionUtils.moneyToText(dtoMap.getBigDecimal("INSTLINT"))));
				liArusKas.appendChild(new Listcell(FunctionUtils.moneyToText(dtoMap.getBigDecimal("BASENMNL"))));
				liArusKas.appendChild(new Listcell(FunctionUtils.moneyToText(dtoMap.getBigDecimal("INTNMNL"))));
				liArusKas.appendChild(new Listcell(FunctionUtils.moneyToText(dtoMap.getBigDecimal("AMOREIR"))));
				liArusKas.appendChild(new Listcell(FunctionUtils.moneyToText(dtoMap.getBigDecimal("ENDBAL"))));
				liArusKas.appendChild(new Listcell(FunctionUtils.moneyToText(dtoMap.getBigDecimal("TEORIBAL"))));

				estimasiArusKasTot += dtoMap.getBigDecimal("ESTIMASI").doubleValue();
				bungaKonversiTot += dtoMap.getBigDecimal("INSTLINT").doubleValue();
				
				nilaiKini += dtoMap.getBigDecimal("NILAI_KINI").doubleValue();
				angsuranPokokTot += dtoMap.getBigDecimal("BASENMNL").doubleValue();
				angsuranBungaTot += dtoMap.getBigDecimal("INTNMNL").doubleValue();
				selisihBungaKontraktualTot += dtoMap.getBigDecimal("AMOREIR").doubleValue();
				SBE=dtoMap.getBigDecimal("SBE");
				listArusKas.appendChild(liArusKas);
			}
			DecimalFormat df = new DecimalFormat("#,##0.00");
			
			ComponentUtil.setValue(decArus1, df.format(estimasiArusKasTot));
			ComponentUtil.setValue(decArus2, df.format(nilaiKini));
			ComponentUtil.setValue(decArus4, df.format(bungaKonversiTot));
			ComponentUtil.setValue(decArus5, df.format(angsuranPokokTot));
			ComponentUtil.setValue(decArus6, df.format(angsuranBungaTot));
			ComponentUtil.setValue(decArus7, df.format(selisihBungaKontraktualTot));
			decIrr.setValue(SBE);
			decSBE.setValue(SBE);
		}
	}

	@Override
	public Object returnValue() {
		// TODO Auto-generated method stub
		return returnValue;
	}
}