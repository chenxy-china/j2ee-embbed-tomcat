import org.apache.catalina.startup.Tomcat;

public final class App2 {
    private static int port = 8080;
    private static String contextPath = "/test";

    public static void main(String[] args) throws Throwable {
        System.out.println("运行");
        Tomcat tomcat = new Tomcat();
        
        String baseDir = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        System.out.println("baseDir:" + baseDir);
        
        tomcat.setBaseDir(baseDir);
        System.out.println("工作目录: " + tomcat.getServer().getCatalinaBase().getAbsolutePath());
        // 以上定义了工作目录，就在当前类编译成的字节码文件的目录
        // 比如C:/Users/chenxy/eclipse-workspace/testj2ee-maven/target/classes/

        tomcat.setPort(port);
        // tomcat.setConnector(new Connector());  tomcat9.0以下用这个

        tomcat.addWebapp(contextPath, baseDir);
        // 相当于增加一个web应用到主机的应用目录(通常是tomcat的webapps目录)
        // contextPath相当于你的http://localhost/{contextPath}
        // baseDir是对应http://localhost/{contextPath}的基准目录
        
        // tomcat.enableNaming();
        // 和使能JNDI相关

        // 手动创建  tomcat9.0以上用这个
        tomcat.getConnector();

        tomcat.start();
        tomcat.getServer().await();
    }
}
