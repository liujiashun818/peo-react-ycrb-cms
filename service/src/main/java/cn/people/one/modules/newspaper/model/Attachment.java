package cn.people.one.modules.newspaper.model;

import lombok.Data;

/**
 * 附件
 */
@Data
public class Attachment {

    // 图片地址
    private String attachmenturl;
    // 图片文件名称
    private String attachmentorigin;
    // 图片说明
    private String attachmentname;
}
