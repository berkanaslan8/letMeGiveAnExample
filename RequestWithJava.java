import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class RequestWithJava {
	
		
        public static void checkSites() {

            try {
                File links = new File("./linkler.txt");
                Scanner scan = new Scanner(links);
                ArrayList<String> list = new ArrayList<>();
                while (scan.hasNext()) {
                    list.add(scan.nextLine());
                }
                File linkStatus = new File("LinkStatus.txt");
                if (!linkStatus.exists()) {
                    linkStatus.createNewFile();
                } else {
                    System.out.println("File already exists");
                }
                BufferedWriter writer = new BufferedWriter(new FileWriter(linkStatus));
                for (String link : list) {
                    try {
                        if (!link.startsWith("http")) {
                            link = "http://" + link;
                        }
                        URL url = new URL(link);  
                        HttpURLConnection.setFollowRedirects(true);
                        HttpURLConnection http = (HttpURLConnection) url.openConnection();
                        http.setRequestMethod("HEAD");
                        http.setConnectTimeout(10000);
                        http.setReadTimeout(10000);
                        int statusCode = http.getResponseCode();

                        if (statusCode == 403) { // If HTTP status code returns 403, this block of code will check the site 3 times. It makes sleep to thread 10 seconds, 15 seconds and 100 seconds stepwise.
                            Thread.sleep(10000);
                            int tryAgainLater = 1;
                            while (tryAgainLater <= 3) {
                                try {
                                    if (!link.startsWith("http")) {
                                        link = "http://" + link;
                                    }
                                    url = new URL(link);
                                    HttpURLConnection.setFollowRedirects(true);
                                    http = (HttpURLConnection) url.openConnection();
                                    http.setRequestMethod("HEAD");
                                    http.setConnectTimeout(5000);
                                    http.setReadTimeout(8000);
                                    statusCode = http.getResponseCode();
                                    http.disconnect();
                                    System.out.println(link + " " + statusCode);
                                    writer.write(link + " " + statusCode);
                                    writer.newLine();
                                } catch (Exception e) {
                                    writer.write(link + " " + e.getMessage());
                                    writer.newLine();

                                    System.out.println(link + " " + e.getMessage());
                                }
                                if (tryAgainLater == 1) {
                                    System.out.println("Second time program tries to get response code");
                                    Thread.sleep(15000);
                                }
                                if (tryAgainLater == 2) {
                                    System.out.println("Third time program tries to get response code");
                                    Thread.sleep(100000);
                                }
                                if (tryAgainLater == 3) {
                                    System.out.println("403 HTTP code hasn't broken.");
                                }
                                ++tryAgainLater;
                            }
                        }

                        http.disconnect();  // It helps to increasing speed of code.
                        System.out.println(link + " " + statusCode);
                        writer.write(link + " " + statusCode);
                        writer.newLine();
                    } catch (Exception e) {
                        writer.write(link + " " + e.getMessage());
                        writer.newLine();

                        System.out.println(link + " " + e.getMessage());
                    }
                }
                try {
                    writer.close();

                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }


            } catch (Exception e) {
                System.out.println(e.getMessage());
            }


        }

}
