package rebue.sbs.dubbo;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.dubbo.common.serialize.support.SerializationOptimizer;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;

import rebue.robotech.ro.Ro;

public class SerializationOptimizerImpl implements SerializationOptimizer {

    private final ResourcePatternResolver      _resolver                     = new PathMatchingResourcePatternResolver();
    private final CachingMetadataReaderFactory _cachingMetadataReaderFactory = new CachingMetadataReaderFactory();
    private List<Class<?>>                     _classes;

    @Override
    public Collection<Class<?>> getSerializableClasses() {
        if (_classes != null) {
            return _classes;
        }

        _classes = new LinkedList<>();
        _classes.add(Ro.class);

        addClasses("**/mo/*Mo.class");
        addClasses("**/jo/*Jo.class");
        addClasses("**/ra/*Ra.class");
        addClasses("**/to/*To.class");

        return _classes;
    }

    /**
     * 添加符合条件的类到_classes中
     * 
     * @param condition 搜索类的条件
     */
    private void addClasses(final String condition) {
        try {
            final Resource[] resources = _resolver
                    .getResources(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + condition);
            for (final Resource resource : resources) {
                final MetadataReader metadataReader = _cachingMetadataReaderFactory.getMetadataReader(resource);
                _classes.add(Class.forName(metadataReader.getClassMetadata().getClassName()));
            }
        } catch (final IOException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("优化序列化扫描类时出现异常");
        }
    }

}
