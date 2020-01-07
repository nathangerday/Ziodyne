package simulation.models.dishwasher;

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
import simulation.events.dishwasher.SetModeEco;
import simulation.events.dishwasher.SetModeStandard;
import simulation.events.dishwasher.SwitchOff;
import simulation.events.dishwasher.SwitchOn;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class DishwasherCoupledModel extends CoupledModel {

    // -------------------------------------------------------------------------
    // Constants and variables
    // -------------------------------------------------------------------------
    public static final String	URI = "DishwasherCoupledModel" ;


    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------
    public DishwasherCoupledModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine, ModelDescriptionI[] submodels, Map<Class<? extends EventI>, EventSink[]> imported, Map<Class<? extends EventI>, ReexportedEvent> reexported, Map<EventSource, EventSink[]> connections, Map<StaticVariableDescriptor, VariableSink[]> importedVars, Map<VariableSource, StaticVariableDescriptor> reexportedVars, Map<VariableSource, VariableSink[]> bindings) throws Exception {
        super(uri, simulatedTimeUnit, simulationEngine, submodels, imported, reexported, connections, importedVars, reexportedVars, bindings);
    }

    // -------------------------------------------------------------------------
    // Methods
    // -------------------------------------------------------------------------
    /**
     * @see fr.sorbonne_u.devs_simulation.models.CoupledModel#getFinalReport()
     */
    @Override
    public SimulationReportI getFinalReport() throws Exception
    {
        StandardCoupledModelReport ret =
                new StandardCoupledModelReport(this.getURI()) ;
        for (int i = 0 ; i < this.submodels.length ; i++) {
            ret.addReport(this.submodels[i].getFinalReport()) ;
        }
        return ret ;
    }

    /**
     * build the simulation architecture corresponding to this coupled model.
     *
     * <p><strong>Contract</strong></p>
     *
     * <pre>
     * pre	true			// no precondition.
     * post	true			// no postcondition.
     * </pre>
     *
     * @return				the simulation architecture corresponding to this coupled model.
     * @throws Exception	<i>TO DO.</i>
     */
    public static Architecture build() throws Exception
    {
        Map<String, AbstractAtomicModelDescriptor> atomicModelDescriptors =
                new HashMap<>() ;

        atomicModelDescriptors.put(
                DishwasherModel.URI,
                AtomicHIOA_Descriptor.create(
                		DishwasherModel.class,
                        DishwasherModel.URI,
                        TimeUnit.SECONDS,
                        null,
                        SimulationEngineCreationMode.ATOMIC_ENGINE)) ;
        atomicModelDescriptors.put(
                DishwasherUserModel.URI,
                AtomicModelDescriptor.create(
                		DishwasherUserModel.class,
                		DishwasherUserModel.URI,
                        TimeUnit.SECONDS,
                        null,
                        SimulationEngineCreationMode.ATOMIC_ENGINE)) ;

        Map<String, CoupledModelDescriptor> coupledModelDescriptors =
                new HashMap<String,CoupledModelDescriptor>() ;

        Set<String> submodels = new HashSet<String>() ;
        submodels.add(DishwasherModel.URI) ;
        submodels.add(DishwasherUserModel.URI) ;

        Map<EventSource,EventSink[]> connections =
                new HashMap<EventSource,EventSink[]>() ;
        EventSource from1 =
                new EventSource(DishwasherUserModel.URI, SwitchOn.class) ;
        EventSink[] to1 =
                new EventSink[] {
                        new EventSink(DishwasherModel.URI, SwitchOn.class)} ;
        connections.put(from1, to1) ;
        EventSource from2 =
                new EventSource(DishwasherUserModel.URI, SwitchOff.class) ;
        EventSink[] to2 = new EventSink[] {
                new EventSink(DishwasherModel.URI, SwitchOff.class)} ;
        connections.put(from2, to2) ;
        EventSource from3 =
                new EventSource(DishwasherUserModel.URI, SetModeEco.class) ;
        EventSink[] to3 = new EventSink[] {
                new EventSink(DishwasherModel.URI, SetModeEco.class)} ;
        connections.put(from3, to3) ;
        EventSource from4 =
                new EventSource(DishwasherUserModel.URI, SetModeStandard.class) ;
        EventSink[] to4 = new EventSink[] {
                new EventSink(DishwasherModel.URI, SetModeStandard.class)} ;
        connections.put(from4, to4) ;

        coupledModelDescriptors.put(
               DishwasherCoupledModel.URI,
                new CoupledHIOA_Descriptor(
                		DishwasherCoupledModel.class,
                		DishwasherCoupledModel.URI,
                        submodels,
                        null,
                        null,
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

