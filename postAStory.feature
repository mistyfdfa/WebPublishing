package stepdefs.common;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import cucumber.api.DataTable;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import resources.common.Logger;
import resources.exceptions.*;
import resources.utilities.Functions;
import resources.gallus.GallusFunctions;
import selenium.common.SelDriver;
import selenium.common.SelFunctions;
/**
 * Generic {@ code stepDefs} for setting up and navigating the environment 
 * regardless of status as banker or applicant
 * 
 * @author IT/QA Barracudas
 * @version MEX_2018.05
 * @see GallusFunctions
 * @since MEX_2017.06 
 */

public class StepDefs extends SelFunctions {
	static SelDriver sd = Shell.sd;
	/*
	 * Class is designated for Step Definitions that can apply to any website
	 */

	/*
	 * Common Cucumber Syntax:
	 * 
	 * \"([^\"]*)\" => String with quotes ([^\"]*) => String without quotes
	 * (.*?) => Integer (?:test1|test2|test3) => can type any option, code will
	 * not change (test1|test2|test3) => chosen option will determine what code
	 * is executed
	 */

	////////////////
	//Assertions
	////////////////
	
	@Given("^My browser is \"([^\"]*)\"$")
	public static void setBrowser(String type) {
		initSelDriver();
		try{
			switch (type.toLowerCase()) {
				case "firefox":
				case "chrome_237":
				case "chrome_235":
				case "chrome_229":
				case "chrome_224":
					Functions.updateProperties("web_driver", "default_driver", type.toLowerCase());
					break;
				default:
					throw new InvalidAutomationParameterException("Driver: "+type);
			}
		} catch (InvalidAutomationParameterException iape) {
			sd.exitGracefully(iape);
		}
	}
	
	@Given("^I am on the ([^\"]*) page$")
	public static void setPage(String pageName) {

		int start = 0;
		for (int i = 0; i < pageName.length(); i++) {
			if (pageName.substring(i, i + 1).equals(" ")) {
				start = i + 1;
				break;
			}
		}
		pageName = pageName.replaceAll(" ", ".");
		pageName = 
				pageName.substring(0, 1).toLowerCase() +
				pageName.substring(1, start) +
				pageName.substring(start, start + 1).toUpperCase() +
				pageName.substring(start + 1) + "Page";
		Logger.logDebug(pageName);
		setPage(pageName);
	}

	/////////////
	//Actions
	/////////////

	/**
	* Uses {@code wDriver} to navigate to {@code site}
	* @param site destination defined by featureFile
	**/
	@Given("^I navigate to \"([^\"]*)\"$")
	public static void navigateTo(String site) {
		sd.get(site);
	}
	
	/**
	* Writes {@code text} into navigate to {@code elementName} which accpets text
	* @param text text to be input 
	* @param elementName name of the element we will attempt to type text into
	**/
	@Then("^I type \"([^\"]*)\" in the ([^\"]*) (?:text field|text box|text area)$")
	public static void typeInField(String text, String elementName) {
		try {
			sd.inputToTextField(elementName, text);
		} catch (ElementNotVisibleException enve) {
			sd.exitGracefully(enve);
		}
	}

	/**
	 * Generic wrapper for {@link #clickElement(String)}
	 * @param elementName
	 * @throws ElementNotVisibleException 
	 */	
	@Then("^I click the ([^\"]*) (?:button|tab|area|section|image|link)$")
	public static void clickElement(String elementName) {
		if (areDebugging())
			Logger.log("elementName: "+elementName);
		
		clickElement(elementName);
		
		try {
			SelFunctions.takeScreenshot(sd);
		} catch (InvalidAutomationParameterException iape) {
			sd.exitGracefully(iape);
		}
	}

	/**
	 * Method to switch to the window created by ApplicantView as well as others.
	 */
	@Then("^I switch to popup$")
	public static void switchToPopup() {
		try {
			Thread.sleep(2000);
			for (final String winHandle : sd.getWindowHandles()) {
				sd.switchTo().window(winHandle);
			}
			Thread.sleep(2000);
		} catch (InterruptedException ie){
			sd.exitGracefully(ie);
		}
	}

	/**
	 * Closes the current window by destroying the driver
	 */
	//TODO should this be exitGracefully()?
	@When("^I close the current window$")
	public static void closeWindow() {
		sd.closeCurrentWindow();
	}

