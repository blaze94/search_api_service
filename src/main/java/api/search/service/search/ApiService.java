package api.search.service.search;

import api.search.domain.param.ApiParam;
import org.elasticsearch.action.search.SearchResponse;

/**
 * Created by jehee on 2017. 5. 30..
 */
public interface ApiService {
    /*일반 검색 쿼리 */
    public SearchResponse search(ApiParam apiParam) throws Exception;

    /*자동완성 검색 쿼리*/
    public String autocomplete(ApiParam apiParam) throws Exception;
}
