package components;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to provide an example
// for the extension of the BCM component model that aims to define a components
// tailored for cyber-physical control systems (CPCS) for Java.
//
// This software is governed by the CeCILL-C license under French law and
// abiding by the rules of distribution of free software.  You can use,
// modify and/ or redistribute the software under the terms of the
// CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
// URL "http://www.cecill.info".
//
// As a counterpart to the access to the source code and  rights to copy,
// modify and redistribute granted by the license, users are provided only
// with a limited warranty  and the software's author,  the holder of the
// economic rights,  and the successive licensors  have only  limited
// liability. 
//
// In this respect, the user's attention is drawn to the risks associated
// with loading,  using,  modifying and/or developing or reproducing the
// software by the user in light of its specific status of free software,
// that may mean  that it is complicated to manipulate,  and  that  also
// therefore means  that it is reserved for developers  and  experienced
// professionals having in-depth computer knowledge. Users are therefore
// encouraged to load and test the software's suitability as regards their
// requirements in conditions enabling the security of their systems and/or 
// data to be ensured and,  more generally, to use and operate it in the 
// same conditions as regards security. 
//
// The fact that you are presently reading this means that you have had
// knowledge of the CeCILL-C license and that you accept its terms.

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cyphy.plugins.devs.SupervisorPlugin;
import fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentAtomicModelDescriptor;
import fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentCoupledModelDescriptor;
import fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentModelArchitecture;
import fr.sorbonne_u.devs_simulation.architectures.SimulationEngineCreationMode;
import fr.sorbonne_u.devs_simulation.examples.molene.SimulationMain;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.utils.PlotterDescription;
import simulation.models.common.TicModel;
import simulation.overall.SGCoupledModel;
import simulation.sil.battery.events.BatteryConsumption;
import simulation.sil.battery.events.BatteryProduction;
import simulation.sil.battery.models.BatteryModel;
import simulation.sil.controller.models.ControllerModel;
import simulation.sil.dishwasher.events.DishwasherConsumption;
import simulation.sil.dishwasher.models.DishwasherCoupledModel;
import simulation.sil.dishwasher.models.DishwasherModel;
import simulation.sil.electricmeter.models.ElectricMeterModel;
import simulation.sil.fridge.events.FridgeConsumption;
import simulation.sil.fridge.models.FridgeCoupledModel;
import simulation.sil.fridge.models.FridgeModel;
import simulation.sil.lamp.events.LampConsumption;
import simulation.sil.lamp.models.LampCoupledModel;
import simulation.sil.lamp.models.LampModel;
import simulation.sil.windturbine.events.WindTurbineProduction;
import simulation.sil.windturbine.models.WindModel;
import simulation.sil.windturbine.models.WindSensorModel;
import simulation.sil.windturbine.models.WindTurbineCoupledModel;
import simulation.sil.windturbine.models.WindTurbineModel;

