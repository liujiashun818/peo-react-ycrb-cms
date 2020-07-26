package cn.people.one.modules.search.repository;


import cn.people.one.modules.search.model.AskSuggestData;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface AskSuggestRepository extends ElasticsearchRepository<AskSuggestData, Long> {
	
}
