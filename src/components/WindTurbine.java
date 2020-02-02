package components;

import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;
import fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentAccessI;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.PostconditionException;
import fr.sorbonne_u.components.exceptions.PreconditionException;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import interfaces.WindTurbineI;
import ports.WindTurbineInboundPort;
import simulation.sil.windturbine.models.WindTurbineCoupledModel;
import simulation.sil.windturbine.models.WindTurbineModel;
import simulation.sil.windturbine.plugin.WindTurbineSimulatorPlugin;

/**
 *The class <code>WindTurbine</code> implements a wind turbine component that will
 * hold the wind turbine simulation model.
 * 
  <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 *
 */
public class WindTurbine extends AbstractCyPhyComponent implements WindTurbineI,EmbeddingComponentAccessI{
    /**
	 * Port that exposes the offered interface of the wind turbine with the given URI to ease the
	 * connection from controller components.
	 */
    protected WindTurbineInboundPort windTurbineInboundPort;
    /** true if wind turbine is activated, false if not*/
    protected boolean isOn;
    /** true if the wind turbine is on break, false if not*/
    protected boolean isOnBreak;
    /** the plugin in order to access the model 	 */
    protected WindTurbineSimulatorPlugin asp;

    /**
     * Create a wind turbine  component
     * 
     * <p><strong>Contract</strong></p>
	 *  
	 * <pre>
	 * pre uri != null
	 * </pre>
	 * 
	 * <post> 
	 * post isOn == LampState.OFF
	 * post findPortFromURI(windTurbineInboundPort.portURI).implementedInterface == LampI.class
	 * post isPortExisting(windTurbineInboundPort.portURI()) == true
	 * post findPortFromURI(windTurbineInboundPort.getportURI).isPublished == true
	 * </post>
	 * 
	 * 
     * @param uri
     * @param windInboundPortURI
     * @throws Exception
     */
    protected WindTurbine(String uri, String windTurbineInboundPortURI) throws Exception{
        super(uri,1,0);
        assert uri != null :new PreconditionException("uri can't be null!") ;
        this.isOn = false;
        this.isOnBreak = false;
        this.addOfferedInterface(WindTurbineI.class);
        windTurbineInboundPort = new WindTurbineInboundPort(windTurbineInboundPortURI, this);
        windTurbineInboundPort.publishPort();

        this.initialise();

        assert this.isOn == false :
            new PostconditionException("The wind turbine's state has not been initialised correctly !");
        assert this.isPortExisting(windTurbineInboundPort.getPortURI()):
            new PostconditionException("The component must have a "
                    + "port with URI " + windTurbineInboundPort.getPortURI()) ;
        assert	this.findPortFromURI(windTurbineInboundPort.getPortURI()).
        getImplementedInterface().equals(WindTurbineI.class) :
            new PostconditionException("The component must have a "
                    + "port with implemented interface WindTurbineI") ;
        assert	this.findPortFromURI(windTurbineInboundPort.getPortURI()).isPublished() :
            new PostconditionException("The component must have a "
                    + "port published with URI " + windTurbineInboundPort.getPortURI()) ;
    }