	/**
	 * Method to switch to the main window after switching away from it
	 */
	//TODO Does this need to be a separate step?
	@When("^I switch back to main page$")
	public static void switchBackToMainPage() {
		try {
			Thread.sleep(2000);
			String winHandleMain = sd.getWindowHandle();
			sd.switchTo().window(winHandleMain);
			Thread.sleep(2000);
		} catch (InterruptedException ie){
			sd.exitGracefully(ie);
		}
	}
	
	/**
	 * Top level definition to wait for {@code num} {@code time} where time is some configuration of second or minute 
	 * @param num
	 * @param time
	 * @throws Throwable
	 */
	@Then("^I wait for (\\d+) (second|seconds|minute|minutes)$")
	public void waitFor(int num, String time) throws Throwable {
		try {
			if (time.equalsIgnoreCase("seconds") || time.equalsIgnoreCase("second")) {
				int mil = num * 1000;
				Logger.log("Waiting for " + num + " seconds...");
				Thread.sleep(mil);
			} else if (time.equals("minutes") || time.equalsIgnoreCase("minute")) {
				int mil = num * 1000 * 60;
				Logger.log("Waiting for " + num + " minutes...");
				Thread.sleep(mil);
			}
			Functions.passStep("Wait successful.");
		} catch (Exception e) {
			e.printStackTrace();
			Functions.failStep("Could not execute wait.");
		}
	}

	/**
	 * {@Link #waitFor(int, String)} except with minutes and seconds set discretely
	 * @param minutes
	 * @param seconds
	 * @throws Throwable
	 */
	//TODO Does this and waitFor need to both exist?
	@Then("^I wait for (\\d+) (?:minute|minutes) and (\\d+) seconds$")
	public void waitForMinutesSeconds(int minutes, int seconds) throws Throwable {
		try {

			int mil = (minutes * 60000) + (seconds * 1000);
			Logger.log("Waiting for " + minutes + " minutes and " + seconds + " seconds...");
			Thread.sleep(mil);
			Functions.passStep("Wait successful.");
		} catch (Exception e) {
			e.printStackTrace();
			Functions.failStep("Could not execute wait.");
		}
	}

	/**
	 * Top level definition to navigate the user forward or backward from the page they are
	 * on. This was built so that the feature files could navigate and not be
	 * built into each step def.
	 * 
	 * @param direction
	 *            that the user wants to go in the browser
	 */
	//TODO is this functionality still needed?
	@And("^I go (back|forward) a page$")
	public static void navigateDirection(String direction) {
		if (direction.equals("back")) {
			sd.goBack();
		} else {
			sd.goForward();
		}
	}

	/**
	 * Top level definition to refresh a page using Gherkin
	 * @see resources.common.Driver#refresh() Driver#refresh()
	 */
	@And("^I refresh the page$")
	public static void refreshPage() {
		sd.refresh();
	}

	/**
	 * Top level definition to take a screenshot
	 * 
	 * @throws Exception
	 */
	@Then("^I take a screenshot$")
	public static void iTakeScreenshot() {
		try{
			SelFunctions.takeScreenshot(sd);
		} catch (InvalidAutomationParameterException iape){
			sd.exitGracefully(iape);
		}
	}

	//////////////////
	//Verifications
	//////////////////
	
	/**
	 * Top level definition to check if an {@code elmnt} is {@code enabled/disabled} based on {@code control}
	 * @param elmnt
	 * @param control
	 * @throws Throwable
	 */
	@Then("^I verify that the ([^\"]*) (?:button|link|tab|text box|text field|image|icon|popup|message) appears (enabled|disabled)$")
	public static void verifyEnabledOrDisabled(String elmnt, String control) throws Throwable {
		WebElement element = null;
		try {
			element = sd.getElement(elmnt);
		} catch (NoSuchElementException e) {
			sd.exitGracefully(e);
		}
		String dis = element.getAttribute("disabled");
		if (control.equalsIgnoreCase("enabled")) {
			if (dis == null) {
				Logger.log("" + elmnt + " button is enabled.");
			} else if (dis.equalsIgnoreCase("true")) {
				Logger.log("" + elmnt + " button is disabled.");
			}
		} else if (control.equalsIgnoreCase("disabled")) {
			if (dis == null) {
				Logger.log("" + elmnt + " button is enabled.");
			} else if (dis.equalsIgnoreCase("true")) {
				Logger.log("" + elmnt + " button is disabled.");
			}
		}
	}

