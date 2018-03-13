0. 서버구조
스프링 부트 (체크아웃후 메이븐 라이브러리 import 이후 바로 자체 서버가 기동됨)1


1. 엘라스틱서치
http://gofile.me/32vUe/GykSKPQqd  다운로드 (패스워드 : 2580)
설치내역
자동완성 플러그인
은전한님 플러그인
스펠체커용 ICU 플러그인
상점정보 10만건


2. 세팅
한글형태소분석기
스펠체커
자동완성분석기
-PUT store
<pre>
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
</pre>

3. 매핑

-PUT http://localhost:9200/store/_mappings/info
<pre>
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
</pre>

4. 로그스태쉬를 이용한 색인

<pre>
input {
  jdbc {
    jdbc_driver_library => "/home/ec2-user/logstash-6.1.3/mysql-connector-java-5.1.18.jar"
    jdbc_driver_class => "com.mysql.jdbc.Driver"
    jdbc_connection_string => "jdbc:mysql://123.142.190.80:23306/foodblog"
    jdbc_user => "crawl_user"
    jdbc_password => "crawl_user!!"
    statement => "SELECT * FROM naveraddress WHERE not isnull(lat) LIMIT 100000"
    jdbc_paging_enabled => "true"
    jdbc_page_size => "5000"
#    schedule => "* * * * *"
  }
}

#    WHERE id > :sql_last_value
#    use_column_value => true
#    tracking_column => id

filter {
    mutate {
       split => { "category" => ">" }
       add_field => {
	"category1" => "%{[category][0]}"
 	"category2" => "%{[category][1]}"
       }
       split => { "address" => " " }
       add_field => {
	"region" => "%{[address][0]}"
       }
       remove_field => [  "@version", "@timestamp"]
       add_field => {
        "location" => ["%{lat},%{lng}"]
        }
    }
}

output {
  stdout {
        codec => rubydebug
    }
  elasticsearch {
    hosts => ["127.0.0.1:9200"]
        index => "store"
        document_type => "info"
        manage_template => false
  }
}
</pre>