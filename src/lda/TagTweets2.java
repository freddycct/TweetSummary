package lda;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

import newalgo.Tagger;
import newalgo.Tagger.TaggedToken;
import np_lda.NPLexer;

public class TagTweets2 
{
	Tagger tagger;
	NPLexer lexer;
	
	public TagTweets2() throws Exception
	{
		tagger = new Tagger();
		String modelFilename = "jar/model.alldata.gz";
		tagger.loadModel(modelFilename);
		lexer = new NPLexer();
	}
	
	public List<String> findNPs(String content) throws IOException
	{
		return findNPs(this.tagger, this.lexer, content);
	}
	
	public static List<String> findNPs(Tagger tagger, NPLexer lexer, String content) throws IOException
	{
		List<TaggedToken> taggedTokens = tagger.tokenizeAndTag(content);
		//Get Tokens
		List<String> nps = new LinkedList<String>();
		for(TaggedToken tt : taggedTokens)
		{
			nps.add(tt.token.toLowerCase());
		}
		return nps;
	}
	
	public static void main(String [] args) throws Exception
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String line;
		TagTweets2 tagtweets = new TagTweets2();
		
		while((line=br.readLine())!=null)
		{	
			String [] tokens = line.split("\t");
			
			long status_id  = Long.parseLong(tokens[0]);
			int user_id     = Integer.parseInt(tokens[1]);
			String content  = tokens[2].trim();
			String gmt_time = tokens[3];
			long long_time = Timestamp.valueOf(gmt_time).getTime();
			System.out.println(String.format("%d\t%d\t%s\t%d", status_id, user_id, content, long_time));
			if(content.length() > 0)
			{
				//Print out the Noun Phrases
				List<String> nps = tagtweets.findNPs(content);
				for(String np : nps)
				{
					System.out.println(String.format("%s\t-1", np.toLowerCase()));
				}
				System.out.println();
			}
		}
		br.close();
	}
}
