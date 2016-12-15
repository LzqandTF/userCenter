package com.yijiawang.web.platform.userCenter.util;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.Map;


/**
 * json工具类
 * @author liuyang
 *
 */
public class JsonUtils {

	
	private static ObjectMapper objectMapper;
	
	static{
	 	
	   objectMapper= new ObjectMapper();
	   objectMapper.setSerializationConfig(objectMapper.getSerializationConfig().withSerializationInclusion( 
               JsonSerialize.Inclusion.NON_NULL)); 
	}
	
	/**
	 * transfer josn String to Map
	 * @param jsonStr
	 * @return Map
	 * @throws Exception 
	 */
	@SuppressWarnings("rawtypes")
	public static Map convertToMap(String jsonStr){
		try{
		
			Map result = objectMapper.readValue(jsonStr,Map.class);
		
		    return result;
	
		}catch(Exception e){
			
			throw new RuntimeException("转换失败");
		}
	}
	
	/**
	 * transfer josn String to Map
	 * @return Map
	 * @throws Exception 
	 */
	@SuppressWarnings("rawtypes")
	public static Map convertToMap(Object obj){
		try{
		
			String jsonStr = convertToStr(obj);		
		    return convertToMap(jsonStr);
	
		}catch(Exception e){
			
			throw new RuntimeException("转换失败");
		}
	}
	
	/**
	 * transfer josnString to Map
	 * @param jsonStr
	 * @return Object
	 * @throws Exception 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Object convertToObject(String jsonStr,Class className){
		try{
		     return objectMapper.readValue(jsonStr, className);
	
		}catch(Exception e){
			
			throw new RuntimeException("转换失败");
		}
		
	}
	
	/**
	 * transfer josnString to Map
	 * @param jsonStr
	 * @return String
	 * @throws Exception 
	 */
	public static String convertToStr(Object obj) {
		
		if(obj==null) return null;
		try {
			return objectMapper.writeValueAsString(obj);
		} catch (Exception e) {
			 
			return obj.toString();
		} 
	}
}
