package net.sf.l2j.gameserver.network.clientpackets;

import java.util.concurrent.TimeUnit;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.events.l2jdev.EventManager;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.ExShowScreenMessage;

public final class DlgAnswer extends L2GameClientPacket
{
	private int _messageId;
	private int _answer;
	private int _requesterId;
	
	@Override
	protected void readImpl()
	{
		_messageId = readD();
		_answer = readD();
		_requesterId = readD();
	}
	
	@Override
	public void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		if (_messageId == SystemMessageId.RESSURECTION_REQUEST_BY_S1.getId() || _messageId == SystemMessageId.DO_YOU_WANT_TO_BE_RESTORED.getId())
			player.reviveAnswer(_answer);
		else if (_messageId == SystemMessageId.S1_WISHES_TO_SUMMON_YOU_FROM_S2_DO_YOU_ACCEPT.getId())
			player.teleportAnswer(_answer, _requesterId);
		else if (_messageId == 1983 && Config.ALLOW_WEDDING)
			player.engageAnswer(_answer);
		else if (_messageId == SystemMessageId.WOULD_YOU_LIKE_TO_OPEN_THE_GATE.getId())
			player.activateGate(_answer, 1);
		else if (_messageId == SystemMessageId.WOULD_YOU_LIKE_TO_CLOSE_THE_GATE.getId())
			player.activateGate(_answer, 0);
		else if (_messageId == SystemMessageId.TOUR.getId())
		{
			if (RequestBypassToServer._activeRequestTeleport == true && _answer == 1)
				RequestBypassToServer.TeleportZone(player, RequestBypassToServer.getX(), RequestBypassToServer.getY(), RequestBypassToServer.getZ());
			
			if (RequestBypassToServer._activeRequestTeleport == true && _answer == 0)
				player.sendPacket(new ExShowScreenMessage(player.getName() + " You must confirm your teleport press Ok!", (int) TimeUnit.SECONDS.toMillis(2)));
			
		}
		else if (_messageId == SystemMessageId.TVT.getId())
		{
			if (EventManager.getInstance()._request == true && _answer == 1)
			{
				EventManager.getInstance().registerPlayer(player);
			}
			
			if (EventManager.getInstance()._request == true && _answer == 0)
			{
				EventManager.getInstance().unregisterPlayer(player);
			}
		}
		
		else if (_messageId == SystemMessageId.S1_OBTAINED_S3_S2.getId())
		{
			if (RequestBypassToServer._activeRequest == true && _answer == 1)
			{
				RequestBypassToServer._activeRequest = false;
				RequestBypassToServer.BuyNow(player, RequestBypassToServer.getItemId());
			}
			
			if (RequestBypassToServer._activeRequest == true && _answer == 0)
			{
				RequestBypassToServer._activeRequest = false;
				player.sendPacket(new ExShowScreenMessage(player.getName() + " You must confirm your purchase press Ok!", (int) TimeUnit.SECONDS.toMillis(5)));
			}
			if (RequestBypassToServer._activeRequestWeapon == true && _answer == 1)
			{
				RequestBypassToServer._activeRequestWeapon = false;
				RequestBypassToServer.BuyWeaponNow(player, RequestBypassToServer.getItemId());
			}
			
			if (RequestBypassToServer._activeRequestWeapon == true && _answer == 0)
			{
				RequestBypassToServer._activeRequestWeapon = false;
				player.sendPacket(new ExShowScreenMessage(player.getName() + " You must confirm your purchase press Ok!", (int) TimeUnit.SECONDS.toMillis(5)));
			}
			if (RequestBypassToServer._activeArmorRequest == true && _answer == 1)
			{
				RequestBypassToServer._activeArmorRequest = false;
				RequestBypassToServer.BuyDonateArmor(player, RequestBypassToServer.getItemId());
			}
			if (RequestBypassToServer._activeArmorRequest == true && _answer == 0)
			{
				RequestBypassToServer._activeArmorRequest = false;
				player.sendPacket(new ExShowScreenMessage(player.getName() + " You must confirm your purchase press Ok!", (int) TimeUnit.SECONDS.toMillis(5)));
				
			}
			
			if (RequestBypassToServer._activeArmorPartRequest == true && _answer == 1)
			{
				RequestBypassToServer.BuyDonateArmorPart(player, RequestBypassToServer.getItemId());
				RequestBypassToServer._activeArmorPartRequest = false;
			}
			if (RequestBypassToServer._activeArmorPartRequest == true && _answer == 0)
			{
				RequestBypassToServer._activeArmorPartRequest = false;
				player.sendPacket(new ExShowScreenMessage(player.getName() + " You must confirm your purchase press Ok!", (int) TimeUnit.SECONDS.toMillis(5)));
				
			}
			
			
			if (RequestBypassToServer._activeRequestJewel == true && _answer == 1)
			{
				RequestBypassToServer.BuyDonateJewel(player, RequestBypassToServer.getItemId());
				RequestBypassToServer._activeRequestJewel = false;
			}
			if (RequestBypassToServer._activeRequestJewel == true && _answer == 0)
			{
				RequestBypassToServer._activeRequestJewel = false;
				player.sendPacket(new ExShowScreenMessage(player.getName() + " You must confirm your purchase press Ok!", (int) TimeUnit.SECONDS.toMillis(5)));
				
			}
			
		}
		
	}
}