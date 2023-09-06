package net.sf.l2j.gameserver.scripting.script.ai.boss.config;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import net.sf.l2j.commons.config.ExProperties;

/**
 * @author BAN-L2JDEV
 */
public class CoreConfig
{
	protected static final Logger _log = Logger.getLogger(CoreConfig.class.getName());
	
	public static final String CORE_FILE = "./config/boss/Core.ini";
	public static String FWA_FIXTIMEPATTERNOFCORE;
	
	public static void init()
	{
		ExProperties core = load(CORE_FILE);
		
		FWA_FIXTIMEPATTERNOFCORE = core.getProperty("CoreRespawnTimePattern", "");
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
