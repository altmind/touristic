package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Roomtype extends Model{

	public Roomtype reset()
	{
		this.active=true;
		return this;
	}
	
	
	@Override
	public String toString() {
		return name;
	}


	@Required
	public Boolean active;
	
	@Required
	@MaxSize(250)
	public String name;
	
	@Required
	public Integer adults;
	
	@Required
	public Integer children;
	
	@Required
	public Integer adultsExtrabeds;
	
	@Required
	public Integer childrenExtrabeds;
	
	public double sortOrder=1.0d;
}
