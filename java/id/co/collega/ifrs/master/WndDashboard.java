package id.co.collega.ifrs.master;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.zkoss.chart.Chart;
import org.zkoss.chart.Charts;
import org.zkoss.chart.Legend;
import org.zkoss.chart.PlotLine;
import org.zkoss.chart.Point;
import org.zkoss.chart.Series;
import org.zkoss.chart.plotOptions.ColumnPlotOptions;
import org.zkoss.chart.plotOptions.PieDataLabels;
import org.zkoss.chart.plotOptions.PiePlotOptions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

import id.co.collega.ifrs.common.DTOMap;
import id.co.collega.ifrs.common.FunctionUtils;
import id.co.collega.ifrs.master.DashboardModel.DMHead;
import id.co.collega.ifrs.master.service.MasterServices;
import id.co.collega.ifrs.util.ComponentUtil;
import id.co.collega.ifrs.util.MessageBox;
import id.co.collega.v7.seed.config.AuthenticationService;

@Component
@Scope("execution")
public class WndDashboard extends SelectorComposer<Window> {

	private static final  Logger log = LoggerFactory.getLogger(WndDashboard.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Wire
	Charts chart;

	@Autowired
	AuthenticationService auth;
	@Wire
	Listbox listData;
	@Autowired
	MasterServices masterService;

	@Wire
	Label lblLtktBelumLaporkan, lblLtktBelumLaporkanUraian, lblLtkmBelumLapor, lblBelumDikinikan, lblBelumDicleansing, lblLtktBelumLaporkanSpv;

	@Wire
	Vbox vboxBlumLaporSpv,vboxBlumLaporSpv1,vboxBlumLaporSpv2;
	
	@Wire
	Datebox txtTgl;

	FunctionUtils utils = new FunctionUtils();

	private DMHead head;

	@Wire
	Charts chartPie;

	@Wire
	Charts chartWithDrillDown;

	@Wire
	Combobox cmbCabang;

	// @Wire Div divchart;

	private String sql;
	private LineBasicData lbd;
	private ChartPoints dtPoint;
	private Series series;
	private Date dt1, dt2;
	private String sdf1;
	private String sdf2;

	public void doAfterCompose(Window comp) throws Exception {
		// public void doAFterCompose(Window comp) throws Exception{
		super.doAfterCompose(comp);

		doCard();

		// untuk menampilkan Chart
		doLineChart();

		// pie charts
		doPieCharts();

		doLoadData();
		
		ComponentUtil.setValue(txtTgl, new Date());

		isiDataCombo();

		cmbCabang.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {

			@Override
			public void onEvent(Event arg0) throws Exception {
				clearChartDrillDown();
				doChartWithDrillDown();
			}
		});

		
		// doChartWithDrillDown();
	}

	private void isiDataCombo() {
		cmbCabang.getItems().clear();
		Comboitem item;
		if (auth.getUserDetails().getActiveRole().equals("01") || auth.getUserDetails().getActiveRole().equals("05")) {
			String sql = "SELECT KD_CAB, NM_CAB FROM CFG_CABANG";
			List<DTOMap> mapCabang = masterService.getDataMaster(sql, null);
			for (DTOMap cab : mapCabang) {
				item = new Comboitem();
				item.setValue(cab.getString("KD_CAB"));
				item.setLabel(cab.getString("KD_CAB") + " - " + cab.getString("NM_CAB"));
				cmbCabang.appendChild(item);
				cmbCabang.setSelectedIndex(0);
			}
		} else {
			String sql = " SELECT KD_CAB, NM_CAB FROM CFG_CABANG WHERE KD_CAB=? ";
			DTOMap mapCabang = masterService.getMapMaster(sql, new Object[] {auth.getUserDetails().getBranchId()});
			item = new Comboitem();
			item.setValue(mapCabang.getString("KD_CAB"));
			item.setLabel(mapCabang.getString("KD_CAB") + " - " + mapCabang.getString("NM_CAB"));
			cmbCabang.appendChild(item);
			
			cmbCabang.setSelectedIndex(0);
			clearChartDrillDown();
			doChartWithDrillDown();
		}
		

	}

