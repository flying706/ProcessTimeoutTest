public class TestInterruptSleepInThread {
    public static void main(String[] args) {
        Thread t = new Thread(){
          public void run(){
              try {
                  Thread.sleep(30000l);
              } catch (InterruptedException e) {
                  System.out.println("t was interrupted by main");
              }
          }
        };
        t.start();

        try {
            Thread.sleep(3000l);
            t.interrupt();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
