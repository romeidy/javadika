package id.co.collega.ifrs.trx;

import java.math.BigDecimal;
import java.util.Date;

import com.jet.gand.annotation.CIPWidget;
import com.jet.gand.enumerasi.JenisWidgetEnum;
import com.jet.gand.utils.CIPCmpFormattedText;

import id.co.collega.ifrs.common.DTOMap;

public class DTORptPns {

	@CIPWidget(text = "Tgl Tx",size="120px", widget = JenisWidgetEnum.FTEXT,format=CIPCmpFormattedText.DATE_FORMAT,pattern="dd/MM/yyyy", enable=false)
	private Date TGL_TX;
	
	@CIPWidget(text = "CIFID", widget = JenisWidgetEnum.TEXT, enable=false)
	private String CIFID;
	
	@CIPWidget(text = "No Rekening", widget = JenisWidgetEnum.TEXT, enable=false)
	private String NO_REKENING;
	
	@CIPWidget(text = "Nama Lengkap", widget = JenisWidgetEnum.TEXT,sizeHeader="300px",size="230px", enable=false)
	private String NAMA_LENGKAP;
	
	@CIPWidget(text = "Keterangan", widget = JenisWidgetEnum.TEXT, enable=false,sizeHeader="300px")
	private String KETERANGAN_TX;
	
	@CIPWidget(text = "Jumlah Tx", widget = JenisWidgetEnum.LABEL,sizeHeader="100px",visibleOnEdit=false)
	private BigDecimal JUMLAH_TX;
	
	@CIPWidget(text = "D/K", widget = JenisWidgetEnum.TEXT,sizeHeader="50px")
	private String DK;
//	




	@CIPWidget(text = "Cab Tx", widget = JenisWidgetEnum.LABEL,sizeHeader="150px")
	private String KD_CAB_TX;
	
	private String KD_CAB;
	
	
//	keperluan detil

	public String getKD_CAB() {
		return KD_CAB;
	}

	public void setKD_CAB(String kD_CAB) {
		KD_CAB = kD_CAB;
	}

	@CIPWidget(text = "Sk Waktu", widget = JenisWidgetEnum.LABEL,intable=false)
	private Integer SK_WAKTU;
	
	private DTOMap mapSumber;
	
	public DTOMap getMapSumber() {
		return mapSumber;
	}

	public void setMapSumber(DTOMap mapSumber) {
		this.mapSumber = mapSumber;
	}

	public Integer getSK_WAKTU() {
		return SK_WAKTU;
	}

	public void setSK_WAKTU(Integer sK_WAKTU) {
		SK_WAKTU = sK_WAKTU;
	}

	public String getDK() {
		return DK;
	}

	public void setDK(String dK) {
		DK = dK;
	}

	public Date getTGL_TX() {
		return TGL_TX;
	}

	public void setTGL_TX(Date tGL_TX) {
		TGL_TX = tGL_TX;
	}

	public String getCIFID() {
		return CIFID;
	}

	public void setCIFID(String cIFID) {
		CIFID = cIFID;
	}

	public String getNO_REKENING() {
		return NO_REKENING;
	}

	public void setNO_REKENING(String nO_REKENING) {
		NO_REKENING = nO_REKENING;
	}

	public String getNAMA_LENGKAP() {
		return NAMA_LENGKAP;
	}

	public void setNAMA_LENGKAP(String nAMA_LENGKAP) {
		NAMA_LENGKAP = nAMA_LENGKAP;
	}

	public String getKETERANGAN_TX() {
		return KETERANGAN_TX;
	}

	public void setKETERANGAN_TX(String kETERANGAN_TX) {
		KETERANGAN_TX = kETERANGAN_TX;
	}

	public BigDecimal getJUMLAH_TX() {
		return JUMLAH_TX;
	}

	public void setJUMLAH_TX(BigDecimal jUMLAH_TX) {
		JUMLAH_TX = jUMLAH_TX;
	}

	
	public String getKD_CAB_TX() {
		return KD_CAB_TX;
	}

	public void setKD_CAB_TX(String kD_CAB_TX) {
		KD_CAB_TX = kD_CAB_TX;
	}
}
