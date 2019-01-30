package id.co.collega.ifrs.master.report;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.ss.util.cellwalk.CellHandler;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFCreationHelper;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.validator.constraints.Length;
import org.jfree.ui.Align;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.zkoss.zhtml.Sub;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Row;
import org.zkoss.zul.Window;

import com.jet.gand.services.GlobalVariable;
import com.jet.gand.utils.CopyObject;
import com.lowagie.text.pdf.codec.Base64;

import id.co.collega.ifrs.common.DTOMap;
import id.co.collega.ifrs.common.FunctionUtils;
import id.co.collega.ifrs.common.JRreportWindow;
import id.co.collega.ifrs.common.JdbcTemplate;
import id.co.collega.ifrs.master.report.dto.dtoNeraca;
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
public class WndLaporanNeracaKonsolidasi  extends SelectorComposer<Component>{

	@Autowired JdbcTemplate jt;
	@Autowired AuthenticationService auth;
	@Autowired Environment env;
	public DataSession dataSession;
	@Wire Window WndLaporan;
	@Wire Row rowParameter;
	@Wire Row rowParameter3;
	@Wire Row rowParameter4;
	@Wire Datebox txtTgl;
	@Wire Button btnCari;
	@Wire Button btnReset;
	@Wire Button btnCetak;
	@Wire DTOMap workflow;
	@Wire Combobox cmbFormat;
	@Wire Combobox cmbJnsData;
	@Wire Combobox cmbJnsLaporan;
	@Wire Combobox cmbCabang;
	@Wire Combobox cmbCabangKonsol;
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
	
	private List<dtoNeraca> listDataNeraca;
	
	private Date openDate;
	SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
	
