package selenium;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class Level_01_Register_Login {
	String projectPath = System.getProperty("user.dir");
	WebDriver driver;
	Select select;
	String firstName, lastName, email, companyName, password;

	@Parameters({ "browser", "ipAddress", "port" })
	@BeforeClass
	public void beforeClass(String browserName, String ipAddress, String portNumber) {
		DesiredCapabilities capability = null;
		switch (browserName) {
		case "firefox":
			System.setProperty("webdriver.gecko.driver", projectPath + "\\browserDrivers\\geckodriver.exe");
			File pathBinary = new File("C:\\Program Files\\Mozilla Firefox\\firefox.exe");
			FirefoxBinary firefoxBinary = new FirefoxBinary(pathBinary);   
			FirefoxOptions options = new FirefoxOptions();
			
			capability = DesiredCapabilities.firefox();
			capability.setBrowserName("firefox");
			capability.setPlatform(Platform.WINDOWS);
			capability.setCapability(FirefoxOptions.FIREFOX_OPTIONS, options.setBinary(firefoxBinary));
			options.merge(capability);
			break;
		case "chrome":
			System.setProperty("webdriver.chrome.driver", projectPath + "\\browserDrivers\\chromedriver.exe");
			capability = DesiredCapabilities.chrome();
			capability.setBrowserName("chrome");
			capability.setPlatform(Platform.WINDOWS);

			ChromeOptions cOptions = new ChromeOptions();
			cOptions.merge(capability);
			break;
		default:
			throw new RuntimeException("Browser is not valid!");
		}

		try {
			driver = new RemoteWebDriver(new URL(String.format("http://%s:%s/wd/hub", ipAddress, portNumber)),
					capability);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
		driver.manage().window().maximize();

		driver.get("https://demo.nopcommerce.com/");

		firstName = "Automation";
		lastName = "FC";
		email = "automation" + getRandomNumber() + "@us.com";
		companyName = "Tony Buoi Sang";
		password = "tonybuoisang";
	}

	@Test
	public void TC_01_Register() {
		driver.findElement(By.className("ico-register")).click();
		driver.findElement(By.id("gender-male")).click();
		sleepInSecond(2);

		driver.findElement(By.id("FirstName")).sendKeys(firstName);
		driver.findElement(By.id("LastName")).sendKeys(lastName);

		select = new Select(driver.findElement(By.name("DateOfBirthDay")));
		select.selectByVisibleText("10");

		select = new Select(driver.findElement(By.name("DateOfBirthMonth")));
		select.selectByVisibleText("August");

		select = new Select(driver.findElement(By.name("DateOfBirthYear")));
		select.selectByVisibleText("1960");

		driver.findElement(By.id("Email")).sendKeys(email);
		driver.findElement(By.id("Company")).sendKeys(companyName);
		driver.findElement(By.id("Password")).sendKeys(password);
		driver.findElement(By.id("ConfirmPassword")).sendKeys(password);

		driver.findElement(By.id("register-button")).click();
		sleepInSecond(1);

		Assert.assertEquals(driver
				.findElement(By.xpath("//div[@class='page registration-result-page']//div[@class='result']")).getText(),
				"Your registration completed");

		driver.findElement(By.className("ico-logout")).click();
		sleepInSecond(1);
	}

	@Test
	public void TC_02_Login() {
		driver.findElement(By.className("ico-login")).click();
		sleepInSecond(1);

		driver.findElement(By.id("Email")).sendKeys(email);
		driver.findElement(By.id("Password")).sendKeys(password);
		driver.findElement(By.cssSelector(".login-button")).click();
		sleepInSecond(1);

		Assert.assertTrue(driver.findElement(By.className("ico-account")).isDisplayed());
		Assert.assertTrue(driver.findElement(By.className("ico-logout")).isDisplayed());
	}

	public int getRandomNumber() {
		Random rand = new Random();
		return rand.nextInt(999999);
	}

	public void sleepInSecond(long time) {
		try {
			Thread.sleep(time * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@AfterClass
	public void afterClass() {
		driver.quit();
	}

}