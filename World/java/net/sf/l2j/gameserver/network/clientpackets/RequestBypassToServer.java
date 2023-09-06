package net.sf.l2j.gameserver.network.clientpackets;

import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import net.sf.l2j.commons.pool.ThreadPool;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.communitybbs.CommunityBoard;
import net.sf.l2j.gameserver.data.manager.CastleManager;
import net.sf.l2j.gameserver.data.manager.HeroManager;
import net.sf.l2j.gameserver.data.sql.PlayerVariables;
import net.sf.l2j.gameserver.data.xml.AdminData;
import net.sf.l2j.gameserver.data.xml.DressmeArmorData;
import net.sf.l2j.gameserver.data.xml.DressmeArmorJewels;
import net.sf.l2j.gameserver.data.xml.DressmeWeaponData;
import net.sf.l2j.gameserver.data.xml.ItemData;
import net.sf.l2j.gameserver.data.xml.MultisellData;
import net.sf.l2j.gameserver.enums.FloodProtector;
import net.sf.l2j.gameserver.enums.Paperdoll;
import net.sf.l2j.gameserver.events.l2jdev.EventBuffer;
import net.sf.l2j.gameserver.events.l2jdev.EventManager;
import net.sf.l2j.gameserver.events.l2jdev.EventStats;
import net.sf.l2j.gameserver.handler.AdminCommandHandler;
import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.handler.bypasscommand.CommandAutoFarm;
import net.sf.l2j.gameserver.handler.bypasscommand.CommandDonateArmor;
import net.sf.l2j.gameserver.handler.bypasscommand.CommandDonateArmorPart;
import net.sf.l2j.gameserver.handler.bypasscommand.CommandDonateJewels;
import net.sf.l2j.gameserver.handler.bypasscommand.CommandDonateWeapon;
import net.sf.l2j.gameserver.handler.bypasscommand.CommandRaidInfo;
import net.sf.l2j.gameserver.handler.bypasscommand.CommandRanking;
import net.sf.l2j.gameserver.handler.itemhandlers.Books;
import net.sf.l2j.gameserver.handler.itemhandlers.custom.ClassItemManager;
import net.sf.l2j.gameserver.model.DropListChat;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.WorldObject;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.instance.DungeonManagerNpc;
import net.sf.l2j.gameserver.model.actor.instance.OlympiadManagerNpc;
import net.sf.l2j.gameserver.model.entity.Castle;
import net.sf.l2j.gameserver.model.item.DressmeArmor;
import net.sf.l2j.gameserver.model.item.DressmeJewels;
import net.sf.l2j.gameserver.model.item.DressmeWeapon;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.model.olympiad.OlympiadManager;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.network.serverpackets.CharInfo;
import net.sf.l2j.gameserver.network.serverpackets.ConfirmDlg;
import net.sf.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import net.sf.l2j.gameserver.network.serverpackets.InventoryUpdate;
import net.sf.l2j.gameserver.network.serverpackets.MagicSkillUse;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;
import net.sf.l2j.gameserver.network.serverpackets.SiegeInfo;
import net.sf.l2j.gameserver.network.serverpackets.UserInfo;
import net.sf.l2j.gameserver.scripting.QuestState;
import net.sf.l2j.gameserver.taskmanager.AutoGoldBar;

public final class RequestBypassToServer extends L2GameClientPacket
{
	private static final Logger GMAUDIT_LOG = Logger.getLogger("gmaudit");
	private static final String ACTIVED = "<font color=FFFF00>ON</font>";
	private static final String DESATIVED = "<font color=FF0000>OFF</font>";
	public static boolean _activeRequestTeleport = false;
	private String _command;
	
	public static int getX, getY, getZ = 0;
	
	public static int getX()
	{
		return getX;
	}
	
	public static int getY()
	{
		return getY;
	}
	
	public static int getZ()
	{
		return getZ;
	}
	
	@Override
	protected void readImpl()
	{
		_command = readS();
	}
	
