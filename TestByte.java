public class TestByte {   
    public static void main(String[] args) {
        byte b = (byte)46;
        int ub = b & (0xff);
        StdOut.println(ub);
        StdOut.println(0xff);
    }
}