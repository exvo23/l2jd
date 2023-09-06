package net.sf.l2j.gameserver.scripting.script.ai.boss.config;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import net.sf.l2j.commons.config.ExProperties;

/**
 * @author BAN-L2JDEV
 */
public class BenomConfig
{
	protected static final Logger _log = Logger.getLogger(BenomConfig.class.getName());
	
	public static final String BENOM_FILE = "./config/boss/Benom.ini";
	
	public static int[] TELEPORT_ENTRANCE = new int[3];
	public static int[] TELEPORT_EXIT = new int[3];
	public static int[] TELEPORT_THORNE = new int[3];
	public static int[] SPAWN_BENOM_LOCATION = new int[3];
	public static int[] SPAWN_CUBIC_LOCATION = new int[3];
	public static boolean DISABLE_SKILL_TELEPORT;
	
	public static void init()
	{
		ExProperties benom = load(BENOM_FILE);
		
		DISABLE_SKILL_TELEPORT = benom.getProperty("FrequeneSkillTeleport", true);
		
		String[] cubic = benom.getProperty("SpwanLocationCubic", "0,0,0").split(",");
		if (cubic.length < 3)
		{
			System.out.println("Error : config/JDev/Boss/Benom.ini \"SpwanLocationCubic\" coord locations");
		}
		else
		{
			SPAWN_CUBIC_LOCATION[0] = Integer.parseInt(cubic[0]);
			SPAWN_CUBIC_LOCATION[1] = Integer.parseInt(cubic[1]);
			SPAWN_CUBIC_LOCATION[2] = Integer.parseInt(cubic[2]);
		}
		
		String[] trhone = benom.getProperty("TeleportThrone", "0,0,0").split(",");
		if (trhone.length < 3)
		{
			System.out.println("Error : config/JDev/Boss/Benom.ini \"TeleportThrone\" coord locations");
		}
		else
		{
			TELEPORT_THORNE[0] = Integer.parseInt(trhone[0]);
			TELEPORT_THORNE[1] = Integer.parseInt(trhone[1]);
			TELEPORT_THORNE[2] = Integer.parseInt(trhone[2]);
		}
		
		String[] entrance = benom.getProperty("TeleportEntrance", "0,0,0").split(",");
		if (entrance.length < 3)
		{
			System.out.println("Error : config/JDev/Boss/Benom.ini \"TeleportEntrance\" coord locations");
		}
		else
		{
			TELEPORT_ENTRANCE[0] = Integer.parseInt(entrance[0]);
			TELEPORT_ENTRANCE[1] = Integer.parseInt(entrance[1]);
			TELEPORT_ENTRANCE[2] = Integer.parseInt(entrance[2]);
		}
		
		String[] exite = benom.getProperty("TeleportExitZone", "0,0,0").split(",");
		if (exite.length < 3)
		{
			System.out.println("Error : config/JDev/Boss/Baium.ini \"TeleportExitZone\" coord locations");
		}
		else
		{
			TELEPORT_EXIT[0] = Integer.parseInt(exite[0]);
			TELEPORT_EXIT[1] = Integer.parseInt(exite[1]);
			TELEPORT_EXIT[2] = Integer.parseInt(exite[2]);
		}
		
		String[] spawn = benom.getProperty("BossSpawnLocation", "0,0,0").split(",");
		if (spawn.length < 3)
		{
			System.out.println("Error : config/JDev/Boss/Benom.ini \"BossSpawnLocation\" coord locations");
		}
		else
		{
			SPAWN_BENOM_LOCATION[0] = Integer.parseInt(spawn[0]);
			SPAWN_BENOM_LOCATION[1] = Integer.parseInt(spawn[1]);
			SPAWN_BENOM_LOCATION[2] = Integer.parseInt(spawn[2]);
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
