package id.co.collega.v7.ui.component;


import org.zkoss.zul.Tree;

import java.util.Collection;
import java.util.Map;

public interface MenuLoader {

    void loadMenu(Tree tree);

    Collection<MenuTreeItem> loadByRole(String roleCode);

    void doLoadByRole(Tree tree,String roleCode);

    void setMenuTreeItems(Collection<MenuTreeItem> menuTreeItems);

    void openContent(String url,Map<String,Object> parameters);

    void openByCode(String code);
}