	private void doCard() {

		if (auth.getUserDetails().getActiveRole().equals("01") || auth.getUserDetails().getActiveRole().equals("05")) {

			// lblLtktBelumDiLaporkan

			String sql = "SELECT COUNT(1) AS JUMLAH FROM RPT_LTKT WHERE STATUS='0' ";

			DTOMap mapBelumDiLaporkanCabang = masterService.getMapMaster(sql, new Object[] {});
			lblLtktBelumLaporkan.setValue("");
			lblLtktBelumLaporkan.setValue(utils
					.moneyToText(BigDecimal.valueOf(mapBelumDiLaporkanCabang.getInt("JUMLAH"))).replace(".00", ""));

			// lblLtkmBelumLapor
			// sql = "select 'LTKM BELUM LAPOR' as KATEGORI, LTKM_BELUMLAPOR as POINT from
			// RPT_DASHBOARD";
			sql = "SELECT 'LKTM BELUM LAPOR' AS KATEGORI, COUNT(FLG_RPT) AS POINT FROM RPT_LTKM WHERE FLG_RPT='00' ";
			DTOMap mapBelumDiLaporkanCabang2 = masterService.getMapMaster(sql, new Object[] {});
			lblLtkmBelumLapor.setValue("");
			lblLtkmBelumLapor.setValue(utils.moneyToText(BigDecimal.valueOf(mapBelumDiLaporkanCabang2.getInt("POINT")))
					.replace(".00", ""));

			// lblBelumDikinikan
			sql = "SELECT COUNT(1) AS JUMLAH FROM RPT_PENGKINIAN_BELUM";
			DTOMap mapBelumDiLaporkanCabang3 = masterService.getMapMaster(sql, new Object[] {});
			lblBelumDikinikan.setValue("");
			//log.info("\t\n belum di kinikan : " + mapBelumDiLaporkanCabang3.getInt("JUMLAH"));
			lblBelumDikinikan.setValue(utils.moneyToText(BigDecimal.valueOf(mapBelumDiLaporkanCabang3.getInt("JUMLAH")))
					.replace(".00", ""));

			// lblBelumDicleansing
			sql = "SELECT COUNT(1) AS JUMLAH FROM RPT_CIFGANDA WHERE STS_LAPOR='0' ";
			DTOMap mapBelumDiLaporkanCabang4 = masterService.getMapMaster(sql, new Object[] {});
			lblBelumDicleansing.setValue("");
			//log.info("\t\n belum di cleansing : " + mapBelumDiLaporkanCabang4.getInt("JUMLAH"));
			lblBelumDicleansing.setValue(utils
					.moneyToText(BigDecimal.valueOf(mapBelumDiLaporkanCabang4.getInt("JUMLAH"))).replace(".00", ""));
		} else {

			String kdCabang = auth.getUserDetails().getBranchId();
			String statusLapor = "0";
			String lblLapor = "Belum Lapor";
			if (auth.getUserDetails().getActiveRole().equals("02")) {
				statusLapor = "1";
				lblLapor = "belum Verifikasi";
			}
			lblLtktBelumLaporkanUraian.setValue(lblLapor);
			
			String sql = "SELECT COUNT(1) AS JUMLAH FROM ( SELECT COUNT(1) "
			+ " FROM RPT_LTKT_TRX A  INNER JOIN CFG_CABANG B ON A.KD_CAB_LOKTX = B.KD_CAB "
			+ " WHERE KD_CAB_LOKTX= ? AND  STS_LAPOR='"+statusLapor+"' "
			+ " GROUP BY CIFID, TGL_TX, NO_REKENING, NAMA_LENGKAP, DB_KR, STS_LAPOR,KD_CAB_LOKTX, B.NM_CAB ,"
			+ " CAST(TGL_PROSES AS DATE) ORDER BY TGL_TX, KD_CAB_LOKTX)";

			DTOMap mapBelumDiLaporkanCabang = masterService.getMapMaster(sql, new Object[] {auth.getUserDetails().getBranchId()});
			lblLtktBelumLaporkan.setValue("");
			lblLtktBelumLaporkan.setValue(utils
					.moneyToText(BigDecimal.valueOf(mapBelumDiLaporkanCabang.getInt("JUMLAH"))).replace(".00", ""));

			//--tambah status u spv cabang
			if (auth.getUserDetails().getActiveRole().equals("02")) {
				vboxBlumLaporSpv.setVisible(true);
				vboxBlumLaporSpv1.setVisible(true);
				vboxBlumLaporSpv2.setVisible(true);
				
				sql = "SELECT COUNT(1) AS JUMLAH FROM ( SELECT COUNT(1) "
						+ " FROM RPT_LTKT_TRX A  INNER JOIN CFG_CABANG B ON A.KD_CAB_LOKTX = B.KD_CAB "
						+ " WHERE KD_CAB_LOKTX= ? AND  STS_LAPOR='0' "
						+ " GROUP BY CIFID, TGL_TX, NO_REKENING, NAMA_LENGKAP, DB_KR, STS_LAPOR,KD_CAB_LOKTX, B.NM_CAB ,"
						+ " CAST(TGL_PROSES AS DATE) ORDER BY TGL_TX, KD_CAB_LOKTX)";
				
				DTOMap mapBelumDiLaporkanCabangSpv = masterService.getMapMaster(sql, new Object[] {auth.getUserDetails().getBranchId()});
				lblLtktBelumLaporkanSpv.setValue("");
				lblLtktBelumLaporkanSpv.setValue(utils
						.moneyToText(BigDecimal.valueOf(mapBelumDiLaporkanCabangSpv.getInt("JUMLAH"))).replace(".00", ""));
			}
			//---------------------------
			
			
			// lblLtkmBelumLapor
			// sql = "select 'LTKM BELUM LAPOR' as KATEGORI, LTKM_BELUMLAPOR as POINT from
			// RPT_DASHBOARD";
			sql = "SELECT 'LKTM BELUM LAPOR' AS KATEGORI, COUNT(FLG_RPT) AS POINT FROM RPT_LTKM WHERE FLG_RPT='00' AND  KD_CAB=? ";
			DTOMap mapBelumDiLaporkanCabang2 = masterService.getMapMaster(sql, new Object[] {auth.getUserDetails().getBranchId()});
			lblLtkmBelumLapor.setValue("");
			lblLtkmBelumLapor.setValue(utils.moneyToText(BigDecimal.valueOf(mapBelumDiLaporkanCabang2.getInt("POINT")))
					.replace(".00", ""));

			// lblBelumDikinikan
			sql = "SELECT COUNT(1) AS JUMLAH FROM RPT_PENGKINIAN_BELUM WHERE KD_CAB=?";
			DTOMap mapBelumDiLaporkanCabang3 = masterService.getMapMaster(sql, new Object[] {auth.getUserDetails().getBranchId()});
			lblBelumDikinikan.setValue("");
			//log.info("\t\n belum di kinikan : " + mapBelumDiLaporkanCabang3.getInt("JUMLAH"));
			lblBelumDikinikan.setValue(utils.moneyToText(BigDecimal.valueOf(mapBelumDiLaporkanCabang3.getInt("JUMLAH")))
					.replace(".00", ""));

			// lblBelumDicleansing
			sql = "SELECT COUNT(1) AS JUMLAH FROM RPT_CIFGANDA WHERE STS_LAPOR='0' AND KD_CAB=?";
			DTOMap mapBelumDiLaporkanCabang4 = masterService.getMapMaster(sql, new Object[] {auth.getUserDetails().getBranchId()});
			lblBelumDicleansing.setValue("");
			//log.info("\t\n belum di cleansing : " + mapBelumDiLaporkanCabang4.getInt("JUMLAH"));
			lblBelumDicleansing.setValue(utils
					.moneyToText(BigDecimal.valueOf(mapBelumDiLaporkanCabang4.getInt("JUMLAH"))).replace(".00", ""));
			

		}

	}

	private void doPieCharts() {

		series = chartPie.getSeries();
		series.setType("pie");
		// series.setName("Browser share");
		series.setName("DashBoard APU-PPT");

		doDataPoint();

		Chart chartOptional = chartPie.getChart();
		chartOptional.setPlotBorderWidth(0);
		chartOptional.setPlotShadow(false);

		chartPie.getTooltip().setPointFormat("{series.name}: <b>{point.percentage:.1f}%</b>");

		PiePlotOptions plotOptions = chartPie.getPlotOptions().getPie();

		plotOptions.setAllowPointSelect(true);
		plotOptions.setCursor("pointer");
		PieDataLabels dataLabels = (PieDataLabels) plotOptions.getDataLabels();
		dataLabels.setEnabled(true);
		dataLabels.setFormat("<b>{point.name}</b>: {point.percentage:.1f} %");

	}

