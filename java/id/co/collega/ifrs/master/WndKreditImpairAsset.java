package id.co.collega.ifrs.master;

import id.co.collega.ifrs.common.DTOMap;
import id.co.collega.ifrs.common.FunctionUtils;
import id.co.collega.ifrs.common.JadwalAngsur;
import id.co.collega.ifrs.master.service.MasterServices;
import id.co.collega.ifrs.util.ComponentUtil;
import id.co.collega.ifrs.util.MessageBox;
import id.co.collega.v7.seed.config.AuthenticationService;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.UploadEvent;

import id.co.collega.v7.seed.controller.SelectorComposer;

import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.jet.gand.services.GlobalVariable;

@org.springframework.stereotype.Component
@Scope("desktop")
public class WndKreditImpairAsset extends SelectorComposer<Component>{
	@Autowired
	AuthenticationService auth;
	@Autowired
	MasterServices masterService;

	@Wire Window wnd;
	
	@Wire Checkbox chkKapitalisirBunga;
	@Wire Checkbox chkKapitalisirDenda;
	
	@Wire Button btnUploadJadwal;
	@Wire Button btnGenerateJadwal;
	
	@Wire
	Textbox txtNoRekening;
	@Wire Datebox	txtTglAkhir;
	@Wire Datebox	txtTglMulai;
	@Wire Datebox	txtTglAkhirBaru;
	@Wire Datebox	txtTglMulaiBaru;
	@Wire Datebox	txtTglPosisi;
	@Wire Intbox 	intTenor;
	@Wire Intbox 	intTenorBaru;
	@Wire Intbox	txtPeriode;
	@Wire Intbox 	intDPD;
	
	@Wire Radiogroup	radPokok;
	@Wire Radiogroup	radBunga;
	@Wire Radiogroup	rdJadwalAngsur;
	
	@Wire Combobox		cmbJnsBunga;
	@Wire Combobox		cmbRating;
	@Wire Combobox		cmbJnsBungaBaru;
	@Wire Combobox		cmbStage;
	@Wire Radiogroup	STS_CIA;
	
	@Wire Tab			tabArusKas;
	@Wire Tab			tabJadwalAngsur;
	
	@Wire Decimalbox	decPlafon;
	@Wire Decimalbox 	decNilaiWajarLama;
	@Wire Decimalbox 	decNilaiWajarBaru;
	@Wire Decimalbox	decBakiDebet;
	@Wire Decimalbox	decImpair;
	@Wire Decimalbox	decBunga;
	@Wire Decimalbox	decBungaBaru;
	@Wire Decimalbox	decIrrLama;
	@Wire Decimalbox	decIrrBaru;
	@Wire Decimalbox	decEIR;
	@Wire Decimalbox	decEIRBaru;
	@Wire Decimalbox	decTotBunga;
	@Wire Decimalbox	decTotPokok;
	@Wire Decimalbox	decTotJmlAngsur;
	
	@Wire Decimalbox	decTunggPokok;
	@Wire Decimalbox	decKeringananPokok;
	@Wire Decimalbox	decSisaTunggPokok;
	
	@Wire Decimalbox	decTunggBunga;
	@Wire Decimalbox	decKeringananBunga;
	@Wire Decimalbox	decSisaTunggBunga;
	
	@Wire Decimalbox	decTunggDenda;
	@Wire Decimalbox	decKeringananDenda;
	@Wire Decimalbox	decSisaTunggDenda;
	
	@Wire Label	decArus1Lama;
	@Wire Label	decArus2Lama;
	@Wire Label	decArus3Lama;
	@Wire Label	decArus4Lama;
	@Wire Label	decArus5Lama;
	@Wire Label	decArus6Lama;
	@Wire Label	decArus7Lama;
	@Wire Label	decArus8Lama;
	@Wire Label	decArus9Lama;

	@Wire Label	decArus1Baru;
	@Wire Label	decArus2Baru;
	@Wire Label	decArus3Baru;
	@Wire Label	decArus4Baru;
	@Wire Label	decArus5Baru;
	@Wire Label	decArus6Baru;
	@Wire Label	decArus7Baru;
	@Wire Label	decArus8Baru;
	@Wire Label	decArus9Baru;
	
	@Wire Listbox listArusKasLama;
	@Wire Listbox listJadwalAngsur;
	@Wire Listbox listArusKasBaru;
	
	public JadwalAngsur jadwalAngsur=new JadwalAngsur();
	private List<DTOMap> listAngsuran =new ArrayList<DTOMap>(0);
	private List listAngsuranUpload ;
	private List listErr;
	private List listAllRow=new ArrayList();
	private List<Date> listBulanTahun=new ArrayList();
	private DTOMap tipeBungaBaru = new DTOMap();
	
	String user, pass, keterangan, returnValue="gagal" ;
	private double irr = 0;
	private double irrBaru = 0;
	private double estimasiArusKasTot=0;
	private double bungaKonversiTot=0;
	private double nilaiKini=0;
	private double angsuranPokokTot=0;
	private double angsuranBungaTot=0;
	private double selisihBungaKontraktualTot=0;
	private BigDecimal SBE=BigDecimal.ZERO;
	private double totPokok=0;
	private double totBunga=0;
	private double totAngsur=0;

	SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
	DecimalFormat dfGlobal = new DecimalFormat("#,##0.00");
	
	private double estimasiArusKasBaru=0;
	private double bungaKonversiBaru=0;
	private double nilaiKiniArusKasBaru=0;
	private double bungaDiscountTotBaru=0;
	private double amortisasiBaru=0;
	private double bungaAtribusiBaru=0;
	private double selisihBungaKontraktualBaru=0;
	private BigDecimal saldoMustNol;
	DTOMap cfg_sys=(DTOMap) GlobalVariable.getInstance().get("cfgsys");
	private Date dateTglMulaiBaruOLD = null;
	boolean isUpload=false;

	String Aksi;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void doAfterCompose(Component comp) throws Exception  {
		super.doAfterCompose(comp);
		loadRating();
		loadJnsBunga();
		
		cmbJnsBungaBaru.addEventListener(Events.ON_SELECT, new EventListener<Event>() {
			public void onEvent(Event e) throws Exception {
				String typeInt = (String) ComponentUtil.getValue(cmbJnsBungaBaru);
				if(typeInt != null){
					setDetailTipeBunga(typeInt);
				}
			}
		});

		intTenorBaru.addEventListener(Events.ON_BLUR, new EventListener() {
			public void onEvent(Event event) throws Exception {
				if(!(intTenorBaru.getValue() == null)){
					if(intTenorBaru.getValue() > 3){
						throw new WrongValueException(intTenorBaru, "Jangka Waktu tidak boleh lebih dari 3 Bulan");											
					}
				}
			}
		});
		
		txtNoRekening.addEventListener(Events.ON_OK,new EventListener<Event>(){
			public void onEvent(Event e)throws Exception{
				String noRek=(String) ComponentUtil.getValue(txtNoRekening);
				if (noRek!=null) {
					doLoadDataRek(noRek);;
				}
			}
		});
		
		decKeringananPokok.addEventListener(Events.ON_CHANGE,new EventListener<Event>(){
			public void onEvent(Event e)throws Exception{
				calcKeringanan();
			}
		});
		
		decKeringananBunga.addEventListener(Events.ON_CHANGE,new EventListener<Event>(){
			public void onEvent(Event e)throws Exception{
				calcKeringanan();
			}
		});
		
		decKeringananDenda.addEventListener(Events.ON_CHANGE,new EventListener<Event>(){
			public void onEvent(Event e)throws Exception{
				calcKeringanan();
			}
		});
		
		chkKapitalisirBunga.addEventListener(Events.ON_CHECK, new EventListener() {
			public void onEvent(Event e) throws Exception {
				doChangeBakiDebet();
			}
		});
		
		chkKapitalisirDenda.addEventListener(Events.ON_CHECK, new EventListener() {
			public void onEvent(Event e) throws Exception {
				doChangeBakiDebet();
			}
		});
		
		tabJadwalAngsur.addEventListener(Events.ON_CLICK, new EventListener() {
			public void onEvent(Event e) throws Exception {
				Integer isjadwalAngsur=(Integer) ComponentUtil.getValue(rdJadwalAngsur);
				if (isjadwalAngsur.equals(1)) {
					doGenerateArusKasBaru();
					btnUploadJadwal.setDisabled(true);
				} else {
					btnUploadJadwal.setDisabled(false);
					if (listAngsuranUpload.size()==0) {
						resetArusKasBaru();
					}
				}
			}
		});
		
		rdJadwalAngsur.addEventListener(Events.ON_CLICK, new EventListener() {
			public void onEvent(Event e) throws Exception {
				Integer isjadwalAngsur=(Integer) ComponentUtil.getValue(rdJadwalAngsur);
				if (isjadwalAngsur.equals(1)) {
					doGenerateArusKasBaru();
					btnUploadJadwal.setDisabled(true);
				} else {
					btnUploadJadwal.setDisabled(false);
					resetArusKasBaru();
				}
			}
		});
		
		txtTglMulaiBaru.addEventListener("onBlur", new EventListener() {
			public void onEvent(Event e) throws Exception {
				Date tglMulai = (Date) ComponentUtil.getValue(txtTglMulaiBaru);
				Date opendate = (Date) cfg_sys.getDate("OPEN_DATE");
				Integer tenor = (Integer) ComponentUtil.getValue(intTenorBaru);
				if (tglMulai != null && tenor != null) {
				    if(tenor > 999)
				        throw new WrongValueException(intTenorBaru, "Nilai maksimal 999");
					if(tglMulai.compareTo(opendate) < 0)
				        throw new WrongValueException(txtTglMulaiBaru, "Tanggal mulai minimal hari ini "+
				                                  new SimpleDateFormat("dd/MM/yyyy").format(opendate));
				    Calendar cal = Calendar.getInstance();
					cal.setTime(tglMulai);
					cal.add(Calendar.MONTH, tenor);
					txtTglAkhirBaru.setValue(cal.getTime());
					//PERUBAHAN TANGGAL MULAI
				    if(dateTglMulaiBaruOLD != null){
				        if(dateTglMulaiBaruOLD.compareTo(tglMulai) != 0){
    				        if(MessageBox.showConfirm("Anda merubah tanggal mulai, ini akan mengenerate otomatis ulang jadwal angsur!\nKlik cancel untuk membatalkan perubahan.")){
    				            dateTglMulaiBaruOLD = tglMulai;
    				            doGenerateArusKasBaru();
    				        }else{
    				            txtTglMulaiBaru.setValue(dateTglMulaiBaruOLD);
    				            cal.setTime(dateTglMulaiBaruOLD);
            					cal.add(Calendar.MONTH, tenor);
            					txtTglAkhirBaru.setValue(cal.getTime());
    				        }
				        }
				    }else{
					    dateTglMulaiBaruOLD = tglMulai;
				    }
				} else {
					txtTglAkhirBaru.setValue(null);
				}
			}
		});
		
		decBungaBaru.addEventListener(Events.ON_BLUR, new EventListener() {
			public void onEvent(Event e) throws Exception {
				BigDecimal bunga=(BigDecimal) ComponentUtil.getValue(decBungaBaru);
				if (bunga!=null) {
					decEIRBaru.setValue(new BigDecimal(bunga.doubleValue()/12).setScale(8, RoundingMode.HALF_UP));
				}
			}
		});
		
		btnUploadJadwal.setDisabled(true);
		btnUploadJadwal.setUpload("true");
		btnUploadJadwal.addEventListener(Events.ON_UPLOAD, new EventListener() {
            public void onEvent(Event e) throws Exception {
                UploadEvent event = (UploadEvent) e;
                Media media = event.getMedia();
                String[] a = media.getName().split("\\.");
                String ext = a[a.length - 1];
                if (ext.equals("xls")) {
                    doReadFile(media.getStreamData());
        			Aksi = "Upload jadwal angsur No Rekening"+ ComponentUtil.getValue(txtNoRekening);
        			doLogAktfitas(Aksi);
                } else {
                    MessageBox.showInformation("File excel harus dalam format Microsoft Office 2003");
                }
            }
        });
	}
	
