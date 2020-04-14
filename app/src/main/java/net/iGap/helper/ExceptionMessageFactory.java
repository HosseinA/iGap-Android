package net.iGap.helper;

import com.google.gson.Gson;

import net.iGap.api.errorhandler.ErrorModel;

import java.io.IOException;
import java.net.SocketTimeoutException;

import retrofit2.HttpException;

public class ExceptionMessageFactory {

    public static ErrorModel getMessage(Throwable throwable) {
        ErrorModel errorResponse = new ErrorModel();
        errorResponse.setThrowable(throwable);

        if (throwable instanceof HttpException) {
            HttpException httpException = (HttpException) throwable;

            if (httpException.response() != null && httpException.response().errorBody() != null) {
                try {
                    String errorBody = new String(httpException.response().errorBody().bytes());
                    errorResponse = new Gson().fromJson(errorBody, ErrorModel.class);
                    return errorResponse;
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (throwable instanceof SocketTimeoutException) {
            errorResponse.setMessage("اتمام زمان درخواست");
        } else {
            errorResponse.setMessage("خطای نامشخص");
        }

        HelperLog.setErrorLog(new Exception(throwable.getMessage()));

        return errorResponse;
    }
}