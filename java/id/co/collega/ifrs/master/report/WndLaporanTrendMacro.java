package id.co.collega.ifrs.master.report;

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
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;

import com.jet.gand.services.GlobalVariable;

import id.co.collega.ifrs.common.DTOMap;
import id.co.collega.ifrs.common.FunctionUtils;
import id.co.collega.ifrs.common.JdbcTemplate;
import id.co.collega.ifrs.master.service.MasterServices;
import id.co.collega.ifrs.util.ComponentUtil;

@org.springframework.stereotype.Component
@Scope("execution")
public class WndLaporanTrendMacro extends SelectorComposer<Component> {

	@Autowired MasterServices masterService;
    @Autowired JdbcTemplate jt;
	
    private CategoryModel ctm;
	
    @Wire Charts chartLine;
    @Wire Combobox cmbModelMacro;
    
	DTOMap cfg_sys=(DTOMap)GlobalVariable.getInstance().get("cfgsys");
	
	SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MMM");
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		cmbModelMacro.addEventListener(Events.ON_CHANGE,
				new EventListener() {
					public void onEvent(Event event) throws Exception {
						doBeforeLoadData();
					}
				});
		
		doLoadDataProduk();
		doBeforeLoadData();
//		doLineChart();
	}
	
	private void doLoadDataProduk() {
		List<DTOMap> listProduk=masterService.getDataMaster(" SELECT PARMID,PARMNM "
				+ "												FROM CFG_PARM "
				+ "												WHERE PARMGRP=30 ORDER BY PARMID  ",new Object[]{});
			cmbModelMacro.getItems().clear();
		if (listProduk.size() > 0) {
			Comboitem ciPrd=new Comboitem();
			ciPrd.setLabel("All");
			ciPrd.setValue("All");
			cmbModelMacro.appendChild(ciPrd);
			for (DTOMap dtoMap : listProduk) {
				ciPrd=new Comboitem();
				ciPrd.setLabel(dtoMap.getString("PARMID")+" - "+dtoMap.getString("PARMNM"));
				ciPrd.setValue(dtoMap.getString("PARMID"));
				cmbModelMacro.appendChild(ciPrd);
			}
			cmbModelMacro.setSelectedIndex(0);
		}
	}
	
	private void doBeforeLoadData(){
		String prodId= (String) ComponentUtil.getValue(cmbModelMacro);
		if(prodId == "All") {
			doLineChartAll();
		} else {
			String groupId = "30";
			
			String parmgroup = groupId + prodId;
			doLineChart(parmgroup);			
		}
	}
	
	public void doLineChart(String parmgroup){
		CategoryModel model;
		model = new DefaultCategoryModel();
		
    	List<DTOMap> listData = masterService.getDataMaster("SELECT CAST(LEFT(A.PARMID,4)||'-'||RIGHT(A.PARMID,2) AS DATE) TANGGAL ,"
    			+ "	  	CAST(A.PARMIDOTH AS NUMERIC), "
    			+ "		CAST(B.PARMID||' - '||B.PARMNM AS VARCHAR) PRODUK "
    			+ "	  	FROM cfg_parm A "
    			+ "		LEFT OUTER JOIN CFG_PARM B "
    			+ "		ON CAST(B.PARMGRP AS CHAR(2))||B.PARMID = CAST(A.parmgrp AS CHAR(4)) "
    			+ "		WHERE A.parmgrp = " + parmgroup + " ORDER BY 1 ASC"
				, null);

		
		
		if (listData.size() > 0) {
			for (DTOMap map : listData) {
				model.setValue(
						map.getString("PRODUK"), 
						sdf.format(map.getDate("TANGGAL")),
						map.getBigDecimal("PARMIDOTH"));
			}
		}
		
		chartLine.setWidth(1200);
		chartLine.setHeight(400);
		
		chartLine.setAnimation(true);

		chartLine.setModel(model);

		chartLine.setTitle("Trend Macro");

		chartLine.getTitle().setX(-20);
		chartLine.getSubtitle().setX(-20);
		
		chartLine.getYAxis().setTitle("Percent %");
		PlotLine plotLine= new PlotLine();
		plotLine.setValue(0);
		plotLine.setWidth(1);
		plotLine.setColor("#808080");
		
		chartLine.getYAxis().addPlotLine(plotLine);
		
		chartLine.getTooltip().setValueSuffix(" %");
		
		Legend legend=chartLine.getLegend();
		legend.setLayout("vertical");
		legend.setAlign("right");
		legend.setVerticalAlign("middle");
		legend.setBorderWidth(0);
    }
	
	public void doLineChartAll(){
	CategoryModel model;
	model = new DefaultCategoryModel();
	
	List<DTOMap> listData = masterService.getDataMaster("select CAST(LEFT(A.PARMID,4)||'-'||RIGHT(A.PARMID,2)||'-01' AS DATE) TANGGAL ,"
    			+ "	  CAST(A.PARMIDOTH AS NUMERIC),   "
    			+ "	  CAST(B.PARMID||' - '||B.PARMNM AS VARCHAR) PRODUK"
    			+ "	from cfg_parm A LEFT OUTER JOIN CFG_PARM B ON CAST(B.PARMGRP AS CHAR(2))||B.PARMID = CAST(A.parmgrp AS CHAR(4))"
    			+ "	Where LEFT(CAST(A.parmgrp AS VARCHAR),2) = '30' AND A.PARMIDOTH IS NOT NULL ORDER BY 3,1 asc"
				, null);
		if (listData.size() > 0) {
			for (DTOMap map : listData) {
				model.setValue(
						map.getString("PRODUK"), 
						sdf.format(map.getDate("TANGGAL")),
						map.getBigDecimal("PARMIDOTH"));
			}
		}		
		chartLine.setWidth(1300);
		chartLine.setHeight(400);
		
		chartLine.setAnimation(true);
//		crt.setType("bar");
		
		chartLine.setTitle("Trend Macro");

		chartLine.setModel(model);
		
		chartLine.getTitle().setX(-20);
		chartLine.getSubtitle().setX(-20);
		
		chartLine.getYAxis().setTitle("Percen %");
		PlotLine plotLine= new PlotLine();
		plotLine.setValue(0);
		plotLine.setWidth(1);
		plotLine.setColor("#808080");
		
		chartLine.getYAxis().addPlotLine(plotLine);
		
		chartLine.getTooltip().setValueSuffix(" %");
		
		Legend legend=chartLine.getLegend();
		legend.setLayout("vertical");
		legend.setAlign("right");
		legend.setVerticalAlign("middle");
		legend.setBorderWidth(0);
    }

}
