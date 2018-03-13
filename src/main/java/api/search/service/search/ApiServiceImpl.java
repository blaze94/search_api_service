package api.search.service.search;

import api.search.common.ESConnector;
import api.search.common.PageService;
import api.search.domain.param.ApiParam;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.*;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.MultiValueMode;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.*;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.SuggestionBuilder;
import org.elasticsearch.search.suggest.term.TermSuggestionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

import static org.elasticsearch.index.query.QueryBuilders.geoDistanceQuery;

/**
 * Created by jehee on 2017. 5. 30..
 */
@Service
public class ApiServiceImpl implements ApiService {

    @Override
    public SearchResponse search(ApiParam apiParam) throws Exception{
        String query = apiParam.getQ();
        String category1 = apiParam.getCategory1();
        String category2 = apiParam.getCategory2();
        String rate = apiParam.getRate();
        String telephone = apiParam.getTelephone();
        String sort = apiParam.getSort();
        String fq = apiParam.getFq();
        String pt = apiParam.getPt();
        int d = apiParam.getD();
        /*기본 검색 대상 필드*/
        QueryStringQueryBuilder queryStringQueryBuilder = defaultQueryString(query);


        /*기본 검색 조건*/
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                .must(queryStringQueryBuilder);

        /*랭킹 조건*/
        boolQueryBuilder.must(QueryBuilders.functionScoreQuery(ScoreFunctionBuilders.scriptFunction(new Script("Math.log(2 + doc['rate'].value)"))));


        /*결과내 재검색
        * fq=서울 영등포,경기도,부산*/
        if (!Strings.isNullOrEmpty(fq)){
            String[] arrFq = fq.split(",");
            BoolQueryBuilder subBoolQueryBuilder = QueryBuilders.boolQuery();
            for (String list : arrFq){
                QueryStringQueryBuilder subStringQueryBuilder = defaultQueryString(list);
                boolQueryBuilder.filter(subBoolQueryBuilder.should(subStringQueryBuilder));
            }
        }

        /*카테고리 검색 조건*/
        if (!Strings.isNullOrEmpty(category1)){
            boolQueryBuilder.filter(QueryBuilders.termQuery("category1",category1));
        }

        if (!Strings.isNullOrEmpty(category2)){
            boolQueryBuilder.filter(QueryBuilders.termQuery("category2",category2));
        }

        if (!Strings.isNullOrEmpty(rate)){
            boolQueryBuilder.filter(QueryBuilders.termQuery("rate",rate));
        }

        if (!Strings.isNullOrEmpty(telephone)){
            boolQueryBuilder.filter(QueryBuilders.termQuery("telephone",telephone));
        }





        if (!Strings.isNullOrEmpty(pt)) {
            String[] points = new String[2];
            if (pt.equals("")) {
                points[0] = "37.56570958096572";
                points[1] = "126.9032893177229";
            } else {
                points = pt.split(",");
            }



            boolQueryBuilder.filter(QueryBuilders.geoDistanceQuery("location")
                    .point(Double.parseDouble(points[0]), Double.parseDouble(points[1]))
                    .distance(d, DistanceUnit.KILOMETERS));
        }


        /*정렬 조건
        * title 정렬은 title_keyword 필드를 사용한다.*/
        SortBuilder sortBuilder = null;
        FieldSortBuilder fieldSortBuilder = null;
        ScoreSortBuilder scoreSortBuilder = null;
        if (!Strings.isNullOrEmpty(sort)){
            String[] arrSort = sort.toLowerCase().split(" ");

            if (arrSort[0].equals("score")){
                scoreSortBuilder = SortBuilders.scoreSort();
                if ("asc".equals(arrSort[1])){
                    scoreSortBuilder.order(SortOrder.ASC);
                }else{
                    scoreSortBuilder.order(SortOrder.DESC);
                }
                sortBuilder = scoreSortBuilder;
            }else{
                fieldSortBuilder = SortBuilders.fieldSort(arrSort[0].toString());
                if ("asc".equals(arrSort[1])){
                    fieldSortBuilder.order(SortOrder.ASC);
                }else{
                    fieldSortBuilder.order(SortOrder.DESC);
                }
                fieldSortBuilder.sortMode(SortMode.MIN);
                sortBuilder = fieldSortBuilder;
            }
        }else{
            scoreSortBuilder = SortBuilders.scoreSort();
            sortBuilder = scoreSortBuilder;
        }

        /*Highlight*/
        HighlightBuilder highlightBuilder = new HighlightBuilder()
                .preTags("<b>")
                .postTags("</b>")
                .field("title",20,2)
                .field("description",30,3)
                ;



        /*Spellcheck*/
        SuggestionBuilder<?> suggestionBuilder = SuggestBuilders.termSuggestion("title.keyword")
                .stringDistance(TermSuggestionBuilder.StringDistanceImpl.DAMERAU_LEVENSHTEIN)
                .accuracy((float) 0.7)
                .size(2);

        SuggestBuilder suggestBuilder = new SuggestBuilder();
        suggestBuilder.setGlobalText(query)
                .addSuggestion("my-suggestion", suggestionBuilder);

        /*Aggregation*/
        AggregationBuilder aggregationBuilder1 = AggregationBuilders
                .terms("cate_agg")
                .field("category1.keyword").subAggregation(AggregationBuilders.terms("agg2").field("category2.keyword"));
        AggregationBuilder aggregationBuilder2 = AggregationBuilders
                .terms("rate_agg")
                .field("rate");
        AggregationBuilder aggregationBuilder3 = AggregationBuilders
                .terms("region_agg")
                .field("region.keyword");

        List<AggregationBuilder> listAggregationBuilder = new LinkedList<>();
        listAggregationBuilder.add(aggregationBuilder1);
        listAggregationBuilder.add(aggregationBuilder2);
        listAggregationBuilder.add(aggregationBuilder3);


        String[] indexName = {"store"};
        SearchResponse searchResponse = ESConnector.getInstance().search(indexName, "info", boolQueryBuilder, sortBuilder, highlightBuilder, suggestBuilder, listAggregationBuilder, apiParam);
        return searchResponse;
    }

