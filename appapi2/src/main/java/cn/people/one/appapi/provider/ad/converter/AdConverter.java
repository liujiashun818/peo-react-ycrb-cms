package cn.people.one.appapi.provider.ad.converter;

import cn.people.one.appapi.provider.ad.model.Ad;
import cn.people.one.appapi.provider.ad.model.AdImage;
import cn.people.one.appapi.provider.ad.model.WebView;
import cn.people.one.appapi.util.NumberUtils;
import cn.people.one.appapi.vo.ArticleVO;

import java.util.*;

/**
 * Created by wilson on 2018-10-26.
 */
public class AdConverter {

    public static ArticleVO toArticleVO(Ad ad) {
        if (ad == null) {
            return null;
        }

        ArticleVO vo = new ArticleVO();
        vo.setId(ad.getId());
        vo.setSysCode("advert");
        vo.setType("advert");
        vo.setArticleId(ad.getId());
        vo.setCategoryId(ad.getChannel());
        vo.setListTitle(ad.getTitle());
        vo.setViewType(NumberUtils.equals(1, ad.getViewType()) ? "advert_2" : "advert_3");
        vo.setTags("广告");
        vo.setPlace(ad.getMessageOrder());
        switch (ad.getClickType()) {
            case 1:
                vo.setLinkType(2);
                break;
            case 2:
                vo.setLinkType(1);
                break;
            case 3:
                vo.setLinkType(31);
                break;
            case 4:
                vo.setLinkType(0);
                break;
        }
        vo.setLink(ad.getClickData());
        vo.setBeginTime(new Date(ad.getStartTime() * 1000));
        vo.setEndTime(new Date(ad.getEndTime() * 1000));
        vo.setAdImages(setImageDesc(ad.getImages()));
        return vo;
    }

    public static List<ArticleVO> toArticleVO(Collection<Ad> ads) {
        if (ads == null || ads.size() < 1) {
            return Collections.emptyList();
        }

        List<ArticleVO> list = new ArrayList<>(ads.size());
        for (Ad ad : ads) {
            ArticleVO vo = toArticleVO(ad);
            if (vo != null) {
                list.add(vo);
            }
        }
        return list;
    }

    public static List<ArticleVO> toArticleVO(List<Ad> ads) {
        if (ads == null || ads.size() < 1) {
            return Collections.emptyList();
        }

        List<ArticleVO> list = new ArrayList<>(ads.size());
        for (Ad ad : ads) {
            ArticleVO vo = toArticleVO(ad);
            if (vo != null) {
                list.add(vo);
            }
        }
        return list;
    }

    public static WebView toWebView(Ad ad) {
        if (ad == null) {
            return null;
        }

        WebView wv = new WebView();
        wv.setId(ad.getId());
        wv.setTagid(ad.getChannel());
        wv.setStartTime(new Date(ad.getStartTime() * 1000));
        wv.setEndTime(new Date(ad.getEndTime() * 1000));
        wv.setType(1);
        wv.setAdvertPosition(5);
        wv.setTitle(ad.getTitle());
        wv.setLink(ad.getClickData());
        wv.setImages(setImageDesc(ad.getImages()));
        wv.setClickType(ad.getClickType());
        return wv;
    }

    public static List<WebView> toWebView(List<Ad> ads) {
        if (ads == null || ads.size() < 1) {
            return Collections.emptyList();
        }

        List<WebView> wvs = new ArrayList<>();
        for (Ad ad : ads) {
            WebView wv = toWebView(ad);
            if (wv != null) {
                wvs.add(wv);
            }
        }
        return wvs;
    }

    private static List<AdImage> setImageDesc(List<AdImage> images) {
        if (images == null || images.size() < 1) {
            return images;
        }

        for (AdImage image : images) {
            switch (image.getSize()) {
                case 1:
                    image.setDesc("480X800");
                    break;
                case 2:
                    image.setDesc("640X960");
                    break;
                case 3:
                    image.setDesc("640X1136");
                    break;
                case 4:
                    image.setDesc("720X1280");
                    break;
                case 5:
                    image.setDesc("1080X1920");
                    break;
                case 6:
                    image.setDesc("480X270");
                    break;
                case 7:
                    image.setDesc("640X360");
                    break;
                case 8:
                    image.setDesc("720X406");
                    break;
                case 9:
                    image.setDesc("1080X608");
                    break;
                case 10:
                    image.setDesc("112X86");
                    break;
                case 11:
                    image.setDesc("180X136");
                    break;
                case 12:
                    image.setDesc("202X154");
                    break;
                case 13:
                    image.setDesc("304X230");
                    break;
                case 14:
                    image.setDesc("480X136");
                    break;
                case 15:
                    image.setDesc("640X180");
                    break;
                case 16:
                    image.setDesc("720X202");
                    break;
                case 17:
                    image.setDesc("1080X304");
                    break;
                case 18:
                    image.setDesc("640X180");
                    break;
                case 19:
                    image.setDesc("1080X303");
                    break;
                default:
                    image.setDesc("unknown");
                    break;
            }
        }

        return images;
    }

}
