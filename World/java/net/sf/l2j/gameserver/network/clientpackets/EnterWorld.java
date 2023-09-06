package net.sf.l2j.gameserver.network.clientpackets;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import net.sf.l2j.commons.pool.ThreadPool;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.communitybbs.manager.MailBBSManager;
import net.sf.l2j.gameserver.data.AnnouncerCastleLord;
import net.sf.l2j.gameserver.data.AnnouncerHero;
import net.sf.l2j.gameserver.data.SkillTable;
import net.sf.l2j.gameserver.data.SkillTable.FrequentSkill;
import net.sf.l2j.gameserver.data.manager.CastleManager;
import net.sf.l2j.gameserver.data.manager.ClanHallManager;
import net.sf.l2j.gameserver.data.manager.CoupleManager;
import net.sf.l2j.gameserver.data.manager.CrownManager;
import net.sf.l2j.gameserver.data.manager.DimensionalRiftManager;
import net.sf.l2j.gameserver.data.manager.PetitionManager;
import net.sf.l2j.gameserver.data.manager.SevenSignsManager;
import net.sf.l2j.gameserver.data.sql.PlayerVariables;
import net.sf.l2j.gameserver.data.xml.AdminData;
import net.sf.l2j.gameserver.data.xml.AnnouncementData;
import net.sf.l2j.gameserver.data.xml.DressmeArmorData;
import net.sf.l2j.gameserver.data.xml.DressmeWeaponData;
import net.sf.l2j.gameserver.data.xml.MapRegionData.TeleportType;
import net.sf.l2j.gameserver.enums.CabalType;
import net.sf.l2j.gameserver.enums.SayType;
import net.sf.l2j.gameserver.enums.SealType;
import net.sf.l2j.gameserver.enums.SiegeSide;
import net.sf.l2j.gameserver.enums.ZoneId;
import net.sf.l2j.gameserver.enums.actors.ClassRace;
import net.sf.l2j.gameserver.handler.bypasscommand.CommandCastleAnnouncer;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.instance.ClassMaster;
import net.sf.l2j.gameserver.model.actor.instance.ColorManager;
import net.sf.l2j.gameserver.model.clanhall.ClanHall;
import net.sf.l2j.gameserver.model.clanhall.SiegableHall;
import net.sf.l2j.gameserver.model.entity.Castle;
import net.sf.l2j.gameserver.model.entity.Siege;
import net.sf.l2j.gameserver.model.holder.IntIntHolder;
import net.sf.l2j.gameserver.model.item.DressmeArmor;
import net.sf.l2j.gameserver.model.item.DressmeWeapon;
import net.sf.l2j.gameserver.model.olympiad.Olympiad;
import net.sf.l2j.gameserver.model.pledge.Clan;
import net.sf.l2j.gameserver.model.pledge.SubPledge;
import net.sf.l2j.gameserver.network.GameClient;
import net.sf.l2j.gameserver.network.GameClient.GameClientState;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.network.serverpackets.CreatureSay;
import net.sf.l2j.gameserver.network.serverpackets.Die;
import net.sf.l2j.gameserver.network.serverpackets.EtcStatusUpdate;
import net.sf.l2j.gameserver.network.serverpackets.ExMailArrived;
import net.sf.l2j.gameserver.network.serverpackets.ExStorageMaxCount;
import net.sf.l2j.gameserver.network.serverpackets.FriendList;
import net.sf.l2j.gameserver.network.serverpackets.HennaInfo;
import net.sf.l2j.gameserver.network.serverpackets.ItemList;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;
import net.sf.l2j.gameserver.network.serverpackets.PlaySound;
import net.sf.l2j.gameserver.network.serverpackets.PledgeShowMemberListAll;
import net.sf.l2j.gameserver.network.serverpackets.PledgeShowMemberListUpdate;
import net.sf.l2j.gameserver.network.serverpackets.PledgeSkillList;
import net.sf.l2j.gameserver.network.serverpackets.QuestList;
import net.sf.l2j.gameserver.network.serverpackets.ShortCutInit;
import net.sf.l2j.gameserver.network.serverpackets.SkillCoolTime;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;
import net.sf.l2j.gameserver.network.serverpackets.UserInfo;
import net.sf.l2j.gameserver.scripting.Quest;
import net.sf.l2j.gameserver.scripting.specialtutorial.SpecialTutorialGremlin;
import net.sf.l2j.gameserver.skills.L2Skill;
import net.sf.l2j.gameserver.taskmanager.GameTimeTaskManager;
import net.sf.l2j.gameserver.taskmanager.HeroTaskManager;
import net.sf.l2j.gameserver.taskmanager.PremiumTaskManager;

