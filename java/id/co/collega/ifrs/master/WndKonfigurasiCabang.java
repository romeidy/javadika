package id.co.collega.ifrs.master;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;

import com.jet.gand.component.CIPDateTextZk;

import id.co.collega.ifrs.common.DTOMap;
import id.co.collega.ifrs.common.JdbcTemplate;
import id.co.collega.ifrs.master.service.MasterServices;
import id.co.collega.ifrs.util.ComponentUtil;
import id.co.collega.ifrs.util.MessageBox;
import id.co.collega.v7.seed.config.AuthenticationService;
import id.co.collega.v7.ui.component.DialogUtil;
import id.co.collega.v7.ui.component.DialogUtil.PopupMode;

@org.springframework.stereotype.Component
@Scope("execution")
public class WndKonfigurasiCabang extends SelectorComposer<Component> {

	@Autowired
	AuthenticationService auth;

	@Autowired
	MasterServices masterService;

	@Autowired
	JdbcTemplate jt;

	@Wire
	Combobox cmbKdCab;
	@Wire
	Combobox cmbKdCabKonsol;
	@Wire
	Radiogroup radioJnsKantor;
	@Wire
	CIPDateTextZk txtTglImplemen;
	@Wire
	Textbox txtAlamat;
	
	@Wire
	Textbox txtProvinsi;
	@Wire
	Textbox txtKota;
	
	@Wire
	Button btnReset;

	@Wire
	Listbox listDataCabang;

	@Wire
	Button btnUpdate;

	@Wire
	Button btnExp;

	private List<DTOMap> listDto = new ArrayList<DTOMap>();

	private List<DTOMap> listData;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

//		btnExp.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
//
//			@Override
//			public void onEvent(Event event) throws Exception {
//				doCetak();
//
//			}
//		});

