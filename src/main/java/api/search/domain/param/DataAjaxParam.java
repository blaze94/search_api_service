package api.search.domain.param;

/**
 * Created by Hyundai on 2017-07-10.
 */
public class DataAjaxParam {
    String library;
    String cat1;
    String cat2;
    String cat3;
    String book;
    String recommendType;
    String keywordCnt;
    String listCnt;

    public String getLibrary() {
        return library;
    }

    public void setLibrary(String library) {
        this.library = library;
    }

    public String getCat1() {
        return cat1;
    }

    public void setCat1(String cat1) {
        this.cat1 = cat1;
    }

    public String getCat2() {
        return cat2;
    }

    public void setCat2(String cat2) {
        this.cat2 = cat2;
    }

    public String getCat3() {
        return cat3;
    }

    public void setCat3(String cat3) {
        this.cat3 = cat3;
    }

    public String getBook() {
        return book;
    }

    public void setBook(String book) {
        this.book = book;
    }

    public String getRecommendType() {
        return recommendType;
    }

    public void setRecommendType(String recommendType) {
        this.recommendType = recommendType;
    }

    public String getKeywordCnt() {
        return keywordCnt;
    }

    public void setKeywordCnt(String keywordCnt) {
        this.keywordCnt = keywordCnt;
    }

    public String getListCnt() {
        return listCnt;
    }

    public void setListCnt(String listCnt) {
        this.listCnt = listCnt;
    }
}
