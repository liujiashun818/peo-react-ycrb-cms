package cn.people.one.appapi.vo.newspaper;

import lombok.Data;

import java.util.List;

/**
 * 报纸文章详情信息
 * @author YX
 * @date 2018/10/23
 * @comment
 */
@Data
public class PaperDetail {
    //文章详情和列表共有字段
    private String id;//文章id: "20181008_56011353",
    private String points;//热区: "382,3;1041,3;1041,233;382,233",
    private String paperName;//报纸编码: "xzrb","cdrb"
    private String pjCode;//: "xzrbc_10_201705",
    private String sysCode="paper";//系统编码: "paper",
    private String articleId;//文章id，同ID: "20181008_56011353",
    private String newsTimestamp;//文章时间: "1538928000000",
    private String newsDatetime;//文章日期: "2018-10-08",
    private String title;//文章标题: "《百家讲坛》将播出特别节目《平“语”近人—习近平总书记用典》",
    private String categoryId;//: "8",
    private String newsLink;//: "1|20181008_56011353"
    //文章详情特有字段
    private List<String> newsClassId;//: [ ],
    private String shortTitle;//: "黑科技每日秀",
    private String introTitle;//: "",
    private String subTitle;//: "“成都造”高精尖产品组团亮相",
    private String type;//: "paper",
    private String description;//: "",
    private String content;//: "<p> </p><p> 依托四川大学华西医院生物治疗国家重点实验室研发的多种免疫基因治疗新品种，在临床试验中已经使病人获益；</p><p> “成都造”放射性栓塞微球，预计年治疗肝癌患者4万例以上；</p><p> 全球首款人工智能语音识别芯片，为家电装上“耳朵”和“嘴巴”，语音识别率高达95%以上……</p><p> 9日，备受期待的2018“双创周”“黑科技每日秀”启动，“成都造”高精尖科技产品组团亮相。</p><p> “黑科技每日秀”围绕人工智能、机器人、生命健康等热门领域，每日发布科技前沿创新产品和设计方案，开展黑科技项目产品体验和黑科技项目路演活动，搭建资本和项目对接的平台、东部和西部互动的平台，全面展示中国创新创业第四城的风采。</p><p> “黑科技每日秀”还打造互动大屏，远程连线全国六大城市“双创周”现场，开展实时直播。同时，现场还带来大量互动体验活动，举办产品相关项目路演和专家测评分享交流会，使观众在第一时间感受到最新科技成果对生产生活带来的改变。</p><p> </p><p> 路演</p><p> 线上线下联动 实现资源西引</p><p> </p><p> 黑科技项目路演活动区域面积约90平方米，集合数字大屏与互动多媒体交互技术，实景化呈现西部路演中心平台，举办黑科技产品、技术发布及项目路演。同时，通过项目演示、邀请全国各地投资人线上连线及点评、大众投资者线上提问等环节，助力西部项目与海内外创新创业资源的互通以及人才、资本的对接，实现资源西引，展现西部“双创”核心动力。</p><p> </p><p> “成都造”钇-90炭微球</p><p> 预计年治疗肝癌患者4万例</p><p> </p><p> 此次路演的成都纽瑞特医疗科技有限公司是一家致力于放射性药物创新与产业化的科技型企业。创新产品包括放射性栓塞微球、放射性标记单抗类药物、钼-99/锝-99发生器、锶-90/钇-90发生器等，将用于治疗晚期肝癌、淋巴癌等疾病，并实现钼-99、钇-90等临床急需的医用放射性同位素国产化。</p><p> “放射性栓塞微球是目前国际上介入治疗不可手术切除的中晚期肝癌最有效的产品。” 成都纽瑞特医疗科技有限公司副总经理葛强介绍，栓塞微球是永久性栓塞剂，尤其是目前已应用于临床的载药微球，不仅可永久性栓塞肿瘤供血动脉，而且微球可装载化疗药物，进入肿瘤内后缓慢释放化疗药物，通过增加瘤体内的化疗药物浓度，而降低全身药物浓度，达到增加疗效及减少毒副反应的目的。</p><p> 葛强介绍，公司研发的钇-90炭微球，是利用活性炭微球吸附放射性钇-90经固化后形成，与国外同类产品相比，该微球放射性比活度高、释放度低，不需要核反应堆辐照，产品技术处于国际领先水平。“1000克的钇-90炭微球产品可以治疗1万例肝癌患者，该产品销售价格为国外产品的30%-50%。产品上市后，将为肝癌患者提供新的生机，预计年治疗肝癌患者4万例以上，销售收入20亿元以上，产生巨大的社会效益和经济效益。”</p><p> </p><p> 国内首款全自主知识产权高通量基因分型系统GeneMatrix</p><p> 每套系统每天处理数万例检测</p><p> </p><p> “瀚辰光翼以‘智造中国基因检测平台，服务中国基因检测事业，跻身全球基因检测市场’为企业使命。”路演现场，成都瀚辰光翼科技有限责任公司博士张晗说，公司组建了一支由领域知名教授、海归博士组成的国际一流研发团队，致力于打造自主创新、自主产权的基因检测仪器、试剂、软件全套生态系统，提供高通量、高度自动化、高性价比的基因检测解决方案。</p><p> 张晗介绍，瀚辰光翼此前已向市场推出了国内首款全自主知识产权高通量基因分型系统GeneMatrix，该产品针对现代农业中的大规模分子育种需求，以及日益快速增长的健康管理基因检测市场，将快速、极高通量、全程自动化、检测精准、高性价比等特性结合在一起，每套系统每天可处理多达数万例检测，并显著降低检测成本。</p><p> 记者了解到，该系统中的Matrix Arrayer 反应板制备仪是一款集高通量加样、微量分液、超声清洗、自动化封装等功能于一体的全自动流体工作站。可将DNA样本和试剂微量分液至高精度微孔板，并对其进行自动化封装，降低试剂消耗，减少人为操作，提高检测精度；Matrix Cycler 高通量水浴热循环仪是一款自动化高通量水浴热循环设备。单次操作可同时对20个高精度微孔板进行热循环扩增。模块化设计，可根据客户的需求调整热循环通量；Matrix Master 数据管理分析系统是一款基因分型数据综合分析管理软件，提供灵活的版面设置和强大的数据管理分析功能，快速获取原始检测数据，实验结果还可导出到Excel。</p><p> </p><p> 将在成都建立</p><p> 免疫与基因治疗的关键性公共平台</p><p> </p><p> “免疫基因治疗是前沿生物技术与医学、免疫学、药学、材料学等多学科交叉融合而形成的针对恶性肿瘤等重大疾病进行临床治疗的新手段，能解决传统药物或治疗手段不能解决的问题，在疾病的治疗方面起到重要作用。”四川大学生物治疗国家重点实验室主任魏于全介绍说，“依托于四川大学华西医院生物治疗国家重点实验室，我们研发了多种免疫基因治疗新品种，在临床试验中已经使病人获益。”魏于全说，在省市创新创业政策的引领下，学校和医院相继出台职务科技成果转化的实施细则，他们的免疫与基因治疗项目成果实现了科技转化，已经获得了企业投资并成立了股份公司。共计转化了78项免疫与基因治疗品种，获得了超过16亿元的投资，科技人员占股比例在45%左右（科技成果估值总额为13.9亿元）。</p><p> “随着一些关键技术的进一步突破，我们将在成都建立免疫与基因治疗的关键性公共平台，面对企业开展开放性的CRO和CMO服务工作，加快促进免疫与基因治疗新技术的发展和新产品的开发。”他表示。</p><p> </p><p> 展出</p><p> </p><p> “成都造”黑科技产品亮点纷呈</p><p> </p><p> 黑科技项目产品体验区域面积约300平方米，展示人工智能、生命健康等领域的“成都造”黑科技产品，亮点纷呈。</p><p> </p><p> 全球首款人工智能语音识别芯片</p><p> 为家电装上 “耳朵”“嘴巴”</p><p> </p><p> 设想一下，当你下班回到家，只需说一句：我回来了，所有家居设备立即苏醒，空调自动开启调到最舒适温度，电视自动播放到你最喜欢的电视剧，窗帘自动拉开映入绚丽的晚霞。你只需慵懒地躺在沙发上，一切都为你时刻待命。现在，这已不是只能在电影里出现的桥段，人工智能的发展让梦想已经开始照进现实。</p><p> “小薇开门。”在黑色的智能微波炉面前，成都外国语学校附属小学（高新美年校区）的学生纷纷喊道，只见微波炉的门立刻就自动打开了。“小薇”内含10大分类菜单，60道美食菜谱。用户说出菜谱名字后，微波炉便可自动调节到该菜单所需的模式，无须选择火候和时间。</p><p> 微波炉搭载了由启英泰伦研发的全球首款人工智能语音识别芯片CI1006，采用了深度神经网络（DNN）、全方位唤醒，能够识别10米远处发出的语音命令，并且从用户说出命令到执行只需0.2秒~0.8秒。CI1006芯片可赋能家电等终端设备，为设备装上“耳朵”和“嘴巴”，实现设备的本地语音识别功能，语音识别率高达95%以上。</p><p> </p><p> 无线充电</p><p> 堪比“最强大脑”</p><p> </p><p> “不需要充电线，只要把手机放在无线充电发射板就可以充电，真的太神奇了。” 活动现场，成都市易冲无线科技有限公司的无线充电发射板引起众多关注。</p><p> 无线充电正成为终端设备创新导入新一轮的“风口”。此次参加展出的易冲无线创新性设计采用了第二代无线充电技术，整套技术产品对于无线充电领域来说，最大的亮点就是在充电位置自由度上得到了完美地提升，带来了手机无线充电全新的用户体验。</p><p> 在接收端，易冲无线的EC4016芯片支持多种无线充电表标准，实现全兼容，可以自动识别发射端充电协议，支持无线充电功率最大可以达到15W，实现了无线快充。“目前市面上的无线充电产品充电需要点对点对准，同时功率过小，大大影响了用户体验。本产品可以达到15W自由位置充电，充电速度可以做到和有线充电一样快。”该公司相关负责人说。</p><p> </p><p> 全球首款无源智能标签</p><p> 可以“猪脸识别”</p><p> </p><p> 此次参加展出的睿畜科技同样惹人眼球。睿畜科技以创新的智能硬件为基础，配合AI算法，解决畜牧行业痛点。公司自主研发的智能解决方案，瞄准规模化养殖效率提升，畜牧金融风控管理与溯源体系升级，赋能万亿级的市场。</p><p> 睿畜科技推出了三套智能养殖解决方案。公司研发的睿畜耳标为全球首款无源智能标签，解决的是生猪的真实身份问题。“传统养殖户给猪投保，由于很难确定猪的唯一身份，普遍存在‘换猪骗保’的问题。智能耳标具有休眠、激活、计时、自毁四项功能，一旦佩戴在猪耳上，会自动激活并创建档案信息，同时取下自毁。这样可以有效实现猪的身份鉴定，降低保险机构的赔付率。”该公司负责人介绍。</p><p> 睿畜医生是目前唯一已落地的生猪可穿戴设备，通过LPWAN传感器与智能算法实现母猪排卵预测。它可以采集母猪核心体温、活动量等体征数据。这些数据会实时上传到云端，通过AI算法分析猪是否处于排卵期，以及是否生病。此外，睿畜天蓬为市面上首款基于物联网与计算机视觉的智能养殖解决方案，实现养殖场精细化管理。</p><p> 本报记者 宋妍妍 摄影 张全能</p>",
    private String copyfrom;//: "成都日报",
    private String authors;//: "宋妍妍",
    private String tags;//: "",
    private List<NewsInfoMedias> medias;//: [],
    private String introduction;//: "",
    private String shareUrl;//: "http://vshare.cdrb.com.cn/#/paper/paper/20181008_5248550583884531193/20181008",
    private String shareSlogan;//: "",
    private List<NewsInfoMedias> imgall;//: [],
    private String cover;//: "http://vimg.people.cn/prod/common/img/d
}