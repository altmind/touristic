package controllers;

import play.*;
import play.data.validation.Valid;
import play.data.validation.Validation;
import play.db.jpa.JPQL;
import play.i18n.Messages;
import play.libs.I18N;
import play.mvc.*;
import tools.DataAccess;
import tools.Paginator;
import tools.Pair;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.commons.lang.StringUtils;

import extensions.QueryStringExtensions;

import bo.filters.FilterClient;
import bo.filters.Filter;
import bo.filters.FilterRequest;

import models.*;
import models.Client.Salutation;
import models.UserRole.UserRoleType;

@With(Secure.class)
public class Requests extends Controller {
	public static int ENTITIES_PER_PAGE = Integer.parseInt(Play.configuration.getProperty("entities.per.page"));
	public static SimpleDateFormat sdf = new SimpleDateFormat(I18N.getDateFormat());
	public static SimpleDateFormat timesdf = new SimpleDateFormat("HH:mm");
	public static final Set<String> serviceTypes = new HashSet<String>(Arrays.asList("hotel", "transfer", "any"));

	@Check("agent")
	public static void list(FilterRequest filter, String resetFilter) {
		if (org.apache.commons.lang.StringUtils.isNotBlank(resetFilter))
			redirect(QueryStringExtensions.queryStringExcluding(request, "filter\\..*", "resetFilter"), true);

		Pair<String, Object[]> dbparams = DataAccess.prepareFilter(new DataAccess.RequestParam(String.class, "filter.requestNumber", "requestNumber"), new DataAccess.RequestParam(Boolean.class,
				"filter.active", "Active", Boolean.TRUE));

		String query = DataAccess.findQuery("Request", dbparams.getFirst()) + DataAccess.orderQuery(request);
		List<Request> requests = Request.find(query, dbparams.getSecond()).fetch(QueryStringExtensions.queryPageNumber(request), ENTITIES_PER_PAGE);
		long entitiesCount = Client.count(DataAccess.findQuery("Request", dbparams.getFirst()), dbparams.getSecond());
		render(filter, request, requests, entitiesCount);
	}

	@Check("agent")
	public static void voucher(Long id) {
		Request requests = Request.findById(id);
		notFoundIfNull(requests);
		Config config = Config.getConfig();
		renderTemplate("reports/voucher.html", requests, config);
	}

	@Check("agent")
	public static void invoice(Long id) {
		Request requests = Request.findById(id);
		notFoundIfNull(requests);
		Config config = Config.getConfig();
		renderTemplate("reports/invoice.html", requests, config);
	}

	@Check("agent")
	public static void view(long id) {
		Request requests = Request.findById(id);
		notFoundIfNull(requests);

		show(requests);
	}

	@Check("agent")
	public static void create() {
		Request requests = new Request().reset();
		requests.active = false;
		requests.save();
		requests.requestNumber = "" + requests.id;
		requests.save();
		redirect("Requests.view", requests.id);
	}

	private static void show(Request requests) {

		renderTemplate("requests/view.html", requests);
	}

	public static void chooseServiceType(long id) {

		if (request.params.get("do.save") != null && serviceTypes.contains(request.params.get("serviceType"))) {
			redirect("Requests.createService", id, request.params.get("serviceType"));
		} else {
			Request requests = Request.findById(id);
			notFoundIfNull(requests);
			renderTemplate("requests/chooseServiceType.html", requests);
		}
	}

	public static void createService(long id, String serviceType) throws ParseException {
		Request requests = Request.findById(id);
		notFoundIfNull(requests);

		if (request.params.get("do.save") != null) {

			try {
				editService(id, serviceType);
				view(requests.id);
			}
			catch (Throwable ex)
			{
				Logger.error(ex.getMessage(), ex);
				createService(id, serviceType);
			} 
			
			
		} else {
			if (!serviceTypes.contains(serviceType))
				notFound();
			List<Mealtype> mealtypes = Mealtype.find("byActive", true).fetch();
			List<Roomtype> roomtypes = Roomtype.find("byActive", true).fetch();
			List<Hotel> hotels = Hotel.find("byActive", true).fetch();
			List<City> cities = City.find("byActive", true).fetch();
			renderTemplate("requests/addService.html", requests, serviceType, mealtypes, roomtypes, hotels, cities);
		}
	}

