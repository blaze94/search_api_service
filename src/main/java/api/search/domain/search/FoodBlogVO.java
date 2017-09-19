package api.search.domain.search;

import javax.persistence.*;
import java.io.Serializable;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * Created by choi on 2017-04-28.
 */
@Entity
@Table(name = "blogcrawl", schema = "foodblog")
public class FoodBlogVO implements Serializable{
    private static final long serialVersionUID = -6797594364391845686L;

    private Integer idx;
    private String url;
    private String title;
    private String content;
    private String iscrawled;
    private String imgsrc;
    private String publishdate;
    private String insertdate;
    private String author;
    private String lastpublished;
    private Integer rate;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name="idx", unique=true, nullable=false)
    public Integer getIdx() {
        return idx;
    }

    public void setIdx(Integer idx) {
        this.idx = idx;
    }

    @Column(name="url", nullable=false)
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Column(name="title", nullable=false)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Column(name="content", nullable=false)
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Column(name="iscrawled", nullable=false)
    public String getIscrawled() {
        return iscrawled;
    }

    public void setIscrawled(String iscrawled) {
        this.iscrawled = iscrawled;
    }

    @Column(name="imgsrc", nullable=false)
    public String getImgsrc() {
        return imgsrc;
    }

    public void setImgsrc(String imgsrc) {
        this.imgsrc = imgsrc;
    }

    @Column(name="publishdate", nullable=false)
    public String getPublishdate() {
        return publishdate;
    }

    public void setPublishdate(String publishdate) {
        this.publishdate = publishdate;
    }

    @Column(name="insertdate", nullable=false)
    public String getInsertdate() {
        return insertdate;
    }

    public void setInsertdate(String insertdate) {
        this.insertdate = insertdate;
    }

    @Column(name="author", nullable=false)
    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Column(name="lastpublished", nullable=false)
    public String getLastpublished() {
        return lastpublished;
    }

    public void setLastpublished(String lastpublished) {
        this.lastpublished = lastpublished;
    }

    @Column(name="rate", nullable=false)
    public Integer getRate() {
        return rate;
    }

    public void setRate(Integer rate) {
        this.rate = rate;
    }
}
