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

import org.apache.poi.ss.usermodel.IndexedColors;
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
public class WndLaporanECLLonggarTarik extends SelectorComposer<Component>{

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
	
	@Wire Combobox cmbProduk;
	@Wire Combobox cmbCabang;
	@Wire Textbox txtNoRek;
	@Wire Checkbox chkNoRek;
	@Wire Decimalbox decTotECL;
	
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
	private List<DTOMap> listDataECL;
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
		
		loadDataProduk();
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

	private void loadDataProduk() {
		List<DTOMap> listProduk=masterService.getDataMaster(" SELECT PARMID,PARMNM "
				+ "												FROM CFG_PARM "
				+ "												WHERE PARMGRP=1 "
				+ "													AND PARMIDOTH='1' ORDER BY PARMID  ",new Object[]{});
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
			if( isValid()){
				BigDecimal totECL=BigDecimal.ZERO;
				listData.getItems().clear();
				listAllRow.clear();
				page=1;
				String produk="",cabang="",noRek="";
				
				if (ComponentUtil.getValue(cmbProduk)!=null && !((String) ComponentUtil.getValue(cmbProduk)).equals("All")) {
					produk = " AND A.PRODID='"+((String) ComponentUtil.getValue(cmbProduk))+"' ";
				}
				
				if (ComponentUtil.getValue(cmbCabang)!=null && !((String) ComponentUtil.getValue(cmbCabang)).equals("All")) {
					cabang = " AND B.BRANCHID='"+((String) ComponentUtil.getValue(cmbCabang))+"' ";
				}
				
				if (ComponentUtil.getValue(txtNoRek)!=null && !chkNoRek.isChecked()) {
					noRek = " AND B.ACCNBR='"+((String) ComponentUtil.getValue(txtNoRek))+"' ";
				}
				
				Date tglPeriode=(Date)ComponentUtil.getValue(txtTgl);
				listDataECL = new ArrayList<DTOMap>();
				listDataECL = masterService.getDataMaster("	"
							+ "			SELECT B.* "
							+ "			FROM LOAN_MASTER A,"
							+ "			     ECL_LT_OFF B"
							+ "			WHERE A.ACCNBR = B.ACCNBR "
							+ "				AND A.TGL_POS=B.TGL_POS "
							+ "				AND A.ACCSTS NOT IN (0,6,7,8,9)"
							+ "				AND A.TGL_POS='"+sdf.format(tglPeriode)+"' " + produk + cabang + noRek
							+ "			ORDER BY 3,4,2 ",new Object[]{});
				if (listDataECL != null && listDataECL.size()>0) {
					for (int i = 0; i < listDataECL.size(); i++) {
						DTOMap map = listDataECL.get(i);
						Listitem item = new Listitem();
						item.setAttribute("DATA", map);
						item.appendChild(new Listcell(map.getInt("ECL_SEQ").toString()));
						item.appendChild(new Listcell(map.getString("BRANCHID")));
						item.appendChild(new Listcell(map.getString("ACCNBR")));
						item.appendChild(new Listcell(sdf.format(map.getDate("TGL_ANGSUR"))));
						item.appendChild(new Listcell(FunctionUtils.moneyToText(map.getBigDecimal("PD"))));
						item.appendChild(new Listcell(FunctionUtils.moneyToText(map.getBigDecimal("LGD"))));
						item.appendChild(new Listcell(FunctionUtils.moneyToText(map.getBigDecimal("EAD"))));
						item.appendChild(new Listcell(FunctionUtils.moneyToText(map.getBigDecimal("DF"))));
						item.appendChild(new Listcell(FunctionUtils.moneyToText(map.getBigDecimal("ECL"))));
						totECL = totECL.add(map.getBigDecimal("ECL"));
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
					
					decTotECL.setValue(totECL);
				}else{
					MessageBox.showInformation("Data tidak ditemukan");
				}
			}
			if (chkNoRek.isChecked()) {
				aksi = "Search posisi : " + txtTgl.getValue() + ", produk : " + 
						cmbProduk + ", Semua Norek";
			} else {
				aksi = "Search posisi : " + txtTgl.getValue() + ", produk : " + 
						cmbProduk + ", Norek : " + txtNoRek;
			}
			doLogAktfitas(aksi);
			
		}catch (Exception e) {
			e.printStackTrace();
			MessageBox.showError(e.getMessage());
		}
	}

