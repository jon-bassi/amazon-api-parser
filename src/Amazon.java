import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.FileWriter;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Scanner;
import java.io.File;


/**
 * TODO:
 * try to combine the reviews and ratings into one method, also, add in 
 * the dates the reviews were written
 * 
 * @author jon-bassi
 */

public class Amazon
{
   // product data
   private static final String productHomeURL =
         "https://www.amazon.com/dp/";
   
   // reviews
   private static final String baseURL = 
         "https://www.amazon.com/product-reviews/";
   private static final String baseURL2 = 
         "/ref=cm_cr_pr_top_link_?ie=UTF8&pageNumber=";
   private static final String baseURL3 = 
         "&showViewpoints=0&sortBy=bySubmissionDateDescending";
   
   private int trycount;
   
   public Amazon()
   {
      trycount = 0;
   }
   
   public String getProductURL(String productID)
   {
      return baseURL + productID + baseURL2;
   }
   
   public String getReviewURL(String productID, int page)
   {
      return baseURL + productID + baseURL2 + page + baseURL3;
   }
   
   public String parseProductInfo(String productID) throws IOException, InterruptedException
   {
      if (++trycount > 4)
      {
         System.out.println("Maximum number of try's reached, skipping " + productID);
         return "";
      }
      String url = productHomeURL + productID;
      try {
         
         
         Document doc = Jsoup.connect(url).ignoreContentType(true).get();
         String output = "";
         
         // productinfo
         String[] piarr = doc.select("[id=\"feature-bullets\"] .a-list-item").toString().split("\n");
         
         for (int i = 0; i < piarr.length; i++)
         {
            String s = piarr[i];
            s = s.trim().replaceAll("<span class=\"a-list-item\">", "");
            s = s.replaceAll("</span>", "");
            s = s.replaceAll("[,;'\"?()]", "");
            if (!output.contains(s))
               output += s + "\n";
         }
         
         // technical details
         String[] tdarr = doc.select("#technical-data .content li").toString().split("\n");
         for (int i = 0; i < tdarr.length; i++)
         {
            String s = tdarr[i];
            s = s.trim().replaceAll("<li>", "");
            s = s.replaceAll("<b>", "");
            s = s.replaceAll("</b>", "");
            s = s.replaceAll("</li>", "");
            s = s.replaceAll("[,;'\"?()]", "");
            if (!output.contains(s))
               output += s + "\n";
         }
         
         // price
         String price = doc.select("[id=\"priceblock_ourprice\"]").toString();
         price = price.replace(("<span id=\"priceblock_ourprice\" class=\"a-size-medium a-color-price\">"), "");
         price = price.replace("</span>", "");
         output += "Price: " + price;
         trycount = 0;
         return output;
      }
      catch (HttpStatusException e)
      {
         System.out.println("count: " + trycount);
         System.out.println("Error connecting to " + url + ", reconnecting in 2 sec");
         Thread.sleep(2000);
         return parseProductInfo(productID);
      }
      catch (SocketTimeoutException e)
      {
         System.out.println("Timeout Exception, reconnecting in 2 sec");
         Thread.sleep(2000);
         return parseProductInfo(productID);
      }
      catch (Exception e)
      {
         e.printStackTrace();
         return "";
      }
   }
   
   public String parseReviews(String productID, int page) throws IOException, InterruptedException
   {
      try
      {
         String url = getReviewURL(productID,page);
         Document doc = Jsoup.connect(url).ignoreContentType(true).get();
         String output = doc.select("[class=\"reviewText\"]").toString();
         output = output.replaceAll("<br>","");
         output = output.replaceAll("</div>","");
         return output;
      }
      catch (HttpStatusException e)
      {
         System.out.println("HTTP Exception, reconnecting in 2 sec");
         Thread.sleep(2000);
         return parseReviews(productID,page);
      }
      catch (SocketTimeoutException e)
      {
         System.out.println("Timeout Exception, reconnecting in 2 sec");
         Thread.sleep(2000);
         return parseReviews(productID,page);
      }
      catch (Exception e)
      {
         e.printStackTrace();
         return "";
      }
   }
   
   public String parseRatings(String url) throws IOException, InterruptedException
   {
      try
      {
      Document doc = Jsoup.connect(url).ignoreContentType(true).get();
      return doc.select("[class^=swSprite").toString();
      
      }
      catch (HttpStatusException e)
      {
         System.out.println("HTTP Exception, reconnecting in 2 sec");
         Thread.sleep(2000);
         return parseRatings(url);
      }
      catch (SocketTimeoutException e)
      {
         System.out.println("Timeout Exception, reconnecting in 2 sec");
         Thread.sleep(2000);
         return parseRatings(url);
      }
      catch (Exception e)
      {
         e.printStackTrace();
         return "";
      }
   }
   
   public String cleanReviews(String reviews) throws IOException
   {
      System.out.println("cleaning up reviews...");
      
      String data = "";
      String[] reviewarr = reviews.split("\n");
      
      // burn first line
      for (int i = 1; i < reviewarr.length; i++)
      {
         String next = reviewarr[i];
         if (next.contains("<div class=\"reviewText\">"))
         {
            data += "\n";
            next = reviewarr[++i];
         }
         data += "" + next.trim();
      }
      
      System.out.println("...done");
      return data;
   }
   
   public void cleanRatings(File tempRatingsFile, String productID) throws IOException
   {
      System.out.println("cleaning up ratings...");
      
      String filename = productID + " stars.txt";
      Scanner starInput = new Scanner(tempRatingsFile);
      String data = "";
      
      // burn first line
      starInput.nextLine();
      while (starInput.hasNext())
      {
         for (int i = 0; i < 2; i++)
         {
            starInput.nextLine();
            if (!starInput.hasNext())
               break;
         }
         if (!starInput.hasNext())
            break;
         String next = starInput.nextLine();
         while(!next.contains("<input type=\"image\""))
         {
            data +=next.charAt(29) + "\n";
            starInput.nextLine();
            next = starInput.nextLine();
         }
      }
      starInput.close();
      FileWriter starFileEdit = new FileWriter(filename);
      starFileEdit.write(data);
      starFileEdit.close();
      
      System.out.println("...done");
   }
}
