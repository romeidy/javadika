package id.co.collega.ifrs.master;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
//import org.zkoss.zk.ui.Executions;
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
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Image;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Window;

import com.jet.gand.services.GlobalVariable;

import id.co.collega.ifrs.common.Cryptograph;
import id.co.collega.ifrs.common.DTOMap;
import id.co.collega.ifrs.common.JdbcTemplate;
import id.co.collega.ifrs.master.service.MasterServices;
import id.co.collega.ifrs.util.ComponentUtil;
import id.co.collega.ifrs.util.MessageBox;
import id.co.collega.v7.seed.config.AuthenticationService;
import id.co.collega.v7.ui.component.DialogUtil;
import id.co.collega.v7.ui.component.MenuTreeItem;
import id.co.collega.v7.ui.component.MenuTreeNode;
import id.co.collega.v7.ui.component.DialogUtil.PopupMode;
import id.co.collega.v7.ui.component.composite.MainControllerV2;

@org.springframework.stereotype.Component
@Scope("execution")
public class WndPengumuman extends SelectorComposer<Component> {

	@Autowired
	AuthenticationService auth;

	@Wire
	Textbox txtUserInput;
	@Wire
	Textbox txtNamaInput;
	@Wire
	Textbox txtPengumuman;
	@Wire
	Combobox cmbStatus;
	@Wire
	Datebox txtTanggalInput;
	@Wire
	Listbox listPengumuman;

	@Wire
	Window wnd;

	@Wire
	Button btnSimpan;
	@Wire
	Button btnHapus;
	@Wire
	Button btnReset;
	@Wire
	Button btnExport;

	@Autowired
	MasterServices masterService;
	@Autowired
	AuthenticationService authService;
	@Autowired
	JdbcTemplate jt;

	Boolean onLoad = false;
	@Wire
	DTOMap dtoDelete;
	private List<DTOMap> listData;

	private Date tglInput;

	private DTOMap dto;

	String Aksi;
	
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		btnSimpan.addEventListener(Events.ON_CLICK, new EventListener() {
			public void onEvent(Event event) throws Exception {
				if (checkPrivInsert()) {
					doCekSpv();	
				}
				//doSimpan();
				// doReset();
			}
		});

		btnReset.addEventListener(Events.ON_CLICK, new EventListener() {
			public void onEvent(Event event) throws Exception {
				doReset();
			}
		});

		btnHapus.addEventListener(Events.ON_CLICK, new EventListener() {
			public void onEvent(Event event) throws Exception {
				if (checkPrivDelete()) {
					doHapus1();	
				}
				doReset();
			}
		});

		listPengumuman.addEventListener(Events.ON_DOUBLE_CLICK, new EventListener() {
			public void onEvent(Event event) throws Exception {
				doEdit();
			}
		});

