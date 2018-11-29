package ai.asymmetric.PGS.SandBox;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Classe com um For paralelo. 
// Baseado no exemplo apresentado pelo usu�rio  Weimin Xiao
// na seguinte Thread de discuss�o do StackOverflow:
// http://stackoverflow.com/questions/4010185/parallel-for-for-java

/* Exemplo de uso:

    public static void main(String [] argv) {
        
        Loop.withIndex(0, 9, new Loop.Each() {
            public void run(int i) {
                System.out.println(i*10);
            }
        });
    }


*/


public class Loop {
    public interface Each {
        void run(int i);
    }

    private static final int CPUs = Runtime.getRuntime().availableProcessors();

    public static void withIndex(int start, int stop, final Each body) {
        int chunksize = (stop - start + CPUs - 1) / CPUs;
        int loops = (stop - start + chunksize - 1) / chunksize;
        ExecutorService executor = Executors.newFixedThreadPool(CPUs);
        final CountDownLatch latch = new CountDownLatch(loops);
        for (int i=start; i<stop;) {
            final int lo = i;
            i += chunksize;
            final int hi = (i<stop) ? i : stop;
            executor.submit(new Runnable() {
                public void run() {
                    for (int i=lo; i<hi; i++)
                        body.run(i);
                    latch.countDown();
                }
            });
        }
        try {
            latch.await();
        } catch (InterruptedException e) {}
        executor.shutdown();
    }

}