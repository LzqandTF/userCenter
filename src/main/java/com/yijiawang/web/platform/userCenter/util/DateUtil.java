package com.yijiawang.web.platform.userCenter.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public final class DateUtil {

	/**
	 * 英文简写（默认）如：2010-12
	 */
	public static String FORMAT_SHORTER = "yyyy-MM";
	/**
	 * 英文简写（默认）如：2010-12-01
	 */
	public static String FORMAT_SHORT = "yyyy-MM-dd";

	/**
	 * 英文简写（默认）如：20101201
	 */
	public static String FORMAT_SHORT_EXT = "yyyyMMdd";
	/**
	 * 英文全称如：2010-12-01 23:15:06
	 */
	public static String FORMAT_LONG = "yyyy-MM-dd HH:mm:ss";

	/**
	 * 英文全称如：2010-12-01 23:15
	 */
	public static String FORMAT_WITHOUT_SECOND = "yyyy-MM-dd HH:mm";

	/**
	 * 英文全称如：20101201231506
	 */
	public static String FORMAT_LONG_UNMARK = "yyyyMMddHHmmss";

	/**
	 * 精确到毫秒的完整时间 如：yyyy-MM-dd HH:mm:ss.S
	 */
	public static String FORMAT_FULL = "yyyy-MM-dd HH:mm:ss.S";

	// 格式 2015/07/07 12:55:59
	public static String FORMAT_FULL_NEW = "yyyy/MM/dd";

	/**
	 * 中文简写如：2010年12月01日
	 */
	public static String FORMAT_SHORT_CN = "yyyy年MM月dd";

	/**
	 * 中文全称如：2010年12月01日23时15分06秒
	 */

	public static String FORMAT_LONG_CN = "yyyy年MM月dd日HH时mm分ss秒";

	/**
	 * 精确到毫秒的完整中文时间
	 */
	public static String FORMAT_FULL_CN = "yyyy年MM月dd日HH时mm分ss秒SSS毫秒";

	private DateUtil(){
		throw new Error("不能实例化");
	}
	/**
	 * 获得默认的 date pattern
	 */
	public static String getDatePattern() {
		return FORMAT_LONG;
	}

	/**
	 * 根据预设格式返回当前日期
	 * 
	 * @return
	 */
	public static String getNow() {
		return format(new Date());
	}

	/**
	 * 获取yyyyMMdd格式的日期
	 * 
	 * @return
	 */
	public static String getShortTime() {
		return format(new Date(), FORMAT_SHORT_EXT);
	}

	/**
	 * 根据用户格式返回当前日期
	 * 
	 * @param format
	 * @return
	 */
	public static String getNow(String format) {
		return format(new Date(), format);
	}

	/**
	 * 使用预设格式格式化日期
	 * 
	 * @param date
	 * @return
	 */
	public static String format(Date date) {
		return format(date, getDatePattern());
	}

	/**
	 * 将 带格式的日期时间字符串dt转换为不带格式的日期时间字符串
	 * 
	 * @param dt
	 *            输入格式为：yyyy-MM-dd HH:mm:ss
	 * @return 返回格式为：yyyyMMddHHmmss
	 */
	public static String formatDateStrToShortDateStr(String dt) {
		try {
			return new SimpleDateFormat(FORMAT_LONG_UNMARK).format(new SimpleDateFormat(FORMAT_LONG).parse(dt));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 使用用户格式格式化日期
	 * 
	 * @param date
	 *            日期
	 * @param pattern
	 *            日期格式
	 * @return
	 */
	public static String format(Date date, String pattern) {
		String returnValue = "";
		if (date != null) {
			SimpleDateFormat df = new SimpleDateFormat(pattern);
			returnValue = df.format(date);
		}
		return returnValue;
	}

	/**
	 * 使用预设格式提取字符串日期
	 * 
	 * @param strDate
	 *            日期字符串
	 * @return
	 */
	public static Date parse(String strDate) {
		return parse(strDate, FORMAT_LONG_UNMARK);
	}

	/**
	 * 使用用户格式提取字符串日期
	 * 
	 * @param strDate
	 *            日期字符串
	 * @param pattern
	 *            日期格式
	 * @return
	 */
	public static Date parse(String strDate, String pattern) {
		SimpleDateFormat df = new SimpleDateFormat(pattern);
		try {
			return df.parse(strDate);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 使用用户格式提取字符串日期
	 * 
	 * @param strDate
	 *            日期字符串
	 * @param pattern
	 *            日期格式
	 * @return
	 */
	public static Date parse(Date strDate, String pattern) {
		SimpleDateFormat df = new SimpleDateFormat(pattern);
		try {
			String tempDate = df.format(strDate);
			return df.parse(tempDate);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 在日期上增加数个整月
	 * 
	 * @param date
	 *            日期
	 * @param n
	 *            要增加的月数
	 * @return
	 */
	public static Date addMonth(Date date, int n) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MONTH, n);
		return cal.getTime();
	}

	/**
	 * 在日期上增加天数
	 * 
	 * @param date
	 *            日期
	 * @param n
	 *            要增加的天数
	 * @return
	 */
	public static Date addDay(Date date, int n) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, n);
		return cal.getTime();
	}
	
	
	
	/**
	 * 在日期上增加分钟
	 * 
	 * @param date
	 *            分
	 * @param n
	 *            要增加的天数
	 * @return
	 */
	public static Date addMinute(Date date, int n) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MINUTE, n);
		return cal.getTime();
	}

	/**
	 * 当前时间向前推移n天
	 * @param date
	 * @param n
	 * @return
	 */
	public static Date subDay(Date date, int n) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, -n);
		return cal.getTime();
	}
	
	/**
	 * 在日期上增加秒数
	 * 
	 * @param date
	 *            日期
	 * @param n
	 *            要增加的秒数
	 * @return
	 */
	public static Date addSecond(Date date, int n) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.SECOND, n);
		return cal.getTime();
	}

	/**
	 * 获取时间戳(年-月-日 时:分:秒.毫秒) 例如:2014-08-21 11:33:01.622
	 */
	public static String getTimeString() {
		SimpleDateFormat df = new SimpleDateFormat(FORMAT_FULL);
		Calendar calendar = Calendar.getInstance();
		return df.format(calendar.getTime());
	}

	/**
	 * 获取日期年份
	 * 
	 * @param date
	 *            日期
	 * @return
	 */
	public static String getYear(Date date) {
		return format(date).substring(0, 4);
	}

	/**
	 * 计算两日期相差月份数
	 * 
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static int countMonths(Date d1, Date d2) {
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		c1.setTime(d1);
		c2.setTime(d2);
		return Math.abs(c2.get(Calendar.MONDAY) - c1.get(Calendar.MONTH));
	}

	/**
	 * 按默认格式的字符串距离今天的天数
	 * 
	 * @param date
	 *            日期字符串
	 * @return
	 */
	public static int countDays(String date) {
		long t = Calendar.getInstance().getTime().getTime();
		Calendar c = Calendar.getInstance();
		c.setTime(parse(date));
		long t1 = c.getTime().getTime();
		return (int) (t / 1000 - t1 / 1000) / 3600 / 24;
	}

	/**
	 * 按用户格式字符串距离今天的天数
	 * 
	 * @param date
	 *            日期字符串
	 * @param format
	 *            日期格式
	 * @return
	 */
	public static int countDays(String date, String format) {
		long t = Calendar.getInstance().getTime().getTime();
		Calendar c = Calendar.getInstance();
		c.setTime(parse(date, format));
		long t1 = c.getTime().getTime();
		return (int) (t / 1000 - t1 / 1000) / 3600 / 24;
	}

	/**
	 * 按用户格式字符串距离今天的天数
	 * 
	 * @param date
	 *            日期
	 * @return
	 */
	public static int countDays(Date date) {
		long t = Calendar.getInstance().getTime().getTime();
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		long t1 = c.getTime().getTime();
		return (int) (t / 1000 - t1 / 1000) / 3600 / 24;
	}
	

	/**
	 * 取得当前月最大天数
	 * 
	 * @return
	 */
	public static int getMonthMaxDay() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 取得当前月最小天数
	 * 
	 * @return
	 */
	public static int getMonthMinDay() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 取得每月最大天数
	 * 
	 * @param date
	 * @return
	 */
	public static int getMonthMaxDay(String date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(parse(date));
		return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 取得每月最小天数
	 * 
	 * @param date
	 * @return
	 */
	public static int getMonthMinDay(String date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(parse(date));
		return calendar.getActualMinimum(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 得到本月的第一天
	 */
	public static Date getMonthFirstDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));

		return calendar.getTime();
	}

	/**
	 * 得到本月的最后一天
	 */
	public static Date getMonthLastDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		return calendar.getTime();
	}

	/**
	 * 按用户格式字符串取得每月最大天数
	 * 
	 * @param date
	 * @return
	 */
	public static int getMonthMaxDay(String date, String format) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(parse(date, format));
		return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 根据开始与结束时间取得间隔月份
	 * 
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static int calculateIntervalMonths(Date startTime, Date endTime) {
		Calendar start = Calendar.getInstance();
		start.setTime(startTime);
		Calendar end = Calendar.getInstance();
		end.setTime(endTime);

		int year = end.get(Calendar.YEAR) - start.get(Calendar.YEAR);
		int mont = end.get(Calendar.MONTH) - start.get(Calendar.MONTH);
		return year * 12 + mont;
	}

	/**
	 * 根据开始与结束时间取得间隔月份
	 * 
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static int calculateIntervalYear(Date startTime, Date endTime) {
		Calendar start = Calendar.getInstance();
		start.setTime(startTime);
		Calendar end = Calendar.getInstance();
		end.setTime(endTime);

		int year = end.get(Calendar.YEAR) - start.get(Calendar.YEAR);
		return year;
	}

	/**
	 * 计算当前日期与账单日之间的逾期期数
	 * 
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static int calculatedelayPeriods(Date repayDate) {
		Calendar calendar = Calendar.getInstance();
		Calendar current = new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
		Calendar start = Calendar.getInstance();
		start.setTime(repayDate);
		int year = current.get(Calendar.YEAR) - start.get(Calendar.YEAR);
		int mont = current.get(Calendar.MONTH) - start.get(Calendar.MONTH);
		int delayPeriods = year * 12 + mont;
		Date repayDateMonthly = setOneDay(14);
		if (current.getTime().after(repayDateMonthly)) {
			delayPeriods = delayPeriods + 1;
		}
		return delayPeriods;
	}

	public static void set(Date date, int value, int field) {
		if (date == null)
			return;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(field, value);
		date.setTime(calendar.getTime().getTime());
	}

	/**
	 * 设置到本月的最后一天
	 * 
	 * @param endTime
	 */
	public static void setLastDayOfMonth(Date date) {
		if (date == null)
			return;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		calendar.set(Calendar.DAY_OF_MONTH, lastDay);
		date.setTime(calendar.getTime().getTime());
	}

	/**
	 * 传入时间是否在两个时间之间
	 * 
	 * @param input
	 * @param start
	 * @param end
	 * @return
	 */
	public static boolean between(Date input, Date start, Date end) {
		return compareTo(input, start) >= 0 && compareTo(input, end) <= 0;
	}

	public static Date now(String format) {
		return parse(new Date(), format);
	}

	public static int compareTo(Date date1, Date date2) {
		if (date1 == null && date2 == null)
			return 0;
		if (date1 == null && date2 != null)
			return -1;
		if (date1 != null && date2 == null)
			return 1;
		return (int) (date1.compareTo(date2));
	}

	/**
	 * 取得Linux数字时间戳
	 * 
	 * @return
	 */
	public static String getLinuxTimestamp() {
		return String.valueOf(new Date().getTime() / 1000);
	}

	/**
	 * 取得数字时间戳
	 * 
	 * @return
	 */
	public static String getTimestamp() {
		return String.valueOf(new Date().getTime());
	}

	public static String formatDate(Date old_date, String paten) {
		String date = null;
		if (old_date == null)
			return null;
		SimpleDateFormat tempDate = new SimpleDateFormat(paten);
		try {
			date = tempDate.format(old_date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * 是否在还款时间内，（7号到14号，02点到16点）
	 * 立即还款按钮是否可点 1 可点击，2点完提示北银2点到16点之间，3不可点
	 * @param input
	 * @param start
	 * @param end
	 * @return
	 */
	public static String canRepay(Date orderPeriodMonthCreateToPay) {
		Calendar ca1 = Calendar.getInstance();
		Calendar ca2 = Calendar.getInstance();
		int year = ca1.get(Calendar.YEAR);// 获取年份
		int month = ca1.get(Calendar.MONTH);// 获取月份
		int day = ca1.get(Calendar.DATE);// 获取日
		ca1.set(year, month, day, 2, 0, 0);
		ca2.set(year, month, day, 16, 0, 0);
		int calculatedelayMonth = calculatedelayMonth(orderPeriodMonthCreateToPay);
		if (calculatedelayMonth < 0) {
			// 当前月小于还款月，不能还款，设置为3
			return "3";
		} else if (calculatedelayMonth > 0) {
			// 当前月大于还款月，相应时间段内可以还款
			return between(new Date(), ca1.getTime(), ca2.getTime()) == true ? "1" : "2";
		} else {// 当前月等于还款月
			if (day < 7) {
				return "3";
			} else
				return between(new Date(), ca1.getTime(), ca2.getTime()) == true ? "1" : "2";
		}
	}
	
	/**
	 * 计算当前日期月份是否在账单日期月份之后（需要考虑跨年的情况）
	 */
	public static int calculatedelayMonth(Date repayDate) {
	    Calendar calendar = Calendar.getInstance();
	    Calendar current = new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
	    Calendar start = Calendar.getInstance();
	    start.setTime(repayDate);
	    int year = current.get(Calendar.YEAR) - start.get(Calendar.YEAR);
	    int mont = current.get(Calendar.MONTH) - start.get(Calendar.MONTH);
	    int delayMonth = year * 12 + mont;
	    return delayMonth;
	}
	
	
	/** 计算年龄 */
	public static String getAge(Date birthDay) throws Exception {
		Calendar cal = Calendar.getInstance();

		if (cal.before(birthDay)) {
			throw new IllegalArgumentException("The birthDay is before Now.It's unbelievable!");
		}

		int yearNow = cal.get(Calendar.YEAR);
		int monthNow = cal.get(Calendar.MONTH) + 1;
		int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);

		cal.setTime(birthDay);
		int yearBirth = cal.get(Calendar.YEAR);
		int monthBirth = cal.get(Calendar.MONTH);
		int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);

		int age = yearNow - yearBirth;

		if (monthNow <= monthBirth) {
			if (monthNow == monthBirth) {
				// monthNow==monthBirth
				if (dayOfMonthNow < dayOfMonthBirth) {
					age--;
				}
			} else {
				// monthNow>monthBirth
				age--;
			}
		}

		return age + "";
	}

	/**
	 *根据身份证号计算年龄
	 */
	public static String getAgeByIdNo(String identityCardNo) {
	    String age = "0";
	    try {
	        String birthday = "";
	        if (identityCardNo.length() == 15) {
	            birthday = "19" + identityCardNo.substring(6, 8) + identityCardNo.substring(8, 10) + identityCardNo.substring(10, 12);
	        } else if (identityCardNo.length() == 18) {
	            birthday = identityCardNo.substring(6, 10) + identityCardNo.substring(10, 12) + identityCardNo.substring(12, 14);
	        } else {
	            System.out.println("身份证号格式不正确");
	            return "-1";
	        }
	        Date birthdayDate = DateUtil.parse(birthday, FORMAT_SHORT_EXT);
	        age = getAge(birthdayDate);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return age;
	}
	
	/**
	 * 时间相减获取天 d1-d2
	 * */
	public static int getBetweenDays(Date d1, Date d2) {
		try {
			int betweenDays = 0;
			Calendar c1 = Calendar.getInstance();
			Calendar c2 = Calendar.getInstance();
			c1.setTime(d1);
			c2.setTime(d2);
			// 保证第二个时间一定大于第一个时间
			if (c1.after(c2)) {
				// c1 = c2;
				// c2.setTime(d1);
			}
			int betweenYears = c1.get(Calendar.YEAR) - c2.get(Calendar.YEAR);
			betweenDays = c1.get(Calendar.DAY_OF_YEAR) - c2.get(Calendar.DAY_OF_YEAR);
			for (int i = 0; i < betweenYears; i++) {
				c2.set(Calendar.YEAR, (c2.get(Calendar.YEAR) + 1));
				betweenDays += c2.getMaximum(Calendar.DAY_OF_YEAR);
			}
			return betweenDays;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 获取当前日
	 * 
	 * @return
	 */
	public static int getCurrentDay() {
		Calendar cal = Calendar.getInstance();
		int day = cal.get(Calendar.DATE);
		return day;
	}

	/**
	 * 设置当前月某一日的日期
	 * 
	 * @param days
	 * @return
	 */
	public static Date setOneDay(int days) {
		Calendar cal = Calendar.getInstance();
		Calendar calendar = new GregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), days);
		return calendar.getTime();
	}
	
	/**
	 * 判断时间差是否在五分钟之内
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static boolean compareToByMinute(Date date1, Date date2) {
		return date1.getTime() - date2.getTime() < 5*60*1000;
	}
	
	public static Date addMinutes() {
		return new Date();
	}
	/**
	 * 获取到凌晨的时间差（秒）
	 * @param date
	 * @return
	 */
	public static int getSeparatedSecond(Date date) {
		Date tomorrow = parse(addDay(date, 1), FORMAT_SHORT);
		long tomorrowTime = tomorrow.getTime();
		long nowTime = date.getTime();
		return (int)(tomorrowTime - nowTime)/1000;
	}

	public static void main(String[] args) {
	
		System.out.println(getSeparatedSecond(new Date()));
				
	}
	
	/**  
     * 计算两个日期之间相差的天数  
     * @param smdate 较小的时间 
     * @param bdate  较大的时间 
     * @return 相差天数 
     * @throws ParseException  
     */    
    public static int daysBetween(Date smdate,Date bdate){    
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");  
        try {
			smdate=sdf.parse(sdf.format(smdate));
			bdate=sdf.parse(sdf.format(bdate));  
        } catch (ParseException e) {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
        }  
        Calendar cal = Calendar.getInstance();    
        cal.setTime(smdate);    
        long time1 = cal.getTimeInMillis();                 
        cal.setTime(bdate);    
        long time2 = cal.getTimeInMillis();         
        long between_days=(time2-time1)/(1000*3600*24);  
            
       return Integer.parseInt(String.valueOf(between_days));           
    }
}
