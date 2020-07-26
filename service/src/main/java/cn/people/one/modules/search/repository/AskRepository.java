package cn.people.one.modules.search.repository;

import cn.people.one.modules.search.model.AskIndexData;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author YX
 * @date 2019-06-17
 * @comment
 */
public interface AskRepository extends ElasticsearchRepository<AskIndexData, Long> {
}
