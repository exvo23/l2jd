package net.sf.l2j.gameserver.communitybbs.manager.custom;

import java.util.StringTokenizer;

import net.sf.l2j.commons.pool.ThreadPool;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.communitybbs.manager.BaseBBSManager;
import net.sf.l2j.gameserver.data.cache.HtmCache;
import net.sf.l2j.gameserver.data.xml.MultisellData;
import net.sf.l2j.gameserver.enums.actors.ClassId;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.model.olympiad.OlympiadManager;
import net.sf.l2j.gameserver.network.serverpackets.HennaInfo;
import net.sf.l2j.gameserver.skills.L2Skill;

/**
 * @author BAN L2JDEV
 */
public class GmShopCBManager extends BaseBBSManager
{
	protected GmShopCBManager()
	{
	}
	
	@Override
	public void parseCmd(String command, Player player)
	{
		if (!checkAllowed(player))
			return;
		
		if (command.startsWith("_cbmultisell;"))
		{
			StringTokenizer st = new StringTokenizer(command, ";");
			st.nextToken();
			
			String a = st.nextToken();

			player.setIsUsingCMultisell(true);
			MultisellData.getInstance().separateAndSendCb("" + a, player, false);

			String content = HtmCache.getInstance().getHtm(CB_PATH + "custom/GmShop.htm");
			content = content.replaceAll("%ServerName%", "L2JDev-Project");
			separateAndSend(content, player);	
		}
		
		else if (command.startsWith("_cbSetRace"))
		{
			final StringTokenizer st = new StringTokenizer(command, " ");
			st.nextToken();
			try
			{
				ItemInstance itemInstance = player.getInventory().getItemByItemId(Config.ItemIdChangeRace);
				
				final int newClassId = Integer.parseInt(st.nextToken());
				if (newClassId < 0 || newClassId > ClassId.VALUES.length)
					return;
				
				final ClassId newClass = ClassId.VALUES[newClassId];
				
				// Don't bother with dummy classes.
				if (newClass.getLevel() == -1)
				{
					player.sendMessage("You tried to set an invalid class for " + player.getName() + ".");
					return;
				}
				// Don't bother edit ClassId if already set the same.
				if (player.getClassId() == newClass)
				{
					player.sendMessage(player.getName() + " is already a(n) " + newClass.toString() + ".");
					return;
				}
				
				if (player.isSubClassActive())
				{
					player.sendMessage("You cannot change your Main Class while you're with Sub Class.");
					return;
				}
				
				if (OlympiadManager.getInstance().isRegisteredInComp(player))
				{
					player.sendMessage("You cannot change your Main Class while you have been registered for olympiad match.");
					return;
				}
				
				if (itemInstance == null || !itemInstance.isStackable() && player.getInventory().getItemCount(Config.ItemIdChangeRace, -1) < (Config.PrinceChangeRace))
				{
					player.sendMessage("You not enough " + Config.ConsumeNameRace + ".");
					player.sendMessage(player.getName() + " " + "Change Race Prince " + Config.PrinceChangeRace + " " + Config.ConsumeNameRace + ".");
					return;
				}
				else if (itemInstance.isStackable())
				{
					if (!player.destroyItemByItemId("Adena", Config.ItemIdChangeRace, Config.PrinceChangeRace, player.getTarget(), true))
					{
						player.sendMessage("You not enough " + Config.ConsumeNameRace + ".");
						player.sendMessage(player.getName() + " " + "Change Race Prince " + Config.PrinceChangeRace + " " + Config.ConsumeNameRace + ".");
						return;
					}
				}
				
				player.setClassId(newClass.getId());
				if (!player.isSubClassActive())
					player.setBaseClass(newClass);
				
				for (final L2Skill skill : player.getSkills().values())
					player.removeSkill(skill.getId(), true);
				
				player.refreshWeightPenalty();
				player.store();
				player.sendPacket(new HennaInfo(player));
				player.broadcastUserInfo();
				player.sendSkillList();
				player.rewardSkills();
				player.sendMessage("You successfully " + player.getName() + " class to " + newClass.toString() + ".");
				player.sendMessage("You will Be Disconected in 5 Seconds!");
				ThreadPool.schedule(() -> player.logout(false), 5000);
			}
			catch (Exception e)
			{
				String content = HtmCache.getInstance().getHtm(CB_PATH + "custom/ChangeRace.htm");
				content = content.replaceAll("%name%", player.getName());
				content = content.replaceAll("%Accontname%", player.getAccountName());
				content = content.replaceAll("%HwidIp%", player.getClient().toString());
				content = content.replace("%class%", player.getTemplate().getClassName());
				separateAndSend(content, player);
			}
			
		}
		
		else
			super.parseCmd(command, player);
	}
	
	public boolean checkAllowed(Player activeChar)
	{
		String msg = null;
		if (activeChar.isSitting())
			msg = "You can't use Community Community Shop when you sit!";
		if (msg != null)
		{
			activeChar.sendMessage(msg);
		}
		return msg == null;
	}
	
	public static GmShopCBManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final GmShopCBManager INSTANCE = new GmShopCBManager();
	}
}
