package net.sf.l2j.gameserver.data.xml;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.item.kind.Item;
import net.sf.l2j.gameserver.xmlfactory.XMLDocumentFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * @author BAN-L2JDEV
 */
public class ItemRestrictionData
{
	private static Logger _log = Logger.getLogger(ItemRestrictionData.class.getName());
	
	private static final HashMap<Integer, itemRestrictionsSettings> _classItemsRestriction = new HashMap<>();
	
	public static ItemRestrictionData getInstance()
	{
		return SingletonHolder._instance;
	}
	
	public final void load()
	{
		_classItemsRestriction.clear();
		try
		{
			File f = new File("./data/xml/restritions/classItemsRestrictions.xml");
			Document doc = XMLDocumentFactory.getInstance().loadDocument(f);
			
			Node n = doc.getFirstChild();
			for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
			{
				if (d.getNodeName().equalsIgnoreCase("class"))
				{
					NamedNodeMap attrs = d.getAttributes();
					
					final int id = Integer.parseInt(attrs.getNamedItem("id").getNodeValue());
					
					if (id == -1)
					{
						System.out.println("Problem on Item Restriction with class id " + id);
						continue;
					}
					
					if (_classItemsRestriction.containsKey(id))
						System.out.println("The class " + id + " is double writted in the ItemRestriction config.");
					
					final String[] blockItems = attrs.getNamedItem("blockItems").getNodeValue().split(";");
					final String[] exceptionOfBlocks = attrs.getNamedItem("exceptionOfBlocks").getNodeValue().split(";");
					
					_classItemsRestriction.put(id, new itemRestrictionsSettings(blockItems, exceptionOfBlocks));
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			_log.warning("ItemRestriction Data: Error while loading class item: " + e);
		}
		_log.info("ItemRestriction Data: Loaded " + _classItemsRestriction.size() + " classes.");
	}
	
	protected ItemRestrictionData()
	{
		load();
	}
	
	public boolean isClassInItemRestriction(Player activeChar)
	{
		return _classItemsRestriction.containsKey(activeChar.getClassId().getId());
	}
	
	public itemRestrictionsSettings getClassItemRestriction(Player activeChar)
	{
		return _classItemsRestriction.get(activeChar.getClassId().getId());
	}
	
	private static class SingletonHolder
	{
		protected static final ItemRestrictionData _instance = new ItemRestrictionData();
	}
	
	public class itemRestrictionsSettings
	{
		private final ArrayList<Integer> _blockItems;
		private final ArrayList<String> _typeBlocked;
		private final ArrayList<Integer> _exceptionOfBlocks;
		
		public itemRestrictionsSettings(String[] blockItems, String[] exceptionOfBlocks)
		{
			_blockItems = new ArrayList<>();
			_typeBlocked = new ArrayList<>();
			_exceptionOfBlocks = new ArrayList<>();
			for (String blockItemContain : blockItems)
			{
				if (blockItemContain.matches("[0-9]+")) // Check for text ONLY DIGITS.
					_blockItems.add(Integer.parseInt(blockItemContain));
				else
					_typeBlocked.add(blockItemContain);
			}
			for (String exceptionBlockItemContain : exceptionOfBlocks)
			{
				_exceptionOfBlocks.add(Integer.parseInt(exceptionBlockItemContain));
			}
		}
		
		/**
		 * @param item
		 * @return the enchant chance under double format.
		 */
		public final boolean itemIsBlocked(Item item)
		{
			final int item_id = item.getItemId();
			
			if (_exceptionOfBlocks.contains(item_id))
				return false;
			
			if (_typeBlocked.contains(item.getItemType().toString()))
				return true;
			
			if (_blockItems.contains(item_id))
				return true;
			
			return false;
		}
	}
}
