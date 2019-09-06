package rebue.sbs.redis;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest(classes = RedisConfig.class)
public class RedisTests {
    private static final String REDIS_KEY_TEST_PREFIX = "rebue.sbs.redis.test.";

    @Resource
    private RedisClient         redisClient;

    /**
     * 测试一些常用方法
     */
    @Test
    @Disabled
    public void test01() throws RedisSetException {
        log.info("测试一些常用方法");
        Assertions.assertFalse(redisClient.exists("123"));
        Assertions.assertFalse(redisClient.exists("123".getBytes()));
        // 自增(如果没有找到key，会自动创建key并设置为1)
        Assertions.assertEquals(1L, redisClient.incr("123").longValue());
        Assertions.assertTrue(redisClient.exists("123"));
        Assertions.assertTrue(redisClient.exists("123".getBytes()));
        Assertions.assertEquals(2L, redisClient.incr("123").longValue());
        Assertions.assertEquals(3L, redisClient.incr("123").longValue());
        Assertions.assertEquals(4L, redisClient.incr("123").longValue());
        Assertions.assertEquals(5L, redisClient.incr("123").longValue());
        assertThat(redisClient.del("123")).isGreaterThan(0L);

        // String
        Assertions.assertNull(redisClient.get("123"));
        redisClient.set("123", "abc");
        Assertions.assertEquals("abc", redisClient.get("123"));
        assertThat(redisClient.del("123")).isGreaterThan(0L);
        Assertions.assertNull(redisClient.get("123"));

        // Object
        Assertions.assertNull(redisClient.getObj("123", Student.class));
        final Date now = new Date();
        redisClient.setObj("123", new Student(1L, "N001", "张三", (short) 28, now));
        final Student student = redisClient.getObj("123", Student.class);
        Assertions.assertNotNull(student);
        Assertions.assertEquals(1L, student.getId().longValue());
        Assertions.assertEquals("N001", student.getNum());
        Assertions.assertEquals("张三", student.getName());
        Assertions.assertEquals((short) 28, student.getAge());
        Assertions.assertEquals(now, student.getBirthday());
        assertThat(redisClient.del("123")).isGreaterThan(0L);
        Assertions.assertNull(redisClient.getObj("123", Student.class));
    }

    /**
     * 测试超时
     */
    @Test
    @Disabled
    public void test02() throws InterruptedException, RedisSetException {
        log.info("测试超时");
        // String
        redisClient.set("123", "abc", 3);
        Assertions.assertTrue(redisClient.get("123").equals("abc"));
        Thread.sleep(3000);
        Assertions.assertNull(redisClient.get("123"));

        // Object
        final Date now = new Date();
        redisClient.setObj("123", new Student(1L, "N001", "张三", (short) 28, now), 3);
        Student student = redisClient.getObj("123", Student.class);
        Assertions.assertNotNull(student);
        Assertions.assertEquals(student.getId().longValue(), 1L);
        Assertions.assertEquals(student.getNum(), "N001");
        Assertions.assertEquals(student.getName(), "张三");
        Assertions.assertEquals(student.getAge(), (short) 28);
        Assertions.assertEquals(student.getBirthday(), now);
        Thread.sleep(3000);
        student = redisClient.getObj("123", Student.class);
        Assertions.assertNull(student);

        // incr
        Assertions.assertNull(redisClient.get("123"));
        Assertions.assertEquals(1L, redisClient.incr("123", 3).longValue());
        Assertions.assertEquals(2L, redisClient.incr("123", 3).longValue());
        Assertions.assertEquals(3L, redisClient.incr("123", 3).longValue());
        Thread.sleep(3000);
        Assertions.assertNull(redisClient.get("123"));
    }

    /**
     * 测试pop
     */
    @Test
    @Disabled
    public void test03() throws RedisSetException {
        log.info("测试pop");
        // String
        Assertions.assertNull(redisClient.get("123"));
        redisClient.set("123", "abc");
        Assertions.assertEquals(redisClient.pop("123"), "abc");
        Assertions.assertNull(redisClient.get("123"));

        // Object
        Assertions.assertNull(redisClient.getObj("123", Student.class));
        final Date now = new Date();
        redisClient.setObj("123", new Student(1L, "N001", "张三", (short) 28, now));
        final Student student = redisClient.popObj("123", Student.class);
        Assertions.assertNotNull(student);
        Assertions.assertEquals(student.getId().longValue(), 1L);
        Assertions.assertEquals(student.getNum(), "N001");
        Assertions.assertEquals(student.getName(), "张三");
        Assertions.assertEquals(student.getAge(), (short) 28);
        Assertions.assertEquals(student.getBirthday(), now);
        Assertions.assertNull(redisClient.getObj("123", Student.class));
    }

    /**
     * 测试模糊查询
     */
    @Test
    public void test04() {
        log.info("测试模糊查询");

        redisClient.setObj(REDIS_KEY_TEST_PREFIX + "123", new Student(1L, "N001", "张三", (short) 28, new Date()), 3);
        redisClient.setObj(REDIS_KEY_TEST_PREFIX + "124", new Student(2L, "N002", "李四", (short) 28, new Date()), 3);
        redisClient.setObj(REDIS_KEY_TEST_PREFIX + "125", new Student(3L, "N003", "王五", (short) 27, new Date()), 3);
        redisClient.setObj(REDIS_KEY_TEST_PREFIX + "126", new Student(4L, "N004", "赵六", (short) 26, new Date()), 3);
        redisClient.setObj(REDIS_KEY_TEST_PREFIX + "127", new Student(5L, "N005", "钱七", (short) 25, new Date()), 3);
        redisClient.setObj(REDIS_KEY_TEST_PREFIX + "128", new Student(6L, "N006", "孙八", (short) 24, new Date()), 3);

        // String
        final List<Student> listByWildcard = redisClient.listByWildcard(REDIS_KEY_TEST_PREFIX + "*", Student.class);
        for (final Student student : listByWildcard) {
            log.info(student.toString());
        }
    }

    /*
     * 测试缓存Map类的对象
     */
    @Test
    @Disabled
    public void test05() throws RedisSetException {
        log.info("测试缓存Map类的对象");
        final Date now = new Date();
        final Map<String, Object> map1 = new LinkedHashMap<>();
        map1.put("a", "a");
        map1.put("b", 1);
        map1.put("c", true);
        map1.put("d", now);
        redisClient.setObj("test", map1, 10);
        @SuppressWarnings("unchecked")
        final Map<String, Object> map2 = redisClient.getObj("test", Map.class);
        Assertions.assertEquals(map1, map2);
    }

}
