import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.HashSet;

import org.json.JSONObject;


public class JSONSearch 
{
	public static void main(String [] args) throws Exception
	{
		HashSet<String> all_users = new HashSet<String>();
		String line;
		
		BufferedReader br = new BufferedReader(new FileReader("josh_users/list.txt"));
		while((line=br.readLine())!=null)
		{
			all_users.add(line.toLowerCase());
		}
		br.close();
		
		br = new BufferedReader(new InputStreamReader(System.in));
		while((line=br.readLine())!=null)
		{
			try
			{
				JSONObject js_tweet = new JSONObject(line);
				JSONObject js_user = js_tweet.getJSONObject("user");
				String screen_name = js_user.getString("screen_name").toLowerCase();
				if(all_users.contains(screen_name))
				{
					System.out.println(line);
				}
			}
			catch (Exception e) {}
		}
		br.close();
	}
}
