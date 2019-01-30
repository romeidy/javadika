package id.co.collega.ifrs.master.report;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFCreationHelper;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
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
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.jet.gand.services.GlobalVariable;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import id.co.collega.ifrs.common.DTOMap;
import id.co.collega.ifrs.common.FunctionUtils;
import id.co.collega.ifrs.common.JRreportWindow;
import id.co.collega.ifrs.common.JdbcTemplate;
import id.co.collega.ifrs.master.service.MasterServices;
import id.co.collega.ifrs.util.ComponentUtil;
import id.co.collega.ifrs.util.MessageBox;
import id.co.collega.v7.ef.common.DataSession;
import id.co.collega.v7.seed.config.AuthenticationService;
import id.co.collega.v7.ui.component.DialogUtil;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRImageLoader;

@org.springframework.stereotype.Component
@Scope("desktop")
public class WndLaporanNominatifKredit extends SelectorComposer<Component>{

	@Autowired AuthenticationService auth;
	@Autowired Environment env;
	
	public DataSession dataSession;
	@Autowired JdbcTemplate jt;
	@Wire Window WndLaporanLTKT;
//	@Wire org.zkoss.zul.Row rowParameter;
	@Wire Datebox txtTgl;
	@Wire Button btnCari;
	@Wire Button btnReset;
	@Wire Button btnCetak;
	@Wire DTOMap workflow;
	@Wire Combobox cmbFormat;
	
	@Wire Combobox cmbJnsData;
	@Wire Combobox cmbCabangKonsol;
	@Wire org.zkoss.zul.Row rowParameter3;
	@Wire org.zkoss.zul.Row rowParameter4;
	@Wire Combobox cmbProduk;
	@Wire Combobox cmbCabang;
	@Wire Combobox cmbStage;
	@Wire Textbox txtNoRek;
	@Wire Checkbox chkNoRek;
	
	@Wire Decimalbox decPlafond;
	@Wire Decimalbox decSaldoAkhir;
	@Wire Decimalbox decModifikasi;
	@Wire Decimalbox decImpairAsset;
	@Wire Decimalbox decAmor;
	@Wire Decimalbox decNilaiWajar;
	@Wire Decimalbox decECL1;
	@Wire Decimalbox decECL2;
	@Wire Decimalbox decLonggarTarik;
	
	@Wire Listbox listData;
	@Wire Listheader lhECL1;
	@Wire Listheader lhImp;
	@Autowired MasterServices masterService;
	
	protected String location;
	protected String bankId;
	protected String branchId;
	protected String userId;
	protected String userName;
	protected String bankNm;
	protected String branchNm;
	protected String branchAddr;
	protected String flagCore;
	
	Boolean onLoad = false;
	
	private List<DTOMap> map = new ArrayList<DTOMap>(0);
	private List<DTOMap> listDataNomiKr;
	private List<DTOMap> listda;
	private Date openDate;
	
	
	@Wire Div moldingPaging;
	@Wire Intbox insertPage;
	@Wire Button firstPage;
    @Wire Button previous;
    @Wire Button next;
    @Wire Button lastPage;
    
    @Wire Label perData;
    @Wire Label perPage;
    @Wire Label allData;
	private Integer manyRow;
	private Integer page;
	public List listAllRow=new ArrayList<>();
	
	SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
	
