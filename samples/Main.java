import java.io.*;

public class Main {
    public static void main(String[] args) {
        try {
            int a = 2;
            int b = 1;
            int c;
            if (b > 1) {
                b--;
                b = 2;
            } else if (b > 1) {
                b--;
            }
            System.out.println(a / b);
        } catch (ArithmeticException e) {
        }
    }
}
