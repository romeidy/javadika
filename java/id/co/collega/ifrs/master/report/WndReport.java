package id.co.collega.ifrs.master.report;

import java.util.HashMap;

import net.sf.jasperreports.engine.JRDataSource;

import org.springframework.context.annotation.Scope;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zul.Window;

import id.co.collega.v7.ui.component.JasperReport;

//@org.springframework.stereotype.Component
//@Scope("desktop")
public class WndReport extends Window {

	public WndReport(String namaFileReport,String type,JRDataSource ds,HashMap params) {
		JasperReport.createComponent(this, JasperReport.TYPE_PDF, namaFileReport, null, params);
	}

}
