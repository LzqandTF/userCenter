/**
 * 
 */
package com.yijiawang.web.platform.userCenter.util;

import java.util.ResourceBundle;

/**
 * 应用参数工具类，获得config配置文件中定义的参数
 *
 */
public class AppConfig {

	private static final String BUNDLE_NAME = "config/config";
//	//CRM配置文件
//	private static final String BUNDLECRM_NAME = "config/crmInterface";
//	//支付配置文件名
//	private static final String PAY_PARAM = "config/payParam";
//	//CDN配置文件名
//	private static final String APPCONF_PARAM = "props/appconf";
	
	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);
//	//CRM
//	private static final ResourceBundle RESOURCE_BUNDLE_CRM = ResourceBundle.getBundle(BUNDLECRM_NAME);
//	//支付配置
//	private static final ResourceBundle RESOURCE_BUNDLE_PAY = ResourceBundle.getBundle(PAY_PARAM);
//	////CDN配置文件名
//	private static final ResourceBundle RESOURCE_BUNDLE_APP = ResourceBundle.getBundle(APPCONF_PARAM);

	
	private AppConfig() {
	}
	
	public static String getString(String key) {
		try {
			String messager = RESOURCE_BUNDLE.getString(key).trim();
			return messager;
		} catch (Exception e) {
			String messager = "不能在配置文件" + BUNDLE_NAME + "中发现参数：" + '!' + key
					+ '!';
			throw new RuntimeException(messager);
		}
		
	}
//	/**
//	 * 查询CRM配置文件
//	 * @param key
//	 * @return
//	 */
//	public static String getCRMInterString(String key) {
//		try {
//			String messager = RESOURCE_BUNDLE_CRM.getString(key).trim();
//			return messager;
//		} catch (Exception e) {
//			String messager = "不能在配置文件" + RESOURCE_BUNDLE_CRM + "中发现参数：" + '!' + key
//					+ '!';
//			throw new RuntimeException(messager);
//		}
//	}
//	/**
//	 * 查询支付配置文件
//	 * @param key
//	 * @return
//	 */
//	public static String getPayString(String key) {
//		try {
//			String messager = RESOURCE_BUNDLE_PAY.getString(key).trim();
//			return messager;
//		} catch (Exception e) {
//			String messager = "不能在配置文件" + RESOURCE_BUNDLE_PAY + "中发现参数：" + '!' + key + '!';
//			throw new RuntimeException(messager);
//		}
//	}
//	/**
//	 * 查询cdn的bucketId （用以取cdn的访问根目录）
//	 * @param key
//	 * @return
//	 */
//	public static String getAppconfString(String key) {
//		try {
//			String messager = RESOURCE_BUNDLE_APP.getString(key).trim();
//			return messager;
//		} catch (Exception e) {
//			String messager = "不能在配置文件" + RESOURCE_BUNDLE_APP + "中发现参数：" + '!' + key + '!';
//			throw new RuntimeException(messager);
//		}
//	}
}
