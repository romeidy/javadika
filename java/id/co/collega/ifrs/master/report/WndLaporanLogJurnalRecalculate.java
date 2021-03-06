package id.co.collega.ifrs.master.report;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
import id.co.collega.v7.seed.controller.SelectorComposer;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRImageLoader;

@org.springframework.stereotype.Component
@Scope("desktop")
public class WndLaporanLogJurnalRecalculate extends SelectorComposer<Component>{

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
	
	@Wire Combobox cmbCabang;
	@Wire Textbox txtNoRek;
	@Wire Checkbox chkNoRek;
	@Wire Decimalbox decTotMutasi;
	
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
	private List<DTOMap> listDataLogJurnalRecalculate;
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
	
	String aksi;
	
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
				if(chkNoRek.isChecked()){
				aksi = "Search tanggal Posisi : " + txtTgl.getValue() + ", cabang: " + 
							cmbCabang + ", COA : Semua";					
				}
				aksi = "Search tanggal Posisi : " + txtTgl.getValue() + ", cabang: " + 
						cmbCabang + ", COA :" + txtNoRek;
				doLogAktfitas(aksi);
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
	
	/*@SuppressWarnings("unchecked")
	private void getDetail(){
		if(listData.getSelectedItem()!=null){
			DTOMap map = (DTOMap) listData.getSelectedItem().getAttribute("DATA");
			Map<String, Object> mapss = new HashMap<String, Object>();
			mapss.put("data", map);
			
			DlgEngine dlg = new DlgEngine();
			
			DialogUtil.
			showPopupDialogCloseOnly("/page/dialog/WndDialogLaporanLTKT.zul", "Rincian Transaksi", getSelf(), new EventListener<Event>(){
				public void onEvent(Event e)throws Exception{
					doFind();
				}
			}, mapss);
		}
	}*/
	
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
				BigDecimal totMutasi=BigDecimal.ZERO;
				page=1;
				listAllRow.clear();
				listData.getItems().clear();
				
				String cabang="",noRek="";
				
				if (ComponentUtil.getValue(cmbCabang)!=null && !((String) ComponentUtil.getValue(cmbCabang)).equals("All")) {
					cabang = " AND A.BRANCHID='"+((String) ComponentUtil.getValue(cmbCabang))+"' ";
				}
				
				if (ComponentUtil.getValue(txtNoRek)!=null && !chkNoRek.isChecked()) {
					noRek = " AND A.ACCNBR='"+((String) ComponentUtil.getValue(txtNoRek))+"' ";
				}
				
