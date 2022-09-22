package mall.common;

public class ZhongHeMallException extends RuntimeException {

    public ZhongHeMallException() {
    }

    public ZhongHeMallException(String message) {
        super(message);
    }

    /**
     * 丢出一个异常
     *
     * @param message
     */
    public static void fail(String message) {
        throw new ZhongHeMallException(message);
    }

}
