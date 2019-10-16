package components;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.PostconditionException;
import fr.sorbonne_u.components.exceptions.PreconditionException;
import interfaces.FridgeI;
import ports.FridgeInboundPort;

public class Fridge extends AbstractComponent implements FridgeI{
    protected boolean fridgeOn;
    protected boolean freezerOn;
    protected float fridgeTemp;
    protected float freezerTemp;

    protected String uri;
    protected FridgeInboundPort p;

    protected Fridge(String uri) throws Exception {
        super(uri, 1, 0);
        assert uri != null :  new PreconditionException("uri can't be null!") ;
        this.uri = uri;
        this.fridgeOn = true;
        this.freezerOn = true;
        this.fridgeTemp = 5;
        this.freezerTemp = -15;

        this.addOfferedInterface(FridgeI.class);
        p = new FridgeInboundPort(uri, this);
        p.publishPort();

        assert this.uri.equals(uri) :
            new PostconditionException("The URI prefix has not been initialised!");
        assert this.fridgeOn == true :
            new PostconditionException("The fridge's state has not been initialised correctly !");
        assert this.freezerOn == true :
            new PostconditionException("The freezer's state has not been initialised correctly !");
        assert this.isPortExisting(p.getPortURI()):
            new PostconditionException("The component must have a "
                    + "port with URI " + p.getPortURI()) ;
        assert this.findPortFromURI(p.getPortURI()).
        getImplementedInterface().equals(FridgeI.class) :
            new PostconditionException("The component must have a "
                    + "port with implemented interface FridgeI") ;

        assert  this.findPortFromURI(p.getPortURI()).isPublished() :
            new PostconditionException("The component must have a "
                    + "port published with URI " + p.getPortURI()) ;
    }

    @Override
    public void shutdown() throws ComponentShutdownException {
        try {
            p.unpublishPort();
        }catch(Exception e) {
            throw new ComponentShutdownException(e);
        }
        super.shutdown();
    }

    @Override
    public void shutdownNow() throws ComponentShutdownException {
        try {
            p.unpublishPort();
        }catch(Exception e) {
            throw new ComponentShutdownException(e);
        }
        super.shutdownNow();
    }

    @Override
    public void switchFridge() {
        this.fridgeOn = !this.fridgeOn;
    }

    @Override
    public void switchFreezer() {
        this.freezerOn = !this.freezerOn;
    }

    @Override
    public boolean isFridgeOn(){
        return this.fridgeOn;
    }

    @Override
    public boolean isFreezerOn(){
        return this.freezerOn;
    }

    @Override
    public float getFridgeTemp(){
        return this.fridgeTemp;
    }

    @Override
    public float getFreezerTemp(){
        return this.freezerTemp;
    }

    @Override
    public void setFridgeTemp(float t){
        this.fridgeTemp = t;
    }

    @Override
    public void setFreezerTemp(float t){
        this.freezerTemp = t;
    }
}