	@Override
	protected void runImpl()
	{
		if (_command.isEmpty())
			return;
		
		if (!getClient().performAction(FloodProtector.SERVER_BYPASS))
			return;
		
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		if (_command.startsWith("raidinfo"))
		{
			CommandRaidInfo.showChatWindow(player, 1);
		}
		

		if (_command.startsWith("admin_"))
		{
			String command = _command.split(" ")[0];
			
			final IAdminCommandHandler ach = AdminCommandHandler.getInstance().getHandler(command);
			if (ach == null)
			{
				if (player.isGM())
					player.sendMessage("The command " + command.substring(6) + " doesn't exist.");
				
				LOGGER.warn("No handler registered for admin command '{}'.", command);
				return;
			}
			
			if (!AdminData.getInstance().hasAccess(command, player.getAccessLevel()))
			{
				player.sendMessage("You don't have the access rights to use this command.");
				LOGGER.warn("{} tried to use admin command '{}' without proper Access Level.", player.getName(), command);
				return;
			}
			
			if (Config.GMAUDIT)
				GMAUDIT_LOG.info(player.getName() + " [" + player.getObjectId() + "] used '" + _command + "' command on: " + ((player.getTarget() != null) ? player.getTarget().getName() : "none"));
			
			ach.useAdminCommand(_command, player);
		}
		if (_command.startsWith("startgb"))
		{
			if (player.isAutoGb())
			{
				player.setAutoGb(false);
				AutoGoldBar.getInstance().remove(player);
			}
			else
			{
				player.setAutoGb(true);
				AutoGoldBar.getInstance().add(player);
			}
			Books.showMenuHtml(player);
			
		}
		else if (_command.startsWith("player_help "))
		{
			final String path = _command.substring(12);
			if (path.indexOf("..") != -1)
				return;
			
			final StringTokenizer st = new StringTokenizer(path);
			final String[] cmd = st.nextToken().split("#");
			
			final NpcHtmlMessage html = new NpcHtmlMessage(0);
			html.setFile("data/html/help/" + cmd[0]);
			if (cmd.length > 1)
			{
				final int itemId = Integer.parseInt(cmd[1]);
				html.setItemId(itemId);
				
				if (itemId == 7064 && cmd[0].equalsIgnoreCase("lidias_diary/7064-16.htm"))
				{
					final QuestState qs = player.getQuestList().getQuestState("Q023_LidiasHeart");
					if (qs != null && qs.getCond() == 5 && qs.getInteger("diary") == 0)
						qs.set("diary", "1");
				}
			}
			html.disableValidation();
			player.sendPacket(html);
		}
		
		else if (_command.startsWith("Link "))
		{
			final String path = _command.substring(5).trim();
			if (path.indexOf("..") != -1)
				return;
			
			final NpcHtmlMessage html = new NpcHtmlMessage(0);
			html.setFile("data/html/" + path);
			html.replace("%objectId%", "" + player.getTargetId());
			html.replace("%ServerName%", Config.SERVER_NAME);
			html.replace("%bloking%", player.isDisableHair() ? DESATIVED : ACTIVED);
			html.replace("%count%", player.getBuffCount() + " / " + player.getMaxBuffCount());
			html.replace("%online%", World.getInstance().getPlayers().size());
			html.replace("%dresmedisable%", player.isSkinDisable() ? ACTIVED : DESATIVED);
			html.replace("%autogb%", player.isAutoGb() ? ACTIVED : DESATIVED);
			html.replace("%dungstat1%", DungeonManagerNpc.getPlayerStatus(player, 1));
			html.replace("%dungstat2%", DungeonManagerNpc.getPlayerStatus(player, 2));
			player.sendPacket(html);
		}
		else if (_command.startsWith("event_vote"))
		{
			EventManager.getInstance().addVote(player, Integer.parseInt(_command.substring(11)));
		}
		else if (_command.equals("event_register"))
		{
			EventManager.getInstance().registerPlayer(player);
		}
		else if (_command.equals("event_unregister"))
		{
			EventManager.getInstance().unregisterPlayer(player);
		}
		else if (_command.startsWith("eventvote "))
		{
			EventManager.getInstance().addVote(player, Integer.parseInt(_command.substring(10)));
		}
		else if (_command.startsWith("eventstats "))
		{
			try
			{
				EventStats.getInstance().showHtml(Integer.parseInt(_command.substring(11)), player);
			}
			catch (Exception e)
			{
				player.sendMessage("Currently there are no statistics to show.");
			}
		}
		else if (_command.startsWith("eventstats_show "))
		{
			EventStats.getInstance().showPlayerStats(Integer.parseInt(_command.substring(16)), player);
		}
		else if (_command.equals("eventbuffershow"))
		{
			EventBuffer.getInstance().showHtml(player);
		}
		else if (_command.startsWith("eventbuffer "))
		{
			EventBuffer.getInstance().changeList(player, Integer.parseInt(_command.substring(12, _command.length() - 2)), (Integer.parseInt(_command.substring(_command.length() - 1)) == 0 ? false : true));
			EventBuffer.getInstance().showHtml(player);
		}
		else if (_command.startsWith("eventinfo "))
		{
			int eventId = Integer.valueOf(_command.substring(10));
			
			NpcHtmlMessage html = new NpcHtmlMessage(0);
			html.setFile("data/html/eventinfo/" + eventId + ".htm");
			html.replace("%amount%", String.valueOf(EventManager.getInstance().getInt(eventId, "rewardAmmount")));
			html.replace("%item%", ItemData.getInstance().createDummyItem(EventManager.getInstance().getInt(eventId, "rewardId")).getItemName());
			html.replace("%minlvl%", String.valueOf(EventManager.getInstance().getInt(eventId, "minLvl")));
			html.replace("%maxlvl%", String.valueOf(EventManager.getInstance().getInt(eventId, "maxLvl")));
			html.replace("%time%", String.valueOf(EventManager.getInstance().getInt(eventId, "matchTime") / 60));
			html.replace("%players%", String.valueOf(EventManager.getInstance().getInt(eventId, "minPlayers")));
			html.replace("%url%", EventManager.getInstance().getString("siteUrl"));
			html.replace("%buffs%", EventManager.getInstance().getBoolean(eventId, "removeBuffs") ? "Self" : "Full");
			player.sendPacket(html);
			player.sendPacket(ActionFailed.STATIC_PACKET);
		}
		else if (_command.startsWith("eventIndex"))
			Books.showCPanel(player);
		
		else if (_command.startsWith("_teleport"))
		{
			
			StringTokenizer st = new StringTokenizer(_command, " ");
			st.nextToken();
			
			int x = Integer.parseInt(st.nextToken());
			int y = Integer.parseInt(st.nextToken());
			int z = Integer.parseInt(st.nextToken());
			
			if (!Config.KARMA_PLAYER_CAN_USE_GK && player.getKarma() > 0)
			{
				player.sendMessage(player.getName() + " " + "You dont teleport for karma.");
				return;
			}
			getX = x;
			getY = y;
			getZ = z;
			_activeRequestTeleport = true;
			
			if (_activeRequestTeleport)
			{
				ConfirmDlg confirm = new ConfirmDlg(SystemMessageId.TOUR.getId());
				confirm.addString("Lest go! now " + player.getName() + "?");
				confirm.addTime(11000);
				confirm.addRequesterId(player.getObjectId());
				player.sendPacket(confirm);
			}
			
		}
		else if (_command.startsWith("npc_"))
		{
			if (!player.validateBypass(_command))
				return;
			
			int endOfId = _command.indexOf('_', 5);
			String id;
			if (endOfId > 0)
				id = _command.substring(4, endOfId);
			else
				id = _command.substring(4);
			
			try
			{
				final WorldObject object = World.getInstance().getObject(Integer.parseInt(id));
				
				if (object instanceof Npc && endOfId > 0 && player.getAI().canDoInteract(object))
					((Npc) object).onBypassFeedback(player, _command.substring(endOfId + 1));
				
				player.sendPacket(ActionFailed.STATIC_PACKET);
			}
			catch (NumberFormatException nfe)
			{
			}
		}
		// Navigate throught Manor windows
		else if (_command.startsWith("manor_menu_select?"))
		{
			WorldObject object = player.getTarget();
			if (object instanceof Npc)
				((Npc) object).onBypassFeedback(player, _command);
		}
		if (_command.startsWith("_cb"))
		{
			CommunityBoard.getInstance().handleCommands(getClient(), _command);
		}
		if (_command.startsWith("bbs_") || _command.startsWith("_bbs") || _command.startsWith("_friend") || _command.startsWith("_mail") || _command.startsWith("_block"))
		{
			CommunityBoard.getInstance().handleCommands(getClient(), _command);
		}
		else if (_command.startsWith("Quest "))
		{
			if (!player.validateBypass(_command))
				return;
			
			String[] str = _command.substring(6).trim().split(" ", 2);
			if (str.length == 1)
				player.getQuestList().processQuestEvent(str[0], "");
			else
				player.getQuestList().processQuestEvent(str[0], str[1]);
		}
		else if (_command.startsWith("_match"))
		{
			String params = _command.substring(_command.indexOf("?") + 1);
			StringTokenizer st = new StringTokenizer(params, "&");
			int heroclass = Integer.parseInt(st.nextToken().split("=")[1]);
			int heropage = Integer.parseInt(st.nextToken().split("=")[1]);
			int heroid = HeroManager.getInstance().getHeroByClass(heroclass);
			if (heroid > 0)
				HeroManager.getInstance().showHeroFights(player, heroclass, heroid, heropage);
		}
		else if (_command.startsWith("_diary"))
		{
			String params = _command.substring(_command.indexOf("?") + 1);
			StringTokenizer st = new StringTokenizer(params, "&");
			int heroclass = Integer.parseInt(st.nextToken().split("=")[1]);
			int heropage = Integer.parseInt(st.nextToken().split("=")[1]);
			int heroid = HeroManager.getInstance().getHeroByClass(heroclass);
			if (heroid > 0)
				HeroManager.getInstance().showHeroDiary(player, heroclass, heroid, heropage);
		}
		else if (_command.startsWith("arenachange")) // change
		{
			final boolean isManager = player.getCurrentFolk() instanceof OlympiadManagerNpc;
			if (!isManager)
			{
				// Without npc, command can be used only in observer mode on arena
				if (!player.isInObserverMode() || player.isInOlympiadMode() || player.getOlympiadGameId() < 0)
					return;
			}
			
			// Olympiad registration check.
			if (OlympiadManager.getInstance().isRegisteredInComp(player))
			{
				player.sendPacket(SystemMessageId.WHILE_YOU_ARE_ON_THE_WAITING_LIST_YOU_ARE_NOT_ALLOWED_TO_WATCH_THE_GAME);
				return;
			}
			if (EventManager.getInstance().players.contains(player))
			{
				player.sendMessage("You can not observe games while registered for an event!");
				return;
			}
			final int arenaId = Integer.parseInt(_command.substring(12).trim());
			player.enterOlympiadObserverMode(arenaId);
		}
		
		else if (_command.startsWith("class_index"))
		{
			NpcHtmlMessage html = new NpcHtmlMessage(0);
			html.setFile("data/html/mods/ItemClassChanger/Class.htm");
			player.sendPacket(html);
			player.sendPacket(ActionFailed.STATIC_PACKET);
		}
		else if (_command.startsWith("classe"))
		{
			StringTokenizer st = new StringTokenizer(_command);
			st.nextToken();
			String type = null;
			type = st.nextToken();
			try
			{
				if (player.getBaseClass() != player.getClassId().getId())
				{
					player.sendMessage("You need to be in your base class to be able to use this item.");
					return;
				}
				
				if (player.getStatus().getLevel() < 79)
				{
					player.sendMessage("You need to be at least 80 level in order to use class card.");
					return;
				}
				
				if (player.isInOlympiadMode())
				{
					player.sendMessage("This item cannot be used on olympiad games.");
					return;
				}
				ClassItemManager.ClassChangeCoin(player, type);
			}
			catch (StringIndexOutOfBoundsException e)
			{
			}
		}
		StringTokenizer st = new StringTokenizer(_command, " ");
		String actualCommand = st.nextToken(); // Get actual command
		if (_command.startsWith("multisell"))
		{
			
			StringTokenizer st1 = new StringTokenizer(_command, " ");
			st1.nextToken();
			MultisellData.getInstance().separateAndSend(_command.substring(9).trim(), player, null, false);
		}
		else if (actualCommand.equalsIgnoreCase("Exc_Multisell"))
		{
			if (st.countTokens() < 1)
				return;
			MultisellData.getInstance().separateAndSend(_command.substring(9).trim(), player, null, true);
		}
		else if (_command.startsWith("DropList"))
		{
			
			int npcId = Integer.parseInt(st.nextToken());
			int page = (st.hasMoreTokens()) ? Integer.parseInt(st.nextToken()) : 1;
			
			DropListChat.ShiffNpcDropList(player, npcId, page);
		}
		
		else if (_command.startsWith("AutoFarm"))
			CommandAutoFarm.dashboard(player);
		
		else if (actualCommand.startsWith("OlyRanking"))
		{
			int rankId = Integer.parseInt(st.nextToken());
			
			OlympiadManagerNpc.getViwerRanking(player, rankId);
		}
		else if (actualCommand.startsWith("RankingStat"))
		{
			int rankId = Integer.parseInt(st.nextToken());
			CommandRanking.RankingPvP(player, rankId);
		}
		else if (actualCommand.startsWith("CastleRegister"))
		{
			int castleId = Integer.parseInt(st.nextToken());
			if (player.getClan() != null && !player.isClanLeader())
			{
				player.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
				return;
			}
			
			Castle castle = CastleManager.getInstance().getCastleById(castleId);
			if (castle != null && castleId != 0)
				player.sendPacket(new SiegeInfo(castle));
		}
		// DressMe OnBypassFeedback Handler
		else if (actualCommand.startsWith("user_dres-hair"))
		{
			if (player.isDisableHair())
				player.setDisableHair(false);
			else
				player.setDisableHair(true);
			player.broadcastUserInfo();
			
		}
		else if (actualCommand.startsWith("user_dres-remove"))
		{
			if (player.isSkinDisable())
				player.setDisableSkin(false);
			else
				player.setDisableSkin(true);
			
			PlayerVariables.unsetVar(player, "skin");
			for (final Player player1 : player.getKnownType(Player.class))
			{
				player1.sendPacket(new CharInfo(player1));
				player1.sendPacket(new CharInfo(player));
				player.sendPacket(new CharInfo(player1));
				player.sendPacket(new CharInfo(player));
				
				player1.sendPacket(new UserInfo(player1));
				player1.sendPacket(new UserInfo(player));
				player.sendPacket(new UserInfo(player1));
				player.sendPacket(new UserInfo(player));
			}
		}
		
		else if (actualCommand.startsWith("viwer_dres-weapon"))
		{
			int itemId = Integer.parseInt(st.nextToken());
			ItemInstance rhand = player.getInventory().getItemFrom(Paperdoll.RHAND);
			
			if (rhand == null)
			{
				DressmeWeapon dress = DressmeWeaponData.getInstance().getItemId(itemId);
				
				player.setSkinWeapon(dress);
				player.sendMessage("Buy now have required items " + ItemData.getInstance().getTemplate(dress.getPrinceId()).getName() + " " + dress.getPrinceCont());
				player.sendPacket(new ExShowScreenMessage("Buy now have required items! " + ItemData.getInstance().getTemplate(dress.getPrinceId()).getName() + " " + dress.getPrinceCont(), (int) TimeUnit.SECONDS.toMillis(4)));
				ThreadPool.schedule(() -> VisualTesteWeapon(player), 1000 * 15);
				player.broadcastUserInfo();
				return;
			}
			player.sendPacket(new ExShowScreenMessage("Remove Weapon to view! ", (int) TimeUnit.SECONDS.toMillis(4)));
			
		}
		else if (actualCommand.startsWith("user_dres-weapon"))
		{
			int itemId = Integer.parseInt(st.nextToken());
			ItemInstance rhand = player.getInventory().getItemFrom(Paperdoll.RHAND);
			if (rhand == null)
			{
				DressmeWeapon dress = DressmeWeaponData.getInstance().getItemId(itemId);
				
				if (player.getInventory().getItemCount(dress.getPrinceId(), -1) < (dress.getPrinceCont()))
				{
					player.sendPacket(new ExShowScreenMessage("You don't have required items! " + ItemData.getInstance().getTemplate(dress.getPrinceId()).getName() + " " + dress.getPrinceCont(), (int) TimeUnit.SECONDS.toMillis(2)));
					player.sendMessage("You don't have required items! " + ItemData.getInstance().getTemplate(dress.getPrinceId()).getName() + " " + dress.getPrinceCont());
					return;
				}
				
				_activeRequestWeapon = true;
				_itemId = dress.getItemId();
				if (_activeRequestWeapon)
				{
					ConfirmDlg confirm = new ConfirmDlg(SystemMessageId.S1_OBTAINED_S3_S2.getId());
					confirm.addString("Buy now " + player.getName() + " " + ItemData.getInstance().getTemplate(dress.getChestId()).getName() + "?");
					confirm.addTime(11000);
					confirm.addRequesterId(player.getObjectId());
					player.sendMessage(player.getName() + " buy now " + ItemData.getInstance().getTemplate(dress.getChestId()).getName() + ".");
					player.sendPacket(confirm);
				}
				return;
			}
			player.sendPacket(new ExShowScreenMessage("Remove Weapon to Buy! ", (int) TimeUnit.SECONDS.toMillis(4)));
		}
		else if (actualCommand.startsWith("viwer_dres-armor"))
		{
			int itemId = Integer.parseInt(st.nextToken());
			DressmeArmor dress = DressmeArmorData.getInstance().getItemId(itemId);
			
			player.setSkinArmor(dress);
			player.sendMessage("Buy now have required items " + ItemData.getInstance().getTemplate(dress.getPrinceId()).getName() + " " + dress.getPrinceCont());
			player.sendPacket(new ExShowScreenMessage("Buy now have required items! " + ItemData.getInstance().getTemplate(dress.getPrinceId()).getName() + " " + dress.getPrinceCont(), (int) TimeUnit.SECONDS.toMillis(4)));
			ThreadPool.schedule(() -> VisualTeste(player), 1000 * 15);
			player.broadcastUserInfo();
		}
		else if (actualCommand.startsWith("user_dres-armor"))
		{
			int itemId = Integer.parseInt(st.nextToken());
			DressmeArmor dress = DressmeArmorData.getInstance().getItemId(itemId);
			
			if (player.getInventory().getItemCount(dress.getPrinceId(), -1) < (dress.getPrinceCont()))
			{
				player.sendPacket(new ExShowScreenMessage("You don't have required items! " + ItemData.getInstance().getTemplate(dress.getPrinceId()).getName() + " " + dress.getPrinceCont(), (int) TimeUnit.SECONDS.toMillis(2)));
				player.sendMessage("You don't have required items! " + ItemData.getInstance().getTemplate(dress.getPrinceId()).getName() + " " + dress.getPrinceCont());
				return;
			}
			
			_activeRequest = true;
			_itemId = dress.getItemId();
			if (_activeRequest)
			{
				ConfirmDlg confirm = new ConfirmDlg(SystemMessageId.S1_OBTAINED_S3_S2.getId());
				confirm.addString("Buy now " + player.getName() + " " + ItemData.getInstance().getTemplate(dress.getChestId()).getName() + "?");
				confirm.addTime(11000);
				confirm.addRequesterId(player.getObjectId());
				player.sendMessage(player.getName() + " buy now " + ItemData.getInstance().getTemplate(dress.getChestId()).getName() + ".");
				player.sendPacket(confirm);
			}
		}
		else if (actualCommand.startsWith("viwer_donate-armor"))
		{
			int itemId = Integer.parseInt(st.nextToken());
			DressmeArmor dress = DressmeArmorData.getInstance().getItemId(itemId);
			
			player.setSkinArmor(dress);
			player.sendMessage("Buy now have required items " + ItemData.getInstance().getTemplate(dress.getPrinceId()).getName() + " " + dress.getPrinceCont());
			player.sendPacket(new ExShowScreenMessage("Buy now have required items! " + ItemData.getInstance().getTemplate(dress.getPrinceId()).getName() + " " + dress.getPrinceCont(), (int) TimeUnit.SECONDS.toMillis(4)));
			ThreadPool.schedule(() -> VisualTeste(player), 1000 * 15);
			player.broadcastUserInfo();
		}
		else if (actualCommand.startsWith("user_donate-armor"))
		{
			int itemId = Integer.parseInt(st.nextToken());
			DressmeArmor dress = DressmeArmorData.getInstance().getItemId(itemId);
			
			if (player.getInventory().getItemCount(dress.getPrinceId(), -1) < (dress.getPrinceCont()))
			{
				player.sendPacket(new ExShowScreenMessage("You don't have required items! " + ItemData.getInstance().getTemplate(dress.getPrinceId()).getName() + " " + dress.getPrinceCont(), (int) TimeUnit.SECONDS.toMillis(2)));
				player.sendMessage("You don't have required items! " + ItemData.getInstance().getTemplate(dress.getPrinceId()).getName() + " " + dress.getPrinceCont());
				return;
			}
			
			_activeArmorRequest = true;
			_itemId = dress.getItemId();
			if (_activeArmorRequest)
			{
				ConfirmDlg confirm = new ConfirmDlg(SystemMessageId.S1_OBTAINED_S3_S2.getId());
				confirm.addString("Buy now " + player.getName() + " " + ItemData.getInstance().getTemplate(dress.getChestId()).getName() + "?");
				confirm.addTime(11000);
				confirm.addRequesterId(player.getObjectId());
				player.sendMessage(player.getName() + " buy now " + ItemData.getInstance().getTemplate(dress.getChestId()).getName() + ".");
				player.sendPacket(confirm);
			}
		}
		else if (actualCommand.startsWith("user_armor-part"))
		{
			int itemId = Integer.parseInt(st.nextToken());
			DressmeArmor dress = DressmeArmorData.getInstance().getItemId(itemId);
			
			if (player.getInventory().getItemCount(dress.getPrinceId(), -1) < (dress.getPrinceCont()))
			{
				player.sendPacket(new ExShowScreenMessage("You don't have required items! " + ItemData.getInstance().getTemplate(dress.getPrinceId()).getName() + " " + dress.getPrinceCont(), (int) TimeUnit.SECONDS.toMillis(2)));
				player.sendMessage("You don't have required items! " + ItemData.getInstance().getTemplate(dress.getPrinceId()).getName() + " " + dress.getPrinceCont());
				return;
			}
			
			_activeArmorPartRequest = true;
			_itemId = dress.getItemId();
			if (_activeArmorPartRequest)
			{
				ConfirmDlg confirm = new ConfirmDlg(SystemMessageId.S1_OBTAINED_S3_S2.getId());
				confirm.addString("Buy now " + player.getName() + " " + ItemData.getInstance().getTemplate(dress.getChestId()).getName() + "?");
				confirm.addTime(11000);
				confirm.addRequesterId(player.getObjectId());
				player.sendMessage(player.getName() + " buy now " + ItemData.getInstance().getTemplate(dress.getChestId()).getName() + ".");
				player.sendPacket(confirm);
			}
		}
		
		else if (actualCommand.startsWith("user_donate-jewel"))
		{
			int itemId = Integer.parseInt(st.nextToken());
			DressmeJewels dress = DressmeArmorJewels.getInstance().getItemId(itemId);
			
			if (player.getInventory().getItemCount(dress.getPrinceId(), -1) < (dress.getPrinceCont()))
			{
				player.sendPacket(new ExShowScreenMessage("You don't have required items! " + ItemData.getInstance().getTemplate(dress.getPrinceId()).getName() + " " + dress.getPrinceCont(), (int) TimeUnit.SECONDS.toMillis(2)));
				player.sendMessage("You don't have required items! " + ItemData.getInstance().getTemplate(dress.getPrinceId()).getName() + " " + dress.getPrinceCont());
				return;
			}
			
			_activeRequestJewel = true;
			_itemId = dress.getLinkId();
			if (_activeRequestJewel)
			{
				ConfirmDlg confirm = new ConfirmDlg(SystemMessageId.S1_OBTAINED_S3_S2.getId());
				confirm.addString("Buy now " + player.getName() + " " + ItemData.getInstance().getTemplate(dress.getJewelsId()).getName() + "?");
				confirm.addTime(11000);
				confirm.addRequesterId(player.getObjectId());
				player.sendMessage(player.getName() + " buy now " + ItemData.getInstance().getTemplate(dress.getJewelsId()).getName() + ".");
				player.sendPacket(confirm);
			}
		}
		
		if (actualCommand.startsWith("chat"))
		{
			int val = Integer.parseInt(st.nextToken());
			CommandRaidInfo.showChatWindow(player, val);

		}
	}
	
