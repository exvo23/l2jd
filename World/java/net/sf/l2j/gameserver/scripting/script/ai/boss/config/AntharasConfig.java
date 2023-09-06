package net.sf.l2j.gameserver.scripting.script.ai.boss.config;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import net.sf.l2j.commons.config.ExProperties;

/**
 * @author BAN-L2JDEV
 */
public class AntharasConfig
{
	protected static final Logger _log = Logger.getLogger(AntharasConfig.class.getName());
	
	public static final String ANTHARAS_FILE = "./config/boss/Antharas.ini";
	
	public static int WAIT_TIME_ANTHARAS;
	public static String FWA_FIXTIMEPATTERNOFANTHARAS;
	public static boolean SPAWN_MINIONS;
	
	public static int ANTHARAS_ITEM_REQUEST;
	public static int ANTHARAS_ITEM_REQUEST_CONT;
	public static int[] TELEPORT_ENTRANCE = new int[3];
	
	public static void init()
	{
		ExProperties antharas = load(ANTHARAS_FILE);
		
		SPAWN_MINIONS = antharas.getProperty("SpawnMinions", true);
		
		WAIT_TIME_ANTHARAS = antharas.getProperty("AntharasWaitTime", 30) * 60000;
		FWA_FIXTIMEPATTERNOFANTHARAS = antharas.getProperty("AntharasRespawnTimePattern", "");
		
		// Item Request Enter
		ANTHARAS_ITEM_REQUEST = antharas.getProperty("RequestEnterItemId", 10);
		ANTHARAS_ITEM_REQUEST_CONT = antharas.getProperty("RequestEnterItemIdCont", 10);
		
		String[] propertyPtLoc = antharas.getProperty("TeleportEntrance", "0,0,0").split(",");
		if (propertyPtLoc.length < 3)
		{
			System.out.println("Error : config/JDev/Boss/Antharas.ini \"TeleportEntrance\" coord locations");
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
