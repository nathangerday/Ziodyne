package components;

import connectors.LampConnector;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import interfaces.LampControllerI;
import ports.LampControllerOutboundPort;

public class Controller extends AbstractComponent{

    private LampControllerOutboundPort p;
    private String uri;
    private String uri_lamp;

    protected Controller(String uri, String uri_lamp) throws Exception{
        super(uri, 1, 0);
        this.uri = uri;
        this.uri_lamp = uri_lamp;

        this.addRequiredInterface(LampControllerI.class);
        p = new LampControllerOutboundPort(uri,this);
        p.publishPort();
    }

    @Override
    public void start() throws ComponentStartException{
        super.start();
        try {
            this.doPortConnection(uri, uri_lamp, LampConnector.class.getCanonicalName());
        }catch(Exception e) {
            throw new ComponentStartException(e);
        }
    }

    @Override
    public void execute() throws Exception{
        super.execute();
        this.p.switchLamp();
        System.out.println("Lampe etat : " + p.isLampOn());
        this.p.switchLamp();
        System.out.println("Lampe etat : " + p.isLampOn());
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
    public void finalise() throws Exception{
        this.doPortDisconnection(p.getPortURI());
        super.finalise();
    }
}
