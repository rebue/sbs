package rebue.sbs.dozer.converter;

import org.dozer.CustomConverter;

import rebue.wheel.idworker.IdWorker3Helper;

/**
 * ID字段自动生成唯一long序列号
 * 
 * @author 张柏子
 *
 */
public class IdGenConverter implements CustomConverter {

    @Override
    public Object convert(Object existingDestinationFieldValue, Object sourceFieldValue, Class<?> destinationClass,
            Class<?> sourceClass) {
        return IdWorker3Helper.nextId();
    }

}
