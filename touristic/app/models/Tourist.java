package models;

import java.util.Date;

import javax.persistence.Entity;

import play.data.validation.InPast;
import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.db.jpa.Model;
import play.mvc.Before;
import models.Client.Salutation;

@Entity
public class Tourist extends Model {
	@MinSize(1)
	@MaxSize(100)
	@Required
	public String name;
	
	@Required
	public Salutation salutation;
	
	@InPast
	public Date dateOfBirth;

	@Override
	public String toString() {
		return "Tourist [name=" + name + ", salutation=" + salutation + "]";
	}
	
}
