import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;




public class Amazon
{
   //code here
   
   private static final String baseURL = 
         "https//www.amazpn.com/product-reviews/";
   private static final String baseURL2 = 
         "ref=cm_cr_pr_top_link_?ie=UTF8&pageNumber=";
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
   
   public String parseURL(String url) throws IOException
   {
      Document doc = Jsoup.connect(url).get();
      return doc.toString();
   }
   
}
