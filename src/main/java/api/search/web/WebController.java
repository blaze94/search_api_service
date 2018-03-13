package api.search.web;


import api.search.common.PageService;
import api.search.domain.param.PagingParam;
import api.search.domain.param.PagingResult;
import api.search.domain.param.ApiParam;
import api.search.service.search.ApiService;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.Strings;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.elasticsearch.search.suggest.term.TermSuggestion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.Normalizer;
import java.util.*;

/**
 * Created by choi on 2017-04-26.
 */
@Controller
@RequestMapping("/web")
public class WebController {

    @Autowired
    PageService pageService;

    @Autowired
    ApiService apiService;

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public ModelAndView elasticsearchSelect(ModelMap model, HttpServletRequest request, ApiParam apiParam, PagingParam pagingParam) throws Exception {
        String result = "";
        String query = apiParam.getQ();
        String type = "info";
        String rate = apiParam.getRate();


        int nowPage = 0;
        if (apiParam.getFrom() == null) {
            pagingParam.setNowPage(0);
        } else {
            pagingParam.setNowPage(apiParam.getFrom());
        }

        if (Strings.isNullOrEmpty(query)) {
            apiParam.setQ("*");
        }

        SearchResponse searchResponse = apiService.search(apiParam);

        Terms regionAgg = searchResponse.getAggregations().get("region_agg");
        Terms cateAgg = searchResponse.getAggregations().get("cate_agg");
        Terms rateAgg = searchResponse.getAggregations().get("rate_agg");


        List<Map<String, String>> mapList = new ArrayList<>();

        Integer totalCnt = (int) searchResponse.getHits().getTotalHits();
        pagingParam = pageService.initPage(pagingParam, "/web/search");
        pagingParam.setTotalCount(totalCnt);

        PagingResult pagingResult = pageService.getPage(pagingParam);
        model.put("regionAgg", regionAgg.getBuckets());
        model.put("cateAgg", cateAgg.getBuckets());
        model.put("rateAgg", rateAgg.getBuckets());
        model.put("pagingResult", pagingResult);
        model.put("q", query);

        if (apiParam.getSize() != null) {
            model.put("size", apiParam.getSize());
        } else {
            model.put("size", 12);
        }

        for (SearchHit hit : searchResponse.getHits()) {

            Map<String, Object> map = hit.getSourceAsMap();
            Map<String, String> mapData = new HashMap<String, String>();

            mapData.put("score", String.valueOf(hit.getScore()));

            if (hit.getHighlightFields().get("title") != null) {
                mapData.put("title", hit.getHighlightFields().get("title").getFragments()[0].toString());
            } else {
                if (map.get("title") != null) {
                    mapData.put("title", map.get("title").toString());
                } else {
                    mapData.put("title", "");
                }
            }

            if (hit.getHighlightFields().get("description") != null) {
                mapData.put("description", hit.getHighlightFields().get("description").getFragments()[0].toString());
            } else {
                if (map.get("description") != null) {
                    mapData.put("description", map.get("description").toString());
                } else {
                    mapData.put("description", "");
                }
            }
            mapData.put("rate", map.get("rate").toString());
            mapData.put("category1", map.get("category1").toString());
            mapData.put("category2", map.get("category2").toString());
            mapData.put("telephone", map.get("telephone").toString());
            mapData.put("link", map.get("link").toString());
            mapData.put("address", map.get("address").toString());
            mapList.add(mapData);
        }


        List<String> spellList = new ArrayList<String>();
        Suggest suggest = searchResponse.getSuggest();

        if (suggest != null) {
            TermSuggestion termSuggestion = searchResponse.getSuggest().getSuggestion("my-suggestion");
            List<TermSuggestion.Entry> listEntry =  termSuggestion.getEntries();

            for (TermSuggestion.Entry entry : listEntry) {
                List<TermSuggestion.Entry.Option> listOption = entry.getOptions();
                for (TermSuggestion.Entry.Option option : listOption) {
                    spellList.add(Normalizer.normalize(option.getText().toString(), Normalizer.Form.NFC));
                }
            }
            model.put("spellResult", spellList);
        }

        model.put("q", apiParam.getQ());
        model.put("sort", apiParam.getSort());
        model.put("fq", apiParam.getFq());
        model.put("pt", apiParam.getPt());
        model.put("type", type);
        model.put("mapList", mapList);

        model.put("apiParam", apiParam);
        return new ModelAndView("/elasticsearch/base", model);
    }

    @RequestMapping(value = "/autocomplete", method = { RequestMethod.GET, RequestMethod.POST })
    public ResponseEntity<String> autocomplete(@RequestParam(value = "query") String query,HttpServletResponse response) {
        String returnJson = "";
        ApiParam apiParam = new ApiParam();
        apiParam.setQ(query);
        try {
            returnJson = apiService.autocomplete(apiParam);
        } catch (Exception e){
            e.printStackTrace();
        }
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", "text/html; charset=UTF-8");
        return new ResponseEntity<String>(returnJson, responseHeaders, HttpStatus.ACCEPTED);
    }


    /*
    지도 좌표를 AJAX로 셀렉하기 위한 메소드
     */
    @RequestMapping(value="/api/select", method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String,Object>>  getShopInJSON(ApiParam apiParam) throws Exception {
        SearchResponse searchResponse = apiService.search(apiParam);
        List<Map<String,Object>> esData = new ArrayList<Map<String,Object>>();
        for(SearchHit hit : searchResponse.getHits()){
            esData.add(hit.getSourceAsMap());
        }
        return esData;
    }

}