	public static boolean _activeArmorRequest = false;
	public static boolean _activeRequestJewel = false;
	public static boolean _activeArmorPartRequest = false;
	public static boolean _activeRequest = false;
	public static boolean _activeRequestWeapon = false;
	public static int _itemId = 0;
	
	public static int getItemId()
	{
		return _itemId;
	}
	
	public static void BuyNow(Player player, int itemId)
	{
		
		DressmeArmor dress = DressmeArmorData.getInstance().getItemId(itemId);
		
		if (player.getInventory().getItemCount(dress.getPrinceId(), -1) < (dress.getPrinceCont()))
		{
			player.sendPacket(new ExShowScreenMessage("You don't have required items! " + ItemData.getInstance().getTemplate(dress.getPrinceId()).getName() + " " + dress.getPrinceCont(), (int) TimeUnit.SECONDS.toMillis(2)));
			player.sendMessage("You don't have required items! " + ItemData.getInstance().getTemplate(dress.getPrinceId()).getName() + " " + dress.getPrinceCont());
			
			player.sendPacket(SystemMessageId.NOT_ENOUGH_ITEMS);
			return;
		}
		
		player.destroyItemByItemId("skin", dress.getPrinceId(), dress.getPrinceCont(), player.getTarget(), true);
		
		player.setSkinArmor(dress);
		
		player.sendPacket(new ExShowScreenMessage(player.getName() + " has been visual change to " + dress.getName() + " and will remain active", (int) TimeUnit.SECONDS.toMillis(5)));
		player.sendMessage(player.getName() + " has been visual change to " + dress.getName() + "and will remain active!");
		
		PlayerVariables.setVar(player, "skin", dress.getItemId(), -1);
		
		if (dress.getGiveItemId())
			player.addItem("Handler", getItemId(), 1, player, true);
		InventoryUpdate playerIU = new InventoryUpdate();
		player.sendPacket(playerIU);
		player.broadcastUserInfo();
		_activeRequest = false;
		_itemId = 0;
		
	}
	
