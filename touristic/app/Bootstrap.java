import models.City;
import models.Country;
import models.User;
import play.Logger;
import play.Play;
import play.db.jpa.JPA;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.test.Fixtures;

@OnApplicationStart
public class Bootstrap extends Job {
	@play.db.jpa.NoTransaction
	public void doJob() {
		boolean doPostGeo=false;
		if (Country.count() == 0 && City.count() == 0) {
			/*Logger.debug("Loading countries.yaml");
			Fixtures.loadModels("db-data/countries.yaml");
			Logger.debug("Loading cities-15000.yaml");
			Fixtures.loadModels("db-data/cities-15000.yaml");
			JPA.em().flush();*/
			Logger.debug("Loading geo.sql");
			Fixtures.executeSQL(Play.getFile("conf/db-data/geo.sql"));
			doPostGeo=true;
			Logger.debug("Loaded geo.sql");
		}
		if (User.count() == 0) {
			Logger.debug("Loading initial.yaml");
			Fixtures.loadModels("initial.yaml");
			JPA.em().flush();
		}
		
		if (doPostGeo)
		{
			Logger.debug("Loading posthotel.sql");
			Fixtures.executeSQL(Play.getFile("conf/db-data/posthotel.sql"));
			Logger.debug("Loaded posthotel.sql");
		}
		Logger.debug("Finished bootstraping");
	}
}