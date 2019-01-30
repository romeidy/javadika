package id.co.collega.ifrs.master;

import id.co.collega.ifrs.common.DTOMap;
import id.co.collega.ifrs.master.service.MasterServices;
import id.co.collega.ifrs.util.ComponentUtil;
import id.co.collega.ifrs.util.MessageBox;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.zkoss.image.AImage;
import org.zkoss.zhtml.Object;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Fileupload;
import org.zkoss.zul.Image;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vlayout;

import com.jet.gand.services.GlobalVariable;

@org.springframework.stereotype.Component
@Scope("execution")
public class WndSPPITest extends SelectorComposer<Component> {

	@Wire
	Image img01;
	@Wire
	Image img02;
	@Wire
	Image img03;
	@Wire
	Image img04;
	@Wire
	Image img05;
	@Wire
	Image img06;
	@Wire
	Image img07;
	@Wire
	Image img08;
	@Wire
	Image img09;
	@Wire
	Image img10;
	@Wire
	Image img11;
	@Wire
	Image img12;

	@Wire
	Image imgArrw01;
	@Wire
	Image imgArrw02;
	@Wire
	Image imgArrw03;
	@Wire
	Image imgArrw04;
	@Wire
	Image imgArrw05;
	@Wire
	Image imgArrw06;
	@Wire
	Image imgArrw07;
	@Wire
	Image imgArrw08;
	@Wire
	Image imgArrw09;
	@Wire
	Image imgArrw10;
	@Wire
	Image imgArrw11;
	@Wire
	Image imgArrw12;
	@Wire
	Image imgArrw13;
	@Wire
	Image imgArrw14;
	@Wire
	Image imgArrw15;
	

	@Wire
	Textbox txtParmId;
	@Wire
	Textbox txtParmNm;

	@Wire
	Textbox txtResult;
	@Wire
	Textbox txtParmGrp;
	@Wire
	Textbox txtParmGrpId;
	@Wire
	Textbox txtParmGrpNm;
	@Wire
	Textbox txt01;
	@Wire
	Textbox txt02;
	@Wire
	Textbox txt03;
	@Wire
	Textbox txt04;
	@Wire
	Textbox txtParmIdFilter;
	@Wire
	Textbox txtParmNmFilter;
	
	@Wire
	Menupopup MnPrd;
	
	@Wire Button btnReset;

	@Wire
	Listbox listDataProduk;
	@Wire
	Listbox listDataSPPI;

	@Wire
	Menuitem MnItem;

	@Autowired
	MasterServices masterService;

	private List<DTOMap> listDto = new ArrayList<DTOMap>();

