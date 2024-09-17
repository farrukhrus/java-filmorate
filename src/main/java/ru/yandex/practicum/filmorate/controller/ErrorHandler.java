package ru.yandex.practicum.filmorate.controller;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private ErrorResponse handleConditionsNotMet(final ConditionsNotMetException e) {
        return new ErrorResponse(
                "Возникла ошибка при валидации запроса",
                e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    private ErrorResponse handleNotFound(final NotFoundException e) {
        return new ErrorResponse(
                "Ресурс не найден",
                e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private ErrorResponse exceptionHandler(final Exception e) {
        return new ErrorResponse(
                "Возникла ошибка",
                e.getMessage()
        );
    }
}
