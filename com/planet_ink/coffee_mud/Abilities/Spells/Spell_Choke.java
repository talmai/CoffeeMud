package com.planet_ink.coffee_mud.Abilities.Spells;

import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;
import com.planet_ink.coffee_mud.utils.*;
import java.util.*;

public class Spell_Choke extends Spell
{
	public Spell_Choke()
	{
		super();
		myID=this.getClass().getName().substring(this.getClass().getName().lastIndexOf('.')+1);
		name="Choke";

		// what the affected mob sees when they
		// bring up their affected list.
		displayText="(Choke spell)";

		quality=Ability.MALICIOUS;

		canAffectCode=Ability.CAN_MOBS;
		canTargetCode=Ability.CAN_MOBS;
		
		canBeUninvoked=true;
		isAutoinvoked=false;

		baseEnvStats().setLevel(16);

		uses=Integer.MAX_VALUE;
		recoverEnvStats();
	}

	public Environmental newInstance()
	{
		return new Spell_Choke();
	}
	public int classificationCode()
	{
		return Ability.SPELL|Ability.DOMAIN_ALTERATION;
	}

	public void affectEnvStats(Environmental affected, EnvStats affectableStats)
	{
		super.affectEnvStats(affected,affectableStats);
		affectableStats.setSensesMask(affectableStats.sensesMask()|EnvStats.CAN_BREATHE);
		affectableStats.setSpeed(Util.div(affectableStats.speed(),2.0));
	}


	public void unInvoke()
	{
		// undo the affects of this spell
		if((affected==null)||(!(affected instanceof MOB)))
			return;
		MOB mob=(MOB)affected;
		super.unInvoke();

		if(canBeUninvoked)
			mob.tell("You begin to breathe easier.");
	}



	public boolean invoke(MOB mob, Vector commands, Environmental givenTarget, boolean auto)
	{
		MOB target=this.getTarget(mob,commands,givenTarget);
		if(target==null) return false;

		// the invoke method for spells receives as
		// parameters the invoker, and the REMAINING
		// command line parameters, divided into words,
		// and added as String objects to a vector.
		if(!super.invoke(mob,commands,givenTarget,auto))
			return false;

		boolean success=profficiencyCheck(0,auto);

		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			invoker=mob;
			FullMsg msg=new FullMsg(mob,target,this,affectType,auto?"":"<S-NAME> incant(s) at <T-NAMESELF>.");
			if(mob.location().okAffect(msg))
			{
				mob.location().send(mob,msg);
				if(!msg.wasModified())
				{
					success=maliciousAffect(mob,target,0,-1);
					if(target.location()==mob.location())
						target.location().show(target,null,Affect.MSG_OK_ACTION,"<S-NAME> start(s) chokeing!");
				}
			}
		}
		else
			return maliciousFizzle(mob,target,"<S-NAME> incant(s) at <T-NAMESELF>, but the spell fizzles.");

		// return whether it worked
		return success;
	}
}
