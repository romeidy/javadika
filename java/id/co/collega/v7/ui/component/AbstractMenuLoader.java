package id.co.collega.v7.ui.component;


import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.InitializingBean;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.Label;
import org.zkoss.zul.Tree;

import java.util.*;

public abstract class AbstractMenuLoader implements InitializingBean,MenuLoader{

    private Component content;

    public void setContent(Component content) {
        this.content = content;
    }

    public Component getContent() {
        return content;
    }

    /**
     * @see AbstractMenuLoader#loadByRole(String)
     * @return
     */
    @Deprecated
    protected abstract Collection<MenuTreeItem> initializeMenuItems();

    public void setMenuTreeItems(Collection<MenuTreeItem> menuTreeItems) {
        this.menuTreeItems = menuTreeItems;
    }

    protected Collection<MenuTreeItem> menuTreeItems;

    protected MenuTreeNode buildRootMenu(){
        MenuTreeNode root = new MenuTreeNode(null, new MenuTreeNodeCollection<MenuTreeItem>());
        List<MenuTreeItem> treeItems = new LinkedList<>(menuTreeItems);
        for (MenuTreeItem treeItem : treeItems) {
            if(StringUtils.isBlank(treeItem.getParentId())){
                root.add(buildMenu(treeItem,treeItems));
            }
        }
        return root;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        menuTreeItems = initializeMenuItems();
        Validate.noNullElements(menuTreeItems, "Failed to initialize bean, getMenuItems() is null");
    }


    public void loadMenu(final Tree tree){
        tree.setModel(new DefaultTreeModel<>(buildRootMenu()));
    }

    public void openContent(String url,Map<String,Object> parameters){
        if(getContent()!=null){
            Component component = getContent();
            Label lblForm = (Label) component.getRoot().getFellowIfAny("lblForm");
            if(lblForm != null) lblForm.setValue(url);
            Components.removeAllChildren(component);
            Executions.createComponents(url, component, parameters);
        }
    }

    public void openByCode(String code){
        Components.removeAllChildren(getContent());
        for (MenuTreeItem menuTreeItem : menuTreeItems) {
            if(menuTreeItem.getId().equals(code)){
                if (menuTreeItem.isProgram()) {
                    Label lblForm = (Label) getContent().getRoot().getFellowIfAny("lblForm");
                    if(lblForm != null) lblForm.setValue(menuTreeItem.getUrl());
                    openContent(menuTreeItem.getUrl(),Collections.<String,Object>emptyMap());
                    break;
                }
            }
        }
    }

    public abstract Collection<MenuTreeItem> loadByRole(String roleCode);

    public void doLoadByRole(Tree tree,String roleCode) {
        this.menuTreeItems = loadByRole(roleCode);
        loadMenu(tree);
    }

    protected MenuTreeNode buildMenu(MenuTreeItem firstLevelItem, List<MenuTreeItem> childItems){
        if(firstLevelItem.isProgram()){
            return new MenuTreeNode(firstLevelItem);
        }else{
            MenuTreeNode subMenu = new MenuTreeNode(firstLevelItem,new MenuTreeNodeCollection<MenuTreeItem>());
            for (MenuTreeItem childItem : childItems) {
                if(firstLevelItem.getId().equals(childItem.getParentId())){
                    subMenu.add(buildMenu(childItem,childItems));
                }
            }
            return subMenu;
        }
    }

}