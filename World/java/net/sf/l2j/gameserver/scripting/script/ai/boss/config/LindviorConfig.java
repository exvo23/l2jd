package net.sf.l2j.gameserver.scripting.script.ai.boss.config;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import net.sf.l2j.commons.config.ExProperties;

/**
 * @author BAN-L2JDEV
 */
public class LindviorConfig
{
	protected static final Logger _log = Logger.getLogger(LindviorConfig.class.getName());
	
	public static final String LINDVIOR_FILE = "./config/boss/Lindvior.ini";
	public static int WAIT_TIME_LINDVIOR;
	public static boolean LINDVIOR_STOP_PLAYER_ANIMATION;
	
	public static int LIDVIOR_ITEM_REQUEST;
	public static int LIDVIOR_ITEM_REQUEST_CONT;
	
	public static int LINDVIOR_TELEPORT_IN_LOC_X;
	public static int LINDVIOR_TELEPORT_IN_LOC_Y;
	public static int LINDVIOR_TELEPORT_IN_LOC_Z;
	
	public static int LIDVIOR_BOSS_ID;
	public static int LINDVIOR_SPWAN_LOC_X;
	public static int LINDVIOR_SPWAN_LOC_Y;
	public static int LINDVIOR_SPWAN_LOC_Z;
	
	public static int LINDVIOR_SKILL_REGEN_A;
	public static int LINDVIOR_SKILL_REGEN_B;
	public static int LINDVIOR_SKILL_REGEN_C;
	
	public static String FWA_FIXTIMEPATTERNOFLINDVIOR;
	
	public static int LINDVIOR_CAMERA_DIST;
	public static int LINDVIOR_CAMERA_YAW;
	public static int LINDVIOR_CAMERA_PITCH;
	public static int LINDVIOR_CAMERA_TIME;
	public static int LINDVIOR_CAMERA_DURATION;
	public static int LINDVIOR_CAMERA_FULLSCREAM;
	
	public static int LINDVIOR_1CAMERA_DIST;
	public static int LINDVIOR_1CAMERA_YAW;
	public static int LINDVIOR_1CAMERA_PITCH;
	public static int LINDVIOR_1CAMERA_TIME;
	public static int LINDVIOR_1CAMERA_DURATION;
	public static int LINDVIOR_1CAMERA_FULLSCREAM;
	
	public static void ini()
	{
		
		final ExProperties Lindvior = load(LINDVIOR_FILE);
		
		LIDVIOR_BOSS_ID = Lindvior.getProperty("LindviorBossId", 11);
		
		LINDVIOR_STOP_PLAYER_ANIMATION = Lindvior.getProperty("ImobilizePlayerAnimation", false);
		
		FWA_FIXTIMEPATTERNOFLINDVIOR = Lindvior.getProperty("LindviorRespawnTimePattern", "");
		
		// time animations start
		WAIT_TIME_LINDVIOR = Lindvior.getProperty("LindviorWaitTime", 10) * 6000;
		
		// Item Request Enter
		LIDVIOR_ITEM_REQUEST = Lindvior.getProperty("RequestEnterItemId", 10);
		LIDVIOR_ITEM_REQUEST_CONT = Lindvior.getProperty("RequestEnterItemIdCont", 10);
		
		// teleport
		LINDVIOR_TELEPORT_IN_LOC_X = Lindvior.getProperty("TeleportXLocationPlayer", 10);
		LINDVIOR_TELEPORT_IN_LOC_Y = Lindvior.getProperty("TeleportYLocationPlayer", 10);
		LINDVIOR_TELEPORT_IN_LOC_Z = Lindvior.getProperty("TeleportZLocationPlayer", 10);
		
		// Spwan Location
		LINDVIOR_SPWAN_LOC_X = Lindvior.getProperty("SpawnLoc_X", 10);
		LINDVIOR_SPWAN_LOC_Y = Lindvior.getProperty("SpawnLoc_Y", 10);
		LINDVIOR_SPWAN_LOC_Z = Lindvior.getProperty("SpawnLoc_Z", 10);
		
		// Lidvior Skill Regen Use Max Player Inside Zone
		LINDVIOR_SKILL_REGEN_A = Lindvior.getProperty("LindviorARegenSkillId", 10);
		LINDVIOR_SKILL_REGEN_B = Lindvior.getProperty("LindviorBRegenSkillId", 10);
		LINDVIOR_SKILL_REGEN_C = Lindvior.getProperty("LindviorCRegenSkillId", 10);
		
		LINDVIOR_CAMERA_DIST = Lindvior.getProperty("CameraModeDist", 10);
		LINDVIOR_CAMERA_YAW = Lindvior.getProperty("CameraModeYaw", 10);
		LINDVIOR_CAMERA_PITCH = Lindvior.getProperty("CameraModePitch", 10);
		LINDVIOR_CAMERA_TIME = Lindvior.getProperty("CameraModeTime", 10);
		LINDVIOR_CAMERA_DURATION = Lindvior.getProperty("CameraModeDuration", 10);
		LINDVIOR_CAMERA_FULLSCREAM = Lindvior.getProperty("CameraModeFullScream", 10);
		
		LINDVIOR_1CAMERA_DIST = Lindvior.getProperty("CameraModeADist", 10);
		LINDVIOR_1CAMERA_YAW = Lindvior.getProperty("CameraModeBYaw", 10);
		LINDVIOR_1CAMERA_PITCH = Lindvior.getProperty("CameraModeCPitch", 10);
		LINDVIOR_1CAMERA_TIME = Lindvior.getProperty("CameraModeDTime", 10);
		LINDVIOR_1CAMERA_DURATION = Lindvior.getProperty("CameraModeEDuration", 10);
		LINDVIOR_1CAMERA_FULLSCREAM = Lindvior.getProperty("CameraModeFFullScream", 10);
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