	public static void BuyDonateArmor(Player player, int itemId)
	{
		
		DressmeArmor dress = DressmeArmorData.getInstance().getItemId(itemId);
		
		if (player.getInventory().getItemCount(dress.getPrinceId(), -1) < (dress.getPrinceCont()))
		{
			player.sendPacket(new ExShowScreenMessage("You don't have required items! " + ItemData.getInstance().getTemplate(dress.getPrinceId()).getName() + " " + dress.getPrinceCont(), (int) TimeUnit.SECONDS.toMillis(2)));
			player.sendMessage("You don't have required items! " + ItemData.getInstance().getTemplate(dress.getPrinceId()).getName() + " " + dress.getPrinceCont());
			
			player.sendPacket(SystemMessageId.NOT_ENOUGH_ITEMS);
			return;
		}
		
		player.destroyItemByItemId("Tkt", dress.getPrinceId(), dress.getPrinceCont(), player.getTarget(), true);
		
		player.sendMessage("You have successfully purchase item.");
		player.sendPacket(new ExShowScreenMessage(player.getName() + " You have successfully purchase item. " + dress.getName(), (int) TimeUnit.SECONDS.toMillis(5)));
		
		if (dress.getGiveItemId())
		{
			CommandDonateArmor.AddItem(player);
			MagicSkillUse mgc = new MagicSkillUse(player, player, 2024, 1, 5, 0);
			player.sendPacket(mgc);
			player.broadcastPacket(mgc);
		}
		
		_activeArmorRequest = false;
		_itemId = 0;
		
	}
	
