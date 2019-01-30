package id.co.collega.ifrs.enumz;

public enum EnumStatistik implements IEnum {
	TRANSAKSI_DEBET("TRANSAKSI DEBET"), 
	TRANSAKSI_KREDIT("TRANSAKSI KREDIT"), 
	TARIK_TUNAI("TARIK TUNAI"),
	SETOR_TUNAI("SETOR TUNAI"),
	SALDO_PORTOFOLIO("SALDO/PORTOFOLIO");
	
	private String name;
	
	private EnumStatistik(String name) {
		this.name = name;
	}
	
	public String[] getItems() {
		String[] items = new String[EnumStatistik.values().length];
		for (int i = 0; i < items.length; i++) {
			items[i] = EnumStatistik.values()[i].getString();
		}
		return items;
	}

	public String getString() {
		return name;
	}

}