	private static void editService(long id, String serviceType) throws ParseException {
		Request requests = Request.findById(id);
		notFoundIfNull(requests);
		
		Service service = null;
		if (serviceTypes.contains(serviceType)) {
			if (serviceType.equals("hotel"))
				service = new HotelService();
			else if (serviceType.equals("transfer"))
				service = new TransferService();
			else if (serviceType.equals("any"))
				service = new AnyService();
			else
				throw new RuntimeException("If it is not an hotel, transfer or any service, what is it?");
			Date startDate = sdf.parse(request.get().params.get("startDate"));
			String comment = request.get().params.get("comment");
			BigDecimal price = new BigDecimal(request.get().params.get("price"));
			service.enduserComment = comment;
			service.startDate = startDate;
			service.price = price;

			if (serviceType.equals("hotel")) {
				HotelService svc = (HotelService) service;
				Long mealtypeId = Long.parseLong(request.get().params.get("mealtypeId"));
				Long roomtypeId = Long.parseLong(request.get().params.get("roomtypeId"));
				Date endDate = sdf.parse(request.get().params.get("startDate"));
				Long hotelId = Long.parseLong(request.get().params.get("hotelId"));
				svc.endDate = endDate;
				svc.hotel = Hotel.findById(hotelId);
				svc.mealtype = Mealtype.findById(mealtypeId);
				svc.roomtype = Roomtype.findById(roomtypeId);

			} else if (serviceType.equals("transfer")) {
				TransferService svc = (TransferService) service;
				String endPosition = request.get().params.get("endPosition");
				Long cityId = Long.parseLong(request.params.get("cityId"));
				String startPosition = request.params.get("startPosition");
				Date startTime = timesdf.parse(request.params.get("startTime"));

				svc.endPosition = endPosition;
				svc.startCity = City.findById(cityId);
				svc.startPosition = startPosition;
				svc.startTime = startTime;
			} else if (serviceType.equals("any")) {
				AnyService svc = (AnyService) service;
				String description = request.params.get("description");
				svc.description = description;
			}
			validation.valid(service);
			service.save();
		}
		requests.services.add(service);
		requests.save();
		
	}

	@Check("agent")
	public static void edit(long id) {
		Request requests = Request.findById(id);
		notFoundIfNull(requests);

		if (request.params.get("do.save") != null) {
			requests.tourlead = Client.findById(Long.parseLong(request.params.get("requests.tourlead.id")));
			try {
				String[] names = request.params.getAll("names[]");
				String[] salutations = request.params.getAll("salutation[]");
				String[] dobs = request.params.getAll("dateOfBirth[]");
				assignTouristFields(requests, names, salutations, dobs);
				validation.valid(requests);
				requests.save();
			} catch (ParseException ex) {
				Logger.error(ex.getMessage(), ex);
			} finally {
				view(requests.id);
			}
		} else {
			List<Client> clients = Client.findAll();
			renderTemplate("requests/edit.html", requests, clients);
		}
	}

	private static void assignTouristFields(Request requests, String[] name, String[] salutation, String[] dateOfBirth) throws ParseException {
		List<Tourist> tourists = new ArrayList<Tourist>();
		SimpleDateFormat sdf = new SimpleDateFormat(I18N.getDateFormat());
		for (int i = 0; i < name.length; i++) {
			Tourist tourist = new Tourist();
			tourist.name = name[i];
			if (StringUtils.isNotBlank(salutation[i]))
				tourist.salutation = Salutation.valueOf(salutation[i]);
			if (StringUtils.isNotBlank(dateOfBirth[i]))
				tourist.dateOfBirth = sdf.parse(dateOfBirth[i]);
			tourists.add(tourist);
		}
		requests.tourists.clear();
		requests.tourists.addAll(tourists);

	}

	public static void addPayment(Long id, BigDecimal amount, Payment.PaymentType paymentType) {
		if (amount.signum() > 0 && paymentType != null) {
			Payment payment = new Payment();
			payment.reset();
			payment.active = true;
			payment.amount = amount;
			payment.createdOn = new Date();
			payment.paymentType = paymentType;
			payment.request = Request.findById(id);
			payment.save();
		}
		view(id);
	}
	
	public static void deleteService(Long id, Long svcId)
	{
		Service service=Service.findById(svcId);
		notFoundIfNull(service);
		Request requests=Request.findById(id);
		notFoundIfNull(requests);
		if (!requests.services.contains(service))
		{
			notFound();
		}
		requests.services.remove(service);
		requests.save();
		service.delete();
		view(id);
	}

}