package simulation.sil.dishwasher.plugin;

import java.util.Map;

import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;
import simulation.sil.dishwasher.models.DishwasherModel;

/**
 * The class <code>DishWasherSimulatorPlugin</code> implements the simulation
 * plug-in for the component <code>Dishwasher</code>.
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 */
public class DishWasherSimulatorPlugin extends AtomicSimulatorPlugin{

    private static final long serialVersionUID = 1L;

    @Override
    public void setSimulationRunParameters(
            Map<String, Object> simParams
            ) throws Exception {
        simParams.put(DishwasherModel.COMPONENT_REF, this.owner) ;
        super.setSimulationRunParameters(simParams) ;
        simParams.remove(DishwasherModel.COMPONENT_REF) ;
    }

    @Override
    public Object getModelStateValue(String modelURI, String name) throws Exception {
        ModelDescriptionI m = this.simulator.getDescendentModel(modelURI) ;
        assert  m instanceof DishwasherModel;
        if(name.equals("time")) {
            return ((DishwasherModel)m).getTimeLeft();
        } else {
            throw new RuntimeException();
        }
    }
}
