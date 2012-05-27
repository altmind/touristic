package models;

import java.util.Date;

import javax.persistence.Entity;

import play.data.validation.InPast;
import play.data.validation.IsTrue;
import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.data.validation.Valid;
import play.db.jpa.Model;

@Entity
public class Client extends Model {
	
	@Override
	public String toString() {
		return name;
	}

	public Client reset()
	{
		this.active=true;
		return this;
	}
	@Required
	public Boolean active;
	
	public enum Salutation {
		MR, MRS, CHLD
	}
	
	@Required
	public Salutation salutation;
	
	@MaxSize(250)
	@MinSize(2)
	@Required
	public String name;
	
	@InPast
	public Date dateOfBirth;
	

	@MaxSize(255)
	public String passportData;
	

	@MaxSize(255)
	public String postalData;
	
}
