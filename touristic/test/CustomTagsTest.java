import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;

import tags.CustomTags;


public class CustomTagsTest {

	@Test
	public void persistFieldsEmpty()
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintWriter pw = new PrintWriter(baos);
		Set<Entry<String,String[]>> args = new HashSet<Entry<String,String[]>>();
		CustomTags.persistFieldsInternal(pw, "", "", args);
		String out = new String(baos.toByteArray());
		Assert.assertEquals("", out);
	}
	
	@Test
	public void persistFieldsAllSimple()
	{
		Writer result = new StringWriter();
		PrintWriter pw = new PrintWriter(result);
		Set<Entry<String,String[]>> args = new HashSet<Entry<String,String[]>>();
		args.add(new AbstractMap.SimpleEntry<String, String[]>("filter.v1",new String[]{"v1","v2"}));
		CustomTags.persistFieldsInternal(pw, "", "", args);
		String out = result.toString();
		Assert.assertTrue(out.contains("filter.v1[]"));
		Assert.assertTrue(out.contains("v1"));
		Assert.assertTrue(out.contains("v2"));
	}
}
