package simulation.sil.dishwasher.models;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.architectures.SimulationEngineCreationMode;
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
import simulation.sil.dishwasher.events.DishwasherConsumption;
import simulation.sil.dishwasher.events.DishwasherOn;


/**
 * The class <code>DishwasherCoupledModel</code> implements the 
 * simulation coupled model for the dishwasher.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 */
public class DishwasherCoupledModel extends CoupledModel {

    private static final long serialVersionUID = 1L;
    // -------------------------------------------------------------------------
    // Constants and variables
    // -------------------------------------------------------------------------
    public static final String	URI = "SILDishwasherCoupledModel" ;


    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public DishwasherCoupledModel(
            String uri,
            TimeUnit simulatedTimeUnit,
            SimulatorI simulationEngine,
            ModelDescriptionI[] submodels,
            Map<Class<? extends EventI>, EventSink[]> imported,
            Map<Class<? extends EventI>, ReexportedEvent> reexported,
            Map<EventSource, EventSink[]> connections,
            Map<StaticVariableDescriptor, VariableSink[]> importedVars,
            Map<VariableSource, StaticVariableDescriptor> reexportedVars,
            Map<VariableSource, VariableSink[]> bindings) throws Exception {
        super(
                uri,
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
    public SimulationReportI getFinalReport() throws Exception {
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
                DishwasherModel.URI,
                AtomicModelDescriptor.create(
                        DishwasherModel.class,
                        DishwasherModel.URI,
                        TimeUnit.SECONDS,
                        null,
                        SimulationEngineCreationMode.ATOMIC_ENGINE)) ;
        atomicModelDescriptors.put(
                DishwasherControllerModel.URI,
                AtomicModelDescriptor.create(
                        DishwasherControllerModel.class,
                        DishwasherControllerModel.URI,
                        TimeUnit.SECONDS,
                        null,
                        SimulationEngineCreationMode.ATOMIC_ENGINE)) ;

        Map<String, CoupledModelDescriptor> coupledModelDescriptors =
                new HashMap<String,CoupledModelDescriptor>() ;

        Set<String> submodels = new HashSet<String>() ;
        submodels.add(DishwasherModel.URI) ;
        submodels.add(DishwasherControllerModel.URI) ;

        // **************************
        // Reexported events
        // **************************

        Map<Class<? extends EventI>,ReexportedEvent> reexported =
                new HashMap<Class<? extends EventI>,ReexportedEvent>();
        reexported.put(DishwasherConsumption.class,
                new ReexportedEvent(DishwasherModel.URI,DishwasherConsumption.class));


        // **************************
        // Events sub - sub
        // **************************

        Map<EventSource,EventSink[]> connections = new HashMap<EventSource,EventSink[]>() ;

        EventSource from = new EventSource(DishwasherControllerModel.URI, DishwasherOn.class) ;
        EventSink[] to = new EventSink[] {new EventSink(DishwasherModel.URI, DishwasherOn.class)} ;
        connections.put(from, to) ;

        coupledModelDescriptors.put(
                DishwasherCoupledModel.URI,
                new CoupledHIOA_Descriptor(
                        DishwasherCoupledModel.class,
                        DishwasherCoupledModel.URI,
                        submodels,
                        null,
                        reexported,
                        connections,
                        null,
                        SimulationEngineCreationMode.COORDINATION_ENGINE,
                        null,
                        null,
                        null)) ;

        return new Architecture(
                DishwasherCoupledModel.URI,
                atomicModelDescriptors,
                coupledModelDescriptors,
                TimeUnit.SECONDS);
    }
}

