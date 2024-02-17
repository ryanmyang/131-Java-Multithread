import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.zip.CRC32;
public class Pigzj {
    private static final byte[] default_header = new byte[]{31, -117, 8, 0, 0, 0, 0, 0, 0, -1};
    
        public static void main (String[] args) {
            ExecutorService executor = Executors.newCachedThreadPool();
            CRC32 crc = new CRC32();
            long totalBytes = 0;
            crc.reset();
            // Print header
            try {
                System.out.write(default_header);
            } catch (IOException e) {
                e.printStackTrace();
            }

            int blockSize = 131072;
            int dictSize = 32768;
            // int blockSize = 1024;
            // int dictSize = 256;
            int blocksMade = 0;
            byte[] nextDict = null;
            byte[] lastDict = new byte['\u8000'];
            boolean outOfInput = false;
            ArrayList<byte[]> allOutputs = new ArrayList<byte[]>();
            // Janky way to have reference int values. Using arrays as mutable ints
            ArrayList<int[]> allOutputSizes = new ArrayList<int[]>();
            

            try (InputStream inputStream = System.in) {

                while (true) {
                    byte[] input = new byte[blockSize];

                    // Attempt to read, and mark done when out of bytes to read
                    int amountRead = inputStream.readNBytes(input, 0, blockSize);
                    if (amountRead < blockSize) {
                        System.err.println("amount read < blocksize, blocksMade = " + blocksMade);
                        outOfInput = true;
                    }
                    totalBytes += amountRead;
                    input = Arrays.copyOfRange(input, 0, amountRead);
                    nextDict = Arrays.copyOfRange(input, input.length-dictSize, input.length);
                    
                    // Once one thread's worth of bytes is read in, create thread
                    /////////////

                    // Setup output

                    byte[] output = new byte[blockSize];
                    // Janky way to have reference int values. Using arrays as mutable ints
                    int[] outputSize = new int[]{0};
                    allOutputs.add(output);
                    allOutputSizes.add(outputSize);


                    // Make thread
                    executor.execute(new blockRunnable(input, lastDict, output, outputSize, !outOfInput));
                    Arrays.fill(input, (byte)0);
                    // swap lastdict to point to nextdict to be used next, and nextdict to be an empty array
                    // both point to same
                    lastDict = nextDict;
                    nextDict = new byte[dictSize];
                    blocksMade++;
                    if (outOfInput) {
                        break;
                    }
                }
                executor.shutdown();
                try {
                    if (executor.awaitTermination(15, TimeUnit.MINUTES)) {
                        System.err.println("Executor Terminated");
                        if (allOutputs.size() != allOutputSizes.size()) {System.err.println("Outputs not same");}

                        // Write all bytes in order
                        for(int i = 0; i < allOutputs.size(); i++) {
                            // System.err.println("outputsize: " + allOutputSizes.get(i)[0]);
                            crc.update(allOutputs.get(i), 0, allOutputSizes.get(i)[0]);
                            //TRY TO USE EXECUTOR TO EXECUTE THREADS AND USE AWAITTERMINATION TO RUN CODE AFTER TO WRITE THE DATA
                            if (allOutputs.get(i) == null) {System.err.println("alloutput null for " + i);}
                            if (allOutputSizes.get(i)[0] == 0) {System.err.println("alloutputsizes null for " + i);}


                            if (allOutputs.get(i).length < allOutputSizes.get(i)[0]) {System.err.println("Outputs not same for " + i);}
                            
                            byte[] bytesToWrite = Arrays.copyOfRange(allOutputs.get(i), 0,allOutputSizes.get(i)[0]);
                            System.out.write(bytesToWrite);
                            // System.err.write(bytesToWrite);

                        }
                        long checksumValue = crc.getValue();
                        System.err.println("checksum Value: " + checksumValue);
                        for (int i = 0; i < Integer.BYTES; i++) {
                            byte b = (byte) (checksumValue & 0xFF); 
                            System.out.write(b); 
                            System.out.flush();
                            checksumValue >>= 8;
                        }
                        System.err.println("totalBytes Value: " + totalBytes);
                        for (int i = 0; i < Integer.BYTES; i++) {
                            byte b = (byte) (totalBytes & 0xFF);
                            System.out.write(b); 
                            System.out.flush();
                            totalBytes >>= 8; 
                        }


                    }
                } catch (InterruptedException e) {
                    System.err.println("Interrupted while waiting for tasks to complete.");
                    executor.shutdownNow();
                }
                

            } catch (IOException e) {
            e.printStackTrace();
        }

    }

}