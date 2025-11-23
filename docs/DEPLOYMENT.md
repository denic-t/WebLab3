# Инструкция по запуску WebLab3

## Текущий статус реализации

### ✅ Готово:
1. **JSF конфигурация**: `web.xml` с `FacesServlet`, маппинг на `.xhtml`
2. **Facelets шаблоны**: 
   - `template.xhtml` — общий шаблон с шапкой
   - `start.xhtml` — стартовая страница с часами (через `TimeBean`)
   - `main.xhtml` — основная страница с формой проверки точки
3. **Managed Beans**:
   - `TimeBean` (@SessionScoped) — отображение времени
   - `PointBean` (@RequestScoped) — ввод координат X/Y/R, валидация, проверка попадания
   - `ResultsBean` (@SessionScoped) — хранение истории результатов, загрузка из БД
4. **JDBC слой**:
   - `DbUtil` — утилита подключения к Oracle
   - `ResultDao` — сохранение/загрузка результатов через чистый JDBC
5. **Навигация**: `faces-config.xml` с правилами `start ↔ main`
6. **Бизнес-логика**: переиспользуется `AreaCalculator` для проверки попадания

### 🔧 Требуется настройка:

#### 1. Настройка Oracle БД

Выполните SQL-скрипт `docs/create_table.sql` в вашей Oracle БД:
```sql
-- Создаёт sequence и таблицу CHECK_RESULTS
CREATE SEQUENCE CHECK_RESULTS_SEQ START WITH 1 INCREMENT BY 1;
CREATE TABLE CHECK_RESULTS (...);
```

#### 2. Настройка подключения к БД

Отредактируйте `src/main/resources/db.properties`:
```properties
db.url=jdbc:oracle:thin:@localhost:1521:xe
db.username=ваш_юзер
db.password=ваш_пароль
```

Замените на реальные параметры вашей Oracle БД.

#### 3. Сборка проекта

```powershell
cd c:\Programming\Web\WebLab3
.\gradlew.bat clean build
```

Результат: `build/libs/WebLab3.war`

#### 4. Деплой на WildFly

1. Скопируйте `WebLab3.war` в `wildfly/standalone/deployments/`
2. Или используйте веб-консоль WildFly для загрузки WAR
3. Запустите WildFly: `bin/standalone.bat`

#### 5. Проверка работы

Откройте браузер: `http://localhost:8080/WebLab3/`

- Должна загрузиться стартовая страница с часами и ссылкой
- Переход на основную страницу → форма X/Y/R
- После проверки точки → результат добавляется в таблицу и сохраняется в БД
- История загружается из БД при старте новой сессии

---

## Что делать дальше:

### 📋 Следующие шаги (опционально):

1. **Динамический график SVG** на `main.xhtml`:
   - Добавить SVG с областью (квадрат, треугольник, четверть круга)
   - JS-обработчик клика по графику → заполнение hidden полей X/Y → submit формы
   - Отрисовка точек из истории на графике

2. **Автообновление часов** на `start.xhtml`:
   - Вариант 1: чистый JS `setInterval` (клиентское время)
   - Вариант 2: PrimeFaces `<p:poll>` + метод `timeBean.update()` (серверное время)

3. **Улучшение валидации**:
   - Кастомные `@FacesValidator` для специфических правил
   - Более детальные сообщения об ошибках

4. **Стилизация**:
   - Дополнить `css/style.css` под новые JSF-компоненты
   - Адаптировать существующие стили для `h:dataTable`, `h:messages`

5. **Удаление старого кода**:
   - Когда JSF-навигация стабильна, удалить `ControllerServlet`, `AreaCheckServlet`
   - Убрать старый `index.jsp` и связанный JS

---

## Примечания:

- **WildFly** уже содержит JSF, CDI, Servlet API — не нужно упаковывать их в WAR
- **Oracle JDBC драйвер** (`ojdbc11`) упакован в WAR через `implementation` в Gradle
- Текущая реализация использует **чистый JDBC**, без ORM (по требованию ТЗ)
- Все бины используют **CDI аннотации** (`@Named`, `@SessionScoped`, `@RequestScoped`)

---

## Возможные проблемы:

1. **ClassNotFoundException: oracle.jdbc.driver.OracleDriver**
   - Убедитесь, что `ojdbc11` добавлен в `build.gradle` и проект пересобран

2. **SQLException при подключении**
   - Проверьте `db.properties`: URL, логин, пароль
   - Убедитесь, что Oracle БД запущена и доступна

3. **JSF-компоненты не отображаются**
   - Проверьте, что открываете `http://.../*.xhtml`, а не `*.jsp`
   - Убедитесь, что `FacesServlet` корректно настроен в `web.xml`

4. **Бины не инжектируются**
   - WildFly должен найти `beans.xml` или автоматически активировать CDI
   - Для явной активации можно создать пустой `WEB-INF/beans.xml`

---

Удачи с лабораторной! 🚀