	@Override
	public void doAfterCompose(Component comp) throws Exception  {
		super.doAfterCompose(comp);
		
		loadDataProduk();
		doLoadCabang();
		
		btnCetak.addEventListener(Events.ON_CLICK,new EventListener<Event>(){
			public void onEvent(Event e)throws Exception{
				doPrint();
			}
		});
		
		btnCari.addEventListener(Events.ON_CLICK,new EventListener<Event>(){
			public void onEvent(Event e)throws Exception{
				doFind();
//				txtTgl.setDisabled(true);
			}
		});
		
		btnReset.addEventListener(Events.ON_CLICK,new EventListener<Event>(){
			public void onEvent(Event e)throws Exception{
				doReset();
//				txtTgl.setDisabled(false);
//				txtTgl2.setDisabled(false);
			}
		});
		
		chkNoRek.addEventListener(Events.ON_CLICK,new EventListener<Event>(){
			public void onEvent(Event e)throws Exception{
				if (!chkNoRek.isChecked()) {
					txtNoRek.setDisabled(false);
				}else{
					ComponentUtil.setValue(txtNoRek, null); 
					txtNoRek.setDisabled(true);
				}
			}
		});
		
		manyRow=50;
		page=1;
		firstPage.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(Event e) throws Exception {
//				Listbox listData = (Listbox) listData;
				listData.getItems().clear();
				((Button)previous).setDisabled(true);
				((Button)firstPage).setDisabled(true);
				((Button)next).setDisabled(false);
				((Button)lastPage).setDisabled(false);
				page = 1;
				showListPerPage(listData,1,page*manyRow);
				ComponentUtil.setValue(perData,"[ 1 - "+String.valueOf(page*manyRow));
			}
		});
		previous.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(Event e) throws Exception {
//				Listbox listData = (Listbox) listData;
				listData.getItems().clear();
				page -= 1;
				if(page.intValue() < 1) page = 1;
				if(page.intValue() == 1){
					//berarti first page
					((Button)previous).setDisabled(true);
					((Button)firstPage).setDisabled(true);
					showListPerPage(listData,1,page*manyRow);
					ComponentUtil.setValue(perData,"[ 1 - "+String.valueOf(page*manyRow));
				}else {
					((Button)previous).setDisabled(false);
					((Button)firstPage).setDisabled(false);
					showListPerPage(listData,((page-1)*manyRow)+1,page*manyRow);
					ComponentUtil.setValue(perData,"[ "+String.valueOf(((page-1)*manyRow)+1)+" - "+String.valueOf(page*manyRow));
				}
				((Button)next).setDisabled(false);
				((Button)lastPage).setDisabled(false);
			}
		});
		next.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(Event e) throws Exception {
//				Listbox listData = (Listbox) listData;
				listData.getItems().clear();
				page += 1;
				if(page.intValue() > ((listAllRow.size()-1)/manyRow)+1) page = ((listAllRow.size()-1)/manyRow)+1;
				if(page.intValue() == ((listAllRow.size()-1)/manyRow)+1){
					//berarti last page
					((Button)next).setDisabled(true);
					((Button)lastPage).setDisabled(true);
					showListPerPage(listData,((page-1)*manyRow)+1,listAllRow.size());
					ComponentUtil.setValue(perData,"[ "+String.valueOf(((page-1)*manyRow)+1)+" - "+String.valueOf(listAllRow.size()));
				}else{
					((Button)next).setDisabled(false);
					((Button)lastPage).setDisabled(false);
					showListPerPage(listData,((page-1)*manyRow)+1,page*manyRow);
					ComponentUtil.setValue(perData,"[ "+String.valueOf(((page-1)*manyRow)+1)+" - "+String.valueOf(page*manyRow));
				}
				((Button)previous).setDisabled(false);
				((Button)firstPage).setDisabled(false);
			}
		});
		lastPage.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(Event e) throws Exception {
//				Listbox listData = (Listbox) listData;
				listData.getItems().clear();
				page = ((listAllRow.size()-1)/manyRow)+1;
				((Button)previous).setDisabled(false);
				((Button)firstPage).setDisabled(false);
				((Button)next).setDisabled(true);
				((Button)lastPage).setDisabled(true);
				showListPerPage(listData,((page-1)*manyRow)+1,listAllRow.size());
				ComponentUtil.setValue(perData,"[ "+String.valueOf(((page-1)*manyRow)+1)+" - "+String.valueOf(listAllRow.size()));
			}
		});
		insertPage.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {
			public void onEvent(Event e) throws Exception {
//				Listbox listData = (Listbox) listData;
				Integer insertPageData =(Integer)ComponentUtil.getValue(insertPage);
				if(insertPageData.intValue() > 0){
					if(insertPageData.intValue() <= ((listAllRow.size()-1)/manyRow)+1){
						listData.getItems().clear();
						page = insertPageData;
						if(insertPageData.intValue() == 1){
							//berarti first page
							((Button)previous).setDisabled(true);
							((Button)firstPage).setDisabled(true);
							((Button)next).setDisabled(false);
							((Button)lastPage).setDisabled(false);
							showListPerPage(listData,1,page*manyRow);
							ComponentUtil.setValue(perData,"[ 1 - "+String.valueOf(page*manyRow));
						}else if(insertPageData.intValue() == ((listAllRow.size()-1)/manyRow)+1){
							//berarti last page
							((Button)previous).setDisabled(false);
							((Button)firstPage).setDisabled(false);
							((Button)next).setDisabled(true);
							((Button)lastPage).setDisabled(true);
							showListPerPage(listData,((page-1)*manyRow)+1,listAllRow.size());
							ComponentUtil.setValue(perData,"[ "+String.valueOf(((page-1)*manyRow)+1)+" - "+String.valueOf(listAllRow.size()));
						}else{
							((Button)previous).setDisabled(false);
							((Button)firstPage).setDisabled(false);
							((Button)next).setDisabled(false);
							((Button)lastPage).setDisabled(false);
							showListPerPage(listData,((page-1)*manyRow)+1,page*manyRow);
							ComponentUtil.setValue(perData,"[ "+String.valueOf(((page-1)*manyRow))+1+" - "+String.valueOf(page*manyRow));
						}
					}
				}
			}
		});
		
		doReset();
		DTOMap map = (DTOMap) GlobalVariable.getInstance().get("syshost");
		openDate=map.getDate("OPEN_DATE");
		txtTgl.setValue(openDate);
		cmbJnsData.setSelectedIndex(0);
		cmbStage.setSelectedIndex(0);
		
		listData.addEventListener(Events.ON_DOUBLE_CLICK, new EventListener<Event>() {
			public void onEvent(Event event) throws Exception {
				if (listData.getItemCount() > 0) {
					Listitem item=listData.getSelectedItem();
					if (item!=null) {
						DTOMap dtoMap=(DTOMap)item.getAttribute("DATA");
						if (dtoMap!=null) {
							DTOMap isExist=(DTOMap)masterService.getMapMaster(" SELECT COUNT(1) AS EXIST FROM CASHFLOW	"
									+ " WHERE ACCNBR=? AND TGL_POS=? ", new Object[]{dtoMap.getString("ACCNBR"),dtoMap.getString("TGL_POS")});
							if (isExist!=null) {
								if (isExist.getBigDecimal("EXIST").compareTo(BigDecimal.ZERO)!=0) {
									getDetail(dtoMap);
								}else{
									MessageBox.showInformation("Tidak ada data CashFlow.");
								}
							}else{
								MessageBox.showInformation("Tidak ada data CashFlow.");
							}
						}
					}
				}
			}
		});
	}
	
	private void getDetail(DTOMap data){
		Map<String, Object> maps = new HashMap<String, Object>();
		maps.put("data", data);
		DialogUtil.
		showPopupDialogCloseOnly("/page/dialog/WndDialogCashFlow.zul", "Informasi Rekening", getSelf(), new EventListener<Event>() {
			public void onEvent(Event event) throws Exception {
				// TODO Auto-generated method stub
				
			}
		}, maps);
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
	
	private void doLoadCabang() {
		cmbCabang.getItems().clear();
		List<DTOMap> listData = (List<DTOMap>) masterService.getDataMaster(
				"SELECT KD_CAB,NM_CAB FROM CFG_CABANG "
				+ "	WHERE "
				+ "		TGL_POS IN (SELECT MAX(TGL_POS) FROM CFG_CABANG) "
				+ "	ORDER BY 1",
				null);
		Comboitem item = new Comboitem();
		item.setLabel("All");
		item.setValue("All");
		cmbCabang.appendChild(item);
		for (DTOMap map : listData) {
			item = new Comboitem();
			item.setLabel(map.getString("KD_CAB")+" - "+map.getString("NM_CAB"));
			item.setValue(map.getString("KD_CAB"));
			cmbCabang.appendChild(item);
		}
		cmbCabang.setSelectedIndex(0);
	}

	private void loadDataProduk() {
		List<DTOMap> listProduk=masterService.getDataMaster(" SELECT PARMID,PARMNM "
				+ "												FROM CFG_PARM "
				+ "												WHERE PARMGRP=1 ORDER BY PARMID  ",new Object[]{});
			cmbProduk.getItems().clear();
		if (listProduk.size() > 0) {
			Comboitem ciPrd=new Comboitem();
			ciPrd.setLabel("All");
			ciPrd.setValue("All");
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

	@SuppressWarnings("unchecked")
	private void doFind() {
		try{
			BigDecimal	totPlafond=BigDecimal.ZERO,totSaldoAkhir=BigDecimal.ZERO,totModifikasi=BigDecimal.ZERO,
					totImpairAsset=BigDecimal.ZERO,totAmor=BigDecimal.ZERO,totNilaiWajar=BigDecimal.ZERO,
					totECL1=BigDecimal.ZERO,totECL2=BigDecimal.ZERO,totLonggarTarik=BigDecimal.ZERO;
			
			if(isValid()){
				page=1;
				listAllRow.clear();
				String 	produk="",cabang="",noRek="",stage="",Q_JnsDataModifikasi="",
						Q_JnsDataECL1="",Q_JnsDataECL2="",Q_JnsDataImpairAsset,
						Q_ParamJnsDataECL1="",Q_ParamJnsDataECL2="",Q_GroupBy="";
				
				String jnsData=(String) ComponentUtil.getValue(cmbJnsData);
				if (jnsData.equals("0")) {
					
					lhECL1.setLabel("CKPN Kolektif");
					lhImp.setLabel("CKPN Individu");
					
					Q_JnsDataModifikasi	=" COALESCE(Z.MODIFIKASI,0) AS MODIFIKASI	";
					Q_JnsDataECL1 		=" COALESCE(Z.ECL1,0) AS ECL1				";
					Q_JnsDataImpairAsset=" COALESCE(Z.IMPAIR_ASET,0) AS IMPAIR_ASET ";
					Q_JnsDataECL2 		=" CAST(0 AS DECIMAL) AS ECL2				";
				} else {
					
					lhECL1.setLabel("ECL1");
					lhImp.setLabel("Impair Aset");
					
					Q_JnsDataModifikasi	=" COALESCE(Z.IMPAIR_ASET,0) AS MODIFIKASI	";
					Q_JnsDataECL1 		=" SUM(CASE WHEN A.VERSION='0' 				"
										+"			THEN  COALESCE(A.ECL,0) 		"
										+"			ELSE 0 END) AS ECL1 			";
					Q_JnsDataECL2		=" SUM(COALESCE(X.ECL,0)) AS ECL2 			";
					Q_JnsDataImpairAsset=" SUM(CASE WHEN A.VERSION='1' 				"
										+"			THEN  COALESCE(A.ECL,0) 		"
										+"			ELSE 0 END) AS IMPAIR_ASET		";
					
					Q_ParamJnsDataECL1	=" LEFT OUTER JOIN ("
										+ "					SELECT ACCNBR,TGL_POS,VERSION,SUM(ECL) AS ECL	"
										+ "					FROM ECL_LT										"
										+ "					GROUP BY TGL_POS,ACCNBR,VERSION) A				"
										+" 	ON Z.ACCNBR=A.ACCNBR				"
										+" 		AND Z.TGL_POS=A.TGL_POS			";
					
					Q_ParamJnsDataECL2	=" LEFT OUTER JOIN ECL_LT_OFF X			"
										+"	ON Z.ACCNBR=X.ACCNBR				"
										+"		AND Z.TGL_POS=X.TGL_POS			"
										+ "		AND Z.BRANCHID=X.BRANCHID		";
					
					Q_GroupBy			=" GROUP BY											"
										+"	Z.PRODID, Z.BRANCHID, Z.ACCNBR,					"
										+"	Z.DPD, Z.RATING, Z.PLAFOND, Z.LNSTRDT,			"
										+"	Z.LNDUEDT, Z.LNPERIOD, Z.BASENMNL, Z.INTNMNL,	"
										+"	Z.PENALTY, Z.ENDBAL, Z.TUNGPKK, Z.TUNGGBNG,		"
										+"	Z.ATRIBUSI, Z.NILAI_PEROLEHAN, Z.MODIFIKASI,	"
										+"	Z.IMPAIR_ASET, Z.AMOREIR,D.PARMNM,E.PARMNM,		"
										+"	Z.ACCRU, Z.ACUM_ACCRU, Z.INTRATE, 				"
										+"	Z.SBE_ANNUAL,Z.WDRSPARE,Z.TGL_POS			";
					
				}
				
				if (ComponentUtil.getValue(cmbProduk)!=null && !((String) ComponentUtil.getValue(cmbProduk)).equals("All")) {
					produk = " AND Z.PRODID='"+((String) ComponentUtil.getValue(cmbProduk))+"' ";
				}
				
				if (ComponentUtil.getValue(cmbCabang)!=null && !((String) ComponentUtil.getValue(cmbCabang)).equals("All")) {
					cabang = " AND Z.BRANCHID='"+((String) ComponentUtil.getValue(cmbCabang))+"' ";
				}
				
				if (ComponentUtil.getValue(txtNoRek)!=null && !chkNoRek.isChecked()) {
					noRek = " AND Z.ACCNBR='"+((String) ComponentUtil.getValue(txtNoRek))+"' ";
				}
				
				if (ComponentUtil.getValue(cmbStage)!=null && !((String) ComponentUtil.getValue(cmbStage)).equals("0")) {
					stage = " AND Y.PARMIDOTH = '"+((String) ComponentUtil.getValue(cmbStage))+"' ";
				}
				
				Date tglPeriode=(Date)ComponentUtil.getValue(txtTgl);
				
				String SQL= "		SELECT 	"
						+ "				Z.PRODID, Z.BRANCHID, Z.ACCNBR, Z.TGL_POS,					"
						+ "				UPPER(D.PARMNM) AS STATUS , UPPER(E.PARMNM) AS JNSBUNGA,	"
						+ "				Z.DPD, Z.RATING, Z.PLAFOND, Z.LNSTRDT, 						"
						+ "				Z.LNDUEDT, Z.LNPERIOD, COALESCE(Z.BASENMNL,0) AS BASENMNL, 	"
						+ "				COALESCE(Z.INTNMNL,0) AS INTNMNL,COALESCE(Z.PENALTY,0) AS PENALTY,		"
						+ "				COALESCE(CASE WHEN Z.ENDBAL < 0 THEN 0 ELSE Z.ENDBAL END ,0) AS ENDBAL, COALESCE(Z.TUNGPKK,0) AS TUNGPKK,		"
						+ "				COALESCE(Z.TUNGGBNG,0) AS TUNGGBNG, COALESCE(Z.ATRIBUSI,0) AS ATRIBUSI,	"
						+ "				COALESCE(Z.NILAI_PEROLEHAN,0) AS NILAI_PEROLEHAN, 						"
						+ "				"+Q_JnsDataModifikasi+","
						+ "				COALESCE(Z.AMOREIR,0) AS AMOREIR,										"
						+ "				"+Q_JnsDataECL1+"," 
						+ "				"+Q_JnsDataImpairAsset+","
						+ "				"+Q_JnsDataECL2+","
						+ "				COALESCE(Z.ACCRU,0) AS ACCRU, COALESCE(Z.ACUM_ACCRU,0) AS ACUM_ACCRU, "
						+ "				COALESCE(Z.INTRATE,0) AS INTRATE, COALESCE(Z.SBE_ANNUAL,0) AS SBE_ANNUAL, "
						+ "				COALESCE(Z.WDRSPARE,0) AS WDRSPARE	"
						+ "			FROM	LOAN_MASTER Z	"+Q_ParamJnsDataECL1
						+ "									LEFT OUTER JOIN CFG_PARM D 				"
						+ "										ON D.PARMGRP=16 					"
						+ "										AND CAST(D.PARMID AS INT)=Z.ACCSTS	"
						+ "									LEFT OUTER JOIN CFG_PARM E				"
						+ "										ON E.PARMGRP=15 					"
						+ "										AND E.PARMID =Z.INTTYPE				"
						+ "									"+Q_ParamJnsDataECL2
						+ "									LEFT OUTER JOIN CFG_PARM Y 				"
						+ "										ON Y.PARMGRP=3 						"
						+ "										AND CAST(Y.PARMID AS INT)=Z.RATING	"
						+ "			WHERE Z.TGL_POS ='"+sdf.format(tglPeriode)+"'					"
						+					produk+cabang+noRek+stage
						+ "					AND Z.ACCSTS NOT IN (0,6,7,8,9)							"
						+ "					AND Z.VERSION='0' 										"
						+			Q_GroupBy
						+ "			ORDER BY 3";
				
				System.out.println(SQL);
				
				listDataNomiKr = new ArrayList<DTOMap>();
				listDataNomiKr = masterService.getDataMaster(SQL,
							new Object[]{});
				if (listDataNomiKr != null && listDataNomiKr.size()>0) {
					for (int i = 0; i < listDataNomiKr.size(); i++) {
						DTOMap map = listDataNomiKr.get(i);
						Listitem item = new Listitem();
						map.put("TGL_POS", sdf.format(tglPeriode));
						item.setAttribute("DATA", map);
						item.appendChild(new Listcell(map.getString("PRODID")));
						item.appendChild(new Listcell(map.getString("BRANCHID")));
						item.appendChild(new Listcell(map.getString("ACCNBR")));
						item.appendChild(new Listcell(FunctionUtils.moneyToText(map.getBigDecimal("PLAFOND")))); 
						item.appendChild(new Listcell(FunctionUtils.moneyToText(map.getBigDecimal("ENDBAL")))); 
						item.appendChild(new Listcell(FunctionUtils.moneyToText(map.getBigDecimal("MODIFIKASI"))));
						item.appendChild(new Listcell(FunctionUtils.moneyToText(map.getBigDecimal("AMOREIR"))));
						BigDecimal nilaiWajar=BigDecimal.ZERO;
						nilaiWajar = map.getBigDecimal("ENDBAL")
								.subtract(map.getBigDecimal("AMOREIR"))
								.subtract(map.getBigDecimal("MODIFIKASI"));
						item.appendChild(new Listcell(FunctionUtils.moneyToText(nilaiWajar)));
						item.appendChild(new Listcell(FunctionUtils.moneyToText(map.getBigDecimal("ECL1"))));
						item.appendChild(new Listcell(FunctionUtils.moneyToText(map.getBigDecimal("IMPAIR_ASET"))));
						item.appendChild(new Listcell(FunctionUtils.moneyToText(map.getBigDecimal("ECL2"))));
						item.appendChild(new Listcell(FunctionUtils.moneyToText(map.getBigDecimal("WDRSPARE"))));
						item.appendChild(new Listcell(map.getInt("DPD").toString()));
						item.appendChild(new Listcell(map.getInt("RATING").toString()));
						item.appendChild(new Listcell(FunctionUtils.moneyToText(map.getBigDecimal("BASENMNL"))));
						item.appendChild(new Listcell(FunctionUtils.moneyToText(map.getBigDecimal("INTNMNL"))));
						item.appendChild(new Listcell(FunctionUtils.moneyToText(map.getBigDecimal("PENALTY"))));
						item.appendChild(new Listcell(FunctionUtils.moneyToText(map.getBigDecimal("TUNGPKK"))));
						item.appendChild(new Listcell(FunctionUtils.moneyToText(map.getBigDecimal("TUNGGBNG"))));
						item.appendChild(new Listcell(FunctionUtils.moneyToText(map.getBigDecimal("ATRIBUSI"))));
						item.appendChild(new Listcell(FunctionUtils.moneyToText(map.getBigDecimal("NILAI_PEROLEHAN"))));
						item.appendChild(new Listcell(FunctionUtils.moneyToText(map.getBigDecimal("ACCRU"))));
						item.appendChild(new Listcell(FunctionUtils.moneyToText(map.getBigDecimal("ACUM_ACCRU"))));
						item.appendChild(new Listcell(FunctionUtils.moneyToText(map.getBigDecimal("INTRATE"))));
						item.appendChild(new Listcell(FunctionUtils.moneyToText(map.getBigDecimal("SBE_ANNUAL"))));
						item.appendChild(new Listcell(map.getDate("LNSTRDT")==null ?"":sdf.format(map.getDate("LNSTRDT"))));
						item.appendChild(new Listcell(map.getDate("LNDUEDT")==null ?"":sdf.format(map.getDate("LNDUEDT"))));
						item.appendChild(new Listcell(map.getInt("LNPERIOD")==null?"":map.getInt("LNPERIOD").toString()));
						item.appendChild(new Listcell(map.getString("STATUS")));
						item.appendChild(new Listcell(map.getString("JNSBUNGA")));
						listAllRow.add(item);

						totPlafond		= totPlafond.add(map.getBigDecimal("PLAFOND"));
						totSaldoAkhir 	= totSaldoAkhir.add(map.getBigDecimal("ENDBAL"));
						totModifikasi 	= totModifikasi.add(map.getBigDecimal("MODIFIKASI"));
						totImpairAsset 	= totImpairAsset.add(map.getBigDecimal("IMPAIR_ASET"));
						totAmor 		= totAmor.add(map.getBigDecimal("AMOREIR"));
						totNilaiWajar 	= totNilaiWajar.add(nilaiWajar); 
						totECL1 		= totECL1.add(map.getBigDecimal("ECL1"));
						totECL2 		= totECL2.add(map.getBigDecimal("ECL2"));
						totLonggarTarik = totLonggarTarik.add(map.getBigDecimal("WDRSPARE"));
					}
					listData.getItems().clear();
					if(listAllRow.size() > manyRow){
						showListPerPage(listData,1,manyRow);
						ComponentUtil.setValue(perData,"[ 1 - "+manyRow.toString());
					}else{
						showListPerPage(listData,1,listAllRow.size());
						ComponentUtil.setValue(perData,"[ 1 - "+String.valueOf(listAllRow.size()));
					}
					ComponentUtil.setValue(perPage,"/ "+String.valueOf(((listAllRow.size()-1)/manyRow)+1));
					ComponentUtil.setValue(allData," / "+String.valueOf(listAllRow.size())+" ]");
					((Button)firstPage).setDisabled(true);
					((Button)previous).setDisabled(true);
					
					((Button)next).setDisabled(false);
					((Button)lastPage).setDisabled(false);
					
					decPlafond.setValue(totPlafond);
					decSaldoAkhir.setValue(totSaldoAkhir);
					decModifikasi.setValue(totModifikasi);
					decImpairAsset.setValue(totImpairAsset);
					decAmor.setValue(totAmor);
					decNilaiWajar.setValue(totNilaiWajar);
					decECL1.setValue(totECL1);
					decECL2.setValue(totECL2);
					decLonggarTarik.setValue(totLonggarTarik);
				}else{
					MessageBox.showInformation("Data tidak ditemukan");
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
			MessageBox.showError(e.getMessage());
		}
	}

	public boolean isValid(){
		boolean isValid=true;
		if (isValid) {
			if (ComponentUtil.getValue(txtTgl) == null) {
				MessageBox.showInformation("Periode Harus diisi.");
				isValid=false;
			}
			if (ComponentUtil.getValue(cmbJnsData) == null) {
				MessageBox.showInformation("Jenis Data diisi.");
				isValid=false;
			}
			if (ComponentUtil.getValue(cmbProduk) == null) {
				MessageBox.showInformation("Produk Harus diisi.");
				isValid=false;
			}
			if (ComponentUtil.getValue(cmbCabang) == null) {
				MessageBox.showInformation("Cabang Harus diisi.");
				isValid=false;
			}
			if (ComponentUtil.getValue(cmbStage) == null) {
				MessageBox.showInformation("Stage Harus diisi.");
				isValid=false;
			}
			if (!chkNoRek.isChecked()) {
				if (ComponentUtil.getValue(txtNoRek) == null) {
					MessageBox.showInformation("No. Rekening Harus diisi.");
					isValid=false;
				}
			}
		}
		return isValid;
	}
	
	public void doReset() {
		listData.getItems().clear();
		txtTgl.setDisabled(false);
		moldingPaging.setVisible(false);
		
		ComponentUtil.setValue(txtTgl, openDate);
		System.out.println("open date ::: "+openDate);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void doPrint() throws SQLException{
		Date date = new Date();
		List<DTOMap> listMap = listDataNomiKr;
		
		if (listDataNomiKr == null || listDataNomiKr.size() <= 0) {
			MessageBox.showInformation("Tidak ada data yang akan dicetak");
			return;
		}
		String type = (String) ComponentUtil.getValue(cmbFormat);

		if (type.equals("pdf")) {
			String fileName="/jasper/LaporanNominatifKredit.jasper";

			String realpath = Executions.getCurrent().getDesktop()
					.getWebApp().getRealPath(fileName);
			System.out.println(realpath);

			DTOMap cfgsys=(DTOMap) GlobalVariable.getInstance().get("syshost");
			DTOMap branchMap=(DTOMap) GlobalVariable.getInstance().get("branchMap");

			Connection conn = jt.getDataSource().getConnection();
			
			HashMap param = new HashMap();
			param.put("period",sdf.format((Date) ComponentUtil.getValue(txtTgl)));
			String imageString = (String) cfgsys.getString("LOGO_BANK");
			byte[] decodedBytes = Base64.decode(imageString);
			try {
				param.put("logo", JRImageLoader.loadImage(decodedBytes));
			} catch (JRException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			param.put("logo", Executions.getCurrent().getDesktop()
//					.getWebApp().getRealPath(env.getRequiredProperty("LOGO_BPD")));
			param.put("namaBank",cfgsys.getString("NAMA_BANK"));
			param.put("cabang",branchMap.getString("KD_CAB"));
			param.put("namaCabang",branchMap.getString("NM_CAB"));
			param.put("printedBy",auth.getUserDetails().getUserId());
			param.put("repId","jv-rpt-0011");
			JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(listDataNomiKr);
			new JRreportWindow(this.getSelf(), true, param, fileName, ds,type,conn);
			doReset();
			conn.close();
		} else {
			doExport();
		}
	}
	
	public void doExport() {
		// Create a Workbook
		XSSFWorkbook workbook = new XSSFWorkbook(); // new HSSFWorkbook() for generating `.xls` file

        /* CreationHelper helps us create instances of various things like DataFormat, 
           Hyperlink, RichTextString etc, in a format (HSSF, XSSF) independent way */
        XSSFCreationHelper createHelper = workbook.getCreationHelper();

        // Create a Sheet
        XSSFSheet sheet = workbook.createSheet("Laporan Nominatif Kredit");
        
        // Create a Font for styling header cells
        XSSFFont headerFontTitle = workbook.createFont();
        
        // title
        headerFontTitle.setFontHeightInPoints((short) 18);
        headerFontTitle.setColor(IndexedColors.BLACK.getIndex());
        
        // Create a CellStyle with the font
        XSSFCellStyle headerCellStyleTitle = workbook.createCellStyle();
        headerCellStyleTitle.setFont(headerFontTitle);
        headerCellStyleTitle.setAlignment(headerCellStyleTitle.ALIGN_CENTER);
        headerCellStyleTitle.setVerticalAlignment(headerCellStyleTitle.VERTICAL_CENTER);
        headerCellStyleTitle.setBorderBottom(XSSFCellStyle.BORDER_MEDIUM);
        
        XSSFRow titleRow = sheet.createRow(0);
        XSSFCell titleCell=titleRow.createCell(0);
        titleCell.setCellValue(sheet.getSheetName());
        titleCell.setCellStyle(headerCellStyleTitle);
        
        XSSFFont headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 14);
        headerFont.setColor(IndexedColors.BLACK.getIndex());

        // Create a CellStyle with the font
        XSSFCellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        // Create a Row
        XSSFRow headerRow = sheet.createRow(3);

        // Freeze Pane
        sheet.createFreezePane(0, 4);
        
        String[] columns = {"Produk", "Cabang", "No. Rekening", "Plafond","Saldo Akhir","Modifikasi",
        					"Amor EIR","Nilai Wajar","ECL1","Impair Asset","ECL2","Longgar Tarik",
        					"Jml. Hr Bunga","Rating","Tagihan Pokok","Tagihan Bunga","Denda","Tungg. Pokok","Tungg. Bunga","Atribusi",
        					"Nilai Perolehan","Bunga Accru","Akm. Accru","Bunga Kontraktual (%)","Bunga Efektif (%)"
        					,"Tgl. Buka","Tgl. Jatuh Tempo","Jangka Waktu","Status","Jns. Bunga"};
        
        // Merge for Title
        sheet.addMergedRegion(new CellRangeAddress(0,1,0,columns.length-1)); 
        
        // Create cells
        for(int i = 0; i < columns.length; i++) {
            XSSFCell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerCellStyle);
        }

        // Create Cell Style for formatting Date
        XSSFCellStyle dateCellStyle = workbook.createCellStyle();
        dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd-MM-yyyy"));

        // Create Other rows and cells with employees data
        int rowNum = 4;
        if (listDataNomiKr.size() > 0) {
			for (int i = 0; i < listDataNomiKr.size(); i++) {
				DTOMap DATA = (DTOMap) listDataNomiKr.get(i);
				String PRODUK			= "'" + DATA.getString("PRODID");
				String CABANG 			= "'" + DATA.getString("BRANCHID");
				String ACCNBR 			= "'" + DATA.getString("ACCNBR");
				BigDecimal PLAFOND 		= DATA.getBigDecimal("PLAFOND");
				BigDecimal SALDO_AKHIR 	= DATA.getBigDecimal("ENDBAL");
				BigDecimal MODIFIKASI 	= DATA.getBigDecimal("MODIFIKASI");
				BigDecimal AMOREIR 		= DATA.getBigDecimal("AMOREIR");
				BigDecimal NILAI_WAJAR	= BigDecimal.ZERO;
						   NILAI_WAJAR 	= DATA.getBigDecimal("ENDBAL")
								   			.subtract(DATA.getBigDecimal("AMOREIR"))
								   			.subtract(DATA.getBigDecimal("MODIFIKASI"));
				BigDecimal ECL1 		= DATA.getBigDecimal("ECL1");
				BigDecimal IMPAIR_ASET 	= DATA.getBigDecimal("IMPAIR_ASET");
				BigDecimal ECL2 		= DATA.getBigDecimal("ECL2");
				BigDecimal WDRSPARE 	= DATA.getBigDecimal("WDRSPARE");
				Integer DPD 			= DATA.getInt("DPD");
				Integer RATING 			= DATA.getInt("RATING");
				BigDecimal BASENMNL 	= DATA.getBigDecimal("BASENMNL");
				BigDecimal INTNMNL		= DATA.getBigDecimal("INTNMNL");
				BigDecimal PENALTY		= DATA.getBigDecimal("PENALTY");
				BigDecimal TUNGPKK		= DATA.getBigDecimal("TUNGPKK");
				BigDecimal TUNGGBNG 	= DATA.getBigDecimal("TUNGGBNG");
				BigDecimal ATRIBUSI		= DATA.getBigDecimal("ATRIBUSI");
				BigDecimal NILAI_PEROLEHAN= DATA.getBigDecimal("NILAI_PEROLEHAN");
				BigDecimal ACCRU		= DATA.getBigDecimal("ACCRU");
				BigDecimal ACUM_ACCRU	= DATA.getBigDecimal("ACUM_ACCRU");
				BigDecimal INTRATE		= DATA.getBigDecimal("INTRATE");
				BigDecimal SBE_ANNUAL 	= DATA.getBigDecimal("SBE_ANNUAL");
				Date LNSTRDT	 		= DATA.getDate("LNSTRDT");
				Date LNDUEDT	 		= DATA.getDate("LNDUEDT");
				Integer LNPERIOD 		= DATA.getInt("LNPERIOD");
				String STATUS 			= DATA.getString("STATUS");
				String JNSBUNGA 		= DATA.getString("JNSBUNGA");
				
				XSSFRow row = sheet.createRow(rowNum++);

				row.createCell(0).setCellValue(PRODUK);
				row.createCell(1).setCellValue(CABANG);
				row.createCell(2).setCellValue(ACCNBR);
				row.createCell(3).setCellValue(PLAFOND.doubleValue());
				row.createCell(4).setCellValue(SALDO_AKHIR.doubleValue());
				row.createCell(5).setCellValue(MODIFIKASI.doubleValue());
				row.createCell(6).setCellValue(AMOREIR.doubleValue());
				row.createCell(7).setCellValue(NILAI_WAJAR.doubleValue());
				row.createCell(8).setCellValue(ECL1.doubleValue());
				row.createCell(9).setCellValue(IMPAIR_ASET.doubleValue());
				row.createCell(10).setCellValue(ECL2.doubleValue());
				row.createCell(11).setCellValue(WDRSPARE.doubleValue());
				row.createCell(12).setCellValue(DPD);
				row.createCell(13).setCellValue(RATING);
				row.createCell(14).setCellValue(BASENMNL.doubleValue());
				row.createCell(15).setCellValue(INTNMNL.doubleValue());
				row.createCell(16).setCellValue(PENALTY.doubleValue());
				row.createCell(17).setCellValue(TUNGPKK.doubleValue());
				row.createCell(18).setCellValue(TUNGGBNG.doubleValue());
				row.createCell(19).setCellValue(ATRIBUSI.doubleValue());
				row.createCell(20).setCellValue(NILAI_PEROLEHAN.doubleValue());
				row.createCell(21).setCellValue(ACCRU.doubleValue());
				row.createCell(22).setCellValue(ACUM_ACCRU.doubleValue());
				row.createCell(23).setCellValue(INTRATE.doubleValue());
				row.createCell(24).setCellValue(SBE_ANNUAL.doubleValue());
				XSSFCell dateOf = row.createCell(25);
				dateOf.setCellValue(LNSTRDT);
				dateOf.setCellStyle(dateCellStyle);
				dateOf = row.createCell(26);
				dateOf.setCellValue(LNDUEDT);
				dateOf.setCellStyle(dateCellStyle);
				row.createCell(27).setCellValue(LNPERIOD);
				row.createCell(28).setCellValue(STATUS);
				row.createCell(29).setCellValue(JNSBUNGA);
			}
		}
        
		// Resize all columns to fit the content size
        for(int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }
        
        // Write the output to a file
        File file=new File(Sessions.getCurrent().getWebApp().getRealPath("/page/report/"),sheet.getSheetName()+".xlsx");
        FileOutputStream fileOut;
		try {
			fileOut = new FileOutputStream(file);
			workbook.write(fileOut);
			fileOut.close();
			// Closing the workbook
			workbook.close();
			
			Filedownload.save(file, null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
