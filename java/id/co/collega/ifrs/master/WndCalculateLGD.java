package id.co.collega.ifrs.master;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Intbox;
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
@Scope("desktop")
public class WndCalculateLGD extends SelectorComposer<Component>{
	
	@Wire Datebox txtPeriode;
	@Wire Intbox intPeriode;
	@Wire Datebox txtTglMulai;
	@Wire Datebox txtTglAkhir;
	@Wire Button btnProses;
	@Wire Button btnReset;
	
	@Autowired MasterServices masterService;
	@Autowired AuthenticationService authService;
	
	SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
	
	Boolean onLoad = false;
	@Wire DTOMap dataUser;
	
	
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		txtPeriode.addEventListener(Events.ON_BLUR, new EventListener<Event>() {
			public void onEvent(Event event) throws Exception {
				doCalcPeriode();
			}
		});
		
		intPeriode.addEventListener(Events.ON_BLUR, new EventListener<Event>() {
			public void onEvent(Event event) throws Exception {
				doCalcPeriode();
			}
		});
		
		btnProses.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(Event event) throws Exception {
				if (validation()) {
					updateProses("3001", "2");// sedang di proses
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								DTOMap userMap=(DTOMap) GlobalVariable.getInstance().get("USER_MASTER");
								DTOMap map=new DTOMap();
								map.put("date",new SimpleDateFormat("yyyy-MM-dd").format((Date) ComponentUtil.getValue(txtPeriode)));
								map.put("rangeyear", (Integer) ComponentUtil.getValue(intPeriode));
								// TODO Auto-generated method stub
								FunctionUtils.doCalculateEngineLGD("3001",
																map.getString("date"),
																map.getInt("rangeyear"),
																userMap.getString("USERID"),
																userMap.getString("PWD"));
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								updateProses("3001", "0");
								e.printStackTrace();
							}
						}
					}).start();
					MessageBox.showInformation("Data berhasil diproses.");
				}
			}
		});
	}
	
	private void updateProses(String kdProses,String status) {
		DTOMap map=new DTOMap();
		map.put("PARMIDOTH", status);
		map.put("PARMGRP",14);
		map.put("PARMID", kdProses);
		map.put("PK", "PARMGRP,PARMID");
		masterService.updateData(map, "CFG_PARM");
	}
	
	private void doCalcPeriode() {
		Date tglPeriode=(Date)ComponentUtil.getValue(txtPeriode);
		if (tglPeriode==null) {
			throw new WrongValueException(txtPeriode, "Tgl. Periode Harus diiisi.");
		}
		Integer jmlPeriode=(Integer)ComponentUtil.getValue(intPeriode);
		if (jmlPeriode==null) {
			throw new WrongValueException(intPeriode, "Umur Data Harus diiisi.");
		}
		
		Date periode=(Date)ComponentUtil.getValue(txtPeriode);
		Calendar cld=Calendar.getInstance();
		cld.setTime(periode);
//		cld.set(Calendar.MONTH,-48); //mundur 5thn
		ComponentUtil.setValue(txtTglMulai,cld.getTime());
		cld.set(Calendar.YEAR, cld.get(Calendar.YEAR)-jmlPeriode);
		
		ComponentUtil.setValue(txtTglAkhir,cld.getTime());
	}
	
	protected boolean validation() {
		boolean isValid=true;
		if (isValid) {
			Date tglPeriode=(Date)ComponentUtil.getValue(txtPeriode);
			if (tglPeriode==null) {
				isValid=false;
				throw new WrongValueException(txtPeriode, "Tgl. Periode Harus diiisi.");
			}
			Integer jmlPeriode=(Integer)ComponentUtil.getValue(intPeriode);
			if (jmlPeriode==null) {
				isValid=false;
				throw new WrongValueException(intPeriode, "Umur Data Harus diiisi.");
			}
			DTOMap data=new DTOMap();
			data.put("PARMGRP",14);
			data.put("PARMID","3001");
			if (!checkProsesData(data)) {
				isValid=false;
			}
		}
		return isValid;
	}
	
	private boolean checkProsesData(DTOMap data) {
		boolean isValid=true;
		if (isValid) {
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
	
	private void doReset(){
		ComponentUtil.setValue(txtPeriode, null);
		ComponentUtil.setValue(txtTglAkhir, null);
		ComponentUtil.setValue(txtTglMulai, null);
	}
	
	
}
