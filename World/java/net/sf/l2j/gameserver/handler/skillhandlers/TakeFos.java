package net.sf.l2j.gameserver.handler.skillhandlers;

import net.sf.l2j.gameserver.enums.skills.SkillType;
import net.sf.l2j.gameserver.handler.ISkillHandler;
import net.sf.l2j.gameserver.model.WorldObject;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.instance.HolyThing;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.skills.L2Skill;

public class TakeFos implements ISkillHandler
{

	private static final SkillType[] SKILL_IDS =
	{
		SkillType.TAKE_FOS
	};
	
	@Override
	public void useSkill(Creature activeChar, L2Skill skill, WorldObject[] targets, ItemInstance itemInstance)
	{
		if (!(activeChar instanceof Player))
			return;
		if (targets.length == 0)
			return;
		WorldObject object = targets[0];
		
		if (object instanceof HolyThing)
		{
	
		}
	}
	
	@Override
	public SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
	
}