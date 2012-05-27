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
import bo.filters.FilterUsers;

import models.*;
import models.UserRole.UserRoleType;

@With(Secure.class)
public class Users extends Controller {
	public static int ENTITIES_PER_PAGE = Integer.parseInt(Play.configuration.getProperty("entities.per.page"));
	
	@Check("manager")
	public static void list(FilterUsers filter, String resetFilter) {
		if (org.apache.commons.lang.StringUtils.isNotBlank(resetFilter))
			redirect(QueryStringExtensions.queryStringExcluding(request, "filter\\..*", "resetFilter" ),true);
		
		Pair<String, Object[]> dbparams = DataAccess.prepareFilter(
				new DataAccess.RequestParam( String.class,"filter.login","Login"),
				new DataAccess.RequestParam( Boolean.class,"filter.active","Active",Boolean.TRUE)
				);
		
		String query=DataAccess.findQuery("User",dbparams.getFirst())+DataAccess.orderQuery(request);
		List<User> users = User.find(query,dbparams.getSecond()).fetch(QueryStringExtensions.queryPageNumber(request), ENTITIES_PER_PAGE);
		long entitiesCount = User.count(DataAccess.findQuery("User",dbparams.getFirst()),dbparams.getSecond());
		render("users/list.html", filter, request, users, entitiesCount);
	}

	private static void show(User user, boolean editableFlag) {
		String editable = " disabled=\"disabled\"";
		if (editableFlag) {
			editable = "";
		}
		
		renderTemplate("users/view.html", user, editable);
	}
	@Check("agent")
	private static void save(User user) {
		validation.valid(user);
		
		if (validation.hasErrors()) {

			show(user, true);
		}
		else
			user.save();
		redirect("Users.list");
	}

	@Check("agent")
	public static void view(long id) {
		User user = User.findById(id);
		notFoundIfNull(user);
		if (request.params.get("do.edit") != null) {
			edit(user, id);
		} else
			show(user, false);
	}

	
	@Check("agent")
	public static void edit(User user, long id) {
		User userFromDatabase = User.findById(id);
		notFoundIfNull(userFromDatabase);
		if (user==null || user.id==null)
			user=userFromDatabase;
		
		if (request.params.get("do.save") != null) {
			save(user);
		} else
			show(user, true);
	}

	@Check("agent")
	public static void create(User user) {
		if (request.params.get("do.save") != null) {
			save(user);
		} else
			show(new User().reset(), true);
	}

}