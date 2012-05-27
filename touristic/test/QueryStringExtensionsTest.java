import org.junit.*;

import extensions.QueryStringExtensions;

import java.io.ByteArrayInputStream;
import java.util.*;

import play.mvc.Http.Cookie;
import play.mvc.Http.Header;
import play.mvc.Http.Request;
import play.test.*;
import models.*;

public class QueryStringExtensionsTest extends UnitTest {

    @Test
    public void queryStringSwitch() {
    	Request req = Request.createRequest("", "GET", "", "?ab=asc&cd=desc&ef", "text/plain", new ByteArrayInputStream(new byte[]{}), "some/get?ab=asc&cd=desc&ef", "", false, 80, "", false, Collections.<String,Header>emptyMap(), Collections.<String,Cookie>emptyMap());
        assertEquals("some/get?ab=asc&cd=asc&ef",QueryStringExtensions.queryStringSwitch(req, "cd", "asc","desc"));
        assertEquals("some/get?ab=asc&cd=desc&ef=asc",QueryStringExtensions.queryStringSwitch(req, "ef", "asc","desc"));
        assertEquals("some/get?ab=desc&cd=desc&ef",QueryStringExtensions.queryStringSwitch(req, "ab", "asc","desc"));
    }
    
    @Test
    public void queryStringIncluding() {
    	Request req = Request.createRequest("", "GET", "", "?ab=asc&cd=desc&ef", "text/plain", new ByteArrayInputStream(new byte[]{}), "some/get?ab=asc&cd=desc&ef", "", false, 80, "", false, Collections.<String,Header>emptyMap(), Collections.<String,Cookie>emptyMap());
        assertEquals("some/get?ab=asc&cd=goo&ef",QueryStringExtensions.queryStringIncluding(req, "cd", "goo"));
        assertEquals("some/get?ab=asc&cd=desc&ef&gh=goo",QueryStringExtensions.queryStringIncluding(req, "gh", "goo"));
        assertEquals("some/get?ab=asc&cd=desc&ef=foo",QueryStringExtensions.queryStringIncluding(req, "ef", "foo"));
    }
    
    @Test
    public void queryStringExcluding() {
    	Request req = Request.createRequest("", "GET", "", "?ab=asc&cd=desc&ef", "text/plain", new ByteArrayInputStream(new byte[]{}), "some/get?ab=asc&cd=desc&ef", "", false, 80, "", false, Collections.<String,Header>emptyMap(), Collections.<String,Cookie>emptyMap());
        assertEquals("some/get?ab=asc&ef",QueryStringExtensions.queryStringExcluding(req, "^cd$"));
        assertEquals("some/get?ab=asc&cd=desc&ef",QueryStringExtensions.queryStringExcluding(req, "gh", "goo"));
        assertEquals("some/get?ab=asc&cd=desc",QueryStringExtensions.queryStringExcluding(req, "ef", "foo"));
        assertEquals("some/get",QueryStringExtensions.queryStringExcluding(req, "(ab|cd|ef)"));
    }


}
