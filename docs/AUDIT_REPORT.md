# Отчёт об аудите проекта WebLab3

**Дата:** 16.11.2025  
**Проверяющий:** GitHub Copilot AI Assistant

---

## 📋 Соответствие техническому заданию

### ✅ Выполнено полностью:

| № | Требование ТЗ | Реализация |
|---|---------------|------------|
| 1 | 2 facelets-шаблона (стартовая + основная) | `start.xhtml`, `main.xhtml` на базе `template.xhtml` |
| 2 | Шапка с ФИО/группой/вариантом | В `template.xhtml`: "Ткачев Денис Владимирович, P3211, вариант 17010" |
| 3 | Форма X/Y/R с валидацией | `PointBean` + JSF `f:validateDoubleRange` |
| 4 | Таблица результатов | `h:dataTable` с источником `#{resultsBean.results}` |
| 5 | Сохранение в Oracle через JDBC | `ResultDao` с чистым JDBC (без ORM) |
| 6 | Session-scoped managed bean | `ResultsBean` с `@SessionScoped` |
| 7 | Аннотации для бинов | CDI `@Named`, `@SessionScoped`, `@RequestScoped` |
| 8 | Навигация в `faces-config.xml` | Правила `start ↔ main` |
| 9 | Ссылка на основную страницу | `<h:link outcome="main">` |
| 10 | Ссылка на стартовую со страницы main | `<h:link outcome="start">` |

---

### ⚠️ Реализовано частично:

| № | Требование ТЗ | Текущий статус | Рекомендация |
|---|---------------|----------------|--------------|
| 1 | **Интерактивные часы с обновлением каждые 5 сек** | Есть `TimeBean`, но часы НЕ обновляются | Добавить JS `setInterval` или `<p:poll>` |
| 2 | **Динамическая картинка области с точками** | Только заглушка `<div>` | Реализовать SVG + JS для отрисовки |
| 3 | **Клик по картинке → проверка точки** | Отсутствует | Добавить обработчик клика + hidden поля |
| 4 | **Смена R → перерисовка графика** | Отсутствует | AJAX-обновление при изменении R |

---

## 🐛 Исправленные критические ошибки

### 1. **Несовместимость конструкторов `CheckResult`**
**Проблема:** `ResultDao` пытался создать объект через конструктор с `LocalDateTime`, которого не было.

**Решение:** ✅ Добавлен второй конструктор:
```java
public CheckResult(double x, double y, double r, boolean hit, 
                   long executionTimeNanos, LocalDateTime timestamp)
```

### 2. **Неверное имя метода в `PointBean`**
**Проблема:** Вызывался `AreaCalculator.isInArea()`, но метод назывался `isHit()`.

**Решение:** ✅ Исправлено на `AreaCalculator.isHit(x, y, r)`

### 3. **Отсутствие `beans.xml`**
**Проблема:** CDI может не активироваться автоматически в WildFly.

**Решение:** ✅ Создан `WEB-INF/beans.xml` с `bean-discovery-mode="all"`

### 4. **Magic numbers без пояснений**
**Проблема:** В `AreaCalculator` коэффициенты `2` и `4` без комментариев.

**Решение:** ✅ Добавлены константы и подробные комментарии:
```java
private static final int TRIANGLE_SLOPE = 2;
private static final int CIRCLE_RADIUS_DIVISOR = 2;
```

---

## 🏗️ Архитектурные рекомендации (SOLID)

### ❌ Нарушения, которые стоит исправить:

#### 1. **Dependency Inversion Principle (DIP)**
**Проблема:** `ResultsBean` создаёт `ResultDao` через `new` → жёсткая связь.

**Рекомендация:**
```java
@Named("resultsBean")
@SessionScoped
public class ResultsBean implements Serializable {
    @Inject
    private ResultDao dao;  // инжектировать вместо new
}
```
Для этого `ResultDao` нужно сделать CDI-бином (`@ApplicationScoped`).

#### 2. **Static Utility Hell**
**Проблема:** `DbUtil` — статический класс с побочными эффектами в `static { }`.

**Рекомендация:** Переписать как инжектируемый сервис:
```java
@ApplicationScoped
public class DbConnectionService {
    private String url;
    private String username;
    private String password;
    
    @PostConstruct
    public void init() { /* загрузить db.properties */ }
    
    public Connection getConnection() throws SQLException { ... }
}
```

