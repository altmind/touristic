package controllers;

import play.*;
import play.data.validation.Valid;
import play.db.jpa.JPQL;
import play.i18n.Messages;
import play.mvc.*;
import tools.DataAccess;
import tools.Paginator;
import tools.Pair;

import java.util.*;

import extensions.QueryStringExtensions;

import bo.filters.Filter;
import bo.filters.FilterCountry;
import bo.filters.FilterHotel;

import models.*;
import models.UserRole.UserRoleType;

@With(Secure.class)
public class CatalogCountries extends Controller {
	public static int ENTITIES_PER_PAGE = Integer.parseInt(Play.configuration.getProperty("entities.per.page"));
	
	@Check("manager")
	public static void list(FilterCountry filter, String resetFilter) {
		if (org.apache.commons.lang.StringUtils.isNotBlank(resetFilter))
			redirect(QueryStringExtensions.queryStringExcluding(request, "filter\\..*", "resetFilter" ),true);
		
		Pair<String, Object[]> dbparams = DataAccess.prepareFilter(
				new DataAccess.RequestParam( String.class,"filter.name","Name"),
				new DataAccess.RequestParam( Boolean.class,"filter.active","Active",Boolean.TRUE)
				);
		
		String query=DataAccess.findQuery("Country",dbparams.getFirst())+DataAccess.orderQuery(request);
		List<Country> countries = Country.find(query,dbparams.getSecond()).fetch(QueryStringExtensions.queryPageNumber(request), ENTITIES_PER_PAGE);
		long entitiesCount = Country.count(DataAccess.findQuery("Country",dbparams.getFirst()),dbparams.getSecond());
		render("catalogs/countries/list.html", filter, request, countries, entitiesCount);
	}

	private static void show(Country country, boolean editableFlag) {
		String editable = " disabled=\"disabled\"";
		if (editableFlag) {
			editable = "";
		}
		
		renderTemplate("catalogs/countries/view.html", country, editable);
	}
	@Check("agent")
	private static void save(Country country) {
		validation.valid(country);
		
		if (validation.hasErrors()) {

			show(country, true);
		}
		else
			country.save();
		redirect("CatalogCountries.list");
	}

	@Check("agent")
	public static void view(long id) {
		Country country = Country.findById(id);
		notFoundIfNull(country);
		if (request.params.get("do.edit") != null) {
			edit(country, id);
		} else
			show(country, false);
	}

	
	@Check("agent")
	public static void edit(Country country, long id) {
		Country countryFromDatabase = Country.findById(id);
		notFoundIfNull(countryFromDatabase);
		if (country==null || country.id==null)
			country=countryFromDatabase;
		
		if (request.params.get("do.save") != null) {
			save(country);
		} else
			show(country, true);
	}

	@Check("agent")
	public static void create(Country country) {
		if (request.params.get("do.save") != null) {
			save(country);
		} else
			show(new Country().reset(), true);
	}

}