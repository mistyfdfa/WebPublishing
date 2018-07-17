package selenium.common;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.jsoup.Connection.Method;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.GeckoDriverService;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.service.DriverService;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import resources.utilities.Functions;
import resources.utilities.Retry;
import resources.common.Logger;
import resources.exceptions.ElementNotVisibleException;
import resources.exceptions.InvalidAutomationParameterException;

public class SelDriver extends ChromeDriver {

	public Class<?> pageClass = null;
	public String mainWindowHandle;
	private static ChromeDriverService service;
	private static String prefDriver = Functions
		.readProperties("web_driver", "default_driver")
		.trim();
	final int WAIT_TIME = 5; //any waiting uses this time
	private int windex = 0;
	Set<String> handles = null;
	protected Retry<Boolean> findRetry;
	
	public SelDriver() {
		super(createService(),addChromeOptions());
		manage().timeouts().implicitlyWait(
				WAIT_TIME, TimeUnit.SECONDS);
		handles = getWindowHandles();
		mainWindowHandle = getMainWindowHandle();
	}
	
	public static ChromeDriverService createService() {
		String pathname = Paths.get("")
			.toAbsolutePath()
        	.toString() 
        	+ Functions
        	.readProperties("web_driver",prefDriver);
        System.setProperty("webdriver.chrome.driver", pathname);		
		service = new ChromeDriverService.Builder()
			.usingDriverExecutable(new File(pathname))
			.usingAnyFreePort()
			.build();
		return service;
	}
		
	private static ChromeOptions addChromeOptions(){
		ChromeOptions options = new ChromeOptions();
		options.addArguments("disable-extensions");
		options.addArguments("start-maximized");
		options.addArguments("disable-infobars"); 
		options.setExperimentalOption("useAutomationExtension", false); 
		return options;
	}
	
	public Class<?> getPageClass(){
		return pageClass;
	}
	
	public void setPageClass(Class<?> classIn){
		pageClass = classIn;
	}
	
	public String getMainWindowHandle(){
		String mainWindow = getWindowHandle();
		Logger.log("Window Handle: "+mainWindow+"\n");
		return mainWindow;
	}
	
	public void switchToMainWindow() {
		switchTo().window(mainWindowHandle);
		windex = 0;
	}
	
	public int getNumberOfWindows() {
		handles = getWindowHandles();
		return handles.size();
	}
	
	public void switchToNextWindow() {
		String[] temp = new String[1];
		temp = handles.toArray(temp);
		if(mainWindowHandle==null)
			mainWindowHandle = getMainWindowHandle();
		if(windex+1 < getNumberOfWindows()){
			switchTo()
			.window(temp[windex++]);
		} else {
			switchToMainWindow();
		}
	}
	
