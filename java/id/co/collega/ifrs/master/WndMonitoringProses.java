package id.co.collega.ifrs.master;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.*;

import org.jfree.data.time.Millisecond;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;

import id.co.collega.v7.seed.controller.SelectorComposer;

import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Progressmeter;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.jet.gand.services.GlobalVariable;

import id.co.collega.ifrs.common.Cryptograph;
import id.co.collega.ifrs.common.DTOMap;
import id.co.collega.ifrs.common.FunctionUtils;
import id.co.collega.ifrs.master.service.MasterServices;
import id.co.collega.ifrs.util.ComponentUtil;
import id.co.collega.ifrs.util.MessageBox;
import id.co.collega.v7.seed.config.AuthenticationService;

@org.springframework.stereotype.Component
@Scope("desktop")
public class WndMonitoringProses extends SelectorComposer<Component> {

	@Wire	Window wnd;
	@Wire 	org.zkoss.zul.Timer timerProgress;
	@Wire	Combobox cmbProses;
	@Wire	Listbox listProses;
	@Wire	Listbox listDetailProses;
	@Wire	Button btnReset;
	@Wire	Progressmeter progressBar;
	@Wire	Label lblProgress;
	
	@Autowired
	MasterServices masterService;
	@Autowired
	AuthenticationService authService;

	@Wire	DTOMap dataUser;
	
	SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
	List<DTOMap> listProsesCutOff = new ArrayList<>();
	Boolean onLoad = false;

	
	String status = "2";
	String Aksi;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		progressBar.setVisible(true);
		
		cmbProses.addEventListener(Events.ON_SELECT, new EventListener<Event>() {
			public void onEvent(Event event) throws Exception {
				doLoadProses();
//				timerProgress.start();
			}
		});
		btnReset.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(Event event) throws Exception {
				doReset();
				doStopProgressBar();
			}
		});
		cmbProses.setSelectedIndex(0);
	
		timerProgress.start();
		
		timerProgress.addEventListener(Events.ON_TIMER, new EventListener<Event>() {
			public void onEvent(Event event) throws Exception {
				if(status.equals("2")){
					doProgressBar();					
				} else {
					doStopProgressBar();
				}	
			}
		});
	}

	private void doLoadProses() {		
		String proses = (String) ComponentUtil.getValue(cmbProses);
		if (proses!=null) {
			timerProgress.start();
			Aksi = "Melakukan Monitoring Proses "+proses;
			doLogAktfitas(Aksi);
			progressBar.setVisible(true);
			lblProgress.setVisible(true);
		}else{
			doStopProgressBar();
			throw new WrongValueException(cmbProses, "Jenis Proses tidak boleh kosong.");
		}
		
		if (listProses.getItemCount() > 0) {
			listProses.getItems().clear();
		}
		if (listDetailProses.getItemCount()>0) {
			listDetailProses.getItems().clear();
		}
		
		List<DTOMap> listDataProses=masterService.getDataMaster(" 	SELECT PARMID,PARMIDOTH AS STATUS,VIEWORDNM AS WAKTU "
				+ "													FROM	CFG_PARM			"
				+ "													WHERE PARMGRP=14 AND PARMID=?", 
														new Object[]{proses});
		
		if (listDataProses.size() > 0) {
			timerProgress.start();
			
			for (DTOMap dtoMap : listDataProses) {
				Listitem li = new Listitem();
				li.setAttribute("DATA", dtoMap);
				li.appendChild(new Listcell(dtoMap.getString("PARMID")));
				li.appendChild(new Listcell(dtoMap.getString("STATUS").equals("0") ? "Belum Proses":
					dtoMap.getString("STATUS").equals("1") ? "Sudah Proses":"Sedang Proses"));
				li.appendChild(new Listcell(dtoMap.getString("WAKTU")));
				listProses.appendChild(li);
				
				status = dtoMap.getString("STATUS");
			}
		}else{
			doStopProgressBar();
		}
		
		
		System.out.println("REFRESH TABLE");
		List<DTOMap> listDataDetailProses = masterService.getDataMaster(
				" 													SELECT	A.SUMBER||' - '||C.PARMNM AS PRODUK,	"
						+ "													A.MODUL AS PROSESID, 			"
						+ "													ATTR1 AS MULAI, 				"
						+ "													ATTR2 AS SELESAI,				"
						+ "													A.ATTR AS LAMA_PROSES,			"
						+ "													A.CREATED_DATE AS TGL_PROSES,	"
						+ "													A.CREATED_BY AS USERID			"
						+ "											FROM SYS_TRX_LOG A						"
						+ "													LEFT OUTER JOIN CFG_PARM B		"
						+ "														ON B.PARMGRP=14				"
						+ "													LEFT OUTER JOIN CFG_PARM C		"
						+ "														ON C.PARMGRP=1				"
						+ "															AND C.PARMID=A.SUMBER	"
						+ "											WHERE									"
						+ "												A.MODUL=B.PARMID					"
						+ "												AND LENGTH(A.SUMBER)<=4				"
						+ "												AND DATE(A.CREATED_DATE)=(	SELECT MAX(DATE(CREATED_DATE))	"
						+ "																			FROM SYS_TRX_LOG				"
						+ "																			WHERE MODUL=?)					"
						+ "												AND A.MODUL=?												"
						+ "											ORDER BY 3",
				new Object[] { proses,proses });
		if (listDataDetailProses.size() > 0) {
			timerProgress.start();	
			progressBar.setVisible(true);
			lblProgress.setVisible(true);
			
			int i=1;
			for (DTOMap dtoMap : listDataDetailProses) {
				Listitem li = new Listitem();
				li.setAttribute("DATA", dtoMap);
				li.appendChild(new Listcell(String.valueOf(i++)));
				li.appendChild(new Listcell(dtoMap.getString("PRODUK")));
				li.appendChild(new Listcell(dtoMap.getString("PROSESID")));
				li.appendChild(new Listcell(dtoMap.getString("MULAI")));
				li.appendChild(new Listcell(dtoMap.getString("SELESAI")));
				double menit=(new BigDecimal(dtoMap.getString("LAMA_PROSES")).doubleValue()/60);
				li.appendChild(new Listcell(FunctionUtils.moneyToText(new BigDecimal(menit).setScale(2, RoundingMode.HALF_UP))));
				li.appendChild(
						new Listcell(new SimpleDateFormat("dd-MM-yyyy").format(dtoMap.getDate("TGL_PROSES"))));
				li.appendChild(new Listcell(dtoMap.getString("USERID")));
				listDetailProses.appendChild(li);
				listDetailProses.setSelectedItem(li);			
			}
			System.out.println("Progress bar : Load data" +" /" + System.currentTimeMillis());
		}else{
			doStopProgressBar();
		}
	}


	
	private void doProgressBar(){
		if(progressBar.getValue() >= 100) {
			progressBar.setValue(0);
			timerProgress.stop();
			doLoadProses();
		} else { 
			progressBar.setValue(progressBar.getValue() + 10);
		} 		
	}
	
	private void doStopProgressBar(){
		timerProgress.stop();
		
		progressBar.setVisible(false);
		progressBar.setValue(0);

		lblProgress.setVisible(false);
	}
	
	private void doReset() {
		cmbProses.setSelectedIndex(-1);
		if (listProses.getItemCount() > 0) {
			listProses.getItems().clear();
		}
		if (listDetailProses.getItemCount() > 0) {
			listDetailProses.getItems().clear();
		}
		timerProgress.stop();
	}

}