	private void doDataPoint() {
		DTOMap dtMstPoint;
		if (auth.getUserDetails().getActiveRole().equals("01") || auth.getUserDetails().getActiveRole().equals("05")) {
			// 1
			// sql = " select 'LTKT SUDAH LAPOR' as KATEGORI,LTKT_SUDAHLAPOR as POINT from
			// RPT_DASHBOARD ";
			sql = "SELECT 'LTKT SUDAH LAPOR' as KATEGORI,count(1) AS POINT  FROM RPT_LTKT WHERE STATUS='3' ";

			dtMstPoint = masterService.getMapMaster(sql, new Object[] {});
			dtPoint = new ChartPoints(dtMstPoint.getString("KATEGORI"), Double.valueOf(dtMstPoint.getInt("POINT")));
			series.addPoint(dtPoint);

			// 2
			// sql = "select 'LTKM BELUM LAPOR' as KATEGORI, LTKM_BELUMLAPOR as POINT from
			// RPT_DASHBOARD ";
			sql = "SELECT 'LTKT BELUM LAPOR' as KATEGORI, count(1) AS POINT FROM RPT_LTKT WHERE STATUS='0' ";
			dtMstPoint = masterService.getMapMaster(sql, new Object[] {});
			dtPoint = new ChartPoints(dtMstPoint.getString("KATEGORI"), Double.valueOf(dtMstPoint.getInt("POINT")));
			series.addPoint(dtPoint);

			// 3

			sql = "SELECT 'LTKM BELUM LAPOR' as KATEGORI,count(1) AS POINT FROM RPT_LTKM WHERE FLG_RPT='03'";
			dtMstPoint = masterService.getMapMaster(sql, new Object[] {});
			dtPoint = new ChartPoints(dtMstPoint.getString("KATEGORI"), Double.valueOf(dtMstPoint.getInt("POINT")));
			series.addPoint(dtPoint);

			// sql = "select 'SIPESAT NASABAH BARU' as KATEGORI, SIPESAT_NASABAHBARU as
			// POINT from RPT_DASHBOARD";
			/*
			 * sql="" dtMstPoint = masterService.getMapMaster(sql, new Object[] {}); dtPoint
			 * = new ChartPoints(dtMstPoint.getString("KATEGORI"),
			 * Double.valueOf(dtMstPoint.getInt("POINT"))); series.addPoint(dtPoint);
			 */

			// 4
			// sql = "select 'CIFGANDA SUSPECT' as KATEGORI, CIFGANDA_SUSPECT as POINT from
			// RPT_DASHBOARD ";
			sql = " SELECT  'CIFGANDA SUSPECT' as KATEGORI,COUNT(1) AS POINT  FROM RPT_CIFGANDA WHERE STS_LAPOR='0' ";
			dtMstPoint = masterService.getMapMaster(sql, new Object[] {});
			dtPoint = new ChartPoints(dtMstPoint.getString("KATEGORI"), Double.valueOf(dtMstPoint.getInt("POINT")));
			series.addPoint(dtPoint);

			// 5
			// sql = "select 'PENGKINIAN BELUM'as KATEGORI, PENGKINIAN_BELUM as POINT from
			// RPT_DASHBOARD";
			sql = " SELECT 'PENGKINIAN BELUM'as KATEGORI,COUNT(1) AS POINT FROM RPT_PENGKINIAN_BELUM ";
			dtMstPoint = masterService.getMapMaster(sql, new Object[] {});
			dtPoint = new ChartPoints(dtMstPoint.getString("KATEGORI"), Double.valueOf(dtMstPoint.getInt("POINT")));
			series.addPoint(dtPoint);
		}else {
			// 1
			// sql = " select 'LTKT SUDAH LAPOR' as KATEGORI,LTKT_SUDAHLAPOR as POINT from
			// RPT_DASHBOARD ";
			sql = "SELECT 'LTKT SUDAH LAPOR' as KATEGORI,count(1) AS POINT  FROM RPT_LTKT_TRX WHERE STS_LAPOR='3' AND KD_CAB_LOKTX=? ";
			
			dtMstPoint = masterService.getMapMaster(sql, new Object[] {auth.getUserDetails().getBranchId()});
			dtPoint = new ChartPoints(dtMstPoint.getString("KATEGORI"), Double.valueOf(dtMstPoint.getInt("POINT")));
			series.addPoint(dtPoint);

			// 2
			// sql = "select 'LTKM BELUM LAPOR' as KATEGORI, LTKM_BELUMLAPOR as POINT from
			// RPT_DASHBOARD ";
			sql = "SELECT 'LTKT BELUM LAPOR' as KATEGORI, count(1) AS POINT FROM RPT_LTKT_TRX WHERE STS_LAPOR='0' AND  KD_CAB_LOKTX=? ";
			dtMstPoint = masterService.getMapMaster(sql, new Object[] {auth.getUserDetails().getBranchId()});
			dtPoint = new ChartPoints(dtMstPoint.getString("KATEGORI"), Double.valueOf(dtMstPoint.getInt("POINT")));
			series.addPoint(dtPoint);

			// 3

			sql = "SELECT 'LTKM BELUM LAPOR' as KATEGORI,count(1) AS POINT FROM RPT_LTKM WHERE FLG_RPT='03' AND KD_CAB=?";
			dtMstPoint = masterService.getMapMaster(sql, new Object[] {auth.getUserDetails().getBranchId()});
			dtPoint = new ChartPoints(dtMstPoint.getString("KATEGORI"), Double.valueOf(dtMstPoint.getInt("POINT")));
			series.addPoint(dtPoint);

			// sql = "select 'SIPESAT NASABAH BARU' as KATEGORI, SIPESAT_NASABAHBARU as
			// POINT from RPT_DASHBOARD";
			/*
			 * sql="" dtMstPoint = masterService.getMapMaster(sql, new Object[] {}); dtPoint
			 * = new ChartPoints(dtMstPoint.getString("KATEGORI"),
			 * Double.valueOf(dtMstPoint.getInt("POINT"))); series.addPoint(dtPoint);
			 */

			// 4
			// sql = "select 'CIFGANDA SUSPECT' as KATEGORI, CIFGANDA_SUSPECT as POINT from
			// RPT_DASHBOARD ";
			sql = " SELECT  'CIFGANDA SUSPECT' as KATEGORI,COUNT(1) AS POINT  FROM RPT_CIFGANDA WHERE STS_LAPOR='0' AND KD_CAB=? ";
			dtMstPoint = masterService.getMapMaster(sql, new Object[] {auth.getUserDetails().getBranchId()});
			dtPoint = new ChartPoints(dtMstPoint.getString("KATEGORI"), Double.valueOf(dtMstPoint.getInt("POINT")));
			series.addPoint(dtPoint);

			// 5
			// sql = "select 'PENGKINIAN BELUM'as KATEGORI, PENGKINIAN_BELUM as POINT from
			// RPT_DASHBOARD";
			sql = " SELECT 'PENGKINIAN BELUM'as KATEGORI,COUNT(1) AS POINT FROM RPT_PENGKINIAN_BELUM WHERE KD_CAB=?";
			dtMstPoint = masterService.getMapMaster(sql, new Object[] {auth.getUserDetails().getBranchId()});
			dtPoint = new ChartPoints(dtMstPoint.getString("KATEGORI"), Double.valueOf(dtMstPoint.getInt("POINT")));
			series.addPoint(dtPoint);
		}

		

		

	}

