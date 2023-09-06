package net.sf.l2j.commons.data;

import java.util.AbstractList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import net.sf.l2j.commons.lang.StringUtil;
import net.sf.l2j.commons.math.MathUtil;

public class Pagination<A> extends AbstractList<A>
{
	private static final String NORMAL_LINE_SIZE = "<img height=17>";
	
	private final StringBuilder _content = new StringBuilder();
	
	private List<A> _list;
	private int _page;
	private int _limit;
	private int _total;
	private int _totalEntries;
	
	public Pagination(Stream<A> stream, int page, int limit)
	{
		this(stream, page, limit, null, null);
	}
	
	public Pagination(Stream<A> stream, int page, int limit, Predicate<A> filter)
	{
		this(stream, page, limit, filter, null);
	}
	
	public Pagination(Stream<A> stream, int page, int limit, Comparator<A> comparator)
	{
		this(stream, page, limit, null, comparator);
	}
	
	public Pagination(Stream<A> stream, int page, int limit, Predicate<A> filter, Comparator<A> comparator)
	{
		_list = initList(stream, filter, comparator);
		_totalEntries = _list.size();
		_limit = Math.max(limit, 1);
		_total = _list.size() / _limit + (_list.size() % _limit == 0 ? 0 : 1);
		_page = MathUtil.limit(page, 1, _total);
		
		if (_list.isEmpty())
			return;
		
		_list = _list.subList((Math.min(page, _total) - 1) * limit, Math.min(Math.min(page, _total) * limit, _list.size()));
	}
	
	private List<A> initList(Stream<A> stream, Predicate<A> filter, Comparator<A> comparator)
	{
		if (stream == null)
			return Collections.emptyList();
		
		if (filter == null && comparator == null)
			return stream.collect(Collectors.toList());
		
		if (comparator == null)
			return stream.filter(filter).collect(Collectors.toList());
		
		if (filter == null)
			return stream.sorted(comparator).collect(Collectors.toList());
		
		return stream.filter(filter).sorted(comparator).collect(Collectors.toList());
	}
	
	public void append(Object... content)
	{
		StringUtil.append(_content, content);
	}
	
	public void generateSpace()
	{
		IntStream.range(size(), _limit).forEach(x -> append(NORMAL_LINE_SIZE));
	}
	
	public void generateSpace(int height)
	{
		IntStream.range(size(), _limit).forEach(x -> append("<img height=", height, ">"));
	}
	
	public void generatePages(String action)
	{
		append("<table width=480 bgcolor=000000><tr><td FIXWIDTH=22 align=center><img height=2><button action=\"", action.replace("%page%", String.valueOf(1)), "\" back=L2UI_CH3.prev1_down fore=L2UI_CH3.prev1 width=16 height=16></td>");
		
		for (int index = _page - 5; index < _page - 1; index++)
			append("<td FIXWIDTH=26 align=center>", (index < 0 ? "" : "<a action=\"" + action.replace("%page%", String.valueOf(index + 1)) + "\">" + String.format("%02d", (index + 1)) + "</a>"), "</td>");
		
		append("<td FIXWIDTH=26 align=center><font color=LEVEL>", String.format("%02d", Math.max(_page, 1)), "</font></td>");
		
		for (int index = _page; index < _page + 4; index++)
			append("<td FIXWIDTH=26 align=center>", (index < _total ? "<a action=\"" + action.replace("%page%", String.valueOf(index + 1)) + "\">" + String.format("%02d", (index + 1)) + "</a>" : ""), "</td>");
		
		append("<td FIXWIDTH=22 align=center><img height=2><button action=\"", action.replace("%page%", String.valueOf(_total)), "\" back=L2UI_CH3.next1_down fore=L2UI_CH3.next1 width=16 height=16></td></tr></table>");
		append("<img src=\"L2UI.SquareGray\" width=280 height=1>");
	}
	
	public void generatePages(StringBuilder sb, String action)
	{
		StringUtil.append(sb, "<table width=270 bgcolor=000000><tr><td width=30 align=center><button action=\"", action.replace("%page%", String.valueOf(1)), "\" back=L2UI_CH3.prev1_down fore=L2UI_CH3.prev1 width=16 height=16></td>");
		
		for (int index = _page - 5; index < _page - 1; index++)
			StringUtil.append(sb, "<td width=30>" + (index < 0 ? "" : "<a action=\"" + action.replace("%page%", String.valueOf(index + 1)) + "\">" + (((index + 1) < 10 ? "0" : "") + (index + 1)) + "</a>") + "</td>");
		
		StringUtil.append(sb, "<td width=30><font color=LEVEL>", ((_page < 10 ? "0" : "") + (_page == 0 ? "1" : _page)), "</font></td>");
		
		for (int index = _page; index < _page + 4; index++)
			StringUtil.append(sb, "<td width=30>" + (index < _total ? "<a action=\"" + action.replace("%page%", String.valueOf(index + 1)) + "\">" + (((index + 1) < 10 ? "0" : "") + (index + 1)) + "</a>" : "") + "</td>");
		
		StringUtil.append(sb, "<td width=30 align=center><button action=\"", action.replace("%page%", String.valueOf(_total)), "\" back=L2UI_CH3.next1_down fore=L2UI_CH3.next1 width=16 height=16></td></tr></table>");
	}
	
