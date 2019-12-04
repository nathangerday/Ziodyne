package connectors;

import components.BatteryState;
import fr.sorbonne_u.components.connectors.AbstractConnector;
import interfaces.BatteryControllerI;
import interfaces.BatteryI;

public class BatteryConnector extends AbstractConnector implements BatteryControllerI {

    public void switchOn() throws Exception {
        ((BatteryI)this.offering).switchOn();
    }

    @Override
    public int getEnergyProduced() throws Exception {
        return ((BatteryI)this.offering).getEnergyProduced();
    }

    @Override
    public int getMaxCapacity() throws Exception {
        return ((BatteryI)this.offering).getMaxCapacity();
    }

    @Override
    public int getCurrentCapacity() throws Exception {
        return ((BatteryI)this.offering).getCurrentCapacity();
    }

    @Override
    public void setMode(BatteryState mode) throws Exception {
        ((BatteryI)this.offering).setMode(mode);
    }
}
