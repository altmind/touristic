package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class UserRole extends Model {

	@ManyToOne()
	@Required
	public User user;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((role == null) ? 0 : role.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserRole other = (UserRole) obj;
		if (role == null) {
			if (other.role != null)
				return false;
		} else if (!role.equals(other.role))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}

	@Required
	public String role;
	
	/*public enum UserRoleType{
		agent, manager, admin
	}*/
	public static class UserRoleType{
		public static final String AGENT="agent";
		public static final String MANAGER="manager";
		public static final String ADMIN="admin";
		public static final String[] entries = new String[]{AGENT,MANAGER,ADMIN};
	}
	
	public String toString()
	{
		return role;
	}	

}