public class EnterWorld extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
		{
			getClient().closeNow();
			return;
		}
		
		getClient().setState(GameClientState.IN_GAME);
		
		final int objectId = player.getObjectId();
		if(Config.ENABLE_IP_BOX && checkIPBox(player, Config.IP_MAX_DUALBOX))
		{
			final NpcHtmlMessage html = new NpcHtmlMessage(0);
			html.setFile("data/html/mods/Hwid.htm");
			html.replace("%name%", player.getName());
			html.replace("%ServerName%", "L2JDEV");
			html.replace("%Hwid_Box%", Config.IP_MAX_DUALBOX);
			html.replace("%Hwid_Time%", Config.IP_TIME_LOGOUT);
			player.setIsImmobilized(true);
			player.disableAllSkills();
			player.getAppearance().setVisible(false);
			sendPacket(html);
		}
		// Set NewChar
		switch (player.getClassId().getId())
		{
			case 0:
			case 10:
			case 18:
			case 25:
			case 31:
			case 38:
			case 44:
			case 49:
			case 53:
				Player.doNewChar(player, 1);
				player.setNewChar(true);
				
				// Starting System
				if (player.getStatus().getLevel() == 1)
				{
					
					if (Config.USE_CUSTOM_CAMERA)
						onCheckNewbieStep(player);
					else
						SpecialTutorialGremlin.showQuestionBuffer(player);
					
					if (Config.PM_MESSAGE)
					{
						player.sendPacket(new CreatureSay(0, SayType.TELL, Config.PM_TEXT1, Config.PM_SERVER_NAME));
						player.sendPacket(new CreatureSay(0, SayType.TELL, player.getName(), Config.PM_TEXT2));
					}
				}
				
				break;
		}
		if (player.isGM())
		{
			if (player.getStatus().getLevel() >= 77 && AdminData.getInstance().hasAccess("admin_gmspeed", player.getAccessLevel()))
				SkillTable.getInstance().getInfo(7029, 4).getEffects(player, player);
			
			LOGGER.warn("Admin: " + player.getName() + " EnterWorld, IP Login: " + player.getIP() + " ");
			
			if (Config.GM_STARTUP_INVULNERABLE && AdminData.getInstance().hasAccess("admin_invul", player.getAccessLevel()))
				player.setInvul(true);
			
			if (Config.GM_STARTUP_INVISIBLE && AdminData.getInstance().hasAccess("admin_hide", player.getAccessLevel()))
				player.getAppearance().setVisible(false);
			
			if (Config.GM_STARTUP_BLOCK_ALL)
				player.getBlockList().setInBlockingAll(true);
			
			if (Config.GM_STARTUP_AUTO_LIST && AdminData.getInstance().hasAccess("admin_gmlist", player.getAccessLevel()))
				AdminData.getInstance().addGm(player, false);
			else
				AdminData.getInstance().addGm(player, true);
		}
		
		if (Config.ALLOW_CLASS_MASTERS)
		{
			
			if (player.getStatus().getLevel() >= 20)
				ClassMaster.showTutorialHtml(player);

		}
		
		// Set dead status if applies
		if (player.getStatus().getHp() < 0.5 && player.isMortal())
			player.setIsDead(true);
		
		player.getMacroList().sendUpdate();
		player.sendPacket(new ExStorageMaxCount(player));
		player.sendPacket(new HennaInfo(player));
		player.updateEffectIcons();
		player.sendPacket(new EtcStatusUpdate(player));
		
		// Clan checks.
		final Clan clan = player.getClan();
		if (clan != null)
		{
			player.sendPacket(new PledgeSkillList(clan));
			
			// Refresh player instance.
			clan.getClanMember(objectId).setPlayerInstance(player);
			
			final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.CLAN_MEMBER_S1_LOGGED_IN).addCharName(player);
			final PledgeShowMemberListUpdate psmlu = new PledgeShowMemberListUpdate(player);
			
			// Send packets to others members.
			for (Player member : clan.getOnlineMembers())
			{
				if (member == player)
					continue;
				
				member.sendPacket(sm);
				member.sendPacket(psmlu);
			}
			
			// Send a login notification to sponsor or apprentice, if logged.
			if (player.getSponsor() != 0)
			{
				final Player sponsor = World.getInstance().getPlayer(player.getSponsor());
				if (sponsor != null)
					sponsor.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOUR_APPRENTICE_S1_HAS_LOGGED_IN).addCharName(player));
			}
			else if (player.getApprentice() != 0)
			{
				final Player apprentice = World.getInstance().getPlayer(player.getApprentice());
				if (apprentice != null)
					apprentice.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOUR_SPONSOR_S1_HAS_LOGGED_IN).addCharName(player));
			}
			
			// Add message at connexion if clanHall not paid.
			final ClanHall ch = ClanHallManager.getInstance().getClanHallByOwner(clan);
			if (ch != null && !ch.getPaid())
				player.sendPacket(SystemMessageId.PAYMENT_FOR_YOUR_CLAN_HALL_HAS_NOT_BEEN_MADE_PLEASE_MAKE_PAYMENT_TO_YOUR_CLAN_WAREHOUSE_BY_S1_TOMORROW);
			
			for (Castle castle : CastleManager.getInstance().getCastles())
			{
				final Siege siege = castle.getSiege();
				if (!siege.isInProgress())
					continue;
				
				final SiegeSide type = siege.getSide(clan);
				if (type == SiegeSide.ATTACKER)
					player.setSiegeState((byte) 1);
				else if (type == SiegeSide.DEFENDER || type == SiegeSide.OWNER)
					player.setSiegeState((byte) 2);
			}
			
			for (SiegableHall hall : ClanHallManager.getInstance().getSiegableHalls())
			{
				if (hall.isInSiege() && hall.isRegistered(clan))
					player.setSiegeState((byte) 1);
			}
			
			player.sendPacket(new PledgeShowMemberListUpdate(player));
			player.sendPacket(new PledgeShowMemberListAll(clan, 0));
			
			for (SubPledge sp : clan.getAllSubPledges())
				player.sendPacket(new PledgeShowMemberListAll(clan, sp.getId()));
			// Check for crowns
			CrownManager.getInstance().checkCrowns(player);
			player.sendPacket(new UserInfo(player));
		}
		
		// Updating Seal of Strife Buff/Debuff
		if (SevenSignsManager.getInstance().isSealValidationPeriod() && SevenSignsManager.getInstance().getSealOwner(SealType.STRIFE) != CabalType.NORMAL)
		{
			CabalType cabal = SevenSignsManager.getInstance().getPlayerCabal(objectId);
			if (cabal != CabalType.NORMAL)
			{
				if (cabal == SevenSignsManager.getInstance().getSealOwner(SealType.STRIFE))
					player.addSkill(FrequentSkill.THE_VICTOR_OF_WAR.getSkill(), false);
				else
					player.addSkill(FrequentSkill.THE_VANQUISHED_OF_WAR.getSkill(), false);
			}
		}
		else
		{
			player.removeSkill(FrequentSkill.THE_VICTOR_OF_WAR.getSkill().getId(), false);
			player.removeSkill(FrequentSkill.THE_VANQUISHED_OF_WAR.getSkill().getId(), false);
		}
		
		if (Config.PLAYER_SPAWN_PROTECTION > 0)
			player.setSpawnProtection(true);
		
		player.spawnMe();
		
		// Set the location of debug packets.
		player.setEnterWorldLoc(player.getX(), player.getY(), -16000);
		
		// Engage and notify partner.
		if (Config.ALLOW_WEDDING)
		{
			for (Entry<Integer, IntIntHolder> coupleEntry : CoupleManager.getInstance().getCouples().entrySet())
			{
				final IntIntHolder couple = coupleEntry.getValue();
				if (couple.getId() == objectId || couple.getValue() == objectId)
				{
					player.setCoupleId(coupleEntry.getKey());
					break;
				}
			}
		}
		
		// Announcements, welcome & Seven signs period messages.
		player.sendPacket(SystemMessageId.WELCOME_TO_LINEAGE);
		if (Config.OLY_ANNOUNCE_GAMES)
			Olympiad.olympiadEnd(player);
		player.sendPacket(SevenSignsManager.getInstance().getCurrentPeriod().getMessageId());
		AnnouncementData.getInstance().showAnnouncements(player, false);
		AnnouncerCastleLord.notifyCastleOwner(player);
		AnnouncerHero.notifyHeroEnter(player);
		
		// If the Player is a Dark Elf, check for Shadow Sense at night.
		if (player.getRace() == ClassRace.DARK_ELF && player.hasSkill(L2Skill.SKILL_SHADOW_SENSE))
			player.sendPacket(SystemMessage.getSystemMessage((GameTimeTaskManager.getInstance().isNight()) ? SystemMessageId.NIGHT_S1_EFFECT_APPLIES : SystemMessageId.DAY_S1_EFFECT_DISAPPEARS).addSkillName(L2Skill.SKILL_SHADOW_SENSE));
		
		// Notify quest for enterworld event, if quest allows it.
		player.getQuestList().getQuests(Quest::isTriggeredOnEnterWorld).forEach(q -> q.onEnterWorld(player));
		
		player.sendPacket(new QuestList(player));
		player.sendSkillList();
		player.sendPacket(new FriendList(player));
		player.sendPacket(new UserInfo(player));
		player.sendPacket(new ItemList(player, false));
		player.sendPacket(new ShortCutInit(player));
		
		// No broadcast needed since the player will already spawn dead to others.
		if (player.isAlikeDead())
			player.sendPacket(new Die(player));
		
		// Unread mails make a popup appears.
		if (MailBBSManager.getInstance().checkIfUnreadMail(player))
		{
			player.sendPacket(SystemMessageId.NEW_MAIL);
			player.sendPacket(new PlaySound("systemmsg_e.1233"));
			player.sendPacket(ExMailArrived.STATIC_PACKET);
		}
		
		// Clan notice, if active.
		if (clan != null && clan.isNoticeEnabled())
		{
			final NpcHtmlMessage html = new NpcHtmlMessage(0);
			html.setFile("data/html/clan_notice.htm");
			html.replace("%clan_name%", clan.getName());
			html.replace("%notice_text%", clan.getNotice().replaceAll("\r\n", "<br>").replaceAll("action", "").replaceAll("bypass", ""));
			sendPacket(html);
		}
		else if (Config.SERVER_NEWS)
		{
			final NpcHtmlMessage html = new NpcHtmlMessage(0);
			html.setFile("data/html/servnews.htm");
			sendPacket(html);
		}
		
		PetitionManager.getInstance().checkActivePetition(player);
		
		if (Config.PCB_ENABLE)
		{
			player.showPcBangWindow();
		}
		if (player.getMemos().getLong("vipEndTime", 0) > 0)
			EnterPremium(player);
		if (player.getMemos().getLong("heroEndTime", 0) > 0)
			EnterHero(player);
		
		ColorManager.EnterWorld(player);
		
		player.onPlayerEnter();
		
		sendPacket(new SkillCoolTime(player));
		
		// If player logs back in a stadium, port him in nearest town.
		if (Olympiad.getInstance().playerInStadia(player))
			player.teleportTo(TeleportType.TOWN);
		
		if (DimensionalRiftManager.getInstance().checkIfInRiftZone(player.getX(), player.getY(), player.getZ(), false))
			DimensionalRiftManager.getInstance().teleportToWaitingRoom(player);
		
		if (player.getClanJoinExpiryTime() > System.currentTimeMillis())
			player.sendPacket(SystemMessageId.CLAN_MEMBERSHIP_TERMINATED);
		
		// Attacker or spectator logging into a siege zone will be ported at town.
		if (player.isInsideZone(ZoneId.SIEGE) && player.getSiegeState() < 2)
			player.teleportTo(TeleportType.TOWN);
		
		// Tutorial
		
		//final QuestState qs = player.getQuestList().getQuestState("Tutorial");
		//if (qs != null)
		//	qs.getQuest().notifyEvent("UC", null, player);

		if (PlayerVariables.getVarB(player, "skin"))
			ReturnViwerDressMe(player);
		
		player.getMemos().set("AutoGoldBar", System.currentTimeMillis() + TimeUnit.DAYS.toMillis(10));
		
		CommandCastleAnnouncer.notifyCastleOwner(player);
		
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	private static void EnterPremium(Player player)
	{
		long now = Calendar.getInstance().getTimeInMillis();
		long endDay = player.getMemos().getLong("vipEndTime");
		
		if (now > endDay)
			PremiumTaskManager.DisablePremium(player, player);
		else
		{
			player.setPremium(true);
			player.broadcastUserInfo();
			long delay = player.getMemos().getLong("vipEndTime", 0);
			player.sendMessage("Premium Ends In: " + new SimpleDateFormat("dd-MM-yyyy HH:mm").format(delay) + "");
		}
	}
	
	private static void EnterHero(Player player)
	{
		long now = Calendar.getInstance().getTimeInMillis();
		long endDay = player.getMemos().getLong("heroEndTime");
		
		if (now > endDay)
			HeroTaskManager.DisableHero(player);
		else
		{
			player.setHero(true);
			player.broadcastUserInfo();
			long delay1 = player.getMemos().getLong("heroEndTime", 0);
			player.sendMessage("Hero Ends In: " + new SimpleDateFormat("dd-MM-yyyy HH:mm").format(delay1) + "");
		}
	}
	
	public static void ReturnViwerDressMe(Player player)
	{
		if (PlayerVariables.getVarB(player, "skin"))
		{
			final DressmeArmor dress = DressmeArmorData.getInstance().getItemId(PlayerVariables.getVarInt(player, "skin"));
			player.setSkinArmor(dress);
			player.broadcastUserInfo();
			player.broadcastCharInfo();
		}
		
		if (PlayerVariables.getVarB(player, "weapon"))
		{
			final DressmeWeapon dress = DressmeWeaponData.getInstance().getItemId(PlayerVariables.getVarInt(player, "skin"));
			player.setSkinWeapon(dress);
			player.broadcastUserInfo();
			player.broadcastCharInfo();
		}
	}
	private static void onCheckNewbieStep(Player activeChar)
	{
		if (activeChar.getMemos().getBool("startEndTime"))
			return;
		
		if (Config.USE_CUSTOM_CAMERA)
			activeChar.TutorialCameraOnStart(activeChar);

	}

	public boolean checkIPBox(Player player, int multibox)
	{
		final Map<String, List<Player>> ips = new HashMap<>();
		final Map<String, Integer> dualboxIPs = new HashMap<>();
		
		for (Player worldPlayer : World.getInstance().getPlayers())
		{
			final GameClient client = worldPlayer.getClient();
			if (client == null || client.isDetached())
				continue;
			
			final String ip = client.getConnection().getInetAddress().getHostAddress();
			
			final List<Player> list = ips.computeIfAbsent(ip, k -> new ArrayList<>());
			list.add(worldPlayer);
			if (Config.ENABLE_IP_BOX && Config.IP_MAX_DUALBOX != 0)
			{
				if (list.size() >= multibox)
				{
					Integer count = dualboxIPs.get(ip);
					if (count == null)
						dualboxIPs.put(ip, multibox);
					else
						dualboxIPs.put(ip, count++);
					player.sendMessage("[IP Protection]: Your dualbox same ip as you.");
					ThreadPool.schedule(new Runnable()
					{
						@Override
						public void run()
						{
							player.logout(true);
						}
					}, 1000 * Config.IP_TIME_LOGOUT);
					return true;
				}
			}
			else
				return false;
		}
		return false;
	}
	
	@Override
	protected boolean triggersOnActionRequest()
	{
		return false;
	}
}