package models;

import java.util.Date;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
@DiscriminatorValue(value="hotel")
public class HotelService extends Service{
	
	@Required
	@ManyToOne
	public Hotel hotel;
	
	@Required
	@ManyToOne
	public Mealtype mealtype;
	
	@Required
	@ManyToOne
	public Roomtype roomtype;
	
	@Required
	public Date endDate;

}
