package rebue.sbs.dozer.converter;

import com.github.dozermapper.core.CustomConverter;

import rebue.wheel.idworker.IdWorker3Helper;

/**
 * ID字段自动生成唯一long序列号
 * 
 * @author 张柏子
 *
 */
public class IdGenConverter implements CustomConverter {

    @Override
    public Object convert(final Object existingDestinationFieldValue, final Object sourceFieldValue, final Class<?> destinationClass, final Class<?> sourceClass) {
        return IdWorker3Helper.getId();
    }

}
