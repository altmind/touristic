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

import bo.filters.FilterClient;
import bo.filters.Filter;

import models.*;
import models.UserRole.UserRoleType;

@With(Secure.class)
public class Clients extends Controller {
	public static int ENTITIES_PER_PAGE = Integer.parseInt(Play.configuration.getProperty("entities.per.page"));
	@Check("agent")
	public static void list(FilterClient filter, String resetFilter) {
		if (org.apache.commons.lang.StringUtils.isNotBlank(resetFilter))
			redirect(QueryStringExtensions.queryStringExcluding(request, "filter\\..*", "resetFilter" ),true);
		
		Pair<String, Object[]> dbparams = DataAccess.prepareFilter(
				new DataAccess.RequestParam( String.class,"filter.clientname","Name"),
				new DataAccess.RequestParam( Boolean.class,"filter.active","Active",Boolean.TRUE)
				);
		
		String query=DataAccess.findQuery("Client",dbparams.getFirst())+DataAccess.orderQuery(request);
		List<Client> clients = Client.find(query,dbparams.getSecond()).fetch(QueryStringExtensions.queryPageNumber(request), ENTITIES_PER_PAGE);
		long entitiesCount = Client.count(DataAccess.findQuery("Client",dbparams.getFirst()),dbparams.getSecond());
		render(filter, request, clients, entitiesCount);
	}

	private static void show(Client client, boolean editableFlag) {
		String editable = " disabled=\"disabled\"";
		if (editableFlag) {
			editable = "";
		}
		renderTemplate("clients/view.html", client, editable);
	}

	private static void save(Client client) {
		//validation.valid(client);
		
		if (validation.hasErrors()) {

			show(client, true);
		}
		else
			client.save();
		redirect("Clients.view",client.id);
	}

	@Check("agent")
	public static void view(long id) {
		Client client = Client.findById(id);
		notFoundIfNull(client);
		if (request.params.get("do.edit") != null) {
			edit(client, id);
		} else
			show(client, false);
	}

	
	@Check("agent")
	public static void edit(Client client, long id) {
		Client clientFromDatabase = Client.findById(id);
		notFoundIfNull(clientFromDatabase);
		if (client==null || client.id==null)
			client=clientFromDatabase;
		
		if (request.params.get("do.save") != null) {
			save(client);
		} else
			show(client, true);
	}

	@Check("agent")
	public static void create(Client client) {
		if (request.params.get("do.save") != null) {
			save(client);
		} else
			show(new Client().reset(), true);
	}

}