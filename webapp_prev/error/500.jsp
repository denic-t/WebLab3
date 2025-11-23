<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Ошибка 500 - Внутренняя ошибка сервера</title>
    <link rel="stylesheet" href="../css/style.css">
</head>
<body>

<header id="main-header">
    <h1>Ткачев Денис Владимирович</h1>
    <p>Группа: P3211 | Вариант: 929292</p>
</header>

<main>
    <div class="container">
        <h2>Ошибка 500 - Внутренняя ошибка сервера</h2>
        <div class="error-message">
            <p>Произошла внутренняя ошибка сервера.</p>
            <p>Приносим извинения за неудобства. Пожалуйста, попробуйте позже.</p>
            <% if (request.getAttribute("javax.servlet.error.message") != null) { %>
                <p><strong>Детали ошибки:</strong> <%= request.getAttribute("javax.servlet.error.message") %></p>
            <% } %>
        </div>
        <div class="navigation">
            <a href="../controller" class="back-link">← Вернуться на главную</a>
        </div>
    </div>
</main>

</body>
</html>