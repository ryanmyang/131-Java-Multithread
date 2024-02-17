import java.io.IOException;
import java.io.InputStream;
import java.util.zip.Deflater;
public class blockRunnable implements Runnable{

    byte[] content = null;
    byte[] dict = null;
    byte[] results = null;
    boolean isLast = false;
    int[] jankyInt_resultSize = new int[]{0};
    int[] outputSize = null;
    
    // Need to manually decide dictionary on call
    blockRunnable(byte[] b, byte[] dictionary, byte[] destination, int[] outputSize, boolean isLast) {
        //arraycopy
        int inputLength = b.length;
        this.content = new byte[inputLength];
        System.arraycopy(b, 0, this.content, 0, 0);;
        this.dict= new byte[dictionary.length];
        System.arraycopy(dictionary, 0, this.dict, 0, 0);
        this.results = destination;
        this.isLast = isLast;
        this.outputSize = outputSize;
        
    }

    public void run() {
        Deflater deflater = new Deflater(-1, true);
        deflater.setDictionary(this.dict);
        deflater.setInput(this.content);
        if (this.isLast) {
            deflater.finish();
        }
        // System.out.println("About to deflate"); 
        int compressedSize = deflater.deflate(this.results, 0, this.content.length, Deflater.FULL_FLUSH);
        this.outputSize[0] = compressedSize;
        jankyInt_resultSize[0] = compressedSize;
        deflater.end();
        // if (this.results == null) {
        //     System.out.println("Results is null after deflate");
        // }
        // System.out.println("Size: " + compressedSize);

        // printBytes(this.results);

        // MOVE THIS CODE TO THE AREA AFTER EXECUTOR FINISHES
        // try (System.out) {
        //     System.out.write(this.results);
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }
        // System.out.println(this.results.toString());

    }

    // synchronized private static void printBytes(byte[] bytes) {
    //     for (byte b : bytes) {
    //         System.out.printf("%02X", b); // %02X formats the byte as a two-digit hexadecimal number
    //     }
    //     System.out.println(); 
    // }

    
}
