package net.sf.l2j.gameserver.scripting.script.ai.boss;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import net.sf.l2j.commons.data.StatSet;
import net.sf.l2j.commons.logging.CLogger;
import net.sf.l2j.commons.pool.ConnectionPool;
import net.sf.l2j.commons.random.Rnd;

import net.sf.l2j.gameserver.data.SchedulingPattern;
import net.sf.l2j.gameserver.data.manager.GrandBossManager;
import net.sf.l2j.gameserver.data.manager.ZoneManager;
import net.sf.l2j.gameserver.data.xml.DoorData;
import net.sf.l2j.gameserver.data.xml.NpcData;
import net.sf.l2j.gameserver.enums.SayType;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.instance.GrandBoss;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.model.spawn.Spawn;
import net.sf.l2j.gameserver.model.zone.type.BossZone;
import net.sf.l2j.gameserver.network.serverpackets.CreatureSay;
import net.sf.l2j.gameserver.scripting.script.ai.AttackableAIScript;
import net.sf.l2j.gameserver.scripting.script.ai.boss.config.VanHalterConfig;
import net.sf.l2j.gameserver.skills.L2Skill;

public class VanHalter extends AttackableAIScript
{
	protected static final CLogger LOGGER = new CLogger(VanHalter.class.getName());
	
	@SuppressWarnings("unused")
	private static final BossZone ZONE = ZoneManager.getInstance().getZoneById(110016, BossZone.class);
	
	private static List<Player> _playersInside = new CopyOnWriteArrayList<>();
	
	private static final int ANDREAS_VAN_HALTER = 29062; // Raid Boss
	private static final int ANDREAS_CAPTAIN = 22188; // Minions Boss
	
	public static final byte ALIVE = 0; // Van halter is spawned and no one has entered yet. Entry is unlocked.
	public static final byte DEAD = 1; // Van halter has been killed. Entry is locked.
	
	// Spawn Creatures & Minions
	protected List<Npc> _royalGuard = new ArrayList<>();
	protected List<Npc> _royalGuardCaptain = new ArrayList<>();
	
	protected List<Spawn> _royalGuardSpawn = new ArrayList<>();
	protected List<Spawn> _royalGuardCaptainSpawn = new ArrayList<>();
	protected Spawn _ritualSacrificeSpawn = null;
	protected Spawn _vanHalterSpawn = null;
	
	private final Set<Npc> _Monster = ConcurrentHashMap.newKeySet();
	
	private static int _killRoyalGuard = 0;
	private static int _killHingPriest = 0;
	private static final int[] TRIOLS =
	{
		32058,
		32059,
		32060,
		32061,
		32062,
		32063,
		32064,
		32065,
		32066
	};
	
	private static final int[] ROYAL_GUARD =
	{
		22176,
		22155
	};
	
	private static final int[] ATTAK_CREATURE_CLOSE_DOOR =
	{
		22188,
		22189,
		22190
	};
	
	public VanHalter()
	{
		super("ai/boss");
		
		final StatSet info = GrandBossManager.getInstance().getStatSet(ANDREAS_VAN_HALTER);
		
		switch (GrandBossManager.getInstance().getBossStatus(ANDREAS_VAN_HALTER))
		{
			case DEAD: // Launch the timer to set DORMANT, or set DORMANT directly if timer expired while offline.
				long temp = (info.getLong("respawn_time") - System.currentTimeMillis());
				if (temp > 0)
					startQuestTimer("van_unlock", null, null, temp);
				else
					GrandBossManager.getInstance().setBossStatus(ANDREAS_VAN_HALTER, DEAD);
				
				deleteRoyalGuard();
				break;
			
			case ALIVE:
				spawnBoss();
				break;
		}
		
		_killRoyalGuard = 0;
		_killHingPriest = 0;
	}
	
	@Override
	protected void registerNpcs()
	{
		
		addAttacked(ANDREAS_VAN_HALTER);
		addMyDying(ANDREAS_VAN_HALTER, ANDREAS_CAPTAIN);
		
		for (int GUARD_ALTAR : ATTAK_CREATURE_CLOSE_DOOR)
			addAttacked(GUARD_ALTAR);
		
		for (int TRIOL : TRIOLS)
			addMyDying(TRIOL);
		
		for (int TOYAL : ROYAL_GUARD)
			addMyDying(TOYAL);
		
	}
	
	Map<Integer, Long> closeDoor = new ConcurrentHashMap<>();
	long currentTime = System.currentTimeMillis();
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		Player player = attacker.getActingPlayer();
		if (player == null || !_Monster.contains(npc))
			return;
		
		if (npc.isInvul())
			return;
		