// -----------------------------------------------------------------------------
/**
 * The class <code>SGSupervisorComponent</code> implements a supervisor for
 * simulations of the household energy management example.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The component creates a simulation architecture and then executes simulation
 * runs. Two architectures are defined:
 * </p>
 * <ol>
 * <li>when the constructors are passed the architecture URI
 *   <code>SimulationArchitectures.MIL</code>, they create a MIL simulation
 *   architecture;</li>
 * <li>when the constructors are passed the architecture URI
 *   <code>SimulationArchitectures.SIL</code>, they create a SIL simulation
 *   architecture.</li>
 * </ol>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 * <p>Created on : 2020-01-20</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class Supervisor extends	AbstractComponent{

    // -------------------------------------------------------------------------
    // Constants and variables
    // -------------------------------------------------------------------------

    /** the supervisor plug-in attached to this component.					*/
    protected SupervisorPlugin		sp ;
    /** maps from URIs of models to URIs of the reflection inbound ports
     *  of the components that hold them.									*/
    protected Map<String,String>	modelURIs2componentURIs ;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * create a supervisor component with a self-generated reflection inbound
     * port URI.
     * 
     * <p><strong>Contract</strong></p>
     * 
     * <pre>
     * pre	simArchitectureURI != null
     * pre	modelURIs2componentURIs != null
     * pre	{@code modelURIs2componentURIs.size() >= 1}
     * post	true			// no postcondition.
     * </pre>
     *
     * @param simArchitectureURI		the URI of the simulation architecture to be created and run.
     * @param modelURIs2componentURIs	map from URIs of the simulation models and the URI of the reflection inbound port of the component holding them.
     * @throws Exception				<i>to do</i>.
     */
    protected Supervisor(
            Map<String,String> modelURIs2componentURIs
            ) throws Exception {
        super(2, 0) ;

        assert	modelURIs2componentURIs != null ;
        assert	modelURIs2componentURIs.size() >= 1 ;

        this.initialise(modelURIs2componentURIs) ;
    }

    /**
     * create a supervisor component with a given reflection inbound port URI.
     * 
     * <p><strong>Contract</strong></p>
     * 
     * <pre>
     * pre	reflectionInboundPortURI != null
     * pre	simArchitectureURI != null
     * pre	modelURIs2componentURIs != null
     * pre	{@code modelURIs2componentURIs.size() >= 1}
     * post	true			// no postcondition.
     * </pre>
     *
     * @param reflectionInboundPortURI	URI of the reflection inbound port of the component.
     * @param simArchitectureURI		the URI of the simulation architecture to be created and run.
     * @param modelURIs2componentURIs	map from URIs of the simulation models and the URI of the reflection inbound port of the component holding them.
     * @throws Exception				<i>to do</i>.
     */
    protected Supervisor(
            String reflectionInboundPortURI,
            Map<String,String> modelURIs2componentURIs
            ) throws Exception{
        super(reflectionInboundPortURI, 2, 0) ;

        assert	modelURIs2componentURIs != null ;
        assert	modelURIs2componentURIs.size() >= 1 ;

        this.initialise(modelURIs2componentURIs) ;
    }

    /**
     * initialise the supervisor component.
     * 
     * <p><strong>Contract</strong></p>
     * 
     * <pre>
     * pre	simArchitectureURI != null
     * pre	modelURIs2componentURIs != null
     * pre	{@code modelURIs2componentURIs.size() >= 1}
     * post	true			// no postcondition.
     * </pre>
     *
     * @param simArchitectureURI		the URI of the simulation architecture to be created and run.
     * @param modelURIs2componentURIs	map from URIs of the simulation models and the URI of the reflection inbound port of the component holding them.
     * @throws Exception				<i>to do</i>.
     */
    protected void initialise(Map<String,String> modelURIs2componentURIs) throws Exception{
        this.modelURIs2componentURIs = modelURIs2componentURIs ;

        this.tracer.setTitle("Supervisor component") ;
        this.tracer.setRelativePosition(0, 4) ;
        this.toggleTracing() ;

        this.sp = new SupervisorPlugin(this.createSILArchitecture()) ;
        sp.setPluginURI("supervisor") ;
        this.installPlugin(this.sp) ;
        this.logMessage("Supervisor plug-in installed...") ;
    }


    /**
     * create the SIL simulation architecture supervised by this component.
     * 
     * <p><strong>Contract</strong></p>
     * 
     * <pre>
     * pre	true			// no precondition.
     * post	true			// no postcondition.
     * </pre>
     *
     * @return				the SIL simulation architecture.
     * @throws Exception	<i>to do</i>.
     */
    @SuppressWarnings("unchecked")
    protected ComponentModelArchitecture createSILArchitecture() throws Exception {
        Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
                new HashMap<>() ;

        // ---------------------------------------------------
        // Models
        // ---------------------------------------------------

        //wind turbine
        atomicModelDescriptors.put(
                WindTurbineCoupledModel.URI,
                ComponentAtomicModelDescriptor.create(
                        WindTurbineCoupledModel.URI,
                        null,
                        (Class<? extends EventI>[])
                        new Class<?>[]{WindTurbineProduction.class},
                        TimeUnit.SECONDS,
                        this.modelURIs2componentURIs.get(WindTurbineCoupledModel.URI)));

        //Lamp
        atomicModelDescriptors.put(
                LampCoupledModel.URI,
                ComponentAtomicModelDescriptor.create(
                        LampCoupledModel.URI,
                        null,
                        (Class<? extends EventI>[])
                        new Class<?>[]{LampConsumption.class},
                        TimeUnit.SECONDS,
                        this.modelURIs2componentURIs.get(LampCoupledModel.URI)));

        //Fridge
        atomicModelDescriptors.put(
                FridgeCoupledModel.URI,
                ComponentAtomicModelDescriptor.create(
                        FridgeCoupledModel.URI,
                        null,
                        (Class<? extends EventI>[])
                        new Class<?>[]{FridgeConsumption.class},
                        TimeUnit.SECONDS,
                        this.modelURIs2componentURIs.get(FridgeCoupledModel.URI)));

        //Electric meter
        atomicModelDescriptors.put(
                ElectricMeterModel.URI,
                ComponentAtomicModelDescriptor.create(
                        ElectricMeterModel.URI,
                        (Class<? extends EventI>[])
                        new Class<?>[]{
                            BatteryConsumption.class,
                            BatteryProduction.class,
                            WindTurbineProduction.class,
                            LampConsumption.class,
                            FridgeConsumption.class,
                            DishwasherConsumption.class
                        },
                        null,
                        TimeUnit.SECONDS,
                        this.modelURIs2componentURIs.get(ElectricMeterModel.URI)));

        //Dishwasher
        atomicModelDescriptors.put(
                DishwasherCoupledModel.URI,
                ComponentAtomicModelDescriptor.create(
                        DishwasherCoupledModel.URI,
                        null,
                        (Class<? extends EventI>[])
                        new Class<?>[]{DishwasherConsumption.class},
                        TimeUnit.SECONDS,
                        this.modelURIs2componentURIs.get(DishwasherCoupledModel.URI)));

        //Controller
        atomicModelDescriptors.put(
                ControllerModel.URI,
                ComponentAtomicModelDescriptor.create(
                        ControllerModel.URI,
                        null,
                        null,
                        TimeUnit.SECONDS,
                        this.modelURIs2componentURIs.get(ControllerModel.URI)));

        //Battery
        atomicModelDescriptors.put(
                BatteryModel.URI,
                ComponentAtomicModelDescriptor.create(
                        BatteryModel.URI,
                        null,
                        (Class<? extends EventI>[])
                        new Class<?>[]{
                            BatteryConsumption.class,
                            BatteryProduction.class
                        },
                        TimeUnit.SECONDS,
                        this.modelURIs2componentURIs.get(BatteryModel.URI)));

        // ---------------------------------------------------
        // Sub models
        // ---------------------------------------------------

        Map<String,CoupledModelDescriptor> coupledModelDescriptors =
                new HashMap<>() ;

        Set<String> submodels = new HashSet<String>() ;
        submodels.add(ControllerModel.URI) ;
        submodels.add(ElectricMeterModel.URI) ;
        submodels.add(BatteryModel.URI) ;
        submodels.add(WindTurbineCoupledModel.URI) ;
        submodels.add(LampCoupledModel.URI) ;
        submodels.add(FridgeCoupledModel.URI) ;
        submodels.add(DishwasherCoupledModel.URI) ;

        // ---------------------------------------------------
        // Events links
        // ---------------------------------------------------

        Map<EventSource,EventSink[]> connections = new HashMap<EventSource,EventSink[]>() ;

        //From battery to electric meter
        connections.put(
                new EventSource(BatteryModel.URI,
                        BatteryConsumption.class),
                new EventSink[] {
                        new EventSink(ElectricMeterModel.URI,
                                BatteryConsumption.class)
                }) ;
        connections.put(
                new EventSource(BatteryModel.URI,
                        BatteryProduction.class),
                new EventSink[] {
                        new EventSink(ElectricMeterModel.URI,
                                BatteryProduction.class)
                }) ;

        //From windturbine coupled to electric meter
        connections.put(
                new EventSource(WindTurbineCoupledModel.URI,
                        WindTurbineProduction.class),
                new EventSink[] {
                        new EventSink(ElectricMeterModel.URI,
                                WindTurbineProduction.class)
                }) ;

        //From lamp coupled to electric meter
        connections.put(
                new EventSource(LampCoupledModel.URI,
                        LampConsumption.class),
                new EventSink[] {
                        new EventSink(ElectricMeterModel.URI,
                                LampConsumption.class)
                }) ;

        //From lamp coupled to electric meter
        connections.put(
                new EventSource(FridgeCoupledModel.URI,
                        FridgeConsumption.class),
                new EventSink[] {
                        new EventSink(ElectricMeterModel.URI,
                                FridgeConsumption.class)
                }) ;

        //From lamp coupled to electric meter
        connections.put(
                new EventSource(DishwasherCoupledModel.URI,
                        DishwasherConsumption.class),
                new EventSink[] {
                        new EventSink(ElectricMeterModel.URI,
                                DishwasherConsumption.class)
                }) ;


        coupledModelDescriptors.put(
                SGCoupledModel.URI,
                ComponentCoupledModelDescriptor.create(
                        SGCoupledModel.class,
                        SGCoupledModel.URI,
                        submodels,
                        null,
                        null,
                        connections,
                        null,
                        SimulationEngineCreationMode.COORDINATION_ENGINE,
                        this.modelURIs2componentURIs.get(SGCoupledModel.URI))) ;

        ComponentModelArchitecture arch =
                new ComponentModelArchitecture(
                        "SIL simulation architecture",
                        SGCoupledModel.URI,
                        atomicModelDescriptors,
                        coupledModelDescriptors,
                        TimeUnit.SECONDS) ;
        return arch ;
    }

    private HashMap<String,Object> getSimulationRunParameters(){
        HashMap<String,Object> simParams = new HashMap<String,Object>();

        //Battery
        simParams.put(
                BatteryModel.URI + ":" + BatteryModel.SERIES_CAPACITY + PlotterDescription.PLOTTING_PARAM_NAME,
                new PlotterDescription(
                        "Battery Capacity",
                        "Time (sec)",
                        "Capacity (W)",
                        SimulationMain.ORIGIN_X,
                        SimulationMain.ORIGIN_Y + SimulationMain.getPlotterHeight(),
                        SimulationMain.getPlotterWidth(),
                        SimulationMain.getPlotterHeight())) ;
        simParams.put(
                BatteryModel.URI + ":" + BatteryModel.SERIES_CONSUMPTION + PlotterDescription.PLOTTING_PARAM_NAME,
                new PlotterDescription(
                        "Battery Consumption",
                        "Time (sec)",
                        "Power (watt)",
                        SimulationMain.ORIGIN_X,
                        SimulationMain.ORIGIN_Y + 2 * SimulationMain.getPlotterHeight(),
                        SimulationMain.getPlotterWidth(),
                        SimulationMain.getPlotterHeight())) ;

        simParams.put(
                BatteryModel.URI + ":" + BatteryModel.SERIES_PRODUCTION + PlotterDescription.PLOTTING_PARAM_NAME,
                new PlotterDescription(
                        "Battery Production",
                        "Time (sec)",
                        "Power (W)",
                        SimulationMain.ORIGIN_X,
                        SimulationMain.ORIGIN_Y + 3 * SimulationMain.getPlotterHeight(),
                        SimulationMain.getPlotterWidth(),
                        SimulationMain.getPlotterHeight())) ;

        //Dishwasher
        simParams.put(
                DishwasherModel.URI + ":" + PlotterDescription.PLOTTING_PARAM_NAME,
                new PlotterDescription(
                        "DishWasher Consumption",
                        "Time (sec)",
                        "Power (W)",
                        SimulationMain.ORIGIN_X + SimulationMain.getPlotterWidth(),
                        SimulationMain.ORIGIN_Y + SimulationMain.getPlotterHeight(),
                        SimulationMain.getPlotterWidth(),
                        SimulationMain.getPlotterHeight()));

        //Electric meter
        simParams.put(
                ElectricMeterModel.URI + ":" + ElectricMeterModel.SERIES_AVAILABLE + PlotterDescription.PLOTTING_PARAM_NAME,
                new PlotterDescription(
                        "Available Energy",
                        "Time (sec)",
                        "Power (W)",
                        SimulationMain.ORIGIN_X + 2 * SimulationMain.getPlotterWidth(),
                        SimulationMain.ORIGIN_Y + SimulationMain.getPlotterHeight(),
                        SimulationMain.getPlotterWidth(),
                        SimulationMain.getPlotterHeight())) ;
        simParams.put(
                ElectricMeterModel.URI + ":" + ElectricMeterModel.SERIES_CONSUMPTION + PlotterDescription.PLOTTING_PARAM_NAME,
                new PlotterDescription(
                        "Total Consumption",
                        "Time (sec)",
                        "Power (W)",
                        SimulationMain.ORIGIN_X + 2 * SimulationMain.getPlotterWidth(),
                        SimulationMain.ORIGIN_Y + 2 * SimulationMain.getPlotterHeight(),
                        SimulationMain.getPlotterWidth(),
                        SimulationMain.getPlotterHeight())) ;

        simParams.put(
                ElectricMeterModel.URI + ":" + ElectricMeterModel.SERIES_PRODUCTION + PlotterDescription.PLOTTING_PARAM_NAME,
                new PlotterDescription(
                        "Total Production",
                        "Time (sec)",
                        "Power (W)",
                        SimulationMain.ORIGIN_X + 2 * SimulationMain.getPlotterWidth(),
                        SimulationMain.ORIGIN_Y + 3 * SimulationMain.getPlotterHeight(),
                        SimulationMain.getPlotterWidth(),
                        SimulationMain.getPlotterHeight())) ;

        //Fridge

        simParams.put(
                FridgeModel.URI + ":" + FridgeModel.SERIES_FRIDGE + PlotterDescription.PLOTTING_PARAM_NAME,
                new PlotterDescription(
                        "Fridge Temperature Model",
                        "Time (sec)",
                        "Celsius",
                        SimulationMain.ORIGIN_X + 3 * SimulationMain.getPlotterWidth(),
                        SimulationMain.ORIGIN_Y + SimulationMain.getPlotterHeight(),
                        SimulationMain.getPlotterWidth(),
                        SimulationMain.getPlotterHeight())) ;
        simParams.put(
                FridgeModel.URI + ":" + FridgeModel.SERIES_FREEZER + PlotterDescription.PLOTTING_PARAM_NAME,
                new PlotterDescription(
                        "Freezer Temperature Model",
                        "Time (sec)",
                        "Celsius",
                        SimulationMain.ORIGIN_X + 3 * SimulationMain.getPlotterWidth(),
                        SimulationMain.ORIGIN_Y + 2*SimulationMain.getPlotterHeight(),
                        SimulationMain.getPlotterWidth(),
                        SimulationMain.getPlotterHeight())) ;
        simParams.put(
                FridgeModel.URI + ":" + FridgeModel.SERIES_POWER + PlotterDescription.PLOTTING_PARAM_NAME,
                new PlotterDescription(
                        "Fridge Power Model",
                        "Time (sec)",
                        "Power (Watt)",
                        SimulationMain.ORIGIN_X + 3 * SimulationMain.getPlotterWidth() ,
                        SimulationMain.ORIGIN_Y + 3*SimulationMain.getPlotterHeight(),
                        SimulationMain.getPlotterWidth(),
                        SimulationMain.getPlotterHeight())) ;

        //Lamp
        simParams.put(
                LampModel.URI + ":" + PlotterDescription.PLOTTING_PARAM_NAME,
                new PlotterDescription(
                        "Lamp Power",
                        "Time (sec)",
                        "Power (Watt)",
                        SimulationMain.ORIGIN_X + 4 * SimulationMain.getPlotterWidth(),
                        SimulationMain.ORIGIN_Y + SimulationMain.getPlotterHeight(),
                        SimulationMain.getPlotterWidth(),
                        SimulationMain.getPlotterHeight()));

        //-----------------------
        //Wind Turbine
        //-----------------------

        //Parameter of TicModel
        simParams.put(TicModel.URI_WINDTURBINE + ":" + TicModel.DELAY_PARAMETER_NAME,
                new Duration(5.0, TimeUnit.SECONDS));
        //Parameters of WindModel
        simParams.put(WindModel.URI + ":" + WindModel.MAX_WIND,15.0);
        simParams.put(WindModel.URI + ":" + WindModel.WMASSF,0.05);
        simParams.put(WindModel.URI + ":" + WindModel.WIS,1.0);
        simParams.put(
                WindModel.URI + ":" + PlotterDescription.PLOTTING_PARAM_NAME,
                new PlotterDescription(
                        "Wind Speed Model",
                        "Time (sec)",
                        "Speed (m/s)",
                        SimulationMain.ORIGIN_X + 5 * SimulationMain.getPlotterWidth(),
                        SimulationMain.ORIGIN_Y + 2*SimulationMain.getPlotterHeight(),
                        SimulationMain.getPlotterWidth(),
                        SimulationMain.getPlotterHeight())) ;
        //Parameters of WindSendsorModel
        simParams.put(
                WindSensorModel.URI + ":" + PlotterDescription.PLOTTING_PARAM_NAME,
                new PlotterDescription(
                        "Wind Sensor Speed Model",
                        "Time (sec)",
                        "Speed (m/s)",
                        SimulationMain.ORIGIN_X + 5 * SimulationMain.getPlotterWidth(),
                        SimulationMain.ORIGIN_Y + 3*SimulationMain.getPlotterHeight(),
                        SimulationMain.getPlotterWidth(),
                        SimulationMain.getPlotterHeight())) ;
        //Parameters of WindTurbineModel
        simParams.put(WindTurbineModel.URI + ":" + WindTurbineModel.MAX_SPEED,10.0);
        simParams.put(WindTurbineModel.URI + ":" + WindTurbineModel.MIN_SPEED,3.0);
        simParams.put(
                WindTurbineModel.URI + ":" + PlotterDescription.PLOTTING_PARAM_NAME,
                new PlotterDescription(
                        "Wind Turbine Power Production",
                        "Time (sec)",
                        "Power (watt)",
                        SimulationMain.ORIGIN_X + 5 * SimulationMain.getPlotterWidth(),
                        SimulationMain.ORIGIN_Y + SimulationMain.getPlotterHeight(),
                        SimulationMain.getPlotterWidth(),
                        SimulationMain.getPlotterHeight()));

        return simParams;
    }

    /**
     * @see fr.sorbonne_u.components.AbstractComponent#execute()
     */
    @Override
    public void	execute() throws Exception {
        super.execute() ;

        this.logMessage("supervisor component begins execution.") ;
        this.sp.createSimulator() ;
        Thread.sleep(1000L) ;
        this.logMessage("supervisor component begins simulation.") ;
        long start = System.currentTimeMillis() ;
        this.sp.setSimulationRunParameters(getSimulationRunParameters());
        this.sp.doStandAloneSimulation(0, 5000.0) ;
        long end = System.currentTimeMillis() ;
        this.logMessage("supervisor component ends simulation. " +
                (end - start)) ;
        Thread.sleep(1000) ;
    }
}
// -----------------------------------------------------------------------------
