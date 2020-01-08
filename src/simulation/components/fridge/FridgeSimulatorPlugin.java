package simulation.components.fridge;

import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;
import simulation.models.fridge.FridgeModel;

public class FridgeSimulatorPlugin extends AtomicSimulatorPlugin{
	 @Override
	    public Object		getModelStateValue(String modelURI, String name)
	            throws Exception
	    {
	        // Get a Java reference on the object representing the corresponding
	        // simulation model.
	        ModelDescriptionI m = this.simulator.getDescendentModel(modelURI) ;
	        // The only model in this example that provides access to some value
	        // is the HairDryerModel.
	        assert	m instanceof FridgeModel;
	        // The following is the implementation of the protocol converting
	        // names used by the caller to the values provided by the model;
	        // alternatively, the simulation model could take care of the
	        // link between names and values.
	        if (name.equals("state")) {
	            return ((FridgeModel)m).getStateFridge() ;
	        }
	        else if(name.equals("temperature-fridge")) {
	        	return ((FridgeModel)m).getFridgeTemperature() ;
	        }
	        else {
	            assert	name.equals("temperature-freezer") ;
	            return ((FridgeModel)m).getFreezerTemperature() ;
	        }
	    }
}
