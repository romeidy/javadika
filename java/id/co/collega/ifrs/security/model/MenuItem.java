package id.co.collega.ifrs.security.model;

import id.co.collega.v7.ui.component.MenuTreeItem;

public class MenuItem implements MenuTreeItem {
	String id;
	String menuName;
    boolean program;
    String url;
    boolean mainMenu;
    String parentId;
    String formId;
    
    public MenuItem(String id, String menuName, String url, boolean program, String parentId, String formId) {
        this.id = id;
        this.menuName = menuName;
        this.url = url;
        this.program = program;
        this.parentId = parentId;
        this.formId = formId;
    }
    
    public boolean isMainMenu() {
    	return mainMenu;
    }
    
    public void setMainMenu(boolean mainMenu) {
    	this.mainMenu = mainMenu;
    }
    
    public void setId(String id) {
    	this.id = id;
    }
    
    public void setMenuName(String menuName) {
    	this.menuName = menuName;
    }
    
    public void setProgram(boolean program) {
    	this.program = program;
    }
    
    public void setUrl(String url) {
    	this.url = url;
    }
    
    public void setParentId(String parentId) {
    	this.parentId = parentId;
    }
    
	public String getId() {
		return id;
	}

	public String getMenuName() {
		return menuName;
	}

	public String getParentId() {
		return parentId;
	}

	public String getUrl() {
		return url;
	}

	public boolean isProgram() {
		return program;
	}

	public String getFormId() {
		return formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}
}
