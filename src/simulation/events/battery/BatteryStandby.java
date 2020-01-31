package simulation.events.battery;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulation.models.battery.BatteryModel;

public class BatteryStandby extends AbstractBatteryEvent{
    private static final long serialVersionUID = 1L;

    public BatteryStandby(Time timeOfOccurrence){
        super(timeOfOccurrence, null);
    }

    @Override
    public String eventAsString(){
        return "BatteryStandyby(" + this.getTimeOfOccurrence().getSimulatedTime() + ")";
    }

    @Override
    public void executeOn(AtomicModel model) {
        BatteryModel m = (BatteryModel)model ;
        m.setState(BatteryModel.State.STANDBY) ;
    }
}
