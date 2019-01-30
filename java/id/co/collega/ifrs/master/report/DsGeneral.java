package id.co.collega.ifrs.master.report;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

import org.apache.commons.beanutils.PropertyUtils;

import com.jet.gand.utils.GetMoneyServer;

import id.co.collega.ifrs.common.DTOMap;

//wonx yukz
public class DsGeneral implements JRDataSource {
	private List<Object> list;
    private int index = -1;
	public DsGeneral(List<Object> ls){
		super();
		this.list=ls;
	}
	
	@Override
	public Object getFieldValue(JRField field) throws JRException {
		System.out.println("MASUK DS GENERAL");
		 String fieldName = field.getName();
		 Object o = list.get(index);
		 Field[] declaredFields = o.getClass().getDeclaredFields();
		 for (Field field2 : declaredFields) {
			 System.out.println("FIELD DS GENERAL: "+fieldName);
			 System.out.println("FIELD2 DS GENERAL: "+field2.getName());
			if(field2.getName().equals(fieldName) || field2.getName()==fieldName){
				try {
					Object obj = PropertyUtils.getProperty(o, field2.getName());
					if(obj==null){
						return "";
					}
					if(PropertyUtils.getPropertyType(o, field2.getName()).equals(BigDecimal.class)){
						return GetMoneyServer.setText((BigDecimal)obj);
					}else if(PropertyUtils.getPropertyType(o, field2.getName()).equals(Date.class)){
						return new SimpleDateFormat("dd-MM-yyyy").format( (Date)obj );
					}else{
						return obj;	
					}
					
					
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		 
	        return "";
	}

	@Override
	public boolean next() throws JRException {
		return ++index < list.size();
	}

}
