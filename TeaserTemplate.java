import LocalDate;

String setting, teaser, rating, tumblrUrl;
String[] tags, subGenre, categories;
int wordCount;
LocalDate posted, updated;

class TeaserTemplate {

  public TeaserTemplate (
    String set,
    String tease,
    String rate,
    String tumblr,
    String[] tags,
    String[] subGenre,
    String[] categories,
    int wordCount,
    LocalDate postDate){
      setting = set;
      teaser = tease;
      rating = rate;
      tumblrUrl = tumblr;
      this.tags = tags;
      this.subGenre = subGenre;
      this.categories = categories;
      posted = postDate;
      updated = LocalDate.today();
    }
}
