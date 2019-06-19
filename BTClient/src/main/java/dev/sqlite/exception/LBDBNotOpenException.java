package dev.sqlite.exception;

/**
 * @author Richx
 */
public class LBDBNotOpenException extends LBException {
    private static final long serialVersionUID = 1L;

    public LBDBNotOpenException() {
        super();
    }

    public LBDBNotOpenException(String detailMessage) {
        super(detailMessage);
    }

}
