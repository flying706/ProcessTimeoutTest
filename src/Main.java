import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.*;

public class Main {

    /*private static Process tlp = null;
    private static FutureTask ft;
    public static void main1(String[] args) throws Exception {
        try {
            int returnCode = timedCall(new Callable<Integer>() {
                public Integer call() throws Exception {
                    Process process = Runtime.getRuntime().exec("mount -t nfs 192.168.1.174:/test");
                    //Process process = Runtime.getRuntime().exec("notepad.exe");
                    tlp = process;
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null)
                        System.out.println("tasklist: " + line);
                    return process.waitFor();
                }
            }, 5l, TimeUnit.SECONDS);
            System.out.println("ok");
        } catch (TimeoutException e) {
            System.out.println("timeout");
            ft.cancel(true);
            tlp.destroy();
            SINGL_THREAD.shutdown();
            System.out.println("done");
        }finally {
            System.out.println("finally");

        }
    }

    private static  ExecutorService SINGL_THREAD = Executors.newSingleThreadExecutor();
    private static final ExecutorService THREAD_POOL = Executors.newCachedThreadPool();

    private static <T> T timedCall(Callable<T> c, long timeout, TimeUnit timeUnit)
            throws InterruptedException, ExecutionException, TimeoutException
    {
        FutureTask<T> task = new FutureTask<T>(c);
        SINGL_THREAD.execute(task);
        ft=task;
        return task.get(timeout, timeUnit);
    }*/


    private static Process pkeep;
    private static BufferedReader readerKeep;
    public static void main(String[] args) {
        ExecutorService SINGL_THREAD = Executors.newSingleThreadExecutor();
        //final ProcessBuilder builder = new ProcessBuilder("notepad.exe");
        final ProcessBuilder builder = new ProcessBuilder("/bin/sh","-c","/mountshells/mountshell.sh /data/data8 192.168.1.174:/test");
        FutureTask<Integer> task = new FutureTask<Integer>(new Callable<Integer>(){
            public Integer call() throws Exception {
                Process process = builder.start();
                pkeep = process;
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                readerKeep = reader;
                String line;
                while ((line = reader.readLine()) != null)
                    System.out.println("tasklist: " + line);
                return process.waitFor();
            }
        });
        System.out.println("task excute");
        SINGL_THREAD.execute(task);

        try {
            System.out.println("task get");
            task.get(5l, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
            try {
                System.out.println(readerKeep.readLine());
                readerKeep.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            pkeep.destroy();
        }finally {
            System.out.println("shutdown");
            SINGL_THREAD.shutdown();
        }
    }
}
