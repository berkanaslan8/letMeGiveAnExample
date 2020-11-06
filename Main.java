package Scripts;
import java.io.IOException;
import java.util.*;

public class Main {
	


    public static void main(String[] args) throws IOException {
    	
    	
        final List<String> webSites = new ArrayList<String>();
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
        Timer myTimer = new Timer();
        Scanner checkingMethod = new Scanner(System.in);

        System.out.print("Select your data for fetching status: (1-Java, 2-Jsoup, 3-Selenium) ");
        int userInput = checkingMethod.nextInt();

        if(userInput == 1) {

            TimerTask sendingRequest = new TimerTask() {

            	int loopTeller = 1;
            	RequestWithJava requestWithJava = new RequestWithJava();
                public void run() {
                    RequestWithJava.checkSites();
                    System.out.println("Loop has ended " + loopTeller + " time(s).");
                    loopTeller++;
                }
            };
            myTimer.schedule(sendingRequest, 0, 360 * 1000);   
        } else if (userInput == 2) {
            TimerTask jsoupTimer = new TimerTask() {
            	int loopTeller = 1;
                public void run() {
                    RequestWithJsoup requestWithJsoup = new RequestWithJsoup();
                    for (String list : webSites) {
                        requestWithJsoup.isWebsiteLive(list);
                    }
                    System.out.println("Loop has ended " + loopTeller + " time(s).");
                    loopTeller++;
                } 
            }; myTimer.schedule(jsoupTimer,0,360 * 1000);
    }
        else if (userInput == 3) {
        	TimerTask seleniumTimer = new TimerTask() {
				
				int loopTeller = 1;
				public void run() {
					RequestWithSelenium reqSel = new RequestWithSelenium();
		        	reqSel.checkBody();
		        	System.out.println("Loop has ended " + loopTeller + " time(s).");
                    loopTeller++;
				}
			}; myTimer.schedule(seleniumTimer,0,360 * 1000);       	
        }
        checkingMethod.close();
}

}