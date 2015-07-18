package akiniyalocts.imgurapiexample;

/**
 * Created by AKiniyalocts on 2/23/15.
 */
public class Constants {
  /*
    Logging flag
   */
  public static final boolean LOGGING = true;

  /*
    Your imgur client id. You need this to upload to imgur.

    More here: https://api.imgur.com/
   */
  public static final String MY_IMGUR_CLIENT_ID = "d94651365f7971a";
  public static final String MY_IMGUR_CLIENT_SECRET = "e7fa4ffc0cfbb942d680b4ee103e81da6555792f";

  /*
    Redirect URL for android.
   */
  public static final String MY_IMGUR_REDIRECT_URL = "http://android";

  /*
    Client Auth
   */
  public static String getClientAuth(){
    return "Client-ID " + MY_IMGUR_CLIENT_ID;
  }

}
