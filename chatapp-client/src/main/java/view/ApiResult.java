package view;

import org.springframework.web.ErrorResponse;

public class ApiResult<T> {

    private boolean success;
    private T data;
    private ErrorDTO error;

    public static <T> ApiResult<T> ok(T data) {
        ApiResult<T> result = new ApiResult<>();
        result.success = true;
        result.data = data;

        return result;
    }

    public static <T> ApiResult<T> fail(ErrorDTO error) {
        ApiResult<T> result = new ApiResult<>();
        result.success = false;
        result.error = error;
        return result;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public ErrorDTO getError() {
        return error;
    }

    public void setError(ErrorDTO error) {
        this.error = error;
    }
}
