package simulation.models.fridge;

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
import simulation.events.fridge.FreezerOn;
import simulation.events.fridge.FreezerOff;
import simulation.events.fridge.FridgeOn;
import simulation.events.fridge.TicEvent;
import simulation.events.fridge.FridgeOff;

public class FridgeCoupledModel extends CoupledModel{

    private static final long serialVersionUID = 1L;
    public static final String  URI = "FridgeCoupledModel" ;

    public FridgeCoupledModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine,
            ModelDescriptionI[] submodels, Map<Class<? extends EventI>, EventSink[]> imported,
            Map<Class<? extends EventI>, ReexportedEvent> reexported, Map<EventSource, EventSink[]> connections,
            Map<StaticVariableDescriptor, VariableSink[]> importedVars,
            Map<VariableSource, StaticVariableDescriptor> reexportedVars, Map<VariableSource, VariableSink[]> bindings)
                    throws Exception {
        super(uri, simulatedTimeUnit, simulationEngine, submodels, imported, reexported, connections, importedVars,
                reexportedVars, bindings);
    }

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

    public static Architecture build() throws Exception
    {
        Map<String, AbstractAtomicModelDescriptor> atomicModelDescriptors =
                new HashMap<>() ;

        atomicModelDescriptors.put(
                FridgeModel.URI,
                AtomicHIOA_Descriptor.create(
                        FridgeModel.class,
                        FridgeModel.URI,
                        TimeUnit.SECONDS,
                        null,
                        SimulationEngineCreationMode.ATOMIC_ENGINE)) ;
        atomicModelDescriptors.put(
                FridgeUserModel.URI,
                AtomicModelDescriptor.create(
                        FridgeUserModel.class,
                        FridgeUserModel.URI,
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

        Map<String, CoupledModelDescriptor> coupledModelDescriptors =
                new HashMap<String,CoupledModelDescriptor>() ;

        Set<String> submodels = new HashSet<String>() ;
        submodels.add(FridgeModel.URI) ;
        submodels.add(FridgeUserModel.URI) ;
        submodels.add(TicModel.URI);

        Map<EventSource,EventSink[]> connections = new HashMap<EventSource,EventSink[]>() ;
        EventSource from1 = new EventSource(FridgeUserModel.URI, FreezerOn.class) ;
        EventSink[] to1 = new EventSink[] {new EventSink(FridgeModel.URI, FreezerOn.class)} ;
        connections.put(from1, to1) ;

        EventSource from2 = new EventSource(FridgeUserModel.URI, FreezerOff.class) ;
        EventSink[] to2 = new EventSink[] {new EventSink(FridgeModel.URI, FreezerOff.class)} ;
        connections.put(from2, to2) ;

        EventSource from3 = new EventSource(FridgeUserModel.URI, FridgeOn.class) ;
        EventSink[] to3 = new EventSink[] {new EventSink(FridgeModel.URI, FridgeOn.class)} ;
        connections.put(from3, to3) ;

        EventSource from4 = new EventSource(FridgeUserModel.URI, FridgeOff.class) ;
        EventSink[] to4 = new EventSink[] {new EventSink(FridgeModel.URI, FridgeOff.class)} ;
        connections.put(from4, to4) ;
        
        EventSource from5 = new EventSource(TicModel.URI, TicEvent.class) ;
        EventSink[] to5 = new EventSink[] {new EventSink(FridgeModel.URI, TicEvent.class)} ;
        connections.put(from5, to5) ;

        coupledModelDescriptors.put(
                FridgeCoupledModel.URI,
                new CoupledHIOA_Descriptor(
                        FridgeCoupledModel.class,
                        FridgeCoupledModel.URI,
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
                FridgeCoupledModel.URI,
                atomicModelDescriptors,
                coupledModelDescriptors,
                TimeUnit.SECONDS);
    }
}
