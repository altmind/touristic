package models;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class AdditionalContacts extends Model{

	public static enum ContactType{
		EMAIL("email"),
		PHONE("phone");
		
		String token;
		ContactType(String token)
		{
			this.token=token;
		}
		@Override
		public String toString() {
			return token;
		}
	}
	

	@MaxSize(250)
	public String content;
	
	@Required
	public ContactType contactType;
	
	@Required
	public String targetEntityName;
	
	@Required
	public Long targetEntityId;
}