	public boolean isValid(){
		if (ComponentUtil.getValue(txtTgl) == null) {
			MessageBox.showInformation("Periode Harus diisi.");
			return false;
		}
		return true;
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
		List<DTOMap> listMap = listDataECL;
		
		if (listDataECL == null || listDataECL.size() <= 0) {
			MessageBox.showInformation("Tidak ada data yang akan dicetak");
			return;
		}
		String type = (String) ComponentUtil.getValue(cmbFormat);

		if (type.equals("pdf")) {
			String fileName="/jasper/LaporanECLLonggarTarik.jasper";

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
			JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(listDataECL);
			new JRreportWindow(this.getSelf(), true, param, fileName, ds,type,conn);
			doReset();
			conn.close();
		} else {
			doExport();
		}
		if (chkNoRek.isChecked()) {
			aksi = "Print posisi: " + txtTgl.getValue() + ", produk : " + 
					cmbProduk + ", Semua Norek";
		} else {
			aksi = "Print posisi : " + txtTgl.getValue() + ", produk : " + 
					cmbProduk + ", Norek:" + txtNoRek;
		}
		doLogAktfitas(aksi);
	}
	
	public void doExport() {
		// Create a Workbook
		XSSFWorkbook workbook = new XSSFWorkbook(); // new HSSFWorkbook() for generating `.xls` file

        /* CreationHelper helps us create instances of various things like DataFormat, 
           Hyperlink, RichTextString etc, in a format (HSSF, XSSF) independent way */
        XSSFCreationHelper createHelper = workbook.getCreationHelper();

        // Create a Sheet
        XSSFSheet sheet = workbook.createSheet("Laporan ECL PD LGD");
        
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
        
        String[] columns = {"Periode", "Cabang", "No. Rekening", "Tgl. Angsur","PD (%)","LGD (%)","EAD","DF","ECL"};
        
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
        if (listDataECL.size() > 0) {
			for (int i = 0; i < listDataECL.size(); i++) {
				DTOMap DATA = (DTOMap) listDataECL.get(i);
				Integer PERIODE = DATA.getInt("ECL_SEQ");
				String CABANG = "'" + DATA.getString("BRANCHID");
				String ACCNBR = "'" + DATA.getString("ACCNBR");
				Date TGL_ANGSUR =DATA.getDate("TGL_ANGSUR");
				BigDecimal PD = DATA.getBigDecimal("PD");
				BigDecimal LGD = DATA.getBigDecimal("LGD");
				BigDecimal EAD = DATA.getBigDecimal("EAD");
				BigDecimal DF = DATA.getBigDecimal("DF");
				BigDecimal ECL = DATA.getBigDecimal("ECL");

				XSSFRow row = sheet.createRow(rowNum++);

				row.createCell(0).setCellValue(PERIODE);
				row.createCell(1).setCellValue(CABANG);
				row.createCell(2).setCellValue(ACCNBR);
				XSSFCell dateOfBirthCell = row.createCell(3);
				dateOfBirthCell.setCellValue(TGL_ANGSUR);
				dateOfBirthCell.setCellStyle(dateCellStyle);
				row.createCell(4).setCellValue(PD.doubleValue());
				row.createCell(5).setCellValue(LGD.doubleValue());
				row.createCell(6).setCellValue(EAD.doubleValue());
				row.createCell(7).setCellValue(DF.doubleValue());
				row.createCell(8).setCellValue(ECL.doubleValue());
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
