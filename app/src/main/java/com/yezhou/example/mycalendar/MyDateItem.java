package com.yezhou.example.mycalendar;

/**
 * Created by Administrator on 2017/11/26.
 */

public class MyDateItem {

    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private int second;
    private String title;
    private String content;
    private boolean isRemind;    // 是否设置提醒
    private String date;    // 格式封装好的date
    private boolean isTimeOut;
    private String time;

    public MyDateItem() {
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public int getSecond() {
        return second;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isRemind() {
        return isRemind;
    }

    public void setRemind(boolean remind) {
        isRemind = remind;
    }

    public void setDate(int y, int m, int d) {    // 日期格式封装成"yyyy-mm-dd"
        year = y;
        month = m + 1;    // dialog中返回的月份减了1
        day = d;

        date = year +"-"+ month + "-" +day;
    }

    public void setDate(String d) {
        date = d;
    }

    public String getDate() {
        return date;
    }

    public boolean isTimeOut() {
        return isTimeOut;
    }

    public void setTimeOut(boolean timeOut) {
        isTimeOut = timeOut;
    }

    public void setTime(String t) {
        time = t;
    }

    public String getTime() {
        return time;
    }

    public void decomposeDate() {    // 分解date
        String [] a = date.split("-");
        year = Integer.parseInt(a[0]);
        month = Integer.parseInt(a[1]);
        day = Integer.parseInt(a[2]);
    }

    public void decomposeTime() {    // 分解time
        String [] a = time.split(":");
        hour = Integer.parseInt(a[0]);
        minute = Integer.parseInt(a[1]);
        second = Integer.parseInt(a[2]);
    }
}
