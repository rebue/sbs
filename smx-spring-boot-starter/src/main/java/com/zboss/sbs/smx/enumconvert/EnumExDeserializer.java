package com.zboss.sbs.smx.enumconvert;

import java.lang.reflect.Type;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONLexer;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.zboss.sbs.smx.enumconvert.BaseEnum;

public class EnumExDeserializer implements ObjectDeserializer {

    @SuppressWarnings("unchecked")
    @Override
    public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
        JSONLexer lexer = parser.getLexer();
        int value = lexer.intValue();
        try {
            for (Object constant : Class.forName(type.getTypeName()).getEnumConstants()) {
                BaseEnum enumItem = (BaseEnum) constant;
                if (enumItem.getValue() == value) {
                    return (T) enumItem;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int getFastMatchToken() {
        // TODO Auto-generated method stub
        return 0;
    }

}
