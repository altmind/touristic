package tools;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import play.Play;
import play.db.jpa.JPQL;
import play.mvc.Http.Request;

public class DataAccess {

	public static String findQuery(String entityName, String query, Object... params) {
		return JPQL.instance.createFindByQuery(entityName, entityName, query, params)+" ";
	}

/*	public static String mergeQuery(String findQuery, String orderQuery) {
		return findQuery+" "+orderQuery;
	}*/

	public static String orderQuery(Request req) {

		String orderByParam = req.params.get(Play.configuration
				.getProperty("entities.paginator.orderby.query.string"));
		if (orderByParam == null)
			orderByParam = "";
		Pattern pattern = Pattern.compile("([\\w\\.]+)/(asc|desc)",
				Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
		Matcher regexMatcher = pattern.matcher(orderByParam);
		if (regexMatcher.matches()) {
			String col = regexMatcher.group(1);
			String order = regexMatcher.group(2);
			return "order by " + col + " " + order+", id asc";
		} else
			return "order by id asc";
	}

	public static Pair<String, Object[]> prepareFilter(RequestParam... paramlist) {
		StringBuffer findQuery = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		
		for (RequestParam param : paramlist) {
			String queryStringParamValue = Request.current.get().params.get(param.getQueryStringParamName());
			if (!StringUtils.isBlank(queryStringParamValue) || param.getDef()!=null)
			{
				if (findQuery.length()==0)
					findQuery.append("by");
				else
					findQuery.append("And");
				findQuery.append(param.getDatabaseColumnName());
				
				if (StringUtils.isBlank(queryStringParamValue) && param.getDef()!=null)
				{
					params.add(param.getDef());
				}
				else if (String.class.equals(param.getClazz()))
				{
					findQuery.append("Ilike");
					params.add("%"+queryStringParamValue+"%");
				}
				else if (Boolean.class.equals(param.getClazz()))
				{
					params.add(Boolean.parseBoolean(queryStringParamValue));
				}
				else if (Long.class.equals(param.getClazz()))
				{
					params.add(Long.parseLong(queryStringParamValue));
				}
				else if (Integer.class.equals(param.getClazz()))
				{
					params.add(Integer.parseInt(queryStringParamValue));
				}
				else
					params.add(queryStringParamValue);
			}
		}
		
		return new Pair<String, Object[]>(findQuery.toString(), params.toArray());
	}
	
	public static class RequestParam
	{
		
		@Override
		public String toString() {
			return "RequestParam [clazz=" + clazz + ", queryStringParamName="
					+ queryStringParamName + ", databaseColumnName="
					+ databaseColumnName + "]";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((clazz == null) ? 0 : clazz.hashCode());
			result = prime
					* result
					+ ((databaseColumnName == null) ? 0 : databaseColumnName
							.hashCode());
			result = prime
					* result
					+ ((queryStringParamName == null) ? 0
							: queryStringParamName.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			RequestParam other = (RequestParam) obj;
			if (clazz == null) {
				if (other.clazz != null)
					return false;
			} else if (!clazz.equals(other.clazz))
				return false;
			if (databaseColumnName == null) {
				if (other.databaseColumnName != null)
					return false;
			} else if (!databaseColumnName.equals(other.databaseColumnName))
				return false;
			if (queryStringParamName == null) {
				if (other.queryStringParamName != null)
					return false;
			} else if (!queryStringParamName.equals(other.queryStringParamName))
				return false;
			return true;
		}

		private Class<?> clazz;
		private String queryStringParamName;
		private String databaseColumnName;
		private Object def;

		public RequestParam(Class<?> clazz, String queryStringParamName, String databaseColumnName)
		{
			this.setClazz(clazz);
			this.setQueryStringParamName(queryStringParamName);
			this.setDatabaseColumnName(databaseColumnName);
		}

		public RequestParam(Class<?> clazz, String queryStringParamName, String databaseColumnName, Object def) {
			this.setClazz(clazz);
			this.setQueryStringParamName(queryStringParamName);
			this.setDatabaseColumnName(databaseColumnName);
			this.setDef(def);
		}

		public Class<?> getClazz() {
			return clazz;
		}

		public void setClazz(Class<?> clazz) {
			this.clazz = clazz;
		}

		public String getQueryStringParamName() {
			return queryStringParamName;
		}

		public void setQueryStringParamName(String queryStringParamName) {
			this.queryStringParamName = queryStringParamName;
		}

		public String getDatabaseColumnName() {
			return databaseColumnName;
		}

		public void setDatabaseColumnName(String databaseColumnName) {
			this.databaseColumnName = databaseColumnName;
		}

		public Object getDef() {
			return def;
		}

		public void setDef(Object def) {
			this.def = def;
		}
	}
	
}
