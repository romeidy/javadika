package id.co.collega.ifrs.util;

import java.math.BigDecimal;
import java.util.List;

import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.impl.InputElement;

public class ComponentUtil {
	public static void clear(Component comp){
		clear(comp.getChildren());
	}
	
	private static void clear(List<Component> child) {
		for (Object o : child) {
			if (o instanceof AbstractComponent) {
				if (o instanceof Combobox) {
					((Combobox) o).setSelectedIndex(-1);
					((Combobox) o).setText("");
				} else if (o instanceof Decimalbox) {
					((Decimalbox) o).setValue(BigDecimal.ZERO);
				} else if (o instanceof InputElement) {
					((InputElement) o).setRawValue(null);
				} else if (((AbstractComponent) o).getChildren().size() > 0) {
					clear(((AbstractComponent) o).getChildren());
				}
			}
		}
	}
	
	public static Object getValue(Component cmp){
		Object retVal = null;
		if (cmp instanceof Combobox){ 
			if (((Combobox)cmp).getSelectedIndex()>-1)
				retVal = ((Combobox)cmp).getSelectedItem().getValue();
		} else if (cmp instanceof Radiogroup){
			if (((Radiogroup)cmp).getSelectedIndex()>-1)
				retVal = Integer.valueOf((String) ((Radiogroup)cmp).getSelectedItem().getValue());
		} else if(cmp instanceof Checkbox){
			if (((Checkbox)cmp).isChecked())retVal=1;
			else retVal=0;
		} else {
			try {
				retVal=(Object)cmp.getClass().getMethod("getValue").invoke(cmp);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		
		if (retVal instanceof String && retVal!=null && "".equals(((String) retVal).trim()))
			return null;
		else
			return retVal;
	}
	
	public static void setValue(Component cmp, Object value){
		if (cmp instanceof Combobox){
			if (value!=null) value = String.valueOf(value).trim();
			Combobox cmb = (Combobox)cmp;
			List<Comboitem> cmbItems = cmb.getItems();
			for (Comboitem cmbItem : cmbItems) {
				if (String.valueOf(cmbItem.getValue()).equals(value)){
					cmb.setSelectedItem(cmbItem);
					return;
				}
			}
			cmb.setSelectedIndex(-1);
		} else if (cmp instanceof Radiogroup){
			Radiogroup radGroup = (Radiogroup)cmp;
			List<Radio> radio = radGroup.getItems();
			for (Radio cmbItem : radio) {
				if (cmbItem.getValue().equals(value+"")){
					radGroup.setSelectedItem(cmbItem);
					return;
				}
			}
		} else if(cmp instanceof Checkbox){
			if (value == null || value.equals(0)) ((Checkbox)cmp).setChecked(false);
			else ((Checkbox)cmp).setChecked(true);
		}else if (cmp instanceof Decimalbox){
			((Decimalbox)cmp).setFormat("#,###.00");
			((Decimalbox)cmp).setValue(BigDecimal.ZERO);
			((Decimalbox)cmp).setConstraint("no negative: nilai tidak boleh negatif.");
		}else if (cmp instanceof InputElement){
			((InputElement)cmp).setRawValue(value);
		} else {
			try {
				if (value == null) return;
				else cmp.getClass().getMethod("setValue",value.getClass()).invoke(cmp,value);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
}
