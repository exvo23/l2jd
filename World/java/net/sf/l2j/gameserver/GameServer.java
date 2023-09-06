package net.sf.l2j.gameserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.logging.LogManager;

import net.sf.l2j.commons.lang.StringUtil;
import net.sf.l2j.commons.logging.CLogger;
import net.sf.l2j.commons.mmocore.SelectorConfig;
import net.sf.l2j.commons.mmocore.SelectorThread;
import net.sf.l2j.commons.pool.ConnectionPool;
import net.sf.l2j.commons.pool.ThreadPool;
import net.sf.l2j.commons.util.SysUtil;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.communitybbs.CommunityBoard;
import net.sf.l2j.gameserver.communitybbs.manager.custom.AuctionBBSManager;
import net.sf.l2j.gameserver.data.SkillTable;
import net.sf.l2j.gameserver.data.cache.CrestCache;
import net.sf.l2j.gameserver.data.cache.HtmCache;
import net.sf.l2j.gameserver.data.manager.BoatManager;
import net.sf.l2j.gameserver.data.manager.BufferManager;
import net.sf.l2j.gameserver.data.manager.BuyListManager;
import net.sf.l2j.gameserver.data.manager.CastleManager;
import net.sf.l2j.gameserver.data.manager.CastleManorManager;
import net.sf.l2j.gameserver.data.manager.ClanHallManager;
import net.sf.l2j.gameserver.data.manager.CoupleManager;
import net.sf.l2j.gameserver.data.manager.CrownManager;
import net.sf.l2j.gameserver.data.manager.CursedWeaponManager;
import net.sf.l2j.gameserver.data.manager.DerbyTrackManager;
import net.sf.l2j.gameserver.data.manager.DimensionalRiftManager;
import net.sf.l2j.gameserver.data.manager.FestivalOfDarknessManager;
import net.sf.l2j.gameserver.data.manager.FishingChampionshipManager;
import net.sf.l2j.gameserver.data.manager.GrandBossManager;
import net.sf.l2j.gameserver.data.manager.HeroManager;
import net.sf.l2j.gameserver.data.manager.PCCafePoint;
import net.sf.l2j.gameserver.data.manager.PartyMatchRoomManager;
import net.sf.l2j.gameserver.data.manager.PetitionManager;
import net.sf.l2j.gameserver.data.manager.RaidBossSpawnManager;
import net.sf.l2j.gameserver.data.manager.RaidPointManager;
import net.sf.l2j.gameserver.data.manager.SevenSignsManager;
import net.sf.l2j.gameserver.data.manager.SpawnManager;
import net.sf.l2j.gameserver.data.manager.ZoneManager;
import net.sf.l2j.gameserver.data.sql.BookmarkTable;
import net.sf.l2j.gameserver.data.sql.ClanTable;
import net.sf.l2j.gameserver.data.sql.OfflineTable;
import net.sf.l2j.gameserver.data.sql.PlayerInfoTable;
import net.sf.l2j.gameserver.data.sql.ServerMemoTable;
import net.sf.l2j.gameserver.data.sql.SpawnTable;
import net.sf.l2j.gameserver.data.xml.AdminData;
import net.sf.l2j.gameserver.data.xml.AnnouncementData;
import net.sf.l2j.gameserver.data.xml.ArmorSetData;
import net.sf.l2j.gameserver.data.xml.AugmentationData;
import net.sf.l2j.gameserver.data.xml.DoorData;
import net.sf.l2j.gameserver.data.xml.DressmeArmorData;
import net.sf.l2j.gameserver.data.xml.DressmeArmorJewels;
import net.sf.l2j.gameserver.data.xml.DressmeWeaponData;
import net.sf.l2j.gameserver.data.xml.EnchantData;
import net.sf.l2j.gameserver.data.xml.FishData;
import net.sf.l2j.gameserver.data.xml.HealSpsData;
import net.sf.l2j.gameserver.data.xml.HennaData;
import net.sf.l2j.gameserver.data.xml.IconTable;
import net.sf.l2j.gameserver.data.xml.InstantTeleportData;
import net.sf.l2j.gameserver.data.xml.ItemData;
import net.sf.l2j.gameserver.data.xml.ItemRestrictionData;
import net.sf.l2j.gameserver.data.xml.ManorAreaData;
import net.sf.l2j.gameserver.data.xml.MapRegionData;
import net.sf.l2j.gameserver.data.xml.MissionData;
import net.sf.l2j.gameserver.data.xml.MultisellData;
import net.sf.l2j.gameserver.data.xml.NewbieBuffData;
import net.sf.l2j.gameserver.data.xml.NpcData;
import net.sf.l2j.gameserver.data.xml.ObserverGroupData;
import net.sf.l2j.gameserver.data.xml.PlayerData;
import net.sf.l2j.gameserver.data.xml.PlayerLevelData;
import net.sf.l2j.gameserver.data.xml.RecipeData;
import net.sf.l2j.gameserver.data.xml.ScriptData;
import net.sf.l2j.gameserver.data.xml.SkillTreeData;
import net.sf.l2j.gameserver.data.xml.SoulCrystalData;
import net.sf.l2j.gameserver.data.xml.SpellbookData;
import net.sf.l2j.gameserver.data.xml.StaticObjectData;
import net.sf.l2j.gameserver.data.xml.SummonItemData;
import net.sf.l2j.gameserver.data.xml.TeleportData;
import net.sf.l2j.gameserver.data.xml.WalkerRouteData;
import net.sf.l2j.gameserver.events.l2jdev.EventBuffer;
import net.sf.l2j.gameserver.events.l2jdev.EventManager;
import net.sf.l2j.gameserver.events.l2jdev.EventStats;
import net.sf.l2j.gameserver.events.l2jdev.InitialPartyFarm;
import net.sf.l2j.gameserver.geoengine.GeoEngine;
import net.sf.l2j.gameserver.handler.AdminCommandHandler;
import net.sf.l2j.gameserver.handler.ChatHandler;
import net.sf.l2j.gameserver.handler.ItemHandler;
import net.sf.l2j.gameserver.handler.SkillHandler;
import net.sf.l2j.gameserver.handler.TargetHandler;
import net.sf.l2j.gameserver.handler.UserCommandHandler;
import net.sf.l2j.gameserver.handler.VoicedCommandHandler;
import net.sf.l2j.gameserver.idfactory.IdFactory;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.boat.BoatGiranTalking;
import net.sf.l2j.gameserver.model.boat.BoatGludinRune;
import net.sf.l2j.gameserver.model.boat.BoatInnadrilTour;
import net.sf.l2j.gameserver.model.boat.BoatRunePrimeval;
import net.sf.l2j.gameserver.model.boat.BoatTalkingGludin;
import net.sf.l2j.gameserver.model.olympiad.Olympiad;
import net.sf.l2j.gameserver.model.olympiad.OlympiadGameManager;
import net.sf.l2j.gameserver.network.GameClient;
import net.sf.l2j.gameserver.network.GamePacketHandler;
import net.sf.l2j.gameserver.taskmanager.AttackStanceTaskManager;
import net.sf.l2j.gameserver.taskmanager.AutofarmResetTaskManager;
import net.sf.l2j.gameserver.taskmanager.DecayTaskManager;
import net.sf.l2j.gameserver.taskmanager.GameTimeTaskManager;
import net.sf.l2j.gameserver.taskmanager.HeroTaskManager;
import net.sf.l2j.gameserver.taskmanager.ItemsOnGroundTaskManager;
import net.sf.l2j.gameserver.taskmanager.PremiumTaskManager;
import net.sf.l2j.gameserver.taskmanager.PvpFlagTaskManager;
import net.sf.l2j.gameserver.taskmanager.RandomAnimationTaskManager;
import net.sf.l2j.gameserver.taskmanager.ShadowItemTaskManager;
import net.sf.l2j.gameserver.taskmanager.WaterTaskManager;
import net.sf.l2j.util.DeadLockDetector;
import net.sf.l2j.util.IPv4Filter;