#### 3. **Single Responsibility (обработка ошибок)**
**Проблема:** `ResultsBean` пишет в `System.err` и сам решает, что делать с ошибкой.

**Рекомендация:** Делегировать уведомления пользователя через `FacesContext.addMessage()` или централизованный `ErrorHandler`.

---

## ✅ Что сделано правильно:

1. ✅ **Immutable DTO** — `CheckResult` с `final` полями
2. ✅ **Utility class** — `AreaCalculator` с приватным конструктором
3. ✅ **Separation of Concerns** — DAO/бизнес-логика/view разделены
4. ✅ **Правильные scopes** — `@SessionScoped` для истории, `@RequestScoped` для формы
5. ✅ **Чистый JDBC** без ORM (по требованию ТЗ)
6. ✅ **Facelets composition** через `ui:composition` и `template`

---

## 📝 Что нужно доделать для сдачи ЛР:

### 🔴 Критично (без этого не примут):

1. **Автообновление часов каждые 5 секунд**
   - Вариант 1 (простой): добавить JS `setInterval` в `start.xhtml`
   - Вариант 2 (JSF-way): использовать PrimeFaces `<p:poll interval="5" update="clock" listener="#{timeBean.update}" />`

2. **Динамический график SVG**
   - Отрисовать область (прямоугольник + треугольник + четверть круга)
   - Показать точки из истории разными цветами (зелёные/красные)

3. **Клик по графику → проверка точки**
   - JS-обработчик `onclick` для вычисления X/Y по координатам клика
   - Заполнение hidden полей `h:inputHidden` и submit формы через `h:commandButton`

### 🟡 Желательно (для хорошей оценки):

4. **Улучшить обработку ошибок БД**
   - Показывать `FacesMessage` вместо `System.err`
   - Логировать через `java.util.logging.Logger`

5. **Настроить db.properties**
   - Вписать реальные параметры Oracle перед запуском

6. **Удалить старый код**
   - Убрать `ControllerServlet`, `AreaCheckServlet`, `index.jsp` после проверки JSF-навигации

---

## 🚀 Инструкция по завершению:

1. **Настроить БД:**
   ```bash
   sqlplus user/pass@localhost:1521/xe < docs/create_table.sql
   ```

2. **Изменить `db.properties`:**
   ```properties
   db.url=jdbc:oracle:thin:@your_host:1521:xe
   db.username=your_user
   db.password=your_pass
   ```

3. **Собрать проект:**
   ```powershell
   .\gradlew.bat clean build
   ```

4. **Задеплоить на WildFly:**
   - Скопировать `build/libs/WebLab3.war` в `wildfly/standalone/deployments/`
   - Запустить сервер: `bin/standalone.bat`

5. **Проверить работу:**
   - Открыть `http://localhost:8080/WebLab3/`
   - Заполнить форму X/Y/R → кнопка "Проверить"
   - Убедиться, что результат сохраняется в БД и отображается в таблице

---

## 📚 Подготовка к защите:

### Вопросы из README, на которые нужно ответить:

1. **JSF vs сервлеты/JSP** — чем отличается, зачем нужен
2. **Facelets-шаблоны** — `ui:composition`, `ui:define`, `ui:insert`
3. **JSF-компоненты** — `h:inputText`, `h:dataTable`, жизненный цикл
4. **Валидаторы** — `f:validateDoubleRange`, кастомные `@FacesValidator`
5. **UIViewRoot** — дерево компонентов на сервере
6. **Managed beans** — scopes (Request/Session/Application), CDI vs JSF
7. **faces-config.xml** — навигация, конфигурация
8. **JDBC** — `DriverManager`, `PreparedStatement`, SQL Injection
9. **ORM vs JDBC** — когда использовать (Hibernate/EclipseLink/JPA)

---

## ✨ Итоговая оценка проекта:

- **Базовая функциональность:** ✅ 85% готово
- **Качество кода:** ✅ Хорошее (после правок)
- **Архитектура:** ⚠️ Приемлемая (есть что улучшить)
- **Готовность к сдаче:** 🟡 **Нужны 2-3 часа на доработку графика и часов**

**Удачи на защите! 🚀**
