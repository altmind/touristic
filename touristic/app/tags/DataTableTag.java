package tags;

import extensions.QueryStringExtensions;
import groovy.lang.Closure;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.sun.org.apache.xml.internal.security.utils.JavaUtils;

import controllers.Application;

import bo.filters.Filter;

import models.Client;

import play.Play;
import play.exceptions.TagInternalException;
import play.exceptions.TemplateExecutionException;
import play.i18n.Messages;
import play.libs.Codec;
import play.mvc.Http.Request;
import play.templates.*;
import play.templates.GroovyTemplate.ExecutableTemplate;

@FastTags.Namespace("dt")
public class DataTableTag extends FastTags {
	
	public static int ENTITIES_PER_PAGE = Integer.parseInt(Play.configuration.getProperty("entities.per.page"));
	
	private static void pushToTagStack(ExecutableTemplate template,
			int fromLine, Closure body, String v) {
		if (body.getProperty("_datatable_tagstack") == null) {
			body.setProperty("_datatable_tagstack", new Stack<String>());
		}
		Stack<String> tagStack = (Stack<String>) body
				.getProperty("_datatable_tagstack");
		tagStack.add(v);
	}

	private static void popFromTagStack(ExecutableTemplate template,
			int fromLine, Closure body) {
		if (body.getProperty("_datatable_tagstack") == null) {
			throw new TemplateExecutionException(template.template, fromLine,
					"Tagstack not found", new TagInternalException(
							"Tagstack not found"));
		}
		Stack<String> tagStack = (Stack<String>) body
				.getProperty("_datatable_tagstack");
		tagStack.pop();
	}

	private static String checkBelowAtTagStack(ExecutableTemplate template,
			int fromLine, Closure body, String... v) {
		if (body.getProperty("_datatable_tagstack") == null) {
			throw new TemplateExecutionException(template.template, fromLine,
					"Tagstack not found", new TagInternalException(
							"Tagstack not found"));
		}
		Stack<String> tagStack = (Stack<String>) body
				.getProperty("_datatable_tagstack");
		String peek = tagStack.peek();
		if (peek == null || !ArrayUtils.contains(v, peek)) {
			String msg = "Datatable nesting error. Expected "
					+ ArrayUtils.toString(v) + " as parent, got " + peek;
			throw new TemplateExecutionException(template.template, fromLine,
					msg, new TagInternalException(msg));
		}
		return peek;
	}

	public static void _datatable(Map<?, ?> args, Closure body,
			PrintWriter out, ExecutableTemplate template, int fromLine) {

		Collection values = (Collection) args.get("values");
		Long datasetSize = (Long) args.get("size");
		int currentPage = 1;
		try {
			currentPage = (int) Integer.parseInt(Request.current.get().params
					.get("filter.page"));
		} catch (NumberFormatException e) {
		}
		String uuid = Codec.UUID();
		Object filter = args.get("filter");
		if (values == null)
			throw new TemplateExecutionException(template.template, fromLine,
					"Datatable requires values property",
					new TagInternalException(
							"Datatable requires values property"));
		// if (filter==null)
		// throw new TemplateExecutionException(template.template, fromLine,
		// "Datatable requires filter property", new
		// TagInternalException("Datatable requires values property"));

		pushToTagStack(template, fromLine, body, "dataTable");
		out.println("<table class=\"datatable\">");
		Object oldValues = body.getProperty("values");
		Object oldFilter = body.getProperty("filter");
		body.setProperty("values", values);
		body.setProperty("filter", filter);
		body.call();
		body.setProperty("filter", oldFilter);
		body.setProperty("values", oldValues);
		out.println("</table>");
		out.println("<div class=\"datatablescroller\">");
		renderPagination(args, body, out, template, fromLine, datasetSize, currentPage, uuid);
		out.println("</div>");
		popFromTagStack(template, fromLine, body);
	}