	public static void BuyDonateArmorPart(Player player, int itemId)
	{
		
		DressmeArmor dress = DressmeArmorData.getInstance().getItemId(itemId);
		
		if (player.getInventory().getItemCount(dress.getPrinceId(), -1) < (dress.getPrinceCont()))
		{
			player.sendPacket(new ExShowScreenMessage("You don't have required items! " + ItemData.getInstance().getTemplate(dress.getPrinceId()).getName() + " " + dress.getPrinceCont(), (int) TimeUnit.SECONDS.toMillis(2)));
			player.sendMessage("You don't have required items! " + ItemData.getInstance().getTemplate(dress.getPrinceId()).getName() + " " + dress.getPrinceCont());
			
			player.sendPacket(SystemMessageId.NOT_ENOUGH_ITEMS);
			return;
		}
		
		player.destroyItemByItemId("Tkt", dress.getPrinceId(), dress.getPrinceCont(), player.getTarget(), true);
		
		player.sendMessage("You have successfully purchase item.");
		player.sendPacket(new ExShowScreenMessage(player.getName() + " You have successfully purchase item. " + dress.getName(), (int) TimeUnit.SECONDS.toMillis(5)));
		
		if (dress.getGiveItemId())
		{
			CommandDonateArmorPart.AddItem1(player);
			MagicSkillUse mgc = new MagicSkillUse(player, player, 2024, 1, 5, 0);
			player.sendPacket(mgc);
			player.broadcastPacket(mgc);
		}
		
		_activeArmorPartRequest = false;
		_itemId = 0;
		
	}
	
