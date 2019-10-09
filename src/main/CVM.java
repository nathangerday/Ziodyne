package main;

import components.Controller;
import components.Lamp;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;

public class CVM extends AbstractCVM{
    public CVM() throws Exception{
        super();
    }

    @Override
    public void deploy() throws Exception{
        AbstractComponent.createComponent(Controller.class.getCanonicalName(),
                new Object[] {Constants.URI_OUTBOUND_LAMP, Constants.URI_INBOUND_LAMP});
        AbstractComponent.createComponent(Lamp.class.getCanonicalName(),
                new Object[] {Constants.URI_INBOUND_LAMP});
        super.deploy();
    }

    @Override
    public void finalise() throws Exception{
        super.finalise();
    }

    public void main(String[] args) throws Exception{
        CVM a = new CVM();
        a.startStandardLifeCycle(20000L);
        Thread.sleep(5000L);
        System.exit(1);
    }
}
