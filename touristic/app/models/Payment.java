package models;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Payment extends Model {

	@Required
	public BigDecimal amount;
	
	@Required
	public Date createdOn;
	
	public enum PaymentType {
		CASHTOAGENT, WIRETRANSFER, CREDITCARD
	}
	
	@Required
	public PaymentType paymentType;
	
	@Required
	@ManyToOne
	public Request request;
	
	@Required
	public Boolean active;
	
	public Payment reset()
	{
		this.active=true;
		return this;
	}
}
