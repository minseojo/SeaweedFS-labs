package reader;

public class Main {

    private static final int ITER = 3;      // 반복 횟수

    public static void main(String[] args) throws Exception {
        ReadLocal readLocal = new ReadLocal();
        ReadFilerHttp readFilerHttp = new ReadFilerHttp();
        ReadFilerGrpc readFilerGrpc = new ReadFilerGrpc();


        bench("Filer gRPC", readFilerGrpc::run);
        bench("Local", readLocal::run);
        bench("Filer HTTP", readFilerHttp::run);
    }

    private static void bench(String name, IORunnable r) throws Exception {
        for (int i = 1; i <= ITER; i++) {
            long startTime = System.currentTimeMillis();
            r.run();
            long endTime = System.currentTimeMillis() - startTime;
            System.out.printf("%s run #%d: %d ms%n", name, i, endTime);
        }
        System.out.println();
    }

    @FunctionalInterface
    interface IORunnable { void run() throws Exception; }
}
