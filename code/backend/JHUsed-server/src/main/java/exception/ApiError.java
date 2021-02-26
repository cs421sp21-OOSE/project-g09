package exception;

/**
 * A generic exception for API errors.
 */
public class ApiError extends RuntimeException {
  private final int status;

  /**
   * Construct ApiError.
   *
   * @param message Error message.
   * @param status API response code.
   */
  public ApiError(String message, int status) {
    super(message);
    this.status = status;
  }

  /**
   * Get Error code.
   *
   * @return the API response (error) code.
   */
  public int getStatus() {
    return status;
  }
}