		if (closeDoor.getOrDefault(player.getObjectId(), 0L) > currentTime)
		{
			player.sendMessage("You cannot use it so often!");
			return;
		}
		
		if (npc.getNpcId() == 22188 || npc.getNpcId() == 22189 || npc.getNpcId() == 22190)
		{
			npc.broadcastPacket(new CreatureSay(npc.getObjectId(), SayType.SHOUT, npc.getName(), "The altar doors were close."));
			// ramp
			DoorData.getInstance().getDoor(19160014).closeMe();
			DoorData.getInstance().getDoor(19160015).closeMe();
			// altar
			DoorData.getInstance().getDoor(19160016).closeMe();
			DoorData.getInstance().getDoor(19160017).closeMe();
			
			closeDoor.put(player.getObjectId(), currentTime + TimeUnit.SECONDS.toMillis(15L));
			
		}
		
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public void onCreated(Npc npc)
	{
		switch (npc.getNpcId())
		{
			case ANDREAS_VAN_HALTER:
				
				break;
		}
		
		super.onCreated(npc);
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{

		if (name.equalsIgnoreCase("clean"))
		{
			
			// Monster Creatures
			_killRoyalGuard = 0;
			_killHingPriest = 0;
			// Players Insides
			_playersInside.clear();
			
			// Delete all monsters and clean the list.
			if (!_Monster.isEmpty())
			{
				_Monster.forEach(Npc::deleteMe);
				_Monster.clear();
			}
		}
		else if (name.equalsIgnoreCase("open"))
		{
			
			DoorData.getInstance().getDoor(19160016).openMe();
			DoorData.getInstance().getDoor(19160017).openMe();
		}
		else if (name.equalsIgnoreCase("close"))
		{
			for (int i = 19160001; i <= 19160009; i++)
				DoorData.getInstance().getDoor(i).closeMe();
			
			for (int i = 19160010; i <= 19160012; i++)
				DoorData.getInstance().getDoor(i).closeMe();
			
			DoorData.getInstance().getDoor(19160013).closeMe();
			DoorData.getInstance().getDoor(19160014).closeMe();
			DoorData.getInstance().getDoor(19160015).closeMe();
			DoorData.getInstance().getDoor(19160016).closeMe();
			DoorData.getInstance().getDoor(19160017).closeMe();
		}
		else if (name.equalsIgnoreCase("van_unlock"))
			spawnBoss();
		return super.onTimer(name, npc, player);
	}
	
	@Override
	public void onMyDying(Npc npc, Creature killer)
	{
		Player player = killer.getActingPlayer();
		if (player == null)
			return;
		
		if (VanHalterConfig.HING_PRIESTLIST_MONSTERS.contains(Integer.valueOf(npc.getNpcId())))
		{
			if (Rnd.get(100) < VanHalterConfig.HingPriestChanceDrop)
				player.addItem("Quest", VanHalterConfig.HingPriestDropItemId, VanHalterConfig.HingPriestAmountDrop, npc, true);
			
			_killHingPriest++;
			
			if (Rnd.get(100) > 33)
			{
				
				int randomChat = Rnd.get(3);
				switch (randomChat)
				{
					case 0:
						player.sendMessage("Creature in Pagan Temple " + "Triols Believer" + " previous room drop Chapel Key" + ".");
						
					case 1:
						player.sendMessage("Defeat " + npc.getName() + " altar doors will open, cont creatures dead ( " + _killHingPriest + " )");
						player.sendMessage("Amount Creature door open, " + VanHalterConfig.CREATURE_HINGPRINT_CONT);
						
						break;
					default:
						player.sendMessage("Defeat " + npc.getName() + " altar doors will open, dead creatures ( " + _killHingPriest + " )");
						player.sendMessage("Amount creature door open," + VanHalterConfig.CREATURE_HINGPRINT_CONT);
						
						break;
				}
			}
			
			if (_killHingPriest == VanHalterConfig.CREATURE_HINGPRINT_CONT)
			{
				npc.broadcastPacket(new CreatureSay(npc.getObjectId(), SayType.SHOUT, npc.getName(), "The altar doors were opened."));
				
				DoorData.getInstance().getDoor(19160014).openMe();
				DoorData.getInstance().getDoor(19160015).openMe();
			}
		}
		if (npc.getNpcId() == 22176)
		{
			_killRoyalGuard++;
			if (_killRoyalGuard == 6)
			{
				npc.broadcastPacket(new CreatureSay(npc.getObjectId(), SayType.SHOUT, npc.getName(), "The altar doors were opened."));
				
				DoorData.getInstance().getDoor(19160016).openMe();
				DoorData.getInstance().getDoor(19160017).openMe();
				startQuestTimer("close", null, null, 1000 * 30);
			}
			
		}
		if (npc.getNpcId() == 22188 || npc.getNpcId() == 22189 || npc.getNpcId() == 22190)
		{
			DoorData.getInstance().getDoor(19160016).closeMe();
			DoorData.getInstance().getDoor(19160017).closeMe();
			
		}
		if (npc.getNpcId() == ANDREAS_VAN_HALTER)
		{
			if (!VanHalterConfig.FWA_FIXTIMEPATTERNOFVANHALTER.isEmpty())
			{
				
				startQuestTimer("clean", npc, null, 1000 * 3);
				startQuestTimer("close", null, null, 1000 * 60);
				
				startQuestTimer("open", null, null, 1000 * 5);
				
				GrandBossManager.getInstance().setBossStatus(ANDREAS_VAN_HALTER, DEAD);
				
			
				final StatSet info = GrandBossManager.getInstance().getStatSet(ANDREAS_VAN_HALTER);
				info.set("respawn_time", System.currentTimeMillis() + getRespawnInterval());
				GrandBossManager.getInstance().setStatSet(ANDREAS_VAN_HALTER, info);
				startQuestTimer("van_unlock", null, null, getRespawnInterval());
				deleteRoyalGuard();
			}
			
		}
		
		super.onMyDying(npc, killer);
	}
	
	protected void loadRoyalGuardCaptain()
	{
		_royalGuardCaptainSpawn.clear();
		
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT id, count, npc_templateid, locx, locy, locz, heading, respawn_delay FROM vanhalter_spawnlist WHERE npc_templateid = ? ORDER BY id"))
		{
			statement.setInt(1, 22188);
			
			try (ResultSet rset = statement.executeQuery())
			{
				while (rset.next())
				{
					
					NpcTemplate template = NpcData.getInstance().getTemplate(rset.getInt("npc_templateid"));
					
					for (int i = 0; i < 1; i++)
					{
						final Npc temp = addSpawn(template.getNpcId(), rset.getInt("locx"), rset.getInt("locy"), rset.getInt("locz"), rset.getInt("heading"), true, 0, false);
						temp.getAI().tryToActive();
						temp.forceRunStance();
						_Monster.add(temp);
					}
					
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	protected void deleteRoyalGuard()
	{
		for (Npc rg : _Monster)
		{
			rg.getSpawn().setRespawnState(false);
			rg.getSpawn().doDelete();
			rg.deleteMe();
		}
		
		_Monster.clear();
	}
	
	protected void loadRoyalGuard()
	{
		_royalGuardSpawn.clear();
		
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT id, count, npc_templateid, locx, locy, locz, heading, respawn_delay FROM vanhalter_spawnlist WHERE npc_templateid between ? and ? ORDER BY id"))
		{
			statement.setInt(1, 22175);
			statement.setInt(2, 22176);
			
			try (ResultSet rset = statement.executeQuery())
			{
				while (rset.next())
				{
					
					NpcTemplate template = NpcData.getInstance().getTemplate(rset.getInt("npc_templateid"));
					
					for (int i = 0; i < 1; i++)
					{
						final Npc temp = addSpawn(template.getNpcId(), rset.getInt("locx"), rset.getInt("locy"), rset.getInt("locz"), rset.getInt("heading"), true, 0, false);
						temp.getAI().tryToActive();
						temp.forceRunStance();
						_Monster.add(temp);
						
					}
					
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private void spawnBoss()
	{
		
		final GrandBoss queen;
		final StatSet info = GrandBossManager.getInstance().getStatSet(ANDREAS_VAN_HALTER);
		
		GrandBossManager.getInstance().setBossStatus(ANDREAS_VAN_HALTER, ALIVE);
		
		queen = (GrandBoss) addSpawn(ANDREAS_VAN_HALTER, -16550, -53562, -10448, info.getInteger("heading"), false, 0, false);
		queen.getStatus().setHpMp(info.getInteger("currentHP"), info.getInteger("currentMP"));
		
		GrandBossManager.getInstance().addBoss(queen);
//		loadRoyalGuard();
//		loadRoyalGuardCaptain();
	}
	
	private static long getRespawnInterval()
	{
		SchedulingPattern timePattern = null;
		
		long now = System.currentTimeMillis();
		try
		{
			timePattern = new SchedulingPattern(VanHalterConfig.FWA_FIXTIMEPATTERNOFVANHALTER);
			long delay = timePattern.next(now) - now;
			return Math.max(60000, delay);
		}
		catch (SchedulingPattern.InvalidPatternException ipe)
		{
			throw new RuntimeException("Invalid respawn data \"" + VanHalterConfig.FWA_FIXTIMEPATTERNOFVANHALTER + "\" in " + VanHalter.class.getSimpleName(), ipe);
		}
	}
}