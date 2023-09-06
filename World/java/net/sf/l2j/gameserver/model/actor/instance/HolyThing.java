package net.sf.l2j.gameserver.model.actor.instance;

import net.sf.l2j.gameserver.data.SkillTable;
import net.sf.l2j.gameserver.events.l2jdev.EventManager;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.network.serverpackets.MagicSkillUse;
import net.sf.l2j.gameserver.skills.L2Skill;

public final class HolyThing extends Folk
{
	public HolyThing(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public boolean isAttackableBy(Creature attacker)
	{
		return false;
	}
	
	@Override
	public void reduceCurrentHp(double damage, Creature attacker, L2Skill skill)
	{
	}
	
	@Override
	public void reduceCurrentHp(double damage, Creature attacker, boolean awake, boolean isDOT, L2Skill skill)
	{
	}
	
	@Override
	public void onInteract(Player player)
	{
		if (getNpcId() == EventManager.getInstance().getCurrentEvent().getInt("ArtifactNpcId"))
		{
			L2Skill skill = SkillTable.getInstance().getInfo(EventManager.getInstance().getCurrentEvent().getInt("SealFortressSkill"), EventManager.getInstance().getCurrentEvent().getInt("SealFortressSkillLevel"));
			player.broadcastPacketInRadius(new MagicSkillUse(player, player, skill.getId(), skill.getLevel(), skill.getHitTime(), skill.getReuseDelay()), 3000);
			
			player.getAI().tryToCast((Creature) player.getTarget(), skill);
		}
	}
}
