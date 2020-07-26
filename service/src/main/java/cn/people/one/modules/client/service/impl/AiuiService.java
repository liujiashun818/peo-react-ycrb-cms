package cn.people.one.modules.client.service.impl;

import cn.people.one.modules.client.model.front.AiuiVO;
import cn.people.one.modules.client.service.IAiuiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by sunday on 2018/10/29.
 */
@Service
@Transactional(readOnly = true)
@Slf4j
public class AiuiService implements IAiuiService {

    /**
     * 智能语音通用提示语
     * @return
     */
    @Override
    public AiuiVO getCueWords(){
        AiuiVO aiuiVO = new AiuiVO();
        String cueWord = "AR扫描,我想看今日新闻,买机票,查天气,我想看本月新闻,订火车票";
        String[] cueWords = cueWord.split(",");
        aiuiVO.setCueWords(cueWords);
        String dialogue = "Hi，我是小晋，我可以和您聊新闻，还可以帮您查天气，您想了解什么？";
        aiuiVO.setDialogue(dialogue);
        return  aiuiVO;
    }
}
