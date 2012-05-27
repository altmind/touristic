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
import bo.filters.FilterHotel;

import models.*;
import models.UserRole.UserRoleType;

@With(Secure.class)
public class CatalogHotels extends Controller {
	public static int ENTITIES_PER_PAGE = Integer.parseInt(Play.configuration.getProperty("entities.per.page"));
	
	@Check("manager")
	public static void list(FilterHotel filter, String resetFilter) {
		if (org.apache.commons.lang.StringUtils.isNotBlank(resetFilter))
			redirect(QueryStringExtensions.queryStringExcluding(request, "filter\\..*", "resetFilter" ),true);
		
		Pair<String, Object[]> dbparams = DataAccess.prepareFilter(
				new DataAccess.RequestParam( String.class,"filter.hotelName","Name"),
				new DataAccess.RequestParam( String.class,"filter.city","City.name"),
				new DataAccess.RequestParam( Boolean.class,"filter.active","Active",Boolean.TRUE)
				);
		
		String query=DataAccess.findQuery("Hotel",dbparams.getFirst())+DataAccess.orderQuery(request);
		List<Hotel> hotels = Hotel.find(query,dbparams.getSecond()).fetch(QueryStringExtensions.queryPageNumber(request), ENTITIES_PER_PAGE);
		long entitiesCount = Hotel.count(DataAccess.findQuery("Hotel",dbparams.getFirst()),dbparams.getSecond());
		render("catalogs/hotels/list.html", filter, request, hotels, entitiesCount);
	}

	private static void show(Hotel hotel, boolean editableFlag) {
		String editable = " disabled=\"disabled\"";
		if (editableFlag) {
			editable = "";
		}
		
		List<City> allCities = City.find("byActive", true).fetch();
		List<HotelCategory> allCategories = HotelCategory.find("byActive", true).fetch();
		List<Language> allLanguages = Language.find("byActive", true).fetch();
		
		renderTemplate("catalogs/hotels/view.html", hotel, editable, allCities, allCategories, allLanguages);
	}
	@Check("agent")
	private static void save(Hotel hotel) {
		validation.valid(hotel);
		
		if (validation.hasErrors()) {

			show(hotel, true);
		}
		else
			hotel.save();
		redirect("CatalogHotels.list");
	}

	@Check("agent")
	public static void view(long id) {
		Hotel hotel = Hotel.findById(id);
		notFoundIfNull(hotel);
		if (request.params.get("do.edit") != null) {
			edit(hotel, id);
		} else
			show(hotel, false);
	}

	
	@Check("agent")
	public static void edit(Hotel hotel, long id) {
		Hotel hotelFromDatabase = Hotel.findById(id);
		notFoundIfNull(hotelFromDatabase);
		if (hotel==null || hotel.id==null)
			hotel=hotelFromDatabase;
		
		if (request.params.get("do.save") != null) {
			save(hotel);
		} else
			show(hotel, true);
	}

	@Check("agent")
	public static void create(Hotel hotel) {
		if (request.params.get("do.save") != null) {
			save(hotel);
		} else
			show(new Hotel().reset(), true);
	}

}