package models;

import javax.persistence.Entity;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Country extends Model {
	
	public Country reset()
	{
		this.active=true;
		return this;
	}
	@Required
	public Boolean active;
	
	@Required
	public String name;

	@Override
	public String toString() {
		return name;
	}
	
}
