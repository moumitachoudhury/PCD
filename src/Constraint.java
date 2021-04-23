public class Constraint{
    public Constraint(int a, int b, int c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }


    public Constraint(int a, int b, int r1, int r2, int r3, int r4)
    {
        this.a = a;
        this.b = b;
        this.r1 = r1;
        this.r2 = r2;
        this.r3 = r3;
        this.r4 = r4;
    }

    @Override
    public String toString() {
        return "Constraint{" +
                "a=" + a +
                ", b=" + b +
                ", c=" + c +
                '}';
    }

    public int getA() {
        return a;
    }

    public int getB() {
        return b;
    }

    public int getC() {
        return c;
    }

    private int a, b, c;

    public int getR1() {
        return r1;
    }

    public int getR2() {
        return r2;
    }

    public int getR3() {
        return r3;
    }

    public int getR4() {
        return r4;
    }

    private int r1, r2, r3, r4;

}