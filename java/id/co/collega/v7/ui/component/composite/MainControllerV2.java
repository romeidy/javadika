package id.co.collega.v7.ui.component.composite;


import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.jfree.chart.demo.BarChartDemo1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
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
import org.zkoss.image.AImage;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.Disable;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zkmax.zul.Nav;
import org.zkoss.zkmax.zul.Navbar;
import org.zkoss.zkmax.zul.Navitem;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Center;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Html;
import org.zkoss.zul.Image;
import org.zkoss.zul.Include;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treecol;
import org.zkoss.zul.Treecols;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Treerow;
import org.zkoss.zul.Vlayout;
import org.zkoss.zul.West;
import org.zkoss.zul.Window;

import com.jet.gand.services.GlobalVariable;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import id.co.collega.ifrs.common.Cryptograph;
import id.co.collega.ifrs.common.DTOMap;
import id.co.collega.ifrs.common.FunctionUtils;
import id.co.collega.ifrs.common.JdbcTemplate;
import id.co.collega.ifrs.master.ChartPoints;
import id.co.collega.ifrs.master.LineBasicData;
import id.co.collega.ifrs.master.service.MasterServices;
import id.co.collega.ifrs.security.service.impl.AuthenticationServiceImpl;
import id.co.collega.v7.core.model.Branch;
import id.co.collega.v7.core.model.HasListBranch;
import id.co.collega.v7.security.UserDetails;
import id.co.collega.v7.ui.component.AbstractMenuLoader;
import id.co.collega.v7.ui.component.DialogUtil;
import id.co.collega.v7.ui.component.MenuLoader;
import id.co.collega.v7.ui.component.MenuTreeItem;
import id.co.collega.v7.ui.component.MenuTreeNode;

@Controller("MainControllerV2")
@Scope("desktop")
public class MainControllerV2 extends SelectorComposer<Borderlayout>{

    protected static final Navitem Navitem = null;

	@Autowired(required = false)
    MenuLoader menuLoader;

    @Autowired(required = false)
    HasListBranch branchService;

    @Autowired
    Environment environment;

    @Autowired
    AuthenticationServiceImpl authenticationServiceImpl;
    
//    @Wire
//    Tree treeMenu;
    @Wire
	Navbar sidebar;

    @Wire
    Div divContent;
    @Wire
    Grid westInfoGrid;
    @Wire
    Html htmlRunText;
    @Wire
    Label lblForm;
    @Wire
    Label lblUser;
    @Wire
    Label lblDate;
    @Wire
    Label lblTime;
    
    @Wire
    Label lblMenu;
    
    @Wire
    Label lblWewenang;

    @Wire
    Combobox cmbRole;

    @Wire
    West westMenu;
    
    @Wire
    Combobox cmbCabang;
    
    @Wire Html lblRunTxt;
    
    @Wire
    Center centerLayout;
    
    @Wire
    Include incContent;
    
    @Wire
    Vlayout logoBankEdit;
    
    boolean onLoad = false;
    
    private ListModelList<Branch> branchesModel;
    private ListModelList<GrantedAuthority> roleModels;
    
    @Autowired MasterServices masterService;
    @Autowired JdbcTemplate jt;
    
    BarChartDemo1 engine;

    String id="";
	DTOMap mapUser=(DTOMap)GlobalVariable.getInstance().get("USER_MASTER");
	
	DTOMap mapSys=(DTOMap)GlobalVariable.getInstance().get("cfgsys");
	
	SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
	
	public HashMap<String, Navitem> mapNavitem;
	public HashMap<String, Nav> mapNav;
	
    /*public void doLineChart(Charts crt,Integer RATING){
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
    }*/
    
