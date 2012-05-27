package models;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.hibernate.annotations.CascadeType;


import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Request extends Model{
	@Required
	@MinSize(1)
	@MaxSize(32)
	public String requestNumber;
	
	@Required
	public Date issueDate;
	
	@Required
	public Date serviceStartDate;
	
	@Required
	@ManyToOne
	public City city;
	
	@Required
	@ManyToOne
	public Client tourlead;
	
	@ElementCollection
	@OneToMany
	public List<Service> services;
	
	@OneToMany(cascade=javax.persistence.CascadeType.ALL)
	public List<Tourist> tourists;
	
	@Required
	@ManyToOne
	public User agent;
	
	@Required
	public boolean active;
	
	public BigDecimal totalPrice()
	{
		BigDecimal total = new BigDecimal(0);
		if (services==null)
			return total;
		for (Service svc : services) {
			total=total.add(svc.price);
		}
		return total;
	}
	public BigDecimal totalPaid()
	{
		if (this.id==null)
			return null;
		Collection<Payment> payments = Payment.find("byActiveAndRequest", true, this).fetch();
		BigDecimal total = new BigDecimal(0);
		for (Payment svc : payments) {
			total=total.add(svc.amount);
		}
		return total;
	}
	public Request reset() {
		return this;
	}
}
