package org.denic_t.web.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.denic_t.web.CheckResult;
import org.denic_t.web.ValidationException;
import org.denic_t.web.service.AreaCheckService;
import org.denic_t.web.session.SessionState;

import java.io.IOException;
 

/**
 * AreaCheckServlet - сервлет для проверки попадания точки в заданную область.
 * Обрабатывает запросы с координатами, выполняет проверку и формирует HTML-страницу с результатами.
 * Сохраняет результаты проверки в HTTP-сессии пользователя.
 */
public class AreaCheckServlet extends HttpServlet {

    // Session keys are managed by SessionState
    private final AreaCheckService areaCheckService = new AreaCheckService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }


    /**
     * Обрабатывает запрос на проверку попадания точки в область.
     */
    private void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    HttpSession session = request.getSession(true);
    SessionState state = new SessionState(session);
    state.setLastX(request.getParameter("x"));
    state.setLastY(request.getParameter("y"));
    state.setLastR(request.getParameter("r"));

        try {
        String source = request.getParameter("source");
        CheckResult result = areaCheckService.check(
            request.getParameter("x"),
            request.getParameter("y"),
            request.getParameter("r"),
            source
        );

            state.addCheckResult(result);
            state.setLastX(String.valueOf(result.getX()));
            state.setLastY(String.valueOf(result.getY()));
            state.setLastR(String.valueOf(result.getR()));
            state.clearErrorMessage();
            state.setFeedbackStatus("success");
            state.setFeedbackMessage("Запрос успешно выполнен.");
            state.setFeedbackResult(result);
        } catch (ValidationException e) {
            state.setErrorMessage(e.getMessage());
            state.setFeedbackStatus("error");
            state.setFeedbackMessage(e.getMessage());
            session.removeAttribute(SessionState.ATTR_FEEDBACK_RESULT);
        }

        response.sendRedirect(request.getContextPath() + "/controller");
    }

    
}