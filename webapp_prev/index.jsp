<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="org.denic_t.web.CheckResult" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.text.DecimalFormatSymbols" %>
<%@ page import="java.util.Locale" %>
<%!
    private static String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "")
                .replace("\n", "\\n");
    }
%>
<%
    String selectedXRaw = (String) request.getAttribute("lastX");
    Double selectedXValue = null;
    if (selectedXRaw != null) {
        try {
            selectedXValue = Double.valueOf(selectedXRaw);
        } catch (NumberFormatException ignored) {
            selectedXValue = null;
        }
    }

    String yValue = (String) request.getAttribute("lastY");
    String rValue = (String) request.getAttribute("lastR");
    String serverError = (String) request.getAttribute("errorMessage");
    String feedbackStatusRaw = (String) request.getAttribute("feedbackStatus");
    String feedbackMessageRaw = (String) request.getAttribute("feedbackMessage");
    CheckResult feedbackResult = (CheckResult) request.getAttribute("feedbackResult");
    String feedbackStatusEscaped = feedbackStatusRaw != null ? feedbackStatusRaw.replace("\\", "\\\\").replace("\"", "\\\"") : "";
    String feedbackMessageEscaped = feedbackMessageRaw != null
            ? feedbackMessageRaw
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "")
                .replace("\n", "\\n")
                .replace("</", "<\\/")
            : "";
    String feedbackDetailX = null;
    String feedbackDetailY = null;
    String feedbackDetailResult = null;
    String feedbackDetailExecutionTime = null;
    String feedbackDetailCheckTime = null;
    Boolean feedbackDetailHit = null;
    if (feedbackResult != null) {
        DecimalFormat decimalFormat = new DecimalFormat("0.########", DecimalFormatSymbols.getInstance(Locale.US));
        feedbackDetailX = decimalFormat.format(feedbackResult.getX());
        feedbackDetailY = decimalFormat.format(feedbackResult.getY());
        feedbackDetailResult = feedbackResult.isHit() ? "Попадание" : "Промах";
        feedbackDetailExecutionTime = String.valueOf(feedbackResult.getExecutionTimeNanos());
        feedbackDetailCheckTime = feedbackResult.getTimestamp().toString();
        feedbackDetailHit = feedbackResult.isHit();
    }

    // Параметры пагинации из атрибутов запроса с безопасными значениями по умолчанию
    Integer currentPageAttr = (Integer) request.getAttribute("currentPage");
    Integer totalPagesAttr = (Integer) request.getAttribute("totalPages");
    Integer pageSizeAttr = (Integer) request.getAttribute("pageSize");
    Integer totalCountAttr = (Integer) request.getAttribute("totalCount");
    int currentPage = currentPageAttr != null ? currentPageAttr.intValue() : 1;
    int totalPages = totalPagesAttr != null ? totalPagesAttr.intValue() : 0;
    int pageSize = pageSizeAttr != null ? pageSizeAttr.intValue() : 10;
    int totalCount = totalCountAttr != null ? totalCountAttr.intValue() : 0;
%>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Лабораторная работа №2</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

<header id="main-header">
    <h1>Ткачев Денис Владимирович</h1>
    <p>Группа: P3211 | Вариант: 929292</p>
</header>

