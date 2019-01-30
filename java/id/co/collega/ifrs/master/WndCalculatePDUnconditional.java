package id.co.collega.ifrs.master;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.validation.Validation;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import com.jet.gand.services.GlobalVariable;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.urlconnection.HTTPSProperties;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import id.co.collega.ifrs.common.DTOMap;
import id.co.collega.ifrs.common.FunctionUtils;
import id.co.collega.ifrs.master.service.MasterServices;
import id.co.collega.ifrs.util.ComponentUtil;
import id.co.collega.ifrs.util.MessageBox;
import id.co.collega.v7.seed.config.AuthenticationService;

@org.springframework.stereotype.Component
@Scope("desktop")
public class WndCalculatePDUnconditional extends SelectorComposer<Component> {

	private static final Logger LOGGER = LoggerFactory.getLogger(WndCalculatePDUnconditional.class);
	
	@Wire
	Datebox txtPeriode;
	@Wire
	Datebox txtTglMulai;
	@Wire
	Datebox txtTglAkhir;
	@Wire
	Intbox intHorizon;
	@Wire Intbox intPeriode;

	@Wire
	Button btnProses;
	@Wire
	Button btnReset;

	@Autowired
	MasterServices masterService;
	@Autowired
	AuthenticationService authService;

	SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

	Boolean onLoad = false;
	@Wire
	DTOMap dataUser;

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		txtPeriode.addEventListener(Events.ON_BLUR, new EventListener <Event>() {
			public void onEvent(Event event) throws Exception {
				doCalcPeriode();
			}
		});
		
		intPeriode.addEventListener(Events.ON_BLUR, new EventListener <Event>() {
			public void onEvent(Event event) throws Exception {
				doCalcPeriode();
			}
		});
		
		btnProses.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(Event event) throws Exception {
				if (validation()) {
					updateProses("2001", "2");
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								DTOMap userMap=(DTOMap) GlobalVariable.getInstance().get("USER_MASTER");
								DTOMap map=new DTOMap();
								map.put("date",new SimpleDateFormat("yyyy-MM-dd").format((Date) ComponentUtil.getValue(txtPeriode)));
								map.put("rangeyear", (Integer) ComponentUtil.getValue(intPeriode));
								map.put("horizon",((Integer)ComponentUtil.getValue(intHorizon)));
								// TODO Auto-generated method stub
								FunctionUtils.doCalculateEnginePDUnconditional("2001",
																map.getString("date"),
																map.getInt("rangeyear"),
																map.getInt("horizon"),
																userMap.getString("USERID"),
																userMap.getString("PWD"));
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								updateProses("2001", "0");
								e.printStackTrace();
							}
						}
					}).start();
					MessageBox.showInformation("Data berhasil diproses.");
				}
			}
		});
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
		
		Calendar cld = Calendar.getInstance();
		cld.setTime(tglPeriode);
//		cld.set(Calendar.MONTH, -48); // mundur 5thn
		ComponentUtil.setValue(txtTglMulai, cld.getTime());
		cld.set(Calendar.YEAR, cld.get(Calendar.YEAR)-jmlPeriode);
		ComponentUtil.setValue(txtTglAkhir, cld.getTime());
	}
	
	private void updateProses(String kdProses,String status) {
		DTOMap map=new DTOMap();
		map.put("PARMIDOTH", status);
		map.put("PARMGRP",14);
		map.put("PARMID", kdProses);
		map.put("PK", "PARMGRP,PARMID");
		masterService.updateData(map, "CFG_PARM");
	}
	
	protected boolean validation() {
		boolean isValid=true;
		if (isValid) {
			Date tglPeriode=(Date)ComponentUtil.getValue(txtPeriode);
			if (tglPeriode==null) {
				isValid=false;
				throw new WrongValueException(txtPeriode, "Tgl. Periode Harus diiisi.");
			}
			
			Integer horizon=(Integer)ComponentUtil.getValue(intHorizon);
			if (horizon==null) {
				isValid=false;
				throw new WrongValueException(intHorizon, "Time Horizon Harus diiisi.");
			}else{
				if (horizon<=0) {
					isValid=false;
					throw new WrongValueException(intHorizon, "Time Horizon tidak boleh 0 atau minus.");
				}
			}
			Integer periode=(Integer)ComponentUtil.getValue(intPeriode);
			if (periode==null) {
				isValid=false;
				throw new WrongValueException(intPeriode, "Umur Data Harus diiisi.");
			}else{
				if (periode<=0) {
					isValid=false;
					throw new WrongValueException(intPeriode, "Umur Data tidak boleh 0 atau minus.");
				}
			}
		}
		return isValid;
	}

	private void doReset() {
		ComponentUtil.setValue(txtPeriode, null);
		ComponentUtil.setValue(txtTglAkhir, null);
		ComponentUtil.setValue(txtTglMulai, null);
	}

}