	private void doLineChart() throws Exception {
		// add by Alben
		doDataLineChart();

		chart.setModel(lbd);
		// chart.setModel(dcm);
		// chart.setModel(LineBasicData.getCategoryModel());
		chart.getTitle().setX(-20);

		chart.getSubtitle().setX(-20);

		chart.getYAxis().setTitle("Jumlah Data");
		PlotLine plotLine = new PlotLine();
		plotLine.setValue(0);
		plotLine.setWidth(1);
		plotLine.setColor("#808080");
		chart.getYAxis().addPlotLine(plotLine);

		chart.getTooltip().setValueSuffix("Transaksi");

		Legend legend = chart.getLegend();
		legend.setLayout("vertical");
		legend.setAlign("right");
		legend.setVerticalAlign("middle");
		legend.setBorderWidth(0);
	}

	private void doDataLineChart() throws Exception {
		lbd = new LineBasicData();
		if (auth.getUserDetails().getActiveRole().equals("01") || auth.getUserDetails().getActiveRole().equals("05")) {
			// List<DTOMap>
			Calendar cal1 = Calendar.getInstance();
			cal1.setTime(new Date());
			Date dt1 = cal1.getTime();

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
			String sdf1 = sdf.format(dt1);
			// System.out.println("Bulan saat ini : "+ sdf1);

			cal1.add(Calendar.YEAR, -1);
			Date dt2 = cal1.getTime();
			String sdf2 = sdf.format(dt2);

			/*sql = "SELECT KODE_CABANG, NAMA_CABANG, MONTH, count(TRANSAKSI ) AS TRANSAKSI FROM ("
					+"select a.kd_cab_loktx as KODE_CABANG, b.nm_cab as NAMA_CABANG , varchar_format(a.tgl_tx,'yyyy-MON') as MONTH, count(a.TGL_TX)as TRANSAKSI FROM rpt_ltkt_trx a "
					+ " inner join cfg_cabang b on a.kd_cab_loktx=b.kd_cab "
					+ "  where varchar_format(a.tgl_tx,'yyyy-MM')  BETWEEN  '" + sdf2 + "' and  '" + sdf1 + "' "
					+ " group by a.kd_cab_loktx ,b.nm_cab,  a.tgl_tx "
					+") a " + 
					"    GROUP BY KODE_CABANG, NAMA_CABANG, MONTH " + 
					"    ORDER BY KODE_CABANG, NAMA_CABANG, MONTH ";*/
			sql="SELECT a.KODE_CABANG ,a.NAMA_CABANG, A.YM AS MONTH, count(a.TRANSAKSI) as TRANSAKSI \r\n" + 
					"FROM ( \r\n" + 
					"	 SELECT a.kd_cab_loktx as KODE_CABANG, b.nm_cab as NAMA_CABANG, varchar_format(a.tgl_tx,'yyyy-MON-dd') as DT,\r\n" + 
					"	 count(a.TGL_TX)as TRANSAKSI, 'LTKT Belum Di Laporkan' as KRITERIA,  \r\n" + 
					"	 varchar_format(a.tgl_tx,'yyyy-MON') as YM \r\n" + 
					"	 FROM RPT_LTKT_TRX A\r\n" + 
					"	 INNER JOIN CFG_CABANG B ON A.KD_CAB_LOKTX = B.KD_CAB\r\n" + 
					"	 WHERE  varchar_format(a.tgl_tx,'yyyy-MM') between   '" + sdf2 + "' and '" + sdf1 + "' "+ 
					"	 GROUP BY CIFID, TGL_TX, NO_REKENING, NAMA_LENGKAP, DB_KR, STS_LAPOR,KD_CAB_LOKTX, B.NM_CAB ,\r\n" + 
					"	 CAST(TGL_PROSES AS DATE) \r\n" + 
					"	 ORDER BY A.TGL_TX, KD_CAB_LOKTX \r\n" + 
					"    ) a\r\n" + 
					"GROUP BY A.KODE_CABANG,A.NAMA_CABANG, A.YM\r\n" ; 

			List<DTOMap> lineData = masterService.getDataMaster(sql, null);

			for (DTOMap dt : lineData) {
				lbd.setValue(dt.getString("NAMA_CABANG"), dt.getString("MONTH"), dt.getInt("TRANSAKSI"));
				// lbd.setValue(dt.getString("KODE"), dt.getString("THBL"),
				// dt.getInt("JMLLAPOR"));

			}

		}else {
			
			Calendar cal1 = Calendar.getInstance();
			cal1.setTime(new Date());
			Date dt1 = cal1.getTime();

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
			String sdf1 = sdf.format(dt1);
			// System.out.println("Bulan saat ini : "+ sdf1);

			cal1.add(Calendar.YEAR, -1);
			Date dt2 = cal1.getTime();
			String sdf2 = sdf.format(dt2);

			/*sql = "SELECT KODE_CABANG, NAMA_CABANG, MONTH, count(TRANSAKSI ) AS TRANSAKSI FROM ( "
					+"select a.kd_cab_loktx as KODE_CABANG, b.nm_cab as NAMA_CABANG , varchar_format(a.tgl_tx,'yyyy-MON') as MONTH, count(a.TGL_TX)as TRANSAKSI FROM rpt_ltkt_trx a "
					+ " inner join cfg_cabang b on a.kd_cab_loktx=b.kd_cab "
					+ "  where varchar_format(a.tgl_tx,'yyyy-MM')  BETWEEN  '" + sdf2 + "' and  '" + sdf1 + "' AND kd_cab_loktx='"+ auth.getUserDetails().getBranchId() +"' "
					+ " group by a.kd_cab_loktx ,b.nm_cab,  a.tgl_tx "
					+") a " + 
					"    GROUP BY KODE_CABANG, NAMA_CABANG, MONTH " + 
					"    ORDER BY KODE_CABANG, NAMA_CABANG, MONTH ";*/
			sql="SELECT a.KODE_CABANG ,a.NAMA_CABANG, A.YM AS MONTH, count(a.TRANSAKSI) as TRANSAKSI \r\n" + 
					"FROM ( \r\n" + 
					"	 SELECT a.kd_cab_loktx as KODE_CABANG, b.nm_cab as NAMA_CABANG, varchar_format(a.tgl_tx,'yyyy-MON-dd') as DT,\r\n" + 
					"	 count(a.TGL_TX)as TRANSAKSI, 'LTKT Belum Di Laporkan' as KRITERIA,  \r\n" + 
					"	 varchar_format(a.tgl_tx,'yyyy-MON') as YM \r\n" + 
					"	 FROM RPT_LTKT_TRX A\r\n" + 
					"	 INNER JOIN CFG_CABANG B ON A.KD_CAB_LOKTX = B.KD_CAB\r\n" + 
					"	 WHERE  varchar_format(a.tgl_tx,'yyyy-MM') between   '" + sdf2 + "' and '" + sdf1 + "' and a.STS_LAPOR='0' AND KD_CAB_LOKTX= '"+ auth.getUserDetails().getBranchId() +"'  \r\n" + 
					"	 GROUP BY CIFID, TGL_TX, NO_REKENING, NAMA_LENGKAP, DB_KR, STS_LAPOR,KD_CAB_LOKTX, B.NM_CAB ,\r\n" + 
					"	 CAST(TGL_PROSES AS DATE) \r\n" + 
					"	 ORDER BY A.TGL_TX, KD_CAB_LOKTX \r\n" + 
					"    ) a\r\n" + 
					"GROUP BY A.KODE_CABANG,A.NAMA_CABANG, A.YM\r\n" ; 

			List<DTOMap> lineData = masterService.getDataMaster(sql, null);

			for (DTOMap dt : lineData) {
				lbd.setValue(dt.getString("NAMA_CABANG"), dt.getString("MONTH"), dt.getInt("TRANSAKSI"));
				// lbd.setValue(dt.getString("KODE"), dt.getString("THBL"),
				// dt.getInt("JMLLAPOR"));

			}
			
		}

		// batas Akhir test Alben

	}

