import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject;

public class JSON2Text 
{
	public static void main(String [] args) throws Exception
	{
		String line;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy");
		
		System.out.println("status_id\tuser_id\tcontent\tgmt_time\tgeo\tplace_id");
		
		while((line=br.readLine())!=null)
		{
			try
			{
				JSONObject js_tweet = new JSONObject(line);
				long status_id = js_tweet.getLong("id");
				JSONObject js_user = js_tweet.getJSONObject("user");
				long user_id = js_user.getLong("id");
				String content = js_tweet.getString("text");
				content = content.replaceAll("[\t\n\r\f]+", " ");
				content = content.replaceAll("[^ a-zA-Z0-9\\[\\]\\;',./`=\\-~!@#$%\\^&*()_+{}|:\"<>?]", "");
				
				String date = js_tweet.getString("created_at");
				Date date2 = sdf.parse(date);
			
				String geo = js_tweet.isNull("geo")     ? "null" : js_tweet.getString("geo");
				String place = js_tweet.isNull("place") ? "null" : js_tweet.getString("place");
			
				Timestamp ts = new Timestamp(date2.getTime());
			
				System.out.println(String.format("%d\t%d\t%s\t%s\t%s\t%s", status_id, user_id, content, ts.toString(), geo, place));
			}
			catch (Exception e) {}
		}
		br.close();
	}
}
