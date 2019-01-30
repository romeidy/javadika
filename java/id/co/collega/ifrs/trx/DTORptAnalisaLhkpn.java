package id.co.collega.ifrs.trx;

import java.math.BigDecimal;
import java.util.Date;

import com.jet.gand.annotation.CIPWidget;
import com.jet.gand.enumerasi.JenisWidgetEnum;
import com.jet.gand.utils.CIPCmpFormattedText;

import id.co.collega.ifrs.common.DTOMap;

public class DTORptAnalisaLhkpn {

	@CIPWidget(text = "No Nsb", widget = JenisWidgetEnum.TEXT, enable=false)
	private String CIFID;
	
	@CIPWidget(text = "Nama", widget = JenisWidgetEnum.TEXT,sizeHeader="200px",size="270px", enable=false)
	private String NAMA_LENGKAP;
	
	@CIPWidget(text = "Kd Jenis Nsb", widget = JenisWidgetEnum.TEXT, intable=false)
	private String KD_JNS_NSB;
	
	@CIPWidget(text = "Tempat Lahir", widget = JenisWidgetEnum.TEXT, intable=false)
	private String TEMPAT_LAHIR;
	
	@CIPWidget(text = "Tanggal Lahir",size="120px", widget = JenisWidgetEnum.FTEXT,format=CIPCmpFormattedText.DATE_FORMAT,pattern="dd/MM/yyyy", enable=false)
	private Date TANGGAL_LAHIR;
	
	@CIPWidget(text = "Kd Pekerjaan", widget = JenisWidgetEnum.TEXT, intable=false)
	private String KD_PEKERJAAN;
	
	@CIPWidget(text = "Bidang Usaha", widget = JenisWidgetEnum.TEXT, intable=false)
	private String BIDANG_USAHA;
	
	@CIPWidget(text = "Instansi Induk", widget = JenisWidgetEnum.TEXT, intable=false)
	private String INSTANSI_INDUK;
	
	@CIPWidget(text = "Unit Kerja", widget = JenisWidgetEnum.TEXT, intable=false)
	private String UNIT_KERJA;
	
	@CIPWidget(text = "Golongan", widget = JenisWidgetEnum.TEXT, intable=false)
	private String GOLONGAN;
	
	@CIPWidget(text = "Rata2 Tx", widget = JenisWidgetEnum.TEXT, intable=false)
	private BigDecimal RATA_TX;
	
	@CIPWidget(text = "Alamat", widget = JenisWidgetEnum.TEXT, enable=false,sizeHeader="370px")
	private String ALAMAT_PEM;
	
	@CIPWidget(text = "Kota", widget = JenisWidgetEnum.TEXT, intable=false)
	private String KOTA;
	
	
	@CIPWidget(text = "Kd Identitas", widget = JenisWidgetEnum.TEXT, intable=false)
	private String KD_IDENTITAS;
	
	@CIPWidget(text = "No Identitas", widget = JenisWidgetEnum.TEXT, intable=false)
	private String NO_IDENTITAS;
	
	@CIPWidget(text = "Tgl Buka Rek", widget = JenisWidgetEnum.TEXT, intable=false)
	private Date TGL_BUKA_REK;
	
	@CIPWidget(text = "Kd Cabang", widget = JenisWidgetEnum.TEXT, intable=false)
	private String KD_CAB;
	
	@CIPWidget(text = "Sum Debet", widget = JenisWidgetEnum.TEXT, intable=false)
	private BigDecimal SUM_DEBET;
	
	@CIPWidget(text = "Count Debet", widget = JenisWidgetEnum.TEXT, intable=false)
	private Integer COUNT_DEBET;
	
	@CIPWidget(text = "Sum Kredit", widget = JenisWidgetEnum.TEXT, intable=false)
	private BigDecimal SUM_KREDIT;
	
	@CIPWidget(text = "Count Kredit", widget = JenisWidgetEnum.TEXT, intable=false)
	private Integer COUNT_KREDIT;
	
	@CIPWidget(text = "Sum Tarik Tunai", widget = JenisWidgetEnum.TEXT, intable=false)
	private BigDecimal SUM_TARIK_TUNAI;
	
	@CIPWidget(text = "Count Tarik Tunai", widget = JenisWidgetEnum.TEXT, intable=false)
	private Integer COUNT_TARIK_TUNAI;
	
	@CIPWidget(text = "Sum Setor Tunai", widget = JenisWidgetEnum.TEXT, intable=false)
	private BigDecimal SUM_SETOR_TUNAI;
	
	@CIPWidget(text = "Count Setor Tunai", widget = JenisWidgetEnum.TEXT, intable=false)
	private Integer COUNT_SETOR_TUNAI;
	
