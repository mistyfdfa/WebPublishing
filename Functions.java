package resources.utilities;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Random;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.commons.configuration.ConfigurationException;
import org.junit.Assert;

import database.DBConnection;
import loanobjects.Loan;
import main.DebugHarness;
import main.ExcelSheet;
import main.GUIFunctions;
import main.Main;
import main.Runner;
import resources.common.Logger;
import selenium.common.SelDriver;
import stepdefs.common.Shell;

public class Functions {
	public static boolean failed = false;
	public static int failCount;
	protected static DBConnection db;
	private static boolean debugging = false;
	private static Hashtable<String, String>
	fileCache = new Hashtable<String, String>();
	
	
	//Debug-Settings---------------------------------------------------
	public static boolean areDebugging() {
		return debugging;
	}
	
	public static void toggleDebugging() {
		debugging = !(debugging);
	}
	
	//Verifications----------------------------------------------------
	public static DBConnection checkConnection(String connectName) {
		if (Main.db != null)
			return Main.db;
		else if (Runner.db != null)
			return Runner.db;
		else if (Shell.db != null)
			return Shell.db;
		else if (DebugHarness.db != null)
			return DebugHarness.db;
		else {
			Logger.log("New connection requested from "+connectName);
			return new DBConnection(connectName);
		}
	}
	
	public static SelDriver checkDriver(){
		if(Main.sd != null)
			return Main.sd;
		else if (Shell.sd != null)
			return Shell.sd;
		else if (DebugHarness.sd != null)
			return DebugHarness.sd;
		else {
			Logger.log("Created new SelDriver");
			return new SelDriver();
		}
	}
	
	public static Loan checkLoan() {
		if (Shell.currentLoan != null)
			return Shell.currentLoan;
		else
			return null;
	}
	
	/**
	 * Returns true if given Strings are exactly equal.
	 */
	public static Boolean compareText(String expectedText, String actualText) {
		Logger.log("Expected: " + expectedText + "\nActual: " + actualText);
		if (expectedText.equals(actualText)) {
			Logger.log("PASS");
			return true;
		} else {
			Logger.log("FAIL");
			return false;
		}
	}
	
	public static Boolean isNotNull(Object obj) {
		try {
			Logger.log(obj.toString());
			return true;
		} catch(NullPointerException e) {
			return false;
		}
	}
	
	public static void logLine(Throwable th) {
		Logger.log(methodName(th) + " " + th.getMessage());
		
	}
	
	public static void logLine(Throwable th, String str){
		Logger.log(methodName(th) + " " + str);
	}
	
	public static void failedAnalysis() {
		Logger.log("-----Test Analysis-----");
		if (failed)
			Assert.fail("Failed Step Count: " + failCount);
		else
			Logger.log("All tests passed!");
	}
	
	/** Asserts test failure and adds given message to Cucumber html report
	 * 
	 * @param  report The message to be output in the Cucumber htmlreport
    */
	public static void failStep(String report) {
		Logger.logWithBreaks("<< STEP FAILED >> "+report);
		//Assert.fail(report);
		failed = true;
		failCount++;
	}
	
	/** Adds given pass message to Cucumber html report
	 * 
	 * @param  report The message to be output in the Cucumber htmlreport
    */
	public static void passStep(String report) {
		Logger.logWithBreaks("<< STEP PASSED >> "+report);
	}
	
	//Transformers-----------------------------------------------------
	public static String localDateToSQL(LocalDate localDate){
		String[] bomArr = localDate.toString().split("-");
		String temp = bomArr[2];
		bomArr[2] = bomArr[0].substring(2,4);
		bomArr[1] = "JUN";
		bomArr[0] = temp;
		temp = "";
		
		for(int i = 0;i<bomArr.length;i++){
			temp += "-" + bomArr[i];
		}
		
		return temp.substring(1);
	}
	
	public static String convertToCamel(String given) {
		String changed = given.trim();
		Boolean spaces = false;
		Boolean uScore = false;
		
		do {
			if(given.contains(" "))
				spaces = true;
			if(given.contains("_"))
				uScore = true;
			
			char[] chars = changed.toCharArray();
			int pos = 0;
			for(int i = 0;i<chars.length;i++) {	
				if (chars[i] ==' ' || chars[i] == '_') {
					pos = i;
					break;
				}
			}
			if (pos == 0) {
				spaces = false;
				uScore = false;
				changed = changed.substring(0,1)
						.toLowerCase()+changed.substring(1);
				break;
			}
			else {	
				changed =
						changed.substring(0,1).toLowerCase() +
						changed.substring(1,pos) + 
						changed.substring(pos+1,pos+2).toUpperCase() +
						changed.substring(pos+2).toLowerCase();
			}
		} while(spaces || uScore);
		return changed;
	}

	public static String convertArListToStr(
			ArrayList<String> arlistToProcess, String conjunction) {
		String[] processedAr = new String[1];
		String processedStr = "";
		processedAr = arlistToProcess.toArray(processedAr);
		for(int i = 0;i<processedAr.length;i++)
			if (processedStr.isEmpty())
				processedStr = processedAr[i];
			else
				processedStr += conjunction + processedAr[i];
    	return processedStr;
	}

    @Deprecated
    public String cleanPhoneNumber(String phoneNum) {
		return phoneNum.replaceAll("[^0-9\\+]", "");
	}
    
