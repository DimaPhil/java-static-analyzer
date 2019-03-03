public class B {
    final static int Max = 1000_000_000;

    public int foo(int unused) {
        System.out.println("Unused variable");
        int used = 2;
        int a = 3;
        return used + a;
    }

    public int foo2(int used) {
        System.out.println("Used variable");
        int a = 3;
        return used + a;
    }

    public void main(String[] args) {
        System.out.println("Hello, world!");
    }
}