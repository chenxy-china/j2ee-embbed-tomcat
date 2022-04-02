
import java.io.File;
import java.io.IOException;
import java.io.Writer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.Context;
import org.apache.catalina.Host;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.core.StandardContext;

public final class App {
    File tmpDir = new File("D:\\embbed-tomcat");
    Tomcat tomcat = new Tomcat();

    public static void main(String[] args) throws Throwable {
        System.out.println("运行");
        new App().init();
    }
    

    private void init() throws Throwable {
        Runtime.getRuntime().addShutdownHook(new Thread(this.Shutdown_Runnable));
        test();
    }

    public Runnable Shutdown_Runnable = new Runnable() {
        @Override
        public void run() {
            try {
                System.out.println("退出操作");
                tomcat.destroy();
            } catch (LifecycleException e) {
                e.printStackTrace();
            }
        }
    };
    
    private void test() throws Throwable {
        tomcat.setBaseDir(tmpDir.getAbsolutePath()); // 设置工作目录 D:\embbed-tomcat
        tomcat.setHostname("localhost"); // 主机名, 调用tomcat.start()后将生成目录: {工作目录}/work/Tomcat/{主机名}/ROOT
        System.out.println("工作目录: " + tomcat.getServer().getCatalinaBase().getAbsolutePath());
        //以上相当于配置了tomcat安装目录/conf/server.xml中的Host
        //指定了一个类似tomcat安装目录下的webapps，位置是{工作目录}/work/Tomcat/{主机名}/
        //{工作目录}/work/Tomcat/{主机名}/ROOT就相当于 tomcat安装目录/webapps/ROOT
        
        tomcat.setPort(80);
        Connector conn = tomcat.getConnector(); // Tomcat 9.0 必须调用 Tomcat#getConnector() 方法之后才会监听端口
        System.out.println("连接器设置完成: " + conn);
        //以上相当于配置了tomcat安装目录/conf/server.xml中的port
        
        // addContext(String contextPath, String docBase)
        // contextPath要使用的上下文映射，""表示根上下文
        // docBase上下文的基础目录，用于静态文件。相对于服务器主目录
        // 必须手动创建  {工作目录}/webapps/{docBase}
        //Context ctx = tomcat.addContext("/emb", /*{webapps}/~*/ "/embedtomcat");
        
        // 如果使用Context ctx = tomcat.addContext("", null); 则不用手动创建上面文件夹
        Context ctx = tomcat.addContext("", null);
        //以上设置说明使用context编程模式,没有默认的web.xml可用
        //以上相当于配置了tomcat安装目录/conf/server.xml中的 Context
        //contextPath对应网址的http://localhost/{contextPath}
        //docBase对应服务器主目录，类似指定tomcat安装目录下的webapps/{docBase}/
        //注意{工作目录}/webapps/{docBase}目录必须存在，不然启动tomcat失败
        //调用tomcat.start()成功后会在{工作目录}/work/Tomcat/{主机名}/下生成{contextPath}同名目录
        
        Tomcat.addServlet(ctx, "globalServlet",globalServlet);
        ctx.addServletMappingDecoded("/", "globalServlet");
        //增加一个URL和servlet之间的映射
        
        tomcat.start();
        System.out.println("tomcat 已启动");
        tomcat.getServer().await();
    }
    
    HttpServlet globalServlet = new HttpServlet() {
        private static final long serialVersionUID = 1L;

        @Override
        protected void service(HttpServletRequest request, HttpServletResponse response)
                throws ServletException, IOException {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/plain");
            response.setHeader("Server", "Embedded Tomcat");
            try (Writer writer = response.getWriter()) {
                writer.write("Hello, Embedded Tomcat!");
                writer.flush();
            }
        }
    };
}
