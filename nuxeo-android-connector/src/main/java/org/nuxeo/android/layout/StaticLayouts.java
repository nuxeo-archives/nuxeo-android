package org.nuxeo.android.layout;


public class StaticLayouts {

	public final static String DEFAULT_LAYOUT = "{ " + LayoutJSONParser.WIDGETS_DEF + "= ["
		+ "{name='title', type='text', attributeName='dc:title', label='Title'},"
		+ "{name='description', type='text', attributeName='dc:description', label='Description'},"
		+ "{name='valid', type='date', attributeName='dc:valid', label='Validity'}"
		+ "], "
		+ " " + LayoutJSONParser.ROWS_DEF + "= ["
		+ "['title'],"
		+ "['description'],"
		+ "['valid']"
		+ "]}";

	public final static String DEFAULT_LAYOUT_WITYH_DATE = "{ " + LayoutJSONParser.WIDGETS_DEF + "= ["
	+ "{name='title', type='text', attributeName='dc:title', label='Title'},"
	+ "{name='description', type='text', attributeName='dc:description', label='Description'},"
	+ "{name='created', type='text', attributeName='dc:created', label='Created'}"
	+ "], "
	+ " " + LayoutJSONParser.ROWS_DEF + "= ["
	+ "['title'],"
	+ "['description'],"
	+ "['created']"
	+ "]}";

}
