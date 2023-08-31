package com.example.map.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public enum AddressResource {
    // 专有名词直接拼音解决，翻译费力不讨好
    SAN_FANG_GONG_ZUO_ZHAN("三方工作站", "sanfanggongzuozhan"),
    JIAN_KANG_YI_ZHAN("健康驿站", "jiankangyizhan"),
    // NUAN_XIN_YI_ZHAN("暖心驿站", "nuanxinyizhan"),
    MU_YING_GUAN_AI_SHI("母婴关爱室", "muyingguanaishi"),
    ZHI_GONG_ZHI_JIA("职工之家", "zhigongzhijia"),
    ZHI_GONG_SHU_WU("职工书屋", "zhigongshuwu"),
    JIE_DAO_FU_WU_ZHAN("街道服务站", "jiedaofuwuzhan"),
    TIAO_JIE_ZHONG_XIN("调解中心", "tiaojiezhongxin"),
    JI_TI_XIE_SHANG("集体协商", "jitixieshang");

    private final String name;
    private final String resourceName;
}
