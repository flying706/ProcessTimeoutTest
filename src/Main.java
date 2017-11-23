import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Set;
import java.util.concurrent.*;

public class Main {

    private static Process pkeep;
    private static BufferedReader readerKeep;
    public static void main(String[] args) {
        ExecutorService SINGLE_THREAD = Executors.newSingleThreadExecutor();
        final ProcessBuilder builder = new ProcessBuilder("/bin/sh","-c","/shell/mountshell.sh /testnfs 192.168.1.174:/test");
        FutureTask<Integer> task = new FutureTask<Integer>(new Callable<Integer>(){
            public Integer call() throws Exception {
                Process process = builder.start();
                pkeep = process;

                /*一般人都这样写，结果。。。。程序结束不了
                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new InputStreamReader(process.getInputStream(),"gbk"));
                    readerKeep = reader;
                    String line;
                    System.out.println("readLine");
                    while ((line = reader.readLine()) != null){
                        System.out.println("tasklist: " + line);
                        System.out.println("readLineRepeat");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
                //重点，读取process输出的信息遇到阻塞怎么办？像上边这样肯定是不行的，阻塞的线程如果不是守护线程 是无法随着程序一起结束的，会一直阻碍整个程序结束
                //这里将产生阻塞的代码提出来，作为一个守护线程，问题完美解决
                PrintProcessMsg ppm = new PrintProcessMsg(process);
                ppm.setDaemon(true);
                ppm.start();

                System.out.println("waitFor");
                System.out.println(Thread.currentThread().getName());
                int i = process.waitFor();


                return i;
            }
        });
        SINGLE_THREAD.execute(task);

        try {
            task.get(3l, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
            //真正的阻塞是不会被该语句干掉的
            task.cancel(true);

            pkeep.destroy();
        }finally {
            SINGLE_THREAD.shutdown();
        }
    }


    static class PrintProcessMsg extends Thread{
        private Process process;
        PrintProcessMsg(Process process){
            this.process = process;
        }
        public void run(){
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(process.getInputStream(),"gbk"));
                readerKeep = reader;
                String line;
                System.out.println("readLine");
                while ((line = reader.readLine()) != null){
                    System.out.println("tasklist: " + line);
                    System.out.println("readLineRepeat");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }


}
