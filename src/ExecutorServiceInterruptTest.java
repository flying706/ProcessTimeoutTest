import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.*;

/*
    测试超时能否中断
 */
public class ExecutorServiceInterruptTest {
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        FutureTask<Integer> task = new FutureTask<Integer>(new Callable<Integer>(){
            public Integer call() throws Exception {
                BlockThread t = new BlockThread();
                //阻塞线程如果设为true，什么都好办，比如读process的输出，设为守护是可以接受的
                t.setDaemon(false);
                t.start();
                return 0;
            }
        });
        System.out.println("开始执行阻塞线程");
        executorService.execute(task);
        try {
            task.get(5l, TimeUnit.SECONDS);
            System.out.println("无法中断阻塞线程");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            //主动中断线程
            System.out.println("主动中断阻塞线程");
            task.cancel(true);
            executorService.shutdown();
            e.printStackTrace();
        }finally {
            executorService.shutdown();
            System.out.println("无法结束整个程序,因为有阻塞的线程存在");
        }
    }

    static class BlockThread extends Thread{
        public void run(){
            try {
                //1,建立udp socket服务。
                DatagramSocket ds = new DatagramSocket(10000);

                //2,创建数据包。
                byte[] buf = new byte[1024];
                DatagramPacket dp = new DatagramPacket(buf,buf.length);
                System.out.println("阻塞线程名为:"+Thread.currentThread().getName()+"---------"+Thread.currentThread().getThreadGroup()+"---"+Thread.currentThread().isDaemon());
                System.out.println("开始阻塞");
                //3,使用接收方法将数据存储到数据包中。
                ds.receive(dp);//阻塞式的。
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
}
