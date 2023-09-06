package net.sf.l2j.gameserver.data.xml;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import net.sf.l2j.commons.data.xml.IXmlReader;

import net.sf.l2j.gameserver.model.item.DressmeWeapon;

import org.w3c.dom.Document;

/**
 * @author BAN L2JDEV
 */
public class DressmeWeaponData implements IXmlReader
{
	private final List<DressmeWeapon> _entries = new ArrayList<>();
	
	public DressmeWeaponData()
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
		parseFile("./data/xml/dress/DressmeWeaponData.xml");
		LOGGER.info("Loaded {} Dressme Weapon.", _entries.size());
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		forEach(doc, "list", listNode -> forEach(listNode, "DressmeId", dressNode -> _entries.add(new DressmeWeapon(parseAttributes(dressNode)))));
	}
	
	public DressmeWeapon getItemId(int itemId)
	{
		return _entries.stream().filter(x -> x.getItemId() == itemId).findFirst().orElse(null);
	}
	

	public List<DressmeWeapon> getEntries()
	{
		return _entries;
	}
	
	public static DressmeWeaponData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final DressmeWeaponData INSTANCE = new DressmeWeaponData();
	}
}
