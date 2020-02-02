package simulation.sil.lamp.models;

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
import simulation.sil.lamp.events.LampConsumption;
import simulation.sil.lamp.events.LampHigh;
import simulation.sil.lamp.events.LampLow;
import simulation.sil.lamp.events.LampMedium;
import simulation.sil.lamp.events.LampOff;

/**
 * The class <code>LampCoupledModel</code> implements the 
 * simulation coupled model for the lamp.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 */
public class LampCoupledModel extends CoupledModel {

    private static final long serialVersionUID = 1L;
    public static final String	URI = "SILLampCoupledModel";

    public LampCoupledModel(
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
        super(uri, simulatedTimeUnit, simulationEngine, submodels, imported, reexported, connections, importedVars, reexportedVars, bindings);
    }

    // -------------------------------------------------------------------------
    // Methods
    // -------------------------------------------------------------------------

    @Override
    public SimulationReportI getFinalReport() throws Exception {
        StandardCoupledModelReport ret =
                new StandardCoupledModelReport(this.getURI());
        for (int i = 0; i < this.submodels.length; i++) {
            ret.addReport(this.submodels[i].getFinalReport());
        }
        return ret;
    }

    public static Architecture build() throws Exception {
        Map<String, AbstractAtomicModelDescriptor> atomicModelDescriptors =
                new HashMap<>();

        atomicModelDescriptors.put(
                LampModel.URI,
                AtomicHIOA_Descriptor.create(
                        LampModel.class,
                        LampModel.URI,
                        TimeUnit.SECONDS,
                        null,
                        SimulationEngineCreationMode.ATOMIC_ENGINE));
        atomicModelDescriptors.put(
                LampUserModel.URI,
                AtomicModelDescriptor.create(
                        LampUserModel.class,
                        LampUserModel.URI,
                        TimeUnit.SECONDS,
                        null,
                        SimulationEngineCreationMode.ATOMIC_ENGINE));

        Map<String, CoupledModelDescriptor> coupledModelDescriptors =
                new HashMap<String,CoupledModelDescriptor>();

        Set<String> submodels = new HashSet<String>();
        submodels.add(LampModel.URI);
        submodels.add(LampUserModel.URI);


        // **************************
        // Reexported events
        // **************************

        Map<Class<? extends EventI>,ReexportedEvent> reexported =
                new HashMap<Class<? extends EventI>,ReexportedEvent>();
        reexported.put(LampConsumption.class,
                new ReexportedEvent(LampModel.URI,LampConsumption.class));

        // **************************
        // Events sub - sub
        // **************************

        Map<EventSource,EventSink[]> connections =
                new HashMap<EventSource,EventSink[]>();
                EventSource from = new EventSource(LampUserModel.URI, LampOff.class);
                EventSink[] to = new EventSink[] {new EventSink(LampModel.URI, LampOff.class)};
                connections.put(from, to);

                from = new EventSource(LampUserModel.URI, LampLow.class);
                to = new EventSink[] {new EventSink(LampModel.URI, LampLow.class)};
                connections.put(from, to);

                from = new EventSource(LampUserModel.URI, LampHigh.class);
                to = new EventSink[] {new EventSink(LampModel.URI, LampHigh.class)};
                connections.put(from, to);

                from = new EventSource(LampUserModel.URI, LampMedium.class);
                to = new EventSink[] {new EventSink(LampModel.URI, LampMedium.class)};
                connections.put(from, to);

                // ****************************
                // Coupled model's construction
                // ****************************

                coupledModelDescriptors.put(
                        LampCoupledModel.URI,
                        new CoupledHIOA_Descriptor(
                                LampCoupledModel.class,
                                LampCoupledModel.URI,
                                submodels,
                                null,
                                reexported,
                                connections,
                                null,
                                SimulationEngineCreationMode.COORDINATION_ENGINE,
                                null,
                                null,
                                null));

                return new Architecture(
                        LampCoupledModel.URI,
                        atomicModelDescriptors,
                        coupledModelDescriptors,
                        TimeUnit.SECONDS);
    }
}
