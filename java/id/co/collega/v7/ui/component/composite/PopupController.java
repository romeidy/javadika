package id.co.collega.v7.ui.component.composite;


import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.Disable;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Separator;
import org.zkoss.zul.Window;

import java.util.LinkedList;
import java.util.List;


public abstract class PopupController<T extends Component> extends SelectorComposer<T> implements EventListener<Event>{

    private Button btnOk;
    private Button btnClose;
    private Window container;
    private EventListener<Event> eventListener;
    private Component parent;


    public void setEventListener(EventListener<Event> eventListener) {
        this.eventListener = eventListener;
    }

    protected void fireEventPopupButton() throws Exception {
        if(eventListener!=null)eventListener.onEvent(new Event("onClick",getBtnOk(),returnValue()));
        getContainer().detach();
    }

    @Override
    public void onEvent(Event event) throws Exception {
        if (onBtnClicked(((Button)event.getTarget()).getLabel())) {
            if(eventListener!=null)eventListener.onEvent(new Event(event.getName(),event.getTarget(),returnValue()));
            getContainer().detach();
        }
    }

    protected boolean onBtnClicked(String eventName) {
        return true;
    }

    @Override
    public void doAfterCompose(T comp) throws Exception {
        super.doAfterCompose(comp);
        parent = comp.getParent();
        comp.detach();
        getContainer().appendChild(comp);
        comp.setParent(getContainer());
        Separator separator = new Separator();
        separator.setSpacing("15px");
        getContainer().appendChild(separator);
        attachButton();

        enableEnterAsTab();
    }

    private void enableEnterAsTab() {
        Iterable<Component> components = container.queryAll(".input");
        final List<Component> componentList = new LinkedList<>();
        for (final Component component : components) {
            componentList.add(component);
            component.addEventListener(Events.ON_OK, new EventListener<Event>() {
                @Override
                public void onEvent(Event event) throws Exception {
                    int index = componentList.indexOf(component);
                    try {
                        if (++index < componentList.size()) {
                            Component nextComponent = componentList.get(index);
                            if (nextComponent instanceof Disable) {
                                while (((Disable) nextComponent).isDisabled()) {
                                    nextComponent = componentList.get(++index);
                                }
                            }
                            if (nextComponent instanceof HtmlBasedComponent) {
                                ((HtmlBasedComponent) nextComponent).focus();

                            }
                        }
                    } catch (IndexOutOfBoundsException ignored) {
                    }
                }
            });
        }
        // TODO: last component set focus to OK button
    }

    private void attachButton() {
        Hbox hbox  = new Hbox();
        hbox.setStyle("margin-left:auto; margin-right:auto");
        hbox.appendChild(getBtnOk());
        hbox.appendChild(getBtnClose());
        getContainer().appendChild(hbox);

        getBtnOk().addEventListener(Events.ON_CLICK, this);
        getBtnClose().addEventListener(Events.ON_CLICK,this);
    }

    public Window getContainer() {
        if(container == null){
            container = buildContainer();
        }
        return container;
    }

    public abstract Object returnValue();

    protected Window buildContainer(){
        Window window = new Window();

        parent.appendChild(window);
        window.setMode(Window.Mode.MODAL);
        window.setClosable(false);
        return window;
    }

    public Button getBtnOk() {
        if(btnOk == null){
            btnOk = new Button("OK");
            btnOk.setSclass("input");
        }
        return btnOk;
    }

    public Button getBtnClose() {
        if(btnClose == null){
            btnClose = new Button("Close");
        }
        return btnClose;
    }
}
