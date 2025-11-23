<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Ошибка - Лабораторная работа №2</title>
    <link rel="stylesheet" href="../css/style.css">
</head>
<body>

<header id="main-header">
    <h1>Ткачев Денис Владимирович</h1>
    <p>Группа: P3211 | Вариант: 929292</p>
</header>

<main>
    <div class="container">
        <h2>Произошла ошибка</h2>
        <div class="error-message">
            <p>К сожалению, произошла непредвиденная ошибка.</p>
            
            <% if (exception != null) { %>
                <p><strong>Тип ошибки:</strong> <%= exception.getClass().getSimpleName() %></p>
                <p><strong>Сообщение:</strong> <%= exception.getMessage() %></p>
            <% } %>
            
            <% if (request.getAttribute("javax.servlet.error.exception") != null) { %>
                <p><strong>Детали:</strong> <%= request.getAttribute("javax.servlet.error.exception") %></p>
            <% } %>
            
            <p>Пожалуйста, проверьте правильность введенных данных и попробуйте снова.</p>
        </div>
        <div class="navigation">
            <a href="../controller" class="back-link">← Вернуться на главную</a>
        </div>
    </div>
</main>

</body>
</html>