	private void doLoadData() throws Exception {

		// role 02 STS_LAPOR = 1

		if (auth.getUserDetails().getActiveRole().equals("01") || auth.getUserDetails().getActiveRole().equals("05")) {// admin / supervisi kantor pusat

			DTOMap dto = new DTOMap();
			String stsLapor = "0";
			String cabang = auth.getUserDetails().getBranchId();

			Listitem item = new Listitem();
			dto = (DTOMap) masterService.getMapMaster("SELECT COUNT(1) AS JUMLAH FROM RPT_LTKT WHERE STATUS='2'",
					new Object[] {});
			item.appendChild(new Listcell("i"));
			item.appendChild(new Listcell("LTKT - BELUM DIOTORISASI"));
			item.appendChild(new Listcell(String.valueOf(dto.getInt("JUMLAH"))));

			listData.appendChild(item);

			item = new Listitem();
			dto = new DTOMap();
			dto = (DTOMap) masterService.getMapMaster("SELECT COUNT(1) AS JUMLAH FROM RPT_LTKT WHERE STATUS='0'",
					new Object[] {});
			item.appendChild(new Listcell("ii"));
			item.appendChild(new Listcell("LTKT - BELUM DILAPORKAN CABANG"));
			item.appendChild(new Listcell(String.valueOf(dto.getInt("JUMLAH"))));
			listData.appendChild(item);

			item = new Listitem();
			dto = new DTOMap();
			dto = (DTOMap) masterService.getMapMaster("SELECT COUNT(1) AS JUMLAH FROM RPT_LTKT WHERE STATUS='4'",
					new Object[] {});
			item.appendChild(new Listcell("iii"));
			item.appendChild(new Listcell("LTKT - PERMINTAAN DIKECUALIKAN"));
			item.appendChild(new Listcell(String.valueOf(dto.getInt("JUMLAH"))));
			listData.appendChild(item);

			item = new Listitem();
			dto = new DTOMap();
			dto = (DTOMap) masterService.getMapMaster("SELECT COUNT(1) AS JUMLAH FROM RPT_LTKT WHERE STATUS='9'",
					new Object[] {});
			item.appendChild(new Listcell("iv"));
			item.appendChild(new Listcell("LTKT - KOREKSI"));
			item.appendChild(new Listcell(String.valueOf(dto.getInt("JUMLAH"))));
			listData.appendChild(item);

			item = new Listitem();
			dto = new DTOMap();
			dto = (DTOMap) masterService.getMapMaster("SELECT COUNT(1) AS JUMLAH FROM TBL_NASABAH_REQ WHERE STS_LAPOR<>2 ",
					new Object[] {});
			item.appendChild(new Listcell("v"));
			item.appendChild(new Listcell("PERMINTAAN CIF"));
			item.appendChild(new Listcell(String.valueOf(dto.getInt("JUMLAH"))));
			listData.appendChild(item);

			item = new Listitem();
			dto = new DTOMap();
			dto = (DTOMap) masterService.getMapMaster("SELECT COUNT(1) AS JUMLAH FROM RPT_CIFGANDA WHERE STS_LAPOR='0'",
					new Object[] {});
			item.appendChild(new Listcell("vi"));
			item.appendChild(new Listcell("SUSPECT CIF GANDA - BELUM VERIFIKASI"));
			item.appendChild(new Listcell(String.valueOf(dto.getInt("JUMLAH"))));
			listData.appendChild(item);

			item = new Listitem();
			dto = new DTOMap();
			dto = (DTOMap) masterService.getMapMaster("SELECT COUNT(1) AS JUMLAH FROM RPT_PENGKINIAN_BELUM",
					new Object[] {});
			item.appendChild(new Listcell("vii"));
			item.appendChild(new Listcell("DATA NASABAH BELUM DIKINIKAN"));
			item.appendChild(new Listcell(String.valueOf(dto.getInt("JUMLAH"))));
			listData.appendChild(item);
		} else {
			DTOMap dto = new DTOMap();
			String stsLapor = "0";
			String cabang = auth.getUserDetails().getBranchId();
			String lblLtktBelumLapor = "LTKT - BELUM DILAPORKAN";
			if (auth.getUserDetails().getActiveRole().equals("02")) {
				stsLapor = "1";
				lblLtktBelumLapor = "LTKT - BELUM DIVERIFIKASI";
			}
			Listitem item = new Listitem();

			// query disamakan dengan modul trx wajib lapor
			dto = (DTOMap) masterService.getMapMaster("SELECT COUNT(1) AS JUMLAH FROM ( " + " SELECT COUNT(1) "
					+ " FROM RPT_LTKT_TRX A  INNER JOIN CFG_CABANG B ON A.KD_CAB_LOKTX = B.KD_CAB "
					+ " WHERE KD_CAB_LOKTX= '" + cabang + "' AND STS_LAPOR= '"+stsLapor+"' "
					+ " GROUP BY CIFID, TGL_TX, NO_REKENING, NAMA_LENGKAP, DB_KR, STS_LAPOR,KD_CAB_LOKTX, B.NM_CAB ,"
					+ " CAST(TGL_PROSES AS DATE) ORDER BY TGL_TX, KD_CAB_LOKTX)", new Object[] {});
			/*dto = (DTOMap) masterService.getMapMaster( "SELECT count(1) AS JUMLAH "
			  +" FROM RPT_LTKT_TRX A  INNER JOIN CFG_CABANG B ON A.KD_CAB_LOKTX = B.KD_CAB  "
			  +"  WHERE KD_CAB_LOKTX= '"+ cabang+"' AND  STS_LAPOR='0' ",new Object[] {});*/

			item.appendChild(new Listcell("i"));
			item.appendChild(new Listcell(lblLtktBelumLapor));
			item.appendChild(new Listcell(String.valueOf(dto.getInt("JUMLAH"))));
			listData.appendChild(item);

			item = new Listitem();
			dto = new DTOMap();
			dto = (DTOMap) masterService
					.getMapMaster("SELECT COUNT(1) AS JUMLAH FROM RPT_LTKT_TRX WHERE  KD_CAB_LOKTX='" + cabang
							+ "' AND STS_LAPOR='9'", new Object[] {});
			item.appendChild(new Listcell("ii"));
			item.appendChild(new Listcell("LTKT - KOREKSI"));
			item.appendChild(new Listcell(String.valueOf(dto.getInt("JUMLAH"))));
			listData.appendChild(item);

			item = new Listitem();
			dto = new DTOMap();
			dto = (DTOMap) masterService.getMapMaster("SELECT COUNT(1) AS JUMLAH FROM TBL_NASABAH_REQ WHERE KD_CABANG='"
					+ cabang + "' AND STS_LAPOR <> '2' ", new Object[] {});
			item.appendChild(new Listcell("iii"));
			item.appendChild(new Listcell("PERMINTAAN PENGKINIAN CIF"));
			item.appendChild(new Listcell(String.valueOf(dto.getInt("JUMLAH"))));
			listData.appendChild(item);

			item = new Listitem();
			dto = new DTOMap();
			/*dto = (DTOMap) masterService.getMapMaster(
					"SELECT COUNT(1) AS JUMLAH FROM RPT_CIFGANDA_RINCI WHERE KD_CAB='" + cabang + "' AND STS_LAPOR='0'",
					new Object[] {});*/
			dto = (DTOMap) masterService.getMapMaster("SELECT COUNT(1) AS JUMLAH FROM RPT_CIFGANDA WHERE STS_LAPOR='0' AND KD_CAB='"+ cabang +"' ", new Object[] {});
			item.appendChild(new Listcell("iv"));
			item.appendChild(new Listcell("SUSPECT CIF GANDA - BELUM VERIFIKASI"));
			item.appendChild(new Listcell(String.valueOf(dto.getInt("JUMLAH"))));
			listData.appendChild(item);

			item = new Listitem();
			dto = new DTOMap();
			dto = (DTOMap) masterService.getMapMaster(
					"SELECT COUNT(1) AS JUMLAH FROM RPT_PENGKINIAN_BELUM WHERE KD_CAB='" + cabang + "'",
					new Object[] {});
			item.appendChild(new Listcell("v"));
			item.appendChild(new Listcell("DATA NASABAH BELUM DIKINIKAN"));
			item.appendChild(new Listcell(String.valueOf(dto.getInt("JUMLAH"))));
			listData.appendChild(item);

		}
	}

