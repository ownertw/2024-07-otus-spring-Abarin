package ru.otus.hw.controllers;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import ru.otus.hw.exceptions.EntityNotFoundException;

@ControllerAdvice
public class MvcExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    public ModelAndView handleNotFoundException(EntityNotFoundException e) {
        ModelAndView modelAndView = new ModelAndView("err404");
        modelAndView.addObject("errors", e.getMessage());
        return modelAndView;
    }
}
