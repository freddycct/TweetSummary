import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.HashSet;

public class Search 
{
	public static boolean ContainsQuery(String content, String [] qwords)
	{
		//content is not in lower case!
		//test whether the tweet contains exactly the sequential ordering in qwords
		String [] toks = content.split("[ \t\n\r\f]+");
		int i;
		for(i=0;i<toks.length - qwords.length + 1;i++)
		{
			if(toks[i].equalsIgnoreCase(qwords[0]))
			{
				for(int j=1;j<qwords.length;j++)
				{
					if(!qwords[j].equalsIgnoreCase(toks[i+j])) 
					{
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}

	public static boolean OrQuery(String content, String [] qwords)
	{
		return true;
	}
	
	public static boolean AndQuery(String content, String [][] qwords)
	{
		String [] toks = content.split("[ \t\n\r\f]+");
		HashSet<String> content_set = new HashSet<String>();
		for(String w : toks)
		{
			content_set.add(w.toLowerCase());
		}
		
		boolean relevant = false;
		for(int i=0;i<qwords.length;i++)
		{
			relevant = relevant || AndQuery(content_set, qwords[i]);
			if(relevant) break;
		}
		return relevant;
	}
	
	public static boolean AndQuery(HashSet<String> content_set, String [] qwords)
	{
		boolean matched = true;
		for(int i=0;i<qwords.length;i++)
		{
			matched = matched & content_set.contains(qwords[i]);
			if(!matched) return false;
		}
		return matched;
	}
	
	public static boolean AndQuery(String content, String [] qwords)
	{
		//content is not in lower case!
		//test whether the tweet contains all the words in qwords
		String [] toks = content.split("[ \t\n\r\f]+");
		HashSet<String> content_set = new HashSet<String>();
		for(String w : toks)
		{
			content_set.add(w.toLowerCase());
		}
		return AndQuery(content_set, qwords);
	}

	public static void main(String [] args) throws Exception
	{
		//search through 37 million tweets
		String query = args[0].toLowerCase();
		String [] multiple_queries = query.split(";");
		String [][] qwords = new String[multiple_queries.length][];
		
		for(int i=0;i<multiple_queries.length;i++)
		{
			qwords[i] = multiple_queries[i].split("[ \t\n\r\f]+");
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		//header
		String line = br.readLine();

		while((line=br.readLine())!=null)
		{
			try
			{
				String [] tokens = line.split("\t");
				String content = tokens[2];
				
				boolean relevant = AndQuery(content, qwords);
				//boolean relevant = ContainsQuery(content, qwords);
				if(relevant)
				{
					long status_id = Long.parseLong(tokens[0]);
					int user_id    = Integer.parseInt(tokens[1]);
					String gmt_time  = tokens[3];
					System.out.println(String.format("%d\t%d\t%s\t%s\t%d", status_id, user_id, content, gmt_time, Timestamp.valueOf(gmt_time).getTime()));
				}
			}
			catch (Exception e) {}
		}
		br.close();
	}
}
