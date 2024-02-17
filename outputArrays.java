import java.util.Arrays;

public class outputArrays {
    byte[] arr = null;
    int size = 0;
    outputArrays(byte[] data, int length) {
        this.arr = Arrays.copyOfRange(data, 0, length);
        this.size = 0;
    }
}
