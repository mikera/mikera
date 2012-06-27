package mikera.engine;

import java.util.Map;

public interface ObjectProperties {
	public Object get(String s);
	
	public Map<String,Object> getAll();
	
	public boolean containsKey(String s);
	
    public void putAll(Map<String,Object> map);	
}
