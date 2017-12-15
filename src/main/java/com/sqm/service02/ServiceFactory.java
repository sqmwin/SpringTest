package com.sqm.service02;

/**
 * <p>
 * 工厂类
 * </p>
 *
 * @author sqm
 * @version 1.0
 */
public class ServiceFactory {
    public IUserService getUserService() {
        return new UserServiceImpl();
    }
}
