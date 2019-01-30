package id.co.collega.ifrs.enumz;

public enum EnumJenisNsb implements IEnum {
	SEMUA("SEMUA"),
	PERORANGAN("PERORANGAN"), 
	KORPORASI("NON PERORANGAN"); 
	
	private String name;
	
	private EnumJenisNsb(String name) {
		this.name = name;
	}
	
	public String[] getItems() {
		String[] items = new String[EnumJenisNsb.values().length];
		for (int i = 0; i < items.length; i++) {
			items[i] = EnumJenisNsb.values()[i].getString();
		}
		return items;
	}

	public String getString() {
		return name;
	}

}
