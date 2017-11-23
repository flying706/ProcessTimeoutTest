import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Set;

public class TestInterruptSocketReceiveInThread {
    public static void main(String[] args) {
        Thread t = new Thread(){
            public void run(){
                System.out.println("t's thread name is:"+Thread.currentThread().getName());
                try {
                    //1,建立udp socket服务。
                    DatagramSocket ds = new DatagramSocket(10000);

                    //2,创建数据包。
                    byte[] buf = new byte[1024];
                    DatagramPacket dp = new DatagramPacket(buf,buf.length);

                    //3,使用接收方法将数据存储到数据包中。
                    ds.receive(dp);//阻塞式的。
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        };
        t.start();

        try {
            Thread.sleep(3000l);
            //想中断一个真正阻塞的线程，什么都没发生
            t.interrupt();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //打印所有线程,线程t依然健在，程序永远不结束
        Set<Thread> threads = Thread.getAllStackTraces().keySet();
        for (Thread thread : threads) {
            System.out.println(thread.getName());
        }
    }
}
