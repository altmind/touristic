package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Comment extends Model{


	@Required
	public String entityName;
	
	@Required
	public Long targetEntityId;
	
	@ManyToOne
	@Required
	public User poster;
	
	@Required
	public Date postTime;
	
	@MaxSize(250)
	public String content;
}
