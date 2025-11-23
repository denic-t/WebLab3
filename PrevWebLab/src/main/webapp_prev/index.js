document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('coordinates-form');
    const xCheckboxes = document.querySelectorAll('input[name="x"]');
    const yInput = document.getElementById('y-input');
    const rInput = document.getElementById('r-input');
    const submitBtn = document.getElementById('submit-btn');
    const plot = document.getElementById('plot');
    const feedbackModal = document.getElementById('feedback-modal');
    const feedbackBackdrop = document.getElementById('feedback-backdrop');
    const feedbackTitle = feedbackModal ? feedbackModal.querySelector('.feedback-modal__title') : null;
    const feedbackMessage = feedbackModal ? feedbackModal.querySelector('.feedback-modal__message') : null;
    const feedbackButton = feedbackModal ? feedbackModal.querySelector('.feedback-modal__button') : null;
    const feedbackDetailsContainer = feedbackModal ? feedbackModal.querySelector('.feedback-modal__details') : null;
    const feedbackDetailsFields = feedbackModal ? {
        x: feedbackModal.querySelector('[data-field="x"]'),
        y: feedbackModal.querySelector('[data-field="y"]'),
        result: feedbackModal.querySelector('[data-field="result"]'),
        executionTime: feedbackModal.querySelector('[data-field="executionTime"]'),
        checkTime: feedbackModal.querySelector('[data-field="checkTime"]')
    } : null;
    const pageSizeModal = document.getElementById('page-size-modal');
    const openPageSizeBtn = document.getElementById('open-page-size-modal');
    const pageSizeInput = document.getElementById('page-size-input');
    const pageSizeCancel = pageSizeModal ? pageSizeModal.querySelector('.page-size-modal__cancel') : null;
    const pageSizeSave = pageSizeModal ? pageSizeModal.querySelector('.page-size-modal__save') : null;
    const pageSizePresetButtons = pageSizeModal ? pageSizeModal.querySelectorAll('.page-size-preset') : null;
    
    let selectedX = null;
    let currentR = null;
    
    const MAX_PRECISION = 7;

    // Инициализация при загрузке страницы
    init();

    function init() {
        hydrateSelections();
        updatePlotWithResults();
        clearErrorMessages();
        localizeTimestamps();
        resetFeedbackDetails();
        showFeedbackFromServer();
    }

    function hydrateSelections() {
        selectedX = null;
        xCheckboxes.forEach(cb => {
            if (cb.checked) {
                selectedX = parseFloat(cb.value);
            }
        });
        updateCurrentR();
    }

    /**
     * Обработка выбора X через checkbox (только один может быть выбран)
     */
    function handleXSelection(checkbox) {
        if (checkbox.checked) {
            // Снимаем выбор со всех остальных checkbox
            xCheckboxes.forEach(cb => {
                if (cb !== checkbox) {
                    cb.checked = false;
                }
            });
            selectedX = parseFloat(checkbox.value);
        } else {
            selectedX = null;
        }
        clearError('x-error');
    }

    /**
     * Валидация формы перед отправкой
     */
    function validateForm() {
        let isValid = true;
        clearErrorMessages();

        // Проверка X
        const checkedX = document.querySelector('input[name="x"]:checked');
        if (!checkedX) {
            showError('x-error', 'Выберите значение X');
            isValid = false;
        } else {
            selectedX = parseFloat(checkedX.value);
        }

        // Проверка Y
        const yValue = yInput.value.trim().replace(',', '.');
        if (!yValue) {
            showError('y-error', 'Введите значение Y');
            isValid = false;
        } else {
            const y = parseFloat(yValue);
            if (isNaN(y)) {
                showError('y-error', 'Y должно быть числом');
                isValid = false;
            } else if (y < -3 || y > 3) {
                showError('y-error', 'Y должно быть в диапазоне от -3 до 3');
                isValid = false;
            }
        }

        // Проверка R
        const rValue = rInput.value.trim().replace(',', '.');
        if (!rValue) {
            showError('r-error', 'Введите значение R');
            isValid = false;
        } else {
            const r = parseFloat(rValue);
            if (isNaN(r)) {
                showError('r-error', 'R должно быть числом');
                isValid = false;
            } else if (r < 1 || r > 4) {
                showError('r-error', 'R должно быть в диапазоне от 1 до 4');
                isValid = false;
            } else {
                currentR = r;
            }
        }

        return isValid;
    }

    function localizeTimestamps() {
        const timeCells = document.querySelectorAll('.result-time[data-utc]');
        timeCells.forEach(cell => {
            const utcValue = cell.getAttribute('data-utc');
            if (!utcValue) {
                return;
            }

            const date = new Date(utcValue);
            if (Number.isNaN(date.getTime())) {
                return;
            }

            cell.textContent = formatDate(date);
        });
    }

    function formatDate(date) {
        const pad = (value) => String(value).padStart(2, '0');
        const year = date.getFullYear();
        const month = pad(date.getMonth() + 1);
        const day = pad(date.getDate());
        const hours = pad(date.getHours());
        const minutes = pad(date.getMinutes());
        const seconds = pad(date.getSeconds());
        return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
    }

    /**
     * Обработка клика по области графика
     */
    function handlePlotClick(event) {
        if (!currentR) {
            alert('Сначала установите радиус области (R)');
            return;
        }

        const svg = event.currentTarget;
        const rect = svg.getBoundingClientRect();
        
        // Получаем координаты клика относительно SVG
        const svgX = event.clientX - rect.left;
        const svgY = event.clientY - rect.top;
        
        // Преобразуем координаты SVG в координаты области
        // SVG: center=(200,200), scale=(1,-1)
        const x = (svgX - 200) * currentR / 150; // 150 - это R на графике
        const y = (200 - svgY) * currentR / 150; // Инверсия Y из-за scale(1,-1)
        
        // Ограничиваем точность
        const roundedX = Math.round(x * Math.pow(10, MAX_PRECISION)) / Math.pow(10, MAX_PRECISION);
        const roundedY = Math.round(y * Math.pow(10, MAX_PRECISION)) / Math.pow(10, MAX_PRECISION);
        
        // Отправляем запрос на сервер
        const form = document.createElement('form');
        form.method = 'GET';
        form.action = 'controller';
        form.style.display = 'none';
        
        const inputX = document.createElement('input');
        inputX.type = 'hidden';
        inputX.name = 'x';
        inputX.value = roundedX;
        form.appendChild(inputX);
        
        const inputY = document.createElement('input');
        inputY.type = 'hidden';
        inputY.name = 'y';
        inputY.value = roundedY;
        form.appendChild(inputY);
        
        const inputR = document.createElement('input');
        inputR.type = 'hidden';
        inputR.name = 'r';
        inputR.value = currentR;
        form.appendChild(inputR);
        
        const inputSource = document.createElement('input');
        inputSource.type = 'hidden';
        inputSource.name = 'source';
        inputSource.value = 'plot';
        form.appendChild(inputSource);

        document.body.appendChild(form);
        form.submit();
    }

    /**
     * Обновление графика с результатами из сессии
     */
    function updatePlotWithResults() {
        const resultsTable = document.querySelector('.results-table tbody');
        if (!resultsTable) return;
        
        const resultsGroup = document.getElementById('result-points');
        if (!resultsGroup) return;
        
        // Очищаем предыдущие точки
        resultsGroup.innerHTML = '';
        
        // Добавляем точки для каждого результата
        const rows = resultsTable.querySelectorAll('tr');
        rows.forEach(row => {
            const cells = row.querySelectorAll('td');
            if (cells.length >= 4) {
                const x = parseFloat(cells[0].textContent);
                const y = parseFloat(cells[1].textContent);
                const r = parseFloat(cells[2].textContent);
                const isHit = cells[3].classList.contains('hit');
                
                addPointToPlot(x, y, r, isHit, resultsGroup);
            }
        });
    }

    /**
     * Добавление точки на график
     */
    function addPointToPlot(x, y, r, isHit, container) {
        const svgX = (x / r) * 150; // Масштабируем к графику
        const svgY = (y / r) * 150;
        
        const circle = document.createElementNS('http://www.w3.org/2000/svg', 'circle');
        circle.setAttribute('cx', svgX);
        circle.setAttribute('cy', svgY);
        circle.setAttribute('r', '3');
        circle.setAttribute('fill', isHit ? '#4CAF50' : '#F44336');
        circle.setAttribute('stroke', '#fff');
        circle.setAttribute('stroke-width', '1');
        
        container.appendChild(circle);
    }

    /**
     * Ограничение точности ввода
     */
    function limitPrecision(event) {
        const input = event.target;
        let value = input.value.replace(',', '.'); // Нормализуем запятую в точку

        if (value.includes('.')) {
            const parts = value.split('.');
            if (parts[1] && parts[1].length > MAX_PRECISION) {
                input.value = `${parts[0]}.${parts[1].slice(0, MAX_PRECISION)}`;
            }
        }
    }

    /**
     * Отображение ошибки
     */
    function showError(elementId, message) {
        const errorElement = document.getElementById(elementId);
        if (errorElement) {
            errorElement.textContent = message;
            errorElement.style.display = 'block';
        }
    }

    /**
     * Очистка конкретной ошибки
     */
    function clearError(elementId) {
        const errorElement = document.getElementById(elementId);
        if (errorElement) {
            errorElement.textContent = '';
            errorElement.style.display = 'none';
        }
    }

    /**
     * Очистка всех ошибок
     */
    function clearErrorMessages() {
        const errorElements = document.querySelectorAll('.error-message');
        errorElements.forEach(element => {
            element.textContent = '';
            element.style.display = 'none';
        });
    }

    /**
     * Обновление значения R при изменении поля
     */
    function updateCurrentR() {
        const rValue = rInput.value.trim().replace(',', '.');
        if (rValue && !isNaN(parseFloat(rValue))) {
            const r = parseFloat(rValue);
            if (r >= 1 && r <= 4) {
                currentR = r;
            }
        }
    }

    // События
    form.addEventListener('submit', (event) => {
        if (!validateForm()) {
            event.preventDefault();
        }
    });

    yInput.addEventListener('input', limitPrecision);
    rInput.addEventListener('input', (event) => {
        limitPrecision(event);
        updateCurrentR();
    });

    // Глобальные функции для использования из JSP
    window.handleXSelection = handleXSelection;
    window.handlePlotClick = handlePlotClick;
    window.hideFeedbackModal = hideFeedbackModal;
    window.openPageSizeModal = openPageSizeModal;
    window.closePageSizeModal = closePageSizeModal;

    function showFeedbackFromServer() {
        if (!window.__FEEDBACK__ || !feedbackModal || !feedbackTitle || !feedbackMessage) {
            return;
        }

        const { status, message, details } = window.__FEEDBACK__;
        if (!status || !message) {
            return;
        }

        showFeedbackModal(status, message, details);
    }

    function showFeedbackModal(status, message, details) {
        if (!feedbackModal || !feedbackBackdrop || !feedbackTitle || !feedbackMessage) {
            return;
        }

    feedbackModal.classList.remove('feedback-modal--success', 'feedback-modal--error');
    feedbackTitle.classList.remove('feedback-modal__title--hit', 'feedback-modal__title--miss');

    const hitState = resolveHitState(details);

        if (status === 'success') {
            feedbackModal.classList.add('feedback-modal--success');
            if (hitState === true) {
                feedbackTitle.textContent = 'Попадание';
                feedbackTitle.classList.add('feedback-modal__title--hit');
            } else if (hitState === false) {
                feedbackTitle.textContent = 'Промах';
                feedbackTitle.classList.add('feedback-modal__title--miss');
            } else {
                feedbackTitle.textContent = 'Результат';
            }
        } else {
            feedbackModal.classList.add('feedback-modal--error');
            feedbackTitle.textContent = 'Ошибка';
        }

        feedbackMessage.textContent = message;
        populateFeedbackDetails(details, status);

        feedbackBackdrop.classList.add('visible');
        feedbackBackdrop.setAttribute('aria-hidden', 'false');
        feedbackModal.classList.add('visible');
        feedbackModal.setAttribute('aria-hidden', 'false');

        if (feedbackButton) {
            feedbackButton.focus();
        }
    }

    function populateFeedbackDetails(details, status) {
        if (!feedbackDetailsContainer || !feedbackDetailsFields) {
            return;
        }

        const showDetails = status === 'success' && details;
        if (!showDetails) {
            resetFeedbackDetails();
            feedbackDetailsContainer.classList.remove('visible');
            feedbackDetailsContainer.setAttribute('aria-hidden', 'true');
            return;
        }

        feedbackDetailsFields.x.textContent = formatNumberForDisplay(details.x);
        feedbackDetailsFields.y.textContent = formatNumberForDisplay(details.y);
        if (feedbackDetailsFields.result) {
            feedbackDetailsFields.result.textContent = details.result || '—';
            feedbackDetailsFields.result.classList.remove('result-cell--hit', 'result-cell--miss');
            const hitState = resolveHitState(details);
            if (hitState === true) {
                feedbackDetailsFields.result.classList.add('result-cell--hit');
            } else if (hitState === false) {
                feedbackDetailsFields.result.classList.add('result-cell--miss');
            }
        }
        feedbackDetailsFields.executionTime.textContent = details.executionTime || '—';
        feedbackDetailsFields.checkTime.textContent = formatCheckTime(details.checkTime);

        feedbackDetailsContainer.classList.add('visible');
        feedbackDetailsContainer.setAttribute('aria-hidden', 'false');
    }

    function resetFeedbackDetails() {
        if (!feedbackDetailsFields) {
            return;
        }
        Object.values(feedbackDetailsFields).forEach((field) => {
            if (field) {
                field.textContent = '—';
                if (field === feedbackDetailsFields.result) {
                    field.classList.remove('result-cell--hit', 'result-cell--miss');
                }
            }
        });
    }

    function formatNumberForDisplay(value) {
        if (value === null || value === undefined || value === '') {
            return '—';
        }
        const numeric = Number(value);
        if (Number.isNaN(numeric)) {
            return value;
        }
        const fixed = numeric.toFixed(Math.min(MAX_PRECISION, 7));
        return Number(fixed).toString();
    }

    function formatCheckTime(value) {
        if (!value) {
            return '—';
        }
        const parsedDate = new Date(value);
        if (Number.isNaN(parsedDate.getTime())) {
            return value;
        }
        return formatDate(parsedDate);
    }

    function resolveHitState(details) {
        if (!details || typeof details.isHit === 'undefined' || details.isHit === null) {
            return null;
        }

        if (typeof details.isHit === 'boolean') {
            return details.isHit;
        }

        if (typeof details.isHit === 'string') {
            const normalized = details.isHit.trim().toLowerCase();
            if (normalized === 'true') {
                return true;
            }
            if (normalized === 'false') {
                return false;
            }
        }

        return null;
    }

    function hideFeedbackModal() {
        if (!feedbackModal || !feedbackBackdrop) {
            return;
        }

        feedbackBackdrop.classList.remove('visible');
        feedbackBackdrop.setAttribute('aria-hidden', 'true');
        feedbackModal.classList.remove('visible');
        feedbackModal.setAttribute('aria-hidden', 'true');
        feedbackModal.classList.remove('feedback-modal--success', 'feedback-modal--error');
        if (feedbackTitle) {
            feedbackTitle.classList.remove('feedback-modal__title--hit', 'feedback-modal__title--miss');
        }
        if (feedbackDetailsContainer) {
            feedbackDetailsContainer.classList.remove('visible');
            feedbackDetailsContainer.setAttribute('aria-hidden', 'true');
        }
        resetFeedbackDetails();
    }

    if (feedbackButton) {
        feedbackButton.addEventListener('click', hideFeedbackModal);
    }

    if (feedbackBackdrop) {
        feedbackBackdrop.addEventListener('click', hideFeedbackModal);
    }

    if (openPageSizeBtn) {
        openPageSizeBtn.addEventListener('click', openPageSizeModal);
    }
    if (pageSizeCancel) {
        pageSizeCancel.addEventListener('click', closePageSizeModal);
    }
    if (pageSizeSave) {
        pageSizeSave.addEventListener('click', applyPageSize);
    }
    if (pageSizePresetButtons && pageSizePresetButtons.length) {
        pageSizePresetButtons.forEach((btn) => {
            btn.addEventListener('click', () => {
                const val = parseInt(btn.getAttribute('data-size'), 10);
                if (Number.isFinite(val) && pageSizeInput) {
                    pageSizeInput.value = String(val);
                }
            });
        });
    }

    function openPageSizeModal() {
        if (!pageSizeModal) return;
        pageSizeModal.classList.add('visible');
        pageSizeModal.setAttribute('aria-hidden', 'false');
        if (pageSizeInput) pageSizeInput.focus();
    }

    function closePageSizeModal() {
        if (!pageSizeModal) return;
        pageSizeModal.classList.remove('visible');
        pageSizeModal.setAttribute('aria-hidden', 'true');
    }

    function applyPageSize() {
        if (!pageSizeInput) return;
        const value = pageSizeInput.value ? parseInt(pageSizeInput.value, 10) : NaN;
        if (!Number.isFinite(value) || value < 1) {
            return;
        }
        const form = document.createElement('form');
        form.method = 'GET';
        form.action = 'controller';
        form.style.display = 'none';

        const inputSize = document.createElement('input');
        inputSize.type = 'hidden';
        inputSize.name = 'pageSize';
        inputSize.value = String(value);
        form.appendChild(inputSize);

        const inputPage = document.createElement('input');
        inputPage.type = 'hidden';
        inputPage.name = 'page';
        inputPage.value = '1';
        form.appendChild(inputPage);

        document.body.appendChild(form);
        form.submit();
    }

    document.addEventListener('keydown', (event) => {
        if (event.key === 'Escape') {
            hideFeedbackModal();
        }
    });
});