		btnExport.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				doExport();
			}

		});

		doReset();
		cekBtnUpdate();
	}

	/*
	 * private boolean isSpvActive() { try { String sql="Select * from CFG_LTKT";
	 * DTOMap map=(DTOMap)masterService.getMapMaster(sql, null);
	 * if(map.getString("ISSUPERVISI_PUSAT").equals("1")) { return true; } return
	 * false; }catch(Exception e) { e.printStackTrace(); return false; }
	 * 
	 * } private void cekBtnUpdate() { if(isSpvActive() &&
	 * auth.getUserDetails().getActiveRole().equals("05")) {
	 * btnSimpan.setDisabled(true); } else { btnSimpan.setDisabled(false); } }
	 */

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
			btnSimpan.setDisabled(true);
		} else {
			btnSimpan.setDisabled(false);
		}
	}

	private void doExport() {
		try {

			HSSFWorkbook workbook = new HSSFWorkbook();
			if (listData == null || listData.isEmpty()) {
				MessageBox.showInformation("Tidak ada data untuk di export");
			} else {
				Integer i = 0;
				Integer j = 1;
				Integer noUrut = 0;
				HSSFSheet firstSheet = null;
				for (DTOMap dto : listData) {
					if (i == 0 || i % 50000 == 0) {
						firstSheet = workbook.createSheet("Nasabah Dikecualikan " + j);
						j++;
						noUrut = 0;

						HSSFRow row = firstSheet.createRow(noUrut);
						HSSFCell cell = row.createCell(0);
						cell.setCellValue("NO");

						cell = row.createCell(1);
						cell.setCellValue("Tanggal");

						cell = row.createCell(2);
						cell.setCellValue("Pengumuman");

						cell = row.createCell(3);
						cell.setCellValue("Publish");
					}

					noUrut++;
					HSSFRow row = firstSheet.createRow(noUrut);
					HSSFCell cell = row.createCell(0);
					cell.setCellValue(getDataStr(noUrut));

					cell = row.createCell(1);
					cell.setCellValue(getDataStr(dto.get("TGL_INPUT")));

					cell = row.createCell(2);
					cell.setCellValue(getDataStr(dto.get("URAIAN")));

					cell = row.createCell(3);
					String stspublish = (String) (cekData(dto.getString("STS_PUBLISH")).equals("0") ? "N0" : "Yes");
					cell.setCellValue(getDataStr(stspublish));

					i++;

				}

			}

			FileOutputStream fos = null;
			try {

				File file = new File("Pemeliharaan Pengumuman.xls");
				fos = new FileOutputStream(file);
				workbook.write(fos);
				fos.close();
				Filedownload.save(file, "xlsx");
				Aksi = "Export Data To Excel "+ file;
				doLogAktfitas(Aksi);

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (fos != null) {
					try {
						fos.flush();
						fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String cekData(String data) {
		if (data == null || data.equals("")) {
			return " - ";
		} else {
			return data;
		}
	}

	private String getDataStr(Object str) {
		String res = new String();

		if (str != null) {
			res = str.toString();
		}

		return res;
	}

	private void doSimpan() {
		doHapus();
		
		DTOMap dto = new DTOMap();
		dto.put("URAIAN", ComponentUtil.getValue(txtPengumuman));
		dto.put("TGL_INPUT", tglInput);
		dto.put("USER_INPUT", ComponentUtil.getValue(txtUserInput));
		dto.put("USERNM_INPUT", ComponentUtil.getValue(txtNamaInput));
		dto.put("STS_PUBLISH", ComponentUtil.getValue(cmbStatus));
		
		masterService.insertData(dto, "CFG_PENGUMUMAN");
		Aksi = "Penambahan data dengan User Input "+ComponentUtil.getValue(txtUserInput)+" "+ComponentUtil.getValue(txtNamaInput);
		doLogAktfitas(Aksi);
		MessageBox.showInformation("Data Pengumuman berhasil disimpan");
		
		
		// try {
		/*if (doValidasi()) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			tglInput = new Date();
			
			if (btnHapus.isVisible()) {
				dto = (DTOMap) listPengumuman.getSelectedItem().getAttribute("DATA");

				dtoDelete = new DTOMap();
				dtoDelete.put("TGL_INPUT", dto.getDate("TGL_INPUT"));
				dtoDelete.put("PK", "TGL_INPUT");
			} else {
				dtoDelete = new DTOMap();
				dtoDelete.put("TGL_INPUT", tglInput);
				dtoDelete.put("PK", "TGL_INPUT");
			}
			doHapus();

			
			 * DTOMap dto = new DTOMap(); dto.put("URAIAN",
			 * ComponentUtil.getValue(txtPengumuman)); dto.put("TGL_INPUT", tglInput);
			 * dto.put("USER_INPUT", ComponentUtil.getValue(txtUserInput));
			 * dto.put("USERNM_INPUT", ComponentUtil.getValue(txtNamaInput));
			 * dto.put("STS_PUBLISH", ComponentUtil.getValue(cmbStatus));
			 * 
			 * masterService.insertData(dto, "CFG_PENGUMUMAN");
			 * MessageBox.showInformation("Data Pengumuman berhasil disimpan");
			 
			
			doCekSpv();

			
			doReset();

			// masterService.insertData(dto, "CFG_PENGUMUMAN");
			// MessageBox.showInformation("Data Pengumuman berhasil disimpan");
		} else {
			doReset();
		}*/
		

		doReset();

	}

	private boolean doValidasi() {
		if (ComponentUtil.getValue(txtPengumuman) == null || ComponentUtil.getValue(txtPengumuman).equals("")) {
			throw new WrongValueException(txtPengumuman, "Pengumuman harus diisi.");
		}
		return true;
	}

	private List<DTOMap> getPengumuman() {
		List<DTOMap> listData = new ArrayList<DTOMap>();
		try {
			listData = masterService.getDataMaster(
					"SELECT * FROM CFG_PENGUMUMAN ORDER BY STS_PUBLISH DESC, TGL_INPUT DESC", new Object[] {});
			return listData;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private void setDataPJK() {
		try {
			listData = getPengumuman();
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
			int i = 1;

			listPengumuman.getItems().clear();
			if (listData != null && !listData.isEmpty()) {
				for (DTOMap dtoMap : listData) {
					Listitem item = new Listitem();
					item.setAttribute("DATA", dtoMap);
					item.appendChild(new Listcell(String.valueOf(i++)));
					item.appendChild(new Listcell(sdf.format(dtoMap.getDate("TGL_INPUT"))));
					item.appendChild(new Listcell(dtoMap.getString("URAIAN")));

					if (dtoMap.getString("STS_PUBLISH").equals("1")) {
						Image img = new Image();
						img.setSrc("/asset/image/hot.png");
						Listcell lc = new Listcell();
						lc.appendChild(img);
						item.appendChild(lc);
					} else {
						item.appendChild(new Listcell(""));
					}

					listPengumuman.appendChild(item);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getStatusNm(String val) {
		try {
			if (val.equals("1")) {
				return "Ya";
			} else {
				return "Tidak";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	private void doEdit() {
		try {
			if (listPengumuman.getSelectedItem() != null) {
				DTOMap dto = (DTOMap) listPengumuman.getSelectedItem().getAttribute("DATA");
				ComponentUtil.setValue(txtTanggalInput, dto.getDate("TGL_INPUT"));
				ComponentUtil.setValue(txtUserInput, dto.getString("USER_INPUT"));
				ComponentUtil.setValue(txtNamaInput, dto.getString("USERNM_INPUT"));
				ComponentUtil.setValue(txtPengumuman, dto.getString("URAIAN"));
				ComponentUtil.setValue(cmbStatus, dto.getString("STS_PUBLISH"));
				btnHapus.setVisible(true);

				dtoDelete = new DTOMap();
				
				dtoDelete.put("URAIAN", dto.getString("URAIAN"));
				dtoDelete.put("PK", "URAIAN");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void doHapus() {
		try {
			if (dtoDelete != null) {
				masterService.deleteData(dtoDelete, "CFG_PENGUMUMAN");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void doHapus1() {
		try {
			if (dtoDelete != null) {

				Messagebox.show("Apakah Anda Yakin mau menghapus data ini .. ?", "KONFIRMASI",
						Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, new EventListener<Event>() {

							@Override
							public void onEvent(Event e) throws Exception {
								if (Messagebox.ON_OK.equals(e.getName())) {
									System.out.println("TGL INPUT ="+dtoDelete.getString("URAIAN"));
									masterService.deleteData(dtoDelete, "CFG_PENGUMUMAN");
									Aksi = "Penghapusan pada data "+ComponentUtil.getValue(txtUserInput)+", "+ComponentUtil.getValue(txtNamaInput);
									doLogAktfitas(Aksi);
									doReset();
								} else if (Messagebox.ON_CANCEL.equals(e.getName())) {
									doReset();
								}

							}

						});

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void doHapusOnly() {
		try {
			if (dtoDelete != null) {
				System.out.println("dtoDelete :::: " + dtoDelete.getDate("TGL_INPUT"));
				masterService.deleteData(dtoDelete, "CFG_PENGUMUMAN");
				if (btnHapus.isVisible()) {
					MessageBox.showInformation("Data Pengumuman berhasil dihapus");
					doReset();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void doReset() {
		txtTanggalInput.setValue(new Date());
		txtNamaInput.setValue(authService.getUserDetails().getUserName());
		txtUserInput.setValue(authService.getUserDetails().getUserId());
		txtPengumuman.setValue("");
		cmbStatus.setSelectedIndex(0);
		btnHapus.setVisible(false);
		setDataPJK();
	}

	private void doCekSpv() {
		if(doValidasi()) {
			if (isSpvActive()) {
				Map<String, Object> mapss = new HashMap<String, Object>();
				DTOMap map = new DTOMap();
				String nmMenu = "Pengumuman ";
				map.put("USERID_INPUT", auth.getUserDetails().getUserId());
				map.put("NM_MENU", nmMenu);
				mapss.put("data", map);
				
				DialogUtil.showPopupDialog("/page/dialog/WndDialogAskSpv.zul", "Otorisasi Perubahaan " + nmMenu, getSelf(), PopupMode.OK_CLOSE, new EventListener<Event>() {

					@Override
					public void onEvent(Event arg0) throws Exception {
						String returnValue = (String) arg0.getData();
						if (returnValue.equals("Berhasil")) {
							//doReset();
							doSimpan();
							doReset();
							cekBtnUpdate();
						}
						
					}
				}, new EventListener<Event>() {

					@Override
					public void onEvent(Event arg0) throws Exception {
						String returnValue = (String) arg0.getData();
						if(returnValue.equals("gagal")) {
							MessageBox.showInformation("Data batal Di Simpan");
							doReset();
							cekBtnUpdate();
						}else {
							MessageBox.showInformation("Data Tidak Bisa Di Simpan");
							doReset();
							cekBtnUpdate();
						}
						
					}
				}, mapss);

				/*DialogUtil.showPopupDialogCloseOnly("/page/dialog/WndDialogAskSpv.zul", "Otorisasi Perubahaan " + nmMenu,
						getSelf(), new EventListener<Event>() {

							@Override
							public void onEvent(Event arg0) throws Exception {
								// doLoadData();
								// String test =(String)arg0.getData();
								// log.info("test : {}", test);
								String returnValue = (String) arg0.getData();
								if (returnValue.equals("Berhasil")) {
									// doReset();
									doSimpan();
									doReset();
									cekBtnUpdate();
								}else if(returnValue.equals("gagal")) {
									MessageBox.showInformation("Data batal Di Simpan");
									MessageBox.showInformation("Data Tidak Bisa Di Simpan");
								}

							}
						}, mapss);*/

			} else {
				doSimpan();
				doReset();
				cekBtnUpdate();
			}
		}
		
	}
}
