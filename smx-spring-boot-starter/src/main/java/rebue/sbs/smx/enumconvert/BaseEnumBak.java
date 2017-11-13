package rebue.sbs.smx.enumconvert;

import com.alibaba.fastjson.JSONAware;

/**
 * 枚举的基础接口<br>
 * 在spring mvc中，如果在Controller中使用了枚举类型接收参数
 */
public interface BaseEnumBak extends JSONAware {
    int getValue();

    default String toJSONString() {
        return Integer.toString(getValue());
    }
}
