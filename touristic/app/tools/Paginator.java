package tools;

import java.util.List;

import play.Play;
import play.mvc.Http.Request;

public class Paginator {
	public static int ENTITIES_PER_PAGE = Integer.parseInt(Play.configuration.getProperty("entities.per.page"));
	
	static public <T> List<T> paginate(List<T> list, Request req){
		String pageParam = req.params.get(Play.configuration.getProperty("entities.paginator.query.string"));
		int pageNumber=1;
		try{
			pageNumber=Integer.parseInt(pageParam);
			if (pageNumber<1) pageNumber=1;
		}
		catch(Throwable t)
		{
			pageNumber=1;
		}
		int startEntity=(pageNumber-1)*ENTITIES_PER_PAGE;
		if (startEntity+1>list.size())
		{
			startEntity=list.size();
		}
		int endEntity=(pageNumber)*ENTITIES_PER_PAGE;
		if (endEntity+1>list.size())
		{
			endEntity=list.size();
		}
		return list.subList(startEntity, endEntity);
	}
}
