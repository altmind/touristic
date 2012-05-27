package models;

import java.util.Currency;

import javax.persistence.Entity;

import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Config extends Model{
	
	@MinSize(2)
	@MaxSize(4)
	@Required
	public String currency;
	

	public static Config getConfig()
	{
		return Config.find("from Config as config ORDER BY config.id desc ").first();
	}
}
