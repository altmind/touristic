package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import models.*;
import models.UserRole.UserRoleType;

@With(Secure.class)
public class Application extends Controller {
	
	@Check("agent")
    public static void index() {
        render();
    }

}