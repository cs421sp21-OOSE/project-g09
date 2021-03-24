package exceptions;

/**
 * Exception throws by ResultSetMapAccumulator
 */
public class ResultSetMapAccumulatorException extends RuntimeException {

  public ResultSetMapAccumulatorException(String message, Throwable cause) {
    super(message, cause);
  }

  public ResultSetMapAccumulatorException(String message) {
    super(message);
  }
}
