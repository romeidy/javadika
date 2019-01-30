package id.co.collega.ifrs.master;


import java.math.BigDecimal;

import org.zkoss.chart.model.CategoryModel;
import org.zkoss.chart.model.DefaultCategoryModel;


public class LineBasicData extends DefaultCategoryModel {
	private String kode;
	private String thBl;
	private BigDecimal lapor;
	
	
	public LineBasicData() {
		super();
		// TODO Auto-generated constructor stub
	}
	public LineBasicData(String kode, String thBl, BigDecimal lapor) {
		super();
		this.kode = kode;
		this.thBl = thBl;
		this.lapor = lapor;
	}
	public String getKode() {
		return kode;
	}
	public void setKode(String kode) {
		this.kode = kode;
	}
	public String getThBl() {
		return thBl;
	}
	public void setThBl(String thBl) {
		this.thBl = thBl;
	}
	public BigDecimal getLapor() {
		return lapor;
	}
	public void setLapor(BigDecimal lapor) {
		this.lapor = lapor;
	}
	
	public void setValue(String kode, String thBl, BigDecimal lapor){
		this.kode=kode;
		this.thBl=thBl;
		this.lapor=lapor;
		
	}
	
	
	
	/*public LineBasicData getCategoryModel(){
		return lin
	}*/

	
    /*private static final CategoryModel model;
    static {
        model = new DefaultCategoryModel();
        model.setValue("LTKT", "Jan", 7.0);
        model.setValue("LTKT", "Feb", 6.9);
        model.setValue("LTKT", "Mar", 9.5);
        model.setValue("LTKT", "Apr", 14.5);
        model.setValue("LTKT", "May", 18.2);
        model.setValue("LTKT", "Jun", 21.5);
        model.setValue("LTKT", "Jul", 25.2);
        model.setValue("LTKT", "Aug", 26.5);
        model.setValue("LTKT", "Sep", 23.3);
        model.setValue("LTKT", "Oct", 18.3);
        model.setValue("LTKT", "Nov", 13.9);
        model.setValue("LTKT", "Dec", 9.6);
        model.setValue("LTKM", "Jan", -0.2);
        model.setValue("LTKM", "Feb", 0.8);
        model.setValue("LTKM", "Mar", 5.7);
        model.setValue("LTKM", "Apr", 11.3);
        model.setValue("LTKM", "May", 17.0);
        model.setValue("LTKM", "Jun", 22.0);
        model.setValue("LTKM", "Jul", 24.8);
        model.setValue("LTKM", "Aug", 24.1);
        model.setValue("LTKM", "Sep", 0.1);
        model.setValue("LTKM", "Oct", 14.1);
        model.setValue("LTKM", "Nov", 8.6);
        model.setValue("LTKM", "Dec", 2.5);
        model.setValue("Data Belum Dikinikan", "Jan", -0.9);
        model.setValue("Data Belum Dikinikan", "Feb", 0.6);
        model.setValue("Data Belum Dikinikan", "Mar", 3.5);
        model.setValue("Data Belum Dikinikan", "Apr", 8.4);
        model.setValue("Data Belum Dikinikan", "May", 13.5);
        model.setValue("Data Belum Dikinikan", "Jun", 17.0);
        model.setValue("Data Belum Dikinikan", "Jul", 18.6);
        model.setValue("Data Belum Dikinikan", "Aug", 17.9);
        model.setValue("Data Belum Dikinikan", "Sep", 14.3);
        model.setValue("Data Belum Dikinikan", "Oct", 9.0);
        model.setValue("Data Belum Dikinikan", "Nov", 3.9);
        model.setValue("Data Belum Dikinikan", "Dec", 1.0);
        model.setValue("Suspect CIF Ganda", "Jan", 3.9);
        model.setValue("Suspect CIF Ganda", "Feb", 4.2);
        model.setValue("Suspect CIF Ganda", "Mar", 5.7);
        model.setValue("Suspect CIF Ganda", "Apr", 8.5);
        model.setValue("Suspect CIF Ganda", "May", 11.9);
        model.setValue("Suspect CIF Ganda", "Jun", 15.2);
        model.setValue("Suspect CIF Ganda", "Jul", 17.0);
        model.setValue("Suspect CIF Ganda", "Aug", 16.6);
        model.setValue("Suspect CIF Ganda", "Sep", 14.2);
        model.setValue("Suspect CIF Ganda", "Oct", 10.3);
        model.setValue("Suspect CIF Ganda", "Nov", 6.6);
        model.setValue("Suspect CIF Ganda", "Dec", 4.8);
    }
    
    public static CategoryModel getCategoryModel() {
        return model;
    }*/
}