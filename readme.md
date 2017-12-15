[TOC]

SpringTest框架学习笔记

- 此笔记通过教程一步一步学习而记录,有bug很正常

# 1. Spring简介

- spring为代码"解耦"
  - 主业务逻辑:项目专用业务,复用性低
  - 系统级业务逻辑:日志,安全,事务等,无专用业务,复用性高

## 1.1 spring特点

- 非侵入式:不会在业务逻辑(POJO层)上出现
- 容器
- IOC:Inversion fo Control:创建实例由spring完成而不是调用者
  - 控制反转的实现方式有依赖查找和依赖注入
    - 依赖查找:Dependency Lookup
    - 依赖注入:Dependency Injection:spring使用,spring与bean之间通过配置文件联系
- AOP:Aspect Orent Programming:面向切面编程

#2. spring快速入门

##2.1 导包

1. spring-framework-5.0.2.RELEASE
   1. 至少是sping框架的四个基本包:beans,core,context,spEL(expression)

   2. maven中引入spring

      1. ```xml
         <dependency>
             <groupId>org.springframework</groupId>
             <artifactId>spring-core</artifactId>
             <version>5.0.2.RELEASE</version>
         </dependency>
         <dependency>
             <groupId>org.springframework</groupId>
             <artifactId>spring-context</artifactId>
             <version>5.0.2.RELEASE</version>
         </dependency>
         <dependency>
             <groupId>org.springframework</groupId>
             <artifactId>spring-beans</artifactId>
             <version>5.0.2.RELEASE</version>
         </dependency>
         <dependency>
             <groupId>org.springframework</groupId>
             <artifactId>spring-expression</artifactId>
             <version>5.0.2.RELEASE</version>
         </dependency>
         ```
2. 三个依赖库
   1. commons-logging.jar
      1. 日志记录与的规范,相当于slf4j.jar的作用
   2. log4j.jar
      1. 日志记录的实现
   3. junit.jar
      1. 测试包

##2.2 定义接口与实体类

- 也就是service方法,业务接口与业务类

## 2.3创建spring配置文件

- idea的右键菜单中就有


- 名称建议:applicationContext.xml

- 配置文件的约束为`%spring%/schema/beans/spring-beans.xsd`

- ```xml
      <!--定义一个实例对象,一个实例对应一个bean-->
      <bean id="studentService" class="com.sqm.service01.IStudentServiceImpl"/>
  id - 此bean实例的唯一标识
  class - 此bean所属的类,只能是类而不能是接口
  ```

##2.4 定义测试类 

```java
    @org.junit.Test
    public void test(){
        //获取容器
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        //从容器中获取对象
        IStudentService service = (IStudentService) context.getBean("studentService");
        service.some();
    }
```

- spring容器:ApplicationContext用于加载spring配置文件

  - 配置文件在项目中的类路径下:通过`ClassPathXmlApplicationContext(String path)`加载

  - 配置文件在本地目录中:通过`FileSystemXmlApplicationContext(String absoultPath)`加载

  - 配置文件在项目的根路径下(与src同级):

    - ```java
      ApplicationContext context = new FileSystemXmlApplicationContext("applicationContext.xml");
      //这里的地址是以项目为基准的相对的地址
      ```

- ~~BeanFactory接口容器~~:也可用于生成spring容器,此接口是ApplicationContext接口的父类**(此方法过时了)**

  - 使用实现类XmlBeanFactory加载配置文件

  - XmlBeanFactory中的构造方法参数Resource为spring配置文件

    - ```java
      public XmlBeanFactory(Resource resource)
      ```

    - Resource是一个接口,其实现类有ClassPathResource(指定类路径的Resource文件)与FileSystemResource(指定根目录或本地磁盘文件)

      - ```java
        构造:
        public ClassPathResource(java.lang.String path)
        public FileSystemResource(java.io.File file)
        ```

    - ​

  - 两种容器的区别

    - 两种容器对象不是同一个对象,即不是同一个容器,**装配时机不同**
    - ApplicationContext容器在容器初始化时将所有对象都装配好,"饿汉式"
      - 执行效率高,占用内存高
    - ~~BeanFactory容器~~采用延迟加载策略,在**第一次**调用getBean()时装配对应对象

# 3. spring 分段学习

## 3.1 Bean的装配

- 即Bean对象的创建,容器根据代码要求创建Bean对象后再传递给代码的过程

### 3.1.1. 默认装配方式

- getBean()**从容器中**获取指定对象,先调用无参构造器

### 3.1.2 动态工厂Bean

