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
   //code here
   
   private static final String baseURL = 
         "https://www.amazon.com/product-reviews/";
   private static final String baseURL2 = 
         "/ref=cm_cr_pr_top_link_?ie=UTF8&pageNumber=";
   private static final String baseURL3 = 
         "&showViewpoints=0&sortBy=bySubmissionDateDescending";
   
   public Amazon()
   {
      // empty
   }
   
   public String getProductURL(String productID)
   {
      return baseURL + productID + baseURL2;
   }
   
   public String getReviewURL(String url, int page)
   {
      return url + page + baseURL3;
   }
   
   public String parseReviews(String url) throws IOException, InterruptedException
   {
      try
      {
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
         return parseReviews(url);
      }
      catch (SocketTimeoutException e)
      {
         System.out.println("Timeout Exception, reconnecting in 2 sec");
         Thread.sleep(2000);
         return parseReviews(url);
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
   
   public void cleanReviews(File tempReviewFile, String productID) throws IOException
   {
      System.out.println("cleaning up reviews...");
      
      String data = "";
      String filename = productID + " reviews.txt";
      Scanner reviewInput = new Scanner(tempReviewFile);
      
      // burn first line
      reviewInput.nextLine();
      while (reviewInput.hasNext())
      {
         String next = reviewInput.nextLine();
         if (next.contains("<div class=\"reviewText\">"))
         {
            data += "\n";
            next = reviewInput.nextLine();
         }
         data += " " + next;
      }
      reviewInput.close();
      FileWriter reviewFileEdit = new FileWriter(filename);
      reviewFileEdit.write(data);
      reviewFileEdit.close();
      
      System.out.println("...done");
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
