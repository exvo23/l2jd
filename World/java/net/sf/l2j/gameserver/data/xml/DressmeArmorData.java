package net.sf.l2j.gameserver.data.xml;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import net.sf.l2j.commons.data.xml.IXmlReader;

import net.sf.l2j.gameserver.model.item.DressmeArmor;

import org.w3c.dom.Document;

/**
 * @author BAN L2JDEV
 */
public class DressmeArmorData implements IXmlReader
{
	private final List<DressmeArmor> _entries = new ArrayList<>();
	
	public DressmeArmorData()
	{
		load();
	}
	
	public void reload()
	{
		_entries.clear();
		load();
	}
	
	@Override
	public void load()
	{
		parseFile("./data/xml/dress/DressmeArmorData.xml");
		LOGGER.info("Loaded {} Dressme Armor.", _entries.size());
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		forEach(doc, "list", listNode -> forEach(listNode, "DressmeId", dressNode -> _entries.add(new DressmeArmor(parseAttributes(dressNode)))));
	}
	
	public DressmeArmor getItemId(int itemId)
	{
		return _entries.stream().filter(x -> x.getItemId() == itemId).findFirst().orElse(null);
	}
	

	public List<DressmeArmor> getEntries()
	{
		return _entries;
	}
	
	public static DressmeArmorData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final DressmeArmorData INSTANCE = new DressmeArmorData();
	}
}