		btnUpdate.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(Event e) throws Exception {
				doUpdate();
			}
		});

		cmbKdCab.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {
			public void onEvent(Event e) throws Exception {
				doLoadData((String) ComponentUtil.getValue(cmbKdCab));
			}
		});

		listDataCabang.addEventListener(Events.ON_DOUBLE_CLICK, new EventListener<Event>() {
			public void onEvent(Event e) throws Exception {
				if (listDataCabang.getSelectedItem() != null) {
					// System.out.println("list data cab");
					DTOMap dto = (DTOMap) listDataCabang.getSelectedItem().getAttribute("DATA");
					doLoadData(dto.getString("KD_CAB"));
				}

			}
		});
		btnReset.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

			@Override
			public void onEvent(Event arg0) throws Exception {
				doReset();

			}
		});

		doLoadData();
		doLoadCabang();
		doLoadCabangKonsol();
		doLoadData((String) ComponentUtil.getValue(cmbKdCab));
		setTidakAktif();
		cekBtnUpdate();

	}

	/*private boolean isSpvActive() {
		try {
			String sql="Select * from CFG_LTKT";
			DTOMap map=(DTOMap)masterService.getMapMaster(sql, null);
			if(map.getString("ISSUPERVISI_PUSAT").equals("1")) {
				return true;
			}
			return false;
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		
	}
	private void cekBtnUpdate() {
		if(isSpvActive() && auth.getUserDetails().getActiveRole().equals("05")) {
			btnUpdate.setDisabled(true);
		} else {
			btnUpdate.setDisabled(false);
		}		
	}*/
	private boolean isSpvActive() {
		try {
			String sql;
			sql = "Select * from CFG_LTKT";
			DTOMap map = (DTOMap) masterService.getMapMaster(sql, null);
			sql = "SELECT ROLEID FROM MST_USER WHERE ROLEID='05'";
			List<DTOMap> mapUser = masterService.getDataMaster(sql, null);
			if (map.getString("ISSUPERVISI_PUSAT").equals("1") && !mapUser.isEmpty()) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	private void cekBtnUpdate() {
		if (auth.getUserDetails().getActiveRole().equals("05")) {
			btnUpdate.setDisabled(true);
		} else {
			btnUpdate.setDisabled(false);
		}
	}

	private void setTidakAktif() {
		cmbKdCabKonsol.setDisabled(true);
		for (Radio rdJenis : radioJnsKantor.getItems()) {
			rdJenis.setDisabled(true);
		}
		txtTglImplemen.setDisabled(true);
		txtAlamat.setDisabled(true);
	}

	private void setAktif(Integer typeCabang) {
		cmbKdCabKonsol.setDisabled(false);
		if (!typeCabang.equals(1)) {
			for (Radio rdJenis : radioJnsKantor.getItems()) {
				rdJenis.setDisabled(false);
			}
		}
		txtTglImplemen.setDisabled(false);
		txtAlamat.setDisabled(false);
	}

	private void doReset() {

		doLoadData();
		doLoadCabang();
		doLoadCabangKonsol();
		doLoadData((String) ComponentUtil.getValue(cmbKdCab));
		setTidakAktif();

	}

	/*private void doCetak() {

		try {

			if (listData == null || listData.size() == 0) {
				MessageBox.showInformation("Tidak ada Data untuk di Export");
			} else {
				Integer count = 0;
				String nmLaporan = "Cabang";
				SXSSFWorkbook workbook = getWorkBook(nmLaporan);
				String tglImplementasi = "";
				for (DTOMap data : listData) {
					count++;
					// System.out.println("Count :"+count);

					SXSSFRow row = workbook.getSheet("Cabang").createRow(count);
					SXSSFCell cell = row.createCell((short) 0);
					cell.setCellValue(count);				
					
					
					cell = row.createCell((short) 1);
					cell.setCellValue(data.getString("KD_CAB"));
					
					cell = row.createCell((short) 2);
					cell.setCellValue(data.getString("NM_CAB"));

					cell = row.createCell((short) 3);
					cell.setCellValue(data.getString("KD_CAB_KONSOL"));

					cell = row.createCell((short) 4);
					// map.getString("KVSY").equals("K")?"Konvensional":"Syariah"));
					cell.setCellValue(data.getString("KVSY").equals("K") ? "Konvensional" : "Syariah");

					cell = row.createCell((short) 5);
					cell.setCellValue(data.getString("ALAMAT"));

					cell = row.createCell((short) 6);
					cell.setCellValue(data.getString("NM_KOTA"));

					if (data.getDate("TGL_IMPLEMENTASI") != null) {
						tglImplementasi = new SimpleDateFormat("dd-MM-yyyy").format(data.getDate("TGL_IMPLEMENTASI"));
					}

					cell = row.createCell((short) 7);
					cell.setCellValue(tglImplementasi);

				}

				File file = new File(nmLaporan + ".xlsx");
				FileOutputStream streamOut = new FileOutputStream(file);
				workbook.write(streamOut);
				streamOut.close();
				Filedownload.save(file, "xlsx");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private SXSSFWorkbook getWorkBook(String nmLaporan) {
		SXSSFWorkbook workbook = new SXSSFWorkbook();
		SXSSFSheet sheet = workbook.createSheet("Cabang");
		XSSFCellStyle style = (XSSFCellStyle) workbook.createCellStyle();
		style.setBorderBottom(XSSFCellStyle.BORDER_THIN);
		style.setBorderLeft(XSSFCellStyle.BORDER_THIN);
		style.setBorderRight(XSSFCellStyle.BORDER_THIN);
		style.setBorderTop(XSSFCellStyle.BORDER_THIN);

		SXSSFRow row = sheet.createRow(0);
		SXSSFCell cell = row.createCell((short) 0);
		row = sheet.createRow(0);
		cell = row.createCell((short) 0);
		cell.setCellValue("No");
		cell.setCellStyle(style);
		
		
		
		cell = row.createCell((short) 1);
		cell.setCellValue("Kode Cabang");
		
		cell = row.createCell((short) 2);
		cell.setCellValue("Nama Cabang");

		cell = row.createCell((short) 3);
		cell.setCellValue("Konsolidasi");

		cell = row.createCell((short) 4);
		cell.setCellValue("KV/SY");

		cell = row.createCell((short) 5);
		cell.setCellValue("Alamat");

		cell = row.createCell((short) 6);
		cell.setCellValue("Kota");

		cell = row.createCell((short) 7);
		cell.setCellValue("Tgl Implementasi");

		return workbook;
	}*/

	@SuppressWarnings("unchecked")
	private void doLoadData() {
		try {
			listData = (List<DTOMap>) masterService.getDataMaster(
					" SELECT * FROM CFG_CABANG "
					+ "		WHERE TGL_POS IN (SELECT MAX(TGL_POS) FROM CFG_CABANG) "
							+ "	ORDER BY KD_CAB ASC",
					null);
			listDto = listData;

			if (listData == null || listData.isEmpty()) {
				MessageBox.showInformation("Data Tidak Ditemukan");
				listDataCabang.getItems().clear();
			} else {
				int x = 1;
				listDataCabang.getItems().clear();
				SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
				for (DTOMap map : listData) {
					Listitem item = new Listitem();
					item.setAttribute("DATA", map);
					item.appendChild(new Listcell(String.valueOf(x++)));
					item.appendChild(new Listcell(map.getString("KD_CAB")));
					item.appendChild(new Listcell(map.getString("KD_CAB_KONSOL")));
					item.appendChild(new Listcell(map.getInt("TIPE_CABANG").equals(1) ? "Pusat" : 
													map.getInt("TIPE_CABANG").equals(2) ? "Cabang":
														map.getInt("TIPE_CABANG").equals(3) ? "Cabang Pembantu":"Kas"));
					item.appendChild(new Listcell(map.getString("ALAMAT")));
					item.appendChild(new Listcell(map.getString("NM_KOTA")));
					item.appendChild(new Listcell(map.getDate("TGL_IMPLEMENTASI") == null ? "-"
							: sdf.format(map.getDate("TGL_IMPLEMENTASI"))));
					listDataCabang.appendChild(item);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@SuppressWarnings("unchecked")
	private void doLoadData(String KdCab) {
		DTOMap dto = new DTOMap();
		try {
			dto = (DTOMap) masterService.getMapMaster(" SELECT * FROM CFG_CABANG "
					+ "									WHERE TGL_POS IN (SELECT MAX(TGL_POS) FROM CFG_CABANG) AND KD_CAB='" + KdCab + "'",
					new Object[] {});
			// System.out.println("date "+dto.getDate("TGL_IMPLEMENTASI"));
			ComponentUtil.setValue(cmbKdCab, dto.getString("KD_CAB"));
			ComponentUtil.setValue(cmbKdCabKonsol, dto.getString("KD_CAB_KONSOL"));
			ComponentUtil.setValue(radioJnsKantor, dto.getInt("TIPE_CABANG"));
			ComponentUtil.setValue(txtTglImplemen, dto.getDate("TGL_IMPLEMENTASI"));
//			ComponentUtil.setValue(txtAlamat, dto.getString("ALAMAT") + ", " + dto.getString("KOTA"));
			ComponentUtil.setValue(txtAlamat, dto.getString("ALAMAT"));
			ComponentUtil.setValue(txtProvinsi, dto.getString("NM_PROVINSI"));
			ComponentUtil.setValue(txtKota, dto.getString("NM_KOTA"));
			setAktif(dto.getInt("TIPE_CABANG"));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@SuppressWarnings("unchecked")
	private void doLoadCabang() {
		cmbKdCab.getItems().clear();

		Comboitem item = new Comboitem();

		for (DTOMap map : listDto) {
			item = new Comboitem();
			item.setLabel(map.getString("KD_CAB") + " - " + map.getString("NM_CAB"));
			item.setValue(map.getString("KD_CAB"));
			cmbKdCab.appendChild(item);
		}

		cmbKdCab.setSelectedIndex(0);
	}

	@SuppressWarnings("unchecked")
	private void doLoadCabangKonsol() {
		cmbKdCabKonsol.getItems().clear();
		List<DTOMap> listData = (List<DTOMap>) masterService.getDataMaster(
				"SELECT KD_CAB,NM_CAB FROM CFG_CABANG "
				+ "	WHERE KD_CAB IN (SELECT DISTINCT KD_CAB_KONSOL "
				+ "						FROM CFG_CABANG "
				+ "						WHERE TGL_POS IN (SELECT MAX(TGL_POS) FROM CFG_CABANG)"
				+ "			AND TGL_POS IN (SELECT MAX(TGL_POS) FROM CFG_CABANG))",
				null);
		Comboitem item = new Comboitem();
		for (DTOMap map : listData) {
			item = new Comboitem();
			item.setLabel(map.getString("NM_CAB"));
			item.setValue(map.getString("KD_CAB"));
			cmbKdCabKonsol.appendChild(item);
		}

	}

	@SuppressWarnings("unchecked")
	private void doUpdate() {
		try {

			if (cekValidasi()) {
				doShowSpv();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void saveData() {
		String sqlUpdate = "UPDATE CFG_CABANG SET NM_PROVINSI='" + ComponentUtil.getValue(txtProvinsi)
				+ "' ," + "NM_KOTA='" + ComponentUtil.getValue(txtKota) + "', " + "ALAMAT='" + txtAlamat.getText() + "' " 
				+ "WHERE KD_CAB='"
				+ ComponentUtil.getValue(cmbKdCab).toString() + "' ";

		// System.out.println(sqlUpdate);

		masterService.update(sqlUpdate, null);

		MessageBox.showInformation("Data Berhasil Di Update");
	}

	private void doShowSpv() {

		/*
		 * DialogUtil.showPopupDialogCloseOnly(
		 * "/page/dialog/WndDialogAskSpv.zul","Approval Supervisi", getSelf(), new
		 * EventListener<Event>() {
		 * 
		 * @Override public void onEvent(Event arg0) throws Exception {
		 * if(arg0.getName().equals("onClick") ){
		 * System.out.println("btn ok di clik..!"); saveData(); doLoadData();
		 * doLoadCabang(); doLoadCabangKonsol(); doLoadData((String)
		 * ComponentUtil.getValue(cmbKdCab)); doLoadProvinsi(); doLoadKota();
		 * setTidakAktif(); } } }, null);
		 */
				
		
		if (isSpvActive()) {				
			Map<String, Object> mapss = new HashMap<String, Object>(); 
			DTOMap map = new DTOMap();
			String nmMenu="Konfigurasi Cabang";
			map.put("USERID_INPUT", auth.getUserDetails().getUserId());
			map.put("NM_MENU", nmMenu);
			//map.put("MODUL", "Pemeliharaan PJK"); 
			mapss.put("data", map);
			
			DialogUtil.showPopupDialog("/page/dialog/WndDialogAskSpv.zul", "Otorisasi Perubahaan " + nmMenu, getSelf(), PopupMode.OK_CLOSE, new EventListener<Event>() {

				@Override
				public void onEvent(Event arg0) throws Exception {
					String returnValue = (String) arg0.getData();
					if (returnValue.equals("Berhasil")) {
						saveData();
						doLoadData();
						doLoadCabang();
						doLoadCabangKonsol();
						doLoadData((String) ComponentUtil.getValue(cmbKdCab));
						setTidakAktif();
					}
					
				}
			}, new EventListener<Event>() {

				@Override
				public void onEvent(Event arg0) throws Exception {
					String returnValue = (String) arg0.getData();
					if(returnValue.equals("gagal")) {
						MessageBox.showInformation("Data batal Di Simpan");
						
					}else {
						MessageBox.showInformation("Data Tidak Bisa Di Simpan");
						
					}
					
				}
			}, mapss);

			/*DialogUtil.showPopupDialogCloseOnly("/page/dialog/WndDialogAskSpv.zul",
					"Otorisasi Perubahaan " + nmMenu, getSelf(), new EventListener<Event>() {

						@Override
						public void onEvent(Event arg0) throws Exception {
							// doLoadData();
							// String test =(String)arg0.getData();
							// log.info("test : {}", test);
							String returnValue = (String) arg0.getData();							
							if (returnValue.equals("Berhasil")) {
								
								saveData();
								doLoadData();
								doLoadCabang();
								doLoadCabangKonsol();
								doLoadData((String) ComponentUtil.getValue(cmbKdCab));
								doLoadProvinsi();
								doLoadKota();
								setTidakAktif();
								
								doUpdate();
								setDataPJK();
								cekBtnUpdate();
							}else if(returnValue.equals("gagal")) {
								MessageBox.showInformation("Data batal Di Simpan");
							}else {
								MessageBox.showInformation("Data Tidak Bisa Di Simpan");
							}

						}
					}, mapss);*/

		} else {
			saveData();
			doLoadData();
			doLoadCabang();
			doLoadCabangKonsol();
			doLoadData((String) ComponentUtil.getValue(cmbKdCab));
			setTidakAktif();
		}
		
		
		
		
		
		/*Map<String, Object> mapss = new HashMap<String, Object>();
		DTOMap map = new DTOMap();
		map.put("KD_PROVINSI", ComponentUtil.getValue(txtProvinsi).toString());
		map.put("KD_KOTA", ComponentUtil.getValue(txtKota).toString());
		map.put("NM_KOTA", txtKota.getText());
		map.put("NM_PROVINSI", txtKota.getText());
		map.put("KD_CAB", ComponentUtil.getValue(cmbKdCab).toString());
		String nmMenu="Konfigurasi Cabang";
		map.put("NM_MENU", nmMenu);
		map.put("MODUL", "Konfigurasi Cabang");
		mapss.put("data", map);
		DialogUtil.showPopupDialogCloseOnly("/page/dialog/WndDialogAskSpv.zul", "Otorisasi Perubahaan "+nmMenu, getSelf(), new EventListener<Event>() {

			@Override
			public void onEvent(Event arg0) throws Exception {
				doLoadData();
				doLoadCabang();
				doLoadCabangKonsol();
				doLoadData((String) ComponentUtil.getValue(cmbKdCab));
				doLoadProvinsi();
				doLoadKota();
				setTidakAktif();				
			}
		}, mapss);
		
		//System.out.println("ask SPV di unload.....");
		doLoadData();
		doLoadCabang();
		doLoadCabangKonsol();
		doLoadData((String) ComponentUtil.getValue(cmbKdCab));
		doLoadProvinsi();
		doLoadKota();
		setTidakAktif();*/
		

		/*DialogUtil.showPopupDialog("/page/dialog/WndDialogAskSpv.zul", "Approval Supervisi", getSelf(),
				PopupMode.OK_CLOSE, new EventListener<Event>() {

					@Override
					public void onEvent(Event arg0) throws Exception {

						if (arg0.getName().equals("onClick")) {
							// System.out.println("btn ok di clik..!");

							// saveData();
							doLoadData();
							doLoadCabang();
							doLoadCabangKonsol();
							doLoadData((String) ComponentUtil.getValue(cmbKdCab));
							doLoadProvinsi();
							doLoadKota();
							setTidakAktif();
						}

					}
				}, new EventListener<Event>() {

					@Override
					public void onEvent(Event arg0) throws Exception {
						// System.out.println("btn2 di click : "+arg0.getName().toString());
						if (arg0.getName().equals("onClick")) {
							// System.out.println("Btn close di click..!");
							doLoadData();
							// doLoadData();
							doLoadCabang();
							doLoadCabangKonsol();
							doLoadData((String) ComponentUtil.getValue(cmbKdCab));
							doLoadProvinsi();
							doLoadKota();
							setTidakAktif();
						}

					}
				}, mapss);*/

	}

	private Boolean cekValidasi() {
		if (ComponentUtil.getValue(txtProvinsi) == null || ComponentUtil.getValue(txtProvinsi).equals("")) {
			throw new WrongValueException(txtProvinsi, "Provinsi harus diisi.");
		}
		if (ComponentUtil.getValue(txtKota) == null || ComponentUtil.getValue(txtKota).equals("")) {
			throw new WrongValueException(txtKota, "Kota harus diisi.");
		}
		if (ComponentUtil.getValue(cmbKdCab) == null || ComponentUtil.getValue(cmbKdCab).equals("")) {
			throw new WrongValueException(cmbKdCab, "Kode Cab harus diisi.");
		}
		return true;
	}

}
