package org.denic_t.web.bean;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.denic_t.web.CheckResult;
import org.denic_t.web.db.ResultDao;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Named("resultsBean")
@SessionScoped
public class ResultsBean implements Serializable {

    private List<CheckResult> results = new ArrayList<>();

    @Inject
    private ResultDao dao;

    @PostConstruct
    public void init() {
        // Загружаем историю результатов из БД при инициализации сессии
        try {
            results = dao.findAll();
        } catch (Throwable e) {
            System.err.println("Failed to load results from DB: " + e.getMessage());
            e.printStackTrace();
            results = new ArrayList<>();
        }
    }

    /**
     * Добавляет новый результат в список и сохраняет в БД.
     */
    public void addResult(CheckResult result) {
        try {
            dao.save(result);
            results.add(0, result); // добавляем в начало (новые сверху)
        } catch (SQLException e) {
            System.err.println("Failed to save result to DB: " + e.getMessage());
            e.printStackTrace();
            // Даже если не сохранилось в БД, показываем в текущей сессии
            results.add(0, result);
        }
    }

    public List<CheckResult> getResults() {
        return results;
    }

    public void clearResults() {
        try {
            dao.clearAll();
            results.clear();
        } catch (SQLException e) {
            System.err.println("Failed to clear results: " + e.getMessage());
        }
    }

    // Pagination
    private int currentPage = 1;
    private int pageSize = 10;

    public List<CheckResult> getPagedResults() {
        int fromIndex = (currentPage - 1) * pageSize;
        if (fromIndex >= results.size()) {
            return new ArrayList<>();
        }
        int toIndex = Math.min(fromIndex + pageSize, results.size());
        return results.subList(fromIndex, toIndex);
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalPages() {
        return (int) Math.ceil((double) results.size() / pageSize);
    }

    public int getTotalCount() {
        return results.size();
    }

    public void nextPage() {
        if (currentPage < getTotalPages()) {
            currentPage++;
        }
    }

    public void prevPage() {
        if (currentPage > 1) {
            currentPage--;
        }
    }

    public void firstPage() {
        currentPage = 1;
    }

    public void lastPage() {
        currentPage = getTotalPages();
    }

    public void changePageSize() {
        String pageSizeParam = jakarta.faces.context.FacesContext.getCurrentInstance().getExternalContext()
                .getRequestParameterMap().get("pageSize");
        if (pageSizeParam != null) {
            try {
                int newSize = Integer.parseInt(pageSizeParam);
                if (newSize > 0) {
                    this.pageSize = newSize;
                    this.currentPage = 1; // Reset to first page
                }
            } catch (NumberFormatException e) {
                // Ignore invalid format
            }
        }
    }
}