	public static void BuyDonateJewel(Player player, int itemId)
	{
		
		DressmeJewels dress = DressmeArmorJewels.getInstance().getItemId(itemId);
		
		if (player.getInventory().getItemCount(dress.getPrinceId(), -1) < (dress.getPrinceCont()))
		{
			player.sendPacket(new ExShowScreenMessage("You don't have required items! " + ItemData.getInstance().getTemplate(dress.getPrinceId()).getName() + " " + dress.getPrinceCont(), (int) TimeUnit.SECONDS.toMillis(2)));
			player.sendMessage("You don't have required items! " + ItemData.getInstance().getTemplate(dress.getPrinceId()).getName() + " " + dress.getPrinceCont());
			
			player.sendPacket(SystemMessageId.NOT_ENOUGH_ITEMS);
			return;
		}
		
		player.destroyItemByItemId("Tkt", dress.getPrinceId(), dress.getPrinceCont(), player.getTarget(), true);
		
		player.sendMessage("You have successfully purchase item.");
		player.sendPacket(new ExShowScreenMessage(player.getName() + " You have successfully purchase item. " + dress.getName(), (int) TimeUnit.SECONDS.toMillis(5)));
		
		CommandDonateJewels.AddJewels(player);
		MagicSkillUse mgc = new MagicSkillUse(player, player, 2024, 1, 5, 0);
		player.sendPacket(mgc);
		player.broadcastPacket(mgc);
		
		_activeRequestJewel = false;
		_itemId = 0;
		
	}
	
