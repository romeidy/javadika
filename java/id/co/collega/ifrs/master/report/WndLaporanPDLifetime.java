package id.co.collega.ifrs.master.report;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

import com.jet.gand.services.GlobalVariable;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import id.co.collega.ifrs.common.DTOMap;
import id.co.collega.ifrs.common.JRreportWindow;
import id.co.collega.ifrs.common.JdbcTemplate;
import id.co.collega.ifrs.master.service.MasterServices;
import id.co.collega.ifrs.util.ComponentUtil;
import id.co.collega.ifrs.util.MessageBox;
import id.co.collega.v7.ef.common.DataSession;
import id.co.collega.v7.seed.controller.SelectorComposer;
import id.co.collega.v7.seed.config.AuthenticationService;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.util.JRImageLoader;

@org.springframework.stereotype.Component
@Scope("execution")
public class WndLaporanPDLifetime  extends SelectorComposer<Component>{

	@Autowired AuthenticationService auth;
	@Autowired Environment env;
	
	public DataSession dataSession;
	@Autowired JdbcTemplate jt;
	@Wire Window WndLaporanLTKT;
//	@Wire Row rowParameter;
	@Wire Datebox txtTgl;
	@Wire Button btnCari;
	@Wire Button btnReset;
	@Wire Button btnCetak;
	@Wire DTOMap workflow;
	@Wire Combobox cmbFormat;
	
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
	private List<DTOMap> listDataPDLifetime;
	private List<DTOMap> listda;
	private Date openDate;
	SimpleDateFormat sdf=new SimpleDateFormat("yyyyMM");

