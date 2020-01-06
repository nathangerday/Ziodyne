package simulation.models.windturbine;

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

public class WindTurbineCoupledModel extends CoupledModel{
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
                AtomicHIOA_Descriptor.create(
                        WindTurbineModel.class,
                        WindTurbineModel.URI,
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
        submodels.add(TicModel.URI) ;
        submodels.add(WindModel.URI) ;
        submodels.add(WindSensorModel.URI) ;
        submodels.add(WindTurbineModel.URI) ;

        return null;
        //        Map<EventSource,EventSink[]> connections =
        //                new HashMap<EventSource,EventSink[]>() ;
        //        EventSource from1 =
        //                new EventSource(LampUserModel.URI, SwitchOn.class) ;
        //        EventSink[] to1 =
        //                new EventSink[] {
        //                        new EventSink(LampModel.URI, SwitchOn.class)} ;
        //        connections.put(from1, to1) ;
        //        EventSource from2 =
        //                new EventSource(LampUserModel.URI, SwitchOff.class) ;
        //        EventSink[] to2 = new EventSink[] {
        //                new EventSink(LampModel.URI, SwitchOff.class)} ;
        //        connections.put(from2, to2) ;
        //        EventSource from3 =
        //                new EventSource(LampUserModel.URI, SetLow.class) ;
        //        EventSink[] to3 = new EventSink[] {
        //                new EventSink(LampModel.URI, SetLow.class)} ;
        //        connections.put(from3, to3) ;
        //        EventSource from4 =
        //                new EventSource(LampUserModel.URI, SetHigh.class) ;
        //        EventSink[] to4 = new EventSink[] {
        //                new EventSink(LampModel.URI, SetHigh.class)} ;
        //        connections.put(from4, to4) ;
        //        EventSource from5 =
        //                new EventSource(LampUserModel.URI, SetMedium.class) ;
        //        EventSink[] to5 = new EventSink[] {
        //                new EventSink(LampModel.URI, SetMedium.class)} ;
        //        connections.put(from5, to5) ;
        //
        //        coupledModelDescriptors.put(
        //               LampCoupledModel.URI,
        //                new CoupledHIOA_Descriptor(
        //                        LampCoupledModel.class,
        //                        LampCoupledModel.URI,
        //                        submodels,
        //                        null,
        //                        null,
        //                        connections,
        //                        null,
        //                        SimulationEngineCreationMode.COORDINATION_ENGINE,
        //                        null,
        //                        null,
        //                        null)) ;
        //
        //        return new Architecture(
        //                LampCoupledModel.URI,
        //                atomicModelDescriptors,
        //                coupledModelDescriptors,
        //                TimeUnit.SECONDS);
    }
}