	public void clearChartDrillDown() {
		chartWithDrillDown.getSeries().setData(new ArrayList<Integer>());
	}

	public void doChartWithDrillDown() {

		chartWithDrillDown.getXAxis().setType("category");
		chartWithDrillDown.setTitle("Total Transaksi");
		chartWithDrillDown.getYAxis().setTitle("Total Transaksi APUPPT");
		chartWithDrillDown.getLegend().setEnabled(false);
		chartWithDrillDown.getPlotOptions().getSeries().setBorderWidth(0);
		chartWithDrillDown.getPlotOptions().getSeries().getDataLabels().setEnabled(true);
		chartWithDrillDown.getPlotOptions().getSeries().getDataLabels().setFormat("{point.y:.f}");
		chartWithDrillDown.getTooltip().setHeaderFormat("<span styel=\"font-size;11px\">{series.name}</span><br>");
		chartWithDrillDown.getTooltip().setPointFormat(
				"<span sytle=\"color:{point.color}\">{point.name}" + "</span>: <b> {point.y:.f}</b> of total<br/>");
		iniSeries();

	}

	public void iniSeries() {
				
		
		Series series = chartWithDrillDown.getSeries();
		series.setName("Header");
		series.setPlotOptions(initPlotOptions());

		ArrayList<Series> drilldowns = new ArrayList<Series>();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");

		Calendar cal = Calendar.getInstance();	

		dt1 = (Date) ComponentUtil.getValue(txtTgl);
		cal.setTime(dt1);
		sdf1 = sdf.format(dt1);
		

		cal.add(Calendar.YEAR, -1);
		
		dt2 = cal.getTime();

		sdf2 = sdf.format(dt2);	

		String sql = "";

		/*sql ="SELECT   a.KODE_CABANG,a.NAMA_CABANG, a.KRITERIA as KRITERIA,  sum(a.TRANSAKSI) as TRANSAKSI \r\n"
				+ "FROM(\r\n" + "	SELECT  KODE_CABANG,  NAMA_CABANG, sum( TRANSAKSI ) AS TRANSAKSI, KRITERIA\r\n"
				+ "	FROM(	 \r\n" 
				+ "		select a.kd_cab_loktx as KODE_CABANG, b.nm_cab as NAMA_CABANG ,    	\r\n"
				+ "		count(a.TGL_TX)as TRANSAKSI, 'LTKT Belum Di Laporkan' as KRITERIA,  varchar_format(a.tgl_tx,'yyyy-MON') as YM    	 \r\n"
				+ "		FROM rpt_ltkt_trx a  	 \r\n"
				+ "		inner join cfg_cabang b on a.kd_cab_loktx=b.kd_cab  	 \r\n"
				+ "		WHERE  varchar_format(a.tgl_tx,'yyyy-MM') between   '" + sdf2 + "' and '" + sdf1+ "' and a.STS_LAPOR='0' \r\n" 
				+ "		AND a.kd_cab_loktx =?	 \r\n"
				+ "		group by a.kd_cab_loktx ,b.nm_cab, a.tgl_tx \r\n" + "	)  a\r\n"
				+ "	GROUP BY 	KODE_CABANG,  NAMA_CABANG, KRITERIA\r\n" 
				+ "	UNION \r\n"
				+ "	SELECT  KODE_CABANG,  NAMA_CABANG, sum( TRANSAKSI ) AS TRANSAKSI, KRITERIA\r\n" + "	FROM(	 \r\n"
				+ "		select a.kd_cab as KODE_CABANG, b.nm_cab as NAMA_CABANG , varchar_format(a.tgl_laporan,'yyyy-MON-dd') as Dt,    	\r\n"
				+ "		 count(a.tgl_laporan)as TRANSAKSI, 'LTKM Belum Di Laporkan' as KRITERIA,    	 \r\n"
				+ "		 varchar_format(a.tgl_laporan,'yyyy-MON') as YM    	 \r\n" + "		 FROM RPT_LTKM a  	 \r\n"
				+ "		 inner join cfg_cabang b on a.kd_cab=b.kd_cab  	 \r\n"
				+ "		 WHERE  varchar_format(a.tgl_laporan,'yyyy-MM') between  '" + sdf2 + "' and '" + sdf1 + "' \r\n"
				+ "		 and a.flg_rpt='00' AND a.kd_cab =? 	 \r\n"
				+ "		 group by a.kd_cab ,b.nm_cab, a.tgl_laporan	\r\n" + "	) a\r\n"
				+ "	GROUP BY 	KODE_CABANG,  NAMA_CABANG, KRITERIA	 	 \r\n" + ")a  \r\n"
				+ "group by  a.KODE_CABANG,a.TRANSAKSI,a.NAMA_CABANG, a.KRITERIA \r\n"
				+ "order by a.KODE_CABANG,a.TRANSAKSI, a.NAMA_CABANG, a.KRITERIA ";*/
		sql=""
			+"SELECT a.KODE_CABANG,a.NAMA_CABANG, a.KRITERIA as KRITERIA,  count(a.TRANSAKSI) as TRANSAKSI, KRITERIA\r\n" + 
			"FROM ( \r\n" + 
			"	 SELECT a.kd_cab_loktx as KODE_CABANG, b.nm_cab as NAMA_CABANG, varchar_format(a.tgl_tx,'yyyy-MON-dd') as DT,\r\n" + 
			"	 count(a.TGL_TX)as TRANSAKSI, 'LTKT Belum Di Laporkan' as KRITERIA,  \r\n" + 
			"	 varchar_format(a.tgl_tx,'yyyy-MON') as YM \r\n" + 
			"	 FROM RPT_LTKT_TRX A\r\n" + 
			"	 INNER JOIN CFG_CABANG B ON A.KD_CAB_LOKTX = B.KD_CAB\r\n" + 
			"	 WHERE  varchar_format(a.tgl_tx,'yyyy-MM') between   '" + sdf2 + "' and '" + sdf1 + "' and a.STS_LAPOR='0' AND KD_CAB_LOKTX= ? \r\n" + 
			"	 GROUP BY CIFID, TGL_TX, NO_REKENING, NAMA_LENGKAP, DB_KR, STS_LAPOR,KD_CAB_LOKTX, B.NM_CAB ,\r\n" + 
			"	 CAST(TGL_PROSES AS DATE) \r\n" + 
			"	 ORDER BY A.TGL_TX, KD_CAB_LOKTX \r\n" + 
			"    ) a\r\n" + 
			"GROUP BY A.KODE_CABANG,A.NAMA_CABANG, A.KRITERIA\r\n" + 
			"UNION\r\n" + 
			"SELECT a.KODE_CABANG,a.NAMA_CABANG, a.KRITERIA as KRITERIA,count( TRANSAKSI ) AS TRANSAKSI, KRITERIA\r\n" + 
			"FROM(	\r\n" + 
			"	select a.kd_cab as KODE_CABANG, b.nm_cab as NAMA_CABANG , varchar_format(a.tgl_laporan,'yyyy-MON-dd') as DT,   \r\n" + 
			"	count(a.tgl_laporan)as TRANSAKSI, 'LTKM Belum Di Laporkan' as KRITERIA,    	 \r\n" + 
			"	varchar_format(a.tgl_laporan,'yyyy-MON') as YM    	\r\n" + 
			"	FROM RPT_LTKM a  	\r\n" + 
			"	inner join cfg_cabang b on a.kd_cab=b.kd_cab \r\n" + 
			"	WHERE  varchar_format(a.tgl_laporan,'yyyy-MM') between  '" + sdf2 + "' and '" + sdf1 + "' \r\n" + 
			"	and a.flg_rpt='00' AND a.kd_cab =? 	\r\n" + 
			"	group by a.kd_cab ,b.nm_cab, a.tgl_laporan		 	\r\n" + 
			"	 )a  \r\n" + 
			"group by  a.KODE_CABANG,a.TRANSAKSI,a.NAMA_CABANG, a.KRITERIA \r\n" + 
			"";
		
		//log.info("sql : {} ", sql);

		List<DTOMap> lsHeaders;
		lsHeaders = masterService.getDataMaster(sql,
				new Object[] { ComponentUtil.getValue(cmbCabang), ComponentUtil.getValue(cmbCabang) });
		LinkedHashMap<DMHead, Double> dms = new LinkedHashMap<DMHead, Double>();

		if (!lsHeaders.isEmpty() || lsHeaders != null) {
			for (DTOMap dt : lsHeaders) {

				// dms.put(new
				// DMHead(dt.getString("KRITERIA")),Double.valueOf(dt.getInt("TRANSAKSI")));
				dms.put(head = new DMHead(dt.getString("KRITERIA")), Double.valueOf(dt.getInt("TRANSAKSI")));

			}

			for (Entry<DMHead, Double> entry : dms.entrySet()) {
				DMHead test = entry.getKey();				
				Point browserGroupPoint = new Point(test.getLbl(), entry.getValue());				
				series.addPoint(browserGroupPoint);
				if (entry.getValue() > 1) {
					drilldowns.add(createBrowserGroupDrilldown(browserGroupPoint, entry));
				}

			}

			chartWithDrillDown.getDrilldown().setSeries(drilldowns);
		} else {
			MessageBox.showInformation("Data untuk Dashboard Total Transaksi kosong..!");
		}

	}

