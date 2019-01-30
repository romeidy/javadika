package id.co.collega.ifrs.util;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Messagebox;

public class MessageBox { 

	public static void show(String messages, String header, Integer style, String type){
		Messagebox.show(messages, header, style, type);
	}
	
	public static void showInformation(String messages){
		Messagebox.show(messages, "INFORMASI", Messagebox.OK, Messagebox.INFORMATION);
	}
	
	public static void showError(String messages){
		Messagebox.show(messages, "KESALAHAN", Messagebox.OK, Messagebox.ERROR);
	}
	
	public static boolean showConfirm(String messages){
		final boolean result[] = {true};
		Messagebox.show(messages, "KONFIRMASI", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, new EventListener<Event>() {
			
			@Override
			public void onEvent(Event e) throws Exception {
				System.out.println("event e "+e.getName()+" "+Messagebox.ON_OK+" "+Messagebox.ON_CANCEL);
				if (Messagebox.ON_OK.equals(e.getName())) {
					result[0] =  true;
					System.out.println("resT : "+result[0]);
				}else if(Messagebox.ON_CANCEL.equals(e.getName())){
					result[0] = false;
				System.out.println("resC : "+result[0]);
				}
			}
		});
		System.out.println("res : "+result[0]);
		return result[0];
	}
	
}
