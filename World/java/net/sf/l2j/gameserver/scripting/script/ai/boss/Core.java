package net.sf.l2j.gameserver.scripting.script.ai.boss;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.l2j.commons.data.StatSet;
import net.sf.l2j.commons.random.Rnd;

import net.sf.l2j.gameserver.data.SchedulingPattern;
import net.sf.l2j.gameserver.data.manager.GrandBossManager;
import net.sf.l2j.gameserver.data.manager.SpawnManager;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.Playable;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.instance.Door;
import net.sf.l2j.gameserver.model.actor.instance.GrandBoss;
import net.sf.l2j.gameserver.model.actor.instance.Monster;
import net.sf.l2j.gameserver.network.NpcStringId;
import net.sf.l2j.gameserver.network.serverpackets.PlaySound;
import net.sf.l2j.gameserver.scripting.script.ai.AttackableAIScript;
import net.sf.l2j.gameserver.scripting.script.ai.boss.config.CoreConfig;
import net.sf.l2j.gameserver.skills.L2Skill;

public class Core extends AttackableAIScript
{
	// Grand boss
	private static final int CORE = 29006;
	
	// Monsters
	private static final int DEATH_KNIGHT = 29007;
	private static final int DOOM_WRAITH = 29008;
	private static final int SUSCEPTOR = 29011;
	
	// NPCs
	private static final int TELEPORTATION_CUBE = 31842;
	
	// Doors
	private static final int CORE_DOOR = 20210001;
	private static final String CORE_DOOR_GUARDS = "core_door_guards";
	
	private static final byte ALIVE = 0; // Core is spawned.
	private static final byte DEAD = 1; // Core has been killed.
	
	private final Set<Npc> _minions = ConcurrentHashMap.newKeySet();
	
	public Core()
	{
		super("ai/boss");
		CoreConfig.init();
		
		final StatSet info = GrandBossManager.getInstance().getStatSet(CORE);
		
		switch (GrandBossManager.getInstance().getBossStatus(CORE))
		{
			case DEAD: // Launch the timer to set DORMANT, or set DORMANT directly if timer expired while offline.
				long temp = (info.getLong("respawn_time") - System.currentTimeMillis());
				if (temp > 0)
					startQuestTimer("core_unlock", null, null, temp);
				else
					GrandBossManager.getInstance().setBossStatus(CORE, DEAD);
				break;
			
			case ALIVE:
				spawnBoss();
				break;
		}
		
	}
	
