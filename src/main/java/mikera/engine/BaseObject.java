package mikera.engine;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * The BaseObject class is the root for all game 
 * 
 * It implements a dynamic, mutable list of properties
 * 
 * @author Mike
 * 
 */
public class BaseObject implements Cloneable, Serializable, ObjectProperties {
	// statics
    private static final long serialVersionUID = 6165762084693059838L;
    public static boolean GET_SET_DEBUG = false;
    public static boolean GET_OUTPUT_DEBUG = false;    
    public static boolean SET_OUTPUT_DEBUG = false; 
    public static final boolean OPTIMIZE = true;  
   
    // properties
    private HashMap<String,Object> local;
    private ObjectProperties inherited;

    
    public BaseObject() {
        // no properties for default BaseObject
    }
    
	@SuppressWarnings("unchecked")
	public BaseObject(HashMap<String,Object> propertiesToCopy, BaseObject parent) {
		if (propertiesToCopy!=null) {
			local=(HashMap<String,Object>)propertiesToCopy.clone();
		}
		inherited=parent;
	}

    /**
     * Clone copies a BaseObject instance, maintaining
     * the same inherited properties
     */
    @SuppressWarnings("unchecked")
	public Object clone() {
        BaseObject o;
		try {
			o = (BaseObject) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new Error(e);
		}
        o.inherited=inherited;
        if (local!=null) {
        	o.local=(HashMap<String,Object>)local.clone();
        }
        return o;
    }

    public final boolean equals(Object o) {
    	return this==o;
    }
    
    public BaseObject(BaseObject parent) {
        this.inherited = parent;
    }
    
    public BaseObject(Map<String,Object> data) {
    	for (Iterator<Entry<String, Object>> it=data.entrySet().iterator(); it.hasNext();) {
    		Entry<String, Object> e=it.next();
    		String key=e.getKey();
    		set(key,e.getValue());
    	}
    }
    
	public void replaceWith(BaseObject t) {
	    if (t.local != null) {
            local = new HashMap<String,Object>(t.local);
        } else {
            local = null;
        }
	    inherited=t.inherited;
	}

    public void set(String s, boolean value) {
        set(s, (value ? Integer.valueOf(1) : Integer.valueOf(0)));
    }

    public void set(String s, int value) {
        set(s, Integer.valueOf(value));
    }
    
    public void set(String s, double value) {
        set(s, new Double(value));
    }

    public void setProperties(Map<String,Object> map) {
    	for (Iterator<Entry<String, Object>> it=map.entrySet().iterator(); it.hasNext();) {
    		Entry<String, Object> e=it.next();
    		String key=e.getKey();
    		set(key,e.getValue());
    	}
    }
    

    /**
     * Checks whether the Stuff object contains a given key
     * 
     * @param key
     * @return True if the receiver contains the key, false otherwise
     */
    public boolean containsKey(String key) {
        if (local != null && local.containsKey(key)) return true;

        // tail-recursive call could give a mild performance
        // benefit with a good compiler that can spot it!
        if (inherited != null) return inherited.containsKey(key);
        return false;
    }

    /**
     * Sets a property value
     * 
     * @param key The key value to set
     * @param value The new value
     * @return True if local value set, false otherwise
     */
    public boolean set(String key, Object value) {
        return realSet(key, value);

    }

    private static final boolean checkEquals(Object a, Object b) {
    	if (a==b) return true;
    	if (a==null) {
    		return false;
    	} else {
    		return a.equals(b);
    	}
    }
    
    private boolean realSet(String key, Object value) {
    	Object parentValue = getInherited(key);
        if (checkEquals(value,parentValue)) {
        	return localRemoveKey(key);
    	}
        
        if ((local!=null)&&local.containsKey(key)) {
	       	Object localValue=local.get(key);
	        if (checkEquals(value,localValue)) {
	        	return false;
	        }
        }
        
        return localSet(key, value);
    }
    
