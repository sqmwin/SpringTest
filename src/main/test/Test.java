import com.sqm.service01.IStudentService;
import com.sqm.service02.IUserService;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.core.io.FileSystemResource;

/**
 * <p>
 * 测试类
 * </p>
 *
 * @author sqm
 * @version 1.0
 */
public class Test {
    @org.junit.Test
    public void test01(){
        //获取容器 通过指定项目目录下的路径
        ApplicationContext context = new FileSystemXmlApplicationContext("applicationContext.xml");
        System.out.println("已获取容器");
        //从容器中获取对象(IOC)(依赖注入DI)
        IStudentService service = (IStudentService) context.getBean("studentService");

        service.some();

        //StudentServiceImpl : no parameter constructor
        //已获取容器
        //do some()
    }

    @org.junit.Test
    //maven中暂时无法通过此方法获取容器
    public void test02(){
        //获取容器 通过类路径
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        //通过容器获取对象
        IStudentService service = (IStudentService) context.getBean("studentService");
        service.some();
    }

    @org.junit.Test
    //此方法过时了
    public void test03(){
        //通过构造BeanFactory获取容器
        BeanFactory factory = new XmlBeanFactory(new FileSystemResource("applicationContext.xml"));
        System.out.println("已获取容器");
        IStudentService service = (IStudentService) factory.getBean("studentService");
        service.some();

        //已获取容器
        //StudentServiceImpl : no parameter constructor
        //do some()
    }

    @org.junit.Test
    public void test04(){
        //获取容器
        ApplicationContext context = new FileSystemXmlApplicationContext("applicationContext2.xml");

        //获取对象
        IUserService userService = (IUserService) context.getBean("userService");

        userService.doSome();
        userService.doOther();
    }

    @org.junit.Test
    public void test05() {
        //通过spring配置中已经设置好的工厂类与目标类来获取对象
        ApplicationContext context = new FileSystemXmlApplicationContext("applicationContext2.xml");
        IUserService userService = (IUserService) context.getBean("userService");
        userService.doSome();
        userService.doOther();
    }

    @org.junit.Test
    public void test06() {
        //静态工厂,代码无耦合
        ApplicationContext context = new FileSystemXmlApplicationContext("applicationContext2.xml");
        IUserService userService = (IUserService) context.getBean("userService2");
        userService.doSome();
        userService.doOther();
    }

    @org.junit.Test
    public void test07(){
        //scope不同,singleton只有一个实例,prototype每次调用都有一个新的实例
        ApplicationContext context = new FileSystemXmlApplicationContext("applicationContext2.xml");
        //singleton
        IUserService userService3 = (IUserService) context.getBean("userService3");
        IUserService userService32 = (IUserService) context.getBean("userService3");
        System.out.println(userService3 == userService32);
        //prototype
        IUserService userService4 = (IUserService) context.getBean("userService4");
        IUserService userService42 = (IUserService) context.getBean("userService4");
        System.out.println(userService4 == userService42);
    }

}