	@Override
	protected void registerNpcs()
	{
		addAttacked(CORE);
		addCreated(CORE);
		addDoorChange(CORE_DOOR);
		addMyDying(CORE, DEATH_KNIGHT, DOOM_WRAITH, SUSCEPTOR);
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		
		if (name.equalsIgnoreCase("spawn_minion"))
		{
			final Monster monster = (Monster) addSpawn(npc.getNpcId(), npc, false, 0, false);
			monster.setRaidRelated();
			
			_minions.add(monster);
		}
		
		else if (name.equalsIgnoreCase("despawn_minions"))
		{
			_minions.forEach(Npc::deleteMe);
			_minions.clear();
		}
		
		else if (name.equalsIgnoreCase("core_unlock"))
		{
			spawnBoss();
		}
		return super.onTimer(name, npc, player);
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (attacker instanceof Playable)
		{
			if (npc.isScriptValue(0))
			{
				npc.setScriptValue(1);
				npc.broadcastNpcSay(NpcStringId.ID_1000001);
				npc.broadcastNpcSay(NpcStringId.ID_1000002);
			}
			else if (Rnd.get(100) == 0)
				npc.broadcastNpcSay(NpcStringId.ID_1000003);
		}
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public void onCreated(Npc npc)
	{
		
		super.onCreated(npc);
	}
	
	private void spawnBoss()
	{
		
		final GrandBoss zaken;
		final StatSet info = GrandBossManager.getInstance().getStatSet(CORE);
		
		GrandBossManager.getInstance().setBossStatus(CORE, ALIVE);
		
		zaken = (GrandBoss) addSpawn(CORE, info.getInteger("loc_x"), info.getInteger("loc_y"), info.getInteger("loc_z"), info.getInteger("heading"), false, 0, false);
		zaken.getStatus().setHpMp(info.getInteger("currentHP"), info.getInteger("currentMP"));
		
		GrandBossManager.getInstance().addBoss(zaken);
		zaken.broadcastPacket(new PlaySound(1, "BS01_A", zaken));
		
		// Spawn minions
		Monster monster;
		for (int i = 0; i < 5; i++)
		{
			int x = 16800 + i * 360;
			monster = (Monster) addSpawn(DEATH_KNIGHT, x, 110000, zaken.getZ(), 280 + Rnd.get(40), false, 0, false);
			monster.setRaidRelated();
			_minions.add(monster);
			
			monster = (Monster) addSpawn(DEATH_KNIGHT, x, 109000, zaken.getZ(), 280 + Rnd.get(40), false, 0, false);
			monster.setRaidRelated();
			_minions.add(monster);
			
			int x2 = 16800 + i * 600;
			monster = (Monster) addSpawn(DOOM_WRAITH, x2, 109300, zaken.getZ(), 280 + Rnd.get(40), false, 0, false);
			monster.setRaidRelated();
			_minions.add(monster);
		}
		
		for (int i = 0; i < 4; i++)
		{
			int x = 16800 + i * 450;
			monster = (Monster) addSpawn(SUSCEPTOR, x, 110300, zaken.getZ(), 280 + Rnd.get(40), false, 0, false);
			monster.setRaidRelated();
			_minions.add(monster);
		}
	}
	
	@Override
	public void onDoorChange(Door door)
	{
		if (door.isOpened())
			SpawnManager.getInstance().spawnEventNpcs(CORE_DOOR_GUARDS, true, false);
		else
			SpawnManager.getInstance().despawnEventNpcs(CORE_DOOR_GUARDS, false);
	}
	
	@Override
	public void onMyDying(Npc npc, Creature killer)
	{
		if (!CoreConfig.FWA_FIXTIMEPATTERNOFCORE.isEmpty())
		{
			if (npc.getNpcId() == CORE)
			{
				GrandBossManager.getInstance().setBossStatus(CORE, DEAD);
				
				npc.broadcastPacket(new PlaySound(1, "BS02_D", npc));
				npc.broadcastNpcSay(NpcStringId.ID_1000004);
				npc.broadcastNpcSay(NpcStringId.ID_1000005);
				npc.broadcastNpcSay(NpcStringId.ID_1000006);
				
				addSpawn(TELEPORTATION_CUBE, 16502, 110165, -6394, 0, false, 900000, false);
				addSpawn(TELEPORTATION_CUBE, 18948, 110166, -6397, 0, false, 900000, false);
				
				startQuestTimer("core_unlock", null, null, getRespawnInterval());
				final StatSet info = GrandBossManager.getInstance().getStatSet(CORE);
				info.set("respawn_time", System.currentTimeMillis() + getRespawnInterval());
				GrandBossManager.getInstance().setStatSet(CORE, info);
				
				startQuestTimer("despawn_minions", null, null, 20000);
				cancelQuestTimers("spawn_minion");
			}
		}
		super.onMyDying(npc, killer);
	}
	
	private static long getRespawnInterval()
	{
		SchedulingPattern timePattern = null;
		
		long now = System.currentTimeMillis();
		try
		{
			timePattern = new SchedulingPattern(CoreConfig.FWA_FIXTIMEPATTERNOFCORE);
			long delay = timePattern.next(now) - now;
			return Math.max(60000, delay);
		}
		catch (SchedulingPattern.InvalidPatternException ipe)
		{
			throw new RuntimeException("Invalid respawn data \"" + CoreConfig.FWA_FIXTIMEPATTERNOFCORE + "\" in " + Core.class.getSimpleName(), ipe);
		}
	}
}