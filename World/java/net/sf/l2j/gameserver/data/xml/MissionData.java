package net.sf.l2j.gameserver.data.xml;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sf.l2j.commons.data.xml.IXmlReader;
import net.sf.l2j.commons.math.MathUtil;

import net.sf.l2j.gameserver.enums.actors.MissionType;
import net.sf.l2j.gameserver.model.Mission;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;

/**
 * @author BAN - L2JDEV
 */
public class MissionData implements IXmlReader
{
	private final Map<MissionType, List<Mission>> _missions = new LinkedHashMap<>();
	
	public MissionData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		parseFile("./data/xml/Missions/MissionsData.xml");
		LOGGER.info("Loaded {} of {} mission data.", _missions.size(), MissionType.values().length);
	}
	
	public void reload()
	{
		_missions.clear();
		load();
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		forEach(doc, "list", listNode -> forEach(listNode, "mission", missionNode ->
		{
			final NamedNodeMap missionAttrs = missionNode.getAttributes();
			final MissionType type = MissionType.valueOf(missionAttrs.getNamedItem("type").getNodeValue());
			
			final List<Mission> missions = new ArrayList<>();
			forEach(missionNode, "stage", stageNode -> missions.add(new Mission(parseAttributes(stageNode))));
			
			_missions.put(type, missions);
		}));
	}
	
	public List<Mission> getMission(MissionType type)
	{
		return _missions.get(type);
	}
	
	public Mission getMissionByLevel(MissionType type, int level)
	{
		return _missions.get(type).stream().filter(mission -> mission.getLevel() == MathUtil.limit(level, 1, _missions.get(type).size())).findFirst().orElse(null);
	}
	
	public Map<MissionType, List<Mission>> getMissions()
	{
		return _missions;
	}
	
	public static MissionData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final MissionData INSTANCE = new MissionData();
	}
}