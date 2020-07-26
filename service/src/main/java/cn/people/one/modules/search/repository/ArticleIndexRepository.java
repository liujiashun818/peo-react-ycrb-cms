package cn.people.one.modules.search.repository;


import cn.people.one.modules.search.model.ArticleIndexData;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ArticleIndexRepository extends ElasticsearchRepository<ArticleIndexData, Long> {
	
}
