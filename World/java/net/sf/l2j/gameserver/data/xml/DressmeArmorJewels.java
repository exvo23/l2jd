package net.sf.l2j.gameserver.data.xml;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import net.sf.l2j.commons.data.xml.IXmlReader;

import net.sf.l2j.gameserver.model.item.DressmeJewels;

import org.w3c.dom.Document;

/**
 * @author BAN L2JDEV
 */
public class DressmeArmorJewels implements IXmlReader
{
	private final List<DressmeJewels> _entries = new ArrayList<>();
	
	public DressmeArmorJewels()
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
		parseFile("./data/xml/dress/DressmeJewelsData.xml");
		LOGGER.info("Loaded {} Dressme Jewels.", _entries.size());
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		forEach(doc, "list", listNode -> forEach(listNode, "JewelsId", dressNode -> _entries.add(new DressmeJewels(parseAttributes(dressNode)))));
	}
	
	public DressmeJewels getItemId(int itemId)
	{
		return _entries.stream().filter(x -> x.getLinkId() == itemId).findFirst().orElse(null);
	}
	

	public List<DressmeJewels> getEntries()
	{
		return _entries;
	}
	
	public static DressmeArmorJewels getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final DressmeArmorJewels INSTANCE = new DressmeArmorJewels();
	}
}
