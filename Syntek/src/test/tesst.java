/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 *
 * @author MrOnly
 */
public class tesst {
    public static void main(String[] args) {
         DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss");
	   //get current date time with Date()
	   Date date = new Date();
	   System.out.println(dateFormat.format(date));
 
	   //get current date time with Calendar()
	   Calendar cal = Calendar.getInstance();
           String temp = dateFormat.format(cal.getTime());
           
	   System.out.println(temp);
           String temp1 = "D:\\Beat truong minh111\\chih lai";
           System.out.println(temp1.substring((temp1.lastIndexOf("\\")+1)));
           System.out.println(System.currentTimeMillis());
    }
}