    /**
	 * Transforms String based on given transform code
	 * @param transformEffect Transform code to use on String
	 * @param input String to transform
	 */
	public static String transform(String transformEffect, String input) {
		switch (transformEffect) {
			case "Split-2":
				return partialString(input,2);
			case "Split-3":
				return partialString(input,3);
			case "Dollars.00":
				return "$" + new DecimalFormat(
						"#,###.00").format(Double.parseDouble(input));
			case "Dollars":
				return "$" + new DecimalFormat(
						"#,###").format(Double.parseDouble(input));
			case "Phone":
				return "(" + input.substring(0, 3)+")" + " " + String.format(
						"%s-%s",input.substring(3,6),input.substring(6,10));
			default:
				exitGracefully(new Exception("[sF:t:E] " + transformEffect + 
						" is not a valid manner of transformation"));
		}
		return null;
	}
	
	public static void updateProperties(String props, String key, String value) {
		Logger.log("Updating property.");
		try{
			PropertiesConfiguration config = 
					new PropertiesConfiguration(props + ".properties");
			config.setProperty(key, value);
			config.save();
			Logger.log("key is defined as: " + readProperties(props,key));
		} catch(NullPointerException npe) {
			exitGracefully(npe);
		} catch(ConfigurationException ce) {
			exitGracefully(ce);
			
		}
		Logger.log(key + " set to " + readProperties(props,key));
	}
	
	//Data Generation-and-Collection-----------------------------------------------------
	public static String methodName(Throwable th){
		StackTraceElement stackTraceElements[] = th.getStackTrace();
		return stackTraceElements[0].getMethodName();
	}
	
	/** Outputs random number between bot and top
	 * @param bot The lowest possible integer
	 * @param top The highest possible integer
	 * @return randNum Random number between bot and top
	*/
	public static int wholeRandom(int top) {
		return wholeRandom(0, top);
	}
	
	public static int wholeRandom(int bot, int top) {
		return new Random().nextInt(top)+bot;
	}
	
	public static float floatRandom() {
		return new Random().nextFloat();
	}
	
	public static ExcelSheet getMappingSheet(
		String excelName, int sheetNumber) {
		try {
			XSSFWorkbook wb = new XSSFWorkbook(
				new FileInputStream(
					new File(extract("/mapping/"+excelName))));
			return new ExcelSheet(wb,sheetNumber);
		} catch(FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch(IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Returns designated word in a multi-word String
	 * @param stringToSplit Full string to pull from
	 * @param wordNumber Number of word in string, starting with 1
	 * @return
	 */
	public static String partialString(String stringToSplit, int wordNumber) {
		String[] strings=stringToSplit.split(" ");
		if (wordNumber > 0)
			wordNumber -= 1;
		return strings[(wordNumber)];
	}
	
	/**
	 * Returns ArrayList of Strings for distinct values in database, 
	 * feeding off of DBConnection in Main class.
	 * @param query Full query for desired list of Strings
	 * @param column Column containing values to be put in ArrayList
	 * @return ArrayList<String> of distinct values according to given query
	 */
	public static ArrayList<String> getList(String column, String query) {
		ResultSet rt;		
		ArrayList<String> arrayList = new ArrayList<String>();
		try {
			rt = db.query(query);
			while(rt.next()) {
				arrayList.add(rt.getString(column));
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return arrayList;
	}
	
	public static Color randomColor(float[] colors) {
		if(areDebugging())
			GUIFunctions.flexBox(
			colors[0]+ " " + colors[1] + " " + colors[2]
			+ " " + colors[3], "ColorInfo");
		for(int j=0;j<colors.length-1;j++) {
			colors[j] += floatRandom();
			if (colors[j] >= 1)
				colors[j] = 0;
		}
		if(areDebugging())
			GUIFunctions.flexBox(
					colors[0] + " " + colors[1] + " " + colors[2]
					+ " " + colors[3], "ColorInfo");
			return new Color(colors[0],colors[1],colors[2],colors[3]);
	}

	public static void exitGracefully(Exception ex) {
		Logger.log(methodName(ex) + " " + ex.getMessage());
		System.exit(1);
	}
	
	public static InetAddress setLocalHost(){
		try{
			InetAddress hostName = InetAddress.getLocalHost();
            Logger.log("Host Name: " + hostName.getHostName());
            return hostName;
		}catch (UnknownHostException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
		return null;
	}
	
	/**
     * Extract the specified resource from inside the jar to the local file system.
     * @param jarFilePath absolute path to the resource
     * @return full file system path if file successfully extracted, else null on error
     */
    public static String extract(String jarFilePath) {
        if (jarFilePath == null)
            return null;
        if (fileCache.contains(jarFilePath))
            return fileCache.get(jarFilePath);

        try {
            InputStream fileStream = Functions.class
            		.getResourceAsStream(jarFilePath);          
            
            if (fileStream == null)
                return null;

            String[] chopped = jarFilePath.split("\\/");
            String fileName = chopped[chopped.length-1];

            File tempFile = File.createTempFile("asdf", fileName);

            tempFile.deleteOnExit();

            OutputStream out = new FileOutputStream(tempFile);

            byte[] buffer = new byte[1024];
            int len = fileStream.read(buffer);
            while(len != -1) {
                out.write(buffer, 0, len);
                len = fileStream.read(buffer);
            }

            fileCache.put(jarFilePath, tempFile.getAbsolutePath());

            fileStream.close();
            out.close();

            return tempFile.getAbsolutePath();

        } catch(IOException e) {
            return null;
        }
    }
    
    public static String readProperties(String props, String key) {
		Properties prop = new Properties();
		String result=null;
		InputStream input;
		try {
			input = new FileInputStream(
				"I:\\git\\digitalmortgage\\src\\test\\props\\"
				+ props + ".properties");
		} catch (FileNotFoundException e1) {
			return null;
		}
		try {
			prop.load(input);
		} catch (IOException e1) {
			exitGracefully(e1);
		}
				
		result = prop.getProperty(key);
		try {
			input.close();
		} 
		catch(IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return result;
	}
}
