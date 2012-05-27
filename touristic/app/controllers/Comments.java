package controllers;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import models.Comment;
import models.User;
import bo.filters.FilterClient;
import play.i18n.Messages;
import play.mvc.Controller;
import tools.DataAccess;

public class Comments  extends Controller {
	
	@Check("agent")
	public static void view(String entityName, Long entityId)
	{
		String query = DataAccess.findQuery("Comment","byEntityNameAndTargetEntityId")+" order by postTime";
		List<Comment> comments = Comment.find(query, entityName, entityId).fetch();
		String content=StringUtils.defaultString(flash.get("content"));
		render(entityName, entityId, comments, content);
	}
	
	@Check("agent")
	public static void add(String entityName, Long entityId)
	{
		Comment comment = new Comment();
		comment.postTime=new Date();
		comment.entityName=entityName;
		comment.targetEntityId=entityId;
		comment.poster=User.find("byLogin", Security.connected()).first();
		comment.content=request.params.get("content");
		validation.valid(comment);
		if (validation.hasErrors())
		{
			flash.error(Messages.get("please.correct.validation.errors"));
			flash.put("content", StringUtils.defaultString(request.params.get("content")));
			flash.keep();
		}
		else
		{
			comment.save();
		}
		view(entityName, entityId);
		
	}
}