    @Override
    public void doAfterCompose(Borderlayout comp) throws Exception {
        super.doAfterCompose(comp);
		
        westInfoGrid.setVisible(Boolean.parseBoolean(environment.getProperty("application.show-left-info-panel", "true")));
        htmlRunText.setVisible(Boolean.parseBoolean(environment.getProperty("application.show-running-text", "true")));
        SimpleDateFormat sf = new SimpleDateFormat("hh:mm:ss");
        Clients.evalJavaScript("startClock('"+sf.format(new Date(System.currentTimeMillis()))+"')");

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String pass = Cryptograph.MD5("password");
        String userId = Cryptograph.MD5(userDetails.getUserId());
        if(userDetails.getPassword().equals(pass) || userId.equals(userDetails.getPassword())){
		        Window x = (Window) Executions.createComponents("/page/dialog/WndDialogGantiPassword.zul",null, null);
				x.doModal();
        }
        
        lblUser.setValue(userDetails.getUserId()+"-"+userDetails.getUserName());
        lblDate.setValue(new SimpleDateFormat("dd/MM/yyyy").format(mapSys.getDate("OPEN_DATE")));
        
        List<DTOMap> listMap = getRunningText();
        StringBuffer sb = new StringBuffer();
        
        int i=1;
        for (DTOMap dtoMap : listMap) {
			sb.append(dtoMap.getString("URAIAN"));
			
			if(i<listMap.size()){
				sb.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;***&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
				i++;
			}
		}
        htmlRunText.setContent("<marquee scrollamount=\"2\" scrolldelay=\"1\" onMouseOver=\"this.stop()\" onMouseOut=\"this.start()\" class=\"running-text\">"+sb.toString()+"</marquee>");
        
//        if(menuLoader!=null){
//            ((AbstractMenuLoader) menuLoader).setContent(divContent);
//            menuLoader.doLoadByRole(treeMenu,userDetails.getActiveRole());
//        }

        roleModels = new ListModelList<>(new ArrayList<>(SecurityContextHolder.getContext().getAuthentication().getAuthorities()));
        for (GrantedAuthority roleModel : roleModels) {
            if(roleModel.getAuthority().equals(userDetails.getActiveRole())){
                roleModels.setSelection(Collections.singleton(roleModel));
                break;
            }
        }
        cmbRole.setModel(roleModels);

        if(userDetails.isAdmin()){
            cmbCabang.setDisabled(false);
        }

        if(branchService != null){
            branchesModel = new ListModelList<>(branchService.getListBranch());
            cmbCabang.setModel(branchesModel);

            for (Branch branch : branchesModel) {
                if(userDetails.getBranchId().equals(branch.getCode())){
                    branchesModel.setSelection(Collections.singleton(branch));
                    break;
                }
            }
        }
        
        divContent.addEventListener(Events.ON_MOUSE_OVER, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
            	westMenu.setOpen(false);
            }
        });
        
        
        
