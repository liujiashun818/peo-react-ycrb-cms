/**   
* @Title: AskGovernmentServiceTest.java 
* @Package cn.people.one.modules.cms.service 
* @Description: TODO(用一句话描述该文件做什么) 
* @author Administrator
* @date 2019年2月20日 下午4:11:59 
* @version V1.0   
*/
package cn.people.one.modules.cms.service;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import cn.people.one.modules.ask.service.IAskGovernmentService;
import lombok.extern.slf4j.Slf4j;

/** 
* @ClassName: AskGovernmentServiceTest 
* @Description: TODO(这里用一句话描述这个类的作用) 
* @author Administrator
* @date 2019年2月20日 下午4:11:59 
*  
*/
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Slf4j()
public class AskGovernmentServiceTest
{
    @Autowired
    IAskGovernmentService service;
    
    @Test
    public void test() {
        /**

 govid=0
GovernmentIds=null
govid=11
GovernmentIds=[374, 371, 564, 559, 3375, 3388, 3389, 3386, 3387, 3384, 3385, 3382, 3383, 3359, 558, 561, 3358, 3361, 3360, 3363, 3362, 3365, 3364, 3367, 3366, 3369, 3368, 560, 660, 3371, 3370, 3373, 3372, 3342, 661, 658, 3343, 3344, 3345, 3346, 3347, 3348, 3349, 3350, 3351, 3352, 3353, 3354, 3355, 659, 664, 3356, 3357, 3329, 3328, 3327, 3326, 665, 662, 3333, 3332, 3331, 3330, 3337, 3336, 663, 668, 3335, 3334, 3341, 3340, 3339, 3338, 3312, 3313, 3310, 3311, 3316, 669, 666, 3317, 3314, 3315, 3320, 3321, 3318, 3319, 3324, 3325, 3322, 3323, 3754, 3755, 667, 672, 3752, 3753, 3750, 3751, 3748, 3749, 3746, 3747, 3744, 3745, 3742, 3743, 3740, 3741, 673, 670, 3771, 3770, 3769, 3768, 3767, 3766, 3765, 3764, 3763, 3762, 3761, 3760, 3759, 3758, 3757, 3756, 3722, 671, 645, 3723, 3724, 3725, 3718, 3719, 3720, 3721, 3714, 3715, 3716, 3717, 3710, 3711]
govid=43
GovernmentIds=[671, 645, 3723, 3724, 3725, 3718, 3719, 3720, 3721, 3714, 3715, 3716, 3717, 3710, 3711]
govid=645
GovernmentIds=[645]
govid=371
GovernmentIds=[371]
         */
        log.info("start");//143
        List<Long> list=service.traverseChild(17L);
        log.info(list.size()+"");//143
        list=service.traverseChild(17L);
        log.info(list.size()+"");//143
        list=service.traverseChild(17L);
        log.info(list.size()+"");//143
        list=service.traverseChild(17L);
        log.info(list.size()+"");//143
        list=service.traverseChild(17L);
        log.info(list.size()+"");//143
        
        list=service.traverseChild(0L);
        log.info(list+"");//0
        list=service.traverseChild(63L);
        log.info(list.size()+"");//15
        
    }
}