	protected void resetArusKasBaru() {
		if (listJadwalAngsur.getItemCount() > 0) {
			listJadwalAngsur.getItems().clear();
		}
		if (listArusKasBaru.getItemCount() > 0) {
			listArusKasBaru.getItems().clear();
		}
		
		ComponentUtil.setValue(decArus1Baru, "");
		ComponentUtil.setValue(decArus2Baru, "");
		ComponentUtil.setValue(decArus3Baru, "");
		ComponentUtil.setValue(decArus4Baru, "");
		ComponentUtil.setValue(decArus5Baru, "");
		ComponentUtil.setValue(decArus6Baru, "");
		ComponentUtil.setValue(decArus7Baru, "");
		
		decTotPokok.setValue(BigDecimal.ZERO);
		decTotBunga.setValue(BigDecimal.ZERO);
		decTotJmlAngsur.setValue(BigDecimal.ZERO);

		decNilaiWajarBaru.setValue(BigDecimal.ZERO);
		decImpair.setValue(BigDecimal.ZERO);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void doReadFile(InputStream fileName) {
		if(!validation()){			
			return;
		}
		 
		listAngsuranUpload = new ArrayList<>();
		 Date dateTglMulaiNew = (Date) ComponentUtil.getValue(txtTglMulaiBaru);
		 Date dateTglJthTempoNew = (Date) ComponentUtil.getValue(txtTglAkhirBaru);
		 Integer intJangkaWktNew = (Integer) ComponentUtil.getValue(intTenorBaru);
		 Integer lastintPeriode;
		 BigDecimal txtMaxkredit =  (BigDecimal) ComponentUtil.getValue(decBakiDebet);
		 Integer intPeriode = (Integer) ComponentUtil.getValue(txtPeriode);
		 try{	        				 
	            listErr=new ArrayList<>();
	            listAllRow.clear();
	            
	            Vector dataHolder;
	            DateFormat formatter ;
	            HSSFCell myCell,myCell_first,myCell_last,tanggalCell,AngsurPokokCell,AngsurBungaCell ;
	            String ls_error="";
	            boolean    lb_imported;
	            String bulan_ke="";	            	            
	            Date tanggal=new Date(), bulanTahun=new Date();	       
	            BigDecimal TotaljumlahPokok = BigDecimal.ZERO;
	            BigDecimal TotaljumlahBunga = BigDecimal.ZERO;
	            BigDecimal TotaljumlahAngsur = BigDecimal.ZERO;
	            BigDecimal TotaljumlahSaldo = BigDecimal.ZERO;
	            BigDecimal saldo = BigDecimal.ZERO;
				BigDecimal angsuran_pokok,angsuran_bunga,jumlahAngsur;
				Integer bln_ke=0;
	            
	            formatter = new SimpleDateFormat("yyyy-MM-dd");
	            dataHolder = FunctionUtils.ReadCSV(fileName);	            
	            if (listJadwalAngsur.getItemCount() > 0) {
					listJadwalAngsur.getItems().clear();
				}
	            int x = listJadwalAngsur.getItemCount();
	            
	            Vector cellStoreVector = (Vector) dataHolder.elementAt(1); //baris ke 0
	            myCell_first = (HSSFCell) cellStoreVector.elementAt(0); //kolom ke 0
	            Vector cellStoreVector_last = (Vector) dataHolder.lastElement(); 
	            myCell_last = (HSSFCell) cellStoreVector_last.elementAt(0); //kolom ke 1	            
	            Integer jumlah =dataHolder.size()-1;	            
	            fileName.close();
	            
	            int nomor = 1;
	            
	            DTOMap map = new DTOMap();
	    		
	    		map.put("jadwalAngsurKe", 0);
	    		map.put("tglJadwal", dateTglMulaiNew);
	    		map.put("noArus", map.get("jadwalAngsurKe"));
	    		map.put("tglArus", map.get("tglJadwal"));
	    		map.put("angsuranPokok", new BigDecimal(0));
	    		map.put("angsuranBunga", new BigDecimal(0));
	    		map.put("totalAngsuran", new BigDecimal(0));
	    		map.put("saldoTeoritis", txtMaxkredit);
	    		listAngsuranUpload.add(map);
	    		
                listBulanTahun.clear();
                listBulanTahun.add(dateTglMulaiNew);
	            for (int i = 1; i <= jumlah; i++) {
	            	 
	            	 cellStoreVector = (Vector) dataHolder.elementAt(i);	            	 
	                 Listitem item=new Listitem();
	            	
	                 myCell = (HSSFCell) cellStoreVector.elementAt(0);
	                 if (myCell.toString()==null || myCell.toString().equals("")) {
	                	 bulan_ke = null;	                	 
	                 }else{
	                	 bulan_ke = myCell.toString();
	                	 bulan_ke = bulan_ke.replace(".0", "").replace(".00", "");
	                	 bln_ke = Integer.parseInt(bulan_ke);
	                	 if(i == jumlah && jumlah != intJangkaWktNew){
	                		MessageBox.show("Jumlah Bulan Angsur harus sama dengan jangka waktu", "Error pada baris ke : "+i, 1, "1");	                		
	                		doClear();
		                	return;		                	
	                	 }
	                	
	                 }
	                 if (bulan_ke == null || bulan_ke.equals("") || bulan_ke.length() == 0 ){
	                     ls_error = "Bulan harus diisi";
	                     MessageBox.show(ls_error, "Error pada baris ke : "+i, 1, "1");
	                     doClear();
	                     return;	                                         
	                 }             	                 

	                 tanggalCell = (HSSFCell) cellStoreVector.elementAt(1);	                 
	                 if (tanggalCell.toString()==null || tanggalCell.toString().equals("")) {
	                	 tanggalCell = null;
	                 }else{	                	 	                	 
	                	 String tgl = sdf.format((Date) tanggalCell.getDateCellValue());	 
	                	 tanggal =  formatter.parse(tgl);	 	                	 
	                	if(!tanggal.after(dateTglMulaiNew)) {	                		
	                		MessageBox.show("Tanggal bulan ke "+i+", harus diatas Tanggal Mulai", "Error pada baris ke : "+i, 1, "1");
	                		doClear();
	                		return;
	                	}
	                	if(i == jumlah && !tanggal.equals(dateTglJthTempoNew)){
	                		MessageBox.show("Tanggal bulan ke "+i+", harus sama dengan Tanggal Jatuh tempo", "Error pada baris ke : "+i, 1, "1");
	                		doClear();
	                		return;
	                	}
	                 }
	                 if (tanggal == null || tanggal.equals("") ){
	                     ls_error = "tanggal harus diisi";
	                     MessageBox.show(ls_error, "Error pada baris ke : "+i, 1, "1");
	                     listErr.add(1);
	                     return;
	                 }	
	                 
	                 AngsurPokokCell = (HSSFCell) cellStoreVector.elementAt(2);
	                 if (AngsurPokokCell.toString()==null || AngsurPokokCell.toString().equals("") || AngsurPokokCell.toString().equals("-")) {
	                	 angsuran_pokok = BigDecimal.ZERO;	                	 
	                 }else{
	                	 String ap = AngsurPokokCell.toString();
	                	 angsuran_pokok = new BigDecimal(ap);	                	 
	                 }
	                 
	                 AngsurBungaCell = (HSSFCell) cellStoreVector.elementAt(3);
	                 if (AngsurBungaCell.toString()==null || AngsurBungaCell.toString().equals("") || AngsurBungaCell.toString().equals("-")) {
	                	 angsuran_bunga = BigDecimal.ZERO;	                	 
	                 }else{
	                	 String ab = AngsurBungaCell.toString();
	                	 angsuran_bunga = new BigDecimal(ab);	                	 
	                 }	                
	                 
	                 jumlahAngsur = angsuran_pokok.add(angsuran_bunga); 
	                 
	                 if(i==1){
	                	 saldo = txtMaxkredit.subtract(angsuran_pokok);
	                 }else{
	                	 saldo = saldo.subtract(angsuran_pokok);
	                 }
	                
	                 if(intPeriode>1){	               
//	                	 MessageBox.showInformation("bln_ke "+bln_ke +" intPeriode "+intPeriode);
	                	 if(bln_ke.equals(intPeriode)){	                		 
	                		 if(angsuran_pokok.doubleValue()==0 || angsuran_bunga.doubleValue()==0){
	                			 MessageBox.show("Nilai Angsur tidak boleh kosong", "Error pada baris ke : "+i, 1, "1");		                		 
			                		doClear();
			                		return;
	                		 }else{
	                			 lastintPeriode = intPeriode;
	                			 intPeriode = intPeriode+intPeriode;	                			  
		                		 if(intPeriode > jumlah){
		                			 intPeriode =lastintPeriode;
		                		 } 
	                		 }
	                	 }else{	                		 
	                		 if(!bln_ke.equals(jumlah)){
	                			 if(angsuran_pokok.doubleValue()!=0 || angsuran_bunga.doubleValue()!=0){
		                			 MessageBox.show("Periode Pembayaran tidak sesuai, nilai angsur selain periode pembayaran harus 0", "Error pada baris ke : "+i, 1, "1");
				                		doClear();
				                		return;
		                		 }	                			 
	                		 }else{
	                			 if(angsuran_pokok.doubleValue()!=0){
	                				 MessageBox.show("Periode Pembayaran tidak sesuai, nilai angsur selain periode pembayaran harus 0", "Error pada baris ke : "+i, 1, "1");
				                		doClear();
				                		return;
	                			 }	                			 
	                		 }
	                	 }
	                }
	                 
	                map = new DTOMap();
	     			map.put("jadwalAngsurKe", bln_ke);
	     			map.put("tglJadwal", tanggal);
	     			map.put("noArus", map.get("jadwalAngsurKe"));
	     			map.put("tglArus", map.get("tglJadwal"));
	     			map.put("angsuranPokok", angsuran_pokok);
	     			map.put("angsuranBunga", angsuran_bunga);
	     			map.put("totalAngsuran", jumlahAngsur);
	     			map.put("saldoTeoritis", saldo);	     			
	     			listAngsuranUpload.add(map);
	     			
	                  TotaljumlahPokok = TotaljumlahPokok.add(angsuran_pokok);
	                  TotaljumlahBunga = TotaljumlahBunga.add(angsuran_bunga);	                  
	                  TotaljumlahAngsur = TotaljumlahAngsur.add(jumlahAngsur);
	                  TotaljumlahSaldo = TotaljumlahSaldo.add(saldo);
	                  
	                 for (Date tanggal_sebelumnya : listBulanTahun) {
	 	            	Calendar cal_sblm = Calendar.getInstance();
	 	            	cal_sblm.setTime(tanggal_sebelumnya);
	 	                int year_sblm = cal_sblm.get(Calendar.YEAR);
	 	                int month_sblm = cal_sblm.get(Calendar.MONTH);
	 	                
	 	                Calendar cal_now = Calendar.getInstance();
	 	            	cal_now.setTime(tanggal);
	 	                int year_now = cal_now.get(Calendar.YEAR);
	 	                int month_now = cal_now.get(Calendar.MONTH);	 	                
	 	                if(year_sblm == year_now && month_sblm == month_now){
	 	                	MessageBox.show("Terdapat bulan dan tahun yang sama", "Error pada bulan ke : "+i, 1, "1");
	 	                	listErr.add(1);
	 	                }	            		 	                
	 	            }	                
	                listBulanTahun.add(tanggal);	                
	            }
	            if(TotaljumlahPokok.doubleValue() != txtMaxkredit.doubleValue()){
	            	MessageBox.showError("Total Angsur Pokok Harus sama dengan Maksimum Kredit");
	            	listErr.add(1);	            	
	            }	            	
	            System.out.println("jml salah :"+listErr.size());
	            if(listErr.size()>0){ 	
	            	doClear();
	            }
	            else{
	            	doGenerateArusKasUpload(listAngsuranUpload);
	            }
	    			            
	     } catch (Exception e){
	            throw new RuntimeException(e);
	     }
	 }
	
	public void doClear(){
		 if (listJadwalAngsur.getItemCount() > 0) {
			listJadwalAngsur.getItems().clear();
		}
    	decTotPokok.setValue(BigDecimal.ZERO);
        decTotBunga.setValue(BigDecimal.ZERO);
        decTotJmlAngsur.setValue(BigDecimal.ZERO);
	}
	
	public void doGenerateArusKasUpload(List angsurUpload) {
		Date tglMulai = (Date) ComponentUtil.getValue(txtTglMulaiBaru);
		int jangkaWaktu = (Integer) ComponentUtil.getValue(intTenorBaru);
		int periodePokok = jangkaWaktu;
			periodePokok = (Integer) ComponentUtil.getValue(txtPeriode);
		int periodeBunga = jangkaWaktu;
			periodeBunga = (Integer) ComponentUtil.getValue(txtPeriode);
		int tenggangPokok = 0;
		int tenggangBunga = 0;
		int distGrpBunga = 0;
		BigDecimal plafond = (BigDecimal) ComponentUtil.getValue(decBakiDebet);
		BigDecimal provFee = BigDecimal.ZERO;
		BigDecimal getFee = BigDecimal.ZERO;
		BigDecimal bunga = (BigDecimal) ComponentUtil.getValue(decBungaBaru);
		BigDecimal discount = BigDecimal.ZERO;
//		System.out.println("cmbHariBunga"+ComponentUtil.getValue(cmbHariBunga));
//		jadwalAngsur.jumlahHariTahun= (int) ComponentUtil.getValue(cmbHariBunga);
		System.out.println("Jenis : "+FunctionUtils.getDigitAt(tipeBungaBaru.getInt("TYPEINT"), 3));
		System.out.println("Round : "+FunctionUtils.getDigitAt(tipeBungaBaru.getInt("TYPEINT"), 2));
		System.out.println("StsRound : "+FunctionUtils.getDigitAt(tipeBungaBaru.getInt("TYPEINT"), 1));
		
		
//		int grp = 0;
//		double a = 0;
		isUpload=true;
		
		irrBaru = ((BigDecimal)ComponentUtil.getValue(decBungaBaru)).doubleValue()/12;
		listAngsuran = jadwalAngsur.doGenerateAngsuranImpairment(tglMulai, jangkaWaktu, periodePokok, periodeBunga, tenggangPokok, tenggangBunga,
				distGrpBunga, plafond, provFee, getFee, bunga.subtract(discount), discount, tipeBungaBaru.getString("KINDINT"), 
				FunctionUtils.getDigitAt(tipeBungaBaru.getInt("TYPEINT"), 3),
				FunctionUtils.getDigitAt(tipeBungaBaru.getInt("TYPEINT"), 2), 
				FunctionUtils.getDigitAt(tipeBungaBaru.getInt("TYPEINT"), 1),
				new BigDecimal(irrBaru),angsurUpload);
		doSetAngsur(listAngsuran);
	}
	
	private void setDetailTipeBunga(String typeInt){
		if (!typeInt.equals("0")) {
			tipeBungaBaru = (DTOMap) masterService.getMapMaster(
					"SELECT PARMIDOTH AS KINDINT,VIEWORD AS TYPEINT "
							+ "	FROM CFG_PARM "
							+ "	WHERE PARMGRP=15 "
							+ "		AND PARMIDOTH=? "
							+ "		AND PARMIDOTH <>'K' ", 
							new Object[] {typeInt});
		}else{
			tipeBungaBaru.put("KINDINT", "A");
			tipeBungaBaru.put("TYPEINT", 572);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void doGenerateArusKasBaru() {
		if (validation()) {
			Date tglMulai = (Date) ComponentUtil.getValue(txtTglMulaiBaru);
			int jangkaWaktu = (Integer) ComponentUtil.getValue(intTenorBaru);
			int periodePokok = jangkaWaktu;
				periodePokok = (Integer) ComponentUtil.getValue(txtPeriode);
			int periodeBunga = jangkaWaktu;
				periodeBunga = (Integer) ComponentUtil.getValue(txtPeriode);
			int tenggangPokok = 0;
			int tenggangBunga = 0;
			int distGrpBunga = 0;
			BigDecimal plafond = (BigDecimal) ComponentUtil.getValue(decBakiDebet);
			BigDecimal provFee = BigDecimal.ZERO;
			BigDecimal getFee = BigDecimal.ZERO;
			BigDecimal bunga = (BigDecimal) ComponentUtil.getValue(decBungaBaru);
			BigDecimal discount = BigDecimal.ZERO;
//			System.out.println("cmbHariBunga"+ComponentUtil.getValue(cmbHariBunga));
//			jadwalAngsur.jumlahHariTahun= (int) ComponentUtil.getValue(cmbHariBunga);
			System.out.println("Jenis : "+FunctionUtils.getDigitAt(tipeBungaBaru.getInt("TYPEINT"), 3));
			System.out.println("Round : "+FunctionUtils.getDigitAt(tipeBungaBaru.getInt("TYPEINT"), 2));
			System.out.println("StsRound : "+FunctionUtils.getDigitAt(tipeBungaBaru.getInt("TYPEINT"), 1));
			
			
//			int grp = 0;
//			double a = 0;
			isUpload=false;
			
			irrBaru = ((BigDecimal)ComponentUtil.getValue(decBungaBaru)).doubleValue()/12;
			listAngsuran = jadwalAngsur.doGenerateAngsuranImpairment(tglMulai, jangkaWaktu, periodePokok, periodeBunga, tenggangPokok, tenggangBunga,
					distGrpBunga, plafond, provFee, getFee, bunga.subtract(discount), discount, tipeBungaBaru.getString("KINDINT"), 
					FunctionUtils.getDigitAt(tipeBungaBaru.getInt("TYPEINT"), 3),
					FunctionUtils.getDigitAt(tipeBungaBaru.getInt("TYPEINT"), 2), 
					FunctionUtils.getDigitAt(tipeBungaBaru.getInt("TYPEINT"), 1),
					new BigDecimal(irrBaru));
			doSetAngsur(listAngsuran);
			Aksi = "Generate Arus Kas baru No Rekening "+ ComponentUtil.getValue(txtNoRekening);
			doLogAktfitas(Aksi);
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void doSetAngsur(List listArusKasKonversi) {
		if (listJadwalAngsur.getItemCount() > 0) {
			listJadwalAngsur.getItems().clear();
		}
		if (listArusKasBaru.getItemCount() > 0) {
			listArusKasBaru.getItems().clear();
		}
		
		SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
		
		String JenisBunga=(String)ComponentUtil.getValue(cmbJnsBungaBaru);
		
		decIrrBaru.setValue(new BigDecimal(0));
		totPokok = 0;
		totBunga = 0;
		totAngsur = 0;
		estimasiArusKasBaru = 0;
		bungaKonversiBaru = 0;
		angsuranPokokTot = 0;
		angsuranBungaTot = 0;
		selisihBungaKontraktualBaru = 0;
		nilaiKiniArusKasBaru = 0;
		bungaAtribusiBaru = 0;
		amortisasiBaru = 0;

		int i = 0;
		saldoMustNol = new BigDecimal(0);
		if(JenisBunga.equals("0")){
			saldoMustNol = (BigDecimal) ComponentUtil.getValue(decBakiDebet);
		}
		double totalBungaKonversi=0;
		for (Object o : listArusKasKonversi) {
			DTOMap dtoMap = (DTOMap) o;
			Listitem itemAngsur = new Listitem();
			//itemAngsur.setId("Jadwal"+dtoMap.get("jadwalAngsurKe"));
			itemAngsur.setAttribute("DATA", dtoMap);
			itemAngsur.appendChild(new Listcell(dtoMap.getInt("jadwalAngsurKe").toString()));
			itemAngsur.appendChild(new Listcell(sdf.format(dtoMap.getDate("tglJadwal"))));
			//Decimal Untuk Pokok
			Decimalbox decimalboxPokok = new Decimalbox();
			if(!JenisBunga.equals("0")){
				decimalboxPokok.setDisabled(true);
			}else{
				decimalboxPokok.setDisabled(false);
			}
			decimalboxPokok.setStyle("text-align:right;");
			decimalboxPokok.setWidth("95%");
			decimalboxPokok.setFormat("#,##0");
			decimalboxPokok.setId("Pokok"+dtoMap.get("jadwalAngsurKe"));
			decimalboxPokok.setValue(dtoMap.getBigDecimal("angsuranPokok"));
			
			org.zkoss.zul.Listcell lcPokok = new org.zkoss.zul.Listcell();
			lcPokok.appendChild(decimalboxPokok);
			itemAngsur.appendChild(lcPokok);
			//Decimal Untuk Pokok
			//Decimal Untuk Bunga
			Decimalbox decimalboxBunga = new Decimalbox();
			if(!JenisBunga.equals("0")){
				decimalboxBunga.setDisabled(true);
			}else{
				decimalboxBunga.setDisabled(false);
			}
			decimalboxBunga.setStyle("text-align:right;");
			decimalboxBunga.setWidth("95%");
			decimalboxBunga.setFormat("#,##0");
			decimalboxBunga.setId("Bunga"+dtoMap.get("jadwalAngsurKe"));
			decimalboxBunga.setValue(dtoMap.getBigDecimal("angsuranBunga"));
			org.zkoss.zul.Listcell lcBunga = new org.zkoss.zul.Listcell();
			lcBunga.appendChild(decimalboxBunga);
			itemAngsur.appendChild(lcBunga);
			//Decimal Untuk Bunga
			
			
			//Dikosongkan
			if(JenisBunga.equals("0")){
				EventListener selectPokok = new EventListener() {
					public void onEvent(Event e) throws Exception {
						onSelectPokok((Decimalbox) e.getTarget());
					}
				};
				
				EventListener selectBunga = new EventListener() {
					public void onEvent(Event e) throws Exception {
						onSelectBunga((Decimalbox) e.getTarget());
					}
				};
				
				decimalboxPokok.addEventListener(Events.ON_BLUR, selectPokok);
				decimalboxBunga.addEventListener(Events.ON_BLUR, selectBunga);
				
				//listener
				if(dtoMap.getBigDecimal("angsuranPokok").doubleValue()==0){
					decimalboxPokok.setDisabled(true);
				}else{
					decimalboxPokok.setValue(new BigDecimal(0));
					dtoMap.put("angsuranPokok",new BigDecimal(0));
				}
				if(dtoMap.getBigDecimal("angsuranBunga").doubleValue()==0){
					decimalboxBunga.setDisabled(true);
				}else{
					decimalboxBunga.setValue(new BigDecimal(0));
					dtoMap.put("angsuranBunga",new BigDecimal(0));
				}
				BigDecimal totalAngsuran = new BigDecimal(0);
				BigDecimal saldoTeoritis = (BigDecimal) ComponentUtil.getValue(decBakiDebet);
				dtoMap.put("totalAngsuran",totalAngsuran);
				dtoMap.put("saldoTeoritis",saldoTeoritis);
				
				if(i==0){
					dtoMap.put("SALDO_AWAL_KONVERSI", BigDecimal.ZERO);
					dtoMap.put("SALDO_AKHIR_KONVERSI", saldoTeoritis);
				}else{
					Listitem itemSebelum=listJadwalAngsur.getItemAtIndex(i-1);
					DTOMap angsurSebelum = (DTOMap) itemSebelum.getAttribute("DATA");
					dtoMap.put("SALDO_AWAL_KONVERSI", angsurSebelum.getBigDecimal("SALDO_AKHIR_KONVERSI"));
					dtoMap.put("SALDO_AKHIR_KONVERSI",angsurSebelum.getBigDecimal("SALDO_AKHIR_KONVERSI"));
				}
				dtoMap.put("ESTIMASI_ARUS", BigDecimal.ZERO);
				dtoMap.put("BUNGA_KONVERSI", BigDecimal.ZERO);
				dtoMap.put("POKOK_ARUS",BigDecimal.ZERO);
				dtoMap.put("BUNGA_ARUS",BigDecimal.ZERO);
				dtoMap.put("SELISIH_BUNGA_KONTRAKTUAL",BigDecimal.ZERO);
//				dtoMap.put("NILAI_KINI_ARUS", BigDecimal.ZERO);
			}
			//Dikosongkan
			
			itemAngsur.appendChild(new Listcell(FunctionUtils.moneyToText(dtoMap.getBigDecimal("totalAngsuran"))));
			itemAngsur.appendChild(new Listcell(FunctionUtils.moneyToText(dtoMap.getBigDecimal("saldoTeoritis"))));
			listJadwalAngsur.appendChild(itemAngsur);

			Listitem itemArus = new Listitem();
			itemArus.appendChild(new Listcell(dtoMap.get("jadwalAngsurKe").toString()));
			itemArus.appendChild(new Listcell(sdf.format(dtoMap.getDate("tglJadwal"))));

			itemArus.appendChild(new Listcell(FunctionUtils.moneyToText(dtoMap.getBigDecimal("ESTIMASI_ARUS"))));
			if(dtoMap.getInt("jadwalAngsurKe") == 0){
				itemArus.appendChild(new Listcell(FunctionUtils.moneyToText(new BigDecimal (0.00))));
				dtoMap.put("NILAI_KINI_ARUS",new BigDecimal (0.00));
			}else{			
				itemArus.appendChild(new Listcell(FunctionUtils.moneyToText(dtoMap.getBigDecimal("NILAI_KINI_ARUS"))));	
			}
			
			itemArus.appendChild(new Listcell(FunctionUtils.moneyToText(dtoMap.getBigDecimal("SALDO_AWAL_KONVERSI"))));
			itemArus.appendChild(new Listcell(FunctionUtils.moneyToText(dtoMap.getBigDecimal("BUNGA_KONVERSI"))));
			itemArus.appendChild(new Listcell(FunctionUtils.moneyToText(dtoMap.getBigDecimal("POKOK_ARUS"))));
			itemArus.appendChild(new Listcell(FunctionUtils.moneyToText(dtoMap.getBigDecimal("BUNGA_ARUS"))));
			itemArus.appendChild(new Listcell(FunctionUtils.moneyToText(dtoMap.getBigDecimal("SELISIH_BUNGA_KONTRAKTUAL"))));
			itemArus.appendChild(new Listcell(FunctionUtils.moneyToText(dtoMap.getBigDecimal("SALDO_AKHIR_KONVERSI"))));
			itemArus.appendChild(new Listcell(FunctionUtils.moneyToText(dtoMap.getBigDecimal("saldoTeoritis"))));

			estimasiArusKasBaru += dtoMap.getBigDecimal("ESTIMASI_ARUS").doubleValue();
			bungaKonversiBaru += dtoMap.getBigDecimal("BUNGA_KONVERSI").doubleValue();
			
			nilaiKiniArusKasBaru += dtoMap.getBigDecimal("NILAI_KINI_ARUS").doubleValue();
			angsuranPokokTot += dtoMap.getBigDecimal("POKOK_ARUS").doubleValue();
			angsuranBungaTot += dtoMap.getBigDecimal("BUNGA_ARUS").doubleValue();
			selisihBungaKontraktualBaru += dtoMap.getBigDecimal("SELISIH_BUNGA_KONTRAKTUAL").doubleValue();
			listArusKasBaru.appendChild(itemArus);
			totPokok += dtoMap.getBigDecimal("angsuranPokok").doubleValue();
			totBunga += dtoMap.getBigDecimal("angsuranBunga").doubleValue();
			totAngsur += dtoMap.getBigDecimal("totalAngsuran").doubleValue();
			i++;
		}
		DecimalFormat df = new DecimalFormat("#,##0.00");
		decIrrBaru.setValue(new BigDecimal(irrBaru).setScale(8, BigDecimal.ROUND_HALF_UP));
		decTotPokok.setValue(BigDecimal.valueOf(totPokok));
		decTotBunga.setValue( BigDecimal.valueOf(totBunga));
		decTotJmlAngsur.setValue( BigDecimal.valueOf(totAngsur));
		decNilaiWajarBaru.setValue( BigDecimal.valueOf(nilaiKiniArusKasBaru));

		
		BigDecimal nilaiWajarBaru = (BigDecimal)ComponentUtil.getValue(decNilaiWajarBaru);
		BigDecimal nilaiWajarLama = (BigDecimal)ComponentUtil.getValue(decNilaiWajarLama);
		
		double selisih = 0;
		selisih = nilaiWajarLama.doubleValue() - nilaiWajarBaru.doubleValue();
		
		if(selisih > 0){
			decImpair.setValue( new BigDecimal(selisih));
		}else if(selisih < 0){
			decImpair.setValue( new BigDecimal(selisih*-1));
		}else{
			decImpair.setValue( new BigDecimal(0.00));
		}
		
		/*for(Listitem itemCkpn : listArusKasBaru.getItems()){
			((Listcell) itemCkpn.getChildren().get(9)).setLabel(df.format((BigDecimal)ComponentUtil.getValue(decSelisihNPV)));
			((Listcell) itemCkpn.getChildren().get(10)).setLabel(df.format((BigDecimal)ComponentUtil.getValue(decCKPNIndividu)));
		}*/
		
		ComponentUtil.setValue(decArus1Baru, df.format(estimasiArusKasBaru));
		ComponentUtil.setValue(decArus2Baru, df.format(nilaiKiniArusKasBaru));
		ComponentUtil.setValue(decArus4Baru, df.format(bungaKonversiBaru));
		ComponentUtil.setValue(decArus5Baru, df.format(angsuranPokokTot));
		ComponentUtil.setValue(decArus6Baru, df.format(angsuranBungaTot));
		ComponentUtil.setValue(decArus7Baru, df.format(selisihBungaKontraktualBaru));
	}
	
	public void onSelectPokok(Decimalbox decPokok) {
		BigDecimal pokok = (BigDecimal) ComponentUtil.getValue(decPokok);
		if(pokok==null){
			pokok = new BigDecimal(0);
			decPokok.setValue(pokok);
		}
		if(pokok.doubleValue() < 0){
			throw new WrongValueException(decPokok, "Nilai Pokok Tidak Boleh Negatif.");
		}
		
		String sid=decPokok.getId();
		sid=sid.replace("Pokok", "");
		Integer id = Integer.valueOf(sid);
		
		doHitungAngsuran(id,pokok,"Pokok");
	}
	
	public void onSelectBunga(Decimalbox decBunga) {
		BigDecimal bunga = (BigDecimal) ComponentUtil.getValue(decBunga);
		if(bunga==null){
			bunga = new BigDecimal(0);
			decBunga.setValue(bunga);
		}
		if(bunga.doubleValue() < 0){
			throw new WrongValueException(decBunga, "Nilai Bunga Tidak Boleh Negatif.");
		}
		
		String sid=decBunga.getId();
		sid=sid.replace("Bunga", "");
		Integer id = Integer.valueOf(sid);
		doHitungAngsuran(id,bunga,"Bunga");
	}
	
	private void doHitungAngsuran(Integer id,BigDecimal angsuran, String jenisangsur){
		angsuran = angsuran.setScale(0, RoundingMode.HALF_UP);
		Listitem item=listJadwalAngsur.getItemAtIndex(id);
		DTOMap dataAngsur = (DTOMap) item.getAttribute("DATA");
		BigDecimal angsuranLama = new BigDecimal(0);
		if(jenisangsur.equals("Pokok")){
			angsuranLama = (BigDecimal)dataAngsur.get("angsuranPokok");
			saldoMustNol=saldoMustNol.add(angsuranLama);
		}
		if(jenisangsur.equals("Bunga")){
			angsuranLama = (BigDecimal)dataAngsur.get("angsuranBunga");
		}
		
		if(jenisangsur.equals("Pokok")){
			dataAngsur.put("angsuranPokok",angsuran);
			saldoMustNol=saldoMustNol.subtract(angsuran);
		}
		if(jenisangsur.equals("Bunga")){
			dataAngsur.put("angsuranBunga",angsuran);
		}
		
		double total = ((BigDecimal)dataAngsur.get("angsuranPokok")).doubleValue() + ((BigDecimal)dataAngsur.get("angsuranBunga")).doubleValue();
		dataAngsur.put("totalAngsuran",new BigDecimal(total));
		((Listcell) item.getChildren().get(4)).setLabel(dfGlobal.format(total));
		if(jenisangsur.equals("Pokok")){
			totPokok = totPokok - angsuranLama.doubleValue();
			totPokok = totPokok + angsuran.doubleValue();
			decTotPokok.setValue(BigDecimal.valueOf(totPokok));
		}
		if(jenisangsur.equals("Bunga")){
			totBunga = totBunga - angsuranLama.doubleValue();
			totBunga = totBunga + angsuran.doubleValue();
			decTotBunga.setValue(BigDecimal.valueOf(totBunga));
		}
		totAngsur = totPokok+totBunga;
		decTotJmlAngsur.setValue(BigDecimal.valueOf(totAngsur));
		//-----------------------------------------------------Arus Kas
		nilaiKiniArusKasBaru -= dataAngsur.getBigDecimal("NILAI_KINI_ARUS").doubleValue();
		
		BigDecimal nilai_kini_arus=new BigDecimal(dataAngsur.getBigDecimal("angsuranPokok").negate()
													.add(dataAngsur.getBigDecimal("angsuranBunga").negate())
													.abs().doubleValue()/Math.pow((1+irrBaru/100), id))
													.setScale(0, RoundingMode.HALF_UP);
		dataAngsur.put("NILAI_KINI_ARUS", nilai_kini_arus);
		//set nilai kini
		Listitem itemArusKas=listArusKasBaru.getItemAtIndex(id);
		((Listcell) itemArusKas.getChildren().get(1)).setLabel(dfGlobal.format((BigDecimal)dataAngsur.get("NILAI_KINI_ARUS")));
		//set nilai kini
		nilaiKiniArusKasBaru += dataAngsur.getBigDecimal("NILAI_KINI_ARUS").doubleValue();
		
		Listitem itemArusKasAwal=listArusKasBaru.getItemAtIndex(0);
		Listitem itemDataArusKasAwal=listJadwalAngsur.getItemAtIndex(0);
		DTOMap dataArusKasAwal = (DTOMap) itemDataArusKasAwal.getAttribute("DATA");
		estimasiArusKasBaru -= dataArusKasAwal.getBigDecimal("ESTIMASI_ARUS").doubleValue();
		dataArusKasAwal.put("ESTIMASI_ARUS", new BigDecimal(nilaiKiniArusKasBaru*-1));
		estimasiArusKasBaru += dataArusKasAwal.getBigDecimal("ESTIMASI_ARUS").doubleValue();
		((Listcell) itemArusKasAwal.getChildren().get(0)).setLabel(dfGlobal.format((BigDecimal)dataArusKasAwal.get("ESTIMASI_ARUS")));
		decNilaiWajarBaru.setValue(BigDecimal.valueOf(nilaiKiniArusKasBaru));
		dataArusKasAwal.put("SALDO_AKHIR_KONVERSI", new BigDecimal(nilaiKiniArusKasBaru));
		((Listcell) itemArusKasAwal.getChildren().get(7)).setLabel(dfGlobal.format((BigDecimal)dataArusKasAwal.get("SALDO_AKHIR_KONVERSI")));
		ComponentUtil.setValue(decArus2Baru, dfGlobal.format(nilaiKiniArusKasBaru));
		//-----------------------------------------------------Arus Kas
		//hitung listSisa
		Integer maxSize = listJadwalAngsur.getItemCount();
		for(int i =1 ; i<maxSize; i++){
			Listitem itemPenerus=listJadwalAngsur.getItemAtIndex(i);
			Listitem itemArusKasSebelumPenerus=listJadwalAngsur.getItemAtIndex(i-1);
			Listitem itemPenerusArusKas=listArusKasBaru.getItemAtIndex(i);
			DTOMap dataAngsurPenerus = (DTOMap) itemPenerus.getAttribute("DATA");
			DTOMap dataArusKasSebelumPenerus = (DTOMap) itemArusKasSebelumPenerus.getAttribute("DATA");
			
			estimasiArusKasBaru -= dataAngsurPenerus.getBigDecimal("ESTIMASI_ARUS").doubleValue();
			bungaKonversiBaru -= dataAngsurPenerus.getBigDecimal("BUNGA_KONVERSI").doubleValue();
			angsuranPokokTot -= dataAngsurPenerus.getBigDecimal("POKOK_ARUS").doubleValue();
			angsuranBungaTot -= dataAngsurPenerus.getBigDecimal("BUNGA_ARUS").doubleValue();
			selisihBungaKontraktualBaru -= dataAngsurPenerus.getBigDecimal("SELISIH_BUNGA_KONTRAKTUAL").doubleValue();
			
			BigDecimal pokok_arus = dataAngsurPenerus.getBigDecimal("angsuranPokok").negate();
			BigDecimal bunga_arus = dataAngsurPenerus.getBigDecimal("angsuranBunga").negate();
			BigDecimal estimasi_arus=pokok_arus.add(bunga_arus).abs();
			BigDecimal saldo_awal_konversi=dataArusKasSebelumPenerus.getBigDecimal("SALDO_AKHIR_KONVERSI");
			BigDecimal bunga_konversi =new BigDecimal(saldo_awal_konversi.doubleValue()*(irrBaru/100)).setScale(0, RoundingMode.HALF_UP);
			BigDecimal selisih_bunga_kontraktual=bunga_konversi.add(bunga_arus);
			BigDecimal saldo_akhir_konversi=saldo_awal_konversi.add(bunga_konversi).add(pokok_arus).add(bunga_arus);
			BigDecimal saldoTeoritis = dataArusKasSebelumPenerus.getBigDecimal("saldoTeoritis").subtract(dataAngsurPenerus.getBigDecimal("angsuranPokok"));
			
			dataAngsurPenerus.put("ESTIMASI_ARUS", estimasi_arus);
			((Listcell) itemPenerusArusKas.getChildren().get(0)).setLabel(dfGlobal.format((BigDecimal)dataAngsurPenerus.get("ESTIMASI_ARUS")));
			dataAngsurPenerus.put("SALDO_AWAL_KONVERSI", saldo_awal_konversi);
			((Listcell) itemPenerusArusKas.getChildren().get(2)).setLabel(dfGlobal.format((BigDecimal)dataAngsurPenerus.get("SALDO_AWAL_KONVERSI")));
			dataAngsurPenerus.put("BUNGA_KONVERSI", bunga_konversi);
			((Listcell) itemPenerusArusKas.getChildren().get(3)).setLabel(dfGlobal.format((BigDecimal)dataAngsurPenerus.get("BUNGA_KONVERSI")));
			dataAngsurPenerus.put("POKOK_ARUS",pokok_arus);
			((Listcell) itemPenerusArusKas.getChildren().get(4)).setLabel(dfGlobal.format((BigDecimal)dataAngsurPenerus.get("POKOK_ARUS")));
			dataAngsurPenerus.put("BUNGA_ARUS",bunga_arus);
			((Listcell) itemPenerusArusKas.getChildren().get(5)).setLabel(dfGlobal.format((BigDecimal)dataAngsurPenerus.get("BUNGA_ARUS")));
			dataAngsurPenerus.put("SELISIH_BUNGA_KONTRAKTUAL",selisih_bunga_kontraktual);
			((Listcell) itemPenerusArusKas.getChildren().get(6)).setLabel(dfGlobal.format((BigDecimal)dataAngsurPenerus.get("SELISIH_BUNGA_KONTRAKTUAL")));
			dataAngsurPenerus.put("SALDO_AKHIR_KONVERSI",saldo_akhir_konversi);
			((Listcell) itemPenerusArusKas.getChildren().get(7)).setLabel(dfGlobal.format((BigDecimal)dataAngsurPenerus.get("SALDO_AKHIR_KONVERSI")));
			dataAngsurPenerus.put("saldoTeoritis",saldoTeoritis);
			((Listcell) itemPenerus.getChildren().get(5)).setLabel(dfGlobal.format((BigDecimal)dataAngsurPenerus.get("saldoTeoritis")));
			((Listcell) itemPenerusArusKas.getChildren().get(8)).setLabel(dfGlobal.format((BigDecimal)dataAngsurPenerus.get("saldoTeoritis")));
			
			
			estimasiArusKasBaru += dataAngsurPenerus.getBigDecimal("ESTIMASI_ARUS").doubleValue();
			bungaKonversiBaru += dataAngsurPenerus.getBigDecimal("BUNGA_KONVERSI").doubleValue();
			angsuranPokokTot += dataAngsurPenerus.getBigDecimal("POKOK_ARUS").doubleValue();
			angsuranBungaTot += dataAngsurPenerus.getBigDecimal("BUNGA_ARUS").doubleValue();
			selisihBungaKontraktualBaru += dataAngsurPenerus.getBigDecimal("SELISIH_BUNGA_KONTRAKTUAL").doubleValue();
			if(i == maxSize-1){
				double selisih = estimasiArusKasBaru - bungaKonversiBaru;
				bunga_konversi = bunga_konversi.add(new BigDecimal(selisih));
				bungaKonversiBaru -= dataAngsurPenerus.getBigDecimal("BUNGA_KONVERSI").doubleValue();
				dataAngsurPenerus.put("BUNGA_KONVERSI", bunga_konversi);
				bungaKonversiBaru += dataAngsurPenerus.getBigDecimal("BUNGA_KONVERSI").doubleValue();
				((Listcell) itemPenerusArusKas.getChildren().get(3)).setLabel(dfGlobal.format((BigDecimal)dataAngsurPenerus.get("BUNGA_KONVERSI")));
				selisih_bunga_kontraktual=bunga_konversi.add(bunga_arus);
				saldo_akhir_konversi=saldo_awal_konversi.add(bunga_konversi).add(pokok_arus).add(bunga_arus);
				selisihBungaKontraktualBaru -= dataAngsurPenerus.getBigDecimal("SELISIH_BUNGA_KONTRAKTUAL").doubleValue();
				dataAngsurPenerus.put("SELISIH_BUNGA_KONTRAKTUAL",selisih_bunga_kontraktual);
				selisihBungaKontraktualBaru += dataAngsurPenerus.getBigDecimal("SELISIH_BUNGA_KONTRAKTUAL").doubleValue();
				((Listcell) itemPenerusArusKas.getChildren().get(6)).setLabel(dfGlobal.format((BigDecimal)dataAngsurPenerus.get("SELISIH_BUNGA_KONTRAKTUAL")));
				dataAngsurPenerus.put("SALDO_AKHIR_KONVERSI",saldo_akhir_konversi);
				((Listcell) itemPenerusArusKas.getChildren().get(7)).setLabel(dfGlobal.format((BigDecimal)dataAngsurPenerus.get("SALDO_AKHIR_KONVERSI")));
				
			}
		}
		ComponentUtil.setValue(decArus1Baru, dfGlobal.format(estimasiArusKasBaru));
		ComponentUtil.setValue(decArus4Baru, dfGlobal.format(bungaKonversiBaru));
		ComponentUtil.setValue(decArus5Baru, dfGlobal.format(angsuranPokokTot));
		ComponentUtil.setValue(decArus6Baru, dfGlobal.format(angsuranBungaTot));
		ComponentUtil.setValue(decArus7Baru, dfGlobal.format(selisihBungaKontraktualBaru));
		
		
		BigDecimal nilaiWajarBaru = (BigDecimal)ComponentUtil.getValue(decNilaiWajarBaru);
		BigDecimal nilaiWajarLama = (BigDecimal)ComponentUtil.getValue(decNilaiWajarLama);
		
		double selisih = 0;
		selisih = nilaiWajarLama.doubleValue() - nilaiWajarBaru.doubleValue();
		
		if(selisih > 0){
			decImpair.setValue(new BigDecimal(0.00));
		}else if(selisih < 0){
			decImpair.setValue(new BigDecimal(selisih*-1));
		}else{
			decImpair.setValue(new BigDecimal(0.00));
		}
		/*for(Listitem itemCkpn : listBoxArusKasImpair.getItems()){
			((Listcell) itemCkpn.getChildren().get(9)).setLabel(dfGlobal.format((BigDecimal)ComponentUtil.getValue(decSelisihNPV")));
			((Listcell) itemCkpn.getChildren().get(10)).setLabel(dfGlobal.format((BigDecimal)ComponentUtil.getValue(decCKPNIndividu")));
		}*/
		//hitung listSisa
	}
	
	@SuppressWarnings("unchecked")
	public boolean validation() {
		boolean isValid = true;
		List wrongValue = new ArrayList(0);
		if (isValid) {
			Integer jangkaWaktu=(Integer) ComponentUtil.getValue(intTenorBaru);
			if (jangkaWaktu == null) {
				isValid = false;
				wrongValue.add(new WrongValueException(intTenorBaru, "Jangka Waktu Harus Diisi"));
			}
			
			Date strtdt = (Date) ComponentUtil.getValue(txtTglMulaiBaru);
			if (strtdt == null) {
				isValid = false;
				wrongValue.add(new WrongValueException(txtTglMulaiBaru, "Tanggal Mulai Harus Diisi"));
			}

			String SukuBunga = (String) ComponentUtil.getValue(cmbJnsBungaBaru);
			if (SukuBunga == null) {
				isValid = false;
				wrongValue.add(new WrongValueException(cmbJnsBungaBaru, "Suku Bunga Harus Diisi"));
			}

			BigDecimal IntRate = (BigDecimal) ComponentUtil.getValue(decBungaBaru);
			if (IntRate == null) {
				isValid = false;
				wrongValue.add(new WrongValueException(decBungaBaru, "Suku Bunga Harus Diisi"));
			}

			if (wrongValue.size() > 0) {
				throw new WrongValuesException((WrongValueException[]) wrongValue.toArray(new WrongValueException[wrongValue.size()]));
			}
			
			Integer periodPokok = (Integer) ComponentUtil.getValue(txtPeriode);
			if (12 % periodPokok != 0 || periodPokok == 2)
				throw new WrongValueException(txtPeriode, "Period Pokok harus bernilai (1, 3, 4, 6, 12)");
			if (SukuBunga != null) {
				if (SukuBunga.charAt(0) == 'B') {
					if (periodPokok > 1)
						throw new WrongValueException(txtPeriode, "Periode Pokok Harus 1");
				}
				//if ((SukuBunga.charAt(0) == 'A' || SukuBunga.charAt(0) == 'D') && periodPokok.intValue() != periodBunga.intValue()) {
			//		throw new WrongValueException(txtPeriodBunga, "Periode Pembayaran Bunga Harus = Pokok");
				//} else if (periodPokok.intValue() != periodBunga.intValue() && periodBunga > 1) {
			//		throw new WrongValueException(txtPeriodBunga, "Periode Pembayaran Bunga Harus = Pokok atau 1");
				//}
			}

		}
		return isValid;
	}
	
	private void doChangeBakiDebet() {
		// BAKI DEBET BARU = BAKI DEBET LAMA-KERINGANAN POKOK + SISA BUNGA + SISA DENDA
		BigDecimal bakiDebet = (BigDecimal) ComponentUtil.getValue(decBakiDebet);
		bakiDebet = bakiDebet==null ? BigDecimal.ZERO : bakiDebet;
		
		BigDecimal keringananPokok = (BigDecimal) ComponentUtil.getValue(decKeringananPokok);
		keringananPokok = keringananPokok==null? BigDecimal.ZERO : keringananPokok;
		
		BigDecimal sisaBunga = new BigDecimal(0);
		if (chkKapitalisirBunga.isChecked()) {
			sisaBunga = (BigDecimal)ComponentUtil.getValue(decSisaTunggBunga);
		}
		
		BigDecimal sisaDenda = new BigDecimal(0);
		if (chkKapitalisirDenda.isChecked()) {
			sisaDenda = (BigDecimal) ComponentUtil.getValue(decSisaTunggDenda);
		}
		BigDecimal bakiDebetBaru = new BigDecimal(bakiDebet.doubleValue() - keringananPokok.doubleValue()
				+ sisaBunga.doubleValue() + sisaDenda.doubleValue());
		decBakiDebet.setValue(bakiDebetBaru);
	}
	
	private void loadRating() {
		List<DTOMap> listRating = (List<DTOMap>) masterService.getDataMaster(" SELECT PARMID,PARMNM,PARMIDOTH from CFG_PARM "
				+ "																WHERE PARMGRP=2 ORDER BY PARMID "
								,new Object[]{});
		Comboitem ciRating = new Comboitem();
		if (listRating.size() > 0) {
			for (DTOMap map : listRating) {
				ciRating = new Comboitem();
				ciRating.setLabel(map.getString("PARMNM"));
				ciRating.setValue(map.getString("PARMID"));
				cmbRating.appendChild(ciRating);
			}
		}
	}
	
	private void loadJnsBunga() {
		List<DTOMap> listJnsBunga = (List<DTOMap>) masterService.getDataMaster(" SELECT PARMID,PARMNM,PARMIDOTH from CFG_PARM "
				+ "																WHERE PARMGRP=15 ORDER BY PARMID "
								,new Object[]{});
		Comboitem ciJnsBunga = new Comboitem();
		if (listJnsBunga.size() > 0) {
			for (DTOMap map : listJnsBunga) {
				ciJnsBunga = new Comboitem();
				ciJnsBunga.setLabel(map.getString("PARMID") + " - "+ map.getString("PARMNM"));
				ciJnsBunga.setValue(map.getString("PARMIDOTH"));
				cmbJnsBunga.appendChild(ciJnsBunga);
				cmbJnsBungaBaru.appendChild(ciJnsBunga);
			}
		}
	}
	
	private void doLoadDataRek(String noRek) {
		DTOMap dataRek=masterService.getMapMaster(" SELECT 	A.TGL_POS,A.INTTYPE,A.LNPERIOD,A.LNSTRDT,A.LNDUEDT,A.ACCSTS,A.RATING,	"
				+ "											A.PLAFOND,A.INTRATE,A.SBE_ANNUAL,A.DPD,B.PARMIDOTH AS STAGE,			"
				+ "											COALESCE(CASE WHEN A.ENDBAL < 0 THEN 0 ELSE A.ENDBAL END ,0) AS ENDBAL,	"
				+ "											COALESCE(A.AMOREIR,0) AS AMOREIR,COALESCE(A.MODIFIKASI,0) AS MODIFIKASI,"
				+ "											A.TUNGPKK AS TUNGGPOKOK,A.TUNGGBNG AS TUNGGBUNGA,A.PENALTY AS DENDA		"
				+ "									FROM LOAN_MASTER A LEFT OUTER JOIN CFG_PARM B									"
				+ "														ON B.PARMGRP=3 AND A.RATING=CAST(B.PARMID AS INT)			"
				+ "									WHERE A.TGL_POS= (SELECT MAX(TGL_POS) FROM LOAN_MASTER WHERE ACCNBR=A.ACCNBR) 	"
				+ "										AND A.ACCNBR=? 		"
				+ "										AND A.VERSION='0'	", new Object[]{noRek});
		if (dataRek!=null) {
			if (dataRek.getInt("ACCSTS").equals(0)) {
				throw new WrongValueException(txtNoRekening, "No. Rekening Status Tidak Aktif.");
			}else if (dataRek.getInt("ACCSTS").equals(6)) {
				throw new WrongValueException(txtNoRekening, "No. Rekening Status Channeling.");
			}else if (dataRek.getInt("ACCSTS").equals(7)) {
				throw new WrongValueException(txtNoRekening, "No. Rekening Status Hapus Buku.");
			}else if (dataRek.getInt("ACCSTS").equals(8)) {
				throw new WrongValueException(txtNoRekening, "No. Rekening Status Hapus Tagih.");
			}else if (dataRek.getInt("ACCSTS").equals(9)) {
				throw new WrongValueException(txtNoRekening, "No. Rekening Status Tutup/Lunas.");
			}else{
				ComponentUtil.setValue(txtNoRekening, noRek);
				ComponentUtil.setValue(txtTglPosisi, dataRek.getDate("TGL_POS"));
				ComponentUtil.setValue(cmbJnsBunga, dataRek.getString("INTTYPE"));
				ComponentUtil.setValue(intTenor, dataRek.getInt("LNPERIOD"));
				decBunga.setValue(dataRek.getBigDecimal("INTRATE"));
				decPlafon.setValue(dataRek.getBigDecimal("PLAFOND"));
				decNilaiWajarLama.setValue(dataRek.getBigDecimal("ENDBAL")
											.subtract(dataRek.getBigDecimal("AMOREIR"))
											.subtract(dataRek.getBigDecimal("MODIFIKASI")));
				decBakiDebet.setValue((BigDecimal) ComponentUtil.getValue(decNilaiWajarLama));
				ComponentUtil.setValue(txtTglMulai, dataRek.getDate("LNSTRDT"));
				ComponentUtil.setValue(txtTglAkhir, dataRek.getDate("LNDUEDT"));
				ComponentUtil.setValue(cmbRating, dataRek.getInt("RATING").toString());
				ComponentUtil.setValue(cmbStage, dataRek.getString("STAGE"));
				ComponentUtil.setValue(intDPD, dataRek.getInt("DPD"));
				decTunggPokok.setValue(dataRek.getBigDecimal("TUNGGPOKOK"));
				decTunggBunga.setValue(dataRek.getBigDecimal("TUNGGBUNGA"));
				decTunggDenda.setValue(dataRek.getBigDecimal("DENDA"));
				calcKeringanan();
				
				doLoadCashFlow(noRek, sdf.format(dataRek.getDate("TGL_POS")));
			}
		}
	}
	
	private void calcKeringanan() {
		BigDecimal tunggPokok=(BigDecimal) ComponentUtil.getValue(decTunggPokok);
		tunggPokok =tunggPokok==null?BigDecimal.ZERO:tunggPokok;
		
		BigDecimal tunggBunga=(BigDecimal) ComponentUtil.getValue(decTunggBunga);
		tunggBunga =tunggBunga==null?BigDecimal.ZERO:tunggBunga;
		
		BigDecimal tunggDenda=(BigDecimal) ComponentUtil.getValue(decTunggDenda);
		tunggDenda =tunggDenda==null?BigDecimal.ZERO:tunggDenda;
		
		BigDecimal keringananPokok=(BigDecimal) ComponentUtil.getValue(decKeringananPokok);
		keringananPokok =keringananPokok==null?BigDecimal.ZERO:keringananPokok;
		
		BigDecimal keringananBunga=(BigDecimal) ComponentUtil.getValue(decKeringananBunga);
		keringananBunga =keringananBunga==null?BigDecimal.ZERO:keringananBunga;
		
		BigDecimal keringananDenda=(BigDecimal) ComponentUtil.getValue(decKeringananDenda);
		keringananDenda =keringananDenda==null?BigDecimal.ZERO:keringananDenda;

		BigDecimal hasil = tunggPokok.subtract(keringananPokok);
		if(hasil.doubleValue() < 0){
			throw new WrongValueException(decKeringananPokok,"Keringanan Melebihi Tunggakan");
		}else{
			decSisaTunggPokok.setValue(hasil);
		}
		
		hasil = tunggBunga.subtract(keringananBunga);
		if(hasil.doubleValue() < 0){
			throw new WrongValueException(decKeringananBunga,"Keringanan Melebihi Tunggakan");
		}else{
			decSisaTunggBunga.setValue(hasil);
		}
		
		hasil = tunggDenda.subtract(keringananDenda);
		if(hasil.doubleValue() < 0){
			throw new WrongValueException(decKeringananDenda,"Keringanan Melebihi Tunggakan");
		}else{
			decSisaTunggDenda.setValue(hasil);
		}
	}
	
	private void doLoadCashFlow(String noRek,String TGL_POS) {
		SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
		
		if (listArusKasLama.getItemCount() > 0) {
			listArusKasLama.getItems().clear();
		}
		
		estimasiArusKasTot = 0;
		bungaKonversiTot = 0;
		angsuranPokokTot = 0;
		angsuranBungaTot = 0;
		selisihBungaKontraktualTot = 0;
		
		String sql = "SELECT * FROM CASHFLOW	"
				+ " WHERE ACCNBR=? AND TGL_POS=? ";
		System.out.println("noRek ="+noRek+"TGL_POS="+TGL_POS);
		List<DTOMap> lsCashFlow = masterService.getDataMaster(sql, new Object[]{noRek,TGL_POS});
		if (lsCashFlow.isEmpty() || lsCashFlow == null) {
			MessageBox.showInformation("Data Tidak Ditemukan");
		} else {
			for (DTOMap dtoMap : lsCashFlow) {
				int x = 1;
				Listitem liArusKas = new Listitem();
				liArusKas.appendChild(new Listcell(dtoMap.getInt("INSTL_SEQ").toString()));
				liArusKas.appendChild(new Listcell(sdf.format(dtoMap.getDate("TGL_ANGSUR"))));
				
				liArusKas.appendChild(new Listcell(FunctionUtils.moneyToText(dtoMap.getBigDecimal("ESTIMASI"))));
				if(dtoMap.getInt("INSTL_SEQ") == 0){
					liArusKas.appendChild(new Listcell(FunctionUtils.moneyToText(new BigDecimal (0.00))));
					dtoMap.put("NILAI_KINI_ARUS",new BigDecimal (0.00));
				}else{			
					liArusKas.appendChild(new Listcell(FunctionUtils.moneyToText(dtoMap.getBigDecimal("NILAI_KINI"))));	
				}
				
				liArusKas.appendChild(new Listcell(FunctionUtils.moneyToText(dtoMap.getBigDecimal("SALDO_AWAL"))));
				liArusKas.appendChild(new Listcell(FunctionUtils.moneyToText(dtoMap.getBigDecimal("INSTLINT"))));
				liArusKas.appendChild(new Listcell(FunctionUtils.moneyToText(dtoMap.getBigDecimal("BASENMNL"))));
				liArusKas.appendChild(new Listcell(FunctionUtils.moneyToText(dtoMap.getBigDecimal("INTNMNL"))));
				liArusKas.appendChild(new Listcell(FunctionUtils.moneyToText(dtoMap.getBigDecimal("AMOREIR"))));
				liArusKas.appendChild(new Listcell(FunctionUtils.moneyToText(dtoMap.getBigDecimal("ENDBAL"))));
				liArusKas.appendChild(new Listcell(FunctionUtils.moneyToText(dtoMap.getBigDecimal("TEORIBAL"))));

				estimasiArusKasTot += dtoMap.getBigDecimal("ESTIMASI").doubleValue();
				bungaKonversiTot += dtoMap.getBigDecimal("INSTLINT").doubleValue();
				
				nilaiKini += dtoMap.getBigDecimal("NILAI_KINI").doubleValue();
				angsuranPokokTot += dtoMap.getBigDecimal("BASENMNL").doubleValue();
				angsuranBungaTot += dtoMap.getBigDecimal("INTNMNL").doubleValue();
				selisihBungaKontraktualTot += dtoMap.getBigDecimal("AMOREIR").doubleValue();
				SBE=dtoMap.getBigDecimal("SBE");
				listArusKasLama.appendChild(liArusKas);
			}
			DecimalFormat df = new DecimalFormat("#,##0.00");
			
			ComponentUtil.setValue(decArus1Lama, df.format(estimasiArusKasTot));
			ComponentUtil.setValue(decArus2Lama, df.format(nilaiKini));
			ComponentUtil.setValue(decArus4Lama, df.format(bungaKonversiTot));
			ComponentUtil.setValue(decArus5Lama, df.format(angsuranPokokTot));
			ComponentUtil.setValue(decArus6Lama, df.format(angsuranBungaTot));
			ComponentUtil.setValue(decArus7Lama, df.format(selisihBungaKontraktualTot));
			decIrrLama.setValue(SBE);
			decEIR.setValue(SBE);
		}
	}
	
	private void doReset() {
		wnd.getChildren().clear();
		
		if (listArusKasLama.getItemCount() > 0) {
			listArusKasLama.getItems().clear();
		}
		if (listJadwalAngsur.getItemCount() > 0) {
			listJadwalAngsur.getItems().clear();
		}
		if (listArusKasBaru.getItemCount() > 0) {
			listArusKasBaru.getItems().clear();
		}
		
		listAllRow.clear();
		listAngsuran.clear();
		listAngsuranUpload.clear();
		listErr.clear();
		listBulanTahun.clear();
		
		isUpload=false;
		
		txtNoRekening.setFocus(true);
	}
}
