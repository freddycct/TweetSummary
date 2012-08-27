import java.io.BufferedReader;
import java.io.InputStreamReader;

public class HighlightWords 
{
	public static String highlight(String content, String phrase, String color)
	{
		String [] phrase_tokens = phrase.split("[ \t\n\r\f]+");
		String [] content_tokens = content.split("[ \t\n\r\f]+");
		
		String highlighted_content = "";
		
		for(int i=0;i<content_tokens.length;i++)
		{
			if(content_tokens[i].equalsIgnoreCase(phrase_tokens[0]))
			{
				String highlight = phrase_tokens[0] + " ";
				int j;
				for(j=1;j<phrase_tokens.length;j++)
				{
					if(phrase_tokens[j].equalsIgnoreCase(content_tokens[i+j]))
					{
						highlight = highlight + phrase_tokens[j] + " ";
					}
					else
					{						
						break;
					}
				}
				if(j == phrase_tokens.length)
				{
					highlight = highlight.trim();
					highlighted_content = String.format("%s <font style=\"background-color: %s\">%s</font> ", highlighted_content, color, highlight);
					i += j-1;
				}
				else
				{
					highlighted_content = highlighted_content + content_tokens[i] + " ";
				}
			}
			else
			{
				highlighted_content = highlighted_content + content_tokens[i] + " ";
			}
		}
		return highlighted_content.trim();
	}
	
	public static void main(String [] args) throws Exception
	{
		String phrase = args[0];
		String color = args[1];
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String line = br.readLine();
		
		System.out.println(line);
		
		while((line=br.readLine())!=null)
		{
			String [] tokens = line.split("\t");
			String content = tokens[0];
			String gmt_time = tokens[1];
			
			content = highlight(content, phrase, color);
			System.out.println(String.format("%s\t%s", content, gmt_time));
		}
		br.close();
	}
}
