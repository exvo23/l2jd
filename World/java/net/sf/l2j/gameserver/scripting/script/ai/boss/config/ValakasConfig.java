package net.sf.l2j.gameserver.scripting.script.ai.boss.config;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import net.sf.l2j.commons.config.ExProperties;

/**
 * @author BAN-L2JDEV
 */
public class ValakasConfig
{
	protected static final Logger _log = Logger.getLogger(ValakasConfig.class.getName());
	public static final String VALAKAS_FILE = "./config/boss/Valakas.ini";
	
	public static int WAIT_TIME_VALAKAS;
	public static String FWA_FIXTIMEPATTERNOFVALAKAS;
	
	public static int VALAKAS_ITEM_REQUEST;
	public static int VALAKAS_ITEM_REQUEST_CONT;
	
	public static void ini()
	{
		final ExProperties valakas = load(VALAKAS_FILE);
		FWA_FIXTIMEPATTERNOFVALAKAS = valakas.getProperty("ValakasRespawnTimePattern", "");
		WAIT_TIME_VALAKAS = valakas.getProperty("ValakasWaitTime", 30) * 60000;
		
		// Item Request Enter
		VALAKAS_ITEM_REQUEST = valakas.getProperty("RequestEnterItemId", 10);
		VALAKAS_ITEM_REQUEST_CONT = valakas.getProperty("RequestEnterItemIdCont", 10);
		
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