				Date tglPeriode=(Date)ComponentUtil.getValue(txtTgl);
				listDataLogJurnalRecalculate = new ArrayList<DTOMap>();
				listDataLogJurnalRecalculate = masterService.getDataMaster("	"
							+ "			SELECT A.*, (CASE WHEN A.DBCR=0 "
							+ "							THEN 'D'"
							+ "							ELSE 'K' END) AS DBKR, B.PARMNMOTH AS ACCNBRNM "
							+ "			FROM HTX_RECALCULATED A LEFT OUTER JOIN CFG_PARM B"
							+ "										ON B.PARMGRP=6"
							+ "											AND A.ACCNBR=B.PARMIDOTH	"
							+ "			WHERE A.TGL_POS='"+sdf.format(tglPeriode)+"' " + cabang + noRek
							+ "			ORDER BY 2,8,ABS(TXAMT),9,4 ",new Object[]{});
				if (listDataLogJurnalRecalculate != null && listDataLogJurnalRecalculate.size()>0) {
					for (int i = 0; i < listDataLogJurnalRecalculate.size(); i++) {
						DTOMap map = listDataLogJurnalRecalculate.get(i);
						Listitem item = new Listitem();
						item.setAttribute("DATA", map);
						item.appendChild(new Listcell(String.valueOf(i+1)));
						item.appendChild(new Listcell(map.getString("BRANCHID")));
						item.appendChild(new Listcell(map.getString("ACCNBR")+" - "+map.getString("ACCNBRNM")));
						item.appendChild(new Listcell(map.getString("CCYID")));
						item.appendChild(new Listcell(sdf.format(map.getDate("TXDATE"))));
						item.appendChild(new Listcell(map.getString("TXID")));
						item.appendChild(new Listcell(map.getString("TXCODE")));
						item.appendChild(new Listcell(map.getString("TXMSG")));
						item.appendChild(new Listcell(FunctionUtils.moneyToText(map.getBigDecimal("TXAMT").abs())));
						item.appendChild(new Listcell(map.getInt("DBCR")==0?"D":"K"));
						item.appendChild(new Listcell(map.getString("CREATED_BY")));
						if (map.getString("VERSION").equals("0")) {
							totMutasi = totMutasi.add(map.getBigDecimal("TXAMT"));
						}
						listAllRow.add(item);
					}
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
					decTotMutasi.setValue(totMutasi);
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
			if (ComponentUtil.getValue(cmbCabang) == null) {
				MessageBox.showInformation("Cabang Harus diisi.");
				isValid=false;
			}
			if (!chkNoRek.isChecked()) {
				if (ComponentUtil.getValue(txtNoRek) == null) {
					MessageBox.showInformation("COA Harus diisi.");
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
		if (listDataLogJurnalRecalculate == null || listDataLogJurnalRecalculate.size() <= 0) {
			MessageBox.showInformation("Tidak ada data yang akan dicetak");
			return;
		}
		String type = (String) ComponentUtil.getValue(cmbFormat);

		if (type.equals("pdf")) {
			String fileName="/jasper/LaporanLogJurnalRecalculate.jasper";

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
			param.put("repId","jv-rpt-0013");
			JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(listDataLogJurnalRecalculate);
			new JRreportWindow(this.getSelf(), true, param, fileName, ds,type,null);
			doReset();
		} else {
			doExport();
		}
		if(chkNoRek.isChecked()){
			aksi = "Print tanggal Posisi : " + txtTgl.getValue() + ", cabang: " + 
						cmbCabang + ", COA : Semua";					
			}
			aksi = "Print tanggal Posisi : " + txtTgl.getValue() + ", cabang: " + 
					cmbCabang + ", COA :" + txtNoRek;
			doLogAktfitas(aksi);
	}
	
	public void doExport() {
		// Create a Workbook
		XSSFWorkbook workbook = new XSSFWorkbook(); // new HSSFWorkbook() for generating `.xls` file

        /* CreationHelper helps us create instances of various things like DataFormat, 
           Hyperlink, RichTextString etc, in a format (HSSF, XSSF) independent way */
        XSSFCreationHelper createHelper = workbook.getCreationHelper();

        // Create a Sheet
        XSSFSheet sheet = workbook.createSheet("Laporan Log Jurnal Recalculate");
        
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
        
        String[] columns = {"No", "Cabang", "No. COA", "Valuta","Tanggal","No. Arsip","Kode Tx","Keterangan","Jml. Mutasi","D/K","User"};
        
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
        if (listDataLogJurnalRecalculate.size() > 0) {
			for (int i = 0; i < listDataLogJurnalRecalculate.size(); i++) {
				DTOMap DATA 		= (DTOMap) listDataLogJurnalRecalculate.get(i);
				Integer NO 			= (i+1);
				String CABANG 		= "'" + DATA.getString("BRANCHID");
				String ACCNBR 		= "'" + DATA.getString("ACCNBR") + " - "+ DATA.getString("ACCNBRNM");
				String CCYID 		= DATA.getString("CCYID");
				Date TANGGAL 		= DATA.getDate("TXDATE");
				String NO_ARSIP 	= DATA.getString("TXID");
				String KODE_TX 		= DATA.getString("TXCODE");
				String KETERANGAN 	= DATA.getString("TXMSG");
				BigDecimal JML_TX 	= DATA.getBigDecimal("TXAMT");
				String DBKR 		= DATA.getString("DBKR");
				String USER 		= DATA.getString("CREATED_BY");

				XSSFRow row = sheet.createRow(rowNum++);

				row.createCell(0).setCellValue(NO);
				row.createCell(1).setCellValue(CABANG);
				row.createCell(2).setCellValue(ACCNBR);
				row.createCell(3).setCellValue(CCYID);
				XSSFCell dateOfBirthCell = row.createCell(4);
				dateOfBirthCell.setCellValue(TANGGAL);
				dateOfBirthCell.setCellStyle(dateCellStyle);
				row.createCell(5).setCellValue(NO_ARSIP);
				row.createCell(6).setCellValue(KODE_TX);
				row.createCell(7).setCellValue(KETERANGAN);
				row.createCell(8).setCellValue(JML_TX.doubleValue());
				row.createCell(9).setCellValue(DBKR);
				row.createCell(10).setCellValue(USER);
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
