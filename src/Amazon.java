import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;




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
      
   }
   
   public String getProductURL(String productID)
   {
      return baseURL + productID + baseURL2;
   }
   
   public String getReviewURL(String url, int page)
   {
      return url + page + baseURL3;
   }
   
   public String parseReviews(String url) throws IOException
   {
      try
      {
      Document doc = Jsoup.connect(url).ignoreContentType(true).get();
      String output = doc.select("[class=\"reviewText\"]").toString();
      output = output.replaceAll("<br>","");
      output = output.replaceAll("</div>","");
      output = output.replaceAll("\n","");
      return output;
      }
      catch (HttpStatusException e)
      {
         System.out.println(url);
         e.printStackTrace();
         return "";
      }
      catch (Exception e)
      {
         e.printStackTrace();
         return "";
      }
   }
   
   public String parseRatings(String url) throws IOException
   {
      try
      {
      Document doc = Jsoup.connect(url).ignoreContentType(true).get();
      return doc.select("[class^=swSprite").toString();
      
      }
      catch (HttpStatusException e)
      {
         System.out.println(url);
         e.printStackTrace();
         return "";
      }
      catch (Exception e)
      {
         e.printStackTrace();
         return "";
      }
   }
}
