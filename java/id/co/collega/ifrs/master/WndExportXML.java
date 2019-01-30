package id.co.collega.ifrs.master;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.ReaderInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;

import id.co.collega.ifrs.common.Cryptograph;
import id.co.collega.ifrs.common.DTOMap;
import id.co.collega.ifrs.master.service.MasterServices;
import id.co.collega.ifrs.util.MessageBox;

@SuppressWarnings("serial")
@org.springframework.stereotype.Component
@Scope("execution")
public class WndExportXML extends SelectorComposer<Component> {

	@Autowired
	MasterServices masterService;

	@Wire
	Button btnExportXML;
	
	@Autowired
	Environment env;
	
	

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		btnExportXML.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

			@Override
			public void onEvent(Event arg0) throws Exception {
				exportXML();
				btnExportXML.setDisabled(true);

			}
		});
	}

	private void exportXML() {
		Thread th = new Thread( new GenerateXML(masterService,env));
		th.run();
		while (th.isAlive()) {
			System.out.println("Masih Berjalan...!");
		}

		MessageBox.showInformation("Tugas sudah selesai...!");

	}

	

	/*
	 * private class GenerateXML {
	 * 
	 * @Autowired MasterServices masterServices;
	 * 
	 * private List<DTOMap> ls1;
	 * 
	 * 
	 * 
	 * sql=" SELECT  * FROM PPATK_EXCEL_RINCI ORDER BY CIFID ";
	 * 
	 * ls1=masterServices. }
	 */

}