	@Override
	public void doAfterCompose(Component comp) throws Exception  {
		super.doAfterCompose(comp);
		cmbJnsData.setSelectedIndex(0);
		cmbJnsLaporan.setSelectedIndex(0);
		
		btnCetak.addEventListener(Events.ON_CLICK,new EventListener<Event>(){
			public void onEvent(Event e)throws Exception{
				if (ComponentUtil.getValue(cmbFormat) == null
						|| ComponentUtil.getValue(cmbFormat).equals("")) {
					throw new WrongValueException(cmbFormat,
							"Format file tidak boleh kosong.");	
				} else {
					doPrint();					
				}
			}
		});
		
		btnCari.addEventListener(Events.ON_CLICK,new EventListener<Event>(){
			public void onEvent(Event e)throws Exception{
				doFind();
//				txtTgl.setDisabled(true);
			}
		});
		
		cmbJnsLaporan.addEventListener(Events.ON_SELECT,new EventListener<Event>(){
			public void onEvent(Event e)throws Exception{
				String jnsLaporan=(String)ComponentUtil.getValue(cmbJnsLaporan);
				if (jnsLaporan!=null) {
					if (jnsLaporan.equals("cabang")) {
						doLoadCabang();
						rowParameter3.setVisible(true);
						rowParameter4.setVisible(false);
						cmbCabangKonsol.setSelectedIndex(-1);
					} else {
						doLoadCabangKonsol();
						rowParameter4.setVisible(true);
						rowParameter3.setVisible(false);
						cmbCabang.setSelectedIndex(-1);
					}
				}
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
		doLoadCabang();
		doReset();
		DTOMap map = (DTOMap) GlobalVariable.getInstance().get("syshost");
		openDate=map.getDate("OPEN_DATE");
		txtTgl.setValue(openDate);
	}
	
	private void doLoadCabangKonsol() {
		cmbCabangKonsol.getItems().clear();
		List<DTOMap> listData = (List<DTOMap>) masterService.getDataMaster(
				"SELECT KD_CAB,NM_CAB FROM CFG_CABANG "
				+ "	WHERE KD_CAB IN (SELECT DISTINCT KD_CAB_KONSOL "
				+ "						FROM CFG_CABANG "
				+ "						WHERE TGL_POS IN (SELECT MAX(TGL_POS) FROM CFG_CABANG))"
				+ "		AND TGL_POS IN (SELECT MAX(TGL_POS) FROM CFG_CABANG)	"
				+ "		ORDER BY 1",
				null);
		Comboitem item = new Comboitem();
		item.setLabel("All");
		item.setValue("All");
		cmbCabangKonsol.appendChild(item);
		for (DTOMap map : listData) {
			item = new Comboitem();
			item.setLabel(map.getString("KD_CAB")+" - "+map.getString("NM_CAB"));
			item.setValue(map.getString("KD_CAB"));
			item.setAttribute("NM_CAB", map.getString("NM_CAB"));
			cmbCabangKonsol.appendChild(item);
		}
		cmbCabangKonsol.setSelectedIndex(0);
	}
	
	private void doLoadCabang() {
		cmbCabang.getItems().clear();
		List<DTOMap> listData = (List<DTOMap>) masterService.getDataMaster(
				"SELECT KD_CAB,NM_CAB FROM CFG_CABANG "
				+ "	WHERE "
				+ "		TGL_POS IN (SELECT MAX(TGL_POS) FROM CFG_CABANG) ORDER BY 1",
				null);
		Comboitem item = new Comboitem();
		for (DTOMap map : listData) {
			item = new Comboitem();
			item.setLabel(map.getString("KD_CAB")+" - "+map.getString("NM_CAB"));
			item.setValue(map.getString("KD_CAB"));
			item.setAttribute("NM_CAB", map.getString("NM_CAB"));
			cmbCabang.appendChild(item);
		}
		cmbCabang.setSelectedIndex(0);
	}
	
	@SuppressWarnings("unchecked")
	private void doFind() {
		try{
			listData.getItems().clear();
			if( isValid()){
				String parmCABANG="",parmJnsDataENDBAL="",parmWhereVERSION="";
				
				String jenisLaporan=(String)ComponentUtil.getValue(cmbJnsLaporan);
				if (jenisLaporan.equals("cabang")) {
					String cabang=(String)ComponentUtil.getValue(cmbCabang);
					parmCABANG = " AND F.KD_CAB = '"+cabang+"' ";
				}else{
					String cabangKonsol =(String)ComponentUtil.getValue(cmbCabangKonsol);
					if (!cabangKonsol.equals("All")) {
						List<DTOMap> listCabang=new ArrayList<>();
						listCabang = jt.query(" SELECT KD_CAB "
								+ "				FROM CFG_CABANG"
								+ "				WHERE KD_CAB_KONSOL= '"+cabangKonsol+"' ",new DTOMap());	
						if (listCabang.size() > 0) {
							cabangKonsol ="";
							for (int i = 0; i < listCabang.size(); i++) {
								DTOMap dtoMap=listCabang.get(i);
								if ((i+1)!=listCabang.size()) {
									cabangKonsol = cabangKonsol+"'"+dtoMap.getString("KD_CAB")+"',";
								}else{
									cabangKonsol = cabangKonsol+"'"+dtoMap.getString("KD_CAB")+"'";
								}
								
							}
							parmCABANG = " AND F.KD_CAB IN ("+cabangKonsol+")";
						}
					}
				}
				
				String jenisData=(String)ComponentUtil.getValue(cmbJnsData);
				
				if (jenisData.equals("0")) {
					parmJnsDataENDBAL = " COALESCE(A.ENDBAL,0)  ";
				} else {
					parmJnsDataENDBAL 	= " (CASE WHEN X.ENDBAL IS NOT NULL THEN COALESCE(X.ENDBAL,0) ELSE COALESCE(A.ENDBAL,0) END) ";
					parmWhereVERSION	= " LEFT OUTER JOIN COA_MASTER X	"
							+ "					ON X.VERSION = '1' 			"
							+ "					AND	X.TGL_POS = A.TGL_POS 	"
							+ "					AND X.BRANCHID = A.BRANCHID "
							+ "					AND	X.CCYID = A.CCYID 		"
							+ "					AND	X.COANBR = A.COANBR		";
				}
				
				SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
				List<DTOMap>  listDataMap = new ArrayList<DTOMap>();
				listDataMap =  jt.query("	"
							+ "			SELECT 	ACTUAL_DATE,					"
							+ "					GL_ACCOUNT_TYPE1_NAME,		"
							+ "					GL_ACCOUNT_TYPE1_CODE,		"
							+ "					GL_ACCOUNT_TYPE3_NAME,		"
							+ "					GL_ACCOUNT_TYPE3_CODE,		"
							+ "					GL_ACCOUNT_TYPE4_CODE,		"
							+ "					GL_ACCOUNT_TYPE4_NAME,		"
							+ "					VALLAS,		"
							+ "					RUPIAH,		"
							+ "					JUMLAH		 "
							+ "			FROM (									"
							+ "				SELECT								"
							+ "						ACTUAL_DATE,				"
							+ "						GL_ACCOUNT_TYPE1_NAME,		"
							+ "						GL_ACCOUNT_TYPE1_CODE,		"
							+ "						GL_ACCOUNT_TYPE3_NAME,		"
							+ "						GL_ACCOUNT_TYPE3_CODE,		"
							+ "						GL_ACCOUNT_TYPE4_CODE,		"
							+ "						GL_ACCOUNT_TYPE4_NAME,		"
							+ "						SUM(VALLAS) AS VALLAS,		"
							+ "						SUM(RUPIAH) AS  RUPIAH,		"
							+ "						VALLAS+RUPIAH AS JUMLAH		"
							+ "				FROM ("
							+ "					SELECT										"
							+ "						A.TGL_POS AS ACTUAL_DATE,				"
							+ "						D.PARMNMOTH AS GL_ACCOUNT_TYPE1_NAME,	"
							+ "						D.PARMIDOTH AS GL_ACCOUNT_TYPE1_CODE,	"
							+ "						C.PARMNMOTH AS GL_ACCOUNT_TYPE3_NAME,	"
							+ "						C.PARMIDOTH AS GL_ACCOUNT_TYPE3_CODE,	"
							+ "						C.PARMID AS GL_ACCOUNT_TYPE4_CODE,		"
							+ "						C.PARMNM AS GL_ACCOUNT_TYPE4_NAME,		"
							+ "							CASE WHEN A.CCYID <>'IDR'			"
							+ "							THEN SUM(							"
							+ "									"+parmJnsDataENDBAL+"		"
							+ "											)*CAST(E.KURS AS INTEGER) "
							+ "						ELSE 0 END AS VALLAS,"
							+ "						0 RUPIAH,								"
							+ "						0 JUMLAH								"
							+ "					FROM COA_MASTER A "+parmWhereVERSION+",		"
							+ "							CFG_PARM B,							"
							+ "							CFG_PARM C ,						"
							+ "							CFG_PARM D,							"
							+ "							CFG_CCY E,							"
							+ "							CFG_CABANG F						"
							+ "					WHERE A.COANBR=B.PARMID					"
							+ "						AND F.TGL_POS=A.TGL_POS					"
							+ "						AND F.KD_CAB=A.BRANCHID					"
							+ "						AND CAST(B.VIEWORD AS VARCHAR) =C.PARMID"
							+ "						AND D.PARMID =C.PARMIDOTH				"
							+ "						AND A.CCYID=E.CCYID						"
							+ "						AND A.TGL_POS=E.TGL_POS					"
							+ "						AND B.PARMGRP=6							"
							+ "						AND D.PARMGRP=12						"
							+ "						AND C.PARMGRP=13						"
							+ "						AND A.VERSION='0'			"+parmCABANG
							+ "					GROUP BY							"
							+ "						ACTUAL_DATE,					"
							+ "						GL_ACCOUNT_TYPE4_CODE,			"
							+ "						GL_ACCOUNT_TYPE4_NAME,			"
							+ "						GL_ACCOUNT_TYPE1_NAME,			"
							+ "						GL_ACCOUNT_TYPE1_CODE,			"
							+ "						GL_ACCOUNT_TYPE3_NAME,			"
							+ "						GL_ACCOUNT_TYPE3_CODE,			"
							+ "						A.CCYID,						"
							+ "						E.KURS						"
							+ "	UNION ALL										"
							+ "					SELECT									"
							+ "						A.TGL_POS AS ACTUAL_DATE,			"
							+ "						D.PARMNMOTH AS GL_ACCOUNT_TYPE1_NAME,"
							+ "						D.PARMIDOTH AS GL_ACCOUNT_TYPE1_CODE,"
							+ "						C.PARMNMOTH AS GL_ACCOUNT_TYPE3_NAME,"
							+ "						C.PARMIDOTH AS GL_ACCOUNT_TYPE3_CODE,"
							+ "						C.PARMID AS GL_ACCOUNT_TYPE4_CODE,	"
							+ "						C.PARMNM AS GL_ACCOUNT_TYPE4_NAME,	"
							+ "						0 VALLAS,							"
							+ "						CASE WHEN A.CCYID<>'USD'		 	"
							+ "						THEN SUM(							"
							+ "								"+parmJnsDataENDBAL+"		"
							+ "										) ELSE 0 			"
							+ "						END AS RUPIAH,						"
							+ "						0 JUMLAH							"
							+ "					FROM COA_MASTER A "+parmWhereVERSION+",	"
							+ "							CFG_PARM B,						"
							+ "							CFG_PARM C,						"
							+ "							CFG_PARM D,						"
							+ "							CFG_CABANG F					"
							+ "					WHERE A.COANBR=B.PARMID					"
							+ "						AND F.TGL_POS=A.TGL_POS						"
							+ "						AND F.KD_CAB=A.BRANCHID						"
							+ "						AND CAST(B.VIEWORD AS VARCHAR) =C.PARMID	"
							+ "						AND D.PARMID =C.PARMIDOTH					"
							+ "						AND B.PARMGRP=6								"
							+ "						AND D.PARMGRP=12							"
							+ "						AND C.PARMGRP=13							"
							+ "						AND A.VERSION='0'			"+parmCABANG	
							+ "					GROUP BY						"
							+ "							ACTUAL_DATE,			"
							+ "							GL_ACCOUNT_TYPE4_CODE,	"
							+ "							GL_ACCOUNT_TYPE4_NAME,	"
							+ "							GL_ACCOUNT_TYPE1_NAME,	"
							+ "							GL_ACCOUNT_TYPE1_CODE,	"
							+ "							GL_ACCOUNT_TYPE3_NAME,	"
							+ "							GL_ACCOUNT_TYPE3_CODE,	"
							+ "							A.CCYID					"
							+ "	UNION ALL											"
							+ "				SELECT									"
							+ "					A.TGL_POS AS ACTUAL_DATE,			"
							+ "					D.PARMNMOTH AS GL_ACCOUNT_TYPE1_NAME,"
							+ "					D.PARMIDOTH AS GL_ACCOUNT_TYPE1_CODE,"
							+ "					C.PARMNMOTH AS GL_ACCOUNT_TYPE3_NAME,"
							+ "					C.PARMIDOTH AS GL_ACCOUNT_TYPE3_CODE,"
							+ "					C.PARMID AS GL_ACCOUNT_TYPE4_CODE,	"
							+ "					C.PARMNM AS GL_ACCOUNT_TYPE4_NAME,	"
							+ "					0 VALLAS,							"
							+ "					0 RUPIAH,							"
							+ "					SUM(VALLAS+RUPIAH) JUMLAH			"
							+ "				FROM COA_MASTER A,						"
							+ "						CFG_PARM B,						"
							+ "						CFG_PARM C,						"
							+ "						CFG_PARM D,						"
							+ "						CFG_CABANG F					"
							+ "				WHERE A.COANBR=B.PARMID					"
							+ "					AND F.TGL_POS=A.TGL_POS					"
							+ "					AND F.KD_CAB=A.BRANCHID					"
							+ "					AND CAST(B.VIEWORD AS VARCHAR) =C.PARMID	"
							+ "					AND D.PARMID =C.PARMIDOTH					"
							+ "					AND B.PARMGRP=6								"
							+ "					AND D.PARMGRP=12							"
							+ "					AND C.PARMGRP=13							"
							+ "					AND A.VERSION='0'				"+parmCABANG
							+ "				GROUP BY							"
							+ "						ACTUAL_DATE,				"
							+ "						GL_ACCOUNT_TYPE4_CODE,		"
							+ "						GL_ACCOUNT_TYPE4_NAME,		"
							+ "						GL_ACCOUNT_TYPE1_NAME,		"
							+ "						GL_ACCOUNT_TYPE1_CODE,		"
							+ "						GL_ACCOUNT_TYPE3_NAME,		"
							+ "						GL_ACCOUNT_TYPE3_CODE,		"
							+ "						A.CCYID						"
							+ "								)XX 	"
							+ "				WHERE GL_ACCOUNT_TYPE4_CODE NOT IN ('223','393')"
							+ "					AND LENGTH(GL_ACCOUNT_TYPE4_CODE)=3	"
							+ "				GROUP BY	ACTUAL_DATE,			"
							+ "							GL_ACCOUNT_TYPE1_NAME,	"
							+ "							GL_ACCOUNT_TYPE1_CODE,	"
							+ "							GL_ACCOUNT_TYPE3_NAME,	"
							+ "							GL_ACCOUNT_TYPE3_CODE,	"
							+ "							GL_ACCOUNT_TYPE4_CODE,	"
							+ "							GL_ACCOUNT_TYPE4_NAME	"
							+ "	UNION ALL										"
							+ "					SELECT  							"
							+ "						A.TGL_POS AS ACTUAL_DATE,		"
							+ "						'ASET' AS GL_ACCOUNT_TYPE1_NAME,"
							+ "						'1' AS GL_ACCOUNT_TYPE1_CODE,	"
							+ "						'ASET ANTAR KANTOR' AS GL_ACCOUNT_TYPE3_NAME,"
							+ "						'10016' AS GL_ACCOUNT_TYPE3_CODE,"
							+ "						CASE WHEN C.PARMID IN ('223','393')"
							+ "						THEN '223'"
							+ "						ELSE C.PARMID END AS GL_ACCOUNT_TYPE4_CODE,"
							+ "						'Melakukan kegiatan operasional di Indonesia 19)' AS GL_ACCOUNT_TYPE4_NAME,"
							+ "						0 AS VALLAS,						"
							+ "						SUM("
							+ "							"+parmJnsDataENDBAL+"			"
							+ "									) AS RUPIAH ,			"
							+ "						SUM("
							+ "							"+parmJnsDataENDBAL+"			"
							+ "									) AS JUMLAH				"
							+ "					FROM	COA_MASTER A "+parmWhereVERSION+","
							+ "							CFG_PARM B,						"
							+ "							CFG_PARM C,						"
							+ "							CFG_CABANG F					"
							+ "					WHERE									"
							+ "						A.COANBR=B.PARMID					"
							+ "						AND F.TGL_POS=A.TGL_POS				"
							+ "						AND F.KD_CAB=A.BRANCHID				"
							+ "						AND CAST(B.VIEWORD AS VARCHAR) =C.PARMID"
							+ "						AND B.PARMGRP=6						"
							+ "						AND C.PARMGRP=13					"
							+ "						AND GL_ACCOUNT_TYPE4_CODE IN ('223','393') "
							+ "						AND A.VERSION='0'					"+parmCABANG
							+ "					GROUP BY 								"
							+ "							ACTUAL_DATE,					"
							+ "							GL_ACCOUNT_TYPE1_NAME,			"
							+ "							GL_ACCOUNT_TYPE1_CODE,			"
							+ "							GL_ACCOUNT_TYPE3_NAME,			"
							+ "							GL_ACCOUNT_TYPE3_CODE,			"
							+ "							GL_ACCOUNT_TYPE4_CODE,			"
							+ "							GL_ACCOUNT_TYPE4_NAME"
							+ "			)ZZ"
							+ "	WHERE GL_ACCOUNT_TYPE1_CODE IN ('1','2','3') "
							+ "			AND ACTUAL_DATE=?	"
							+ "			AND ZZ.JUMLAH<>0	"		
							+ "	ORDER BY 6",  
							new Object[]{sdf.format((Date)ComponentUtil.getValue(txtTgl))},new DTOMap());
				
				convert(listDataMap);
				int x = 1;
				if (listDataNeraca != null && listDataNeraca.size()>0) {
					for (dtoNeraca map : listDataNeraca) {
						Listitem item = new Listitem();
						item.setAttribute("DATA", map);
						item.appendChild(new Listcell(map.getGL_ACCOUNT_TYPE3_NAME()));
						item.appendChild(new Listcell(map.getGL_ACCOUNT_TYPE4_CODE()));
						item.appendChild(new Listcell(map.getGL_ACCOUNT_TYPE4_NAME()));
						item.appendChild(new Listcell(FunctionUtils.moneyToText(map.getRUPIAH())));
						item.appendChild(new Listcell(FunctionUtils.moneyToText(map.getVALLAS())));
						item.appendChild(new Listcell(FunctionUtils.moneyToText(map.getJUMLAH())));
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
	}
	
	 private List<dtoNeraca> convert(List<DTOMap> listDataMap) {
	    listDataNeraca= new ArrayList<dtoNeraca>();
	    for (DTOMap dtoMap : listDataMap) {
	          listDataNeraca.add((dtoNeraca) CopyObject.getObject(dtoNeraca.class, dtoMap.map));
	    }
	    return listDataNeraca;
	 }
	
	public boolean isValid(){
		if (ComponentUtil.getValue(txtTgl) == null) {
			MessageBox.showInformation("Tanggal harus diisi.");
			return false;
		}
		
		if (ComponentUtil.getValue(cmbJnsData) == null) {
			MessageBox.showInformation("Jenis Data harus diisi.");
			return false;
		}
		
		if (ComponentUtil.getValue(cmbJnsLaporan)==null) {
			MessageBox.showInformation("Jenis Laporan harus diisi.");
			return false;
		}else{
			String jenisLaporan=(String)ComponentUtil.getValue(cmbJnsLaporan);
			if (jenisLaporan.equals("cabang")) {
				if (ComponentUtil.getValue(cmbCabang) == null) {
					MessageBox.showInformation("Cabang harus diisi.");
					return false;
				}
			}else{
				if (ComponentUtil.getValue(cmbCabangKonsol) == null) {
					MessageBox.showInformation("Cabang Konsol harus diisi.");
					return false;
				}
			}
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
	public void doPrint() throws SQLException {
		if (listDataNeraca == null || listDataNeraca.size() <= 0) {
			MessageBox.showInformation("Tidak ada data yang akan dicetak");
			return;
		}
		String type = (String) ComponentUtil.getValue(cmbFormat);
		
		if (type.equals("pdf")) {
			String fileName="/jasper/LaporanNeracaKonsolidasi.jasper";
	
			String realpath = Executions.getCurrent().getDesktop()
					.getWebApp().getRealPath(fileName);
			System.out.println(realpath);
	
			DTOMap cfgsys=(DTOMap) GlobalVariable.getInstance().get("syshost");
			DTOMap branchMap=(DTOMap) GlobalVariable.getInstance().get("branchMap");
			
			HashMap param = new HashMap();
			
			String jenisData=(String)ComponentUtil.getValue(cmbJnsData);
			if (jenisData.equals("0")) {
				param.put("repId","jv-rpt-0044");
			} else {
				param.put("repId","jv-rpt-0044-r");
			}
			
			
			String jenisLaporan=(String)ComponentUtil.getValue(cmbJnsLaporan);
			if (jenisLaporan.equals("cabang")) {
				String kdCabang=(String) ComponentUtil.getValue(cmbCabang);
				Comboitem item=cmbCabang.getSelectedItem();
				String namaCabang=(String) item.getAttribute("NM_CAB");
				param.put("judul","LAPORAN NERACA");
				param.put("cabang",kdCabang);
				param.put("namaCabang",namaCabang);
			} else {
				String kdCabang=(String) ComponentUtil.getValue(cmbCabangKonsol);
				param.put("judul","LAPORAN NERACA KONSOLIDASI");
				if (!kdCabang.equals("All")) {
					Comboitem item=cmbCabangKonsol.getSelectedItem();
					String namaCabang=(String) item.getAttribute("NM_CAB");
					param.put("cabang",kdCabang);
					param.put("namaCabang",namaCabang);
				}else{
					DTOMap dtoCabang=(DTOMap) masterService.getMapMaster("SELECT TOP 1 KD_CAB,NM_CAB	"
							+ "										FROM CFG_CABANG		"
							+ "										WHERE TIPE_CABANG = 1"
							+ "											AND TGL_POS = (SELECT MAX(TGL_POS) "
							+ "															FROM CFG_CABANG)",null);	
					if (dtoCabang!=null) {
						param.put("cabang",dtoCabang.getString("KD_CAB"));
						param.put("namaCabang",dtoCabang.getString("NM_CAB")+" KONSOLIDASI");
					}else{
						param.put("cabang",branchMap.getString("KD_CAB"));
						param.put("namaCabang",branchMap.getString("NM_CAB")+" KONSOLIDASI");
					}
	 			}
			}
			param.put("period",sdf.format((Date) ComponentUtil.getValue(txtTgl)));
			param.put("tgl",((Date) ComponentUtil.getValue(txtTgl)));
			
			String imageString = cfgsys.getString("LOGO_BANK");
			byte[] encodedImage = Base64.decode(imageString);
			try {
				param.put("logo", JRImageLoader.loadImage(encodedImage));
			} catch (JRException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			param.put("namaBank",cfgsys.getString("NAMA_BANK"));
			param.put("printedBy",auth.getUserDetails().getUserId());
			JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(listDataNeraca);
			new JRreportWindow(this.getSelf(), true, param, fileName, ds,type,null);
			doReset();
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

        // Create a color for background cell
        XSSFColor colorCyan = new XSSFColor(new java.awt.Color(9, 218, 243));
        
        // Create a Sheet
        XSSFSheet sheet = workbook.createSheet("LAPORAN NERACA");
        
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
        
        // Create a Font for styling cells
        XSSFFont subFontTitle = workbook.createFont();
        
        // subFont
        subFontTitle.setFontHeightInPoints((short) 12);
        subFontTitle.setColor(IndexedColors.BLACK.getIndex());
        subFontTitle.setBold(true);        
        
        //Cellstyle for Sub total 
        XSSFCellStyle cellStyleSub = workbook.createCellStyle();
        cellStyleSub.setFont(subFontTitle);
        cellStyleSub.setFillForegroundColor(colorCyan);
        cellStyleSub.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
        cellStyleSub.setAlignment(CellStyle.ALIGN_CENTER);
        cellStyleSub.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
//        cellStyleSub.setBorderBottom(CellStyle.BORDER_THIN);
//        cellStyleSub.setBorderTop(CellStyle.BORDER_THIN);
        
        //CellStyle for sub total value
        XSSFCellStyle cellStyleSubValue = workbook.createCellStyle();
        cellStyleSubValue.setFont(subFontTitle);
        cellStyleSubValue.setFillForegroundColor(colorCyan);
        cellStyleSubValue.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
        cellStyleSubValue.setAlignment(CellStyle.ALIGN_RIGHT);
        cellStyleSubValue.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
//        cellStyleSubValue.setBorderBottom(CellStyle.BORDER_THIN);
//        cellStyleSubValue.setBorderTop(CellStyle.BORDER_THIN);        
        
        //Cellstyle for align center
        XSSFCellStyle cellStyleRight = workbook.createCellStyle();
        cellStyleRight.setAlignment(CellStyle.ALIGN_RIGHT);
        cellStyleRight.setVerticalAlignment(CellStyle.VERTICAL_CENTER);

        //Cellstyle for align right
        XSSFCellStyle cellStyleCenter = workbook.createCellStyle();
        cellStyleCenter.setAlignment(CellStyle.ALIGN_CENTER);
        cellStyleCenter.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        
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
        
        String[] columns = {"URAIAN","SANDI BI","DESCRIPTION","RUPIAH","VALLAS","JUMLAH"};
        
        // Merge for Title
        sheet.addMergedRegion(new CellRangeAddress(0,1,0,columns.length-1)); 
        
        // Create header cells
        for(int i = 0; i < columns.length; i++) {
            XSSFCell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerCellStyle);
        }
               
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        
        // Create Cell Style for formatting Date
        XSSFCellStyle dateCellStyle = workbook.createCellStyle();
        dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd-MM-yyyy"));
        
        // Create Other rows and cells with employees data
        int rowNum = 4;
        
        if (listDataNeraca.size() > 0) {
            String produk1="";
            int kodeAkun = 1;
            String 	PRODUK = "";
            Double totalSubRupiah = 0.00;
            Double totalSubValas = 0.00;
            Double totalSubJumlah = 0.00;
            Double totalRupiah = 0.00;
            Double totalValas = 0.00;
            Double totalJumlah = 0.00;
            
            
        	for (int i = 0; i < listDataNeraca.size(); i++) {
				dtoNeraca DATA =  listDataNeraca.get(i);
				PRODUK 	= DATA.getGL_ACCOUNT_TYPE1_NAME();
				
				String 	kodeProduk = DATA.getGL_ACCOUNT_TYPE1_CODE();
				int kodeAkunNext= Integer.parseInt(kodeProduk);
				
				XSSFRow row = sheet.createRow(rowNum++);
												
				if (kodeAkun == kodeAkunNext){
					String URAIAN 		= DATA.getGL_ACCOUNT_TYPE3_NAME();
					String SANDI_BI 	= DATA.getGL_ACCOUNT_TYPE4_CODE();
					String DESCRIPTION 	= DATA.getGL_ACCOUNT_TYPE4_NAME();
					BigDecimal RUPIAH 	= DATA.getRUPIAH();
					BigDecimal VALLAS 	= DATA.getVALLAS();
					BigDecimal JUMLAH 	= DATA.getJUMLAH();
					
					row.createCell(0).setCellValue(URAIAN);
					row.createCell(1).setCellValue(SANDI_BI);
					row.createCell(2).setCellValue(DESCRIPTION);
					row.createCell(3).setCellValue(FunctionUtils.moneyToText(RUPIAH));
					row.createCell(4).setCellValue(FunctionUtils.moneyToText(VALLAS));
					row.createCell(5).setCellValue(FunctionUtils.moneyToText(JUMLAH));									
					
					row.getCell(1).setCellStyle(cellStyleCenter);
					row.getCell(3).setCellStyle(cellStyleRight);
					row.getCell(4).setCellStyle(cellStyleRight);
					row.getCell(5).setCellStyle(cellStyleRight);			        			        
					
					//SumSubTotal
					totalSubRupiah += RUPIAH.doubleValue();
					totalSubValas += VALLAS.doubleValue();
					totalSubJumlah += JUMLAH.doubleValue();

					//SumTotal
					totalRupiah += RUPIAH.doubleValue();
					totalValas += VALLAS.doubleValue();
					totalJumlah += JUMLAH.doubleValue();					
					produk1 = PRODUK;
				} else {

					// Merge for Sub Total Cell
			        sheet.addMergedRegion(new CellRangeAddress(rowNum-1,rowNum-1,0,columns.length-4));
			        			 
			        // Insert for Sub Total Cell
			        row.createCell(0).setCellValue( produk1 + " - Total");
			        row.createCell(3).setCellValue(FunctionUtils.moneyToText(BigDecimal.valueOf(totalSubRupiah)));
					row.createCell(4).setCellValue(FunctionUtils.moneyToText(BigDecimal.valueOf(totalSubValas)));
					row.createCell(5).setCellValue(FunctionUtils.moneyToText(BigDecimal.valueOf(totalSubJumlah)));									
			        
					//Set CellStyle
					row.getCell(0).setCellStyle(cellStyleSub);					
					row.getCell(3).setCellStyle(cellStyleSubValue);
					row.getCell(4).setCellStyle(cellStyleSubValue);
					row.getCell(5).setCellStyle(cellStyleSubValue);			        			        
					
					i=i++;

					row = sheet.createRow(rowNum++);
					
					//Reset Sub Total for the next sum sub total
					totalSubRupiah = 0.0;
					totalSubValas = 0.0;
					totalSubJumlah = 0.0;
			        
			        kodeAkun = kodeAkunNext;
			        produk1 = PRODUK;
			        
			        String URAIAN 		= DATA.getGL_ACCOUNT_TYPE3_NAME();
					String SANDI_BI 	= DATA.getGL_ACCOUNT_TYPE4_CODE();
					String DESCRIPTION 	= DATA.getGL_ACCOUNT_TYPE4_NAME();
					BigDecimal RUPIAH 	= DATA.getRUPIAH();
					BigDecimal VALLAS 	= DATA.getVALLAS();
					BigDecimal JUMLAH 	= DATA.getJUMLAH();
					
					row.createCell(0).setCellValue(URAIAN);
					row.createCell(1).setCellValue(SANDI_BI);
					row.createCell(2).setCellValue(DESCRIPTION);
					row.createCell(3).setCellValue(FunctionUtils.moneyToText(RUPIAH));
					row.createCell(4).setCellValue(FunctionUtils.moneyToText(VALLAS));
					row.createCell(5).setCellValue(FunctionUtils.moneyToText(JUMLAH));									
					
					row.getCell(1).setCellStyle(cellStyleCenter);
					row.getCell(3).setCellStyle(cellStyleRight);
					row.getCell(4).setCellStyle(cellStyleRight);
					row.getCell(5).setCellStyle(cellStyleRight);			        			        
					
					totalSubRupiah += RUPIAH.doubleValue();
					totalSubValas += VALLAS.doubleValue();
					totalSubJumlah += JUMLAH.doubleValue();
					
					totalRupiah += RUPIAH.doubleValue();
					totalValas += VALLAS.doubleValue();
					totalJumlah += JUMLAH.doubleValue();					
					produk1 = PRODUK;				
				}
        	} 

        	
//			After end of row add two more cell(for last GL and overall total)
        	
        	sheet.addMergedRegion(new CellRangeAddress(rowNum,rowNum,0,columns.length-4));
			XSSFRow rowLastGL = sheet.createRow(rowNum++);
			
			rowLastGL.createCell(0).setCellValue( PRODUK + " - Total");
	        rowLastGL.createCell(3).setCellValue(FunctionUtils.moneyToText(BigDecimal.valueOf(totalSubRupiah)));
			rowLastGL.createCell(4).setCellValue(FunctionUtils.moneyToText(BigDecimal.valueOf(totalSubValas)));
			rowLastGL.createCell(5).setCellValue(FunctionUtils.moneyToText(BigDecimal.valueOf(totalSubJumlah)));
			
			rowLastGL.getCell(0).setCellStyle(cellStyleSub);
	        
			rowLastGL.getCell(3).setCellStyle(cellStyleSubValue);
			rowLastGL.getCell(4).setCellStyle(cellStyleSubValue);
			rowLastGL.getCell(5).setCellStyle(cellStyleSubValue);
		
        	sheet.addMergedRegion(new CellRangeAddress(rowNum,rowNum,0,columns.length-4));
	        
        	XSSFRow rowOverallTotal = sheet.createRow(rowNum++);	        	        
	        XSSFCell overallTotalCell = rowOverallTotal.createCell(0);
	        overallTotalCell .setCellValue("Overall - Total");
	        overallTotalCell .setCellStyle(cellStyleSub);
	        
	        rowOverallTotal.createCell(3).setCellValue(FunctionUtils.moneyToText(BigDecimal.valueOf(totalRupiah)));
			rowOverallTotal.createCell(4).setCellValue(FunctionUtils.moneyToText(BigDecimal.valueOf(totalValas)));
			rowOverallTotal.createCell(5).setCellValue(FunctionUtils.moneyToText(BigDecimal.valueOf(totalJumlah)));
			
			rowOverallTotal.getCell(3).setCellStyle(cellStyleSubValue);
			rowOverallTotal.getCell(4).setCellStyle(cellStyleSubValue);
			rowOverallTotal.getCell(5).setCellStyle(cellStyleSubValue);			
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

