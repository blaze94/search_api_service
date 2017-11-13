0. 서버구조
스프링 부트 (체크아웃후 메이븐 라이브러리 import 이후 바로 자체 서버가 기동됨)

1. 세팅
한글형태소분석기
스펠체커
자동완성분석기

{
   "settings" : {
     "index":{
       "analysis":{
         "analyzer":{
           "korean":{
             "type":"custom",
             "tokenizer":"seunjeon_default_tokenizer"
           },
          "nfd_normalized": {
            "tokenizer": "standard",
            "char_filter": [
              "nfd_normalizer"
            ]
          },
          "my-index-edge-jaso":{
	        "type":"custom",
	        "tokenizer":"keyword",
	        "filter":["my-jaso-filter","edge_filter"]
	      },
	      "my-index-full-jaso":{
	        "type":"custom",
	        "tokenizer":"keyword",
	        "filter":["edge_reverse_filter","my-jaso-filter","edge_filter"]
	      },
	     "my-search-jaso":{
	        "type":"custom",
	        "tokenizer":"keyword",
	        "filter":["my-jaso-filter"]
	      }
         },
         "tokenizer": {
           "seunjeon_default_tokenizer": {
             "type": "seunjeon_tokenizer",
             "index_eojeol": false,
             "user_words": ["낄끼+빠빠,-100", "c\\+\\+", "어그로", "버카충", "abc마트"]
           }
         },
        "char_filter": {
          "nfd_normalizer": {
            "type": "icu_normalizer",
            "name": "nfc",
            "mode": "decompose"
          }
        },
        "filter" : {
            "my-jaso-filter" : {
                "type" : "qj-analyzer-filter",
                "tokenizer": "keyword",
                "mode":"simple_jaso",
                "jaso_typo" : true
            },
            "edge_filter": {
              "type": "edge_ngram",
              "min_gram": 1,
              "max_gram": 10,
              "token_chars": [
                "letter",
                "digit"
              ]
            },
        	"edge_reverse_filter": {
              "type": "edge_ngram",
              "min_gram": 1,
              "max_gram": 10,
              "side" : "back",
              "token_chars": [
                "letter",
                "digit"
              ]
            }
        }
       } 
     }
   }
 }


 2. 매핑

-PUT /store/_mappings/info
{
"properties": {
    "address": {
        "type": "text",
        "fields": {
            "keyword": {
                "type": "keyword",
                "ignore_above": 256
            }
        }
    },
    "category": {
        "type": "text",
        "fields": {
            "keyword": {
                "type": "keyword",
                "ignore_above": 256
            }
        }
    },
    "category1": {
        "type": "text",
        "fields": {
            "keyword": {
                "type": "keyword",
                "ignore_above": 256
            }
        }
    },
    "category2": {
        "type": "text",
        "fields": {
            "keyword": {
                "type": "keyword",
                "ignore_above": 256
            }
        }
    },
    "description": {
        "type": "text",
        "analyzer": "korean",
        "fields": {
            "keyword": {
                "type": "keyword",
                "ignore_above": 256
            }
        }
    },
    "region": {
        "type": "keyword"
    },
    "idx": {
        "type": "long"
    },
    "lat": {
        "type": "float"
    },
    "link": {
        "type": "keyword"
    },
    "lng": {
        "type": "float"
    },
    "location": {
        "type": "geo_point"
    },
    "rate": {
        "type": "long"
    },
    "roadaddress": {
        "type": "text",
        "analyzer": "korean",
        "fields": {
            "keyword": {
                "type": "keyword",
                "ignore_above": 256
            }
        }
    },
    "telephone": {
        "type": "text",
        "fields": {
            "keyword": {
                "type": "keyword",
                "ignore_above": 256
            }
        }
    },
    "title": {
        "type": "text",
        "analyzer": "korean",
        "fields": {
            "keyword": {
                "type": "keyword",
                "ignore_above": 256
            },
            "spells": {
                "type": "text",
                "fielddata":"true",
                "analyzer": "nfd_normalized"
            },
            "auto": {
                "type": "text",
                "fielddata":"true",
                "analyzer": "my-index-edge-jaso",
                "search_analyzer": "my-search-jaso"
            }

        }
    }
	}
}

