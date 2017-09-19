package api.search.service.search;

import api.search.common.ESConnector;
import api.search.common.PageService;
import api.search.domain.param.ApiParam;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.Strings;
import org.elasticsearch.index.query.*;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.MultiValueMode;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.*;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.SuggestionBuilder;
import org.elasticsearch.search.suggest.term.TermSuggestionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by jehee on 2017. 5. 30..
 */
@Service
public class ApiServiceImpl implements ApiService {

    @Override
    public SearchResponse search(ApiParam apiParam) throws Exception{
        String query = apiParam.getQ();
        String type = apiParam.getType();
        String rate = apiParam.getRate();
        String price = apiParam.getPrice();
        String sort = apiParam.getSort();
        String fq = apiParam.getFq();

        /*기본 검색 대상 필드*/
        QueryStringQueryBuilder queryStringQueryBuilder = defaultQueryString(query);


        /*기본 검색 조건*/
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                .must(queryStringQueryBuilder);

        /*랭킹 조건*/
        boolQueryBuilder.must(QueryBuilders.functionScoreQuery(ScoreFunctionBuilders.scriptFunction(new Script("Math.log(2 + doc['rate'].value)"))));
        boolQueryBuilder.must(QueryBuilders.functionScoreQuery(ScoreFunctionBuilders.scriptFunction(new Script("_score - doc['title.keyword'].values.size()"))));
        boolQueryBuilder.must(QueryBuilders.functionScoreQuery(ScoreFunctionBuilders.gaussDecayFunction("publishdate", System.currentTimeMillis(), "1095d", "365d").setMultiValueMode(MultiValueMode.MAX)));

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
        if (!Strings.isNullOrEmpty(rate)){
            boolQueryBuilder.filter(QueryBuilders.termQuery("rate",rate));
        }
        if (!Strings.isNullOrEmpty(price)){
            boolQueryBuilder.filter(QueryBuilders.termQuery("price",price));
        }


//        /*출판년도 검색 조건*/
//        if (!Strings.isNullOrEmpty(publishYear)){
//            boolQueryBuilder.filter(QueryBuilders.termQuery("publish_year",publishYear));
//        }

        /*가격 범위 검색 조건*/
        if (!Strings.isNullOrEmpty(price)){
            String[] arrPublishYearRange = price.split("-");
            if (arrPublishYearRange.length > 1){
                RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("price")
                        .from(arrPublishYearRange[0])
                        .to(arrPublishYearRange[1])
                        .includeLower(false)    /*지정일자 포함 유무*/
                        .includeUpper(false);   /*지정일자 포함 유무*/
                boolQueryBuilder.filter(rangeQuery);
            }
        }
//
//
//        /*언어코드 검색 조건*/
//        if (!Strings.isNullOrEmpty(languageCode)){
//            String[] arrLanguageCode = languageCode.split(",");
//            BoolQueryBuilder subBoolQueryBuilder = QueryBuilders.boolQuery();
//            for (String list : arrLanguageCode){
//                boolQueryBuilder.filter(subBoolQueryBuilder.should(QueryBuilders.termQuery("language_code",list)));
//            }
//        }

        /*정렬 조건
        * title 정렬은 title_keyword 필드를 사용한다.*/
        SortBuilder sortBuilder = null;
        FieldSortBuilder fieldSortBuilder = null;
        ScoreSortBuilder scoreSortBuilder = null;
        if (!Strings.isNullOrEmpty(sort)){
            String[] arrSort = sort.split(" ");

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
                .field("content",20,2)
                .field("content",30,3)
                .field("author")
                .field("publisher")
                .field("keyword");



        /*Spellcheck*/
        SuggestionBuilder<?> suggestionBuilder = SuggestBuilders.termSuggestion("title")
                .stringDistance(TermSuggestionBuilder.StringDistanceImpl.JAROWINKLER)
                .size(1);

        SuggestBuilder suggestBuilder = new SuggestBuilder();
        suggestBuilder.setGlobalText(query)
                .addSuggestion("my-suggestion", suggestionBuilder);

        /*Aggregation*/
        AggregationBuilder aggregationBuilder = AggregationBuilders
                .terms("agg")
                .field("author.keyword");

        String[] indexName = {"foodblog"};

        SearchResponse searchResponse = ESConnector.getInstance().search(indexName, "post", boolQueryBuilder, sortBuilder, highlightBuilder, suggestBuilder, aggregationBuilder, apiParam);

        /*Spellcheck result*/
//        Suggest suggest = searchResponse.getSuggest();
//        Suggest.Suggestion suggestion = suggest.getSuggestion("my-suggestion");
//        List<Suggest.Suggestion.Entry> list=suggestion.getEntries();
//        for (Suggest.Suggestion.Entry entry : list) {
//            List<Suggest.Suggestion.Entry.Option> options=entry.getOptions();
//            for (Suggest.Suggestion.Entry.Option option : options) {
//                System.out.println("suggestion : " + option.getText().toString());
//            }
//        }
//

//
//        Map<String, Object> daa = new HashMap<>();
//        daa.put("result",searchResponse.getHits());
//        daa.put("suggest",list);

        return searchResponse;
    }

    @Override
    public String autocomplete(ApiParam apiParam) throws Exception{
        String returnJson = "";
        String query = apiParam.getQ();

        if (!Strings.isNullOrEmpty(query)){
            MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(query)
                    .tieBreaker(0.1F)
                    .field("suggest",10.0F);
//                    .field("keywords.keywords_full",0.3F);

            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                    .must(multiMatchQueryBuilder)
                    .should(QueryBuilders.termQuery("suggest",query).boost(15.0F));

            boolQueryBuilder.must(QueryBuilders.functionScoreQuery(ScoreFunctionBuilders.scriptFunction(new Script("_score - doc['suggest'].values.size()"))));
            boolQueryBuilder.must(QueryBuilders.functionScoreQuery(ScoreFunctionBuilders.scriptFunction(new Script("_score + doc['type'].value"))));
            SearchResponse searchResponse = ESConnector.getInstance().executeAutocompleteQuery(boolQueryBuilder, "foodblog", "post");
//            SearchResponse searchResponse = ESConnector.getInstance().executeAutocompleteQuery(boolQueryBuilder, "auto", "book");

            String resultStr = "";

            resultStr="{";
            resultStr +="\"query\":\"" + query + "\",";
            resultStr +="\"suggestions\": [";

            int i = 0;
            for (SearchHit hit : searchResponse.getHits()){
                String value = hit.getSourceAsMap().get("keywords").toString();
//                value += "==" + hit.getScore();

                if (i == 0) {
                    resultStr += "\""+ value.trim() + "\"";
                } else {
                    resultStr += ",\""+ value.trim() + "\"";
                }

                i++;
            }

            resultStr +="]}";
            resultStr = resultStr.replaceAll("<strong>","").replaceAll("</strong>","");

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
                .field("content",40)
                .field("author",45)
                ;
        return queryStringQueryBuilder;
    }
}