<main>
    <div id="feedback-backdrop" class="feedback-backdrop" aria-hidden="true"></div>
    <div id="feedback-modal" class="feedback-modal" role="dialog" aria-modal="true" aria-hidden="true">
        <div class="feedback-modal__content">
            <h3 class="feedback-modal__title"></h3>
            <p class="feedback-modal__message"></p>
            <div class="feedback-modal__details" aria-hidden="true">
                <table class="feedback-modal__details-table">
                    <thead>
                        <tr>
                            <th>X</th>
                            <th>Y</th>
                            <th>Результат</th>
                            <th>Время (нс)</th>
                            <th>Время проверки</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td data-field="x">—</td>
                            <td data-field="y">—</td>
                            <td data-field="result">—</td>
                            <td data-field="executionTime">—</td>
                            <td data-field="checkTime">—</td>
                        </tr>
                    </tbody>
                </table>
            </div>
            <button type="button" class="feedback-modal__button" onclick="hideFeedbackModal()">Хорошо</button>
        </div>
    </div>

    <div id="page-size-modal" class="feedback-modal" role="dialog" aria-modal="true" aria-hidden="true">
        <div class="feedback-modal__content">
            <h3 class="feedback-modal__title">Размер страницы</h3>
            <div class="input-group" style="margin-top: 10px;">
                <input type="number" id="page-size-input" min="1" max="1000" value="<%= pageSize %>" style="width:120px;">
                <input type="hidden" id="page-size-current-page" value="<%= currentPage %>">
            </div>
            <div style="display:flex; gap: 8px; flex-wrap: wrap; margin-top: 8px;">
                <button type="button" class="page-size-preset" data-size="10">10</button>
                <button type="button" class="page-size-preset" data-size="25">25</button>
                <button type="button" class="page-size-preset" data-size="50">50</button>
                <button type="button" class="page-size-preset" data-size="100">100</button>
            </div>
            <div style="display:flex; gap: 10px; margin-top: 16px;">
                <button type="button" class="page-size-modal__cancel">Отмена</button>
                <button type="button" class="page-size-modal__save">Сохранить</button>
            </div>
        </div>
    </div>

    <!-- Форма для ввода координат -->
    <div class="container" id="input-form">
        <h2>Проверка попадания точки в область</h2>

        <% if (serverError != null && !serverError.isEmpty()) { %>
        <div class="server-error"><%= serverError %></div>
        <% } %>
        
        <form id="coordinates-form" method="GET" action="controller">
            <input type="hidden" name="source" value="form">
            <!-- Выбор X (Checkbox) -->
            <div class="input-group">
                <label>Координата X:</label>
                <div class="checkbox-group">
                    <% double[] xValues = {-2, -1.5, -1, -0.5, 0, 0.5, 1, 1.5, 2}; %>
                    <% for (double xVal : xValues) { %>
                        <label class="checkbox-label">
                            <input type="checkbox" name="x" value="<%= xVal %>" onchange="handleXSelection(this)" <%= (selectedXValue != null && Math.abs(selectedXValue - xVal) < 1e-9) ? "checked" : "" %>>
                            <span><%= xVal %></span>
                        </label>
                    <% } %>
                </div>
                <div class="error-message" id="x-error"></div>
            </div>

            <!-- Ввод Y (Text) -->
            <div class="input-group">
                <label for="y-input">Координата Y (-3 ... 3):</label>
                <input type="text" id="y-input" name="y" placeholder="Введите Y от -3 до 3" value="<%= yValue != null ? yValue : "" %>">
                <div class="error-message" id="y-error"></div>
            </div>

            <!-- Ввод R (Text) -->
            <div class="input-group">
                <label for="r-input">Радиус R (1 ... 4):</label>
                <input type="text" id="r-input" name="r" placeholder="Введите R от 1 до 4" value="<%= rValue != null ? rValue : "" %>">
                <div class="error-message" id="r-error"></div>
            </div>

            <button type="submit" id="submit-btn">Проверить</button>
        </form>
    </div>

    <!-- График области -->
    <div class="container" id="plot-container">
        <h2>График области</h2>
        <div class="plot-wrapper">
            <svg id="plot" width="400" height="400" xmlns="http://www.w3.org/2000/svg" onclick="handlePlotClick(event)">
                <!-- Определяем маркер стрелки -->
                <defs>
                    <marker id="arrow" markerWidth="10" markerHeight="10" refX="9" refY="3" 
                            orient="auto" markerUnits="strokeWidth" fill="#333">
                        <polygon points="0,0 0,6 9,3" />
                    </marker>
                </defs>
                
                <g transform="translate(200, 200) scale(1, -1)">
                    <!-- Закрашенная область -->
                    <path class="area-shape" d="M 0,0 L 150,0 L 150,150 L 0,150 Z" />
                    <path class="area-shape" d="M 0,0 L 75,0 L 0,-150 Z" />
                    <path class="area-shape" d="M 0,0 L -75,0 A 75,75 0 0 1 0,-75 Z" />

                    <!-- Оси координат -->
                    <line class="axis" x1="-190" y1="0" x2="190" y2="0" marker-end="url(#arrow)"/>
                    <line class="axis" x1="0" y1="-190" x2="0" y2="190" marker-end="url(#arrow)"/>

                    <!-- Деления на осях -->
                    <g class="axis-marks">
                        <!-- Метки на оси X -->
                        <line x1="-150" y1="-5" x2="-150" y2="5"/>
                        <line x1="-75" y1="-5" x2="-75" y2="5"/>
                        <line x1="75" y1="-5" x2="75" y2="5"/>
                        <line x1="150" y1="-5" x2="150" y2="5"/>
                        
                        <!-- Метки на оси Y -->
                        <line x1="-5" y1="-150" x2="5" y2="-150"/>
                        <line x1="-5" y1="-75" x2="5" y2="-75"/>
                        <line x1="-5" y1="75" x2="5" y2="75"/>
                        <line x1="-5" y1="150" x2="5" y2="150"/>
                    </g>

                    <!-- Подписи осей -->
                    <g class="axis-labels" transform="scale(1, -1)">
                        <!-- Ось X -->
                        <text x="185" y="15">x</text>
                        <text x="150" y="15">R</text>
                        <text x="75" y="15">R/2</text>
                        <text x="-75" y="15">-R/2</text>
                        <text x="-150" y="15">-R</text>
                        <!-- Ось Y -->
                        <text x="-15" y="-180">y</text>
                        <text x="-25" y="-150">R</text>
                        <text x="-35" y="-75">R/2</text>
                        <text x="-45" y="75">-R/2</text>
                        <text x="-35" y="150">-R</text>
                    </g>
                    
                    <!-- Группа для точек результатов -->
                    <g id="result-points"></g>
                </g>
            </svg>
        </div>
    </div>

    <!-- Параметры пагинации -->
    <div class="container" id="pagination-controls">
        <h2>Пагинация результатов</h2>
        <div class="pagination-actions" style="margin-bottom: 8px;">
            <button type="button" id="open-page-size-modal">Размер страницы</button>
        </div>
        <div class="pagination-nav">
            <form method="GET" action="controller" style="display:inline-block;">
                <input type="hidden" name="pageSize" value="<%= pageSize %>">
                <input type="hidden" name="page" value="1">
                <button type="submit" <%= (currentPage <= 1 ? "disabled" : "") %>>« Первая</button>
            </form>
            <form method="GET" action="controller" style="display:inline-block;">
                <input type="hidden" name="pageSize" value="<%= pageSize %>">
                <input type="hidden" name="page" value="<%= Math.max(1, currentPage - 1) %>">
                <button type="submit" <%= (currentPage <= 1 ? "disabled" : "") %>>‹ Предыдущая</button>
            </form>
            <span class="pagination-status">
                Стр. <strong><%= currentPage %></strong>
                из <strong><%= totalPages %></strong>
                (всего <strong><%= totalCount %></strong>)
                <% int fromItem = totalCount == 0 ? 0 : ((currentPage - 1) * pageSize + 1);
                   int toItem = Math.min(currentPage * pageSize, totalCount); %>
                <span style="margin-left:8px;">Элементы <strong><%= fromItem %></strong>–<strong><%= toItem %></strong></span>
            </span>
            <form method="GET" action="controller" style="display:inline-block; margin-left: 10px;">
                <input type="hidden" name="pageSize" value="<%= pageSize %>">
                <input type="number" name="page" min="1" value="<%= currentPage %>" style="width: 80px;">
                <button type="submit">Перейти</button>
            </form>
            <form method="GET" action="controller" style="display:inline-block;">
                <input type="hidden" name="pageSize" value="<%= pageSize %>">
                <input type="hidden" name="page" value="<%= (totalPages > 0 ? Math.min(totalPages, currentPage + 1) : 1) %>">
                <button type="submit" <%= (totalPages == 0 || currentPage >= totalPages ? "disabled" : "") %>>Следующая ›</button>
            </form>
            <form method="GET" action="controller" style="display:inline-block;">
                <input type="hidden" name="pageSize" value="<%= pageSize %>">
                <input type="hidden" name="page" value="<%= (totalPages > 0 ? totalPages : 1) %>">
                <button type="submit" <%= (totalPages <= 1 || currentPage == totalPages ? "disabled" : "") %>>Последняя »</button>
            </form>
        </div>
    </div>

    <!-- Таблица с результатами предыдущих проверок -->
    <div class="container" id="results-table">
        <h2>История проверок</h2>
        
        <%
            List<CheckResult> results = (List<CheckResult>) request.getAttribute("pagedResults");
            if (results != null && !results.isEmpty()) {
        %>
        <table class="results-table">
            <thead>
                <tr>
                    <th>X</th>
                    <th>Y</th>
                    <th>R</th>
                    <th>Результат</th>
                    <th>Время (нс)</th>
                    <th>Время проверки</th>
                </tr>
            </thead>
            <tbody>
                <% for (CheckResult result : results) { %>
                <tr>
                    <td><%= result.getX() %></td>
                    <td><%= result.getY() %></td>
                    <td><%= result.getR() %></td>
                    <td class="<%= result.isHit() ? "hit" : "miss" %>">
                        <%= result.isHit() ? "Попадание" : "Промах" %>
                    </td>
                    <td><%= result.getExecutionTimeNanos() %></td>
                    <td class="result-time" data-utc="<%= result.getCurrentTime() %>">--</td>
                </tr>
                <% } %>
            </tbody>
        </table>
        <% } else { %>
        <p>Пока нет результатов проверки. Введите координаты и нажмите "Проверить".</p>
        <% } %>
    </div>
</main>

<script src="index.js"></script>
<script>
    window.__FEEDBACK__ = {
        status: "<%= feedbackStatusEscaped %>",
        message: "<%= feedbackMessageEscaped %>"
    };
</script>
<% if (feedbackResult != null) { %>
<script>
    window.__FEEDBACK__.details = {
        x: "<%= escapeJson(feedbackDetailX) %>",
        y: "<%= escapeJson(feedbackDetailY) %>",
        result: "<%= escapeJson(feedbackDetailResult) %>",
        executionTime: "<%= escapeJson(feedbackDetailExecutionTime) %>",
    checkTime: "<%= escapeJson(feedbackDetailCheckTime) %>",
    isHit: '<%= feedbackDetailHit != null ? feedbackDetailHit.toString() : "" %>'
    };
</script>
<% } else { %>
<script>
    window.__FEEDBACK__.details = null;
</script>
<% } %>

</body>
</html>