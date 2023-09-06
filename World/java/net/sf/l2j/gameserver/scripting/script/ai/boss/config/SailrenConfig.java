package net.sf.l2j.gameserver.scripting.script.ai.boss.config;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import net.sf.l2j.commons.config.ExProperties;

/**
 * @author BAN-L2JDEV
 */
public class SailrenConfig
{
	protected static final Logger _log = Logger.getLogger(SailrenConfig.class.getName());
	public static final String SAILREN_FILE = "./config/boss/Sailren.ini";
	
	public static String FWA_FIXTIMEPATTERNOFSAILREN;
	public static int WAIT_TIME_SAILREN;
	
	public static void ini()
	{
		final ExProperties sailren = load(SAILREN_FILE);
		
		FWA_FIXTIMEPATTERNOFSAILREN = sailren.getProperty("SailrenRespawnTimePattern", "");
		WAIT_TIME_SAILREN = sailren.getProperty("SailrenWaitTime", 5) * 60000;
		
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
