package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class City extends Model {
	
	@Override
	public String toString() {
		return name+"["+country.name+"]";
	}

	public City reset()
	{
		this.active=true;
		return this;
	}
	@Required
	public Boolean active;
	
	@Required
	public String name;
	
	@ManyToOne
	@Required
	public Country country;
}
