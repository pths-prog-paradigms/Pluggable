public class Single {
    public static short function(short x) {
        return (short) (x % 2 == 0 ? x / 2 : 3 * x + 1);
    }
}