	public static void BuyWeaponNow(Player player, int itemId)
	{
		
		DressmeWeapon dress = DressmeWeaponData.getInstance().getItemId(itemId);
		
		if (player.getInventory().getItemCount(dress.getPrinceId(), -1) < (dress.getPrinceCont()))
		{
			player.sendPacket(new ExShowScreenMessage("You don't have required items! " + ItemData.getInstance().getTemplate(dress.getPrinceId()).getName() + " " + dress.getPrinceCont(), (int) TimeUnit.SECONDS.toMillis(2)));
			player.sendMessage("You don't have required items! " + ItemData.getInstance().getTemplate(dress.getPrinceId()).getName() + " " + dress.getPrinceCont());
			
			player.sendPacket(SystemMessageId.NOT_ENOUGH_ITEMS);
			return;
		}
		
		player.destroyItemByItemId("skin", dress.getPrinceId(), dress.getPrinceCont(), player.getTarget(), true);
		
		player.sendMessage("You have successfully purchase item.");
		player.sendPacket(new ExShowScreenMessage(player.getName() + " You have successfully purchase item. " + dress.getName(), (int) TimeUnit.SECONDS.toMillis(5)));
		
		if (dress.getGiveItemId())
		{
			CommandDonateWeapon.AddItem(player);
			MagicSkillUse mgc = new MagicSkillUse(player, player, 2024, 1, 5, 0);
			player.sendPacket(mgc);
			player.broadcastPacket(mgc);
			
		}
		_activeRequestWeapon = false;
		_itemId = 0;
	}
	
