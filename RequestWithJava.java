package Scripts;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

public class RequestWithJava {

	private static List<String> webSites;
	private static Logger logger;

	public static void checkSites() {
		webSites = new ArrayList<String>();
		
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
		

		for (String link : webSites) {
			try {
				if (!link.startsWith("http")) {
					link = "http://" + link;
				}

				int statusCode = getStatusCode(link);

				if (statusCode == 403) { // If HTTP status code returns 403, this block of code will check the site 3
											// times. It makes sleep to thread 10 seconds, 15 seconds and 100 seconds
											// stepwise.
					int tryAgainLater = 1;
					Thread.sleep(10000);
					while (tryAgainLater <= 3) {
						if (tryAgainLater == 2) {
							logger.info("Second time program tries to get response code");
							Thread.sleep(15000);
						}
						if (tryAgainLater == 3) {
							logger.info("Third time program tries to get response code");
							Thread.sleep(100000);
						}
						if (tryAgainLater == 3) {
							logger.info("403 HTTP code hasn't broken.");
						}
						try {
							if (!link.startsWith("http")) {
								link = "http://" + link;
							}
							statusCode = getStatusCode(link);
							logger.info(link + " " + statusCode);

						} catch (Exception e) {
							logger.error(link + " " + e.getMessage());
						}

						if (statusCode != 403) {
							break;
						}

						++tryAgainLater;
					}
				}

				logger.info(link + " " + statusCode);
			} catch (Exception e) {
				logger.error(link + " " + e.getMessage());
			}
		}

	}

	public static int getStatusCode(String link) throws Exception {
		URL url = new URL(link);
		HttpURLConnection.setFollowRedirects(true);
		HttpURLConnection http = (HttpURLConnection) url.openConnection();
		http.setRequestMethod("HEAD");
		http.setConnectTimeout(10000);
		http.setReadTimeout(10000);
		int statusCode = http.getResponseCode();
		http.disconnect();

		return statusCode;

	}

}
