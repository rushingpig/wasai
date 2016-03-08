package com.seastar.wasai.utils;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
  
public class RelativeDateFormat {  
  
    private static final long ONE_MINUTE = 60000L;  
    private static final long ONE_HOUR = 3600000L;  
    private static final long ONE_DAY = 86400000L;  
    private static final long ONE_WEEK = 604800000L;  
  
    private static final String ONE_SECOND_AGO = "秒前";  
    private static final String ONE_MINUTE_AGO = "分钟前";  
    private static final String ONE_HOUR_AGO = "小时前";  
    private static final String ONE_DAY_AGO = "天前";  
    private static final String ONE_MONTH_AGO = "个月前";  
    private static final String ONE_YEAR_AGO = "年前";  
  
    public static String formatDiffDate(Date date) {  
        long delta = new Date().getTime() - date.getTime();  
        if (delta < 1L * ONE_MINUTE) {  
            long seconds = toSeconds(delta);  
            return (seconds <= 0 ? "刚刚发表" : seconds) + ONE_SECOND_AGO;  
        }  
        if (delta < 45L * ONE_MINUTE) {  
            long minutes = toMinutes(delta);  
            return (minutes <= 0 ? 1 : minutes) + ONE_MINUTE_AGO;  
        }  
        if (delta < 24L * ONE_HOUR) {  
            long hours = toHours(delta);  
            return (hours <= 0 ? 1 : hours) + ONE_HOUR_AGO;  
        }  
        if (delta < 48L * ONE_HOUR) {  
            return "昨天";  
        }  
        if (delta < 30L * ONE_DAY) {  
            long days = toDays(delta);  
            return (days <= 0 ? 1 : days) + ONE_DAY_AGO;  
        }  
        if (delta < 12L * 4L * ONE_WEEK) {  
            long months = toMonths(delta);  
            return (months <= 0 ? 1 : months) + ONE_MONTH_AGO;  
        } else {  
            long years = toYears(delta);  
            return (years <= 0 ? 1 : years) + ONE_YEAR_AGO;  
        }  
    }  
    
    public static String format(Date date) {  
    	Date currentDate = new Date();
    	Calendar currentCal = Calendar.getInstance();
    	currentCal.setTime(currentDate);
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(date);
    	int diffDay = currentCal.get(Calendar.DAY_OF_YEAR) - cal.get(Calendar.DAY_OF_YEAR);
    	int diffYear = currentCal.get(Calendar.YEAR) - cal.get(Calendar.YEAR);
    	
    	if(diffYear == 0){
    		if(diffDay == 0){
        		return dateToStr(date,"HH:mm");
        	}else if(diffDay == 1){
        		return "昨天";
        	}else if(diffDay == 2){
        		return "前天";
        	}else{
        		return dateToStr(date,"MM月dd日");
        	}
    	}else{
    		return dateToStr(date,"yyyy年MM月dd日");
    	}
    	
    }
    
    public static String dateToStr(Date date,String pattern){
    	SimpleDateFormat sdf = new SimpleDateFormat(pattern);
    	return sdf.format(date);
    }
  
    private static long toSeconds(long date) {  
        return date / 1000L;  
    }  
  
    private static long toMinutes(long date) {  
        return toSeconds(date) / 60L;  
    }  
  
    private static long toHours(long date) {  
        return toMinutes(date) / 60L;  
    }  
  
    private static long toDays(long date) {  
        return toHours(date) / 24L;  
    }  
  
    private static long toMonths(long date) {  
        return toDays(date) / 30L;  
    }  
  
    private static long toYears(long date) {  
        return toMonths(date) / 365L;  
    }  
  
} 