package api.search.domain.param;

import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;

/**
 * Created by Hyundai on 2017-07-10.
 */

public class ApiParam implements Serializable {

    private static final long serialVersionUID = 4385857263750266038L;

    private String q;                   //검색키워드
    private String type;                //도서,음반,매거진(book,magazine,music)
    private String rate;         //년도별 필터(옵션)
    private String price;    //년도 범위 필터(옵션)
    private String agg;                 //집합셋
    private String agg_sort;             //집합셋 출력 순서
    private String sort;                //검색 정렬 조건_count desc)
    private Integer size;                //출력 개수
    private Integer from;                //결과출력 시작위치
    private String fq;                  //결과내 재검색

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getFq() {
        return fq;
    }

    public void setFq(String fq) {
        this.fq = fq;
    }

    public String getAgg() {
        return agg;
    }

    public void setAgg(String agg) {
        this.agg = agg;
    }

    public String getAgg_sort() {
        return agg_sort;
    }

    public void setAgg_sort(String agg_sort) {
        this.agg_sort = agg_sort;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getFrom() {
        return from;
    }

    public void setFrom(Integer from) {
        this.from = from;
    }
}
