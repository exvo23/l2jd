package net.sf.l2j.gameserver.data.manager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.l2j.commons.data.StatSet;
import net.sf.l2j.commons.pool.ConnectionPool;
import net.sf.l2j.commons.pool.ThreadPool;
import net.sf.l2j.commons.random.Rnd;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.data.sql.SpawnTable;
import net.sf.l2j.gameserver.data.xml.NpcData;
import net.sf.l2j.gameserver.geoengine.GeoEngine;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.instance.RaidBoss;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.model.location.Location;
import net.sf.l2j.gameserver.model.spawn.Spawn;
import net.sf.l2j.gameserver.taskmanager.GameTimeTaskManager;
import net.sf.l2j.gameserver.taskmanager.PvpFlagTaskManager;

import javolution.util.FastMap;

/**
 * @author juven
 */
public class RaidBossSpawnManager
{
	protected final static Logger _log = Logger.getLogger(RaidBossSpawnManager.class.getName());
	
	protected static Map<Integer, RaidBoss> _bosses = new HashMap<>();
	protected static Map<Integer, Spawn> _spawns = new HashMap<>();
	protected static Map<Integer, StatSet> _storedInfo = new HashMap<>();
	protected static Map<Integer, ScheduledFuture<?>> _schedules = new HashMap<>();
	
	public static enum StatusEnum
	{
		ALIVE,
		DEAD,
		UNDEFINED
	}
	
	public RaidBossSpawnManager()
	{
		init();
	}
	
	private static void checkArea()
	{
		final Collection<Player> pls = World.getInstance().getAllPlayers().values();
		for (Player player : pls)
		{
			if (player == null || !player.isOnline() || player.getClient() == null || player.getClient().isDetached())
			{
				continue;
			}

			for (Npc npc : player.getKnownTypeInRadius(RaidBoss.class, Config.BOSS_CHAOTIC_RANGED))
			{
				if (npc != null)
				{

					// System.out.println("asss");
					if (player.getPvpFlag() == 0)
					{
						player.setPvpFlag(1);
						PvpFlagTaskManager.getInstance().add(player, Config.PVP_NORMAL_TIME);
						player.broadcastUserInfo();
					}
					else
					{
						player.setPvpFlag(1);
						PvpFlagTaskManager.getInstance().add(player, Config.PVP_NORMAL_TIME);
					}
				}
			}
		}
	}
	
