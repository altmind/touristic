package models;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Image extends Model{
	
	public static enum DescriptionType{
		FRONT("image.description.type.front"),
		ROOMS("image.description.type.rooms"),
		LOBBY("image.description.type.lobby"),
		RESTARAUNT("image.description.type.restaraunt"),
		CONFERENCEROOM("image.description.type.conferenceroom"),
		EXTERIOR("image.description.type.exterior");
		
		String token;
		DescriptionType(String token)
		{
			this.token=token;
		}
		@Override
		public String toString() {
			return token;
		}
	}
	
	@Required
	@ManyToOne
	public Hotel hotel;
	
	@Required
	@Enumerated(EnumType.STRING)
	public DescriptionType descriptionType;
	
	@Required
	public String imageUrl;

}
