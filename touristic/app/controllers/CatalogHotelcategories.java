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
import bo.filters.FilterHotelcategory;

import models.*;
import models.UserRole.UserRoleType;

@With(Secure.class)
public class CatalogHotelcategories extends Controller {
	public static int ENTITIES_PER_PAGE = Integer.parseInt(Play.configuration.getProperty("entities.per.page"));
	
	@Check("manager")
	public static void list(FilterHotelcategory filter, String resetFilter) {
		if (org.apache.commons.lang.StringUtils.isNotBlank(resetFilter))
			redirect(QueryStringExtensions.queryStringExcluding(request, "filter\\..*", "resetFilter" ),true);
		
		Pair<String, Object[]> dbparams = DataAccess.prepareFilter(
				new DataAccess.RequestParam( String.class,"filter.name","Name"),
				new DataAccess.RequestParam( Integer.class,"filter.starRating","starRating"),
				new DataAccess.RequestParam( Boolean.class,"filter.active","Active",Boolean.TRUE)
				);
		
		String query=DataAccess.findQuery("HotelCategory",dbparams.getFirst())+DataAccess.orderQuery(request);
		List<HotelCategory> hotelcategories = HotelCategory.find(query,dbparams.getSecond()).fetch(QueryStringExtensions.queryPageNumber(request), ENTITIES_PER_PAGE);
		long entitiesCount = HotelCategory.count(DataAccess.findQuery("HotelCategory",dbparams.getFirst()),dbparams.getSecond());
		render("catalogs/hotelcategories/list.html", filter, request, hotelcategories, entitiesCount);
	}

	private static void show(HotelCategory hotelcategory, boolean editableFlag) {
		String editable = " disabled=\"disabled\"";
		if (editableFlag) {
			editable = "";
		}
		
		renderTemplate("catalogs/hotelcategories/view.html", hotelcategory, editable);
	}
	@Check("agent")
	private static void save(HotelCategory hotelcategory) {
		validation.valid(hotelcategory);
		
		if (validation.hasErrors()) {

			show(hotelcategory, true);
		}
		else
			hotelcategory.save();
		redirect("CatalogHotelcategories.list");
	}

	@Check("agent")
	public static void view(long id) {
		HotelCategory hotelcategory = HotelCategory.findById(id);
		notFoundIfNull(hotelcategory);
		if (request.params.get("do.edit") != null) {
			edit(hotelcategory, id);
		} else
			show(hotelcategory, false);
	}

	
	@Check("agent")
	public static void edit(HotelCategory hotelcategory, long id) {
		HotelCategory hotelcategoryFromDatabase = HotelCategory.findById(id);
		notFoundIfNull(hotelcategoryFromDatabase);
		if (hotelcategory==null || hotelcategory.id==null)
			hotelcategory=hotelcategoryFromDatabase;
		
		if (request.params.get("do.save") != null) {
			save(hotelcategory);
		} else
			show(hotelcategory, true);
	}

	@Check("agent")
	public static void create(HotelCategory hotelcategory) {
		if (request.params.get("do.save") != null) {
			save(hotelcategory);
		} else
			show(new HotelCategory().reset(), true);
	}

}