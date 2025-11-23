# Обязанности Java-классов проекта WebLab2

Ниже собраны краткие описания назначения каждого Java-файла из каталога `src/main/java`.

## Пакет `org.denic_t.web`

| Класс | Ответственность | Ключевые зависимости |
|-------|-----------------|----------------------|
| `AreaCalculator` | Содержит чистую геометрическую функцию `isHit(x, y, r)` для определения попадания точки в область. Не хранит состояния и не занимается валидацией. | Нет прямых зависимостей, работает только с примитивами. |
| `CheckResult` | DTO результата проверки: координаты, флаг попадания, штамп времени (формат `yyyy-MM-dd HH:mm:ss`), время выполнения. Гарантирует неизменяемость данных. | `java.time.LocalDateTime`, `java.time.format.DateTimeFormatter`. |
| `Params` | DTO валидированных входных параметров (x, y, r). Используется доменной логикой. | Нет. |
| `ParamsFactory` | Центральная точка создания `Params`. Отвечает за преобразование строки запроса/карты в числа и делегирует проверку `ParamsValidator`. | `QueryParser`, `ParamsValidator`, `ValidationException`. |
| `ParamsValidator` | Инкапсулирует бизнес-правила для X, Y, R. Генерирует `ValidationException` при нарушении. | `ValidationException`. |
| `QueryParser` | Разбирает query string в карту параметров с декодированием UTF-8. | `java.net.URLDecoder`. |
| `ValidationException` | Исключение уровня доменной валидации. Расширяет `RuntimeException` и поддерживает вложенную причину. | `RuntimeException`. |

## Пакет `org.denic_t.web.service`

| Класс | Ответственность | Ключевые зависимости |
|-------|-----------------|----------------------|
| `AreaCheckService` | Оркестрация доменной операции: создаёт `Params` через `ParamsFactory`, вызывает `AreaCalculator`, формирует `CheckResult`. Снимает с веб-слоя обязанность выполнять бизнес-логику. | `ParamsFactory`, `AreaCalculator`, `CheckResult`, `ValidationException`. |

## Пакет `org.denic_t.web.servlet`

| Класс | Ответственность | Ключевые зависимости |
|-------|-----------------|----------------------|
| `ControllerServlet` | Центральный контроллер MVC. Определяет, содержит ли запрос координаты, и перенаправляет либо на `AreaCheckServlet`, либо на `index.jsp`. Не содержит бизнес-логики. | Jakarta Servlet API (`HttpServlet`, `RequestDispatcher`). |
| `AreaCheckServlet` | HTTP-обёртка над `AreaCheckService`. Извлекает параметры из запроса, делегирует проверку сервису, сохраняет историю и состояние формы в сессии и перенаправляет обратно на `ControllerServlet`/`index.jsp`. | `AreaCheckService`, `CheckResult`, Jakarta Servlet API. |

## Пакет `org.denic_t.web.filter`

| Класс | Ответственность | Ключевые зависимости |
|-------|-----------------|----------------------|
| `EncodingFilter` | Принудительно устанавливает кодировку UTF-8 для запросов/ответов и отключает кэширование для динамических маршрутов (`controller`, `check`). | Jakarta Servlet API (`Filter`, `HttpServletRequest`, `HttpServletResponse`). |

---
