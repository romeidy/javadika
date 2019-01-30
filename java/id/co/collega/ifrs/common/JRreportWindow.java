package id.co.collega.ifrs.common;

import id.co.collega.v7.ef.common.DataSession;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.*;
import net.sf.jasperreports.engine.export.oasis.JROdtExporter;
import net.sf.jasperreports.j2ee.servlets.ImageServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Window;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Creates a window with a JasperReport in it.
 * 
 * @author bbruhns
 * @author sgerth
 * 
 */
public class JRreportWindow extends Window implements Serializable {

   private static final long serialVersionUID = -5587316458377274805L;
   private transient static final Logger logger = LoggerFactory.getLogger(JRreportWindow.class);

   private transient JRreportWindow window;
   private transient Iframe report;

   /* The parent that calls the report */
   private transient Component parent;

   /* if true, shows the ReportWindow in ModalMode */
   private transient boolean modal;

   /* Report params like subreports, title, author etc. */
   private transient HashMap reportParams;

   /* Reportname with whole path must ends with .jasper (it's compiled) */
   private transient String reportPathName;

   /* JasperReports Datasource */
   private transient JRDataSource ds;

   /* 'pdf', 'xml', .... */
   private transient String type;

   private Connection conn;
   
   private String fileName;

   /**
    * Constructor.<br>
    * <br>
    * Creates a report window container.<br>
    * 
    * @param parent
    * @param modal
    * @param reportParams
    * @param reportPathName
    * @param ds
    * @param type
    */
   public JRreportWindow(Component parent, boolean modal, HashMap<Object, Object> reportParams, String reportPathName, JRDataSource ds, String type, Connection conn) {
      super();
      this.parent = parent;
      this.modal = modal;
      this.reportParams = reportParams;
      this.reportPathName = reportPathName;
      this.ds = ds;
      this.type = type;
      this.window = this;
      this.conn=conn;

      /*if(this.conn!=null){
         try {
            this.conn.close();
         } catch (SQLException e) {
         }
      }*/

      try {
         createReport();
      } catch (FileNotFoundException e) {
         e.printStackTrace();
      }
   }
   
   public JRreportWindow(Component parent, boolean modal, HashMap<Object, Object> reportParams, String reportPathName, JRDataSource ds, String type, Connection conn, String fileName) {
	      super();
	      this.parent = parent;
	      this.modal = modal;
	      this.reportParams = reportParams;
	      this.reportPathName = reportPathName;
	      this.ds = ds;
	      this.type = type;
	      this.window = this;
	      this.conn=conn;
	      this.fileName=fileName;

	      if(this.conn!=null){
	         try {
	            this.conn.close();
	         } catch (SQLException e) {
	         }
	      }

	      try {
	         createReport();
	      } catch (FileNotFoundException e) {
	         e.printStackTrace();
	      }
	   }
   
   

