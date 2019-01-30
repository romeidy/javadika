package id.co.collega.ifrs.master;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.zkoss.chart.Chart;
import org.zkoss.chart.Charts;
import org.zkoss.chart.Legend;
import org.zkoss.chart.PlotLine;
import org.zkoss.chart.Series;
import org.zkoss.chart.Tooltip;
import org.zkoss.chart.model.CategoryModel;
import org.zkoss.chart.model.DefaultCategoryModel;
import org.zkoss.chart.options3D.Options3D;
import org.zkoss.chart.plotOptions.PieDataLabels;
import org.zkoss.chart.plotOptions.PiePlotOptions;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;

import com.jet.gand.services.GlobalVariable;

import id.co.collega.ifrs.common.DTOMap;
import id.co.collega.ifrs.common.FunctionUtils;
import id.co.collega.ifrs.common.JdbcTemplate;
import id.co.collega.ifrs.master.service.MasterServices;

@org.springframework.stereotype.Component
@Scope("execution")
public class WndHome extends SelectorComposer<Component> {

	@Autowired MasterServices masterService;
    @Autowired JdbcTemplate jt;
	
	private LineBasicData lbd;
    private CategoryModel ctm;
	
    @Wire Charts chartPie;
    @Wire Charts chartBar;
    @Wire Charts chartBarTop5;
    @Wire Charts chartPieByRating;
    @Wire Charts chartBarByProduk;
    @Wire Charts chartBarTop10;
    private ChartPoints dtPoint;
    private Series seriesByRating;
	private ChartPoints dtPointByRating;
	private Series series;
	DTOMap cfg_sys=(DTOMap)GlobalVariable.getInstance().get("cfgsys");
	
	SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		doPieChart();
		
		doBarChart();
		
