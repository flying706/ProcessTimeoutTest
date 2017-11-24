import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Set;
import java.util.concurrent.*;

/*
    测试超时能否中断
 */
public class ExecutorServiceInterruptSocketTest {
    public static void main(String[] args) throws SocketException {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        final DatagramSocket ds = new DatagramSocket(10000);
        FutureTask<Integer> task = new FutureTask<Integer>(new Callable<Integer>(){
            public Integer call() throws Exception {
                BlockThread t = new BlockThread(ds);
                //阻塞线程如果设为true，什么都好办，比如读process的输出，设为守护是可以接受的
                t.setDaemon(true);
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

        try {
            Thread.sleep(3000l);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //打印所有线程,线程t依然健在，程序永远不结束
        Set<Thread> threads = Thread.getAllStackTraces().keySet();
        for (Thread thread : threads) {
            System.out.println(thread.getName()+"--"+thread.isInterrupted()+"--"+thread.isDaemon());
            if(thread.getName().equals("Thread-0")){
                thread.interrupt();
                ds.close();
            }
        }

        System.out.println("------------repeat-----------------");

        threads = Thread.getAllStackTraces().keySet();
        for (Thread thread : threads) {
            System.out.println(thread.getName()+"--"+thread.isInterrupted()+"--"+thread.isDaemon());
        }
    }

    static class BlockThread extends Thread{
        private DatagramSocket ds;
        public BlockThread(DatagramSocket ds){
            this.ds = ds;
        }
        public void run(){
            try {
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
