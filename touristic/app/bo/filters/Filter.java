package bo.filters;

import play.data.validation.Required;

public abstract class Filter {
	public Integer from;
	public Integer count;
	public String orderBy;
	public Boolean asc;
}
