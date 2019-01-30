package id.co.collega.ifrs.master;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.zkoss.image.AImage;
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
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vlayout;
import org.zkoss.zul.Window;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import id.co.collega.ifrs.common.DTOMap;
import id.co.collega.ifrs.master.service.MasterServices;
import id.co.collega.ifrs.util.ComponentUtil;
import id.co.collega.ifrs.util.MessageBox;

@org.springframework.stereotype.Component
@Scope("execution")
public class WndKonfigurasiSystem extends SelectorComposer<Component>{
	
	@Wire Window wnd;
	@Wire Textbox txtKdBank;
	@Wire Textbox txtNmBank;
	@Wire Textbox txtAlamat;
	@Wire Textbox txtHostEngine;
	@Wire Textbox txtJam;
	@Wire Textbox txtMenit;
	
	@Wire Label lbljam;
	@Wire Label lblmenit;
	
	@Wire Radiogroup rdoAutoProses;
	@Wire Radiogroup rdoAudit;
	
	@Wire Button btnSimpan;
	@Wire Button btnReset;
	@Wire Button btnUpload;
	
	@Wire Vlayout imgLogo;
//	@Wire Vlayout imgLogo2;
	
	@Autowired MasterServices masterService;
	
	boolean onLoad = false;
	String imageString;
	String KD_BANK_OLD="";
	public void doSave(){
		
	}
	
