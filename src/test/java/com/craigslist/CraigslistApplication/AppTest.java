package com.craigslist.CraigslistApplication;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.BeforeTest;

/*Questions
why java.util and awt few times works only utils not awt why?
why test annotation not recognized.
*/

// 
public class AppTest {

	WebDriver driver;
	String URL;

	@BeforeTest
	public void setup() {
		System.setProperty("webdriver.chrome.driver", "C:\\Utils\\ChromeDriver\\chromedriver.exe");
		driver = new ChromeDriver();
		URL = "https://sfbay.craigslist.org";
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

	}

	@org.testng.annotations.Test
	public void navigateApp() throws InterruptedException, IOException {
		String searchTerm = "auto";
		String loginURL = "https://accounts.craigslist.org/login";
		driver.get(loginURL);
		loginCreds("neelima.chennavaram@gmail.com", "Ansh123$");
		// driver.findElement(By.xpath("//a[text()='craigslist'])")).click();
		driver.navigate().to(URL);
		String parentHandle = driver.getWindowHandle();
		WebElement searchBox = driver.findElement(By.id("query"));
		searchBox.sendKeys(searchTerm);
		searchBox.sendKeys(Keys.ENTER);
		Set<String> handles = driver.getWindowHandles();
		for (String handle : handles) {
			if (!handle.equals(parentHandle)) {
				driver.switchTo().window(handle);
			}
		}

		String Count = driver.findElement(By.cssSelector("span.totalcount")).getText();
		int totalCount = Integer.parseInt(Count);
		System.out.println(totalCount);

		List<WebElement> allAutoSearchImages = driver.findElements(By.cssSelector("li.result-row"));

		int pageCount = allAutoSearchImages.size();
		int noOfPages = totalCount / pageCount;

		if (totalCount % pageCount > 0) {
			noOfPages += 1;
		}

		printSpecificPage(2, noOfPages, totalCount, allAutoSearchImages);

		driver.findElement(By.cssSelector("a.saveme")).click();
		verifyAutomsg(searchTerm);
		String subId = driver.findElement(By.name("subID")).getAttribute("value");
		String subName = driver.findElement(By.name("subName")).getAttribute("value");

		APITest test = new APITest();
		test.deleteSearch(subId, subName);

		driver.navigate().to("https://accounts.craigslist.org/logout");
	}

	private void printSpecificPage(int specificPageNum, int noOfPages, int totalCount,
			List<WebElement> allAutoSearchImages) {
		
		if(specificPageNum > noOfPages)
			System.out.println("Invalid page number or insufficient no. of result pages");
		
		for (int i = 0; i < totalCount; i++) {
			
			if(i+1 != specificPageNum)
				continue;
			
			System.out.println("printing the resultes from page: " + (i+1)); 
			allAutoSearchImages = driver.findElements(By.cssSelector("li.result-row"));
			for (WebElement allAutoSearchImage : allAutoSearchImages) {

				String adTitle = allAutoSearchImage.findElement(By.cssSelector("a.result-title.hdrlnk")).getText();

				System.out.println("Title " + adTitle);
				System.out.println("Total count of each page ads " + allAutoSearchImages.size());

			}
			WebElement nextPage = driver.findElement(By.cssSelector("a.button.next"));
			if (nextPage.isDisplayed()) {
				nextPage.click();
				try {
					Thread.sleep(15);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private void verifyAutomsg(String searchTerm) {
		String savedmsg = driver.findElement(By.cssSelector("div.alert.alert-md.alert-success.saved-searches-alert"))
				.getText();
		if (savedmsg.equals("Your \"" + searchTerm + "\" search has been saved.")) {
			System.out.println("Verified the Saved Search");
		}

	}

	private void loginCreds(String UserId, String Password) {
		WebElement Email = driver.findElement(By.cssSelector("input#inputEmailHandle"));
		Email.sendKeys(UserId);
		WebElement Password1 = driver.findElement(By.cssSelector("input#inputPassword"));
		Password1.sendKeys(Password);
		WebElement login = driver.findElement(By.xpath("//button[@type='submit']"));
		login.click();

	}

}
