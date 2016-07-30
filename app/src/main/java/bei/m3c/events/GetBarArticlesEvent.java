package bei.m3c.events;

import java.util.List;

import bei.m3c.models.BarArticle;

public class GetBarArticlesEvent {
    public List<BarArticle> barArticles;

    public GetBarArticlesEvent(List<BarArticle> barArticles) {
        this.barArticles = barArticles;
    }
}
