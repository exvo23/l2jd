package net.sf.l2j.gameserver.scripting.script.ai.boss.config;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import net.sf.l2j.commons.config.ExProperties;

/**
 * @author BAN-L2JDEV
 */
public class FafurionConfig
{
	protected static final Logger _log = Logger.getLogger(FafurionConfig.class.getName());
	
	public static final String FAFURION_FILE = "./config/boss/Fafurion.ini";
	
	public static int WAIT_TIME_FAFURION;
	public static String FWA_FIXTIMEPATTERNOFFAFURION;
	public static boolean SPAWN_MINIONS;
	
	public static int FAFURION_ITEM_REQUEST;
	public static int FAFURION_ITEM_REQUEST_CONT;
	public static int[] TELEPORT_ENTRANCE = new int[3];
	
	public static int MAX_PLAYERS_INSIDE_FAFURION;
	
	public static void init()
	{
		ExProperties fafurion = load(FAFURION_FILE);
		
		SPAWN_MINIONS = fafurion.getProperty("SpawnMinions", true);
		
		WAIT_TIME_FAFURION = fafurion.getProperty("FafurionWaitTime", 30) * 60000;
		FWA_FIXTIMEPATTERNOFFAFURION = fafurion.getProperty("FafurionRespawnTimePattern", "");
		
		// Item Request Enter
		FAFURION_ITEM_REQUEST = fafurion.getProperty("RequestEnterItemId", 10);
		FAFURION_ITEM_REQUEST_CONT = fafurion.getProperty("RequestEnterItemIdCont", 10);
		
		String[] propertyPtLoc = fafurion.getProperty("TeleportEntrance", "0,0,0").split(",");
		if (propertyPtLoc.length < 3)
		{
			System.out.println("Error : config/JDev/Boss/Fafurion.ini \"TeleportEntrance\" coord locations");
		}
		else
		{
			TELEPORT_ENTRANCE[0] = Integer.parseInt(propertyPtLoc[0]);
			TELEPORT_ENTRANCE[1] = Integer.parseInt(propertyPtLoc[1]);
			TELEPORT_ENTRANCE[2] = Integer.parseInt(propertyPtLoc[2]);
		}
	}
	
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
