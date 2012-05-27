package controllers;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import models.User;
import models.UserRole.UserRoleType;

import play.Logger;
import play.db.jpa.GenericModel.JPAQuery;
import play.libs.Codec;

public class Security extends Secure.Security {

	static boolean authentify(String username, String password) {
		try {
			User user = User.find("byLoginAndPasswordAndActive", username,
					Codec.hexMD5(password),true).first();
			return user != null;
		} catch (Exception exc) {
			Logger.error(exc, "Exception while authenticating");
			return false;
		}
	}

	static boolean check(String roleType) {

		User user = User.find("byLogin", connected()).first();
		if (user == null)
			throw new RuntimeException("Cannot get roles for user");
		else {
			return user.hasRole(roleType);
		}
	}
}