	private static void renderPagination(Map<?, ?> args, Closure body,
			PrintWriter out, ExecutableTemplate template, int fromLine,
			Long size, int currentPage, String uuid) {
		Integer pages = null;
		if (size != null)
			pages = (int) Math.ceil(size / (double)ENTITIES_PER_PAGE);
		out.println("<table><tr><td>");
		if (currentPage!=1)
			out.println("<a href=\""+QueryStringExtensions.queryStringIncluding(Request.current.get(), "filter.page", currentPage-1)+"\">&lt;</a>");
		else
			out.println("&lt;");
		out.println("</td><td>");

		if (pages != null) {
			out.println(Messages.get("datatable.pagination.page"));
			
			out.println("<select id=\"pagination_select_"+uuid+"\">");
			for (int i = 1; i <= pages; i++) {
				out.println("<option value=\"" + i + "\""+(i==currentPage?" selected=\"selected\"":"")+">" + i + "</option>");
			}
			out.println("</select>");
			String urlWithMissingPageNumber = QueryStringExtensions.queryStringIncluding(Request.current(), "filter.page", "{0}");
			out.println("<script>$('#pagination_select_"+uuid+"').change(function(){var pagenr = $('#pagination_select_"+uuid+"').val(); var url = \""+urlWithMissingPageNumber+"\".replace('{0}',pagenr); location.replace(url); });</script>");
			out.println(Messages.get("datatable.pagination.page.of.total")
					+ " ");
			out.println(pages);
		} else {
			out.println(Messages.get("datatable.pagination.pages"));
		}
		out.println("</td><td>");
		if (currentPage<pages)
			out.println("<a href=\""+QueryStringExtensions.queryStringIncluding(Request.current.get(), Play.configuration.getProperty("entities.paginator.query.string"), currentPage+1)+"\">&gt;</a>");
		else
			out.println("&gt;");
		
		out.println("</td></tr></table>");
	}

	public static void _head(Map<?, ?> args, Closure body, PrintWriter out,
			ExecutableTemplate template, int fromLine) {
		checkBelowAtTagStack(template, fromLine, body, "dataTable");
		pushToTagStack(template, fromLine, body, "head");
		out.println("<thead><tr>");
		body.call();
		out.println("</tr></thead>");
		popFromTagStack(template, fromLine, body);
	}

	public static void _row(Map<?, ?> args, Closure body, PrintWriter out,
			ExecutableTemplate template, int fromLine) {

		checkBelowAtTagStack(template, fromLine, body, "dataTable");

		Collection values = (Collection) body.getProperty("values");
		if (values == null)
			throw new AssertionError(
					"Datatable tag should have checked values existence. Strangely we dont have any.");

		pushToTagStack(template, fromLine, body, "row");
		Object oldValue = body.getProperty("row");

		for (Object value : values) {
			out.println("<tr>");
			body.setProperty("row", value);
			body.call();
			out.println("</tr>");
		}

		body.setProperty("row", oldValue);
		popFromTagStack(template, fromLine, body);
	}

	public static void _col(Map<?, ?> args, Closure body, PrintWriter out,
			ExecutableTemplate template, int fromLine) {
		String tagStackBelow = checkBelowAtTagStack(template, fromLine, body,
				"head", "row");
		// TODO: wtf why arg named "orderby" is passed here as "arg"
		String orderBy = (String) args.get("arg");
		if (!"head".equals(tagStackBelow) && orderBy != null)
			throw new TemplateExecutionException(template.template, fromLine,
					"Only columns in head can have orderBy",
					new TagInternalException(
							"Only columns in head can have orderBy"));
		pushToTagStack(template, fromLine, body, "col");
		out.println("<td>");
		if ("head".equals(tagStackBelow)) {
			if (StringUtils.isNotBlank(orderBy)) {
				out.println("<a href=\"");
				out.println(QueryStringExtensions.queryStringSwitch(
						Request.current.get(), "filter.order",
						orderBy + "/asc", orderBy + "/desc"));
				out.println("\">");
			}
			body.call();
			if (StringUtils.isNotBlank(orderBy))
				out.println("</a>");
		} else
			body.call();
		out.println("</td>");
		popFromTagStack(template, fromLine, body);
	}
}