//        treeMenu.addEventListener(Events.ON_DOUBLE_CLICK, new EventListener<Event>() {
//            @Override
//            public void onEvent(Event event) throws Exception {
//                Tree tree = (Tree) event.getTarget();
//                if(tree.getItemCount() <= 0) return;
//                if(tree.getSelectedCount() <= 0) return;
//
//                Treeitem selectedItem = tree.getSelectedItem();
//                MenuTreeNode node = selectedItem.getValue();
//                MenuTreeItem item = node.getData();
//                
//                if(item.isProgram()){
//                	lblMenu.setValue(item.getMenuName());
//                    lblForm.setValue(item.getUrl());
////                  westMenu.setOpen(false);
////                   	menuLoader.openByCode(item.getId());
//                    menuLoader.openContent(item.getUrl(), Collections.<String,Object>emptyMap());
////                    final Component component = divContent.getFirstChild();
////                    enterAsTab(component);
//                }else{
//                	if(selectedItem.isOpen()){
//                		selectedItem.setOpen(false);
//                	}else{
//                		selectedItem.setOpen(true);
//                	}
//                }
//            }
//        });
        doLoadMenu();
        doSeacrhImage();
        
    }

    @SuppressWarnings("rawtypes")
	private void doLoadMenu() {
    	sidebar.getChildren().clear();
		mapNav = new HashMap<String, Nav>();
		mapNavitem = new HashMap<String, Navitem>();
		EventListener selectItem = new EventListener() {
			public void onEvent(Event e) throws Exception {
				onSelectItem((Navitem) e.getTarget());
				getMenu(Navitem);
			}
		};
		
//		List<MenuTreeItem> items = (List<MenuTreeItem>) authenticationServiceImpl.getMenuItemsByRole(mapUser.getString("ROLEID"));
		List<MenuTreeItem> items = (List<MenuTreeItem>) authenticationServiceImpl.getMenuItemsByUser(mapUser.getString("USERID"), 
				mapUser.getString("ROLEID"));

		for (MenuTreeItem item : items) {
			if (item.getUrl() != null) {
				Navitem navItem = new Navitem();
				navItem.setAttribute("id", item.getId());
				navItem.setAttribute("name", item.getMenuName());
				navItem.setAttribute("parent", item.getParentId());
				navItem.setAttribute("url", item.getUrl());
				navItem.setAttribute("isProgram", item.isProgram());
				navItem.setLabel(item.getMenuName());
				navItem.addEventListener(Events.ON_DOUBLE_CLICK, selectItem);
				if (item.getId().equals("000000")) {
					navItem.setImage("/asset/image/home.png");
				}else{
					navItem.setImage("/asset/image/leaf.png");
				}
				mapNavitem.put(item.getId(), navItem);
			} else {
				Nav nav = new Nav();
				nav.setAttribute("id", item.getId());
				nav.setAttribute("name", item.getMenuName());
				nav.setAttribute("parent", item.getParentId());
				nav.setAttribute("url", item.getUrl());
				nav.setAttribute("isProgram", null);
				nav.setImage("/asset/image/folder.png");
				nav.setLabel(item.getMenuName());
				mapNav.put(item.getId(), nav);
			}
		}

		for (MenuTreeItem item : items) {
			if (item.getUrl() != null) {
				Navitem navItem = mapNavitem.get(item.getId());
				if (item.getParentId() != null) {
					Nav navParent = mapNav.get(item.getParentId());
					if (navParent == null) {
						continue;
					} else {
						navParent.appendChild(navItem);
					}
				} else {
					sidebar.appendChild(navItem);
				}
			} else {
				Nav nav = mapNav.get(item.getId());
				if (item.getParentId() != null) {
					Nav navParent = mapNav.get(item.getParentId());
					if (navParent == null) {
						continue;
					} else {
						navParent.appendChild(nav);
					}
				} else {
					sidebar.appendChild(nav);
				}
			}
		}
	}
    
    public void onSelectItem(Navitem item) {
		if (item != null) {
			try {
				Include include = (Include) incContent;
				if (item.getAttribute("isProgram")!=null) {
					GlobalVariable.getInstance().put("menuid", item.getAttribute("id"));
					GlobalVariable.getInstance().put("nmmenu", item.getAttribute("name"));
					include.setSrc((String) item.getAttribute("url"));
					include.invalidate();
					lblMenu.setValue((String) item.getAttribute("name"));
					lblForm.setValue((String) item.getAttribute("url"));
				}
			} catch (org.zkoss.zk.ui.UiException e) {
				DialogUtil.showDialog("Maaf anda tidak diizinkan mengakses menu ini.", "PERINGATAN");
			}
		}
	}

    /*@Listen(Events.ON_CHANGE+" = #cmbRole")
    public void changeMenuRole(Event event){
        ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).setActiveRole(cmbRole.getSelectedItem().<String>getValue());
        menuLoader.doLoadByRole(treeMenu, cmbRole.getSelectedItem().<String>getValue());
    }*/

    @Listen(Events.ON_CHANGE+" = #cmbCabang")
    public void onChangeCmbBranch(Event event){
        Comboitem comboitem = cmbCabang.getSelectedItem();
        if(comboitem!=null){
            ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).setBranchId(((String) comboitem.getValue()));
        }
    }
    
    public List<DTOMap> getRunningText(){
    	List<DTOMap> listData = new ArrayList<>();
    	try {
    		listData = masterService.getDataMaster("SELECT * FROM CFG_PENGUMUMAN WHERE STS_PUBLISH='1' ", null);		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listData;
    	
    }
    public List<DTOMap> getMenu(Navitem item){
    	List<DTOMap> listData = new ArrayList<>();
    	try {
//    		listData = ;		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listData;
    }
    private void doSeacrhImage(){
    	DTOMap datas = new DTOMap();
		datas = masterService.getMapMaster("SELECT LOGO_BANK FROM CFG_SYS", new Object[]{});
		doLoadImage(datas);
    }
    
    private void doLoadImage(DTOMap data){
    	if (data != null){
    	String imageString = (String) data.get("LOGO_BANK");
		doDecodeFromString(imageString);
    	}
    	onLoad = true;
		
    }
    
    
    private void doDecodeFromString(String imageString) {
		byte[] decodedBytes = Base64.decode(imageString);
		org.zkoss.zul.Image image = new org.zkoss.zul.Image();			
		try {
			image.setContent(new AImage("Gambar", decodedBytes));
			  image.setParent(logoBankEdit);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
