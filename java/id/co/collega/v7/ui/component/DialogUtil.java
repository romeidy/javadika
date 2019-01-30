package id.co.collega.v7.ui.component;

import id.co.collega.v7.ui.component.composite.CustomMessageDialog;
import id.co.collega.v7.ui.component.composite.PopupController;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import java.util.Map;

public class DialogUtil {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(DialogUtil.class);

    interface Dialog{
        void show();
    }
    static class ConfirmationDialogBuilder implements Dialog{
        private String question;

        private EventListener<Event> onOk;

        private EventListener<Event> onCancel;

        ConfirmationDialogBuilder(String question){
            this.question = question;
        }

        public void onOk(EventListener<Event> eventListener){
            this.onOk = eventListener;
        }

        public void onCancel(EventListener<Event> eventListener){
            this.onCancel = eventListener;
        }

        public void show(){
            Messagebox.show(this.question, "Confirmation dialog", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, new EventListener<Event>() {
                @Override
                public void onEvent(Event event) throws Exception {
                    if (event.getName().equals(Messagebox.ON_OK)) {
                        if (onOk != null) onOk.onEvent(event);
                    } else if (event.getName().equals(Messagebox.ON_CANCEL)) {
                        if (onCancel != null) onCancel.onEvent(event);
                    }
                }
            });

        }
    }

    static class CommonDialogBuilder implements Dialog{
        private String message;
        private String title;
        private String icon;

        private EventListener<Event> onClose;

        private void onClose(EventListener<Event> eventListener){
            this.onClose = eventListener;
        }
        CommonDialogBuilder(String message,String title){
            this.message = message;
            this.title = title;
        }

        @Override
        public void show() {
            Messagebox.show(message,title,Messagebox.OK,icon!=null && !"".equals(icon)?icon:Messagebox.INFORMATION,onClose);
        }
    }


    public static ConfirmationDialogBuilder createConfirmationDialog(String question){
        return new ConfirmationDialogBuilder(question);
    }

    public static void showConfirmationDialog(String question,EventListener<Event> onOk,EventListener<Event> onCancel){
        ConfirmationDialogBuilder dialogBuilder = new ConfirmationDialogBuilder(question);
        dialogBuilder.onCancel = onCancel;
        dialogBuilder.onOk = onOk;
        dialogBuilder.show();
    }

    public static Component createAndShowCustomDialog(String message,String title,Component parent,EventListener<Event> onCloseEvent){
        Window window = (Window) Executions.createComponents("~./ui/component/customDialog.zul",parent,null);
        CustomMessageDialog customMessageDialog = (CustomMessageDialog) Components.getComposer(window);
        customMessageDialog.getBtnClose().setLabel("Cancel");
        customMessageDialog.getMessage().setValue(message);
        window.setTitle(title);
        if(onCloseEvent!=null) customMessageDialog.getBtnClose().addEventListener(1, Events.ON_CLICK,onCloseEvent);
        return window;
    }

    public enum PopupMode{CLOSE_ONLY, OK_CLOSE}
    public static void showPopupDialogCloseOnly(String zulUrl,String title,Component parent, EventListener<Event> onClose,Map<String,Object> args){
        showPopupDialog(zulUrl,title,parent, PopupMode.CLOSE_ONLY,null,onClose,args);
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
            logger.error("You should use PopupController composer for pop Dialog",e);
            throw e;
        }
    }

    public static CommonDialogBuilder createDialog(String message,String title){
        return new CommonDialogBuilder(message,title);
    }

    public static void showDialog(String message,String title){
        CommonDialogBuilder dialogBuilder = new CommonDialogBuilder(message,title);
        dialogBuilder.show();
    }

    public static void showDialog(String message,String title, EventListener<Event> onOk){
        CommonDialogBuilder dialogBuilder = new CommonDialogBuilder(message,title);
        dialogBuilder.onClose = onOk;
        dialogBuilder.show();
    }


    public static void showLoading(){
        Clients.showBusy("Loading...");
    }

    public static void clearLoading(){
        Clients.clearBusy();
    }

}