	private List<DTOMap> listData;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		img01.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(Event e) throws Exception {
				doReset();
				if (img01.getId() != null) {

					txtParmGrp.setValue("20");
					// txtParmGrpId.setValue("01");
				}
				doLoadPrd();
			}
		});

		img02.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(Event e) throws Exception {
				doReset();
				if (img01.getId() != null) {

					txtParmGrp.setValue("20");
					// txtParmGrpId.setValue("01");
				}
				doLoadPrd();
			}
		});

		img03.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(Event e) throws Exception {
				doReset();
				if (img01.getId() != null) {

					txtParmGrp.setValue("20");
					// txtParmGrpId.setValue("01");
				}
				doLoadPrd();
			}
		});

		txtParmId.addEventListener(Events.ON_BLUR, new EventListener<Event>() {
			public void onEvent(Event e) throws Exception {
				if (txtParmId != null) {
					imgArrw01.setVisible(true);
					img04.setVisible(true);
				} else {
					System.out.println("MESSSAGE NULL");
				}
			}
		});

		txtParmNm.addEventListener(Events.ON_CHANGE,
				new EventListener<Event>() {
					public void onEvent(Event e) throws Exception {
						if (txtParmId != null) {
							imgArrw01.setVisible(true);
							img04.setVisible(true);
						} else {
							System.out.println("MESSSAGE NULL");
						}
					}
				});

		txt01.addEventListener(Events.ON_BLUR, new EventListener<Event>() {
			public void onEvent(Event e) throws Exception {
				txt02.setFocus(true);
				if (txt01.getValue() != null) {
					if (txt01.getValue().equals("Memenuhi")) {
						imgArrw02.setVisible(true);
						img05.setVisible(true);

					} else {
						System.out.println("MESSSAGE NULL 1");
					}

				} else {
					System.out.println("MESSSAGE NULL 2");
				}
			}
		});

		txt02.addEventListener(Events.ON_BLUR, new EventListener<Event>() {
			public void onEvent(Event e) throws Exception {
				if (txt02.getValue() != null) {
					if (txt02.getValue().equals("BM1")) {
						imgArrw03.setVisible(true);
						imgArrw04.setVisible(true);
						img07.setVisible(true);
						txt03.setFocus(true);
					} else {
						System.out.println("MESSSAGE NULL");
					}

				} else {
					System.out.println("MESSSAGE NULL");
				}
			}
		});

		txt03.addEventListener(Events.ON_BLUR, new EventListener<Event>() {
			public void onEvent(Event e) throws Exception {
				// if (txt03.getValue() != null) {
				// if(txt03.getValue().equals("Tidak")){
				// imgArrw05.setVisible(true);
				// // imgArrw06.setVisible(true);
				// txt04.setFocus(true);
				// img09.setFocus(true);
				// img09.setVisible(true);
				// doSave();
				// }else{
				// System.out.println("MESSSAGE NULL");
				// }
				//
				// } else {
				// System.out.println("MESSSAGE NULL");
				// }
			}
		});

		listDataProduk.addEventListener(Events.ON_DOUBLE_CLICK,
				new EventListener<Event>() {
					public void onEvent(Event e) throws Exception {
						Listitem item = listDataProduk.getSelectedItem();

						if (item != null) {
							// System.out.println("list data cab");
							DTOMap dto = (DTOMap) listDataProduk
									.getSelectedItem().getAttribute("DATA");
							doData(dto.getString("PARMID"));

						} else {
							System.out.println("MESSSAGE NULL");
						}
					}
				});

		img09.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(Event e) throws Exception {
				img09.setVisible(true);
				int parmGrp = 2001;
				doSave(parmGrp);

			}
		});

		img10.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(Event e) throws Exception {
				img10.setVisible(true);
				int parmGrp = 2002;
				doSave(parmGrp);
			}
		});

		img11.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(Event e) throws Exception {
				img11.setVisible(true);
				int parmGrp = 2003;
				doSave(parmGrp);
			}
		});

		img12.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(Event e) throws Exception {
				img12.setVisible(true);
				int parmGrp = 2002;
				doSave(parmGrp);
			}
		});
		
		txtParmIdFilter.addEventListener(Events.ON_BLUR, new EventListener<Event>() {
			public void onEvent(Event e) throws Exception {
				doLoadSPPI();
			}
		});
		
		txtParmNmFilter.addEventListener(Events.ON_BLUR, new EventListener<Event>() {
			public void onEvent(Event e) throws Exception {
				doLoadSPPI();
			}
		});
		
		btnReset.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(Event e) throws Exception {
				doReset();
			}
		});
		
		

		// img03.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
		// public void onEvent(Event e) throws Exception {
		// doLoadPrd();
		// }
		// });

		doLoadSPPI();
		doImageload(); 
	}

	protected void doReset() {
		ComponentUtil.setValue(txtParmId, null);
		ComponentUtil.setValue(txtParmNm, null);
		ComponentUtil.setValue(txtParmGrp, null);
		ComponentUtil.setValue(txtParmGrpId, null);
		ComponentUtil.setValue(txtParmIdFilter, null);
		ComponentUtil.setValue(txtParmNmFilter, null);

		ComponentUtil.setValue(txt01, null);
		ComponentUtil.setValue(txt02, null);
		ComponentUtil.setValue(txt03, null);
		ComponentUtil.setValue(txt04, null);
		doLoadSPPI();
	}

	public void doData(String parmId) {
		DTOMap dto = new DTOMap();
		try {
			dto = (DTOMap) masterService.getMapMaster(
					" SELECT PARMID, PARMNM FROM CFG_PARM" + " WHERE PARMID='"
							+ parmId + "' AND PARMGRP=1", new Object[] {});
			ComponentUtil.setValue(txtParmId, dto.getString("PARMID"));
			ComponentUtil.setValue(txtParmNm, dto.getString("PARMNM"));
			txtParmIdFilter.setFocus(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void doLoadPrd() {

		try {
			listData = masterService
					.getDataMaster(
							"SELECT A.PARMID, A.PARMNM, B.PARMGRP  FROM CFG_PARM A LEFT OUTER JOIN CFG_PARM B ON B.PARMGRP IN (2001,2002,2003) "
									+ "AND B.PARMID=A.PARMID WHERE A.PARMGRP=1",
							new Object[] {});

			listDto = listData;

			if (listData == null || listData.isEmpty()) {
				MessageBox.showInformation("Data Tidak Ditemukan");
				listDataProduk.getItems().clear();
			} else {
				int x = 1;
				listDataProduk.getItems().clear();
				for (DTOMap map : listData) {
					Listitem item = new Listitem();
					item.setAttribute("DATA", map);
					item.appendChild(new Listcell(String.valueOf(x++)));
					item.appendChild(new Listcell(map.getString("PARMID")
							+ " - " + map.getString("PARMNM")));
					item.appendChild(new Listcell(
							map.getInt("PARMGRP") == null ? ""
									: map.getInt("PARMGRP") == 2001 ? "Amortised Cost"
											: map.getInt("PARMGRP") == 2002 ? "FVOCI"
													: "FVPL"));
					listDataProduk.appendChild(item);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void doLoadSPPI() {
			String kdprd = (String) ComponentUtil.getValue(txtParmIdFilter);
			if(kdprd == null){
				kdprd="";
			
			}else{
				kdprd=" AND A.PARMID='"+ kdprd +"' ";
			}
			
			String nmprd = (String) ComponentUtil.getValue(txtParmNmFilter);
			if(nmprd ==  null){
				nmprd="";
			}else{
				nmprd=" AND UPPER(A.PARMNM) LIKE '%"+ nmprd.toUpperCase() +"%' ";
			}
				
			
		try {
			listData = masterService
					.getDataMaster(
							"SELECT A.PARMID, A.PARMNM, B.PARMGRP  FROM CFG_PARM A LEFT OUTER JOIN CFG_PARM B ON B.PARMGRP IN (2001,2002,2003) "
									+ "AND B.PARMID=A.PARMID WHERE A.PARMGRP=1 "+ kdprd + nmprd,
							new Object[] {});
			listDto = listData;

			if (listData == null || listData.isEmpty()) {
				MessageBox.showInformation("Data Tidak Ditemukan");
				listDataSPPI.getItems().clear();
			} else {
				int x = 1;
				listDataSPPI.getItems().clear();
				for (DTOMap map : listData) {
					Listitem item = new Listitem();
					item.setAttribute("DATA", map);
					item.appendChild(new Listcell(String.valueOf(x++)));
					item.appendChild(new Listcell(map.getString("PARMID")
							+ " - " + map.getString("PARMNM")));
					item.appendChild(new Listcell(
							map.getInt("PARMGRP") == null ? ""
									: map.getInt("PARMGRP") == 2001 ? "Amortised Cost"
											: map.getInt("PARMGRP") == 2002 ? "FVOCI"
													: "FVPL"));
					listDataSPPI.appendChild(item);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// public void onEvent(Event event) {
	//
	// final String path =
	// target.getDesktop().getWebApp().getRealPath("/imgs/1.jpg");
	// final AImage image = new AImage(path);
	// final Image i = new Image();
	// i.setContent(image);
	// target.getFellow("result").appendChild(i);
	//
	// }
	public void doSave(final int parmGrp) {

		final DTOMap mapsys = (DTOMap) GlobalVariable
				.getInstance()
				.get("cfgsys");

		final DTOMap mapuser = (DTOMap) GlobalVariable
				.getInstance()
				.get("USER_MASTER");
		Messagebox.show("Apakah Anda Yakin simpan data ini .. ?", "KONFIRMASI",
				Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION,
				new EventListener<Event>() {
					@Override
					public void onEvent(Event e) throws Exception {
						if (Messagebox.ON_OK.equals(e.getName())) {
							DTOMap map = new DTOMap();
							String parmId = (String) ComponentUtil
									.getValue(txtParmId);
							String parmNm = (String) ComponentUtil
									.getValue(txtParmNm);
							final DTOMap key = (DTOMap) masterService
									.getMapMaster(
											"select PARMGRP, PARMID  from CFG_PARM where PARMID='"
													+ parmId
													+ "' AND PARMGRP IN(2001, 2002, 2003)",
											new Object[] {});
							if (key != null) {
								Messagebox
										.show("Data dengan kode "
												+ parmId
												+ " - "
												+ parmNm
												+ " \n sudah ada pada klasifikasi "+ (key.getInt("PARMGRP") == 2001 ? "Amortised Cost"
																				   : key.getInt("PARMGRP") == 2002 ? "FVOCI": "FVPL")
												+ "\n Anda yakin ingin mengubah data ini?",
												"KONFIRMASI", Messagebox.OK
														| Messagebox.CANCEL,
												Messagebox.QUESTION,
												new EventListener<Event>() {
													@Override
													public void onEvent(Event e)
															throws Exception {
														if (Messagebox.ON_OK.equals(e
																.getName())) {

															DTOMap delMap = new DTOMap();
															delMap.put(
																	"PARMGRP",
																	key.getInt("PARMGRP"));
															delMap.put(
																	"PARMID",
																	ComponentUtil
																			.getValue(txtParmId));
															System.out
																	.println(key
																			.getInt("PARMGRP"));
															System.out
																	.println(ComponentUtil
																			.getValue(txtParmId));
															delMap.put("PK",
																	"PARMGRP,PARMID");
															masterService
																	.deleteData(
																			delMap,
																			"CFG_PARM");

															DTOMap map = new DTOMap();
															System.out
																	.println("DELETE");
															map.put("PARMGRP",
																	parmGrp);
															map.put("PARMID",
																	ComponentUtil
																			.getValue(txtParmId));
															map.put("PK",
																	"PARMGRP,PARMID");
															masterService
																	.deleteData(
																			map,
																			"CFG_PARM");

															map.put("PARMGRP",
																	parmGrp);
															map.put("PARMID",
																	ComponentUtil
																			.getValue(txtParmId));
															map.put("PARMNM",
																	txtParmNm
																			.getValue());
															map.put("STATUS", 1);
															map.put("CRTDATE",
																	mapsys.getDate("OPEN_DATE"));
															map.put("CRTUSER",
																	mapuser.getString("USERID"));
															masterService
																	.insertData(
																			map,
																			"CFG_PARM");
															System.out
																	.println("BENAR !!");
															MessageBox.showInformation("Data Berhasil disimpan");
															doLoadSPPI();
															doReset();

														} else if (Messagebox.ON_CANCEL.equals(e
																.getName())) {

														}
													}
												});
							} else {

								map.put("PARMGRP", parmGrp);
								map.put("PARMID",
										ComponentUtil.getValue(txtParmId));
								map.put("PARMNM", txtParmNm.getValue());
								map.put("STATUS", 1);
								map.put("CRTDATE",
										mapsys.getDate("OPEN_DATE"));
								map.put("CRTUSER",
										mapuser.getString("USERID"));
								masterService.insertData(map, "CFG_PARM");
								System.out.println("BENAR !!");
								MessageBox.showInformation("Data Berhasil disimpan");
								doLoadSPPI();
								doReset();
							}

						} else if (Messagebox.ON_CANCEL.equals(e.getName())) {

						}

					}

				});

	}

	public void doImageload() {
		
		img04.setVisible(false);
		img05.setVisible(false);
		img06.setVisible(false);
		img07.setVisible(false);
		img08.setVisible(false);
		img09.setVisible(false);
		img10.setVisible(false);
		img11.setVisible(false);
		img12.setVisible(false);
		
		// ARROW IMAGE
		imgArrw01.setVisible(false);
		imgArrw02.setVisible(false);
		imgArrw03.setVisible(false);
		imgArrw04.setVisible(false);
		imgArrw05.setVisible(false);
		imgArrw06.setVisible(false);
		imgArrw07.setVisible(false);
		imgArrw08.setVisible(false);
		imgArrw09.setVisible(false);
		imgArrw10.setVisible(false);
		imgArrw11.setVisible(false);
		imgArrw12.setVisible(false);
		imgArrw13.setVisible(false);
		imgArrw14.setVisible(false);
		imgArrw15.setVisible(false);
	}
	// END

}
