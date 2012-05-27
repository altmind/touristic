package models;

import java.util.Date;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
@DiscriminatorValue(value="transfer")
public class TransferService extends Service{
	@Required
	@ManyToOne
	public City startCity;
	
	@MaxSize(255)
	public String startPosition;
	
	@MaxSize(255)
	public String endPosition;
	
	@Required
	public Date startTime;
}
