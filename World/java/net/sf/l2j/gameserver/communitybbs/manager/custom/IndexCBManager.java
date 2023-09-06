package net.sf.l2j.gameserver.communitybbs.manager.custom;

import java.text.SimpleDateFormat;
import java.util.StringTokenizer;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.communitybbs.manager.BaseBBSManager;
import net.sf.l2j.gameserver.data.cache.HtmCache;
import net.sf.l2j.gameserver.data.sql.ClanTable;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.model.pledge.Clan;
import net.sf.l2j.gameserver.taskmanager.HeroTaskManager;
import net.sf.l2j.gameserver.taskmanager.PremiumTaskManager;

/**
 * @author BAN L2JDEV
 */
public class IndexCBManager extends BaseBBSManager
{
	protected IndexCBManager()
	{
	}
	
	@Override
	public void parseCmd(String command, Player player)
	{
		if (command.startsWith("_cbhome"))
			showIndex(player);

		else if (command.startsWith("_cbBuy"))
		{
			showIndex(player);
			
			StringTokenizer st = new StringTokenizer(command, ";");
			st.nextToken();
			String action = st.nextToken();
			
			if (action.equals("vip30days"))
			{
				ItemInstance itemInstance = player.getInventory().getItemByItemId(Config.PremiumConsumeItemId);
				if (itemInstance == null || !itemInstance.isStackable() && player.getInventory().getItemCount(Config.PremiumConsumeItemId, -1) < (Config.PrinceBuyPremium30))
				{
					player.sendMessage("You not enough " + Config.ConsumeName + ".");
					player.sendMessage(player.getName() + " " + "30 Days Premium from " + Config.PrinceBuyPremium30 + " " + Config.ConsumeName + ".");
					return;
				}
				else if (itemInstance.isStackable())
				{
					if (!player.destroyItemByItemId("Adena", Config.PremiumConsumeItemId, Config.PrinceBuyPremium30, player.getTarget(), true))
					{
						player.sendMessage("You not enough " + Config.ConsumeName + ".");
						player.sendMessage(player.getName() + " " + "30 Days Premium from " + Config.PrinceBuyPremium30 + " " + Config.ConsumeName + ".");
						return;
					}
				}
				PremiumTaskManager.ApllyPremium(player, player, 30);
			}
			
			else if (action.equals("vip90days"))
			{
				ItemInstance itemInstance = player.getInventory().getItemByItemId(Config.PremiumConsumeItemId);
				if (itemInstance == null || !itemInstance.isStackable() && player.getInventory().getItemCount(Config.PremiumConsumeItemId, -1) < (Config.PrinceBuyPremium90))
				{
					player.sendMessage("You not enough " + Config.ConsumeName + ".");
					player.sendMessage(player.getName() + " " + "90 Days Premium from " + Config.PrinceBuyPremium90 + " " + Config.ConsumeName + ".");
					return;
				}
				else if (itemInstance.isStackable())
				{
					if (!player.destroyItemByItemId("Adena", Config.PremiumConsumeItemId, Config.PrinceBuyPremium90, player.getTarget(), true))
					{
						player.sendMessage("You not enough " + Config.ConsumeName + ".");
						player.sendMessage(player.getName() + " " + "90 Days Premium from " + Config.PrinceBuyPremium90 + " " + Config.ConsumeName + ".");
						return;
					}
				}
				PremiumTaskManager.ApllyPremium(player, player, 90);
				
			}
			if (action.equals("hero30days"))
			{
				ItemInstance itemInstance = player.getInventory().getItemByItemId(Config.PremiumConsumeItemId);
				if (itemInstance == null || !itemInstance.isStackable() && player.getInventory().getItemCount(Config.PremiumConsumeItemId, -1) < (Config.PrinceBuyHero30))
				{
					player.sendMessage("You not enough " + Config.ConsumeName + ".");
					player.sendMessage(player.getName() + " " + "30 Days Hero from " + Config.PrinceBuyHero30 + " " + Config.ConsumeName + ".");
					return;
				}
				else if (itemInstance.isStackable())
				{
					
					if (!player.destroyItemByItemId("Adena", Config.PremiumConsumeItemId, Config.PrinceBuyHero30, player.getTarget(), true))
					{
						player.sendMessage("You not enough " + Config.ConsumeName + ".");
						player.sendMessage(player.getName() + " " + "30 Days Hero from " + Config.PrinceBuyHero30 + " " + Config.ConsumeName + ".");
						return;
					}
				}
				HeroTaskManager.ApllyHero(player, 30);
			}
			if (action.equals("hero90days"))
			{
				ItemInstance itemInstance = player.getInventory().getItemByItemId(Config.PremiumConsumeItemId);
				if (itemInstance == null || !itemInstance.isStackable() && player.getInventory().getItemCount(Config.PremiumConsumeItemId, -1) < (Config.PrinceBuyHero90))
				{
					player.sendMessage("You not enough " + Config.ConsumeName + ".");
					player.sendMessage(player.getName() + " " + "90 Days Hero from " + Config.PrinceBuyHero90 + " " + Config.ConsumeName + ".");
					return;
				}
				else if (itemInstance.isStackable())
				{
					if (!player.destroyItemByItemId("Adena", Config.PremiumConsumeItemId, Config.PrinceBuyHero90, player.getTarget(), true))
					{
						player.sendMessage("You not enough " + Config.ConsumeName + ".");
						player.sendMessage(player.getName() + " " + "90 Days Hero from " + Config.PrinceBuyHero90 + " " + Config.ConsumeName + ".");
						return;
					}
				}
				HeroTaskManager.ApllyHero(player, 90);
			}
			
		}
		else
			super.parseCmd(command, player);
	}
	
