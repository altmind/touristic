package tags;

import groovy.lang.Closure;

import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import play.data.binding.Binder;
import play.data.validation.Validation;
import play.mvc.Http.Request;
import play.mvc.Scope.Flash;
import play.templates.FastTags;
import play.templates.JavaExtensions;
import play.templates.FastTags.Namespace;
import play.templates.GroovyTemplate.ExecutableTemplate;
@FastTags.Namespace("c")
public class CustomTags extends FastTags {

	@Deprecated
	public static void _persistFields(Map<?, ?> args, Closure body, PrintWriter out, ExecutableTemplate template, int fromLine)
	{
		
		String includes = StringUtils.defaultString((String) args.get("includes"));
		String excludes = StringUtils.defaultString((String) args.get("excludes"));
		
		persistFieldsInternal(out, includes, excludes, Request.current().get().params.all().entrySet());
		
	}
	@Deprecated
	public static void persistFieldsInternal(PrintWriter out, String includes,
			String excludes, Set<Entry<String, String[]>> entrySet) {
		
		for( Entry<String, String[]> e : entrySet)
		{
			String name=e.getKey();
			if (name.equals("body"))
				continue;
			if (e.getValue().length>1)
			{
				name+="[]";
			}
			for(String v : e.getValue())
			{
				String s="<input type=\"hidden\" name=\""+name+"\" value=\""+v+"\"/> ";
				out.println(s);
			}			
		}
		
	}
	/**
	 * Copy of parent's FastTags _field method for extensibility.
	 * @param args
	 * @param body
	 * @param out
	 * @param template
	 * @param fromLine
	 */
	public static void _field(Map<?, ?> args, Closure body, PrintWriter out, ExecutableTemplate template, int fromLine) {
        Map<String,Object> field = new HashMap<String,Object>();
        String _arg = args.get("arg").toString();
        field.put("name", _arg);
        field.put("id", _arg.replace('.','_'));
        field.put("flash", Flash.current().get(_arg));
        field.put("flashArray", field.get("flash") != null && !StringUtils.isEmpty(field.get("flash").toString()) ? field.get("flash").toString().split(",") : new String[0]);
        field.put("error", Validation.error(_arg));
        field.put("errorClass", field.get("error") != null ? "hasError" : "");       
        getProperty(body, field, _arg);
        if (!field.containsKey("value"))
        {
        	Object val = args.get("value");
        	if (val!=null)
        	{
        		field.put("value", val);
        	}
        }
        body.setProperty("field", field);
        body.call();
    }
	private static void getProperty(Closure body, Map<String, Object> field,
			String name) {
		
        String[] pieces = name.split("\\.");
		Object obj = body.getProperty(pieces[0]);
		StringBuffer newName=new StringBuffer();
		for(int i=1; i<pieces.length; i++)
		{
			if (newName.length()>0)
				newName.append(".");
			newName.append(pieces[i]);
		}
		Object v = Binder.bind(obj, newName.toString(), Collections.<String,String[]>emptyMap());
        if(obj != null){
            if(pieces.length > 1){
                for(int i = 1; i < pieces.length; i++){
                    try{
                        Field f = obj.getClass().getField(pieces[i]);
                        if(i == (pieces.length-1)){
                            try{
                                Method getter = obj.getClass().getMethod("get"+JavaExtensions.capFirst(f.getName()));
                                field.put("value", getter.invoke(obj, new Object[0]));
                            }catch(NoSuchMethodException e){
                                field.put("value",f.get(obj).toString());
                            }
                        }else{
                            obj = f.get(obj);
                        }
                    }catch(Exception e){
                        // if there is a problem reading the field we dont set any value
                    }
                }
            }else{
                field.put("value", obj);
            }
        }
	}
	
}
