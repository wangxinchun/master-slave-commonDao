package com.job.db.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.job.db.dataservice.exception.BeanException;

/**
 * 封装javabean的Read and Write api
 */
public class BeanUtil {

    private final static Logger log = Logger.getLogger(BeanUtil.class);

    private Map<MethodKey, BeanProperty> map;

    private static ConcurrentHashMap<Class<?>, Map<MethodKey, BeanProperty>> propMapCache = new ConcurrentHashMap<Class<?>, Map<MethodKey,BeanProperty>>();
    private static Map<Class<?>, Object> primitiveDefaultValue = new HashMap<Class<?>, Object>();
    
    static {
    	primitiveDefaultValue.put(boolean.class, false);
    	primitiveDefaultValue.put(byte.class, (byte)0);
    	primitiveDefaultValue.put(char.class, (char)0);
    	primitiveDefaultValue.put(short.class, (short)0);
    	primitiveDefaultValue.put(int.class, (int)0);
    	primitiveDefaultValue.put(long.class, (long)0);
    	primitiveDefaultValue.put(float.class, (float)0);
    	primitiveDefaultValue.put(double.class, (double)0);
    }
    
    private Class<?> clz;
    
    public BeanUtil(Class<?> clz) {
    	this.clz = clz;
    	
    	map = propMapCache.get(clz);
    	
    	if (map != null) {
    		return;
    	}
    	
    	map = new HashMap<MethodKey, BeanProperty>();
    	
        Method[] methods = clz.getMethods();
        for (Method method : methods) {
        	String name = method.getName();
        	if (name.startsWith("set") && name.length() > 3 && method.getParameterTypes().length == 1 && !method.getParameterTypes()[0].isEnum()) {
        		name = java.beans.Introspector.decapitalize(name.substring(3));
        	
        		MethodKey key = new MethodKey(name, method.getParameterTypes()[0]);
        		BeanProperty prop = map.get(key);
        		if (prop == null) {
        			prop = new BeanProperty();
        			map.put(key, prop);
        		}
        		
        		map.put(new MethodKey(name, null), prop);
        		prop.setWriteMethod(method);
        	}
        }

        for (Method method : methods) {
        	String name = method.getName();
        	if (name.startsWith("get") && name.length() > 3 && method.getParameterTypes().length == 0) {
        		name = java.beans.Introspector.decapitalize(name.substring(3));
        	
        		MethodKey key = new MethodKey(name, method.getReturnType());
        		
        		BeanProperty prop = map.get(key);
        		if (prop != null) {
        			map.put(new MethodKey(name, null), prop); // 与get返回类型相同的优先尝试
        		}
        	}
        }

        for (Method method : methods) {
        	String name = method.getName();
        	if (name.startsWith("is") && name.length() > 2 && method.getParameterTypes().length == 0) {
        		name = java.beans.Introspector.decapitalize(name.substring(2));
        		
        		MethodKey key = new MethodKey(name, method.getReturnType());
        		
        		BeanProperty prop = map.get(key);
        		if (prop != null) {
        			map.put(new MethodKey(name, null), prop); // 与is返回类型相同的优先尝试
        		}
        	}
        }
        
        propMapCache.put(clz, map);
    }

    public void setValue(Object o, String property, Object value) {
    	
    	Class<?> clz = null;
    	if (value != null) {
    		clz = value.getClass();
    	}
    	
        BeanProperty prop = map.get(new MethodKey(property, clz));
        if (prop == null) {
        	if (value != null) { // 自我修复子类
        		prop = fixMissType(o, property, value);
        	}
        
        	if (prop == null) {
        		log.error("miss property: class=" + o.getClass() + ", property=" + property + ", clz=" + clz + ", value=" + value, new Exception());
        		return;
        	}
        }

        if (value == null && prop.getWriteParamentType().isPrimitive()) {
        	value = primitiveDefaultValue.get(prop.getWriteParamentType());
        }
        
        Method writeMethod = prop.getWriteMethod();
        if (writeMethod != null) {
            try {
                writeMethod.invoke(o, new Object[]{value});
            } catch (Exception e) {
            	log.error("set property error! "+writeMethod+",value:" + value+",value type:"+ (null == value ? "" : value.getClass()),e);
                throw new BeanException("set property error! "+writeMethod+",value:" + value+",value type:"+(null == value ? "" : value.getClass()), e);
            } 
        } else {
        	log.error("bean " + o + "has not write method for property " + property, new Exception());
            throw new BeanException("bean " + o + "has not write method for property " + property);
        }
    }

