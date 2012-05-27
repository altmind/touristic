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
import bo.filters.FilterRoomtype;

import models.*;
import models.UserRole.UserRoleType;

@With(Secure.class)
public class CatalogRoomtypes extends Controller {
	public static int ENTITIES_PER_PAGE = Integer.parseInt(Play.configuration.getProperty("entities.per.page"));
	
	@Check("manager")
	public static void list(FilterRoomtype filter, String resetFilter) {
		if (org.apache.commons.lang.StringUtils.isNotBlank(resetFilter))
			redirect(QueryStringExtensions.queryStringExcluding(request, "filter\\..*", "resetFilter" ),true);
		
		Pair<String, Object[]> dbparams = DataAccess.prepareFilter(
				new DataAccess.RequestParam( String.class,"filter.name","Name"),
				new DataAccess.RequestParam( Boolean.class,"filter.active","Active",Boolean.TRUE)
				);
		
		String query=DataAccess.findQuery("Roomtype",dbparams.getFirst())+DataAccess.orderQuery(request);
		List<Roomtype> roomtypes = Roomtype.find(query,dbparams.getSecond()).fetch(QueryStringExtensions.queryPageNumber(request), ENTITIES_PER_PAGE);
		long entitiesCount = Roomtype.count(DataAccess.findQuery("Roomtype",dbparams.getFirst()),dbparams.getSecond());
		render("catalogs/roomtypes/list.html", filter, request, roomtypes, entitiesCount);
	}

	private static void show(Roomtype roomtype, boolean editableFlag) {
		String editable = " disabled=\"disabled\"";
		if (editableFlag) {
			editable = "";
		}
		
		renderTemplate("catalogs/roomtypes/view.html", roomtype, editable);
	}
	@Check("agent")
	private static void save(Roomtype roomtype) {
		validation.valid(roomtype);
		
		if (validation.hasErrors()) {

			show(roomtype, true);
		}
		else
			roomtype.save();
		redirect("CatalogRoomtypes.list");
	}

	@Check("agent")
	public static void view(long id) {
		Roomtype roomtype = Roomtype.findById(id);
		notFoundIfNull(roomtype);
		if (request.params.get("do.edit") != null) {
			edit(roomtype, id);
		} else
			show(roomtype, false);
	}

	
	@Check("agent")
	public static void edit(Roomtype roomtype, long id) {
		Roomtype roomtypeFromDatabase = Roomtype.findById(id);
		notFoundIfNull(roomtypeFromDatabase);
		if (roomtype==null || roomtype.id==null)
			roomtype=roomtypeFromDatabase;
		
		if (request.params.get("do.save") != null) {
			save(roomtype);
		} else
			show(roomtype, true);
	}

	@Check("agent")
	public static void create(Roomtype roomtype) {
		if (request.params.get("do.save") != null) {
			save(roomtype);
		} else
			show(new Roomtype().reset(), true);
	}

}