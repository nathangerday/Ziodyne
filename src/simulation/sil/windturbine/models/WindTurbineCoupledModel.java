package simulation.sil.windturbine.models;

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
import simulation.events.common.TicEvent;
import simulation.models.common.TicModel;
import simulation.sil.windturbine.events.WindReading;
import simulation.sil.windturbine.events.WindTurbineProduction;

/**
 * The class <code>WindTurbineCoupledModel</code> implements the 
 * simulation coupled model for the windturbine.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 */
public class WindTurbineCoupledModel extends CoupledModel{

    private static final long serialVersionUID = 1L;
    // -------------------------------------------------------------------------
    // Constants and variables
    // -------------------------------------------------------------------------
    public static final String  URI = "WindTurbineCoupledModel" ;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------
    public WindTurbineCoupledModel(
            String uri,
            TimeUnit simulatedTimeUnit,
            SimulatorI simulationEngine,
            ModelDescriptionI[] submodels,
            Map<Class<? extends EventI>, EventSink[]> imported,
            Map<Class<? extends EventI>, ReexportedEvent> reexported,
            Map<EventSource, EventSink[]> connections,
            Map<StaticVariableDescriptor, VariableSink[]> importedVars,
            Map<VariableSource, StaticVariableDescriptor> reexportedVars,
            Map<VariableSource, VariableSink[]> bindings
            ) throws Exception {
        super(uri,
                simulatedTimeUnit,
                simulationEngine,
                submodels,
                imported,
                reexported,
                connections,
                importedVars,
                reexportedVars,
                bindings);
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
                WindModel.URI,
                AtomicHIOA_Descriptor.create(
                        WindModel.class,
                        WindModel.URI,
                        TimeUnit.SECONDS,
                        null,
                        SimulationEngineCreationMode.ATOMIC_ENGINE)) ;
        atomicModelDescriptors.put(
                WindSensorModel.URI,
                AtomicHIOA_Descriptor.create(
                        WindSensorModel.class,
                        WindSensorModel.URI,
                        TimeUnit.SECONDS,
                        null,
                        SimulationEngineCreationMode.ATOMIC_ENGINE)) ;
        atomicModelDescriptors.put(
                WindTurbineModel.URI,
                AtomicModelDescriptor.create(
                        WindTurbineModel.class,
                        WindTurbineModel.URI,
                        TimeUnit.SECONDS,
                        null,
                        SimulationEngineCreationMode.ATOMIC_ENGINE)) ;
        atomicModelDescriptors.put(
                TicModel.URI_WINDTURBINE,
                AtomicModelDescriptor.create(
                        TicModel.class,
                        TicModel.URI_WINDTURBINE,
                        TimeUnit.SECONDS,
                        null,
                        SimulationEngineCreationMode.ATOMIC_ENGINE)) ;

        Map<String, CoupledModelDescriptor> coupledModelDescriptors =
                new HashMap<String,CoupledModelDescriptor>() ;


        Set<String> submodels = new HashSet<String>() ;
        submodels.add(TicModel.URI_WINDTURBINE) ;
        submodels.add(WindModel.URI) ;
        submodels.add(WindSensorModel.URI) ;
        submodels.add(WindTurbineModel.URI) ;

        //*********************************** 
        //Reexported events
        //***********************************

        Map<Class<? extends EventI>,ReexportedEvent> reexported =
                new HashMap<Class<? extends EventI>,ReexportedEvent>();
        reexported.put(WindTurbineProduction.class,
                new ReexportedEvent(WindTurbineModel.URI,WindTurbineProduction.class));

        //*********************************** 
        //Connections Event between submodels
        //***********************************

        Map<EventSource,EventSink[]> connections = new HashMap<EventSource,EventSink[]>() ;

        EventSource from = new EventSource(TicModel.URI_WINDTURBINE, TicEvent.class) ;
        EventSink[] to = new EventSink[] {new EventSink(WindSensorModel.URI, TicEvent.class)};
        connections.put(from, to);

        from = new EventSource(WindSensorModel.URI, WindReading.class) ;
        to = new EventSink[] {new EventSink(WindTurbineModel.URI, WindReading.class)};
        connections.put(from, to);

        //*********************************** 
        //Bindings variable between submodels
        //***********************************

        Map<VariableSource, VariableSink[]> bindings = new HashMap<VariableSource,VariableSink[]>();
        VariableSource from_v = new VariableSource("wind",Double.class,WindModel.URI);
        VariableSink[] to_v = new VariableSink[] {new VariableSink("wind", Double.class, WindSensorModel.URI)};
        bindings.put(from_v, to_v);

        coupledModelDescriptors.put(
                WindTurbineCoupledModel.URI,
                new CoupledHIOA_Descriptor(
                        WindTurbineCoupledModel.class,
                        WindTurbineCoupledModel.URI,
                        submodels,
                        null,
                        reexported,
                        connections,
                        null,
                        SimulationEngineCreationMode.COORDINATION_ENGINE,
                        null,
                        null,
                        bindings)) ;

        return new Architecture(
                WindTurbineCoupledModel.URI,
                atomicModelDescriptors,
                coupledModelDescriptors,
                TimeUnit.SECONDS);
    }
}
