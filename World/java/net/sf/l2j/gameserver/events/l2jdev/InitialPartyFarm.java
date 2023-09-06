package net.sf.l2j.gameserver.events.l2jdev;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import net.sf.l2j.commons.pool.ThreadPool;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.data.manager.SpawnManager;
import net.sf.l2j.gameserver.data.manager.ZoneManager;
import net.sf.l2j.gameserver.data.xml.NpcData;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.instance.Monster;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.model.spawn.Spawn;
import net.sf.l2j.gameserver.model.zone.type.PartyFarmZone;
import net.sf.l2j.gameserver.model.zone.type.subtype.SpawnZoneType;

import javolution.util.FastList;

/**
 * @author BAN - JDEV
 */
public class InitialPartyFarm
{
	protected static FastList<Spawn> MonsterEvent = new FastList<>();
	private static InitialPartyFarm _instance = null;
	protected static final Logger _log = Logger.getLogger(InitialPartyFarm.class.getName());
	private Calendar NextEvent;
	private final SimpleDateFormat format = new SimpleDateFormat("HH:mm");
	private static Map<SpawnZoneType, Integer> _zones;
	private static List<Creature> _character;
	
	public static InitialPartyFarm getInstance()
	{
		if (_instance == null)
			_instance = new InitialPartyFarm();
		return _instance;
	}
	
	public String getRestartNextTime()
	{
		if (NextEvent.getTime() != null)
			return format.format(NextEvent.getTime());
		return "Erro";
	}
	
	public void StartCalculationOfNextEventTime()
	{
		try
		{
			Calendar currentTime = Calendar.getInstance();
			Calendar testStartTime = null;
			long flush2 = 0L;
			long timeL = 0L;
			int count = 0;
			for (String timeOfDay : Config.EVENT_BEST_FARM_INTERVAL_BY_TIME_OF_DAY)
			{
				testStartTime = Calendar.getInstance();
				testStartTime.setLenient(true);
				String[] splitTimeOfDay = timeOfDay.split(":");
				testStartTime.set(11, Integer.parseInt(splitTimeOfDay[0]));
				testStartTime.set(12, Integer.parseInt(splitTimeOfDay[1]));
				testStartTime.set(13, 0);
				if (testStartTime.getTimeInMillis() < currentTime.getTimeInMillis())
					testStartTime.add(5, 1);
				timeL = testStartTime.getTimeInMillis() - currentTime.getTimeInMillis();
				if (count == 0)
				{
					flush2 = timeL;
					NextEvent = testStartTime;
				}
				if (timeL < flush2)
				{
					flush2 = timeL;
					NextEvent = testStartTime;
				}
				count++;
			}
			_log.info("[Party Farm]: Next Event: " + NextEvent.getTime().toString());
			ThreadPool.schedule(new StartEventTask(), flush2);
		}
		catch (Exception e)
		{
			System.out.println("[Party Farm]: error in config check!");
		}
	}
	
	class StartEventTask implements Runnable
	{
		StartEventTask()
		{
			_zones = new ConcurrentHashMap<>();
			_character = new ArrayList<>();
			
		}
		
		@Override
		public void run()
		{
			InitialPartyFarm._log.info("[Party Farm]: Event Started.");
			
			World.announceToOnlinePlayers("[" + Config.EVENT_NAME + "]: Teleport Now! " + PartyFarmZone.StringName() + ".", true);
			
			World.announceToOnlinePlayers("[" + Config.EVENT_NAME + "]: Duration: " + Config.EVENT_BEST_FARM_TIME + " minute(s)!", true);
			
			ZoneTask();
			waiter(Config.EVENT_BEST_FARM_TIME * 60 * 1000);
			

		}
	}
	
	public static void spawn()
	{
		for (int i = 0; i < Config.MONSTER_LOCS_COUNT; i++)
		{
			int[] coord = Config.MONSTER_LOCS[i];
			
			NpcTemplate template = NpcData.getInstance().getTemplate(Config.monsterId);
			try
			{
				final Spawn spawn = new Spawn(template);
				spawn.setLoc(coord[0], coord[1], coord[2], 0);
				spawn.setRespawnState(true);
				spawn.setRespawnDelay(Config.PARTY_FARM_MONSTER_DALAY);
				spawn.doSpawn(false);
				SpawnManager.getInstance().addSpawn(spawn);
				MonsterEvent.add(spawn);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	public static void ZoneTask()
	{
		for (SpawnZoneType zone : ZoneManager.getInstance().getAllZones(PartyFarmZone.class))
		{
			_zones.put(zone, 0);
			
			for (Creature character : zone.getCharacters())
			{
				
				if ((character instanceof Monster))
				{
					_character.add(character);
					character.decayMe();
				}
			}
			
		}
		spawn();
	}
	
	protected void waiter(long interval)
	{
		long startWaiterTime = System.currentTimeMillis();
		int seconds = (int) (interval / 1000L);
		while (startWaiterTime + interval > System.currentTimeMillis())
		{
			seconds--;
			switch (seconds)
			{
				case 3600:
					
					World.announceToOnlinePlayers("[Party Farm]: " + seconds / 60 / 60 + " hour(s) till event finish!");
					break;
				case 60:
				case 120:
				case 180:
				case 240:
				case 300:
				case 600:
				case 900:
				case 1800:
					
					World.announceToOnlinePlayers("[Party Farm]: " + seconds / 60 + " minute(s) till event finish!");
					break;
				case 1:
					Finish_Event();
				case 2:
				case 3:
				case 10:
				case 15:
				case 30:
					
					World.announceToOnlinePlayers("[Party Farm]: " + seconds + " second(s) till event finish!");
					break;
			}
			long startOneSecondWaiterStartTime = System.currentTimeMillis();
			while (startOneSecondWaiterStartTime + 1000L > System.currentTimeMillis())
				try
				{
					Thread.sleep(1L);
				}
				catch (InterruptedException ie)
				{
					ie.printStackTrace();
				}
		}
	}
	
	public void Finish_Event()
	{
		if (!MonsterEvent.isEmpty())
		{
			for (Spawn Mobs : MonsterEvent)
			{
				Mobs.setRespawnState(false);
				Mobs.doDelete();
				SpawnManager.getInstance().deleteSpawn(Mobs);
			}
			MonsterEvent.clear();
		}
		
		if (!_character.isEmpty())
		{
			for (Creature mob : _character)
			{
				mob.spawnMe();
			}
			_character.clear();
		}
		
	}
}
