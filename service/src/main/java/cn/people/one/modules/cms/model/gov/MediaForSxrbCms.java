package cn.people.one.modules.cms.model.gov;

import lombok.Data;

import java.io.Serializable;

/**
 * @author YX
 * @date 2019-03-15
 * @comment
 */
@Data
public class MediaForSxrbCms implements Serializable {
    /**
     * id
     */
    private Long id;
    /**
     * 描述
     */
    private String description;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 文件地址
     */
    private String fileUrl;

    /**
     * 编码类型
     */
    private String encodeType;

    /**
     * 名称 *title
     */
    private String title;
    /**
     * 类型
     */
    private String type;
    /**
     * 图片宽 x
     */
    private String imageWidth;
    /**
     * 图片高 x
     */
    private String imageHeight;
    /**
     * 原始URL  * path
     */
    private String sourceUrl;
    /**
     * 文件大小   *size
     */
    private String fileSize;
    /**
     * 时长
     */
    private String times;

}
