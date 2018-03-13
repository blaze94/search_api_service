package api.search.common;

import api.search.domain.param.ApiParam;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.suggest.SuggestBuilder;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by GGBX08 on 2017-07-11.
 */
public class ESConnector {
    public static ESConnector instance;

    public RestHighLevelClient getClient() {
        return client;
    }

    private static RestHighLevelClient client;
    private ESConnector(){}


    public static synchronized ESConnector getInstance() throws UnknownHostException {
        if(instance == null){
            instance = new ESConnector();

            Header[] defaultHeaders = new Header[]{new BasicHeader("header", "value")};
            client = new RestHighLevelClient(
                    RestClient.builder(
                            new HttpHost("13.124.211.211", 9200, "http"),
                            new HttpHost("13.124.211.211", 9201, "http")).setDefaultHeaders(defaultHeaders));

        }
        return  instance;
    }

    public SearchResponse search(String[] indexName, String typeName, QueryBuilder queryBuilder, SortBuilder sortBuilders, HighlightBuilder highlightBuilder, SuggestBuilder suggestBuilder, List<AggregationBuilder> listAggregationBuilder, ApiParam apiParam) throws IOException {
        int fromRows = 0;
        int size = 12;

        if (apiParam.getSize() != null && apiParam.getSize() > 0){
            size = apiParam.getSize();
        }

        if (apiParam.getFrom() != null && apiParam.getFrom() > 0){
            fromRows = (apiParam.getFrom() -1) * size;
        }

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(queryBuilder);
        sourceBuilder.from(fromRows);
        sourceBuilder.size(size);
        sourceBuilder.highlighter(highlightBuilder);
        sourceBuilder.sort(sortBuilders);
        if (listAggregationBuilder != null){
            for(AggregationBuilder aggregationBuilder : listAggregationBuilder) {
                sourceBuilder.aggregation(aggregationBuilder);
            }
        }
        sourceBuilder.suggest(suggestBuilder);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.source(sourceBuilder);
        searchRequest.indices(indexName);
        searchRequest.types(typeName);
        SearchResponse searchResponse =  client.search(searchRequest);
//        System.out.println(searchResponse);

        return  searchResponse;
    }

    public SearchResponse executeAutocompleteQuery(String indexName, String typeName,QueryBuilder queryBuilder, AggregationBuilder aggregationBuilder) throws Exception{
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(queryBuilder);
        sourceBuilder.from(0);
        sourceBuilder.size(10);
        sourceBuilder.aggregation(aggregationBuilder);

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.source(sourceBuilder);
        searchRequest.indices(indexName);
        searchRequest.types(typeName);

        SearchResponse searchResponse =  client.search(searchRequest);

        return searchResponse;
    }
}
