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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
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

    @RequestMapping(value="/search", method= RequestMethod.GET)
    public ModelAndView elasticsearchSelect(ModelMap model, HttpServletRequest request, ApiParam apiParam, PagingParam pagingParam) throws  Exception{
        String result = "";
        String query = apiParam.getQ();
        String type = apiParam.getType();
        String rate = apiParam.getRate();
        String price = apiParam.getPrice();


//        String magazineTitleReplace = "Jamie Magazine.";
//        magazineTitleReplace = magazineTitleReplace.replaceAll("\\.$","").trim();
//        System.out.println(magazineTitleReplace);

        int nowPage = 0;
        if (apiParam.getFrom() == null){
            pagingParam.setNowPage(0);
        }else{
            pagingParam.setNowPage(apiParam.getFrom());
        }

        if (Strings.isNullOrEmpty(query)){
            apiParam.setQ("*");
        }

        SearchResponse searchResponse  = apiService.search(apiParam);

        Terms agg = searchResponse.getAggregations().get("agg");

        System.out.println(agg.getBuckets().size());

        for (Terms.Bucket list : agg.getBuckets()){
            System.out.println(list.getKeyAsString());
        }

        List<Map<String,String>> mapList = new ArrayList<>();

        Integer totalCnt = (int)searchResponse.getHits().getTotalHits();
        pagingParam = pageService.initPage(pagingParam, "/web/search");
        pagingParam.setTotalCount(totalCnt);

        PagingResult pagingResult = pageService.getPage(pagingParam);

        model.put("pagingResult", pagingResult);
        model.put("q", query);

        if (apiParam.getSize() != null){
            model.put("size", apiParam.getSize());
        }else{
            model.put("size",12);
        }

        for (SearchHit hit : searchResponse.getHits()) {

            Map<String, Object> map = hit.getSourceAsMap();
            Map<String, String> mapData = new HashMap<String, String>();

            mapData.put("score", String.valueOf(hit.getScore()));

            if (hit.getHighlightFields().get("title") != null){
                mapData.put("title", hit.getHighlightFields().get("title").getFragments()[0].toString());
            }else{
                if (map.get("title") != null){
                    mapData.put("title", map.get("title").toString());
                }else{
                    mapData.put("title", "");
                }
            }

            if (hit.getHighlightFields().get("author") != null){
                mapData.put("author", hit.getHighlightFields().get("author").getFragments()[0].toString());
            }else{
                if (map.get("author") != null){
                    mapData.put("author", map.get("author").toString());
                }else{
                    mapData.put("author", "");
                }
            }

            if (hit.getHighlightFields().get("content") != null){
                mapData.put("content", hit.getHighlightFields().get("content").getFragments()[0].toString());
            }else{
                if (map.get("content") != null){
                    mapData.put("content", map.get("content").toString());
                }else{
                    mapData.put("content", "");
                }
            }

            mapList.add(mapData);
        }

        model.put("type", type);
        model.put("mapList", mapList);
        return new ModelAndView("/elasticsearch/main", model);
    }

    @RequestMapping(value="/autoComplete", method= RequestMethod.GET)
    public @ResponseBody String auto(ApiParam apiParam) throws  Exception{
        String returnJson = "";

        returnJson = apiService.autocomplete(apiParam);

        return returnJson;
    }


    @RequestMapping(value="/test", method= RequestMethod.GET)
    public ModelAndView testPage(ModelMap model) throws  Exception{

        return new ModelAndView("/elasticsearch/page-starter",model);
    }
}