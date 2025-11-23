package org.denic_t.web.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.denic_t.web.CheckResult;
import org.denic_t.web.session.SessionState;

import java.io.IOException;

/**
 * ControllerServlet - Контроллер в архитектуре MVC.
 * Определяет тип запроса и делегирует его обработку соответствующему
 * компоненту.
 * Все запросы к приложению должны проходить через этот сервлет.
 */
public class ControllerServlet extends HttpServlet {

    private static final String PARAM_X = "x";
    private static final String PARAM_Y = "y";
    private static final String PARAM_R = "r";
    private static final String PARAM_PAGE = "page";
    private static final String PARAM_PAGE_SIZE = "pageSize";
    private static final String INDEX_JSP = "/index.jsp";
    private static final String AREA_CHECK_SERVLET = "/check";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }



    /**
     * Обрабатывает запросы и определяет, куда их направить.
     * Если запрос содержит параметры координат - направляет на проверку области.
     * Иначе - направляет на главную JSP страницу.
     */
    private void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
                
        boolean hasCoordinates = hasRequiredParameters(request);

        if (hasCoordinates) {
            getServletContext().getRequestDispatcher(AREA_CHECK_SERVLET)
                    .forward(request, response);
        } else {
            exposeSessionState(request);
            applyPagination(request);
            getServletContext().getRequestDispatcher(INDEX_JSP)
                    .forward(request, response);
        }
    }

    /**
     * Проверяет наличие всех необходимых параметров для проверки области.
     * 
     * @param request HTTP запрос
     * @return true, если все параметры присутствуют, иначе false
     */
    private boolean hasRequiredParameters(HttpServletRequest request) {
        String x = request.getParameter(PARAM_X);
        String y = request.getParameter(PARAM_Y);
        String r = request.getParameter(PARAM_R);

        return x != null && !x.trim().isEmpty() &&
                y != null && !y.trim().isEmpty() &&
                r != null && !r.trim().isEmpty();
    }

    private void exposeSessionState(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }
        SessionState state = new SessionState(session);
        state.getLastX().ifPresent(v -> request.setAttribute("lastX", v));
        state.getLastY().ifPresent(v -> request.setAttribute("lastY", v));
        state.getLastR().ifPresent(v -> request.setAttribute("lastR", v));

        state.getErrorMessage().ifPresent(msg -> {
            request.setAttribute("errorMessage", msg);
            state.clearErrorMessage();
        });

        if (state.getFeedbackStatus().isPresent() && state.getFeedbackMessage().isPresent()) {
            request.setAttribute("feedbackStatus", state.getFeedbackStatus().get());
            request.setAttribute("feedbackMessage", state.getFeedbackMessage().get());
            state.getFeedbackResult().ifPresent(res -> {
                request.setAttribute("feedbackResult", res);
                // clear only the result here, status/message cleared below
                session.removeAttribute(SessionState.ATTR_FEEDBACK_RESULT);
            });
            session.removeAttribute(SessionState.ATTR_FEEDBACK_STATUS);
            session.removeAttribute(SessionState.ATTR_FEEDBACK_MESSAGE);
        } else {
            state.getFeedbackResult().ifPresent(res -> {
                request.setAttribute("feedbackResult", res);
                session.removeAttribute(SessionState.ATTR_FEEDBACK_RESULT);
            });
        }
    }

    private void applyPagination(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }

        SessionState state = new SessionState(session);

        java.util.List<CheckResult> allResults = state.getCheckResults();
        if (allResults.isEmpty()) {
            request.setAttribute("pagedResults", java.util.Collections.emptyList());
            request.setAttribute("currentPage", 1);
            request.setAttribute("totalPages", 0);
            request.setAttribute("pageSize", 10);
            request.setAttribute("totalCount", 0);
            return;
        }

        int totalCount = allResults.size();

        Integer sessionPageSize = state.getPageSize().orElse(null);
        Integer sessionPage = state.getPage().orElse(null);

        int pageSize = parsePositiveIntOrDefault(request.getParameter(PARAM_PAGE_SIZE),
                sessionPageSize != null ? sessionPageSize : 10, 1, 1000);
        int page = parsePositiveIntOrDefault(request.getParameter(PARAM_PAGE),
                sessionPage != null ? sessionPage : 1, 1, Integer.MAX_VALUE);

        int totalPages = (pageSize > 0) ? (int) Math.ceil(totalCount / (double) pageSize) : 0;
        if (totalPages == 0) {
            page = 1;
        } else if (page > totalPages) {
            page = totalPages;
        }

        // Индексы для слайса (стабильный порядок добавления, без сдвигов)
        int fromIndex = Math.max(0, Math.min(totalCount, (page - 1) * pageSize));
        int toIndex = Math.max(fromIndex, Math.min(totalCount, fromIndex + pageSize));
    java.util.List<CheckResult> paged = allResults.subList(fromIndex, toIndex);

        // Экспорт атрибутов в запрос
        request.setAttribute("pagedResults", paged);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("pageSize", pageSize);
        request.setAttribute("totalCount", totalCount);

        // Сохраняем настройки пагинации в сессию.
        state.setPage(page);
        state.setPageSize(pageSize);
    }

    private int parsePositiveIntOrDefault(String raw, int defaultValue, int min, int max) {
        if (raw == null || raw.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            int val = Integer.parseInt(raw.trim());
            if (val < min) return min;
            if (val > max) return max;
            return val;
        } catch (NumberFormatException ignored) {
            return defaultValue;
        }
    }
}