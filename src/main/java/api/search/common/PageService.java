package api.search.common;


import api.search.domain.param.PagingParam;
import api.search.domain.param.PagingResult;

/**
 * Created by nobaksan on 15. 7. 29..
 */
public interface PageService {
    PagingParam initPage(PagingParam pagingParam, String actionPath);
    PagingResult getPage(PagingParam pagingParam);

}