package net.sf.l2j.gameserver.network.clientpackets;

import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.WorldObject;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.entity.Duel.DuelState;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;

public final class Action extends L2GameClientPacket
{
	private int _objectId;
	private int _actionId;
	
	@Override
	protected void readImpl()
	{
		_objectId = readD();
		readD(); // originX
		readD(); // originY
		readD(); // originZ
		_actionId = readC();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		if (player.isInObserverMode())
		{
			player.sendPacket(SystemMessageId.OBSERVERS_CANNOT_PARTICIPATE);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (player.getActiveRequester() != null)
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		final WorldObject target = (player.getTargetId() == _objectId) ? player.getTarget() : World.getInstance().getObject(_objectId);
		if (target == null)
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		final Player targetPlayer = target.getActingPlayer();
		if (targetPlayer != null && targetPlayer.getDuelState() == DuelState.DEAD)
		{
			player.sendPacket(SystemMessageId.OTHER_PARTY_IS_FROZEN);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		switch (_actionId)
		{
			
			case 0:
				target.onAction(player, false, false);
				break;
			
			case 1:
				target.onActionShift(player);
				break;
			
			default:
				// Invalid action detected (probably client cheating), log this
				LOGGER.warn(player.getName() + " requested invalid action: " + _actionId);
				player.sendPacket(ActionFailed.STATIC_PACKET);
				break;
		}
	}
	
	@Override
	protected boolean triggersOnActionRequest()
	{
		return false;
	}
}