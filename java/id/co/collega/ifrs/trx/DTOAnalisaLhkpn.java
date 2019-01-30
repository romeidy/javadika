package id.co.collega.ifrs.trx;

import java.util.Date;

import com.jet.gand.annotation.CIPWidget;
import com.jet.gand.enumerasi.JenisWidgetEnum;
import com.jet.gand.utils.CIPCmpFormattedText;

public class DTOAnalisaLhkpn {

	
	@CIPWidget(text = "CIFID", widget = JenisWidgetEnum.TEXT, enable=false)
	private String CIFID;
	
	@CIPWidget(text = "Nama Lengkap", widget = JenisWidgetEnum.TEXT,sizeHeader="300px",size="230px", enable=false)
	private String NAMA_LENGKAP;
	

	@CIPWidget(text = "Tgl Lahir",size="120px", widget = JenisWidgetEnum.FTEXT,format=CIPCmpFormattedText.DATE_FORMAT,pattern="dd/MM/yyyy", enable=false)
	private Date TANGGAL_LAHIR;
	
	
	@CIPWidget(text = "Alamat", widget = JenisWidgetEnum.TEXT, enable=false,sizeHeader="300px")
	private String ALAMAT_PEM;


	public String getCIFID() {
		return CIFID;
	}


	public void setCIFID(String cIFID) {
		CIFID = cIFID;
	}


	public String getNAMA_LENGKAP() {
		return NAMA_LENGKAP;
	}


	public void setNAMA_LENGKAP(String nAMA_LENGKAP) {
		NAMA_LENGKAP = nAMA_LENGKAP;
	}


	public Date getTANGGAL_LAHIR() {
		return TANGGAL_LAHIR;
	}


	public void setTANGGAL_LAHIR(Date tANGGAL_LAHIR) {
		TANGGAL_LAHIR = tANGGAL_LAHIR;
	}


	public String getALAMAT_PEM() {
		return ALAMAT_PEM;
	}


	public void setALAMAT_PEM(String aLAMAT_PEM) {
		ALAMAT_PEM = aLAMAT_PEM;
	}
	
}
