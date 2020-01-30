package simulation.sil.fridge.plugin;

import java.util.Map;

import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;
import simulation.sil.fridge.models.FridgeModel;

public class FridgeSimulatorPlugin extends AtomicSimulatorPlugin {
    
    private static final long serialVersionUID = 1L;

    @Override
    public void setSimulationRunParameters(
            Map<String, Object> simParams
            ) throws Exception {
        simParams.put(FridgeModel.COMPONENT_REF, this.owner) ;
        super.setSimulationRunParameters(simParams) ;
        simParams.remove(FridgeModel.COMPONENT_REF) ;
    }

    @Override
    public Object getModelStateValue(String modelURI, String name) throws Exception {
        ModelDescriptionI m = this.simulator.getDescendentModel(modelURI) ;
        assert  m instanceof FridgeModel;
        if(name.equals("power")) {
            return ((FridgeModel)m).getPower();
        }
        throw new RuntimeException();
    }
}
