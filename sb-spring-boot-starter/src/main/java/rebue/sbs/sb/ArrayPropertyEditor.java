package rebue.sbs.sb;

import java.beans.PropertyEditorSupport;
import java.lang.reflect.Array;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistry;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ArrayPropertyEditor extends PropertyEditorSupport implements PropertyEditorRegistrar {

    @Override
    public String getAsText() {
        log.info("getAsText: {}", getValue());
        return super.getAsText();
    }

    @Override
    public void setAsText(final String text) throws IllegalArgumentException {
        log.info("setAsText: {}", text);
        if (StringUtils.isNotBlank(text)) {
            setValue(text);
        }
        else {
            setValue(null);
        }
    }

    @Override
    public void registerCustomEditors(final PropertyEditorRegistry registry) {
        registry.registerCustomEditor(Array.class, this);
    }

}
