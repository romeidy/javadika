package id.co.collega.ifrs.master;

import java.util.Date;

import org.zkoss.chart.Point;

public class DashboardModelShare extends Point { 
	
	private String judul;
	private Date date;
	private Number trx;
	
	
	public DashboardModelShare(String judul, Date date, Number trx) {
		super(judul +"-"+ date, trx);
		this.judul = judul;
		this.date = date;
		this.trx = trx;
	}
	public String getJudul() {
		return judul;
	}
	public void setJudul(String judul) {
		this.judul = judul;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public Number getTrx() {
		return trx;
	}
	public void setTrx(Number trx) {
		this.trx = trx;
	}
	
	
	
	
	
	
	
	
	
}