	public static void VisualTeste(Player player)
	{
		DressmeArmor free = DressmeArmorData.getInstance().getItemId(0);
		player.setSkinArmor(free);
		player.broadcastUserInfo();
		
		if (PlayerVariables.getVarB(player, "skin"))
			EnterWorld.ReturnViwerDressMe(player);
	}
	
	public static void VisualTesteWeapon(Player player)
	{
		DressmeWeapon free = DressmeWeaponData.getInstance().getItemId(0);
		player.setSkinWeapon(free);
		player.broadcastUserInfo();
		
		if (PlayerVariables.getVarB(player, "weapon"))
			EnterWorld.ReturnViwerDressMe(player);
	}
	
	public static void showChatWindow(Player player, String filename)
	{
		final NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile(filename);
		
		html.replace("%count%", player.getBuffCount() + " / " + player.getMaxBuffCount());
		html.replace("%AugmentsRate%", "" + Config.AUGMENTATION_BASESTAT_CHANCE + "%");
		html.replace("%ServerName%", Config.SERVER_NAME);
		
		html.replace("%bloking%", player.isDisableHair() ? DESATIVED : ACTIVED);
		html.replace("%dresmedisable%", player.isSkinDisable() ? ACTIVED : DESATIVED);
		html.replace("%online%", World.getInstance().getPlayers().size());
		
		if (EventManager.getInstance().isRunning() && EventManager.getInstance().isRegistered(player))
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		player.sendPacket(html);
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	public static void TeleportZone(Player player, int a, int b, int c)
	{
		player.teleportTo(a, b, c, 0);
	}
}