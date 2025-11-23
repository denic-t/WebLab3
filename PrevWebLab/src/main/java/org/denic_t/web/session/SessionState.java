package org.denic_t.web.session;

import jakarta.servlet.http.HttpSession;
import org.denic_t.web.CheckResult;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * SessionState provides a typed facade for HttpSession attributes
 * 
 */
public class SessionState implements Serializable {
    public static final String ATTR_RESULTS = "checkResults";
    public static final String ATTR_LAST_X = "lastX";
    public static final String ATTR_LAST_Y = "lastY";
    public static final String ATTR_LAST_R = "lastR";
    public static final String ATTR_ERROR_MESSAGE = "errorMessage";
    public static final String ATTR_FEEDBACK_STATUS = "feedbackStatus";
    public static final String ATTR_FEEDBACK_MESSAGE = "feedbackMessage";
    public static final String ATTR_FEEDBACK_RESULT = "feedbackResult";
    public static final String ATTR_PAGE = "page";
    public static final String ATTR_PAGE_SIZE = "pageSize";

    private final HttpSession session;

    public SessionState(HttpSession session) {
        this.session = session;
    }

    @SuppressWarnings("unchecked")
    public List<CheckResult> getCheckResults() {
        Object value = session.getAttribute(ATTR_RESULTS);
        if (value == null) return Collections.emptyList();
        if (value instanceof List<?>) {
            return (List<CheckResult>) value;
        }
        return Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    public List<CheckResult> getOrCreateCheckResults() {
        Object value = session.getAttribute(ATTR_RESULTS);
        if (value instanceof List<?>) {
            return (List<CheckResult>) value;
        }
        List<CheckResult> list = new ArrayList<>();
        session.setAttribute(ATTR_RESULTS, list);
        return list;
    }

    public void addCheckResult(CheckResult result) {
        List<CheckResult> list = getOrCreateCheckResults();
        list.add(result);
    }

    public Optional<String> getLastX() { return getString(ATTR_LAST_X); }
    public Optional<String> getLastY() { return getString(ATTR_LAST_Y); }
    public Optional<String> getLastR() { return getString(ATTR_LAST_R); }
    public void setLastX(String x) { session.setAttribute(ATTR_LAST_X, x); }
    public void setLastY(String y) { session.setAttribute(ATTR_LAST_Y, y); }
    public void setLastR(String r) { session.setAttribute(ATTR_LAST_R, r); }

    public Optional<String> getErrorMessage() { return getString(ATTR_ERROR_MESSAGE); }
    public void setErrorMessage(String message) { session.setAttribute(ATTR_ERROR_MESSAGE, message); }
    public void clearErrorMessage() { session.removeAttribute(ATTR_ERROR_MESSAGE); }

    public Optional<String> getFeedbackStatus() { return getString(ATTR_FEEDBACK_STATUS); }
    public Optional<String> getFeedbackMessage() { return getString(ATTR_FEEDBACK_MESSAGE); }
    public Optional<CheckResult> getFeedbackResult() {
        Object value = session.getAttribute(ATTR_FEEDBACK_RESULT);
        if (value instanceof CheckResult) {
            return Optional.of((CheckResult) value);
        }
        return Optional.empty();
    }
    public void setFeedbackStatus(String status) { session.setAttribute(ATTR_FEEDBACK_STATUS, status); }
    public void setFeedbackMessage(String message) { session.setAttribute(ATTR_FEEDBACK_MESSAGE, message); }
    public void setFeedbackResult(CheckResult result) { session.setAttribute(ATTR_FEEDBACK_RESULT, result); }
    public void clearFeedback() {
        session.removeAttribute(ATTR_FEEDBACK_STATUS);
        session.removeAttribute(ATTR_FEEDBACK_MESSAGE);
        session.removeAttribute(ATTR_FEEDBACK_RESULT);
    }

    public Optional<Integer> getPage() { return getInteger(ATTR_PAGE); }
    public Optional<Integer> getPageSize() { return getInteger(ATTR_PAGE_SIZE); }
    public void setPage(int page) { session.setAttribute(ATTR_PAGE, page); }
    public void setPageSize(int pageSize) { session.setAttribute(ATTR_PAGE_SIZE, pageSize); }

    private Optional<String> getString(String name) {
        Object value = session.getAttribute(name);
        return (value instanceof String) ? Optional.of((String) value) : Optional.empty();
    }

    private Optional<Integer> getInteger(String name) {
        Object value = session.getAttribute(name);
        return (value instanceof Integer) ? Optional.of((Integer) value) : Optional.empty();
    }
}