    @Override
    public String autocomplete(ApiParam apiParam) throws Exception{
        String returnJson = "";
        String query = apiParam.getQ();

        if (!Strings.isNullOrEmpty(query)){
            MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(query)
                    .tieBreaker(0.1F)
                    .field("title.auto",10.0F);

//                    .field("keywords.keywords_full",0.3F);

            //중복 제거를 위한 agg 사용 (실무에서는 중복 데이터가 제거된 자동완성용 데이터 셋을 따로 준비함)
            AggregationBuilder aggregationBuilder = AggregationBuilders
                    .terms("auto_agg")
                    .field("title.keyword");

            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                    .must(multiMatchQueryBuilder);
            boolQueryBuilder.must(QueryBuilders.functionScoreQuery(ScoreFunctionBuilders.scriptFunction(new Script("_score - doc['title.auto'].values.size()"))));
            SearchResponse searchResponse = ESConnector.getInstance().executeAutocompleteQuery("store", "info",boolQueryBuilder,aggregationBuilder);




            String resultStr = "";

            resultStr="{";
            resultStr +="\"query\":\"" + query + "\",";
            resultStr +="\"suggestions\": [";
            Terms autoAgg = searchResponse.getAggregations().get("auto_agg");


            int i = 0;
            for (Terms.Bucket bucket : autoAgg.getBuckets()){
                String value = bucket.getKeyAsString();
//                value += "==" + hit.getScore();

                if (i == 0) {
                    resultStr += "\""+ value.trim() + "\"";
                } else {
                    resultStr += ",\""+ value.trim() + "\"";
                }

                i++;
            }

            resultStr +="]}";
            returnJson = resultStr;
        }else{
            returnJson = "";
        }

        return returnJson;
    }

    public QueryStringQueryBuilder defaultQueryString(String query){
        QueryStringQueryBuilder queryStringQueryBuilder = QueryBuilders.queryStringQuery(query)
                .tieBreaker(0.5F)
                .defaultOperator(Operator.AND)
                .field("title",50)
                .field("description",40)
                .field("category1",10)
                .field("category2",10)
                .field("address",1)
                .field("roadaddress",1)
                ;
        return queryStringQueryBuilder;
    }
}





















