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

public class WindTurbine extends AbstractCyPhyComponent implements WindTurbineI,EmbeddingComponentAccessI{

    protected WindTurbineInboundPort windTurbineInboundPort;
    protected boolean isOn;
    protected boolean isOnBreak;
    protected WindTurbineSimulatorPlugin asp;

    protected WindTurbine(String uri, String windTurbineInboundPortURI) throws Exception{
        super(uri,1,0);
        assert uri != null :new PreconditionException("uri can't be null!") ;
        this.isOn = false;
        this.isOnBreak = false;
        this.addOfferedInterface(WindTurbineI.class);
        windTurbineInboundPort = new WindTurbineInboundPort(windTurbineInboundPortURI, this);
        windTurbineInboundPort.publishPort();

        initialise();

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


    @Override
    public void shutdown() throws ComponentShutdownException {
        try {
            windTurbineInboundPort.unpublishPort();
        }catch(Exception e) {
            throw new ComponentShutdownException(e);
        }
        super.shutdown();
    }


    @Override
    public void shutdownNow() throws ComponentShutdownException {
        try {
            windTurbineInboundPort.unpublishPort();
        }catch(Exception e) {
            throw new ComponentShutdownException(e);
        }
        super.shutdownNow();
    }


    @Override
    public boolean isOn() {
        return isOn;
    }


    @Override
    public boolean isOnBreak() {
        return isOnBreak;
    }


    @Override
    public void switchBreak() throws Exception {
        isOnBreak = !isOnBreak;
    }


    @Override
    public double getWindSpeed() throws Exception {
        return (double) asp.getModelStateValue(WindTurbineModel.URI, "speed");
    }


    @Override
    protected Architecture createLocalArchitecture(String modelURI) throws Exception{
        return WindTurbineCoupledModel.build();
    }


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

    @Override
    public void setEmbeddingComponentStateValue(String name , Object value) {
        if(name.equals("state")) {
            this.isOn = (boolean)value;
        } else {
            throw new RuntimeException();
        }
    }
}