	private Series createBrowserGroupDrilldown(Point browserGroupPoint, Entry<DMHead, Double> entry) {

		DMHead browserGroup = entry.getKey();
		
		
		String browserGroupLabel = browserGroup.getLbl();		
		browserGroupPoint.setDrilldown(browserGroupLabel);
		Series drilldownSeries = new Series(browserGroupLabel);

		for (DashboardModel browser : lsKriteria(browserGroup)) {
			// drilldownSeries.addPoint(new Point(new SimpleDateFormat("yyyy-MM-dd").format(
			// browser.getTglTrx()), browser.getTrx()));
			// drilldownSeries.addPoint(new Point(browser.getCabang(), browser.getTrx()));
			drilldownSeries.addPoint(new Point(browser.getTglTrx(), browser.getTrx()));
		}

		return drilldownSeries;
	}

	private List<DashboardModel> lsKriteria(DMHead kriteria) {
		List<DashboardModel> dm = new LinkedList<DashboardModel>();
		String sql="";
		List<DTOMap> lsDetail;
		if(kriteria.getLbl().equals("LTKT Belum Di Laporkan")) {
			/*sql = "select a.kd_cab_loktx as KODE_CABANG, b.nm_cab as NAMA_CABANG,varchar_format( a.tgl_tx,'yyyy-MON-dd') as DT , 	\r\n"
					+ "		count(a.TGL_TX)as TRANSAKSI, 'LTKT Belum Di Laporkan' as KRITERIA,  varchar_format(a.tgl_tx,'yyyy-MON') as YM    	 \r\n"
					+ "		FROM rpt_ltkt_trx a  	 \r\n"
					+ "		inner join cfg_cabang b on a.kd_cab_loktx=b.kd_cab  	 \r\n"
					+ "		WHERE  varchar_format(a.tgl_tx,'yyyy-MM') between   '" + sdf2 + "' and '" + sdf1+ "' and a.STS_LAPOR='0' \r\n" 
					+ "		AND a.kd_cab_loktx =?	 \r\n"
					+ "		group by a.kd_cab_loktx ,b.nm_cab, a.tgl_tx ";*/
			sql=""
				+"SELECT a.kd_cab_loktx as KODE_CABANG, b.nm_cab as NAMA_CABANG, varchar_format(a.tgl_tx,'yyyy-MON-dd') as DT," + 
				"	 count(a.TGL_TX)as TRANSAKSI, 'LTKT Belum Di Laporkan' as KRITERIA,  \r\n" + 
				"	 varchar_format(a.tgl_tx,'yyyy-MON') as YM \r\n" + 
				"	 FROM RPT_LTKT_TRX A\r\n" + 
				"	 INNER JOIN CFG_CABANG B ON A.KD_CAB_LOKTX = B.KD_CAB\r\n" + 
				"	 WHERE  varchar_format(a.tgl_tx,'yyyy-MM') between   '" + sdf2 + "' and '" + sdf1 + "' and a.STS_LAPOR='0' AND KD_CAB_LOKTX= ? \r\n" + 
				" GROUP BY a.kd_cab_loktx, b.nm_cab, a.TGL_TX "+
				" ORDER BY a.kd_cab_loktx, b.nm_cab,a.TGL_TX ";
			
		}else {
			/*sql="	select a.kd_cab as KODE_CABANG, b.nm_cab as NAMA_CABANG , varchar_format(a.tgl_laporan,'yyyy-MON-dd') as DT,    	\r\n"
					+ "		 count(a.tgl_laporan)as TRANSAKSI, 'LTKM Belum Di Laporkan' as KRITERIA,    	 \r\n"
					+ "		 varchar_format(a.tgl_laporan,'yyyy-MON') as YM    	 \r\n" 
					+ "		 FROM RPT_LTKM a  	 \r\n"
					+ "		 inner join cfg_cabang b on a.kd_cab=b.kd_cab  	 \r\n"
					+ "		 WHERE  varchar_format(a.tgl_laporan,'yyyy-MM') between  '" + sdf2 + "' and '" + sdf1 + "' \r\n"
					+ "		 and a.flg_rpt='00' AND a.kd_cab =? 	 \r\n"
					+ "		 group by a.kd_cab ,b.nm_cab, a.tgl_laporan	";*/
			sql=""
				+"select a.kd_cab as KODE_CABANG, b.nm_cab as NAMA_CABANG , varchar_format(a.tgl_laporan,'yyyy-MON') as DT,   \r\n" + 
				"	count(a.tgl_laporan)as TRANSAKSI, 'LTKM Belum Di Laporkan' as KRITERIA,    	 \r\n" + 
				"	varchar_format(a.tgl_laporan,'yyyy-MON') as YM    	\r\n" + 
				"	FROM RPT_LTKM a  	\r\n" + 
				"	inner join cfg_cabang b on a.kd_cab=b.kd_cab \r\n" + 
				"	WHERE  varchar_format(a.tgl_laporan,'yyyy-MM') between  '" + sdf2 + "' and '" + sdf1 + "' \r\n" + 
				"	and a.flg_rpt='00' AND a.kd_cab =?	\r\n" + 
				"	group by a.kd_cab ,b.nm_cab, a.tgl_laporan	";
			
		}	
		
		
		lsDetail = masterService.getDataMaster(sql ,new Object[] { ComponentUtil.getValue(cmbCabang)});
		for (DTOMap dt : lsDetail) {
			dm.add(new DashboardModel(new DMHead(dt.get("KRITERIA").toString()), dt.getString("DT"),
					dt.getInt("TRANSAKSI")));
		}

		return dm;

	}

	private ColumnPlotOptions initPlotOptions() {
		ColumnPlotOptions plotOptions = new ColumnPlotOptions();
		plotOptions.setColorByPoint(true);
		return plotOptions;
	}

}
