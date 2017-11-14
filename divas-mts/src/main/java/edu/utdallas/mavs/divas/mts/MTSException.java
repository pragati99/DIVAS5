package edu.utdallas.mavs.divas.mts;

/**
 * Signals that an MTS exception of some sort has occurred. This class is the
 * general class of exceptions produced by failed or interrupted messaging
 * operations.
 * 
 * 
 */
public class MTSException extends Exception
{
    private static final long serialVersionUID = 1L;

    /**
     * The exception, if applicable, that caused the raising of this <code>MTSException</code>. This member provides a means to further
     * investigate the cause of the exception.
     */
    private Exception         cause;

    /**
     * Constructs an <code>MTSException</code> with the specified detail
     * message and causing exception type. The error message string <code>message</code> can later be retrieved by the <code>Throwable.getMessage()</code> method of class
     * <code>java.lang.Throwable</code>.
     * 
     * @param message
     *        the detail message
     * @param cause
     *        if applicable, the exception that caused the creation of the <code>MTSException</code>. This exception may later be
     *        retrieved for further analysis using the <code>getCause()</code> method.
     */
    public MTSException(String message, Exception cause)
    {
        super(message);
        this.cause = cause;
    }

    /**
     * @return the exception that caused the raising of this <code>MTSException</code> or <code>null</code> if the <code>MTSException</code> was not raised based on the occurance
     *         of another exception.
     */
    @Override
    public Exception getCause()
    {
        return cause;
    }
}
