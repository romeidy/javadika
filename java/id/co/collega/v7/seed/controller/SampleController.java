package id.co.collega.v7.seed.controller;

import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

@Component
@Scope("execution")
public class SampleController extends SelectorComposer<Window>{

    @Wire
    Textbox txtMessage;

    @Listen("onClick = button#btnAlert")
    public void alert(){
        Messagebox.show("Hello "+txtMessage.getValue());
    }
}
