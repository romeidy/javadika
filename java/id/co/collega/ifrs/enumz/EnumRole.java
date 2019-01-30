package id.co.collega.ifrs.enumz;

public enum EnumRole implements IEnum {
	SUPERUSER("XX"), 
	ADMIN_KANTOR_PUSAT("01"), 
	SUPERVISI("02"), 
	PELAPOR1("03"),
	PELAPOR2("04");
	
	private String name;
	
	private EnumRole(String name) {
		this.name = name;
	}
	
	public String[] getItems() {
		String[] items = new String[EnumRole.values().length];
		for (int i = 0; i < items.length; i++) {
			items[i] = EnumRole.values()[i].getString();
		}
		return items;
	}

	public String getString() {
		return name;
	}

}
