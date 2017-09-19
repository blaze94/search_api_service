package api.search.service.search;

import api.search.domain.param.ApiParam;
import org.elasticsearch.action.search.SearchResponse;

/**
 * Created by jehee on 2017. 5. 30..
 */
public interface ApiService {
    public SearchResponse search(ApiParam apiParam) throws Exception;
    public String autocomplete(ApiParam apiParam) throws Exception;
}
