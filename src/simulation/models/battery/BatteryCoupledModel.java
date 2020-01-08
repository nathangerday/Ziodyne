package simulation.models.battery;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.architectures.SimulationEngineCreationMode;
import fr.sorbonne_u.devs_simulation.hioa.architectures.AtomicHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.architectures.CoupledHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.StaticVariableDescriptor;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSink;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSource;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;
import fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.models.CoupledModel;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.events.ReexportedEvent;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.StandardCoupledModelReport;
import simulation.events.battery.BatteryCharging;
import simulation.events.battery.BatteryLevel;
import simulation.events.battery.BatteryOff;
import simulation.events.battery.BatteryOn;
import simulation.events.battery.BatteryProducing;
import simulation.events.battery.TicEvent;


public class BatteryCoupledModel extends CoupledModel {
	

	private static final long serialVersionUID = 1L;
	public static final String  URI = "BatteryCoupledModel" ;

	public BatteryCoupledModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine,
			ModelDescriptionI[] submodels, Map<Class<? extends EventI>, EventSink[]> imported,
			Map<Class<? extends EventI>, ReexportedEvent> reexported, Map<EventSource, EventSink[]> connections,
			Map<StaticVariableDescriptor, VariableSink[]> importedVars,
			Map<VariableSource, StaticVariableDescriptor> reexportedVars, Map<VariableSource, VariableSink[]> bindings)
			throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine, submodels, imported, reexported, connections, importedVars,
				reexportedVars, bindings);
		// TODO Auto-generated constructor stub
	}
	
	
	 // -------------------------------------------------------------------------
    // Methods
    // -------------------------------------------------------------------------
    @Override
    public SimulationReportI getFinalReport() throws Exception{
        StandardCoupledModelReport ret =
                new StandardCoupledModelReport(this.getURI()) ;
        for (int i = 0 ; i < this.submodels.length ; i++) {
            ret.addReport(this.submodels[i].getFinalReport()) ;
        }
        return ret ;
    }

    public static Architecture build() throws Exception{
        Map<String, AbstractAtomicModelDescriptor> atomicModelDescriptors =
                new HashMap<>() ;

        atomicModelDescriptors.put(
               BatteryModel.URI,
                AtomicHIOA_Descriptor.create(
                        BatteryModel.class,
                        BatteryModel.URI,
                        TimeUnit.SECONDS,
                        null,
                        SimulationEngineCreationMode.ATOMIC_ENGINE)) ;
        atomicModelDescriptors.put(
                BatterySensorModel.URI,
                AtomicHIOA_Descriptor.create(
                        BatterySensorModel.class,
                        BatterySensorModel.URI,
                        TimeUnit.SECONDS,
                        null,
                        SimulationEngineCreationMode.ATOMIC_ENGINE)) ;
        atomicModelDescriptors.put(
                TicModel.URI,
                AtomicModelDescriptor.create(
                        TicModel.class,
                        TicModel.URI,
                        TimeUnit.SECONDS,
                        null,
                        SimulationEngineCreationMode.ATOMIC_ENGINE)) ;
        atomicModelDescriptors.put(
                BatteryControllerModel.URI,
                AtomicModelDescriptor.create(
                        BatteryControllerModel.class,
                        BatteryControllerModel.URI,
                        TimeUnit.SECONDS,
                        null,
                        SimulationEngineCreationMode.ATOMIC_ENGINE)) ;

        Map<String, CoupledModelDescriptor> coupledModelDescriptors =
                new HashMap<String,CoupledModelDescriptor>() ;


        Set<String> submodels = new HashSet<String>() ;
        submodels.add(TicModel.URI) ;
        submodels.add(BatteryModel.URI) ;
        submodels.add(BatterySensorModel.URI) ;
        submodels.add(BatteryModel.URI) ;
        submodels.add(BatteryControllerModel.URI) ;

        //*********************************** 
        //Connections Event between submodels
        //***********************************

        Map<EventSource,EventSink[]> connections = new HashMap<EventSource,EventSink[]>() ;
        EventSource from = new EventSource(TicModel.URI, TicEvent.class) ;
        EventSink[] to = new EventSink[] {new EventSink(BatterySensorModel.URI, TicEvent.class)};
        connections.put(from, to);

        from = new EventSource(BatterySensorModel.URI,BatteryLevel.class) ;
        to = new EventSink[] {
                new EventSink(BatteryModel.URI, BatteryLevel.class),
                new EventSink(BatteryControllerModel.URI, BatteryLevel.class)
        };
        connections.put(from, to);

        from = new EventSource(BatteryControllerModel.URI, BatteryOn.class) ;
        to = new EventSink[] {new EventSink(BatteryModel.URI, BatteryOn.class)};
        connections.put(from, to);

        from = new EventSource(BatteryControllerModel.URI, BatteryOff.class) ;
        to = new EventSink[] {new EventSink(BatteryModel.URI, BatteryOff.class)};
        connections.put(from, to);
        
        from = new EventSource(BatteryControllerModel.URI, BatteryCharging.class) ;
        to = new EventSink[] {new EventSink(BatteryModel.URI, BatteryCharging.class)};
        connections.put(from, to);
        
        from = new EventSource(BatteryControllerModel.URI, BatteryProducing.class) ;
        to = new EventSink[] {new EventSink(BatteryModel.URI, BatteryProducing.class)};
        connections.put(from, to);

        //*********************************** 
        //Bindings variable between submodels
        //***********************************

        Map<VariableSource, VariableSink[]> bindings = new HashMap<VariableSource,VariableSink[]>();
        VariableSource from_v = new VariableSource("power",Double.class,BatteryModel.URI);
        VariableSink[] to_v = new VariableSink[] {new VariableSink("power", Double.class, BatterySensorModel.URI)};
        bindings.put(from_v, to_v);

        coupledModelDescriptors.put(
                BatteryCoupledModel.URI,
                new CoupledHIOA_Descriptor(
                        BatteryCoupledModel.class,
                        BatteryCoupledModel.URI,
                        submodels,
                        null,
                        null,
                        connections,
                        null,
                        SimulationEngineCreationMode.COORDINATION_ENGINE,
                        null,
                        null,
                        bindings)) ;

        return new Architecture(
                BatteryCoupledModel.URI,
                atomicModelDescriptors,
                coupledModelDescriptors,
                TimeUnit.SECONDS);
    }

}
