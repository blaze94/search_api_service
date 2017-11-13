package api.search.domain.param;

import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;

/**
 * Created by Hyundai on 2017-07-10.
 */

public class ApiParam implements Serializable {

    private static final long serialVersionUID = 4385857263750266038L;

    private String q;                   //검색키워드
    private String category1;                   //검색키워드
    private String category2;                   //검색키워드
    private String telephone;                   //검색키워드
    private String rate;         //년도별 필터(옵션)
    private String pt;    //년도 범위 필터(옵션)
    private Integer d = 10;
    private String sort;                //검색 정렬 조건_count desc)
    private Integer size;                //출력 개수
    private Integer from = 0;                //결과출력 시작위치
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

    public String getCategory1() {
        return category1;
    }

    public void setCategory1(String category1) {
        this.category1 = category1;
    }

    public String getCategory2() {
        return category2;
    }

    public void setCategory2(String category2) {
        this.category2 = category2;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getPt() {
        return pt;
    }

    public void setPt(String pt) {
        this.pt = pt;
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

    public String getFq() {
        return fq;
    }

    public void setFq(String fq) {
        this.fq = fq;
    }

    public Integer getD() {
        return d;
    }

    public void setD(Integer d) {
        this.d = d;
    }
}
