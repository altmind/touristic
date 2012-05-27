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
import bo.filters.FilterMealtype;
import bo.filters.FilterRoomtype;

import models.*;
import models.UserRole.UserRoleType;

@With(Secure.class)
public class CatalogMealtypes extends Controller {
	public static int ENTITIES_PER_PAGE = Integer.parseInt(Play.configuration.getProperty("entities.per.page"));
	
	@Check("manager")
	public static void list(FilterMealtype filter, String resetFilter) {
		if (org.apache.commons.lang.StringUtils.isNotBlank(resetFilter))
			redirect(QueryStringExtensions.queryStringExcluding(request, "filter\\..*", "resetFilter" ),true);
		
		Pair<String, Object[]> dbparams = DataAccess.prepareFilter(
				new DataAccess.RequestParam( String.class,"filter.name","Name"),
				new DataAccess.RequestParam( Boolean.class,"filter.active","Active",Boolean.TRUE)
				);
		
		String query=DataAccess.findQuery("Mealtype",dbparams.getFirst())+DataAccess.orderQuery(request);
		List<Mealtype> mealtypes = Mealtype.find(query,dbparams.getSecond()).fetch(QueryStringExtensions.queryPageNumber(request), ENTITIES_PER_PAGE);
		long entitiesCount = Mealtype.count(DataAccess.findQuery("Mealtype",dbparams.getFirst()),dbparams.getSecond());
		render("catalogs/mealtypes/list.html", filter, request, mealtypes, entitiesCount);
	}

	private static void show(Mealtype mealtype, boolean editableFlag) {
		String editable = " disabled=\"disabled\"";
		if (editableFlag) {
			editable = "";
		}
		
		renderTemplate("catalogs/mealtypes/view.html", mealtype, editable);
	}
	@Check("agent")
	private static void save(Mealtype mealtype) {
		validation.valid(mealtype);
		
		if (validation.hasErrors()) {

			show(mealtype, true);
		}
		else
			mealtype.save();
		redirect("CatalogMealtypes.list");
	}

	@Check("agent")
	public static void view(long id) {
		Mealtype mealtype = Mealtype.findById(id);
		notFoundIfNull(mealtype);
		if (request.params.get("do.edit") != null) {
			edit(mealtype, id);
		} else
			show(mealtype, false);
	}

	
	@Check("agent")
	public static void edit(Mealtype mealtype, long id) {
		Mealtype mealtypeFromDatabase = Mealtype.findById(id);
		notFoundIfNull(mealtypeFromDatabase);
		if (mealtype==null || mealtype.id==null)
			mealtype=mealtypeFromDatabase;
		
		if (request.params.get("do.save") != null) {
			save(mealtype);
		} else
			show(mealtype, true);
	}

	@Check("agent")
	public static void create(Mealtype mealtype) {
		if (request.params.get("do.save") != null) {
			save(mealtype);
		} else
			show(new Mealtype().reset(), true);
	}

}