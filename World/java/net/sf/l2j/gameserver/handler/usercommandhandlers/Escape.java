package net.sf.l2j.gameserver.handler.usercommandhandlers;

import net.sf.l2j.gameserver.data.SkillTable;
import net.sf.l2j.gameserver.enums.ZoneId;
import net.sf.l2j.gameserver.events.l2jdev.EventManager;
import net.sf.l2j.gameserver.handler.IUserCommandHandler;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;
import net.sf.l2j.gameserver.skills.L2Skill;

public class Escape implements IUserCommandHandler
{
	private static final int[] COMMAND_IDS =
	{
		52
	};
	
	@Override
	public void useUserCommand(int id, Player player)
	{
		if (EventManager.getInstance().isRegistered(player) || player.isInOlympiadMode() || player.isInObserverMode() || player.isFestivalParticipant() || player.isInJail() || player.isInsideZone(ZoneId.BOSS))
		{
			player.sendPacket(SystemMessageId.NO_UNSTUCK_PLEASE_SEND_PETITION);
			return;
		}
		
		// Official timer 5 minutes, for GM 1 second
		if (player.isGM())
			player.getAI().tryToCast(player, 2100, 1);
		else
		{
			int unstuckTimer = 30 * 1000;
			L2Skill skill = SkillTable.getInstance().getInfo(2099, 1);
			skill.getCoolTime();
			player.getAI().tryToCast(player, skill);
			if (unstuckTimer < 30)
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_S2).addString("You will unstuck in " + 30 + " seconds."));
			else
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_S2).addString("You will unstuck i " + 30 + " seconds."));

		}
	}
	
	@Override
	public int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}
}