package rebue.sbx.redis;

import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import rebue.sbs.redis.RedisClient;
import rebue.sbs.redis.RedisConfig;
import rebue.sbs.redis.RedisSetException;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = RedisConfig.class)
public class RedisTests {

    @Resource
    private RedisClient redisClient;

//    @Test
    public void test01() throws RedisSetException {
        Assert.assertFalse(redisClient.exists("123"));
        Assert.assertFalse(redisClient.exists("123".getBytes()));
        // 自增(如果没有找到key，会自动创建key并设置为1)
        Assert.assertEquals(1L, redisClient.incr("123").longValue());
        Assert.assertTrue(redisClient.exists("123"));
        Assert.assertTrue(redisClient.exists("123".getBytes()));
        Assert.assertEquals(2L, redisClient.incr("123").longValue());
        Assert.assertEquals(3L, redisClient.incr("123").longValue());
        Assert.assertEquals(4L, redisClient.incr("123").longValue());
        Assert.assertEquals(5L, redisClient.incr("123").longValue());
        Assert.assertThat(redisClient.del("123"), Matchers.greaterThan(0L));

        // String
        Assert.assertNull(redisClient.get("123"));
        redisClient.set("123", "abc");
        Assert.assertEquals("abc", redisClient.get("123"));
        Assert.assertThat(redisClient.del("123"), Matchers.greaterThan(0L));
        Assert.assertNull(redisClient.get("123"));

        // Object
        Assert.assertNull(redisClient.getObj("123", Student.class));
        Date now = new Date();
        redisClient.setObj("123", new Student(1L, "N001", "张三", (short) 28, now));
        Student student = redisClient.getObj("123", Student.class);
        Assert.assertNotNull(student);
        Assert.assertEquals(1L, student.getId().longValue());
        Assert.assertEquals("N001", student.getNum());
        Assert.assertEquals("张三", student.getName());
        Assert.assertEquals((short) 28, student.getAge());
        Assert.assertEquals(now, student.getBirthday());
        Assert.assertThat(redisClient.del("123"), Matchers.greaterThan(0L));
        Assert.assertNull(redisClient.getObj("123", Student.class));
    }

    /**
     * 测试超时
     */
//    @Test
    public void test02() throws InterruptedException, RedisSetException {
        // String
        redisClient.set("123", "abc", 3);
        Assert.assertTrue(redisClient.get("123").equals("abc"));
        Thread.sleep(3000);
        Assert.assertNull(redisClient.get("123"));

        // Object
        Date now = new Date();
        redisClient.setObj("123", new Student(1L, "N001", "张三", (short) 28, now), 3);
        Student student = redisClient.getObj("123", Student.class);
        Assert.assertNotNull(student);
        Assert.assertEquals(student.getId().longValue(), 1L);
        Assert.assertEquals(student.getNum(), "N001");
        Assert.assertEquals(student.getName(), "张三");
        Assert.assertEquals(student.getAge(), (short) 28);
        Assert.assertEquals(student.getBirthday(), now);
        Thread.sleep(3000);
        student = redisClient.getObj("123", Student.class);
        Assert.assertNull(student);

        // incr
        Assert.assertNull(redisClient.get("123"));
        Assert.assertEquals(1L, redisClient.incr("123", 3).longValue());
        Assert.assertEquals(2L, redisClient.incr("123", 3).longValue());
        Assert.assertEquals(3L, redisClient.incr("123", 3).longValue());
        Thread.sleep(3000);
        Assert.assertNull(redisClient.get("123"));
    }

    /**
     * 测试pop
     */
//    @Test
    public void test03() throws RedisSetException {
        // String
        Assert.assertNull(redisClient.get("123"));
        redisClient.set("123", "abc");
        Assert.assertEquals(redisClient.pop("123"), "abc");
        Assert.assertNull(redisClient.get("123"));

        // Object
        Assert.assertNull(redisClient.getObj("123", Student.class));
        Date now = new Date();
        redisClient.setObj("123", new Student(1L, "N001", "张三", (short) 28, now));
        Student student = redisClient.popObj("123", Student.class);
        Assert.assertNotNull(student);
        Assert.assertEquals(student.getId().longValue(), 1L);
        Assert.assertEquals(student.getNum(), "N001");
        Assert.assertEquals(student.getName(), "张三");
        Assert.assertEquals(student.getAge(), (short) 28);
        Assert.assertEquals(student.getBirthday(), now);
        Assert.assertNull(redisClient.getObj("123", Student.class));
    }

    // 测试模糊查询
    @Test
    public void test04() {
        // String
        Map<String, String> listByWildcard = redisClient.listByWildcard("rebue.suc.svc.user.buy_relation.*");
        System.out.println("模糊查询");
        for (Entry<String, String> item : listByWildcard.entrySet()) {
            System.out.println(item.getKey() + ":" + item.getValue());
        }
    }

}
