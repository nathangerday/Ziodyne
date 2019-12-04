package fr.sorbonne_u.alasca.testeasyrules;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to provide
// examples to students in courses.
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

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.core.DefaultRulesEngine;

//------------------------------------------------------------------------------
/**
 * The class <code>TestEasyRules</code> illustrates the use of the "Easy Rules"
 * inference engine.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 * <p>Created on : 2019-11-15</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			TestEasyRules
{
	// For Easy Rules, facts are simply values associated to URIs (strings)
	// that identifies them.

	/** the URI of the fact asserting the current indoor temperature.		*/
	public static final String	INDOOR_TEMPERATURE = "indoor temperature" ;
	/** the URI of the fact asserting what is a indoor temperature.			*/
	public static final String	HIGH_INDOOR_TEMPERATURE =
												"high indoor temperature" ;
	/** the URI of the fact asserting the value of the current energy
	 *  production margin i.e., currently unused.							*/
	public static final String	CURRENT_PRODUCTION_MARGIN =
												"current production margin" ;
	/** the URI of the fact asserting that the refrigerator must be
	 *  suspended.															*/
	public static final String	SUSPEND_REFRIGERATOR = "suspend refrigerator" ;

	public static void	main(String[] args)
	{
		// Creating a new default rule engine
		RulesEngine re = new DefaultRulesEngine() ;
		// Show the parameters of the rule engine i.e., controlling its
		// inference behaviour.
		System.out.println(re.getParameters()) ;
		// Creating a new rule set
		Rules rules = new Rules() ;
		// Rules in Easy Rules are defined as classes annotated by @Rule and
		// they must be registered in a rule before being applied by a rule
		// engine
		rules.register(new HighIndoorTemperature()) ;
		rules.register(new SuspendRefrigerator()) ;
		// Facts in Easy Rules are defined as a mapping of fact URIs to fact
		// values and put in a fact set.
		Facts facts = new Facts() ;
		facts.put(INDOOR_TEMPERATURE, 35.0) ;
		facts.put(CURRENT_PRODUCTION_MARGIN, 1000.0) ;
		// Show the current facts
		System.out.println(facts.toString()) ;
		// Fire rules for one inference.
		re.fire(rules, facts) ;
		// Show the current facts
		System.out.println(facts.toString()) ;
		// Fire rules again for another inference.
		re.fire(rules, facts) ;
		// Show the current facts
		System.out.println(facts.toString()) ;
	}
}
//------------------------------------------------------------------------------
