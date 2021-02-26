package exception;

/**
 * A generic exception for CRUD operations.
 */
public class DaoException extends RuntimeException {

  /**
   * Constructs a new DaoException.
   *
   * @param message the detail message (which is saved for later retrieval by the getMessage() method).
   * @param cause the cause (which is saved for later retrieval by the getCause() method).
   */
  public DaoException(String message, Throwable cause) {
    super(message, cause);
  }
}
