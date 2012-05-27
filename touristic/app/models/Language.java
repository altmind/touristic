package models;

import javax.persistence.Entity;

import play.data.validation.Match;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Language  extends Model {

	public Language reset()
	{
		this.active=true;
		return this;
	}
	
	@Override
	public String toString(){
		return name;
	}
	
	@Required
	public Boolean active;
	
	@Required
	@Match(value="^[A-Z]{2,3}$", message="Example: AZ")
	public String isoCode;
	
	@Required
	@MaxSize(250)
	public String name;
}
