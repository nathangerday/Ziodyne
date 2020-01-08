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
import simulation.events.fridge.LowerFreezerTemp;
import simulation.events.fridge.LowerFridgeTemp;
import simulation.events.fridge.RaiseFreezerTemp;
import simulation.events.fridge.RaiseFridgeTemp;
import simulation.events.fridge.SwitchOff;
import simulation.events.fridge.SwitchOn;

public class FridgeCoupledModel extends CoupledModel{
	
	public FridgeCoupledModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine,
			ModelDescriptionI[] submodels, Map<Class<? extends EventI>, EventSink[]> imported,
			Map<Class<? extends EventI>, ReexportedEvent> reexported, Map<EventSource, EventSink[]> connections,
			Map<StaticVariableDescriptor, VariableSink[]> importedVars,
			Map<VariableSource, StaticVariableDescriptor> reexportedVars, Map<VariableSource, VariableSink[]> bindings)
			throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine, submodels, imported, reexported, connections, importedVars,
				reexportedVars, bindings);
		// TODO Auto-generated constructor stub
	}

	public static final String	URI = "FridgeCoupledModel" ;

	
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
	        submodels.add(FridgeUserModel.URI) ;

	        Map<EventSource,EventSink[]> connections =
	                new HashMap<EventSource,EventSink[]>() ;
	        EventSource from1 =
	                new EventSource(FridgeUserModel.URI, SwitchOn.class) ;
	        EventSink[] to1 =
	                new EventSink[] {
	                        new EventSink(FridgeModel.URI, SwitchOn.class)} ;
	        connections.put(from1, to1) ;
	        EventSource from2 =
	                new EventSource(FridgeUserModel.URI, SwitchOff.class) ;
	        EventSink[] to2 = new EventSink[] {
	                new EventSink(FridgeModel.URI, SwitchOff.class)} ;
	        connections.put(from2, to2) ;
	        EventSource from3 =
	                new EventSource(FridgeUserModel.URI, RaiseFreezerTemp.class) ;
	        EventSink[] to3 = new EventSink[] {
	                new EventSink(FridgeModel.URI, RaiseFreezerTemp.class)} ;
	        connections.put(from3, to3) ;
	        EventSource from4 =
	                new EventSource(FridgeUserModel.URI, RaiseFridgeTemp.class) ;
	        EventSink[] to4 = new EventSink[] {
	                new EventSink(FridgeModel.URI, RaiseFridgeTemp.class)} ;
	        connections.put(from4, to4) ;
	        EventSource from5 =
	                new EventSource(FridgeUserModel.URI, LowerFreezerTemp.class) ;
	        EventSink[] to5 = new EventSink[] {
	                new EventSink(FridgeModel.URI, LowerFreezerTemp.class)} ;
	        connections.put(from5, to5) ;
	        EventSource from6 =
	                new EventSource(FridgeUserModel.URI, LowerFridgeTemp.class) ;
	        EventSink[] to6 = new EventSink[] {
	                new EventSink(FridgeModel.URI, LowerFridgeTemp.class)} ;
	        connections.put(from6, to6) ;

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
