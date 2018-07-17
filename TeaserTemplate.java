package resources.ShellScriptRunner;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;

import resources.ShellScriptRunner.Utils.FileTools;
import resources.utilities.Functions;

public class TeaserTemplate {
	
	String teaser, rating, tumblrUrl, blogUrl;
	String[] tags, subGenre, categories;
	int wordCount, setIndex;
	LocalDate posted, updated;

	LinkedList<String> settingList;

  public TeaserTemplate (
		  String tease,
		  String rate,
		  String tumblr,
		  String[] tags,
		  String[] subGenre,
		  String[] categories,
		  LocalDate posted,
		  int wordCount,
		  int setIndex,
	LocalDate postDate){
	  teaser = tease;
	  rating = rate;
	  tumblrUrl = tumblr;
	  this.tags = tags;
	  this.subGenre = subGenre;
	  this.categories = categories;
	  posted = postDate;
	  updated = LocalDate.now();
	  this.setIndex = setIndex;
	  
	  try {
		  settingList = FileTools.openFileAndReadLines("settingList.txt");
	  } catch (IOException ioe) {
		  Functions.exitGracefully(ioe);
	  }
	}

	public String formatSetting () {
	  return Functions.readProperties("formatting", "set"+setIndex+"html");
	}
  
  /*
  <em>A Tale from<a href="/tagged/the-far-shore"> The Far Shore</a></em>

A man gets a strange talisman in a grab bag. The woman who is working the booth tells him it supposedly grants "masculine power" when a specific word is spoken though she cannot say which one...

<strong>Rating: P</strong>robably <strong>S</strong>afe <strong>F</strong>or <strong>W</strong>ork | <strong>Pairing: C</strong>is <strong>M</strong>ale / Solo, CM / Femme Fae<strong>
Tags:</strong> Gaining Height, Gaining Muscle Mass, Penis Expansion, Clothing Destruction, Public Nudity, Soft Extortion, Enhanced Libido, Mischief, Implied Sex, Faerie, Demi-Human Partner, Tall Men, Buff Men
<strong>Sub-Genres:</strong> Magical Realism, Growth / Expansion, Demi-Human
<strong>Originally Posted:</strong> 6/9/16 | <strong>Last Updated:</strong> 5/7/18 | <strong>Words:</strong> 1565
  */
}
