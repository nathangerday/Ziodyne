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
import simulation.events.battery.BatteryProducing;
import simulation.events.battery.BatteryStandby;


public class BatteryCoupledModel extends CoupledModel {

    private static final long serialVersionUID = 1L;
    public static final String  URI = "BatteryCoupledModel" ;

    public BatteryCoupledModel(
            String uri,
            TimeUnit simulatedTimeUnit,
            SimulatorI simulationEngine,
            ModelDescriptionI[] submodels,
            Map<Class<? extends EventI>, EventSink[]> imported,
            Map<Class<? extends EventI>, ReexportedEvent> reexported,
            Map<EventSource, EventSink[]> connections,
            Map<StaticVariableDescriptor, VariableSink[]> importedVars,
            Map<VariableSource, StaticVariableDescriptor> reexportedVars,
            Map<VariableSource, VariableSink[]> bindings)throws Exception {
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
        submodels.add(BatteryModel.URI) ;
        submodels.add(BatteryControllerModel.URI) ;

        //*********************************** 
        //Connections Event between submodels
        //***********************************

        Map<EventSource,EventSink[]> connections = new HashMap<EventSource,EventSink[]>() ;
        EventSource from = new EventSource(BatteryControllerModel.URI, BatteryCharging.class) ;
        EventSink[] to = new EventSink[] {new EventSink(BatteryModel.URI, BatteryCharging.class)};
        connections.put(from, to);

        from = new EventSource(BatteryControllerModel.URI, BatteryStandby.class) ;
        to = new EventSink[] {new EventSink(BatteryModel.URI, BatteryStandby.class)};
        connections.put(from, to);

        from = new EventSource(BatteryControllerModel.URI, BatteryProducing.class) ;
        to = new EventSink[] {new EventSink(BatteryModel.URI, BatteryProducing.class)};
        connections.put(from, to);


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
                        null)) ;

        return new Architecture(
                BatteryCoupledModel.URI,
                atomicModelDescriptors,
                coupledModelDescriptors,
                TimeUnit.SECONDS);
    }

}