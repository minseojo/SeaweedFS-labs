package writer;

public class Main {

    private static final int ITER = 3; // 반복 횟수

    public static void main(String[] args) throws Exception {
        WriteFilerGrpc writeFilerGrpc = new WriteFilerGrpc();
        WriteLocal     writeLocal     = new WriteLocal();
        WriteFilerHttp writeFilerHttp = new WriteFilerHttp();

        try {
//            bench("Local (write)",      writeLocal::run);
            bench("Filer HTTP (write)", writeFilerHttp::run);
            bench("Filer gRPC (write)", writeFilerGrpc::run);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void bench(String name, IORunnable r) throws Exception {
        for (int i = 1; i <= ITER; i++) {
//            long start = System.currentTimeMillis();
            r.run();
//            long ms = System.currentTimeMillis() - start;
//            System.out.printf("%s run #%d: %d ms%n", name, i, ms);
        }
        System.out.println();
    }

    @FunctionalInterface
    interface IORunnable { void run() throws Exception; }
}
