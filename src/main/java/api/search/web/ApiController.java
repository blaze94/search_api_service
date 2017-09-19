package api.search.web;


import api.search.common.PageService;
import api.search.domain.param.ApiParam;
import api.search.service.search.ApiService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.PrintWriter;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    PageService pageService;

    @Autowired
    ApiService apiService;

    @RequestMapping(value="/search", method= {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody void search(HttpServletRequest request, HttpServletResponse response, @Valid ApiParam apiParam) throws  Exception{
        String result = "";
        String query = apiParam.getQ();
        String rate = apiParam.getRate();
        String price = apiParam.getPrice();

        if (Strings.isNullOrEmpty(query)){
            apiParam.setQ("*");
        }

        SearchResponse searchResponse = apiService.search(apiParam);
        if (searchResponse != null){
            result = searchResponse.toString();
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonParser jp = new JsonParser();
            JsonElement je = jp.parse(result);
            result = gson.toJson(je);

        }else{
            result = "{\"error\":\"검색 결과가 없습니다.\"}";
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        out.print(result);
        out.flush();
    }

    @RequestMapping(value="/autocomplete", method= {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody void autocomplete(HttpServletResponse response,ApiParam apiParam) throws  Exception{
        String returnJson = "";

        returnJson = apiService.autocomplete(apiParam);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(returnJson);
        returnJson = gson.toJson(je);


        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        out.print(returnJson);
        out.flush();    }

}