	@CIPWidget(text = "Tx Max Debet", widget = JenisWidgetEnum.TEXT, intable=false)
	private BigDecimal TRX_MAX_DEBET;
	
	@CIPWidget(text = "Kode Tx Max Debet", widget = JenisWidgetEnum.TEXT, intable=false)
	private String KD_TX_MAX_DEBET;
	
	@CIPWidget(text = "Ket Tx Max Debet", widget = JenisWidgetEnum.TEXT, intable=false)
	private String KET_TX_MAX_DEBET;
	
	@CIPWidget(text = "No Arsip Max Debet", widget = JenisWidgetEnum.TEXT, intable=false)
	private String NO_ARSIP_MAX_DEBET;
	
	@CIPWidget(text = "Cab Lok Max Debet", widget = JenisWidgetEnum.TEXT, intable=false)
	private String KD_CAB_LOK_MAX_DEBET;
	
	
	@CIPWidget(text = "Tx Max Kredit", widget = JenisWidgetEnum.TEXT, intable=false)
	private BigDecimal TRX_MAX_KREDIT;
	
	@CIPWidget(text = "Tgl Tx Max Kredit", widget = JenisWidgetEnum.TEXT, intable=false)
	private Date TGL_TX_MAX_KREDIT;
	
	@CIPWidget(text = "Kode Tx Max Kredit", widget = JenisWidgetEnum.TEXT, intable=false)
	private String KD_TX_MAX_KREDIT;
	
	@CIPWidget(text = "Ket Tx Max Kredit", widget = JenisWidgetEnum.TEXT, intable=false)
	private String KET_TX_MAX_KREDIT;
	
	@CIPWidget(text = "No Arsip Max Kredit", widget = JenisWidgetEnum.TEXT, intable=false)
	private String NO_ARSIP_MAX_KREDIT;
	
	@CIPWidget(text = "Cab Lok Max Kredit", widget = JenisWidgetEnum.TEXT, intable=false)
	private String KD_CAB_LOK_MAX_KREDIT;
	
	@CIPWidget(text = "Sum Jumlah Tx", widget = JenisWidgetEnum.TEXT, intable=false)
	private BigDecimal SUM_JUMLAH_TX;
	
	@CIPWidget(text = "Avg Jumlah Tx", widget = JenisWidgetEnum.TEXT, intable=false)
	private BigDecimal AVG_JUMLAH_TX;
	
	@CIPWidget(text = "Deviasi Jumlah Tx", widget = JenisWidgetEnum.TEXT, intable=false)
	private BigDecimal DEVIASI_JUMLAH_TX;
	
	@CIPWidget(text = "Sk Waktu", widget = JenisWidgetEnum.LABEL,intable=false)
	private Integer SK_WAKTU;
	
	@CIPWidget(text = "Cab Tx", widget = JenisWidgetEnum.LABEL,sizeHeader="150px")
	private String KD_CAB_TX;
	
	@CIPWidget(text = "No Rekening", widget = JenisWidgetEnum.TEXT, enable=false, sizeHeader="100px")
	private String NO_REKENING;
	
	private DTOMap mapSumber;
	
	public DTOMap getMapSumber() {
		return mapSumber;
	}

