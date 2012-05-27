package extensions;

import java.util.Arrays;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

import play.Play;
import play.mvc.Http.Request;
import play.templates.JavaExtensions;

public class QueryStringExtensions extends JavaExtensions {
	public static String queryStringSwitch(Request req, String param,
			Object... values) {

		StringBuffer sb = new StringBuffer(StringUtils.substringBefore(req.url, "?")).append('?');
		String[] pieces = StringUtils.substringAfter(req.url,"?").split("&");

		String val = null;
		String needle = param + "=";
		for(String e: pieces){
			if (StringUtils.startsWith(e, needle)) {
				String v = e.substring(needle.length());
				for (int i = 0; i < values.length; i++) {
					if (values[i].equals(v)) {
						if (values.length - 1 == i) {
							val = values[0].toString();
							break;
						} else {
							val = values[i + 1].toString();
							break;
						}
					}
				}
			}
		}
		if (val == null)
			val = values[0].toString();
		return queryStringIncluding(req, param, val);
	}

	public static String queryStringIncluding(Request req, String param,
			Object value) {
		
		StringBuffer sb = new StringBuffer(StringUtils.substringBefore(req.url, "?")).append('?');
		
		String[] pieces = StringUtils.substringAfter(req.url,"?").split("&");
		boolean pieceFound = false;
		boolean firstMatch=true;
		for (String piece : pieces) {
			if (!firstMatch)
				sb.append('&');
			firstMatch=false;
			String pieceName;
			if (piece.contains("=")) {
				pieceName = piece.substring(0, piece.indexOf('='));
			} else {
				pieceName = piece;
			}
			if (!param.equals(pieceName))
				sb.append(piece);
			else {
				sb.append(param).append('=').append(value);
				pieceFound = true;
			}
		}
		if (!pieceFound) {
			if (sb.length() > 1)
				sb.append('&');
			sb.append(param).append('=').append(value);
		}
		return sb.toString();

	}

	public static String queryStringExcluding(Request req,
			String... patternStrings) {

		StringBuffer sb = new StringBuffer(StringUtils.substringBefore(req.url, "?")).append('?');
		
		String[] pieces = StringUtils.substringAfter(req.url,"?").split("&");

		Pattern[] patterns = new Pattern[patternStrings.length];
		for (int i = 0; i < patternStrings.length; i++) {
			patterns[i] = Pattern.compile(patternStrings[i],
					Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
		}
		boolean firstMatch=true;
		for (String piece : pieces) {

			String pieceName;
			if (piece.contains("=")) {
				pieceName = piece.substring(0, piece.indexOf('='));
			} else {
				pieceName = piece;
			}
			boolean matches = false;
			for (Pattern pattern : patterns) {
				if (pattern.matcher(pieceName).matches()) {
					matches = true;
					break;
				}
			}
			if (!matches) {
				if (!firstMatch)
					sb.append('&');
				sb.append(piece);
				firstMatch=false;
			}

		}
		if ("?".equals(sb.substring(sb.length()-1)))
			sb.setLength(sb.length()-1);
		return sb.toString();
	}


	public static int queryPageNumber(Request req) {
		String pageParam = req.params.get(Play.configuration
				.getProperty("entities.paginator.query.string"));
		int pageNumber = 1;
		try {
			pageNumber = Integer.parseInt(pageParam);
			if (pageNumber < 1)
				pageNumber = 1;
		} catch (Throwable t) {
			pageNumber = 1;
		}
		return pageNumber;
	}
}
