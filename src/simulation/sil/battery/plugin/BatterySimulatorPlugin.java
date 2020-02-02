package simulation.sil.battery.plugin;

import java.util.Map;

import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;
import simulation.sil.battery.models.BatteryModel;


/**
 * The class <code>BatterySimulatorPlugin</code> implements the simulation
 * plug-in for the component <code>Battery</code>.
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 */
public class BatterySimulatorPlugin extends AtomicSimulatorPlugin {
    private static final long serialVersionUID = 1L;

    @Override
    public void setSimulationRunParameters(
            Map<String, Object> simParams
            ) throws Exception {
        simParams.put(BatteryModel.COMPONENT_REF, this.owner) ;
        super.setSimulationRunParameters(simParams) ;
        simParams.remove(BatteryModel.COMPONENT_REF) ;
    }

    @Override
    public Object getModelStateValue(String modelURI, String name) throws Exception {
        ModelDescriptionI m = this.simulator.getDescendentModel(modelURI) ;
        assert  m instanceof BatteryModel;
        if(name.equals("capacity")) {
            return ((BatteryModel)m).getCapacity();
        } else if (name.equals("max capacity")) {
            return ((BatteryModel)m).getMaxCapacity();
        }
        throw new RuntimeException();
    }
}
