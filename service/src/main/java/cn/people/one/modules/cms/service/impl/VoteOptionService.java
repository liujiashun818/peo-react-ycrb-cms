package cn.people.one.modules.cms.service.impl;

import cn.people.one.core.base.dao.BaseDao;
import cn.people.one.modules.base.service.impl.BaseService;
import cn.people.one.modules.cms.model.VoteOption;
import cn.people.one.modules.cms.service.IVoteOptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by lml on 17-3-15.
 */
@Service
@Transactional(readOnly = true)
@Slf4j
public class VoteOptionService extends BaseService<VoteOption> implements IVoteOptionService {

    @Autowired
    BaseDao dao;

    @Transactional
    public int delete(Long id) {
        return this.vDelete(id);
    }
}