	String aksi;
	@Override
	public void doAfterCompose(Component comp) throws Exception  {
		super.doAfterCompose(comp);
		
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
		
//		listData.addEventListener(Events.ON_DOUBLE_CLICK,new EventListener<Event>(){
//			public void onEvent(Event e)throws Exception{
//				getDetail();
//			}
//		});
		
//		workflow = new DTOMap();
//		workflow = (DTOMap) masterService.getMapMaster("SELECT * FROM REF_WORKFLOW WHERE KD_CAB = ? AND ROLEID = ?", 
//				new Object[]{ auth.getUserDetails().getBranchId(), auth.getUserDetails().getActiveRole()});
//		
		doReset();
		DTOMap map = (DTOMap) GlobalVariable.getInstance().get("syshost");
		openDate=map.getDate("OPEN_DATE");
		txtTgl.setValue(openDate);
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
	
	@SuppressWarnings("unchecked")
	private void doFind() {
		try{
			if( isValid()){
				listData.getItems().clear();
				
				Date tglPeriode=(Date)ComponentUtil.getValue(txtTgl);
				listDataPDLifetime = new ArrayList<DTOMap>();
				listDataPDLifetime =  masterService.getDataMaster("	"
							+ "			SELECT *,(SELECT PARMNM "
							+ "						FROM CFG_PARM "
							+ "						WHERE PARMGRP=1"
							+ "							AND PARMID=REF_PD_LIFETIME.PRODID )AS PRODNM"
							+ "			FROM REF_PD_LIFETIME"
							+ "			WHERE REF_PD_LIFETIME.PERIODE=? ORDER BY REF_PD_LIFETIME.PRODID,REF_PD_LIFETIME.RATING",new Object[]{Integer.valueOf(sdf.format(tglPeriode))});
				if (listDataPDLifetime != null && listDataPDLifetime.size()>0) {
					String produk="";
					for (int i = 0; i < listDataPDLifetime.size(); i++) {
						DTOMap map = listDataPDLifetime.get(i);
						Listitem item = new Listitem();
						item.setAttribute("DATA", map);

						if (!produk.equals(map.getString("PRODID"))){
							Listcell a=new Listcell();
							a.setSpan(6);
							a.setValue(map.getString("PRODID"));
							a.setLabel(map.getString("PRODID")+" - "+map.getString("PRODNM"));
							produk = map.getString("PRODID");
							item.appendChild(a);
							i=i-1;
						}else {
							item.appendChild(new Listcell(map.getInt("RATING").toString()));
							for (int j = 1; j <= 20; j++) {
								if (map.getBigDecimal("PD_"+String.valueOf(j)).doubleValue() > 0) {
									item.appendChild(new Listcell(String.valueOf(map.getBigDecimal("PD_"+String.valueOf(j)).setScale(8, RoundingMode.HALF_UP))));
								} else {
									item.appendChild(new Listcell("0.00000000"));
								}
							}
						}
						listData.appendChild(item);
					}

				}else{
					MessageBox.showInformation("Data tidak ditemukan");
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
			MessageBox.showError(e.getMessage());
		}
		aksi = "Search laporan periode : " + txtTgl.getValue();
		doLogAktfitas(aksi);
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
		ComponentUtil.setValue(txtTgl, openDate);
		System.out.println("open date ::: "+openDate);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void doPrint() throws SQLException{
		Date date = new Date();
		List<DTOMap> listMap = listDataPDLifetime;
		
		if (listDataPDLifetime == null || listDataPDLifetime.size() <= 0) {
			MessageBox.showInformation("Tidak ada data yang akan dicetak");
			return;
		}
		String type = (String) ComponentUtil.getValue(cmbFormat);
		if (type.equals("pdf")) {
			String fileName="/jasper/LaporanPDLifetime.jasper";
			
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
			param.put("namaBank",cfgsys.getString("NAMA_BANK"));
			param.put("cabang",branchMap.getString("KD_CAB"));
			param.put("namaCabang",branchMap.getString("NM_CAB"));
			param.put("printedBy",auth.getUserDetails().getUserId());
			param.put("repId","jv-rpt-0044");
			//			JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(listMap);
			new JRreportWindow(this.getSelf(), true, param, fileName, null,type,conn);
			conn.close();
			doReset();
		} else {
			doExport();
		}
		aksi = "Print laporan periode : " + txtTgl.getValue();
		doLogAktfitas(aksi);
	}
	
	public void doExport() {
		// Create a Workbook
		XSSFWorkbook workbook = new XSSFWorkbook(); // new HSSFWorkbook() for generating `.xls` file XSSFWorkbook = `.xlsx`

        /* CreationHelper helps us create instances of various things like DataFormat, 
           Hyperlink, RichTextString etc, in a format (HSSF, XSSF) independent way */
        XSSFCreationHelper createHelper = workbook.getCreationHelper();

        // Create a Sheet
        XSSFSheet sheet = workbook.createSheet("Laporan PD Lifetime");
        

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
        
        String[] columns = {"Rating", 	"PD 1 (%)", "PD 2 (%)", "PD 3 (%)","PD 4 (%)","PD 5 (%)",
        								"PD 6 (%)", "PD 7 (%)", "PD 8 (%)","PD 9 (%)","PD 10 (%)",
        								"PD 11 (%)", "PD 12 (%)", "PD 13 (%)","PD 14 (%)","PD 15 (%)",
        								"PD 16 (%)", "PD 17 (%)", "PD 18 (%)","PD 19 (%)","PD 20 (%)"};
        
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
        if (listDataPDLifetime.size() > 0) {
        	String produk="";
			for (int i = 0; i < listDataPDLifetime.size(); i++) {
				DTOMap DATA = (DTOMap) listDataPDLifetime.get(i);
				String 	PRODUK 	= "'" + DATA.getString("PRODID");
				Integer RATING	=	DATA.getInt("RATING");
				XSSFRow row = sheet.createRow(rowNum++);
				if (!produk.equals(PRODUK)) {
					produk=PRODUK;
					
					// Merge for Produk
			        sheet.addMergedRegion(new CellRangeAddress(rowNum-1,rowNum-1,0,columns.length-1));
			        
					row.createCell(0).setCellValue(PRODUK+" - "+DATA.getString("PRODNM"));
					i=i-1;
				} else {
					row.createCell(0).setCellValue(RATING);
					for (int j = 1; j <= 20; j++) {
						if (DATA.getBigDecimal("PD_"+String.valueOf(j)).doubleValue() > 0) {
							row.createCell(j).setCellValue(DATA.getBigDecimal("PD_"+String.valueOf(j)).doubleValue());
						} else {
							row.createCell(j).setCellValue(new BigDecimal("0000000000").doubleValue());
						}
					}
				}
				if (RATING==8) {
					rowNum++;
				}
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
