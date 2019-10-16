package components;

import connectors.FridgeConnector;
import connectors.LampConnector;
import connectors.WindTurbineConnector;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import interfaces.FridgeControllerI;
import interfaces.LampControllerI;
import interfaces.WindTurbineControllerI;
import ports.FridgeControllerOutboundPort;
import ports.LampControllerOutboundPort;
import ports.WindTurbineControllerOutboundPort;

//-----------------------------------------------------------------------------
/**
 * The class <code>Controller</code> implements a component that can control a Lamp
 * from Lamp component.
 *
 * <p><strong>Description</strong></p>
 * 
 * The component declares its required service through the required interface
 * <code>LampI</code> which has a <code>isLampOn</code> requested service
 * signature.  The internal method <code>switchLamp</code> implements the
 * main task of the component, as it calls the provider component through the
 * outbound port implementing the connection.  It switches the button On and Off. The <code>start</code> method initiates
 * this process.
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 * <p>Created on : 2018-10-18</p>
 *
 */

public class Controller extends AbstractComponent{
    //equipements ports
    private LampControllerOutboundPort lampOutboundPort;
    private FridgeControllerOutboundPort fridgeOutboundPort;
    private WindTurbineControllerOutboundPort windTurbineOutboundPort;

    //equipments ports uri
    private String lampInboundPortURI;
    private String fridgeInboundPortURI;
    private String windTurbineInboundPortURI;

    protected Controller(
            String uri,
            String lampOutboundPortURI,
            String lampInboundPortURI,
            String fridgeOutboundPortURI,
            String fridgeInboundPortURI,
            String windTurbineOutboundPortURI,
            String windTurbineInboundPortURI) throws Exception{
        super(uri, 1, 0);
        this.lampInboundPortURI = lampInboundPortURI;
        this.fridgeInboundPortURI = fridgeInboundPortURI;
        this.windTurbineInboundPortURI = windTurbineInboundPortURI;

        this.addRequiredInterface(LampControllerI.class);
        this.addRequiredInterface(FridgeControllerI.class);
        this.addRequiredInterface(WindTurbineControllerI.class);
        this.lampOutboundPort = new LampControllerOutboundPort(lampOutboundPortURI,this);
        this.lampOutboundPort.publishPort();
        this.fridgeOutboundPort = new FridgeControllerOutboundPort(fridgeOutboundPortURI,this);
        this.fridgeOutboundPort.publishPort();
        this.windTurbineOutboundPort = new WindTurbineControllerOutboundPort(windTurbineOutboundPortURI,this);
        this.windTurbineOutboundPort.publishPort();
    }


    /**
     * a component is always started by calling this method, so intercept the
     * call and make sure the task of the component is executed.
     * 
     * <p><strong>Contract</strong></p>
     * 
     * <pre>
     * pre	true				// no more preconditions.
     * post	true				// no more postconditions.
     * </pre>
     * 
     * @see fr.sorbonne_u.components.AbstractComponent#start()
     */
    @Override
    public void start() throws ComponentStartException{
        super.start();
        try {
            //connect with the lamp
            this.doPortConnection(
                    this.lampOutboundPort.getPortURI(),
                    this.lampInboundPortURI,
                    LampConnector.class.getCanonicalName());
            //connect with the fridge
            this.doPortConnection(
                    this.fridgeOutboundPort.getPortURI(),
                    this.fridgeInboundPortURI,
                    FridgeConnector.class.getCanonicalName());
            //connect with the wind turbine
            this.doPortConnection(
                    this.windTurbineOutboundPort.getPortURI(),
                    this.windTurbineInboundPortURI,
                    WindTurbineConnector.class.getCanonicalName());
        }catch(Exception e) {
            throw new ComponentStartException(e);
        }
    }

    @Override
    public void execute() throws Exception{
        super.execute();
        System.out.print("Lampe état : ");
        switch(this.lampOutboundPort.getState()) {
        case 0 : System.out.println("éteint");break;
        case 1 : System.out.println("tamisé"); break;
        case 2 : System.out.println("normal"); break;
        case 3 : System.out.println("fort"); break;
        }
        System.out.println("Fridge temp : " + this.fridgeOutboundPort.getFreezerTemp());
        System.out.println("wind speed : " + this.windTurbineOutboundPort.getWindSpeed());
    }

    @Override
    public void shutdown() throws ComponentShutdownException {
        try {
            this.lampOutboundPort.unpublishPort();
            this.fridgeOutboundPort.unpublishPort();
            this.windTurbineOutboundPort.unpublishPort();
        }catch(Exception e) {
            throw new ComponentShutdownException(e);
        }
        super.shutdown();
    }

    @Override
    public void shutdownNow() throws ComponentShutdownException {
        try {
            this.lampOutboundPort.unpublishPort();
            this.fridgeOutboundPort.unpublishPort();
            this.windTurbineOutboundPort.unpublishPort();
        }catch(Exception e) {
            throw new ComponentShutdownException(e);
        }
        super.shutdownNow();
    }

    @Override
    public void finalise() throws Exception{
        this.doPortDisconnection(this.lampOutboundPort.getPortURI());
        this.doPortDisconnection(this.fridgeOutboundPort.getPortURI());
        this.doPortDisconnection(this.windTurbineOutboundPort.getPortURI());
        super.finalise();
    }

}