		doBarChartTop5();
	}
	
	public void doPieChart(){
    	series = chartPie.getSeries();
		series.setType("pie");
		// series.setName("Browser share");
		series.setName("OLIBsIFRS");

		Options3D opt3d = chartPie.getOptions3D();
        opt3d.setEnabled(true);
        opt3d.setAlpha(45);
        opt3d.setBeta(0);
		
		Chart chartOptional = chartPie.getChart();
		chartOptional.setPlotBorderWidth(0);
		chartOptional.setPlotShadow(false);

		chartPie.getTooltip().setPointFormat("{series.name}: <b>{point.percentage:.1f}%</b>");
		
		PiePlotOptions plotOptions = chartPie.getPlotOptions().getPie();
		plotOptions.setAllowPointSelect(true);
		plotOptions.setCursor("pointer");
        plotOptions.getDataLabels().setEnabled(false);
        plotOptions.setDepth(35);
        
        PieDataLabels dataLabels = (PieDataLabels) plotOptions.getDataLabels();
		dataLabels.setEnabled(true);
		dataLabels.setFormat("<b>{point.name}</b> <br> ({point.percentage:.1f} %)");
		
		doDataPoint();
		
		seriesByRating = chartPieByRating.getSeries();
		seriesByRating.setType("pie");
		// series.setName("Browser share");
		seriesByRating.setName("OLIBsIFRS");
		
		Options3D opt3dByRating = chartPieByRating.getOptions3D();
		opt3dByRating.setEnabled(true);
		opt3dByRating.setAlpha(45);
		opt3dByRating.setBeta(0);
		
		Chart chartOptionalByRating = chartPieByRating.getChart();
		chartOptionalByRating.setPlotBorderWidth(0);
		chartOptionalByRating.setPlotShadow(false);

		chartPieByRating.getTooltip().setPointFormat("{series.name}: <b>{point.percentage:.1f}%</b>");
		
		PiePlotOptions plotOptionsByRating = chartPieByRating.getPlotOptions().getPie();
		plotOptionsByRating.setAllowPointSelect(true);
		plotOptionsByRating.setCursor("pointer");
		plotOptionsByRating.getDataLabels().setEnabled(false);
		plotOptionsByRating.setDepth(35);
        
        PieDataLabels dataLabelsByRating = (PieDataLabels) plotOptionsByRating.getDataLabels();
        dataLabelsByRating.setEnabled(true);
        dataLabelsByRating.setFormat("<b>{point.name}</b> <br> ({point.percentage:.1f} %)");
		
		doDataPointByRating();
    }
	
	private void doDataPoint() {
		DTOMap dtMstPoint;
		String sql="";
		Integer countRating=(Integer)jt.queryObject(" SELECT COUNT(DISTINCT PARMIDOTH) "
				+ "										FROM CFG_PARM "
				+ "										WHERE PARMGRP=3 ", Integer.class); // Parameter untuk mencari berapa banyak Stage
		for (int j = 1; j <= countRating; j++) {
			sql = " SELECT ROUND(SUM(B.ECL),0) AS ECL "
					+ "	FROM LOAN_MASTER A "
					+ "			LEFT OUTER JOIN CFG_PARM C "
					+ "					ON C.PARMGRP = 1 "
					+ "						AND C.PARMID  = A.PRODID	"
    				+ "	        LEFT OUTER JOIN CFG_PARM D "
    				+ "					ON D.PARMGRP = 3 "
    				+ "						AND CAST(D.PARMID AS INT) = A.RATING,"
					+ "     ECL_LT B"
					+ "	WHERE A.ACCNBR = B.ACCNBR "
					+ "	AND A.TGL_POS=B.TGL_POS		"
					+ "	AND A.TGL_POS='"+sdf.format(cfg_sys.getDate("ECL_DATE"))+"' "
					+ "	AND A.ACCSTS NOT IN (0,6,7,8,9) "
					+ "	AND D.PARMIDOTH='"+String.valueOf(j)+"' ";
			
			dtMstPoint = masterService.getMapMaster(sql, new Object[] {});
			if (dtMstPoint!=null) {
				if (dtMstPoint.getBigDecimal("ECL") !=null ) {
					dtPoint = new ChartPoints("Stage "+String.valueOf(j)+": Rp. "+FunctionUtils.moneyToText(dtMstPoint.getBigDecimal("ECL")), dtMstPoint.getBigDecimal("ECL").doubleValue());
					series.addPoint(dtPoint);
				}
			}
		}
	}
	
	private void doDataPointByRating() {
		DTOMap dtMstPoint;
		String sql="";
		Integer countRating=(Integer)jt.queryObject(" SELECT COUNT(DISTINCT PARMIDOTH) "
				+ "										FROM CFG_PARM "
				+ "										WHERE PARMGRP=2 ", Integer.class); // Parameter untuk mencari berapa banyak Stage
		for (int j = 1; j <= countRating; j++) {
			sql = " SELECT ROUND(SUM(B.ECL),0) AS ECL "
					+ "	FROM LOAN_MASTER A "
    				+ "	        LEFT OUTER JOIN CFG_PARM D "
    				+ "					ON D.PARMGRP = 2 "
    				+ "						AND CAST(D.PARMID AS INT) = A.RATING,"
					+ "     ECL_LT B"
					+ "	WHERE A.ACCNBR = B.ACCNBR "
					+ "	AND A.TGL_POS=B.TGL_POS		"
					+ "	AND A.TGL_POS='"+sdf.format(cfg_sys.getDate("ECL_DATE"))+"' "
					+ "	AND A.ACCSTS NOT IN (0,6,7,8,9) "
					+ "	AND A.RATING="+j+" ";
				
			dtMstPoint = masterService.getMapMaster(sql, new Object[] {});
			if (dtMstPoint!=null) {
				if (dtMstPoint.getBigDecimal("ECL") !=null ) {
					dtPointByRating = new ChartPoints("Rating "+String.valueOf(j)+": Rp. "+FunctionUtils.moneyToText(dtMstPoint.getBigDecimal("ECL")), dtMstPoint.getBigDecimal("ECL").doubleValue());
					seriesByRating.addPoint(dtPointByRating);
				}
			}
		}
	}
	
	public void doLineChart(Charts crt,Integer RATING){
    	lbd = new LineBasicData();
		List<DTOMap> listData = masterService.getDataMaster(" SELECT A.PRODID,(SELECT DISTINCT PRODUCT_NAME FROM D_PRODUCT WHERE PRODUCT_CODE=A.PRODID) AS PRODUCT_NAME ,A.PD_SEQ,A.PD_PCT from REF_PD_LT_TEMP A WHERE A.RATING="+RATING +" ORDER BY PD_SEQ "
				, null);
		if (listData.size() > 0) {
			for (DTOMap map : listData) {
				lbd.setValue(map.getString("PRODID")+" - "+map.getString("PRODUCT_NAME"),map.getInt("PD_SEQ"),map.getBigDecimal("PD_PCT"));
			}
		}
		
		crt.setWidth(1000);
		crt.setHeight(300);
		
		crt.setAnimation(true);
//		crt.setType("bar");
		
		crt.setTitle("PD Lifetime");
		crt.setSubtitle("Rating : "+RATING.toString());
		crt.setModel(lbd);
		crt.getTitle().setX(-20);
		crt.getSubtitle().setX(-20);
		
		crt.getYAxis().setTitle("Percen %");
		PlotLine plotLine= new PlotLine();
		plotLine.setValue(0);
		plotLine.setWidth(1);
		plotLine.setColor("#808080");
		
		crt.getYAxis().addPlotLine(plotLine);
		
		crt.getTooltip().setValueSuffix(" %");
		
		Legend legend=crt.getLegend();
		legend.setLayout("vertical");
		legend.setAlign("right");
		legend.setVerticalAlign("middle");
		legend.setBorderWidth(0);
    }
	
	public void doBarChart() {
    	ctm=new DefaultCategoryModel();
    	
    	List<DTOMap> listData = masterService.getDataMaster(" SELECT C.VIEWORDNM AS SEGMEN, D.VIEWORDNM AS ECLNM, ROUND(SUM(B.ECL),2) AS ECL"
    			+ "												FROM LOAN_MASTER A "
    			+ "													LEFT OUTER JOIN CFG_PARM C "
    			+ "														ON C.PARMGRP = 1 "
    			+ "															AND C.PARMID  = A.PRODID"
    			+ "											        LEFT OUTER JOIN CFG_PARM D "
    			+ "														ON D.PARMGRP = 3 "
    			+ "															AND CAST(D.PARMID AS INT) = A.RATING,"
    			+ "										    		ECL_LT B"
    			+ "												WHERE A.ACCNBR = B.ACCNBR "
    			+ "													AND A.TGL_POS=B.TGL_POS		"
    			+ "													AND A.TGL_POS='"+sdf.format(cfg_sys.getDate("ECL_DATE"))+"' "
    			+ "													AND A.ACCSTS NOT IN (0,6,7,8,9)"
    			+ "												GROUP BY C.VIEWORDNM, D.VIEWORDNM "
				, null);
		if (listData.size() > 0) {
			for (DTOMap map : listData) {
				ctm.setValue(	map.getString("ECLNM"),
								map.getString("SEGMEN"),
								map.getBigDecimal("ECL").setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
			}
		}
		chartBar.setModel(ctm);
		chartBar.getYAxis().getTitle().setText("Values");
		PlotLine plotLine= new PlotLine();
		plotLine.setValue(0);
		plotLine.setWidth(1);
		plotLine.setColor("#808080");
		chartBar.getYAxis().addPlotLine(plotLine);
		
		Options3D opt3d = chartBar.getOptions3D();
        opt3d.setEnabled(true);
        opt3d.setAlpha(45);
        opt3d.setBeta(0);
		
		chartBar.getYAxis().getLabels().setFormat("Rp. {value:,0f}");
        Tooltip tooltip = chartBar.getTooltip();
        tooltip.setHeaderFormat("<span style=\"font-size:10px\">{point.key}</span><table>");
        tooltip.setPointFormat("<tr><td style=\"color:{series.color};padding:0\">"
        		+ "				{series.name}: </td> "
        		+ "				<td style=\"padding:0\"><b>Rp. {point.y:,.0f}</b></td></tr>");
        tooltip.setFooterFormat("</table>");
        tooltip.setShared(true);
        tooltip.setUseHTML(true);
        
        
        ctm=new DefaultCategoryModel();
    	listData.clear();
    	listData = masterService.getDataMaster(" SELECT TOP 5 C.PARMID+' - '+C.PARMNM AS PRODUK, 	"
    			+ "										'Top 5 ECL by Produk' AS ECLNM, 				"
    			+ "										ROUND(SUM(B.ECL),2) AS ECL			"
    			+ "								FROM LOAN_MASTER A 							"
    			+ "									LEFT OUTER JOIN CFG_PARM C 				"
    			+ "										ON C.PARMGRP = 1 					"
    			+ "											AND C.PARMID  = A.PRODID,		"
    			+ "							        ECL_LT B								"
    			+ "								WHERE A.ACCNBR = B.ACCNBR 					"
    			+ "									AND A.TGL_POS=B.TGL_POS					"
    			+ "									AND A.TGL_POS='"+sdf.format(cfg_sys.getDate("ECL_DATE"))+"' "
    			+ "									AND A.ACCSTS NOT IN (0,6,7,8,9)"
    			+ "								GROUP BY C.PARMID,C.PARMNM "
    			+ "								ORDER BY 3 DESC	"
				, null);
		if (listData.size() > 0) {
			for (DTOMap map : listData) {
				ctm.setValue(	map.getString("ECLNM"),
								map.getString("PRODUK"),
								map.getBigDecimal("ECL").setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
			}
		}
		chartBarByProduk.setModel(ctm);
		chartBarByProduk.getYAxis().getTitle().setText("Values");
		
		PlotLine plotLineByProduk= new PlotLine();
		plotLineByProduk.setValue(0);
		plotLineByProduk.setWidth(1);
		plotLineByProduk.setColor("#808080");
		chartBarByProduk.getYAxis().addPlotLine(plotLineByProduk);
		
		Options3D opt3dByProduk = chartBarByProduk.getOptions3D();
		opt3dByProduk.setEnabled(true);
		opt3dByProduk.setAlpha(45);
		opt3dByProduk.setBeta(0);
		
		chartBarByProduk.getYAxis().getLabels().setFormat("Rp. {value:,0f}");
        Tooltip tooltipByProduk = chartBarByProduk.getTooltip();
        tooltipByProduk.setHeaderFormat("<span style=\"font-size:10px\">{point.key}</span><table>");
        tooltipByProduk.setPointFormat("<tr><td style=\"color:{series.color};padding:0\">"
        		+ "				{series.name}: </td> "
        		+ "				<td style=\"padding:0\"><b>Rp. {point.y:,.0f}</b></td></tr>");
        tooltipByProduk.setFooterFormat("</table>");
        tooltipByProduk.setShared(true);
        tooltipByProduk.setUseHTML(true);
        
        ctm=new DefaultCategoryModel();
    	listData.clear();
    	listData = masterService.getDataMaster(" SELECT TOP 10 Z.PRODID+' - '+Z.PRODNM AS PRODUK, 			"
    			+ "									CASE WHEN Z.ECL_RATING_1 > CAST(0 AS numeric) 			"
    			+ "									THEN CAST(ROUND((Z.ECL_RATING_1 * 100) / Z.NILAI_WAJAR  ,2) AS DECIMAL)	"
    			+ "									ELSE  CAST(0 AS DECIMAL)  END AS OUTSTANDING , 							"
    			+ "					'TOP 10 Expected Credit Lost by Total Outstanding Produk per Rating 1' AS DESCRIPTION	"
    			+ "								FROM (														"
    			+ "										SELECT A.PRODID AS PRODID, C.PARMNM AS PRODNM,		"
    			+ "											SUM((	CASE WHEN ISNULL(A.ENDBAL,0) < 0 		"
    			+ "													THEN 0 									"
    			+ "													ELSE ISNULL(A.ENDBAL,0) END)			"
    			+ "							                -ISNULL(A.AMOREIR,0)							"
    			+ "											-ISNULL(A.MODIFIKASI,0)) AS NILAI_WAJAR,		"
    			+ "						        	       SUM(B.ECL) AS ECL_RATING_1						"
    			+ "								        FROM LOAN_MASTER A "
    			+ "													LEFT OUTER JOIN CFG_PARM C 				"
    			+ "															ON C.PARMGRP = 1 				"
    			+ "															AND C.PARMID  = A.PRODID,		"
    			+ "								             ECL_LT B										"
    			+ "								        WHERE A.TGL_POS = '"+sdf.format(cfg_sys.getDate("ECL_DATE"))+"' AND"
    			+ "								              A.ACCSTS NOT IN (0,6,7,8,9) AND				"
    			+ "								              A.ACCNBR = B.ACCNBR AND						"
    			+ "								              A.TGL_POS = B.TGL_POS AND						"
    			+ "								              A.RATING = 1									"
    			+ "								        GROUP BY A.PRODID,C.PARMNM ) Z								"
    			+ "									ORDER BY 2 DESC											"
				, null);
		if (listData.size() > 0) {
			for (DTOMap map : listData) {
				ctm.setValue(	map.getString("DESCRIPTION"),
								map.getString("PRODUK"),
								map.getBigDecimal("OUTSTANDING").setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
			}
		}
		chartBarTop10.setModel(ctm);
		chartBarTop10.getYAxis().getTitle().setText("Percent (%)");
		
		PlotLine plotLineTop10= new PlotLine();
		plotLineTop10.setValue(0);
		plotLineTop10.setWidth(1);
		plotLineTop10.setColor("#808080");
		chartBarTop10.getYAxis().addPlotLine(plotLineTop10);
		
		Options3D opt3dTop10 = chartBarTop10.getOptions3D();
		opt3dTop10.setEnabled(true);
		opt3dTop10.setAlpha(45);
		opt3dTop10.setBeta(0);
		
		chartBarTop10.getYAxis().getLabels().setFormat("{value:,2f}");
        Tooltip tooltipTop10 = chartBarTop10.getTooltip();
        tooltipTop10.setHeaderFormat("<span style=\"font-size:10px\">{point.key}</span><table>");
        tooltipTop10.setPointFormat("<tr><td style=\"color:{series.color};padding:0\">"
        		+ "				{series.name}: </td> "
        		+ "				<td style=\"padding:0\"><b> {point.y:,2f} (%)</b></td></tr>");
        tooltipTop10.setFooterFormat("</table>");
        tooltipTop10.setShared(true);
        tooltipTop10.setUseHTML(true);
	}
    
    public void doBarChartTop5() {
    	ctm=new DefaultCategoryModel();
    	
    	List<DTOMap> listData = masterService.getDataMaster(" SELECT TOP 5 B.BRANCHID||' - '||C.NM_CAB CAB, "
    			+ "												SUM(B.ECL) ECL , 'ECL Amount' ECLAmount"
    			+ "												FROM LOAN_MASTER A ,"
    			+ "										     			ECL_LT B, "
    			+ "														CFG_CABANG C "
    			+ "												WHERE A.ACCNBR = B.ACCNBR "
    			+ "													AND A.TGL_POS=B.TGL_POS		"
    			+ "													AND C.TGL_POS=B.TGL_POS		"
    			+ "													AND C.KD_CAB=B.BRANCHID		"
    			+ "													AND A.TGL_POS='"+sdf.format(cfg_sys.getDate("ECL_DATE"))+"' "
    			+ "													AND A.ACCSTS NOT IN (0,6,7,8,9)"
    			+ "												GROUP BY CAB"
    			+ "												ORDER BY 2 DESC  "
				, null);
		if (listData.size() > 0) {
			for (DTOMap map : listData) {
				ctm.setValue(	map.getString("CAB"),
								map.getString("ECLAmount"),
								map.getBigDecimal("ECL").setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
			}
		}
		chartBarTop5.setModel(ctm);
		chartBarTop5.getYAxis().getTitle().setText("Values");
		PlotLine plotLine= new PlotLine();
		plotLine.setValue(0);
		plotLine.setWidth(1);
		plotLine.setColor("#808080");

		chartBarTop5.getYAxis().addPlotLine(plotLine);
		
		Options3D opt3d = chartBarTop5.getOptions3D();
        opt3d.setEnabled(true);
//        opt3d.setAlpha(15);
//        opt3d.setBeta(15);
        opt3d.setDepth(30);
		
//		chartBarTop5.getYAxis().getLabels().setFormat("{value} M");
        Tooltip tooltip = chartBarTop5.getTooltip();
        tooltip.setHeaderFormat("<span style=\"font-size:10px\">{point.key}</span><table>");
        tooltip.setPointFormat("<tr><td style=\"color:{series.color};padding:0\">"
        		+ "				{series.name}: </td> "
        		+ "				<td style=\"padding:0\"><b>Rp. {point.y:,.0f}</b></td></tr>");
        tooltip.setFooterFormat("</table>");
        tooltip.setValuePrefix(" Jt");
        tooltip.setShared(true);
        tooltip.setUseHTML(true);
	}

}
