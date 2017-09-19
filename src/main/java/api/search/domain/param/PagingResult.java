package api.search.domain.param;

import java.io.Serializable;

/**
 * Created by nobaksan on 15. 7. 29..
 */
public class PagingResult implements Serializable{

    private static final long serialVersionUID = 5685329491120185150L;
    private String searchWord;
    private String searchColumn;
    private String actionPath;
    private String naviParam;
    private int firstPage;
    private int prevPage;
    private int nextPage;
    private int lastPage;
    private int startPage;
    private int endPage;
    private int nowPage;
    private int countPerPage;
    private int totalCount;
    private int rank;
    private String rankOrder="";
    public int getFirstPage() {
        return firstPage;
    }

    public void setFirstPage(int firstPage) {
        this.firstPage = firstPage;
    }

    public int getPrevPage() {
        return prevPage;
    }

    public void setPrevPage(int prevPage) {
        this.prevPage = prevPage;
    }

    public int getNextPage() {
        return nextPage;
    }

    public void setNextPage(int nextPage) {
        this.nextPage = nextPage;
    }

    public int getLastPage() {
        return lastPage;
    }

    public void setLastPage(int lastPage) {
        this.lastPage = lastPage;
    }

    public String getActionPath() {
        return actionPath;
    }

    public void setActionPath(String actionPath) {
        this.actionPath = actionPath;
    }

    public String getNaviParam() {
        return naviParam;
    }

    public void setNaviParam(String naviParam) {
        this.naviParam = naviParam;
    }

    public String getSearchColumn() {
        return searchColumn;
    }

    public void setSearchColumn(String searchColumn) {
        this.searchColumn = searchColumn;
    }

    public String getSearchWord() {
        return searchWord;
    }

    public void setSearchWord(String searchWord) {
        this.searchWord = searchWord;
    }

    public int getStartPage() {
        return startPage;
    }

    public void setStartPage(int startPage) {
        this.startPage = startPage;
    }

    public int getEndPage() {
        return endPage;
    }

    public void setEndPage(int endPage) {
        this.endPage = endPage;
    }

    public int getNowPage() {
        return nowPage;
    }

    public void setNowPage(int nowPage) {
        this.nowPage = nowPage;
    }

    public int getCountPerPage() {
        return countPerPage;
    }

    public void setCountPerPage(int countPerPage) {
        this.countPerPage = countPerPage;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getRankOrder() {
        return rankOrder;
    }

    public void setRankOrder(String rankOrder) {
        this.rankOrder = rankOrder;
    }
}
