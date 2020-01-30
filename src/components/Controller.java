package components;

import connectors.*;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import interfaces.DishwasherControllerI;
import interfaces.FridgeControllerI;
import interfaces.LampControllerI;
import interfaces.WindTurbineControllerI;
import ports.*;

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
    private DishwasherControllerOutboundPort dishwasherOutboundPort;
    private ElectricMeterControllerOutboundPort electricMeterOutboundPort;
    private BatteryControllerOutboundPort batteryOutboundPort;

    //equipments ports uri
    private String lampInboundPortURI;
    private String fridgeInboundPortURI;
    private String windTurbineInboundPortURI;
    private String dishwasherInboundPortURI;
    private String electricMeterInboundPortURI;
    private String batteryInboundPortURI;

    protected Controller(
            String uri,
            String lampOutboundPortURI,
            String lampInboundPortURI,
            String fridgeOutboundPortURI,
            String fridgeInboundPortURI,
            String windTurbineOutboundPortURI,
            String windTurbineInboundPortURI,
            String dishwasherOutboundPortURI,
            String dishwasherInboundPortURI,
            String electricMeterOutboundPortURI,
            String electricMeterInboundPortURI,
            String batteryOutboundPortURI,
            String batteryInboundPortURI) throws Exception{
        super(uri, 1, 0);
        this.lampInboundPortURI = lampInboundPortURI;
        this.fridgeInboundPortURI = fridgeInboundPortURI;
        this.windTurbineInboundPortURI = windTurbineInboundPortURI;
        this.dishwasherInboundPortURI = dishwasherInboundPortURI;
        this.electricMeterInboundPortURI = electricMeterInboundPortURI;
        this.batteryInboundPortURI = batteryInboundPortURI;

        this.addRequiredInterface(LampControllerI.class);
        this.addRequiredInterface(FridgeControllerI.class);
        this.addRequiredInterface(WindTurbineControllerI.class);
        this.addRequiredInterface(DishwasherControllerI.class);
        this.addRequiredInterface(ElectricMeterControllerOutboundPort.class);
        this.addRequiredInterface(BatteryControllerOutboundPort.class);

        this.lampOutboundPort = new LampControllerOutboundPort(lampOutboundPortURI,this);
        this.lampOutboundPort.publishPort();
        this.fridgeOutboundPort = new FridgeControllerOutboundPort(fridgeOutboundPortURI,this);
        this.fridgeOutboundPort.publishPort();
        this.windTurbineOutboundPort = new WindTurbineControllerOutboundPort(windTurbineOutboundPortURI,this);
        this.windTurbineOutboundPort.publishPort();
        this.dishwasherOutboundPort = new DishwasherControllerOutboundPort(dishwasherOutboundPortURI, this);
        this.dishwasherOutboundPort.publishPort();
        this.electricMeterOutboundPort = new ElectricMeterControllerOutboundPort(electricMeterOutboundPortURI, this);
        this.electricMeterOutboundPort.publishPort();
        this.batteryOutboundPort = new BatteryControllerOutboundPort(batteryOutboundPortURI,this);
        this.batteryOutboundPort.publishPort();
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
            this.doPortConnection(
                    this.dishwasherOutboundPort.getPortURI(),
                    this.dishwasherInboundPortURI,
                    DishwasherConnector.class.getCanonicalName());
            this.doPortConnection(
                    this.electricMeterOutboundPort.getPortURI(),
                    this.electricMeterInboundPortURI,
                    ElectricMeterConnector.class.getCanonicalName());
            this.doPortConnection(
                    this.batteryOutboundPort.getPortURI(),
                    this.batteryInboundPortURI,
                    BatteryConnector.class.getCanonicalName());
        }catch(Exception e) {
            throw new ComponentStartException(e);
        }
    }
    
    
    public void scenario2() throws Exception {
//        if(this.windTurbineOutboundPort.getEnergyProduced() == 0) {
//            System.out.println("Windturbine not producing any energy, setting battery mode to \"producing\" ");
//            this.batteryOutboundPort.setMode(BatteryState.Producing);
//        }
//        
//        
//        if(this.electricMeterOutboundPort.getConsommation() == 0) {
//            System.out.println("No energy being consumed, switching on some devices");
//            if(!this.fridgeOutboundPort.isFridgeOn()) {
//                System.out.println("Switching fridge on");
//                this.fridgeOutboundPort.switchFridge();
//            }
//            
//            if(!this.fridgeOutboundPort.isFreezerOn()) {
//                System.out.println("Switching freezer on");
//                this.fridgeOutboundPort.switchFreezer();
//            }
//        }else if(this.electricMeterOutboundPort.getConsommation() > 50) {
//            System.out.println("Too much energy being consumed, switching off some devices");
//            if(this.fridgeOutboundPort.isFridgeOn()) {
//                System.out.println("Switching fridge off");
//                this.fridgeOutboundPort.switchFridge();
//            }
//            
//            if(this.fridgeOutboundPort.isFreezerOn()) {
//                System.out.println("Switching freezer off");
//                this.fridgeOutboundPort.switchFreezer();
//            }
//        }
//        
//        System.out.println("Is fridge on ? " + this.fridgeOutboundPort.isFridgeOn());
//        System.out.println("Is freezer on ? " + this.fridgeOutboundPort.isFreezerOn());
//        
//        
//        System.out.println("Setting the dishwasher to eco mode");
//        this.dishwasherOutboundPort.setDishwasherModeEco(true);
//        System.out.println("Is the dishwasher on ecode mode ? " + this.dishwasherOutboundPort.isDishwasherModeEco());
//        
//        
//        System.out.println("Starting the dishwasher");
//        this.dishwasherOutboundPort.startDishwasherProgram();
//        System.out.println("The time left on the dishwasher program is : " + this.dishwasherOutboundPort.getDishwasherTimeLeft());
//        
//        int lampIntensity = this.lampOutboundPort.getState();
//        System.out.println("The lamp intensity is : " + lampIntensity);
//        
//        if(lampIntensity > 5 && this.fridgeOutboundPort.isFridgeOn()) {
//            System.out.println("Switching off the fridge to have enough energy for the lamp");
//            this.fridgeOutboundPort.switchFridge();
//        }
//        
//        System.out.println("Is fridge on ? " + this.fridgeOutboundPort.isFridgeOn());
//        System.out.println("Is freezer on ? " + this.fridgeOutboundPort.isFreezerOn());
//        
    }
    
    
    
    public void scenario1() throws Exception {
//    	/*
//    	 *i want to turn off the fridge, turn it on and set the fridge temperature  
//    	 */	
//    	if(this.fridgeOutboundPort.isFridgeOn() && this.fridgeOutboundPort.isFreezerOn()) {
//    		System.out.println("Turning the fridge off");
//	    	//switch fridge compartment off
//	    	this.fridgeOutboundPort.switchFridge();
//	   
//	    	//switch freezer comparment off
//	    	this.fridgeOutboundPort.switchFreezer();
//	    	
//	    	System.out.println("Is the fridge compartment turned off ? "+this.fridgeOutboundPort.isFridgeOn());
//	    	System.out.println("Is the freezer compartment turned off ? "+this.fridgeOutboundPort.isFreezerOn());
//    	}
//    	
//    	System.out.println("Turning the fridge on");
//    	
//    	//switch fridge compartment on
//    	this.fridgeOutboundPort.switchFridge();
//    	System.out.println("Is the fridge compartment turned on ? "+this.fridgeOutboundPort.isFridgeOn());
//    	
//    	//switch freezer comparment on
//    	this.fridgeOutboundPort.switchFreezer();
//    	System.out.println("Is the freezer compartment turned off ? "+this.fridgeOutboundPort.isFreezerOn());
//    	
//    	//Check is the fridge and freezer temperature
//    	System.out.println("Fridge temperature : "+this.fridgeOutboundPort.getFridgeTemp());
//    	System.out.println("Freezer temperature : "+this.fridgeOutboundPort.getFreezerTemp());
//    	
//    	System.out.println("Setting fridge temperature");
//    	//fridge compartment is too hot
//    	this.fridgeOutboundPort.setFridgeTemp(2);
//    	
//    	//freezer compartment is not cold enough
//    	this.fridgeOutboundPort.setFreezerTemp(-20);
//    	
//    	//Check is the fridge and freezer temperature
//      	System.out.println("Fridge temperature : "+this.fridgeOutboundPort.getFridgeTemp());
//    	System.out.println("Freezer temperature : "+this.fridgeOutboundPort.getFreezerTemp());
//    	
//    	/*
//    	 * do the laundry with dishwasher in eco mode
//    	 */
//    	
//    	if(!this.dishwasherOutboundPort.isDishwasherModeEco()) {
//    		this.dishwasherOutboundPort.setDishwasherModeEco(true);
//    		System.out.println("Starting dishwasher in eco mode");
//    		this.dishwasherOutboundPort.startDishwasherProgram();
//    		
//    	}
//    	
//    	System.out.println("Time left : "+this.dishwasherOutboundPort.getDishwasherTimeLeft() );
    	
    	
    
    }

    @Override
    public void execute() throws Exception{
        super.execute();
        scenario2();
     /*   System.out.print("Lampe état : ");
        switch(this.lampOutboundPort.getState()) {
        case 0 : System.out.println("éteint");break;
        case 1 : System.out.println("tamisé"); break;
        case 2 : System.out.println("normal"); break;
        case 3 : System.out.println("fort"); break;
        }
        System.out.println("Fridge temp : " + this.fridgeOutboundPort.getFreezerTemp());
        System.out.println("wind speed : " + this.windTurbineOutboundPort.getWindSpeed());
        System.out.println("Dishwasher time left : "+ this.dishwasherOutboundPort.getDishwasherTimeLeft());
        System.out.println("Electric consommation : "+ this.electricMeterOutboundPort.getConsommation());
        System.out.println("Battery max capacity : "+ this.batteryOutboundPort.getMaxCapacity());*/
    }

    @Override
    public void shutdown() throws ComponentShutdownException {
        try {
            this.lampOutboundPort.unpublishPort();
            this.fridgeOutboundPort.unpublishPort();
            this.windTurbineOutboundPort.unpublishPort();
            this.dishwasherOutboundPort.unpublishPort();
            this.electricMeterOutboundPort.unpublishPort();
            this.batteryOutboundPort.unpublishPort();
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
            this.dishwasherOutboundPort.unpublishPort();
            this.electricMeterOutboundPort.unpublishPort();
            this.batteryOutboundPort.unpublishPort();
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
        this.doPortDisconnection(this.dishwasherOutboundPort.getPortURI());
        this.doPortDisconnection(this.electricMeterOutboundPort.getPortURI());
        this.doPortDisconnection(this.batteryOutboundPort.getPortURI());
        super.finalise();
    }
}