	@Then("^I verify that the following (?:tabs|buttons|images|links|text fields|elements|radio buttons|check boxes|icons) (are|are not) "
			+ "present on the current page$")
	public void verifyDocuments(String an, List<String> elementList) {

		int count = 0;
		for (int i = 0; i < elementList.size(); i++) {
			try{
				WebElement element = sd.getElement(elementList.get(i));
				if (sd.isVisible(element)) {
					count++;
					Logger.log("" + elementList.get(i) + " is present on the current page.");
				} else {
					Logger.log("" + elementList.get(i) + " is NOT present on the current page.");
				}
			} catch (ElementNotVisibleException | InvalidAutomationParameterException e) {
				sd.exitGracefully(e);
			}

			if (an.equalsIgnoreCase("are")) {
				if (count == elementList.size()) {
					Functions.passStep("All expected elements are on the current page.");
				} else {
					Functions.failStep("Not all expected elements are on the current page.");
				}
			} else {
				if (count == 0) {
					Functions.passStep("None of the given elements are on the current page.");
				} else {
					Functions.failStep("Not all given elements are not on the current page.");
				}
			}
			try {
				SelFunctions.takeScreenshot(sd);
			} catch (InvalidAutomationParameterException iape) {
				sd.exitGracefully(iape);
			}
		}
	}

	@Then("^I verify that the document is marked as viewed with a check mark$")
	public static void verifyDocCheckMark() {
		WebElement checkMark = null;
		try {
			try {
				checkMark = sd.getElement("checkMark");
			} catch (InvalidAutomationParameterException e) {
				sd.exitGracefully(e);
			}
			sd.isVisible(checkMark);
		} catch (ElementNotVisibleException e) {
			e.printStackTrace();
		}
		Dimension dimensions = checkMark.getSize();
		if (dimensions.width > 0 && dimensions.height > 0) {
			Logger.log("Check mark is displayed correctly for document.");
		} else {
			Logger.log("Check mark is not displayed correctly for document.");
		}
	}

	@Then("^I verify that the ([^\"]*) (?:button|link|tab|text box|text field|image|icon|popup|message|error|element) (is|is not) visible$")
	public static void verifyElementVisible(String elmnt, String isn) throws Throwable {

		WebElement element = sd.getElement(elmnt);
		boolean isVis = sd.isVisible(element);
		if (isVis && isn.equalsIgnoreCase("is")) {
			Functions.passStep("" + elmnt + " is visible.");
		} else if (isVis && isn.equalsIgnoreCase("is not")) {
			Functions.failStep("" + elmnt + " is visible.");
		} else if (!isVis && isn.equalsIgnoreCase("is")) {
			Functions.failStep("" + elmnt + " is not visible.");
		} else {
			Functions.passStep("" + elmnt + " is not visible.");
		}
		SelFunctions.takeScreenshot(sd);

	}

	@Then("^I verify the ([^\"]*) (?:button|link|tab|text box|text field|image|icon|popup|message|error|element|field) contains \"([^\"]*)\"$")
	public static void verifyElementContains(String element, String expectedText) throws ElementNotVisibleException {
		Functions.compareText(sd.elementText(element),expectedText);
		try {
			SelFunctions.takeScreenshot(sd);
		} catch (InvalidAutomationParameterException iape) {
			sd.exitGracefully(iape);
		}
	}

	//TODO is this redundant with isVisible()?
	public static boolean isElementPresent(By by) {
		sd.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		try {
			sd.findElement(by);
			return true;
		} catch (NoSuchElementException e) {
			return false;
		} finally {
			sd.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		}
	}

	@Then("^I verify the \"([^\"]*)\" contains$")
	public static void verifyLinkTextMatches(String webElementName, DataTable verbiageList) throws Exception {
		List<String> ExpectedTextToMatch = verbiageList.asList(String.class);
		WebElement webElementLinkText = sd.getElement(webElementName);
		String linkText = webElementLinkText.getAttribute("innerText").trim();
		if (!linkText.contains(ExpectedTextToMatch.get(0))) {
			Functions.failStep("Text does not match for headers");
			Logger.log("Expected: " + ExpectedTextToMatch.get(0));
			Logger.log("Actual: " + linkText);
		}
	}
}
