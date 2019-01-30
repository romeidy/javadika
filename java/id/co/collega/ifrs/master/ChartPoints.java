package id.co.collega.ifrs.master;

import org.zkoss.chart.Point;

public class ChartPoints extends Point {

	private String kategori;
	private Double point;
	
	
	
	public ChartPoints() {
		super();
		// TODO Auto-generated constructor stub
	}
	public ChartPoints(String kategori, Double point) {
		super(kategori, point);
		this.kategori = kategori;
		this.point = point;
	}
	public String getKategori() {
		return kategori;
	}
	public void setKategori(String kategori) {
		this.kategori = kategori;
	}
	public Double getPoint() {
		return point;
	}
	public void setPoint(Double point) {
		this.point = point;
	} 
}
