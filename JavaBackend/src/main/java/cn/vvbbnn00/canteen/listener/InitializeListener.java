package cn.vvbbnn00.canteen.listener;

import cn.vvbbnn00.canteen.service.SystemService;
import cn.vvbbnn00.canteen.util.LogUtils;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class InitializeListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContextListener.super.contextInitialized(sce);

        LogUtils.info("Servlet, 起動！");
        try {
            SystemService systemService = new SystemService();
            systemService.init();
        } catch (Exception e) {
            LogUtils.error(e.getMessage(), e);
            return;
        }
        LogUtils.info("Servlet init succeeded.");
    }
}