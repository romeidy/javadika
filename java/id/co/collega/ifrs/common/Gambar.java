
package id.co.collega.ifrs.common;

import org.zkoss.zk.ui.Executions;

import com.jet.gand.services.GlobalVariable;

import id.co.collega.ifrs.common.DTOMap;


public class Gambar {

	public final static String getLogoBank(){
		String pathImage="/asset/image/";
		DTOMap map=(DTOMap) GlobalVariable.getInstance().get("syshost");
		String realpath = Executions.getCurrent().getDesktop()
				.getWebApp().getRealPath((String) pathImage+map.get("URL_LOGO_BANK")); 
		System.out.println("URL_LOGO_BANK : "+map.get("URL_LOGO_BANK"));
		System.out.println("REAL PATH LOGO BANK: "+realpath);
		return realpath;
	}
}
