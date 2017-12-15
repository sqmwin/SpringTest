package com.sqm.service02;

/**
 * <p>
 * 静态工厂类
 * </p>
 *
 * @author sqm
 * @version 1.0
 */
public class ServiceFactory2 {
    public static IUserService getUserService(){
        return new UserServiceImpl();
    }
}
