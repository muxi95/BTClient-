package dev.sqlite.exception;

/**
 * 所有异常的基类
 * @author Richx
 */
@SuppressWarnings("AlibabaClassMustHaveAuthor")
public class LBException extends Exception {
    private static final long serialVersionUID = 1L;

    public LBException() {
        super();
    }

    public LBException(String detailMessage) {
        super(detailMessage);
    }
}