	public void wait(int seconds) {
		Logger.log("Waiting for "+seconds+" seconds...");
		try {
			Thread.sleep(seconds*1000);
			Logger.log("Wait complete.");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void goForward() {
		navigate().forward();
		Logger.log("Navigated forward.");
	}
	
	public void goBack() {
		navigate().back();
		Logger.log("Navigated back.");
	}
	
	public void refresh() {
		navigate().refresh();
		Logger.log("Page refreshed.");
	}
	
	/**
	 * Method is used to select a web element from the page object
	 * repository
	 * 
	 * @param 
	 * 		elementName is the name of object that is returned from the 
	 * 		page object repository
	 * @return 
	 * 		the WebElement to be used by other Selenium functions
	 * @throws
	 * 		ElementNotVisibleException 
	 * @throws InvalidAutomationParameterException 
	 */	
	public WebElement getElement(String elementName) 
			throws ElementNotVisibleException, InvalidAutomationParameterException {
		if(Functions.areDebugging()) 
			Logger.log("[sF:gE:I] elementName to get(): " + elementName);
		try {
			Field field = pageClass.getField(Functions.convertToCamel(elementName));
			field.setAccessible(true);
			WebElement webElement = (WebElement) field.get(elementName);
			if (isVisible(webElement)) {
				//the element exists, so return it so the automation can act on it
				if (Functions.areDebugging()) Logger.log(
						"[sF:gE:I] "+elementName+" retrieved a webElement.");
				return webElement;
			}
		} catch(NoSuchFieldException nsfe) {
			throw new ElementNotVisibleException(
				"[sF:gE:E] "+elementName + "was not found.");
		} catch (Exception e) {
			e.printStackTrace();
		}
		throw new InvalidAutomationParameterException(
				"[sF:gE:E] "+elementName+" did not have any hits in this context.");
	}
	
	public String getXpath(String elementName)
			throws InvalidAutomationParameterException {
		
		Field field = null;
		try {
			field = pageClass.getField(Functions.convertToCamel(elementName));
			field.setAccessible(true);
		} catch (NoSuchFieldException nsfe) {
			throw new InvalidAutomationParameterException(
					"[sF:gX:E] "+elementName+" is not a valid field in "+
					pageClass.getSimpleName()+"!");
		} catch (SecurityException e) {
			throw new InvalidAutomationParameterException(
					"[sF:gX:E] Not permitted to access fields of "+
					pageClass.getSimpleName()+"!");
		}
		try {
			return (String) field.get(elementName);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new InvalidAutomationParameterException(
					"[sF:gX:E] Attempted to access fields of"
					+ pageClass.getSimpleName() + "illegally!");
		}
	}
	
	/**
	 * Method will wait for an object to be visible before completing
	 * @param elmnt is the element that has the wait condition set upon.
	 */
	public void waitUntilVisible(WebElement elmnt) {
		waitUntilVisible(elmnt, 1);		
	}
	
	public void waitUntilVisible(WebElement elmnt, int timeToWait) {
		WebDriverWait wait = new WebDriverWait(this,timeToWait);
		wait.until(ExpectedConditions.visibilityOf(elmnt));
	}
	
	public boolean isVisible(String string) { 
		return isVisible(selectElement(string));
	}
	
	public boolean isVisibleWithRetry(final WebElement toPass) {
	    	return findRetry.run(() -> isVisible(toPass));
	}
		
	public boolean isVisible(WebElement element) {
		try {
			WebDriverWait wait = new WebDriverWait(this,this.WAIT_TIME);
			wait.until(ExpectedConditions.visibilityOf(element));
			if (Functions.areDebugging()) Logger.log(
					"[sF:iV:I] "+elementText(element)+" is visible!");
			return true;
		} catch (TimeoutException e) {
			Logger.log("[sF:iV:W] Timed out looking for element.");
			return false;
		} catch (Exception e) {
			Functions.logLine(new ElementNotVisibleException("Was unable to find "+elementText(element)));
			return false;
		}
	}
	
	/**
	 * Method is used to find an element on the page based on the XPath of the object
	 * @param xpath used to locate an element on a page
	 * @return a WebElement that can be acted upon
	 * @throws InvalidAutomationParameterException 
	 */
	public WebElement findElement(String xpath)
			throws InvalidAutomationParameterException {
		return findElement(By.xpath(xpath));
	}
	
	//TODO Use By by to figure out how we are searching for an element
	public WebElement findElement(By findBy) {
		WebElement element = null;
		element = findBy.findElement(this);
		
		try {
			isVisible(element);
			if (Functions.areDebugging()) Logger.log(
				"[sF:fE:i] "+findBy.toString()+
				" search found an element.");
			return element;
		} catch (TimeoutException e) {
			exitGracefully("[sF:fE:E] " + elementText(element)
							+ " selected by " + findBy.toString()
							+ " was not visible.");
			return null;
		}
	}
	
	public WebElement selectElement(final String elementName){
		try {
			return getElement(elementName);
		} catch (ElementNotVisibleException |InvalidAutomationParameterException e1) {
			try {
				return findElement(elementName);
			} catch (InvalidAutomationParameterException e) {
				exitGracefully(e);
				return null;
			}
		}
	}

	/**
	 * Gets inner text of web element for given object name in repository
	 * @param elementName Name of selenium WebElement in object repository
	 * @throws ElementNotVisibleException 
	 */
    public String elementText(WebElement element) {
		return element.getAttribute("innerText").trim();
	}
    
    public String elementText(String elementName) 
			throws ElementNotVisibleException {
		if(Functions.areDebugging())
			Logger.log("\n[sF:gET:I] Selenium Element: "+elementName+"");
		try {
			return elementText(getElement(elementName));
		} catch (WebDriverException | InvalidAutomationParameterException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * This method takes a list of web elements and iterates through to 
	 * grab their attribute value in order to test for text validation
	 * 
	 * @param elements list that is to be converted into string values
	 * @return inner text values for every web element in the elements list
	 */
	public List<String> elementTextBatch(List<WebElement> elements){
		List<String> linkText = new ArrayList<String>();
		for(WebElement el : elements){
			linkText.add(elementText(el));
		}
		return linkText;
	}
	
	/**
	 * Method to execute a mouse click on an element that is already in the page object repository
	 * @param elementName is the name of the element from the corresponding object repository page
	 * @throws ElementNotVisibleException 
	 */
	public boolean clickElement(String elementName) {
		if (pageClass==null)
			exitGracefully("WebElement page not initialized.");
		WebElement temp = selectElement(elementName);
		if (!clickElement(temp))
			clickElement(temp);
		return true;
	}
	
	public boolean clickElement(final WebElement webL) {
	    	//return findRetry.run(() -> 
	    	//{
	    		try {
	    			webL.click();
	    			return true;
	    		} catch (WebDriverException wde) {
	    			return false;
	    		}
	    	//});
	}
    
	//TODO this functionality in clickElement
	public void selectCheckbox(String elementName, boolean targetState)
			throws ElementNotVisibleException {
		if (pageClass==null)
			exitGracefully("WebElement page not initialized.");
	
		WebElement temp = selectElement(elementName);
							
		if (temp.isSelected()!=targetState)
			if(clickElement(temp))
				Logger.log("[sF:sC:E] Selected checkbox "
					+ elementName +".");
			else
				throw new ElementNotVisibleException(
					"[sF:sC:E] Was not able to click on"+elementName);
		else
			Logger.log("[sD:sC:W] "+elementName+" was already in requested state.");
	}
	
	public void selectFromDropdown(String dropDownElementName,String selectionText) 
			throws ElementNotVisibleException {
		if (pageClass==null)
			exitGracefully("[sF:sFD:E] WebElement page not initialized.");
		WebElement webElement = null;
		try {
			webElement = getElement(dropDownElementName);
		} catch (InvalidAutomationParameterException e) {
			exitGracefully(e);
		}
		Select dropdown = new Select(webElement);
		dropdown.selectByVisibleText(selectionText);
		Logger.log("[sF:sFD:I] Selected "+selectionText+" from " + 
			dropDownElementName+" dropdown.");
	}	
	
		
		public void selectRadioButton(
				String radioButtonElementName,String selectionText) {
			if (pageClass==null) {
				String xpath = "";
				try {
					xpath = getXpath(radioButtonElementName);
				} catch (InvalidAutomationParameterException iape1) {
					Logger.log(iape1.getMessage());
					return;
				}
				
				xpath = xpath+"/button[contains(text(),'" + selectionText+"')]";
				
				try {
					WebElement webElement = findElement(xpath);
					webElement.click();
					Logger.log("[sF:sRB:I] Selected "+selectionText+" from "
								+radioButtonElementName+" radio buttons.");
				} catch (InvalidAutomationParameterException iape2) {
					exitGracefully(
							"[sF:sRB:E] Unable to click " + radioButtonElementName);
				}
			} else {
				exitGracefully("[sF:sRB:E] WebElement page not initialized.");
			}	
		}

		
		/**
		 * Method writes the given text to an input field
		 * @param elementName is the text field element which is where the text will go
		 * @param text to be entered into the field
		 * @throws InvalidAutomationParameterException 
		 * @throws ElementNotVisibleException 
		 */
		public void inputToTextField(String elementName,String text)
				throws ElementNotVisibleException {
			inputToTextField(selectElement(elementName), text);
		}
		
		public void inputToTextField(WebElement element,String text) 
				throws ElementNotVisibleException {	
			if (pageClass==null)
				exitGracefully("[sF:itTF:E] WebElement page not initialized.");
			isVisible(element);
			element.clear();
			element.sendKeys(text);
			Logger.log("[sF:iMtTF:I] Typed "+text+" into "
					+element.getText()+" field.");
		}
		
		public void inputMultipleToTextFields(HashMap<String,String> data)
				throws ElementNotVisibleException {
			if (pageClass==null)
				exitGracefully("[sF:iMtTF:E] WebElement page not initialized.");
			
			Set<Entry<String,String>> set = data.entrySet();
			Iterator<Entry<String,String>> iterator = set.iterator();
			String elementName = null;
				
			while(iterator.hasNext()) {
			    try {
				    Entry<String,String> pair = iterator.next();
					elementName = pair.getKey().toString();
					String text = pair.getValue().toString();
						inputToTextField(
							selectElement(elementName), text);
			    } catch (NullPointerException e) {
			    	throw new ElementNotVisibleException(
			    		"[sF:iMtTF:E] "+elementName+" was not found!");
			    }
			}
		}
	
		/**
		 * Uses the sendKeys method to send a document to the given upload button.
		 * 
		 * @param 
		 * 		filePath The absolute file path of the document that should be uploaded. 
		 * 		Document to be uploaded should be in upload_location folder in Reporting.properties
		 * @param 
		 * 		element The WebElement that would normally be clicked to upload the document. 
		 * 		These elements typically have an 'input' type of 'file'.
		 */
		public void uploadDocumentToElement(
				String docName, WebElement element) {
			
			if (pageClass==null)
				exitGracefully("[sF:uDtE:I] WebElement page not initialized.");
			
			String filePath = Paths.get("").toAbsolutePath().toString() + 
				Functions.readProperties("reporting","upload_location") + docName;
			Logger.log("[sF:uDtE:I] Uploading document from "+filePath+".");
			element.sendKeys(filePath);	
		}
		
		//For testing xPaths while debugging, 
		//Altering these lines while the debugger is running 
		//will hotfix the code and restart the block for another
		//pass at another xPath
		public String xPathTest(String xpath){
			String temp = "//form"; //[contains(text(), \""+xpath.trim()+"\")]";
			//if(Functions.areDebugging())
				Logger.log("[sF:rR:I] Potential xpath: "+temp);
			try {
				Logger.log("[sF:rR:I] Got: "+findElement(By.xpath(temp)).getText());
				Logger.log("[sF:rR:I] Got: "+findElement(By.xpath(temp)).getTagName());
			} catch (WebDriverException wde) {
				Logger.log("[sF:rR:E] Better try something else!");
			}
			return temp;
		}
		
		public void exitGracefully(Exception e){
			exitGracefully(
				"Error in" + Functions.methodName(e) + " " + e.getMessage());
			return;
		}
		
		public void exitGracefully(String err){
			Logger.log(err);
			destroyDriver();
			return;
		}
		
		public void closeCurrentWindow() {
			close();
		}
		
		public void destroyDriver() {
			quit();
			service.stop();
		}
}
