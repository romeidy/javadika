package id.co.collega.v7.seed.config;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import id.co.collega.v7.seed.config.AuthenticationService;
import id.co.collega.v7.ui.component.AbstractMenuLoader;
import id.co.collega.v7.ui.component.MenuTreeItem;

public class ApplicationMenuLoader extends AbstractMenuLoader{
	@Autowired AuthenticationService authService;
	
    @Override
    protected Collection<MenuTreeItem> initializeMenuItems() {
        return authService.getMenuItems();
    }

    @Override
    public Collection<MenuTreeItem> loadByRole(String s) {
        return authService.getMenuItemsByRole(s);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
    }
}
