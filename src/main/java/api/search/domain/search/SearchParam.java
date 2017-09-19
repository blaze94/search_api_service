package api.search.domain.search;

/**
 * Created by choi on 2017-04-26.
 */
public class SearchParam {
    private String q;
    private String sort;    // String type ',' multi sort. ex) [field] desc, [field] asc
    private Integer rows;    // Integer type. ex) 10 or 20
    private Integer start;   // Integer type. ex) 10 or 20

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }
}
