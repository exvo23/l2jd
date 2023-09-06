package net.sf.l2j.gameserver.communitybbs.manager.custom;

import java.util.List;
import java.util.StringTokenizer;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.communitybbs.manager.BaseBBSManager;
import net.sf.l2j.gameserver.data.cache.HtmCache;
import net.sf.l2j.gameserver.data.manager.CastleManager;
import net.sf.l2j.gameserver.data.xml.TeleportData;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.entity.Castle;
import net.sf.l2j.gameserver.model.location.TeleportLocation;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;

/**
 * @author BAN L2JDEV
 */
public class TeleportCBManager extends BaseBBSManager
{
	protected TeleportCBManager()
	{
	}
	
	@Override
	public void parseCmd(String command, Player player)
	{
		if (command.startsWith("_cbGoto"))
		{
			try
			{
				final StringTokenizer st = new StringTokenizer(command, " ");
				st.nextToken();
				
				teleport(player, Integer.parseInt(st.nextToken()));

			}
			catch (final Exception e)
			{
				player.sendPacket(ActionFailed.STATIC_PACKET);
			}
			player.sendPacket(ActionFailed.STATIC_PACKET);
		}
		else if (command.startsWith("_cbLink"))
		{
			final StringTokenizer st = new StringTokenizer(command, " ");
			st.nextToken();
			
			int val = Integer.parseInt(st.nextToken());
			
			showGK(player, val);
		
			
			
		}
		else if (command.startsWith("_cbPage_Shop"))
		{
			final StringTokenizer st = new StringTokenizer(command, " ");
			st.nextToken();
			
			int val = Integer.parseInt(st.nextToken());
			
			showGMLink(player, val);
		}
		else
			super.parseCmd(command, player);
		
	}
	
	private static void showGMLink(Player activeChar, int val)
	{
		
		String content = HtmCache.getInstance().getHtm(CB_PATH + "custom/GlobalShop/50002" + "-" + val + ".htm");
		content = content.replaceAll("%name%", activeChar.getName());
		
		separateAndSend(content, activeChar);
		BaseBBSManager.separateAndSend(content, activeChar);
	}
	
	private static void showGK(Player activeChar, int val)
	{
		
		String content = HtmCache.getInstance().getHtm(CB_PATH + "custom/Teleport/50001" + "-" + val + ".htm");
		content = content.replaceAll("%name%", activeChar.getName());
		
		separateAndSend(content, activeChar);
		BaseBBSManager.separateAndSend(content, activeChar);
	}
	
	/**
	 * @param player : The {@link Player} to test.
	 * @return True if the teleport is possible, false otherwise.
	 */
	protected boolean isTeleportAllowed(Player player)
	{
		return true;
	}
	
	/**
	 * Teleport the {@link Player} into the {@link Npc}'s {@link TeleportLocation}s {@link List} index.<br>
	 * <br>
	 * Following checks are done : {@link #isTeleportAllowed(Player)}, castle siege, price.
	 * @param player : The {@link Player} to test.
	 * @param index : The {@link TeleportLocation} index information to retrieve from this {@link Npc}'s instant teleports {@link List}.
	 */
	protected void teleport(Player player, int index)
	{
		if (!isTeleportAllowed(player))
			return;
		if (player.isInCombat())
		{
			player.sendMessage("You can't use Community Teleport when you in combat!");
			
			String content = HtmCache.getInstance().getHtm(CB_PATH + "custom/Teleport.htm");
			content = content.replaceAll("%ServerName%", "L2JDev-Project");
			separateAndSend(content, player);
			return;
		}
		if (!Config.KARMA_PLAYER_CAN_USE_GK && player.getKarma() > 0)
		{
			player.sendMessage("You can't use Community Teleport when you in Karma!");
			
			String content = HtmCache.getInstance().getHtm(CB_PATH + "custom/Teleport.htm");
			content = content.replaceAll("%ServerName%", "L2JDev-Project");
			separateAndSend(content, player);
			return;
		}
		final List<TeleportLocation> teleports = TeleportData.getInstance().getTeleports(50001);
		if (teleports == null || index > teleports.size())
			return;
		
		final TeleportLocation teleport = teleports.get(index);
		if (teleport == null)
			return;
		
		if (teleport.getCastleId() > 0)
		{
			final Castle castle = CastleManager.getInstance().getCastleById(teleport.getCastleId());
			if (castle != null && castle.getSiege().isInProgress())
			{
				player.sendPacket(SystemMessageId.CANNOT_PORT_VILLAGE_IN_SIEGE);
				return;
			}
		}
		
		if (Config.FREE_TELEPORT || teleport.getPriceCount() == 0 || player.destroyItemByItemId("InstantTeleport", teleport.getPriceId(), teleport.getPriceCount(), null, true))
			player.teleportTo(teleport, 20);
		
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	public static TeleportCBManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final TeleportCBManager INSTANCE = new TeleportCBManager();
	}
}