    private boolean localSet(String key, Object value) {
   		ensureLocal();
   		local.put(key,value);
   		return true;
    }
    
    private boolean localRemoveKey(String key) {
    	if (local!=null) {
    		local.remove(key);
    	}
    	return true;
    }
    
    public final Object localGet(String key) {
    	if (local==null) return null;
    	return local.get(key);
    }
    
    public final Object getInherited(String key) {
    	if (inherited==null) return null;
    	return inherited.get(key);
    }
    
    public final boolean containsLocalKey(String key) {
    	if (local==null) return false;
    	return local.containsKey(key);
    }

    private final void ensureLocal() {
    	if (local==null) local=new HashMap<String,Object>();
    }
    
    public final String getString(String s) {
        return (String) get(s);
    }

    public int getStatIfAbsent(String stat, int ifAbsent) {
        Integer value = (Integer) get(stat);
        return value == null ? ifAbsent : value.intValue();
    }

    public int getInteger(String s) {
        return getBaseInteger(s);
    }

    /**
     * Gets a base, unmodified property value
     * 
     * This should differ from getStat(key) only if modifiers are present
     * 
     * @param key
     * @return
     */
    private int getBaseInteger(String key) {
    	Integer i=(Integer)getUnmodifiedValue(key);
    	if (i==null) return 0;
        return i.intValue();
    }

    public final boolean getFlag(String key) {
        Integer b = (Integer) get(key);
        return (b == null) ? false : (b.intValue() > 0);
    }
    
    public final double getDouble(String key) {
        Double d = (Double) get(key);
        return (d == null) ? 0.0 : (d.doubleValue());
    }

    public int incInteger(String s, int v) {
        int newValue=getBaseInteger(s) + v;
		set(s, newValue);
		return newValue;
    }
    
    /**
     * This is the critical method in BaseObject that returns a property value
     * from the properties hash.
     * 
     * Note that this is *overridden* by Thing to implement modifiers, hence all
     * property access should go through get(key) or else modifiers will not
     * work correctly.
     * 
     * @param key
     *            The name of the property value to retreive
     * @return Property value
     */

    public Object get(String key) {
    	return getUnmodifiedValue(key);
    }
    	
    private Object getUnmodifiedValue(String key) {
        // check current Stuff if present
        if (local != null) {
            // need to do it this way in case local
            // value is set to null!
            if (local.containsKey(key)) { return local.get(key); }
        }
        // default to base Stuff
        if (inherited != null) return inherited.get(key);
        // nothing found
        return null;
    }

    /**
     * Get a single Map of all property pairs
     */
    public Map<String,Object> getAll() {
        Map<String,Object> map = new HashMap<String,Object>();
        putAll(map);
        return map;
    }
    
    public void putAll(Map<String,Object> map) {	
    	if (inherited!=null) {
    		inherited.putAll(map);
    	}
    	
    	if (local!=null) {
    		local.putAll(map);
    	}
    }

    public ObjectProperties getInherited() {
        return inherited;
    }

    public HashMap<String,Object> getAllLocal() {
        return local;
    }
    
    public Object getLocal(String key) {
    	return localGet(key);
    }
    
    public int getPropertyDepth(String key) {
    	if ((local!=null)&&local.containsKey(key)) return 1;
    	if (inherited==null) return 0;
    	if (!(inherited instanceof BaseObject)) return inherited.containsKey(key)?2:0;
    	return ((BaseObject)inherited).getPropertyDepth(key)+1;
    }

    /**
     * Create Stuff with single top-level hash
     * 
     * Useful if chain of base Stuff gets too long
     */
    public static BaseObject getFlattened(BaseObject source) {
        BaseObject destination = new BaseObject();
        destination.local=new HashMap<String,Object>();
        source.putAll(destination.local);
        return destination;
    }

    public void flattenProperties() {
    	BaseObject flattened = BaseObject.getFlattened(this);
    	this.local = flattened.local;
    	this.inherited = null;
    }
}
