package models;

import javax.persistence.Entity;

import play.data.validation.Match;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class HotelCategory extends Model {
	
	public HotelCategory reset()
	{
		this.active=true;
		return this;
	}
	@Required
	public Boolean active;
	
	@Required
	@Match(value="^[A-Z]{1,50}$", message="Example: APT")
	public String code;
	
	@Required
	@MaxSize(250)
	public String name;
	
	@Required
	public Integer starRating;

	@Override
	public String toString() {
		return name;
	}
	
}
