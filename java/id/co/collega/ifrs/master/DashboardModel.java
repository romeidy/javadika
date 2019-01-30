package id.co.collega.ifrs.master;

import java.util.Date;

public class DashboardModel {
	
	
	static class DMHead{
		private String lbl;
		
		DMHead(String lbl){
			this.lbl=lbl;
		}
		
		public String getLbl(){
			return lbl;
		}
		
	}
	
	/*static class DMHead{
		private String lbl;
		private Date date;
		
		DMHead(String lbl, Date date){
			this.lbl=lbl;
			this.date=date;
		}
		
		public String getLbl(){
			return lbl;
		}
		public Date getDate(){
			return date;
		}
		
	}*/
	
	
	private DMHead group;
	//private String cabang;
	private String tglTrx;
	private Number trx;
	
	
	
	public DashboardModel() {
		super();
		// TODO Auto-generated constructor stub
	}
	//Date tglTrx String cabang
	// Date tglTrx
	public DashboardModel(DMHead group,String tglTrx , Number trx) {
		super();
		this.group = group;
		//this.cabang = cabang;
		this.tglTrx=tglTrx;
		this.trx = trx;
	}
	public DMHead getGroup() {
		return group;
	}
	public void setGroup(DMHead group) {
		this.group = group;
	}
	/*public String getCabang() {
		return cabang;
	}
	public void setCabang(String cabang) {
		this.cabang = cabang;
	}*/
	public Number getTrx() {
		return trx;
	}
	public void setTrx(Number trx) {
		this.trx = trx;
	}
	public String getTglTrx(){
		return tglTrx;
	}
	public void setTglTrx(String tglTrx){
		this.tglTrx=tglTrx;
	}
	
	/*public Date getTglTrx(){
		return tglTrx;
	}
	public void setTglTrx(Date tglTrx){
		this.tglTrx=tglTrx;
	}*/
	
	

}