   private DataSession dataSession;
   public DataSession getDataSession() {
      if(dataSession==null){
         dataSession = (DataSession) Sessions.getCurrent().getAttribute(DataSession.class.getSimpleName());
      }
      return  dataSession;
   }
   /**
    * Creates the report in a modal window.
    * 
    * @throws FileNotFoundException
    */
   private void createReport() throws FileNotFoundException {

		if ((Boolean) modal == null) {
			modal = true;
		}

		if (reportPathName.isEmpty()) {
			throw new FileNotFoundException(reportPathName);
		}

		if (type.isEmpty()) {
			type = "pdf";
		}

		if(modal){
			this.setTitle("View Report");
			this.setHeight("100%");
			this.setWidth("80%");
			this.setClosable(true);
			this.setMaximizable(true);
			this.setMinimizable(true);
			this.setSizable(true);
		} else {
			this.setHeight("10px");
			this.setWidth("10px");
		}
		
		this.setParent(parent);
		this.setVisible(true);
		this.addEventListener("onClose", new OnCloseReportEventListener());

		report = new Iframe();
		this.appendChild(report);
		report.setHflex("true");
		report.setVflex("true");
		report.setId("jasperReportId");


		if(type.equals("pdf")){
			Map exportParams = new HashMap();
			StringBuffer sciptPdf = new StringBuffer();
			//sciptPdf.append("this.print({bUI: true,bSilent: false,bShrinkToFit: false});");
			sciptPdf.append("var pp = this.getPrintParams();");
			sciptPdf.append("var fv = pp.constants.flagValues;");
			sciptPdf.append("pp.flags |= (fv.suppressCenter | fv.suppressRotate);");
			sciptPdf.append("pp.interactive = 0;");
			sciptPdf.append("pp.pageHandling = 0;");
			sciptPdf.append("this.print(pp);");
			//String sciptPdf="this.print({bUI: true,bSilent: false,bShrinkToFit: false});";
			exportParams.put(JRPdfExporterParameter.PDF_JAVASCRIPT,sciptPdf.toString());
			reportParams.putAll(exportParams);
		}


		InputStream is = ((ServletContext) Sessions.getCurrent().getWebApp().getNativeContext()).getResourceAsStream(reportPathName);
		try {
			reportParams.put("REPORT_CONNECTION",conn);

			JasperPrint jp = null;

			if(ds!=null){
				jp = JasperFillManager.fillReport(is, reportParams, ds);
			}else{
				jp = JasperFillManager.fillReport(is,reportParams,conn);
			}
			String jpName = jp.getName();
			if(jpName.length() > 31){
				jp.setName(jpName.substring(0,31));
			}
			byte[] jsReport;

			AMedia media = null;

			if("xls".equals(type)){
				JRXlsExporter jrXlsExporter = new JRXlsExporter();
				jrXlsExporter.setParameters(reportParams);
				jrXlsExporter.setParameter(JRExporterParameter.JASPER_PRINT,jp);
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				try {
					jrXlsExporter.setParameter(JRExporterParameter.OUTPUT_STREAM,bos);
					jrXlsExporter.exportReport();
					jsReport = bos.toByteArray();
					media = new AMedia("preview.xls",type,"application/vnd.ms-excel",jsReport);
				}finally {
					try {
						bos.close();
					} catch (IOException e) {
					}
				}
			}else if("csv".equals(type)){
				JRCsvExporter jrCsvExporter = new JRCsvExporter();
				jrCsvExporter.setParameters(reportParams);
				jrCsvExporter.setParameter(JRExporterParameter.JASPER_PRINT,jp);
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				try {
					jrCsvExporter.setParameter(JRExporterParameter.OUTPUT_STREAM,bos);
					jrCsvExporter.exportReport();
					jsReport = bos.toByteArray();
					media = new AMedia("preview.csv",type,"text/csv",jsReport);
				}finally {
					try {
						bos.close();
					} catch (IOException e) {
					}
				}
			}else if("jxl".equals(type)){
				JExcelApiExporter jExcelApiExporter = new JExcelApiExporter();
				jExcelApiExporter.setParameters(reportParams);
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				jExcelApiExporter.setParameter(JRExporterParameter.JASPER_PRINT,jp);
				try {
					jExcelApiExporter.setParameter(JRExporterParameter.OUTPUT_STREAM,bos);
					jExcelApiExporter.exportReport();
					jsReport = bos.toByteArray();
					media = new AMedia("preview.xls","xls","application/vnd.ms-excel",jsReport);
				}finally {
					try {
						bos.close();
					} catch (IOException e) {
					}
				}
			}else if("html".equals(type)){
				JRHtmlExporter jrHtmlExporter = new JRHtmlExporter();
				jrHtmlExporter.setParameters(reportParams);
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				jrHtmlExporter.setParameter(JRExporterParameter.JASPER_PRINT,jp);
				((HttpSession) Sessions.getCurrent().getNativeSession()).setAttribute(ImageServlet.DEFAULT_JASPER_PRINT_SESSION_ATTRIBUTE, jp);
				jrHtmlExporter.setParameter(JRHtmlExporterParameter.IMAGES_URI, Executions.getCurrent().getContextPath()+"/jriservlet/image?image=");
				try {
					jrHtmlExporter.setParameter(JRExporterParameter.OUTPUT_STREAM,bos);
					jrHtmlExporter.exportReport();
					jsReport = bos.toByteArray();
					media = new AMedia("preview.html","html","text/html",jsReport);
				}finally {
					try {
						bos.close();
					} catch (IOException e) {
					}
				}
			}else if("rtf".equals(type)){
				JRRtfExporter jrRtfExporter = new JRRtfExporter();
				jrRtfExporter.setParameters(reportParams);
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				jrRtfExporter.setParameter(JRExporterParameter.JASPER_PRINT,jp);
				try {
					jrRtfExporter.setParameter(JRExporterParameter.OUTPUT_STREAM,bos);
					jrRtfExporter.exportReport();
					jsReport = bos.toByteArray();
					media = new AMedia("preview.rtf","rtf","application/rtf",jsReport);
				}finally {
					try {
						bos.close();
					} catch (IOException e) {
					}
				}
			}else if("xml".equals(type)){
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				try {
					JasperExportManager.exportReportToXmlStream(jp, bos);
					media = new AMedia("preview",type,"application/pdf",bos.toByteArray());
				}finally {
					try {
						bos.close();
					} catch (IOException e) {
					}
				}
			}else if("odt".equals(type)){
				JROdtExporter jrOdtExporter = new JROdtExporter();
				jrOdtExporter.setParameters(reportParams);
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				jrOdtExporter.setParameter(JRExporterParameter.JASPER_PRINT,jp);
				try {
					jrOdtExporter.setParameter(JRExporterParameter.OUTPUT_STREAM,bos);
					jrOdtExporter.exportReport();
					jsReport = bos.toByteArray();
					media = new AMedia("preview.odt","odt","application/vnd.oasis.openTcument.text",jsReport);
				}finally {
					try {
						bos.close();
					} catch (IOException e) {
					}
				}
			}else{
				JRPdfExporter jrPdfExporter = new JRPdfExporter();
				jrPdfExporter.setParameters(reportParams);
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				jrPdfExporter.setParameter(JRExporterParameter.JASPER_PRINT,jp);
				try {
					jrPdfExporter.setParameter(JRExporterParameter.OUTPUT_STREAM,bos);
					jrPdfExporter.exportReport();
					jsReport = bos.toByteArray();
					media = new AMedia("preview.pdf","pdf","application/pdf",jsReport);
				}finally {
					try {
						bos.close();
					} catch (IOException e) {
					}
				}
			}
			report.setContent(media);
		} catch (JRException e) {
			e.printStackTrace();
		}finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
				}
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + report.getId());
		}


		if (modal == true) {
			try {
				this.doModal();
			} catch (SuspendNotAllowedException e) {
				e.printStackTrace();
			}
		}

	}

   /**
    * EventListener for closing the Report Window.<br>
    * 
    * @author sge
    * 
    */
   public final class OnCloseReportEventListener implements EventListener {
      @Override
      public void onEvent(Event event) throws Exception {
         closeReportWindow();
      }
   }

   /**
    * We must clear something to prevent errors or problems <br>
    * by opening the report several times. <br>
    */
   private void closeReportWindow() {

      if (logger.isDebugEnabled()) {
         logger.debug("detach Report and close ReportWindow");
      }

      window.removeEventListener("onClose", new OnCloseReportEventListener());

      // TODO check this
      report.detach();
      window.getChildren().clear();
      window.onClose();

   }
}
