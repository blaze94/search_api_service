package api.search.common;

import api.search.domain.param.ApiParam;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by GGBX08 on 2017-07-11.
 */
public class ESConnector {
    public static ESConnector instance;

    public TransportClient getClient() {
        return client;
    }

    private static TransportClient client;
    private ESConnector(){}


    public static synchronized ESConnector getInstance() throws UnknownHostException {
        if(instance == null){
            instance = new ESConnector();

            Settings settings = Settings.builder()
                    .put("cluster.name", "my-application")
                    .build();

            client = new PreBuiltTransportClient(settings)
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("13.124.36.214"), 9300));    //검증 터널링
//                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("175.198.113.122"), 58801));    //개발서버(집)
//                      .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("10.21.48.74"), 9300));     //검증계 검색 서버
//                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9300));
        }
        return  instance;
    }

    public SearchResponse search(String[] indexName, String typeName, QueryBuilder queryBuilder, SortBuilder sortBuilders, HighlightBuilder highlightBuilder, SuggestBuilder suggestBuilder, AggregationBuilder aggregationBuilder, ApiParam apiParam) {
        int fromRows = 0;
        int size = 12;

        if (apiParam.getSize() != null && apiParam.getSize() > 0){
            size = apiParam.getSize();
        }

        if (apiParam.getFrom() != null && apiParam.getFrom() > 0){
            fromRows = (apiParam.getFrom() -1) * size;
        }

        SearchRequestBuilder searchRequestBuilder = client.prepareSearch().setIndices(indexName)
                .setTypes(typeName)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(queryBuilder)
                .setFrom(fromRows)
                .setSize(size)
                .highlighter(highlightBuilder)
                .addSort(sortBuilders)
                .suggest(suggestBuilder);

        if (aggregationBuilder != null){
            searchRequestBuilder.addAggregation(aggregationBuilder);
        }

        System.out.println(searchRequestBuilder.toString());
        SearchResponse searchResponse =  searchRequestBuilder.execute().actionGet();
//        System.out.println(searchResponse);


        return  searchResponse;
    }

    public SearchResponse executeAutocompleteQuery(QueryBuilder queryBuilder, String indexName, String typeName) throws Exception{
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch().setIndices(indexName)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setTypes(typeName)
                .setQuery(queryBuilder)
                .setSize(10);

        System.out.println(searchRequestBuilder.toString());
        SearchResponse searchResponse =  searchRequestBuilder.execute().actionGet();

        return searchResponse;
    }
}
