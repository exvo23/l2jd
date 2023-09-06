package net.sf.l2j.gameserver.model.dungeon;

import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;

import net.sf.l2j.commons.pool.ThreadPool;

import net.sf.l2j.gameserver.data.manager.SpawnManager;
import net.sf.l2j.gameserver.data.sql.PlayerVariables;
import net.sf.l2j.gameserver.data.sql.SpawnTable;
import net.sf.l2j.gameserver.data.xml.NpcData;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.instance.DungeonMob;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.model.instancemanager.Instance;
import net.sf.l2j.gameserver.model.instancemanager.InstanceManager;
import net.sf.l2j.gameserver.model.location.Location;
import net.sf.l2j.gameserver.model.spawn.Spawn;
import net.sf.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import net.sf.l2j.gameserver.network.serverpackets.ExShowScreenMessage.SMPOS;
import net.sf.l2j.gameserver.network.serverpackets.MagicSkillUse;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

/**
 * @author BAN
 */
public class Dungeon
{
	private DungeonTemplate template;
	private List<Player> players;
	private ScheduledFuture<?> dungeonCancelTask = null;
	private ScheduledFuture<?> nextTask = null;
	private ScheduledFuture<?> timerTask = null;
	private DungeonStage currentStage = null;
	private long stageBeginTime = 0;
	private List<DungeonMob> mobs = new CopyOnWriteArrayList<>();
	private Instance instance;
	
	public Dungeon(DungeonTemplate template, List<Player> players)
	{
		this.template = template;
		this.players = players;
		instance = InstanceManager.getInstance().createInstance();
		
		for (Player player : players)
		{
			player.setDungeon(this);
		}
		
		for (Creature player : players)
		{
			player.setIsDungeon(true);
		}
		
		beginTeleport();
	}
	
	public void onPlayerDeath(Player player)
	{
		if (!players.contains(player))
			return;
		
		if (players.size() == 1)
			ThreadPool.schedule(() -> deathDungeon(), 5 * 1000);
		
	}
	
	public synchronized void onMobKill(DungeonMob mob)
	{
		if (!mobs.contains(mob))
			return;
		
		deleteMob(mob);
		
		if (mobs.isEmpty())
		{
			if (dungeonCancelTask != null)
				dungeonCancelTask.cancel(false);
			if (timerTask != null)
				timerTask.cancel(true);
			if (nextTask != null)
				nextTask.cancel(true);
			
			for (Player player : players)
				if (player.isDead())
					player.doRevive();
				
			getNextStage();
			if (currentStage == null) // No more stages
			{
				rewardPlayers();
				if (template.getRewardHtm().equals("NULL"))
				{
					broadcastScreenMessage("You have completed the dungeon!", 5);
					teleToTown();
				}
				else
				{
					broadcastScreenMessage("You have completed the dungeon! Select your reward", 5);
					// teleToTown();
				}
				InstanceManager.getInstance().deleteInstance(instance.getId());
				DungeonManager.getInstance().removeDungeon(this);
			}
			else
			{
				broadcastScreenMessage("You have completed stage " + (currentStage.getOrder() - 1) + "! Next stage begins in 10 seconds.", 5);
				ThreadPool.schedule(() -> teleToStage(), 5 * 1000);
				nextTask = ThreadPool.schedule(() -> beginStage(), 10 * 1000);
			}
		}
	}
	
	private void rewardPlayers()
	{
		
		
		if (!template.getRewardHtm().equals("NULL"))
		{
			NpcHtmlMessage htm = new NpcHtmlMessage(0);
			htm.setFile(template.getRewardHtm());
			for (Player player : players)
				player.sendPacket(htm);
		}
		else
		{
			for (Player player : players)
			{
				player.setInstance(InstanceManager.getInstance().getInstance(0), true);
				player.teleportTo(82635, 148798, -3464, 25);
				PlayerVariables.setVar(player, "dungeon_atleast1time", "true", -1);
				for (Entry<Integer, Integer> itemId : template.getRewards().entrySet())
				{
					player.addItem("dungeon reward", itemId.getKey(), itemId.getValue(), null, true);
				}
			}
			for (Creature player : players)
			{
				player.setIsDungeon(false);
			}
		}
	}
	
	private void teleToStage()
	{
		if (!currentStage.teleport())
			return;
		
		for (Player player : players)
			player.teleportTo(currentStage.getLocation(), 25);
	}
	
	private void teleToTown()
	{
		for (Player player : players)
		{
			
			DungeonManager.getInstance().getDungeonParticipants().remove(Integer.valueOf(player.getObjectId()));
			player.setDungeon(null);
			player.setInstance(InstanceManager.getInstance().getInstance(0), true);
			player.teleportTo(82635, 148798, -3464, 25);
			
			InstanceManager.getInstance().deleteInstance(instance.getId());
			DungeonManager.getInstance().removeDungeon(this);
		}
	}
	
