import com.spring.redis.User;
import com.spring.redis.UserBiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;


@Test
@ContextConfiguration(locations = { "classpath:application-aop.xml","classpath:application-propertyFile.xml",
		"classpath:application-redis.xml"})
public class TestRedisAop extends AbstractTestNGSpringContextTests {

	@Autowired
	UserBiz userBiz;

	@Test()
	void testUserGenerator() {

//		User user = new User();
//		user.setAge(18);
//		user.setName("donnie");
//		user.setUserId("111");
//		user.setSex("man");
//		user.setIphone("133");
//		userBiz.insert(user);
//
//		User user1 = userBiz.find("111");
//		System.out.println(user1.toString());
//
//		user1.setAge(20);
//		user1.setName("donnie2");
//		userBiz.update(user1);
//
//		User user2 = userBiz.find("111");
//		System.out.println(user2.toString());
//
//		userBiz.delete("111");
//
//
//		User user3 = userBiz.find("111");
//		System.out.println(user3.toString());

//		List<User> donnie = userBiz.list("donnie");
//		for (User user : donnie) {
//			System.out.println(user);
//		}

		User user = userBiz.find("222", "baby");
		System.out.println(user);

	}

}