	private BeanProperty fixMissType(Object o, String property, Object value) {
		
		Map<MethodKey, BeanProperty> map = this.map;
		for (MethodKey key : map.keySet()) {
			if (!property.equals(key.name)) {
				continue;
			}
			
			BeanProperty prop = map.get(key);
			
			if (prop.getWriteParamentType().isAssignableFrom(value.getClass())) {
				Map<MethodKey, BeanProperty> tmp = new HashMap<MethodKey, BeanProperty>(map);
				tmp.put(new MethodKey(property, value.getClass()), prop);
				propMapCache.put(clz, tmp);
				this.map = tmp;
				log.warn("fix miss type: clz=" + clz + ", property=" + property + ", value=" + value + ", valueClz=" + value.getClass());
				return prop;
			}
		}

		BeanProperty prop = map.get(new MethodKey(property, null));
		if (prop != null) {
			Map<MethodKey, BeanProperty> tmp = new HashMap<MethodKey, BeanProperty>(map);
			tmp.put(new MethodKey(property, value.getClass()), prop);
			propMapCache.put(clz, tmp);
			this.map = tmp;
			log.warn("fix miss type2: clz=" + clz + ", property=" + property + ", value=" + value + ", valueClz=" + value.getClass());
			return prop;
		}
		
		return null;
	}

	public static String capitalize(String name) {
		if (name.length() > 1) {
			return name.substring(0, 1).toUpperCase() + name.substring(1);
		}
		return name.toUpperCase();
	}

	public void setRequestValue(Object o, String property, Object value) {

		Class<?> clz = null;
		if (value != null) {
			clz = value.getClass();
		}
		
		BeanProperty prop = map.get(new MethodKey(property,clz));
        if (prop == null) {
        	prop = map.get(new MethodKey(property,null));
        	if (prop == null) {
        		return;
        	}
        }
        if(long.class.equals(prop.getWriteParamentType()) && value != null) {
            value = Long.parseLong((String)value);
        }
        if(Integer.class.equals(prop.getWriteParamentType()) && value != null && value instanceof String) {
            try {
                if(!StringUtils.isEmpty((String)value) && !((String)value).equals("null"))  value = Integer.parseInt((String)value);
                else {
                    value = 0;
                }
            } catch (NumberFormatException e) {
                value = 0;
            }
        }
		if (boolean.class.equals(prop.getWriteParamentType())) {
            if(value == null) {
                value = false;
            }else {
                String num = (String) value;
                value = num.equals("1");
            }

		}
        Method writeMethod = prop.getWriteMethod();
        if (writeMethod != null) {
            try {
                writeMethod.invoke(o, new Object[]{value});
            } catch (IllegalAccessException e) {
                throw new BeanException("set property value " + value + "error", e);
            } catch (InvocationTargetException e) {
                throw new BeanException("set property value " + value + "error", e);
            }
        } else {
            throw new BeanException("bean " + o + "has not write method for property " + property);
        }
    }

    public Set<String> getPropertyNames() {
        Set<String> names = new HashSet<String>();
        for (MethodKey key : map.keySet()) {
        	names.add(key.name);
        }
        return names;
    }
    
    private static class MethodKey {
    	private Class<?> clz;
    	private String name;
    	
		public MethodKey(String name, Class<?> clz) {
			super();
			this.name = name;
			this.clz = clz;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((clz == null) ? 0 : clz.hashCode());
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			MethodKey other = (MethodKey) obj;
		
			if (clz == null) {
				if (other.clz != null)
					return false;
			} else if (!clz.equals(other.clz))
				return false;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		}
    	
    }

}
