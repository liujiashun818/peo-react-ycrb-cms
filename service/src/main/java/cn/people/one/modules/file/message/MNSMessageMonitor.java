package cn.people.one.modules.file.message;

import cn.people.one.core.util.text.StringUtils;
import cn.people.one.modules.file.model.ActivityList;
import cn.people.one.modules.file.model.MediaInfo;
import cn.people.one.modules.file.model.MessageBody;
import cn.people.one.modules.file.service.impl.MediaInfoService;
import com.aliyun.mns.client.MNSClient;
import com.aliyun.mns.model.Message;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.mts.model.v20140618.QueryMediaListByURLRequest;
import com.aliyuncs.mts.model.v20140618.QueryMediaListByURLResponse;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

/**
 * User: 张新征
 * Date: 2017/3/5 17:15
 * Description:
 */
@Slf4j
@Component
public class MNSMessageMonitor implements CommandLineRunner {

    @Autowired
    private MNSClient client;

    @Autowired
    private IAcsClient acsClient;

    @Value("${aliyun.oss.mediaEndpoint}")
    private String mediaEndpoint;

    @Value("${aliyun.oss.location}")
    private String location;

    @Value("${aliyun.oss.queue}")
    private String queue;

    @Value("${theone.project.code}")
    private String code;

    @Autowired
    private MediaInfoService mediaInfoService;

    @Override
    public void run(String... strings) throws Exception {
        MessageReceiver receiver = new MessageReceiver(1, client, queue);
        while (true) {
            Message message = receiver.receiveMessage();
            log.info("阿里云消息》"+message);
            log.info("阿里云转码消息》messageId:"+message.getMessageId());
            log.info("阿里云转码消息》messageBody为===>>>" + message.getMessageBody());

            try {
                //将消息保存到数据库中
                Gson gson = new Gson();
                MessageBody messageBody = gson.fromJson(message.getMessageBody(), MessageBody.class);
                if ("Report".equals(messageBody.getType()) && "Success".equals(messageBody.getState())) {//判断消息为转码成功的消息
                    String name = messageBody.getMediaWorkflowExecution().getInput().getInputFile().getObject();
                    if(StringUtils.isBlank(name) || !name.contains(code)){
                        continue;
                    }
                    if (!mediaEndpoint.endsWith("/")) {
                        mediaEndpoint += "/";
                    }
                    String fileUrl = mediaEndpoint + name;//存储音视频文件路径
                    QueryMediaListByURLRequest request = new QueryMediaListByURLRequest();
                    request.setFileURLs(fileUrl);
                    request.setIncludePlayList(true);
                    QueryMediaListByURLResponse response = acsClient.getAcsResponse(request);
                    //获取媒体类型文件源文间及转码文件的信息
                    QueryMediaListByURLResponse.Media media = response.getMediaList().get(0);
                    log.info("媒体信息流为===>>>"+gson.toJson(media));
                    MediaInfo mediaInfo = mediaInfoService.fetch(media.getTitle());
                    if (null != mediaInfo) {
                        List<ActivityList> list = messageBody.getMediaWorkflowExecution().getActivityList();
                        mediaInfo.setTransTime(list.get(list.size()-1).getEndTime());//文件转码成功的时间
                        mediaInfo.setStatus(1);//1转码成功
                        mediaInfo.setBitRate(media.getBitrate());//原文件码率
                        mediaInfo.setMediaId(media.getMediaId());//媒体id
                        mediaInfo.setCover(media.getCoverURL());//视频截图
                        if (media.getPlayList().size() == 3) {
                            for (QueryMediaListByURLResponse.Media.Play play : media.getPlayList()) {
                                if (play.getActivityName().contains("hd")) {
                                    mediaInfo.setHdUrl(play.getFile().getURL());//高清url
                                    String paySize = play.getSize();
                                    if(paySize != null && !"".equals(paySize)) {
                                        mediaInfo.setHdSize(Long.valueOf(paySize));//高清大小
                                    }
                                    mediaInfo.setHdBitRate(play.getBitrate());//高清码率
                                } else if (play.getActivityName().contains("sd")) {
                                    mediaInfo.setSdUrl(play.getFile().getURL());//标清url
                                    String paySize = play.getSize();
                                    if(paySize != null && !"".equals(paySize)) {
                                        mediaInfo.setSdSize(Long.valueOf(paySize));//标清大小
                                    }
                                    mediaInfo.setSdBitRate(play.getBitrate());//标清码率
                                } else if (play.getActivityName().contains("ld")) {
                                    mediaInfo.setLdUrl(play.getFile().getURL());//流畅url
                                    String paySize = play.getSize();
                                    if(paySize != null && !"".equals(paySize)) {
                                        mediaInfo.setLdSize(Long.valueOf(paySize));//流畅大小
                                    }
                                    mediaInfo.setLdBitRate(play.getBitrate());//流畅码率
                                }
                            }
                        } else if (media.getPlayList().size() == 2) {
                            for (QueryMediaListByURLResponse.Media.Play play : media.getPlayList()) {
                                if (play.getActivityName().equals("MP3-64")) {
                                    mediaInfo.setMp3SmallUrl(play.getFile().getURL());// mp3-64 url
                                    String paySize = play.getSize();
                                    if(paySize != null && !"".equals(paySize)) {
                                        mediaInfo.setMp3SmallSize(Long.valueOf(paySize));// mp3-64 size
                                    }
                                    mediaInfo.setMp3SmallBitRate(play.getBitrate());// mp3-64 码率
                                } else if (play.getActivityName().equals("MP3-128")) {
                                    mediaInfo.setMp3BigUrl(play.getFile().getURL());//mp3-128url
                                    String paySize = play.getSize();
                                    if(paySize != null && !"".equals(paySize)) {
                                        mediaInfo.setMp3BigSize(Long.valueOf(paySize));//mp3-128 size
                                    }
                                    mediaInfo.setMp3BigBitRate(play.getBitrate());// mp3-128 码率
                                }
                            }
                        }
                        if (StringUtils.isNotBlank(media.getDuration())) {
                            mediaInfo.setDuration(media.getDuration());
                        }
                        mediaInfoService.save(mediaInfo);
                    }
                }
            } catch (Exception e){
                log.error("媒体转码转码失败 do sleep 30秒",e);
                Thread.sleep(30 * 1000);
            }

            //删除消息
            try {
                client.getQueueRef(queue).deleteMessage(message.getReceiptHandle());
            } catch (Exception e) {
                log.error("媒体转码转码失败");
            }
        }
    }

}
