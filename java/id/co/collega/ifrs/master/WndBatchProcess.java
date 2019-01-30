package id.co.collega.ifrs.master;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;

import com.jet.gand.services.GlobalVariable;

import id.co.collega.ifrs.common.Cryptograph;
import id.co.collega.ifrs.common.DTOMap;
import id.co.collega.ifrs.common.FunctionUtils;
import id.co.collega.ifrs.master.service.MasterServices;
import id.co.collega.ifrs.util.ComponentUtil;
import id.co.collega.ifrs.util.MessageBox;
import id.co.collega.v7.seed.config.AuthenticationService;

@org.springframework.stereotype.Component
@Scope("execution")
public class WndBatchProcess extends SelectorComposer<Component>{
	
	@Wire Textbox txtPassOld;
	@Wire Textbox txtPassNew;
	@Wire Textbox txtPassConf;
	@Wire Button btnYa;
	@Wire Button btnTidak;
	@Wire Label lblDdMmmmYyyy;
	
	@Wire Listbox listProses;
	
	@Autowired MasterServices masterService;
	@Autowired AuthenticationService authService;
	
	SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
	List<DTOMap> listProsesCutOff=new ArrayList<>();
	Boolean onLoad = false;
	@Wire DTOMap dataUser;
	
	
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		doLoadDataProcess();
		btnYa.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(Event event) throws Exception {
				doProses();
			}
		});
		
		btnTidak.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(Event event) throws Exception {
				doReset();
			}
		});
		listProses.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(Event event) throws Exception {
				valCheck();
			}
		});
		ComponentUtil.setValue(lblDdMmmmYyyy, sdf.format(((DTOMap)GlobalVariable.getInstance().get("cfgsys")).getDate("OPEN_DATE")));
	}
	
	private void valCheck() {
		if (listProses.getItemCount()>0) {
			for (int i = 0; i < listProses.getItemCount(); i++) {
				Listitem item=listProses.getItemAtIndex(i);
				if (item.isSelected()) {
					if (i>0) {
						autoCheckItem(i);
					}
				}
			}
		}

	}
	
	private void autoCheckItem(Integer seq) {
		for (int i = 0; i <= seq; i++) {
			Listitem item=listProses.getItemAtIndex(i);
			
			item.setSelected(true);
		}
	}

	private void doProses() {
		if (listProses.getItemCount() > 0) {
			boolean isSucces = true;
			DTOMap userMap=(DTOMap) GlobalVariable.getInstance().get("USER_MASTER");
			
			doCheckDataProses();

			if (listProsesCutOff.size() > 0) {
				for (int i = 0; i < listProses.getItemCount(); i++) {
					Listitem item= listProses.getItemAtIndex(i);
					DTOMap data = (DTOMap) item.getAttribute("DATA");
					if (checkProsesData(data, i)) {
						updateProses(data, "2");// sedang di proses
						data.put("PARMIDOTH", "2");
						if (!item.isDisabled()) {
							String proses = FunctionUtils.doCalculateEngineCutOff(data.getString("PARMID"),userMap.getString("USERID"),userMap.getString("PWD"));
							System.out.println("RESULT" + proses);
							if (!proses.equals("1")) {
								data.put("PARMIDOTH", "0");
								MessageBox.showError("Proses "+data.getString("PARMNM")+"gagal \n"+"("+proses+")");
								isSucces = false;
								break;
							} else {
								isSucces = true;
								data.put("PARMIDOTH", "1");
							}
						}
					}else{
						data.put("PARMIDOTH", "0");
						isSucces=false;break;
					}
					item.setAttribute("DATA", data);
				}
				if (isSucces) {
					MessageBox.showInformation("Proses berhasil.");
					doLoadDataProcess();
				} else {
					MessageBox.showInformation("Proses gagal.");
					doLoadDataProcess();
				}
				
			}else{
				MessageBox.showInformation("Tidak ada data yang diproses.");
			}
		} else {
			MessageBox.showInformation("Tidak ada data yang diproses.");
		}
	}
	
	private void doCheckDataProses() {
		for (Listitem item : listProses.getItems()) {
			if (item.isSelected()) {
				DTOMap data=(DTOMap) item.getAttribute("DATA");
				listProsesCutOff.add(data);
			}
		}
	}
	
	private boolean checkProsesData(DTOMap data,Integer seqData) {
		boolean isValid=true;
		if (isValid) {
			System.out.println("PARMID = "+data.getString("PARMID")+"DATA KE="+seqData);
			DTOMap dataProses=masterService.getMapMaster(" 	SELECT PARMIDOTH 		"
					+ "										FROM CFG_PARM 			"
					+ "										WHERE PARMGRP=? 		"
					+ "											AND PARMID=?		", 
											new Object[]{		data.getInt("PARMGRP"),
																data.getString("PARMID")});
			if (dataProses!=null) {
				System.out.println("PARMIOTH = "+dataProses.getString("PARMIDOTH"));
				if (dataProses.getString("PARMIDOTH").equals("1")) {
					MessageBox.showInformation("Proses "+dataProses.getString("PARMNM")+"\n Sudah Diproses...");
					isValid=false;
				}else if (dataProses.getString("PARMIDOTH").equals("2")) {
					MessageBox.showInformation("Proses "+dataProses.getString("PARMNM")+"\n Sedang dalam proses...");
					isValid=false;
				}else{
					isValid=true;
				}
			}else{
				isValid=false;
			}
		}
		return isValid;
	}
	
	private void updateProses(DTOMap data,String status) {
		DTOMap map=new DTOMap();
		map.put("PARMIDOTH", status);
		map.put("PARMGRP", data.getInt("PARMGRP"));
		map.put("PARMID", data.getString("PARMID"));
		map.put("PK", "PARMGRP,PARMID");
		masterService.updateData(map, "CFG_PARM");
	}

	private void doLoadDataProcess() {
		List<DTOMap> listProcess=(List<DTOMap>) masterService.getDataMaster(" SELECT * FROM CFG_PARM 		"
				+ "																WHERE PARMGRP=14 			"
				+ "																	AND STATUS=1 			"
				+ "																	AND LEFT(PARMID,1)='1' 	"
				+ "																ORDER BY CAST(PARMID AS INT) ",new Object[]{});
		listProses.getItems().clear();
		if (listProcess.size() > 0) {
			for (int i = 0; i < listProcess.size(); i++) {
				DTOMap dtoProcess=listProcess.get(i);
				Listitem li=new Listitem();
				li.setAttribute("DATA", dtoProcess);
				li.appendChild(new Listcell(dtoProcess.getString("PARMID")));
				li.appendChild(new Listcell(dtoProcess.getString("PARMNM")));
				li.appendChild(new Listcell(dtoProcess.getString("PARMIDOTH").equals("0") ?
											"Belum Proses": 
											dtoProcess.getString("PARMIDOTH").equals("1") ?
											"Sudah Proses":"Sedang Proses"));
				li.appendChild(new Listcell(dtoProcess.getString("VIEWORDNM")));
				if (dtoProcess.getString("PARMIDOTH").equals("1")) {
					li.setDisabled(true);
				}
				listProses.appendChild(li);
			}
		}
	}
	
	private void doReset(){
		listProsesCutOff.clear();
		
		doLoadDataProcess();
	}
	
	
}