- 通过工厂类创建Bean实例而不是直接使用spring容器来装配Bean对象,此时工厂类将与Bean类耦合在一起

  1. 将动态工厂Bean作为普通Bean使用

     - 在配置文件中注册动态工厂Bean,容器通过getBean获取动态工厂对象,再由动态工厂对象获取目标对象;**此时目标对象不由spring容器来管理**

     - ```xml
       <bean id="factory" class="com.sqm.service02.ServiceFactory"/>
       ```

     - ```java
       public class ServiceFactory{
       	public IUserService getUserSedrvice{
           	return new UserServiceImpl();
           }
       }
       ```

     - 缺点是工厂类与目标类耦合在一起,测试类也与工厂耦合

  2. 使用Spring的动态工厂Bean

     - Spring有专门的属性定义来使用动态工厂创建Bean

       - bean标签中的factory-bean属性指定**此Bean的**工厂Bean,factory-method属性指定创建此类对象的工厂中的方法;当工厂Bean和目标Bean都配置后,此时测试类中不需要获取工厂Bean对象,可直接获取目标Bean对象

       - ```xml
         <bean id="factory" class="com.sqm.service02.ServiceFactory"/>
         <bean id="userService" factory-bean="factory" factory-method="getUserService"/>
         ```

### 3.1.3 静态工厂Bean

- 通过工厂模式的静态工厂来创建实例Bean:创建Bean的方法是静态的

- 此时**无需在配置文件中配置工厂Bean,**,但仍然需要指定目标对象Bean,此时bean的class属性值为工厂类,factory-method为静态方法,不指定factory-bean了

  - ```xml
    <bean id = "userService2" class="com.sqm.service02.ServiceFactory" factory-method="getUserService"/>
    ```

### 3.1.4 容器中的bean作用域

- 通过bean标签的scope属性指定作用域,以下是能取的5中作用域值:

  1. **singleton**:单态模式,默认是此模式;在整个spring容器中,此bean是单例
  2. **prototype**:原型模式;每次getBean()都是新的实例
  3. request:对每个HTTP请求,生成一个新的实例
  4. session:对每个不同的HTTPsession,生成一个新的实例
  5. global session:当使用portlet集群时有效,多个web应用共享一个session;一般应用中等效于session

- scope值为3,4,5作用域时只针对web应用中的spring,才有效

- scope="singleton"时,此bean在容器创建时就装配好

- scope="prototype"时,使用实例时才装配

- ```xml
  <bean id ="userService3" class="com.sqm.serive02.UserServiceImpl" scope="singleton"/>
  <bean id ="userService4" class="com.sqm.serive02.UserServiceImpl" scope="prototype"/>
  ```

### 3.1.5 bean后处理器

- 一种特殊bean,容器中所有bean在初始化时,均会**自动执行**该类的两个方法,因为是自动执行无需程序调用,所以无需指定id属性

- 在bean后处理器的方法中,只要对bean类与类中的方法进行判断,即可扩展或增强指定的bean的指定方法;

- 代码中自定义bean后处理器类,是**实现BeanPostProcessor接口**的类

  - 接口中两个方法分别在目标bean的初始化完毕之前和完毕之后执行,返回功能被扩展或加强的目标bean

  - ```java
    //初始化之前自动调用
    public Object postProcessBeforeInitialization(Object bean,String beanId) throws BeansException
    //初始化之后自动调用
    public Object postProcessAfterInitialization(Object bean,String beanId) throws BeansException
    bean - 系统即将初始化的bean实例
    beanId - 该bean的id值,若没有id则为name值
    两个方法返回bean(即在两个方法中加强后的原bean对象)
    ```

- 例如自定义bean后处理器,重写初始化后执行的方法,加强名为some的方法

- ```java
  //此时参数bean被fianl修饰,一旦赋值则无法改变
  @Override
  public Object postProcessBeforeInitialization(final Object bean,String beanId) throws BeansException {
  	//只针对id为studentService的bean对象
      if("studentService".equals(beanId){
  		//通过动态代理加强bean对象
        	//Proxy.newProxyInstance(ClassLoader classLoader,Class[] interfaces,Method handler)
          Object proxy = Proxy.newPorxyInstance(bean.getClass().getClassLoader(),bean.getClass().getInterfaces(),new InvocationHandler(){
  			//动态代理加强原方法
  			@Override
              public Object invoke(Object proxy,Method method,Object[] args) throws Throwable{
                  if("some".equals(method.getName())){
                    //这里面是加强的方法
  					System.out.println("目标方法执行开始时间" + System.currentTimeMillis());
                    		Object result = method.invoke(bean,args);
  					System.out.println("目标方法执行结束时间" + System.currentTimeMillis());
                    		return result;
                  }
                	//不加强的方法仍然按之前的继续
                	return method.invoke(bean,args);
              }
          });
        return proxy;
      }
  	return bean;
  }
  ```

- ​