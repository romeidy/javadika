package id.co.collega.ifrs.master.report.dto;

import java.math.BigDecimal;
import java.sql.Date;

public class dtoNeraca {
	private String CABANG;
	private Date ACTUAL_DATE;
	private String GL_ACCOUNT_TYPE1_NAME;
	private String GL_ACCOUNT_TYPE1_CODE;
	private String GL_ACCOUNT_TYPE3_NAME;
	private String GL_ACCOUNT_TYPE3_CODE;
	private String GL_ACCOUNT_TYPE4_NAME;
	private String GL_ACCOUNT_TYPE4_CODE;
	private BigDecimal VALLAS;
	private BigDecimal RUPIAH;
	private BigDecimal JUMLAH;
	
	public String getCABANG() {
		return CABANG;
	}
	public void setCABANG(String cABANG) {
		CABANG = cABANG;
	}
	public Date getACTUAL_DATE() {
		return ACTUAL_DATE;
	}
	public void setACTUAL_DATE(Date aCTUAL_DATE) {
		ACTUAL_DATE = aCTUAL_DATE;
	}
	public String getGL_ACCOUNT_TYPE1_NAME() {
		return GL_ACCOUNT_TYPE1_NAME;
	}
	public void setGL_ACCOUNT_TYPE1_NAME(String gL_ACCOUNT_TYPE1_NAME) {
		GL_ACCOUNT_TYPE1_NAME = gL_ACCOUNT_TYPE1_NAME;
	}
	public String getGL_ACCOUNT_TYPE1_CODE() {
		return GL_ACCOUNT_TYPE1_CODE;
	}
	public void setGL_ACCOUNT_TYPE1_CODE(String gL_ACCOUNT_TYPE1_CODE) {
		GL_ACCOUNT_TYPE1_CODE = gL_ACCOUNT_TYPE1_CODE;
	}
	public String getGL_ACCOUNT_TYPE3_NAME() {
		return GL_ACCOUNT_TYPE3_NAME;
	}
	public void setGL_ACCOUNT_TYPE3_NAME(String gL_ACCOUNT_TYPE3_NAME) {
		GL_ACCOUNT_TYPE3_NAME = gL_ACCOUNT_TYPE3_NAME;
	}
	public String getGL_ACCOUNT_TYPE3_CODE() {
		return GL_ACCOUNT_TYPE3_CODE;
	}
	public void setGL_ACCOUNT_TYPE3_CODE(String gL_ACCOUNT_TYPE3_CODE) {
		GL_ACCOUNT_TYPE3_CODE = gL_ACCOUNT_TYPE3_CODE;
	}
	public String getGL_ACCOUNT_TYPE4_NAME() {
		return GL_ACCOUNT_TYPE4_NAME;
	}
	public void setGL_ACCOUNT_TYPE4_NAME(String gL_ACCOUNT_TYPE4_NAME) {
		GL_ACCOUNT_TYPE4_NAME = gL_ACCOUNT_TYPE4_NAME;
	}
	public String getGL_ACCOUNT_TYPE4_CODE() {
		return GL_ACCOUNT_TYPE4_CODE;
	}
	public void setGL_ACCOUNT_TYPE4_CODE(String gL_ACCOUNT_TYPE4_CODE) {
		GL_ACCOUNT_TYPE4_CODE = gL_ACCOUNT_TYPE4_CODE;
	}
	public BigDecimal getVALLAS() {
		return VALLAS;
	}
	public void setVALLAS(BigDecimal vALLAS) {
		VALLAS = vALLAS;
	}
	public BigDecimal getRUPIAH() {
		return RUPIAH;
	}
	public void setRUPIAH(BigDecimal rUPIAH) {
		RUPIAH = rUPIAH;
	}
	public BigDecimal getJUMLAH() {
		return JUMLAH;
	}
	public void setJUMLAH(BigDecimal jUMLAH) {
		JUMLAH = jUMLAH;
	}
	
}