	public void generateSearch(String action, int height)
	{
		append("<table width=280 height=", height, "><tr>");
		append("<td width=70 align=center>Search</td>");
		append("<td width=140><edit var=\"search\" width=130 height=15></td>");
		append("<td width=70><button value=\"Find\" action=\"", action, " 1 $search\" width=65 height=19 back=\"L2UI_ch3.smallbutton2_over\" fore=\"L2UI_ch3.smallbutton2\"></td>");
		append("</tr><tr>");
		append("<td></td>");
		append("<td align=center>Found ", _totalEntries, " results</td>");
		append("<td></td>");
		append("</tr></table>");
	}
	
	public String getContent()
	{
		return _content.toString();
	}
	
	public void resetContent()
	{
		_content.setLength(0);
	}
	
	@Override
	public A get(int index)
	{
		return _list.get(index);
	}
	
	@Override
	public int size()
	{
		return _list.size();
	}
	
	public void generateSpace(StringBuilder sb)
	{
		IntStream.range(size(), _limit).forEach(x -> sb.append(NORMAL_LINE_SIZE));
	}
	
	public void generateSpace(StringBuilder sb, String content)
	{
		IntStream.range(size(), _limit).forEach(x -> sb.append(content));
	}
	
	public void generatePagesMedium(StringBuilder sb, String action)
	{
		StringUtil.append(sb, "<table width=400><tr><td width=35 align=left><button action=\"", action.replace("%page%", String.valueOf(1)), "\" back=L2UI_CH3.prev1_down fore=L2UI_CH3.prev1 width=16 height=16></td>");
		
		for (int index = _page - 6; index < _page - 1; index++)
			StringUtil.append(sb, "<td width=30 align=center>" + (index < 0 ? "" : "<a action=\"" + action.replace("%page%", String.valueOf(index + 1)) + "\">" + (((index + 1) < 10 ? "0" : "") + (index + 1)) + "</a>") + "</td>");
		
		StringUtil.append(sb, "<td width=30 align=center><font color=LEVEL>", ((_page < 10 ? "0" : "") + (_page == 0 ? "1" : _page)), "</font></td>");
		
		for (int index = _page; index < _page + 5; index++)
			StringUtil.append(sb, "<td width=30 align=center>" + (index < _total ? "<a action=\"" + action.replace("%page%", String.valueOf(index + 1)) + "\">" + (((index + 1) < 10 ? "0" : "") + (index + 1)) + "</a>" : "") + "</td>");
		
		StringUtil.append(sb, "<td width=35 align=right><button action=\"", action.replace("%page%", String.valueOf(_total)), "\" back=L2UI_CH3.next1_down fore=L2UI_CH3.next1 width=16 height=16></td></tr></table>");
	}
	
	public void generatePagesCommunity(StringBuilder sb, String action)
	{
		StringUtil.append(sb, "<table width=256 bgcolor=000000><tr><td width=25 align=center><button action=\"", action.replace("%page%", String.valueOf(1)), "\" back=L2UI_CH3.prev1_down fore=L2UI_CH3.prev1 width=16 height=16></td>");
		
		for (int index = _page - 10; index < _page - 1; index++)
			StringUtil.append(sb, "<td width=30>" + (index < 0 ? "" : "<a action=\"" + action.replace("%page%", String.valueOf(index + 1)) + "\">" + (((index + 1) < 10 ? "0" : "") + (index + 1)) + "</a>") + "</td>");
		
		StringUtil.append(sb, "<td width=30><font color=LEVEL>", ((_page < 10 ? "0" : "") + (_page == 0 ? "1" : _page)), "</font></td>");
		
		for (int index = _page; index < _page + 9; index++)
			StringUtil.append(sb, "<td width=30>" + (index < _total ? "<a action=\"" + action.replace("%page%", String.valueOf(index + 1)) + "\">" + (((index + 1) < 10 ? "0" : "") + (index + 1)) + "</a>" : "") + "</td>");
		
		StringUtil.append(sb, "<td width=25 align=center><button action=\"", action.replace("%page%", String.valueOf(_total)), "\" back=L2UI_CH3.next1_down fore=L2UI_CH3.next1 width=16 height=16></td></tr></table>");
	}
}