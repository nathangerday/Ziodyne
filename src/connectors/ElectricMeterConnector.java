package connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import interfaces.ElectricMeterControllerI;
import interfaces.ElectricMeterI;

public class ElectricMeterConnector extends AbstractConnector implements ElectricMeterControllerI {

    @Override
    public double getAvailableEnergy() throws Exception {
        return ((ElectricMeterI)this.offering).getAvailableEnergy();
    }

    @Override
    public double getProduction() throws Exception {
        return ((ElectricMeterI)this.offering).getProduction();
    }

    @Override
    public double getConsumption() throws Exception{
        return ((ElectricMeterI)this.offering).getConsumption();
    }
}
