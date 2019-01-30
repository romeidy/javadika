package id.co.collega.ifrs.master;

import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;

import id.co.collega.v7.ui.component.DialogUtil;
import id.co.collega.v7.ui.component.DialogUtil.PopupMode;
import id.co.collega.v7.ui.component.composite.PopupController;

public class DialogUtilModify extends DialogUtil {
	
	public static void showPopupDialogCloseOnly(String zulUrl,String title,Component parent, EventListener<Event> onClose,Map<String,Object> args){
        showPopupDialog(zulUrl,title,parent, null,null,onClose,args);
    }
	public static void showPopupDialogCloseOnly(String zulUrl,String title,Component parent,Map<String,Object> args){
        showPopupDialog(zulUrl,title,parent, null,null,null,args);
    }
	
	public static void showPopupDialog(String zulUrl,String title, final Component parent,PopupMode popupMode,final EventListener<Event> onOk, final EventListener<Event> onClose,Map<String,Object> args){
        try {
            final Component popupComponent  = Executions.createComponents(zulUrl,parent,args);
            final PopupController popupController = (PopupController<?>) Components.getComposer(popupComponent);
            if(popupController == null){
                throw new NullPointerException("Controller for popup "+zulUrl+" not found, apply composer to use popup");
            }
            if(title != null){
                popupController.getContainer().setTitle(title);
            }else{
                popupController.getContainer().setTitle("Dialog");
            }
            if(PopupMode.CLOSE_ONLY == popupMode){
                popupController.getBtnOk().setVisible(false);
            }else if (PopupMode.OK_CLOSE == popupMode){
                popupController.getBtnClose().setLabel("Cancel");
            }
            popupController.setEventListener(new EventListener<Event>() {
                @Override
                public void onEvent(Event event) throws Exception {
                    if(event.getTarget().equals(popupController.getBtnOk())){
                        if(onOk!=null)onOk.onEvent(event);
                    }else if(event.getTarget().equals(popupController.getBtnClose())){
                        if(onClose !=null)onClose.onEvent(event);
                    }
                    parent.removeChild(popupComponent);
                }
            });
        }catch (ClassCastException e){
            //logger.error("You should use PopupController composer for pop Dialog",e);
            throw e;
        }
    }

}
