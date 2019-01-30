package id.co.collega.ifrs.master;

import java.math.BigDecimal;

import org.zkoss.chart.model.DefaultCategoryModel;

public class CategoryModel extends DefaultCategoryModel {
	
	private String lData;
	private String hData;
	private BigDecimal vData;

	public CategoryModel() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public CategoryModel(String lData, String hData, BigDecimal vData) {
		super();
		this.lData = lData;
		this.hData = hData;
		this.vData = vData;
	}
	
	public String getlData() {
		return lData;
	}

	public void setlData(String lData) {
		this.lData = lData;
	}

	public String gethData() {
		return hData;
	}

	public void sethData(String hData) {
		this.hData = hData;
	}

	public BigDecimal getvData() {
		return vData;
	}

	public void setvData(BigDecimal vData) {
		this.vData = vData;
	}

	public void setValue(String lData, String hData, BigDecimal vData){
		this.lData = lData;
		this.hData = hData;
		this.vData = vData;
	}

}
