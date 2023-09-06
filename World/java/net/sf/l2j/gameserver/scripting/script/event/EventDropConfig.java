package net.sf.l2j.gameserver.scripting.script.event;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import net.sf.l2j.commons.config.ExProperties;

/**
 * @author BAN - L2JDEV
 *
 */
public class EventDropConfig
{
	protected static final Logger _log = Logger.getLogger(EventDropConfig.class.getName());
	
	public static final String EVENTS = "./config/events/Christmas_HeavyMedal.properties";
	
	public static void init()
	{
		ExProperties events = load(EVENTS);
		
		EVENT_MEDAL_COUNT = events.getProperty("EventMedalCount", 1);
		EVENT_MEDAL_CHANCE = events.getProperty("EventMedalChance", 40);
		
		GLITTERING_MEDAL_COUNT = events.getProperty("GlitteringMedalCount", 1);
		GLITTERING_MEDAL_CHANCE = events.getProperty("GlitteringMedalChance", 2);
		
		
		STAR_CHANCEID = events.getProperty("StarItemId", 5556);
		BEAD_CHANCEID = events.getProperty("BeadItemId", 5557);
		FIR_CHANCEID = events.getProperty("FirItemId", 5558);
		FLOWER_CHANCEID = events.getProperty("FlowerItemId", 5559);
		
		
		STAR_CHANCE = events.getProperty("StarChance", 20);
		BEAD_CHANCE = events.getProperty("BeadChance", 20);
		FIR_CHANCE = events.getProperty("FirChance", 50);
		FLOWER_CHANCE = events.getProperty("FlowerChance", 5);
		
		STAR_COUNT = events.getProperty("StarCount", 1);
		BEAD_COUNT = events.getProperty("BeadCount", 1);
		FIR_COUNT = events.getProperty("FirCount", 1);
		FLOWER_COUNT = events.getProperty("FlowerCount", 1);
		
	}
	
	public static int EVENT_MEDAL_COUNT;
	public static int EVENT_MEDAL_CHANCE;
	
	public static int GLITTERING_MEDAL_COUNT;
	public static int GLITTERING_MEDAL_CHANCE;
	
	
	public static int STAR_CHANCEID;
	public static int BEAD_CHANCEID;
	public static int FIR_CHANCEID;
	public static int FLOWER_CHANCEID;
	
	public static int STAR_CHANCE;
	public static int BEAD_CHANCE;
	public static int FIR_CHANCE;
	public static int FLOWER_CHANCE;
	
	public static int STAR_COUNT;
	public static int BEAD_COUNT;
	public static int FIR_COUNT;
	public static int FLOWER_COUNT;
	
	public static ExProperties load(String filename)
	{
		return load(new File(filename));
	}
	
	public static ExProperties load(File file)
	{
		ExProperties result = new ExProperties();
		
		try
		{
			result.load(file);
		}
		catch (IOException e)
		{
			_log.warning("Error loading config : " + file.getName() + "!");
		}
		
		return result;
	}
}
