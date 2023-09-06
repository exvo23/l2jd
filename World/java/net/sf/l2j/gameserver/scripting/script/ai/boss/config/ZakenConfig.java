package net.sf.l2j.gameserver.scripting.script.ai.boss.config;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import net.sf.l2j.commons.config.ExProperties;

/**
 * @author BAN-L2JDEV
 */

public class ZakenConfig
{
	protected static final Logger _log = Logger.getLogger(AntharasConfig.class.getName());
	
	public static final String ZAKEN_FILE = "./config/boss/Zaken.ini";
	
	public static int ZAKEN_TIME_DOOR;
	public static String FWA_FIXTIMEPATTERNOFZAKEN;
	
	public static void init()
	{
		ExProperties zaken = load(ZAKEN_FILE);
		ZAKEN_TIME_DOOR = zaken.getProperty("ZakenDoorTimeClose", 200);
		
		FWA_FIXTIMEPATTERNOFZAKEN = zaken.getProperty("ZakenRespawnTimePattern", "");
		
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
