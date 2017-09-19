package api.search.common;

import api.search.domain.param.PagingParam;
import api.search.domain.param.PagingResult;
import org.springframework.stereotype.Service;

/**
 * Created by nobaksan on 15. 7. 29..
 */
@Service
public class PageServiceImpl implements PageService {


    @Override
    public PagingParam initPage(PagingParam pagingParam, String actionPath) {
        //초기값 생성
        if(pagingParam.getNowPage() == 0){
            pagingParam.setNowPage(1);
            pagingParam.setSearchWord(null);
        }
        pagingParam.setActionPath(actionPath);
        pagingParam.setBlockCount(5);
        pagingParam.setCountPerPage(10);
        return pagingParam;
    }

    public PagingResult getPage(PagingParam pagingParam){
        PagingResult pagingResult = new PagingResult();

        String actionPath = pagingParam.getActionPath();
        String sNowPage = String.valueOf(pagingParam.getNowPage());
        String sTotalCount = String.valueOf(pagingParam.getTotalCount());
        String sCountPerPage = String.valueOf(pagingParam.getCountPerPage());
        String sblockCount = String.valueOf(pagingParam.getBlockCount());
        String searchColumn = pagingParam.getSearchColumn();
        String searchWord = pagingParam.getSearchWord();
        String navi_param = pagingParam.getNaviParam();
        String rankOrder = pagingParam.getRankOrder();
        int rank = 1;
        System.out.println(sTotalCount+":sTotalCount");

        int nowPage = (sNowPage == null || sNowPage.trim().equals("")) ? 1 : Integer.valueOf(sNowPage);
        int totalCount = (sTotalCount == null || sTotalCount.trim().equals("")) ? 0 : Integer.valueOf(sTotalCount);
        int countPerPage = (sCountPerPage == null || sCountPerPage.trim().equals("")) ? 1 : Integer.valueOf(sCountPerPage);
        int countPerBlock = (sblockCount == null || sblockCount.trim().equals("")) ? 1 : Integer.valueOf(sblockCount);
        System.out.println(countPerPage+":countPerPage");

        int totalPage = (int)( (totalCount-1)/countPerPage ) + 1;
        if(totalPage == 0) totalPage = 1 ;

        int totalBlock   = (int)((totalPage-1)/(countPerBlock));
        int nowBlock     = (int)((nowPage - 1) / countPerBlock);

        int firstPage = 0;
        int prevPage = 0;
        int nextPage = 0;
        int lastPage = 0;

        if (nowBlock > 0) {
            firstPage = 1;
        }
        if( nowPage > 1 ) {
            prevPage = nowPage - 1;
        }

        int startPage = nowBlock * countPerBlock + 1;
        int endPage = countPerBlock * (nowBlock + 1);

        if ( endPage > totalPage ) endPage = totalPage;


        if( nowPage < totalPage ) {
            nextPage = nowPage + 1;
        }
        if( nowBlock < totalBlock ) {
            lastPage = totalPage;
        }

        if("asc".equals(rankOrder)){
            rank = (nowPage-1)*pagingParam.getCountPerPage();
        }else{
            rank = totalCount - ((nowPage-1)*countPerPage);


        }


        pagingResult.setActionPath(actionPath);
        pagingResult.setSearchColumn(searchColumn);
        pagingResult.setSearchWord(searchWord);
        pagingResult.setNaviParam(navi_param);
        pagingResult.setStartPage(startPage);
        pagingResult.setEndPage(endPage);
        pagingResult.setFirstPage(firstPage);
        pagingResult.setPrevPage(prevPage);
        pagingResult.setNextPage(nextPage);
        pagingResult.setLastPage(lastPage);
        pagingResult.setNowPage(nowPage);
        pagingResult.setCountPerPage(countPerPage);
        pagingResult.setTotalCount(totalCount);
        pagingResult.setRank(rank);
        pagingResult.setRankOrder(rankOrder);
        return pagingResult;
    }


//    private PagingResult setPagingInfo(Page paging){
//        paging.setStartNum(paging.getTotalCount() - (paging.getNowPage()-1) * paging.getCountPerPage());
//        //시작 seq
//        paging.setStartseq(paging.getTotalCount()-paging.getStartNum());
//        //종료 seq
//        paging.setEndseq(paging.getTotalCount()-paging.getStartNum()+paging.getCountPerPage());
//        return paging;
//    }
}
