package simulation.events.lamp;

import fr.sorbonne_u.cyphy.examples.sg.equipments.hairdryer.models.events.SetHigh;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulation.models.lamp.LampModel;

public class SetLow extends AbstractLampEvent {
    /**
     * create an event from the given time of occurrence and event description.
     *
     * <p><strong>Contract</strong></p>
     *
     * <pre>
     * pre	timeOfOccurrence != null
     * post	this.getTimeOfOccurrence().equals(timeOfOccurrence)
     * post	this.getEventInformation.equals(content)
     * </pre>
     *
     * @param timeOfOccurrence time of occurrence of the created event.
     */
    public SetLow(Time timeOfOccurrence) {
        super(timeOfOccurrence, null);
    }


    /**
     * @see fr.sorbonne_u.devs_simulation.models.events.Event#eventAsString()
     */
    @Override
    public String	eventAsString()
    {
        return "Lamp::SetLow" ;
    }

    /**
     * @see fr.sorbonne_u.devs_simulation.es.events.ES_Event#hasPriorityOver(fr.sorbonne_u.devs_simulation.models.events.EventI)
     */
    @Override
    public boolean			hasPriorityOver(EventI e)
    {
        if (e instanceof SwitchOn  || e instanceof SetHigh || e instanceof SetMedium) {
            return false ;
        } else {
            return true ;
        }
    }

    /**
     * @see fr.sorbonne_u.devs_simulation.models.events.Event#executeOn(fr.sorbonne_u.devs_simulation.models.AtomicModel)
     */
    @Override
    public void	executeOn(AtomicModel model)
    {
        assert	model instanceof LampModel;

        LampModel m = (LampModel)model ;
        if (m.getState() == LampModel.State.HIGH) {
            m.setState(LampModel.State.LOW) ;
        }
    }
}
