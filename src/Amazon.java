import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;
import java.net.SocketTimeoutException;


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
}