	public void doUpdate(){
		
		if(validation()){
			final String ProsesTime;
//			String jam_final;
//			String menit_final;
//			String jam_final_sub;
//			String menit_final_sub;
			String jam = (String)ComponentUtil.getValue(txtJam);
			String menit = (String)ComponentUtil.getValue(txtMenit);
//			String jam_sub = jam.substring(0,1);
//			String menit_sub = menit.substring(0,1);
//			int jam_final_sub_parse = Integer.parseInt(jam_sub);
//			int menit_final_sub_parse = Integer.parseInt(menit_sub);
//			if (jam_final_sub_parse == 0) {
//				jam_final_sub = jam.substring(1,2);
//			}else{
//				jam_final_sub = (String)ComponentUtil.getValue(txtJam);
//			}
//			if (menit_final_sub_parse == 0) {
//				menit_final_sub = menit.substring(1,2);
//			}else{
//				menit_final_sub = (String)ComponentUtil.getValue(txtMenit);
//			}
			ProsesTime = jam+":"+menit ;
			Messagebox.show("Apakah Anda Yakin Update data ini ..?","KONFIRMASI",
					Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, new EventListener<Event>(){
				
		@Override
		public void onEvent(Event e) throws Exception {
				if(Messagebox.ON_OK.equals(e.getName())) {
					DTOMap map = new DTOMap();
					map.put("KD_BANK", ComponentUtil.getValue(txtKdBank));
					map.put("NAMA_BANK", ComponentUtil.getValue(txtNmBank));
					map.put("ALAMAT_BANK", ComponentUtil.getValue(txtAlamat));
					map.put("HOST_ENGINE", ComponentUtil.getValue(txtHostEngine));
					map.put("LOGO_BANK", imageString);
					map.put("PROCESS_TIME",ProsesTime);
					map.put("FLG_PROCESS", ComponentUtil.getValue(rdoAutoProses));
					map.put("FLG_AUDIT", ComponentUtil.getValue(rdoAudit));
					map.put("PK", "KD_BANK");
					masterService.updateData(map, "CFG_SYS");
					
					MessageBox.showInformation("Data Berhasil di Update");
//						doReset();
					}else if(Messagebox.ON_CANCEL.equals(e.getName())){
//						doReset();
					}
					
				}
				
			});
			
		}
	}
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		txtKdBank.addEventListener(Events.ON_OK, new EventListener(){
			public void onEvent(Event e) throws Exception {
				doSearch();
				
			}
		});
		
		
		btnSimpan.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(Event e) throws Exception {
				doUpdate();
			}
		});
		
		rdoAutoProses.addEventListener(Events.ON_CLICK, new EventListener(){
			public void onEvent(Event e) throws Exception {
				Integer jenisProses=(Integer)ComponentUtil.getValue(rdoAutoProses);
				if(jenisProses == 0){
            		lbljam.setVisible(false);
            		lblmenit.setVisible(false);
            		txtJam.setVisible(false);
            		txtMenit.setVisible(false);
            	}else{
            		lbljam.setVisible(true);
            		lblmenit.setVisible(true);
            		txtJam.setVisible(true);
            		txtMenit.setVisible(true);
            	}
			}
		});
		
		btnReset.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(Event e) throws Exception {
				doReset();
			}
		});
		txtJam.addEventListener(Events.ON_BLUR, new EventListener<Event>() {
			public void onEvent(Event e) throws Exception {
				doTimeValidasi();
			}
		});
		txtMenit.addEventListener(Events.ON_BLUR, new EventListener<Event>() {
			public void onEvent(Event e) throws Exception {
				doTimeValidasi();
			}
		});
		
		btnUpload.addEventListener(Events.ON_UPLOAD, new EventListener<Event>() {
			public void onEvent(Event e) throws Exception {
				
				String encodedFile;
				Integer maxHeight = 64;
				Integer maxWidth = 184;
				
				
				UploadEvent evnt = (UploadEvent) e;
				org.zkoss.util.media.Media media = evnt.getMedia();
								
//				String[] a = media.getName().split("\\.");
//                String ext = a[a.length - 1];
				String ext = media.getFormat();
                if(ext.equalsIgnoreCase("png") || ext.equalsIgnoreCase("jpg") || ext.equalsIgnoreCase("jpeg")){
                	if (media instanceof org.zkoss.image.Image) {
                		
        				org.zkoss.zul.Image image = new org.zkoss.zul.Image();
        					  imgLogo.getChildren().clear();
        					  System.out.println(((org.zkoss.image.Image) media).getHeight());
        					  System.out.println(((org.zkoss.image.Image) media).getWidth());
        					  
        					  BufferedImage imageSize = ImageIO.read(media.getStreamData());
        					  BufferedImage resized = resize(imageSize, 64, 184);
        					  
        					  image.setContent(resized);
        					  image.setParent(imgLogo);
        					  
        					  System.out.println("Ukuran Height : "+resized.getHeight());
        					  System.out.println("Ukuran Width	: "+resized.getWidth());
        					  
        					  
//        					  image.setContent((org.zkoss.image.Image) media);
//              				  image.setParent(imgLogo);
        					  
        					  
              				  doEncodeToString(resized);
              				  
                	}else {
//           				 Messagebox.show("Ukuran gambar harus 184px x 64px");
           				Messagebox.show("File bukan gambar : "+media, "Error", Messagebox.OK, Messagebox.ERROR);
           				 doSearch();
          			  	}
                	}else {
                		
                		MessageBox.showInformation("Ekstensi file bukan .PNG / .JPG / .JPEG ekstensi gambar anda : "+media.getFormat());
                	}
                }
			
		});
		doSearch();
	}
		
	public void doReset() {
		ComponentUtil.setValue(txtKdBank, null);
		ComponentUtil.setValue(txtNmBank, null);
		ComponentUtil.setValue(txtAlamat, null);
		ComponentUtil.setValue(txtHostEngine, null);
		imgLogo.getChildren().clear();
		onLoad = false;
		KD_BANK_OLD="";
	}
	
	private void doEdit(DTOMap data){
		if (data != null){
			int FlgProses = (Integer)data.get("FLG_PROCESS");
			if (FlgProses == 0) {
        		lbljam.setVisible(false);
        		lblmenit.setVisible(false);
        		txtJam.setVisible(false);
        		txtMenit.setVisible(false);
			}else{
        		lbljam.setVisible(true);
        		lblmenit.setVisible(true);
        		txtJam.setVisible(true);
        		txtMenit.setVisible(true);
        		String Time = (String)data.get("PROCESS_TIME");
    			ComponentUtil.setValue(txtJam, Time.substring(0,2));
    			ComponentUtil.setValue(txtMenit, Time.substring(3));
			}
			ComponentUtil.setValue(txtKdBank, data.get("KD_BANK"));
			ComponentUtil.setValue(txtNmBank, data.get("NAMA_BANK"));
			ComponentUtil.setValue(txtAlamat, data.get("ALAMAT_BANK"));
			ComponentUtil.setValue(txtHostEngine, data.get("HOST_ENGINE"));
			ComponentUtil.setValue(rdoAutoProses, data.get("FLG_PROCESS"));
			ComponentUtil.setValue(rdoAudit, data.get("FLG_AUDIT"));
			try{
				String imageString = (String) data.get("LOGO_BANK");
				
				doDecodeFromString(imageString);				
			} catch(Exception e){
				MessageBox.showInformation("Logo Bank belum ada : "+ e);
			}
			KD_BANK_OLD=data.getString("KD_BANK");
			onLoad = true;
		}
	}
	
	private void doDecodeFromString(String imageString) {
		byte[] decodedBytes = Base64.decode(imageString);
		org.zkoss.zul.Image image = new org.zkoss.zul.Image();			
		try {
			image.setContent(new AImage("png", decodedBytes));
			image.setParent(imgLogo);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void doEncodeToString(BufferedImage resized){
//		byte[] bit = resized.getByteData();
		try {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(resized, "png", baos);
		byte[] imageBit = baos.toByteArray();
		baos.close();
		System.out.println("Image Buffered to Bit : " +imageBit);
		String encodedImage = Base64.encode(imageBit);
		System.out.println("Image Encode : " +encodedImage);
		imageString = encodedImage;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
		
	private void doSearch(){
//		DTOMap Kdbank = masterService.getMapMaster("SELECT KD_BANK FROM CFG_SYS", new Object[]{});
		DTOMap datas = new DTOMap();
//		String kdbank = "116";
		datas = masterService.getMapMaster("SELECT KD_BANK, NAMA_BANK, LOGO_BANK, ALAMAT_BANK, HOST_ENGINE, PROCESS_TIME, FLG_PROCESS, FLG_AUDIT FROM CFG_SYS", new Object[]{});
		imageString = datas.getString("LOGO_BANK");
		doEdit(datas);
	}
	
	private boolean validation() {
		boolean isValid = true;
		if(isValid){
			List wrongValue = new ArrayList(0);
			String kdbnk = (String) ComponentUtil.getValue(txtKdBank);
			if(kdbnk == null){
				wrongValue.add(new WrongValueException(txtKdBank, "Kode Bank Harus Diisi"));
			}
			String nmbank = (String) ComponentUtil.getValue(txtNmBank);
			if(nmbank == null){
				wrongValue.add(new WrongValueException(txtNmBank, "Nama Bank Harus Diisi"));	
			}
			String almtbank = (String) ComponentUtil.getValue(txtNmBank);
			if(almtbank == null){
				wrongValue.add(new WrongValueException(txtAlamat, "Alamat Bank Harus Diisi"));	
			}
			if (wrongValue.size() > 0) {
				throw new WrongValuesException(
						(WrongValueException[]) wrongValue.toArray(new WrongValueException[wrongValue.size()]));
			}
		}
		return isValid;
	}
	
	public void onUpload(UploadEvent event){
		
	}
	
	private static BufferedImage resize(BufferedImage img, int height, int width){
		java.awt.Image tmp = img.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);
		BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		java.awt.Graphics2D g2d = resized.createGraphics();
		g2d.drawImage(tmp, 0, 0, null);
		g2d.dispose();
		return resized;
		
	}
	private void doTimeValidasi(){
		String jam_final_sub;
		String menit_final_sub;
		String jam = (String)ComponentUtil.getValue(txtJam);
		String menit = (String)ComponentUtil.getValue(txtMenit);
		int jam_final = Integer.parseInt((String)ComponentUtil.getValue(txtJam));
		int menit_final = Integer.parseInt((String)ComponentUtil.getValue(txtMenit));
		String jam_sub = jam.substring(0,1);
		String menit_sub = menit.substring(0,1);
		if (jam.length() < 2) {
			ComponentUtil.setValue(txtJam, "0"+jam);
		}
		if (menit.length() < 2) {
			ComponentUtil.setValue(txtMenit, "0"+menit);
		}
		if (jam_final > 24) {
			throw new WrongValueException(txtJam,"Jam tidak boleh melebihi 24");
		}
		if (menit_final > 0 && jam_final == 24){
			throw new WrongValueException(txtMenit,"Mximal jam 24, menit harus 0");
		}
		if (jam_final < 24 && menit_final == 60){
			ComponentUtil.setValue(txtJam, jam_final+1);
			ComponentUtil.setValue(txtJam, 0);
		}
		if (menit_final > 60){
			throw new WrongValueException(txtMenit,"Mximal 60 menit");
		}
	}

}
