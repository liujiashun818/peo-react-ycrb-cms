package cn.people.one.modules.ask.service;

import cn.people.one.modules.ask.model.AskQuestionPush;
import cn.people.one.modules.ask.model.AskQuestionReply;
import cn.people.one.modules.base.service.IBaseService;

public interface IAskQuestionPushService extends IBaseService<AskQuestionPush> {
    /**
     * 保存问政推送
     * @param askQuestionReply
     */
    Object savePushContent(AskQuestionReply askQuestionReply);
}