	public static RaidBossSpawnManager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	@SuppressWarnings("resource")
	private void init()
	{
		if(Config.BOSS_CHAOTIC_ENABLE)
		ThreadPool.scheduleAtFixedRate(() -> checkArea(), 1 * 1000, 1 * 1000); // seconds
		_bosses = new FastMap<>();
		_schedules = new FastMap<>();
		_storedInfo = new FastMap<>();
		_spawns = new FastMap<>();
		
		try (Connection con = ConnectionPool.getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT * from raidboss_spawnlist ORDER BY boss_id");
			ResultSet rset = statement.executeQuery();
			int x = 0;
			int y = 0;
			int z = 0;
			long respawnTime;
			while (rset.next())
			{
				final NpcTemplate template = getValidTemplate(rset.getInt("boss_id"));
				if (template != null)
				{
					final Spawn spawnDat = new Spawn(template);
					// Get new coordinates.
					final int nx = rset.getInt("loc_x");
					final int ny = rset.getInt("loc_y");
					
					// Validate new coordinates.
					Location loc = GeoEngine.getInstance().getValidLocation(rset.getInt("loc_x"), rset.getInt("loc_y"), rset.getInt("loc_z"), nx, ny, rset.getInt("loc_z"), null);
					x = loc.getX();
					y = loc.getY();
					z = loc.getZ();
					spawnDat.setLoc(x, y, z, rset.getInt("heading"));
					
					spawnDat.setRespawnDelay(rset.getInt("spawn_time"));
					respawnTime = rset.getLong("random_time");
					
					addNewSpawn(spawnDat, rset.getLong("respawn_time"), rset.getDouble("currentHP"), rset.getDouble("currentMP"), false);
					updateStoredInfo(spawnDat, respawnTime, rset.getDouble("currentHP"), rset.getDouble("currentMP"));
				}
				else
				{
					_log.warning("RaidBossSpawnManager: Could not load raidboss #" + rset.getInt("boss_id") + " from DB");
				}
			}
			
			_log.info("RaidBossSpawnManager: Loaded " + _bosses.size() + " instances.");
			_log.info("RaidBossSpawnManager: Scheduled " + _schedules.size() + " instances.");
			
			rset.close();
			statement.close();
		}
		catch (SQLException e)
		{
			_log.warning("RaidBossSpawnManager: Couldnt load raidboss_spawnlist table.");
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "Error while initializing RaidBossSpawnManager: " + e.getMessage(), e);
		}
		
	}
	
	private static class spawnSchedule implements Runnable
	{
		private final int bossId;
		
		public spawnSchedule(int npcId)
		{
			bossId = npcId;
		}
		
		@Override
		public void run()
		{
			RaidBoss raidboss = null;
			
			if (bossId == 25328)
			{
				if (GameTimeTaskManager.getInstance().isNight())
					raidboss = (RaidBoss) _spawns.get(bossId).doSpawn(false);
			}
			else
				raidboss = (RaidBoss) _spawns.get(bossId).doSpawn(false);
			if (raidboss != null)
			{
				raidboss.setRaidStatus(StatusEnum.ALIVE);
				StatSet info = new StatSet();
				info.set("currentHP", raidboss.getStatus().getMaxHp());
				info.set("currentMP", raidboss.getStatus().getMaxMp());
				info.set("respawnTime", 0L);
				_storedInfo.put(bossId, info);
				_log.info("Spawning Raid Boss " + raidboss.getName());
				_bosses.put(bossId, raidboss);
			}
			_schedules.remove(bossId);
		}
	}
	
	public static void updateStatus(RaidBoss boss, boolean isBossDead, Creature killer)
	{
		if (!_storedInfo.containsKey(boss.getNpcId()))
		{
			return;
		}
		StatSet info = _storedInfo.get(boss.getNpcId());
		if (isBossDead)
		{
			boss.setRaidStatus(StatusEnum.DEAD);
			long respawnTime;
			long respawn_delay;
			
			int RespawnMinDelay = boss.getSpawn().getRespawnDelay();
			int RespawnMaxDelay = boss.getSpawn().getRespawnDelay();
			respawn_delay = Rnd.get((int) (RespawnMinDelay * 1000 * 1.0), (int) (RespawnMaxDelay * 1000 * 1.0));
			respawnTime = Calendar.getInstance().getTimeInMillis() + respawn_delay;
			
			info.set("currentHP", boss.getStatus().getMaxHp());
			info.set("currentMP", boss.getStatus().getMaxMp());
			info.set("respawnTime", respawnTime);
			Calendar time = Calendar.getInstance();
			time.setTimeInMillis(respawnTime);
			_log.info("RaidBossSpawnManager: Updated " + boss.getName() + " respawn time to " + time.getTime());
			
			if (!_schedules.containsKey(boss.getNpcId()))
			{
				ScheduledFuture<?> futureSpawn;
				futureSpawn = ThreadPool.schedule(new spawnSchedule(boss.getNpcId()), respawn_delay);
				_schedules.put(boss.getNpcId(), futureSpawn);
				updateDb();
			}
			RaidBoss raids = RaidBossSpawnManager.getInstance().getBosses().get(boss.getNpcId());
			if (raids != null && raids.getRaidStatus() == StatusEnum.ALIVE)
			{
				updateDb();
				_storedInfo.put(boss.getNpcId(), info);
				System.out.println(raids.getName() + " Raid found Alive in statusUpdate");
				return;
			}
			
		}
		else
		{
			boss.setRaidStatus(StatusEnum.ALIVE);
			info.set("currentHP", boss.getStatus().getHp());
			info.set("currentMP", boss.getStatus().getMp());
			info.set("respawnTime", 0L);
		}
		_storedInfo.put(boss.getNpcId(), info);
	}
	
	@SuppressWarnings("resource")
	public void addNewSpawn(Spawn spawnDat, long respawnTime, double currentHP, double currentMP, boolean storeInDb)
	{
		if (spawnDat == null)
			return;
		
		final int bossId = spawnDat.getNpcId();
		if (_spawns.containsKey(bossId))
			return;
		
		final long time = Calendar.getInstance().getTimeInMillis();
		
		SpawnTable.getInstance().addNewSpawn(spawnDat, false);
		
		if (respawnTime == 0L || (time > respawnTime))
		{
			RaidBoss raidboss = null;
			
			if (bossId == 25328)
			{
				if (GameTimeTaskManager.getInstance().isNight())
					raidboss = (RaidBoss) spawnDat.doSpawn(false);
			}
			else
				raidboss = (RaidBoss) spawnDat.doSpawn(false);
			
			if (raidboss != null)
			{
				currentHP = (currentHP == 0) ? raidboss.getStatus().getMaxHp() : currentHP;
				currentMP = (currentMP == 0) ? raidboss.getStatus().getMaxMp() : currentMP;
				
				raidboss.getStatus().setHp(currentHP);
				raidboss.getStatus().setMp(currentMP);
				raidboss.setRaidStatus(StatusEnum.ALIVE);
				SpawnManager.getInstance().addSpawn(spawnDat);
				_bosses.put(bossId, raidboss);
				
				final StatSet info = new StatSet();
				info.set("currentHP", currentHP);
				info.set("currentMP", currentMP);
				info.set("respawnTime", 0L);
				
				_storedInfo.put(bossId, info);
			}
		}
		else
		{
			long spawnTime = respawnTime - Calendar.getInstance().getTimeInMillis();
			_schedules.put(bossId, ThreadPool.schedule(new spawnSchedule(bossId), spawnTime));
		}
		
		_spawns.put(bossId, spawnDat);
		
		if (storeInDb)
		{
			try (Connection con = ConnectionPool.getConnection())
			{
				PreparedStatement statement = con.prepareStatement("INSERT INTO raidboss_spawnlist (boss_id,loc_x,loc_y,loc_z,heading,respawn_time,currentHp,currentMp) values(?,?,?,?,?,?,?,?)");
				statement.setInt(1, spawnDat.getNpcId());
				statement.setInt(2, spawnDat.getLocX());
				statement.setInt(3, spawnDat.getLocY());
				statement.setInt(4, spawnDat.getLocZ());
				statement.setInt(5, spawnDat.getHeading());
				statement.setLong(6, respawnTime);
				statement.setDouble(7, currentHP);
				statement.setDouble(8, currentMP);
				statement.execute();
				statement.close();
			}
			catch (Exception e)
			{
				// problem with storing spawn
				_log.log(Level.WARNING, "RaidBossSpawnManager: Could not store raidboss #" + bossId + " in the DB:" + e.getMessage(), e);
			}
		}
	}
	
	@SuppressWarnings("resource")
	public void deleteSpawn(Spawn spawnDat, boolean updateDb)
	{
		if (spawnDat == null)
			return;
		
		final int bossId = spawnDat.getNpcId();
		if (!_spawns.containsKey(bossId))
			return;
		
		SpawnTable.getInstance().deleteSpawn(spawnDat, false);
		SpawnManager.getInstance().deleteSpawn(spawnDat);
		_spawns.remove(bossId);
		
		if (_bosses.containsKey(bossId))
			_bosses.remove(bossId);
		
		if (_schedules.containsKey(bossId))
		{
			final ScheduledFuture<?> f = _schedules.remove(bossId);
			f.cancel(true);
		}
		
		if (_storedInfo.containsKey(bossId))
			_storedInfo.remove(bossId);
		
		if (updateDb)
		{
			try (Connection con = ConnectionPool.getConnection())
			{
			
				PreparedStatement statement = con.prepareStatement("DELETE FROM raidboss_spawnlist WHERE boss_id=?");
				statement.setInt(1, bossId);
				statement.execute();
				statement.close();
			}
			catch (Exception e)
			{
				// problem with deleting spawn
				_log.log(Level.WARNING, "RaidBossSpawnManager: Could not remove raidboss #" + bossId + " from DB: " + e.getMessage(), e);
			}
		}
	}
	
	@SuppressWarnings("resource")
	private static void updateDb()
	{
		try (Connection con = ConnectionPool.getConnection())
		{
			PreparedStatement statement = con.prepareStatement("UPDATE raidboss_spawnlist SET respawn_time = ?, currentHP = ?, currentMP = ? WHERE boss_id = ?");
			
			for (Map.Entry<Integer, StatSet> infoEntry : _storedInfo.entrySet())
			{
				final int bossId = infoEntry.getKey();
				
				final RaidBoss boss = _bosses.get(bossId);
				if (boss == null)
					continue;
				
				if (boss.getRaidStatus().equals(StatusEnum.ALIVE))
					updateStatus(boss, false, null);
				
				final StatSet info = infoEntry.getValue();
				if (info == null)
					continue;
				
				statement.setLong(1, info.getLong("respawnTime"));
				statement.setDouble(2, info.getDouble("currentHP"));
				statement.setDouble(3, info.getDouble("currentMP"));
				statement.setInt(4, bossId);
				statement.executeUpdate();
				statement.clearParameters();
			}
			statement.close();
		}
		catch (SQLException e)
		{
			_log.log(Level.WARNING, "RaidBossSpawnManager: Couldnt update raidboss_spawnlist table " + e.getMessage(), e);
		}
	}
	
	public StatusEnum getRaidBossStatusId(int bossId)
	{
		if (_bosses.containsKey(bossId))
			return _bosses.get(bossId).getRaidStatus();
		
		if (_schedules.containsKey(bossId))
			return StatusEnum.DEAD;
		
		return StatusEnum.UNDEFINED;
	}
	
	public NpcTemplate getValidTemplate(int bossId)
	{
		NpcTemplate template = NpcData.getInstance().getTemplate(bossId);
		if (template == null)
			return null;
		
		if (!template.isType("RaidBoss"))
			return null;
		
		return template;
	}
	
	public void notifySpawnNightBoss(RaidBoss raidboss)
	{
		StatSet info = new StatSet();
		info.set("currentHP", raidboss.getStatus().getMaxHp());
		info.set("currentMP", raidboss.getStatus().getMaxMp());
		info.set("respawnTime", 0L);
		raidboss.setRaidStatus(StatusEnum.ALIVE);
		_storedInfo.put(raidboss.getNpcId(), info);
		_log.info("Spawning Night Raid Boss " + raidboss.getName());
		_bosses.put(raidboss.getNpcId(), raidboss);
	}
	
	public boolean isDefined(int bossId)
	{
		return _spawns.containsKey(bossId);
	}
	
	public Map<Integer, RaidBoss> getBosses()
	{
		return _bosses;
	}
	
	public Map<Integer, Spawn> getSpawns()
	{
		return _spawns;
	}
	
	public void reloadBosses()
	{
		init();
	}
	
	/**
	 * Saves all raidboss status and then clears all info from memory, including all schedules.
	 */
	public void cleanUp()
	{
		updateDb();
		_bosses.clear();
		if (_schedules != null)
		{
			for (Integer bossId : _schedules.keySet())
			{
				ScheduledFuture<?> f = _schedules.get(bossId);
				f.cancel(true);
			}
		}
		_schedules.clear();
		_storedInfo.clear();
		_spawns.clear();
	}
	
	public static void updateStoredInfo(Spawn spawnDat, long respawnTime, double currentHP, double currentMP)
	{
		if (spawnDat == null)
		{
			return;
		}
		int bossId = spawnDat.getNpcId();
		StatSet info = new StatSet();
		info.set("currentHP", currentHP);
		info.set("currentMP", currentMP);
		info.set("respawnTime", respawnTime);
		_storedInfo.put(bossId, info);
	}
	
	public StatSet getStatsSet(int bossId)
	{
		return _storedInfo.get(bossId);
	}
	
	private static class SingletonHolder
	{
		protected static final RaidBossSpawnManager _instance = new RaidBossSpawnManager();
	}
}