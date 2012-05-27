package models;

import java.util.Date;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.hibernate.mapping.Bag;

import play.data.validation.Email;
import play.data.validation.Match;
import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Hotel extends Model{

	public Hotel reset()
	{
		this.active=true;
		return this;
	}
	
	@Required
	public Boolean active;
	
	@Required
	@MinSize(2)
	@MaxSize(250)
	public String name; 

	@ManyToOne
	@Required
	public City city;
	
	@ManyToOne
	public User creator;
	
	@ManyToOne
	public HotelCategory category;
	
	@Match(value="^\\+[0-9 \\-]+$", message="Example: +307 11 4242424")
	@MinSize(4)
	@MaxSize(50)
	public String phone;
	
	@Email
	@Required
	@MaxSize(250)
	public String email;
	
	@Match(value="^-?\\d+(?:\\.\\d+)?; ?-?\\d+(?:\\.\\d+)?$", message="Example: 33;-17.07")
	public String geoCoordinates;
	
	@Required
	@ManyToOne
	public Language language;
	
	@OneToMany
	public Set<AdditionalContacts> additionalContacts;
	
	public double sortOrder=1.0d;

	@Override
	public String toString() {
		return name;
	}
	
}
