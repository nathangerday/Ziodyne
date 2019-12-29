package simulation.components.lamp;

import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;
import simulation.models.lamp.LampModel;

public class LampSimulatorPlugin  extends AtomicSimulatorPlugin {


    /**
     * @see fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin#getModelStateValue(java.lang.String, java.lang.String)
     */
    @Override
    public Object		getModelStateValue(String modelURI, String name)
            throws Exception
    {
        // Get a Java reference on the object representing the corresponding
        // simulation model.
        ModelDescriptionI m = this.simulator.getDescendentModel(modelURI) ;
        // The only model in this example that provides access to some value
        // is the LampModel.
        assert	m instanceof LampModel;
        // The following is the implementation of the protocol converting
        // names used by the caller to the values provided by the model;
        // alternatively, the simulation model could take care of the
        // link between names and values.
        if (name.equals("state")) {
            return ((LampModel)m).getState() ;
        } else {
            assert	name.equals("intensity") ;
            return ((LampModel)m).getIntensity() ;
        }
    }
}
