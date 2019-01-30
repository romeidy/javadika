package id.co.collega.v7.seed.config;

import id.co.collega.v7.rte.ui.services.RteUiService;
import id.co.collega.v7.ui.component.AbstractMenuLoader;
import id.co.collega.v7.ui.component.MenuTreeItem;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;

public class ProgramBasedMenuLoader extends AbstractMenuLoader{

    @Autowired
    RteUiService rteUiService;

    @Override
    @Deprecated
    protected Collection<MenuTreeItem> initializeMenuItems() {
        return null;
    }

    @Override
    public Collection<MenuTreeItem> loadByRole(String s) {
        return null;
    }
}
