package net.sf.l2j.gameserver.model.zone.type;

import net.sf.l2j.gameserver.enums.MessageType;
import net.sf.l2j.gameserver.enums.ZoneId;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.group.Party;
import net.sf.l2j.gameserver.model.zone.type.subtype.SpawnZoneType;

/**
 * @author BAN - L2JDev
 */
public class PartyFarmZone extends SpawnZoneType
{
	public static String _StringName;
	
	public PartyFarmZone(int id)
	{
		super(id);
	}
	
	@Override
	public void setParameter(String name, String value)
	{
		if (name.equals("ZoneName"))
			_StringName = value;
		else
			super.setParameter(name, value);
	}
	
	@Override
	protected void onEnter(Creature character)
	{
		character.setInsideZone(ZoneId.PARTYFARM, true);
		
		if (character instanceof Player)
		{
			
			if (character instanceof Player)
				((Player) character).sendMessage("" + character.getName() + " " + "Entered " + StringName() + " Combat Zone.");
			
			Party party = character.getParty();
			if (party != null)
			{
				// If a party is in progress, leave it
				if (character.getParty() != null)
					party.removePartyMember((Player) character, MessageType.LEFT);
			}

		}
	}
	
	@Override
	protected void onExit(Creature character)
	{
		character.setInsideZone(ZoneId.PARTYFARM, false);
		
		if (character instanceof Player)
			((Player) character).sendMessage("" + character.getName() + " " + "Left " + StringName() + " Combat Zone.");
	}
	
	public static String StringName()
	{
		return _StringName;
	}
}
