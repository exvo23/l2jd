package net.sf.l2j.gameserver.scripting.script.ai.boss.config;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import net.sf.l2j.commons.config.ExProperties;

/**
 * @author BAN-L2JDEV
 */
public class BaiumConfig
{
	protected static final Logger _log = Logger.getLogger(BaiumConfig.class.getName());
	
	public static final String BAIUM_FILE = "./config/boss/Baium.ini";
	
	public static boolean SPAWN_MINIONS;
	public static String FWA_FIXTIMEPATTERNOFBAIUM;
	public static int BAIUM_ITEM_REQUEST;
	public static int BAIUM_ITEM_REQUEST_CONT;
	public static int[] TELEPORT_ENTRANCE = new int[3];
	
	public static int[] TELEPORT_EXIT = new int[3];
	
	public static void init()
	{
		ExProperties baium = load(BAIUM_FILE);
		
		SPAWN_MINIONS = baium.getProperty("SpawnMinions", true);
		
		FWA_FIXTIMEPATTERNOFBAIUM = baium.getProperty("BaiumRespawnTimePattern", "");
		
		// Item Request Enter
		BAIUM_ITEM_REQUEST = baium.getProperty("RequestEnterItemId", 10);
		BAIUM_ITEM_REQUEST_CONT = baium.getProperty("RequestEnterItemIdCont", 10);
		
		String[] propertyPtLoc = baium.getProperty("TeleportEntrance", "0,0,0").split(",");
		if (propertyPtLoc.length < 3)
		{
			System.out.println("Error : config/JDev/Boss/Baium.ini \"TeleportEntrance\" coord locations");
		}
		else
		{
			TELEPORT_ENTRANCE[0] = Integer.parseInt(propertyPtLoc[0]);
			TELEPORT_ENTRANCE[1] = Integer.parseInt(propertyPtLoc[1]);
			TELEPORT_ENTRANCE[2] = Integer.parseInt(propertyPtLoc[2]);
		}
		
		String[] exit = baium.getProperty("TeleportExitZone", "0,0,0").split(",");
		if (exit.length < 3)
		{
			System.out.println("Error : config/JDev/Boss/Baium.ini \"TeleportExitZone\" coord locations");
		}
		else
		{
			TELEPORT_EXIT[0] = Integer.parseInt(exit[0]);
			TELEPORT_EXIT[1] = Integer.parseInt(exit[1]);
			TELEPORT_EXIT[2] = Integer.parseInt(exit[2]);
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
