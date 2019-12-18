package simulation.models.lamp;

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
import simulation.events.lamp.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class LampCoupledModel extends CoupledModel {

    // -------------------------------------------------------------------------
    // Constants and variables
    // -------------------------------------------------------------------------
    public static final String	URI = "LampCoupledModel" ;


    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------
    public LampCoupledModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine, ModelDescriptionI[] submodels, Map<Class<? extends EventI>, EventSink[]> imported, Map<Class<? extends EventI>, ReexportedEvent> reexported, Map<EventSource, EventSink[]> connections, Map<StaticVariableDescriptor, VariableSink[]> importedVars, Map<VariableSource, StaticVariableDescriptor> reexportedVars, Map<VariableSource, VariableSink[]> bindings) throws Exception {
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
                LampModel.URI,
                AtomicHIOA_Descriptor.create(
                        LampModel.class,
                        LampModel.URI,
                        TimeUnit.SECONDS,
                        null,
                        SimulationEngineCreationMode.ATOMIC_ENGINE)) ;
        atomicModelDescriptors.put(
                LampUserModel.URI,
                AtomicModelDescriptor.create(
                        LampUserModel.class,
                        LampUserModel.URI,
                        TimeUnit.SECONDS,
                        null,
                        SimulationEngineCreationMode.ATOMIC_ENGINE)) ;

        Map<String, CoupledModelDescriptor> coupledModelDescriptors =
                new HashMap<String,CoupledModelDescriptor>() ;

        Set<String> submodels = new HashSet<String>() ;
        submodels.add(LampModel.URI) ;
        submodels.add(LampUserModel.URI) ;

        Map<EventSource,EventSink[]> connections =
                new HashMap<EventSource,EventSink[]>() ;
        EventSource from1 =
                new EventSource(LampUserModel.URI, SwitchOn.class) ;
        EventSink[] to1 =
                new EventSink[] {
                        new EventSink(LampModel.URI, SwitchOn.class)} ;
        connections.put(from1, to1) ;
        EventSource from2 =
                new EventSource(LampUserModel.URI, SwitchOff.class) ;
        EventSink[] to2 = new EventSink[] {
                new EventSink(LampModel.URI, SwitchOff.class)} ;
        connections.put(from2, to2) ;
        EventSource from3 =
                new EventSource(LampUserModel.URI, SetLow.class) ;
        EventSink[] to3 = new EventSink[] {
                new EventSink(LampModel.URI, SetLow.class)} ;
        connections.put(from3, to3) ;
        EventSource from4 =
                new EventSource(LampUserModel.URI, SetHigh.class) ;
        EventSink[] to4 = new EventSink[] {
                new EventSink(LampModel.URI, SetHigh.class)} ;
        connections.put(from4, to4) ;
        EventSource from5 =
                new EventSource(LampUserModel.URI, SetMedium.class) ;
        EventSink[] to5 = new EventSink[] {
                new EventSink(LampModel.URI, SetMedium.class)} ;
        connections.put(from4, to4) ;

        coupledModelDescriptors.put(
               LampCoupledModel.URI,
                new CoupledHIOA_Descriptor(
                        LampCoupledModel.class,
                        LampCoupledModel.URI,
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
                LampCoupledModel.URI,
                atomicModelDescriptors,
                coupledModelDescriptors,
                TimeUnit.SECONDS);
    }
}