    /**
 	 * Initialise the lamp by installing the plugin for accessing to the model.
 	 * 
 	 * @throws Exception
 	 */
    private void initialise() throws Exception{
        Architecture localArchitecture = this.createLocalArchitecture(null) ;
        this.asp = new WindTurbineSimulatorPlugin();
        this.asp.setPluginURI(localArchitecture.getRootModelURI()) ;
        this.asp.setSimulationArchitecture(localArchitecture) ;
        this.installPlugin(this.asp) ;
        this.toggleLogging() ;
    }


//    @Override
//    public void execute() throws Exception {
//        // @remove A garder que en standalone
//        HashMap<String,Object> simParams = new HashMap<String,Object>();
//
//        //Parameter of TicModel
//        simParams.put(TicModel.URI_WINDTURBINE + ":" + TicModel.DELAY_PARAMETER_NAME,
//                new Duration(5.0, TimeUnit.SECONDS));
//        //Parameters of WindModel
//        simParams.put(WindModel.URI + ":" + WindModel.MAX_WIND,15.0);
//        simParams.put(WindModel.URI + ":" + WindModel.WMASSF,0.05);
//        simParams.put(WindModel.URI + ":" + WindModel.WIS,1.0);
//        simParams.put(
//                WindModel.URI + ":" + PlotterDescription.PLOTTING_PARAM_NAME,
//                new PlotterDescription(
//                        "Wind Speed Model",
//                        "Time (sec)",
//                        "Speed (m/s)",
//                        SimulationMain.ORIGIN_X,
//                        SimulationMain.ORIGIN_Y + 2*SimulationMain.getPlotterHeight(),
//                        SimulationMain.getPlotterWidth(),
//                        SimulationMain.getPlotterHeight())) ;
//        //Parameters of WindSendsorModel
//        simParams.put(
//                WindSensorModel.URI + ":" + PlotterDescription.PLOTTING_PARAM_NAME,
//                new PlotterDescription(
//                        "Wind Sensor Speed Model",
//                        "Time (sec)",
//                        "Speed (m/s)",
//                        SimulationMain.ORIGIN_X,
//                        SimulationMain.ORIGIN_Y + 3*SimulationMain.getPlotterHeight(),
//                        SimulationMain.getPlotterWidth(),
//                        SimulationMain.getPlotterHeight())) ;
//        //Parameters of WindTurbineModel
//        simParams.put(WindTurbineModel.URI + ":" + WindTurbineModel.MAX_SPEED,10.0);
//        simParams.put(WindTurbineModel.URI + ":" + WindTurbineModel.MIN_SPEED,3.0);
//        simParams.put(
//                WindTurbineModel.URI + ":" + PlotterDescription.PLOTTING_PARAM_NAME,
//                new PlotterDescription(
//                        "Wind Turbine Power Production",
//                        "Time (sec)",
//                        "Power (watt)",
//                        SimulationMain.ORIGIN_X,
//                        SimulationMain.ORIGIN_Y + SimulationMain.getPlotterHeight(),
//                        SimulationMain.getPlotterWidth(),
//                        SimulationMain.getPlotterHeight()));
//
//
//        this.asp.setSimulationRunParameters(simParams);
//        asp.setDebugLevel(0);
//        asp.doStandAloneSimulation(0.0, 500.0);
//    }



    /**
 	 * Shutdown the component
 	 * 
 	 * @throws ComponentShutdownException
 	 */
    @Override
    public void shutdown() throws ComponentShutdownException {
        try {
            windTurbineInboundPort.unpublishPort();
        }catch(Exception e) {
            throw new ComponentShutdownException(e);
        }
        super.shutdown();
    }


    /**
  	 * Shutdown the component now
  	 * 
  	 * @throws ComponentShutdownException
  	 */
    @Override
    public void shutdownNow() throws ComponentShutdownException {
        try {
            windTurbineInboundPort.unpublishPort();
        }catch(Exception e) {
            throw new ComponentShutdownException(e);
        }
        super.shutdownNow();
    }


    /**
     * Return isOn value 
     * 
     * @return isOn
     */
    @Override
    public boolean isOn() {
        return isOn;
    }


    /**
     * Return isOnBreak value 
     * 
     * @return isOnBreak
     */
    @Override
    public boolean isOnBreak() {
        return isOnBreak;
    }

    /**
     * Set the wind turbine on break or not on break
     */
    @Override
    public void switchBreak() throws Exception {
        isOnBreak = !isOnBreak;
    }


    /** 
     *  Return the wind speed value
     * 
     * @return current wind speed
     */
    @Override
    public double getWindSpeed() throws Exception {
        return (double) asp.getModelStateValue(WindTurbineModel.URI, "speed");
    }


    /**
	 * Create local architecture 
	 * 
	 * @param URI of the model
	 * @return local architecture of the lamp
	 */
    @Override
    protected Architecture createLocalArchitecture(String modelURI) throws Exception{
        return WindTurbineCoupledModel.build();
    }

	/**
	 * Return the embedding component state value.
	 * 
	 * @param name of the component
	 * @return isOn, or isOnBreak
	 * 
	 */
    @Override
    public Object getEmbeddingComponentStateValue(String name) throws Exception{
        if(name.equals("state")) {
            return isOn;
        } else if(name.equals("break")) {
            return isOnBreak;
        } else {
            throw new RuntimeException();
        }
    }

    /**
  	 * Set a new embedding component state value.
  	 * 
  	 * @param name of the component
  	 * @param new state value
  	 * 
  	 */
    @Override
    public void setEmbeddingComponentStateValue(String name , Object value) {
        if(name.equals("state")) {
            this.isOn = (boolean)value;
        } else {
            throw new RuntimeException();
        }
    }
}