	public void deathDungeon()
	{
		for (Player player : players)
		{
		
			if (player.isDead())
				player.doRevive();
			player.teleportTo(currentStage.getLocation(), 25);
			
		}
	}
	
	private void cancelDungeon()
	{
		
		for (DungeonMob mob : mobs)
			deleteMob(mob);
		broadcastScreenMessage("You have failed to complete the dungeon. You will be teleported back in 5 seconds.", 5);
		
		ThreadPool.schedule(() -> teleToTown(), 5 * 1000);
		InstanceManager.getInstance().deleteInstance(instance.getId());
		DungeonManager.getInstance().removeDungeon(this);
		
		if (nextTask != null)
			nextTask.cancel(true);
		
		if (timerTask != null)
			timerTask.cancel(true);
		
		if (dungeonCancelTask != null)
			dungeonCancelTask.cancel(true);
	}
	
	private void deleteMob(DungeonMob mob)
	{
		if (!mobs.contains(mob))
			return;
		
		mobs.remove(mob);
		
		final Spawn spawn = (Spawn) mob.getSpawn();
		spawn.doDelete();
		SpawnManager.getInstance().deleteSpawn(spawn);
		SpawnTable.getInstance().deleteSpawn(spawn, true);
		
		mob.deleteMe();
	}
	
	private void beginStage()
	{
		for (int mobId : currentStage.getMobs().keySet())
			spawnMob(mobId, currentStage.getMobs().get(mobId));
		
		stageBeginTime = System.currentTimeMillis();
		timerTask = ThreadPool.scheduleAtFixedRate(() -> broadcastTimer(), 5 * 1000, 1000);
		nextTask = null;
		dungeonCancelTask = ThreadPool.schedule(() -> cancelDungeon(), 1000 * 60 * currentStage.getMinutes());
		broadcastScreenMessage("You have " + currentStage.getMinutes() + " minutes to finish stage " + currentStage.getOrder() + "!", 5);
	}
	
	private void spawnMob(int mobId, List<Location> locations)
	{
		NpcTemplate template = NpcData.getInstance().getTemplate(mobId);
		try
		{
			for (Location loc : locations)
			{
				Spawn spawn = new Spawn(template);
				spawn.setLoc(loc.getX(), loc.getY(), loc.getZ(), 0);
				spawn.setRespawnDelay(1);
				spawn.setRespawnState(false);
				spawn.doSpawn(false);
				
				((DungeonMob) spawn.getNpc()).setDungeon(this);
				spawn.getNpc().setInstance(instance, false); // Set instance first
				// SpawnTable.getInstance().addNewSpawn(spawn, false); // TODO: Useless
				SpawnManager.getInstance().addSpawn(spawn);
				// Add it to memory
				mobs.add((DungeonMob) spawn.getNpc());
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private void teleportPlayers()
	{
		for (Player player : players)
			player.setInstance(instance, true);
		
		teleToStage();
		
		broadcastScreenMessage("Stage " + currentStage.getOrder() + " begins in 10 seconds!", 5);
		
		nextTask = ThreadPool.schedule(() -> beginStage(), 10 * 1000);
	}
	
	private void beginTeleport()
	{
		getNextStage();
		for (Player player : players)
		{
			player.broadcastPacket(new MagicSkillUse(player, 1050, 1, 10000, 10000));
			broadcastScreenMessage("You will be teleported in 10 seconds!", 3);
		}
		
		nextTask = ThreadPool.schedule(() -> teleportPlayers(), 10 * 1000);
	}
	
	private void getNextStage()
	{
		currentStage = currentStage == null ? template.getStages().get(1) : template.getStages().get(currentStage.getOrder() + 1);
	}
	
	private void broadcastTimer()
	{
		int secondsLeft = (int) (((stageBeginTime + (1000 * 60 * currentStage.getMinutes())) - System.currentTimeMillis()) / 1000);
		int minutes = secondsLeft / 60;
		int seconds = secondsLeft % 60;
		ExShowScreenMessage packet = new ExShowScreenMessage(String.format("%02d:%02d", minutes, seconds), 1010, SMPOS.BOTTOM_RIGHT, false);
		for (Player player : players)
			player.sendPacket(packet);
	}
	
	private void broadcastScreenMessage(String msg, int seconds)
	{
		ExShowScreenMessage packet = new ExShowScreenMessage(msg, seconds * 1000, SMPOS.BOTTOM_CENTER, false);
		for (Player player : players)
			player.sendPacket(packet);
	}
	
	public List<Player> getPlayers()
	{
		return players;
	}
}