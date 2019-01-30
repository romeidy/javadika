package id.co.collega.ifrs.master.report;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
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
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRImageLoader;

@org.springframework.stereotype.Component
@Scope("desktop")
public class WndLaporanRincianCOA extends SelectorComposer<Component>{

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
	@Wire Combobox cmbCabang;
	@Wire Textbox txtNoRek;
	@Wire Checkbox chkNoRek;
	@Wire Decimalbox decTotSaldoAwal;
	@Wire Decimalbox decTotMutDb;
	@Wire Decimalbox decTotMutCr;
	@Wire Decimalbox decTotSaldoAkhir;
	
	@Wire Listbox listData;
	
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
	private List<DTOMap> listDataRincianCOA;
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
		
		loadDataCabang();
		
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
		cmbJnsData.setSelectedIndex(0);
		doReset();
		DTOMap map = (DTOMap) GlobalVariable.getInstance().get("syshost");
		openDate=map.getDate("OPEN_DATE");
		txtTgl.setValue(openDate);
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
	
	private void loadDataCabang() {
		List<DTOMap> listCabang=masterService.getDataMaster(" SELECT DISTINCT KD_CAB,NM_CAB "
				+ "												FROM CFG_CABANG ORDER BY KD_CAB  ",new Object[]{});
		cmbCabang.getItems().clear();
		if (listCabang.size() > 0) {
			Comboitem ciCab=new Comboitem();
			ciCab.setLabel("All");
			ciCab.setValue("All");
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

	@SuppressWarnings("unchecked")
	private void doFind() {
		try{
			if( isValid()){
				BigDecimal totSaldoAwal=BigDecimal.ZERO;
				BigDecimal totMutDb=BigDecimal.ZERO;
				BigDecimal totMutCr=BigDecimal.ZERO;
				BigDecimal totSaldoAkhir=BigDecimal.ZERO;
				
				listAllRow.clear();
				page=1;
				String cabang="",noRek="",version="",parmVersion="",parmJnsDataENDBAL="";
				
				String jnsData=(String) ComponentUtil.getValue(cmbJnsData);
				if (jnsData.equals("0")) {
					version 			= " AND A.VERSION='0' ";
					parmJnsDataENDBAL	= "	ROUND(COALESCE(A.ENDBAL,0),4) AS ENDBAL ";
				}else if (jnsData.equals("1")) {
					parmJnsDataENDBAL 	= " ROUND((CASE WHEN X.ENDBAL IS NOT NULL THEN COALESCE(X.ENDBAL,0) ELSE COALESCE(A.ENDBAL,0) END),4) AS ENDBAL ";
					version 			= " AND A.VERSION='0' ";
					parmVersion			= " LEFT OUTER JOIN COA_MASTER X					"
										+ "					ON 	X.VERSION = '1' 			"
										+ "						AND	X.TGL_POS = A.TGL_POS 	"
										+ "						AND X.BRANCHID = A.BRANCHID "
										+ "						AND	X.CCYID = A.CCYID 		"
										+ "						AND	X.COANBR = A.COANBR		";
				}else{
					version = " AND A.VERSION='1' ";
					parmJnsDataENDBAL	= "	COALESCE(A.ENDBAL,0) AS ENDBAL ";
				}
				
				if (ComponentUtil.getValue(cmbCabang)!=null && !((String) ComponentUtil.getValue(cmbCabang)).equals("All")) {
					cabang = " AND A.BRANCHID='"+((String) ComponentUtil.getValue(cmbCabang))+"' ";
				}
				
				if (ComponentUtil.getValue(txtNoRek)!=null && !chkNoRek.isChecked()) {
					noRek = " AND A.COANBR='"+((String) ComponentUtil.getValue(txtNoRek))+"' ";
				}
				
				Date tglPeriode=(Date)ComponentUtil.getValue(txtTgl);
				
				String sql= "	"
						+ "	SELECT 	A.BRANCHID,A.COANBR,A.CCYID,										"
						+ "			ROUND(COALESCE(A.STRTDTBAL,0),4) AS STRTDTBAL, 								"
						+ "			COALESCE(A.DBMUT,0) AS DBMUT,										"
						+ "			COALESCE(A.CRMUT,0) AS CRMUT,										"
						+ "			X1.PARMNM AS COANM,X1.SANDI,										"+parmJnsDataENDBAL
						+ "	FROM COA_MASTER A "+parmVersion
						+ " 			,( SELECT PARMIDOTH AS COANBR,PARMNM,X3.SANDI					"
						+ "		    		FROM CFG_PARM X4,											"
						+ "		    		( SELECT X2.SANDI											"
						+ "					  	FROM ( SELECT PARMID									"
						+ "		     	    		   	FROM CFG_PARM									"
						+ "		        	       		WHERE PARMGRP = 12 ) X1,	"
						+ "		     	   	            ( SELECT PARMID AS SANDI, PARMIDOTH				"
						+ "		                           FROM CFG_PARM								"
						+ "		                           WHERE PARMGRP = 13 ) X2						"
						+ "		                WHERE X1.PARMID = X2.PARMIDOTH							"
						+ "		      		) X3														"
						+ "		      		WHERE X4.PARMGRP = 6 AND X4.VIEWORD = CAST(X3.SANDI AS INT)	"
						+ " 		  	)X1																"
						+ "	WHERE A.TGL_POS='"+sdf.format(tglPeriode)+"' " + cabang + noRek + version
						+ "			AND A.COANBR=X1.COANBR	"
						+ "	ORDER BY 2,4,3 ";
				
				
				listDataRincianCOA = new ArrayList<DTOMap>();
				System.out.println("SQL ="+sql);
				listDataRincianCOA = masterService.getDataMaster(sql,new Object[]{});
				if (listDataRincianCOA != null && listDataRincianCOA.size()>0) {
					for (int i = 0; i < listDataRincianCOA.size(); i++) {
						DTOMap map = listDataRincianCOA.get(i);
						Listitem item = new Listitem();
						item.setAttribute("DATA", map);
						item.appendChild(new Listcell(String.valueOf(i+1)));
						item.appendChild(new Listcell(map.getString("BRANCHID")));
						item.appendChild(new Listcell(map.getString("COANBR")+" - "+map.getString("COANM")));
						item.appendChild(new Listcell(map.getString("CCYID")));
						item.appendChild(new Listcell(map.getString("SANDI")));
						item.appendChild(new Listcell(FunctionUtils.moneyToText(map.getBigDecimal("STRTDTBAL"))));
						item.appendChild(new Listcell(FunctionUtils.moneyToText(map.getBigDecimal("DBMUT"))));
						item.appendChild(new Listcell(FunctionUtils.moneyToText(map.getBigDecimal("CRMUT"))));
						item.appendChild(new Listcell(FunctionUtils.moneyToText(map.getBigDecimal("ENDBAL"))));
						if (!map.getString("SANDI").equals("465")) {
							totSaldoAwal  = totSaldoAwal .add(map.getBigDecimal("STRTDTBAL"));
							totMutDb	  = totMutDb	 .add(map.getBigDecimal("DBMUT"));
							totMutCr	  = totMutCr	 .add(map.getBigDecimal("CRMUT"));
							totSaldoAkhir = totSaldoAkhir.add(map.getBigDecimal("ENDBAL"));
						}
						listAllRow.add(item);
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
					
					decTotSaldoAwal.setValue(totSaldoAwal);
					decTotMutDb.setValue(totMutDb);
					decTotMutCr.setValue(totMutCr);
					decTotSaldoAkhir.setValue(totSaldoAkhir);
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
			if (ComponentUtil.getValue(cmbCabang) == null) {
				MessageBox.showInformation("Cabang Harus diisi.");
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
		
		if (listDataRincianCOA == null || listDataRincianCOA.size() <= 0) {
			MessageBox.showInformation("Tidak ada data yang akan dicetak");
			return;
		}
		String type = (String) ComponentUtil.getValue(cmbFormat);

		if (type.equals("pdf")) {
			String fileName="/jasper/LaporanRincianCOA.jasper";

			String realpath = Executions.getCurrent().getDesktop()
					.getWebApp().getRealPath(fileName);
			System.out.println(realpath);

			DTOMap cfgsys=(DTOMap) GlobalVariable.getInstance().get("syshost");
			DTOMap branchMap=(DTOMap) GlobalVariable.getInstance().get("branchMap");

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
			param.put("repId","jv-rpt-0014");
			JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(listDataRincianCOA);
			new JRreportWindow(this.getSelf(), true, param, fileName, ds,type,null);
			doReset();
		} else {
			doExport();
		}
	}
	
	public void doExport() {
		// Create a Workbook
        XSSFWorkbook workbook = new XSSFWorkbook(); // (XSSFWorkbook() for generating `.xls` file) (XSSFWorkbook for generating `.xlsx` file)

        /* CreationHelper helps us create instances of various things like DataFormat, 
           Hyperlink, RichTextString etc, in a format (XSSF, XSSF) independent way */
        XSSFCreationHelper createHelper = workbook.getCreationHelper();

        // Create a Sheet
        XSSFSheet sheet = workbook.createSheet("Laporan Rincian COA");
        
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
        
        String[] columns = {"No", "Cabang", "No. COA","Nama COA", "Valuta","Sandi", "Saldo Awal","Mut. DB","Mut. KR","Saldo Akhir"};
        
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
        
        XSSFCellStyle cs = workbook.createCellStyle();
//        XSSFDataFormat df = workbook.createDataFormat();
        cs.setDataFormat(createHelper.createDataFormat().getFormat("_( #,##0.00_);_( (#,##0.00);_( \"-\"??_);_(@_)"));
        
        // Create Other rows and cells with employees data
        int rowNum = 4;
        String sa_string;
        String sa_sub;
        String sa_subnow;
        String sawal_string;
        String sawal_sub;
        String sawal_subnow;
        
        BigDecimal totSaldoAwal=BigDecimal.ZERO;
		BigDecimal totMutDb=BigDecimal.ZERO;
		BigDecimal totMutCr=BigDecimal.ZERO;
		BigDecimal totSaldoAkhir=BigDecimal.ZERO;
		
		XSSFCellStyle cellStyleRight = workbook.createCellStyle();
        cellStyleRight.setAlignment(CellStyle.ALIGN_RIGHT);
        cellStyleRight.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        
        if (listDataRincianCOA.size() > 0) {
			for (int i = 0; i < listDataRincianCOA.size(); i++) {
				DTOMap DATA 			= (DTOMap) listDataRincianCOA.get(i);
				Integer NO 				= (i+1);
				String CABANG 			= "'" + DATA.getString("BRANCHID");
				String NO_COA 			= "'" + DATA.getString("COANBR");
				String NM_COA 			= DATA.getString("COANM");
				String CCYID 			= DATA.getString("CCYID");
				String SANDI 			= DATA.getString("SANDI");
				BigDecimal SALDO_AWAL 	= DATA.getBigDecimal("STRTDTBAL");
				BigDecimal MUT_DB 		= DATA.getBigDecimal("DBMUT");
				BigDecimal MUT_KR 		= DATA.getBigDecimal("CRMUT");
				BigDecimal SALDO_AKHIR 	= DATA.getBigDecimal("ENDBAL");

				if (!DATA.getString("SANDI").equals("465")) {
					totSaldoAwal  = totSaldoAwal .add(DATA.getBigDecimal("STRTDTBAL"));
					totMutDb	  = totMutDb	 .add(DATA.getBigDecimal("DBMUT"));
					totMutCr	  = totMutCr	 .add(DATA.getBigDecimal("CRMUT"));
					totSaldoAkhir = totSaldoAkhir.add(DATA.getBigDecimal("ENDBAL"));
				}

				
				
				XSSFRow row = sheet.createRow(rowNum++);

				row.createCell(0).setCellValue(NO);
				row.createCell(1).setCellValue(CABANG);
				row.createCell(2).setCellValue(NO_COA);
				row.createCell(3).setCellValue(NM_COA);
				row.createCell(4).setCellValue(CCYID);
				row.createCell(5).setCellValue(SANDI);
				/*XSSFCell cell =row.createCell(5);
				cell.setCellType(Cell.CELL_TYPE_NUMERIC);
				cell.setCellValue(String.valueOf(SALDO_AWAL.doubleValue()));
				
				cell =row.createCell(6);
				cell.setCellType(Cell.CELL_TYPE_NUMERIC);
				cell.setCellValue(String.valueOf(MUT_DB.doubleValue()));
				
				cell =row.createCell(7);
				cell.setCellType(Cell.CELL_TYPE_NUMERIC);
				cell.setCellValue(String.valueOf(MUT_KR.doubleValue()));
				
				cell =row.createCell(8);
				cell.setCellType(Cell.CELL_TYPE_NUMERIC);
				cell.setCellValue(new Double(String.valueOf(SALDO_AKHIR.doubleValue())));*/
				
				
//				row.createCell(6).setCellValue(SALDO_AWAL.doubleValue());
//				row.createCell(7).setCellValue(MUT_DB.setScale(15, RoundingMode.HALF_EVEN).doubleValue());
//				row.createCell(8).setCellValue(MUT_KR.setScale(15, RoundingMode.HALF_EVEN).doubleValue());
//				row.createCell(9).setCellValue(SALDO_AKHIR.doubleValue());
				

				
//				sawal_string = SALDO_AWAL.toString();
//				if(sawal_string.length() >= 5){
//					sawal_sub = sawal_string.substring(0, sawal_string.length() - 5);
//					sawal_subnow = sawal_sub + ".0000";
//					
//				}else{
//					sawal_subnow = SALDO_AWAL.toString();
//				}
//				double sawal_final = Double.parseDouble(sawal_subnow);
//				
//				XSSFCell cell6 = row.createCell((short)6);
//				cell6.setCellValue(sawal_final);
//				cell6.setCellStyle(cs);
//				
//				XSSFCell cell7 = row.createCell((short)7);
//				cell7.setCellValue(MUT_DB.doubleValue());
//				cell7.setCellStyle(cs);
//				
//				XSSFCell cell8 = row.createCell((short)8);
//				cell8.setCellValue(MUT_KR.doubleValue());
//				cell8.setCellStyle(cs);
//				
//				sa_string = SALDO_AKHIR.toString();
//				if(sa_string.length() >= 5){
//					sa_sub = sa_string.substring(0, sa_string.length() - 5);
//					sa_subnow = sa_sub + ".0000";
//				}else{
//					sa_subnow = SALDO_AKHIR.toString();
//				}
//				double sa_final = Double.parseDouble(sa_subnow);
//				
//				XSSFCell cell9 = row.createCell((short)9);
//				cell9.setCellValue(sa_final);
//				cell9.setCellStyle(cs);
//				
//				System.out.println(sa_subnow);
				

//				row.createCell(6).setCellValue(SALDO_AWAL.toString());
//				row.createCell(7).setCellValue(MUT_DB.toString());
//				row.createCell(8).setCellValue(MUT_KR.toString());
//				row.createCell(9).setCellValue(SALDO_AKHIR.toString());
				
//				row.createCell(6).setCellValue(SALDO_AWAL.setScale(15, RoundingMode.FLOOR).doubleValue());
				
//				row.createCell(9).setCellValue(SALDO_AKHIR.setScale(15, RoundingMode.FLOOR).doubleValue());

				row.createCell(6).setCellValue(FunctionUtils.moneyToText(SALDO_AWAL));
				row.createCell(7).setCellValue(MUT_DB.setScale(15, RoundingMode.FLOOR).doubleValue());
				row.createCell(8).setCellValue(MUT_KR.setScale(15, RoundingMode.FLOOR).doubleValue());
				row.createCell(9).setCellValue(FunctionUtils.moneyToText(SALDO_AKHIR));
				
				row.getCell(6).setCellStyle(cellStyleRight);
				row.getCell(9).setCellStyle(cellStyleRight);

//				System.out.print(SALDO_AWAL.toPlainString());
//				System.out.print(MUT_DB.toPlainString());
//				System.out.print(MUT_KR.toPlainString());
//				System.out.print(SALDO_AKHIR.toPlainString());
//				
//				System.out.print(SALDO_AWAL.toString());
//				System.out.print(MUT_DB.toString());
//				System.out.print(MUT_KR.toString());
//				System.out.print(SALDO_AKHIR.toString());
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
	
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    long factor = (long) Math.pow(10, places);
	    value = value * factor;
	    long tmp = Math.round(value);
	    return (double) tmp / factor;
	}
	
	public static double roundB(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_DOWN);
	    return bd.doubleValue();
	}
}
