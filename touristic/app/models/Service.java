package models;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class Service extends Model{
	
	@Required
	public Date startDate;
	
	public BigDecimal price;
	
	public String enduserComment;
}
