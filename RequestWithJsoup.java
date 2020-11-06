package Scripts;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;


/* This util class parses website information */
public class RequestWithJsoup {
    private static final Properties env;
    private static final String CNAME_ATTRIB = "CNAME";
    private static final String[] CNAME_ATTRIBS = { CNAME_ATTRIB };
    private static String url;
    private String CName = null;
    private String IP = null;
    static {
        env = new Properties();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.dns.DnsContextFactory");
    }



    public String formatURL(String URL) {
        if (URL.startsWith("http://"))
            URL = URL.substring(7);
        if (URL.startsWith("https://"))
            URL = URL.substring(8);
        if (URL.contains("/"))
            URL = URL.substring(0, URL.indexOf("/"));
        if (URL.startsWith("www."))
            URL = URL.substring(4);
        return URL;
    }

    public boolean DNSLookup(String URL) {
        String args = "www." + URL;
        // String args=URL;

        try {
            InetAddress inetAddress;
            // if first character is a digit then assume is an address
            if (Character.isDigit(args.charAt(0))) { // convert address from
                // string representation
                // to byte array
                byte[] b = new byte[4];
                String[] bytes = args.split("[.]");
                for (int i = 0; i < bytes.length; i++) {
                    b[i] = new Integer(bytes[i]).byteValue();
                }
                // get Internet Address of this host address
                inetAddress = InetAddress.getByAddress(b);
            } else { // get Internet Address of this host name
                inetAddress = InetAddress.getByName(args);
            }

            this.IP = inetAddress.getHostAddress();
            // this.CName=inetAddress.getCanonicalHostName();
            this.CName = getCNAME(args);

            if (this.CName != null) {
                if (this.CName.endsWith(".")) {
                    this.CName = this.CName.substring(0, this.CName.length() - 1);
                }
            }

        } catch (UnknownHostException exception) {
            System.out.println("ERROR: No Internet Address for '" + args + "'");
            return false;
        } catch (NamingException exception) {
            System.out.println("ERROR: No DNS record for '" + args + "'");
        }
        return true;

    }

    public static String getCNAME(String host) throws NamingException {
        return getCNAME(new InitialDirContext(env), host);
    }

    private static String getCNAME(InitialDirContext idc, String host) throws NamingException {
        String cname = host;
        Attributes attrs = idc.getAttributes(host, CNAME_ATTRIBS);
        Attribute attr = attrs.get(CNAME_ATTRIB);
        String name;
        try {
            name = (String) attr.get(0);
        } catch (Exception ex) {
            return null;
        }

        return name;
    }

    /**
     * This function returns given website's status <br>
     * if returns true  => Website live <br>
     * if returns false => Website down <br>
     */
    public boolean isWebsiteLive(String website) {

        System.setProperty("http.maxRedirects", "700");
        System.setProperty("http.protocol.allow-circular-redirects", "true");

        // If no url were provided
        if(website == null) {
            return false;
        }
        else {
            if(!website.contains("http://") && !website.contains("https://")) {
                website = "http://"+website;
            }
        }

        try{
            String formattedUrl = website;//StringUtils.NormalizeUrl(StringUtils.extractUrls(website).get(0));
            URL url = new URL(formattedUrl);

            HttpURLConnection connection = openConnection(url);
            String expandedURL = connection.getHeaderField("Location");

            /* BD 2019-08-22 */
            if(expandedURL != null && expandedURL.contains(".k12.tr")) {
                return true; // We are not going to check this address because VPN can't access their site. That's why we assume it's live...
            }
            /* BD 2019-08-22 */

            int responseCode = connection.getResponseCode();
            System.out.println(website + " " + responseCode);
            connection.disconnect();

			/*List<String> extractedUrlList = StringUtils.ExtractDomains(expandedURL);
			if(extractedUrlList.size() > 0){

				// true => URL contain blackListed domain
				// false => URL doesn't contain blackListed domain
				boolean isHtmlContainBlackListDomain = isHtmlContainBlackListDomain(connection);

				connection.getInputStream().close();

				// If HTML doesn't contain blackListedDomain then move to the next control, if contains blacklisted domain then return false
				if(isHtmlContainBlackListDomain) {
					return false;
				}
			}*/

            // Response code >= 200 && < 300 means that success
            if(responseCode >= 200 && responseCode < 300) {
                boolean isHtmlContainBlackListDomain = isHtmlContainBlackListDomain(connection);

                connection.getInputStream().close();



                String webSiteContent="";
                webSiteContent = getLandingPageHashAndPageContent(website);
                if(webSiteContent !=null) {
                    if(isPageContentContainsBlacklistedPhrases(webSiteContent)) {
                        return false;
                    }
                }
				
				
				/*if(isHtmlContainBlackListDomain) {
					return false;
				}
				else {
					return true;
				}*/


            }

            // Response code >= 300 && < 400 means that redirection occured
            //if(responseCode >= 300 && responseCode < 400){

            //HttpURLConnection con = openConnection(url);
				/*List<String> extractedUrlList2 = StringUtils.ExtractDomains(con.getURL().toString());

				if(extractedUrlList2.size() > 0){

					boolean isHtmlContainBlackListDomain = isHtmlContainBlackListDomain(con);

					if(isHtmlContainBlackListDomain) {
						return false;
					}
					else {
						String webSiteContent = getLandingPageHashAndPageContent(website);
						if(webSiteContent !=null) {
							if(isPageContentContainsBlacklistedPhrases(webSiteContent)) {
								return false;
							}
							else {
								return true;
							}
						}
					}
				}
			}*/

            // Response code >= 400 && < 600 means that client error occured
            if(responseCode >= 400 && responseCode < 600){
                return false;
            }

            return false;
        }catch(Exception e){
            return false;
        }
    }