	public static void showIndex(Player activeChar)
	{
		String content = HtmCache.getInstance().getHtm(CB_PATH + "custom/home.htm");
		content = content.replaceAll("%name%", activeChar.getName());
		content = content.replaceAll("%Accontname%", activeChar.getAccountName());
		content = content.replaceAll("%HwidIp%", activeChar.getIP());
		content = content.replaceAll("%class%", activeChar.getTemplate().getClassName());
		content = content.replaceAll("%ServerName%", "L2JDev-Project");
		content = content.replaceAll("%max_players%", String.valueOf(World.getInstance().getPlayers().size()));
		content = content.replaceAll("%pvpkills%", String.valueOf(activeChar.getPvpKills()));
		content = content.replaceAll("%pkkills%", String.valueOf(activeChar.getPkKills()));
		
		Clan playerClan = ClanTable.getInstance().getClan(activeChar.getClanId());
		
		if (playerClan != null)
			content = content.replaceAll("%clan%", playerClan.getName());
		else
			content = content.replaceAll("%clan%", "");
		
		content = content.replaceAll("%rate_xp%", String.valueOf(Config.RATE_XP));
		content = content.replaceAll("%rate_sp%", String.valueOf(Config.RATE_SP));
		content = content.replaceAll("%rate_adena%", String.valueOf(Config.RATE_DROP_CURRENCY));
		content = content.replaceAll("%rate_items%", String.valueOf(Config.RATE_DROP_ITEMS));
		content = content.replaceAll("%rate_spoil%", String.valueOf(Config.RATE_DROP_SPOIL));
		
		content = content.replaceAll("%Premium%", activeChar.isPremium() ? ACTIVED : DESATIVED);
		
		long delay = activeChar.getMemos().getLong("vipEndTime", 0);
		content = content.replaceAll("%PremiumEnd%", new SimpleDateFormat("dd-MM-yyyy HH:mm").format(delay) + "");
		BaseBBSManager.separateAndSend(content, activeChar);
	}
	
	private static final String ACTIVED = "<font color=00FF00>ON</font>";
	private static final String DESATIVED = "<font color=FF0000>OFF</font>";
	
	@Override
	protected String getFolder()
	{
		return "custom/";
	}
	
	public static IndexCBManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final IndexCBManager INSTANCE = new IndexCBManager();
	}
}