	public void setMapSumber(DTOMap mapSumber) {
		this.mapSumber = mapSumber;
	}
	
	
	public String getKD_CAB_TX() {
		return KD_CAB_TX;
	}
	public void setKD_CAB_TX(String kD_CAB_TX) {
		KD_CAB_TX = kD_CAB_TX;
	}
	public String getNO_REKENING() {
		return NO_REKENING;
	}
	public void setNO_REKENING(String nO_REKENING) {
		NO_REKENING = nO_REKENING;
	}
	public Integer getSK_WAKTU() {
		return SK_WAKTU;
	}
	public void setSK_WAKTU(Integer sK_WAKTU) {
		SK_WAKTU = sK_WAKTU;
	}
	
	
	
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
	public String getKD_JNS_NSB() {
		return KD_JNS_NSB;
	}
	public void setKD_JNS_NSB(String kD_JNS_NSB) {
		KD_JNS_NSB = kD_JNS_NSB;
	}
	public String getTEMPAT_LAHIR() {
		return TEMPAT_LAHIR;
	}
	public void setTEMPAT_LAHIR(String tEMPAT_LAHIR) {
		TEMPAT_LAHIR = tEMPAT_LAHIR;
	}
	public Date getTANGGAL_LAHIR() {
		return TANGGAL_LAHIR;
	}
	public void setTANGGAL_LAHIR(Date tANGGAL_LAHIR) {
		TANGGAL_LAHIR = tANGGAL_LAHIR;
	}
	public String getKD_PEKERJAAN() {
		return KD_PEKERJAAN;
	}
	public void setKD_PEKERJAAN(String kD_PEKERJAAN) {
		KD_PEKERJAAN = kD_PEKERJAAN;
	}
	public String getBIDANG_USAHA() {
		return BIDANG_USAHA;
	}
	public void setBIDANG_USAHA(String bIDANG_USAHA) {
		BIDANG_USAHA = bIDANG_USAHA;
	}
	public String getINSTANSI_INDUK() {
		return INSTANSI_INDUK;
	}
	public void setINSTANSI_INDUK(String iNSTANSI_INDUK) {
		INSTANSI_INDUK = iNSTANSI_INDUK;
	}
	public String getUNIT_KERJA() {
		return UNIT_KERJA;
	}
	public void setUNIT_KERJA(String uNIT_KERJA) {
		UNIT_KERJA = uNIT_KERJA;
	}
	public String getGOLONGAN() {
		return GOLONGAN;
	}
	public void setGOLONGAN(String gOLONGAN) {
		GOLONGAN = gOLONGAN;
	}
	public BigDecimal getRATA_TX() {
		return RATA_TX;
	}
	public void setRATA_TX(BigDecimal rATA_TX) {
		RATA_TX = rATA_TX;
	}
	public String getALAMAT_PEM() {
		return ALAMAT_PEM;
	}
	public void setALAMAT_PEM(String aLAMAT_PEM) {
		ALAMAT_PEM = aLAMAT_PEM;
	}
	public String getKOTA() {
		return KOTA;
	}
	public void setKOTA(String kOTA) {
		KOTA = kOTA;
	}
	public String getKD_IDENTITAS() {
		return KD_IDENTITAS;
	}
	public void setKD_IDENTITAS(String kD_IDENTITAS) {
		KD_IDENTITAS = kD_IDENTITAS;
	}
	public String getNO_IDENTITAS() {
		return NO_IDENTITAS;
	}
	public void setNO_IDENTITAS(String nO_IDENTITAS) {
		NO_IDENTITAS = nO_IDENTITAS;
	}
	public Date getTGL_BUKA_REK() {
		return TGL_BUKA_REK;
	}
	public void setTGL_BUKA_REK(Date tGL_BUKA_REK) {
		TGL_BUKA_REK = tGL_BUKA_REK;
	}
	public String getKD_CAB() {
		return KD_CAB;
	}
	public void setKD_CAB(String kD_CAB) {
		KD_CAB = kD_CAB;
	}
	public BigDecimal getSUM_DEBET() {
		return SUM_DEBET;
	}
	public void setSUM_DEBET(BigDecimal sUM_DEBET) {
		SUM_DEBET = sUM_DEBET;
	}
	public Integer getCOUNT_DEBET() {
		return COUNT_DEBET;
	}
	public void setCOUNT_DEBET(Integer cOUNT_DEBET) {
		COUNT_DEBET = cOUNT_DEBET;
	}
	public BigDecimal getSUM_KREDIT() {
		return SUM_KREDIT;
	}
	public void setSUM_KREDIT(BigDecimal sUM_KREDIT) {
		SUM_KREDIT = sUM_KREDIT;
	}
	public Integer getCOUNT_KREDIT() {
		return COUNT_KREDIT;
	}
	public void setCOUNT_KREDIT(Integer cOUNT_KREDIT) {
		COUNT_KREDIT = cOUNT_KREDIT;
	}
	public BigDecimal getSUM_TARIK_TUNAI() {
		return SUM_TARIK_TUNAI;
	}
	public void setSUM_TARIK_TUNAI(BigDecimal sUM_TARIK_TUNAI) {
		SUM_TARIK_TUNAI = sUM_TARIK_TUNAI;
	}
	public Integer getCOUNT_TARIK_TUNAI() {
		return COUNT_TARIK_TUNAI;
	}
	public void setCOUNT_TARIK_TUNAI(Integer cOUNT_TARIK_TUNAI) {
		COUNT_TARIK_TUNAI = cOUNT_TARIK_TUNAI;
	}
	public BigDecimal getSUM_SETOR_TUNAI() {
		return SUM_SETOR_TUNAI;
	}
	public void setSUM_SETOR_TUNAI(BigDecimal sUM_SETOR_TUNAI) {
		SUM_SETOR_TUNAI = sUM_SETOR_TUNAI;
	}
	public Integer getCOUNT_SETOR_TUNAI() {
		return COUNT_SETOR_TUNAI;
	}
	public void setCOUNT_SETOR_TUNAI(Integer cOUNT_SETOR_TUNAI) {
		COUNT_SETOR_TUNAI = cOUNT_SETOR_TUNAI;
	}
	public BigDecimal getTRX_MAX_DEBET() {
		return TRX_MAX_DEBET;
	}
	public void setTRX_MAX_DEBET(BigDecimal tRX_MAX_DEBET) {
		TRX_MAX_DEBET = tRX_MAX_DEBET;
	}
	public String getKD_TX_MAX_DEBET() {
		return KD_TX_MAX_DEBET;
	}
	public void setKD_TX_MAX_DEBET(String kD_TX_MAX_DEBET) {
		KD_TX_MAX_DEBET = kD_TX_MAX_DEBET;
	}
	public String getKET_TX_MAX_DEBET() {
		return KET_TX_MAX_DEBET;
	}
	public void setKET_TX_MAX_DEBET(String kET_TX_MAX_DEBET) {
		KET_TX_MAX_DEBET = kET_TX_MAX_DEBET;
	}
	public String getNO_ARSIP_MAX_DEBET() {
		return NO_ARSIP_MAX_DEBET;
	}
	public void setNO_ARSIP_MAX_DEBET(String nO_ARSIP_MAX_DEBET) {
		NO_ARSIP_MAX_DEBET = nO_ARSIP_MAX_DEBET;
	}
	public String getKD_CAB_LOK_MAX_DEBET() {
		return KD_CAB_LOK_MAX_DEBET;
	}
	public void setKD_CAB_LOK_MAX_DEBET(String kD_CAB_LOK_MAX_DEBET) {
		KD_CAB_LOK_MAX_DEBET = kD_CAB_LOK_MAX_DEBET;
	}
	public BigDecimal getTRX_MAX_KREDIT() {
		return TRX_MAX_KREDIT;
	}
	public void setTRX_MAX_KREDIT(BigDecimal tRX_MAX_KREDIT) {
		TRX_MAX_KREDIT = tRX_MAX_KREDIT;
	}
	public Date getTGL_TX_MAX_KREDIT() {
		return TGL_TX_MAX_KREDIT;
	}
	public void setTGL_TX_MAX_KREDIT(Date tGL_TX_MAX_KREDIT) {
		TGL_TX_MAX_KREDIT = tGL_TX_MAX_KREDIT;
	}
	public String getKD_TX_MAX_KREDIT() {
		return KD_TX_MAX_KREDIT;
	}
	public void setKD_TX_MAX_KREDIT(String kD_TX_MAX_KREDIT) {
		KD_TX_MAX_KREDIT = kD_TX_MAX_KREDIT;
	}
	public String getKET_TX_MAX_KREDIT() {
		return KET_TX_MAX_KREDIT;
	}
	public void setKET_TX_MAX_KREDIT(String kET_TX_MAX_KREDIT) {
		KET_TX_MAX_KREDIT = kET_TX_MAX_KREDIT;
	}
	public String getNO_ARSIP_MAX_KREDIT() {
		return NO_ARSIP_MAX_KREDIT;
	}
	public void setNO_ARSIP_MAX_KREDIT(String nO_ARSIP_MAX_KREDIT) {
		NO_ARSIP_MAX_KREDIT = nO_ARSIP_MAX_KREDIT;
	}
	public String getKD_CAB_LOK_MAX_KREDIT() {
		return KD_CAB_LOK_MAX_KREDIT;
	}
	public void setKD_CAB_LOK_MAX_KREDIT(String kD_CAB_LOK_MAX_KREDIT) {
		KD_CAB_LOK_MAX_KREDIT = kD_CAB_LOK_MAX_KREDIT;
	}
	public BigDecimal getSUM_JUMLAH_TX() {
		return SUM_JUMLAH_TX;
	}
	public void setSUM_JUMLAH_TX(BigDecimal sUM_JUMLAH_TX) {
		SUM_JUMLAH_TX = sUM_JUMLAH_TX;
	}
	public BigDecimal getAVG_JUMLAH_TX() {
		return AVG_JUMLAH_TX;
	}
	public void setAVG_JUMLAH_TX(BigDecimal aVG_JUMLAH_TX) {
		AVG_JUMLAH_TX = aVG_JUMLAH_TX;
	}
	public BigDecimal getDEVIASI_JUMLAH_TX() {
		return DEVIASI_JUMLAH_TX;
	}
	public void setDEVIASI_JUMLAH_TX(BigDecimal dEVIASI_JUMLAH_TX) {
		DEVIASI_JUMLAH_TX = dEVIASI_JUMLAH_TX;
	}
}