    /* This function creates and returns new HttpURLConnection for given URL */
    private HttpURLConnection openConnection(URL url) {

        try {

            HttpURLConnection connection = (HttpURLConnection) url.openConnection(); //using proxy may increase latency
            connection.setInstanceFollowRedirects(true);
            connection.setConnectTimeout(90 * 1000);
            connection.setReadTimeout(90 * 1000);
            connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/36.0.1985.125 Safari/537.36");

            URLConnection conn = url.openConnection();
            String redirect = conn.getHeaderField("Location");
            if (redirect != null){
                connection = (HttpURLConnection) new URL(redirect).openConnection();
            }
            connection.connect();
            return connection;
        } catch (IOException e) {

        }

        return null;
    }

    /**
     * 	This function checks page's source if it contains blackListed url's <br>
     *	If returns true  => Page source contain blacklisted domains <br>
     *	If returns false => Page source doesn't contain blacklisted domains <br>
     */
    private boolean isHtmlContainBlackListDomain(URLConnection con) {

        List<String> blackListDomainSites = new ArrayList<String>();
        blackListDomainSites.add("doruk.net");
        blackListDomainSites.add("natro.com");
        blackListDomainSites.add("domainborsasi.com");
        blackListDomainSites.add("daha.net");
        blackListDomainSites.add("tasarimrehberi.com");
        blackListDomainSites.add("hugedomains.com");

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                for(String blackListDomain: blackListDomainSites) {
                    if(inputLine.contains(blackListDomain)) {
                        return true;
                    }
                }
            }

            in.close();
        }
        catch(Exception e) {

        }

        return false;
    }

    /* This function checks page content for text like "Under construction, Account suspended" etc. */
    private boolean isPageContentContainsBlacklistedPhrases(String pageContent) {

        /* Too long content probably contains black listed phrases */
        if(pageContent.length() > 1000) {
            return false;
        }
        if(pageContent.length() <30) {
            return true;
        }
        return pageContent.toLowerCase().contains("under construction")
                || pageContent.toLowerCase().contains("account has been suspended")
                || pageContent.toLowerCase().contains("account suspended")
                || pageContent.toLowerCase().contains("buy this domain")
                || pageContent.toLowerCase().contains("no website at this address")
                || pageContent.toLowerCase().contains("it is possible you have reached this page because")
                || pageContent.toLowerCase().contains("sitemiz bakýmdadýr")
                || pageContent.toLowerCase().contains("sitemiz bakým aþamasýnda")
                || pageContent.toLowerCase().contains("sitemiz bakýma girmiþtir")
                || pageContent.toLowerCase().contains("sayfa bulunamadý")
                || pageContent.toLowerCase().contains("Talk to a domain expert")
                || pageContent.toLowerCase().contains("There has been a critical error on your website")
                || pageContent.toLowerCase().contains("the protection measure has been taken for this website");
    }


    /** This function returns given url's page source's hashed version and page content to check if it's really live.
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException **/
    private String getLandingPageHashAndPageContent(String websiteUrl) throws KeyManagementException, NoSuchAlgorithmException {

        String pageContent = null;
        String website = websiteUrl;
        if(website.equals("") || website == null) {
            return "";
        }

        try {
            // To get the 307 redirection errors
            Document document = null;

            Connection.Response res = Jsoup
                    .connect(website)
                    .timeout(120000)
                    .ignoreHttpErrors(true)
                    .followRedirects(true)

                    .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
                    .execute();

            if (res.statusCode() == 307) {
                String sNewUrl = res.header("Location");
                if (sNewUrl != null && sNewUrl.length() > 7) {
                    website = sNewUrl;
                }

            }

            document = Jsoup.connect(website)
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                    .referrer("http://www.google.com")
                    .timeout(120000)
                    .followRedirects(true)
                    .ignoreHttpErrors(true)
                    .get();

            boolean frameError = false;
            try {
                if(document.body() == null) {
                    document = Jsoup.connect(document.select("frame[name=main]").attr("src")).get();
                }
            } catch (Exception e) {
                frameError=true;
            }
            //check meta tags
            //  <meta http-equiv="refresh" content="0; url=http://crededata.com/" />
            try {
                if(document.body() == null || document.body().text()==null || document.body().text().equals("")) {
                    URI uri = URI.create(website);

                    for (Element refresh : document.select("html head meta[http-equiv=refresh]")) {

                        Matcher m = Pattern.compile("(?si)\\d+;\\s*url=(.+)|\\d+")
                                .matcher(refresh.attr("content"));

                        // find the first one that is valid
                        if (m.matches()) {
                            if (m.group(1) != null)
                                document= Jsoup.connect(uri.resolve(m.group(1)).toString()).get();
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                //frameError=true;
            }

            //check frame again with full url
            if(frameError) {
                try {
                    if(document.body() == null) {
                        document = Jsoup.connect(website+"/"+document.select("frame[name=main]").attr("src")).get();
                    }
                } catch (Exception e) {
                    frameError=true;
                }
            }


            if(document.body() != null) {
                pageContent = document.body().text();
            }
            else {
                try {
                    pageContent = document.html();
                } catch (Exception e) {
                }
            }

        } catch (IOException e) {

        }

        return pageContent;
    }


}