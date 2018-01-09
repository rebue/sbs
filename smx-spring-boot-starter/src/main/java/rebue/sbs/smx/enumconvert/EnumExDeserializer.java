package rebue.sbs.smx.enumconvert;

import java.lang.reflect.Type;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONLexer;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;

public class EnumExDeserializer implements ObjectDeserializer {

	@SuppressWarnings("unchecked")
	@Override
	public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
		if (fieldName == null)
			return null;
		JSONLexer lexer = parser.getLexer();
		int value = lexer.intValue();
		try {
			for (Object constant : Class.forName(type.getTypeName()).getEnumConstants()) {
				BaseEnumBak enumItem = (BaseEnumBak) constant;
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
		return 0;
	}

}
