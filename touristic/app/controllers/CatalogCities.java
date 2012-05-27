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
import bo.filters.FilterCity;

import models.*;
import models.UserRole.UserRoleType;

@With(Secure.class)
public class CatalogCities extends Controller {
	public static int ENTITIES_PER_PAGE = Integer.parseInt(Play.configuration.getProperty("entities.per.page"));
	
	@Check("manager")
	public static void list(FilterCity filter, String resetFilter) {
		if (org.apache.commons.lang.StringUtils.isNotBlank(resetFilter))
			redirect(QueryStringExtensions.queryStringExcluding(request, "filter\\..*", "resetFilter" ),true);
		
		Pair<String, Object[]> dbparams = DataAccess.prepareFilter(
				new DataAccess.RequestParam( String.class,"filter.name","Name"),
				new DataAccess.RequestParam( String.class,"filter.country","Country.name"),
				new DataAccess.RequestParam( Boolean.class,"filter.active","Active",Boolean.TRUE)
				);
		
		String query=DataAccess.findQuery("City",dbparams.getFirst())+DataAccess.orderQuery(request);
		List<City> cities = City.find(query,dbparams.getSecond()).fetch(QueryStringExtensions.queryPageNumber(request), ENTITIES_PER_PAGE);
		long entitiesCount = City.count(DataAccess.findQuery("City",dbparams.getFirst()),dbparams.getSecond());
		render("catalogs/cities/list.html", filter, request, cities, entitiesCount);
	}

	private static void show(City city, boolean editableFlag) {
		String editable = " disabled=\"disabled\"";
		if (editableFlag) {
			editable = "";
		}
		
		List<Country> allCountries = Country.find("byActive", true).fetch();
		
		renderTemplate("catalogs/cities/view.html", city, editable, allCountries);
	}
	@Check("agent")
	private static void save(City city) {
		validation.valid(city);
		
		if (validation.hasErrors()) {

			show(city, true);
		}
		else
			city.save();
		redirect("CatalogCities.list");
	}

	@Check("agent")
	public static void view(long id) {
		City hity = City.findById(id);
		notFoundIfNull(hity);
		if (request.params.get("do.edit") != null) {
			edit(hity, id);
		} else
			show(hity, false);
	}

	
	@Check("agent")
	public static void edit(City city, long id) {
		City hotelFromDatabase = City.findById(id);
		notFoundIfNull(hotelFromDatabase);
		if (city==null || city.id==null)
			city=hotelFromDatabase;
		
		if (request.params.get("do.save") != null) {
			save(city);
		} else
			show(city, true);
	}

	@Check("agent")
	public static void create(City city) {
		if (request.params.get("do.save") != null) {
			save(city);
		} else
			show(new City().reset(), true);
	}

}