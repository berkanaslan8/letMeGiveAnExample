package Scripts;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;


public class RequestWithSelenium {
	WebDriver driver;
	public String bodyCheck;
	private List<String> webSites;
	public List<String> errorMsgs;
	public List<String> errorSites;
	public WebDriverWait waitBody;
	public String body;
	public Logger logger = Logger.getLogger(RequestWithSelenium.class);
	
	
	public RequestWithSelenium() {
			
		webSites = new ArrayList<String>();
		errorMsgs = new ArrayList<String>();
		errorSites = new ArrayList<String>();
		
		System.setProperty("webdriver.chrome.driver", "C:\\chromedriver.exe");
		ChromeOptions options = new ChromeOptions();
		options.addArguments("headless");
		options.addArguments("window-size=1024x768");
		driver = new ChromeDriver(options);
		waitBody = new WebDriverWait(driver,20);
		
		

        webSites.add("http://abcozelegitim.com");
        webSites.add("https://www.ml.com.tr/");
        webSites.add("https://satis.mpaket.com/?sayfa=anasayfa");
        webSites.add("https://www.erzstore.com/");
        webSites.add("https://www.onlinebilisim.net/");
        webSites.add("https://www.profdrorhansen.com/");
        webSites.add("http://www.akmerbilisim.com/");
        webSites.add("https://www.toptanhafizakarti.net/");
        webSites.add("https://www.namotto.net/kullanici/Giris.aspx");
        webSites.add("https://www.guncelyazilim.com.tr/");
        webSites.add("https://www.syroxburada.com/");
        webSites.add("https://www.karaelmassurucukursu.com/");
        webSites.add("https://app.kendinyonet.com/Login.aspx?ReturnUrl=%2f");
        webSites.add("https://www.vizvize.com/");
        webSites.add("https://www.b2bozsangrup.com/");
        webSites.add("https://www.elsisteknoloji.com/");
        webSites.add("http://monemel.com/");
        webSites.add("https://www.dumlupinarsurucukursu.com.tr/");
        webSites.add("https://mtcbaby.com/");
        webSites.add("https://www.satrik.com/");
        
       
        errorMsgs.add("Page not found");
        errorMsgs.add("404 Not Found");
        errorMsgs.add("Something went wrong");
        errorMsgs.add("sitemiz bak�m a�amas�ndad�r");
        errorMsgs.add("bad request");
        errorMsgs.add("Internal Server Error");
        errorMsgs.add("Gateway Timeout");
        errorMsgs.add("buy this domain");
        errorMsgs.add("Run Time Error");
        errorMsgs.add("403 Forbidden");
        errorMsgs.add("Sayfa bulunamad�");
        errorMsgs.add("There has been a critical error on your website");
        errorMsgs.add("the protection measure has been taken for this website");
        
	}
	
	public void checkBody() {
		for(String siteFinder: webSites) {
			driver.get(siteFinder);
			System.out.println("Selenium has visited " + siteFinder + " succesfully.");	
			try {
				waitBody.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body")));
				bodyCheck = driver.findElement(By.tagName("body")).getText();

			} catch (Exception e) {
				e.printStackTrace();
				logger.error("Body of site is not reachable: " + siteFinder);
			}
			
			
			for(String errFinder: errorMsgs) {
				if(bodyCheck.contains(errFinder)) {
					errorSites.add(siteFinder);
				}					
		}
		}
		driver.close();
		if(errorSites.isEmpty()) {
			logger.info("There is no down site has found.");
		} else {
			logger.info("Down site(s) list has shown below.");
			int listMeter = 1;
			for(String downSites: errorSites) {
				logger.error(listMeter + "th site: " + downSites);
				listMeter++;
			}
		}
	}
}

