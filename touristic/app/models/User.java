package models;

import java.util.Arrays;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import models.UserRole.UserRoleType;

import org.apache.commons.collections.CollectionUtils;

import play.data.validation.Email;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class User extends Model {

	@Transient
	public User reset()
	{
		this.active=true;
		return this;
	}
	
	@Required
	public Boolean active;
	
	@Required
	@Email
	public String email;
	
	@Required
	public String login;
	
	@MinSize(5)
	public String password;
	
/*	@OneToMany(mappedBy="user")
	public Set<UserRole> roles;*/
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((active == null) ? 0 : active.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((login == null) ? 0 : login.hashCode());
		result = prime * result
				+ ((password == null) ? 0 : password.hashCode());
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
		User other = (User) obj;
		if (active == null) {
			if (other.active != null)
				return false;
		} else if (!active.equals(other.active))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (login == null) {
			if (other.login != null)
				return false;
		} else if (!login.equals(other.login))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		return true;
	}

	@Transient
	public boolean hasRole(String role)
	{
		if(!Arrays.asList(UserRoleType.entries).contains(role))
		{
			throw new RuntimeException("Specified role does not exist");
		}
/*		if (roles!=null){
			for (UserRole r : roles) {
				if (role.equals(r.role)) {
					return true;
				}
			}
		}
		return false;*/
		UserRole roleEntity = UserRole.find("byUserAndRole", this, role).first();
		return roleEntity!=null;
		
	}

}