public class GameServer
{
	private static final CLogger LOGGER = new CLogger(GameServer.class.getName());
	
	private final SelectorThread<GameClient> _selectorThread;
	
	private static GameServer _gameServer;
	
	public static void main(String[] args) throws Exception
	{
		_gameServer = new GameServer();
	}
	final long serverLoadStart = System.currentTimeMillis();
	public GameServer() throws Exception
	{
		
		// Create log folder
		new File("./log").mkdir();
		new File("./log/chat").mkdir();
		new File("./log/console").mkdir();
		new File("./log/error").mkdir();
		new File("./log/gmaudit").mkdir();
		new File("./log/item").mkdir();
		new File("./data/crests").mkdirs();
		
		// Create input stream for log file -- or store file data into memory
		try (InputStream is = new FileInputStream(new File("config/logging.properties")))
		{
			LogManager.getLogManager().readConfiguration(is);
		}
		
		StringUtil.printSection("Config");
		Config.loadGameServer();
		
		StringUtil.printSection("Poolers");
		ConnectionPool.init();
		ThreadPool.init();
		
		StringUtil.printSection("IdFactory");
		IdFactory.getInstance();
		
		StringUtil.printSection("Cache");
		HtmCache.getInstance();
		CrestCache.getInstance();
		
		StringUtil.printSection("World");
		World.getInstance();
		MapRegionData.getInstance();
		AnnouncementData.getInstance();
		ServerMemoTable.getInstance();

		StringUtil.printSection("Skills");
		SkillTable.getInstance();
		SkillTreeData.getInstance();
		
		StringUtil.printSection("Items");
		ItemData.getInstance();
		SummonItemData.getInstance();
		HennaData.getInstance();
		BuyListManager.getInstance();
		MultisellData.getInstance();
		RecipeData.getInstance();
		ArmorSetData.getInstance();
		FishData.getInstance();
		SpellbookData.getInstance();
		SoulCrystalData.getInstance();
		AugmentationData.getInstance();
		CursedWeaponManager.getInstance();
		IconTable.getInstance();
		DressmeArmorData.getInstance();
		DressmeWeaponData.getInstance();
		DressmeArmorJewels.getInstance();

		EnchantData.getInstance();
		AuctionBBSManager.getInstance().load();
		ItemRestrictionData.getInstance();
		MissionData.getInstance().load();
		
		StringUtil.printSection("Admins");
		AdminData.getInstance();
		BookmarkTable.getInstance();
		PetitionManager.getInstance();
		
		StringUtil.printSection("Characters");
		PlayerData.getInstance();
		PlayerInfoTable.getInstance();
		PlayerLevelData.getInstance();
		PartyMatchRoomManager.getInstance();
		RaidPointManager.getInstance();
		HealSpsData.getInstance();
	
		StringUtil.printSection("Community server");
		if (Config.ENABLE_CB_CUSTOM)
		{
			LOGGER.info("Loaded {Community Bord} Custom.");
			CommunityBoard.getInstance();
		}
		
		StringUtil.printSection("Clans");
		ClanTable.getInstance();
		
		StringUtil.printSection("Geodata & Pathfinding");
		GeoEngine.getInstance();
		
		StringUtil.printSection("Zones");
		ZoneManager.getInstance();
		
		StringUtil.printSection("Castles & Clan Halls");
		CastleManager.getInstance();
		ClanHallManager.getInstance();
		CrownManager.getInstance();
		
		StringUtil.printSection("Task Managers");
		AttackStanceTaskManager.getInstance();
		DecayTaskManager.getInstance();
		GameTimeTaskManager.getInstance();
		ItemsOnGroundTaskManager.getInstance();
		PvpFlagTaskManager.getInstance();
		RandomAnimationTaskManager.getInstance();
		ShadowItemTaskManager.getInstance();
		WaterTaskManager.getInstance();
		PremiumTaskManager.getInstance();
		HeroTaskManager.getInstance();
		AutofarmResetTaskManager.getInstance();
		
		StringUtil.printSection("Seven Signs");
		SevenSignsManager.getInstance();
		FestivalOfDarknessManager.getInstance();
		
		StringUtil.printSection("Manor Manager");
		ManorAreaData.getInstance();
		CastleManorManager.getInstance();
		
		StringUtil.printSection("NPCs");
		BufferManager.getInstance();
		//NpcData.getInstance();
		WalkerRouteData.getInstance();
		DoorData.getInstance().spawn();
		StaticObjectData.getInstance();
		//SpawnManager.getInstance();
		GrandBossManager.getInstance();
		DimensionalRiftManager.getInstance();
		NewbieBuffData.getInstance();
		InstantTeleportData.getInstance();
		TeleportData.getInstance();
		ObserverGroupData.getInstance();
		//SpawnTable.getInstance();
		CastleManager.getInstance().loadArtifacts();
		//RaidBossSpawnManager.getInstance();
		
		StringUtil.printSection("Olympiads & Heroes");
		OlympiadGameManager.getInstance();
		Olympiad.getInstance();
		HeroManager.getInstance();
		
		StringUtil.printSection("Quests & Scripts");
		NpcData.getInstance();
		ScriptData.getInstance();
		SpawnManager.getInstance();
		SpawnTable.getInstance();
		
		RaidBossSpawnManager.getInstance();
		StringUtil.printSection("Party Farm");
		InitialPartyFarm.getInstance().StartCalculationOfNextEventTime();
		
		if (Config.ALLOW_BOAT)
		{
			BoatManager.getInstance();
			BoatGiranTalking.load();
			BoatGludinRune.load();
			BoatInnadrilTour.load();
			BoatRunePrimeval.load();
			BoatTalkingGludin.load();
		}
		StringUtil.printSection("Offline");
		if (Config.OFFLINE_RESTORE && (Config.OFFLINE_TRADE_ENABLE || Config.OFFLINE_CRAFT_ENABLE))
			OfflineTable.getInstance().restore();

		StringUtil.printSection("Events");
		DerbyTrackManager.getInstance();
		//LotteryManager.getInstance(); // Disable Temporal
		
		EventManager.getInstance();
		EventStats.getInstance();
		if (EventManager.getInstance().getBoolean("eventBufferEnabled"))
			EventBuffer.getInstance();
		
		if (Config.PCB_ENABLE)
		{
			StringUtil.printSection("Point Cafe");
			ThreadPool.scheduleAtFixedRate(PCCafePoint.getInstance(), Config.PCB_INTERVAL * 1000, Config.PCB_INTERVAL * 1000);
		}
		if (Config.ALLOW_WEDDING)
			CoupleManager.getInstance();
		
		if (Config.ALLOW_FISH_CHAMPIONSHIP)
			FishingChampionshipManager.getInstance();
		
		StringUtil.printSection("Spawns");
		SpawnManager.getInstance().spawn();
		
		StringUtil.printSection("Handlers");
		LOGGER.info("Loaded {} admin command handlers.", AdminCommandHandler.getInstance().size());
		LOGGER.info("Loaded {} chat handlers.", ChatHandler.getInstance().size());
		LOGGER.info("Loaded {} item handlers.", ItemHandler.getInstance().size());
		LOGGER.info("Loaded {} skill handlers.", SkillHandler.getInstance().size());
		LOGGER.info("Loaded {} target handlers.", TargetHandler.getInstance().size());
		LOGGER.info("Loaded {} user command handlers.", UserCommandHandler.getInstance().size());
		LOGGER.info("Loaded {} voiced command handlers.", VoicedCommandHandler.getInstance().size());
		LOGGER.info("========================================================");
		LOGGER.info("Hospede seu servidor na L2JCenter.com");
		LOGGER.info("Cupom L2JBAN e ganhe 10% de desconto na primeira compra.");
		LOGGER.info("========================================================");
		LOGGER.info("Hospede seu servidor na LinusHost.com.br");
		LOGGER.info("Servidores de alta performance e protetion DDoS premium.");
		LOGGER.info("Cupom L2JBAN e ganhe 10% de desconto recorrente.");
		LOGGER.info("========================================================");
		
		StringUtil.printSection("System");
		Runtime.getRuntime().addShutdownHook(Shutdown.getInstance());
		
		if (Config.DEADLOCK_DETECTOR)
		{
			LOGGER.info("Deadlock detector is enabled. Timer: {}s.", Config.DEADLOCK_CHECK_INTERVAL);
			
			final DeadLockDetector deadDetectThread = new DeadLockDetector();
			deadDetectThread.setDaemon(true);
			deadDetectThread.start();
		}
		else
			LOGGER.info("Deadlock detector is disabled.");
		
		LOGGER.info("Gameserver has started, used memory: {} / {} Mo.", SysUtil.getUsedMemory(), SysUtil.getMaxMemory());
		LOGGER.info("Maximum allowed players: {}.", Config.MAXIMUM_ONLINE_USERS);
		LOGGER.info("Server Loaded in " + ((System.currentTimeMillis() - serverLoadStart) / 1000) + " seconds.");
		
		StringUtil.printSection("Login");
		LoginServerThread.getInstance().start();
		
		final SelectorConfig sc = new SelectorConfig();
		sc.MAX_READ_PER_PASS = Config.MMO_MAX_READ_PER_PASS;
		sc.MAX_SEND_PER_PASS = Config.MMO_MAX_SEND_PER_PASS;
		sc.SLEEP_TIME = Config.MMO_SELECTOR_SLEEP_TIME;
		sc.HELPER_BUFFER_COUNT = Config.MMO_HELPER_BUFFER_COUNT;
		
		final GamePacketHandler handler = new GamePacketHandler();
		_selectorThread = new SelectorThread<>(sc, handler, handler, handler, new IPv4Filter());
		
		InetAddress bindAddress = null;
		if (!Config.GAMESERVER_HOSTNAME.equals("*"))
		{
			try
			{
				bindAddress = InetAddress.getByName(Config.GAMESERVER_HOSTNAME);
			}
			catch (Exception e)
			{
				LOGGER.error("The GameServer bind address is invalid, using all available IPs.", e);
			}
		}
		
		try
		{
			_selectorThread.openServerSocket(bindAddress, Config.GAMESERVER_PORT);
		}
		catch (Exception e)
		{
			LOGGER.error("Failed to open server socket.", e);
			System.exit(1);
		}
		_selectorThread.start();
	}
	
	public static GameServer getInstance()
	{
		return _gameServer;
	}
	
	public SelectorThread<GameClient> getSelectorThread()
	{
		return _selectorThread;
	}
}