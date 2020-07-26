package cn.people.one.modules.cms.service.impl;

import cn.people.one.modules.base.service.impl.BaseService;
import cn.people.one.modules.cms.model.Vote;
import cn.people.one.modules.cms.service.IVoteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by lml on 2017/3/21.
 */
@Service
@Transactional(readOnly = true)
public class VoteService  extends BaseService<Vote> implements IVoteService{

    @Transactional
    public int delete(Long id) {
        return this.vDelete(id);
    }
}
