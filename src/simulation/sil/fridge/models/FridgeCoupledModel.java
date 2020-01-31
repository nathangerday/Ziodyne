package simulation.sil.fridge.models;

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
import simulation.sil.fridge.events.FreezerClose;
import simulation.sil.fridge.events.FreezerOpen;
import simulation.sil.fridge.events.FridgeClose;
import simulation.sil.fridge.events.FridgeConsumption;
import simulation.sil.fridge.events.FridgeOpen;

public class FridgeCoupledModel extends CoupledModel{

    private static final long serialVersionUID = 1L;
    public static final String  URI = "SILFridgeCoupledModel" ;

    public FridgeCoupledModel(
            String uri,
            TimeUnit simulatedTimeUnit,
            SimulatorI simulationEngine,
            ModelDescriptionI[] submodels,
            Map<Class<? extends EventI>, EventSink[]> imported,
            Map<Class<? extends EventI>, ReexportedEvent> reexported,
            Map<EventSource, EventSink[]> connections,
            Map<StaticVariableDescriptor, VariableSink[]> importedVars,
            Map<VariableSource, StaticVariableDescriptor> reexportedVars,
            Map<VariableSource, VariableSink[]> bindings)
                    throws Exception {
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

        Map<String, CoupledModelDescriptor> coupledModelDescriptors =
                new HashMap<String,CoupledModelDescriptor>() ;

        Set<String> submodels = new HashSet<String>() ;
        submodels.add(FridgeModel.URI) ;
        submodels.add(FridgeUserModel.URI);

        //*********************************** 
        //Reexported events
        //***********************************
        
        Map<Class<? extends EventI>,ReexportedEvent> reexported =
                new HashMap<Class<? extends EventI>,ReexportedEvent>();
        reexported.put(FridgeConsumption.class,
                new ReexportedEvent(FridgeModel.URI,FridgeConsumption.class));
        
        //*********************************** 
        //Connections Event between submodels
        //***********************************
        
        Map<EventSource,EventSink[]> connections = new HashMap<EventSource,EventSink[]>() ;

        EventSource from5 = new EventSource(FridgeUserModel.URI, FridgeClose.class) ;
        EventSink[] to5 = new EventSink[] {new EventSink(FridgeModel.URI, FridgeClose.class)} ;
        connections.put(from5, to5) ;

        EventSource from6 = new EventSource(FridgeUserModel.URI, FridgeOpen.class) ;
        EventSink[] to6 = new EventSink[] {new EventSink(FridgeModel.URI, FridgeOpen.class)} ;
        connections.put(from6, to6) ;

        EventSource from7 = new EventSource(FridgeUserModel.URI, FreezerClose.class) ;
        EventSink[] to7 = new EventSink[] {new EventSink(FridgeModel.URI, FreezerClose.class)} ;
        connections.put(from7, to7) ;

        EventSource from8 = new EventSource(FridgeUserModel.URI, FreezerOpen.class) ;
        EventSink[] to8 = new EventSink[] {new EventSink(FridgeModel.URI, FreezerOpen.class)} ;
        connections.put(from8, to8) ;
        
        coupledModelDescriptors.put(
                FridgeCoupledModel.URI,
                new CoupledHIOA_Descriptor(
                        FridgeCoupledModel.class,
                        FridgeCoupledModel.URI,
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
                FridgeCoupledModel.URI,
                atomicModelDescriptors,
                coupledModelDescriptors,
                TimeUnit.SECONDS);
    }
}
