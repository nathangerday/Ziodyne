package simulation.overall;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to provide an example
// for the extension of the BCM component model that aims to define a components
// tailored for cyber-physical control systems (CPCS) for Java.
//
// This software is governed by the CeCILL-C license under French law and
// abiding by the rules of distribution of free software.  You can use,
// modify and/ or redistribute the software under the terms of the
// CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
// URL "http://www.cecill.info".
//
// As a counterpart to the access to the source code and  rights to copy,
// modify and redistribute granted by the license, users are provided only
// with a limited warranty  and the software's author,  the holder of the
// economic rights,  and the successive licensors  have only  limited
// liability. 
//
// In this respect, the user's attention is drawn to the risks associated
// with loading,  using,  modifying and/or developing or reproducing the
// software by the user in light of its specific status of free software,
// that may mean  that it is complicated to manipulate,  and  that  also
// therefore means  that it is reserved for developers  and  experienced
// professionals having in-depth computer knowledge. Users are therefore
// encouraged to load and test the software's suitability as regards their
// requirements in conditions enabling the security of their systems and/or 
// data to be ensured and,  more generally, to use and operate it in the 
// same conditions as regards security. 
//
// The fact that you are presently reading this means that you have had
// knowledge of the CeCILL-C license and that you accept its terms.

import java.util.Map;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;
import fr.sorbonne_u.devs_simulation.models.CoupledModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.events.ReexportedEvent;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;

// -----------------------------------------------------------------------------
/**
 * The class <code>SGCoupledModel</code> implements the overall coupled model
 * for the household energy management example.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 */
public class SGCoupledModel extends	CoupledModel {

    private static final long serialVersionUID = 1L;
    public static final String	URI = "SGCoupledModel" ;
    
    /**
     * Constructs the SGCoupledModel
     * @param uri the uri for the coupledModel
     * @param simulatedTimeUnit time unit of the simulation
     * @param simulationEngine engine for the simulation
     * @param submodels array of all submodels
     * @param imported map of imported events
     * @param reexported map of reexported events
     * @param connections map of event connections
     * @throws Exception
     */
    public SGCoupledModel(
            String uri,
            TimeUnit simulatedTimeUnit,
            SimulatorI simulationEngine,
            ModelDescriptionI[] submodels,
            Map<Class<? extends EventI>,
            EventSink[]> imported,
            Map<Class<? extends EventI>,
            ReexportedEvent> reexported,
            Map<EventSource,EventSink[]> connections
            ) throws Exception{
        super(uri, simulatedTimeUnit, simulationEngine, submodels,
                imported, reexported, connections) ;
    }
}
