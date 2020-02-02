package simulation.sil.electricmeter.plugin;

import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;
import simulation.sil.electricmeter.models.ElectricMeterModel;

/**
 * The class <code>ElectricMeterSimulatorPlugin</code> implements the simulation
 * plug-in for the component <code>ElectricMeter</code>.
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 */
public class ElectricMeterSimulatorPlugin extends AtomicSimulatorPlugin {
    
    private static final long serialVersionUID = 1L;

    @Override
    public Object getModelStateValue(String modelURI, String name) throws Exception {
        ModelDescriptionI m = this.simulator.getDescendentModel(modelURI) ;
        assert  m instanceof ElectricMeterModel;
        if(name.equals("available")) {
            return ((ElectricMeterModel)m).getAvailableEnergy();
        } else if(name.equals("consumption")) {
            return ((ElectricMeterModel)m).getConsumption();
        } else if(name.equals("production")) {
            return ((ElectricMeterModel)m).getProduction();
        } else {
            throw new RuntimeException();
        }
    }
}
