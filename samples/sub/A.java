public class A {
    void foo() {
        int a = 0;
        for (int i = 0; i < 10; i++) {
            a++;
        }
        int b;
        int c = 1;
        System.out.println(c);
    }

    void foo2() {
        int a = 1;
        try {
            a = 2;
            b = 3;
        } catch (ArithmeticException exc) {
            System.out.println(exc);
        } catch (IOException ignored) {

        } catch (Throwable exc) {
        }
    }
}
