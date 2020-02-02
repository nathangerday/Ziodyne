package simulation.sil.windturbine.plugin;

import java.util.Map;

import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;
import simulation.sil.windturbine.models.WindTurbineModel;

/**
 * The class <code>WindTurbineSimulatorPlugin</code> implements the simulation
 * plug-in for the component <code>WindTurbine</code>.
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 */
public class WindTurbineSimulatorPlugin extends AtomicSimulatorPlugin {

    private static final long serialVersionUID = 1L;

    @Override
    public void setSimulationRunParameters(
            Map<String, Object> simParams
            ) throws Exception {
        simParams.put(WindTurbineModel.COMPONENT_REF, this.owner) ;
        super.setSimulationRunParameters(simParams) ;
        simParams.remove(WindTurbineModel.COMPONENT_REF) ;
    }

    @Override
    public Object getModelStateValue(String modelURI, String name) throws Exception {
        ModelDescriptionI m = this.simulator.getDescendentModel(modelURI) ;
        assert  m instanceof WindTurbineModel;
        if(name.equals("speed")) {
            return ((WindTurbineModel)m).getSpeed();
        }
        throw new RuntimeException();
    }
}
